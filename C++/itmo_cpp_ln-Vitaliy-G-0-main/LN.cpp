#include "LN.h"

#include "custom_exceptions.h"

size_t customStrlen(const char *str)
{
	size_t length = 0;
	while (str[length] != '\0')
	{
		++length;
	}
	return length;
}

char customToLower(char ch)
{
	if (ch >= 'A' && ch <= 'Z')
	{
		return ch + ('a' - 'A');
	}
	return ch;
}

LN::LN(ll value)
{
	isNegative = value < 0;
	isNaN = false;
	digits = {};
	if (value == 0)
	{
		digits.resize(1);
		digits[0] = 0;
		return;
	}
	ull base = 1'000'000'000;
	ull num = value > 0 ? value : -value;
	while (num != 0)
	{
		digits.push_back(num % base);
		num /= base;
	}
}

LN::LN(const char *s) : LN(s, customStrlen(s)) {}

LN::LN(const std::string_view &s) : LN(s, s.length()) {}

LN::LN(const std::string_view &s, size_t length)
{
	isNegative = false;
	isNaN = false;
	digits = {};

	if (length == 3 && s[0] == 'N' && s[1] == 'a' && s[2] == 'N' || length <= 0)
	{
		isNaN = true;
		return;
	}

	size_t idx = 0;
	if (s[0] == '-')
	{
		idx++;
		isNegative = true;
	}
	LN ln(0ll);
	LN base = LN(16ll);
	for (size_t i = idx; i < length; i++)
	{
		char digit = s[i];
		if (digit >= '0' && digit <= '9')
		{
			ln = ln * base + LN((ll)(digit - '0'));
		}
		else if ((digit >= 'A' && digit <= 'F') || (digit >= 'a' && digit <= 'f'))
		{
			ln = ln * base + LN((ll)(customToLower(digit) - 'a' + 10));
		}
		else
		{
			isNaN = true;
			break;
		}
	}
	digits = ln.digits;
}

LN::~LN() = default;

LN::LN(LN &&that) noexcept
{
	digits = that.digits;
	isNegative = that.isNegative;
	isNaN = that.isNaN;
	that.digits = {};
}

LN::LN(const LN &that)
{
	digits = that.digits;
	isNegative = that.isNegative;
	isNaN = that.isNaN;
}

LN operator""_ln(const char *s)
{
	return LN(s);
}

LN &LN::operator=(LN &&that) noexcept
{
	digits = that.digits;
	isNegative = that.isNegative;
	isNaN = that.isNaN;
	that.digits = {};
	return *this;
}

LN &LN::operator=(const LN &that)
{
	if (this == &that)
	{
		return *this;
	}
	digits = that.digits;
	isNegative = that.isNegative;
	isNaN = that.isNaN;
	return *this;
}

int LN::compareTo(const LN &that) const
{
	if (1 == digits.size() && 1 == that.digits.size() && digits[0] == that.digits[0] && digits[0] == 0)
	{
		return 0;
	}
	if (isNegative && !(that.isNegative))
	{
		return -1;
	}
	if (!isNegative && (that.isNegative))
	{
		return 1;
	}
	int res = compareToAbs(that);
	return isNegative ? -res : res;
}

int LN::compareToAbs(const LN &that) const
{
	if (digits.size() > that.digits.size())
	{
		return 1;
	}
	if (digits.size() < that.digits.size())
	{
		return -1;
	}
	for (size_t i = 0; i < digits.size(); i++)
	{
		size_t idx = digits.size() - i - 1;
		if (digits[idx] == that.digits[idx])
			continue;
		return digits[idx] > that.digits[idx] ? 1 : -1;
	}
	return 0;
}

bool operator<(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN)
	{
		return false;
	}
	return a.compareTo(b) < 0;
}

bool operator>(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN)
	{
		return false;
	}
	return a.compareTo(b) > 0;
}

bool operator==(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN)
	{
		return false;
	}
	return a.compareTo(b) == 0;
}

bool operator!=(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN)
	{
		return false;
	}
	return a.compareTo(b) != 0;
}

