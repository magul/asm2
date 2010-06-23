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

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Calendar;
import java.util.Date;


/**
 * Generates a report showing animals on the shelter
 * who were adopted between two dates and returned
 * after six months.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class AnimalReturnedPostSix extends Report {
    private String sqlFrom = "";
    private String sqlTo = "";
    private Date from = new Date();
    private Date to = new Date();

    public AnimalReturnedPostSix(Date from, Date to) {
        this.from = from;
        this.to = to;
        this.sqlFrom = Utils.getSQLDate(from);
        this.sqlTo = Utils.getSQLDate(to);
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports",
            "Animals_returned_after_6_months_of_adoption_at_",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            Adoption ad = new Adoption();
            ad.openRecordset("MovementDate >= '" + sqlFrom +
                "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
                Adoption.MOVETYPE_ADOPTION + " AND ReturnDate Is Not Null");

            if (ad.getEOF()) {
                addParagraph(Global.i18n("reports", "No_adoptions_on_file."));

                return;
            }

            setStatusBarMax((int) ad.getRecordCount());

            tableNew();
            tableAddRow();
            tableAddCell(bold(Global.i18n("reports", "Code")));
            tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
            tableAddCell(bold(Global.i18n("reports", "Type")));
            tableAddCell(bold(Global.i18n("reports", "Species")));
            tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
            tableAddCell(bold(Global.i18n("reports", "Date_Adopted")));
            tableAddCell(bold(Global.i18n("reports", "Reason_for_Return")));
            tableFinishRow();

            boolean shownOne = false;

            while (!ad.getEOF()) {
                // Calculate 6 months after adoption date
                Calendar sixMonthsAfter = Utils.dateToCalendar(ad.getMovementDate());
                sixMonthsAfter.add(Calendar.MONTH, 6);

                Calendar returnDate = Utils.dateToCalendar(ad.getReturnDate());

                // Is the return date after six months after adoption?
                if (returnDate.after(sixMonthsAfter)) {
                    tableAddRow();
                    tableAddCell(ad.getAnimal().getShelterCode());
                    tableAddCell(ad.getAnimal().getReportAnimalName());
                    tableAddCell(ad.getAnimal().getAnimalTypeName());
                    tableAddCell(ad.getAnimal().getSpeciesName());
                    tableAddCell(ad.getAnimal().getShelterLocationName());
                    tableAddCell(Utils.formatDateLong(ad.getMovementDate()));
                    tableAddCell(ad.getReasonForReturn());
                    tableFinishRow();
                    shownOne = true;
                }

                incrementStatusBar();
                ad.moveNext();
            }

            tableFinish();

            if (shownOne) {
                addTable();
            } else {
                addParagraph(Global.i18n("reports",
                        "No_animals_have_been_returned_after_6_months_of_adoption."));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
