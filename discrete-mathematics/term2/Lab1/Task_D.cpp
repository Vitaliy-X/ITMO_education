#include <fstream>
#include <iostream>
#include <map>
#include <set>
#include <vector>

using namespace std;

using ll = long long;
using uint = unsigned int;

int main()
{
	ifstream in("problem4.in");
	uint n, m, k, l;
	in >> n >> m >> k >> l;

	vector< bool > flags(n, false);
	for (int i = 0; i < k; ++i)
	{
		int tmp;
		in >> tmp;
		flags[tmp - 1] = true;
	}

	vector< vector< set< int > > > abc(n, vector< set< int > >(26));
	for (int i = 0; i < m; ++i)
	{
		int start, finish;
		char c;
		in >> start >> finish >> c;
		abc[start - 1][c - 'a'].insert(finish - 1);
	}
	in.close();

	auto* w = new vector< map< int, int > >(n);
	for (int i = 0; i < n; i++)
	{
		(*w)[i] = map< int, int >();
		for (int c = 0; c < 26; c++)
		{
			for (int el : abc[i][c])
			{
				(*w)[i][el]++;
			}
		}
	}

	vector< vector< ll > > dp(l + 1, vector< ll >(n, 0));
	dp[0][0] = 1;
	for (int i = 0; i < l; ++i)
	{
		for (int j = 0; j < n; ++j)
		{
			for (auto& item : (*w)[j])
			{
				dp[i + 1][item.first] += dp[i][j] * item.second;
				dp[i + 1][item.first] %= (ll)(1e9 + 7);
			}
		}
	}

	ll result = 0;
	for (int i = 0; i < n; ++i)
	{
		if (flags[i])
		{
			result += dp[l][i];
			result %= (ll)(1e9 + 7);
		}
	}

	ofstream out("problem4.out");
	out << result;
	out.close();

	delete w;

	return 0;
}