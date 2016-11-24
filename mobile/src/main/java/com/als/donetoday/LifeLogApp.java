package com.als.donetoday;

import android.app.Application;
import android.os.Process;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManagerFix;
import android.util.Log;

import com.als.donetoday.preferences.AppPreferences;
import com.als.donetoday.reminder.ReminderManager;
import com.als.util.Logr;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.BufferedReader;
import java.io.FileReader;

public class LifeLogApp extends Application {

    public static LifeLogApp app = null;

    private boolean isNewInstallation = false;
    private boolean isNewVersion      = false;

    private int  numberOfStarts = 0;
    private long msFirstStart   = System.currentTimeMillis();

    public LifeLogApp() {
        LifeLogApp.app = this;
    }

    @Override
    public void onCreate() {

        final Thread.UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable throwable) {
                Logr.error("" + thread, throwable);
                if (previousHandler != null) {
                    previousHandler.uncaughtException(thread, throwable);
                }
            }
        });
        Logr.init(this);

        numberOfStarts = AppPreferences.getNumberOfStarts(this);
        AppPreferences.setNumberOfStarts(this, numberOfStarts + 1);

        msFirstStart = AppPreferences.getMsFirstStart(this);


        super.onCreate();

        isNewInstallation = !AppPreferences.isVersionCodeSet(this);

        final int oldVersionCode = AppPreferences.getVersionCode(this, -1);
        final int newVersionCode = BuildConfig.VERSION_CODE;
        isNewVersion = oldVersionCode != newVersionCode;

        AppPreferences.setVersionCode(this, newVersionCode);

        Logr.info("Is new installation: " + isNewInstallation);

        PreferenceManagerFix.setDefaultValues(this, R.xml.preferences, false);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AndroidThreeTen.init(this);

        ReminderManager.updateAlarm(this);
    }

    public boolean isNewInstallation() {
        return isNewInstallation;
    }

    public boolean isNewVersion() {
        return isNewVersion;
    }

    public int getNumberOfStarts() {
        return numberOfStarts;
    }

    public long getMsFirstStart() {
        return msFirstStart;
    }

    public static String getAppName() {

        if (app != null) {
            // try to determine the app name based on the manifest
            // this assumes the app class in the manifest extends this class
            try {
                final int res = app.getApplicationInfo().labelRes;
                if (res != 0) {
                    return app.getString(res);
                }
            } catch (final RuntimeException e) {
                Log.wtf("App.getAppName", e);
            }
        }

        // use app package name instead
        final String pkg = getAppPackage();
        if (pkg != null) {
            return pkg;
        }

        // use process id
        return "Process " + Process.myPid();
    }

    public static String getAppDescription() {

        if (app != null) {
            // try to determine the app name based on the manifest
            // this assumes the app class in the manifest extends this class
            try {
                final int res = app.getApplicationInfo().descriptionRes;
                if (res != 0) {
                    return app.getString(res);
                }
            } catch (final RuntimeException e) {
                Log.wtf("App.getAppDescription", e);
            }
        }

        return null;
    }

    public static String getAppPackage() {

        if (app != null) {
            try {
                return app.getApplicationInfo().packageName;
            } catch (final RuntimeException e) {
                Log.wtf("App.getAppName", e);
            }
        }

        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader("/proc/" + Process.myPid() + "/cmdline"));

            final char[] buf = new char[1024];
            final int n = r.read(buf); // reads too many characters!
            for (int i = 0; i < n; i++) { // => search for end of name
                if (buf[i] == 0) {
                    return new String(buf, 0, i);
                }
            }

            return new String(buf, 0, n);// r.readLine(); // readLine() reads too many characters
        } catch (final Exception e) {
            Log.wtf("App.getAppPackage", e);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (final Exception e) {
                    Log.wtf("Logr", e);
                }
            }
        }
        return null;
    }
}
