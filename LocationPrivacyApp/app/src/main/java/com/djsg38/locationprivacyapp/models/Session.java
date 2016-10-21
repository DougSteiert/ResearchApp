package com.djsg38.locationprivacyapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by user404d on 10/18/16.
 */

public class Session extends RealmObject {
    public RealmList<Location> locations;
    public RealmList<Preference> preferences;

    public RealmList<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(RealmList<Preference> preferences) {
        this.preferences = preferences;
    }

    public RealmList<Location> getLocations() {
        return locations;
    }

    public void setLocations(RealmList<Location> locations) {
        this.locations = locations;
    }

}
