#include <cstdio>
#include <set>
#include <vector>

using namespace std;

struct FSG
{
	struct State
	{
		vector< set< char > > transitions;
	};
	State states['Z' - 'A' + 1];
	char start;
	set< char > eps_position;

	explicit FSG(char start) : start(start) {}

	void add_trans(char A) { eps_position.insert(A); }

	void add_trans(char A, set< char > C) { states[A - 'A'].transitions.push_back(C); }

	void print(FILE *out)
	{
		for(char i = 0; i < 'Z' - 'A' + 1; i++)
		{
			if(eps_position.find((char)(i + 'A')) == eps_position.end())
			{
				for(auto &tr : states[i].transitions)
				{
					bool res_tr = true;
					for(auto next : tr)
					{
						if(eps_position.find(next) == eps_position.end())
							res_tr = false;
					}
					if(res_tr) {eps_position.insert((char)(i + 'A')); i = 0 - 1; break;}
				}
			}
		}

		for (auto cur : eps_position)
		{
			fprintf(out, "%c ", cur);
		}
	}
};

FSG *compute(FILE *in)
{
	size_t n;
	char S;
	fscanf(in, "%zu %c\n", &n, &S);

	FSG *fsg = new FSG(S);

	while (n--)
	{
		char A;
		fscanf(in, "%c ->", &A);

		char N;
		int cnt1 = 0;
		int cnt2 = 0;
		set< char > tr;
		while (fscanf(in, "%c", &N) != EOF)
		{
			if (N == '\n')
				break;
			if (N >= 'a' && N <= 'z')
			{
				cnt2++;
				continue;
			}
			if (N == ' ' || N == '\t')
				continue;
			if (N >= 'A' && N <= 'Z')
			{
				tr.insert(N);
				cnt1++;
			}
		}
		if (cnt2 == 0)
		{
			if (cnt1 != 0)
				fsg->add_trans(A, tr);
			else
				fsg->add_trans(A);
		}
	}

	return fsg;
}

int main()
{
	FILE *in = fopen("epsilon.in", "r");
	FILE *out = fopen("epsilon.out", "w");
	FSG *fsg = compute(in);
	fsg->print(out);
	fclose(in);
	fclose(out);
	return 0;
}