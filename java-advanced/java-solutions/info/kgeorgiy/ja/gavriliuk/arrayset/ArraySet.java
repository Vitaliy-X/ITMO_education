package info.kgeorgiy.ja.gavriliuk.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> data;
    private final Comparator<? super T> comparator;

    private ArraySet(List<T> data, Comparator<? super T> comparator) {
        this.data = data;
        this.comparator = comparator;
    }

    public ArraySet(Collection<? extends T> data, Comparator<? super T> comparator) {
        if (!data.isEmpty()) {
            TreeSet<T> tree = new TreeSet<>(comparator);
            tree.addAll(data);
            this.data = List.copyOf(tree);
        } else {
            this.data = Collections.emptyList();
        }
        this.comparator = comparator;
    }

    public ArraySet(Collection<? extends T> elements) {
        this(elements, null);
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Comparator<? super T> comparator) {
        this(Collections.emptyList(), comparator);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (compare(fromElement, toElement) <= 0) {
            return new ArraySet<>(data.subList(indexOf(fromElement), indexOf(toElement)), comparator);
        }
        throw new IllegalArgumentException("fromElement must precede toElement");
    }

    private int indexOf(T element) {
        int index = Collections.binarySearch(data, element, comparator);
        return index >= 0 ? index : -1 - index;
    }

    @Override
    public ArraySet<T> headSet(T toElement) {
        return new ArraySet<>(data.subList(0, indexOf(toElement)), comparator);
    }

    @Override
    public ArraySet<T> tailSet(T fromElement) {
        return new ArraySet<>(data.subList(indexOf(fromElement), size()), comparator);
    }

    @Override
    public T first() {
        checkIsNotEmpty();
        return data.get(0);
    }

    @Override
    public T last() {
        checkIsNotEmpty();
        return data.get(size()-1);
    }

    private void checkIsNotEmpty() {
        if (data.isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Comparable)) {
            return false;
        }
        try {
            return Collections.binarySearch(data, (T) o, comparator) >= 0;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        return comparator != null ? comparator.compare(a, b) : ((Comparable<? super T>) a).compareTo(b);
    }
}
