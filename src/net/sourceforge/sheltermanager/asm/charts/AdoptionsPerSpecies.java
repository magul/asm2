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
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;


/**
 * Generates a chart, showing adoptions for a whole calendar year, broken down
 * by species.
 *
 * @author Robin Rawson-Tetley
 */
public class AdoptionsPerSpecies extends Chart {
    private String monthname = "";
    private String year = "";
    private int selectedYear = 0;

    public AdoptionsPerSpecies() {
        String selyear = (String) Dialog.getYear(Global.i18n("charts",
                    "Which_year_is_this_graph_for?"));
        selectedYear = Integer.parseInt(selyear);

        // Set title flags
        year = selyear;

        this.start();
    }

    public String getTitle() {
        return Global.i18n("charts",
            "Monthly_Adoption_analysis_(by_species)_for_") + year;
    }

    public boolean createGraph() throws Exception {
        // Get total number of species used in the time period
        Calendar firstOfYear = Calendar.getInstance();
        firstOfYear.set(Calendar.YEAR, selectedYear);
        firstOfYear.set(Calendar.MONTH, 0);
        firstOfYear.set(Calendar.DAY_OF_MONTH, 1);

        String firstYearSql = SQLRecordset.getSQLRepresentationOfDate(Utils.calendarToDate(
                    firstOfYear));
        Calendar lastOfYear = Calendar.getInstance();
        lastOfYear.set(Calendar.YEAR, selectedYear);
        lastOfYear.set(Calendar.MONTH, 11);
        lastOfYear.set(Calendar.DAY_OF_MONTH, 31);

        String lastYearSql = SQLRecordset.getSQLRepresentationOfDate(Utils.calendarToDate(
                    lastOfYear));

        SQLRecordset spec = new SQLRecordset();
        spec.openRecordset(
            "SELECT SpeciesID, SpeciesName, COUNT(SpeciesID) AS TotOfSpecies FROM animal " +
            "INNER JOIN species ON animal.SpeciesID = species.ID " +
            "INNER JOIN adoption ON animal.ID = adoption.AnimalID " +
            "WHERE MovementDate >= '" + firstYearSql + "' AND " +
            "MovementDate <= '" + lastYearSql + "' AND " + "MovementType = " +
            Adoption.MOVETYPE_ADOPTION +
            " GROUP BY SpeciesID, SpeciesName ORDER BY SpeciesName", "animal");

        if (spec.getEOF()) return false;

        // Outline model - 12 columns (Month, Year period)
        // No species rows 
        int[][] model = new int[(int) spec.getRecordCount()][12];

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

            spec.moveFirst();

            int sp = 0;

            while (!spec.getEOF()) {
                // ------------ ADOPTION FIGURES PER SPECIES/MONTH ----
                SQLRecordset adoption = new SQLRecordset();

                model[sp][i] = DBConnection.executeForCount(
                        "SELECT Count(adoption.ID) FROM adoption " +
                        "INNER JOIN animal " +
                        "ON adoption.AnimalID = animal.ID WHERE " +
                        "MovementDate >= '" + firstDay + "' AND " +
                        "MovementDate <= '" + lastDay + "' AND " +
                        "MovementType = " + Adoption.MOVETYPE_ADOPTION +
                        " AND " + "SpeciesID = " + spec.getField("SpeciesID"));
                spec.moveNext();
                sp++;
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

        String[] rows = new String[(int) spec.getRecordCount()];
        spec.moveFirst();

        for (int i = 0; i < spec.getRecordCount(); i++) {
            rows[i] = spec.getField("SpeciesName").toString() + " (" +
                spec.getField("TotOfSpecies").toString() + ")";
            spec.moveNext();
        }

        data = new ObjectChartDataModel(model, columns, rows);

        return checkModelIsNotZeroes(model, columns.length, rows.length);
    }
}
