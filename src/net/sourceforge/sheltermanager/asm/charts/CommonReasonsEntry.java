/*
 Animal Shelter Manager
 Copyright(c)2000-2009, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.EntryReason;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;


/**
 * Generates a chart, showing the most common reasons of entry over a year.
 *
 * @author Robin Rawson-Tetley
 */
public class CommonReasonsEntry extends Chart {
    private String monthname = "";
    private String year = "";
    private int selectedYear = 0;

    public CommonReasonsEntry() {
        String selyear = (String) Dialog.getYear(Global.i18n("charts",
                    "Which_year_is_this_graph_for?"));
        selectedYear = Integer.parseInt(selyear);

        // Set title flags
        year = selyear;

        this.start();
    }

    public String getTitle() {
        return Global.i18n("charts", "Animal_Entry_Reasons_for_") + year;
    }

    public boolean createGraph() throws Exception {
        // Outline model - 12 columns (Month, Year period)
        // rows = record count of entry reasons
        EntryReason er = new EntryReason();
        er.openRecordset("ID > 0 ORDER BY ReasonName");

        int[][] model = new int[(int) er.getRecordCount()][12];

        setStatusBarMax(12);

        for (int i = 0; i < 12; i++) {
            // Calculate month boundaries
            Calendar firstDayOfMonth = Calendar.getInstance();
            firstDayOfMonth.set(Calendar.YEAR, selectedYear);
            firstDayOfMonth.set(Calendar.MONTH, i);
            firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            firstDayOfMonth.set(Calendar.HOUR, 0);
            firstDayOfMonth.set(Calendar.MINUTE, 0);

            Calendar lastDayOfMonth = (Calendar) firstDayOfMonth.clone();
            lastDayOfMonth.add(Calendar.MONTH, 1);
            lastDayOfMonth.add(Calendar.DAY_OF_MONTH, -1);

            // Get SQL dates
            String firstDay = SQLRecordset.getSQLRepresentationOfDate(Utils.calendarToDate(
                        firstDayOfMonth));
            String lastDay = SQLRecordset.getSQLRepresentationOfDate(Utils.calendarToDate(
                        lastDayOfMonth));

            // Add up the figures
            er.moveFirst();

            int eri = 0;

            while (!er.getEOF()) {
                // Get the total figures
                SQLRecordset rs = new SQLRecordset();
                rs.openRecordset(
                    "SELECT COUNT(*) AS Tot FROM animal WHERE DateBroughtIn >= '" +
                    firstDay + "' AND " + "DateBroughtIn <= '" + lastDay +
                    "' AND EntryReasonID = " + er.getID(), "animal");

                try {
                    if (!rs.getEOF()) {
                        model[eri][i] = Integer.parseInt(rs.getField("Tot")
                                                           .toString());
                    }
                } catch (NumberFormatException e) {
                }

                er.moveNext();
                eri++;
                rs.free();
                rs = null;
            }

            incrementStatusBar();
        }

        String[] columns = {
                Global.i18n("charts", "Jan"), Global.i18n("charts", "Feb"),
                Global.i18n("charts", "Mar"), Global.i18n("charts", "Apr"),
                Global.i18n("charts", "May"), Global.i18n("charts", "Jun"),
                Global.i18n("charts", "Jul"), Global.i18n("charts", "Aug"),
                Global.i18n("charts", "Sep"), Global.i18n("charts", "Oct"),
                Global.i18n("charts", "Nov"), Global.i18n("charts", "Dec")
            };
        int eri = 0;
        er.moveFirst();

        String[] rows = new String[(int) er.getRecordCount()];

        while (!er.getEOF()) {
            rows[eri] = er.getReasonName();
            er.moveNext();
            eri++;
        }

        data = new ObjectChartDataModel(model, columns, rows);

        return checkModelIsNotZeroes(model, columns.length, rows.length);
    }
}
