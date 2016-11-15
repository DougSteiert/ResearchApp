package com.djsg38.locationprivacyapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreferenceListFragment extends Fragment {

    private Realm realm;
    private Random rand;

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

        rand = new Random();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Session session = realm.where(Session.class).findFirst();
                        Preference preference = realm.createObject(Preference.class, "Doot" + String.valueOf(rand.nextInt() % 100));
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

        ListView preferenceList = (ListView) getActivity().findViewById(R.id.preferenceList);
        RealmList<Preference> preferences = realm.where(Session.class).findFirst().getPreferences();
        final ListAdapter preferenceListAdapter = new PreferenceListAdapter(this.getContext(), preferences);
        preferenceList.setAdapter(preferenceListAdapter);
        preferenceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Preference preference = (Preference) preferenceListAdapter.getItem(position);
                final String preferenceName = preference.getName();

                FragmentTransaction fmt = getFragmentManager().beginTransaction();
                Fragment pfm = PreferenceFragment.newInstance(preferenceName);
                fmt.replace(R.id.content_preference_list, pfm);
                fmt.addToBackStack(null);
                fmt.commit();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(Preference.class).equalTo("name", preferenceName).findAll().deleteAllFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Snackbar.make(getActivity().findViewById(R.id.content_preference_list), "preference chosen", 1500).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
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
