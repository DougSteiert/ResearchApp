package com.djsg38.locationprivacyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button showProcesses;
    Button activateMockLocs;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            logLocation();
            handler.postDelayed(runnable, 10000);
        }
    };

    public void logLocation() {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("Mock Location: ", loc.toString());
    }

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

    View.OnClickListener activateMockLocations = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("MockLocs", "Clicked mock Locs.");
            initiateMockLocs();
        }
    };

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
            startTimer();
        }
        catch (Exception e) {
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

        showProcesses = (Button) findViewById(R.id.listApps);
        showProcesses.setOnClickListener(listRunningApps);

        activateMockLocs = (Button) findViewById(R.id.activateMock);
        activateMockLocs.setOnClickListener(activateMockLocations);

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

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
        initiateMockLocs();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
