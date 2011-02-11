/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.AnimalDiet;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Class for embedding media facilities in a frame.
 */
@SuppressWarnings("serial")
public class DietSelector extends ASMSelector {
    /** The animal ID for the link */
    public int animalID = 0;

    /** The array of data to fill the table */
    public String[][] tabledata;

    /**
     * A flag to say whether there is anything interesting in this control
     */
    private boolean hasDiet = false;
    private UI.Button btnAdd;
    private UI.Button btnDelete;
    private UI.Button btnEdit;

    /** Creates new form BeanForm */
    public DietSelector() {
        init("uianimal", false);
    }

    public Vector<Object> getTabOrder() {
        return null;
    }

    public Object getDefaultFocusedComponent() {
        return null;
    }

    /**
     * Reads current user's security settings and deactivates things they can't
     * do.
     */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAnimalDiet()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalDiet()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteAnimalDiet()) {
            btnDelete.setEnabled(false);
        }
    }

    /**
     * Sets the appropriate animal ID
     *
     * @param animalID
     *            The ID of the animal.
     * @param linkType ignored
     */
    public void setLink(int animalID, int linkType) {
        this.animalID = animalID;
    }

    public void dispose() {
        tabledata = null;
    }

    /**
     * Fills the table with the diet entries for the passed link.
     */
    public void updateList() {
        AnimalDiet animaldiet = new AnimalDiet();
        animaldiet.openRecordset("AnimalID = " + animalID);

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        tabledata = new String[(int) animaldiet.getRecordCount()][5];

        // Create an array of headers for the accounts (one less than
        // array because 4th col will hold ID
        String[] columnheaders = {
                Global.i18n("uianimal", "Start_Date"),
                Global.i18n("uianimal", "Diet"),
                Global.i18n("uianimal", "Description"),
                Global.i18n("uianimal", "Notes")
            };

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!animaldiet.getEOF()) {
                tabledata[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            animaldiet.getDateStarted()));
                tabledata[i][1] = animaldiet.getDietName();
                tabledata[i][2] = LookupCache.getDietDescription(animaldiet.getDietID());
                tabledata[i][3] = Utils.nullToEmptyString(animaldiet.getComments());
                tabledata[i][4] = animaldiet.getID().toString();
                hasDiet = true;
                i++;
                animaldiet.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 4);
    }

    /** Returns true if there is some content in the list */
    public boolean hasData() {
        return hasDiet;
    }

    public void addToolButtons() {
        btnAdd = addToolButton(UI.getButton(null, i18n("Create_new_diet"), 'n',
                    IconManager.getIcon(IconManager.SCREEN_ANIMALDIETS_NEW),
                    UI.fp(this, "actionAdd")), false);

        btnEdit = addToolButton(UI.getButton(null, i18n("Edit_this_diet"), 'e',
                    IconManager.getIcon(IconManager.SCREEN_ANIMALDIETS_EDIT),
                    UI.fp(this, "actionEdit")), true);

        btnDelete = addToolButton(UI.getButton(null, i18n("Delete_this_diet"),
                    'd',
                    IconManager.getIcon(IconManager.SCREEN_ANIMALDIETS_DELETE),
                    UI.fp(this, "actionDelete")), true);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionDelete() {
        // Read the highlighted table record and get the ID
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            // Remove it from the database
            try {
                SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                         .getModel();
                String s = "Delete From animaldiet Where ID = " + id;
                DBConnection.executeAction(s);

                if (AuditTrail.enabled()) {
                    AuditTrail.deleted("animaldiet",
                        LookupCache.getAnimalByID(animalID).getShelterCode() +
                        " " +
                        LookupCache.getAnimalByID(animalID).getAnimalName() +
                        " " +
                        tablemodel.getValueAt(getTable().getSelectedRow(), 0)
                                  .toString() + " " +
                        tablemodel.getValueAt(getTable().getSelectedRow(), 1)
                                  .toString());
                }

                // update the list
                this.updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        // Read the highlighted table record and get the ID
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        try {
            DietEdit ed = new DietEdit(this);
            ed.openForEdit(Integer.toString(id));
            Global.mainForm.addChild(ed);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionAdd() {
        DietEdit ed = new DietEdit(this);
        ed.openForNew(animalID);
        Global.mainForm.addChild(ed);
    }
}
