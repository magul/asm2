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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteria;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteriaListener;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;


/**
 * Generates a report showing all animals on/off the shelter who require a
 * medical treatment.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class VaccinationDiary extends Report implements DiaryCriteriaListener {
    public static final int VACC_ALL = 0;
    public static final int VACC_ONSHELTER = 1;
    public static final int VACC_OFFSHELTER = 2;
    private int reportType = 0;
    public int type = 0;
    public Date dateUpto = new Date();
    public Date dateFrom = new Date();
    public Date dateTo = new Date();
    public String ourtitle = "";

    /** Creates a new instance of VaccinationDiary */
    public VaccinationDiary(int theReportType) {
        reportType = theReportType;
        Global.mainForm.addChild(new DiaryCriteria(this,
                Global.i18n("reports", "Vaccination_Diary_Criteria")));
    }

    public String getTitle() {
        String reportName = "";

        switch (reportType) {
        case VACC_ALL:
            reportName = Global.i18n("reports", "(All_Animals)");

            break;

        case VACC_ONSHELTER:
            reportName = Global.i18n("reports", "(On_Shelter)");

            break;

        case VACC_OFFSHELTER:
            reportName = Global.i18n("reports", "(Off_Shelter)");

            break;
        }

        switch (type) {
        case DiaryCriteria.UPTO_TODAY:
            return Global.i18n("reports", "Vaccination_Diary_for", reportName,
                Utils.getReadableTodaysDate());

        case DiaryCriteria.UPTO_SPECIFIED:
            return Global.i18n("reports", "Vaccination_Diary_upto", reportName,
                Utils.formatDateLong(dateUpto));

        case DiaryCriteria.BETWEEN_TWO:
            return Global.i18n("reports", "Vaccination_Diary_between",
                reportName, Utils.formatDateLong(dateFrom),
                Utils.formatDateLong(dateTo));
        }

        return Global.i18n("reports", "Error");
    }

    public void generateReport() {
        try {
            String sql = "SELECT av.DateRequired, av.Comments, a.AnimalName, " +
                "a.ShelterCode, a.ShortCode, atp.AnimalType, il.LocationName, " +
                "vt.VaccinationType " + "FROM animalvaccination av " +
                "INNER JOIN animal a ON a.ID = av.AnimalID " +
                "INNER JOIN animaltype atp ON atp.ID = a.AnimalTypeID " +
                "INNER JOIN internallocation il ON il.ID = a.ShelterLocation " +
                "INNER JOIN vaccinationtype vt ON vt.ID = av.VaccinationID " +
                "WHERE ";

            switch (type) {
            case DiaryCriteria.UPTO_TODAY:

                Calendar upto = Calendar.getInstance();
                upto.set(Calendar.HOUR_OF_DAY, 23);
                upto.set(Calendar.MINUTE, 59);
                upto.set(Calendar.SECOND, 59);
                sql += ("DateOfVaccination Is Null AND DateRequired <= '" +
                Utils.getSQLDate(upto) + "'");

                break;

            case DiaryCriteria.UPTO_SPECIFIED:
                sql += ("DateOfVaccination Is Null AND DateRequired <= '" +
                Utils.getSQLDate(dateUpto) + "'");

                break;

            case DiaryCriteria.BETWEEN_TWO:
                sql += ("DateOfVaccination Is Null AND DateRequired >= '" +
                Utils.getSQLDate(dateFrom) + "' AND DateRequired <= '" +
                Utils.getSQLDate(dateTo) + "'");

                break;
            }

            switch (reportType) {
            case VACC_ALL:
                break;

            case VACC_ONSHELTER:
                sql += " AND Archived = 0";

                break;

            case VACC_OFFSHELTER:
                sql += " AND Archived = 1";

                break;
            }

            SQLRecordset av = new SQLRecordset(sql);

            if (av.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "No_animals_match_your_criteria."));
            } else {
                setStatusBarMax((int) av.getRecordCount());
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Code")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Vaccination_Type")));
                tableAddCell(bold(Global.i18n("reports", "Date_Required")));
                tableAddCell(bold(Global.i18n("reports", "Comments")));
                tableFinishRow();

                while (!av.getEOF()) {
                    tableAddRow();
                    tableAddCell(code(av));
                    tableAddCell(av.getString("AnimalName"));
                    tableAddCell(av.getString("AnimalType"));
                    tableAddCell(av.getString("LocationName"));
                    tableAddCell(av.getString("VaccinationType"));
                    tableAddCell(Utils.formatDateLong(av.getDate("DateRequired")));
                    tableAddCell(av.getString("Comments"));
                    tableFinishRow();
                    av.moveNext();
                    incrementStatusBar();
                }

                tableFinish();
                addTable();
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
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
