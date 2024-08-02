//
// Created by Виталий on 14.04.2024.
//
#include <iostream>
#include <vector>
#include <numeric>
#include <algorithm>

using std::cin;
using std::cout;
using std::vector;
using std::ostream;

using Int = long long;
using VecInt = vector<Int>;

const int MAX_TERMS = 20;

class Ratio {
  public:
	Int numerator, denominator;

	Ratio(Int num = 0, Int denom = 1) {
		Int gcd_val = std::gcd(num, denom);
		numerator = num / gcd_val;
		denominator = denom / gcd_val;
	}

	Ratio& operator+=(const Ratio& other) {
		Int common_denom = std::lcm(denominator, other.denominator);
		numerator = numerator * (common_denom / denominator) + other.numerator * (common_denom / other.denominator);
		denominator = common_denom;
		Int gcd_val = std::gcd(numerator, denominator);
		numerator /= gcd_val;
		denominator /= gcd_val;
		return *this;
	}
};

Ratio operator+(const Ratio& lhs, const Ratio& rhs) {
	return Ratio(lhs.numerator * (rhs.denominator) + rhs.numerator * (lhs.denominator), lhs.denominator * rhs.denominator);
}

Ratio operator-(const Ratio& lhs, const Ratio& rhs) {
	return lhs + Ratio(-rhs.numerator, rhs.denominator);
}

Ratio operator*(const Ratio& lhs, const Ratio& rhs) {
	return Ratio(lhs.numerator * rhs.numerator, lhs.denominator * rhs.denominator);
}

Ratio operator/(const Ratio& lhs, const Ratio& rhs) {
	return Ratio(lhs.numerator * rhs.denominator, lhs.denominator * rhs.numerator);
}

ostream& operator<<(ostream& os, const Ratio& ratio) {
	os << ratio.numerator << '/' << ratio.denominator;
	return os;
}

using VecRatio = vector<Ratio>;

class Poly {
  public:
	VecRatio coeffs;

	Poly(VecRatio coeffs) : coeffs(std::move(coeffs)) {
		while (this->coeffs.size() > MAX_TERMS) {
			this->coeffs.pop_back();
		}
	}

	size_t size() const {
		return coeffs.size();
	}

	Ratio at(size_t idx) const {
		return idx < coeffs.size() ? coeffs[idx] : Ratio();
	}

	void printTerms(size_t upTo) const {
		for (size_t i = 0; i <= upTo; ++i) {
			cout << coeffs[i] << ' ';
		}
		cout << '\n';
	}

	Poly& operator+=(const Poly& other) {
		size_t max_size = std::max(size(), other.size());
		coeffs.resize(max_size, Ratio());

		for (size_t i = 0; i < other.size(); ++i) {
			coeffs[i] += other.at(i);
		}

		return *this;
	}
};

Poly operator*(const Poly& lhs, const Poly& rhs) {
	VecRatio product(lhs.size() + rhs.size() - 1, Ratio());

	for (size_t i = 0; i < lhs.size(); ++i) {
		for (size_t j = 0; j < rhs.size(); ++j) {
			product[i + j] += lhs.at(i) * rhs.at(j);
		}
	}

	return Poly(std::move(product));
}

int main() {
	int rank, terms;
	cin >> rank >> terms;

	VecRatio poly_coeffs;
	for (int i = 0; i <= terms; ++i) {
		Int coeff;
		cin >> coeff;
		poly_coeffs.emplace_back(coeff);
	}

	VecInt factorial(terms + 1, 1);
	for (int i = 1; i <= terms; ++i) {
		factorial[i] = factorial[i - 1] * i;
	}

	Poly result(VecRatio{Ratio()});
	for (int i = 0; i <= terms; ++i) {
		Poly term(VecRatio{Ratio(1)});
		for (int j = terms; j > 0; --j) {
			VecRatio tempCoeffs;
			tempCoeffs.push_back(Ratio(j - i));
			tempCoeffs.push_back(Ratio(1));
			term = term * Poly(std::move(tempCoeffs));
		}
		term = term * Poly(VecRatio{Ratio(1, factorial[terms])});
		for (int j = 0; j < i; ++j) {
			term = term / Poly(VecRatio{Ratio(rank)});
		}
		result += term * Poly(VecRatio{poly_coeffs[i]});
	}
	result.printTerms(terms);
}