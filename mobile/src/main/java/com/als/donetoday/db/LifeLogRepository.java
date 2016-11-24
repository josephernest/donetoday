package com.als.donetoday.db;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.als.donetoday.db.LifeLogContract.LifeLogEntryTable;
import com.als.donetoday.reminder.ReminderManager;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Collection;

public class LifeLogRepository {

    private static Cursor getEntries(@NonNull final Context ctxt,
                                     @NonNull final Uri uri,
                                     @Nullable final String[] projection,
                                     @Nullable final String selection,
                                     @Nullable final String[] selectionArgs,
                                     @Nullable final String sortOrder) {

        final Cursor c = ctxt.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);

        if (c == null) {
            return null;
        }

        if (c.getCount() < 1) {
            c.close();
            return null;
        }

        return c;
    }

//    public static Cursor getEntries(@NonNull final Context ctxt,
//                                    @Nullable final String[] projection,
//                                    @Nullable final String selection,
//                                    @Nullable final String[] selectionArgs,
//                                    @Nullable final String sortOrder) {
//        return getEntries(ctxt, LifeLogEntryTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
//    }


//    public static Cursor getEntriesAll(@NonNull final Context ctxt) {
//        return getEntries(ctxt, null, null, null, null);
//    }

    public final static String LATEST_ENTRY_CONDITION = LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS + " = (" +
                                                        "SELECT MAX(SQT." + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS + ") " +
                                                        " FROM " + LifeLogEntryTable.TABLE_NAME + " SQT " +
                                                        " WHERE " +
                                                        "SQT." + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_UUID +
                                                        " = " +
                                                        LifeLogEntryTable.TABLE_NAME + "." + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_UUID + ")";


//    public static Cursor getEntriesLatest(@NonNull final Context ctxt) {
//        return getEntries(ctxt,
//                          null, // projection
//                          LATEST_ENTRY_CONDITION, // selection
//                          null, // selectionArgs
//                          null // sortOrder
//                         );
//    }

    @Nullable
    public static LifeLogEntry getEntryById(@NonNull final Context ctxt, final long id) {

        final Cursor c = getEntries(ctxt,
                                    ContentUris.withAppendedId(LifeLogEntryTable.CONTENT_URI_ID_BASE, id),
                                    null, // projection
                                    null, // selection
                                    null, // selectionArgs
                                    null // sortOrder
                                   );
        if (c == null) {
            return null;
        }

        try {
            return new LifeLogEntryCursorWrapper(c).first();
        } finally {
            c.close();
        }
    }

    @Nullable
    public static Uri insertEntry(@NonNull final Context ctxt, @NonNull final LifeLogEntry entry) {

        final ContentValues contentValues = new ContentValues();

        contentValues.put(LifeLogEntryTable._ID, entry.getId());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_UUID, entry.getUUID().toString());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CREATION_DATETIME_MS, entry.getCreationDateTimeMs().toEpochMilli());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS, entry.getChangeDateTimeMs().toEpochMilli());

        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE, entry.getEntryTimeZone().toString());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE_OFFSET_S, entry.getEntryTimeZoneOffset().getTotalSeconds());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALDATE_EPOCHDAY, entry.getEntryStartLocalDate().toEpochDay());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALTIME_S, entry.getEntryStartLocalTime() == null ? null : entry.getEntryStartLocalTime().toSecondOfDay());
        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_DURATION_S, entry.getEntryDuration() == null ? null : entry.getEntryDuration().getSeconds());

        contentValues.put(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TEXT, entry.getEntryText());

        final Uri rowUri = ctxt.getContentResolver().insert(
                LifeLogEntryTable.CONTENT_URI,
                contentValues);

        ReminderManager.updateAlarm(ctxt);

        return rowUri;
    }

    public static void deleteEntries(@NonNull final Context ctxt, final Collection<Long> ids) throws RemoteException, OperationApplicationException {

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        for (final Long id : ids) {
            final ContentProviderOperation operation =
                    ContentProviderOperation
                            .newDelete(LifeLogEntryTable.CONTENT_URI)
                            .withSelection(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ID + " = ?", new String[]{Long.toString(id)})
                            .build();

            operations.add(operation);
        }

        ctxt.getContentResolver().applyBatch(LifeLogContract.AUTHORITY, operations);

        ReminderManager.updateAlarm(ctxt);
    }

    public static Instant getLatestChangeDate(@NonNull final Context ctxt) {

        final Cursor c = getEntries(ctxt,
                                    LifeLogEntryTable.CONTENT_URI,
                                    new String[]{LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS}, // projection
                                    LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS +
                                    " = (SELECT MAX(" + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS + ")" +
                                    " FROM " + LifeLogEntryTable.TABLE_NAME + ")",
                                    null, // selectionArgs
                                    LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS + " DESC" // sortOrder
                                   );
        if (c == null) {
            return null;
        }

        try {
            if (c.moveToFirst()) {
                return Instant.ofEpochMilli(c.getLong(c.getColumnIndex(LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS)));
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }
}
