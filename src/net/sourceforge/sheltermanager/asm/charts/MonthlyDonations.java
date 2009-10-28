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

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
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
    private String monthname = "";
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
        // Outline model - 12 columns (Month, Year period)
        // 3 rows (Brought In, Adoption and OwnerDonation)
        int[][] model = new int[4][12];

        setStatusBarMax(12);

        double totBI = 0;
        double totAd = 0;
        double totOd = 0;
        double totAl = 0;
        double totalBI = 0;
        double totalAd = 0;
        double totalOd = 0;
        double totalAl = 0;

        for (int i = 0; i < 12; i++) {
            totBI = 0;
            totAd = 0;
            totOd = 0;
            totAl = 0;

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

            // Get the total figures
            totBI = DBConnection.executeForSum(
                    "SELECT SUM(AmountDonatedOnEntry) AS Total " +
                    "FROM animal WHERE DateBroughtIn >= '" + firstDay +
                    "' AND " + "DateBroughtIn <= '" + lastDay +
                    "' AND AmountDonatedOnEntry > 0");

            totAd = DBConnection.executeForSum("SELECT SUM(Donation) AS Total " +
                    "FROM ownerdonation WHERE Date >= '" + firstDay + "' AND " +
                    "Date <= '" + lastDay + "' AND MovementID > 0");

            totOd = DBConnection.executeForSum("SELECT SUM(Donation) AS Total " +
                    "FROM ownerdonation WHERE Date >= '" + firstDay + "' AND " +
                    "Date <= '" + lastDay + "' AND MovementID = 0");

            totAl = DBConnection.executeForSum(
                    "SELECT SUM(DonationSize) AS Total " +
                    "FROM animalwaitinglist WHERE DatePutOnList >='" +
                    firstDay + "' AND " + "DatePutOnList <= '" + lastDay +
                    "' AND DonationSize > 0");

            model[0][i] = (int) totAd;
            model[1][i] = (int) totBI;
            model[2][i] = (int) totOd;
            model[3][i] = (int) totAl;

            totalAd += totAd;
            totalBI += totBI;
            totalOd += totOd;
            totalAl += totAl;

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
        String[] rows = {
                Global.i18n("charts", "Adoptions_(") + Global.currencySymbol +
                Double.toString(Utils.round(totalAd, 2)) + ")",
                
                Global.i18n("charts", "Brought_In_(") + Global.currencySymbol +
                Double.toString(Utils.round(totalBI, 2)) + ")",
                
                Global.i18n("charts", "donations") + Global.currencySymbol +
                Double.toString(Utils.round(totalOd, 2)) + ")",
                
                Global.i18n("charts", "waitinglist") + Global.currencySymbol +
                Double.toString(Utils.round(totalAl, 2)) + ")",
            };

        data = new ObjectChartDataModel(model, columns, rows);

        return checkModelIsNotZeroes(model, columns.length, rows.length);
    }
}