bool operator<=(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN)
	{
		return false;
	}
	return a.compareTo(b) <= 0;
}

bool operator>=(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN)
	{
		return false;
	}
	return a.compareTo(b) >= 0;
}

void LN::add(const LN &that)
{
	size_t sizeMax = digits.size() > that.digits.size() ? digits.size() : that.digits.size();
	ull res;
	uint carry = 0;
	ull base = 1'000'000'000;
	for (size_t i = 0; i < sizeMax || carry; i++)
	{
		if (i >= digits.size())
		{
			digits.push_back(0);
		}
		res = (ll)digits[i] + carry + (i < that.digits.size() ? that.digits[i] : 0);
		carry = res >= base ? 1 : 0;
		digits[i] = carry == 1 ? res - base : res;
	}
	while (digits.size() > 1 && digits.back() == 0)
	{
		digits.pop_back();
	}
}

void LN::subtract(const LN &that)
{
	uint carry = 0;
	ll res;
	ull base = 1'000'000'000;
	int compRes = compareToAbs(that);
	if (compRes == 0)
	{
		digits.resize(1);
		digits[0] = 0;
	}
	else if (compRes > 0)
	{
		for (size_t i = 0; i < that.digits.size() || carry; i++)
		{
			res = digits[i] - (carry + (i < that.digits.size() ? that.digits[i] : 0));
			carry = res < 0 ? 1 : 0;
			digits[i] = carry == 1 ? res + base : res;
		}
		while (digits.size() > 1 && digits.back() == 0)
		{
			digits.pop_back();
		}
	}
	else
	{
		isNegative = !isNegative;
		VectorLN temp = that.digits;
		for (size_t i = 0; i < digits.size() || carry; i++)
		{
			res = temp[i] - (carry + (i < digits.size() ? digits[i] : 0));
			carry = res < 0 ? 1 : 0;
			temp[i] = carry == 1 ? res + base : res;
		}
		while (temp.size() > 1 && temp.back() == 0)
		{
			temp.pop_back();
		}
		digits = temp;
	}
}

LN operator+(const LN &a, const LN &b)
{
	LN res(a);
	res += b;
	return res;
}

LN operator-(const LN &a, const LN &b)
{
	LN res(a);
	res -= b;
	return res;
}

LN operator*(const LN &a, const LN &b)
{
	LN res(a);
	res *= b;
	return res;
}

LN operator/(const LN &a, const LN &b)
{
	LN res(a);
	res /= b;
	return res;
}

LN operator-(const LN &that)
{
	LN res(that);
	res.isNegative = !res.isNegative;
	return res;
}

LN operator%(const LN &a, const LN &b)
{
	if (a.isNaN || b.isNaN || !b)
	{
		return LN("NaN");
	}
	return a - ((a / b) * b);
}

LN &LN::operator+=(const LN &that)
{
	if (isNaN || that.isNaN)
	{
		isNaN = true;
		return *this;
	}
	if (isNegative == that.isNegative)
	{
		add(that);
	}
	else
	{
		subtract(that);
	}
	return *this;
}

LN &LN::operator-=(const LN &that)
{
	if (isNaN || that.isNaN)
	{
		isNaN = true;
		return *this;
	}
	if (isNegative == that.isNegative)
	{
		subtract(that);
	}
	else
	{
		add(that);
	}
	return *this;
}

LN &LN::operator*=(const LN &that)
{
	isNegative = isNegative != that.isNegative;
	uint carry = 0;
	ull base = 1'000'000'000;
	ull cur;
	VectorLN tmp(digits.size() + that.digits.size(), 0);

	for (size_t i = 0; i < digits.size(); i++)
	{
		for (size_t j = 0; j < that.digits.size() || carry; j++)
		{
			cur = (j < that.digits.size() ? (ll)tmp[i + j] + (ll)digits[i] * that.digits[j] : tmp[i + j]) + carry;
			tmp[i + j] = cur % base;
			carry = cur / base;
		}
	}
	while (tmp.size() > 1 && tmp.back() == 0)
	{
		tmp.pop_back();
	}
	digits = tmp;
	return *this;
}

