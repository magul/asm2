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

import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.DiaryTaskDetail;
import net.sourceforge.sheltermanager.asm.bo.DiaryTaskHead;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * This class contains all code for editing diary task headers.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class DiaryTaskHeadEdit extends ASMForm {
    DiaryTaskHead dth = null;
    DiaryTaskView parent = null;
    private UI.Button btnDelete;
    private UI.Button btnNewTask;
    private UI.Button btnSave;
    private UI.Button btnViewTask;
    private UI.ComboBox cboType;
    private UI.Table tblList;
    private UI.TextField txtName;

    /** Creates new form EditDiaryTaskHead */
    public DiaryTaskHeadEdit(DiaryTaskView parent) {
        this.parent = parent;
        init(Global.i18n("uidiary", "Edit_Diary_Task"),
            IconManager.getIcon(IconManager.SCREEN_EDITDIARYTASKHEAD), "uidiary");
    }

    public void dispose() {
        dth.free();
        dth = null;
        parent = null;
        super.dispose();
    }

    public void openForEdit(DiaryTaskHead thedth) {
        dth = thedth;
        loadData();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtName);
        ctl.add(cboType);
        ctl.add(btnNewTask);
        ctl.add(btnViewTask);
        ctl.add(btnDelete);
        ctl.add(btnSave);

        return ctl;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return txtName;
    }

    public void loadData() {
        try {
            txtName.setText(dth.getName());
            cboType.setSelectedIndex(dth.getRecordType().intValue());
            this.setTitle(i18n("edit_diary_task_title", txtName.getText()));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        updateList();
    }

    public boolean saveData() {
        try {
            dth.setName(txtName.getText());
            dth.setRecordType(new Integer(cboType.getSelectedIndex()));

            try {
                dth.save();
                
                if (AuditTrail.enabled())
                	AuditTrail.changed("diarytaskhead", dth.getName());
                
                parent.updateList();

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void updateList() {
        DiaryTaskDetail dtd = new DiaryTaskDetail();

        // Get the data
        try {
            dtd.openRecordset("DiaryTaskHeadID = " + dth.getID());
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) dtd.getRecordCount()][5];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Day_Pivot"), i18n("For"), i18n("Subject"), i18n("Note")
            };

        // Build the data
        int i = 0;

        try {
            while (!dtd.getEOF()) {
                datar[i][0] = dtd.getDayPivot().toString();
                datar[i][1] = dtd.getWhoFor();
                datar[i][2] = dtd.getSubject();
                datar[i][3] = dtd.getNote();
                datar[i][4] = dtd.getID().toString();

                i++;
                dtd.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        tblList.setTableData(columnheaders, datar, i, 4);
        updateButtons();
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getBorderLayout());
        UI.Panel details = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel mid = UI.getPanel(UI.getBorderLayout());

        // Top panel ====================================
        UI.ToolBar t = UI.getToolBar();

        btnSave = (UI.Button) t.add(UI.getButton(null, i18n("Save_the_header"),
                    's',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITDIARYTASKHEAD_SAVE),
                    UI.fp(this, "saveData")));

        txtName = (UI.TextField) UI.addComponent(details, i18n("Name:_"),
                UI.getTextField());

        cboType = UI.getCombo(new String[] { i18n("Animal"), i18n("Owner") });
        UI.addComponent(details, i18n("Type:"), cboType);

        top.add(t, UI.BorderLayout.NORTH);
        top.add(details, UI.BorderLayout.CENTER);
        add(top, UI.BorderLayout.NORTH);

        // Middle panel ===================================
        tblList = UI.getTable(UI.fp(this, "tableClicked"),
                UI.fp(this, "tableDoubleClicked"));

        t = UI.getToolBar();
        btnNewTask = (UI.Button) t.add(UI.getButton(null,
                    i18n("New_task_detail"), 'n',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITDIARYTASKHEAD_NEWDETAIL),
                    UI.fp(this, "actionNew")));

        btnViewTask = (UI.Button) t.add(UI.getButton(null,
                    i18n("Edit_the_selected_task"), 'e',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITDIARYTASKHEAD_EDITDETAIL),
                    UI.fp(this, "actionEdit")));

        btnDelete = (UI.Button) t.add(UI.getButton(null,
                    i18n("Delete_this_task"), 'd',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITDIARYTASKHEAD_DELETEDETAIL),
                    UI.fp(this, "actionDelete")));

        mid.add(t, UI.BorderLayout.NORTH);
        UI.addComponent(mid, tblList);
        add(mid, UI.BorderLayout.CENTER);
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
        updateButtons();
    }

    public void updateButtons() {
        btnDelete.setEnabled(tblList.getSelectedRow() != -1);
        btnViewTask.setEnabled(tblList.getSelectedRow() != -1);
    }

    public void actionNew() {
        DiaryTaskDetailEdit edtd = new DiaryTaskDetailEdit();

        try {
            edtd.openForNew(this, dth.getID().intValue());
        } catch (CursorEngineException e) {
        }

        Global.mainForm.addChild(edtd);
    }

    public void actionEdit() {
        // Make sure a row is selected
        if (tblList.getSelectedRow() == -1) {
            return;
        }

        // Get the record
        DiaryTaskDetail dtd = new DiaryTaskDetail();
        dtd.openRecordset("ID = " + tblList.getSelectedID());

        // Edit it
        DiaryTaskDetailEdit edtd = new DiaryTaskDetailEdit();
        edtd.openForEdit(dtd, this);
        Global.mainForm.addChild(edtd);
    }

    public void actionDelete() {
        // Make sure a row is selected
        if (tblList.getSelectedRow() == -1) {
            return;
        }

        // Get the ID
        int id = tblList.getSelectedID();

        // Ask
        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM diarytaskdetail WHERE ID = " + id;
                DBConnection.executeAction(sql);
                
                if (AuditTrail.enabled()) 
                	AuditTrail.deleted("diarytaskdetail", 
                		tblList.getModel().getValueAt(tblList.getSelectedRow(), 1).toString() + " " +
                		tblList.getModel().getValueAt(tblList.getSelectedRow(), 2).toString());

                updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }
}
