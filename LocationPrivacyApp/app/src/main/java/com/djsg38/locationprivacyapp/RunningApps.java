package com.djsg38.locationprivacyapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.djsg38.library.AndroidProcesses;
import com.squareup.picasso.Picasso;

import com.djsg38.locationprivacyapp.listRunningApps.fragments.ProcessListFragment;
import com.djsg38.locationprivacyapp.listRunningApps.picasso.AppIconRequestHandler;

public class RunningApps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, new ProcessListFragment()).commit();
        }
    }

}
