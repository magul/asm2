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
package net.sourceforge.sheltermanager.asm.ui.diary;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.DiaryTaskDetail;
import net.sourceforge.sheltermanager.asm.bo.DiaryTaskHead;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.DateFormatException;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.asm.wordprocessor.OwnerDocument;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Vector;


/**
 * This class contains all code for executing diary tasks against an animal.
 *
 * @author Robin Rawson-Tetley
 */
public class DiaryTaskExecute extends ASMForm {
    private Animal animal = null;
    private Owner owner = null;
    private AnimalEdit animalparent = null;
    private OwnerEdit ownerparent = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.CheckBox chkOpenNotes;
    private UI.ComboBox cboDiaryTask;

    public DiaryTaskExecute(Animal theanimal, AnimalEdit parent) {
        animal = theanimal;
        this.animalparent = parent;
        init(Global.i18n("uidiary", "Execute_Diary_Task"),
            IconManager.getIcon(IconManager.SCREEN_EXECUTEDIARYTASK), "uidiary");
    }

    public DiaryTaskExecute(Owner theowner, OwnerEdit parent) {
        owner = theowner;
        this.ownerparent = parent;
        init(Global.i18n("uidiary", "Execute_Diary_Task"),
            IconManager.getIcon(IconManager.SCREEN_EXECUTEDIARYTASK), "uidiary");
    }

