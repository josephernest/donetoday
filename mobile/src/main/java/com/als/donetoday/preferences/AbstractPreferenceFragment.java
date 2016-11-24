package com.als.donetoday.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.als.util.Logr;
import com.als.util.StringUtils;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public abstract class AbstractPreferenceFragment
        extends PreferenceFragmentCompatDividers
        implements OnSharedPreferenceChangeListener {

    /**
     * If the activity implements OnSetNameListener, it is informed about the preference screen title
     * when this fragment is resumed. Can be used e.g. to adjust a fragment header or action bar title.
     */
    public interface OnSetTitleCallback {
        void setFragmentTitle(String title);
    }

    /**
     * If a DialogPreference implements this interface, this fragment asks it for the PreferenceDialogFragmentCompat
     */
    public interface DialogCreatingPreference {
        DialogFragment createDialogFragment();
    }

    /**
     * If a DialogPreference implements this interface, this fragment asks it to start an activity.
     * I don't see a good way to forward onActivityResult to the preference (how can it be found?),
     * so it must be implemented e.g. in the PreferenceFragment.
     */
    public interface StartForResultPreference {
        void startForResult(Activity activity, PreferenceFragmentCompat fragment);
    }

    protected static final String FRAGMENT_DIALOG_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    /**
     * Overwrite in subclasses to update preferences (e.g. summaries) when preference values change and onResume().
     */
    public abstract void updateState(final Context ctxt, final String key);

    @Override
    public void onResume() {
        super.onResume();

        final FragmentActivity activity = getActivity();
        if (activity instanceof OnSetTitleCallback) {
            ((OnSetTitleCallback) activity).setFragmentTitle(getPreferenceScreenTitle());
        }

        updateState(getActivity(), null);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedpreferences, final String key) {
        final Activity a = getActivity();
        if (a != null) {
            updateState(a, key);
            a.sendBroadcast(new Intent("com.als.taskstodo." + StringUtils.upperInitial(key) + "Changed"));
        }
    }

    @Nullable
    private String getPreferenceScreenTitle() {
        try {
            return getPreferenceScreen().getTitle().toString();
        } catch (final NullPointerException e) {
            Logr.ignored(e);
            return null;
        }
    }

    @Override
    public void onDisplayPreferenceDialog(final Preference preference) {
        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {

            DialogFragment f = null;

            if (preference instanceof StartForResultPreference) {
                final StartForResultPreference p = (StartForResultPreference) preference;
                p.startForResult(getActivity(), this);

            }
            else if (preference instanceof DialogCreatingPreference) {
                final DialogCreatingPreference p = (DialogCreatingPreference) preference;
                f = p.createDialogFragment();
            }
            else {
                super.onDisplayPreferenceDialog(preference);
            }

            if (f != null) {
                f.setTargetFragment(this, 0);
                f.show(this.getFragmentManager(), FRAGMENT_DIALOG_TAG);
            }
        }
    }
}