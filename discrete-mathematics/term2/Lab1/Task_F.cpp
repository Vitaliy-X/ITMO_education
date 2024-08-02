#include <iostream>
#include <map>
#include <vector>

using namespace std;

using ll = long long;
using uint = unsigned int;

struct State
{
	bool flagFinal, flagDevilish;
	map<char, int> ways;

	State()
	{
		this->flagFinal = false;
		this->flagDevilish = false;
	}

	void addTransition(int way, char sym) { ways.insert({sym, way}); }
};

struct DFA
{
	vector<State> states;

	DFA(int size)
	{
		states.reserve(size);
		states.resize(size);
	}

	int size() { return states.size(); }

	void addTransition(int source, int destination, char symbol)
	{
		states[source].addTransition(destination, symbol);
	}

	void setFinal(int index) { states[index].flagFinal = true; }

	void countDevilish()
	{
		int i = 0;
		while (i < states.size())
		{
			bool tmp = false;
			auto it = states[i].ways.begin();
			while (it != states[i].ways.end() && !tmp)
			{
				if (it->second != i)
					tmp = true;
				it++;
			}
			if (!tmp)
				states[i].flagDevilish = true;
			i++;
		}
	}

	bool isIsomorphic(DFA &d2)
	{
		vector<bool> history(size());
		fill(history.begin(), history.end(), false);
		return checker(history, d2, 0, 0);
	}

	bool checker(vector<bool> &history, DFA &d2, int v1, int v2)
	{
		history[v2] = true;
		if (states[v1].flagFinal != d2.states[v2].flagFinal || states[v1].flagDevilish != d2.states[v2].flagDevilish)
			return false;
		bool result = true;
		for (const auto &j : states[v1].ways)
		{
			auto it = d2.states[v2].ways.find(j.first);
			if (it == d2.states[v2].ways.end())
				return false;
			int u_child = j.second;
			int v_child = it->second;
			if (!history[u_child])
			{
				result &= checker(history, d2, u_child, v_child);
			}
		}
		return result;
	}
};

void build(vector<DFA> &d)
{
	int n, m, k, a, b;
	char symbol;
	d.reserve(2);
	for (int j = 0; j < 2; j++)
	{
		cin >> n >> m >> k;
		d.emplace_back(n);
		for (int i = 0; i < k; i++)
		{
			cin >> a;
			d[j].setFinal(a - 1);
		}
		for (int i = 0; i < m; i++)
		{
			cin >> a >> b >> symbol;
			d[j].addTransition(a - 1, b - 1, symbol);
		}
		d[j].countDevilish();
	}
}

int main()
{
	freopen("isomorphism.in", "r", stdin);
	freopen("isomorphism.out", "w", stdout);

	vector< DFA > d;
	build(d);

	string message = (d[0].isIsomorphic(d[1])) ? "YES" : "NO";
	cout << message;
	return 0;
}