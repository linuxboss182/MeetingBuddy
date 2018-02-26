package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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
import java.util.List;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Created by jtgaulin on 2/21/18.
 */

public class ApplicationManager extends Application implements OnSuccessListener<Void>, OnFailureListener {

    ////Network
    private NetworkManager networkManager = new NetworkManager(this);
    public NetworkManager getNetworkManager(){
        return networkManager;
    }

    ////Geofence
    private String ENTER_EVENT = "enter";
    private Float geofenceRadius  = 30.0f;

    ////Google API
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;


    void setupGeofences(ArrayList<Meeting> meetings){
        // Setup Geofences
        mGeofenceList = new ArrayList<>();
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        for(Meeting m : meetings) {
//            System.out.println("Meeting: " + m.getName());
            Geofence newFence = new Geofence.Builder().setRequestId(String.format("%s",m.getMeetingID()))
                    .setCircularRegion(
                            m.getLatitude(),
                            m.getLongitude(),
                            geofenceRadius //meters
                    )
                    .setExpirationDuration(1000 * 60 * 10) //10 minutes
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();

            mGeofenceList.add(newFence);
        }

        if(!mGeofenceList.isEmpty()) {
            try {
                mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                        .addOnSuccessListener(this)
                        .addOnFailureListener(this);
            } catch (SecurityException e) {
                Log.e("Geofencelient", e.toString());
            }

            LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(this);
            GeofenceBroadcastReceiver receiver = new GeofenceBroadcastReceiver(this);
            lbc.registerReceiver(receiver, new IntentFilter("googlegeofence"));
        }
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        PendingIntent mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onSuccess(Void aVoid) {

    }

    @Override
    public void onFailure(@NonNull Exception e) {

    }

    private class GeofenceBroadcastReceiver extends BroadcastReceiver
    {
        public GeofenceBroadcastReceiver(ApplicationManager applicationManager) {

        }

        public void onReceive(Context context, Intent intent) {
            ArrayList<String> geofenceIDs = intent.getStringArrayListExtra("IDs");

            List<String> fences = new ArrayList<String>();
            for(Geofence geo : mGeofenceList){
                fences.add(geo.getRequestId());
            }

            for (String g : geofenceIDs)
            {
                if (fences.contains(g))
                {
                    System.out.println("Arrived at meeting: " + g);

                    //Tell backend to mark us arrived
                    arrivedMeeting(g);
                }
            }
        }
    }

    private void arrivedMeeting(String meetingID){
        JSONObject creds = new JSONObject();
        try {
            creds.put("meetingID", Integer.parseInt(meetingID));
            creds.put("status", "arrived");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkManager.post(NetworkManager.url+"/updateAttendance", creds.toString(), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("Failed to connect");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
//                    }
//                });
            }
            @Override
            public void onResponse(Response response) throws IOException {
//                final String responseStr = response.body().string();
//                final int statusCode = response.code();
//                try {
//                    final JSONObject jsonRes = new JSONObject(responseStr);
//                    final String status = jsonRes.getString("status");
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                    if(status.equals("success")) {
//
//                        //Go to My meetings, send it my account information
//                        Intent i = new Intent(getApplicationContext(), MyMeetingActivity.class);
//                        startActivity(i);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

}
