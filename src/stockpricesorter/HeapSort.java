import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import stockpricesorter.StockData;
import stockpricesorter.StockPriceSorter.SortResult;

public class HeapSort {

    public static SortResult runHeapSort(List<StockData> data, String metric) {
        List<StockData> list = new ArrayList<>(data); // Copy to avoid mutating original
        SortStats stats = new SortStats();
        long startTime = System.nanoTime();

        Comparator<StockData> comparator = getComparator(metric);
        int n = list.size();

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, comparator, stats);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            Collections.swap(list, 0, i);
            stats.moves++;
            heapify(list, i, 0, comparator, stats);
        }

        long endTime = System.nanoTime();
        return new SortResult(list, stats.getComparisons(), stats.getMoves(), (endTime - startTime) / 1_000_000);
    }

    private static void heapify(List<StockData> list, int size, int root, Comparator<StockData> comparator, SortStats stats) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < size) {
            stats.comparisons++;
            if (comparator.compare(list.get(left), list.get(largest)) > 0) {
                largest = left;
            }
        }

        if (right < size) {
            stats.comparisons++;
            if (comparator.compare(list.get(right), list.get(largest)) > 0) {
                largest = right;
            }
        }

        if (largest != root) {
            Collections.swap(list, root, largest);
            stats.moves++;
            heapify(list, size, largest, comparator, stats);
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

    private static class SortStats {
        int comparisons = 0;
        int moves = 0;

        int getComparisons() { return comparisons; }
        int getMoves() { return moves; }
    }
}
