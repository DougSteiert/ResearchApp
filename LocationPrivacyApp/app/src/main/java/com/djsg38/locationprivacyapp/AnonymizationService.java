package com.djsg38.locationprivacyapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
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

import java.util.concurrent.ExecutionException;

public class AnonymizationService extends Service {

    LocationAnonymizer locationAnonymizer;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        AnonymizationService getService() {
            return AnonymizationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationAnonymizer.stopMockLocs();
        //stopService();
        Toast.makeText(this, "Anonymization Service Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Anonymization Started", Toast.LENGTH_SHORT).show();
        showNotif();

        try {
            int kValue = intent.getIntExtra("kValue", 1);
            locationAnonymizer = new LocationAnonymizer(this, this, kValue);
            Log.i("locationAnonymizer", "Alive and Well");
        }
        catch (Exception e) {
            String msg = (e.getMessage() == null)?"Failed to init.":e.getMessage();
            Log.i("Init error", msg);
        }
        return mBinder;
    }

    // Stop the service from running
    public void stopService() {
        locationAnonymizer.stopMockLocs();
    }

    private void showNotif() {
        //Intent intent = new Intent(INTENT_FILTER);
        //intent.putExtra("action", "stopService");

        Notification notif = new Notification.Builder(this)
                .setContentTitle("LPAnon")
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .build();

        // Do not let user clear notification
        notif.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1395, notif);
    }
}
