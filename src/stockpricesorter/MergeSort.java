import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSort {

    public static SortResult runMergeSort(List<StockData> data, String metric) {
        List<StockData> list = new ArrayList<>(data); // Create copy to avoid mutating original
        SortStats stats = new SortStats();
        long startTime = System.nanoTime();

        Comparator<StockData> comparator = getComparator(metric);
        mergeSort(list, 0, list.size() - 1, comparator, stats);

        long endTime = System.nanoTime();
        return new SortResult(list, stats.comparisons, stats.moves, (endTime - startTime) / 1_000_000);
    }

    private static void mergeSort(List<StockData> list, int left, int right, Comparator<StockData> comparator, SortStats stats) {
        if (left < right) {
            int mid = left + (right - left) / 2; // Avoid integer overflow
            mergeSort(list, left, mid, comparator, stats);
            mergeSort(list, mid + 1, right, comparator, stats);
            merge(list, left, mid, right, comparator, stats);
        }
    }

    private static void merge(List<StockData> list, int left, int mid, int right, Comparator<StockData> comparator, SortStats stats) {
        List<StockData> temp = new ArrayList<>(right - left + 1);
        int i = left;
        int j = mid + 1;
        int k = 0;

        // Merge the two sublists into temp
        while (i <= mid && j <= right) {
            stats.comparisons++;
            if (comparator.compare(list.get(i), list.get(j)) <= 0) {
                temp.add(list.get(i++));
            } else {
                temp.add(list.get(j++));
            }
            stats.moves++;
        }

        // Copy remaining elements from left sublist
        while (i <= mid) {
            temp.add(list.get(i++));
            stats.moves++;
        }

        // Copy remaining elements from right sublist
        while (j <= right) {
            temp.add(list.get(j++));
            stats.moves++;
        }

        // Copy back to original list
        for (int m = 0; m < temp.size(); m++) {
            list.set(left + m, temp.get(m));
        }
    }

    private static Comparator<StockData> getComparator(String metric) {
        return switch (metric.toLowerCase()) {
            case "gain" -> Comparator.comparingDouble(StockData::getGain);
            case "percent" -> Comparator.comparingDouble(StockData::getPercentChange);
            case "close" -> Comparator.comparingDouble(StockData::getClose);
            default -> throw new IllegalArgumentException("Invalid metric: " + metric + ". Use 'gain', 'percent', or 'close'.");
        };
    }

    // Record to hold sorting statistics
    private static class SortStats {
        int comparisons = 0;
        int moves = 0; // Renamed from "swaps" to better reflect the operation
    }
}

// Assuming this is the SortResult class
record SortResult(List<StockData> sortedList, int comparisons, int moves, long timeMs) {}