LN &LN::operator/=(const LN &that)
{
	LN divisor = that;
	bool flag = isNegative != divisor.isNegative;
	isNegative = false;
	divisor.isNegative = false;

	if (divisor == LN(0ll))
	{
		isNaN = true;
		return *this;
	}
	if (*this < divisor)
	{
		*this = LN(0ll);
		return *this;
	}

	VectorLN res(digits.size(), 0);
	LN remainder(0ll);
	remainder.digits.resize(digits.size());

	ull base = 1'000'000'000;

	for (size_t i = digits.size(); i-- > 0;)
	{
		remainder *= LN((ll)base);
		remainder.digits[0] = digits[i];

		uint quotient;
		uint left = 0;
		uint right = base;

		while (left + 1 < right)
		{
			uint mid = (left + right) / 2;
			if (divisor * LN(mid) <= remainder)
			{
				left = mid;
			}
			else
			{
				right = mid;
			}
		}

		quotient = left;
		remainder -= divisor * LN(quotient);
		res[i] = quotient;
	}

	while (res.size() > 1 && res.back() == 0)
	{
		res.pop_back();
	}

	digits = res;
	isNegative = flag;
	return *this;
}

LN &LN::operator%=(const LN &that)
{
	*this = *this % that;
	return *this;
}

LN &LN::operator-()
{
	isNegative = !isNegative;
	return *this;
}

LN operator~(const LN &a)
{
	if (a.isNegative && a != LN(0ll))
	{
		return LN("NaN");
	}
	if (a == LN(0ll))
	{
		return LN(0ll);
	}

	LN x0(a);
	LN x1 = (x0 + a / x0) / LN(2ll);
	LN one(1ll);

	while (x1 < x0)
	{
		x0 = x1;
		x1 = (x0 + a / x0) / LN(2ll);
	}

	return x0;
}

LN::operator bool() const
{
	if (isNaN)
	{
		return true;
	}
	size_t cnt = 0;
	for (size_t i = 0; i < digits.size(); i++)
	{
		if (digits[i] == 0)
			cnt++;
	}
	return cnt != digits.size();
}

LN::operator long long() const
{
	if (isNaN)
	{
		throw OutOfRangeError("Can't convert NaN");
	}
	if (!(*this))
	{
		return 0ll;
	}
	ll sign = isNegative ? -1 : 1;
	ll res = digits[0];
	ll base = 1'000'000'000;
	ll last = res;

	for (size_t i = 1; i < digits.size(); i++)
	{
		res += sign * digits[i] * base;
		if ((!isNegative && res < last) || (isNegative && res > last))
		{
			throw OutOfRangeError("Did not fit in long long");
		}
		last = res;
		base *= base;
	}
	return res;
}

char *LN::toString()
{
	if (isNaN)
	{
		char *result = new char[4];
		const char *source = "NaN";
		for (size_t i = 0; i < 4; ++i)
		{
			result[i] = source[i];
		}
		return result;
	}
	if (!(*this))
	{
		char *result = new char[2];
		const char *source = "0";
		for (size_t i = 0; i < 2; ++i)
		{
			result[i] = source[i];
		}
		return result;
	}
	LN sixteen(16ll);
	const char *HEX_LOOKUP = "0123456789ABCDEF";

	bool flag = isNegative;
	isNegative = false;
	LN decimalNumber = *this;

	size_t length = 0;
	while (decimalNumber > LN(0ll))
	{
		decimalNumber /= sixteen;
		length++;
	}
	if (flag)
	{
		length++;
	}

	char *result = new char[length + 1];
	result[length] = '\0';

	size_t index = length - 1;
	decimalNumber = *this;
	while (decimalNumber > LN(0ll))
	{
		LN remainder = decimalNumber % sixteen;
		char s = HEX_LOOKUP[remainder.digits[0]*16];
		result[index--] = s;
		decimalNumber /= sixteen;
	}
	if (flag)
	{
		result[index--] = '-';
	}
	return result;
}