package stockpricesorter;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StockPriceSorter {

    public static class FilteredData {
        public LinkedList<StockData> tenYearList = new LinkedList<>();
        public LinkedList<StockData> fiveYearList = new LinkedList<>();
        public LinkedList<StockData> oneYearList = new LinkedList<>();
    }

    public static class SortResult {
        public List<StockData> sortedList;
        public int comparisons;
        public int swaps;
        public long runtimeMicros;

        public SortResult(List<StockData> sortedList, int comparisons, int swaps, long runtimeMicros) {
            this.sortedList = sortedList;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.runtimeMicros = runtimeMicros;
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Stock Price Sorter: Testing Heap, Merge, and Quick Sort with original, reversed, and randomized data.");
        String filePath = "S&P 500 Historical Data.csv";
        System.out.println("Looking for file at: " + new java.io.File(filePath).getAbsolutePath());

        FilteredData data = readAndFilterSP500Data(filePath);

        String[] metrics = {"gain", "percent", "close"};
        String[] datasets = {"tenYear", "fiveYear", "oneYear"};

        Map<String, DefaultCategoryDataset> charts = new HashMap<>();

        for (String metric : metrics) {
            for (String period : datasets) {
                List<StockData> original = getListCopy(data, period);
                List<StockData> reversed = reverseList(getListCopy(data, period));
                List<StockData> randomized = fullyRandomize(getListCopy(data, period));

                System.out.println("\n===== " + period.toUpperCase() + " / " + metric.toUpperCase() + " =====");
                System.out.printf("%-12s | %-11s | %-20s | %-10s | %-10s\n", "Sort Type", "Data Order", "Time (microseconds)", "Swaps", "Comparisons");
                System.out.println("---------------------------------------------------------------");

                // Original
                SortResult heapOrig = runHeapSortWithMicros(original, metric);
                SortResult mergeOrig = runMergeSortWithMicros(original, metric);
                SortResult quickOrig = runQuickSortWithMicros(original, metric);
                printTableRow("HeapSort", "Original", heapOrig);
                printTableRow("MergeSort", "Original", mergeOrig);
                printTableRow("QuickSort", "Original", quickOrig);

                // Add to chart
                String chartKey = period + ":" + metric;
                DefaultCategoryDataset chart = new DefaultCategoryDataset();
                chart.setValue(heapOrig.runtimeMicros / 1000.0, "HeapSort", period);
                chart.setValue(mergeOrig.runtimeMicros / 1000.0, "MergeSort", period);
                chart.setValue(quickOrig.runtimeMicros / 1000.0, "QuickSort", period);
                charts.put(chartKey, chart);

                // Reversed
                SortResult heapRev = runHeapSortWithMicros(reversed, metric);
                SortResult mergeRev = runMergeSortWithMicros(reversed, metric);
                SortResult quickRev = runQuickSortWithMicros(reversed, metric);
                printTableRow("HeapSort", "Reversed", heapRev);
                printTableRow("MergeSort", "Reversed", mergeRev);
                printTableRow("QuickSort", "Reversed", quickRev);

                // Randomized
                SortResult heapRand = runHeapSortWithMicros(randomized, metric);
                SortResult mergeRand = runMergeSortWithMicros(randomized, metric);
                SortResult quickRand = runQuickSortWithMicros(randomized, metric);
                printTableRow("HeapSort", "Randomized", heapRand);
                printTableRow("MergeSort", "Randomized", mergeRand);
                printTableRow("QuickSort", "Randomized", quickRand);
            }
        }

        // Display graphs for original data
        for (Map.Entry<String, DefaultCategoryDataset> entry : charts.entrySet()) {
            String key = entry.getKey();
            DefaultCategoryDataset dataset = entry.getValue();
            if (key.contains("gain")) {
                ResultsVisualizer.initUI(dataset);
            } else if (key.contains("percent")) {
                ResultsVisualizer.initUR(dataset);
            } else if (key.contains("close")) {
                ResultsVisualizer.initUV(dataset);
            }
        }
    }

    public static void printTableRow(String algorithm, String order, SortResult result) {
        double runtimeMicros = result.runtimeMicros;
        System.out.printf("%-12s | %-11s | %-20.3f | %-10d | %-10d\n",
                algorithm, order, runtimeMicros, result.swaps, result.comparisons);
    }

    public static List<StockData> getListCopy(FilteredData data, String period) {
        switch (period) {
            case "tenYear": return new LinkedList<>(data.tenYearList);
            case "fiveYear": return new LinkedList<>(data.fiveYearList);
            case "oneYear": return new LinkedList<>(data.oneYearList);
            default: return new LinkedList<>();
        }
    }

    public static FilteredData readAndFilterSP500Data(String filePath) {
        FilteredData result = new FilteredData();
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();
            try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build()) {
                String[] row;
                boolean isFirstRow = true;
                while ((row = reader.readNext()) != null) {
                    if (isFirstRow) { isFirstRow = false; continue; }
                    if (row.length < 7 || row[0].trim().isEmpty()) continue;

                    Date parsedDate;
                    try {
                        parsedDate = inputFormat.parse(row[0].replace("\"", "").trim());
                    } catch (ParseException e) { continue; }

                    row[0] = outputFormat.format(parsedDate);
                    try {
                        double open = Double.parseDouble(row[1].replace(",", ""));
                        double close = Double.parseDouble(row[4].replace(",", ""));
                        StockData stock = new StockData(row[0], open, close);
                        result.tenYearList.add(stock);

                        Date fiveYearsAgo = inputFormat.parse("03/28/2020");
                        Date oneYearAgo = inputFormat.parse("03/28/2024");

                        if (!parsedDate.before(fiveYearsAgo)) result.fiveYearList.add(stock);
                        if (!parsedDate.before(oneYearAgo)) result.oneYearList.add(stock);
                    } catch (NumberFormatException e) { continue; }
                }
            }
        } catch (IOException | CsvValidationException | ParseException e) {
            System.err.println("Error reading/parsing the CSV: " + e.getMessage());
        }

        return result;
    }

    public static List<StockData> fullyRandomize(List<StockData> data) {
        Collections.shuffle(data);
        return data;
    }

    public static List<StockData> reverseList(List<StockData> data) {
        Collections.reverse(data);
        return data;
    }

    public static SortResult runQuickSortWithMicros(List<StockData> data, String metric) {
        long start = System.nanoTime();
        SortResult result = QuickSort.runQuickSort(data, metric);
        long end = System.nanoTime();
        return new SortResult(result.sortedList, result.comparisons, result.swaps, (end - start) / 1000);
    }

    public static SortResult runMergeSortWithMicros(List<StockData> data, String metric) {
        long start = System.nanoTime();
        SortResult result = MergeSort.runMergeSort(data, metric);
        long end = System.nanoTime();
        return new SortResult(result.sortedList, result.comparisons, result.swaps, (end - start) / 1000);
    }

    public static SortResult runHeapSortWithMicros(List<StockData> data, String metric) {
        long start = System.nanoTime();
        SortResult result = HeapSort.runHeapSort(data, metric);
        long end = System.nanoTime();
        return new SortResult(result.sortedList, result.comparisons, result.swaps, (end - start) / 1000);
    }
}
