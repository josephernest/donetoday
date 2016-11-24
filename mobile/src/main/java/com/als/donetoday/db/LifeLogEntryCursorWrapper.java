package com.als.donetoday.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public class LifeLogEntryCursorWrapper implements Closeable {

    private final Cursor cursor;

    private final int columnIndexID;
    private final int columnIndexUUID;
    private final int columnIndexCreationDateTime;
    private final int columnIndexChangeDateTime;
    private final int columnIndexEntryTimezone;
    private final int columnIndexEntryStartLocalDate;
    private final int columnIndexEntryStartLocalTime;
    private final int columnIndexEntryDuration;
    private final int columnIndexEntryText;

    public LifeLogEntryCursorWrapper(final Cursor cursor) {
        this.cursor = cursor;
        columnIndexID = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ID);
        columnIndexUUID = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_UUID);
        columnIndexCreationDateTime = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CREATION_DATETIME_MS);
        columnIndexChangeDateTime = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS);
        columnIndexEntryTimezone = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE);
        columnIndexEntryStartLocalDate = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALDATE_EPOCHDAY);
        columnIndexEntryStartLocalTime = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALTIME_S);
        columnIndexEntryDuration = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_DURATION_S);
        columnIndexEntryText = cursor.getColumnIndex(LifeLogContract.LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TEXT);
    }

    // cursor movement ----------------------------------------------------------------------------

    public boolean moveToFirst() {
        return cursor.moveToFirst();
    }

    public boolean moveToNext() {
        return cursor.moveToNext();
    }

    public boolean moveTo(final int position) {
        return cursor.moveToPosition(position);
    }

    public int getCount() {
        return cursor.getCount();
    }

    // Closeable ----------------------------------------------------------------------------------

    @Override
    public void close() throws IOException {
        cursor.close();
    }

    // field access -------------------------------------------------------------------------------

    @Nullable
    public Long getId() {
        return cursor.getLong(columnIndexID);
    }

    @NonNull
    public UUID getUUID() {
        return UUID.fromString(cursor.getString(columnIndexUUID));
    }


    @NonNull
    public Instant getCreationDateTimeMs() {
        return Instant.ofEpochMilli(cursor.getLong(columnIndexCreationDateTime));
    }

    @NonNull
    public Instant getChangeDateTimeMs() {
        return Instant.ofEpochMilli(cursor.getLong(columnIndexChangeDateTime));
    }

    @NonNull
    public ZoneId getEntryTimeZone() {
        return ZoneId.of(cursor.getString(columnIndexEntryTimezone));
    }

    @NonNull
    public LocalDate getEntryStartLocalDate() {
        return LocalDate.ofEpochDay(cursor.getLong(columnIndexEntryStartLocalDate));
    }

    @Nullable
    public LocalTime getEntryStartLocalTime() {
        if (cursor.isNull(columnIndexEntryStartLocalTime)) {
            return null;
        }
        return LocalTime.ofSecondOfDay(cursor.getLong(columnIndexEntryStartLocalTime));
    }

    @Nullable
    public Duration getEntryDuration() {
        if (cursor.isNull(columnIndexEntryDuration)) {
            return null;
        }
        return Duration.ofSeconds(cursor.getLong(columnIndexEntryDuration));
    }

    @Nullable
    public String getEntryText() {
        return cursor.getString(columnIndexEntryText);
    }

    // getting entries ----------------------------------------------------------------------------

    public LifeLogEntry current() {
        return new LifeLogEntry(
                getId(),
                getUUID(),
                getCreationDateTimeMs(),
                getChangeDateTimeMs(),

                getEntryTimeZone(),
                getEntryStartLocalDate(),
                getEntryStartLocalTime(),
                getEntryDuration(),
                getEntryText());
    }

    public LifeLogEntry first() {
        return get(0);
    }

    public LifeLogEntry get(final int position) {
        final int orgPosition = cursor.getPosition();
        try {
            return cursor.moveToPosition(position) ? current() : null;
        } finally {
            cursor.moveToPosition(orgPosition);
        }
    }
}
