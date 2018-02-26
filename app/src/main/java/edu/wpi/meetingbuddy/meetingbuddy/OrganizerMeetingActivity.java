package edu.wpi.meetingbuddy.meetingbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paul on 2/19/2018.
 */

public class OrganizerMeetingActivity extends AppCompatActivity {
    TextView meetingText;
    TextView dateText;
    TextView timeText;
    TextView locationText;
    TextView attendeesText;
    TextView presentAttendeesText;
    ListView attendeesList;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> names;
    Bundle extra;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_meeting_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the data from the intent
        Meeting thisMeeting = (Meeting) getIntent().getSerializableExtra("Meeting");

        JSONObject jason = new JSONObject();
        try {
            jason.put("meetingID", thisMeeting.getMeetingID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkManager networkManager = ((ApplicationManager) this.getApplication()).getNetworkManager();
        networkManager.post(NetworkManager.url + "/getMeetingAttendance", jason.toString(), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("Failed to connect");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to connect, error in phone number getter", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ArrayList<String> strings = new ArrayList<>();
                final String responseStr = response.body().string();
                try {
                    final JSONArray jsonArr = new JSONArray(responseStr);
                    Log.e("Response string", responseStr);
                    for(int i = 0; i < jsonArr.length(); i++){
                        String first = jsonArr.getJSONObject(i).getString("firstName");
                        String last = jsonArr.getJSONObject(i).getString("lastName");
                        String id = jsonArr.getJSONObject(i).getString("accountID");
                        strings.add( "ID: " + id + ": " + first + " " + last );
                        Log.e("NAMES ADDED TO THE S", first + " " + last);
                    }
                    callback(strings);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Reference the widgets
        attendeesList = findViewById(R.id.AttendeesList);
        meetingText = findViewById(R.id.MeetingText);
        dateText = findViewById(R.id.DateText);
        timeText = findViewById(R.id.TimeText);
        locationText = findViewById(R.id.LocationText);
        attendeesText = findViewById(R.id.AttendeesText);
        presentAttendeesText = findViewById(R.id.PresentAttendeesText);


        // Init the widgets
        meetingText.setText(thisMeeting.getName());
        dateText.setText(thisMeeting.getDate());
        timeText.setText(thisMeeting.getTime());
        locationText.setText(String.format("%s%s%s%s", "Longitude: ",String.format("%1$,.2f", thisMeeting.getLongitude()), " Latitude: ",String.format("%1$,.2f", thisMeeting.getLatitude())));
//        attendeesText.setText(thisMeeting.getClassSize());



    }
    public void callback(ArrayList<String> names){
        this.names = names;
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, names);
        attendeesList.setAdapter(listAdapter);
    }
}
