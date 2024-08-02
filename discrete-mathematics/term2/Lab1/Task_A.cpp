#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

typedef unsigned int uint;

struct Way
{
	char element;
	int purpose;

	Way(char symbol, int purpose)
	{
		this->element = symbol;
		this->purpose = purpose;
	}
};

vector< vector< Way > > ways;
vector< bool > final_positions;
int current_positions;

int create_way(char symbol)
{
	int tmp = current_positions;
	for (auto &i : ways[tmp])
	{
		if (i.element == symbol)
		{
			return i.purpose;
		}
	}
	return -1;
}

int checker(const string &word)
{
	current_positions = 0;
	for (char i : word)
	{
		current_positions = create_way(i);
		if (current_positions == -1)
			return false;
	}
	return final_positions[current_positions];
}

int main()
{
	ifstream in;
	in.open("problem1.in");
	string word;
	uint n, m, k;
	in >> word;
	in >> n >> m >> k;

	final_positions.resize(n);
	ways.resize(n);
	fill(final_positions.begin(), final_positions.end(), false);
	for (int i = 0; i < k; i++)
	{
		int d;
		in >> d;
		final_positions[d - 1] = true;
	}
	for (int i = 0; i < m; i++)
	{
		uint a, b;
		char c;
		in >> a >> b >> c;
		ways[a - 1].emplace_back(c, b - 1);
	}
	in.close();
	string result = !checker(word) ? "Rejects" : "Accepts";

	ofstream out;
	out.open("problem1.out");
	out << result;
	out.close();

	return 0;
}