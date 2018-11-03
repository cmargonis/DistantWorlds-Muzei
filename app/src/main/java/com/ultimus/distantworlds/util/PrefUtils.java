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
