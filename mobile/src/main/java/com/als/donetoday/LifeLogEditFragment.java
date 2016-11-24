package com.als.donetoday;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.als.donetoday.db.LifeLogEntry;
import com.als.donetoday.db.LifeLogRepository;
import com.als.util.Logr;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.timezonepicker.TimeZoneInfo;
import com.codetroopers.betterpickers.timezonepicker.TimeZonePickerDialogFragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

public class LifeLogEditFragment extends Fragment implements View.OnClickListener {

    public final static String TAG                  = "LifeLogEditFragment";
    public final static String ARG_LIFELOG_ENTRY_ID = "lifelogEntryId";
    public final static String SAVE_LIFELOG_ENTRY   = "lifelogEntry";

    public LifeLogEditFragment() {
    }

    private Button   year;
    private Button   monthDay;
    private Button   timezone;
    private EditText entryText;

    // we keep the entry current wrt. all editable fields except entryText. EntryText is set in getLifeLogEntry().
    // TODO: we should use a better, straight forward approach
    private LifeLogEntry entry;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Bundle arguments = getArguments();
            final Long entryId = arguments == null ? -1L : arguments.getLong(ARG_LIFELOG_ENTRY_ID, -1L);
            if (entryId != -1L) {
                entry = LifeLogRepository.getEntryById(getContext(), entryId);
            } else {
                entry = LifeLogEntry.getTemplate();
            }
        } else {
            entry = (LifeLogEntry) savedInstanceState.getSerializable(SAVE_LIFELOG_ENTRY);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_LIFELOG_ENTRY, entry);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lifelog_edit, container, false);

        year = (Button) view.findViewById(R.id.year);
        monthDay = (Button) view.findViewById(R.id.month_day);
        timezone = (Button) view.findViewById(R.id.timezone);
        entryText = (EditText) view.findViewById(R.id.editText);

        year.setOnClickListener(this);
        monthDay.setOnClickListener(this);
        timezone.setOnClickListener(this);

        lifeLogEntry2Form(entry);

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void lifeLogEntry2Form(final LifeLogEntry entry) {

        final ZoneId entryTimeZone = entry.getEntryTimeZone();
        final LocalDate entryStartLocalDate = entry.getEntryStartLocalDate();
        // final LocalTime entryStartLocalTime = entry.getEntryStartLocalTime(); // TODO
        // final Duration entryDuration = entry.getEntryDuration(); // TODO
        final String entryText = entry.getEntryText();

        final long epochMS = entryStartLocalDate.atStartOfDay(entryTimeZone).toEpochSecond() * 1000L;

        year.setText(Integer.toString(entryStartLocalDate.getYear()));
        monthDay.setText(DateUtils.formatDateTime(getActivity(), epochMS,
                                                  DateUtils.FORMAT_SHOW_WEEKDAY
                                                  | DateUtils.FORMAT_SHOW_DATE
                                                  | DateUtils.FORMAT_NO_YEAR
                                                  | DateUtils.FORMAT_ABBREV_MONTH
                                                  | DateUtils.FORMAT_ABBREV_WEEKDAY));
        timezone.setText(entryTimeZone.getId());
        this.entryText.setText(entryText);
    }


    private void setDate(final int year, final int monthOfYear, final int dayOfMonth) {
        entry = getLifeLogEntry().withEntryStartLocalDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth));
        lifeLogEntry2Form(entry);
    }

    public void setTimeZone(final String timeZone) {
        entry = getLifeLogEntry().withEntryTimeZone(ZoneId.of(timeZone));
        lifeLogEntry2Form(entry);
    }

    @NonNull
    public LifeLogEntry getLifeLogEntry() {
        return entry.withEntryText(entryText.getText().toString().trim());
    }

    public void setLifeLogEntry(final LifeLogEntry entry) {
        this.entry = entry;
        lifeLogEntry2Form(entry);
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.year:
            case R.id.month_day: {

                // TODO: if only >= Lollipop is supported, we might use the build in version, however, for timezones, we still would need BetterPickers
                final CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(final CalendarDatePickerDialogFragment dialog, final int year, final int monthOfYear, final int dayOfMonth) {
                                setDate(year, monthOfYear, dayOfMonth);
                            }
                        });
                final LocalDate concernedDay = entry.getEntryStartLocalDate();
                cdp.setPreselectedDate(concernedDay.getYear(), concernedDay.getMonthValue() - 1, concernedDay.getDayOfMonth());
                cdp.show(getFragmentManager(), "CDP");
            }
            break;

            case R.id.timezone: {

                final TimeZonePickerDialogFragment tzpd = new TimeZonePickerDialogFragment();
                tzpd.setOnTimeZoneSetListener(new TimeZonePickerDialogFragment.OnTimeZoneSetListener() {
                    @Override
                    public void onTimeZoneSet(final TimeZoneInfo tzi) {
                        setTimeZone(tzi.mTzId);
                    }
                });

                final LocalDate concernedDay = entry.getEntryStartLocalDate();
                final ZoneId concernedTimeZone = entry.getEntryTimeZone();

                final long epochMS = concernedDay.atStartOfDay(concernedTimeZone).toEpochSecond() * 1000L;

                final Bundle bundle = new Bundle();
                bundle.putLong(TimeZonePickerDialogFragment.BUNDLE_START_TIME_MILLIS, epochMS);
                bundle.putString(TimeZonePickerDialogFragment.BUNDLE_TIME_ZONE, concernedTimeZone.getId());
                tzpd.setArguments(bundle);
                tzpd.show(getFragmentManager(), "TZPD");
            }
            break;

            default:
                Logr.error("received onClick call for an unknown view with id " + v.getId());
        }
    }
}
