package com.djsg38.locationprivacyapp.models;

import android.util.Log;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by user404d on 10/18/16.
 */

public class Session extends RealmObject {
    public RealmList<Location> realLocations;
    public RealmList<Location> mockLocations;
    public RealmList<Preference> preferences;

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
        Location new_loc = new Location();
        new_loc.setLat(location.getLatitude());
        new_loc.setLong(location.getLongitude());
        realLocations.add(new_loc);
        // maybe trim list when it gets large
        if (realLocations.size() > 50) {
            for (Location loc : realLocations) {
                Log.i("Locations tracked: ",
                        String.valueOf(loc.getLat()) + ", " + String.valueOf(loc.getLong()));
            }
            int locs = realLocations.size();
            while (locs-- > 25) {
                realLocations.deleteLastFromRealm();
            }
        }
    }

    public void addNewMockLocation(android.location.Location location) {
        Location new_loc = new Location();
        new_loc.setLat(location.getLatitude());
        new_loc.setLong(location.getLongitude());
        mockLocations.add(new_loc);
        // maybe trim list when it gets large
        if (mockLocations.size() > 50) {
            for (Location loc : mockLocations) {
                Log.i("Locations tracked: ",
                        String.valueOf(loc.getLat()) + ", " + String.valueOf(loc.getLong()));
            }
            int locs = mockLocations.size();
            while (locs-- > 25) {
                mockLocations.deleteLastFromRealm();
            }
        }
    }

    public void addPreference(String packageName, String applicationName) {

    }
}
