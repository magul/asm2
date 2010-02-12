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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Calendar;


/**
 * Generates a report showing animals on the shelter aged over the specified
 * length who have not been combi tested. Assumes cat == speciesid == 2
 *
 * @author Robin Rawson-Tetley
 */
public class UnCombiTestedCats extends Report {
    private int weeksOld = 14;

    public UnCombiTestedCats() {
        // Get the period
        String period = (String) Dialog.getInput(Global.i18n("reports",
                    "Include_cats_aged_how_many_weeks_and_above?"),
                Global.i18n("reports", "Select_Age"),
                new String[] {
                    Global.i18n("reports", "1_week"),
                    Global.i18n("reports", "2_weeks"),
                    Global.i18n("reports", "3_weeks"),
                    Global.i18n("reports", "4_weeks"),
                    Global.i18n("reports", "5_weeks"),
                    Global.i18n("reports", "6_weeks"),
                    Global.i18n("reports", "7_weeks"),
                    Global.i18n("reports", "8_weeks"),
                    Global.i18n("reports", "9_weeks"),
                    Global.i18n("reports", "10_weeks"),
                    Global.i18n("reports", "11_weeks"),
                    Global.i18n("reports", "12_weeks"),
                    Global.i18n("reports", "13_weeks"),
                    Global.i18n("reports", "14_weeks"),
                    Global.i18n("reports", "15_weeks"),
                    Global.i18n("reports", "16_weeks"),
                    Global.i18n("reports", "26_weeks"),
                    Global.i18n("reports", "52_weeks")
                }, Global.i18n("reports", "14_weeks"));

        try {
            weeksOld = Integer.parseInt(period.substring(0, 2).trim());
            this.start();
        } catch (Exception e) {
        }
    }

    public String getTitle() {
        return Global.i18n("reports",
            "Cats_not_combi-tested_on_the_shelter_aged_",
            Integer.toString(weeksOld), Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            // Calculate selected time period before today
            Calendar weeksBefore = Calendar.getInstance();
            weeksBefore.add(Calendar.WEEK_OF_YEAR, -weeksOld);

            String theDate = Utils.getSQLDateOnly(weeksBefore);

            // Get cat species
            Integer catSpecies = null;
            catSpecies = new Integer(2);

            Animal an = new Animal();
            an.openRecordset("CombiTested = 0 AND DateOfBirth <= '" + theDate +
                "'" + " AND SpeciesID = " + catSpecies +
                " Order By ShelterLocation");

            if (an.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "No_cats_matching_your_criteria_were_found_on_the_shelter."));

                return;
            }

            setStatusBarMax((int) an.getRecordCount());

            tableNew();
            tableAddRow();
            tableAddCell(bold(Global.i18n("reports", "Code")));
            tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
            tableAddCell(bold(Global.i18n("reports", "Age")));
            tableAddCell(bold(Global.i18n("reports", "Type")));
            tableAddCell(bold(Global.i18n("reports", "Internal_Location")));
            tableFinishRow();

            boolean shownOne = false;

            while (!an.getEOF()) {
                if (an.isAnimalOnShelter()) {
                    tableAddRow();
                    tableAddCell(an.getShelterCode());
                    tableAddCell(an.getReportAnimalName());
                    tableAddCell(an.getAge());
                    tableAddCell(an.getAnimalTypeName());
                    tableAddCell(an.getShelterLocationName());
                    tableFinishRow();

                    shownOne = true;
                }

                incrementStatusBar();
                an.moveNext();
            }

            tableFinish();

            if (shownOne) {
                addTable();
            } else {
                addParagraph(Global.i18n("reports",
                        "No_cats_matching_your_criteria_were_found_on_the_shelter."));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
