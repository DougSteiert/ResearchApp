package com.djsg38.locationprivacyapp.models;

import io.realm.RealmObject;

/**
 * Created by user404d on 10/20/16.
 */

public class Preference extends RealmObject {
    public String name;
    public boolean isService;
    public double privacyScale;

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

}
