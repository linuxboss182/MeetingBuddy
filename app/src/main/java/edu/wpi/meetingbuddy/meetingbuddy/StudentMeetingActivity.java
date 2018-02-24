package edu.wpi.meetingbuddy.meetingbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import static android.app.PendingIntent.getActivity;

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
    Button callButton;
    String phoneNumber;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_meeting_view);

        Meeting thisMeeting = (Meeting) getIntent().getSerializableExtra("Meeting");
        // Get the organizer's phone number
        JSONObject jason = new JSONObject();
        try {
            jason.put("accountID", thisMeeting.getOrganizer());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManager networkManager = ((ApplicationManager) this.getApplication()).getNetworkManager();
        networkManager.get(NetworkManager.url + "/getAccount", jason.toString(), new Callback() {
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
                phoneNumber = response.body().string();
                System.out.println("Received phone number: " + phoneNumber);
            }
        });

        // Register widgets
        meetingText = findViewById(R.id.MeetingText);
        dateText = findViewById(R.id.DateText);
        timeText = findViewById(R.id.TimeText);
        locationText = findViewById(R.id.LocationText);
        organizerText = findViewById(R.id.OrganizerText);
        statusText = findViewById(R.id.StatusText);
        callButton = findViewById(R.id.callButton);

        // Init the widgets
        meetingText.setText(thisMeeting.getName());
        dateText.setText(thisMeeting.getDate());
        timeText.setText(thisMeeting.getTime());
        locationText.setText(String.format("%s%s%s%s", "Longitude: ",String.format("%1$,.2f", thisMeeting.getLongitude()), " Latitude: ",String.format("%1$,.2f", thisMeeting.getLatitude())));
        organizerText.setText(thisMeeting.getOrganizer());
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                startActivity(i);
            }
        });

    }
}
