//
// Created by Виталий on 14.04.2024.
//
#include <vector>
#include <iostream>

using namespace std;

using Long = long long;
using VectorLong = vector<Long>;

const Long MODULO = 100000000000LL;

class FractionalPolynomial {
	VectorLong numeratorCoeffs;
	VectorLong denominatorCoeffs;
	size_t degDenominator;
	size_t degNumerator{};

	void initializeDenominator(const VectorLong &recurrenceCoeffs) {
		denominatorCoeffs[0] = 1;
		for (size_t i = 1; i <= degDenominator; i++) {
			denominatorCoeffs[i] = -recurrenceCoeffs[i - 1];
		}
	}

	void computeNumerator(const VectorLong &initialCoeffs) {
		numeratorCoeffs[0] = initialCoeffs[0];
		for (size_t i = 1; i < degDenominator; i++) {
			Long tempSum = 0;
			for (size_t j = 1; j <= i; j++) {
				tempSum = (tempSum + initialCoeffs[i - j] * denominatorCoeffs[j]) % MODULO;
			}
			numeratorCoeffs[i] = (initialCoeffs[i] + tempSum) % MODULO;
		}
	}

	void findDegrees() {
		degNumerator = 0;
		for (size_t i = degDenominator; i > 0; i--) {
			if (numeratorCoeffs[i] != 0) {
				degNumerator = i;
				break;
			}
		}
	}

  public:
	FractionalPolynomial(size_t degree, const VectorLong &initialCoeffs, const VectorLong &recurrenceCoeffs)
		: degDenominator(degree) {
		numeratorCoeffs.resize(degree + 1, 0);
		denominatorCoeffs.resize(degree + 1, 0);
		initializeDenominator(recurrenceCoeffs);
		computeNumerator(initialCoeffs);
		findDegrees();
	}

	void printNumerator() const;
	void printDenominator() const;
};

void FractionalPolynomial::printNumerator() const
{
	cout << degNumerator << '\n';
	for (size_t i = 0; i <= degNumerator; i++) {
		cout << numeratorCoeffs[i] << ' ';
	}
}

void FractionalPolynomial::printDenominator() const
{
	cout << degDenominator << '\n';
	for (size_t i = 0; i <= degDenominator; i++) {
		cout << denominatorCoeffs[i] << ' ';
	}
}

int main() {
	size_t degree;
	cin >> degree;

	VectorLong initialCoeffs(degree), recurrenceCoeffs(degree);
	for (size_t i = 0; i < degree; i++) {
		cin >> initialCoeffs[i];
	}
	for (size_t i = 0; i < degree; i++) {
		cin >> recurrenceCoeffs[i];
	}

	FractionalPolynomial fracPoly(degree, initialCoeffs, recurrenceCoeffs);

	fracPoly.printNumerator();
	fracPoly.printDenominator();

	return EXIT_SUCCESS;
}