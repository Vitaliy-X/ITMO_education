#include "LN.h"
#include "custom_exceptions.h"
#include "return_codes.h"

#include <fstream>
#include <iostream>
#include <stack>

bool isNumber(std::string buffer)
{
	size_t i = 0;
	if (buffer[i] == '-')
	{
		i++;
		if (buffer[i] == '\0')
		{
			return false;
		}
	}
	while (true)
	{
		if (buffer[i] == '\0')
		{
			break;
		}
		if (!('0' <= buffer[i] && buffer[i] <= '9') && !('a' <= buffer[i] && buffer[i] <= 'f') &&
			!('A' <= buffer[i] && buffer[i] <= 'F'))
		{
			return false;
		}
		i++;
	}
	return true;
}

LN processOperation(const std::string& op, LN a, const LN& b)
{
	switch (op[0])
	{
	case '_':
		return -a;
	case '~':
		return ~a;
	case '+':
		return a + b;
	case '-':
		return a - b;
	case '*':
		return a * b;
	case '/':
		return a / b;
	case '%':
		return a % b;
	case '<':
		return op[1] == '=' ? (a <= b ? LN(1ll) : LN(0ll)) : (a < b ? LN(1ll) : LN(0ll));
	case '>':
		return op[1] == '=' ? (a >= b ? LN(1ll) : LN(0ll)) : (a > b ? LN(1ll) : LN(0ll));
	case '=':
		return a == b ? LN(1ll) : LN(0ll);
	case '!':
		return a != b ? LN(1ll) : LN(0ll);
	default:
		return LN("NaN");
	}
}

int main(int argc, char* argv[])
{
	if (argc != 3)
	{
		fprintf(stderr, "Invalid number of parameters! Expected: 3, got: %d", argc);
		return ERROR_PARAMETER_INVALID;
	}

	std::ifstream in(argv[1]);
	if (!in)
	{
		fprintf(stderr, "File can't be opened \"%s\"!\n", argv[1]);
		return ERROR_CANNOT_OPEN_FILE;
	}

	std::string line;
	std::stack< LN > stackOfOperands;

	while (in >> line)
	{
		if (isNumber(line))
		{
			try
			{
				stackOfOperands.emplace(line);
			} catch (const MemoryAllocationError& e)
			{
				std::cerr << std::string(e.what()) + "operand is very large" << std::endl;
				return ERROR_OUT_OF_MEMORY;
			}
		}
		else
		{
			LN a = stackOfOperands.top();
			stackOfOperands.pop();
			LN b = (line != "_" && line != "~") ? stackOfOperands.top() : LN();
			if (line != "_" && line != "~")
			{
				stackOfOperands.pop();
			}
			LN tmp;
			try
			{
				tmp = processOperation(line, a, b);
			} catch (const MemoryAllocationError& e)
			{
				std::cerr << std::string(e.what()) + "result is very large" << std::endl;
				return ERROR_OUT_OF_MEMORY;
			}
			stackOfOperands.push(tmp);
		}
	}
	in.close();

	std::ofstream out(argv[2]);
	if (!out.is_open())
	{
		fprintf(stderr, "File can't be opened \"%s\"!\n", argv[2]);
		return ERROR_CANNOT_OPEN_FILE;
	}

	while (!stackOfOperands.empty())
	{
		out << stackOfOperands.top().toString() << std::endl;
		stackOfOperands.pop();
	}
	out.close();
	return SUCCESS;
}