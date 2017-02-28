package com.djsg38.locationprivacyapp.models;

import android.util.Log;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Session extends RealmObject {
    public RealmList<Location> realLocations;
    public RealmList<Trace> multipleTraces;
    public RealmList<Location> mockLocations;
    public RealmList<Location> mobilityTrace;
    public RealmList<Preference> preferences;

    public RealmList<Preference> getPreferences() {
        return preferences;
    }

    public Session setPreferences(RealmList<Preference> preferences) {
        this.preferences = preferences;
        return this;
    }

    public void addNewMultipleTrace(Trace trace) {
        multipleTraces.add(trace);
    }

    public RealmList<Trace> getMultipleTraces() {
        return multipleTraces;
    }

    public RealmList<Location> getRealLocations() {
        return realLocations;
    }

    public Session setRealLocations(RealmList<Location> realLocations) {
        this.realLocations = realLocations;
        return this;
    }

    public RealmList<Location> getMockLocations() {
        return mockLocations;
    }

    public Session setMockLocations(RealmList<Location> mockLocations) {
        this.mockLocations = mockLocations;
        return this;
    }

    public RealmList<Location> getMobilityTrace() {
        return mobilityTrace;
    }

    public Session setMobilityTrace(RealmList<Location> mobilityTrace) {
        this.mobilityTrace = mobilityTrace;
        return this;
    }

    public void addNewRealLocation(android.location.Location location) {
        realLocations.add(new Location()
                .setLat(location.getLatitude())
                .setLong(location.getLongitude()));
    }

    public void addNewMockLocation(android.location.Location location) {
        mockLocations.add(new Location()
                .setLat(location.getLatitude())
                .setLong(location.getLongitude()));
    }
}
