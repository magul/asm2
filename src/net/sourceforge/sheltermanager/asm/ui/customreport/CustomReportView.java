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
package net.sourceforge.sheltermanager.asm.ui.customreport;

import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Handles viewing of custom reports
 *
 * @author Robin Rawson-Tetley
 */
public class CustomReportView extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnView;

    /** Creates new form ViewCustomReports */
    public CustomReportView() {
        init(Global.i18n("uicustomreport", "Custom_Reports"),
            IconManager.getIcon(IconManager.SCREEN_VIEWCUSTOMREPORTS),
            "uicustomreport");
        updateList();
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecCreateCustomReports()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewCustomReports()) {
            btnView.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteCustomReports()) {
            btnDelete.setEnabled(false);
        }
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public boolean formClosing() {
        return false;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    /**
     * Refreshes the list with the current set of diary tasks on the system.
     */
    public void updateList() {
        CustomReport cr = new CustomReport();

        // Get the data
        cr.openRecordset("ID > 0 ORDER BY Title");

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) cr.getRecordCount()][3];

        // Create an array of headers for the table
        String[] columnheaders = { i18n("Title"), i18n("category") };

        // Build the data
        int i = 0;

        try {
            while (!cr.getEOF()) {
                datar[i][0] = cr.getTitle();
                datar[i][1] = Utils.nullToEmptyString(cr.getCategory());
                datar[i][2] = cr.getID().toString();

                i++;
                cr.moveNext();
            }

            cr.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 2);

        // Tell the main menu to update since this routine is
        // usually called following a change
        Global.mainForm.refreshCustomReports();
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, null, 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWCUSTOMREPORTS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, null, 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWCUSTOMREPORTS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, null, 'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWCUSTOMREPORTS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM customreport WHERE ID = " + id;
                DBConnection.executeAction(sql);
                
                if (AuditTrail.enabled())
                	AuditTrail.deleted("customreport", 
                		getTable().getModel().getValueAt(
                			getTable().getSelectedRow(), 0).toString());
                
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

        CustomReportEdit ecr = new CustomReportEdit(this);
        CustomReport cr = new CustomReport();
        cr.openRecordset("ID = " + id);
        ecr.openForEdit(cr);
        Global.mainForm.addChild(ecr);
    }

    public void actionNew() {
        CustomReportEdit ecr = new CustomReportEdit(this);
        ecr.openForNew();
        Global.mainForm.addChild(ecr);
    }
}
