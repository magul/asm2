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
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteria;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteriaListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Date;


/**
 * Generates a report that mixes data from the OnShelter vaccination diary and
 * diary notes addressed to Vets.
 *
 * The whole shebang is then broken down and ordered by internal location.
 *
 * Diary notes assigned to the vets user that aren't linked to an animal
 * are shown in a section at the end.
 *
 * @author Robin Rawson-Tetley
 */
public class Vets extends Report implements DiaryCriteriaListener {
    public int type = 0;
    public Date dateUpto = new Date();
    public Date dateFrom = new Date();
    public Date dateTo = new Date();
    public String ourtitle = "";

    public Vets() {
        Global.mainForm.addChild(new DiaryCriteria(this,
                Global.i18n("reports", "Vet_Diary_Criteria")));
    }

    /** Generates the report content */
    public void generateReport() {
        Diary diary = new Diary();
        Diary diarynol = new Diary();
        AnimalVaccination av = new AnimalVaccination();

        java.util.Calendar cal = java.util.Calendar.getInstance();
        // Set the time to 23:59 to ensure all notes are picked up
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);

        String today = net.sourceforge.sheltermanager.asm.utility.Utils.getSQLDate(cal);

        switch (type) {
        case DiaryCriteria.UPTO_TODAY:
            diary.openRecordset("DiaryDateTime <= '" + today + "'" +
                " AND LinkType = " + Diary.LINKTYPE_ANIMAL +
                " AND DateCompleted Is Null AND DiaryForName Like '" +
                Global.getVetsDiaryUser() + "%'" +
                " ORDER BY DiaryForName, DiaryDateTime");
            diarynol.openRecordset("DiaryDateTime <= '" + today + "'" +
                " AND DateCompleted Is Null AND DiaryForName Like '" +
                Global.getVetsDiaryUser() + "%' AND LinkType = 0 " +
                " ORDER BY DiaryForName, DiaryDateTime");
            av.openRecordset("DateOfVaccination Is Null AND DateRequired <= '" +
                today + "'");

            break;

        case DiaryCriteria.UPTO_SPECIFIED:
            diary.openRecordset("DiaryDateTime <= '" +
                Utils.getSQLDate(dateUpto) + "'" + " AND LinkType = " +
                Diary.LINKTYPE_ANIMAL +
                " AND DateCompleted Is Null AND DiaryForName Like '" +
                Global.getVetsDiaryUser() + "%'" +
                " ORDER BY DiaryForName, DiaryDateTime");
            diarynol.openRecordset("DiaryDateTime <= '" +
                Utils.getSQLDate(dateUpto) + "'" +
                " AND DateCompleted Is Null AND DiaryForName Like '" +
                Global.getVetsDiaryUser() + "%' AND LinkType = 0" +
                " ORDER BY DiaryForName, DiaryDateTime");
            av.openRecordset("DateOfVaccination Is Null AND DateRequired <= '" +
                Utils.getSQLDate(dateUpto) + "'");

            break;

        case DiaryCriteria.BETWEEN_TWO:
            diary.openRecordset("DiaryDateTime >= '" +
                Utils.getSQLDate(dateFrom) + "' AND DiaryDateTime <= '" +
                Utils.getSQLDate(dateTo) + "'" + " AND LinkType = " +
                Diary.LINKTYPE_ANIMAL +
                " AND DateCompleted Is Null AND DiaryForName Like '" +
                Global.getVetsDiaryUser() + "%'" +
                " ORDER BY DiaryForName, DiaryDateTime");
            diarynol.openRecordset("DiaryDateTime >= '" +
                Utils.getSQLDate(dateFrom) + "' AND DiaryDateTime <= '" +
                Utils.getSQLDate(dateTo) + "'" +
                " AND DateCompleted Is Null AND DiaryForName Like '" +
                Global.getVetsDiaryUser() + "%' AND LinkType = 0" +
                " ORDER BY DiaryForName, DiaryDateTime");
            av.openRecordset("DateOfVaccination Is Null AND DateRequired >= '" +
                Utils.getSQLDate(dateFrom) + "' AND DateRequired <= '" +
                Utils.getSQLDate(dateTo) + "'");

            break;
        }

        // Get a list of all internal locations to enumerate through
        SQLRecordset il = LookupCache.getInternalLocationLookup();
        try {
        	il.moveFirst();
        }
        catch (Exception e) {
        	Global.logException(e, getClass());
        }

        setStatusBarMax((int) il.getRecordCount());

        boolean startedDisplay = false;

        // Work out what we are showing
        boolean showDiary = !diary.getEOF();
        boolean showNolinkDiary = !diarynol.getEOF();
        boolean showVacc = !av.getEOF();
        boolean showSomething = false;

