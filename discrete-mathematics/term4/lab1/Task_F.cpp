//
// Created by Виталий on 14.04.2024.
//
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

const long long MOD = 1e9 + 7;

void readCoefficients(vector<long long>& coefficients, size_t size) {
	coefficients.resize(size);
	for (size_t i = 0; i < size; i++) {
		cin >> coefficients[i];
	}
}

void calculateResults(vector<long long>& coefficients, vector<long long>& result, vector<long long>& prefix, size_t elements) {
	sort(coefficients.begin(), coefficients.end());
	result[0] = 1ll;
	for (long long coefficient : coefficients) {
		result[coefficient] = 1ll;
	}

	prefix[0] = 1;
	for (size_t i = 1; i <= elements; i++) {
		for (long long coefficient : coefficients) {
			if (i <= coefficient) {
				break;
			}
			result[i] = (result[i] + prefix[i - coefficient]) % MOD;
		}
		for (size_t j = 0; j <= i; j++) {
			prefix[i] = (prefix[i] + result[j] * result[i - j]) % MOD;
		}
	}
}

void printResults(vector<long long>& result, size_t elements) {
	for (size_t i = 1; i <= elements; i++) {
		cout << result[i] << ' ';
	}
}

int main() {
	size_t size, elements;
	cin >> size >> elements;

	vector<long long> coefficients;
	readCoefficients(coefficients, size);

	vector<long long> result(elements + 1, 0), prefix(elements + 1, 0);
	calculateResults(coefficients, result, prefix, elements);

	printResults(result, elements);

	return EXIT_SUCCESS;
}

