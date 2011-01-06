/*
 Animal Shelter Manager
 Copyright(c)2000-2010, R. Rawson-Tetley

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTIBILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston
 MA 02111-1307, USA.

 Contact me by electronic mail: bobintetley@users.sourceforge.net
 */
package net.sourceforge.sheltermanager.asm.charts;

import java.io.File;
import java.io.FileOutputStream;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.reportviewer.ReportViewer;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import de.progra.charting.ChartEncoder;
import de.progra.charting.DefaultChart;
import de.progra.charting.model.ObjectChartDataModel;
import de.progra.charting.render.BarChartRenderer;
import de.progra.charting.render.InterpolationChartRenderer;
import de.progra.charting.render.LineChartRenderer;
import de.progra.charting.render.PieChartRenderer;
import de.progra.charting.render.PlotChartRenderer;


/**
 * Chart superclass for generating Charts.
 *
 * @author Robin Rawson-Tetley
 */
public class Chart extends Thread {
    String filename = "";
    protected ObjectChartDataModel data = null;

    public void run() {
        try {
            setStatusText(Global.i18n("charts", "Generating_Graph_-_") +
                getTitle() + "...");

            if (!createGraph()) {
                Dialog.showError(Global.i18n("charts",
                        "there_is_no_data_to_show_on_the_chart"));
                setStatusText("");

                return;
            }

            DefaultChart chart = null;
            String title = getTitle();

            setStatusText("");

            // Ask what kind of chart they would like to generate
            String charttype = (String) Dialog.getInput(Global.i18n("charts",
                        "What_type_of_graph_would_you_like_to_generate?"),
                    Global.i18n("charts", "Graph_Type"),
                    new String[] {
                        Global.i18n("charts", "Bar_Chart"),
                        Global.i18n("charts", "Line_Chart"),
                        Global.i18n("charts", "Plot_Chart")
                    }, Global.i18n("charts", "Bar_Chart"));

            if (charttype.startsWith(Global.i18n("charts", "Pie"))) {
                chart = new DefaultChart(data, title);
                chart.addChartRenderer(new PieChartRenderer(data), 1);

                // chart.addChartRenderer(new PieChartRenderer(data,
                // RowColorModel.getInstance(data)), 1);
            } else if (charttype.startsWith(Global.i18n("charts", "Bar"))) {
                chart = new DefaultChart(data, title,
                        DefaultChart.LINEAR_X_LINEAR_Y);
                chart.addChartRenderer(new BarChartRenderer(
                        chart.getCoordSystem(), data), 1);

                // chart.addChartRenderer(new
                // BarChartRenderer(chart.getCoordSystem(),
                // data,
                // RowColorModel.getInstance(data)), 1);
            } else if (charttype.startsWith(Global.i18n("charts", "Line"))) {
                chart = new DefaultChart(data, title,
                        DefaultChart.LINEAR_X_LINEAR_Y);
                chart.addChartRenderer(new LineChartRenderer(
                        chart.getCoordSystem(), data), 1);

                // chart.addChartRenderer(new
                // LineChartRenderer(chart.getCoordSystem(),
                // data,
                // RowColorModel.getInstance(data)), 1);
            } else if (charttype.startsWith(Global.i18n("charts", "Plot"))) {
                chart = new DefaultChart(data, title,
                        DefaultChart.LINEAR_X_LINEAR_Y);
                chart.addChartRenderer(new PlotChartRenderer(
                        chart.getCoordSystem(), data), 1);

                // chart.addChartRenderer(new
                // PlotChartRenderer(chart.getCoordSystem(),
                // data,
                // RowColorModel.getInstance(data)), 1);
            } else if (charttype.startsWith(Global.i18n("charts",
                            "Interpolation"))) {
                chart = new DefaultChart(data, title,
                        DefaultChart.LINEAR_X_LINEAR_Y);
                chart.addChartRenderer(new InterpolationChartRenderer(
                        chart.getCoordSystem(), data), 1);

                // chart.addChartRenderer(new
                // InterpolationChartRenderer(chart.getCoordSystem(),
                // data,
                // RowColorModel.getInstance(data)), 1);
            } else {
                throw new Exception(Global.i18n("charts", "Unknown_chart_type_'") +
                    charttype + "'");
            }

            setStatusText(Global.i18n("charts", "Rendering_Graph..."));
            chart.setBounds(new java.awt.Rectangle(0, 0, 800, 600));
            output(chart);
            setStatusText("");

            filename = null;
            data = null;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        } finally {
            resetStatusBar();
        }
    }

    /**
     * Override in subclass to generate the DefaultChart object return false for
     * zero data
     */
    public boolean createGraph() throws Exception {
        return false;
    }

    /** Override with the real title in your subclass */
    public String getTitle() {
        return "";
    }

    /**
     * Displays the chart to the user. Uses configuration settings to determine
     * which image viewer to use.
     */
    protected void output(DefaultChart c) throws Exception {
        File f = Utils.getNewTempFile("png");
        filename = f.getAbsolutePath();

        // Save the chart as a PNG file to the random filename
        ChartEncoder.createPNG(new FileOutputStream(f), c);

        // The write was successful - add an entry to
        // the cache.
        Global.localCache.addEntry(f.getName(),
            Global.i18n("charts", "Chart_-_") + getTitle());

        // See if the options say we are using our internal
        // browser to display the report
        if (Global.useInternalReportViewer) {
            // Make a quick HTML document to view the chart
            String fname = f.getName();
            String view = "<html><body bgcolor=\"white\"><img src=\"" + fname +
                "\"></body></html>";
            File html = Utils.getNewTempFile("html");
            Utils.writeFile(html.getAbsolutePath(), view.getBytes());

            ReportViewer rv = new ReportViewer(html.getAbsolutePath(),
                    getTitle());
            net.sourceforge.sheltermanager.asm.globals.Global.mainForm.addChild(rv);
        } else {
            FileTypeManager.shellExecute(filename);
        }
    }

    /**
     * Sets the maximum value on the status bar to be used with this report,
     * along with how often to update it.
     *
     * @param max
     *            The maximum status bar value
     */
    protected void setStatusBarMax(int max) {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.initStatusBarMax(max);
    }

    /**
     * Sets the status bar text while generating the report
     *
     * @param text
     *            The new status bar text
     */
    protected void setStatusText(String text) {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.setStatusText(text);
    }

    /**
     * Updates the value on the status bar to be used with this report.
     */
    protected void incrementStatusBar() {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.incrementStatusBar();
    }

    /** Resets the status bar after the report is done */
    protected void resetStatusBar() {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.resetStatusBar();
    }

    /** Checks that the model has something other than zeroes */
    protected boolean checkModelIsNotZeroes(int[][] model, int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int z = 0; z < cols; z++) {
                if (model[z][i] > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Checks that the model has some kind of variance (ie Doesn't contain all the same value) */
    protected boolean checkModelForVariance(double[][] model, int rows, int cols) {
        double firstval = model[0][0];

        for (int i = 0; i < rows; i++) {
            for (int z = 0; z < cols; z++) {
                if (model[z][i] != firstval) {
                    return true;
                }
            }
        }

        return false;
    }
}
