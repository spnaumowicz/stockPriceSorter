package stockpricesorter;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import stockpricesorter.StockData;
import stockpricesorter.MergeSort;

public class StockPriceSorter {

    public static class FilteredData {
        // Original version
        // public LinkedList<String[]> tenYearList = new LinkedList<>();
        // public LinkedList<String[]> fiveYearList = new LinkedList<>();
        // public LinkedList<String[]> oneYearList = new LinkedList<>();

        // Updated version using StockData
        public LinkedList<StockData> tenYearList = new LinkedList<>();
        public LinkedList<StockData> fiveYearList = new LinkedList<>();
        public LinkedList<StockData> oneYearList = new LinkedList<>();
    }

    public static class SortResult {
        // Original version
        // public LinkedList<String[]> sortedList;
        // public SortResult(LinkedList<String[]> sortedList, int comparisons, int swaps, long runtimeMillis) {
        //     this.sortedList = sortedList;
        //     this.comparisons = comparisons;
        //     this.swaps = swaps;
        //     this.runtimeMillis = runtimeMillis;
        // }

        // Updated version using StockData
        public List<StockData> sortedList;
        public int comparisons;
        public int swaps;
        public long runtimeMillis;

        public SortResult(List<StockData> sortedList, int comparisons, int swaps, long runtimeMillis) {
            this.sortedList = sortedList;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.runtimeMillis = runtimeMillis;
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Stock Price Sorter. This program will test three sorting algorithms: heap sort, quick sort, and merge sort, against datasets of various sizes to determine the best one.");
        String filePath = "S&P 500 Historical Data.csv";

        System.out.println("Looking for file at: " + new java.io.File(filePath).getAbsolutePath());

        FilteredData data = readAndFilterSP500Data(filePath);

        System.out.println("Data Processed and Cleaned - 3 lists generated with dates in yyyyMMdd format and records with missing values omitted:");
        System.out.println("10-year list size: " + data.tenYearList.size());
        System.out.println("5-year list size: " + data.fiveYearList.size());
        System.out.println("1-year list size: " + data.oneYearList.size());
    }

    public static FilteredData readAndFilterSP500Data(String filePath) {
        FilteredData result = new FilteredData();

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(false)
                    .build();

            try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(parser)
                    .build()) {

                String[] row;
                boolean isFirstRow = true;

                while ((row = reader.readNext()) != null) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue;
                    }

                    if (row.length < 7 || row[0].trim().isEmpty()) {
                        continue;
                    }

                    Date parsedDate;
                    try {
                        parsedDate = inputFormat.parse(row[0].replace("\"", "").trim());
                    } catch (ParseException e) {
                        continue;
                    }

                    row[0] = outputFormat.format(parsedDate);

                    // Original code using String[]
                    // result.tenYearList.add(row);
                    // Date fiveYearsAgo = inputFormat.parse("03/28/2020");
                    // Date oneYearAgo = inputFormat.parse("03/28/2024");
                    // if (!parsedDate.before(fiveYearsAgo)) {
                    //     result.fiveYearList.add(row);
                    // }
                    // if (!parsedDate.before(oneYearAgo)) {
                    //     result.oneYearList.add(row);
                    // }

