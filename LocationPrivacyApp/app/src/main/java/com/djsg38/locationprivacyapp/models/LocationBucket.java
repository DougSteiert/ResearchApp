package com.djsg38.locationprivacyapp.models;

import io.realm.RealmObject;

/**
 * Created by user404d on 10/26/16.
 */

public class LocationBucket extends RealmObject {
    public int locHash, count;

    public int getLocHash() {
        return locHash;
    }

    void setLocHash(int hash) {
        this.locHash = hash;
    }

    public int getCount() {
        return count;
    }

    void setCount(int c) {
        this.count = c;
    }

    void incCount() {
        this.count++;
    }
}
