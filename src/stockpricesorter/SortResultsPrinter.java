package stockpricesorter;

public class SortResultsPrinter {

    // This is a simple output helper for printing sort results to the console
    public static void printSummary(SortPriceSorter.SortResult result, String algorithm, String metric) {
        System.out.println("=== Sort Summary ===");
        System.out.println("Algorithm      : " + algorithm);
        System.out.println("Sorted by      : " + metric);
        System.out.println("Comparisons    : " + result.comparisons);
        System.out.println("Moves          : " + result.swaps);
        System.out.println("Runtime (ms)   : " + result.runtimeMillis);
        System.out.println("====================\n");
    }

//This is just a starting version for the print helper. Feel free to build on this by adding options to preview the top 5 sorted entries or adapt it to later output to file/graph if needed.
}
