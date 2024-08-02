#include <vector>
#include <iostream>

using namespace std;

vector<long long> a;
vector<long long> tree;

void build(long v, long L, long R)
{
    if (L == R-1)
    {
        tree[v] = a[L];
        return;
    }
    long M = L + (R - L) / 2;
    build(2 * v + 1, L, M);
    build(2 * v + 2, M, R);
    tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
}

long long get_sum(long v, long L, long R, long l, long r)
{
    if (r <= L || R <= l) return 0;
    if (l <= L && R <= r) return tree[v];
    long M = L + (R - L) / 2;
    return get_sum(2 * v + 1, L, M, l, r) + get_sum(2 * v + 2, M, R, l, r);
}

void set(long v, long L, long R, long i, long x)
{
    if (L == R - 1)
    {
        tree[v] = x;
        a[i] = x;
        return;
    }
    long M = (L + R) / 2;
    if (i < M) set(2 * v + 1, L, M, i, x);
    else set(2 * v + 2, M, R, i, x);
    tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
}


int main() {
    ios_base::sync_with_stdio(false);
    long n, x;
    cin >> n;
    for (int i = 0; i < n; i++) {
        cin >> x;
        a.push_back(x);
    }
    tree = vector<long long>(4*n);
    build(0, 0, n);

    string f;
    while (cin >> f) {
        long i, x;
        cin >> i >> x;
        if (f == "sum") {
            cout << get_sum(0, 0, n, i-1, x) << endl;
        } else {
            set(0, 0, n, i-1, x);
        }
    }
    return 0;
}