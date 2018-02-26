package edu.wpi.meetingbuddy.meetingbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    Bundle extra;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_meeting_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // String array and listview things for present attendees
        String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
                "Jupiter", "Saturn", "Uranus", "Neptune"};
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll( Arrays.asList(planets) );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

        System.out.println("We got this far");

        // Reference the widgets
        attendeesList = findViewById(R.id.AttendeesList);
        meetingText = findViewById(R.id.MeetingText);
        dateText = findViewById(R.id.DateText);
        timeText = findViewById(R.id.TimeText);
        locationText = findViewById(R.id.LocationText);
        attendeesText = findViewById(R.id.AttendeesText);
        presentAttendeesText = findViewById(R.id.PresentAttendeesText);

        System.out.println("We got this far1");

        attendeesList.setAdapter(listAdapter);

        System.out.println("We got this far2");

        // Get the data from the intent
        Meeting thisMeeting = (Meeting) getIntent().getSerializableExtra("Meeting");

        // Init the widgets
        meetingText.setText(thisMeeting.getName());
        dateText.setText(thisMeeting.getDate());
        timeText.setText(thisMeeting.getTime());
        locationText.setText(String.format("%s%s%s%s", "Longitude: ",String.format("%1$,.2f", thisMeeting.getLongitude()), " Latitude: ",String.format("%1$,.2f", thisMeeting.getLatitude())));
        attendeesText.setText(thisMeeting.getClassSize());



    }
}
