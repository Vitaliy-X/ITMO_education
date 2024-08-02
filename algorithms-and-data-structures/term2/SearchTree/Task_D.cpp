#include <iostream>

using namespace std;

const int MAXN = 500005;

struct Node {
	int value;
	int key;
	int subtree_size = 1;
	int left = -1;
	int right = -1;
	bool empty = true;
	bool isEmpty = true;
	Node() {}
	Node(int a) {
		value = a;
		key = rand();
	}
};

Node treap[MAXN];
int idx = 0, root = -1, cnt = 0;
int last = 0;

int get_sz(int v) {
	return v == -1 ? 0 : treap[v].subtree_size;
}

bool get_free(int v) {
	return v != -1 && treap[v].empty;
}

void calculate(int v) {
	if (v == -1) return;
	treap[v].subtree_size = get_sz(treap[v].left) + get_sz(treap[v].right) + 1;
	treap[v].empty = get_free(treap[v].left) || get_free(treap[v].right) || treap[v].isEmpty;
}

pair<int, int> split(int x, int t) {
	if (t == -1) { return {-1, -1}; }
	int sz = get_sz(treap[t].left);
	if (sz < x)
	{
		pair< int, int > tmp = split(x - sz - 1, treap[t].right);
		treap[t].right = tmp.first;
		calculate(t);
		calculate(tmp.second);
		return { t, tmp.second };
	}
	else
	{
		pair< int, int > tmp = split(x, treap[t].left);
		treap[t].left = tmp.second;
		calculate(t);
		calculate(tmp.first);
		return { tmp.first, t };
	}
}

int merge(int t1, int t2)
{
	if (t1 == -1)
	{
		return t2;
	}
	if (t2 == -1)
	{
		return t1;
	}
	if (treap[t1].key <= treap[t2].key)
	{
		treap[t1].right = merge(treap[t1].right, t2);
		calculate(t1);
		return t1;
	}
	else
	{
		treap[t2].left = merge(t1, treap[t2].left);
		calculate(t2);
		return t2;
	}
}

void add(int x) {
	if (idx != 0)
	{
		int node = idx;
		treap[idx++] = Node(x);
		root = merge(root, node);
	}
	else
	{
		root = idx;
		treap[idx++] = Node(x);
	}
}

void print_tree(int t) {
	if (t == -1 || cnt <= 0)
		return;
	print_tree(treap[t].left);
	if (cnt-- > 0) cout << treap[t].value << ' ';
	print_tree(treap[t].right);
}

void insert(int position, int value) {
	pair <int, int> tmp = split(position - 1, root);
	int second = tmp.second;
	int subtree_size = 0;
	while (1) {
		if (get_free(treap[second].left)) {
			second = treap[second].left;
		} else if (treap[second].isEmpty) {
			subtree_size += get_sz(treap[second].left);
			break;
		} else if (get_free(treap[second].right)) {
			subtree_size += get_sz(treap[second].left) + 1;
			second = treap[second].right;
		}
	}
	pair <int, int> tmp2 = split(subtree_size, tmp.second);
	int current_subtree_size = position - 1 + subtree_size + 1;
	last = max(last, current_subtree_size);
	pair <int, int> t2 = split(1, tmp2.second);
	int first = t2.first;
	if (first < MAXN) {
		treap[first].value = value;
		treap[first].isEmpty = false;
		treap[first].empty = false;
	}
	calculate(t2.second);
	calculate(tmp2.first);
	calculate(t2.first);
	calculate(tmp.first);
	root = merge(tmp.first, merge(t2.first, merge(tmp2.first, t2.second)));
}

int main() {
	int n, m;
	cin >> n >> m;
	for (int i = 0; i < n + m + 200; i++) {
		add(0);
	}
	last = n;
	for (int i = 0; i < n; i++) {
		int left;
		cin >> left;
		insert(left, i + 1);
	}
	cnt = last;
	cout << cnt << "\n";
	print_tree(root);
	return 0;
}
