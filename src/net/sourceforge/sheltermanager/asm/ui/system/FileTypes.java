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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.io.File;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;


/**
 * File Type editing form
 *
 * @author Robin Rawson-Tetley
 */
public class FileTypes extends ASMView {
    String[][] data = null;
    String[] columnheaders = {
            Global.i18n("uisystem", "Extension"),
            Global.i18n("uisystem", "Handler")
        };
    boolean dirty = false;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnSave;
    private UI.Button btnScan;

    /** Creates new form FileTypes */
    public FileTypes() {
        init(Global.i18n("uisystem", "Edit_File_Types"),
            IconManager.getIcon(IconManager.SCREEN_FILETYPES), "uisystem");
        loadData();
    }

    public void dispose() {
        data = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(getTable());
        ctl.add(btnNew);
        ctl.add(btnDelete);
        ctl.add(btnSave);
        ctl.add(btnScan);

        return ctl;
    }

    public void enableButtons() {
        btnSave.setEnabled(dirty);
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public void rescanTypes() {
        if (!Dialog.showYesNo(i18n("Warning_overwrite_existing_types"),
                    i18n("Overwrite_existing_types"))) {
            return;
        }

        // Remove existing database of types
        File f = new File(Global.tempDirectory + File.separator +
                "filetypes.properties");
        f.delete();
        FileTypeManager.initManager();
        loadData();
    }

    public void loadData() {
        try {
            Properties types = FileTypeManager.getTypes();

            data = new String[types.values().size()][2];

            int i = 0;
            Enumeration itk = types.keys();
            Iterator itv = types.values().iterator();

            while (itk.hasMoreElements()) {
                data[i][0] = (String) itk.nextElement();
                data[i][1] = (String) itv.next();
                i++;
            }

            getTable().setTableData(columnheaders, data, i, 2, 0);
            dirty = false;
            enableButtons();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_file_type"), 'n',
                IconManager.getIcon(IconManager.SCREEN_FILETYPES_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnDelete = UI.getButton(null, i18n("Delete_the_selected_type"), 'd',
                IconManager.getIcon(IconManager.SCREEN_FILETYPES_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnSave = UI.getButton(null, i18n("Save_the_list_of_filetypes"), 's',
                IconManager.getIcon(IconManager.SCREEN_FILETYPES_SAVE),
                UI.fp(this, "saveData"));
        addToolButton(btnSave, false);

        btnScan = UI.getButton(null,
                i18n("Automatically_determine_filetypes_from_installed_programs"),
                'c', IconManager.getIcon(IconManager.SCREEN_FILETYPES_SCAN),
                UI.fp(this, "rescanTypes"));
        addToolButton(btnScan, false);
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
    }

    public void updateList() {
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public void actionEdit() {
        if (getTable().getSelectedRow() != -1) {
            FileTypeEdit fte = new FileTypeEdit(this);
            fte.openForEdit();
            Global.mainForm.addChild(fte);
            dirty = true;
            enableButtons();
        }
    }

    public boolean formClosing() {
        // Don't destroy if the user has unsaved changes and are not sure
        if (this.dirty) {
            if (!Dialog.showYesNoWarning(Global.i18n("uianimal",
                            "You_have_unsaved_changes_-_are_you_sure_you_wish_to_close?"),
                        Global.i18n("uianimal", "Unsaved_Changes"))) {
                return true;
            }
        }

        return false;
    }

    public boolean saveData() {
        // Create a new filetypes property object
        try {
            // Loop through the data array, creating
            // a new property for each one
            int maxrows = getTable().getModel().getRowCount();
            Properties newTypes = new Properties();

            for (int i = 0; i < maxrows; i++) {
                newTypes.setProperty(data[i][0], data[i][1]);
            }

            FileTypeManager.setTypes(newTypes);
            FileTypeManager.saveTypes();
            dirty = false;
            enableButtons();

            return true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void actionDelete() {
        // Create a new version of our data array which
        // is one size smaller in the row department
        int oldsize = getTable().getModel().getRowCount();
        String[][] data2 = new String[oldsize - 1][2];

        // Copy the old array into the new one, upto
        // the selected row
        for (int i = 0; i < getTable().getSelectedRow(); i++) {
            data2[i][0] = data[i][0];
            data2[i][1] = data[i][1];
        }

        // Copy the rest of the array into the new one
        // after the selected row
        for (int i = getTable().getSelectedRow() + 1; i < oldsize; i++) {
            data2[i - 1][0] = data[i][0];
            data2[i - 1][1] = data[i][1];
        }

        data = data2;

        // Update the data
        getTable().setTableData(columnheaders, data, oldsize - 1, 2, 0);

        dirty = true;
        enableButtons();
    }

    public void actionNew() {
        // Create a new version of our data array which
        // is one size larger in the row department
        int oldsize = getTable().getModel().getRowCount();
        String[][] data2 = new String[oldsize + 1][2];

        // Copy the old array into the new one
        for (int i = 0; i < oldsize; i++) {
            data2[i][0] = data[i][0];
            data2[i][1] = data[i][1];
        }

        // Set default values
        data2[data2.length - 1][0] = "[ext]";
        data2[data2.length - 1][1] = "[program]";

        data = data2;

        // Update the data
        getTable().setTableData(columnheaders, data, oldsize + 1, 2, 0);
        dirty = true;
        enableButtons();
    }

    public void setValue(int row, String extension, String program) {
        data[row][0] = extension;
        data[row][1] = program;
        getTable().setTableData(columnheaders, data, data.length, 2, 0);
        dirty = true;
        enableButtons();
    }
}
