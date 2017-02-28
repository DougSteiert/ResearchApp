package com.djsg38.locationprivacyapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.djsg38.locationprivacyapp.NearbyPlaces.ConvertPlaceTypes;
import com.djsg38.locationprivacyapp.NearbyPlaces.FindNearbyPlaces;
import com.djsg38.locationprivacyapp.NearbyPlaces.PlaceAttributes;
import com.djsg38.locationprivacyapp.models.Semantics;
import com.djsg38.locationprivacyapp.models.Session;
import com.djsg38.locationprivacyapp.models.Trace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmList;

public class LocationAnonymizer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    AnonymizationService anonymizationService;
    private static Random rand;
    private int realLocUse;
    private int randIndex;

    // Create manual place types to cycle through for first-round testing
    private ArrayList<String> manualPlaceTypes = new ArrayList<>();

    private int count = 0;

    com.djsg38.locationprivacyapp.models.Location currentLoc;

    Realm realm;

    GenerateNearbyCities cityGen;
    ArrayList<XMLAttributes> randLocs;
    ArrayList<String> cityNames;
    ArrayList<LatLng> cityCoords;

    ConvertPlaceTypes convertPlaceTypes;
    FindNearbyPlaces findNearbyPlaces;
    ArrayList<PlaceAttributes> places;
    List<Integer> types;
    List<String> typeStrings;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateMockLocation();
            Double randTime = LocationAnonymizer.rand.nextDouble() * 29000;
            int time = randTime.intValue() + 1000;

            // 30000 = 30s approx
            if(handler != null) handler.postDelayed(this, time);
        }
    };

    public LocationAnonymizer(Context context, AnonymizationService anonymizationService, Integer kValue) throws ExecutionException, InterruptedException {
        this.context = context;
        this.anonymizationService = anonymizationService;

        realm = Realm.getDefaultInstance();
        Session session = realm.where(Session.class).findFirst();

        // Add some values for manual testing
        manualPlaceTypes.add("restaurant");
        manualPlaceTypes.add("university");
        manualPlaceTypes.add("gas_station");
        manualPlaceTypes.add("movie_theater");
        manualPlaceTypes.add("cafe");
        manualPlaceTypes.add("health");

        findNearbyPlaces = new FindNearbyPlaces();
        places = new ArrayList<>();

        rand = new Random();

        cityGen = new GenerateNearbyCities();
        randLocs = cityGen.generateLocations(kValue);
        cityNames = new ArrayList<>();
        cityCoords = new ArrayList<>();

        int randTypeIndex;

        realm.beginTransaction();
        if(!session.getMultipleTraces().isEmpty()) {
            session.getMultipleTraces().deleteAllFromRealm();
        }
        realm.commitTransaction();

        // randLocs will be empty if kvalue was 1, which then causes mock locations to break
        // as getting a random index on something of size 0 fails

        int id = 2;
        for (XMLAttributes data : randLocs) {
            realm.beginTransaction();
            Trace trace = new Trace();
            trace.singleTrace = new RealmList<>();

            cityNames.add(data.getName());
            cityCoords.add(new LatLng(data.getLat(), data.getLng()));
            //Log.i("Fake city", data.getName());

            Location loc = new Location("");
            loc.setLatitude(data.getLat());
            loc.setLongitude(data.getLng());
            randTypeIndex = rand.nextInt(manualPlaceTypes.size());
            places = findNearbyPlaces.findNearbyPlaces(loc, manualPlaceTypes.get(randTypeIndex));

            for(PlaceAttributes place: places) {
                Semantics temp = new Semantics();
                if(place.getName() != null) {
                    temp.setLat(place.getLat());
                    temp.setLng(place.getLng());
                    temp.setName(place.getName());

                    if(!realm.isClosed()) {
                        trace.setID(id);
                        trace.addNewSemanticTrace(temp);
                    }
                }
                else {
                    trace.addNewSemanticTrace(temp.setName("").setLat(0).setLng(0));
                }
                Log.i("WHOA", temp.getName());
            }

            if(!trace.getSingleTrace().isEmpty()) {
                if(!realm.isClosed()) {
                    Log.i("ID", String.valueOf(trace.getID()));
                    for(Semantics place : trace.getSingleTrace()) {
                        Log.i("Trace", place.getName());
                    }
                    session.addNewMultipleTrace(trace);
                }
            }

            id++;
            realm.commitTransaction();
        }

        if(!session.getMultipleTraces().isEmpty()) {
            Log.i("size", String.valueOf(session.getMultipleTraces().size()));
            int i=1;
            for(Trace traces : session.getMultipleTraces()) {
                Log.i("IDhi",String.valueOf(traces.getID()));
                if(!traces.getSingleTrace().isEmpty()) {
                    for(Semantics place : traces.getSingleTrace()) {
                        if(place.getName() != null) {
                            Log.i("MultTraces" +i, String.valueOf(place.getName()));
                        }
                    }
                }
                else {
                    Log.i("Trace", "Is empty I guess?");
                }

                i++;
            }
        }
        else {
            Log.i("MultTrace", "is empty I guess");
        }

        currentLoc = new com.djsg38.locationprivacyapp.models.Location();
        // real locations is potentially empty
        currentLoc.setLat(session.getRealLocations().last().getLat());
        currentLoc.setLong(session.getRealLocations().last().getLong());

        Log.i("CurrentLoc", String.valueOf(currentLoc.getLat()) + ',' + String.valueOf(currentLoc.getLong()));

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
        handler.removeCallbacksAndMessages(null);
        runnable = null;
        handler = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, false);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        realm.close();
    }

    // Initialize the ability to set mock locations
    private void setMockLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update the current mocked location to a new value
    private void updateMockLocation() {
        randIndex = rand.nextInt(cityCoords.size());


        // Don't ask, don't tell
        // Just random number divisible by 3
        realLocUse = rand.nextInt(51);
        // Another method to ensure app doesn't go too long without using real loc


        Location mockLoc = new Location(LocationManager.NETWORK_PROVIDER);

        // If the number is divisible by 3, then go ahead and use the real location (random choice)
        if((((realLocUse % 3) == 0) && (count > 2)) || count == 10) {

            mockLoc.setLatitude(currentLoc.getLat());
            mockLoc.setLongitude(currentLoc.getLong());
            Log.i("YO", "used real loc");
            count = 0;
        }
        else {
            mockLoc.setLatitude(cityCoords.get(randIndex).latitude);
            mockLoc.setLongitude(cityCoords.get(randIndex).longitude);
            count++;
        }

        mockLoc.setAccuracy(20);
        mockLoc.setTime(System.currentTimeMillis());
        mockLoc.setElapsedRealtimeNanos(System.nanoTime());

        Log.i("MockedLoc", mockLoc.toString());

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, mockLoc);
            if(!realm.isClosed()) {
                String time = getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS");
                realm.beginTransaction();
                realm.where(Session.class).findFirst().getMobilityTrace()
                        .add(new com.djsg38.locationprivacyapp.models.Location()
                                .setLat(mockLoc.getLatitude())
                                .setLong(mockLoc.getLongitude())
                                .setTime(time));
                realm.commitTransaction();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    // Begin faking the location
    public void initiateMockLocs() {
        setMockLocation();
        updateMockLocation();
        startTimer();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onLocationChanged(final Location location) {
        Log.i("LocationChangedService", location.toString());

        Session session = realm.where(Session.class).findFirst();
        RealmList<com.djsg38.locationprivacyapp.models.Location>
                mockLocs = session.getMockLocations();

        if (mockLocs.where()
                .equalTo("Lat", location.getLatitude())
                .equalTo("Long", location.getLongitude()).findFirst() == null) {
            realm.beginTransaction();
            session.addNewMockLocation(location);
            realm.commitTransaction();
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
