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
package net.sourceforge.sheltermanager.asm.ui.animalname;

import net.sourceforge.sheltermanager.asm.bo.AnimalName;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Handles viewing of animal name database
 *
 * @author Robin Rawson-Tetley
 */
public class NamesView extends ASMView {
    private UI.Button btnView;
    private UI.Button btnDelete;
    private UI.Button btnNew;

    /** Creates new form ViewNames */
    public NamesView() {
        init(Global.i18n("uianimalname", "Animal_Name_Database"),
            IconManager.getIcon(IconManager.SCREEN_VIEWNAMES), "uianimalname");
        updateList();
    }

    public void updateList() {
        AnimalName an = new AnimalName();

        // Get the data
        an.openRecordset("ID > 0 ORDER BY Name");

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) an.getRecordCount()][3];

        // Create an array of headers for the table
        String[] columnheaders = { i18n("Name"), i18n("Sex") };

        // Build the data
        int i = 0;

        try {
            while (!an.getEOF()) {
                datar[i][0] = an.getName();
                datar[i][1] = an.getSexName();
                datar[i][2] = an.getID().toString();

                i++;
                an.moveNext();
            }

            an.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 2);
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(btnNew);
        v.add(btnView);
        v.add(btnDelete);
        v.add(getTable());

        return v;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_animal_name"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWNAMES_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit_the_highlighted_name"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWNAMES_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, i18n("Delete_the_highlighted_name"),
                'd', IconManager.getIcon(IconManager.SCREEN_VIEWNAMES_DELETE),
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

        if (Dialog.showYesNoWarning(i18n("Really_delete_this_record?"),
                    i18n("Really_Delete"))) {
            try {
                String sql = "DELETE FROM animalname WHERE ID = " + id;
                DBConnection.executeAction(sql);
                updateList();
            } catch (Exception e) {
                Dialog.showError(i18n("An_error_occurred_deleting_the_record:\n") +
                    e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        NamesEdit en = new NamesEdit(this);
        AnimalName a = new AnimalName();
        a.openRecordset("ID = " + id);
        en.openForEdit(a);
        Global.mainForm.addChild(en);
    }

    public void actionNew() {
        NamesEdit en = new NamesEdit(this);
        en.openForNew();
        Global.mainForm.addChild(en);
    }
}
