package com.djsg38.locationprivacyapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class PreferenceList extends AppCompatActivity {

    Realm realm;

    private RealmChangeListener<Realm> realmListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm realm) {
            invalidateViews();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Session session = realm.where(Session.class).findFirst();
                        Preference preference = realm.createObject(Preference.class);
                        preference.setName("Steve");
                        preference.setPrivacyScale(1.0);
                        preference.setService(false);
                        session.getPreferences().add(preference);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Snackbar.make(view, "successfully added new pref", 2000).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setupViews() {
        return;
    }

    private void invalidateViews() {
        return;
    }

}
