//
// Created by Виталий on 29.10.2023.
//
#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

void readGraph(vector< vector< bool > >& graph, int numNodes)
{
	for (int i = 1; i < numNodes; ++i)
	{
		for (int j = 0; j < i; ++j)
		{
			char edge;
			cin >> edge;
			if (edge == '1')
			{
				graph[i][j] = true;
			}
			else
			{
				graph[j][i] = true;
			}
		}
	}
}

void processGraph(vector< int >& path, vector< vector< bool > >& graph, int numNodes)
{
	for (int i = 1; i < numNodes; ++i)
	{
		auto it = path.begin();
		while (it != path.end() && graph[*it][i])
		{
			++it;
		}
		path.insert(it, i);
	}
}

void printResult(const vector< int >& result)
{
	for (const int & node : result)
	{
		cout << node + 1 << ' ';
	}
}

int main()
{
	ios::sync_with_stdio(false);

	int numNodes;
	cin >> numNodes;

	vector< vector< bool > > graph(numNodes, vector< bool >(numNodes, false));
	readGraph(graph, numNodes);

	vector< int > path(1, 0);
	processGraph(path, graph, numNodes);

	int start = path[0], finish = 0;
	for (int i = path.size() - 1; i >= 2; --i)
	{
		if (graph[path[i]][start])
		{
			finish = i;
			break;
		}
	}

	vector< int > result(path.begin(), path.begin() + finish + 1);
	path.erase(path.begin(), path.begin() + finish + 1);

	auto startIterator = path.begin();
	while (startIterator != path.end())
	{
		auto point = result.begin();
		while (point != result.end() && graph[*point][*startIterator])
		{
			point++;
		}

		if (point == result.end())
		{
			startIterator++;
		}
		else
		{
			result.insert(point, path.begin(), startIterator + 1);
			path.erase(path.begin(), startIterator + 1);
			startIterator = path.begin();
		}
	}

	printResult(result);

	return EXIT_SUCCESS;
}