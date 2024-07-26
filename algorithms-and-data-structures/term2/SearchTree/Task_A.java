//package SearchTree;

import java.util.*;

public class Task_A {
    public static Node root;

    public static void main(String[] args) {
        root = new Node();

        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String operation = in.next();
            int x = in.nextInt();

            if (operation.equals("insert")) {
                insert(x);
            } else if (operation.equals("delete")) {
                delete(x);
            } else if (operation.equals("exists")) {
                exists(x);
            } else if (operation.equals("next")) {
                next(x);
            } else if (operation.equals("prev")) {
                prevPrint(x);
            }
        }
    }

    public static boolean isEmpty() {
        return (root.right == null) && (root.left == null);
    }

    public static void exists(int x) {
        System.out.println(find(x) ? "true" : "false");
    }

    public static boolean find(int x) {
        if (isEmpty()) return false;

        Node current = root.right;
        boolean ex;

        while (true) {
            if (current.value > x) {
                if (current.left != null) {
                    current = current.left;
                } else {
                    ex = false;
                    break;
                }
            } else if (current.value < x) {
                if (current.right != null) {
                    current = current.right;
                } else {
                    ex = false;
                    break;
                }
            } else {
                ex = true;
                break;
            }
        }

        return ex;
    }

    public static void insert(int x) {
        if (find(x)) return;

        Node cur = root.right;
        Node parent = root;

        while (cur != null) {
            parent = cur;
            if (cur.value < x) {
                cur = cur.right;
            } else {
                cur = cur.left;
            }
        }

        Node newNode = new Node(x);
        newNode.parent = parent;

        if (parent.value < x) {
            parent.right = newNode;
        } else {
            parent.left = newNode;
        }
    }

    public static void next(int x) {
        if (isEmpty()) {
            System.out.println("none");
            return;
        }

        Node current = root.right;
        Node successor = null;

        while (current != null) {
            if (current.value > x) {
                successor = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (successor == null) {
            System.out.println("none");
        } else {
            System.out.println(successor.value);
        }
    }

    public static Node prev(int x) {
        if (isEmpty()) return null;

        Node current = root.right;
        Node predecessor = null;

        while (current != null) {
            if (current.value < x) {
                predecessor = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }

        return predecessor;
    }


    public static void prevPrint(int x) {
        Node node = prev(x);
        if (node != null) {
            System.out.println(node.value);
        }
        else {
            System.out.println("none");
        }
    }

    public static void delete(int x) {
        if (!find(x)) return;

        Node cur = root;

        while (cur.value != x) {
            cur = x < cur.value ? cur.left : cur.right;
        }

        Node parent = cur.parent;
        Node child = cur.left != null ? cur.left : cur.right;

        if (child != null) child.parent = parent;

        if (cur == root) {
            root = child;
        } else if (cur == parent.left) {
            parent.left = child;
        } else {
            parent.right = child;
        }

        cur.parent = null;
        cur.left = null;
        cur.right = null;
    }
}

class Node {
    public int value;
    public Node left;
    public Node right;
    public Node parent;

    public Node() {}

    public Node(int value) {
        this.value = value;
    }
}