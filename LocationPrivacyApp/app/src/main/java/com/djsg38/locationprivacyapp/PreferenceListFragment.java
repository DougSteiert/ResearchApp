package com.djsg38.locationprivacyapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import java.util.zip.Inflater;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

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
                Fragment pfm = PreferenceFragment.newInstance("ayy", "lmao");
                fmt.replace(R.id.fragment_content, pfm);
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
                        Snackbar.make(getActivity().findViewById(R.id.preference_list_container), "preference chosen", 1500).show();
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
