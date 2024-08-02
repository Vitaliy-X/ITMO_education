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
int root = -1;
int cnt = 0;

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
	if (treap[t].value >= x)
	{
		pair< int, int > tmp = split(x, treap[t].left);
		treap[t].left = tmp.second;
		calculate_size(t);
		return { tmp.first, t };
	}
	else
	{
		pair< int, int > tmp = split(x, treap[t].right);
		treap[t].right = tmp.first;
		calculate_size(t);
		return { t, tmp.second };
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

void remove(int x)
{
	if (!find(root, x))
		return;
	pair< int, int > tmp1 = split(x, root), tmp2 = split(x + 1, tmp1.second);
	root = merge(tmp1.first, tmp2.second);
}

void exists(int value)
{
	cout << (find(root, value) ? "true" : "false") << endl;
}

int next(int x)
{
	pair< int, int > tmp = split(x + 1, root);
	int t = tmp.second;
	int num = INT_MIN;
	while (t != -1)
	{
		num = treap[t].value;
		t = treap[t].left;
	}
	root = merge(tmp.first, tmp.second);
	return num;
}

int prev(int x)
{
	pair< int, int > tmp = split(x, root);
	int t = tmp.first;
	int num = INT_MIN;
	while (t != -1)
	{
		num = treap[t].value;
		t = treap[t].right;
	}
	root = merge(tmp.first, tmp.second);
	return num;
}

int k_element(int t, int k)
{
	while (t != -1)
	{
		int left = (treap[t].left != -1) ? treap[treap[t].left].subtree_size : 0;
		if (left + 1 == k)
		{
			return treap[t].value;
		}
		else if (left + 1 > k)
		{
			t = treap[t].left;
		}
		else
		{
			k -= left + 1;
			t = treap[t].right;
		}
	}
	return -1;
}

int main()
{
	int n;
	cin >> n;
	for (int i = 0; i < n; i++)
	{
		int x, y;
		cin >> x >> y;
		if (x == 0)
		{
			cout << k_element(root, cnt - y + 1) << "\n";
		}
		else if (x == 1)
		{
			add(y);
			cnt++;
		}
		else
		{
			remove(y);
			cnt--;
		}
	}
	return 0;
}