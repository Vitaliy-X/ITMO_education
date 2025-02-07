import java.util.concurrent.atomic.*;

/**
 * @author Гаврилюк Виталий
 */

public class Solution implements Lock<Solution.Node> {
    private final Environment env;
    private final AtomicReference<Node> tail = new AtomicReference<>(null);

    public Solution(Environment env) {
        this.env = env;
    }

    @Override
    public Node lock() {
        Node my = new Node();
        my.locked.set(true);
        Node pred = this.tail.getAndSet(my);
        if (pred != null) {
            pred.nextNode.set(my);
            while (true) {
                if (!my.locked.get()) {
                    break;
                }
                this.env.park();
            }
        }
        return my;
    }

    @Override
    public void unlock(Node node) {
        if (node.nextNode.get() == null) {
            if (this.tail.compareAndSet(node, null)) {
                return;
            } else {
                while (node.nextNode.get() == null) {}
            }
        }
        Node next = node.nextNode.get();
        next.locked.set(false);
        this.env.unpark(next.thread);
    }

    static class Node {
        final Thread thread = Thread.currentThread();
        final AtomicReference<Node> nextNode = new AtomicReference<>(null);
        final AtomicReference<Boolean> locked = new AtomicReference<>(false);
    }
}
