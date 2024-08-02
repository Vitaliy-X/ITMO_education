//
// Created by Виталий on 14.04.2024.
//
#include <iostream>
#include <vector>

using std::cin;
using std::cout;
using std::vector;

using ll = long long;
using Vector = vector<ll>;

const int MODULO = 998244353;
const int LIMIT = 5001;

class Series {
  public:
	Vector terms;

	Series(Vector init) : terms(std::move(init)) {
		trim();
	}

	explicit Series(ll value) : terms(1, value) {}

	size_t length() const {
		return terms.size();
	}

	ll getTerm(size_t idx) const {
		return idx < terms.size() ? terms[idx] : 0;
	}

	void displayTerms(size_t count) const {
		for (size_t i = 1; i <= count; ++i) {
			cout << getTerm(i) << '\n';
		}
	}

	void trim() {
		if (terms.size() > LIMIT) {
			terms.resize(LIMIT);
		}
	}
};

Series operator+(const Series& a, const Series& b) {
	Vector result = a.length() < b.length() ? b.terms : a.terms;
	for (size_t i = 0; i < std::min(a.length(), b.length()); ++i) {
		result[i] = (a.getTerm(i) + b.getTerm(i)) % MODULO;
	}
	return Series(std::move(result));
}

Series operator*(const Series& a, const Series& b) {
	Vector result(a.length() + b.length() - 1, 0);
	for (size_t i = 0; i < a.length(); ++i) {
		for (size_t j = 0; j < b.length(); ++j) {
			result[i + j] = (result[i + j] + a.getTerm(i) * b.getTerm(j)) % MODULO;
		}
	}
	return Series(std::move(result));
}

Series operator/(const Series& a, const Series& b) {
	Vector result(LIMIT, 0);
	result[0] = a.getTerm(0) / b.getTerm(0);
	for (int i = 1; i < LIMIT; ++i) {
		ll sum = 0;
		for (int j = 0; j < i; ++j) {
			sum = (sum + result[j] * b.getTerm(i - j)) % MODULO;
		}
		result[i] = ((MODULO + a.getTerm(i) - sum) % MODULO) / b.getTerm(0);
	}
	return Series(std::move(result));
}

int main() {
	int maxDegree, outputTerms;
	cin >> maxDegree >> outputTerms;

	Series currentSeries(Vector{0, 1});
	Series quotient(1);

	for (int i = 3; i <= maxDegree; i++) {
		Series tempSeries = quotient;
		tempSeries.terms.insert(tempSeries.terms.begin(), 0);
		tempSeries.trim();
		quotient = Series(-1) * currentSeries + quotient;
		currentSeries = tempSeries;
	}

	(currentSeries / quotient).displayTerms(outputTerms);

	return EXIT_SUCCESS;
}
