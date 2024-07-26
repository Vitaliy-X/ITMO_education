#ifndef VECTORLN_H
#define VECTORLN_H

#include <cstddef>
#include <cstdint>

class VectorLN
{
  public:
	// Конструктор по умолчанию
	VectorLN();

	// Конструктор с заданным размером и значением
	VectorLN(std::size_t size, uint32_t value);

	// Деструктор
	~VectorLN();

	// Копирующий конструктор
	VectorLN(const VectorLN& other);

	// Оператор присваивания копированием
	VectorLN& operator=(const VectorLN& other);

	// Добавление элемента в конец вектора
	void push_back(uint32_t value);

	// Получение размера вектора
	[[nodiscard]] std::size_t size() const;

	// Получение последнего элемента вектора
	[[nodiscard]] uint32_t back() const;

	// Удаление последнего элемента вектора
	void pop_back();

	// Изменение размера вектора
	void resize(std::size_t new_size);

	// Получение элемента по индексу
	uint32_t& operator[](std::size_t index);

	// Получение элемента по индексу (константная версия)
	const uint32_t& operator[](std::size_t index) const;

  private:
	uint32_t* data_;
	std::size_t size_;
	std::size_t capacity_;

	void increase_capacity();
	static void fill(uint32_t* first, uint32_t* last, uint32_t value);
	static void copy(const uint32_t* first, const uint32_t* last, uint32_t* dest);
};
#endif	  // VECTORLN_H