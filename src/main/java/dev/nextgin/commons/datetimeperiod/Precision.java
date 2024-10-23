package dev.nextgin.commons.datetimeperiod;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;

/**
 * Defines the precision levels available for DateTimePeriod operations. The precision determines
 * the smallest unit of time that is considered significant for period calculations and
 * comparisons.
 * <p>
 * Each precision level is associated with a specific time interval that represents its
 * granularity.
 */
public enum Precision {
    /**
     * Year precision, with an interval of one year. Periods with this precision will
     * ignore units smaller than years in all operations.
     */
    YEAR(Period.ofYears(1)),

    /**
     * Day precision, with an interval of one month. Periods with this precision will ignore units
     * smaller than days in all operations.
     */
    MONTH(Period.ofMonths(1)),

    /**
     * Day precision, with an interval of one day. Periods with this precision will
     * ignore units smaller than days in all operations.
     */
    DAY(Period.ofDays(1)),

    /**
     * Hour precision, with an interval of one hour. Periods with this precision will ignore units
     * smaller than hours in all operations.
     */
    HOUR(Duration.ofHours(1)),

    /**
     * Minute precision, with an interval of one minute. Periods with this precision will ignore
     * units smaller than minutes in all operations.
     */
    MINUTE(Duration.ofMinutes(1)),

    /**
     * Second precision, with an interval of one second. Periods with this precision will ignore
     * units smaller than seconds (such as milliseconds) in all operations.
     */
    SECOND(Duration.ofSeconds(1)),
    ;

    private final TemporalAmount interval;

    Precision(TemporalAmount interval) {
        this.interval = interval;
    }

    /**
     * Rounds a LocalDateTime to this precision level by truncating smaller units. For example:
     * <pre>
     * LocalDateTime dt = LocalDateTime.parse("2024-03-15T14:30:45");
     * Precision.HOUR.round(dt)   // returns 2024-03-15T14:00
     * Precision.DAY.round(dt)    // returns 2024-03-15T00:00
     * Precision.MONTH.round(dt)  // returns 2024-03-01T00:00
     * </pre>
     *
     * @param dt The LocalDateTime to round.
     * @return A new LocalDateTime rounded to the specified precision
     */
    public LocalDateTime round(LocalDateTime dt) {
        final LocalDate date = dt.toLocalDate();
        return switch (this) {
            case YEAR -> LocalDateTime.of(dt.getYear(), 1, 1, 0, 0);
            case MONTH -> LocalDateTime.of(dt.getYear(), dt.getMonth(), 1, 0, 0);
            case DAY -> date.atStartOfDay();
            case HOUR -> date.atTime(dt.getHour(), 0);
            case MINUTE -> date.atTime(dt.getHour(), dt.getMinute());
            case SECOND -> date.atTime(dt.toLocalTime().withNano(0));
        };
    }

    /**
     * Returns the time interval associated with this precision level.
     *
     * @return the TemporalAmount representing the precision interval
     */
    public TemporalAmount interval() {
        return interval;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
