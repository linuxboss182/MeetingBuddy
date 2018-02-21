package edu.wpi.meetingbuddy.meetingbuddy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Paul on 2/17/2018.
 */

public class StudentMeetingActivity extends AppCompatActivity {

    private TextView MeetingText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_meeting_view);

        Meeting thisMeeting = (Meeting) getIntent().getSerializableExtra("Meeting");

        MeetingText = findViewById(R.id.MeetingText);
        MeetingText.setText(thisMeeting.getName());

    }
}
