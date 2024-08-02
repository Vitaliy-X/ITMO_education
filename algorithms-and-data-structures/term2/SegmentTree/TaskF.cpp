#include <iostream>
#include <vector>
#include <cmath>

using namespace std;

vector<int> a;
int u[2], v[2], r[2];
int sparse_table[200000][20];

void foo(int n, int i);
int query(int u, int v);
void generate_line(int n, int l);

signed main() {
    int n, m;
    cin >> n >> m;
    a = vector<int>(n+1);
    cin >> a[0];
    cin >> u[0] >> v[0];
    for (int i = 1; i < n; ++i) {
        a[i] = (23 * a[i - 1] + 21563) % 16714589;
    }
    for (int i = 0; i <= log2(n); i++) {
        generate_line(n, i);
    }
    for (int i = 1; i < m; ++i) {
        foo(n, i);
    }

    cout << u[(m - 1) % 2] << " " << v[(m - 1) % 2] << " " << query(u[(m - 1) % 2] - 1, v[(m - 1) % 2] - 1);
    return 0;
}

void generate_line(int n, int l) {
    for (int i = 0; i < n - l; i++) {
        if (l > 0) {
            sparse_table[i][l] = min(sparse_table[i][l - 1], sparse_table[i + (1 << (l - 1))][l - 1]);
        } else {
            sparse_table[i][l] = a[i];
        }
    }
}

void foo(int n, int i) {
    r[(i - 1) % 2] = query(u[(i - 1) % 2] - 1, v[(i - 1) % 2] - 1);
    u[i % 2] = (17 * u[(i - 1) % 2] + 751 + r[(i - 1) % 2] + 2 * (i)) % n + 1;
    v[i % 2] = (13 * v[(i - 1) % 2] + 593 + r[(i - 1) % 2] + 5 * (i)) % n + 1;
}

int query(int u, int v) {
    if (u > v)
    {
        swap(u, v);
    }
    int length = (log2(1 + v - u));
    return min(sparse_table[u][length], sparse_table[v - (1 << length) + 1][length]);
}