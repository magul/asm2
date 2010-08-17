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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.AnimalLitter;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for viewing litters
 *
 * @author Robin Rawson-Tetley
 */
public class LitterView extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnEdit;
    private UI.Button btnNew;
    private UI.Button btnRefresh;
    private UI.Button btnViewAnimals;
    private UI.CheckBox chkShowExpired;

    public LitterView() {
        init(Global.i18n("uianimal", "Animal_Litters"),
            IconManager.getIcon(IconManager.SCREEN_VIEWLITTERS), "uianimal");
        updateList();
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddLitterLog()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeLitterLog()) {
            btnEdit.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteLitterLog()) {
            btnDelete.setEnabled(false);
        }
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(chkShowExpired);
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public boolean formClosing() {
        return false;
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void updateList() {
        AnimalLitter al = AnimalLitter.getRecentLitters();

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) al.getRecordCount()][9];

        // Are we using acceptance no or litter id?
        String header = i18n("Acceptance");

        if (Configuration.getBoolean("AutoLitterIdentification")) {
            header = i18n("litterid");
        }

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Parent"), i18n("Species"), i18n("Date"), i18n("Expires"),
                i18n("Number_in_litter"), header, i18n("Remaining"),
                i18n("Comments")
            };

        // Build the data
        int i = 0;

        int totalCat = 0;
        int totalDog = 0;
        int totalOther = 0;

        try {
            Date atDate = new Date();

            while (!al.getEOF()) {
                if (!al.hasExpired(atDate) || chkShowExpired.isSelected()) {
                    datar[i][0] = al.getParentName();
                    datar[i][1] = al.getSpeciesName();
                    datar[i][2] = Utils.formatTableDate(al.getDate());
                    datar[i][3] = Utils.formatTableDate(al.getInvalidDate());
                    datar[i][4] = al.getNumberInLitter().toString();
                    datar[i][5] = Utils.nullToEmptyString(al.getAcceptanceNumber());
                    datar[i][6] = al.getAnimalsRemaining().toString();
                    datar[i][7] = al.getComments();
                    datar[i][8] = al.getID().toString();

                    if (al.getSpeciesName().equalsIgnoreCase(i18n("Cat"))) {
                        totalCat++;
                    } else if (al.getSpeciesName().equalsIgnoreCase(i18n("Dog"))) {
                        totalDog++;
                    } else {
                        totalOther++;
                    }

                    i++;
                }

                al.moveNext();
            }

        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 8);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("New_litter"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWLITTERS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnEdit = UI.getButton(null, i18n("Edit_this_litter"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWLITTERS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_litter"), 'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWLITTERS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_VIEWLITTERS_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnViewAnimals = UI.getButton(null,
                i18n("View_the_animals_in_this_litter"), 'a',
                IconManager.getIcon(IconManager.SCREEN_VIEWLITTERS_ANIMALS),
                UI.fp(this, "actionViewAnimals"));
        addToolButton(btnViewAnimals, true);

        chkShowExpired = UI.getCheckBox(i18n("include_expired"));
        chkShowExpired.setOpaque(false);
        addToolButton(chkShowExpired, false);
    }

    public void actionViewAnimals() {
        // Drop out if no row was selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        // Drop out if there is no acceptance number
        String acceptance = (String) getTable().getModel()
                                         .getValueAt(getTable().getSelectedRow(),
                4);

        if (acceptance.equals("")) {
            return;
        }

        // Open a find animal form, set the logical location to all
        // and the acceptance number to the one currently selected

        // Create FindAnimal
        AnimalFind fa = new AnimalFind();

        // Add it to the desktop
        Global.mainForm.addChild(fa);

        // Set the appropriate values
        fa.cboLocation.setSelectedIndex(1);
        fa.txtAcceptanceNo.setText(acceptance);
        fa.txtAgeFrom.setText("0");
        fa.txtAgeTo.setText("0.5");
        fa.chkIncludeDeceased.setSelected(true);

        // Run the search
        fa.runSearch();

        // Put the cursor back to a pointer (normally the ASMFind
        // structure does it)
        UI.cursorToPointer();
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

        // Make sure they are sure
        if (!Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            return;
        }

        try {
            SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                     .getModel();

            String sql = "DELETE FROM animallitter WHERE ID = " + id;
            DBConnection.executeAction(sql);

            if (AuditTrail.enabled()) {
                AuditTrail.deleted("animallitter",
                    tablemodel.getValueAt(getTable().getSelectedRow(), 1)
                              .toString() + " " +
                    tablemodel.getValueAt(getTable().getSelectedRow(), 3)
                              .toString());
            }

            updateList();
        } catch (Exception e) {
            Dialog.showError(UI.messageDeleteError() + e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Create a litter
        AnimalLitter litter = new AnimalLitter();
        litter.openRecordset("ID = " + id);

        // Create an edit form and fire it off
        LitterEdit el = new LitterEdit(this);
        el.openForEdit(litter);
        Global.mainForm.addChild(el);
    }

    public void actionNew() {
        // Create a new EditLitter form
        LitterEdit el = new LitterEdit(this);
        el.openForNew();
        Global.mainForm.addChild(el);
    }
}
