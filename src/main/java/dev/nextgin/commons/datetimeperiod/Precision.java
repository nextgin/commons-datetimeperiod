package dev.nextgin.commons.datetimeperiod;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public enum Precision {
    YEAR(Period.ofYears(1)),
    MONTH(Period.ofMonths(1)),
    DAY(Period.ofDays(1)),
    HOUR(Duration.ofHours(1)),
    MINUTE(Duration.ofMinutes(1)),
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
     * <p>
     * Rounding behavior for each precision:
     * - YEAR: rounds to the start of the year
     * - MONTH: rounds to the start of the month
     * - DAY: rounds to the start of the day (midnight)
     * - HOUR: rounds to the start of the hour
     * - MINUTE: rounds to the start of the minute
     * - SECOND: rounds to the start of the second
     * </p>
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

    public TemporalAmount interval() {
        return interval;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
