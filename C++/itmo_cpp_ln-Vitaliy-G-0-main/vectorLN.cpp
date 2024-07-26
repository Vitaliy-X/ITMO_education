#include "vectorLN.h"

#include "custom_exceptions.h"

#include <new>
#include <stdexcept>

VectorLN::VectorLN() : data_(nullptr), size_(0), capacity_(0) {}

VectorLN::VectorLN(std::size_t size, uint32_t value) :
	data_(new(std::nothrow) uint32_t[size]), size_(size), capacity_(size)
{
	if (data_ == nullptr)
		throw MemoryAllocationError("Memory allocation for initialization failed: ");
	fill(data_, data_ + size_, value);
}

VectorLN::~VectorLN()
{
	delete[] data_;
}

VectorLN::VectorLN(const VectorLN& other) :
	data_(new(std::nothrow) uint32_t[other.capacity_]), size_(other.size_), capacity_(other.capacity_)
{
	if (data_ == nullptr)
		throw MemoryAllocationError("Failed to allocate memory while copying: ");
	copy(other.data_, other.data_ + other.size_, data_);
}

VectorLN& VectorLN::operator=(const VectorLN& other)
{
	if (this == &other)
	{
		return *this;
	}
	auto* new_data = new (std::nothrow) uint32_t[other.capacity_];
	if (new_data == nullptr)
		throw MemoryAllocationError("Failed to allocate memory while copying: ");
	copy(other.data_, other.data_ + other.size_, new_data);

	delete[] data_;
	data_ = new_data;
	size_ = other.size_;
	capacity_ = other.capacity_;

	return *this;
}

void VectorLN::push_back(uint32_t value)
{
	if (size_ == capacity_)
	{
		increase_capacity();
	}
	data_[size_++] = value;
}

std::size_t VectorLN::size() const
{
	return size_;
}

uint32_t VectorLN::back() const
{
	if (size_ == 0)
	{
		throw std::runtime_error("Vector is empty");
	}
	return data_[size_ - 1];
}

void VectorLN::pop_back()
{
	if (size_ == 0)
	{
		throw std::runtime_error("Vector is empty");
	}
	size_--;
}

void VectorLN::resize(std::size_t new_size)
{
	if (new_size > capacity_)
	{
		auto* new_data = new (std::nothrow) uint32_t[new_size];
		if (new_data == nullptr)
			throw MemoryAllocationError("Memory reallocation failed: ");

		copy(data_, data_ + size_, new_data);
		delete[] data_;
		data_ = new_data;
		capacity_ = new_size;
	}

	size_ = new_size;
}

uint32_t& VectorLN::operator[](std::size_t index)
{
	if (index >= size_)
	{
		throw std::out_of_range("Index out of range");
	}
	return data_[index];
}

const uint32_t& VectorLN::operator[](std::size_t index) const
{
	if (index >= size_)
	{
		throw std::out_of_range("Index out of range");
	}
	return data_[index];
}

void VectorLN::increase_capacity()
{
	std::size_t new_capacity = capacity_ == 0 ? 1 : capacity_ * 2;
	auto* new_data = new (std::nothrow) uint32_t[new_capacity];
	if (new_data == nullptr)
		throw MemoryAllocationError("Memory allocation failed: ");
	copy(data_, data_ + size_, new_data);
	delete[] data_;
	data_ = new_data;
	capacity_ = new_capacity;
}

void VectorLN::fill(uint32_t* first, uint32_t* last, uint32_t value)
{
	for (uint32_t* it = first; it != last; ++it)
	{
		*it = value;
	}
}

void VectorLN::copy(const uint32_t* first, const uint32_t* last, uint32_t* dest)
{
	for (; first != last; ++first, ++dest)
	{
		*dest = *first;
	}
}