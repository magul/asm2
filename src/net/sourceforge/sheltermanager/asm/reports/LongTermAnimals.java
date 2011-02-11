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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Calendar;


/**
 * Generates a report showing animals who have been on the shelter longer than 3
 * months
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class LongTermAnimals extends Report {
    public LongTermAnimals() {
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "Long_Term_Animals_title",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            // Calculate 3 months before today
            Calendar threeMonthsBefore = Calendar.getInstance();
            threeMonthsBefore.add(Calendar.MONTH, -3);

            Animal an = new Animal();
            an.openRecordset("ID > 0 Order By DateBroughtIn");

            if (an.getEOF()) {
                addParagraph(Global.i18n("reports", "No_animals_on_file."));

                return;
            }

            setStatusBarMax((int) an.getRecordCount());

            tableNew();
            tableAddRow();
            tableAddCell(bold(Global.i18n("reports", "Code")));
            tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
            tableAddCell(bold(Global.i18n("reports", "Type")));
            tableAddCell(bold(Global.i18n("reports", "Species")));
            tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
            tableAddCell(bold(Global.i18n("reports", "Date_Of_Entry")));
            tableFinishRow();

            boolean shownOne = false;

            while (!an.getEOF()) {
                if (an.isAnimalOnShelter()) {
                    Calendar recentdate = Utils.dateToCalendar(an.getMostRecentEntry());

                    if (recentdate.before(threeMonthsBefore)) {
                        tableAddRow();
                        tableAddCell(code(an));
                        tableAddCell(an.getReportAnimalName());
                        tableAddCell(an.getAnimalTypeName());
                        tableAddCell(an.getSpeciesName());
                        tableAddCell(an.getShelterLocationName());
                        tableAddCell(Utils.formatDateLong(
                                an.getMostRecentEntry()));
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
                        "There_are_no_long_term_animals_on_the_shelter."));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
