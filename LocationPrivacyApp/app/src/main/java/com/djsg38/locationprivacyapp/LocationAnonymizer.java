package com.djsg38.locationprivacyapp;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationAnonymizer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    AnonymizationService anonymizationService;
    MainActivity mainActivity;

    static Location previousLoc;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setMockLocation();
            logLocation();
        }
    };

    // Just log the mock location and feed to UI
    public void logLocation() {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        previousLoc = loc;
        Log.i("Mock Location: ", loc.toString());
        Toast.makeText(context, "Mocked (lat, lng): (" + String.valueOf(loc.getLatitude()) + ", " + String.valueOf(loc.getLongitude()) + ")", Toast.LENGTH_SHORT).show();
    }

    public LocationAnonymizer(Context context, AnonymizationService anonymizationService) {
        this.context = context;
        this.anonymizationService = anonymizationService;

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        initiateMockLocs();
    }

    // Initiate a timer for logging location
    public void startTimer() {
        Log.i("Timer", "Started timer.");
        handler.postDelayed(runnable, 10000);
    }

    // Stop faking the location
    public void stopMockLocs() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Log.i("MockLocs", "Deactivating mock locs");
        handler.removeCallbacks(runnable);
        LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, false);
        Toast.makeText(context, "Your current location is: (" + String.valueOf(loc.getLatitude()) + ", " + String.valueOf(loc.getLongitude()) + ")", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void setMockLocation() {
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
            LatLng location = new LatLng(mockLoc.getLatitude(), mockLoc.getLongitude());
            mainActivity.updateCoords(mockLoc.getLatitude(), mockLoc.getLongitude());

            Log.i("Mock Location: ", loc.toString());

            logLocation();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Begin faking the location
    public void initiateMockLocs() {
        setMockLocation();
        startTimer();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i("GoogleApiClient", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LocationChangedService", location.toString());

        if(MainActivity.activated) {
            initiateMockLocs();
        }
        else {
            previousLoc = location;
        }
    }

    // Initialize a GoogleApiClient object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
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
}
