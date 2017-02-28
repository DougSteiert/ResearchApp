package com.djsg38.locationprivacyapp.models;

import io.realm.RealmObject;

public class Semantics extends RealmObject {
    public String type;
    public double lat;
    public double lng;
    public String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Semantics setName(String placeName) {
        this.name = placeName;
        return this;
    }

    public Semantics setLng(double longitude) {
        this.lng = longitude;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public Semantics setLat(double latitude) {
        this.lat = latitude;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public Location getLocation() {
        Location location = new Location();
        location.setLong(lng);
        location.setLat(lat);

        return location;
    }

    public Semantics setType(String aType) {
        this.type = aType;
        return this;
    }
}
