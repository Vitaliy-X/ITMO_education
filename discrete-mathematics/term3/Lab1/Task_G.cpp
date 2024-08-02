//
// Created by Виталий on 07.11.2023.
//
#include <iostream>
#include <vector>
#include <set>
#include <stack>

using namespace std;

int main()
{
	ios_base::sync_with_stdio(false);

	int n, m;
	cin >> n >> m;

	vector< int > degree(n + 1, 0);
	vector< int > color(n + 1, -1);
	vector< vector<int> > adj(n + 1);

	for (int i = 0; i < m; i++)
	{
		int u, v;
		cin >> u >> v;
		adj[u].push_back(v);
		adj[v].push_back(u);
		degree[u]++;
		degree[v]++;
	}

	int k = 0;
	int start = 1;
	for (int i = 1; i <= n; i++)
	{
		if (k < degree[i])
		{
			k = degree[i];
			start = i;
		}
	}
	if (k % 2 == 0)
		k++;

	cout << k << endl;

	stack< int > stack;
	stack.push(start);

	while (!stack.empty())
	{
		int node = stack.top();
		stack.pop();

		if (color[node] != -1)
			continue;

		vector<bool> used_colors(k, false);
		for (int it : adj[node])
		{
			if (color[it] != -1)
			{
				used_colors[color[it]] = true;
			}
		}

		for (int c = 0; c < k; ++c)
		{
			if (!used_colors[c])
			{
				color[node] = c;
				break;
			}
		}

		for (int it : adj[node])
		{
			if (color[it] == -1)
			{
				stack.push(it);
			}
		}
	}

	for (int i = 1; i <= n; i++)
	{
		cout << color[i] + 1 << endl;
	}

	return EXIT_SUCCESS;
}