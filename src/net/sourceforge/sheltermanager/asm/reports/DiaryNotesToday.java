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

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteria;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteriaListener;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.util.Date;


/**
 * Generates a report showing diary notes
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class DiaryNotesToday extends Report implements DiaryCriteriaListener {
    private final static long END_OF_DAY = (long) (1000 * 60 * 60 * 23);
    public int type = 0;
    public Date dateUpto = new Date();
    public Date dateFrom = new Date();
    public Date dateTo = new Date();
    public String ourtitle = "";

    public DiaryNotesToday() {
        Global.mainForm.addChild(new DiaryCriteria(this,
                Global.i18n("reports", "Diary_Criteria")));
    }

    /** Generates the report content */
    public void generateReport() {
        Diary diary = new Diary();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        // Set the time to 23:59 to ensure all notes are picked up
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);

        switch (type) {
        case DiaryCriteria.UPTO_TODAY:

            String today = net.sourceforge.sheltermanager.asm.utility.Utils.getSQLDate(cal);
            diary.openRecordset("DiaryDateTime <= '" + today + "'" +
                " AND DateCompleted Is Null ORDER BY DiaryForName, DiaryDateTime");

            break;

        case DiaryCriteria.UPTO_SPECIFIED:
            diary.openRecordset("DiaryDateTime <= '" +
                Utils.getSQLDateOnly(dateUpto) + "'" +
                " AND DateCompleted Is Null ORDER BY DiaryForName, DiaryDateTime");

            break;

        case DiaryCriteria.BETWEEN_TWO:
            diary.openRecordset("DiaryDateTime >= '" +
                Utils.getSQLDateOnly(dateFrom) + "' AND DiaryDateTime <= '" +
                Utils.getSQLDateOnly(dateTo) + "'" +
                " AND DateCompleted Is Null ORDER BY DiaryForName, DiaryDateTime");

            break;
        }

        // If there aren't any notes, end this now
        if (diary.getEOF()) {
            addParagraph(Global.i18n("reports",
                    "No_diary_notes_match_your_criteria."));

            return;
        }

        setStatusBarMax((int) diary.getRecordCount());

        String curName = "";
        boolean firstTable = true;

        while (!diary.getEOF()) {
            try {
                if (!curName.equalsIgnoreCase(diary.getDiaryForName())) {
                    if (!firstTable) {
                        tableFinish();
                        addTable();
                    } else {
                        firstTable = false;
                    }

                    addLevelTwoHeader(diary.getDiaryForName());

                    tableNew();
                    tableAddRow();
                    tableAddCell(bold(Global.i18n("reports", "Date")));
                    tableAddCell(bold(Global.i18n("reports", "Subject")));
                    tableAddCell("");
                    tableAddCell(bold(Global.i18n("reports", "Text")));
                    tableFinishRow();

                    curName = diary.getDiaryForName();
                }

                tableAddRow();
                tableAddCell(Utils.formatDateTimeLong(diary.getDiaryDateTime()));
                tableAddCell(diary.getSubject());
                tableAddCell(diary.getLinkInfoThis());
                tableAddCell(diary.getNote());
                tableFinishRow();

                diary.moveNext();
                incrementStatusBar();
            } catch (Exception e) {
                // Break and carry on if an exception occurs
                break;
            }
        }

        // Add the final unwritten table specification
        tableFinish();
        addTable();
    }

    /** Returns the report title */
    public String getTitle() {
        switch (type) {
        case DiaryCriteria.UPTO_TODAY:
            return Global.i18n("reports", "Diary_for_",
                net.sourceforge.sheltermanager.asm.utility.Utils.getReadableTodaysDate());

        case DiaryCriteria.UPTO_SPECIFIED:
            return Global.i18n("reports", "Diary_upto_",
                Utils.formatDateLong(dateUpto));

        case DiaryCriteria.BETWEEN_TWO:
            return Global.i18n("reports", "Diary_between_",
                Utils.formatDateLong(dateFrom), Utils.formatDateLong(dateTo));
        }

        return java.util.ResourceBundle.getBundle("locale/reports")
                                       .getString("Error");
    }

    public void dateChosen(Date from, Date to) {
        dateFrom = from;
        dateTo = to;
        dateFrom.setTime(dateFrom.getTime() + (END_OF_DAY));
        type = DiaryCriteria.BETWEEN_TWO;
        this.start();
    }

    public void normalChosen() {
        type = DiaryCriteria.UPTO_TODAY;
        this.start();
    }

    public void uptoChosen(Date upto) {
        dateUpto = upto;
        dateUpto.setTime(dateUpto.getTime() + (END_OF_DAY));
        type = DiaryCriteria.UPTO_SPECIFIED;
        this.start();
    }
}
