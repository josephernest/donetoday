package com.als.donetoday.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.als.util.Cast;
import com.als.util.CollectionUtils;
import com.als.util.Logr;
import com.als.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class PreferenceUtils {

    public static String getStringPreferenceValue(Context ctxt, String preferenceName, String dflt) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
            return prefs.getString(preferenceName, dflt);
        } catch (Throwable e) {
            Logr.ignored(e);
            return dflt;
        }
    }

    public static void setStringPreferenceValue(Context ctxt, String preferenceName, String value) {
        SharedPreferences        prefs  = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(preferenceName, value);
        editor.apply();
    }

    public static Set<String> getStringSetPreferenceValue(Context ctxt, String preferenceName, Set<String> dflt) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
            String            v     = prefs.getString(preferenceName, "");

            if (StringUtils.isNullSpaceEmpty(v))
                return Cast.unchecked(Collections.EMPTY_SET);
            else
                return new HashSet<>(Arrays.asList(v.split(",")));
        } catch (Throwable e) {
            Logr.ignored(e);
            return dflt;
        }
    }

    public static void setStringSetPreferenceValue(Context ctxt, String preferenceName, Set<String> value) {
        SharedPreferences        prefs  = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(preferenceName, CollectionUtils.concat(value, ","));
        editor.apply();
    }

    public static int getIntPreferenceValue(Context ctxt, String preferenceName, int dflt) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
            return Integer.parseInt(prefs.getString(preferenceName, "" + dflt));
        } catch (Throwable e) {
            Logr.ignored(e);
            return dflt;
        }
    }

    //  def initIntPreference(ctx: Context, preferenceName: Symbol, value: Int) {
    //    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    //    if (!prefs.contains(preferenceName.name)) {
    //      val e = prefs.edit()
    //      e.putInt(preferenceName.name, value)
    //      e.commit()
    //    }
    //  }

    public static void setIntPreferenceValue(Context ctxt, String preferenceName, int value) {
        SharedPreferences        prefs  = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(preferenceName, "" + value);
        editor.apply();
    }

    public static Float getFloatPreferenceValue(Context ctxt, String preferenceName, Float dflt) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
            return Float.parseFloat(prefs.getString(preferenceName, "" + dflt));
        } catch (Throwable e) {
            Logr.ignored(e);
            return dflt;
        }
    }

    public static Boolean getBooleanPreferenceValue(Context ctxt, String preferenceName, Boolean dflt) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return prefs.getBoolean(preferenceName, dflt);
    }

    public static Boolean getBooleanPreferenceValue(Context ctxt, String preferenceName, int defaultResource) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);

        if (prefs.contains(preferenceName))
            return prefs.getBoolean(preferenceName, true);

        return Boolean.parseBoolean(ctxt.getResources().getString(defaultResource));
    }

    public static void setBooleanPreferenceValue(Context ctxt, String preferenceName, Boolean value) {
        SharedPreferences        prefs  = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(preferenceName, value);
        editor.apply();
    }
}
