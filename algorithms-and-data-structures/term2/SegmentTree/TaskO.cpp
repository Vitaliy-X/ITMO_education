#include <iostream>

using namespace std;

//#define int long long
#define MAXN 129

int n;

int t[MAXN][MAXN][MAXN];

void upd(int x, int y, int z, int val)
{
    for (int i = x; i < n; i = (i | (i + 1)))
    {
        for (int j = y; j < n; j = (j | (j + 1)))
        {
            for (int k = z; k < n; k = (k | (k + 1)))
            {
                t[i][j][k] += val;
            }
        }
    }
}

int get(int x, int y, int z)
{
    int res = 0;
    for (int i = x; i >= 0; i = (i & (i + 1)) - 1)
    {
        for (int j = y; j >= 0; j = (j & (j + 1)) - 1)
        {
            for (int k = z; k >= 0; k = (k & (k + 1)) - 1)
            {
                res += t[i][j][k];
            }
        }
    }
    return res;
}

signed main()
{
    ios_base::sync_with_stdio(0);
    cin.tie(0);
    cout.tie(0);
    cin >> n;
    while (true) {
        int q;
        cin >> q;
        if (q == 3) {
            return 0;
        }
        if (q == 1) {
            int x, y, z, k;
            cin >> x >> y >> z >> k;
            upd(x, y, z, k);
        } else {
            int x1, y1, z1, x2, y2, z2;
            cin >> x1 >> y1 >> z1 >> x2 >> y2 >> z2;
            int ans = get(x2, y2, z2)
                      - get(x2, y2, z1 - 1)
                      - get(x2, y1 - 1, z2)
                      - get(x1 - 1, y2, z2)
                      + get(x1 - 1, y1 - 1, z2)
                      + get(x1 - 1, y2, z1 - 1)
                      + get(x2, y1 - 1, z1 - 1)
                      - get(x1 - 1, y1 - 1, z1 - 1);
            cout << ans << "\n";
        }
    }
    return 0;
}
