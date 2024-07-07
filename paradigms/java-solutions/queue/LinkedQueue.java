package queue;

public class LinkedQueue extends AbstractQueue {
    private Node back = new Node(null, null);
    private Node start = new Node(null, back);

    @Override
    protected void enqueueImpl(final Object element) {
        back.element = element;
        back.prev = new Node(null, null);
        back = back.prev;
    }

    public Object element() {
        assert size >= 1;
        return start.prev.element;
    }

    @Override
    protected Object dequeueImpl() {
        Object result = start.prev.element;
        start.prev.element = null;
        start.prev = start.prev.prev;
        return result;
    }

    public void clear() {
        back = new Node(null, null);
        start = new Node(null, back);
        size = 0;
    }

    protected void toStr(StringBuilder str) {
        str.append(start.prev.element);
        toStr(str, start.prev.prev, size-1);
    }

    private void toStr(StringBuilder str, Node start, int size) {
        if (size > 0) {
            str.append(", ");
            str.append(start.element);
        } else {
            return;
        }
        toStr(str, start.prev, size-1);
    }

    private static class Node {
        private Object element;
        private Node prev;

        public Node(Object element, Node prev) {
            this.element = element;
            this.prev = prev;
        }
    }
}