#include <vector>
#include <iostream>

using namespace std;

vector<int> a;
vector<int> fenwick(100001);

void update(int x, int v, int n) {
    while (x <= n) {
        fenwick[x] += v;
        x += x & -x;
    }
}

int get_sum(int x) {
    int result = 0;
    while (x > 0) {
        result += fenwick[x];
        x -= x & -x;
    }
    return result;
}

int get_sum(int left, int right) {
    return get_sum(right) - get_sum(left - 1);
}

int main()
{
    int n, m;
    int tmp;
    cin >> n;
    a.push_back(0);
    for (int i = 1; i <= n; i++)
    {
        cin >> tmp;
        a.push_back(tmp);
        if (i % 2 == 0)
        {
            a[i] = -a[i];
        }
        update(i, a[i], n);
    }
    cin >> m;
    int op, l, r;
    for (int i = 0; i < m; i++)
    {
        cin >> op >> l >> r;
        if (!op)
        {
            int v = r;
            if (l % 2 == 0)
            {
                v = -v;
            }
            update(l, v - a[l], n);
            a[l] = v;
        }
        else
        {
            int res = get_sum(l, r);
            if (l % 2 == 0)
            {
                res = -res;
            }
            cout << res << endl;
        }
    }
    return 0;
}
