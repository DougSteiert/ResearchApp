package com.djsg38.locationprivacyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import io.realm.Realm;

public class PreferenceList
        extends AppCompatActivity
        implements PreferenceFragment.OnFragmentInteractionListener {

    Realm realm;

    public void onFragmentInteraction(Realm realm) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.content_preference_list, new PreferenceListFragment())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
