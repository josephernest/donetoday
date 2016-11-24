package com.als.donetoday.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ReminderManager.updateAlarm(context);
    }
}
