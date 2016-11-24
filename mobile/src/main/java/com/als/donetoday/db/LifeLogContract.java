package com.als.donetoday.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.als.donetoday.BuildConfig;

/**
 * The contract for the {@link LifeLogContentProvider}.
 */
public final class LifeLogContract {

    public static final String AUTHORITY = BuildConfig.LIFE_LOG_AUTHORITY;

    private LifeLogContract() {
    }

    /**
     * LifeLogEntry table contract
     */
    public static final class LifeLogEntryTable implements BaseColumns {

        private LifeLogEntryTable() {
        }

        public static final String TABLE_NAME = "T_LIFELOGENTRY";

        // URI definitions ------------------------------------------------------------------------

        private static final String SCHEME = "content://";

        public static final String PATH_LIFELOGENTRY                   = "/lifeLogEntry";
        public static final String PATH_LIFELOGENTRY_ID                = "/lifeLogEntry/";

        // 0-relative position of a note ID segment in the path part of PATH_LIFELOGENTRY_ID
        public static final int LIFELOGENTRY_ID_PATH_POSITION = 1;

        public static final Uri CONTENT_URI         = Uri.parse(SCHEME + AUTHORITY + PATH_LIFELOGENTRY);
        public static final Uri CONTENT_URI_ID_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_LIFELOGENTRY_ID);

        // MIME type definitions ------------------------------------------------------------------

        public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.als.lifelogentry";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.als.lifelogentry";

        // Column definitions ---------------------------------------------------------------------

        // managed by the app

        // _id is needed e.g. for lists etc. we let the db create it
        public static final String COLUMN_NAME_LIFELOGENTRY_ID = _ID;

        // same uuid => same entry, potentially different versions
        public static final String COLUMN_NAME_LIFELOGENTRY_UUID = "A_UUID";

        // creation timestamp (epoch ms) of the first version
        public static final String COLUMN_NAME_LIFELOGENTRY_CREATION_DATETIME_MS = "A_CREATION_DATETIME_MS";

        // change timestamp (epoch ms) of the entry (i.e. creation timestamp of this version)
        public static final String COLUMN_NAME_LIFELOGENTRY_CHANGE_DATETIME_MS = "A_CHANGE_DATETIME_MS";

        // based on user input
        public static final String COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE = "A_ENTRY_TIMEZONE";

        // offset to UTC in seconds for the given timezone at the given local date & time
        public static final String COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE_OFFSET_S        = "A_ENTRY_TIMEZONE_OFFSET_S";
        public static final String COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALDATE_EPOCHDAY = "A_ENTRY_START_LOCALDATE_EPOCHDAY";
        public static final String COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALTIME_S        = "A_ENTRY_START_LOCALTIME_S"; // null for "whole day entries"
        public static final String COLUMN_NAME_LIFELOGENTRY_ENTRY_DURATION_S               = "A_ENTRY_DURATION_S"; // null without duration

        public static final String COLUMN_NAME_LIFELOGENTRY_ENTRY_TEXT = "A_ENTRY_TEXT"; // may be null

        // Standard sort orders -------------------------------------------------------------------

        /**
         * The default sort order for this table: by local date and time
         */
        public static final String DEFAULT_SORT_ORDER_LOCAL_TIME =
                COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALDATE_EPOCHDAY + ", " + COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALTIME_S;

        /**
         * Alternative sort order for this table: by UTC
         */
        public static final String ALTERNATIVE_SORT_ORDER_GLOBAL_TIME =
                COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALDATE_EPOCHDAY + " * 86400"
                + " + coalesce(" + COLUMN_NAME_LIFELOGENTRY_ENTRY_START_LOCALTIME_S + ", 0)"
                + " + " + COLUMN_NAME_LIFELOGENTRY_ENTRY_TIMEZONE_OFFSET_S;
    }
}