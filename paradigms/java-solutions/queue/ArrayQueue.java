package queue;

public class ArrayQueue extends AbstractQueue {
    private Object [] elements = new Object[8];
    private int start;

    @Override
    protected void enqueueImpl(final Object element) {
        ensureCapacity(size);
        elements[(start + size) % elements.length] = element;
    }

    private void ensureCapacity(int size) {
        if (size == 0) {
            size++;
        }
        if (elements.length <= size) {
            Object[] copy = new Object[2 * size];
            System.arraycopy(elements, start, copy, 0, elements.length - start);
            System.arraycopy(elements, 0, copy, elements.length - start, start);
            start = 0;
            elements = copy;
        }
    }

    public Object element() {
        assert size >= 1;
        return elements[start];
    }

    @Override
    protected Object dequeueImpl() {
        final Object result = elements[start];
        elements[start] = null;
        start++;
        if (start == elements.length) {
            start = 0;
        }
        return result;
    }

    public void clear() {
        elements = new Object[8];
        start = 0;
        size = 0;
    }

    protected void toStr(StringBuilder str) {
        str.append(elements[start]);
        for (int i = 1; i < size; i++) {
            str.append(", ");
            if (elements[(start+i) % elements.length] != null) {
                str.append(elements[(start+i) % elements.length]);
            }
        }
    }
}