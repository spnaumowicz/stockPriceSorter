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

import org.jfree.data.category.DefaultCategoryDataset;

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
        SortResult heapSortResultTenG = runHeapSort(data.tenYearList, "gain");
        SortResult heapSortResultFiveG = runHeapSort(data.fiveYearList, "gain");
        SortResult heapSortResultOneG = runHeapSort(data.oneYearList, "gain");
        
        SortResult heapSortResultTenP = runHeapSort(data.tenYearList, "percent");
        SortResult heapSortResultFiveP = runHeapSort(data.fiveYearList, "percent");
        SortResult heapSortResultOneP = runHeapSort(data.oneYearList, "percent");
        
        SortResult heapSortResultTenC = runHeapSort(data.tenYearList, "close");
        SortResult heapSortResultFiveC = runHeapSort(data.fiveYearList, "close");
        SortResult heapSortResultOneC = runHeapSort(data.oneYearList, "close");
        
        SortResult mergeSortResultTenG = runMergeSort(data.tenYearList, "gain");
        SortResult mergeSortResultFiveG = runMergeSort(data.fiveYearList, "gain");
        SortResult mergeSortResultOneG = runMergeSort(data.oneYearList, "gain");
        
        SortResult mergeSortResultTenP = runMergeSort(data.tenYearList, "percent");
        SortResult mergeSortResultFiveP = runMergeSort(data.fiveYearList, "percent");
        SortResult mergeSortResultOneP = runMergeSort(data.oneYearList, "percent");
        
        SortResult mergeSortResultTenC = runMergeSort(data.tenYearList, "close");
        SortResult mergeSortResultFiveC = runMergeSort(data.fiveYearList, "close");
        SortResult mergeSortResultOneC = runMergeSort(data.oneYearList, "close");
        
        SortResult quickSortResultTenG= runQuickSort(data.tenYearList, "gain");
        SortResult quickSortResultFiveG = runQuickSort(data.fiveYearList, "gain");
        SortResult quickSortResultOneG = runQuickSort(data.oneYearList, "gain");
        
        SortResult quickSortResultTenP = runQuickSort(data.tenYearList, "percent");
        SortResult quickSortResultFiveP = runQuickSort(data.fiveYearList, "percent");
        SortResult quickSortResultOneP = runQuickSort(data.oneYearList, "percent");
        
        SortResult quickSortResultTenC = runQuickSort(data.tenYearList, "close");
        SortResult quickSortResultFiveC = runQuickSort(data.fiveYearList, "close");
        SortResult quickSortResultOneC = runQuickSort(data.oneYearList, "close");
        
        // =========================
        // ===== HeapSort Results =====
        // =========================

        // HeapSort "gain"
        SortResultsPrinter.printSummary(heapSortResultTenG, "HeapSort", "gain");
        SortResultsPrinter.printSummary(heapSortResultFiveG, "HeapSort", "gain");
        SortResultsPrinter.printSummary(heapSortResultOneG, "HeapSort", "gain");

        // HeapSort "percent"
        SortResultsPrinter.printSummary(heapSortResultTenP, "HeapSort", "percent");
        SortResultsPrinter.printSummary(heapSortResultFiveP, "HeapSort", "percent");
        SortResultsPrinter.printSummary(heapSortResultOneP, "HeapSort", "percent");

        // HeapSort "close"
        SortResultsPrinter.printSummary(heapSortResultTenC, "HeapSort", "close");
        SortResultsPrinter.printSummary(heapSortResultFiveC, "HeapSort", "close");
        SortResultsPrinter.printSummary(heapSortResultOneC, "HeapSort", "close");


        // ==========================
        // ===== MergeSort Results =====
        // ==========================

        // MergeSort "gain"
        SortResultsPrinter.printSummary(mergeSortResultTenG, "MergeSort", "gain");
        SortResultsPrinter.printSummary(mergeSortResultFiveG, "MergeSort", "gain");
        SortResultsPrinter.printSummary(mergeSortResultOneG, "MergeSort", "gain");

        // MergeSort "percent"
        SortResultsPrinter.printSummary(mergeSortResultTenP, "MergeSort", "percent");
        SortResultsPrinter.printSummary(mergeSortResultFiveP, "MergeSort", "percent");
        SortResultsPrinter.printSummary(mergeSortResultOneP, "MergeSort", "percent");

        // MergeSort "close"
        SortResultsPrinter.printSummary(mergeSortResultTenC, "MergeSort", "close");
        SortResultsPrinter.printSummary(mergeSortResultFiveC, "MergeSort", "close");
        SortResultsPrinter.printSummary(mergeSortResultOneC, "MergeSort", "close");


        // ==========================
        // ===== QuickSort Results =====
        // ==========================

        // QuickSort "gain"
        SortResultsPrinter.printSummary(quickSortResultTenG, "QuickSort", "gain");
        SortResultsPrinter.printSummary(quickSortResultFiveG, "QuickSort", "gain");
        SortResultsPrinter.printSummary(quickSortResultOneG, "QuickSort", "gain");

        // QuickSort "percent"
        SortResultsPrinter.printSummary(quickSortResultTenP, "QuickSort", "percent");
        SortResultsPrinter.printSummary(quickSortResultFiveP, "QuickSort", "percent");
        SortResultsPrinter.printSummary(quickSortResultOneP, "QuickSort", "percent");

        // QuickSort "close"
        SortResultsPrinter.printSummary(quickSortResultTenC, "QuickSort", "close");
        SortResultsPrinter.printSummary(quickSortResultFiveC, "QuickSort", "close");
        SortResultsPrinter.printSummary(quickSortResultOneC, "QuickSort", "close");

        System.out.println("Data Processed and Cleaned - 3 lists generated with dates in yyyyMMdd format and records with missing values omitted:");
        System.out.println("10-year list size: " + data.tenYearList.size());
        System.out.println("5-year list size: " + data.fiveYearList.size());
        System.out.println("1-year list size: " + data.oneYearList.size());
        

        //gain datasets
        DefaultCategoryDataset oneYearDatasetGain = new DefaultCategoryDataset();
        oneYearDatasetGain.setValue(heapSortResultOneG.runtimeMillis, "One Year", "HeapSort");
        oneYearDatasetGain.setValue(mergeSortResultOneG.runtimeMillis, "One Year", "MergeSort ");
        oneYearDatasetGain.setValue(quickSortResultOneG.runtimeMillis, "One Year", "QuickSort");
        
        DefaultCategoryDataset fiveYearDatasetGain = new DefaultCategoryDataset();
        fiveYearDatasetGain.setValue(heapSortResultFiveG.runtimeMillis, "Five Year", "HeapSort");
        fiveYearDatasetGain.setValue(mergeSortResultFiveG.runtimeMillis, "Five Year", "MergeSort");
        fiveYearDatasetGain.setValue(quickSortResultFiveG.runtimeMillis, "Five Year", "QuickSort");
        
         DefaultCategoryDataset tenYearDatasetGain = new DefaultCategoryDataset();
        tenYearDatasetGain.setValue(heapSortResultTenG.runtimeMillis, "Ten Year", "HeapSort");
        tenYearDatasetGain.setValue(mergeSortResultTenG.runtimeMillis, "Ten Year", "MergeSort");
        tenYearDatasetGain.setValue(quickSortResultTenG.runtimeMillis, "Ten Year", "QuickSort");
        
        //percent datasets
        DefaultCategoryDataset oneYearDatasetPercent = new DefaultCategoryDataset();
        oneYearDatasetPercent.setValue(heapSortResultOneP.runtimeMillis, "One Year", "HeapSort");
        oneYearDatasetPercent.setValue(mergeSortResultOneP.runtimeMillis, "One Year", "MergeSort ");
        oneYearDatasetPercent.setValue(quickSortResultOneP.runtimeMillis, "One Year", "QuickSort");
        
        DefaultCategoryDataset fiveYearDatasetPercent = new DefaultCategoryDataset();
        fiveYearDatasetPercent.setValue(heapSortResultFiveP.runtimeMillis, "Five Year", "HeapSort");
        fiveYearDatasetPercent.setValue(mergeSortResultFiveP.runtimeMillis, "Five Year", "MergeSort");
        fiveYearDatasetPercent.setValue(quickSortResultFiveP.runtimeMillis, "Five Year", "QuickSort");
        
        DefaultCategoryDataset tenYearDatasetPercent = new DefaultCategoryDataset();
        tenYearDatasetPercent.setValue(heapSortResultTenP.runtimeMillis, "Ten Year", "HeapSort");
        tenYearDatasetPercent.setValue(mergeSortResultTenP.runtimeMillis, "Ten Year", "MergeSort");
        tenYearDatasetPercent.setValue(quickSortResultTenP.runtimeMillis, "Ten Year", "QuickSort");
        
        //close datasets
        DefaultCategoryDataset oneYearDatasetClose = new DefaultCategoryDataset();
        oneYearDatasetClose.setValue(heapSortResultOneC.runtimeMillis, "One Year", "HeapSort");
        oneYearDatasetClose.setValue(mergeSortResultOneC.runtimeMillis, "One Year", "MergeSort ");
        oneYearDatasetClose.setValue(quickSortResultOneC.runtimeMillis, "One Year", "QuickSort");
        
        DefaultCategoryDataset fiveYearDatasetClose = new DefaultCategoryDataset();
        fiveYearDatasetClose.setValue(heapSortResultFiveC.runtimeMillis, "Five Year", "HeapSort");
        fiveYearDatasetClose.setValue(mergeSortResultFiveC.runtimeMillis, "Five Year", "MergeSort");
        fiveYearDatasetClose.setValue(quickSortResultFiveC.runtimeMillis, "Five Year", "QuickSort");
        
        DefaultCategoryDataset tenYearDatasetClose = new DefaultCategoryDataset();
        tenYearDatasetClose.setValue(heapSortResultTenC.runtimeMillis, "Ten Year", "HeapSort");
        tenYearDatasetClose.setValue(mergeSortResultTenC.runtimeMillis, "Ten Year", "MergeSort");
        tenYearDatasetClose.setValue(quickSortResultTenC.runtimeMillis, "Ten Year", "QuickSort");
        
        //results visualizers for "gain", "percent", and "close"
        ResultsVisualizer.initUI(tenYearDatasetGain);
        ResultsVisualizer.initUI(oneYearDatasetGain);
        ResultsVisualizer.initUI(fiveYearDatasetGain);
        
        ResultsVisualizer.initUR(tenYearDatasetPercent);
        ResultsVisualizer.initUR(oneYearDatasetPercent);
        ResultsVisualizer.initUR(fiveYearDatasetPercent);
        
        ResultsVisualizer.initUV(tenYearDatasetClose);
        ResultsVisualizer.initUV(oneYearDatasetClose);
        ResultsVisualizer.initUV(fiveYearDatasetClose);
   
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
    public static SortResult runQuickSort(List<StockData> data, String metric) {
        // This method applies the quick sort algorithm to the list and returns performance data
//        int comparisons = 0;
//        int swaps = 0;
//        long startTime = System.currentTimeMillis();
//
//        // Sorting logic goes here
//
//        long endTime = System.currentTimeMillis();
        return QuickSort.runQuickSort(data, metric);
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
    public static SortResult runHeapSort(List<StockData> data, String metric) {
        // This method uses the HeapSort class to sort the StockData list
        // Specify the comparator based on the metric you want (e.g., gain, percent change, or close)
       // Comparator<StockData> comparator = Comparator.comparingDouble(StockData::getGain);  // Example: Sorting by gain

        // Call the runHeapSort method from HeapSort.java
        return HeapSort.runHeapSort(data, metric);  // The HeapSort class handles sorting and returns the result
    }

    public static void displayResults(SortResult result) {
        // This method will display the contents of the SortResult object including comparisons, swaps, and runtime
        System.out.println("Sorted List Size: " + result.sortedList.size());
        System.out.println("Comparisons: " + result.comparisons);
        System.out.println("Swaps: " + result.swaps);
        System.out.println("Runtime (ms): " + result.runtimeMillis);
    }
}