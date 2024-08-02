#include <iostream>

typedef long long ll;

using namespace std;

const ll MAXN = 1000005;

struct Node
{
	ll value;
	ll key;
	int left = -1;
	int right = -1;
	long long sum = 0;

	Node() { key = rand(); }
	Node(ll a)
	{
		value = a;
		key = rand();
		sum = a;
	}
};

Node treap[MAXN];
ll idx = 0;
ll root = -1;

void calculate_sum(int i)
{
	if (i == -1)
		return;
	treap[i].sum = treap[i].value + ((treap[i].left != -1) ? treap[treap[i].left].sum : 0) +
				   ((treap[i].right != -1) ? treap[treap[i].right].sum : 0);
}

pair< int, int > split(ll x, int t)
{
	if (t == -1)
	{
		return { -1, -1 };
	}
	if (treap[t].value >= x)
	{
		pair< int, int > tmp = split(x, treap[t].left);
		treap[t].left = tmp.second;
		calculate_sum(t);
		return { tmp.first, t };
	}
	else
	{
		pair< int, int > tmp = split(x, treap[t].right);
		treap[t].right = tmp.first;
		calculate_sum(t);
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
		calculate_sum(t1);
		return t1;
	}
	else
	{
		treap[t2].left = merge(t1, treap[t2].left);
		calculate_sum(t2);
		return t2;
	}
}

bool find(int root, ll x)
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

void add(ll x)
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

ll get_sum(ll left, ll right)
{
	pair< int, int > tmp1 = split(right + 1, root);
	pair< int, int > tmp2 = split(left, tmp1.first);
	long long ans = treap[tmp2.second].sum;
	root = merge(merge(tmp2.first, tmp2.second), tmp1.second);
	return ans;
}

int main()
{
	int n;
	cin >> n;
	ll p = 0;
	for (int i = 0; i < n; i++)
	{
		char sign;
		cin >> sign;
		if (sign == '?')
		{
			ll y, z;
			cin >> y >> z;
			p = get_sum(y, z);
			cout << p << "\n";
		}
		if (sign == '+')
		{
			ll y;
			cin >> y;
			y += p;
			y %= ll(1e9);
			p = 0;
			if (find(root, y))
				continue;
			add(y);
			p = 0;
		}
	}
	return 0;
}