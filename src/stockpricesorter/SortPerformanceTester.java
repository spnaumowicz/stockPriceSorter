package stockpricesorter;

import java.util.*;
import java.util.function.Supplier;

public class SortPerformanceTester {
    private static final Map<String, Supplier<List<StockData>>> DATASET_SIZES = Map.of(
        "1Y", (Supplier<List<StockData>>) ArrayList::new,
        "5Y", (Supplier<List<StockData>>) ArrayList::new,
        "10Y", (Supplier<List<StockData>>) ArrayList::new
    );

    private static final Map<String, java.util.function.Function<List<StockData>, List<StockData>>> ORDERS = Map.of(
        "Random", StockPriceSorter::fullyRandomize,
        "NearlySorted", StockPriceSorter::partiallyRandomize,
        "Reverse", StockPriceSorter::reverseList
    );

    private static final Map<String, Comparator<StockData>> METRICS = Map.of(
        "gain", Comparator.comparingDouble(StockData::getGain),
        "percent", Comparator.comparingDouble(StockData::getPercentChange),
        "close", Comparator.comparingDouble(StockData::getClose)
    );

    private final StockPriceSorter.FilteredData data;

    public SortPerformanceTester(StockPriceSorter.FilteredData data) {
        this.data = Objects.requireNonNull(data, "FilteredData cannot be null");
    }

    public void runAllTests() {
        DATASET_SIZES.forEach((size, supplier) -> {
            List<StockData> baseList = getDataBySize(size);

            ORDERS.forEach((order, orderFunction) -> {
                List<StockData> orderedList = orderFunction.apply(cloneList(baseList));

                METRICS.forEach((metric, comparator) -> {
                    System.out.printf("===== %s | %s | Sorted by: %s =====%n", size, order, metric);

                    runAndPrintSort("QuickSort", () -> StockPriceSorter.runQuickSort(cloneList(orderedList)), metric);
                    runAndPrintSort("MergeSort", () -> StockPriceSorter.runMergeSort(cloneList(orderedList), metric), metric);
                    runAndPrintSort("HeapSort", () -> HeapSort.runHeapSort(cloneList(orderedList), comparator), metric);
                });
            });
        });
    }

    private void runAndPrintSort(String sortName, Supplier<SortResult> sortSupplier, String metric) {
        SortResult result = sortSupplier.get();
        SortResultsPrinter.printSummary(result, sortName, metric);
    }

    private List<StockData> getDataBySize(String size) {
        return switch (size) {
            case "1Y" -> new ArrayList<>(data.oneYearList);
            case "5Y" -> new ArrayList<>(data.fiveYearList);
            case "10Y" -> new ArrayList<>(data.tenYearList);
            default -> throw new IllegalArgumentException("Invalid dataset size: " + size);
        };
    }

    private static List<StockData> cloneList(List<StockData> original) {
        return original.stream()
            .map(sd -> new StockData(sd.getDate(), sd.getOpen(), sd.getClose()))
            .toList();
    }
}
