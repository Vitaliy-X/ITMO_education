#include <fstream>
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

const int MODULE = 1'000'000'007;

struct Rule
{
	char a;
	char b;
	bool isTerminate;

	Rule(char a, char b, bool isTerminate) : a(a), b(b), isTerminate(isTerminate) {}
};

class NFC
{
  private:
	int n{};
	int start;
	vector< vector< Rule > > rules;
	vector< vector< vector< int > > > dp;
	string str;

  public:
	explicit NFC(const string& inputFile)
	{
		ifstream in(inputFile);
		char startChar;
		in >> n >> startChar;
		start = startChar - 65;
		rules.resize(26);
		dp.resize(26, vector< vector< int > >(500, vector< int >(500, -1)));
		for (int i = 0; i < n; i++)
		{
			char nt;
			in >> nt;
			string s;
			getline(in >> ws, s);
			s = s.substr(3);
			s.erase(remove_if(s.begin(), s.end(), ::isspace), s.end());
			char rhsA = s[0];
			char rhsB = (s.length() >= 2) ? s[1] : 97;
			bool isTerm = (s.length() == 1);
			Rule rhs(rhsA, rhsB, isTerm);
			rules[nt - 'A'].push_back(rhs);
		}

		in >> str;
		in.close();
	}

	int dpCalc(int now, int left, int right)
	{
		if (dp[now][left][right] == -1)
		{
			int answer = 0;
			for (auto cur : rules[now])
			{
				if (!cur.isTerminate)
				{
					for (int j = left + 1; j < right; j++)
					{
						answer = (((static_cast< long long >(dpCalc(cur.a - 'A', left, j)) + 0LL) * dpCalc(cur.b - 'A', j, right) + answer) % MODULE);
					}
				}
				else
				{
					if ((left + 1 == right) && (str[left] == cur.a))
					{
						answer = (answer + 1) % MODULE;
					}
				}
			}
			dp[now][left][right] = answer;
		}
		return dp[now][left][right];
	}

	int solve() { return dpCalc(start, 0, str.length()); }
};

int main()
{
	NFC calc("nfc.in");
	ofstream out("nfc.out");
	out << calc.solve() << endl;
	out.close();
	return 0;
}