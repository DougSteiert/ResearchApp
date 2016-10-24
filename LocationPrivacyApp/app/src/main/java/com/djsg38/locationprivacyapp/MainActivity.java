package com.djsg38.locationprivacyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.os.Handler;

import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button showProcesses;
    Button activateMockLocs;
    TextView latView;
    TextView longView;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    static Boolean activated = false;

    AnonymizationService anonymizationService;

    // Wait for click on "List Running Applications" button and create new activity
    View.OnClickListener listRunningApps = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RunningApps.class);
            startActivity(intent);
        }
    };

    // Wait for clock on "Activate Mock Locations"
    View.OnClickListener activateMockLocations = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("MockLocs", "Clicked mock Locs.");

            if (!activated) {
                activateMockLocs.setText("Deactivate Mock Locations");
                startService(new Intent(MainActivity.this, AnonymizationService.class));
                activated = true;
            } else {
                activateMockLocs.setText("Activate Mock Locations");
                stopService(new Intent(MainActivity.this, AnonymizationService.class));
                activated = false;
            }
        }
    };

    public void updateCoords(double lat, double lng) {
        latView.setText("Lat: " + String.valueOf(lat));
        longView.setText("Long: " + String.valueOf(lng));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        showProcesses = (Button) findViewById(R.id.listApps);
        showProcesses.setOnClickListener(listRunningApps);

        activateMockLocs = (Button) findViewById(R.id.activateMock);
        activateMockLocs.setOnClickListener(activateMockLocations);

        latView = (TextView) findViewById(R.id.latView);
        longView = (TextView) findViewById(R.id.longView);

        anonymizationService = new AnonymizationService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Initialize a GoogleApiClient object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    // Initialize a LocationRequest object
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat =  location.getLatitude();
        double lng = location.getLongitude();

        latView.setText("Lat: " + String.valueOf(lat));
        longView.setText("Long: " + String.valueOf(lng));
    }

    @Override
    public void onConnected(Bundle bundle) {
        //do things related to location access.
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
