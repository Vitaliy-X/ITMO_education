#include <iostream>
#include <vector>
#include <stdlib.h>

using namespace std;

int maxValue(vector<int> dp, int pos, int k)
{
    int i = 1, maxValue = INT_MIN;
    while (pos - i >= 0 && i <= k)
    {
        maxValue = max(maxValue, dp[pos-i]);
    }
    return maxValue;
}

int main()
{
    int n, k, var;
    cin >> n >> k;
    vector<int> a(n-2, 0);
    for (int i = 0; i < n-2; i++)
    {
        cin >> a.at(i);
    }
    cout << a[0];

    vector<int> dp; 
    dp.push_back(0);
    for (int i = 1; i < 3; i++)
    {
        // int maxV = maxValue(dp, 3, k);
        dp.push_back(a[i] + maxValue(dp, i, k));
    }
    cout << dp[1];

    for (int i = 0; i < dp.size(); i++)
    {
        cout << dp[i] << endl;
    }
    return 0;
}