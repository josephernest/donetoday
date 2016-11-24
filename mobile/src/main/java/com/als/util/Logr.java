package com.als.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.als.donetoday.BuildConfig;
import com.als.donetoday.LifeLogApp;
import com.als.donetoday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

public class Logr {

    /*
        Add preferences, e.g. like the following, see Tasks To Do for an example implementation:

        <PreferenceCategory android:title="@string/Pref_Section_Logging">
            <CheckBoxPreference
                android:defaultValue="@bool/Pref_LogToFile_Default"
                android:key="@string/Pref_LogToFile_Key"
                android:summary="@string/Pref_LogToFile_Summary_Inactive"
                android:title="@string/Pref_LogToFile_Title" />
            <Preference
                android:key="@string/Pref_DeleteLog_Key"
                android:title="@string/Pref_DeleteLog_Title" />
        </PreferenceCategory>
     */

    public final static String LOG_2_FILE_PREFERENCE_NAME = "prefLogToFile";

    private static String  logTag   = "NoTag";
    private static File    logFile  = null;
    private static boolean log2File = true;

    /**
     * Called from UtilApp
     */
    public synchronized static void init(@NonNull final Context ctxt) {

        // log tag
        logTag = ctxt.getString(R.string.log_tag).replaceAll("\\s+", "").replaceAll("#", "");

        // log file
        // We use getExternalFilesDir()
        // - to make the files (semi-)public
        // - to ensure they are deleted when the app is uninstalled
        // - to ensure we don't need storage rights just for logging (starting with sdk 19)
        logFile = new File(ctxt.getExternalFilesDir(null), "app.log");

        // log to file
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctxt);

        log2File = sp.getBoolean(ctxt.getString(R.string.Pref_LogToFile_Key), ctxt.getResources().getBoolean(R.bool.Pref_LogToFile_Default));
    }

    public synchronized static void setLog2File(final boolean log2File) {
        Logr.log2File = log2File;
    }

    public synchronized static boolean isLogging2File() {
        return log2File;
    }

