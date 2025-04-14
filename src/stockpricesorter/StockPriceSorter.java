package stockpricesorter;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
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
        public java.util.List<StockData> sortedList;
        public int comparisons;
        public int swaps;
        public long runtimeMicros;

        public SortResult(java.util.List<StockData> sortedList, int comparisons, int swaps, long runtimeMicros) {
            this.sortedList = sortedList;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.runtimeMicros = runtimeMicros;
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Stock Price Sorter: Testing Heap, Merge, and Quick Sort with reversed, randomized, and partially randomized data.");
        String filePath = "S&P 500 Historical Data.csv";
        System.out.println("Looking for file at: " + new java.io.File(filePath).getAbsolutePath());

        FilteredData data = readAndFilterSP500Data(filePath);

        String[] metrics = {"gain", "percent", "close"};
        String[] datasets = {"tenYear", "fiveYear", "oneYear"};
        String[] dataTypes = {"Reversed", "Randomized", "PartiallyRand"};

        Map<String, DefaultCategoryDataset> chartMap = new HashMap<>();

        for (String metric : metrics) {
            for (String period : datasets) {
                java.util.List<StockData> reversed = reverseList(getListCopy(data, period));
                java.util.List<StockData> randomized = fullyRandomize(getListCopy(data, period));
                java.util.List<StockData> partial = partiallyRandomize(getListCopy(data, period));

                System.out.println("\n===== " + period.toUpperCase() + " / " + metric.toUpperCase() + " =====");
                System.out.printf("%-12s | %-15s | %-20s | %-10s | %-10s\n", "Sort Type", "Data Order", "Time (microseconds)", "Swaps", "Comparisons");
                System.out.println("-------------------------------------------------------------------------------");

                for (String type : dataTypes) {
                    java.util.List<StockData> workingList;
                    switch (type) {
                        case "Reversed": workingList = reversed; break;
                        case "Randomized": workingList = randomized; break;
                        case "PartiallyRand": workingList = partial; break;
                        default: continue;
                    }

                    SortResult heap = runHeapSortWithMicros(new LinkedList<>(workingList), metric);
                    SortResult merge = runMergeSortWithMicros(new LinkedList<>(workingList), metric);
                    SortResult quick = runQuickSortWithMicros(new LinkedList<>(workingList), metric);

                    printTableRow("HeapSort", type, heap);
                    printTableRow("MergeSort", type, merge);
                    printTableRow("QuickSort", type, quick);

                    String label = period + "-" + metric + " (" + type + ")";
                    chartMap.computeIfAbsent("HeapSort", k -> new DefaultCategoryDataset()).addValue(heap.runtimeMicros, "HeapSort", label);
                    chartMap.computeIfAbsent("MergeSort", k -> new DefaultCategoryDataset()).addValue(merge.runtimeMicros, "MergeSort", label);
                    chartMap.computeIfAbsent("QuickSort", k -> new DefaultCategoryDataset()).addValue(quick.runtimeMicros, "QuickSort", label);
                }
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Sorting Algorithm Performance (All Data Types)",
                "Test Case", "Time (μs)",
                combineDatasets(chartMap), PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = lineChart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        plot.setRenderer(renderer);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        domainAxis.setMaximumCategoryLabelWidthRatio(0.8f);

        JFrame frame = new JFrame("Sorting Times by Algorithm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(lineChart));
        frame.pack();
        frame.setVisible(true);
    }

    public static DefaultCategoryDataset combineDatasets(Map<String, DefaultCategoryDataset> datasets) {
        DefaultCategoryDataset combined = new DefaultCategoryDataset();
        for (DefaultCategoryDataset ds : datasets.values()) {
            for (int row = 0; row < ds.getRowCount(); row++) {
                Comparable rowKey = ds.getRowKey(row);
                for (int col = 0; col < ds.getColumnCount(); col++) {
                    Comparable columnKey = ds.getColumnKey(col);
                    Number value = ds.getValue(rowKey, columnKey);
                    combined.addValue(value, rowKey, columnKey);
                }
            }
        }
        return combined;
    }

    public static void printTableRow(String algorithm, String order, SortResult result) {
        double runtimeMicros = result.runtimeMicros;
        System.out.printf("%-12s | %-15s | %-20.3f | %-10d | %-10d\n",
                algorithm, order, runtimeMicros, result.swaps, result.comparisons);
    }

    public static java.util.List<StockData> getListCopy(FilteredData data, String period) {
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

    public static java.util.List<StockData> fullyRandomize(java.util.List<StockData> data) {
        java.util.Collections.shuffle(data);
        return data;
    }

    public static java.util.List<StockData> partiallyRandomize(java.util.List<StockData> data) {
        int swapCount = data.size() / 4;
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < swapCount; i++) {
            int index1 = rand.nextInt(data.size());
            int index2 = rand.nextInt(data.size());
            java.util.Collections.swap(data, index1, index2);
        }
        return data;
    }

    public static java.util.List<StockData> reverseList(java.util.List<StockData> data) {
        java.util.Collections.reverse(data);
        return data;
    }

    public static SortResult runQuickSortWithMicros(java.util.List<StockData> data, String metric) {
        long start = System.nanoTime();
        SortResult result = QuickSort.runQuickSort(data, metric);
        long end = System.nanoTime();
        return new SortResult(result.sortedList, result.comparisons, result.swaps, (end - start) / 1000);
    }

    public static SortResult runMergeSortWithMicros(java.util.List<StockData> data, String metric) {
        long start = System.nanoTime();
        SortResult result = MergeSort.runMergeSort(data, metric);
        long end = System.nanoTime();
        return new SortResult(result.sortedList, result.comparisons, result.swaps, (end - start) / 1000);
    }

    public static SortResult runHeapSortWithMicros(java.util.List<StockData> data, String metric) {
        long start = System.nanoTime();
        SortResult result = HeapSort.runHeapSort(data, metric);
        long end = System.nanoTime();
        return new SortResult(result.sortedList, result.comparisons, result.swaps, (end - start) / 1000);
    }
}
