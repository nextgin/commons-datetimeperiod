package dev.nextgin.commons.datetimeperiod;

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

    public static DateTimePeriod make(LocalDate start, LocalDate end) {
        return make(start.atStartOfDay(), end.atStartOfDay(), Precision.DAY);
    }

    public static DateTimePeriod make(LocalDateTime start, LocalDateTime end) {
        return make(start, end, Precision.SECOND);
    }

    public static DateTimePeriod make(LocalDateTime start, LocalDateTime end, Precision precision) {
        return new DateTimePeriod(precision.round(start), precision.round(end), precision);
    }

    /**
     * Checks if this period overlaps with the given period.
     *
     * @param period The DateTimePeriod to check for overlap
     * @return true if this period overlaps with the given period, false otherwise
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
        return Objects.equals(start, period.start) && Objects.equals(end, period.end) && precision == period.precision;
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
