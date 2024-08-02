//
// Created by Виталий on 28.10.2023.
//
#include <iostream>
#include <set>
#include <vector>

using namespace std;

int main()
{
	int n;
	cin >> n;

	vector< set< int > > graph(n);
	vector< int > degrees(n, 0);

	for (int i = 0; i < n - 1; ++i)
	{
		int a, b;
		cin >> a >> b;
		a--;
		b--;
		graph[a].insert(b);
		graph[b].insert(a);
		degrees[a]++;
		degrees[b]++;
	}

	set< int > leafNodes;
	for (int i = 0; i < n; ++i)
	{
		if (degrees[i] == 1)
		{
			leafNodes.insert(i);
		}
	}

	int processedNodes = 0;
	while (processedNodes < n - 2)
	{
		int leafNode = *leafNodes.begin();
		leafNodes.erase(leafNodes.begin());

		int adjacentNode = *graph[leafNode].begin();
		graph[adjacentNode].erase(leafNode);
		degrees[adjacentNode]--;

		cout << adjacentNode + 1 << " ";

		if (degrees[adjacentNode] == 1)
		{
			leafNodes.insert(adjacentNode);
		}

		processedNodes++;
	}

	return EXIT_SUCCESS;
}
