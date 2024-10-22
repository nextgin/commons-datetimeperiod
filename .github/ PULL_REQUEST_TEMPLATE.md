## Description

Add functionality to calculate the gap between two `DateTimePeriod` instances, returning a new `DateTimePeriod` representing the interval between them.

## Implementation

- New method to calculate the gap between two periods
- Throws `DateTimePeriodException` when the precision does not match
- Returns `null` if periods overlap or touch

## Usage Example

```java
DateTimePeriod period1 = DateTimePeriod.make(
   LocalDate.parse('2024-01-01'),
   LocalDate.parse('2024-01-05')
);
DateTimePeriod period2 = DateTimePeriod.make(
    LocalDate.parse('2024-01-10'),
   LocalDate.parse('2024-01-31')
);

DateTimePeriod gap = period1.gapTo(period2);
// gap represents [2024-01-06T00:00:00, 2024-01-09T00:00:00]
```

