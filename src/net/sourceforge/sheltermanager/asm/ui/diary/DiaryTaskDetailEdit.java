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
package net.sourceforge.sheltermanager.asm.ui.diary;

import net.sourceforge.sheltermanager.asm.bo.DiaryTaskDetail;
import net.sourceforge.sheltermanager.asm.bo.Users;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.util.Vector;


/**
 * This class contains all code for editing diary task detail records.
 *
 * @author Robin Rawson-Tetley
 */
public class DiaryTaskDetailEdit extends ASMForm {
    DiaryTaskDetail dtd = null;
    DiaryTaskHeadEdit parent = null;
    private UI.ComboBox cboFor;
    private UI.Button btnCancel;
    private UI.Spinner spnPivot;
    private UI.TextField txtSubject;
    private UI.Button btnOk;
    private UI.TextArea txtNote;

    /** Creates new form EditDiaryTaskDetail */
    public DiaryTaskDetailEdit() {
        init(Global.i18n("uidiary", "Edit_Diary_Task_Detail"),
            IconManager.getIcon(IconManager.SCREEN_EDITDIARYTASKDETAIL),
            "uidiary");
    }

    public void dispose() {
        dtd.free();
        dtd = null;
        parent = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboFor);
        ctl.add(spnPivot);
        ctl.add(txtSubject);
        ctl.add(txtNote);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboFor;
    }

    public void openForEdit(DiaryTaskDetail thedtd, DiaryTaskHeadEdit theparent) {
        parent = theparent;
        dtd = thedtd;
        loadData();
    }

    public void openForNew(DiaryTaskHeadEdit theparent, int theHeaderID) {
        parent = theparent;
        this.setTitle(i18n("Create_New_Diary_Task_Detail"));
        spnPivot.setValue(new Integer(0));

        try {
            dtd = new DiaryTaskDetail();
            dtd.openRecordset("ID = 0");
            dtd.addNew();
            dtd.setDiaryTaskHeadID(new Integer(theHeaderID));
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_creating_the_record:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /** Fills onscreen combo boxes for lookup purposes */
    public void loadLookups() {
        // Users
        Users users = new Users();
        users.openRecordset("ID > 0 Order By UserName");

        while (!users.getEOF()) {
            try {
                cboFor.addItem(users.getUserName());
                users.moveNext();
            } catch (CursorEngineException e) {
                Global.logException(e, getClass());

                break;
            }
        }

        users = null;
    }

    public void loadData() {
        try {
            setTitle(i18n("Edit_Diary_Task_Detail"));

            cboFor.setSelectedItem(dtd.getWhoFor());
            spnPivot.setValue(dtd.getDayPivot());
            txtSubject.setText(dtd.getSubject());
            txtNote.setText(dtd.getNote());
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_loading_the_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        try {
            dtd.setWhoFor((String) cboFor.getSelectedItem());
            dtd.setDayPivot((Integer) spnPivot.getValue());
            dtd.setSubject(txtSubject.getText());
            dtd.setNote(txtNote.getText());

            try {
                dtd.save();
                parent.updateList();
                dispose();

                return true;
            } catch (Exception e) {
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
                Global.logException(e, getClass());
            }
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_saving_the_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(3));

        Users users = new Users();
        users.openRecordset("ID > 0 Order By UserName");

        cboFor = UI.getCombo(i18n("For:"),
                "SELECT UserName FROM users ORDER BY UserName", "UserName");
        cboFor.setEditable(true);
        UI.addComponent(p, i18n("For:"), cboFor);
        p.add(UI.getLabel());

        spnPivot = (UI.Spinner) UI.addComponent(p, i18n("Diarise_For:"),
                UI.getSpinner(0, 365));
        p.add(UI.getLabel(i18n("days_from_today_(or_0_to_prompt)")));

        txtSubject = (UI.TextField) UI.addComponent(p, i18n("Subject:"),
                UI.getTextField());
        p.add(UI.getLabel());

        txtNote = (UI.TextArea) UI.addComponent(p, i18n("Note:"),
                UI.getTextArea());
        p.add(UI.getLabel());

        btnOk = (UI.Button) p.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) p.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(p, UI.BorderLayout.CENTER);
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }
}
