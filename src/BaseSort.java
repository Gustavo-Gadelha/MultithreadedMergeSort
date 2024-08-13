import java.util.Arrays;
import java.util.Objects;

public class BaseSort {
    private static final int ARRAY_SIZE = (int) 8e6;
    private static final int TOTAL_THREADS = 8;

    public static void main(String[] args) {
        int[] array = ArrayHelpers.createArray(ARRAY_SIZE);
        // System.out.println("Array before sorting: " + Arrays.toString(array));

        Thread[] threads = new Thread[TOTAL_THREADS];
        int segmentSize = Math.ceilDiv(ARRAY_SIZE, TOTAL_THREADS);

        long startTime = System.nanoTime();
        System.out.printf("SEGMENTS: %d SIZE: %d\n", TOTAL_THREADS, segmentSize);
        for (int i = 0; i < TOTAL_THREADS; i++) {
            int start = i * segmentSize;
            int end = Math.min(start + segmentSize, ARRAY_SIZE);

            if (start < end) {
                System.out.printf("starting thread (%d) -> ", i);
                System.out.printf("START: %d END: %d\n", start, end);
                threads[i] = new Thread(() -> mergeSort(array, start, end));
                threads[i].start();
            }
        }

        // Wait for all threads to complete
        joinThreads(threads);

        int sortedSegments = (int) Arrays.stream(threads).filter(Objects::nonNull).count();
        mergeSegments(array, sortedSegments, segmentSize);

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

    private static void mergeSegments(int[] array, int sortedSegments, int segmentSize) {
        int remainingSegments = sortedSegments;
        int currentSize = segmentSize;

        while (remainingSegments > 1) {
            remainingSegments = Math.ceilDiv(remainingSegments, 2);
            currentSize *= 2;

            System.out.printf("SEGMENTS: %d SIZE: %d\n", remainingSegments, currentSize);
            for (int i = 0; i < remainingSegments; i++) {
                int start = i * currentSize;
                int middle = start + currentSize / 2;
                int end = Math.min(start + currentSize, array.length);

                if (middle >= end) break;
                System.out.printf("merging segment (%d) -> ", i);
                System.out.printf("START: %d MIDDLE: %d END: %d\n", start, middle, end);
                ArrayHelpers.mergeArray(array, start, middle, end);
            }
        }
    }

    private static void mergeSort(int[] array, int start, int end) {
        // Recursion stops when the range contains only one element
        if (end - start <= 1) return;
        int middle = Math.floorDiv(end + start, 2);
        mergeSort(array, start, middle);
        mergeSort(array, middle, end);
        ArrayHelpers.mergeArray(array, start, middle, end);
    }

    private static void joinThreads(Thread[] threads) {
        Arrays.stream(threads).filter(Objects::nonNull).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}