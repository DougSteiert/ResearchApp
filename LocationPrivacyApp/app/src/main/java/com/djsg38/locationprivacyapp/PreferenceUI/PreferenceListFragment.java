package com.djsg38.locationprivacyapp.PreferenceUI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.djsg38.locationprivacyapp.R;
import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import io.realm.Realm;
import io.realm.RealmList;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                final String preferenceName = preference.getName(),
                        preferencePackageName = preference.getPackageName();

                FragmentTransaction fmt = getFragmentManager().beginTransaction();
                Fragment pfm = PreferenceFragment.newInstance(preferenceName,
                        preferencePackageName);
                fmt.replace(R.id.content_preference_list, pfm);
                fmt.addToBackStack(null);
                fmt.commit();
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
