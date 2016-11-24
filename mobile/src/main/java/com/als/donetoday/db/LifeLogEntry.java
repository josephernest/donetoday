package com.als.donetoday.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.als.util.ObjectUtils;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java.io.Serializable;
import java.util.UUID;

public class LifeLogEntry implements Serializable {

    @Nullable
    private final Long id;

    @NonNull
    private final UUID    uuID;
    @NonNull
    private final Instant creationDateTimeMs;
    @NonNull
    private final Instant changeDateTimeMs;

    @NonNull
    private final ZoneId    entryTimeZone;
    @NonNull
    private final LocalDate entryStartLocalDate;
    @Nullable
    private final LocalTime entryStartLocalTime;
    @Nullable
    private final Duration  entryDuration;

    @Nullable
    private final String entryText;

    public static LifeLogEntry getTemplate() {
        return new LifeLogEntry();
    }

    /**
     * The template for the editor
     */
    private LifeLogEntry() {

        id = null; // created by the persistence mechanism

        this.uuID = UUID.randomUUID();
        this.creationDateTimeMs = Instant.now();
        this.changeDateTimeMs = this.creationDateTimeMs;

        this.entryTimeZone = ZoneId.systemDefault();
        this.entryStartLocalDate = LocalDate.now();
        this.entryStartLocalTime = null;
        this.entryDuration = null;

        this.entryText = null;
    }

    public LifeLogEntry(@Nullable final Long id,
                        @NonNull final UUID uuID,
                        @NonNull final Instant creationDateTimeMs,
                        @NonNull final Instant changeDateTimeMs,

                        @NonNull final ZoneId entryTimeZone,
                        @NonNull final LocalDate entryStartLocalDate,
                        @Nullable final LocalTime entryStartLocalTime,
                        @Nullable final Duration entryDuration,
                        @Nullable final String entryText) {
        this.id = id;
        this.uuID = uuID;
        this.creationDateTimeMs = creationDateTimeMs;
        this.changeDateTimeMs = changeDateTimeMs;
        this.entryTimeZone = entryTimeZone;
        this.entryStartLocalDate = entryStartLocalDate;
        this.entryStartLocalTime = entryStartLocalTime;
        this.entryDuration = entryDuration;
        this.entryText = entryText;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    @NonNull
    public UUID getUUID() {
        return uuID;
    }


    @NonNull
    public Instant getCreationDateTimeMs() {
        return creationDateTimeMs;
    }

    @NonNull
    public Instant getChangeDateTimeMs() {
        return changeDateTimeMs;
    }

    @NonNull
    public ZoneId getEntryTimeZone() {
        return entryTimeZone;
    }

    @NonNull
    public ZoneOffset getEntryTimeZoneOffset() {
        if (entryStartLocalTime == null) {
            return entryTimeZone.getRules().getOffset(entryStartLocalDate.atStartOfDay(entryTimeZone).toInstant());
        } else {
            return entryTimeZone.getRules().getOffset(entryStartLocalDate.atTime(entryStartLocalTime));
        }
    }

    @NonNull
    public LocalDate getEntryStartLocalDate() {
        return entryStartLocalDate;
    }

    @Nullable
    public LocalTime getEntryStartLocalTime() {
        return entryStartLocalTime;
    }

    @Nullable
    public Duration getEntryDuration() {
        return entryDuration;
    }

    @Nullable
    public String getEntryText() {
        return entryText;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof LifeLogEntry && ObjectUtils.equals(getId(), ((LifeLogEntry) o).getId());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(getId());
    }

    public boolean changed() {
        return id == null;
    }

    @NonNull
    public LifeLogEntry withEntryTimeZone(@NonNull final ZoneId entryTimeZone) {
        if (this.entryTimeZone.equals(entryTimeZone)) {
            return this;
        }

        return new LifeLogEntry(
                null, // new entry
                this.uuID,
                this.creationDateTimeMs,
                Instant.now(), // changeDateTimeMs

                entryTimeZone,
                this.entryStartLocalDate,
                this.entryStartLocalTime,
                this.entryDuration,
                this.entryText);
    }

    @NonNull
    public LifeLogEntry withEntryStartLocalDate(@NonNull final LocalDate entryStartLocalDate) {
        if (this.entryStartLocalDate.equals(entryStartLocalDate)) {
            return this;
        }

        return new LifeLogEntry(
                null, // new entry
                this.uuID,
                this.creationDateTimeMs,
                Instant.now(), // changeDateTimeMs

                this.entryTimeZone,
                entryStartLocalDate,
                this.entryStartLocalTime,
                this.entryDuration,
                this.entryText);
    }

    @NonNull
    public LifeLogEntry withEntryStartLocalTime(@Nullable final LocalTime entryStartLocalTime) {
        if (ObjectUtils.equals(this.entryStartLocalTime, entryStartLocalTime)) {
            return this;
        }

        return new LifeLogEntry(
                null, // new entry
                this.uuID,
                this.creationDateTimeMs,
                Instant.now(), // changeDateTimeMs

                this.entryTimeZone,
                this.entryStartLocalDate,
                entryStartLocalTime,
                this.entryDuration,
                this.entryText);
    }

    @NonNull
    public LifeLogEntry withEntryDuration(@Nullable final Duration entryDuration) {
        if (ObjectUtils.equals(this.entryDuration, entryDuration)) {
            return this;
        }

        return new LifeLogEntry(
                null, // new entry
                this.uuID,
                this.creationDateTimeMs,
                Instant.now(), // changeDateTimeMs

                this.entryTimeZone,
                this.entryStartLocalDate,
                this.entryStartLocalTime,
                entryDuration,
                this.entryText);
    }

    @NonNull
    public LifeLogEntry withEntryText(@Nullable final String entryText) {
        if (ObjectUtils.equals(this.entryText, entryText)) {
            return this;
        }

        return new LifeLogEntry(
                null, // new entry
                this.uuID,
                this.creationDateTimeMs,
                Instant.now(), // changeDateTimeMs

                this.entryTimeZone,
                this.entryStartLocalDate,
                this.entryStartLocalTime,
                this.entryDuration,
                entryText);
    }
}