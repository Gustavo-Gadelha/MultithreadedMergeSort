import java.util.stream.IntStream;

public class ArrayHelpers {
    private ArrayHelpers() {
    }

    public static int[] createArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            // Generate random numbers between 0 and size
            array[i] = (int) (Math.random() * size);
        }

        return array;
    }

    public static void mergeArray(int[] array, int start, int middle, int end) {
        // System.out.printf("START: %d MIDDLE: %d END: %d\n", start, middle, end);
        int[] temp = new int[end - start];
        int tempIndex = 0;

        int leftIndex = start;
        int rightIndex = middle;

        do {
            if (array[leftIndex] < array[rightIndex]) {
                temp[tempIndex++] = array[leftIndex++];
            } else {
                temp[tempIndex++] = array[rightIndex++];
            }
        } while (leftIndex < middle && rightIndex < end);

        System.arraycopy(array, leftIndex, temp, tempIndex, middle - leftIndex);
        System.arraycopy(array, rightIndex, temp, tempIndex + middle - leftIndex, end - rightIndex);
        System.arraycopy(temp, 0, array, start, temp.length);
    }

    public static boolean isSorted(int[] array) {
        return IntStream.range(0, array.length - 1).noneMatch(i -> array[i] > array[i + 1]);
    }
}
