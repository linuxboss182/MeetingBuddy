package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by s7sal on 2/15/2018.
 */

public class CreateMeetingActivity extends AppCompatActivity {

    static final int PICK_MAP_POINT_REQUEST = 999;  // The request code
    static final int PICK_PEOPLE_REQUEST = 888;  // The request code

    EditText nameET;
    String nameString;
    EditText dateET;
    String dateString;
    EditText timeET;
    String timeString;
    EditText peopleET;
    String peopleString;
    EditText locationET;
    String locationString;
    Button create;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    private NetworkManager networkManager;
    Calendar myCalendar;
    String lop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        networkManager = ((ApplicationManager) this.getApplication()).getNetworkManager();

        myCalendar = Calendar.getInstance();
        nameET = findViewById(R.id.nameET);
        dateET = findViewById(R.id.dateET);
        timeET = findViewById(R.id.timeET);
        peopleET = findViewById(R.id.peopleET);
        locationET = findViewById(R.id.locationET);
        create = findViewById(R.id.createMeetingBtn);

        ////////// Listeners ///////////////////////////

        peopleET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PeopleSearchActivity.class);
                //intent.putExtras(data);
                startActivityForResult(intent, PICK_PEOPLE_REQUEST);
            }
        });

        locationET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), LocationSelectorActivity.class);
//                startActivity(i);
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

        //Create account
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create account
                //...
                JSONObject creds = new JSONObject();
                try {
                    creds.put("name", nameString);
                    creds.put("date", dateString);
                    creds.put("time", timeString);
                    creds.put("people", peopleString);
                    creds.put("location", locationString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkManager.post(NetworkManager.url+"/newMeeting", creds.toString(), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        System.out.println("Failed to connect");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        final String responseStr = response.body().string();
                        final int statusCode = response.code();
                        try {
                            final JSONObject jsonRes = new JSONObject(responseStr);
                            final String status = jsonRes.getString("status");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                                }
                            });

                            if(status.equals("success")) {
                                //Get my account information
                                Account myAccount = new Account();
                                myAccount.fromJSON(jsonRes);

                                //Go to My meetings, send it my account information
                                Intent i = new Intent(getApplicationContext(), MyMeetingActivity.class);
                                i.putExtra("Account", myAccount);
                                startActivity(i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

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
        if (requestCode == PICK_PEOPLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                lop = data.getStringExtra("listOfPeople");
                peopleET.setText(lop);
                //peopleET.setText("Ron");

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("Name", nameString);
        savedInstanceState.putString("Time", timeString);
        savedInstanceState.putString("Date", dateString);
        savedInstanceState.putString("People", peopleString);
        savedInstanceState.putString("Location", locationString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        String name = savedInstanceState.getString("Name");
        String time = savedInstanceState.getString("Time");
        String date = savedInstanceState.getString("Date");
        String people = savedInstanceState.getString("People");
        String location = savedInstanceState.getString("Location");

        nameET.setText(name);
        timeET.setText(time);
        dateET.setText(date);
        peopleET.setText(people);
        locationET.setText(location);
    }

}