                    // Updated version using StockData
                    try {
                        double open = Double.parseDouble(row[1].replace(",", ""));
                        double close = Double.parseDouble(row[4].replace(",", ""));
                        StockData stock = new StockData(row[0], open, close);

                        result.tenYearList.add(stock);

                        Date fiveYearsAgo = inputFormat.parse("03/28/2020");
                        Date oneYearAgo = inputFormat.parse("03/28/2024");

                        if (!parsedDate.before(fiveYearsAgo)) {
                            result.fiveYearList.add(stock);
                        }

                        if (!parsedDate.before(oneYearAgo)) {
                            result.oneYearList.add(stock);
                        }

                    } catch (NumberFormatException e) {
                        continue;
                    }
                }

            }
        } catch (IOException | CsvValidationException | ParseException e) {
            System.err.println("Error reading/parsing the CSV: " + e.getMessage());
        }

        return result;
    }

    // Original versions using String[]
    // public static LinkedList<String[]> fullyRandomize(LinkedList<String[]> data) {
    //     Collections.shuffle(data);
    //     return data;
    // }

    // public static LinkedList<String[]> partiallyRandomize(LinkedList<String[]> data) {
    //     int swapCount = data.size() / 4;
    //     Random rand = new Random();
    //     for (int i = 0; i < swapCount; i++) {
    //         int index1 = rand.nextInt(data.size());
    //         int index2 = rand.nextInt(data.size());
    //         Collections.swap(data, index1, index2);
    //     }
    //     return data;
    // }

    // public static LinkedList<String[]> reverseList(LinkedList<String[]> data) {
    //     Collections.reverse(data);
    //     return data;
    // }

    // Updated versions using StockData
    public static List<StockData> fullyRandomize(List<StockData> data) {
        // This method completely randomizes the order of the linked list
        Collections.shuffle(data);
        return data;
    }

    public static List<StockData> partiallyRandomize(List<StockData> data) {
        // This method swaps 25% of the elements in the linked list to partially randomize it
        int swapCount = data.size() / 4;
        Random rand = new Random();
        for (int i = 0; i < swapCount; i++) {
            int index1 = rand.nextInt(data.size());
            int index2 = rand.nextInt(data.size());
            Collections.swap(data, index1, index2);
        }
        return data;
    }

    public static List<StockData> reverseList(List<StockData> data) {
        // This method reverses the order of elements in the linked list
        Collections.reverse(data);
        return data;
    }

    // Sorting method placeholders

    // Original QuickSort placeholder
    // public static SortResult runQuickSort(LinkedList<String[]> data) {
    //     // This method applies the quick sort algorithm to the linked list and returns performance data
    //     int comparisons = 0;
    //     int swaps = 0;
    //     long startTime = System.currentTimeMillis();
    //
    //     // Sorting logic goes here
    //
    //     long endTime = System.currentTimeMillis();
    //     return new SortResult(data, comparisons, swaps, endTime - startTime);
    // }

    // Original MergeSort placeholder
    // public static SortResult runMergeSort(LinkedList<String[]> data) {
    //     // This method applies the merge sort algorithm to the linked list and returns performance data
    //     int comparisons = 0;
    //     int swaps = 0;
    //     long startTime = System.currentTimeMillis();
    //
    //     // Sorting logic goes here
    //
    //     long endTime = System.currentTimeMillis();
    //     return new SortResult(data, comparisons, swaps, endTime - startTime);
    // }

    // Original HeapSort placeholder
    // public static SortResult runHeapSort(LinkedList<String[]> data) {
    //     // This method applies the heap sort algorithm to the linked list and returns performance data
    //     int comparisons = 0;
    //     int swaps = 0;
    //     long startTime = System.currentTimeMillis();
    //
    //     // Sorting logic goes here
    //
    //     long endTime = System.currentTimeMillis();
    //     return new SortResult(data, comparisons, swaps, endTime - startTime);
    // }

    // Updated QuickSort
    public static SortResult runQuickSort(List<StockData> data) {
        // This method applies the quick sort algorithm to the list and returns performance data
        int comparisons = 0;
        int swaps = 0;
        long startTime = System.currentTimeMillis();

        // Sorting logic goes here

        long endTime = System.currentTimeMillis();
        return new SortResult(data, comparisons, swaps, endTime - startTime);
    }

    // Updated MergeSort
    public static SortResult runMergeSort(List<StockData> data, String metric) {
        // This method applies the merge sort algorithm to the list and returns performance data
        //int comparisons = 0;
        //int swaps = 0;
        //long startTime = System.currentTimeMillis();

        //New Code using MergeSort.java
        return MergeSort.runMergeSort(data, metric);
        //NOTE: we can call it by
        //SortResult result = runMergeSort(data.tenYearList, "gain");
        //displayResults(result);

        

        // Sorting logic goes here

        //long endTime = System.currentTimeMillis();
        //return new SortResult(data, comparisons, swaps, endTime - startTime);
    }

    // Updated HeapSort
    public static SortResult runHeapSort(List<StockData> data) {
        // This method applies the heap sort algorithm to the list and returns performance data
        int comparisons = 0;
        int swaps = 0;
        long startTime = System.currentTimeMillis();

        // Sorting logic goes here

        long endTime = System.currentTimeMillis();
        return new SortResult(data, comparisons, swaps, endTime - startTime);
    }

    public static void displayResults(SortResult result) {
        // This method will display the contents of the SortResult object including comparisons, swaps, and runtime
        System.out.println("Sorted List Size: " + result.sortedList.size());
        System.out.println("Comparisons: " + result.comparisons);
        System.out.println("Swaps: " + result.swaps);
        System.out.println("Runtime (ms): " + result.runtimeMillis);
    }
}
