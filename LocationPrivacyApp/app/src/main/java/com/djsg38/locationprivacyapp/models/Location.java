package com.djsg38.locationprivacyapp.models;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Location extends RealmObject {
    public double Long, Lat;

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

}

