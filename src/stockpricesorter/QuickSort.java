package stockpricesorter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import stockpricesorter.StockData;
import stockpricesorter.StockPriceSorter.SortResult;

public class QuickSort {

    public static SortResult runQuickSort(List<StockData> data, String metric) {
        List<StockData> list = new ArrayList<>(data); // Copy to avoid mutating original
        SortStats stats = new SortStats();
        long startTime = System.nanoTime();

        Comparator<StockData> comparator = getComparator(metric);
        quickSort(list, 0, list.size() - 1, comparator, stats);

        long endTime = System.nanoTime();
        return new SortResult(list, stats.comparisons, stats.moves, (endTime - startTime) / 1_000_000);
    }

    private static void quickSort(List<StockData> list, int low, int high, Comparator<StockData> comparator, SortStats stats) {
        if (low < high) {
            int partitionIndex = partition(list, low, high, comparator, stats);

            quickSort(list, low, partitionIndex - 1, comparator, stats);
            quickSort(list, partitionIndex + 1, high, comparator, stats);
        }
    }

    private static int partition(List<StockData> list, int low, int high, Comparator<StockData> comparator, SortStats stats) {
        // Using the rightmost element as pivot
        StockData pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            stats.comparisons++;
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;

                // Swap list[i] and list[j]
                StockData temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
                stats.moves++;
            }
        }

        // Swap list[i+1] and list[high] (or pivot)
        StockData temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        stats.moves++;

        return i + 1;
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
    }
}