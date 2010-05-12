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

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 *
 * @author robin
 */

/** Represents the treatment view pane */
public class MedicalTreatmentSelector extends ASMSelector {
    private MedicalSelector mparent;
    private int animalID = 0;
    private UI.Button btnEdit;
    private UI.Button btnDelete;
    private UI.Button btnRefresh;
    private UI.Button btnUptoDate;
    private UI.Button btnUptoSelected;

    public MedicalTreatmentSelector(MedicalSelector mparent) {
        this.mparent = mparent;
        init("uimedical", false, true, null);
    }

    public void addToolButtons() {
        btnEdit = UI.getButton(null, i18n("Edit_the_highlighted_treatment"),
                'e',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWMEDICALS_EDITTREATMENT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnRefresh = UI.getButton(null, i18n("Refresh"), 'r',
                IconManager.getIcon(IconManager.SCREEN_VIEWMEDICALS_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnDelete = UI.getButton(null,
                i18n("Delete_the_highlighted_treatment"), 'd',
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
        Vector ctl = new Vector();
        ctl.add(getTable());

        return ctl;
    }

    public boolean hasData() {
        return getTable().getRowCount() != 0;
    }

    public void setLink(int animalID, int linkType) {
        this.animalID = animalID;
    }

    public void setSecurity() {
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
        AnimalMedicalTreatment amt = new AnimalMedicalTreatment();

        // If we have a link, use the animal id
        if (animalID != 0) {
            amt.openRecordset("AnimalID = " + animalID);
        } else {
            
	    // Show all active entries
            AnimalMedical am = new AnimalMedical();
            am.openRecordset("Status = " + AnimalMedical.STATUS_ACTIVE +
            " AND EXISTS(SELECT Archived FROM animal WHERE " +
	    "ID = animalmedical.AnimalID AND Archived = 0)");

            // Build a list of IDs
            StringBuffer idList = new StringBuffer();

            try {
                while (!am.getEOF()) {
                    if (idList.length() > 0) {
                        idList.append(",");
                    }

                    idList.append(am.getID());
                    am.moveNext();
                }

                am.free();
                am = null;
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            if (idList.length() > 0) {
                Calendar date1 = Calendar.getInstance();
                date1.set(Calendar.HOUR_OF_DAY, 0);
                date1.set(Calendar.MINUTE, 0);
                date1.set(Calendar.SECOND, 0);

                Calendar date2 = Calendar.getInstance();
                date2.set(Calendar.HOUR_OF_DAY, 23);
                date2.set(Calendar.MINUTE, 59);
                date2.set(Calendar.SECOND, 59);
                // Only show active and in need of giving in medical book, or
                // were done today.
                amt.openRecordset("AnimalMedicalID In (" + idList.toString() +
                    ") AND DateGiven Is Null Or (DateGiven >= '" +
                    Utils.getSQLDateOnly(date1) + "' AND DateGiven <= '" +
                    Utils.getSQLDateOnly(date2) + "')");
            } else {
                amt.openRecordset("ID = 0");
            }
        }

        // Create an array to hold the results for the table
        String[][] datar = null;

        if (animalID != 0) {
            datar = new String[(int) amt.getRecordCount()][13];
        } else {
            // If there is no animal link, we need to show animals in the list
            datar = new String[(int) amt.getRecordCount()][15];
        }

        // Create an array of headers for the table
        String[] columnheaders = null;

        if (animalID != 0) {
            columnheaders = new String[] {
                    i18n("Treatment_Name"), i18n("Dosage"), i18n("Status"),
                    i18n("Comments"), i18n("Date_Required"), i18n("Date_Given"),
                    i18n("Sequence"), i18n("Total_Today"), i18n("Comments")
                };
        } else {
            columnheaders = new String[] {
                    i18n("Code"), i18n("Name"), i18n("Treatment_Name"),
                    i18n("Dosage"), i18n("Status"), i18n("Comments"),
                    i18n("Date_Required"), i18n("Date_Given"), i18n("Sequence"),
                    i18n("Total_Today"), i18n("Comments")
                };
        }

        // Build the data
        int i = 0;

        try {
            while (!amt.getEOF()) {
                if (animalID == 0) {
                    if (amt.getAnimal() != null) {
                        datar[i][0] = amt.getAnimal().getShelterCode();
                        datar[i][1] = amt.getAnimal().getAnimalName();
                    } else {
                        datar[i][0] = "";
                        datar[i][1] = "";
                    }

                    datar[i][2] = amt.getAnimalMedical().getTreatmentName();
                    datar[i][3] = amt.getAnimalMedical().getDosage();
                    datar[i][4] = amt.getAnimalMedical().getNamedStatus();
                    // datar[i][5] = amt.getAnimalMedical().getNamedFrequency();
                    // datar[i][6] =
                    // amt.getAnimalMedical().getNamedNumberOfTreatments();
                    // datar[i][7] =
                    // Utils.nullToEmptyString(Utils.formatMySQLDateNoTime(amt.getAnimalMedical().getStartDate()));
                    datar[i][5] = amt.getAnimalMedical().getComments();
                    datar[i][6] = Utils.nullToEmptyString(Utils.formatTableDate(
                                amt.getDateRequired()));
                    datar[i][7] = Utils.nullToEmptyString(Utils.formatTableDate(
                                amt.getDateGiven()));
                    datar[i][8] = amt.getTreatmentNumber().toString();
                    datar[i][9] = amt.getTotalTreatments().toString();
                    datar[i][10] = amt.getComments();
                    datar[i][11] = amt.getID().toString();
                } else {
                    datar[i][0] = amt.getAnimalMedical().getTreatmentName();
                    datar[i][1] = amt.getAnimalMedical().getDosage();
                    datar[i][2] = amt.getAnimalMedical().getNamedStatus();
                    // datar[i][3] = amt.getAnimalMedical().getNamedFrequency();
                    // datar[i][4] =
                    // amt.getAnimalMedical().getNamedNumberOfTreatments();
                    // datar[i][5] =
                    // Utils.nullToEmptyString(Utils.formatMySQLDateNoTime(amt.getAnimalMedical().getStartDate()));
                    datar[i][3] = amt.getAnimalMedical().getComments();
                    datar[i][4] = Utils.nullToEmptyString(Utils.formatTableDate(
                                amt.getDateRequired()));
                    datar[i][5] = Utils.nullToEmptyString(Utils.formatTableDate(
                                amt.getDateGiven()));
                    datar[i][6] = amt.getTreatmentNumber().toString();
                    datar[i][7] = amt.getTotalTreatments().toString();
                    datar[i][8] = amt.getComments();
                    datar[i][9] = amt.getID().toString();
                }

                i++;
                amt.moveNext();
            }

            amt.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create our new table model and shove it into the table
        SortableTableModel model = new SortableTableModel();

        if (animalID != 0) {
            setTableData(columnheaders, datar, i, 9);
        } else {
            setTableData(columnheaders, datar, i, 11);
        }
    }

    public void actionUptoSelected() {
        // Update all selected treatments displayed to today's date
        try {
            int tid = getTable().getSelectedID();

            if (tid == -1) {
                return;
            }

            int lastRow = mparent.regimeview.getTable().getSelectedRow();
            int[] selrows = getTable().getSelectedRows();
            SortableTableModel treatmentmodel = (SortableTableModel) getTable()
                                                                         .getModel();

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

                amt.moveFirst();

                while (!amt.getEOF()) {
                    amt.getAnimalMedical().generateTreatments();
                    amt.moveNext();
                }

                amt.free();
                amt = null;
            }

            mparent.regimeview.updateList();
            // Return the medical entry back to the last selected one
            mparent.regimeview.getTable()
                              .changeSelection(lastRow, 0, false, false);
            mparent.regimetview.updateList();
            updateList();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionUptoDate() {
        // Update all treatments displayed to today's date
        try {
            if (mparent.regimeview.getTable().getRowCount() == 0) {
                return;
            }

            int lastRow = mparent.regimeview.getTable().getSelectedRow();
            int[] selrows = getTable().getSelectedRows();
            SortableTableModel treatmentmodel = (SortableTableModel) getTable()
                                                                         .getModel();

            for (int i = 0; i < treatmentmodel.getRowCount(); i++) {
                String id = treatmentmodel.getIDAt(i);

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
            am.openRecordset("Status = " +
                Integer.toString(AnimalMedical.STATUS_ACTIVE));

            while (!am.getEOF()) {
                am.generateTreatments();
                am.moveNext();
            }

            mparent.regimeview.updateList();
            // Return the medical entry back to the last selected one
            mparent.regimeview.getTable()
                              .changeSelection(lastRow, 0, false, false);
            mparent.regimetview.updateList();
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
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        try {
            // Get the selected record
            AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
            amt.openRecordset("ID = " + id);

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
}
