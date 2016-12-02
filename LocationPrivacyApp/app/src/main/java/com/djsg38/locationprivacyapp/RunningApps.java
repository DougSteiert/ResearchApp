package com.djsg38.locationprivacyapp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RunningApps extends AppCompatActivity {

    ListView processList;
    ArrayList<String> processes;
    ArrayAdapter<String> arrayAdapter;

    private final int interval = 10000; // 10 seconds
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateProcesses();
            handler.postDelayed(runnable, interval);
        }
    };

    public void startTimer() {
        handler.postDelayed(runnable, interval);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        processes = new ArrayList<String>();
        processList = (ListView) findViewById(R.id.processList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, processes);
        processList.setAdapter(arrayAdapter);

        updateProcesses();

        startTimer();
    }

    public void updateProcesses() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService((ACTIVITY_SERVICE));

        processes.clear();
        List<ActivityManager.RunningServiceInfo> procInfos = activityManager.getRunningServices(50);

        for(int i = 0; i < procInfos.size(); i++) {
            processes.add(procInfos.get(i).process);
        }

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        runnable = null;
        handler = null;
    }
}
