package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeopleSearchActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    CustomAdapter adapter;
    TextView selectedUsersTV;

    private NetworkManager networkManager;
    ArrayList<String> usernames;
    ArrayList<String> selectedUsers;
    String allUsers;
    String trimmed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_search);

        networkManager = ((ApplicationManager) this.getApplication()).getNetworkManager();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernames = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        //try to retrieve list of usernames from db
        try {
            retrieveData();
            //Log.e("usernames:", usernames.get(0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        usernames.add("smcateer");
        usernames.add("paul");
        usernames.add("jesse");

        allUsers= "";
        trimmed = "";


        final ListView listView = findViewById(R.id.listView);
        selectedUsersTV = findViewById(R.id.selectedUsersTV);
        adapter = new CustomAdapter(this, R.layout.item_row, usernames);
        listView.setAdapter(adapter);
        adapter.addAll(usernames);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //onItemClick() callback method
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                //Get ListView clicked item's corresponded Array element value
                String clickedItemValue = usernames.get(position);

                //Generate a Toast message
                String toastMessage = "Position : "+position + " || Value : " + clickedItemValue;

                //Apply the ListView background color as user selected item value
                //listView.setBackgroundColor(Color.parseColor(clickedItemValue));

                //Display user response as a Toast message
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                addUserToSearchBar(clickedItemValue);

            }
        });

    }

    public void addUserToSearchBar(String user) {
        selectedUsers.add(user);
        allUsers = allUsers + user + ", ";
        trimmed = allUsers.substring(0, allUsers.length()-2);
        selectedUsersTV.setText(trimmed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return false;
            }
        });
        return true;

    }

    private void filterData(String query) {
        List<String> output = new ArrayList<>();
        List<String> filteredOutput = new ArrayList<>();


        if (searchView != null) {
            for (String str: output) {
                if (str.toLowerCase().startsWith(query.toLowerCase())){
                    filteredOutput.add(str);
                }
            }
        } else {
            filteredOutput = output;
        }
        adapter = new CustomAdapter(this, R.layout.item_row, filteredOutput);
        //listView.setAdapter(adapter);
        //adapter.addAll(usernames);


     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("listOfPeople", trimmed);
                returnIntent.putStringArrayListExtra("selectedArrayList", selectedUsers);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void retrieveData() {
        networkManager.post(NetworkManager.url + "/Search", "", new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("Failed to connect");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseStr = response.body().string();
                final int statusCode = response.code();
                try {

                    //Get usernames from server
                    final JSONArray jsonUsernames = new JSONArray(responseStr);

                    for (int i = 0; i < jsonUsernames.length(); i++) {
                        usernames.add(i, jsonUsernames.getJSONObject(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
