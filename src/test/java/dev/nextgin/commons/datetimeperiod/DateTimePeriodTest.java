package dev.nextgin.commons.datetimeperiod;

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

    @Nested
    class Gap {

        @Test
        void shouldDetermineGapBetweenTwoPeriods() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = current.gap(period);

            // Then
            assertThat(result).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 11), LocalDate.of(2024, 1, 14)));
        }

        @Test
        void shouldDetermineGapBetweenTwoPeriodsInReverseOrder() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = current.gap(period);

            // Then
            assertThat(result).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 11), LocalDate.of(2024, 1, 14)));
        }

        @Test
        void shouldReturnNull_whenTwoPeriodsTouche() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 11), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = current.gap(period);

            // Then
            assertThat(result).isNull();
        }

        @Test
        void shouldReturnNull_whenTwoPeriodsOverlap() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 8), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = current.gap(period);

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    class Overlap {

        @Test
        void givenTwoPeriods_shouldDetermineOverlapBetweenThem() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = a.overlap(b);

            // Then
            assertThat(result).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 31)));
        }

        @Test
        void shouldReturnNull_whenTwoPeriodsDoNotOverlap() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 5, 15), LocalDate.of(2024, 5, 20));

            // When
            DateTimePeriod result = a.overlap(b);

            // Then
            assertThat(result).isNull();
        }

        @Test
        void givenPeriodsWithOverlap_overlapAll_shouldDetermineTheOverlap() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 29));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = current.overlapAll(a, b);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5)));
        }

        @Test
        void givenPeriodsWithoutOverlap_overlapAll_shouldReturnNull() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriod result = current.overlapAll(a, b);

            // Then
            assertThat(result).isNull();
        }

        @Test
        void givenEmptyPeriods_overlapAll_shouldReturnThis() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));

            // When
            DateTimePeriod result = current.overlapAll();

            // Then
            assertThat(result).isEqualTo(current);
        }

        @Test
        void givenMultiplePeriods_overlapAny_shouldDetermineMultipleOverlaps() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 15));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 6));
            DateTimePeriod c = DateTimePeriod.make(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriodCollection result = current.overlapAny(a, b, c);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 31)));
            assertThat(result.get(1)).isEqualTo(b);
            assertThat(result.get(2))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 15)));
        }

        @Test
        void overlapAny_shouldReturnEmptyCollection_whenNoOverlapsBetweenPeriods() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriodCollection result = current.overlapAny(a, b);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class Subtract {

        @Test
        void shouldThrowException_whenPrecisionDoesNotMatch() {
            // Given
            DateTimePeriod p1 = DateTimePeriod.make(
                    LocalDate.of(2024, 1, 1).atStartOfDay(),
                    LocalDate.of(2024, 1, 5).atStartOfDay(),
                    Precision.MINUTE);
            DateTimePeriod p2 = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 20));

            // When & Then
            assertThatExceptionOfType(DateTimePeriodException.class)
                    .isThrownBy(() -> p1.subtract(p2))
                    .extracting(Throwable::getMessage)
                    .satisfies(message -> assertThat(message).isEqualTo("Periods precision does not match."));
        }

        @Test
        void givenTwoPeriodsWithoutOverlap_shouldReturnFirstPeriod() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 20));

            // When
            DateTimePeriodCollection result = a.subtract(b);

            // Then
            assertThat(result).hasSize(1).singleElement().satisfies(resultPeriod -> assertThat(resultPeriod)
                    .isEqualTo(a));
        }

        @Test
        void givenPeriodABeforePeriodB_shouldReturnStartOfPeriodA() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 5), LocalDate.of(2024, 2, 20));

            // When
            DateTimePeriodCollection result = a.subtract(b);

            // Then
            assertThat(result).hasSize(1).singleElement().satisfies(resultPeriod -> assertThat(resultPeriod)
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 4))));
        }

        @Test
        void givenPeriodAAfterPeriodB_shouldReturnStartOfPeriodA() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 20));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 5), LocalDate.of(2024, 2, 10));

            // When
            DateTimePeriodCollection result = a.subtract(b);

            // Then
            assertThat(result).hasSize(1).singleElement().satisfies(resultPeriod -> assertThat(resultPeriod)
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 20))));
        }

        @Test
        void givenPeriodABiggerThanPeriodB_shouldReturnStartOfPeriodABeforePeriodBAndEndOfPeriodBToEndOfPeriodA() {
            // Given
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 28));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 10));

            // When
            DateTimePeriodCollection result = a.subtract(b);

            // Then
            assertThat(result).hasSize(2);
            DateTimePeriod first = result.get(0);
            assertThat(first).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 4)));
            DateTimePeriod second = result.get(1);
            assertThat(second).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 28)));
        }

        @Test
        void subtractAll() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 3, 15));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 20));
            DateTimePeriod c = DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 3, 31));

            // When
            DateTimePeriodCollection result = period.subtractAll(a, b, c);

            // Then
            assertThat(result).hasSize(1);
            DateTimePeriod first = result.get(0);
            assertThat(first).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 9)));
        }

        @Test
        void givenOverlappedPeriods_shouldReturnEmptyCollection() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 20));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 1, 31), LocalDate.of(2024, 2, 25));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

            // When
            DateTimePeriodCollection result = period.subtractAll(a, b);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        void givenComplexPeriods_shouldReturnMultiplePeriods() {
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 3, 20));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 10));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31));
            DateTimePeriod c = DateTimePeriod.make(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 20));

            // When
            DateTimePeriodCollection result = period.subtractAll(a, b, c);

            // Then
            assertThat(result).hasSize(2);
            DateTimePeriod first = result.get(0);
            assertThat(first).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 21), LocalDate.of(2024, 2, 4)));
            DateTimePeriod second = result.get(1);
            assertThat(second).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 29)));
        }

        @Test
        void givenMultiplePeriodsWithinLongCurrentPeriod_shouldReturnCorrectPeriods() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31));
            DateTimePeriod a = DateTimePeriod.make(LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 10));
            DateTimePeriod b = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 20));

            // When
            DateTimePeriodCollection result = period.subtractAll(a, b);

            // Then
            assertThat(result).hasSize(3);
            DateTimePeriod first = result.get(0);
            assertThat(first).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 9)));
            DateTimePeriod second = result.get(1);
            assertThat(second).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 21), LocalDate.of(2024, 2, 4)));
            DateTimePeriod third = result.get(2);
            assertThat(third).isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 2, 11), LocalDate.of(2024, 3, 31)));
        }

        @Test
        void shouldReturnAnEmptyCollection_whenPeriodsArePeriodsLengthIsZero() {
            // Given
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

            // When
            DateTimePeriodCollection result = period.subtractAll();

            // Then
            assertThat(result).hasSize(1).singleElement().satisfies(element -> assertThat(element)
                    .isEqualTo(period));
        }
    }

    @Nested
    class DiffSymmetric {

        @Test
        void twoPeriodsWithoutOverlap() {
            // Given
            DateTimePeriod current = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod period = DateTimePeriod.make(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriodCollection result = current.diffSymmetric(period);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0)).isEqualTo(current);
            assertThat(result.get(1)).isEqualTo(period);
        }

        @Test
        void periodABeforePeriodBWithOverlap() {
            // Given
            DateTimePeriod periodA = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));
            DateTimePeriod periodB = DateTimePeriod.make(LocalDate.of(2024, 1, 8), LocalDate.of(2024, 2, 29));

            // When
            DateTimePeriodCollection result = periodA.diffSymmetric(periodB);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 7)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 11), LocalDate.of(2024, 2, 29)));
        }

        @Test
        void periodAAfterPeriodBWithOverlap() {
            // Given
            DateTimePeriod periodA = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 31));
            DateTimePeriod periodB = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));

            // When
            DateTimePeriodCollection result = periodA.diffSymmetric(periodB);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 9)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 31)));
        }

        @Test
        void periodBWithinPeriodA() {
            // Given
            DateTimePeriod periodA = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
            DateTimePeriod periodB = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));

            // When
            DateTimePeriodCollection result = periodA.diffSymmetric(periodB);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 9)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 31)));
        }

        @Test
        void periodAWithinPeriodB() {
            // Given
            DateTimePeriod periodA = DateTimePeriod.make(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));
            DateTimePeriod periodB = DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

            // When
            DateTimePeriodCollection result = periodA.diffSymmetric(periodB);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 9)));
            assertThat(result.get(1))
                    .isEqualTo(DateTimePeriod.make(LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 31)));
        }
    }
}
