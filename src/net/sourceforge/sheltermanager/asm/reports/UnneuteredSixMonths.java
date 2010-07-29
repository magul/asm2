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
 * Generates a report showing animals on the shelter aged over 6 months who have
 * not been neutered.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class UnneuteredSixMonths extends Report {
    private int speciesID = 0;

    public UnneuteredSixMonths() {
        // Get the species
        speciesID = Dialog.getSpecies();
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports",
            "Non-neutered_animals_on_the_shelter_aged_6_months_or_over_at_",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            // Calculate 6 months before today
            Calendar sixMonthsBefore = Calendar.getInstance();
            sixMonthsBefore.add(Calendar.MONTH, -6);

            String theDate = Utils.getSQLDate(sixMonthsBefore);

            Animal an = new Animal();
            an.openRecordset("Neutered = 0 AND DateOfBirth <= '" + theDate +
                "'" +
                ((speciesID != 0) ? (" AND SpeciesID = " + speciesID) : "") +
                " Order By ShelterLocation");

            if (an.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "No_animals_aged_over_six_months_on_the_shelter_require_neutering."));

                return;
            }

            setStatusBarMax((int) an.getRecordCount());
            tableNew();
            tableAddRow();
            tableAddCell(bold(Global.i18n("reports", "Code")));
            tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
            tableAddCell(bold(Global.i18n("reports", "Type")));
            tableAddCell(bold(Global.i18n("reports", "Internal_Location")));
            tableFinishRow();

            boolean shownOne = false;

            while (!an.getEOF()) {
                if (an.isAnimalOnShelter()) {
                    tableAddRow();
                    tableAddCell(code(an));
                    tableAddCell(an.getReportAnimalName());
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
                        "No_animals_aged_over_six_months_on_the_shelter_require_neutering."));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
