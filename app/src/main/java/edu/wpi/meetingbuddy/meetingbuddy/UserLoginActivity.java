package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.IntentFilter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;



/**
 * Created by Anh on 2/15/2018.
 */

public class UserLoginActivity extends Activity{

    private EditText username;
    private EditText password;
    private Button login;
    private Button create_account;
    private String user_name;
    private String user_password;

    private NetworkManager networkManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_log_in);

        networkManager = ((ApplicationManager) this.getApplication()).getNetworkManager();

        ActivityCompat.requestPermissions(this,
                new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                100);

        username = findViewById(R.id.username);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_name = s.toString();
                //Save the username for authentication
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password = findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_password = s.toString();
                //Save the password for authentication
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        login = findViewById(R.id.log_in);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log in
                JSONObject creds = new JSONObject();
                try {
                    creds.put("username", user_name);
                    creds.put("password", user_password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkManager.post(NetworkManager.url+"/Login", creds.toString(), new Callback() {
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

//                        final String messageText = "Status code : " + response.code() +
//                                "n" +
//                                "Response body : " + responseStr;
//                        System.out.println("Received response");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                            }
                        });


                        if(status.equals("success")) {
                            //Setup geofences
                            networkManager.post(NetworkManager.url+"/getMyMeetings", "", new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    System.out.println("Failed to connect (applicationManager)");
                                }
                                @Override
                                public void onResponse(Response response) throws IOException {
                                    final String responseStr = response.body().string();
                                    ArrayList<Meeting> meetings = new ArrayList<>();

                                    try {
                                        //Get meetings from server
                                        final JSONArray jsonMeetings = new JSONArray(responseStr); //TODO FIX THIS
//                                        ArrayList<Meeting> meetings = new ArrayList<>();
                                        for(int i=0;i<jsonMeetings.length();i++){
                                            Meeting newMeeting = new Meeting();
                                            newMeeting.fromJSON(jsonMeetings.getJSONObject(i));
                                            meetings.add(newMeeting);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    // Setup Geofences
                                    ((ApplicationManager) getApplication()).setupGeofences(meetings);


                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                                }
                            });

                            //Get my account information
                            Account myAccount = new Account();
                            myAccount.fromJSON(jsonRes);

                            //Go to My meetings, send it my account information
	
							/*Intent i = new Intent(getApplicationContext(), FindAvailabilityActivity.class);
							i.putExtra("Account", myAccount);
							startActivity(i);*/
							
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

        create_account = findViewById(R.id.create_account);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),CreateAccountActivity.class);
                startActivity(i);
            }
        });


    }
}