    public void dispose() {
        animal = null;
        animalparent = null;
        owner = null;
        ownerparent = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboDiaryTask);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboDiaryTask;
    }

    private void generateAnimalTask() {
        Diary newDiary = null;
        Calendar rollingDate = null;

        try {
            // Find all detail entries for the selected header
            DiaryTaskHead dth = new DiaryTaskHead();
            dth.openRecordset("Name Like '" +
                (String) cboDiaryTask.getSelectedItem() + "'");

            DiaryTaskDetail dtd = new DiaryTaskDetail();
            dtd.openRecordset("DiaryTaskHeadID = " + dth.getID());

            // Get the animal search tags
            AnimalDocument ad = new AnimalDocument(animal, true);

            // Start the rolling date at today
            rollingDate = Calendar.getInstance();

            // Create the diary note buffer
            newDiary = new Diary();
            newDiary.openRecordset("ID = 0");

            // Keep a list of the notes we create
            Vector notes = new Vector();

            while (!dtd.getEOF()) {
                newDiary.addNew();
                newDiary.setDiaryForName(dtd.getWhoFor());

                // Ask the user if there is a pivot of 0 specified
                if (dtd.getDayPivot().intValue() == 0) {
                    String thedate = Dialog.getDateInput(i18n("please_enter_a_date_for_task",
                                dth.getName(), dtd.getSubject()),
                            i18n("Enter_Date"));

                    // If the user doesn't supply a date, they must have
                    // pressed cancel, so stop now.
                    if (thedate.equals("")) {
                        // thedate =
                        // Utils.formatCalendarAsUserDate(Calendar.getInstance());
                        return;
                    }

                    Calendar mydate = null;

                    try {
                        mydate = Utils.dateToCalendar(Utils.parseDate(thedate));
                    } catch (ParseException e) {
                        Dialog.showError(i18n("invalid_date", thedate));

                        return;
                    }

                    newDiary.setDiaryDateTime(Utils.calendarToDate(mydate));
                    rollingDate = mydate;
                } else {
                    rollingDate.add(Calendar.DAY_OF_MONTH,
                        dtd.getDayPivot().intValue());
                    newDiary.setDiaryDateTime(Utils.calendarToDate(rollingDate));
                }

                newDiary.setSubject(dtd.getSubject());

                // Replace keys in note
                newDiary.setNote(ad.replaceInText(dtd.getNote()));

                // Create the animal link
                newDiary.setLinkID(animal.getID());
                newDiary.setLinkType(new Integer(Diary.LINKTYPE_ANIMAL));

                // Remember it
                notes.add(newDiary.getID());

                // Next task detail
                dtd.moveNext();
            }

            // Save the records
            newDiary.save(Global.currentUserName);

            // Update the list on the parent
            if (animalparent != null) {
                animalparent.updateDiary();
            }

            // Tell the user
            Dialog.showInformation(i18n("Diary_entries_successfully_created."),
                i18n("Diary_Task_Complete"));

            // If the box is ticked, open the diary notes
            if (chkOpenNotes.isSelected()) {
                for (int i = 0; i < notes.size(); i++) {
                    Diary d = new Diary();
                    d.openRecordset("ID = " + notes.get(i).toString());

                    DiaryEdit ed = new DiaryEdit();
                    ed.openForEdit(d);
                    Global.mainForm.addChild(ed);
                    d = null;
                    ed = null;
                }
            }

            dispose();
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_creating_the_diary_notes:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    private void generateOwnerTask() {
        Diary newDiary = null;
        Calendar rollingDate = null;

        try {
            // Find all detail entries for the selected header
            DiaryTaskHead dth = new DiaryTaskHead();
            dth.openRecordset("Name Like '" +
                (String) cboDiaryTask.getSelectedItem() + "'");

            DiaryTaskDetail dtd = new DiaryTaskDetail();
            dtd.openRecordset("DiaryTaskHeadID = " + dth.getID());

            // Get the owner search tags
            OwnerDocument od = new OwnerDocument(owner, true);

            // Start the rolling date at today
            rollingDate = Calendar.getInstance();

            // Create the diary note buffer
            newDiary = new Diary();
            newDiary.openRecordset("ID = 0");

            // Keep a list of the notes we create
            Vector notes = new Vector();

            while (!dtd.getEOF()) {
                newDiary.addNew();
                newDiary.setDiaryForName(dtd.getWhoFor());

                // Ask the user if there is a pivot of 0 specified
                if (dtd.getDayPivot().intValue() == 0) {
                    String thedate = Dialog.getDateInput(Global.i18n(
                                "uidiary", "enter_date_task", dth.getName(),
                                dtd.getSubject()), i18n("Enter_Date"));

                    // Cancel if the user cancelled the date
                    if (thedate.equals("")) {
                        // thedate =
                        // Utils.formatCalendarAsUserDate(Calendar.getInstance());
                        return;
                    }

                    Calendar mydate = null;

                    try {
                        mydate = Utils.dateToCalendar(Utils.parseDate(thedate));
                    } catch (ParseException e) {
                        Dialog.showError(i18n("invalid_date", thedate));

                        return;
                    }

                    newDiary.setDiaryDateTime(Utils.calendarToDate(mydate));
                    rollingDate = mydate;
                } else {
                    rollingDate.add(Calendar.DAY_OF_MONTH,
                        dtd.getDayPivot().intValue());
                    newDiary.setDiaryDateTime(Utils.calendarToDate(rollingDate));
                }

                newDiary.setSubject(dtd.getSubject());

                // Replace keys in note
                newDiary.setNote(od.replaceInText(dtd.getNote()));

                // Create the owner link
                newDiary.setLinkID(owner.getID());
                newDiary.setLinkType(new Integer(Diary.LINKTYPE_OWNER));

                // Remember it
                notes.add(newDiary.getID());

                // Next task detail
                dtd.moveNext();
            }

            // Save the records
            newDiary.save(Global.currentUserName);

            // Update the list on the parent
            if (ownerparent != null) {
                ownerparent.updateDiary();
            }

            // Tell the user
            Dialog.showInformation(i18n("Diary_entries_successfully_created."),
                i18n("Diary_Task_Complete"));

            // If the box is ticked, open the diary notes
            if (chkOpenNotes.isSelected()) {
                for (int i = 0; i < notes.size(); i++) {
                    Diary d = new Diary();
                    d.openRecordset("ID = " + notes.get(i).toString());

                    DiaryEdit ed = new DiaryEdit();
                    ed.openForEdit(d);
                    Global.mainForm.addChild(ed);
                    d = null;
                    ed = null;
                }
            }

            dispose();
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_creating_the_diary_notes:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(2));

        cboDiaryTask = UI.getCombo(i18n("Task:"),
                "SELECT Name FROM diarytaskhead WHERE RecordType = " +
                ((animal != null) ? DiaryTaskHead.RECORDTYPE_ANIMAL
                                  : DiaryTaskHead.RECORDTYPE_OWNER) +
                " ORDER BY Name", "Name");
        UI.addComponent(p, i18n("Task:"), cboDiaryTask);

        chkOpenNotes = (UI.CheckBox) UI.addComponent(p, "",
                UI.getCheckBox(i18n("edit_notes_after_creation"),
                    i18n("tick_this_box_to_edit_the_created_diary_notes")));

        btnOk = (UI.Button) p.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "actionOk")));
        btnCancel = (UI.Button) p.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(p, UI.BorderLayout.CENTER);
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public boolean formClosing() {
        return false;
    }

    public void actionOk() {
        if (animal != null) {
            generateAnimalTask();
        } else {
            generateOwnerTask();
        }
    }
}
