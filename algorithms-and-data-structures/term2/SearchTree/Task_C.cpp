#include <algorithm>
#include <deque>
#include <iostream>
#include <vector>

using namespace std;

const int MAXN = 1000005;

struct Node
{
	int value;
	int key;
	int left = -1;
	int right = -1;
	int id = -1;

	Node() { key = rand(); }

	Node(int a)
	{
		value = a;
		key = rand();
	}

	Node(int a, int b, int i)
	{
		value = a;
		key = b;
		id = i;
	}
};

Node treap[MAXN];
int idx = 0;
int root = -1;

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
		return { tmp.first, t };
	}
	else
	{
		pair< int, int > tmp = split(x, treap[t].right);
		treap[t].right = tmp.first;
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
		return t1;
	}
	else
	{
		treap[t2].left = merge(t1, treap[t2].left);
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

deque< int > queue;
vector< pair< int, pair< int, int > > > vertex_data;
vector< int > parent;
vector< pair< int, int > > parent_child;

void print(int v, int from)
{
	if (v == -1)
	{
		return;
	}
	if (treap[v].left != -1)
	{
		parent_child[treap[v].id].first = treap[treap[v].left].id;
	}
	if (from != -1)
		parent[treap[v].id] = treap[from].id;
	if (treap[v].right != -1)
	{
		parent_child[treap[v].id].second = treap[treap[v].right].id;
	}
	print(treap[v].left, v);
	print(treap[v].right, v);
}

int main()
{
	int n;
	cin >> n;
	vertex_data.resize(n);
	parent.resize(n, -1);
	parent_child.resize(n, { -1, -1 });
	for (int i = 0; i < n; i++)
	{
		int a, b;
		cin >> a >> b;
		vertex_data[i] = { a, { b, i } };
	}
	sort(vertex_data.begin(), vertex_data.end());
	for (int i = 0; i < n; i++)
	{
		queue.push_back(idx);
		treap[idx++] = Node(vertex_data[i].first, vertex_data[i].second.first, vertex_data[i].second.second);
	}
	while (queue.size() > 1)
	{
		root = queue.front();
		deque< int > q;
		while (!queue.empty())
		{
			int i1 = queue.front();
			queue.pop_front();
			if (queue.empty())
			{
				q.push_back(i1);
				break;
			}
			int i2 = queue.front();
			queue.pop_front();
			q.push_back(merge(i1, i2));
		}
		queue.swap(q);
	}
	root = queue.front();
	queue.pop_front();
	print(root, -1);
	cout << "YES\n";
	for (int i = 0; i < n; i++)
	{
		cout << parent[i] + 1 << ' ' << parent_child[i].first + 1 << ' ' << parent_child[i].second + 1 << "\n";
	}
	return 0;
}