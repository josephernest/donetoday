package com.als.donetoday;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.als.donetoday.db.LifeLogEntry;
import com.als.donetoday.db.LifeLogEntryCursorWrapper;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class LifeLogEntryFastAdapter extends AbstractAdapter<LifeLogEntryFastAdapterItem>
        implements StickyRecyclerHeadersAdapter<LifeLogEntryFastAdapterItem.ViewHolder> {

    private LifeLogEntryCursorWrapper cursorWrapper;
    private Cursor                    cursor;
    protected int columnIndexId = 0;


    public LifeLogEntryFastAdapter() {
        this(null);
    }

    public LifeLogEntryFastAdapter(@Nullable final Cursor cursor) {
        changeCursor(cursor);
    }

    public void getColumnIndizes(final Cursor cursor) {
        columnIndexId = cursor != null ? cursor.getColumnIndex(BaseColumns._ID) : -1;
    }

    private boolean dataAvailable() {
        return cursor != null && !cursor.isClosed();
    }

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public int getAdapterItemCount() {
        return dataAvailable() ? cursor.getCount() : 0;
    }

    @Override
    public List<LifeLogEntryFastAdapterItem> getAdapterItems() {
        throw new UnsupportedOperationException();
    }


    private final SparseArray<LifeLogEntryFastAdapterItem> pos2item = new SparseArray<>();

    @Override
    public LifeLogEntryFastAdapterItem getAdapterItem(final int position) {

        if (!dataAvailable()) {
            return null;
        }

        final LifeLogEntryFastAdapterItem i = pos2item.get(position);
        if (i != null) {
            return i;
        }

        if (cursorWrapper.moveTo(position)) {
            final Long id = cursorWrapper.getId();
            if (id != null) {
                final LifeLogEntryFastAdapterItem item =
                        new LifeLogEntryFastAdapterItem(cursorWrapper.current())
                                .withIdentifier(id);

                mapPossibleType(item);
                pos2item.put(position, item);

                return item;
            }
        }
        return null;
    }

    @Override
    public int getAdapterPosition(final LifeLogEntryFastAdapterItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAdapterPosition(final long identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGlobalPosition(final int position) {
        throw new UnsupportedOperationException();
    }

    public void changeCursor(@Nullable final Cursor cursor) {

        final Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * @return the old cursor, not closed!
     */
    public Cursor swapCursor(@Nullable final Cursor newCursor) {

        cursorWrapper = newCursor == null ? null : new LifeLogEntryCursorWrapper(newCursor);

        if (newCursor == cursor) {
            return null;
        }

        final int oldItemCount = getItemCount();

        final Cursor old = this.cursor;
        this.cursor = newCursor;
        pos2item.clear();

        getColumnIndizes(cursor);
        if (dataAvailable()) {
            getFastAdapter().notifyAdapterDataSetChanged();
        } else {
            getFastAdapter().notifyAdapterItemRangeRemoved(0, oldItemCount);
        }

        return old;
    }

    @Override
    public long getHeaderId(final int position) {
        final LifeLogEntryFastAdapterItem item = getItem(position);
        return item.getModel().getEntryStartLocalDate().toEpochDay();
    }

    @Override
    public LifeLogEntryFastAdapterItem.ViewHolder onCreateHeaderViewHolder(final ViewGroup parent) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_lifelog_list_entry, parent, false);
        return new LifeLogEntryFastAdapterItem.ViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final LifeLogEntryFastAdapterItem.ViewHolder holder, final int position) {
        final LifeLogEntryFastAdapterItem item = getItem(position);
        final LifeLogEntry entry = item.getModel();

        final long epochMS = entry.getEntryStartLocalDate().atStartOfDay(entry.getEntryTimeZone()).toEpochSecond() * 1000L;

        holder.entryText.setText(
                DateUtils.formatDateTime(null, epochMS,
                                         DateUtils.FORMAT_SHOW_WEEKDAY
                                         | DateUtils.FORMAT_SHOW_DATE
                                         | DateUtils.FORMAT_SHOW_YEAR
//                                         | DateUtils.FORMAT_NO_YEAR
                                         | DateUtils.FORMAT_ABBREV_MONTH
                                         | DateUtils.FORMAT_ABBREV_WEEKDAY));
    }
}
