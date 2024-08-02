//
// Created by Виталий on 29.10.2023.
//
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

typedef vector< vector< bool > > graph;

int numVertices;

vector< vector< bool > > removeEdge(const vector< vector< bool > > &g, int u, int v)
{
	vector< vector< bool > > res(g);
	res[u][v] = res[v][u] = false;
	return res;
}

vector< vector< bool > > contractEdge(const vector< vector< bool > > &g, int u, int v)
{
	int n = (int)g.size();
	if (u > v)
		swap(u, v);

	vector< vector< bool > > res(n - 1, vector< bool >(n - 1));
	for (int i = 0; i < n; ++i)
	{
		if (i == v)
			continue;

		int i_res = (i < v ? i : i - 1);

		copy(g[i].begin(), g[i].begin() + v, res[i_res].begin());
		copy(g[i].begin() + v + 1, g[i].end(), res[i_res].begin() + v);
	}

	for (int i = 0; i < n; ++i)
	{
		if (i == v)
			continue;
		int i_res = (i < v ? i : i - 1);
		res[u][i_res] = res[u][i_res] || g[v][i];
	}

	for (int i = 0; i < n; ++i)
	{
		if (i == v)
			continue;

		int i_res = (i < v ? i : i - 1);
		res[i_res][u] = res[i_res][u] || g[i][v];
	}

	res[u][u] = false;

	return res;
}

bool isEdgeless(const vector< vector< bool > > &g)
{
	int n = (int)g.size();
	for (int i = 0; i < n; ++i)
	{
		for (int j = 0; j < n; ++j)
		{
			if (g[i][j])
			{
				return false;
			}
		}
	}
	return true;
}

vector< int > subtractPolynomials(const vector< int > &a, const vector< int > &b)
{
	vector< int > res(numVertices);
	for (int i = 0; i < numVertices; ++i)
	{
		res[i] = a[i] - b[i];
	}
	return res;
}

vector< int > chromaticPolynomial(const vector< vector< bool > > &g)
{
	vector< int > cp(numVertices, 0);
	if (isEdgeless(g))
	{
		int deg = g.size();
		cp[deg]++;
		return cp;
	}

	pair< int, int > uv;
	int n = (int)g.size();
	for (int i = 1; i < n; ++i)
	{
		for (int j = 0; j < i; ++j)
		{
			if (g[i][j])
			{
				uv = { i, j };
			}
		}
	}
	int u = uv.first, v = uv.second;

	return subtractPolynomials(chromaticPolynomial(removeEdge(g, u, v)), chromaticPolynomial(contractEdge(g, u, v)));
}

int main()
{
	ios::sync_with_stdio(false);

	int n, m;
	cin >> n >> m;
	numVertices = n + 1;

	graph g(n, vector< bool >(n, false));
	vector< int > c(n, -1);

	for (int i = 0; i < m; ++i)
	{
		int a, b;
		cin >> a >> b;
		a--;
		b--;
		g[a][b] = g[b][a] = true;
	}

	vector< int > cp = std::move(chromaticPolynomial(g));

	reverse(cp.begin(), cp.end());
	cout << cp.size() - 1 << endl;
	for (int i : cp)
		cout << i << ' ';

	return EXIT_SUCCESS;
}
/*
4 5
1 2
1 3
2 3
2 4
3 4
*/