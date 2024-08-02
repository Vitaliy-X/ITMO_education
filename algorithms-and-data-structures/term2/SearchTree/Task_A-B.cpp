#include <iostream>

using namespace std;

const int MAXN = 100005;

string s;

struct Node {
    int value;
    int key;
    int left = -1;
    int right = -1;

    Node() {
        key = rand();
    }
    Node(int a) {
        value = a;
        key = rand();
    }
};

Node treap[MAXN];
int idx = 0;
int root = -1;

pair<int, int> split(int x, int t) {
    if (t == -1) { return {-1, -1}; }
    if (treap[t].value >= x) {
        pair <int, int> tmp = split(x, treap[t].left);
        treap[t].left = tmp.second;
        return {tmp.first, t};
    } else {
        pair <int, int> tmp = split(x, treap[t].right);
        treap[t].right = tmp.first;
        return {t, tmp.second};
    }
}

int merge(int t1, int t2) {
    if (t1 == -1) { return t2; }
    if (t2 == -1) { return t1; }
    if (treap[t1].key <= treap[t2].key) {
        treap[t1].right = merge(treap[t1].right, t2);
        return t1;
    } else {
        treap[t2].left = merge(t1, treap[t2].left);
        return t2;
    }
}

bool find(int root, int x) {
    while (root != -1) {
        if (treap[root].value == x) { return true; }
        root = treap[root].value < x ? treap[root].right : treap[root].left;
    }
    return false;
}

void add(int x) {
    if (find(root, x)) return;
    if (idx == 0) {
        root = idx;
        treap[idx++] = Node(x);
    } else {
        pair<int, int> t = split(x, root);
        int node = idx;
        treap[idx++] = Node(x);
        root = merge(merge(t.first, node), t.second);
    }
}

void remove(int x) {
    if (!find(root, x)) return;
    pair <int, int> tmp1 = split(x, root), tmp2 = split(x + 1, tmp1.second);
    root = merge(tmp1.first, tmp2.second);
}

void exists(int value) {
    cout << (find(root, value) ? "true" : "false") << endl;
}

int next(int x) {
    pair <int, int> tmp = split(x + 1, root);
    int t = tmp.second;
    int num = INT_MIN;
    while (t != -1) {
        num = treap[t].value;
        t = treap[t].left;
    }
    root = merge(tmp.first, tmp.second);
    return num;
}

int prev(int x) {
    pair <int, int> tmp = split(x, root);
    int t = tmp.first;
    int num = INT_MIN;
    while (t != -1) {
        num = treap[t].value;
        t = treap[t].right;
    }
    root = merge(tmp.first, tmp.second);
    return num;
}

int main() {
    int x;
    while (cin >> s >> x) {
        if (s == "insert") add(x);
        if (s == "exists") exists(x);
        if (s == "delete") remove(x);
        if (s == "next") {
            int result = next(x);
            result != INT_MIN ? cout << result << "\n" : cout << "none\n";
        }
        if (s == "prev") {
            int result = prev(x);
            result != INT_MIN ? cout << result << "\n" : cout << "none\n";
        }
    }
    return 0;
}
