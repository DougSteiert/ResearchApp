package com.djsg38.locationprivacyapp.PreferenceUI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.djsg38.locationprivacyapp.R;
import com.djsg38.locationprivacyapp.models.Preference;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by user404d on 11/2/16.
 */

public class PreferenceListAdapter extends RealmBaseAdapter<Preference> implements ListAdapter {
    public static class ViewHolder {
        TextView name;
        TextView rating;
    }

    public PreferenceListAdapter(Context context, OrderedRealmCollection<Preference> realmResults) {
        super(context, realmResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.preference_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.preferenceListItemName);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.preferenceListItemRating);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Preference preference = adapterData.get(position);
        viewHolder.name.setText(preference.getName().toString());
        viewHolder.rating.setText(String.valueOf(preference.getPrivacyScale()));
        return convertView;
    }
}
