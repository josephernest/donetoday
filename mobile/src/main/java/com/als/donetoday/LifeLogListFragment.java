package com.als.donetoday;

import android.app.Activity;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.als.donetoday.db.LifeLogContract;
import com.als.donetoday.db.LifeLogRepository;
import com.als.util.Logr;
import com.als.util.fastadapter.FastAdapterRecyclerViewFragment;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LifeLogListFragment
        extends FastAdapterRecyclerViewFragment<LifeLogEntryFastAdapterItem, LifeLogEntryFastAdapterItem.ViewHolder, LifeLogEntryFastAdapterItem> {

    public interface OnEntryClickedListener {
        void onEntryClicked(long entryId);
    }

    public final static  String TAG       = "LifeLogListFragment";
    private final static int    LOADER_ID = 1;

    private LifeLogEntryFastAdapter entryFastAdapter;


    public LifeLogListFragment() {
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getRecyclerView().setHasFixedSize(true);

//        new UndoHelper(fastAdapter, new UndoHelper.UndoListener<LifeLogEntryFastAdapterItem>() {
//            @Override
//            public void commitRemove(Set<Integer> positions, ArrayList<FastAdapter.RelativeInfo<LifeLogEntryFastAdapterItem>> removed) {
//                Logr.error("Positions: " + positions.toString() + " Removed: " + removed.size());
//            }
//        });
    }

    FastAdapter<LifeLogEntryFastAdapterItem> fastAdapter;

    public FastAdapter<LifeLogEntryFastAdapterItem> onCreateFastAdapter() {

        fastAdapter = new FastAdapter<LifeLogEntryFastAdapterItem>()
                .withSelectable(true)
                .withMultiSelect(true)
                .withSelectOnLongClick(true);
        fastAdapter.setHasStableIds(true);

        entryFastAdapter = new LifeLogEntryFastAdapter();

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(entryFastAdapter);
        getRecyclerView().addItemDecoration(decoration);

        //so the headers are aware of changes
        entryFastAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });

        entryFastAdapter.wrap(fastAdapter);

        return fastAdapter;
    }

    @Override
    @MenuRes
    public int getActionModeMenuId() {
        return R.menu.menu_fragment_lifelog_list;
    }


    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
                return new CursorLoader(getContext(), LifeLogContract.LifeLogEntryTable.CONTENT_URI,
                                        null, // projection
                                        LifeLogRepository.LATEST_ENTRY_CONDITION, // selection
                                        null, // selectionArgs
                                        null); // sortOrder
            }

            @Override
            public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
                getEntryFastAdapter().swapCursor(data);
            }

            @Override
            public void onLoaderReset(final Loader<Cursor> loader) {
                getEntryFastAdapter().swapCursor(null);
            }
        });
    }

    public LifeLogEntryFastAdapter getEntryFastAdapter() {
        return entryFastAdapter;
    }

    @Override
    public boolean onClick(final View v, final IAdapter<LifeLogEntryFastAdapterItem> adapter, final LifeLogEntryFastAdapterItem item, final int position) {

        final Activity activity = getActivity();
        if (activity instanceof OnEntryClickedListener) {
            final OnEntryClickedListener entryClickedListener = (OnEntryClickedListener) activity;
            entryClickedListener.onEntryClicked(item.getIdentifier());
            return true;
        }
        return false;
    }

    @Override
    public ActionMode.Callback onCreateActionModeCallback() {
        return new LifeLogListFragmentActionModeCallback();
    }

    private class LifeLogListFragmentActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_share:
                    // TODO
                    Snackbar.make(getRecyclerView(), "To be implemented", Snackbar.LENGTH_LONG).show();
                    mode.finish();
                    return true;

                case R.id.action_delete:

//                    mUndoHelper.remove(findViewById(android.R.id.content), "Item removed", "Undo", Snackbar.LENGTH_LONG, mFastAdapter.getSelections());
                    try {

                        final Set<LifeLogEntryFastAdapterItem> selectedItems = getFastAdapter().getSelectedItems();

                        // convert items to ids
                        final List<Long> selectedIds = new ArrayList<>();
                        for (final LifeLogEntryFastAdapterItem selItem : selectedItems) {
                            selectedIds.add(selItem.getIdentifier());
                        }

                        LifeLogRepository.deleteEntries(getContext(), selectedIds);
                        mode.finish();
                    } catch (RemoteException | OperationApplicationException e) {
                        Logr.error(e);
                        Snackbar.make(getRecyclerView(),
                                      getString(R.string.Error_CouldNotSaveEntry, e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                        return false;
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {

        }
    }
}
