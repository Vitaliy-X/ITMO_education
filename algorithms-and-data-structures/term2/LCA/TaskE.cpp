#include <iostream>
#include <vector>
#include <algorithm>
#include <string>

using namespace std;

int log2(int n) {
	int logVal = 0;
	while (n >>= 1) {
		logVal++;
	}
	return logVal;
}

class Tree {
  public:
	explicit Tree(int maxSize) {
		maxSize += 10;
		root = 1;
		curNumber = 1;
		live.resize(maxSize);
		parent.resize(maxSize);
		depth.resize(maxSize);
		int logSize = log2(maxSize) + 1;
		dp.resize(maxSize, vector<int>(logSize));

		live[1] = true;
		parent[1] = 0;
		depth[1] = 0;
		log = logSize;
	}

	int lca(int v, int u) {
		if (depth[v] > depth[u]) {
			swap(v, u);
		}

		int h = depth[u] - depth[v];
		for (int i = log - 1; i >= 0; i--) {
			if ((1 << i) <= h) {
				h -= (1 << i);
				u = dp[u][i];
			}
		}

		if (u == v) {
			return v;
		}

		for (int i = log - 1; i >= 0; i--) {
			if (dp[v][i] != dp[u][i]) {
				v = dp[v][i];
				u = dp[u][i];
			}
		}

		return parent[v];
	}

	int LCA(int v, int u) {
		int lc = lca(v, u);
		return findParent(lc);
	}

	void add(int p) {
		curNumber++;
		depth[curNumber] = depth[p] + 1;
		live[curNumber] = true;
		parent[curNumber] = p;
		dp[curNumber][0] = p;
		for (int i = 1; i <= log2(curNumber); i++) {
			dp[curNumber][i] = dp[dp[curNumber][i - 1]][i - 1];
		}
	}

	void kill(int v) { live[v] = false;
	}

	int findParent(int node) {
		if (live[node]) {
			return node;
		}

		return parent[node] = findParent(parent[node]);
	}

  private:
	vector<bool> live;
	vector<int> parent;
	vector<int> depth;
	vector<vector<int>> dp;
	int root;
	int curNumber;
	int log;
};

int main() {
	int n;
	cin >> n;
	Tree tree(n);

	for (int i = 0; i < n; i++) {
		string query;
		cin >> query;

		if (query == "+") {
			int v;
			cin >> v;
			tree.add(v);
		} else if (query == "-") {
			int v;
			cin >> v;
			tree.kill(v);
		} else if (query == "?") {
			int v, u;
			cin >> v >> u;
			cout << tree.LCA(v, u) << '\n';
		}
	}

	return 0;
}
