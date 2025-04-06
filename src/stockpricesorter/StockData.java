package stockpricesorter;

public class StockData {
    private String date;
    private double open;
    private double close;
    private double gain;           // Absolute gain/loss (close - open)
    private double percentChange;  // ((close - open) / open) * 100

    public StockData(String date, double open, double close) {
        this.date = date;
        this.open = open;
        this.close = close;
        this.gain = close - open;
        this.percentChange = (open != 0) ? ((close - open) / open) * 100.0 : 0.0;
    }

    public String getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getGain() {
        return gain;
    }

    public double getPercentChange() {
        return percentChange;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Open: " + open + ", Close: " + close +
                ", Gain: " + gain + ", % Change: " + percentChange;
    }
}