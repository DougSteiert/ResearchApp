package com.djsg38.locationprivacyapp.PreferenceUI;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.djsg38.locationprivacyapp.R;
import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreferenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferenceFragment extends Fragment {
    private static final String PREFERENCE_NAME = "preference_name",
            PREFERENCE_PACKAGE_NAME = "preference_package_name";

    private String preference_name,
            preference_package_name;

    private OnFragmentInteractionListener mListener;

    EditText edit_name;
    SeekBar edit_privacy_scale;
    Button save,delete;

    private Realm realm;

    public PreferenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Parameter 1.
     * @param package_name Parameter 2.
     * @return A new instance of fragment PreferenceFragment.
     */
    public static Fragment newInstance(String name, String package_name) {
        PreferenceFragment fragment = new PreferenceFragment();
        Bundle args = new Bundle();
        args.putString(PREFERENCE_NAME, name);
        args.putString(PREFERENCE_PACKAGE_NAME, package_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.preference_fragment, container, false);
        edit_name = (EditText) view.findViewById(R.id.applicationNameField);
        edit_privacy_scale = (SeekBar) view.findViewById(R.id.privacyScaleBar);
        save = (Button) view.findViewById(R.id.saveButton);
        delete = (Button) view.findViewById(R.id.deleteButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSavePressed((EditText) v.getRootView().findViewById(edit_name.getId()),
                        (SeekBar) v.getRootView().findViewById(edit_privacy_scale.getId()));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePressed();
            }
        });

        if (getArguments() != null) {
            preference_name = getArguments().getString(PREFERENCE_NAME);
            preference_package_name = getArguments().getString(PREFERENCE_PACKAGE_NAME);
            Preference preference = realm.where(Preference.class).equalTo("name", preference_name).findFirst();
            if(preference != null) {
                edit_name.setText(preference.getName());
                edit_privacy_scale.setProgress((int) preference.getPrivacyScale());
            }
        }
        return view;
    }

    public void onSavePressed(EditText edit_name, SeekBar edit_privacy_scale) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Session session = realm.where(Session.class).findFirst();
        Preference preference = session.getPreferences().where()
                .equalTo("packageName", preference_package_name).findFirst();
        if(preference == null) {
            Log.i("bad thing", preference_package_name);
        }
        else {
            preference.setName(edit_name.getText().toString());
            preference.setPrivacyScale(edit_privacy_scale.getProgress());
        }
        realm.commitTransaction();
        preference_name = preference.getName();
        Toast.makeText(getContext(), "Saved " + preference_name, Toast.LENGTH_LONG).show();
    }

    public void onDeletePressed() {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Session.class).findFirst().getPreferences()
                .where().equalTo("packageName", preference_package_name)
                .findAll().deleteAllFromRealm();
        realm.commitTransaction();
        Toast.makeText(getContext(), "Deleted " + preference_name, Toast.LENGTH_LONG).show();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        realm.close();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Realm realm);
    }
}
