package com.djsg38.locationprivacyapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djsg38.locationprivacyapp.R;
import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by user404d on 11/2/16.
 */

public class PreferenceListView extends LinearLayout {

    private Realm realm;
    private boolean initialized;

    final public View.OnClickListener new_pref_listener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Realm realm = Realm.getDefaultInstance();
            TextView pref_list_item = (TextView) v.findViewById(R.id.preferenceListItemName);
            final String pref_list_id = pref_list_item.getText().toString();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Session session = realm.where(Session.class).findFirst();
                    Preference pref = session.getPreferences().where().equalTo("name", pref_list_id).findFirst();
                    Snackbar.make(v, pref.toString(), 2500).show();
                }
            });
            realm.close();
        }
    };

    public PreferenceListView(Context context) {
        super(context);
        Log.i("preference list view", "created!");
        initialize(context);
    }

    public PreferenceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("preference list view", "created!");
        initialize(context);
    }

    public PreferenceListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("preference list view", "created!");
        initialize(context);
    }

    public void initialize(Context context) {
        this.initialized = false;
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inf.inflate(R.layout.fragment_preference_list, this);
        create_children();
        this.initialized = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.initialized) {
            this.initialized = false;
            create_children();
            this.initialized = true;
        }
    }

    private void create_children() {
        if(realm != null) {
            realm = Realm.getDefaultInstance();

            Log.i("Preference List", "On Draw for preference list called!");

            RealmList<Preference> prefList = realm.where(Session.class).findFirst().getPreferences();
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (Preference pref : prefList) {
                View pref_view = inf.inflate(R.layout.preference_list_item, this, false);
                pref_view.setOnClickListener(new_pref_listener);
                TextView prefText = (TextView) pref_view.findViewById(R.id.preferenceListItemName);
                prefText.setText(pref.getName());
                prefText = (TextView) pref_view.findViewById(R.id.preferenceListItemRating);
                prefText.setText(String.valueOf(pref.getPrivacyScale()));
                this.addView(pref_view);
            }

            realm.close();
        }
    }
}
