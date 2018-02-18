package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by s7sal on 2/15/2018.
 */

public class CreateMeetingActivity extends AppCompatActivity {

    EditText dateET;
    EditText timeET;
    EditText peopleET;
    EditText locationET;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    Calendar myCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myCalendar = Calendar.getInstance();
        dateET = findViewById(R.id.dateET);
        timeET = findViewById(R.id.timeET);
        peopleET = findViewById(R.id.peopleET);
        locationET = findViewById(R.id.locationET);


        ////////// Listeners ///////////////////////////
        peopleET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               Intent i = new Intent(getApplicationContext(), PeopleSearchActivity.class);
               startActivity(i);
            }
        });

        locationET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), LocationSelectorActivity.class);
                //startActivity(i);
                pickPointOnMap();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        dateET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateMeetingActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalendar.set(Calendar.MINUTE, minute);
                    updateTime();
                }
            }
        };

        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateMeetingActivity.this, time, hour, minute, false);
                timePickerDialog.setTitle("Choose time:");
                timePickerDialog.show();
            }
        });
    }

    static final int PICK_MAP_POINT_REQUEST = 999;  // The request code
    private void pickPointOnMap() {
        Intent pickPointIntent = new Intent(this, LocationSelectorActivity.class);
        startActivityForResult(pickPointIntent, PICK_MAP_POINT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_MAP_POINT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                LatLng latLng = (LatLng) data.getParcelableExtra("picked_point");
                locationET.setText(latLng.latitude + ", " + latLng.longitude);
            }
        }
    }


    private void updateDate() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateET.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTime() {
        String myFormat = "hh:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        timeET.setText(sdf.format(myCalendar.getTime()));
    }

}

