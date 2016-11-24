package com.als.donetoday.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.als.donetoday.db.LifeLogContract.LifeLogEntryTable;

public class LifeLogDatabaseHelper extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME    = "LifeLog.db";
    protected final static int    DATABASE_VERSION = 1;

    LifeLogDatabaseHelper(final Context ctxt) {
        super(ctxt, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + LifeLogEntryTable.TABLE_NAME + " ("

                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ID + "                              INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_UUID + "                            TEXT    NOT NULL,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CREATION_DATETIME_MS + "            INTEGER NOT NULL,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS + "              INTEGER NOT NULL, "

                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE + "                  TEXT    NOT NULL COLLATE LOCALIZED,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE_OFFSET_S + "         INTEGER NOT NULL COLLATE LOCALIZED,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALDATE_EPOCHDAY + "  INTEGER NOT NULL COLLATE LOCALIZED,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALTIME_S + "         INTEGER COLLATE LOCALIZED,"
                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_DURATION_S + "                INTEGER COLLATE LOCALIZED,"

                   + LifeLogEntryTable.COLUMN_NAME_LIFELOGENTRY_ENTRY_TEXT + "                      TEXT    COLLATE LOCALIZED"

                   + ");");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // nop
    }
}
