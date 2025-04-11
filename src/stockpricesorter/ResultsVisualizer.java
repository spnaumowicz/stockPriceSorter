package stockpricesorter;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class ResultsVisualizer {
//gain dataset
public static void initUI(CategoryDataset dataset) {
//CategoryDataset dataset = createDataset();
JFreeChart gainChart = createChartGain(dataset);
JFrame frame = new JFrame("Algorithm Comparison");
frame.setSize(400, 400);

ChartPanel chartPanelGain = new ChartPanel(gainChart);
chartPanelGain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
chartPanelGain.setBackground(Color.white);
frame.getContentPane().add(chartPanelGain);

frame.setLocationRelativeTo(null);
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setVisible(true);
}
//percent dataset
public static void initUR(CategoryDataset dataset) {
	//CategoryDataset dataset = createDataset();
	JFreeChart percentChart = createChartPercent(dataset);
	JFrame frame = new JFrame("Algorithm Comparison");
	frame.setSize(400, 400);
	
	ChartPanel chartPanelPercent = new ChartPanel(percentChart);
	chartPanelPercent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	chartPanelPercent.setBackground(Color.white);
	frame.getContentPane().add(chartPanelPercent);

	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
	}
//close dataset
public static void initUV(CategoryDataset dataset) {
	//CategoryDataset dataset = createDataset();
	JFreeChart closeChart = createChartClose(dataset);
	JFrame frame = new JFrame("Algorithm Comparison");
	frame.setSize(400, 400);
	
	ChartPanel chartPanelClose = new ChartPanel(closeChart);
	chartPanelClose.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	chartPanelClose.setBackground(Color.white);
	frame.getContentPane().add(chartPanelClose);

	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
	}


private static JFreeChart createChartGain(CategoryDataset dataset) {
return ChartFactory.createBarChart(
"Gain",
"",
"",
dataset,
PlotOrientation.VERTICAL,
false, true, false);
}

private static JFreeChart createChartPercent(CategoryDataset dataset) {
return ChartFactory.createBarChart(
"Percent",
"",
"",
dataset,
PlotOrientation.VERTICAL,
false, true, false);
}

private static JFreeChart createChartClose(CategoryDataset dataset) {
return ChartFactory.createBarChart(
"Close",
"",
"",
dataset,
PlotOrientation.VERTICAL,
false, true, false);
}


}

public class ResultsVisualizer {
    // Placeholder for future chart/graph visualizations
    // e.g., using JavaFX, JFreeChart
}
