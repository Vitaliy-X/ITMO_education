#include <fstream>
#include <iostream>
#include <map>
#include <set>

using namespace std;

map< char, set< string > > grammar;

bool canGenerateString(string s, char state, int position)
{
	if (position > s.size() - 1)
	{
		return false;
	}
	for (auto transition : grammar[state])
	{
		if (s[position] == transition[0] &&
			(transition.size() == 1 && s.size() - 1 == position
			 || canGenerateString(s, transition[1], position + 1)))
		{
			return true;
		}
	}
	return false;
}

void read(ifstream &inputFile, char &startSymbol)
{
	int numStates;
	inputFile >> numStates >> startSymbol;

	for (int i = 0; i < numStates; i++)
	{
		string tmp, transitionString;
		set< string > transitions;
		char nonTerminal;
		inputFile >> nonTerminal >> tmp >> transitionString;
		transitions.emplace(transitionString);
		if (auto hasTransitions = grammar.find(nonTerminal); hasTransitions != grammar.end())
		{
			hasTransitions->second.emplace(transitionString);
		}
		else
		{
			grammar.emplace(nonTerminal, transitions);
		}
	}
}

int main()
{
	ifstream inputFile("automaton.in");

	char startSymbol;
	read(inputFile, startSymbol);

	int numStrings;
	inputFile >> numStrings;
	ofstream outputFile("automaton.out");

	for (int i = 0; i < numStrings; i++)
	{
		string str;
		inputFile >> str;
		outputFile << (canGenerateString(str, startSymbol, 0) ? "yes" : "no") << "\n";
	}
	inputFile.close();
	outputFile.close();

	return 0;
}