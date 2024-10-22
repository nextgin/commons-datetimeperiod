package dev.nextgin.commons.datetimeperiod;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

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
