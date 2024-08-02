//
// Created by Виталий on 27.05.2023.
//
#include <set>
#include <vector>
#include <cstdio>
#include <string>

using namespace std;

constexpr int MAX = 1000005;
int color[MAX], res[MAX];
vector<vector<int>> g;
set<int> s[MAX];

void dfs(int v) {
	s[v].insert(color[v]);

	for (int to : g[v]) {
		dfs(to);

		if (s[v].size() < s[to].size()) {
			s[v].swap(s[to]);
		}

		s[v].insert(s[to].begin(), s[to].end());
		s[to].clear();
	}

	res[v] = s[v].size();
}

int main() {
	int n, p, c;
	scanf("%d", &n);
	g.resize(n + 1);

	for (int i = 1; i <= n; i++) {
		scanf("%d %d", &p, &c);
		g[p].push_back(i);
		color[i] = c;
	}

	dfs(0);

	string output;
	for (int i = 1; i <= n; i++) {
		output += to_string(res[i]) + " ";
	}

	printf("%s\n", output.c_str());
}