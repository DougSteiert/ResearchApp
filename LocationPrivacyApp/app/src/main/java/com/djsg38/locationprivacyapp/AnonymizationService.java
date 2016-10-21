package com.djsg38.locationprivacyapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class AnonymizationService extends Service {

    //public static final String INTENT_FILTER = "AnonymizationIntent";

    MainActivity mMainActivity;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            logLocation();
        }
    };

    // Initiate a timer for logging location
    public void startTimer() {
        Log.i("Timer", "Started timer.");
        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMainActivity = new MainActivity();
        initiateMockLocs();
    }

    // Just log the mock location and feed to UI
    public void logLocation() {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mMainActivity.mGoogleApiClient);
        mMainActivity.previousLoc = loc;
        Log.i("Mock Location: ", loc.toString());
        Toast.makeText(this, "Mocked (lat, lng): (" + String.valueOf(loc.getLatitude()) + ", " + String.valueOf(loc.getLongitude()) + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Anonymization Service Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Anonymization Started", Toast.LENGTH_SHORT).show();
        showNotif();
        return Service.START_STICKY;
    }

    private void stopAnonymization() {
        this.stopService(new Intent(getBaseContext(), AnonymizationService.class));
    }

    private void showNotif() {
        //Intent intent = new Intent(INTENT_FILTER);
        //intent.putExtra("action", "stopService");

        Notification notif = new Notification.Builder(this)
                .setContentTitle("LPAnon")
                .setSmallIcon(R.drawable.ic_plusone_standard_off_client)
                .build();

        // Do not let user clear notification
        notif.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1395, notif);
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
            LocationServices.FusedLocationApi.setMockMode(mMainActivity.mGoogleApiClient, true);
            LocationServices.FusedLocationApi.setMockLocation(mMainActivity.mGoogleApiClient, mockLoc);

            Location loc = LocationServices.FusedLocationApi.getLastLocation(mMainActivity.mGoogleApiClient);
            Log.i("Mock Location: ", loc.toString());

            Log.i("Timer", "Going to start timer.");
            logLocation();
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String provider) {

    }
}
