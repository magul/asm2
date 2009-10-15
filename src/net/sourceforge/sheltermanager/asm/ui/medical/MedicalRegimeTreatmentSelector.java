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
package net.sourceforge.sheltermanager.asm.ui.medical;

import net.sourceforge.sheltermanager.asm.bo.AnimalMedical;
import net.sourceforge.sheltermanager.asm.bo.AnimalMedicalTreatment;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Date;
import java.util.Vector;


/** Represents the treatment view pane */
public class MedicalRegimeTreatmentSelector extends ASMSelector {
    private int medicalID = 0;
    private UI.Button btnNew;
    private UI.Button btnEdit;
    private UI.Button btnDelete;
    private UI.Button btnUptoDate;
    private UI.Button btnUptoSelected;
    private MedicalSelector mparent;

    public MedicalRegimeTreatmentSelector(MedicalSelector mparent) {
        this.mparent = mparent;
        init("uimedical", false, true, null);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("create_new_treatment"), 't',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWMEDICALS_NEWTREATMENT),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnEdit = UI.getButton(null, i18n("Edit_the_highlighted_treatment"),
                'v',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWMEDICALS_EDITTREATMENT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null,
                i18n("Delete_the_highlighted_treatment"), 'x',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWMEDICALS_DELETETREATMENT),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnUptoDate = UI.getButton(null,
                i18n("Bring_all_treatments_upto_date"), 'b',
                IconManager.getIcon(IconManager.SCREEN_VIEWMEDICALS_UPTODATE),
                UI.fp(this, "actionUptoDate"));
        addToolButton(btnUptoDate, false);

        btnUptoSelected = UI.getButton(null,
                i18n("Bring_selected_treatments_upto_date"), 's',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWMEDICALS_UPTODATE_SELECTED),
                UI.fp(this, "actionUptoSelected"));
        addToolButton(btnUptoSelected, true);
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public boolean hasData() {
        return getTable().getRowCount() != 0;
    }

    public void setLink(int medicalID, int linkType) {
        this.medicalID = medicalID;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAnimalMedical()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMedical()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteAnimalMedical()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecBulkCompleteAnimalMedical()) {
            btnUptoDate.setEnabled(false);
            btnUptoSelected.setEnabled(false);
        }
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void updateList() {
        if (medicalID <= 0) {
            return;
        }

        // Get the data
        AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
        amt.openRecordset("AnimalMedicalID = " + medicalID + " ORDER BY ID");

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) amt.getRecordCount()][7];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Date_Required"), i18n("Date_Given"), i18n("Given_By"),
                i18n("No"), i18n("Total_Today"), i18n("Comments")
            };

        // Build the data
        int i = 0;

        try {
            while (!amt.getEOF()) {
                datar[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            amt.getDateRequired()));
                datar[i][1] = Utils.nullToEmptyString(Utils.formatTableDate(
                            amt.getDateGiven()));
                datar[i][2] = amt.getGivenBy();
                datar[i][3] = amt.getTreatmentNumber().toString();
                datar[i][4] = amt.getTotalTreatments().toString();
                datar[i][5] = amt.getComments();
                datar[i][6] = amt.getID().toString();

                i++;
                amt.moveNext();
            }

            amt.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 6);
        mparent.tview.updateList();
    }

    public void actionUptoDate() {
        // Update all treatments displayed to today's date
        try {
            int id = mparent.regimeview.getTable().getSelectedID();
            int lastRow = mparent.regimeview.getTable().getSelectedRow();

            AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
            amt.openRecordset("AnimalMedicalID = " + id);

            while (!amt.getEOF()) {
                if (amt.getDateGiven() == null) {
                    amt.setDateGiven(new Date());
                    amt.setGivenBy(Global.currentUserName);
                }

                amt.moveNext();
            }

            amt.save();
            amt.free();
            amt = null;

            // Generate any new treatments required
            AnimalMedical am = new AnimalMedical();
            am.openRecordset("ID = " + id);
            am.generateTreatments();

            // If our regime is complete as a result,
            // we need to update the whole list
            if (am.getStatus()
                      .equals(Integer.toString(AnimalMedical.STATUS_COMPLETED))) {
                mparent.regimeview.updateList();
                // Return the medical entry back to the last selected one
                mparent.regimeview.getTable()
                                  .changeSelection(lastRow, 0, false, false);
                mparent.regimeview.updateList();
            } else {
                updateList();
            }

            am.free();
            am = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionUptoSelected() {
        int tid = getTable().getSelectedID();

        if (tid == -1) {
            return;
        }

        try {
            SortableTableModel treatmentmodel = (SortableTableModel) getTable()
                                                                         .getModel();

            if (treatmentmodel.getRowCount() == 0) {
                return;
            }

            int lastRow = mparent.regimeview.getTable().getSelectedRow();
            int[] selrows = getTable().getSelectedRows();

            for (int i = 0; i < selrows.length; i++) {
                String id = treatmentmodel.getIDAt(selrows[i]);

                AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
                amt.openRecordset("ID = " + id);

                while (!amt.getEOF()) {
                    if (amt.getDateGiven() == null) {
                        amt.setDateGiven(new Date());
                        amt.setGivenBy(Global.currentUserName);
                    }

                    amt.moveNext();
                }

                amt.save();
                amt.free();
                amt = null;
            }

            // Generate any new treatments required
            AnimalMedical am = new AnimalMedical();
            am.openRecordset("ID = " + medicalID);
            am.generateTreatments();

            mparent.regimeview.updateList();

            // Return the medical entry back to the last selected one
            mparent.regimeview.getTable()
                              .changeSelection(lastRow, 0, false, false);
            updateList();

            am.free();
            am = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionDelete() {
        int tid = getTable().getSelectedID();

        if (tid == -1) {
            return;
        }

        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM animalmedicaltreatment WHERE ID = " +
                    tid;
                DBConnection.executeAction(sql);
                updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        int tid = getTable().getSelectedID();

        if (tid == -1) {
            return;
        }

        try {
            // Get the selected record
            AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
            amt.openRecordset("ID = " + tid);

            // Create an edit form
            TreatmentEdit et = new TreatmentEdit(mparent);
            et.openForEdit(amt);
            amt = null;

            // Display
            Global.mainForm.addChild(et);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionNew() {
        try {
            AnimalMedical am = new AnimalMedical();
            am.openRecordset("ID = " + medicalID);

            // Create an edit form
            TreatmentEdit et = new TreatmentEdit(mparent);
            et.openForNew(am);
            am = null;

            // Display
            Global.mainForm.addChild(et);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
