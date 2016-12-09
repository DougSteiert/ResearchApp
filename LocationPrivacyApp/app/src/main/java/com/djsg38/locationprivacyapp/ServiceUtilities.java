package com.djsg38.locationprivacyapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.djsg38.locationprivacyapp.models.Preference;
import com.djsg38.locationprivacyapp.models.Session;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by user404d on 12/5/16.
 */

public class ServiceUtilities {
    static final String mainAppName = "Location Privacy App",
            mainPackageName = "com.djsg38.locationprivacyapp";

    static void addLocationActivities(Context context, Realm realm) {
        final PackageManager pm = context.getPackageManager();
        final ArrayList<ApplicationInfo> procInfos = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(PackageManager.GET_META_DATA);

        //make async to avoid ui lag
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Session session = realm.where(Session.class).findFirst();
                for (int i = 0; i < procInfos.size(); i++) {
                    String app = procInfos.get(i).packageName;

                    Preference preference = realm.where(Preference.class)
                            .equalTo("packageName", app).findFirst();

                    Integer coarse_perms = pm.checkPermission("android.permission.ACCESS_COARSE_LOCATION", app),
                            fine_perms = pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", app),
                            sys_perms = procInfos.get(i).flags & ApplicationInfo.FLAG_SYSTEM;
                    if (preference == null && !app.equals(mainPackageName)
                            && (coarse_perms == PackageManager.PERMISSION_GRANTED
                            || fine_perms == PackageManager.PERMISSION_GRANTED)
                            && sys_perms == 0) {
                        String appName = (String) pm.getApplicationLabel(procInfos.get(i));
                        preference = new Preference();
                        preference.setPackageName(app);
                        preference.setPrivacyScale(5);
                        preference.setName(appName);
                        preference.setService(false);
                        Log.i("preference", preference.toString());
                        session.getPreferences().add(preference);
                    }
                }
            }
        });
    }

}
