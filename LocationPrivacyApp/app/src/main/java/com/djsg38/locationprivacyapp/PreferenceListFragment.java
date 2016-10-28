package com.djsg38.locationprivacyapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreferenceListFragment extends Fragment {

    private Realm realm;

    public PreferenceListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preference_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LinearLayout pref_list = (LinearLayout) getActivity().findViewById(R.id.preference_list);
                LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Session session = realm.where(Session.class).findFirst();
                for(Preference pref : session.getPreferences()) {
                    View pref_view = inf.inflate(R.layout.preference_list_item, pref_list, false);
                    TextView prefText = (TextView) pref_view.findViewById(R.id.preference_text);
                    prefText.setText(pref.toString());
                    pref_list.addView(pref_view);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }
}
