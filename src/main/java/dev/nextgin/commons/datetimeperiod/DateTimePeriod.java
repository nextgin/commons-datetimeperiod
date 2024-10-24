package dev.nextgin.commons.datetimeperiod;

import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a period of time between two date/time points, providing operations for comparing,
 * manipulating, and analyzing time periods.
 * <p>
 * This class is immutable and thread-safe. All operations that would modify the period return a new
 * instance rather than modifying the existing one.
 */
public class DateTimePeriod implements Serializable, Comparable<DateTimePeriod> {

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
     * Creates a period instance representing a time period between two dates with DAY precision.
     * This method converts the LocalDate parameters to LocalDateTime using start of day (00:00:00)
     * for both dates.
     *
     * @param start The starting date of the period. Must not be null.
     * @param end   The ending date of the period. Must not be null.
     * @return A new period instance representing the period between start and end dates with
     * day-level precision
     * @see #make(LocalDateTime, LocalDateTime, Precision)
     */
    public static DateTimePeriod make(LocalDate start, LocalDate end) {
        return make(start.atStartOfDay(), end.atStartOfDay(), Precision.DAY);
    }

    /**
     * Creates a period instance representing a time period between two dates with SECOND precision.
     * This is a convenience method that calls
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
     * @throws DateTimePeriodException if precision does not match
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
     * @throws DateTimePeriodException if precision does not match
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
     * @throws DateTimePeriodException if precision does not match
     */
    @Nullable public DateTimePeriod gap(DateTimePeriod period) {
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
     * Returns a period that overlap with the given period and this period.
     *
     * @param period to check for overlap
     * @return A new period representing the overlapping time, or null if there's no overlap
     * @throws DateTimePeriodException if precision does not match
     */
    @Nullable public DateTimePeriod overlap(DateTimePeriod period) {
        this.ensurePrecisionMatches(period);

        LocalDateTime start = this.start().isAfter(period.start()) ? this.start() : period.start();
        LocalDateTime end = period.end().isAfter(this.end()) ? this.end() : period.end();

        if (start.isAfter(end)) {
            return null;
        }

        return DateTimePeriod.make(start, end, this.precision());
    }

    /**
     * Returns a period that overlap with all given periods and this period.
     *
     * @param periods to check for overlap
     * @return A new period representing the overlapping time, or null if there's no overlap
     * @throws DateTimePeriodException if precision does not match
     */
    @Nullable public DateTimePeriod overlapAll(DateTimePeriod... periods) {
        if (periods.length == 0) {
            return this;
        }

        DateTimePeriod overlap = this;

        for (DateTimePeriod other : periods) {
            overlap = overlap.overlap(other);
            if (overlap == null) {
                return null;
            }
        }

        return overlap;
    }

    /**
     * Returns a collection of periods that overlap with any of the given periods.
     *
     * @param periods to check for overlap
     * @return A collection containing all periods that overlap with at least one of the input
     * periods
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection overlapAny(DateTimePeriod... periods) {
        DateTimePeriodCollection overlaps = DateTimePeriodCollection.empty();
        for (DateTimePeriod period : periods) {
            DateTimePeriod overlap = this.overlap(period);
            if (overlap == null) {
                continue;
            }

            overlaps.add(overlap);
        }
        return overlaps;
    }

    /**
     * Subtracts the given period from this period, returning a new collection containing the
     * remaining non-overlapping periods.
     *
     * @param period to be subtracted from this period
     * @return A collection containing the remaining periods after subtraction
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection subtract(DateTimePeriod period) {
        this.ensurePrecisionMatches(period);

        if (!this.overlapsWith(period)) {
            return DateTimePeriodCollection.of(this);
        }

        DateTimePeriodCollection collection = DateTimePeriodCollection.empty();
        if (this.start().isBefore(period.start())) {
            collection.add(DateTimePeriod.make(
                    this.start(), period.start().minus(this.precision().interval()), this.precision()));
        }

        if (this.end().isAfter(period.end())) {
            collection.add(
                    DateTimePeriod.make(period.end().plus(this.precision().interval()), this.end(), this.precision()));
        }

        return collection;
    }

    /**
     * Subtracts all given periods from this period, returning a collection containing the remaining
     * non-overlapping periods.
     *
     * @param periods to be subtracted
     * @return A collection containing the remaining periods after subtraction
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection subtractAll(DateTimePeriod... periods) {
        DateTimePeriodCollection collection = DateTimePeriodCollection.of(this);

        if (periods.length == 0) {
            return collection;
        }

        DateTimePeriodCollection[] subtractions = new DateTimePeriodCollection[periods.length];
        for (int i = 0; i < periods.length; i++) {
            subtractions[i] = this.subtract(periods[i]);
        }
        return collection.overlapAll(subtractions);
    }

    /**
     * Subtracts all given periods from this period, returning a collection containing the remaining
     * non-overlapping periods.
     *
     * @param periods to be subtracted
     * @return A collection containing the remaining periods after subtraction
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection subtractAll(Collection<DateTimePeriod> periods) {
        return this.subtractAll(periods.toArray(new DateTimePeriod[0]));
    }

    /**
     * Calculates the symmetric difference between this period and the specified period. The
     * symmetric difference includes all time ranges that belong to either this period or the
     * specified period, but not both.
     *
     * @param period The period to calculate the symmetric difference with
     * @return A collection containing the periods that represent the symmetric difference
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection diffSymmetric(DateTimePeriod period) {
        this.ensurePrecisionMatches(period);

        if (!this.overlapsWith(period)) {
            return DateTimePeriodCollection.of(this, period);
        }

        DateTimePeriod boundaries = DateTimePeriodCollection.of(this, period).boundaries();
        DateTimePeriod overlap = this.overlap(period);
        assert boundaries != null;
        return boundaries.subtract(overlap);
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

    /**
     * Returns the start date and time of this period.
     *
     * @return the start LocalDateTime of this period
     */
    public LocalDateTime start() {
        return start;
    }

    /**
     * Returns the end date and time of this period.
     *
     * @return the end LocalDateTime of this period
     */
    public LocalDateTime end() {
        return end;
    }

    /**
     * Returns the precision level at which this period operates.
     *
     * @return the Precision enum value representing this period's precision level,
     * @see Precision
     */
    public Precision precision() {
        return precision;
    }

    /**
     * Calculates the duration of this period.
     * The duration represents the total length of time between the start
     * and end points, taking into account the period's precision.
     *
     * @return the Duration representing the length of this period.
     */
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
        return Objects.equals(start, period.start) && Objects.equals(end, period.end) && precision == period.precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, precision);
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
