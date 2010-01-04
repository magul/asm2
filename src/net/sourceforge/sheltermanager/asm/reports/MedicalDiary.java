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

import net.sourceforge.sheltermanager.asm.bo.AnimalMedicalTreatment;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteria;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteriaListener;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Calendar;
import java.util.Date;


/**
 * Generates a report showing all animals on the shelter who require a medical
 * treatment.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class MedicalDiary extends Report implements DiaryCriteriaListener {
    public int type = 0;
    public Date dateUpto = new Date();
    public Date dateFrom = new Date();
    public Date dateTo = new Date();
    public String ourtitle = "";

    /** Creates a new instance of MedicalDiary */
    public MedicalDiary() {
        Global.mainForm.addChild(new DiaryCriteria(this,
                Global.i18n("reports", "Medical_Diary_Criteria")));
    }

    public String getTitle() {
        switch (type) {
        case DiaryCriteria.UPTO_TODAY:
            return Global.i18n("reports", "Medical_Diary_for",
                Utils.getReadableTodaysDate());

        case DiaryCriteria.UPTO_SPECIFIED:
            return Global.i18n("reports", "Medical_Diary_upto",
                Utils.formatDateLong(dateUpto));

        case DiaryCriteria.BETWEEN_TWO:
            return Global.i18n("reports", "Medical_Diary_between",
                Utils.formatDateLong(dateFrom), Utils.formatDateLong(dateTo));
        }

        return Global.i18n("reports", "Error");
    }

    public void generateReport() {
        try {
            AnimalMedicalTreatment amt = new AnimalMedicalTreatment();

            switch (type) {
            case DiaryCriteria.UPTO_TODAY:

                Calendar upto = Calendar.getInstance();
                upto.set(Calendar.HOUR_OF_DAY, 23);
                upto.set(Calendar.MINUTE, 59);
                upto.set(Calendar.SECOND, 59);
                amt.openRecordset("DateGiven Is Null AND DateRequired <= '" +
                    Utils.getSQLDate(upto) +
                    "' ORDER BY DateRequired, AnimalID");

                break;

            case DiaryCriteria.UPTO_SPECIFIED:
                amt.openRecordset("DateGiven Is Null AND DateRequired <= '" +
                    Utils.getSQLDate(dateUpto) +
                    "' ORDER BY DateRequired, AnimalID");

                break;

            case DiaryCriteria.BETWEEN_TWO:
                amt.openRecordset("DateGiven Is Null AND DateRequired >= '" +
                    Utils.getSQLDate(dateFrom) + "' AND DateRequired <= '" +
                    Utils.getSQLDate(dateTo) +
                    "' ORDER BY DateRequired, AnimalID");

                break;
            }

            if (amt.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "No_animals_match_your_criteria."));
            } else {
                setStatusBarMax((int) amt.getRecordCount());
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Code")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Treatment_Type")));
                tableAddCell(bold(Global.i18n("reports", "Dosage")));
                tableAddCell(bold(Global.i18n("reports", "Number")));
                tableAddCell(bold(Global.i18n("reports", "Date_Required")));
                tableAddCell(bold(Global.i18n("reports", "Regime_Comments")));
                tableAddCell(bold(Global.i18n("reports", "Comments")));
                tableFinishRow();

                while (!amt.getEOF()) {
                    try {
                        // Don't include off shelter animals
                        boolean okToAdd = amt.getAnimal().fastIsAnimalOnShelter();

                        if (okToAdd) {
                            tableAddRow();
                            tableAddCell(amt.getAnimal().getShelterCode());
                            tableAddCell(amt.getAnimal().getReportAnimalName());
                            tableAddCell(amt.getAnimal().getAnimalTypeName());
                            tableAddCell(amt.getAnimal().getSpeciesName());
                            tableAddCell(amt.getAnimal().getShelterLocationName());
                            tableAddCell(amt.getAnimalMedical()
                                            .getTreatmentName());
                            tableAddCell(amt.getAnimalMedical().getDosage());
                            tableAddCell(amt.getTreatmentNumber() + " / " +
                                amt.getTotalTreatments());
                            tableAddCell(Utils.formatDateLong(
                                    amt.getDateRequired()));
                            tableAddCell(Utils.nullToEmptyString(
                                    amt.getAnimalMedical().getComments()));
                            tableAddCell(Utils.nullToEmptyString(
                                    amt.getComments()));
                            tableFinishRow();
                        }
                    } catch (Exception e) {
                    }

                    amt.moveNext();
                    incrementStatusBar();
                }

                tableFinish();
                addTable();
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "An_error_occurred_generating_the_report", e.getMessage()));
            Global.logException(e, getClass());
        }
    }

    public void dateChosen(Date from, Date to) {
        dateFrom = from;
        dateTo = to;
        type = DiaryCriteria.BETWEEN_TWO;
        this.start();
    }

    public void normalChosen() {
        type = DiaryCriteria.UPTO_TODAY;
        this.start();
    }

    public void uptoChosen(Date upto) {
        dateUpto = upto;
        type = DiaryCriteria.UPTO_SPECIFIED;
        this.start();
    }
}
