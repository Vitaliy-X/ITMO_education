package search;

public class BinarySearch {
    // :NOTE: POST? PRED?
    // Pre: args[int i] && args.size() != 0
    // Post: R : arr[R] <= x, R - наименьший
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int [] arr = new int[args.length-1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(args[i+1]);
        }
        System.out.println(interactiveBinarySearch(arr, x));
//        System.out.println(recursiveBinarySearch(arr, x, -1, arr.length));
    }

    // :NOTE: forall i, j?
    // Pre: forall i, j: i <= j -> arr[i] >= arr[j]
    // Post: R : arr[R] <= x, R - наименьший
    public static int interactiveBinarySearch(int [] arr, int x) {
        // Pre: i <= j -> arr[i] >= arr[j]
        int left = -1;
        // Post: left == -1 && i <= j -> arr[i] >= arr[j]

        // Pre: left == -1 && i <= j -> arr[i] >= arr[j]
        int right = arr.length;
        // Post: right == arr.length && left = -1 && i <= j -> arr[i] >= arr[j]

        // I: arr[left] > x && arr[right] <= x && (i <= j) => (arr[i] >= arr[j])
        /*
        Т.к N(натуральные числа) э right и right с каждой итерацией уменьшается в 2 раза, последовательность
        ограничена снизу (max(0, left)) -> цикл не бесконечный.
         */
        while (right > left + 1) {
            // Pre: right > left + 1 && I
            int mid = left + (right - left)/2;
            // Post: mid == (left + right)/2 && right > left + 1 && I

            // Pre: mid == (left + right)/2 && right > left + 1 && I
            if (arr[mid] > x) {
                // Pre: arr[mid] > x && m = (l + r) / 2 && I && right > left + 1
                left = mid;
                // Post: left == mid && I && arr[right] <= x
            } else {
                // Pre: arr[mid] <= x && I
                right = mid;
                // Post: right == mid && I
            }
            // Post: I && right - left = (right'-left')/2
        }
        // Post: I && right = left + 1 && right - is MIN, because a[right-1] != x
        return right;
    }

    // :NOTE: pred && post left, right
    // Pre: forall i, j: i <= j -> arr[i] >= arr[j] && left >= 0 && size > right > left
    // Post: R : arr[R] <= x, R - наименьший
    /*
    Т.к N э right и right с каждой итерацией уменьшается в 2 раза, последовательность
    ограничена снизу (max(0, left)) -> цикл не бесконечный.
     */
    public static int recursiveBinarySearch(int [] arr, int x, int left, int right) {
        // Pre: arr[left] > x && arr[right] <= x && (i <= j) => (arr[i] >= arr[j])
        if (right > left + 1) {
            // Pre: arr[left] > x && arr[right] <= x && (i <= j) => (arr[i] >= arr[j]) && right > left + 1
            int mid = left + (right - left) / 2;
            // Post: mid == (l + r) / 2 && arr[left] > x && arr[right] <= x && (i <= j) =>
            // (arr[i] >= arr[j]) && right > left + 1
            if (arr[mid] > x) {
                // Pre: arr[mid] > x && mid == (left + right) / 2 && arr[right] <= x && (i <= j) =>
                // (arr[i] >= arr[j]) && right > left + 1
                return recursiveBinarySearch(arr, x, mid, right);
                // Post: R : a[R] <= x, R - MIN
            } else {
                // Pred: arr[mid] <= x && mid = (left + right) / 2 && arr[left] > x && (i <= j) =>
                // (arr[i] >= arr[j]) && right > left + 1
                return recursiveBinarySearch(arr, x, left, mid);
                // Post: R : a[R] <= x, R - MIN>
            }
        } else {
            // arr[left] > x && arr[right] <= x && (i <= j) => (arr[i] >= arr[j]) && right = left + 1
        }
        // arr[right] <= x, right - наименьший
        return right;
    }
}