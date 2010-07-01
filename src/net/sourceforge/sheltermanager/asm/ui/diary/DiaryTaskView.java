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
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * This class contains all code for viewing diary task headers.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class DiaryTaskView extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnView;

    /** Creates new form DiaryTasks */
    public DiaryTaskView() {
        init(Global.i18n("uidiary", "Edit_Diary_Tasks"),
            IconManager.getIcon(IconManager.SCREEN_DIARYTASKS), "uidiary");
        updateList();
    }

    public void updateList() {
        DiaryTaskHead dth = new DiaryTaskHead();

        // Get the data
        dth.openRecordset("ID > 0 ORDER BY Name");

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) dth.getRecordCount()][4];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Name"), i18n("Type"), i18n("Number_Of_Tasks")
            };

        // Build the data
        int i = 0;

        try {
            while (!dth.getEOF()) {
                datar[i][0] = dth.getName();

                switch (dth.getRecordType().intValue()) {
                case DiaryTaskHead.RECORDTYPE_ANIMAL:
                    datar[i][1] = i18n("Animal");

                    break;

                case DiaryTaskHead.RECORDTYPE_OWNER:
                    datar[i][1] = i18n("Owner");

                    break;

                default:
                    datar[i][1] = "??";

                    break;
                }

                // Calculate how many detail records this
                // header has
                DiaryTaskDetail dtd = new DiaryTaskDetail();
                dtd.openRecordset("DiaryTaskHeadID = " + dth.getID());

                if (dtd.getEOF()) {
                    datar[i][2] = "0";
                } else {
                    datar[i][2] = Long.toString(dtd.getRecordCount());
                }

                dtd = null;

                // Put the ID in there for later use
                datar[i][3] = dth.getID().toString();

                i++;
                dth.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 3);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Create_a_new_diary_task"), 'n',
                IconManager.getIcon(IconManager.SCREEN_DIARYTASKS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit_this_diary_task"), 'e',
                IconManager.getIcon(IconManager.SCREEN_DIARYTASKS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_diary_task"), 'd',
                IconManager.getIcon(IconManager.SCREEN_DIARYTASKS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public boolean formClosing() {
        return false;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Ask
        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM diarytaskdetail WHERE DiaryTaskHeadID = " +
                    id;
                DBConnection.executeAction(sql);
                sql = "DELETE FROM diarytaskhead WHERE ID = " + id;
                DBConnection.executeAction(sql);

                if (AuditTrail.enabled()) {
                    AuditTrail.deleted("diarytaskhead",
                        getTable().getValueAt(getTable().getSelectedRow(), 0)
                            .toString());
                }

                updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get the header record
        DiaryTaskHead dth = new DiaryTaskHead();
        dth.openRecordset("ID = " + id);

        // Fire up the editing screen
        DiaryTaskHeadEdit edth = new DiaryTaskHeadEdit(this);
        edth.openForEdit(dth);
        Global.mainForm.addChild(edth);
    }

    public void actionNew() {
        // Ask them for a name so we can create the record
        String thename = Dialog.getInput(i18n("Please_enter_a_name_for_the_new_task:"),
                i18n("New_Diary_Task"));

        // Abandon if the name is not valid
        if ((thename == null) || (thename.equals(""))) {
            return;
        }

        // Create our record and open the edit screen on it
        try {
            DiaryTaskHead dth = new DiaryTaskHead();
            dth.openRecordset("ID = 0");
            dth.addNew();
            dth.setName(thename);
            dth.setRecordType(new Integer(0));

            // This record is deliberately saved, as the user
            // can attach detail records from this point in and
            // it does have an ID. If they don't save, all the
            // detail records will be orphaned. Hence we do it
            // automatically.
            dth.save();

            if (AuditTrail.enabled()) {
                AuditTrail.create("diarytaskhead", thename);
            }

            // Fire up the editing screen
            DiaryTaskHeadEdit edth = new DiaryTaskHeadEdit(this);
            edth.openForEdit(dth);
            Global.mainForm.addChild(edth);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
