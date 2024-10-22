# Date Time Period &mdash; `datetimeperiod`

This package helps you work with dates and times in a more flexible way. It lets you:

- Compare multiple time periods at once
- Find out when different time periods overlap
- Calculate the gaps between time periods
- Do basic comparisons between two time periods

`DateTimePeriod` objects are always immutable, ensuring that input dates remain unchanged and
operations does not cause side effects.

> This package is still a **work in progress**.

## Installation

You can pull the dependency from the central Maven repositories:

```xml

<dependency>
  <groupId>dev.nextgin.commons</groupId>
  <artifactId>datetimeperiod</artifactId>
  <version></version>
</dependency>
```

## Usage

The `DateTimePeriod` methods:

### Creating periods

Create period with specific date-times and precision:

```java
DateTimePeriod period = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 9, 30),
        LocalDateTime.of(2024, 1, 1, 17, 45),
        Precision.MINUTE
);
```

Create period with default `SECOND` precision:

```java
DateTimePeriod period = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 9, 30),
        LocalDateTime.of(2024, 1, 1, 17, 45)
);
```

Create period using dates (automatically uses `DAY` precision):

```java
DateTimePeriod period = DateTimePeriod.make(
        LocalDate.of(2024, 1, 1),
        LocalDate.of(2024, 1, 5)
);
```

### `boolean overlapsWith(DateTimePeriod period)`

Whether a period overlaps with another period.

```java
DateTimePeriod morning = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 9, 0),
        LocalDateTime.of(2024, 1, 1, 12, 0)
);

DateTimePeriod midday = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 11, 0),
        LocalDateTime.of(2024, 1, 1, 14, 0)
);

boolean overlaps = morning.overlapsWith(midday); // returns true
```

### `boolean touchesWith(DateTimePeriod period)`

Whether a period touches with another period. Two periods touch if the end of one period is
exactly the start of the other.

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-05")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-05"),
        LocalDate.parse("2024-01-31")
);

boolean touches = period1.touchesWith(period2); // returns true
```

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-05")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-02"),
        LocalDate.parse("2024-01-31")
);

boolean touches = period1.touchesWith(period2); // returns false
```

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-05")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-07"),
        LocalDate.parse("2024-01-31")
);

boolean touches = period1.touchesWith(period2); // returns false
```

### `DateTimePeriod renew()`

Renew creates a new period instance with the same duration as the current one,
starting from the end.

![](./docs/images/period-renew.svg)

```java
DateTimePeriod period = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-05")
);

DateTimePeriod renew = period.renew();
// renew represents [2024-01-06T00:00, 2024-01-10T00:00]
```

### `boolean contains(LocalDateTime localDateTime)`

Whether a period contains a specified point in time.

```java
DateTimePeriod workday = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 9, 0),
        LocalDateTime.of(2024, 1, 1, 17, 0)
);

LocalDateTime lunchtime = LocalDateTime.of(2024, 1, 1, 12, 30);
boolean containsTime = workday.contains(lunchtime); // returns true

LocalDateTime evening = LocalDateTime.of(2024, 1, 1, 19, 0);
boolean containsEvening = workday.contains(evening); // returns false
```

### `boolean contains(DateTimePeriod period)`

Whether a period fully contains another period.

```java
DateTimePeriod workday = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 9, 0),
        LocalDateTime.of(2024, 1, 1, 17, 0)
);

DateTimePeriod morning = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 9, 0),
        LocalDateTime.of(2024, 1, 1, 12, 0)
);

boolean containsMorning = workday.contains(morning); // returns true

DateTimePeriod evening = DateTimePeriod.make(
        LocalDateTime.of(2024, 1, 1, 16, 0),
        LocalDateTime.of(2024, 1, 1, 22, 0)
);
boolean containsEvening = workday.contains(evening); // returns false
```

### `DateTimePeriod gap(DateTimePeriod period)`

Calculates the gap between two periods.

![](./docs/images/period-gap.svg)

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-05")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-10"),
        LocalDate.parse("2024-01-31")
);

DateTimePeriod gap = period1.gap(period2);
// gap represents [2024-01-06T00:00, 2024-01-09T00:00]
```

### `DateTimePeriod overlap(DateTimePeriod period)`

Overlap of two periods.

![](./docs/images/period-overlap.svg)

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-05"),
        LocalDate.parse("2024-01-20")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-15")
);

DateTimePeriod overlap = period1.overlap(period2);
// overlap represents [2024-01-05T00:00, 2024-01-15T00:00]
```

### `DateTimePeriod overlapAll(DateTimePeriod... periods)`

Finds the overlapping period that is common to all provided periods. If there is no single period
that overlaps with all others, returns null.

![](./docs/images/period-overlap-all.svg)

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-05"),
        LocalDate.parse("2024-01-20")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-15")
);

DateTimePeriod period3 = DateTimePeriod.make(
        LocalDate.parse("2024-01-10"),
        LocalDate.parse("2024-01-31")
);

DateTimePeriod overlapAll = period1.overlapAll(period2, period3);
// overlapAll represents [2024-01-10T00:00, 2024-01-15T00:00]
```

### `DateTimePeriodCollection overlapAny(DateTimePeriod... periods)`

Finds all overlapping periods among the provided periods. When two or more periods overlap, the
overlapping period is added to the result collection.

![](./docs/images/period-overlap-any.svg)

```java
DateTimePeriod period1 = DateTimePeriod.make(
        LocalDate.parse("2024-01-20"),
        LocalDate.parse("2024-01-31")
);

DateTimePeriod period2 = DateTimePeriod.make(
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-10")
);

DateTimePeriod period3 = DateTimePeriod.make(
        LocalDate.parse("2024-01-05"),
        LocalDate.parse("2024-01-25")
);

DateTimePeriod overlapAny = period1.overlapAny(period2, period3);
// [[2024-01-05, 2024-01-10], [2024-01-20, 2024-01-25]]
```

---

### Testing

```bash
./mvnw verify
```

### Changelog

Please see [CHANGELOG](CHANGELOG.md) for more information on what has changed recently.

### Security

If you discover any security related issues, please
email [contact@nextgin.dev](mailto:contact@nextgin.dev) instead of using
the issue tracker.

## Credits

- [Othmane OUTAMA](https://github.com/ooutama)
- [All Contributors](../../contributors)

## Support us

## License

The MIT License (MIT). Please see [License File](LICENSE) for more information.
