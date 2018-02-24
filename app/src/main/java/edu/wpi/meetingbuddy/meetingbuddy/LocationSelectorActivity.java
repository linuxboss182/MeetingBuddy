package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class LocationSelectorActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button selectLocationBtn;
    private GoogleMap mMap;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LOCATION", "LocationSelectorActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        if(mapFragment != null) {
            Log.d("LOCATION", "Map Fragment is not null");
        }
        mapFragment.getMapAsync(this);


        selectLocationBtn = (Button) findViewById(R.id.selectLocationBtn);

        selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Worcester, Mass,
        // and move the map's camera to the same location.
        LatLng worcester = new LatLng(42.273706, -71.808413);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(worcester, 18), 2000, null);
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                counter++;
                if (counter < 2) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Dropped Pin"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 2000, null);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("picked_point",latLng);
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot set more than one meeting point.", Toast.LENGTH_LONG).show();
                }


            }
        });

        Log.d("LOCATION", "OnMapReady finished");
    }



}
