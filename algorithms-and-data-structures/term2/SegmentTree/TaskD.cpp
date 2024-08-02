#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;
typedef unsigned int uint;

uint n, k;
struct Node {
    ll data = 0;
    ll modify_set = LONG_LONG_MIN;
    ll modify_add = 0;
};

vector<int> a;
vector<Node> tree;

void build()
{
    k = 1;
    while (k < a.size())
    {
        k *= 2;
    }

    for (uint i = 0; i < a.size(); i++)
    {
        tree[k - 1 + i].data = a[i];
    }

    for (uint i = a.size(); i < k; i++)
    {
        tree[k - 1 + i].data = LONG_LONG_MAX;
    }

    for (int i = k - 2; i >= 0; i--)
    {
        tree[i].data = min(tree[2 * i + 1].data, tree[2 * i + 2].data);
    }
}

void push(int v, int l, int r)
{
    if (r - l == 1)
    {
        return;
    }
    if (tree[v].modify_set != LONG_LONG_MIN)
    {
        tree[2 * v + 1].modify_set = tree[v].modify_set;
        tree[2 * v + 1].data = tree[v].modify_set;
        tree[2 * v + 1].modify_add = 0;

        tree[2 * v + 2].modify_set = tree[v].modify_set;
        tree[2 * v + 2].data = tree[v].modify_set;
        tree[2 * v + 2].modify_add = 0;

        tree[v].modify_set = LONG_LONG_MIN;
    }
    tree[2 * v + 1].modify_add += tree[v].modify_add;
    tree[2 * v + 1].data += tree[v].modify_add;

    tree[2 * v + 2].modify_add += tree[v].modify_add;
    tree[2 * v + 2].data += tree[v].modify_add;

    tree[v].modify_add = 0;
}

void set(int v, int l, int r, int a1, int b, int x)
{
    push(v, l, r);
    if (b <= l || a1 >= r)
    {
        return;
    }
    if (l >= a1 && r <= b)
    {
        tree[v].modify_set = x;
        tree[v].modify_add = 0;
        tree[v].data = x;
        return;
    }
    int mid = l + (r - l) / 2;
    set(2 * v + 1, l, mid, a1, b, x);
    set(2 * v + 2, mid, r, a1, b, x);
    tree[v].data = min(tree[2 * v + 1].data, tree[2 * v + 2].data);
}

void add(int v, int l, int r, int a1, int b, int x)
{
    push(v, l, r);
    if (b <= l || a1 >= r)
    {
        return;
    }
    if (l >= a1 && r <= b)
    {
        tree[v].modify_add += x;
        tree[v].data += x;
        return;
    }
    int mid = l + (r - l) / 2;
    add(2 * v + 1, l, mid, a1, b, x);
    add(2 * v + 2, mid, r, a1, b, x);
    tree[v].data = min(tree[2 * v + 1].data, tree[2 * v + 2].data);
}

ll min(int v, int l, int r, int a1, int b)
{
    push(v, l, r);
    if (a1 <= l && r <= b)
    {
        return tree[v].data;
    }
    if (l >= b || r <= a1)
    {
        return LONG_LONG_MAX;
    }
    int mid = l + (r - l) / 2;
    return min(min(2 * v + 1, l, mid, a1, b), min(2 * v + 2, mid, r, a1, b));
}

int main()
{
    cin >> n;
    a.resize(n);
    tree.resize(4*n);
    for (size_t i = 0; i < n; i++)
    {
        cin >> a[i];
    }
    build();
    string op;
    while (cin >> op)
    {
        int l, r;
        cin >> l >> r;
        if (op == "min")
        {
            cout << min(0, 0, k, l - 1, r) << "\n";
        }
        else
        {
            int x;
            cin >> x;
            if (op == "set") {
                set(0, 0, k, l - 1, r, x);
            } else {
                add(0, 0, k, l - 1, r, x);
            }
        }
    }
    return 0;
}