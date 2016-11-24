package com.als.donetoday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.als.donetoday.db.LifeLogEntry;
import com.als.donetoday.db.LifeLogRepository;
import com.als.util.Logr;

public class LifeLogEditActivity extends AppCompatActivity {

    public final static String ARG_LIFELOG_ENTRY_ID = "lifelogEntryId";
    public final static String RESULT_LIFELOG_ENTRY = "lifelogEntry";

    private LifeLogEditFragment editFragment;

    public static void startForResult(@NonNull final Activity activity, @Nullable final Long entryId, final int requestCode) {
        final Intent intent = new Intent(activity, LifeLogEditActivity.class);
        intent.putExtra(ARG_LIFELOG_ENTRY_ID, entryId);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifelog_edit);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        editFragment = (LifeLogEditFragment) getSupportFragmentManager().findFragmentByTag(LifeLogEditFragment.TAG);

        if (savedInstanceState == null) {
            final Bundle args = getIntent().getExtras();
            final Long entryId = args == null ? -1L : args.getLong(ARG_LIFELOG_ENTRY_ID, -1L);
            if (entryId != -1L) {
                final LifeLogEntry entry = LifeLogRepository.getEntryById(this, entryId);
                editFragment.setLifeLogEntry(entry);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_fragment_lifelog_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:

                setResult(RESULT_CANCELED);
                finish();
                return true;

            case R.id.action_save:

                final LifeLogEntry entry = editFragment.getLifeLogEntry();
                if (!entry.changed()) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return false;
                }

                try {
                    LifeLogRepository.insertEntry(this, entry);
                } catch (final RuntimeException e) {
                    Logr.error(e);
                    Snackbar.make(findViewById(R.id.toolbar),
                                  getString(R.string.Error_CouldNotSaveEntry, e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                    return false;
                }

                final Intent i = getIntent().putExtra(RESULT_LIFELOG_ENTRY, entry);

                setResult(RESULT_OK, i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
