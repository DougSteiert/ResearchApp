/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.djsg38.locationprivacyapp.listRunningApps.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.djsg38.library.AndroidProcesses;
import com.djsg38.library.models.AndroidAppProcess;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AndroidAppProcessLoader extends AsyncTask<Void, Void, List<AndroidAppProcess>> {

  private final Listener listener;
  private final Context context;

  public AndroidAppProcessLoader(Context context, Listener listener) {
    this.context = context.getApplicationContext();
    this.listener = listener;
  }

  @Override protected List<AndroidAppProcess> doInBackground(Void... params) {
    List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

    // sort by app name
    Collections.sort(processes, new Comparator<AndroidAppProcess>() {

      @Override public int compare(AndroidAppProcess lhs, AndroidAppProcess rhs) {
        return Utils.getName(context, lhs).compareToIgnoreCase(Utils.getName(context, rhs));
      }
    });

    return processes;
  }

  @Override protected void onPostExecute(List<AndroidAppProcess> androidAppProcesses) {
    listener.onComplete(androidAppProcesses);
  }

  public interface Listener {

    void onComplete(List<AndroidAppProcess> processes);
  }

}
