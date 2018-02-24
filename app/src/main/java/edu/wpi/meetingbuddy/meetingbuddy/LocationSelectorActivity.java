package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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
        //googleMap.addMarker(new MarkerOptions().position(worcester)
          //      .title("Marker in Worcester"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(worcester));
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng)
                     .title("Dropped Pin"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Intent returnIntent = new Intent();
                returnIntent.putExtra("picked_point",latLng);
                setResult(Activity.RESULT_OK, returnIntent);
            }
        });

        Log.d("LOCATION", "OnMapReady finished");
    }

}
