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
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Date;


/**
 * Generates a report showing animals on the shelter who were adopted and
 * returned within a certain period, complete with their adoption histories,
 * including how long they were adopted for.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class ReturnedAnimals extends Report {
    private Date from = new Date();
    private Date to = new Date();
    private String sqlFrom = "";
    private String sqlTo = "";

    /** Creates a new instance of NeverVaccinated */
    public ReturnedAnimals(Date from, Date to) {
        this.from = from;
        this.to = to;
        this.sqlFrom = Utils.getSQLDateOnly(from);
        this.sqlTo = Utils.getSQLDateOnly(to);
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "Returned_animals_report_between_",
            Utils.formatDateLong(from), Utils.formatDateLong(to));
    }

    public void generateReport() {
        try {
            Animal an = new Animal();
            an.openRecordset("");

            if (an.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "There_are_no_animals_on_the_shelter"));

                return;
            }

            setStatusBarMax((int) an.getRecordCount());

            boolean shownOne = false;

            while (!an.getEOF()) {
                // See if the current animal has been returned in our period
                Adoption ad = new Adoption();
                ad.openRecordset("AnimalID = " + an.getID() +
                    " AND MovementType = " + Adoption.MOVETYPE_ADOPTION +
                    " AND ReturnDate >= '" + sqlFrom + "' AND ReturnDate <= '" +
                    sqlTo + "'");

                if (!ad.getEOF()) {
                    // It has - get all the adoption records for this animal
                    ad.openRecordset("AnimalID = " + an.getID() +
                        " AND MovementType = " + Adoption.MOVETYPE_ADOPTION);

                    // Show animal details
                    addParagraph(bold(Global.i18n("reports",
                                "returned_animal_details",
                                an.getReportAnimalName(), an.getShelterCode(),
                                an.getSexName(), an.getSpeciesName(),
                                an.getAnimalTypeName(),
                                an.getShelterLocationName())));

                    addHorizontalRule();

                    // Show adoption records
                    tableNew();
                    tableAddRow();
                    tableAddCell(bold(Global.i18n("reports", "Date_Adopted")));
                    tableAddCell(bold(Global.i18n("reports", "Date_Returned")));
                    tableAddCell(bold(Global.i18n("reports", "Reason_For_Return")));
                    tableAddCell("");
                    tableAddCell(bold(Global.i18n("reports", "Owner")));
                    tableAddCell(bold(Global.i18n("reports", "Length")));
                    tableFinishRow();

                    while (!ad.getEOF()) {
                        tableAddRow();
                        tableAddCell(Utils.formatDateLong(ad.getMovementDate()));
                        tableAddCell(Utils.formatDateLong(ad.getReturnDate()));
                        tableAddCell(LookupCache.getEntryReasonNameForID(
                                ad.getReturnedReasonID()));
                        tableAddCell(Utils.nullToEmptyString(
                                ad.getReasonForReturn()));
                        tableAddCell(ad.getOwner().getOwnerName());
                        tableAddCell(ad.getAdoptionLength());
                        tableFinishRow();

                        ad.moveNext();
                    }

                    tableFinish();
                    addTable();
                    addHorizontalRule();

                    shownOne = true;
                }

                incrementStatusBar();
                an.moveNext();
            }

            if (!shownOne) {
                addParagraph(Global.i18n("reports",
                        "No_animals_have_entered_the_shelter_and_been_returned_in_this_period."));
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "An_error_occurred_generating_the_report", e.getMessage()));
            Global.logException(e, getClass());
        }
    }
}
