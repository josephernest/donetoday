package com.als.util.fastadapter;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.als.donetoday.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;

/**
 * Similar to ListFragment, but for RecyclerViews, with support for contextual action bars and FastAdapters.
 */
public abstract class FastAdapterRecyclerViewFragment<Tag, VH extends RecyclerView.ViewHolder, Item extends IItem<Tag, VH>>
        extends Fragment
        implements FastAdapter.OnClickListener<Item> {

    private RecyclerView      recyclerView;
    private FastAdapter<Item> fastAdapter;
    private ActionModeHelper  mActionModeHelper;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public FastAdapter<Item> getFastAdapter() {
        return fastAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        // setup recycler view
        final View view = inflater.inflate(getLayoutId(), container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // setup fast adapter
        fastAdapter = onCreateFastAdapter();
        recyclerView.setAdapter(fastAdapter);

        fastAdapter
                .withSavedInstanceState(savedInstanceState)
                .withOnPreClickListener(new FastAdapter.OnClickListener<Item>() {
                    @Override
                    public boolean onClick(final View v, final IAdapter<Item> adapter, final Item item, final int position) {

                        if (isActionModeActive()) {
                            final Boolean res = mActionModeHelper.onClick(item);
                            return res != null ? res : false;
                        } else {
                            return FastAdapterRecyclerViewFragment.this.onClick(v, adapter, item, position);
                        }
                    }
                })
                .withOnPreLongClickListener(new FastAdapter.OnLongClickListener<Item>() {
                    @Override
                    public boolean onLongClick(final View v, final IAdapter<Item> adapter, final Item item, final int position) {
                        if (mActionModeHelper != null) {
                            final ActionMode actionMode = mActionModeHelper.onLongClick((AppCompatActivity) getActivity(), position);
                            return actionMode != null;
                        } else {
                            return false;
                        }
                    }
                });

        // setup contextual actionbar
        final ActionMode.Callback actionModeCallback = onCreateActionModeCallback();
        if (actionModeCallback != null) {
            mActionModeHelper = new ActionModeHelper(fastAdapter, getActionModeMenuId(), actionModeCallback)
                    .withTitleProvider(new ActionModeHelper.ActionModeTitleProvider() {
                        @Override
                        public String getTitle(final int selected) {
                            return getContext().getString(R.string.actionmode_title, selected);
                        }
                    });
        }

        return view;
    }

    @LayoutRes
    public int getLayoutId() {
        return R.layout.fragment_recyclerview;
    }

    @MenuRes
    public int getActionModeMenuId() {
        return 0;
    }

    public abstract FastAdapter<Item> onCreateFastAdapter();

    public ActionMode.Callback onCreateActionModeCallback() {
        return null;
    }

    public boolean isActionModeActive() {
        return mActionModeHelper != null && mActionModeHelper.isActive();
    }

    public void finishActionMode() {
        if (isActionModeActive()) {
            mActionModeHelper.getActionMode().finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Only called when not in contextual action mode
     */
    @Override
    public boolean onClick(final View v, final IAdapter<Item> adapter, final Item item, final int position) {
        return false;
    }
}
