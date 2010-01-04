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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * This class contains all code for viewing a list of additional fields
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class ConfigureAdditional extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnView;

    public ConfigureAdditional() {
        init(Global.i18n("uisystem", "edit_additional_fields"),
            IconManager.getIcon(IconManager.SCREEN_CONFIGUREADDITIONAL),
            "uisystem", false, true, null);
        updateList();
    }

    public void dispose() {
        // Update the lookup cache
        LookupCache.invalidate();
        LookupCache.fill();
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(btnNew);
        ctl.add(btnView);
        ctl.add(btnDelete);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return false;
    }

    public void loadData() {
    }

    public void updateList() {
        // Get the data
        AdditionalField af = new AdditionalField();

        try {
            af.openRecordset("");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) af.getRecordCount()][6];

        // Create an array of headers for the table
        String[] columnheaders = new String[] {
                i18n("linktype"), i18n("fieldname"), i18n("fieldlabel"),
                i18n("fieldtype"), i18n("displayindex")
            };

        // Build the data
        int i = 0;

        try {
            while (!af.getEOF()) {
                datar[i][0] = af.getLinkTypeName();
                datar[i][1] = af.getFieldName();
                datar[i][2] = af.getFieldLabel();
                datar[i][3] = af.getFieldTypeName();
                datar[i][4] = af.getDisplayIndex().toString();
                datar[i][5] = af.getID().toString();
                i++;
                af.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 5);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, Global.i18n("uilookups", "New"), 'n',
                IconManager.getIcon(IconManager.SCREEN_CONFIGUREADDITIONAL_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, Global.i18n("uilookups", "Edit"), 'e',
                IconManager.getIcon(IconManager.SCREEN_CONFIGUREADDITIONAL_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, Global.i18n("uilookups", "Delete"), 'd',
                IconManager.getIcon(
                    IconManager.SCREEN_CONFIGUREADDITIONAL_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Make sure they're sure
        if (!Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            return;
        }

        // Enumerate all selected
        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int z = 0; z < selrows.length; z++) {
            // Get the ID of the lookup item
            String theID = tablemodel.getIDAt(selrows[z]);

            boolean canDelete = true;

            try {
                // Make sure there's nothing stored in the database
                // for this field
                SQLRecordset nrs = new SQLRecordset();
                nrs.openRecordset("SELECT AdditionalFieldID FROM additional " +
                    "WHERE AdditionalFieldID = " + theID, "additional");

                if (!nrs.getEOF()) {
                    // The field is in use - bomb out
                    Dialog.showError(i18n("field_is_in_use"));
                    Global.logError(i18n("field_is_in_use"),
                        "ConfigureAdditional.actionDelete");
                    canDelete = false;
                }

                String sql = "DELETE FROM additionalfield WHERE ID = " + theID;

                try {
                    if (canDelete) {
                        DBConnection.executeAction(sql);
                    }
                } catch (Exception e) {
                    Dialog.showError(UI.messageDeleteError() + e.getMessage());
                    Global.logException(e, getClass());
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // Redisplay after deletion.
        updateList();
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Open it in the edit form
        AdditionalFieldEdit el = new AdditionalFieldEdit(this);
        el.openForEdit(id);
        Global.mainForm.addChild(el);
    }

    public void actionNew() {
        AdditionalFieldEdit el = new AdditionalFieldEdit(this);
        el.openForNew();
        Global.mainForm.addChild(el);
    }
}
