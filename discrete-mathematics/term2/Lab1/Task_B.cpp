#include <fstream>
#include <iostream>
#include <queue>
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
vector< int > last_position;
queue< pair< int, int > > current_position;

void create_way(char symbol)
{
	int tmp = current_position.front().second;
	for (; !current_position.empty() && current_position.front().second == tmp; current_position.pop())
	{
		int j = current_position.front().first;
		for (auto& i : ways[j])
		{
			if ((i.element == symbol) && (last_position[i.purpose] != tmp + 1))
			{
				current_position.emplace(i.purpose, tmp + 1);
				last_position[i.purpose] = tmp + 1;
			}
		}
	}
}

int checker(const string& word)
{
	current_position.emplace(0, -1);
	for (char el : word)
	{
		create_way(el);
		if (current_position.empty())
			return 0;
	}
	int result = 0;
	while (!(current_position.empty()))
	{
		result |= final_positions[current_position.front().first];
		current_position.pop();
	}
	return result;
}

int main()
{
	ifstream in;
	in.open("problem2.in");

	string word;
	uint n, m, k;
	in >> word;
	in >> n >> m >> k;

	last_position.resize(n);
	final_positions.resize(n);
	ways.resize(n);
	fill(last_position.begin(), last_position.end(), -1);
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
	out.open("problem2.out");
	out << result;
	out.close();

	return 0;
}