//    public synchronized static boolean logFileExists() {
//        return logFile != null && logFile.exists();
//    }

    public synchronized static File getLogFile() {
        return logFile;
    }

    /**
     * Returns true, if there's no log file after this method returns.
     */
    public synchronized static boolean deleteLogFile() {
        return logFile == null || !logFile.exists() || logFile.delete();
    }

    private static final String LOGR = Logr.class.getName();

    @NonNull
    private static String getCaller() {

        final StackTraceElement stack[] = new Throwable().getStackTrace();

        if (stack != null && stack.length > 0) {

            int i = 0;

            // search until the first frame of this class
            for (; i < stack.length; i++) {
                if (stack[i].getClassName().equals(LOGR)) {
                    break;
                }
            }
            // search until the first frame before this class
            for (; i < stack.length; i++) {
                if (!stack[i].getClassName().equals(LOGR)) {
                    break;
                }
            }

            if (i < stack.length) {
                return "" + stack[i];
            }
        }
        return "Unknown Caller";
    }

    public synchronized static void logToFile(final String caller, final int level, final CharSequence msg, final Throwable throwable) {


        if (log2File && logFile != null) {

            OutputStreamWriter writer = null;
            try {

                final String[] Levels2Name = {
                        "0", "1",
                        "Verbose", "Debug", "Info", "Warning", "Error", "Assert"
                };

                writer = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");

                writer.write(new Date().toString() + "\t" + Levels2Name[level] + "\t" + caller + "\t" + msg + "\n");
                if (throwable != null) {
                    writer.write(Log.getStackTraceString(throwable));
                }

            } catch (final IOException e) {
                // prevent infinite recursion
                final boolean l2f = log2File;
                setLog2File(false);
                Log.e(logTag, "Error writing to log file", e);
                setLog2File(l2f);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (final Exception e) {
                        Logr.ignored(e);
                    }
                }
            }
        }
    }

    public synchronized static void log(@NonNull final String tag, final int level, final CharSequence message, final Throwable throwable) {

        if (Log.isLoggable(tag, level) && (message != null || throwable != null)) {

            final CharSequence msg    = message == null ? throwable.getMessage() : message;
            final String       caller = getCaller();

            logToFile(caller, level, msg, throwable);

            switch (level) {
                case Log.VERBOSE:
                    Log.v(tag, caller + ": " + msg, throwable);
                    break;
                case Log.DEBUG:
                    Log.d(tag, caller + ": " + msg, throwable);
                    break;
                case Log.INFO:
                    Log.i(tag, caller + ": " + msg, throwable);
                    break;
                case Log.WARN:
                default:
                    Log.w(tag, caller + ": " + msg, throwable);
                    break;
                case Log.ERROR:
                    Log.e(tag, caller + ": " + msg, throwable);
                    break;
                case Log.ASSERT:
                    Log.wtf(tag, caller + ": " + msg, throwable);
                    break;
            }
        }
    }

    public static void verbose(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.VERBOSE, message, throwable);
    }

    public static void debug(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.DEBUG, message, throwable);
    }

    public static void info(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.INFO, message, throwable);
    }

    public static void ignored(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.WARN, "Ignored exception: " + message, throwable);
    }

    public static void warning(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.WARN, message, throwable);
    }

    public static void error(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.ERROR, message, throwable);
    }

    public static void severe(final CharSequence message, final Throwable throwable) {
        log(logTag, Log.ASSERT, message, throwable);
    }

    public static void verbose(final CharSequence message) {
        log(logTag, Log.VERBOSE, message, null);
    }

    public static void debug(final CharSequence message) {
        log(logTag, Log.DEBUG, message, null);
    }

    public static void info(final CharSequence message) {
        log(logTag, Log.INFO, message, null);
    }

    public static void ignored(final CharSequence message) {
        log(logTag, Log.WARN, "Ignored exception: " + message, null);
    }

    public static void warning(final CharSequence message) {
        log(logTag, Log.WARN, message, null);
    }

    public static void error(final CharSequence message) {
        log(logTag, Log.ERROR, message, null);
    }

    public static void severe(final CharSequence message) {
        log(logTag, Log.ASSERT, message, null);
    }

    public static void verbose(final Throwable throwable) {
        log(logTag, Log.VERBOSE, null, throwable);
    }

    public static void debug(final Throwable throwable) {
        log(logTag, Log.DEBUG, null, throwable);
    }

    public static void info(final Throwable throwable) {
        log(logTag, Log.INFO, null, throwable);
    }

    public static void ignored(final Throwable throwable) {
        log(logTag, Log.WARN, null, throwable);
    }

    public static void warning(final Throwable throwable) {
        log(logTag, Log.WARN, "Ignored exception", throwable);
    }

    public static void error(final Throwable throwable) {
        log(logTag, Log.ERROR, null, throwable);
    }

    public static void severe(final Throwable throwable) {
        log(logTag, Log.ASSERT, null, throwable);
    }


    public static boolean shareLogFile(final Activity activity, final View someView4Snackbars, final String fileProviderAuthority) {

        final File logFile = Logr.getLogFile();
        if (logFile == null || !logFile.isFile()) {
            if (someView4Snackbars != null) {
                Snackbar.make(someView4Snackbars, R.string.Pref_ShareLogFile_NoFile, Snackbar.LENGTH_LONG)
                        .show();
            }
            return false;
        }

        // Use the FileProvider to get a content URI
        try {
            final Uri logFileUri = FileProvider.getUriForFile(activity, fileProviderAuthority, logFile);

            // TODO: check with various android versions
            final Intent shareIntent =
                    ShareCompat.IntentBuilder.from(activity)
                                             .setType("text/plain")//x-log") // plain/text ?
                                             .setEmailTo(new String[]{activity.getString(R.string.ShareLogFile_Mail_Address)})
                                             .setSubject(activity.getString(R.string.ShareLogFile_Mail_Subject, activity.getString(R.string.app_name)))
                                             .setText(message())
                                             .setStream(logFileUri)
                                             // .startChooser();
                                             .getIntent();

            if (Build.VERSION_CODES.JELLY_BEAN < Build.VERSION.SDK_INT) { // JellyBean / 16 / 4.1
                // starting with sdk 16, streams are copied to clipData => FLAG_GRANT_READ_URI_PERMISSION should work without setting data
                shareIntent.setData(logFileUri); // => gmail uses the uri as target email address
            }

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // applies to the uri in setData or clipData (if clipData is not set, it is copied from streams for sdk >= 16)!

            final ResolveInfo ri = activity.getPackageManager().resolveActivity(shareIntent, 0);
            if (ri == null) {
                // no activity found that can handle this intent

                if (someView4Snackbars != null) {
                    Snackbar.make(someView4Snackbars, R.string.ShareLogFile_NoSharingAppFound, Snackbar.LENGTH_LONG)
                            .show();
                }

                return false;
            }

            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.ShareLogFile_ShareWith)));

        } catch (final IllegalArgumentException e) {
            final String msg = activity.getString(R.string.ShareLogFile_FileCantBeShared);

            Logr.error(msg);

            if (someView4Snackbars != null) {
                Snackbar.make(someView4Snackbars, msg, Snackbar.LENGTH_LONG)
                        .show();
            }
        }


        return true;
    }

    private static final  String androidVersion = Build.VERSION.RELEASE + " CL " + Build.VERSION.INCREMENTAL;

    @SuppressWarnings("boxing")
    public static String message() {
        final long[] mem = getMemSizes();
        return MessageFormat.format(new Date().toString(), LifeLogApp.getAppName() + " V" + BuildConfig.VERSION_NAME, Build.MODEL + ", " + Build.FINGERPRINT, androidVersion, //
                                    ("ntf = " + (mem[MEM_NATIVE] + mem[MEM_TOTAL] + mem[MEM_FREE])) + " / max = " + mem[MEM_MAX] + ", native = " + mem[MEM_NATIVE] + ", total = " + mem[MEM_TOTAL] + ", free = " + mem[MEM_FREE],
                                    // getTotalInternalMemorySize(), getAvailableInternalMemorySize(),
                                    Locale.getDefault().getDisplayName());
    }

    public static final int MEM_MAX    = 0;
    public static final int MEM_NATIVE = 1;
    public static final int MEM_TOTAL  = 2;
    public static final int MEM_FREE   = 3;

    public static long[] getMemSizes() {
        final Runtime rt = Runtime.getRuntime();
        return new long[]{//
                          rt.maxMemory(),//
                          Debug.getNativeHeapAllocatedSize(),//
                          rt.totalMemory(),//
                          rt.freeMemory() //
        };
    }
}