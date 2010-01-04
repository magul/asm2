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

import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.text.ParseException;

import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for editing diary notes.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class DiaryEdit extends ASMForm {
    private Diary diary = null;
    private int linkID = 0;
    private int linkType = 0;
    private DiarySelector parent = null;
    private UI.Button btnOk;
    private UI.ComboBox cboFor;
    private UI.Button btnCancel;
    private UI.TextArea txtAudit;
    private DateField txtCompleted;
    private DateField txtDate;
    private UI.TextArea txtNote;
    private UI.TextField txtSubject;
    private UI.TextField txtTime;
    private String audit = null;

    /** Creates new form EditDiary */
    public DiaryEdit() {
        init("", IconManager.getIcon(IconManager.SCREEN_EDITDIARY), "uidiary");
    }

    public void dispose() {
        diary.free();
        parent = null;
        diary = null;
        super.dispose();
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    /**
     * Sets the tab ordering for the screen using the FlexibleFocusManager class
     */
    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboFor);
        ctl.add(txtDate.getTextField());
        ctl.add(txtTime);
        ctl.add(txtSubject);
        ctl.add(txtCompleted.getTextField());
        ctl.add(txtNote);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboFor;
    }

    public void openForNew(int linkID, int linkType, DiarySelector parent) {
        this.linkID = linkID;
        this.linkType = linkType;
        this.parent = parent;
        openForNew();
    }

    public void openForNew() {
        openForNew(null);
    }

    /** Sets the screen into creation mode of a new diary note */
    public void openForNew(String diaryFor) {
        // Default the date and time fields
        Date d = new Date();

        try {
            txtDate.setText(Utils.formatDate(d));
            txtTime.setText(Utils.formatTime(d));

            if (diaryFor != null) {
                cboFor.setSelectedItem(diaryFor);
            }
        } catch (Exception e) {
        }

        // Create a new diary object
        try {
            diary = new Diary();
            diary.openRecordset("ID = 0");
            diary.addNew();
        } catch (CursorEngineException e) {
            Dialog.showError(Global.i18n("uidiary",
                    "An_error_occurred_creating_a_new_diary_record:_") +
                e.getMessage());
        }

        // We're ready to go
        if (linkID != 0) {
            this.setTitle(i18n("new_diary_title",
                    Diary.getLinkInfo(linkID, linkType)));
        } else {
            this.setTitle(i18n("Create_New_Diary_Note"));
        }
    }

    /**
     * Sets the screen into editing mode of the passed diary object.
     *
     * @param thediary
     *            The Diary object to edit.
     */
    public void openForEdit(Diary diary, DiarySelector parent) {
        this.diary = diary;
        this.parent = parent;
        loadData();
    }

    public void openForEdit(Diary diary) {
        this.diary = diary;
        loadData();
    }

    /**
     * Loads the data from the object into the controls.
     */
    public void loadData() {
        try {
            // Auditing information
            audit = i18n("created_lastchange",
                    Utils.formatDateTimeLong(diary.getCreatedDate()),
                    diary.getCreatedBy(),
                    Utils.formatDateTimeLong(diary.getLastChangedDate()),
                    diary.getLastChangedBy());

            cboFor.setSelectedItem(diary.getDiaryForName());
            txtSubject.setText(diary.getSubject());
            txtNote.setText(diary.getNote());

            linkID = diary.getLinkID().intValue();
            linkType = diary.getLinkType().intValue();

            // Date and Time
            try {
                txtDate.setText(Utils.formatDate(diary.getDiaryDateTime()));
                txtTime.setText(Utils.formatTime(diary.getDiaryDateTime()));
                txtCompleted.setText(Utils.formatDate(diary.getDateCompleted()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            if (linkID != 0) {
                this.setTitle(i18n("edit_diary_title",
                        Diary.getLinkInfo(linkID, linkType)));
            } else {
                this.setTitle(i18n("Edit_Diary_Note"));
            }
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("An_error_occurred_reading_the_diary_note:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Saves the controls back to the business object and saves it
     *
     * @return true if the save was successful.
     */
    public boolean saveData() {
        try {
            diary.setDiaryForName((String) cboFor.getSelectedItem());
            diary.setSubject(txtSubject.getText().trim());
            diary.setNote(txtNote.getText().trim());
            diary.setLinkID(new Integer(linkID));
            diary.setLinkType(new Integer(linkType));

            // Date and Time
            try {
                diary.setDiaryDateTime(Utils.parseDate(txtDate.getText(),
                        txtTime.getText()));
                diary.setDateCompleted(Utils.parseDate(txtCompleted.getText()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }

            try {
                diary.save(Global.currentUserName);

                if (parent != null) {
                    parent.updateList();
                }

                dispose();

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
            }
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("uidiary",
                    "An_error_occurred_reading_the_diary_note:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.Panel pnlTop = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pnlMid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pnlBot = UI.getPanel(UI.getFlowLayout());

        add(pnlTop, UI.BorderLayout.NORTH);
        add(pnlMid, UI.BorderLayout.CENTER);
        add(pnlBot, UI.BorderLayout.SOUTH);

        cboFor = UI.getCombo("SELECT UserName FROM users ORDER BY UserName",
                "UserName");
        cboFor.setEditable(true);
        cboFor.addItem(Global.getVetsDiaryUser());
        UI.addComponent(pnlTop, i18n("For:"), cboFor);

        txtDate = UI.getDateField(i18n("The_date_this_reminder_should_appear"));
        UI.addComponent(pnlTop, i18n("Date:"), txtDate);

        txtTime = UI.getTextField(i18n("The_time_any_action_should_be_taken"));
        UI.addComponent(pnlTop, i18n("Time:"), txtTime);

        txtSubject = UI.getTextField(i18n("The_subject_of_the_note"));
        UI.addComponent(pnlTop, i18n("Subject:"), txtSubject);

        txtCompleted = UI.getDateField(i18n("The_date_this_diary_note_was_completed"));
        UI.addComponent(pnlTop, i18n("Completed:"), txtCompleted);

        txtNote = (UI.TextArea) UI.addComponent(pnlMid, i18n("Note:"),
                UI.getTextArea());

        btnOk = UI.getButton(i18n("Ok"), i18n("Save_this_diary_note_and_exit"),
                'o', null, UI.fp(this, "saveData"));
        pnlBot.add(btnOk);

        btnCancel = UI.getButton(i18n("Cancel"), i18n("Close_without_saving"),
                'o', null, UI.fp(this, "actionCancel"));
        pnlBot.add(btnCancel);
    }

    public void actionCancel() {
        dispose();
    }
}
