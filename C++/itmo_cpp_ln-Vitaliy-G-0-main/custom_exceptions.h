#ifndef CUSTOM_EXCEPTIONS_H
#define CUSTOM_EXCEPTIONS_H

#include <exception>
#include <string>

class MemoryAllocationError : public std::exception
{
  public:
	explicit MemoryAllocationError(std::string message) : message_(std::move(message)) {}
	[[nodiscard]] const char* what() const noexcept override { return message_.c_str(); }

  private:
	std::string message_;
};

class OutOfRangeError : public std::exception
{
  public:
	explicit OutOfRangeError(std::string message) : message_(std::move(message)) {}
	[[nodiscard]] const char* what() const noexcept override { return message_.c_str(); }

  private:
	std::string message_;
};

#endif	  // CUSTOM_EXCEPTIONS_H