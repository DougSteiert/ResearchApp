package com.djsg38.locationprivacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.location.Location;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.djsg38.locationprivacyapp.PreferenceUI.PreferenceList;
import com.djsg38.locationprivacyapp.models.Session;
import com.djsg38.locationprivacyapp.models.Trace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback
{

    Button showProcesses;
    Button activateMockLocs;
    private Button showPreferences, mobilityTrace;
    TextView latView;
    TextView longView;
    EditText kValue;
    private int inputValue;
    private String value;
    private GoogleMap mMap;

    Boolean isServiceRunning = false;

    android.location.LocationListener locationListener;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;

    Realm realm;
    RealmConfiguration config;

    ServiceConnection serviceConnection;

    static Boolean activated = false;

    AnonymizationService anonymizationService;

    // Wait for click on "List Running Applications" button and create new activity
    View.OnClickListener listRunningApps = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RunningApps.class);
            startActivity(intent);
        }
    };

    // Wait for clock on "Activate Mock Locations"
    View.OnClickListener activateMockLocations = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("MockLocs", "Clicked mock Locs.");

            if (!activated) {
                value = kValue.getText().toString();

                if (value.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a value for k.", Toast.LENGTH_SHORT).show();
                    return;
                }

                inputValue = Integer.parseInt(value);

                if (inputValue < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter a value greater than zero.", Toast.LENGTH_SHORT).show();
                    return;
                }

                isServiceRunning = true;
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                activateMockLocs.setText(R.string.deactivate_mock_locations);

                Intent intent = new Intent(MainActivity.this, AnonymizationService.class);
                intent.putExtra("kValue", inputValue);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(kValue.getWindowToken(), 0);
                activated = true;
            } else {
                isServiceRunning = false;
                activateMockLocs.setText(R.string.activate_mock_locations);
                unbindService(serviceConnection);
                activated = false;

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

                Session session = realm.where(Session.class).findFirst();

                for(com.djsg38.locationprivacyapp.models.Location loc : session.getMockLocations()) {
                    Log.i("Fake tracked: ",
                            String.valueOf(loc.getLat()) + ", " + String.valueOf(loc.getLong()));
                }

                for(com.djsg38.locationprivacyapp.models.Location loc : session.getRealLocations()) {
                    Log.i("Real tracked: ",
                            String.valueOf(loc.getLat()) + ", " + String.valueOf(loc.getLong()));
                }
            }
        }
    };

    View.OnClickListener listPreferences = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), PreferenceList.class);
            startActivity(intent);
        }
    };

    View.OnClickListener mobilityTracer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MobilityTrace.class);
            startActivity(intent);
        }
    };

    public void updateCoords(double lat, double lng) {
        latView.setText("Lat: " + String.valueOf(lat));
        longView.setText("Lng: " + String.valueOf(lng));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Realm.init(this);
        realm = Realm.getDefaultInstance(); /*
//        Comment this comment-block to essentially delete the DB.
//        This might need to be done if models are changed.

        config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
//        */

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                long sessionCount = realm.where(Session.class).count();
                if (sessionCount > 1) {
                    realm.deleteAll();
                }

                Session session = realm.where(Session.class).findFirst();

                if (session == null) {
                    realm.createObject(Session.class);
                }
//                /*
                else {
                    session.getMobilityTrace().deleteAllFromRealm();
                }
//                */
            }
        });

        ServiceUtilities.addLocationActivities(getApplicationContext(), realm);

        Log.i("Total sessions: ", String.valueOf(realm.where(Session.class).count()));

        MapFragment reported_loc = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        reported_loc.getMapAsync(this);

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AnonymizationService.LocalBinder binder = (AnonymizationService.LocalBinder) service;
                anonymizationService = binder.getService();
                activated = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                activated = false;
            }
        };

        anonymizationService = new AnonymizationService();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(!isServiceRunning) {

                    Boolean isIn = false;

                    Log.i("RealLoc", location.toString());
                    Session session = realm.where(Session.class).findFirst();

                    // Checks if the current location has already been placed in realLocations
                    for(com.djsg38.locationprivacyapp.models.Location loc : session.getRealLocations()) {
                        if(loc.getLat() == location.getLatitude() && loc.getLong() == location.getLongitude()) {
                            isIn = true;
                        }
                    }

                    // If the location is not in there, add it
                    // Trying to prevent duplicates
                    if(!isIn) {
                        realm.beginTransaction();
                        session.addNewRealLocation(location);
                        realm.commitTransaction();
                    }
                }

                Log.i("location", location.toString());
                updateCoords(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        latView = (TextView) findViewById(R.id.latView);
        longView = (TextView) findViewById(R.id.longView);
        kValue = (EditText) findViewById(R.id.kValue);

        showProcesses = (Button) findViewById(R.id.listApps);
        showProcesses.setOnClickListener(listRunningApps);

        activateMockLocs = (Button) findViewById(R.id.activateMock);
        activateMockLocs.setOnClickListener(activateMockLocations);

        showPreferences = (Button) findViewById(R.id.preferenceList);
        showPreferences.setOnClickListener(listPreferences);

        mobilityTrace = (Button) findViewById(R.id.mob_trace_button);
        mobilityTrace.setOnClickListener(mobilityTracer);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        if(isServiceRunning) unbindService(serviceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Initialize a GoogleApiClient object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateCoords(location.getLatitude(), location.getLongitude());
        if(mMap != null) {
            LatLng reported = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(reported)
                    .title("Reported Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            CameraPosition moveMap = new CameraPosition.Builder()
                    .target(reported)
                    .zoom(7)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(moveMap), 2000, null);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //do things related to location access.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RealmList<com.djsg38.locationprivacyapp.models.Location>
                mobilityTrace = realm.where(Session.class).findFirst().getMobilityTrace();
        if(mobilityTrace.size() > 0) {
            LatLng reported = new LatLng(mobilityTrace.last().getLat(),
                    mobilityTrace.last().getLong());
            mMap.addMarker(new MarkerOptions()
                    .position(reported)
                    .title("Reported Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            CameraPosition moveMap = new CameraPosition.Builder()
                    .target(reported)
                    .zoom(7)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(moveMap), 2000, null);
        }
    }
}
