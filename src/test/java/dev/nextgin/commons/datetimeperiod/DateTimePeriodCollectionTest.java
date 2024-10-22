package dev.nextgin.commons.datetimeperiod;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class OverlapAll {

        @Test
        void shouldDetermineMultipleOverlapsForASingleCollection() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 10)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 20), LocalDate.of(2024, 1, 25)));

            DateTimePeriodCollection collection = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 22), LocalDate.of(2024, 1, 31)));

            // When
            DateTimePeriodCollection result = current.overlapAll(collection);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 10)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 22), LocalDate.of(2024, 1, 25)));
        }

        @Test
        void shouldDetermineMultipleOverlapsForMultipleCollections() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 25)));

            DateTimePeriodCollection a = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)));

            DateTimePeriodCollection b = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 20)));

            // When
            DateTimePeriodCollection result = current.overlapAll(a, b);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 7)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 20)));
        }
    }

    @Nested
    class Boundaries {

        @Test
        void shouldDetermineBoundariesOfACollection() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 31)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)),
                    DateTimePeriod.make(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)));

            // When
            DateTimePeriod result = current.boundaries();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)));
        }

        @Test
        void givenEmptyCollection_shouldReturnNull() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of();

            // When
            DateTimePeriod result = current.boundaries();

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    class Subtract {

        @Test
        void shouldSubtractAPeriodFromCollection() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10)),
                    DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 29)));

            DateTimePeriod subtract = DateTimePeriod.make(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 21));

            // When
            DateTimePeriodCollection result = current.subtract(subtract);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 19)));
            assertThat(result.get(2))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 22), LocalDate.of(2024, 2, 29)));
        }

        @Test
        void shouldSubtractCollectionFromACollection() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)),
                    DateTimePeriod.make(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)));

            DateTimePeriodCollection collection = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 31)),
                    DateTimePeriod.make(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 20)));

            // When
            DateTimePeriodCollection result = current.subtract(collection);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 9)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 14)));
            assertThat(result.get(2))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 3, 21), LocalDate.of(2024, 3, 31)));
        }

        @Test
        void shouldReturnSelf_whenCollectionIsEmpty() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)),
                    DateTimePeriod.make(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)));

            // When
            DateTimePeriodCollection result1 = current.subtract();
            DateTimePeriodCollection result2 = current.subtract(DateTimePeriodCollection.empty());

            // Then
            assertThat(result1).isEqualTo(current);
            assertThat(result2).isEqualTo(current);
        }
    }

    @Nested
    class Gaps {

        @Test
        void shouldDetermineGapsOfACollection() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 20), LocalDate.of(2024, 1, 25)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 30), LocalDate.of(2024, 1, 31)));

            // When
            DateTimePeriodCollection result = current.gaps();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 9)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 19)));
            assertThat(result.get(2))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 26), LocalDate.of(2024, 1, 29)));
        }

        @Test
        void shouldReturnEmptyCollection_whenCollectionEmpty() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.empty();

            // When
            DateTimePeriodCollection result = current.gaps();

            // Then
            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        void shouldReturnEmptyCollection_whenNoGapsBetweenPeriods() {
            // Given
            DateTimePeriodCollection current = DateTimePeriodCollection.of(
                    DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20)),
                    DateTimePeriod.make(LocalDate.of(2024, 1, 20), LocalDate.of(2024, 2, 15)));

            // When
            DateTimePeriodCollection result = current.gaps();

            // Then
            assertThat(result).isNotNull().isEmpty();
        }
    }
}
