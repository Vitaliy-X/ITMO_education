package search;

public class BinarySearchShift {
    public static void main(String[] args) {
        if (args.length == 1) {
            System.out.println(0);
            return;
        }
        int [] arr = new int[args.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(args[i]);
        }
        System.out.println(binarySearchShift(arr));
    }


    // Pre: forall i, j: (i < j -> arr[i] > arr[j]) << k // :NOTE: Описать что такое двиг
    // Post: R : arr[R] == max
    public static int binarySearchShift(int [] arr) {
        // Pre: (i < j -> arr[i] > arr[j]) << k
        int sum = 0, i = 0;
        // Post: (i < j -> arr[i] > arr[j]) << k && sum == 0 && i == 0

        // I: (i < j -> arr[i] > arr[j]) << k
        while (i < arr.length) {
            // Pre: I && i < arr.length
            if (arr[i] % 2 == 1) {
                // Pre: I && arr[i] - нечётный
                sum++;
                // Post: I && arr[i] - нечётный && sum' = sum + 1
            } else {
                // I && sum' = sum && arr[i] - чётный
            }
            // Post: I && sum - "кол-во нечетных чисел" == 0

            // Pre: I && sum - "кол-во нечетных чисел" == 0
            i++;
            // Post: I && sum - "кол-во нечетных чисел" == 0 && i' = i + 1
        }
        // Post: (i < j -> arr[i] > arr[j]) << k && i == arr.length && sum - "кол-во нечетных чисел" == 0

        // Pre: (i < j -> arr[i] > arr[j]) << k && sum - "кол-во нечетных чисел" == 0
        if (sum % 2 == 0) {
            // Pre: (i < j -> arr[i] > arr[j]) << k && "кол-во нечётных чисел - чётно"
            // Post: R : arr[R] == max
            return recursiveBinarySearchShift(arr, -1, arr.length-1);
        } else {
            // Pre: (i < j -> arr[i] > arr[j]) << k && "кол-во нечётных чисел - нечётно"
            // Post: R : arr[R] == max
            return interactiveBinarySearchShift(arr);
        }
    }


    // Pre: forall i, j: (i < j -> arr[i] > arr[j]) << k
    // Post: R : arr[R] == max
    public static int interactiveBinarySearchShift(int [] arr) {
        // Pre: (i < j -> arr[i] > arr[j]) << k
        int left = -1;
        // Post: left == -1 && (i < j -> arr[i] > arr[j]) << k

        // Pre: left == -1 && (i < j -> arr[i] > arr[j]) << k
        int right = arr.length-1;
        // Post: right == arr.length-1 && left = -1 && (i < j -> arr[i] > arr[j]) << k

        // I: (i < j) => (arr[i] > arr[j]) << k
        /*
        Т.к N(натуральные числа) э right и right с каждой итерацией уменьшается в 2 раза, последовательность
        ограничена снизу (max(0, left)) -> цикл не бесконечный.
         */
        while (right > left + 1) {
            // Pre: right > left + 1 && I
            int mid = left + (right - left)/2;
            // Post: mid == (left + right)/2 && right > left + 1 && I

            // Pre: mid == (left + right)/2 && right > left + 1 && I
            if (arr[mid] < arr[right]) {
                // Pre: arr[mid] < arr[right] && mid = (left + right) / 2 && I && right > left + 1
                left = mid;
                // Post: left == mid && I && arr[mid] < arr[right]
            } else {
                // Pre: arr[mid] > arr[right] && I
                right = mid;
                // Post: right == mid && I
            }
            // Post: I && right - left = (right'-left')/2
        }
        // Post: I && right = left + 1 && arr[right] - is MAX
        return right;
    }

    // Pre: forall i, j: (i < j -> arr[i] > arr[j]) << k && left >= 0 && size > right > left
    // Post: R : arr[R] == max && R == k
    /*
    Т.к N э right и right с каждой итерацией уменьшается в 2 раза, последовательность
    ограничена снизу (max(0, left)) -> цикл не бесконечный.
     */
    public static int recursiveBinarySearchShift(int [] arr, int left, int right) {
        // Pre: (i < j -> arr[i] > arr[j]) << k
        if (right > left + 1) {
            // Pre: (i <= j) -> (arr[i] >= arr[j]) && right > left + 1
            int mid = left + (right - left) / 2;
            // Post: mid == (left + right) / 2 && (i <= j) -> (arr[i] >= arr[j]) && right > left + 1
            if (arr[mid] < arr[right]) {
                // Pre: arr[mid] < arr[right] && mid == (left + right) / 2 && (i <= j) ->
                // (arr[i] >= arr[j]) && right > left + 1
                return recursiveBinarySearchShift(arr, mid, right);
                // Post: R : arr[R] == k, R - MIN
            } else {
                // Pred: arr[mid] <= x && mid = (left + right) / 2 && arr[left] > x && (i <= j) ->
                // (arr[i] >= arr[j]) && right > left + 1
                return recursiveBinarySearchShift(arr, left, mid);
                // Post: R : arr[R] == MAX, R == k
            }
        } else {
            // arr[left] > arr[right] && (i <= j) -> (arr[i] >= arr[j]) && right = left + 1
        }
        // Post: arr[right] == MAX, right == k
        return right;
    }
}