package queue;

public abstract class AbstractQueue implements Queue {
    protected int size;

    @Override
    public void enqueue(final Object element) {
        assert element != null;
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    @Override
    public Object dequeue() {
        assert size >= 1;
        size--;
        return dequeueImpl();
    }

    @Override
    public String toStr() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (size > 0) {
            toStr(str);
        }
        str.append("]");
        return str.toString();
    }

    protected abstract Object dequeueImpl();

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    protected abstract void toStr(StringBuilder str);

    @Override
    public int count(Object x) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            Object element = dequeue();
            if (element.equals(x)) {
                count++;
            }
            enqueue(element);
        }
        return count;
    }
}
