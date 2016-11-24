package com.als.donetoday;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.als.donetoday.preferences.PreferenceActivity;
import com.als.util.Logr;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Random;

public class LifeLogListActivity extends AppCompatActivity implements LifeLogListFragment.OnEntryClickedListener {

    private final static int REQUEST_CODE_ENTRY_NEW = 0;

    private final static int ITEM_SETTINGS_ID = 1;
    private final static int ITEM_ABOUT_ID    = 2;

    private Drawer drawer;

    private LifeLogListFragment listFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifelog_list);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int[] drawables = new int[]{
                R.drawable.download11,
                R.drawable.download14,
                R.drawable.download19
        };

        final ImageView appBarImage = (ImageView) findViewById(R.id.app_bar_image);
        appBarImage.setImageResource(drawables[new Random().nextInt(drawables.length)]);


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.drawer_header)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.action_settings)
                                .withIdentifier(ITEM_SETTINGS_ID)
                                .withIcon(R.drawable.ic_settings_black_24dp)
                                .withIconTintingEnabled(true)
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withName(R.string.action_about)
                                .withIdentifier(ITEM_ABOUT_ID)
                                .withIcon(R.drawable.ic_info_black_24dp)
                                .withIconTintingEnabled(true)
                                .withSelectable(false)
                               )
                .withSelectedItem(-1) // no selection
                .withSavedInstance(savedInstanceState)
                // .withShowDrawerOnFirstLaunch(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(final View view, final int position, final IDrawerItem drawerItem) {
                        closeDrawer();

                        switch ((int) drawerItem.getIdentifier()) {
                            case ITEM_SETTINGS_ID:
                                startActivity(new Intent(LifeLogListActivity.this, PreferenceActivity.class));
                                return true;

                            case ITEM_ABOUT_ID:
                                new AboutDialogFragment().show(getSupportFragmentManager(), AboutDialogFragment.TAG);
                                return true;

                            default:
                                Logr.ignored("Unknown menu item with id " + drawerItem.getIdentifier());
                                return false;
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(final View drawerView) {
                        supportInvalidateOptionsMenu();
                        listFragment.finishActionMode();
                    }

                    @Override
                    public void onDrawerClosed(final View drawerView) {
                        supportInvalidateOptionsMenu();
                    }

                    @Override
                    public void onDrawerSlide(final View drawerView, final float slideOffset) {
                    }
                })
                .build();

        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        final FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.addEntry);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startEditor(null);
            }
        });

        listFragment = (LifeLogListFragment) getSupportFragmentManager().findFragmentByTag(LifeLogListFragment.TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawer.getActionBarDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void closeDrawer() {
        drawer.closeDrawer();
    }

    public void startEditor(final Long entryId) {
        finishActionMode();
        LifeLogEditActivity.startForResult(LifeLogListActivity.this, entryId, REQUEST_CODE_ENTRY_NEW);
    }

    public void finishActionMode() {
        if (listFragment != null) {
            listFragment.finishActionMode();
        }
    }

    @Override
    public void onEntryClicked(final long entryId) {
        startEditor(entryId);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_ENTRY_NEW:
                // nothing to do
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}