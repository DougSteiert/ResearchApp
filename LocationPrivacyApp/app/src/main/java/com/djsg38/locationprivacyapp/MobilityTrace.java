package com.djsg38.locationprivacyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.djsg38.locationprivacyapp.models.Location;
import com.djsg38.locationprivacyapp.models.Semantics;
import com.djsg38.locationprivacyapp.models.Session;
import com.djsg38.locationprivacyapp.models.Trace;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import io.realm.Realm;

public class MobilityTrace extends AppCompatActivity implements OnMapReadyCallback {
    private Realm realm;
    private GoogleMap mob_map;

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
        realm.close();
        realm = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mob_map = googleMap;

        drawMobilityTrace(mob_map);
    }

    private void drawMobilityTrace(GoogleMap mob_map) {
        Session session = realm.where(Session.class).findFirst();

        if (session.getMobilityTrace().size() > 1) {
            PolylineOptions mob_trace = new PolylineOptions();

            for(com.djsg38.locationprivacyapp.models.Location loc : session.getRealLocations()) {
                mob_map.addMarker(new MarkerOptions()
                        .position(new LatLng(loc.getLat(),
                                loc.getLong()))
                        .title("Real Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }

            Location ends = session.getMobilityTrace().last();

            /*mob_map.addMarker(new MarkerOptions()
                    .position(new LatLng(ends.getLat(),
                            ends.getLong()))
                    .title("Current Location " + String.valueOf(ends.getTime()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/

            Boolean isReal = false;
            for(Trace reportedLoc : session.getMultipleTraces()) {
                /*for(com.djsg38.locationprivacyapp.models.Location loc : session.getRealLocations()) {
                    if (reportedLoc. == loc) {
                        isReal = true;
                    }
                }*/
                Log.i("hdkfds", reportedLoc.toString());
                /*Location location = reportedLoc.getLocation();
                Log.i("hmmm", String.valueOf(location.getLat()));
                if(!isReal) {
                    mob_map.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLat(),
                                    location.getLong()))
                            .title("Reported Location " + String.valueOf(reportedLoc.getName()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }*/
            }

            for(Location reported_loc : session.getMobilityTrace()) {
                LatLng pos = new LatLng(reported_loc.getLat(),
                        reported_loc.getLong());
                mob_trace.add(pos);
            }

            mob_map.addPolyline(mob_trace.color(Color.BLUE).width(5));

            CameraPosition move = new CameraPosition.Builder()
                    .target(new LatLng(ends.getLat(),
                            ends.getLong()))
                    .zoom(5)
                    .build();

            mob_map.animateCamera(CameraUpdateFactory.newCameraPosition(move), 2000, null);
        }
    }
}
