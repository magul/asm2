/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import de.progra.charting.model.ObjectChartDataModel;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;


/**
 * Generates a custom chart from a SQL query:
 * Column 1 is the X axis name
 * Column 2... are the items within each X axis
 *             Field names of columns are used for row names
 *
 * @author Robin Rawson-Tetley
 */
public class CustomChart extends Chart {
    private String sql = null;
    private String title = null;

    public CustomChart(String sql, String title) {
        this.sql = sql;
        this.title = title;
        this.start();
    }

    public String getTitle() {
        return title;
    }

    public boolean createGraph() throws Exception {
        // Run the SQL
        SQLRecordset r = new SQLRecordset();
        r.openRecordset(sql, "animal");

        // Bail if no records
        if (r.getRecordCount() == 0) {
            return false;
        }

        // Row and column count
        int cols = (int) r.getRecordCount();
        int rows = r.getFieldCount() - 1; // (don't include first column as that's X label)
        double[][] model = new double[rows][cols];
        String[] columns = new String[cols];
        String[] rownames = new String[rows];

        // Fill the model
        int c = 0;

        while (!r.getEOF()) {
            // Column values
            for (int i = 2; i <= r.getFieldCount(); i++) {
                Object v = r.getField(r.getFieldName(i));
                double dv = 0;

                try {
                    dv = Double.parseDouble(v.toString());
                } catch (Exception e) {
                }

                model[i - 2][c] = dv;
                Global.logDebug("model[" + (i - 2) + "][" + c + "] = " + dv,
                    "CustomChart.createGraph");
            }

            // X Label
            columns[c] = r.getField(r.getFieldName(1)).toString();

            r.moveNext();
            c++;
        }

        // Grab the row names
        for (int i = 2; i <= r.getFieldCount(); i++) {
            rownames[i - 2] = r.getFieldName(i);
            Global.logDebug("rownames[" + (i - 2) + "] = " + r.getFieldName(i),
                "CustomChart.createGraph");
        }

        data = new ObjectChartDataModel(model, columns, rownames);

        return checkModelForVariance(model, columns.length, rownames.length);
    }
}
