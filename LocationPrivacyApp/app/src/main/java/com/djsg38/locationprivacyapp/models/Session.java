package com.djsg38.locationprivacyapp.models;

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

}
