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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalLitter;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.DeathReason;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.text.MessageFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Generates some useful monthly animal figures
 *
 * @author Robin Rawson-Tetley
 */
public class AnimalFigures extends Report {
    private String monthname = "";
    private String year = "";
    private Calendar firstDayOfMonth = null;

    // Summary totals that get calculated along the way
    private String totalUnwantedSummary = "";
    private String totalAdoptedSummary = "";
    private String totalPTSSummary = "";

    public AnimalFigures() {
        // Request month
        String[] months = {
                Global.i18n("reports", "January"),
                Global.i18n("reports", "February"),
                Global.i18n("reports", "March"), Global.i18n("reports", "April"),
                Global.i18n("reports", "May"), Global.i18n("reports", "June"),
                Global.i18n("reports", "July"), Global.i18n("reports", "August"),
                Global.i18n("reports", "September"),
                Global.i18n("reports", "October"),
                Global.i18n("reports", "November"),
                Global.i18n("reports", "December")
            };

        String selmonth = (String) Dialog.getInput(Global.i18n("reports",
                    "Which_month_is_this_report_for?"),
                Global.i18n("reports", "Select_Month"), months,
                months[Calendar.getInstance().get(Calendar.MONTH)]);

        // Request year
        String selyear = (String) Dialog.getYear(Global.i18n("reports",
                    "Which_year_is_this_report_for?"));

        // Now that we have the month and year, we need to translate them into a
        // calendar
        int iselmonth = 0;

        if (selmonth.equals(Global.i18n("reports", "January"))) {
            iselmonth = Calendar.JANUARY;
        }

        if (selmonth.equals(Global.i18n("reports", "February"))) {
            iselmonth = Calendar.FEBRUARY;
        }

        if (selmonth.equals(Global.i18n("reports", "March"))) {
            iselmonth = Calendar.MARCH;
        }

        if (selmonth.equals(Global.i18n("reports", "April"))) {
            iselmonth = Calendar.APRIL;
        }

        if (selmonth.equals(Global.i18n("reports", "May"))) {
            iselmonth = Calendar.MAY;
        }

        if (selmonth.equals(Global.i18n("reports", "June"))) {
            iselmonth = Calendar.JUNE;
        }

        if (selmonth.equals(Global.i18n("reports", "July"))) {
            iselmonth = Calendar.JULY;
        }

        if (selmonth.equals(Global.i18n("reports", "August"))) {
            iselmonth = Calendar.AUGUST;
        }

        if (selmonth.equals(Global.i18n("reports", "September"))) {
            iselmonth = Calendar.SEPTEMBER;
        }

        if (selmonth.equals(Global.i18n("reports", "October"))) {
            iselmonth = Calendar.OCTOBER;
        }

        if (selmonth.equals(Global.i18n("reports", "November"))) {
            iselmonth = Calendar.NOVEMBER;
        }

        if (selmonth.equals(Global.i18n("reports", "December"))) {
            iselmonth = Calendar.DECEMBER;
        }

        int iselyear = Integer.parseInt(selyear);

        // Set title flags
        monthname = selmonth;
        year = selyear;

        // Create a Calendar representing the first day of the selected
        // month and year
        firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR, 0);
        firstDayOfMonth.set(Calendar.MINUTE, 0);
        firstDayOfMonth.set(Calendar.SECOND, 0);
        firstDayOfMonth.set(Calendar.MONTH, iselmonth);
        firstDayOfMonth.set(Calendar.YEAR, iselyear);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        this.start();
    }

    public String getTitle() {
        return MessageFormat.format(Global.i18n("reports", "Animal_Figures_for_"),
            new Object[] { monthname, year });
    }

    public void generateReport() {
        try {
            // Update litter figures to make sure any that should be cancelled are
            AnimalLitter.updateLitters();

            // Based on Species
            if (Configuration.getBoolean("AnimalFiguresGroupBySpecies")) {
                // SPECIES ==============================================
                SQLRecordset sp = LookupCache.getSpeciesLookup();
                sp.moveFirst();

                // Compile list of species for which we have animal data
                Vector v = new Vector();

                while (!sp.getEOF()) {
                    if (0 < DBConnection.executeForCount(
                                "SELECT COUNT(ID) FROM animal WHERE SpeciesID = " +
                                sp.getField("ID"))) {
                        v.add(sp.getField("ID"));
                    }

                    sp.moveNext();
                }

                // 15 steps per generation
                int maxent = 15 * (v.size());
                setStatusBarMax(maxent);

                for (int i = 0; i < v.size(); i++) {
                    genSpeciesFigs((Integer) v.get(i));
                }
            } else {
                // ANIMAL TYPES ========================================
                // Initialise the status bar upto
                // the maximum steps - 14 steps in number of groups
                SQLRecordset at = LookupCache.getAnimalTypeLookup();
                at.moveFirst();

                // Compile list of types for which we have animal data
                Vector v = new Vector();

                while (!at.getEOF()) {
                    if (0 < DBConnection.executeForCount(
                                "SELECT COUNT(ID) FROM animal WHERE AnimalTypeID = " +
                                at.getField("ID"))) {
                        v.add(at.getField("ID"));
                    }

                    at.moveNext();
                }

                // 14 steps per generation
                int maxent = 14 * (v.size());
                setStatusBarMax(maxent);

                for (int i = 0; i < v.size(); i++) {
                    genAnimalTypeFigs((Integer) v.get(i));
                }
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "An_error_occurred_generating_the_report", e.getMessage()));
            Global.logException(e, getClass());
        } finally {
            resetStatusBar();
        }
    }

    /** Fills a row of the figures table from a query.
     *  The query should have two columns, the first a date
     *  and the second a count for that date.
     * @param sql The query
     * @param dateField The name of the field holding the date
     * @param countField the name of the field holding the count
     */
    public int[] fillRow(String sql, String dateField, String countField) {
        int[] row = new int[40];

        for (int i = 0; i < row.length; i++)
            row[i] = 0;

        try {
            SQLRecordset r = new SQLRecordset();
            r.openRecordset(sql, "animal");

            while (!r.getEOF()) {
                Calendar c = Utils.dateToCalendar((Date) r.getField(dateField));
                row[c.get(Calendar.DAY_OF_MONTH)] = ((Integer) r.getField(countField)).intValue();
                r.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return row;
    }

    public void genSpeciesFigs(Integer speciesID) throws Exception {
        
        String animalName = "";
        Calendar lastDayOfMonth = null;
        int noDaysInMonth = 0;
        boolean fosterOnShelter = Configuration.getBoolean("FosterOnShelter");

        // Load the set of death reasons
        DeathReason dr = new DeathReason();
        dr.openRecordset("ID > 0 ORDER BY ReasonName");

        // Get last day of month and number of days in month
        lastDayOfMonth = (Calendar) firstDayOfMonth.clone();
        lastDayOfMonth.add(Calendar.MONTH, 1);
        lastDayOfMonth.add(Calendar.DAY_OF_MONTH, -1);
        noDaysInMonth = lastDayOfMonth.get(Calendar.DAY_OF_MONTH);

        String sqlFirstDayOfMonth = Utils.getSQLDateOnly(firstDayOfMonth);
        String sqlLastDayOfMonth = Utils.getSQLDateOnly(lastDayOfMonth);

        // Draw the day of the month table
        tableNew(false);
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Day_Of_Month")));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(bold(Integer.toString(i)));
        }

        tableAddCell(bold(Global.i18n("reports", "Avg")));
        tableFinishRow();

        // Row totals
        int[] animalTotal = new int[40];
        int[] animalTotalNoFoster = new int[40];

        // --------------------------------------------------------------
        // INVENTORY SECTION
        // --------------------------------------------------------------
        // ==============================================================

        // --------------------------------------------------------------
        // Animals On Shelter
        // --------------------------------------------------------------
        tableAddRow();
        tableAddCell(Global.i18n("reports", "On_Shelter"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            int total = Animal.getNumberOfAnimalsOnShelter(Utils.calendarToDate(
                        atDate), speciesID.intValue(), 0, 0, Animal.ALLAGES);
            tableAddCell(Integer.toString(total));
            animalTotal[i] = total;
            animalTotalNoFoster[i] = animalTotal[i];
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Animals On Foster - Only valid if "FosterOnShelter" set
        // --------------------------------------------------------------
        if (fosterOnShelter) {
            tableAddRow();
            tableAddCell(Global.i18n("reports", "on_foster_in_figures"));

            for (int i = 1; i <= noDaysInMonth; i++) {
                Calendar atDate = (Calendar) firstDayOfMonth.clone();
                atDate.add(Calendar.DAY_OF_MONTH, i - 1);

                int total = Animal.getNumberOfAnimalsOnFoster(Utils.calendarToDate(
                            atDate), speciesID.intValue(), 0);
                tableAddCell(Integer.toString(total));
                animalTotal[i] += total;
            }

            tableFinishRow();
        }

        // --------------------------------------------------------------
        // Litters
        // --------------------------------------------------------------
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Litters"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            int total = AnimalLitter.getNumberOfLittersOnShelter(Utils.calendarToDate(
                        atDate), speciesID.intValue());
            tableAddCell(Integer.toString(total));
        }

        tableFinishRow();

        incrementStatusBar();


        // --------------------------------------------------------------
        // Totals and Averages
        // --------------------------------------------------------------
        // Draw totals
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Total")));

        boolean gotInventory = false;
        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(bold(Integer.toString(animalTotal[i])));
            if (animalTotal[i] > 0) gotInventory = true;
        }

        // Do we actually have any data for this species? If all the 
        // total rows are zero, then abort now and don't bother putting
        // it on the report
        if (!gotInventory) {
           // Move the progress bar on
           for (int i = 0; i < 13; i++) { incrementStatusBar(); }
           return;
        }


        // Calculate average animal movement
        double average = 0;
        int averageitems = 0;

        for (int i = 1; i <= noDaysInMonth; i++) {
            if (animalTotal[i] > 0) {
                average += (double) animalTotal[i];
                averageitems++;
            }
        }

        average = (average / averageitems);
        tableAddCell(bold(Double.toString(Utils.round(average, 2))));
        tableFinishRow();

        // Output the species header - we can do this because tables aren't
        // appended into the report until finishTable() is called
        animalName = LookupCache.getSpeciesName(speciesID);
        addLevelTwoHeader(animalName);

        // --------------------------------------------------------------
        // IN SECTION
        // --------------------------------------------------------------
        // ==============================================================

        // --------------------------------------------------------------
        // Brought In animals
        // --------------------------------------------------------------
        int[] broughtInAnimals = new int[40];

        broughtInAnimals = fillRow(
                "SELECT DateBroughtIn, COUNT(ID) AS Total FROM animal WHERE " +
                "SpeciesID = " + speciesID + " AND DateBroughtIn >= '" +
                sqlFirstDayOfMonth + "'" + " AND DateBroughtIn <= '" +
                sqlLastDayOfMonth + "'" +
                " AND IsTransfer = 0 AND NonShelterAnimal = 0" +
                " GROUP BY DateBroughtIn", "DateBroughtIn", "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Returned Animals
        // --------------------------------------------------------------
        int[] returnedAnimals = new int[40];

        returnedAnimals = fillRow(
                "SELECT ReturnDate, COUNT(animal.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                "WHERE SpeciesID = " + speciesID + " AND ReturnDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND ReturnDate <= '" +
                sqlLastDayOfMonth + "'" + " AND MovementType = " +
                Adoption.MOVETYPE_ADOPTION + " GROUP BY ReturnDate",
                "ReturnDate", "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Transferred In Animals
        // --------------------------------------------------------------
        int[] transferInAnimals = new int[40];

        transferInAnimals = fillRow(
                "SELECT DateBroughtIn, COUNT(ID) AS Total FROM animal WHERE " +
                "SpeciesID = " + speciesID + " AND DateBroughtIn >= '" +
                sqlFirstDayOfMonth + "'" + " AND DateBroughtIn <= '" +
                sqlLastDayOfMonth + "'" +
                " AND IsTransfer <> 0 AND NonShelterAnimal = 0" +
                " GROUP BY DateBroughtIn", "DateBroughtIn", "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Returned Animals from Fostering
        // --------------------------------------------------------------
        int[] returnedFoster = new int[40];

        returnedFoster = fillRow(
                "SELECT ReturnDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "SpeciesID = " + speciesID + " AND MovementType = " +
                Adoption.MOVETYPE_FOSTER + " AND ReturnDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND ReturnDate <= '" +
                sqlLastDayOfMonth + "'" + " GROUP BY ReturnDate", "ReturnDate",
                "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Returned Animals from Other. Other includes
        // Transfer returns, Escaped, Stolen, Released, Reclaimed and Retailer
        // --------------------------------------------------------------
        int[] returnedOther = new int[40];

        returnedOther = fillRow(
                "SELECT ReturnDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "SpeciesID = " + speciesID + " AND " + "MovementType <> " +
                Adoption.MOVETYPE_FOSTER + " AND " + "MovementType <> " +
                Adoption.MOVETYPE_ADOPTION + " AND ReturnDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND ReturnDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY ReturnDate", "ReturnDate",
                "Total");
        incrementStatusBar();

        // ================================================================
        // Actually draw the input figures onto the table. We do it here because
        // some figures can be affected by others:
        // ================================================================
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Incoming"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(broughtInAnimals[i]);
        }

        tableFinishRow();

        // Returned animals
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Returned"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(returnedAnimals[i]);
        }

        tableFinishRow();

        // Transferred in
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Transferred_In"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(transferInAnimals[i]);
        }

        tableFinishRow();

        // From Fostering
        tableAddRow();
        tableAddCell(Global.i18n("reports", "From_Fostering"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(returnedFoster[i]);
        }

        tableFinishRow();

        // From Other
        tableAddRow();
        tableAddCell(Global.i18n("reports", "From_Other"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(returnedOther[i]);
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // Sub Total for In Section
        // --------------------------------------------------------------
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "SubTotal")));

        int[] animalSubTotalIn = new int[40];

        for (int i = 1; i <= noDaysInMonth; i++) {
            animalSubTotalIn[i] = broughtInAnimals[i] + returnedAnimals[i] +
                transferInAnimals[i] + returnedFoster[i] + returnedOther[i];
            tableAddCell(bold(Integer.toString(animalSubTotalIn[i])));
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // OUT SECTION
        // --------------------------------------------------------------
        // ==============================================================

        // --------------------------------------------------------------
        // Animals Adopted Out
        // --------------------------------------------------------------
        int[] adoptedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Adopted"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID Where ";

            sql += ("SpeciesID = " + speciesID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_ADOPTION +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            adoptedAnimals[i] = DBConnection.executeForCount(sql);
            tableAddCell(adoptedAnimals[i]);
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Reclaimed Animals
        // --------------------------------------------------------------
        int[] reclaimedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Returned_To_Owner"));

        reclaimedAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "SpeciesID = " + speciesID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_RECLAIMED + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(reclaimedAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Escaped Animals
        // --------------------------------------------------------------
        int[] escapedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Escaped"));

        escapedAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "SpeciesID = " + speciesID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_ESCAPED + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(escapedAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Stolen Animals
        // --------------------------------------------------------------
        int[] stolenAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Stolen"));

        stolenAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "SpeciesID = " + speciesID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_STOLEN + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(stolenAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Released Animals
        // --------------------------------------------------------------
        int[] releasedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Released_To_Wild"));

        releasedAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "SpeciesID = " + speciesID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_RELEASED + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(releasedAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Animals Transferred Out
        // --------------------------------------------------------------
        int[] transferredOut = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Transferred_Out"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID Where ";

            sql += ("SpeciesID = " + speciesID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_TRANSFER +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            transferredOut[i] = DBConnection.executeForCount(sql);
            tableAddCell(transferredOut[i]);
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Animals Fostered Out
        // --------------------------------------------------------------
        int[] fosteredOut = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "To_Fostering"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID Where ";

            sql += ("SpeciesID = " + speciesID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_FOSTER +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            fosteredOut[i] = DBConnection.executeForCount(sql);
            tableAddCell(fosteredOut[i]);
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Dead Animals
        // --------------------------------------------------------------
        int[] deadAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Died"));

        // Include case to stop them disappearing from inventory
        deadAnimals = fillRow(
                "SELECT DeceasedDate, COUNT(ID) AS Total FROM animal WHERE " +
                "(SpeciesID = " + speciesID + " AND DeceasedDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND DeceasedDate <= '" +
                sqlLastDayOfMonth + "'" +
                " AND PutToSleep = 0 AND DiedOffShelter = 0" +
                " AND NonShelterAnimal = 0) OR " + "(SpeciesID = " + speciesID +
                " AND DeceasedDate >= '" + sqlFirstDayOfMonth + "'" +
                " AND DeceasedDate <= '" + sqlLastDayOfMonth + "'" +
                " AND PutToSleep = 0 AND DiedOffShelter = 0" +
                " AND NonShelterAnimal = 0)" + " GROUP BY DeceasedDate",
                "DeceasedDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(deadAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // PTS Animals
        // --------------------------------------------------------------
        int[] ptsAnimals = new int[40];
        totalPTSSummary = "";

        tableAddRow();
        tableAddCell(Global.i18n("reports", "PTS"));

        // Include case to stop them disappearing from inventory
        ptsAnimals = fillRow(
                "SELECT DeceasedDate, COUNT(ID) AS Total FROM animal WHERE " +
                "SpeciesID = " + speciesID + " AND DeceasedDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND DeceasedDate <= '" +
                sqlLastDayOfMonth + "'" +
                " AND PutToSleep <> 0 AND DiedOffShelter = 0" +
                " AND NonShelterAnimal = 0" + " GROUP BY DeceasedDate",
                "DeceasedDate", "Total");

        int totalPTS = 0;
        for (int i = 1; i <= noDaysInMonth; i++) {
            // Summary totals
            totalPTS += ptsAnimals[i];
            tableAddCell(Integer.toString(ptsAnimals[i]));
        }

        // Get the total for each category
        if (totalPTS > 0) {
            totalPTSSummary += totalPTS + " (";
            String sql = "SELECT ReasonName, COUNT(animal.ID) AS Total FROM animal " +
                "INNER JOIN deathreason ON deathreason.ID = animal.PTSReasonID " +
                "WHERE SpeciesID = " + speciesID + " AND DeceasedDate >= '" +
                sqlFirstDayOfMonth + "' AND DeceasedDate <= '" +
                sqlLastDayOfMonth + "' AND PutToSleep <> 0 AND DiedOffShelter = 0 " +
                "AND NonShelterAnimal = 0 " +
                "GROUP BY ReasonName";
            try {
                SQLRecordset drs = new SQLRecordset();
                drs.openRecordset(sql, "animal");
                while (!drs.getEOF()) {
                    if (!totalPTSSummary.endsWith("(")) totalPTSSummary += ", ";
                    totalPTSSummary += drs.getField("Total").toString() + " " + drs.getField("ReasonName").toString();
                    drs.moveNext();
                }
                drs.free();
                totalPTSSummary += ")";
            }
            catch (Exception e) {
                Global.logException(e, getClass());
            }

        }
        else {
            totalPTSSummary = "0";
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Other (includes to Retailer)
        // --------------------------------------------------------------
        int[] otherOut = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "to_other"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID WHERE ";

            sql += ("SpeciesID = " + speciesID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_RETAILER +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            otherOut[i] = DBConnection.executeForCount(sql);
            tableAddCell(otherOut[i]);
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // Sub Total for Out Section
        // --------------------------------------------------------------
        tableFinishRow();
        tableAddCell(bold(Global.i18n("reports", "SubTotal")));

        int[] animalSubTotalOut = new int[40];

        for (int i = 1; i <= noDaysInMonth; i++) {
            animalSubTotalOut[i] = adoptedAnimals[i] + reclaimedAnimals[i] +
                escapedAnimals[i] + stolenAnimals[i] + transferredOut[i] +
                fosteredOut[i] + deadAnimals[i] + ptsAnimals[i] +
                releasedAnimals[i] + otherOut[i];
            tableAddCell(bold(Integer.toString(animalSubTotalOut[i])));
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // Master Total
        // --------------------------------------------------------------
        Calendar theDayBeforeFirst = (Calendar) firstDayOfMonth.clone();
        theDayBeforeFirst.add(Calendar.DAY_OF_MONTH, -1);

        Date dayBeforeFirst = Utils.calendarToDate(theDayBeforeFirst);
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Day_Total") + "<br/>" +
                Global.i18n("reports",
                    "Day_total_of_last_day_of_previous_month",
                    Integer.toString(Animal.getNumberOfAnimalsOnShelter(
                            dayBeforeFirst, speciesID.intValue(), 0, 0,
                            Animal.ALLAGES)))));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(bold(Integer.toString(animalTotalNoFoster[i])));
        }

        // ==============================================================
        // ==============================================================
        // SUMMARY TOTALS FOR END OF REPORT
        // ==============================================================
        // ==============================================================

        // BROUGHT TO THE SHELTER ---------------------------
        // ==============================================================
        int totalAnimals = 0;
        int totReturnedAnimals = 0;
        int totTransferIn = 0;
        int totReturnFoster = 0;
        int totReturnOther = 0;

        // Add up the totals
        for (int i = 1; i <= noDaysInMonth; i++) {
            totalAnimals += animalSubTotalIn[i];
            totReturnedAnimals += returnedAnimals[i];
            totTransferIn += transferInAnimals[i];
            totReturnFoster += returnedFoster[i];
            totReturnOther += returnedOther[i];
        }

        // Build the summary string for Misc Brought In
        totalUnwantedSummary = Integer.toString(totalAnimals);

        boolean hasExtra = false;
        String additional = "";

        if (totReturnedAnimals > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_returned",
                Integer.toString(totReturnedAnimals));
            hasExtra = true;
        }

        if (totTransferIn > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_transferred_in",
                Integer.toString(totTransferIn));
            hasExtra = true;
        }

        if (totReturnFoster > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_returned_from_foster_homes",
                Integer.toString(totReturnFoster));
            hasExtra = true;
        }

        if (totReturnOther > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports",
                "_returned_from_other_(transfers,_released,_escaped,_stolen_or_reclaimed)",
                Integer.toString(totReturnOther));
            hasExtra = true;
        }

        if (hasExtra) {
            totalUnwantedSummary = Global.i18n("reports", "unwanted_including",
                    Integer.toString(totalAnimals), additional);
        }

        // ADOPTED ---------------------------
        // ==============================================================
        totalAnimals = 0;

        int totFostered = 0;
        int totTransferred = 0;
        int totReclaimed = 0;
        int totEscaped = 0;
        int totStolen = 0;
        int totReleased = 0;

        // Add up the totals
        for (int i = 1; i <= noDaysInMonth; i++) {
            totalAnimals += adoptedAnimals[i]; // animalSubTotalOut[i] for
                                               // all

            totFostered += fosteredOut[i];
            totTransferred += transferredOut[i];
            totReclaimed += reclaimedAnimals[i];
            totEscaped += escapedAnimals[i];
            totStolen += stolenAnimals[i];
            totReleased += releasedAnimals[i];
        }

        // Build the summary string for Misc Adopted
        totalAdoptedSummary = Integer.toString(totalAnimals);

        hasExtra = false;
        additional = "";

        if (totFostered > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_to_foster_home",
                Integer.toString(totFostered));
            hasExtra = true;
        }

        if (totTransferred > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_transferred_out",
                Integer.toString(totTransferred));
            hasExtra = true;
        }

        if (totReclaimed > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_returned_to_owner",
                Integer.toString(totReclaimed));
            hasExtra = true;
        }

        if (totEscaped > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_escaped",
                Integer.toString(totEscaped));
            hasExtra = true;
        }

        if (totStolen > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_stolen",
                Integer.toString(totStolen));
            hasExtra = true;
        }

        if (hasExtra) {
            totalAdoptedSummary = Global.i18n("reports", "_plus_additional",
                    Integer.toString(totalAnimals), additional);
        }

        // --------------------------------------------------------------
        // Display table and finish
        // --------------------------------------------------------------
        tableFinishRow();
        tableFinish();
        addTable();

        // --------------------------------------------------------------
        // Show the summary
        // --------------------------------------------------------------
        createSummary();
    }

    public void genAnimalTypeFigs(Integer animalTypeID)
        throws Exception {
        String animalName = "";
        Calendar lastDayOfMonth = null;
        int noDaysInMonth = 0;
        boolean fosterOnShelter = Configuration.getBoolean("FosterOnShelter");

        // Load the set of death reasons
        DeathReason dr = new DeathReason();
        dr.openRecordset("ID > 0 ORDER BY ReasonName");

        // Get last day of month and number of days in month
        lastDayOfMonth = (Calendar) firstDayOfMonth.clone();
        lastDayOfMonth.add(Calendar.MONTH, 1);
        lastDayOfMonth.add(Calendar.DAY_OF_MONTH, -1);
        noDaysInMonth = lastDayOfMonth.get(Calendar.DAY_OF_MONTH);

        String sqlFirstDayOfMonth = Utils.getSQLDateOnly(firstDayOfMonth);
        String sqlLastDayOfMonth = Utils.getSQLDateOnly(lastDayOfMonth);

        // Draw the day of the month table
        tableNew(false);
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Day_Of_Month")));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(bold(Integer.toString(i)));
        }

        tableAddCell(bold(Global.i18n("reports", "Avg")));
        tableFinishRow();

        // Row totals
        int[] animalTotal = new int[40];
        int[] animalTotalNoFoster = new int[40];

        // --------------------------------------------------------------
        // INVENTORY SECTION
        // --------------------------------------------------------------
        // ==============================================================

        // --------------------------------------------------------------
        // Animals On Shelter
        // --------------------------------------------------------------
        tableAddRow();
        tableAddCell(Global.i18n("reports", "On_Shelter"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            int total = Animal.getNumberOfAnimalsOnShelter(Utils.calendarToDate(
                        atDate), 0, animalTypeID.intValue(), 0, Animal.ALLAGES);
            tableAddCell(Integer.toString(total));
            animalTotal[i] = total;
            animalTotalNoFoster[i] = animalTotal[i];
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Animals On Foster - Only valid if "FosterOnShelter" set
        // --------------------------------------------------------------
        if (fosterOnShelter) {
            tableAddRow();
            tableAddCell(Global.i18n("reports", "on_foster_in_figures"));

            for (int i = 1; i <= noDaysInMonth; i++) {
                Calendar atDate = (Calendar) firstDayOfMonth.clone();
                atDate.add(Calendar.DAY_OF_MONTH, i - 1);

                int total = Animal.getNumberOfAnimalsOnFoster(Utils.calendarToDate(
                            atDate), 0, animalTypeID.intValue());
                tableAddCell(Integer.toString(total));
                animalTotal[i] += total;
            }

            tableFinishRow();
        }

        // --------------------------------------------------------------
        // Totals and Averages
        // --------------------------------------------------------------
        // Draw totals
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Total")));

        boolean gotInventory = false;
        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(bold(Integer.toString(animalTotal[i])));
            if (animalTotal[i] > 0) gotInventory = true;
        }

        // Do we actually have any data for this type? If all the 
        // total rows are zero, then abort now and don't bother putting
        // it on the report
        if (!gotInventory) {
           // Move the progress bar on
           for (int i = 0; i < 13; i++) { incrementStatusBar(); }
           return;
        }

        // Calculate average animal movement
        double average = 0;
        int averageitems = 0;

        for (int i = 1; i <= noDaysInMonth; i++) {
            if (animalTotal[i] > 0) {
                average += (double) animalTotal[i];
                averageitems++;
            }
        }

        average = (average / averageitems);
        tableAddCell(bold(Double.toString(Utils.round(average, 2))));
        tableFinishRow();

        // Output the type header - we can do this because tables aren't
        // appended into the report until finishTable() is called
        animalName = LookupCache.getAnimalTypeName(animalTypeID);
        addLevelTwoHeader(animalName);

        // --------------------------------------------------------------
        // IN SECTION
        // --------------------------------------------------------------
        // ==============================================================

        // --------------------------------------------------------------
        // Brought In animals
        // --------------------------------------------------------------
        int[] broughtInAnimals = new int[40];

        broughtInAnimals = fillRow(
                "SELECT DateBroughtIn, COUNT(ID) AS Total FROM animal WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND DateBroughtIn >= '" +
                sqlFirstDayOfMonth + "'" + " AND DateBroughtIn <= '" +
                sqlLastDayOfMonth + "'" +
                " AND IsTransfer = 0 AND NonShelterAnimal = 0" +
                " GROUP BY DateBroughtIn", "DateBroughtIn", "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Returned Animals
        // --------------------------------------------------------------
        int[] returnedAnimals = new int[40];

        returnedAnimals = fillRow(
                "SELECT ReturnDate, COUNT(animal.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                "WHERE AnimalTypeID = " + animalTypeID +
                " AND ReturnDate >= '" + sqlFirstDayOfMonth + "'" +
                " AND ReturnDate <= '" + sqlLastDayOfMonth + "'" +
                " AND MovementType = " + Adoption.MOVETYPE_ADOPTION +
                " GROUP BY ReturnDate", "ReturnDate", "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Transferred In Animals
        // --------------------------------------------------------------
        int[] transferInAnimals = new int[40];

        transferInAnimals = fillRow(
                "SELECT DateBroughtIn, COUNT(ID) AS Total FROM animal WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND DateBroughtIn >= '" +
                sqlFirstDayOfMonth + "'" + " AND DateBroughtIn <= '" +
                sqlLastDayOfMonth + "'" +
                " AND IsTransfer <> 0 AND NonShelterAnimal = 0" +
                " GROUP BY DateBroughtIn", "DateBroughtIn", "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Returned Animals from Fostering
        // --------------------------------------------------------------
        int[] returnedFoster = new int[40];

        returnedFoster = fillRow(
                "SELECT ReturnDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND MovementType = " +
                Adoption.MOVETYPE_FOSTER + " AND ReturnDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND ReturnDate <= '" +
                sqlLastDayOfMonth + "'" + " GROUP BY ReturnDate", "ReturnDate",
                "Total");

        incrementStatusBar();

        // --------------------------------------------------------------
        // Returned Animals from Other. Other includes
        // Transfer returns, Escaped, Stolen, Released, Reclaimed and Retailer
        // --------------------------------------------------------------
        int[] returnedOther = new int[40];

        returnedOther = fillRow(
                "SELECT ReturnDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND " +
                "MovementType <> " + Adoption.MOVETYPE_FOSTER + " AND " +
                "MovementType <> " + Adoption.MOVETYPE_ADOPTION +
                " AND ReturnDate >= '" + sqlFirstDayOfMonth + "'" +
                " AND ReturnDate <= '" + sqlLastDayOfMonth + "'" +
                "GROUP BY ReturnDate", "ReturnDate", "Total");
        incrementStatusBar();

        // ================================================================
        // Actually draw the input figures onto the table. We do it here because
        // some figures can be affected by others:
        // ================================================================
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Incoming"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(broughtInAnimals[i]);
        }

        tableFinishRow();

        // Returned animals
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Returned"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(returnedAnimals[i]);
        }

        tableFinishRow();

        // Transferred in
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Transferred_In"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(transferInAnimals[i]);
        }

        tableFinishRow();

        // From Fostering
        tableAddRow();
        tableAddCell(Global.i18n("reports", "From_Fostering"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(returnedFoster[i]);
        }

        tableFinishRow();

        // From Other
        tableAddRow();
        tableAddCell(Global.i18n("reports", "From_Other"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(returnedOther[i]);
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // Sub Total for In Section
        // --------------------------------------------------------------
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "SubTotal")));

        int[] animalSubTotalIn = new int[40];

        for (int i = 1; i <= noDaysInMonth; i++) {
            animalSubTotalIn[i] = broughtInAnimals[i] + returnedAnimals[i] +
                transferInAnimals[i] + returnedFoster[i] + returnedOther[i];
            tableAddCell(bold(Integer.toString(animalSubTotalIn[i])));
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // OUT SECTION
        // --------------------------------------------------------------
        // ==============================================================

        // --------------------------------------------------------------
        // Animals Adopted Out
        // --------------------------------------------------------------
        int[] adoptedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Adopted"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID Where ";

            sql += ("AnimalTypeID = " + animalTypeID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_ADOPTION +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            adoptedAnimals[i] = DBConnection.executeForCount(sql);
            tableAddCell(adoptedAnimals[i]);
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Reclaimed Animals
        // --------------------------------------------------------------
        int[] reclaimedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Returned_To_Owner"));

        reclaimedAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_RECLAIMED + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(reclaimedAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Escaped Animals
        // --------------------------------------------------------------
        int[] escapedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Escaped"));

        escapedAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_ESCAPED + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(escapedAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Stolen Animals
        // --------------------------------------------------------------
        int[] stolenAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Stolen"));

        stolenAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_STOLEN + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(stolenAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Released Animals
        // --------------------------------------------------------------
        int[] releasedAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Released_To_Wild"));

        releasedAnimals = fillRow(
                "SELECT MovementDate, COUNT(adoption.ID) AS Total FROM adoption " +
                "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE " +
                "AnimalTypeID = " + animalTypeID + " AND " + "MovementType = " +
                Adoption.MOVETYPE_RELEASED + " AND MovementDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND MovementDate <= '" +
                sqlLastDayOfMonth + "'" + "GROUP BY MovementDate",
                "MovementDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(releasedAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Animals Transferred Out
        // --------------------------------------------------------------
        int[] transferredOut = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Transferred_Out"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID Where ";

            sql += ("AnimalTypeID = " + animalTypeID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_TRANSFER +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            transferredOut[i] = DBConnection.executeForCount(sql);
            tableAddCell(transferredOut[i]);
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Animals Fostered Out
        // --------------------------------------------------------------
        int[] fosteredOut = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "To_Fostering"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID Where ";

            sql += ("AnimalTypeID = " + animalTypeID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_FOSTER +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            fosteredOut[i] = DBConnection.executeForCount(sql);
            tableAddCell(fosteredOut[i]);
        }

        tableFinishRow();

        incrementStatusBar();

        // --------------------------------------------------------------
        // Dead Animals
        // --------------------------------------------------------------
        int[] deadAnimals = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "Died"));

        // Include case to stop them disappearing from inventory
        deadAnimals = fillRow(
                "SELECT DeceasedDate, COUNT(ID) AS Total FROM animal WHERE " +
                "(AnimalTypeID = " + animalTypeID + " AND DeceasedDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND DeceasedDate <= '" +
                sqlLastDayOfMonth + "'" +
                " AND PutToSleep = 0 AND DiedOffShelter = 0" +
                " AND NonShelterAnimal = 0) OR " + "(AnimalTypeID = " +
                animalTypeID + " AND DeceasedDate >= '" + sqlFirstDayOfMonth +
                "'" + " AND DeceasedDate <= '" + sqlLastDayOfMonth + "'" +
                " AND PutToSleep = 0 AND DiedOffShelter = 0" +
                " AND NonShelterAnimal = 0)" + " GROUP BY DeceasedDate",
                "DeceasedDate", "Total");

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(Integer.toString(deadAnimals[i]));
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // PTS Animals
        // --------------------------------------------------------------
        int[] ptsAnimals = new int[40];
        totalPTSSummary = "";

        tableAddRow();
        tableAddCell(Global.i18n("reports", "PTS"));

        // Include case to stop them disappearing from inventory
        ptsAnimals = fillRow(
                "SELECT DeceasedDate, COUNT(ID) AS Total FROM animal WHERE " +
                "(AnimalTypeID = " + animalTypeID + " AND DeceasedDate >= '" +
                sqlFirstDayOfMonth + "'" + " AND DeceasedDate <= '" +
                sqlLastDayOfMonth + "'" +
                " AND PutToSleep <> 0 AND DiedOffShelter = 0" +
                " AND NonShelterAnimal = 0)" + " GROUP BY DeceasedDate",
                "DeceasedDate", "Total");

        int totalPTS = 0;
        for (int i = 1; i <= noDaysInMonth; i++) {
            // Summary totals
            totalPTS += ptsAnimals[i];
            tableAddCell(Integer.toString(ptsAnimals[i]));
        }

        // Get the total for each category
        if (totalPTS > 0) {
            totalPTSSummary += totalPTS + " (";
            String sql = "SELECT ReasonName, COUNT(animal.ID) AS Total FROM animal " +
                "INNER JOIN deathreason ON deathreason.ID = animal.PTSReasonID " +
                "WHERE AnimalTypeID = " + animalTypeID + " AND DeceasedDate >= '" +
                sqlFirstDayOfMonth + "' AND DeceasedDate <= '" +
                sqlLastDayOfMonth + "' AND PutToSleep <> 0 AND DiedOffShelter = 0 " +
                "AND NonShelterAnimal = 0 " +
                "GROUP BY ReasonName";
            try {
                SQLRecordset drs = new SQLRecordset();
                drs.openRecordset(sql, "animal");
                while (!drs.getEOF()) {
                    if (!totalPTSSummary.endsWith("(")) totalPTSSummary += ", ";
                    totalPTSSummary += drs.getField("Total").toString() + " " + drs.getField("ReasonName").toString();
                    drs.moveNext();
                }
                drs.free();
                totalPTSSummary += ")";
            }
            catch (Exception e) {
                Global.logException(e, getClass());
            }

        }
        else {
            totalPTSSummary = "0";
        }

        tableFinishRow();
        incrementStatusBar();

        // --------------------------------------------------------------
        // Other (includes to Retailer)
        // --------------------------------------------------------------
        int[] otherOut = new int[40];
        tableAddRow();
        tableAddCell(Global.i18n("reports", "to_other"));

        for (int i = 1; i <= noDaysInMonth; i++) {
            Calendar atDate = (Calendar) firstDayOfMonth.clone();
            atDate.add(Calendar.DAY_OF_MONTH, i - 1);

            String sqldate = Utils.getSQLDateOnly(atDate);
            String sql = "SELECT COUNT(adoption.ID) FROM adoption INNER JOIN animal On animal.ID = adoption.AnimalID WHERE ";

            sql += ("AnimalTypeID = " + animalTypeID + " AND MovementDate = '" +
            sqldate + "' AND MovementType = " + Adoption.MOVETYPE_RETAILER +
            " AND (ReturnDate Is Null Or ReturnDate > '" + sqldate + "')");

            otherOut[i] = DBConnection.executeForCount(sql);
            tableAddCell(otherOut[i]);
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // Sub Total for Out Section
        // --------------------------------------------------------------
        tableFinishRow();
        tableAddCell(bold(Global.i18n("reports", "SubTotal")));

        int[] animalSubTotalOut = new int[40];

        for (int i = 1; i <= noDaysInMonth; i++) {
            animalSubTotalOut[i] = adoptedAnimals[i] + reclaimedAnimals[i] +
                escapedAnimals[i] + stolenAnimals[i] + transferredOut[i] +
                fosteredOut[i] + deadAnimals[i] + ptsAnimals[i] +
                releasedAnimals[i] + otherOut[i];
            tableAddCell(bold(Integer.toString(animalSubTotalOut[i])));
        }

        tableFinishRow();

        // --------------------------------------------------------------
        // Master Total
        // --------------------------------------------------------------
        Calendar theDayBeforeFirst = (Calendar) firstDayOfMonth.clone();
        theDayBeforeFirst.add(Calendar.DAY_OF_MONTH, -1);

        Date dayBeforeFirst = Utils.calendarToDate(theDayBeforeFirst);
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Day_Total") + "<br/>" +
                Global.i18n("reports",
                    "Day_total_of_last_day_of_previous_month",
                    Integer.toString(Animal.getNumberOfAnimalsOnShelter(
                            dayBeforeFirst, 0, animalTypeID.intValue(), 0,
                            Animal.ALLAGES)))));

        for (int i = 1; i <= noDaysInMonth; i++) {
            tableAddCell(bold(Integer.toString(animalTotalNoFoster[i])));
        }

        // ==============================================================
        // ==============================================================
        // SUMMARY TOTALS FOR END OF REPORT
        // ==============================================================
        // ==============================================================

        // BROUGHT TO THE SHELTER ---------------------------
        // ==============================================================
        int totalAnimals = 0;
        int totReturnedAnimals = 0;
        int totTransferIn = 0;
        int totReturnFoster = 0;
        int totReturnOther = 0;

        // Add up the totals
        for (int i = 1; i <= noDaysInMonth; i++) {
            totalAnimals += animalSubTotalIn[i];
            totReturnedAnimals += returnedAnimals[i];
            totTransferIn += transferInAnimals[i];
            totReturnFoster += returnedFoster[i];
            totReturnOther += returnedOther[i];
        }

        // Build the summary string for Misc Brought In
        totalUnwantedSummary = Integer.toString(totalAnimals);

        boolean hasExtra = false;
        String additional = "";

        if (totReturnedAnimals > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_returned",
                Integer.toString(totReturnedAnimals));
            hasExtra = true;
        }

        if (totTransferIn > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_transferred_in",
                Integer.toString(totTransferIn));
            hasExtra = true;
        }

        if (totReturnFoster > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_returned_from_foster_homes",
                Integer.toString(totReturnFoster));
            hasExtra = true;
        }

        if (totReturnOther > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports",
                "_returned_from_other_(transfers,_released,_escaped,_stolen_or_reclaimed)",
                Integer.toString(totReturnOther));
            hasExtra = true;
        }

        if (hasExtra) {
            totalUnwantedSummary = Global.i18n("reports", "unwanted_including",
                    Integer.toString(totalAnimals), additional);
        }

        // ADOPTED ---------------------------
        // ==============================================================
        totalAnimals = 0;

        int totFostered = 0;
        int totTransferred = 0;
        int totReclaimed = 0;
        int totEscaped = 0;
        int totStolen = 0;
        int totReleased = 0;

        // Add up the totals
        for (int i = 1; i <= noDaysInMonth; i++) {
            totalAnimals += adoptedAnimals[i]; // animalSubTotalOut[i] for
                                               // all

            totFostered += fosteredOut[i];
            totTransferred += transferredOut[i];
            totReclaimed += reclaimedAnimals[i];
            totEscaped += escapedAnimals[i];
            totStolen += stolenAnimals[i];
            totReleased += releasedAnimals[i];
        }

        // Build the summary string for Misc Adopted
        totalAdoptedSummary = Integer.toString(totalAnimals);

        hasExtra = false;
        additional = "";

        if (totFostered > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_to_foster_home",
                Integer.toString(totFostered));
            hasExtra = true;
        }

        if (totTransferred > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_transferred_out",
                Integer.toString(totTransferred));
            hasExtra = true;
        }

        if (totReclaimed > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_returned_to_owner",
                Integer.toString(totReclaimed));
            hasExtra = true;
        }

        if (totEscaped > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_escaped",
                Integer.toString(totEscaped));
            hasExtra = true;
        }

        if (totStolen > 0) {
            if (!additional.equals("")) {
                additional += ", ";
            }

            additional += Global.i18n("reports", "_stolen",
                Integer.toString(totStolen));
            hasExtra = true;
        }

        if (hasExtra) {
            totalAdoptedSummary = Global.i18n("reports", "_plus_additional",
                    Integer.toString(totalAnimals), additional);
        }

        // --------------------------------------------------------------
        // Display table and finish
        // --------------------------------------------------------------
        tableFinishRow();
        tableFinish();
        addTable();

        // --------------------------------------------------------------
        // Show the summary
        // --------------------------------------------------------------
        createSummary();
    }

    /** Creates the summary figures for the end of the report */
    private void createSummary() throws Exception {
        addLevelThreeHeader(Global.i18n("reports", "Summary_Totals:"));
        tableNew();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Brought_to_the_Shelter")));
        tableAddCell(totalUnwantedSummary);
        tableFinishRow();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Adopted") + "</b>"));
        tableAddCell(totalAdoptedSummary);
        tableFinishRow();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Put_To_Sleep")));
        tableAddCell(totalPTSSummary);
        tableFinishRow();
        tableFinish();
        addTable();
    }
}
