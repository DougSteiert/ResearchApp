package com.djsg38.locationprivacyapp.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user404d on 10/26/16.
 */

public class LocationBucket extends RealmObject {
    public int locHash, count = 0;

    public int getLocHash() {
        return locHash;
    }

    public int getCount() {
        return count;
    }

    void incCount() {
        this.count++;
    }
}
