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
import net.sourceforge.sheltermanager.asm.bo.InternalLocation;
import net.sourceforge.sheltermanager.asm.bo.Species;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;


/**
 * Generates a chart, showing adoptions for a whole calendar year, broken down
 * by the block they were adopted from
 *
 * @author Robin Rawson-Tetley
 */
public class AdoptionsPerBlock extends Chart {
    private String monthname = "";
    private String year = "";
    private int selectedYear = 0;
    private int speciesID = 0;
    private String speciesName = Global.i18n("charts", "all");

    public AdoptionsPerBlock() {
        // Get species
        speciesID = Dialog.getSpecies();

        String selyear = (String) Dialog.getYear(Global.i18n("charts",
                    "Which_year_is_this_graph_for?"));
        selectedYear = Integer.parseInt(selyear);

        // Look up the name
        if (speciesID != 0) {
            try {
                Species s = new Species();
                s.openRecordset("ID = " + speciesID);
                speciesName = s.getSpeciesName();
            } catch (Exception e) {
            }
        }

        // Set title flags
        year = selyear;

        this.start();
    }

    public String getTitle() {
        return Global.i18n("charts", "Monthly_Adoption_analysis_(by_location,_") +
        speciesName + Global.i18n("charts", "_species)_for_") + year;
    }

    public boolean createGraph() throws Exception {
        // Outline model - 12 columns (Month, Year period)
        // 1 row for each internal location
        InternalLocation il = new InternalLocation();
        il.openRecordset("ID > 0 ORDER BY LocationName");

        int[][] model = new int[(int) il.getRecordCount()][12];

        // Total row
        int[] locationTotals = new int[(int) il.getRecordCount()];

        setStatusBarMax(12);

        double totDogs = 0;
        double totCats = 0;
        double totMisc = 0;
        double totalDogs = 0;
        double totalCats = 0;
        double totalMisc = 0;

        for (int i = 0; i < 12; i++) {
            totDogs = 0;
            totCats = 0;
            totMisc = 0;

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

            il.moveFirst();

            int z = 0;

            while (!il.getEOF()) {
                // ------------ ADOPTION FIGURES ----------------
                SQLRecordset adoption = new SQLRecordset();

                adoption.openRecordset(
                    "SELECT AnimalID FROM adoption INNER JOIN animal " +
                    "ON adoption.AnimalID = animal.ID WHERE " +
                    "MovementDate >= '" + firstDay + "' AND " +
                    "MovementDate <= '" + lastDay + "' AND " +
                    "MovementType = " + Adoption.MOVETYPE_ADOPTION + " AND " +
                    "ShelterLocation = " + il.getID() +
                    ((speciesID != 0) ? (" AND SpeciesID = " + speciesID) : ""),
                    "adoption");

                model[z][i] = (int) adoption.getRecordCount();

                locationTotals[z] += model[z][i];

                z++;
                il.moveNext();
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

        // Build row headers from block names
        il.moveFirst();

        String[] rows = new String[(int) il.getRecordCount()];
        int i = 0;

        while (!il.getEOF()) {
            rows[i] = il.getLocationName() + " (" +
                Integer.toString(locationTotals[i]) + ")";
            il.moveNext();
            i++;
        }

        data = new ObjectChartDataModel(model, columns, rows);

        return checkModelIsNotZeroes(model, columns.length, rows.length);
    }
}
