package Lab_1;

import java.util.Arrays;
import java.util.Scanner;

public class Task_D {
    private static int [] heap;
    private static int last_idx = 0;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        heap = new int[n];
        for (int i = 0; i < n; i++) {
            if (in.nextByte() == 0) {
                insert(in.nextInt());
            } else {
                extract();
            }
        }
        in.close();
    }

    private static void insert(int num) {
        last_idx++;
        if (heap.length == last_idx) {
            heap = Arrays.copyOf(heap, 2 * heap.length);
        }
        heap[last_idx] = num;
        siftUp(last_idx);
    }
    private static void extract() {
        System.out.println(heap[0]);
        heap[0] = heap[last_idx];
        last_idx--;
        siftDown(0);
    }

    private static void siftUp(int index) {
        int parentIndex = (index - 1) / 2; // узнаем индекс родителя
        int x = heap[index]; // берем элемент
        while (index > 0 && heap[parentIndex] < x) {// если родительский элемент меньше
            heap[index] = heap[parentIndex];// то меняем его местами с рассматриваемым
            index = parentIndex;
            parentIndex = (parentIndex - 1) / 2;// берем новый родительский индекс и повторяем сравнение элементов
        }
        heap[index] = x;// соохраняем результат
    }

    private static void siftDown(int index) {
        int largerChild;
        int top = heap[index];
        while (index < (last_idx + 1) / 2) {
            int leftChild = 2 * index + 1;  // вычисляем индексы в массиве для левого узла ребенка
            int rightChild = leftChild + 1; // и правого

            if (rightChild < last_idx + 1 && heap[leftChild] < heap[rightChild]) {
                largerChild = rightChild;
            }// вычисляем ребенка вершину с наибольшим числовым значением
            else {
                largerChild = leftChild;
            }

            if (top >= heap[largerChild]) {// если значение вершины больше или равно
                //значени его наибольшего ребенка
                break;// то выходим из метода
            }

            heap[index] = heap[largerChild];// заменяем вершину, большей дочерней вершиной
            index = largerChild; // текущий индекс переходит вниз
        }
        heap[index] = top; // задаем конечное местоположение для элемента
    }
}