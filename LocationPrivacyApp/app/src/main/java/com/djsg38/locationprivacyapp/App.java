package com.djsg38.locationprivacyapp;

import android.app.Application;

import com.djsg38.library.AndroidProcesses;
import com.djsg38.locationprivacyapp.listRunningApps.picasso.AppIconRequestHandler;
import com.squareup.picasso.Picasso;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        AndroidProcesses.setLoggingEnabled(true);
        Picasso.setSingletonInstance(new Picasso.Builder(this)
                .addRequestHandler(new AppIconRequestHandler(this))
                .build());
    }

}
