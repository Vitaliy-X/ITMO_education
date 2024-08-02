//
// Created by Виталий on 14.04.2024.
//
#include <iostream>
#include <vector>

using namespace std;

using Int64 = long long;
using Vector = vector<Int64>;

const Int64 Mod = 998244353LL;

Int64 gcdExtended(Int64 a, Int64 b, Int64 &x, Int64 &y) {
	if (a == 0) {
		x = 0; y = 1;
		return b;
	}
	Int64 x1, y1;
	Int64 gcd = gcdExtended(b % a, a, x1, y1);
	x = y1 - (b / a) * x1;
	y = x1;
	return gcd;
}

Int64 modularInverse(Int64 a) {
	Int64 x, y;
	Int64 g = gcdExtended(a, Mod, x, y);
	if (g != 1) return -1; // Inverse doesn't exist
	else return (x % Mod + Mod) % Mod;
}

class Series {
  public:
	Vector coefficients;

	Series() = default;
	explicit Series(Vector coeffs): coefficients(move(coeffs)) {}

	Series inverseSeries(size_t degree) const {
		Vector inv(degree, 0);
		inv[0] = 1; // Assuming the first coefficient is 1 for simplicity

		for (size_t i = 1; i < degree; ++i) {
			for (size_t j = 1; j <= i; ++j) {
				inv[i] = (inv[i] + coefficients[j] * inv[i - j]) % Mod;
			}
			inv[i] = (Mod - inv[i]) % Mod;
			inv[i] = (inv[i] * modularInverse(i)) % Mod;
		}

		return Series(inv);
	}

	void printSeries() const {
		for (Int64 coeff : coefficients) {
			cout << coeff << " ";
		}
		cout << endl;
	}
};

Series operator*(const Series &lhs, const Series &rhs) {
	size_t n = lhs.coefficients.size();
	size_t m = rhs.coefficients.size();
	Vector result(n + m - 1, 0);

	for (size_t i = 0; i < n; ++i) {
		for (size_t j = 0; j < m; ++j) {
			result[i + j] = (result[i + j] + lhs.coefficients[i] * rhs.coefficients[j]) % Mod;
		}
	}

	return Series(result);
}

int main() {
	ios::sync_with_stdio(false);
	cin.tie(nullptr);
	cout.tie(nullptr);

	size_t degree;
	cin >> degree;
	Vector coeffs(degree);
	for (size_t i = 0; i < degree; ++i) {
		cin >> coeffs[i];
	}

	Series series(coeffs);
	// Example usage
	auto invSeries = series.inverseSeries(degree);
	invSeries.printSeries();

	return 0;
}