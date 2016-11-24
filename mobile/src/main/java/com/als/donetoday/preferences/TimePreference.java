package com.als.donetoday.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.als.donetoday.R;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class TimePreference
        extends DialogPreference
        implements AbstractPreferenceFragment.DialogCreatingPreference {

    @NonNull
    private LocalTime value    = LocalTime.parse(getContext().getString(R.string.DefaultReminderTime_Default));
    private boolean   valueSet = false;

    private LocalTime defltValue;

    public TimePreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.time_picker_dialog);

    }

    @Nullable
    public static String toPreferenceString(@Nullable final LocalTime time) {
        return time == null ? null : time.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @NonNull
    public static LocalTime fromPreferenceString(@NonNull final Context ctxt, @Nullable String string) {
        if (string == null) {
            string = ctxt.getString(R.string.DefaultReminderTime_Default);
        }

        try {
            return LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (final DateTimeParseException e) {
            return LocalTime.parse(ctxt.getString(R.string.DefaultReminderTime_Default));
        }
    }

    public void setValue(@NonNull final LocalTime value) {

        final boolean changed = !this.value.equals(value);
        if (changed || !valueSet) {
            this.value = value;
            valueSet = true;
            persistString(toPreferenceString(value));
            if (changed) {
                notifyChanged();
            }
        }
    }

    @NonNull
    public LocalTime getValue() {
        return value;
    }

    @NonNull
    private LocalTime getPersistedTime(@NonNull final LocalTime time) {
        if (!shouldPersist()) {
            return time;
        }

        return fromPreferenceString(getContext(), getPersistedString(toPreferenceString(time)));
    }

    @Override
    @NonNull
    protected LocalTime onGetDefaultValue(@NonNull final TypedArray a, final int index) {
        defltValue = fromPreferenceString(getContext(), a.getString(index));
        return defltValue;
    }

//    @Nullable
//    /* package */ LocalTime getDefaultValue() {
//        return defltValue;
//    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, @NonNull final Object defaultValue) {
        setValue(restoreValue ? getPersistedTime(getValue()) : (LocalTime) defaultValue);
    }

    @Override
    @NonNull
    public DialogFragment createDialogFragment() {
        final TimePreferenceDialogFragment d = TimePreferenceDialogFragment.newInstance(getKey());

        final LocalTime t = getValue();
        d.setStartTime(t.getHour(), t.getMinute());

        return d;
    }
}
