package com.djsg38.locationprivacyapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user404d on 10/20/16.
 */

public class Preference extends RealmObject {
    @PrimaryKey
    public String name;

    public boolean isService;
    public double privacyScale;
    public RealmList<Location> before,after;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isService() {
        return isService;
    }

    public void setService(boolean service) {
        isService = service;
    }

    public double getPrivacyScale() {
        return privacyScale;
    }

    public void setPrivacyScale(double privacyScale) {
        this.privacyScale = privacyScale;
    }

    public RealmList<Location> getBefore() {
        return before;
    }

    public void setBefore(RealmList<Location> before) {
        this.before = before;
    }

    public RealmList<Location> getAfter() {
        return after;
    }

    public void setAfter(RealmList<Location> after) {
        this.after = after;
    }



}
