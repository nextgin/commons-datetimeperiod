package nextgin.commons.datetimeperiod;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PrecisionTest {

    @Test
    void year() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.YEAR.round(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
    }

    @Test
    void month() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.MONTH.round(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 9, 1, 0, 0, 0));
    }

    @Test
    void day() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.DAY.round(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 9, 11, 0, 0, 0));
    }

    @Test
    void hour() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.HOUR.round(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 9, 11, 18, 0, 0));
    }

    @Test
    void minute() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.MINUTE.round(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 9, 11, 18, 30, 0));
    }

    @Test
    void second() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.SECOND.round(dateTime);
        assertThat(result).isEqualTo(dateTime);
    }

    @Test
    void decrement() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 11, 18, 30, 11);
        LocalDateTime result = Precision.SECOND.decrement(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 9, 11, 18, 30, 10));
    }

    @Test
    void increment() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 14, 1, 59, 59);
        LocalDateTime result = Precision.MINUTE.increment(dateTime);
        assertThat(result).isEqualTo(LocalDateTime.of(2024, 3, 14, 2, 0));
    }
}
