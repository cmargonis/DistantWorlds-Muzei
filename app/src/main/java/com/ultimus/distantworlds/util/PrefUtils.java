/*
 *  Copyright 2018 Chris Margonis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ultimus.distantworlds.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ultimus on 29/3/2016.
 */
public class PrefUtils {

    public static int getUpdateFrequencyInMillis(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Interval is in minutes
        String strInterval = prefs.getString("update_frequency", "180");

        return Integer.parseInt(strInterval) * 60 * 1000;
    }

    public static boolean getPrefOnlyWifi(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean("update_only_wifi", true);
    }
}
