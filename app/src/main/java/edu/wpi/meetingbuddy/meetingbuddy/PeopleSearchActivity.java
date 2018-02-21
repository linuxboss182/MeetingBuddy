package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class PeopleSearchActivity extends AppCompatActivity {

    //MaterialSearchView materialSearchView;
    MenuItem doneBtn;
    ArrayList lop;
    SearchView searchView;
    ListView listView;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_search);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lop = new ArrayList<>();
        lop.add(new Account());

        final ListView listView = findViewById(R.id.listView);
        adapter = new CustomAdapter(this, R.layout.item_row, lop);

        //listView.setOnItemClickListener(adapter, view, position, 1);
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
                getData(newText);
                return false;
            }
        });
        //MenuItem doneItem = menu.findItem(R.id.done);
//

//        doneItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("listOfPeople", lop);
//                setResult(Activity.RESULT_OK, returnIntent);
//                return true;
//            }
//        });
        return true;

    }

    private void getData(String query) {
        List<Account> output = new ArrayList<>();
        List<Account> filteredOutput = new ArrayList<>();


        if (searchView != null) {
            for (Account acc: output) {
                if (acc.getUsername().toLowerCase().startsWith(query.toLowerCase())){
                    filteredOutput.add(acc);
                }
            }
        } else {
            filteredOutput = output;


        }
        adapter = new CustomAdapter(this, R.layout.item_row, filteredOutput);
        listView.setAdapter(adapter);



     }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.activity_main_update_menu_item:
//                Toast.makeText(this, "update clicked", Toast.LENGTH_SHORT).show();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }



}
