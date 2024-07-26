#ifndef LN_H
#define LN_H

#include "vectorLN.h"
#include <string_view>

typedef long long ll;
typedef unsigned long long ull;
typedef unsigned int uint;

class LN
{
  public:
	explicit LN(ll value = 0);
	explicit LN(const char *s);
	explicit LN(const std::string_view &s);
	~LN();
	LN(LN &&that) noexcept;
	LN(const LN &that);
	LN &operator=(LN &&that) noexcept;
	LN &operator=(const LN &that);

	friend LN operator+(const LN &a, const LN &b);
	friend LN operator-(const LN &a, const LN &b);
	friend LN operator*(const LN &a, const LN &b);
	friend LN operator/(const LN &a, const LN &b);
	friend LN operator%(const LN &a, const LN &b);
	friend LN operator~(const LN &a);
	friend LN operator-(const LN &a);

	LN &operator+=(const LN &a);
	LN &operator-=(const LN &a);
	LN &operator*=(const LN &a);
	LN &operator/=(const LN &divisor);
	LN &operator-();
	LN &operator%=(const LN &a);

	friend bool operator<(const LN &a, const LN &b);
	friend bool operator>(const LN &a, const LN &b);
	friend bool operator==(const LN &a, const LN &b);
	friend bool operator!=(const LN &a, const LN &b);
	friend bool operator<=(const LN &a, const LN &b);
	friend bool operator>=(const LN &a, const LN &b);

	explicit operator bool() const;
	explicit operator long long() const;
	char *toString();
	friend LN operator""_ln(const char *);

  private:
	explicit LN(const std::string_view &s, size_t length);
	VectorLN digits;
	bool isNaN, isNegative;

	[[nodiscard]] int compareTo(const LN &that) const;
	[[nodiscard]] int compareToAbs(const LN &that) const;
	void add(const LN &that);
	void subtract(const LN &that);
};
#endif