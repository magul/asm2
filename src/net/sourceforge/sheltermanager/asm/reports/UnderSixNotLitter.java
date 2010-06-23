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
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Calendar;


/**
 * Generates a report showing animals on the shelter under six months old who
 * are not part of a litter.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class UnderSixNotLitter extends Report {
    private int speciesID = 0;

    public UnderSixNotLitter() {
        // Get the species
        speciesID = Dialog.getSpecies();
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports",
            "Animals_on_the_shelter_under_six_months_old,_not_part_of_a_litter_at_",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            // Calculate 6 months before today
            Calendar sixMonthsBefore = Calendar.getInstance();
            sixMonthsBefore.add(Calendar.MONTH, -6);

            String theDate = Utils.getSQLDate(sixMonthsBefore);

            Animal an = new Animal();
            an.openRecordset(((speciesID != 0)
                ? (" SpeciesID = " + speciesID + " AND ") : "") +
                "DateOfBirth >= '" + theDate + "'" +
                " ORDER BY ShelterLocation");

            if (an.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "All_animals_on_the_shelter_of_that_species_are_older_than_six_months."));

                return;
            }

            setStatusBarMax((int) an.getRecordCount());
            tableNew();
            tableAddRow();
            tableAddCell(bold(Global.i18n("reports", "Code")));
            tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
            tableAddCell(bold(Global.i18n("reports", "Type")));
            tableAddCell(bold(Global.i18n("reports", "Internal_Location")));
            tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
            tableFinishRow();

            boolean shownOne = false;

            while (!an.getEOF()) {
                if (an.isAnimalOnShelter()) {
                    if (!an.isPartOfLitter()) {
                        tableAddRow();
                        tableAddCell(an.getShelterCode());
                        tableAddCell(an.getReportAnimalName());
                        tableAddCell(an.getAnimalTypeName());
                        tableAddCell(an.getShelterLocationName());
                        tableAddCell(an.getAcceptanceNumber());
                        tableFinishRow();
                        shownOne = true;
                    }
                }

                incrementStatusBar();
                an.moveNext();
            }

            tableFinish();

            if (shownOne) {
                addTable();
            } else {
                addParagraph(Global.i18n("reports",
                        "All_animals_on_the_shelter_of_the_selected_species_are_part_of_a_litter."));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
