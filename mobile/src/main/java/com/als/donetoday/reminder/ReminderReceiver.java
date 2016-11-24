package com.als.donetoday.reminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context ctxt, final Intent intent) {
        final NotificationManager nm = (NotificationManager) ctxt.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(ReminderNotification.REMINDER_NOTIFICATION_ID, ReminderNotification.getNotification(ctxt));
    }

    public static PendingIntent createPendingIntent(final Context ctxt) {
        final Intent ai = new Intent(ctxt, ReminderReceiver.class);
        return PendingIntent.getBroadcast(ctxt, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
