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
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;


/**
 * Generates a chart, showing donations for a complete, calendar year, broken
 * down by type.
 *
 * @author Robin Rawson-Tetley
 */
public class MonthlyDonations extends Chart {
    private String year = "";
    private int selectedYear = 0;

    public MonthlyDonations() {
        String selyear = (String) Dialog.getYear(Global.i18n("charts",
                    "Which_year_is_this_graph_for?"));
        selectedYear = Integer.parseInt(selyear);

        // Set title flags
        year = selyear;

        this.start();
    }

    public String getTitle() {
        return Global.i18n("charts", "Monthly_Donation_analysis_(by_type)_for_") +
        year;
    }

    public boolean createGraph() throws Exception {
        setStatusBarMax(12);

        // Get a list of donation types used over this period
        SQLRecordset dt = new SQLRecordset();

        Calendar firstDayOfYear = Calendar.getInstance();
        firstDayOfYear.set(Calendar.YEAR, selectedYear);
        firstDayOfYear.set(Calendar.MONTH, 0);
        firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfYear = Calendar.getInstance();
        lastDayOfYear.set(Calendar.YEAR, selectedYear);
        lastDayOfYear.set(Calendar.MONTH, 11);
        lastDayOfYear.set(Calendar.DAY_OF_MONTH, 31);

        dt.openRecordset(
            "SELECT DISTINCT donationtype.ID, DonationName FROM donationtype " +
            "INNER JOIN ownerdonation ON ownerdonation.DonationTypeID = donationtype.ID " +
            "WHERE Date >= '" + Utils.getSQLDate(firstDayOfYear) + "' AND " +
            "Date <= '" + Utils.getSQLDate(lastDayOfYear) + "'", "donationtype");

        if (dt.getEOF()) {
            return false;
        }

        String[] dtname = new String[(int) dt.getRecordCount()];
        double[] dtot = new double[(int) dt.getRecordCount()];

        // Outline model - 12 columns (Month, Year period)
        // 3 rows (Brought In, Adoption and OwnerDonation)
        int[][] model = new int[dtot.length][12];

        for (int i = 0; i < 12; i++) {
            // Calculate month boundaries
            Calendar firstDayOfMonth = Calendar.getInstance();
            firstDayOfMonth.set(Calendar.YEAR, selectedYear);
            firstDayOfMonth.set(Calendar.MONTH, i);
            firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            firstDayOfMonth.set(Calendar.HOUR, 0);
            firstDayOfMonth.set(Calendar.MINUTE, 0);
            firstDayOfMonth.set(Calendar.SECOND, 0);

            Calendar lastDayOfMonth = (Calendar) firstDayOfMonth.clone();
            lastDayOfMonth.add(Calendar.MONTH, 1);

            // Get SQL dates
            String firstDay = SQLRecordset.getSQLRepresentationOfDate(Utils.calendarToDate(
                        firstDayOfMonth));
            String lastDay = SQLRecordset.getSQLRepresentationOfDate(Utils.calendarToDate(
                        lastDayOfMonth));

            // Add up donations of type
            int col = 0;
            dt.moveFirst();

            while (!dt.getEOF()) {
                dtname[col] = dt.getField("DonationName").toString();
                model[col][i] = (int) DBConnection.executeForSum(
                        "SELECT SUM(Donation) / 100 AS Total FROM ownerdonation " +
                        "WHERE Date >= '" + firstDay + "' AND " + "Date < '" +
                        lastDay + "' AND " + "DonationTypeID = " +
                        dt.getField("ID"));
                dtot[col] += (double) model[col][i];
                col++;
                dt.moveNext();
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

        String[] rows = new String[dtot.length];

        for (int i = 0; i < dtot.length; i++) {
            rows[i] = dtname[i] + " (" + Double.toString(dtot[i]) + ")";
        }

        data = new ObjectChartDataModel(model, columns, rows);

        return checkModelIsNotZeroes(model, columns.length, rows.length);
    }
}
