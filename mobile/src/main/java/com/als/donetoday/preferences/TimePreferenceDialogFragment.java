package com.als.donetoday.preferences;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.threeten.bp.LocalTime;

public class TimePreferenceDialogFragment extends RadialTimePickerDialogFragment {

    protected static final String ARG_KEY = "key";
    private TimePreference mPreference;

    public static TimePreferenceDialogFragment newInstance(final String key) {
        final TimePreferenceDialogFragment fragment = new TimePreferenceDialogFragment();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    public TimePreferenceDialogFragment() {
        setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
            @Override
            public void onTimeSet(final RadialTimePickerDialogFragment dialog, final int hourOfDay, final int minute) {
                final LocalTime t = LocalTime.of(hourOfDay, minute);
                getPreference().setValue(t);
            }
        });
    }

    public TimePreference getPreference() {
        if (mPreference == null) {
            final String key = getArguments().getString(ARG_KEY);
            final DialogPreference.TargetFragment fragment = (DialogPreference.TargetFragment) getTargetFragment();
            mPreference = (TimePreference) fragment.findPreference(key);
        }

        return mPreference;
    }
}
