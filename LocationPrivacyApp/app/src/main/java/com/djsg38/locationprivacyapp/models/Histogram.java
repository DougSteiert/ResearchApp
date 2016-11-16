package com.djsg38.locationprivacyapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by user404d on 10/26/16.
 */

public class Histogram extends RealmObject {
    public RealmList<LocationBucket> table;

    void updateCount(int hash) {
        table.where().equalTo("locHash", hash).findFirst().incCount();
    }

    public RealmList<LocationBucket> getTable() {
        return table;
    }

    public void setTable(RealmList<LocationBucket> table) {
        this.table = table;
    }
}
