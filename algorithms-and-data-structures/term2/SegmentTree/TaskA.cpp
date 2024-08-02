#include <vector>
#include <iostream>
#include <cmath>

using namespace std;

vector<unsigned int> a;
vector<long long> pref;
void build(int size)
{
    pref.push_back(a[0]);
    for (int i = 1; i < size; ++i) {
        pref.push_back(a[i] + pref[i-1]);
    }
}

long long get_sum(int l, int r)
{
    if (l < 1) return pref[r];
    return pref[r] - pref[l-1];
}


int main() {
    int n, x, y, a0;
    cin >> n >> x >> y >> a0;
    a = vector<unsigned int>(n);
    a[0] = a0;
    int mod = (int)pow(2, 16);
    for (int i = 1; i < n; ++i) {
        a[i] = ((x * a[i-1] + y) % mod);
    }

    build(n);

    int m, z, t, b0;
    cin >> m >> z >> t >> b0;
    mod = (int)pow(2, 30);

    vector<unsigned int> c(2*m);
    unsigned int last = b0;
    long long sum = 0;
    for (int i = 0; i < 2*m; ++i) {
        c[i] = (last % n);
        last = ((z * last + t) % mod);
    }
    for (int i = 0; i < m; ++i) {
        sum += get_sum(min(c[2*i], c[2*i+1]), max(c[2*i], c[2*i+1]));
    }

    cout << sum;
    return 0;
}