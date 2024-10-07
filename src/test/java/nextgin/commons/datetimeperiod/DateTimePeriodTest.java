package nextgin.commons.datetimeperiod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DateTimePeriodTest {

    @Test
    void shouldThrowException_whenStartIsAfterEnd() {
        assertThatExceptionOfType(DateTimePeriodException.class)
                .isThrownBy(() -> DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2023, 5, 5)))
                .extracting(Throwable::getMessage)
                .satisfies(message -> assertThat(message)
                        .isEqualTo("The end time '2024-01-01T00:00' is before the start time '2023-05-05T00:00'."));
    }

    @Test
    void giveAPeriod_shouldRenew() {
        // Given
        LocalDate date = LocalDate.of(2024, 9, 11);
        DateTimePeriod period = DateTimePeriod.make(
                date.atTime(LocalTime.of(13, 10)), date.atTime(LocalTime.of(13, 30)), Precision.MINUTE);

        // When
        DateTimePeriod result = period.renew();

        // Then
        assertThat(result.start()).isEqualTo(date.atTime(LocalTime.of(13, 31)));
        assertThat(result.end()).isEqualTo(date.atTime(LocalTime.of(13, 51)));
    }

    @Test
    void givenAPeriod_shouldReturnDurationBetweenStartAndEnd() {
        // Given
        DateTimePeriod period = DateTimePeriod.make(
                LocalDate.of(2024, 9, 11).atTime(LocalTime.of(13, 10)),
                LocalDate.of(2024, 9, 15).atTime(LocalTime.of(13, 30)),
                Precision.DAY);

        // When
        Duration result = period.duration();

        // Then
        assertThat(result).isEqualTo(Duration.ofDays(4));
    }

    @Nested
    class Contains {

        @Test
        void shouldDetermineWhetherAPeriodContainsALocalDateTime() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 30));

            // Assertions
            assertThat(period.contains(LocalDate.of(2024, 9, 1).atTime(14, 30))).isTrue();
            assertThat(period.contains(LocalDate.of(2024, 9, 15).atStartOfDay()))
                    .isTrue();
            assertThat(period.contains(LocalDate.of(2024, 9, 30).atTime(LocalTime.MAX)))
                    .isTrue();

            assertThat(period.contains(LocalDate.of(2024, 10, 1).atStartOfDay()))
                    .isFalse();
            assertThat(period.contains(LocalDate.of(2024, 8, 31).atStartOfDay()))
                    .isFalse();
        }

        @Test
        void shouldDetermineWhetherAPeriodContainsAnotherPeriod() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

            // Assertions
            assertThat(period.contains(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15))))
                    .isTrue();
            assertThat(period.contains(period)).isTrue();
            assertThat(period.contains(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1))))
                    .isFalse();
            assertThat(period.contains(DateTimePeriod.make(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 15))))
                    .isFalse();
            assertThat(period.contains(DateTimePeriod.make(LocalDate.of(2023, 11, 1), LocalDate.of(2024, 2, 5))))
                    .isFalse();
        }
    }

    @Nested
    class TouchesWith {

        @Test
        void shouldReturnFalse_whenTwoPeriodsDoNotTouch() {
            // Given
            List<DateTimePeriod[]> periods = List.of(
                    new DateTimePeriod[] {
                        DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                        DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(
                                LocalDate.of(2024, 2, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 29).atStartOfDay(),
                                Precision.MINUTE),
                        DateTimePeriod.make(
                                LocalDate.of(2024, 1, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 15).atStartOfDay(),
                                Precision.MINUTE)
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(
                                LocalDate.of(2024, 1, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 15).atStartOfDay(),
                                Precision.MINUTE),
                        DateTimePeriod.make(
                                LocalDate.of(2024, 2, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 29).atStartOfDay(),
                                Precision.MINUTE)
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 29)),
                        DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                        DateTimePeriod.make(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 29))
                    });

            // When & Then
            for (DateTimePeriod[] period : periods) {
                assertThat(period).hasSize(2);
                boolean result = period[0].touchesWith(period[1]);
                assertThat(result).isFalse();
            }
        }

        @Test
        void shouldReturnTrue_whenTwoPeriodsTouch() {
            // Given
            List<DateTimePeriod[]> periods = List.of(
                    new DateTimePeriod[] {
                        DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                        DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29))
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)),
                        DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(
                                LocalDate.of(2024, 1, 1).atStartOfDay(),
                                LocalDate.of(2024, 1, 31).atTime(LocalTime.MAX)),
                        DateTimePeriod.make(
                                LocalDate.of(2024, 2, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 29).atTime(LocalTime.MAX))
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(
                                LocalDate.of(2024, 2, 1).atTime(18, 30),
                                LocalDate.of(2024, 2, 29).atStartOfDay(),
                                Precision.MINUTE),
                        DateTimePeriod.make(
                                LocalDate.of(2024, 1, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 1).atTime(18, 29),
                                Precision.MINUTE)
                    },
                    new DateTimePeriod[] {
                        DateTimePeriod.make(
                                LocalDate.of(2024, 1, 1).atStartOfDay(),
                                LocalDate.of(2024, 2, 1).atTime(18, 29),
                                Precision.HOUR),
                        DateTimePeriod.make(
                                LocalDate.of(2024, 2, 1).atTime(19, 59),
                                LocalDate.of(2024, 2, 29).atStartOfDay(),
                                Precision.HOUR)
                    });

            // When & Then
            for (DateTimePeriod[] period : periods) {
                assertThat(period).hasSize(2);
                boolean result = period[0].touchesWith(period[1]);
                assertThat(result).isTrue();
            }
        }
    }

    @Nested
    class CompareTo {

        @Test
        void shouldReturn0_whenPeriodsAreEqual() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));

            // When
            int result = a.compareTo(b);

            // Then
            assertThat(result).isEqualTo(0);
        }

        @Test
        void shouldReturn1_whenAIsGtB() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));

            // When
            int result = a.compareTo(b);

            // Then
            assertThat(result).isEqualTo(1);
        }

        @Test
        void shouldReturnNegative1_whenAIsLtB() {
            // Given
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));

            // When
            int result = a.compareTo(b);

            // Then
            assertThat(result).isEqualTo(-1);
        }
    }

    @Test
    void toString_shouldContainsStartAndEnd() {
        assertThat(DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15))
                        .toString())
                .isEqualTo("[2024-01-10T00:00, 2024-01-15T00:00]");
    }

    @Test
    void equals_shouldReturnFalse_whenTypeIsMismatchedOrElementIsNull() {
        assertThat(DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15))
                        .equals(null))
                .isFalse();
        assertThat(DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15))
                        .equals("null"))
                .isFalse();
    }
}
