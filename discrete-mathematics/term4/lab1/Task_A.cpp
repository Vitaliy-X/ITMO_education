//
// Created by Виталий on 14.04.2024.
//

#include <iostream>
#include <vector>

using namespace std;

using Long = long long;
using Vector = vector<Long>;

const Long MODULO = 998244353;

class Poly {
  public:
	Vector coefficients;

	explicit Poly(Vector&& coeffs) : coefficients(std::move(coeffs)) {}

	explicit Poly(size_t degree) {
		coefficients.resize(degree + 1);
		for (size_t i = 0; i <= degree; ++i) {
			cin >> coefficients[i];
		}
	}

	[[nodiscard]] Long at(size_t index) const {
		return index < coefficients.size() ? coefficients[index] : 0;
	}

	[[nodiscard]] size_t len() const;

	[[nodiscard]] size_t deg() const;

	void printWithoutDeg() const;

	void printWithDeg() const;
};

void Poly::printWithDeg() const
{
	cout << deg() << '\n';
	for (Long coeff : coefficients) {
		cout << coeff << ' ';
	}
	cout << '\n';
}

void Poly::printWithoutDeg() const
{
	for (Long coeff : coefficients) {
		cout << coeff << ' ';
	}
	cout << '\n';
}

size_t Poly::deg() const
{ return coefficients.size() - 1; }

size_t Poly::len() const
{ return coefficients.size(); }

// redefine operations

Poly operator+(const Poly& lhs, const Poly& rhs) {
	size_t lhs_len = lhs.len(), rhs_len = rhs.len();
	Vector result(max(lhs_len, rhs_len), 0);

	for (size_t i = 0; i < lhs_len; ++i) {
		result[i] = lhs.at(i);
	}
	for (size_t i = 0; i < rhs_len; ++i) {
		result[i] = (result[i] + rhs.at(i)) % MODULO;
	}

	return Poly(std::move(result));
}

Poly operator/(const Poly& lhs, const Poly& rhs) {
	Vector result(1000, 0);
	result[0] = lhs.at(0) / rhs.at(0);
	for (int i = 1; i < 1000; ++i) {
		Long sum = 0;
		for (int j = 0; j < i; ++j) {
			sum = (sum + result[j] * rhs.at(i - j)) % MODULO;
		}
		result[i] = ((MODULO + lhs.at(i) - sum) % MODULO) / rhs.at(0);
	}
	return Poly(std::move(result));
}

Poly operator*(const Poly& lhs, const Poly& rhs) {
	size_t lhs_len = lhs.len(), rhs_len = rhs.len();
	Vector result(lhs_len + rhs_len - 1, 0);

	for (size_t i = 0; i < lhs_len; ++i) {
		for (size_t j = 0; j < rhs_len; ++j) {
			result[i + j] = (result[i + j] + lhs.at(i) * rhs.at(j)) % MODULO;
		}
	}

	return Poly(std::move(result));
}

int main() {
	size_t degree1, degree2;
	cin >> degree1 >> degree2;
	Poly poly1(degree1), poly2(degree2);

	(poly1 + poly2).printWithDeg();
	(poly1 * poly2).printWithDeg();
	(poly1 / poly2).printWithoutDeg();

	return EXIT_SUCCESS;
}