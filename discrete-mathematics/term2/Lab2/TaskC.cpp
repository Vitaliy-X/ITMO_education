#include <set>

using namespace std;

struct FSG
{
	struct State
	{
		bool isGen = false;
		bool isSpec = false;
		bool isReach = false;
		set< set< char > > transitions;
	} states['Z' - 'A' + 1];

	set< char > reachable;
	set< char > generating;
	char start;

	explicit FSG(char start) : start(start) {}

	void add_trans(char A) { generating.insert(A); }

	void add_trans(char A, const set< char > &C) { states[A - 'A'].transitions.insert(C); }

	void addSpec(char A) { states[A - 'A'].isSpec = true; }

	int getComp(int i, set< char > &set, bool flag)
	{
		for (auto &tr : states[i].transitions)
		{
			for (auto next : tr)
			{
				if (set.find(next) == set.end())
				{
					set.insert((char)(next));
					flag = true;
				}
			}
			if (flag)
			{
				i = -1;
				break;
			}
		}
		return i;
	}

	void find_generating_states()
	{
		for (int i = 0; i < 'Z' - 'A' + 1; i++)
		{
			if (generating.find((char)(i + 'A')) == generating.end())
			{
				for (auto &tr : states[i].transitions)
				{
					bool flag = true;
					for (auto next : tr)
					{
						if (generating.find(next) == generating.end())
						{
							flag = false;
						}
					}
					if (flag)
					{
						generating.insert((char)(i + 'A'));
						i = -1;
						break;
					}
				}
			}
		}
	}

	void eliminate_unreachable_transitions()
	{
		for (int i = 0; i < 'Z' - 'A' + 1; i++)
		{
			for (auto &tr : states[i].transitions)
			{
				bool flag = true;
				for (auto next : tr)
				{
					if (generating.find(next) == generating.end())
						flag = false;
				}
				if (!flag)
				{
					states[i].transitions.erase(tr);
					i--;
					break;
				}
			}
		}
	}

	void print(FILE *out)
	{
		find_generating_states();

		for (auto cur : generating)
		{
			states[cur - 'A'].isGen = true;
		}

		eliminate_unreachable_transitions();

		reachable.insert(start);
		for (int i = 0; i < 'Z' - 'A' + 1; i++)
		{
			if (reachable.find((char)(i + 'A')) != reachable.end())
			{
				i = getComp(i, reachable, false);
			}
		}

		for (auto cur : reachable)
		{
			states[cur - 'A'].isReach = true;
		}

		for (char i = 'A'; i <= 'Z'; i++)
		{
			if (states[i - 'A'].isSpec)
			{
				if (!states[i - 'A'].isGen || !states[i - 'A'].isReach)
					fprintf(out, "%c ", i);
			}
		}
	}
};

void parseHeader(FILE *in, size_t *n, char *S) {
	fscanf(in, "%zu %c\n", n, S);
}

void parseSpec(FSG *fsg, char spec) {
	fsg->addSpec(spec);
}

void parseTransitions(FSG *fsg, char A, FILE *in) {
	char N;
	int cnt1 = 0;
	int cnt2 = 0;
	set<char> tr;

	while (fscanf(in, "%c", &N) != EOF) {
		if (N == '\n')
			break;
		if (N >= 'a' && N <= 'z') {
			cnt2++;
			continue;
		}
		if (N == ' ' || N == '\t')
			continue;
		if (N >= 'A' && N <= 'Z') {
			fsg->addSpec(N);
			tr.insert(N);
			cnt1++;
		}
	}

	if (cnt1 != 0) {
		fsg->add_trans(A, tr);
	} else {
		fsg->add_trans(A);
	}
}

FSG *compute(FILE *in) {
	size_t n;
	char S;
	parseHeader(in, &n, &S);

	FSG *fsg = new FSG(S);
	fsg->addSpec(S);

	while (n--) {
		char A;
		fscanf(in, "%c ->", &A);
		parseSpec(fsg, A);
		parseTransitions(fsg, A, in);
	}

	return fsg;
}

int main()
{
	FILE *in = fopen("useless.in", "r");
	FSG *fsg = compute(in);

	FILE *out = fopen("useless.out", "w");
	fsg->print(out);

	delete fsg;
	fclose(in);
	fclose(out);

	return 0;
}