package edu.wpi.meetingbuddy.meetingbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Paul on 2/19/2018.
 */

public class OrganizerMeetingActivity extends AppCompatActivity {
    TextView meetingText;
    TextView dateText;
    TextView timeText;
    TextView locationText;
    TextView attendeesText;
    Bundle extra;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reference the widgets
        meetingText = findViewById(R.id.MeetingText);
        dateText = findViewById(R.id.DateText);
        timeText = findViewById(R.id.TimeText);
        locationText = findViewById(R.id.LocationText);
        attendeesText = findViewById(R.id.AttendeesText);

        // Get the data from the intent
        Meeting thisMeeting = (Meeting) getIntent().getSerializableExtra("Meeting");

        // Init the widgets
        meetingText.setText(thisMeeting.getName());
        dateText.setText(thisMeeting.getDate());
        timeText.setText(thisMeeting.getTime());
        locationText.setText(String.format("%s%s%s%s", "Longitude: ",String.format("%1$,.2f", thisMeeting.getLongitude()), " Latitude: ",String.format("%1$,.2f", thisMeeting.getLatitude())));
        attendeesText.setText(thisMeeting.getClassSize());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_meeting_view);
    }
}
