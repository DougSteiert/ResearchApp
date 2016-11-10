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

import java.util.ArrayList;
import java.util.Random;

public class LocationAnonymizer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    AnonymizationService anonymizationService;
    MainActivity mainActivity;
    Random rand;
    int randIndex;

    GenerateNearbyCities cityGen;
    ArrayList<XMLAttributes> randLocs;
    ArrayList<String> cityNames;
    ArrayList<LatLng> cityCoords;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("Handler", "hi");
            updateMockLocation();

            handler.postDelayed(this, 10000);
        }
    };

    public LocationAnonymizer(Context context, AnonymizationService anonymizationService) {
        this.context = context;
        this.anonymizationService = anonymizationService;

        cityGen = new GenerateNearbyCities();
        randLocs = cityGen.generateLocations();
        cityNames = new ArrayList<>();
        cityCoords = new ArrayList<>();

        for(XMLAttributes data : randLocs) {
            cityNames.add(data.getName());
            cityCoords.add(new LatLng(data.getLat(), data.getLng()));
        }

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    // Initiate a timer for logging location
    public void startTimer() {
        Log.i("Timer", "Started");
        runnable.run();
    }

    // Stop faking the location
    public void stopMockLocs() {
        handler.removeCallbacks(runnable);
        LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, false);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // Initialize the ability to set mock locations
    private void setMockLocation() {
        try {
            LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update the current mocked location to a new value
    private void updateMockLocation() {
        rand = new Random();
        randIndex = rand.nextInt(cityCoords.size());

        Location mockLoc = new Location(LocationManager.NETWORK_PROVIDER);
        mockLoc.setLatitude(cityCoords.get(randIndex).latitude);
        mockLoc.setLongitude(cityCoords.get(randIndex).longitude);
        mockLoc.setAccuracy(20);
        mockLoc.setTime(System.currentTimeMillis());
        mockLoc.setElapsedRealtimeNanos(System.nanoTime());

        try {
            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, mockLoc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Begin faking the location
    public void initiateMockLocs() {
        setMockLocation();
        updateMockLocation();
        startTimer();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i("GoogleApiClient", "Connected");

        initiateMockLocs();
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

        updateMockLocation();
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
