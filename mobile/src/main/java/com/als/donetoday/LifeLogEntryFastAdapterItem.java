package com.als.donetoday;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.als.donetoday.db.LifeLogEntry;
import com.mikepenz.fastadapter.items.GenericAbstractItem;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.List;

public class LifeLogEntryFastAdapterItem
        extends GenericAbstractItem<LifeLogEntry, LifeLogEntryFastAdapterItem, LifeLogEntryFastAdapterItem.ViewHolder> {

    public LifeLogEntryFastAdapterItem(final LifeLogEntry lifeLogEntry) {
        super(lifeLogEntry);
    }

    @Override
    public int getType() {
        return R.id.fragment_life_log_list_entry;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_lifelog_list_entry;
    }

    @Override
    public void bindView(final ViewHolder holder, final List payloads) {
        super.bindView(holder, payloads);

        final LifeLogEntry entry = this.getModel();

        holder.concernedTime.setText(entry.getEntryStartLocalTime() == null ? "" :
                                     entry.getEntryStartLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
        holder.entryText.setText(entry.getEntryText());

        // debug infos
        holder.debugInfos.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        holder.entryId.setText("(" + entry.getId() + " / " + entry.getUUID() + " / " + entry.getChangeDateTimeMs() + ") "
                               + entry.getEntryStartLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView     concernedTime;
        public final TextView     entryText;
        public final LinearLayout debugInfos;
        public final TextView     entryId;

        public ViewHolder(final View view) {
            super(view);
            concernedTime = (TextView) view.findViewById(R.id.concernedTime);
            entryText = (TextView) view.findViewById(R.id.entryText);

            debugInfos = (LinearLayout) view.findViewById(R.id.debugInfos);
            entryId = (TextView) view.findViewById(R.id.entryId);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + entryText.getText() + "'";
        }
    }
}
