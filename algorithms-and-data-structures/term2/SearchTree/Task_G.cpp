#include <iostream>

using namespace std;

const int MAXN = 100005;

struct Node
{
	int value;
	int key;
	int left = -1;
	int right = -1;
	int subtree_size = 1;

	Node() { key = rand(); }
	Node(int a)
	{
		value = a;
		key = rand();
	}
};

Node treap[MAXN];
int idx = 0;
int root = 0;

void calculate_size(int i)
{
	int left = (treap[i].left != -1) ? treap[treap[i].left].subtree_size : 0;
	int right = (treap[i].right != -1) ? treap[treap[i].right].subtree_size : 0;
	treap[i].subtree_size = left + right + 1;
}

pair< int, int > split(int x, int t)
{
	if (t == -1)
	{
		return { -1, -1 };
	}
	int subtree_size = treap[t].left == -1 ? 0 : treap[treap[t].left].subtree_size;
	if (subtree_size < x)
	{
		pair< int, int > tmp = split(x - subtree_size - 1, treap[t].right);
		treap[t].right = tmp.first;
		calculate_size(t);
		return { t, tmp.second };
	}
	else
	{
		pair< int, int > tmp = split(x, treap[t].left);
		treap[t].left = tmp.second;
		calculate_size(t);
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
		calculate_size(t1);
		return t1;
	}
	else
	{
		treap[t2].left = merge(t1, treap[t2].left);
		calculate_size(t2);
		return t2;
	}
}

bool find(int root, int x)
{
	while (root != -1)
	{
		if (treap[root].value == x)
		{
			return true;
		}
		root = treap[root].value < x ? treap[root].right : treap[root].left;
	}
	return false;
}

void add(int x)
{
	if (find(root, x))
		return;
	if (idx == 0)
	{
		root = idx;
		treap[idx++] = Node(x);
	}
	else
	{
		pair< int, int > t = split(x, root);
		int node = idx;
		treap[idx++] = Node(x);
		root = merge(merge(t.first, node), t.second);
	}
}

void print(int t)
{
	if (t == -1)
		return;
	print(treap[t].left);
	cout << treap[t].value << ' ';
	print(treap[t].right);
}

void shift(int left, int right)
{
	pair< int, int > tmp1 = split(right + 1, root);
	pair< int, int > tmp2 = split(left, tmp1.first);
	root = merge(tmp2.second, merge(tmp2.first, tmp1.second));
}

int main()
{
	int n, m;
	cin >> n >> m;
	for (int i = 0; i < n; i++)
	{
		add(i + 1);
	}
	for (int i = 0; i < m; i++)
	{
		int l, r;
		cin >> l >> r;
		shift(--l, --r);
	}
	print(root);
	return 0;
}