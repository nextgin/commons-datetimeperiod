package dev.nextgin.commons.datetimeperiod;

import java.time.LocalDateTime;

/**
 * Exception thrown to indicate errors in DateTimePeriod operations.
 */
public class DateTimePeriodException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the error condition
     */
    public DateTimePeriodException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the error condition
     * @param cause   the cause of this exception
     */
    public DateTimePeriodException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception indicating that an operation failed due to incompatible precision levels
     * between periods or operations.
     * <p>
     * This occurs when attempting to combine or compare periods with different precision levels.
     *
     * @return a new DateTimePeriodException with an appropriate error message
     */
    public static DateTimePeriodException precisionDoesNotMatch() {
        return new DateTimePeriodException("Periods precision does not match.");
    }

    /**
     * Creates an exception indicating that the end time precedes the start time in a period
     * construction or modification operation.
     *
     * @param start the invalid start time
     * @param end   the invalid end time that precedes the start time
     * @return a new DateTimePeriodException with an appropriate error message
     */
    public static DateTimePeriodException endBeforeStart(LocalDateTime start, LocalDateTime end) {
        return new DateTimePeriodException("The end time '%s' is before the start time '%s'.".formatted(start, end));
    }
}
