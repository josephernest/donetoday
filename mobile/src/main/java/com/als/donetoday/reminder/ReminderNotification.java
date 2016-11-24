package com.als.donetoday.reminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.als.donetoday.LifeLogEditActivity;
import com.als.donetoday.R;

public class ReminderNotification {

    public static final int REMINDER_NOTIFICATION_ID = 1;

    public static Notification getNotification(@NonNull final Context ctxt) {

        // TODO: actions
        // TODO: delete Intent

        final Intent editIntent = new Intent(ctxt, LifeLogEditActivity.class);
        editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        editIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        final PendingIntent pi = PendingIntent.getActivity(ctxt, 0, editIntent, PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Builder(ctxt)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setSmallIcon(R.drawable.ic_app)
                .setTicker(ctxt.getString(R.string.what_did_you_do))
                .setContentTitle(ctxt.getString(R.string.what_did_you_do))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();
    }
}
