import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainSort {
    private static final int ARRAY_SIZE = (int) 8e6;
    private static final int TOTAL_THREADS = 8;
    private static final int TOTAL_TASKS = 8;
    private static final int TASK_RANGE = Math.ceilDiv(ARRAY_SIZE, TOTAL_TASKS);

    public static void main(String[] args) {
        int[] array = ArrayHelpers.createArray(ARRAY_SIZE);
        // System.out.println("Array before sorting: " + Arrays.toString(array));

        System.out.printf("SEGMENTS: %d TASK_RANGE: %d\n", TOTAL_TASKS, TASK_RANGE);
        long startTime = System.nanoTime();
        try (ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS)) {
            for (int index = 0; index < TOTAL_TASKS; index++) {
                int start = index * TASK_RANGE;
                int end = Math.min(start + TASK_RANGE, ARRAY_SIZE);

                if (start >= end) break;

                System.out.printf("SORT  (%d) SUBMITTED -> START: %d END: %d\n", index, start, end);
                executor.submit(() -> mergeSort(array, start, end));
            }
        }

        int remainingSegments = TOTAL_TASKS;
        int segmentRange = TASK_RANGE;

        while (remainingSegments > 1) {
            remainingSegments = Math.ceilDiv(remainingSegments, 2);
            segmentRange *= 2;

            try (ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS)) {
                System.out.printf("SEGMENTS: %d TASK_RANGE: %d\n", remainingSegments, segmentRange);
                for (int index = 0; index < remainingSegments; index++) {
                    int start = index * segmentRange;
                    int middle = start + segmentRange / 2;
                    int end = Math.min(start + segmentRange, ARRAY_SIZE);

                    if (middle >= end) break;

                    System.out.printf("MERGE (%d) SUBMITTED -> START: %d MIDDLE: %d END: %d\n", index, start, middle, end);
                    executor.submit(() -> ArrayHelpers.mergeArray(array, start, middle, end));
                }
            }
        }

        long endTime = System.nanoTime();
        // After all threads have completed sorting, print the sorted array
        // System.out.println("Array after sorting : " + Arrays.toString(array));
        System.out.printf("> Time: %dms\n", Math.divideExact((endTime - startTime), 1000000));

        if (ArrayHelpers.isSorted(array)) {
            System.out.println("> ARRAY IS SORTED");
        } else {
            System.err.println("> ARRAY IS NOT SORTED");
        }
    }

    private static void mergeSort(int[] array, int start, int end) {
        // Recursion stops when the range contains only one element
        if (end - start <= 1) return;

        int middle = Math.floorDiv(end + start, 2);
        MainSort.mergeSort(array, start, middle);
        MainSort.mergeSort(array, middle, end);

        ArrayHelpers.mergeArray(array, start, middle, end);
    }
}