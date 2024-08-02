//
// Created by Виталий on 29.10.2023.
//
#include <algorithm>
#include <deque>
#include <iostream>
#include <vector>

using namespace std;

int main()
{
	int n;
	cin >> n;

	vector< vector< bool > > graph(n, vector< bool >(n, false));

	for (int i = 0; i < n; ++i)
	{
		for (int j = 0; j < i; ++j)
		{
			char x;
			cin >> x;
			if (x == '1')
			{
				graph[j][i] = graph[i][j] = true;
			}
		}
	}

	deque< int > deque;
	for (int i = 0; i < n; ++i)
	{
		deque.push_back(i);
	}

	for (int k = 0; k < n * (n - 1); ++k)
	{
		if (!graph[deque[0]][deque[1]])
		{
			int i = 2;
			while ((!graph[deque[0]][deque[i]] || !graph[deque[1]][i + 1]) && i < n - 1)
			{
				i++;
			}
			if (i == n - 1) {
				i = 2;
				while (i < n && !graph[deque[0]][deque[i]]) {
					i++;
				}
			}
			reverse(deque.begin() + 1, deque.begin() + i + 1);
		}
		deque.push_back(deque.front());
		deque.pop_front();
	}

	while (!deque.empty())
	{
		cout << deque.front() + 1 << ' ';
		deque.pop_front();
	}

	return EXIT_SUCCESS;
}