package com.djsg38.locationprivacyapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user404d on 10/20/16.
 */

public class Preference extends RealmObject {
    @PrimaryKey
    public String packageName;

    public String name;
    public boolean isService;
    public double privacyScale;
    public RealmList<Location> before, after;

    public String getPackageName() {
        return packageName;
    }

    public Preference setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getName() { return name; }

    public Preference setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isService() {
        return isService;
    }

    public Preference setService(boolean service) {
        this.isService = service;
        return this;
    }

    public double getPrivacyScale() {
        return privacyScale;
    }

    public Preference setPrivacyScale(double privacyScale) {
        this.privacyScale = privacyScale;
        return this;
    }

    public RealmList<Location> getBefore() {
        return before;
    }

    public Preference setBefore(RealmList<Location> before) {
        this.before = before;
        return this;
    }

    public RealmList<Location> getAfter() {
        return after;
    }

    public Preference setAfter(RealmList<Location> after) {
        this.after = after;
        return this;
    }



}
