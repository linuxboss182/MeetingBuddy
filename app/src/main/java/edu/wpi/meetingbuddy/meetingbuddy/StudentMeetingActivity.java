package edu.wpi.meetingbuddy.meetingbuddy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Paul on 2/17/2018.
 */

public class StudentMeetingActivity extends AppCompatActivity {

    TextView meetingText;
    TextView dateText;
    TextView timeText;
    TextView locationText;
    TextView organizerText;
    TextView statusText;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_meeting_view);

        Meeting thisMeeting = (Meeting) getIntent().getSerializableExtra("Meeting");

        // Register widgets
        meetingText = findViewById(R.id.MeetingText);
        dateText = findViewById(R.id.DateText);
        timeText = findViewById(R.id.TimeText);
        locationText = findViewById(R.id.LocationText);
        organizerText = findViewById(R.id.OrganizerText);
        statusText = findViewById(R.id.StatusText);

        // Init the widgets
        meetingText.setText(thisMeeting.getName());
        dateText.setText(thisMeeting.getDate());
        timeText.setText(thisMeeting.getTime());
        locationText.setText(String.format("%s%s%s%s", "Longitude: ",String.format("%1$,.2f", thisMeeting.getLongitude()), " Latitude: ",String.format("%1$,.2f", thisMeeting.getLatitude())));
        organizerText.setText(thisMeeting.getOrganizer());

    }
}
