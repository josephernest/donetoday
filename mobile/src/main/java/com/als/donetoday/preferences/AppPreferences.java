package com.als.donetoday.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.als.util.Logr;

public class AppPreferences {

    // In the past, we saved the version code as int or String.
    // So we need to be able to read both.
    protected static final String VersionCode1 = "prefVersionCode"; // saved as String
    protected static final String VersionCode2 = "versionCode"; // saved as int

    protected static final String NumberOfStarts = "prefNumberOfStarts"; // saved as String
    protected static final String MSFirstStart   = "prefMSFirstStart"; // saved as String


    public static int getVersionCode(final Context ctxt, final int defaultVersionCode) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);

        try {
            return Integer.parseInt(prefs.getString(VersionCode1, "" + defaultVersionCode));
        } catch (final Throwable t) {
            try {
                return prefs.getInt(VersionCode2, -1);
            } catch (final ClassCastException e) {
                Logr.ignored(t);
                return defaultVersionCode;
            }
        }
    }

    public static boolean isVersionCodeSet(final Context ctxt) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return prefs.contains(VersionCode1) || prefs.contains(VersionCode2);
    }

    public static void setVersionCode(final Context ctxt, final int versionCode) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);

        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(VersionCode1, "" + versionCode);
        editor.apply();
    }


    public static int getNumberOfStarts(final Context ctxt) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        try {
            return Integer.parseInt(prefs.getString(NumberOfStarts, "1"));
        } catch (final Throwable t) {
            Logr.ignored(t);
            return 1;
        }
    }

    public static long getMsFirstStart(final Context ctxt) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        try {
            return Long.parseLong(prefs.getString(MSFirstStart, "" + System.currentTimeMillis()));
        } catch (final Throwable t) {
            Logr.ignored(t);
            return System.currentTimeMillis();
        }
    }

    public static void setNumberOfStarts(final Context ctxt, final int numberOfStarts) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);

        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NumberOfStarts, "" + numberOfStarts);

        if (!prefs.contains(MSFirstStart)) {
            editor.putString(MSFirstStart, "" + System.currentTimeMillis());
        }

        editor.apply();
    }
}
