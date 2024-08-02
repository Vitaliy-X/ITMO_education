//
// Created by Виталий on 14.04.2024.
//
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

using ll = long long;
using Vec = vector<ll>;

const ll MODULO = 104857601LL;

Vec MultiplyVectors(Vec &vec1, Vec &vec2) {
	size_t len1 = vec1.size(), len2 = vec2.size();
	Vec result(len1 + len2 - 1, 0);

	for (size_t i = 0; i < len1; ++i) {
		for (size_t j = 0; j < len2; ++j) {
			result[i + j] = (result[i + j] + (vec1[i] * vec2[j] % MODULO)) % MODULO;
		}
	}

	return result;
}

Vec InvertSigns(Vec &vec) {
	Vec result(vec);
	for (size_t i = 1; i < vec.size(); i += 2) {
		result[i] = (MODULO - result[i]) % MODULO;
	}
	return result;
}

class LinearRecurrence {
  public:
	Vec sequence, coefficients;

	LinearRecurrence(Vec seq, Vec coef) : sequence(std::move(seq)), coefficients(std::move(coef)) {}

	ll CalculateTerm(size_t n) {
		size_t k = sequence.size();
		Vec p(k, 0);
		for (size_t i = 0; i < k; ++i) {
			ll temp = 0;
			for (size_t j = 0; j < i; ++j) {
				temp = (temp + sequence[i - j - 1] * (-coefficients[j + 1]) % MODULO + MODULO) % MODULO;
			}
			p[i] = (sequence[i] - temp + MODULO) % MODULO;
		}

		Vec coef = coefficients;
		for (n--; n >= k; n >>= 1) {
			Vec negCoef = InvertSigns(coef), squaredCoef = MultiplyVectors(coef, negCoef);
			for (size_t i = 0; i < squaredCoef.size(); i += 2) {
				coef[i / 2] = squaredCoef[i];
			}
			Vec tempVec = MultiplyVectors(p, negCoef);
			for (size_t i = n % 2; i < tempVec.size(); i += 2) {
				p[i / 2] = tempVec[i];
			}
		}

		Vec result(k, 0);
		for (size_t i = 0; i < k; ++i) {
			ll temp = 0;
			for (size_t j = 0; j < i; ++j) {
				temp = (temp + result[i - j - 1] * (-coef[j + 1]) % MODULO + MODULO) % MODULO;
			}
			result[i] = (p[i] + temp) % MODULO;
		}

		return result[n];
	}
};

int main() {
	size_t k;
	ll n;

	cin >> k >> n;

	Vec a(k), c(k + 1, 1);
	for (size_t i = 0; i < k; ++i) cin >> a[i];
	for (size_t i = 1; i <= k; ++i) {
		cin >> c[i];
		c[i] = (MODULO - c[i]) % MODULO;
	}

	LinearRecurrence lr(a, c);
	cout << lr.CalculateTerm(n) << '\n';

	return EXIT_SUCCESS;
}