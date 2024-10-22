package dev.nextgin.commons.datetimeperiod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateTimePeriodCollectionTest {

    @Test
    void empty_shouldCreateANewInstance() {
        DateTimePeriodCollection empty = DateTimePeriodCollection.empty();
        empty.add(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)));
        DateTimePeriodCollection empty2 = DateTimePeriodCollection.empty();
        assertThat(empty).hasSize(1);
        assertThat(empty2).isEmpty();
    }

    @Test
    void emptyIfNull() {
        assertThat(DateTimePeriodCollection.emptyIfNull(null)).isNotNull().isEmpty();
    }

    @Test
    void testToString() {
        // Given
        DateTimePeriodCollection current = DateTimePeriodCollection.of(
                DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)),
                DateTimePeriod.make(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)));

        // When
        String result = current.toString();

        // Then
        assertThat(result).isEqualTo("[[2024-01-01T00:00, 2024-01-20T00:00], [2024-03-01T00:00, 2024-03-31T00:00]]");
    }
}