        while (!il.getEOF()) {
            try {
                // Whether we have started showing anything
                // for this location.
                startedDisplay = false;

                if (showDiary) {
                    diary.moveFirst();
                }

                if (showVacc) {
                    av.moveFirst();
                }

                // Loop through diary note records and
                // add them if they are for this location
                while (!diary.getEOF()) {
                    Animal a = new Animal();
                    a.openRecordset("ID = " + diary.getLinkID());

                    if (a.getShelterLocation().equals(il.getField("ID"))) {
                        if (!startedDisplay) {
                            addLevelTwoHeader(il.getField("LocationName").toString());
                            tableNew();
                            tableAddRow();
                            tableAddCell(bold(Global.i18n("reports", "Date")));
                            tableAddCell(bold(Global.i18n("reports", "Code")));
                            tableAddCell(bold(Global.i18n("reports", "Name")));
                            tableAddCell(bold(Global.i18n("reports", "Location")));
                            tableAddCell(bold(Global.i18n("reports", "Type")));
                            tableAddCell(bold(Global.i18n("reports", "Colour")));
                            tableAddCell(bold(Global.i18n("reports", "Age")));
                            tableAddCell(bold(Global.i18n("reports", "Features")));
                            tableAddCell(bold(Global.i18n("reports",
                                        "Description")));
                            tableFinishRow();
                            startedDisplay = true;
                        }

                        tableAddRow();
                        tableAddCell(Utils.formatDateTimeLong(
                                diary.getDiaryDateTime()));
                        tableAddCell(a.getShortCode());
                        tableAddCell(a.getAnimalName());
                        tableAddCell(a.getAnimalLocationAtDateByName(new Date()));
                        tableAddCell(diary.getSubject());
                        tableAddCell(a.getBaseColourName());
                        tableAddCell(a.getAge());
                        tableAddCell(a.getMarkings());
                        tableAddCell(diary.getNote());
                        tableFinishRow();
                        showSomething = true;
                    }

                    diary.moveNext();
                }

                // Loop through vacc diary records and
                // add them if they are for this location
                // and the animal is still on the shelter
                while (!av.getEOF()) {
                    Animal a = av.getAnimal();

                    if (a.getShelterLocation().equals(il.getField("ID"))) {
                        if (a.getAnimalLocationAtDate(new Date()) == Animal.ONSHELTER) {
                            if (!startedDisplay) {
                                addLevelTwoHeader(il.getField("LocationName").toString());
                                tableNew();
                                tableAddRow();
                                tableAddCell(bold(Global.i18n("reports", "Date")));
                                tableAddCell(bold(Global.i18n("reports", "Code")));
                                tableAddCell(bold(Global.i18n("reports", "Name")));
                                tableAddCell(bold(Global.i18n("reports",
                                            "Location")));
                                tableAddCell(bold(Global.i18n("reports", "Type")));
                                tableAddCell(bold(Global.i18n("reports",
                                            "Colour")));
                                tableAddCell(bold(Global.i18n("reports", "Age")));
                                tableAddCell(bold(Global.i18n("reports",
                                            "Features")));
                                tableAddCell(bold(Global.i18n("reports",
                                            "Description")));
                                tableFinishRow();
                                startedDisplay = true;
                            }

                            tableAddRow();
                            tableAddCell(Utils.formatDate(av.getDateRequired()));
                            tableAddCell(a.getShortCode());
                            tableAddCell(a.getAnimalName());
                            tableAddCell(a.getAnimalLocationAtDateByName(
                                    new Date()));
                            tableAddCell(av.getVaccinationTypeName());
                            tableAddCell(a.getBaseColourName());
                            tableAddCell(a.getAge());
                            tableAddCell(a.getMarkings());
                            tableAddCell(Utils.nullToEmptyString(
                                    av.getComments()));
                            tableFinishRow();
                            showSomething = true;
                        }
                    }

                    av.moveNext();
                }

                // If we displayed something, show it
                if (startedDisplay) {
                    this.tableFinish();
                    addTable();
                }

                il.moveNext();
                incrementStatusBar();
            } catch (Exception e) {
                // Break and carry on if an exception occurs
                break;
            }
        }

        // Do we have some diary entries not linked to an animal/location?
        // If so, show them at the end.
        if (showNolinkDiary) {
            showSomething = true;
            addLevelTwoHeader(Global.getVetsDiaryUser());
            tableNew();
            tableAddRow();
            tableAddCell(bold(Global.i18n("reports", "Date")));
            tableAddCell(bold(Global.i18n("reports", "Subject")));
            tableAddCell(bold(Global.i18n("reports", "Description")));
            tableFinishRow();

            try {
                while (!diarynol.getEOF()) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(diarynol.getDiaryDateTime()));
                    tableAddCell(diarynol.getSubject());
                    tableAddCell(diarynol.getNote());
                    tableFinishRow();
                    diarynol.moveNext();
                }

                tableFinish();
                addTable();
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // Did we show anything?
        if (!showSomething) {
            addParagraph(Global.i18n("reports",
                    "No_diary_or_vaccination_entries_matched_your_criteria."));
        }
    }

    /** Returns the report title */
    public String getTitle() {
        switch (type) {
        case DiaryCriteria.UPTO_TODAY:
            return Global.i18n("reports", "Vet_Diary_for_",
                net.sourceforge.sheltermanager.asm.utility.Utils.getReadableTodaysDate());

        case DiaryCriteria.UPTO_SPECIFIED:
            return Global.i18n("reports", "Vet_Diary_upto_",
                Utils.formatDateLong(dateUpto));

        case DiaryCriteria.BETWEEN_TWO:
            return Global.i18n("reports", "Vet_Diary_between_",
                Utils.formatDateLong(dateFrom), Utils.formatDateLong(dateTo));
        }

        return Global.i18n("reports", "Error");
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
