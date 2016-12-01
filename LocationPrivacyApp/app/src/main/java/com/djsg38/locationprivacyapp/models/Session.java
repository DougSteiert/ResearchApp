package com.djsg38.locationprivacyapp.models;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by user404d on 10/18/16.
 */

public class Session extends RealmObject {
    public RealmList<Location> realLocations;
    public RealmList<Location> mockLocations;
    public RealmList<Preference> preferences;

    private Realm realm;

    public RealmList<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(RealmList<Preference> preferences) {
        this.preferences = preferences;
    }

    public RealmList<Location> getRealLocations() {
        return realLocations;
    }

    public void setRealLocations(RealmList<Location> realLocations) {
        this.realLocations = realLocations;
    }

    public RealmList<Location> getMockLocations() {
        return mockLocations;
    }

    public void setMockLocations(RealmList<Location> mockLocations) {
        this.mockLocations = mockLocations;
    }

    public void addNewRealLocation(android.location.Location location) {
        if (realm == null) realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        com.djsg38.locationprivacyapp.models.Location new_loc = new com.djsg38.locationprivacyapp.models.Location();
        new_loc.setLat(location.getLatitude());
        new_loc.setLong(location.getLongitude());
        Session session = realm.where(Session.class).findFirst();
        session.getRealLocations().add(new_loc);
        // maybe trim list when it gets large
        if (session.getRealLocations().size() > 50) {
            for (com.djsg38.locationprivacyapp.models.Location loc : session.getRealLocations()) {
                Log.i("Locations tracked: ",
                        String.valueOf(loc.getLat()) + ", " + String.valueOf(loc.getLong()));
            }
            int locs = session.getRealLocations().size();
            while (locs-- > 25) {
                session.getRealLocations().deleteLastFromRealm();
            }
        }
        realm.commitTransaction();
    }

    public void addNewMockLocation(android.location.Location location) {
        if (realm == null) realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        com.djsg38.locationprivacyapp.models.Location new_loc = new com.djsg38.locationprivacyapp.models.Location();
        new_loc.setLat(location.getLatitude());
        new_loc.setLong(location.getLongitude());
        Session session = realm.where(Session.class).findFirst();
        session.getMockLocations().add(new_loc);
        // maybe trim list when it gets large
        if (session.getMockLocations().size() > 50) {
            for (com.djsg38.locationprivacyapp.models.Location loc : session.getMockLocations()) {
                Log.i("Locations tracked: ",
                        String.valueOf(loc.getLat()) + ", " + String.valueOf(loc.getLong()));
            }
            int locs = session.getMockLocations().size();
            while (locs-- > 25) {
                session.getMockLocations().deleteLastFromRealm();
            }
        }
        realm.commitTransaction();
    }
}
