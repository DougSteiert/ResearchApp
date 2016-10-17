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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button showProcesses;
    Button activateMockLocs;

    TextView latView;
    TextView longView;

    Boolean activated = false;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    Location previousLoc;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            logLocation();
        }
    };

    // Just log the mock location and feed to UI
    public void logLocation() {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        previousLoc = loc;
        Log.i("Mock Location: ", loc.toString());
        Toast.makeText(this, "Mocked (lat, lng): (" + String.valueOf(loc.getLatitude()) + ", " + String.valueOf(loc.getLongitude()) + ")", Toast.LENGTH_SHORT).show();
    }

    // Initiate a timer for logging location
    public void startTimer() {
        Log.i("Timer", "Started timer.");
        handler.postDelayed(runnable, 10000);
    }

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
                initiateMockLocs();
                activated = true;
            } else {
                activateMockLocs.setText("Activate Mock Locations");
                stopMockLocs();
                activated = false;
            }
        }
    };

    // Stop faking the location
    public void stopMockLocs() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Log.i("MockLocs", "Deactivating mock locs");
        handler.removeCallbacks(runnable);
        LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, false);
        Toast.makeText(this, "Your current location is: (" + String.valueOf(loc.getLatitude()) + ", " + String.valueOf(loc.getLongitude()) + ")", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // Begin faking the location
    public void initiateMockLocs() {
        Location mockLoc = new Location(LocationManager.NETWORK_PROVIDER);
        mockLoc.setLatitude(41.881832);
        mockLoc.setLongitude(-87.623177);
        mockLoc.setAccuracy(20);
        mockLoc.setTime(System.currentTimeMillis());
        mockLoc.setElapsedRealtimeNanos(System.nanoTime());

        try {
            LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, true);
            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, mockLoc);

            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i("Mock Location: ", loc.toString());

            Log.i("Timer", "Going to start timer.");
            logLocation();
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        latView = (TextView) findViewById(R.id.latView);
        longView = (TextView) findViewById(R.id.longView);

        showProcesses = (Button) findViewById(R.id.listApps);
        showProcesses.setOnClickListener(listRunningApps);

        activateMockLocs = (Button) findViewById(R.id.activateMock);
        activateMockLocs.setOnClickListener(activateMockLocations);

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    // Initialize a LocationRequest object
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    // Initialize a GoogleApiClient object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    public void onConnected(Bundle bundle) {
        //do things related to location access.
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double lat =  location.getLatitude();
        double lng = location.getLongitude();

        latView.setText("Lat: " + String.valueOf(lat));
        longView.setText("Long: " + String.valueOf(lng));

        if(activated) {
            initiateMockLocs();
        }
        else {
            previousLoc = location;
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
