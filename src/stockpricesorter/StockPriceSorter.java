/* Authors: Amen Adio, Joon Han, Bethany Keller, Stefan Naumowicz, David Rando */

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

public class StockPriceSorter {

    public static class FilteredData {
        public LinkedList<String[]> tenYearList = new LinkedList<>();
        public LinkedList<String[]> fiveYearList = new LinkedList<>();
        public LinkedList<String[]> oneYearList = new LinkedList<>();
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Stock Price Sorter.  This program will test three sorting algorithms - heap sort, quick sort, and merge sort - against datasets of various size to determine the best use case for each");
        String filePath = "S&P 500 Historical Data.csv";

        // Optional: Show where Java is looking for the file
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
            // Configure CSV parser to handle quoted commas correctly
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
                        continue; // Skip header
                    }

                    // Skip rows that are incomplete
                    if (row.length < 7 || row[0].trim().isEmpty()) {
                        continue; // Only skip if date is missing or row is malformed
                    }


                    // Parse and reformat date
                    Date parsedDate;
                    try {
                        parsedDate = inputFormat.parse(row[0].replace("\"", "").trim());
                    } catch (ParseException e) {
                        continue;
                    }

                    row[0] = outputFormat.format(parsedDate);  // Convert date to yyyymmdd format

                    result.tenYearList.add(row);

                    Date fiveYearsAgo = inputFormat.parse("03/28/2020");
                    Date oneYearAgo = inputFormat.parse("03/28/2024");

                    if (!parsedDate.before(fiveYearsAgo)) {
                        result.fiveYearList.add(row);
                    }

                    if (!parsedDate.before(oneYearAgo)) {
                        result.oneYearList.add(row);
                    }
                }

            }
        } catch (IOException | CsvValidationException | ParseException e) {
            System.err.println("Error reading/parsing the CSV: " + e.getMessage());
        }

        return result;
    }
}