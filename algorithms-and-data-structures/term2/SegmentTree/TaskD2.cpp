#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

vector<int> a;
ll eps = 1e18 + 2, none = -1e18 - 2;
size_t n, k;
struct node {
    ll value = 0;
    ll set = none;
    ll add = 0;
};
vector<node> tree;


void build(int v, int L, int R)
{
    if (L == R-1)
    {
        tree[v].value = a[L];
        return;
    }
    long M = L + (R - L) / 2;
    build(2 * v + 1, L, M);
    build(2 * v + 2, M, R);
    tree[v].value = min(tree[2 * v + 1].value, tree[2 * v + 2].value);
}

void push(size_t v, size_t l, size_t r) {
    if (r - l == 1) return;
    if (tree[v].set != none) {
        tree[2 * v + 1].set = tree[v].set;
        tree[2 * v + 2].set = tree[v].set;
        tree[2 * v + 1].value = tree[v].set;
        tree[2 * v + 2].value = tree[v].set;
        tree[2 * v + 1].add = 0;
        tree[2 * v + 2].add = 0;
        tree[v].set = none;
    }
    tree[2 * v + 1].add += tree[v].add;
    tree[2 * v + 2].add += tree[v].add;
    tree[2 * v + 1].value += tree[v].add;
    tree[2 * v + 2].value += tree[v].add;
    tree[v].add = 0;
}

void update(size_t v, int l, int r, int a1, int b, int x) {
    push(v, l, r);
    if (b <= l || a1 >= r) {
        return;
    }
    if (l >= a1 && r <= b) {
        tree[v].set = x;
        tree[v].add = 0;
        tree[v].value = x;
        return;
    }
    update(2 * v + 1, l, (l + r) / 2, a1, b, x);
    update(2 * v + 2, (l + r) / 2, r, a1, b, x);
    tree[v].value = min(tree[2 * v + 1].value, tree[2 * v + 2].value);
}

void add(int v, int l, int r, int a1, int b, int x) {
    push(v, l, r);
    if (b <= l || a1 >= r) {
        return;
    }
    if (l >= a1 && r <= b) {
        tree[v].add += x;
        tree[v].value += x;
        return;
    }
    add(2 * v + 1, l, (l + r) / 2, a1, b, x);
    add(2 * v + 2, (l + r) / 2, r, a1, b, x);
    tree[v].value = min(tree[2 * v + 1].value, tree[2 * v + 2].value);
}

ll min(size_t v, int l, int r, int a1, int b) {
    push(v, l, r);
    if (a1 <= l && r <= b) {
        return tree[v].value;
    }
    if (l >= b || r <= a1) {
        return eps;
    }
    return min(min(2 * v + 1, l, (l + r) / 2, a1, b),
               min(2 * v + 2, (l + r) / 2, r, a1, b));
}

signed main() {
    cin >> n;
    a.resize(n);
    tree.resize(4*n);
    for (size_t i = 0; i < n; i++) {
        cin >> a[i];
    }
    build(0, 0, n);
    string op;
    while (cin >> op) {
        int l, r;
        cin >> l >> r;
        if (op == "min") {
            cout << min(0, 0, k, l - 1, r) << "\n";
        } else {
            int x;
            cin >> x;
            if (op == "set") {
                update(0, 0, k, l - 1, r, x);
            } else {
                add(0, 0, k, l - 1, r, x);
            }
        }
    }
    return 0;
}


//void push(size_t v, size_t l, size_t r) {
//    if (r - l == 1) return;
//    if (tree[v].set != none) {
//        tree[2 * v + 1].set = tree[v].set;
//        tree[2 * v + 2].set = tree[v].set;
//        tree[2 * v + 1].value = tree[v].set;
//        tree[2 * v + 2].value = tree[v].set;
//        tree[2 * v + 1].add = 0;
//        tree[2 * v + 2].add = 0;
//        tree[v].set = none;
//    }
//    tree[2 * v + 1].add += tree[v].add;
//    tree[2 * v + 2].add += tree[v].add;
//    tree[2 * v + 1].value += tree[v].add;
//    tree[2 * v + 2].value += tree[v].add;
//    tree[v].add = 0;
//}