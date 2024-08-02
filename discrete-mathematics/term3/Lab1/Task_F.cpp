//
// Created by Виталий on 29.10.2023.
//
#include <iostream>
#include <vector>

using namespace std;

int main() {
	ios::sync_with_stdio(false);

	int totalNodes;
	cin >> totalNodes;

	vector<int> pruferCode(totalNodes - 2);
	for (int i = 0; i < totalNodes - 2; ++i) {
		cin >> pruferCode[i];
		pruferCode[i]--;
	}

	vector<int> degrees(totalNodes, 1);
	for (int i = 0; i < totalNodes - 2; ++i) {
		++degrees[pruferCode[i]];
	}

	int pointer = 0;
	while (pointer < totalNodes && degrees[pointer] != 1) {
		++pointer;
	}
	int leaf = pointer;

	for (int i = 0; i < totalNodes - 2; ++i) {
		int vertex = pruferCode[i];
		cout << leaf + 1 << ' ' << vertex + 1 << endl;

		--degrees[leaf];
		if (--degrees[vertex] != 1 || vertex >= pointer)
		{
			++pointer;
			while (pointer < totalNodes && degrees[pointer] != 1)
			{
				++pointer;
			}
			leaf = pointer;
		}
		else
		{
			leaf = vertex;
		}
	}

	for (int vertex = 0; vertex < totalNodes - 1; ++vertex) {
		if (degrees[vertex] == 1) {
			cout << vertex + 1 << ' ' << totalNodes << endl;
		}
	}

	return 0;
}