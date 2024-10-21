package dev.nextgin.commons.datetimeperiod;

import java.time.Duration;
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

    public LocalDateTime round(LocalDateTime date) {
        return switch (this) {
            case YEAR -> LocalDateTime.of(date.getYear(), 1, 1, 0, 0);
            case MONTH -> LocalDateTime.of(date.getYear(), date.getMonth(), 1, 0, 0);
            case DAY -> LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0);
            case HOUR -> LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), 0);
            case MINUTE -> LocalDateTime.of(
                    date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), date.getMinute());
            case SECOND -> LocalDateTime.of(
                    date.getYear(),
                    date.getMonth(),
                    date.getDayOfMonth(),
                    date.getHour(),
                    date.getMinute(),
                    date.getSecond(),
                    0);
        };
    }

    public LocalDateTime decrement(LocalDateTime date) {
        return this.round(date.minus(this.interval()));
    }

    public LocalDateTime increment(LocalDateTime date) {
        return this.round(date.plus(this.interval()));
    }

    public TemporalAmount interval() {
        return interval;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
