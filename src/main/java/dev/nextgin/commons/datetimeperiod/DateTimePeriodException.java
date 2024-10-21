package dev.nextgin.commons.datetimeperiod;

import java.time.LocalDateTime;

public class DateTimePeriodException extends RuntimeException {

    public DateTimePeriodException(String message) {
        super(message);
    }

    public DateTimePeriodException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DateTimePeriodException precisionDoesNotMatch() {
        return new DateTimePeriodException("Periods precision does not match.");
    }

    public static DateTimePeriodException endBeforeStart(LocalDateTime start, LocalDateTime end) {
        return new DateTimePeriodException("The end time '%s' is before the start time '%s'.".formatted(start, end));
    }
}
