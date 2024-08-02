//
// Created by Виталий on 29.10.2023.
//
#include <list>
#include <iostream>

using namespace std;

bool shouldSwap(int i, int j) {
	cout << 1 << ' ' << i << ' ' << j << endl;
	cout.flush();
	string answer;
	cin >> answer;
	return answer[0] == 'Y';
}

int main() {
	ios::sync_with_stdio(false);

	int n;
	cin >> n;

	list<int> list;
	auto i = 1;
	while (i <= n) {
		list.push_back(i++);
	}

	list.sort(shouldSwap);

	cout << 0 << ' ';
	for (const int &i : list) {
		cout << i << ' ';
	}
	cout << endl;

	return EXIT_SUCCESS;
}