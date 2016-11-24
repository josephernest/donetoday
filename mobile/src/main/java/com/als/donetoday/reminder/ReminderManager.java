package com.als.donetoday.reminder;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.als.donetoday.db.LifeLogRepository;
import com.als.donetoday.preferences.PreferenceFragment;
import com.als.util.Logr;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

public class ReminderManager {

    public static void updateAlarm(@NonNull final Context ctxt) {
        setExactAlarm(ctxt,
                      AlarmManager.RTC_WAKEUP,
                      calculateNextReminder(ZoneId.systemDefault(),
                                            PreferenceFragment.getReminderTime(ctxt),
                                            PreferenceFragment.getReminderOffset(ctxt),
                                            LifeLogRepository.getLatestChangeDate(ctxt))
                              .toEpochSecond() * 1000L,
                      ReminderReceiver.createPendingIntent(ctxt));
    }

    private static ZonedDateTime calculateNextReminder(
            @NonNull final ZoneId currentTimeZone,
            @NonNull final LocalTime reminderTime,
            @Nullable final Duration reminderOffset,
            @Nullable final Instant latestEntryCreationTime) {

        // reminderDateTime = today @ reminderTime + reminderOffset in currentTimeZone
        final LocalDate today = LocalDate.now();
        ZonedDateTime reminderDateTime = today.atTime(reminderTime).atZone(currentTimeZone);

        if (reminderOffset != null) {
            reminderDateTime = reminderDateTime.plus(reminderOffset);
        }

        //  if latestEntryCreationTime is later than reminderDateTime
        //      increment reminderToday by days until it's after latestEntryCreation
        if (latestEntryCreationTime != null) {
            while (latestEntryCreationTime.isAfter(reminderDateTime.toInstant())) {
                reminderDateTime = reminderDateTime.plus(Duration.ofDays(1));
            }
        }

        return reminderDateTime;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void setExactAlarm(@NonNull final Context ctxt, final int type, final long triggerAtMillis, @NonNull final PendingIntent pi) {
        final AlarmManager alarms = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);

        Logr.info("Setting reminder at " + Instant.ofEpochMilli(triggerAtMillis));

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) { // Android 6.0/23
            alarms.setExactAndAllowWhileIdle(type, triggerAtMillis, pi);
        } else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) { // Android 4.4/19
            alarms.setExact(type, triggerAtMillis, pi);
        } else {
            alarms.set(type, triggerAtMillis, pi);
        }
    }

}
