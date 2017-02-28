package com.djsg38.locationprivacyapp.models;


import io.realm.RealmList;
import io.realm.RealmObject;

public class Trace extends RealmObject {
    public RealmList<Semantics> singleTrace;
    private int ID;

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return ID;
    }

    public void addNewSemanticTrace(Semantics place) {
        singleTrace.add(place);
    }

    public RealmList<Semantics> getSingleTrace() {
        return singleTrace;
    }
}
