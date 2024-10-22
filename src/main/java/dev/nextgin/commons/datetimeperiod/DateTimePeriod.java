package dev.nextgin.commons.datetimeperiod;

import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateTimePeriod implements Serializable, Cloneable, Comparable<DateTimePeriod> {

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Precision precision;
    private final transient Duration duration;

    private DateTimePeriod(LocalDateTime start, LocalDateTime end, Precision precision) {
        if (start.isAfter(end)) {
            throw DateTimePeriodException.endBeforeStart(start, end);
        }

        this.start = start;
        this.end = end;
        this.precision = precision;
        this.duration = Duration.between(start, end);
    }

    /**
     * Creates a period instance representing a time period between two dates with DAY
     * precision. This method converts the LocalDate parameters to LocalDateTime using start of day
     * (00:00:00) for both dates.
     *
     * @param start The starting date of the period. Must not be null.
     * @param end   The ending date of the period. Must not be null.
     * @return A new period instance representing the period between start and end dates
     * with day-level precision
     * @see #make(LocalDateTime, LocalDateTime, Precision)
     */
    public static DateTimePeriod make(LocalDate start, LocalDate end) {
        return make(start.atStartOfDay(), end.atStartOfDay(), Precision.DAY);
    }

    /**
     * Creates a period instance representing a time period between two dates with SECOND
     * precision. This is a convenience method that calls
     * {@link #make(LocalDateTime, LocalDateTime, Precision)} with {@link Precision#SECOND} as the
     * default precision.
     *
     * @param start The starting date and time of the period. Must not be null.
     * @param end   The ending date and time of the period. Must not be null.
     * @return A new period instance representing the period between start and end dates
     * @see #make(LocalDateTime, LocalDateTime, Precision)
     */
    public static DateTimePeriod make(LocalDateTime start, LocalDateTime end) {
        return make(start, end, Precision.SECOND);
    }

    /**
     * Creates a period instance representing a time period between two dates with specified
     * precision.
     *
     * @param start     The starting date and time of the period.
     * @param end       The ending date and time of the period.
     * @param precision The precision level for the period calculation (e.g., DAY, HOUR, MINUTE).
     * @return A new period instance representing the period between start and end dates
     */
    public static DateTimePeriod make(LocalDateTime start, LocalDateTime end, Precision precision) {
        return new DateTimePeriod(precision.round(start), precision.round(end), precision);
    }

    /**
     * Checks if this period overlaps with the given period.
     *
     * @param period The DateTimePeriod to check for overlap
     * @return true if this period overlaps with the given period, false otherwise
     * @throws DateTimePeriodException if precision does not match
     */
    public boolean overlapsWith(DateTimePeriod period) {
        this.ensurePrecisionMatches(period);

        if (this.start().isAfter(period.end())) {
            return false;
        }

        if (period.start().isAfter(this.end())) {
            return false;
        }

        return true;
    }

    /**
     * Creates a new period instance with the same duration as this one, starting from the end of
     * this period.
     *
     * @return A new period with the same duration, starting from the end of this period.
     */
    public DateTimePeriod renew() {
        long diff = this.duration().toMillis();
        LocalDateTime start = this.end().plus(this.precision().interval());
        LocalDateTime end = start.plus(diff, ChronoUnit.MILLIS);
        return make(start, end, this.precision());
    }

    /**
     * Checks if this period touches with the given period. Two periods touch if the end of one
     * period is exactly the start of the other.
     *
     * @param period The period to check for touching
     * @return true if this period touches the specified period without overlapping, false otherwise
     */
    public boolean touchesWith(DateTimePeriod period) {
        this.ensurePrecisionMatches(period);

        if (period.start().isAfter(this.end())) {
            return Duration.between(this.end().plus(this.precision().interval()), period.start())
                    .isZero();
        }

        if (this.start().isAfter(period.end())) {
            return Duration.between(period.end().plus(period.precision().interval()), this.start())
                    .isZero();
        }

        return false;
    }

    /**
     * Calculates the gap between this period and the specified period.
     *
     * @param period The period to calculate the gap with
     * @return A new period representing the gap between the two periods, or null if the periods
     * overlap or touch
     */
    @Nullable
    public DateTimePeriod gap(DateTimePeriod period) {
        this.ensurePrecisionMatches(period);

        if (this.overlapsWith(period)) {
            return null;
        }

        if (this.touchesWith(period)) {
            return null;
        }

        if (!this.start().isBefore(period.end())) {
            return make(
                    period.end().plus(this.precision().interval()),
                    this.start().minus(this.precision().interval()),
                    this.precision());
        }

        return make(
                this.end().plus(this.precision().interval()),
                period.start().minus(this.precision().interval()),
                this.precision());
    }

    /**
     * Checks if this period contains the specified point in time.
     *
     * @param localDateTime The LocalDateTime to check
     * @return true if the specified time is within this period, false otherwise
     */
    public boolean contains(LocalDateTime localDateTime) {
        LocalDateTime roundedDate = this.precision().round(localDateTime);
        return !roundedDate.isBefore(this.start()) && !roundedDate.isAfter(this.end());
    }

    /**
     * Checks if this period fully contains the given period.
     *
     * @param period The period to check
     * @return true if the specified period is entirely contained within this period, false
     * otherwise
     */
    public boolean contains(DateTimePeriod period) {
        return !this.start().isAfter(period.start()) && !this.end().isBefore(period.end());
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return end;
    }

    public Precision precision() {
        return precision;
    }

    public Duration duration() {
        return duration;
    }

    private void ensurePrecisionMatches(DateTimePeriod period) {
        if (this.precision() == period.precision()) {
            return;
        }

        throw DateTimePeriodException.precisionDoesNotMatch();
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", this.start(), this.end());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        DateTimePeriod period = (DateTimePeriod) object;
        return Objects.equals(start, period.start) && Objects.equals(end, period.end)
                && precision == period.precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, precision);
    }

    @Override
    public DateTimePeriod clone() {
        try {
            return (DateTimePeriod) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int compareTo(DateTimePeriod period) {
        if (this.equals(period)) {
            return 0;
        } else if (this.start().isBefore(period.start())) {
            return -1;
        } else {
            return 1;
        }
    }
}
