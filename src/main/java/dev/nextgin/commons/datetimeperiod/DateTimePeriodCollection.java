package dev.nextgin.commons.datetimeperiod;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A specialized collection for managing and manipulating sets of DateTimePeriod objects. This
 * collection provides additional operations specific to time period management while maintaining
 * the standard Collection interface contract.
 *
 * @see java.util.Collection
 * @see DateTimePeriod
 */
public class DateTimePeriodCollection implements Collection<DateTimePeriod> {

    private final ArrayList<DateTimePeriod> data;

    private DateTimePeriodCollection(ArrayList<DateTimePeriod> periods) {
        this.data = periods;
    }

    /**
     * Creates a new collection from the given periods.
     *
     * @param periods to be included in the new collection
     * @return A new collection containing all the periods from the input periods
     */
    public static DateTimePeriodCollection of(DateTimePeriod... periods) {
        ArrayList<DateTimePeriod> data = new ArrayList<>(periods.length);
        Collections.addAll(data, periods);
        return new DateTimePeriodCollection(data);
    }

    /**
     * Creates a new collection from the given Collection of periods.
     *
     * @param periods A Collection of periods to be included in the new collection
     * @return A new collection containing all the periods from the input collection
     */
    public static DateTimePeriodCollection of(Collection<DateTimePeriod> periods) {
        return new DateTimePeriodCollection(new ArrayList<>(periods));
    }

    /**
     * Creates an empty DateTimePeriodCollection.
     *
     * @return A new, empty DateTimePeriodCollection
     */
    public static DateTimePeriodCollection empty() {
        return DateTimePeriodCollection.of(Collections.emptyList());
    }

    /**
     * Returns the input collection if it's not null, otherwise returns an empty collection.
     *
     * @param collection to check
     * @return The input collection if not null, otherwise a new empty collection
     */
    public static DateTimePeriodCollection emptyIfNull(@Nullable DateTimePeriodCollection collection) {
        return collection != null ? collection : empty();
    }

    /**
     * Calculates the overlap of all periods across the given collections.
     *
     * @param collections to be considered for overlap
     * @return A new collection containing periods that represent the common overlap across all
     * input collections. If there is no common overlap, an empty collection is returned.
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection overlapAll(DateTimePeriodCollection... collections) {
        DateTimePeriodCollection overlap = this;
        for (DateTimePeriodCollection collection : collections) {
            overlap = overlap.overlap(collection);
        }
        return overlap;
    }

    private DateTimePeriodCollection overlap(DateTimePeriodCollection collection) {
        DateTimePeriodCollection overlaps = DateTimePeriodCollection.empty();
        for (DateTimePeriod period : this) {
            for (DateTimePeriod otherPeriod : collection) {
                DateTimePeriod overlap = period.overlap(otherPeriod);
                if (overlap == null) {
                    continue;
                }

                overlaps.add(overlap);
            }
        }
        return overlaps;
    }

    /**
     * Calculates the boundary period that encompasses all periods in this collection.
     *
     * @return A DateTimePeriod representing the earliest start time and latest end time of all
     * periods in the collection. If the collection is empty, returns null.
     */
    @Nullable public DateTimePeriod boundaries() {
        LocalDateTime start = null;
        LocalDateTime end = null;
        for (DateTimePeriod period : this.data) {
            if (start == null || start.isAfter(period.start())) {
                start = period.start();
            }

            if (end == null || period.end().isAfter(end)) {
                end = period.end();
            }
        }

        if (start == null || end == null) {
            return null;
        }

        return DateTimePeriod.make(start, end, this.data.get(0).precision());
    }

    /**
     * Subtracts the specified periods from this collection.
     *
     * @param periods to be subtracted from this collection
     * @return A new collection containing the remaining periods after subtraction
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection subtract(DateTimePeriod... periods) {
        if (periods.length == 0) {
            return this;
        }

        DateTimePeriodCollection subtractedPeriods = DateTimePeriodCollection.empty();
        for (DateTimePeriod period : this) {
            subtractedPeriods.addAll(period.subtractAll(periods).data);
        }

        return subtractedPeriods;
    }

    /**
     * Subtracts the specified collection from this collection.
     *
     * @param collection to be subtracted from this collection
     * @return A new collection containing the remaining periods after subtraction
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection subtract(DateTimePeriodCollection collection) {
        if (collection.size() == 0) {
            return this;
        }

        return this.subtract(collection.toArray(new DateTimePeriod[0]));
    }

    /**
     * Calculates the gaps between the periods in this collection.
     *
     * @return A new collection containing periods that represent the time gaps between the periods
     * in this collection. If there are no gaps (i.e., all periods are contiguous or overlapping),
     * returns an empty collection.
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection gaps() {
        DateTimePeriod boundaries = this.boundaries();
        if (boundaries == null) {
            return DateTimePeriodCollection.empty();
        }

        return boundaries.subtractAll(this.toArray(new DateTimePeriod[0]));
    }

    /**
     * Calculates the intersection of this collection with the given period.
     *
     * @param intersection The DateTimePeriod to intersect with this collection
     * @return A new collection containing periods that represent the intersection of each period in
     * this collection with the given period. Periods that don't intersect are excluded from the
     * result.
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection intersect(DateTimePeriod intersection) {
        DateTimePeriodCollection intersected = DateTimePeriodCollection.empty();

        for (DateTimePeriod period : this) {
            DateTimePeriod overlap = intersection.overlap(period);
            if (overlap == null) {
                continue;
            }
            intersected.add(overlap);
        }

        return intersected;
    }

    /**
     * Calculates the union of all periods in this collection.
     *
     * @return A new collection containing non-overlapping periods that represent the union of all
     * periods in this collection. Overlapping or adjacent periods are merged into single periods.
     * @throws DateTimePeriodException if precision does not match
     */
    public DateTimePeriodCollection union() {
        DateTimePeriod boundaries = this.boundaries();
        if (boundaries == null) {
            return DateTimePeriodCollection.empty();
        }

        return boundaries.subtractAll(boundaries.subtractAll(this));
    }

    /**
     * Returns the period at the specified position in this collection.
     *
     * @param index index of the element to return
     * @return the period at the specified position in this collection
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public DateTimePeriod get(int index) {
        return this.data.get(index);
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public boolean contains(Object element) {
        return this.data.contains(element);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < this.data.size(); i++) {
            sb.append(this.data.get(i).toString());
            if (i != this.data.size() - 1) {
                sb.append(',');
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    @Nonnull
    public Iterator<DateTimePeriod> iterator() {
        return data.iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return this.data.toArray();
    }

    @Override
    @Nonnull
    public <T> T[] toArray(@Nonnull T[] array) {
        return this.data.toArray(array);
    }

    @Override
    public boolean add(DateTimePeriod period) {
        return this.data.add(period);
    }

    @Override
    public boolean remove(Object element) {
        return this.data.remove(element);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> collection) {
        return this.data.containsAll(collection);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends DateTimePeriod> collection) {
        return this.data.addAll(collection);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        return this.data.removeAll(collection);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        return this.data.retainAll(collection);
    }

    @Override
    public void clear() {
        this.data.clear();
    }
}
