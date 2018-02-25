package edu.wpi.meetingbuddy.meetingbuddy

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.widget.Toast

class GeofenceTransitionsIntentService : IntentService("geofencetransitionservice")
{
    override fun onHandleIntent(intent: Intent) {
        val geofencingEvent: GeofencingEvent = GeofencingEvent.fromIntent(intent);

        val lbcIntent = Intent("googlegeofence") //Send to any reciever listening for this

        if (geofencingEvent.hasError()) {
            //val errorMessage : String = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.e("GeofenceIntent", "error");
            return;
        }

        val geofenceTransition: Int = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            val triggeringGeofences: List<Geofence> = geofencingEvent.triggeringGeofences;
            val geofenceIDs : ArrayList<String> = ArrayList<String>()

            for (geo : Geofence in triggeringGeofences)
            {
                Log.d("Geofence Trigger", "Event: 'enter', ID: " + geo.requestId);
                geofenceIDs.add(geo.requestId)
            }

//            Toast.makeText(this, "ENTERED: " + TextUtils.join(", ", geofenceIDs), Toast.LENGTH_LONG).show()

            lbcIntent.putStringArrayListExtra("IDs", geofenceIDs);
            lbcIntent.putExtra("event", "enter");

            LocalBroadcastManager.getInstance(this).sendBroadcast(lbcIntent)  //Send the intent
        }
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            val triggeringGeofences: List<Geofence> = geofencingEvent.triggeringGeofences;
            val geofenceIDs : ArrayList<String> = ArrayList<String>()

            for (geo : Geofence in triggeringGeofences)
            {
                Log.d("Geofence Trigger", "Event: 'exit', ID: " + geo.requestId);
                geofenceIDs.add(geo.requestId)
            }

//            Toast.makeText(this, "EXITED: " + TextUtils.join(", ", geofenceIDs), Toast.LENGTH_LONG).show()

            lbcIntent.putStringArrayListExtra("IDs", geofenceIDs);
            lbcIntent.putExtra("event", "exit");

            LocalBroadcastManager.getInstance(this).sendBroadcast(lbcIntent)  //Send the intent
        }
        else
        {
            // Log the error.
            Log.e("geoeventerror", "Unknown geofence event: " + geofenceTransition.toString());
        }
    }
}
