package stockpricesorter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import stockpricesorter.StockData;
import stockpricesorter.StockPriceSorter;

public class MergeSort {

    public static StockPriceSorter.SortResult runMergeSort(List<StockData> data, String metric) {
        List<StockData> list = new ArrayList<>(data); // Avoid modifying original data
        SortStats stats = new SortStats();
        long startTime = System.nanoTime();

        Comparator<StockData> comparator = getComparator(metric);
        mergeSort(list, 0, list.size() - 1, comparator, stats);

        long endTime = System.nanoTime();
        return new StockPriceSorter.SortResult(
                list,
                stats.getComparisons(),
                stats.getMoves(),
                (endTime - startTime) / 1_000_000
        );
    }

    private static void mergeSort(List<StockData> list, int left, int right,
                                  Comparator<StockData> comparator, SortStats stats) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid, comparator, stats);
            mergeSort(list, mid + 1, right, comparator, stats);
            merge(list, left, mid, right, comparator, stats);
        }
    }

    private static void merge(List<StockData> list, int left, int mid, int right,
                              Comparator<StockData> comparator, SortStats stats) {
        List<StockData> temp = new ArrayList<>(right - left + 1);
        int i = left, j = mid + 1;

        while (i <= mid && j <= right) {
            stats.incrementComparisons();
            if (comparator.compare(list.get(i), list.get(j)) <= 0) {
                temp.add(list.get(i++));
            } else {
                temp.add(list.get(j++));
            }
            stats.incrementMoves();
        }

        while (i <= mid) {
            temp.add(list.get(i++));
            stats.incrementMoves();
        }

        while (j <= right) {
            temp.add(list.get(j++));
            stats.incrementMoves();
        }

        for (int m = 0; m < temp.size(); m++) {
            list.set(left + m, temp.get(m));
        }
    }

    private static Comparator<StockData> getComparator(String metric) {
        return switch (metric.toLowerCase()) {
            case "gain" -> Comparator.comparingDouble(StockData::getGain);
            case "percent" -> Comparator.comparingDouble(StockData::getPercentChange);
            case "close" -> Comparator.comparingDouble(StockData::getClose);
            default -> throw new IllegalArgumentException("Invalid metric: " + metric);
        };
    }

    // Internal helper class to track comparisons and moves
    private static class SortStats {
        private int comparisons = 0;
        private int moves = 0;

        public void incrementComparisons() { comparisons++; }
        public void incrementMoves() { moves++; }
        public int getComparisons() { return comparisons; }
        public int getMoves() { return moves; }
    }
}
