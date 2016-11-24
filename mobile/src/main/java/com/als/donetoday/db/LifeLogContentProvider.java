package com.als.donetoday.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.als.util.Logr;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.als.donetoday.db.LifeLogContract.LifeLogEntryTable;

public class LifeLogContentProvider extends ContentProvider {

    private LifeLogDatabaseHelper dbHelper;

    private static final UriMatcher sUriMatcher;
    private static final int LIFE_LOG_ENTRY    = 1;
    private static final int LIFE_LOG_ENTRY_ID = 2;

    private static final Map<String, String> LifeLogProjectionMap = new HashMap<>();

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(LifeLogContract.AUTHORITY, LifeLogEntryTable.PATH_LIFELOGENTRY, LIFE_LOG_ENTRY);
        sUriMatcher.addURI(LifeLogContract.AUTHORITY, LifeLogEntryTable.PATH_LIFELOGENTRY_ID + "#", LIFE_LOG_ENTRY_ID);

        try {
            for (final Field f : LifeLogEntryTable.class.getDeclaredFields()) {
                if (f.getName().startsWith("COLUMN_NAME_LIFELOGENTRY_")) {
                    final String value = (String) f.get(null);
                    LifeLogProjectionMap.put(value, value);
                }
            }
        } catch (final IllegalAccessException e) {
            Logr.severe(e);
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new LifeLogDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // we only have one table...
        queryBuilder.setTables(LifeLogEntryTable.TABLE_NAME);

        // adjust projection and where clause
        switch (sUriMatcher.match(uri)) {
            case LIFE_LOG_ENTRY:
                queryBuilder.setProjectionMap(LifeLogProjectionMap);
                break;

            case LIFE_LOG_ENTRY_ID:
                queryBuilder.setProjectionMap(LifeLogProjectionMap);
                queryBuilder.appendWhere(LifeLogEntryTable._ID + "=" + uri.getPathSegments().get(LifeLogEntryTable.LIFELOGENTRY_ID_PATH_POSITION));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final String orderBy = TextUtils.isEmpty(sortOrder)
                               ? LifeLogEntryTable.DEFAULT_SORT_ORDER_LOCAL_TIME
                               : sortOrder;

        final Cursor c = queryBuilder.query(dbHelper.getReadableDatabase(),
                                            projection,
                                            selection,
                                            selectionArgs,
                                            null, // groupBy
                                            null, // having
                                            orderBy);

        // noinspection ConstantConditions
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull final Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LIFE_LOG_ENTRY:
                return LifeLogEntryTable.CONTENT_TYPE;

            case LIFE_LOG_ENTRY_ID:
                return LifeLogEntryTable.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues values) {

        // Only the full provider URI is allowed for inserts.
        if (sUriMatcher.match(uri) != LIFE_LOG_ENTRY) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final long rowId = dbHelper.getWritableDatabase().insert(
                LifeLogEntryTable.TABLE_NAME,
                LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TEXT,  // SQLite sets this column value to null if values is empty.
                values);

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            final Uri noteUri = ContentUris.withAppendedId(LifeLogEntryTable.CONTENT_URI_ID_BASE, rowId);
            // Notifies observers registered against this provider that the data changed.

            // noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull final Uri uri, final String selection, final String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int count;

        switch (sUriMatcher.match(uri)) {
            case LIFE_LOG_ENTRY:
                count = db.delete(LifeLogEntryTable.TABLE_NAME, selection, selectionArgs);
                break;

            case LIFE_LOG_ENTRY_ID:
                final String entryId = uri.getPathSegments().get(1);
                count = db.delete(LifeLogEntryTable.TABLE_NAME,
                                  LifeLogEntryTable._ID + "=" + entryId
                                  + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int count;

        switch (sUriMatcher.match(uri)) {
            case LIFE_LOG_ENTRY:
                count = db.update(LifeLogEntryTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LIFE_LOG_ENTRY_ID:
                final String noteId = uri.getPathSegments().get(1);
                count = db.update(LifeLogEntryTable.TABLE_NAME, values,
                                  LifeLogEntryTable._ID + "=" + noteId
                                  + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
