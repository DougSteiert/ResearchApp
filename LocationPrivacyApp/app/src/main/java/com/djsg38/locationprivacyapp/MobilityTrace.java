package com.djsg38.locationprivacyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.djsg38.locationprivacyapp.models.Location;
import com.djsg38.locationprivacyapp.models.Session;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by user404d on 12/7/16.
 */

public class MobilityTrace extends AppCompatActivity implements OnMapReadyCallback {
    private Realm realm;
    private RealmChangeListener mobiliityTraceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility_trace);

        realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MapFragment mob_tracer = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mobility_trace_map);
        mob_tracer.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.where(Session.class).findFirst().removeChangeListeners();
        mobiliityTraceListener = null;
        realm.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mob_map = googleMap;

        Session session = realm.where(Session.class).findFirst();
        PolylineOptions mob_trace = new PolylineOptions();
        for(Location reported_loc : session.getMockLocations()) {
            LatLng pos = new LatLng(reported_loc.getLat(),
                    reported_loc.getLong());
            mob_map.addMarker(new MarkerOptions()
                    .position(pos));
            mob_trace.add(pos);
        }
        mob_map.addPolyline(mob_trace.color(Color.RED).width(5));

        mobiliityTraceListener = new RealmChangeListener<Session>() {
            @Override
            public void onChange(Session session) {
                Location reported_loc = session.getMockLocations().last();
                LatLng pos = new LatLng(reported_loc.getLat(),
                        reported_loc.getLong());
                mob_map.addMarker(new MarkerOptions()
                        .position(pos));
                mob_map.addPolyline((new PolylineOptions())
                        .add(pos)
                        .color(Color.RED)
                        .width(5));
            }
        };

        realm.where(Session.class).findFirst().addChangeListener(mobiliityTraceListener);
    }
}
