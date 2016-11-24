package com.als.donetoday.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.text.format.DateFormat;
import android.view.View;

import com.als.donetoday.BuildConfig;
import com.als.donetoday.R;
import com.als.donetoday.reminder.ReminderManager;
import com.als.util.Logr;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;

public class PreferenceFragment extends AbstractPreferenceFragment {

    private final static String PREF_REMINDER_TIME   = "DoneTodayReminderTime";
    private final static String PREF_REMINDER_OFFSET = "DoneTodayReminderOffset";

    private Preference reminderTime;
    //    private Preference reminderOffset;

    private Preference prefLogToFile;
    private Preference prefDeleteLogFile;
    private Preference prefShareLogFile;

    @Override
    public void onCreatePreferencesFix(final Bundle bundle, final String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        reminderTime = findPreference(PREF_REMINDER_TIME);
        //        reminderOffset = findPreference(PREF_REMINDER_OFFSET);

        // logging ------------------------------

        prefLogToFile = findPreference(getContext().getString(R.string.Pref_LogToFile_Key));
        prefDeleteLogFile = findPreference(getContext().getString(R.string.Pref_DeleteLogFile_Key));
        if (prefDeleteLogFile != null) {
            prefDeleteLogFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final boolean ret = Logr.deleteLogFile();

                    final View v = getView();
                    if (v != null) {

                        // We don't offer a undo since in the meantime, a new file could have been created.
                        // => It could need significant effort to "prepend" the old log file
                        // and on the other hand, the logs probably are not that important...

                        Snackbar.make(v,
                                      ret ? R.string.Pref_DeleteLogFile_Deleted : R.string.Pref_DeleteLogFile_CouldNotDelete,
                                      Snackbar.LENGTH_LONG)
                                .show();
                    }
                    return ret;
                }
            });
        }

        prefShareLogFile = findPreference(getContext().getString(R.string.Pref_ShareLogFile_Key));
        if (prefShareLogFile != null) {
            prefShareLogFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    return Logr.shareLogFile(getActivity(), getView(), getFileProviderAuthority());
                }
            });
        }

        // logging end --------------------------
    }

    protected String getFileProviderAuthority() {
        return BuildConfig.FILES_AUTHORITY;
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedpreferences, final String key) {
        super.onSharedPreferenceChanged(sharedpreferences, key);

        if (PREF_REMINDER_OFFSET.equals(key) || PREF_REMINDER_TIME.equals(key)) {
            ReminderManager.updateAlarm(getContext());
        }
    }

    @Override
    public void updateState(final Context ctxt, final String key) {

        if (reminderTime != null) {
            final LocalTime reminderTime = getReminderTime(ctxt);

            this.reminderTime.setSummary(
                    reminderTime.format(
                            DateTimeFormatter.ofPattern(
                                    DateFormat.is24HourFormat(ctxt) ? "H:mm" : "K:mm a")));
        }

        // logging ------------------------------
        final boolean shouldLog = logToFile(ctxt);

        final File logFile = Logr.getLogFile();
        prefLogToFile.setSummary(
                ctxt.getString(
                        shouldLog ? R.string.Pref_LogToFile_Summary_Active : R.string.Pref_LogToFile_Summary_Inactive,
                        logFile == null ? "no file set yet" : logFile.getAbsolutePath())
                                );

        // a log file could be created while this option has been disabled...  prefDeleteLog.setEnabled(Logr.logFileExists());

        if (shouldLog != Logr.isLogging2File()) {
            Logr.setLog2File(shouldLog);
        }

        // logging end --------------------------
    }

    public static Boolean logToFile(@NonNull final Context ctxt) {
        return PreferenceUtils.getBooleanPreferenceValue(ctxt,
                                                         ctxt.getString(R.string.Pref_LogToFile_Key),
                                                         ctxt.getResources().getBoolean(R.bool.Pref_LogToFile_Default));
    }


    public static void setReminderTime(@NonNull final Context ctxt, @Nullable final LocalTime time) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctxt).edit();
        editor.putString(PREF_REMINDER_TIME, time == null ? null : time.format(DateTimeFormatter.ISO_LOCAL_TIME));
        editor.apply();
    }

    @NonNull
    public static LocalTime getReminderTime(@NonNull final Context ctxt) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        final String timeString = sharedPreferences.getString(PREF_REMINDER_TIME, null);
        return timeString == null ? LocalTime.of(18, 0, 0) : LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME);
    }


    public static void setReminderOffset(@NonNull final Context ctxt, @Nullable final Duration reminderOffset) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctxt).edit();
        editor.putString(PREF_REMINDER_OFFSET, reminderOffset == null ? null : reminderOffset.toString());
        editor.apply();
    }

    @NonNull
    public static Duration getReminderOffset(@NonNull final Context ctxt) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        final String offsetString = sharedPreferences.getString(PREF_REMINDER_OFFSET, null);
        return offsetString == null ? Duration.ZERO : Duration.parse(offsetString);
    }
}
