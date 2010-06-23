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

import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


public class VaccinationSelector extends ASMSelector
    implements VaccinationParent {
    private int animalid = 0;
    private String[][] vaccinationtabledata = null;
    private boolean hasVacc = false;
    private UI.Button btnAdd;
    private UI.Button btnEdit;
    private UI.Button btnDelete;
    private UI.Button btnComplete;
    private UI.Button btnReschedule;

    public VaccinationSelector() {
        init("uianimal", false);
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
        return hasVacc;
    }

    public void setLink(int animalid, int linkType) {
        this.animalid = animalid;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAnimalVaccination()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalVaccination()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteAnimalVaccination()) {
            btnDelete.setEnabled(false);
        }
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void updateList() {
        hasVacc = false;

        AnimalVaccination animalvaccinations = new AnimalVaccination();

        try {
            animalvaccinations.openRecordset("AnimalID = " + animalid);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        vaccinationtabledata = new String[(int) animalvaccinations.getRecordCount()][6];

        // Create an array of headers for the accounts
        String[] columnheaders = {
                i18n("Type"), i18n("Required"), i18n("Given"), i18n("Comments")
            };

        // loop through the data and fill the array
        int i = 0;

        try {
            hasVacc = !animalvaccinations.getEOF();

            while (!animalvaccinations.getEOF()) {
                vaccinationtabledata[i][0] = animalvaccinations.getVaccinationTypeName();

                try {
                    vaccinationtabledata[i][1] = Utils.formatTableDate(animalvaccinations.getDateRequired());
                    vaccinationtabledata[i][2] = Utils.formatTableDate(animalvaccinations.getDateOfVaccination());
                } catch (Exception e) {
                }

                vaccinationtabledata[i][3] = Utils.nullToEmptyString(animalvaccinations.getComments());
                vaccinationtabledata[i][4] = animalvaccinations.getID()
                                                               .toString();
                i++;
                animalvaccinations.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, vaccinationtabledata, i, 4);
    }

    public void updateVaccinations() {
        updateList();
    }

    public void addToolButtons() {
        btnAdd = UI.getButton(null, i18n("New_vaccination_record"), 'n',
                IconManager.getIcon(IconManager.SCREEN_EDITVACCINATIONS_NEW),
                UI.fp(this, "actionAdd"));
        addToolButton(btnAdd, false);

        btnEdit = UI.getButton(null,
                i18n("Edit_the_highlighted_vaccination_record"), 'e',
                IconManager.getIcon(IconManager.SCREEN_EDITVACCINATIONS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null,
                i18n("Delete_the_highlighted_vaccination_record"), 'd',
                IconManager.getIcon(IconManager.SCREEN_EDITVACCINATIONS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnComplete = UI.getButton(null,
                i18n("complete_selected_vaccinations"), 'c',
                IconManager.getIcon(
                    IconManager.SCREEN_EDITVACCINATIONS_MARKCOMPLETE),
                UI.fp(this, "actionComplete"));
        addToolButton(btnComplete, true);

        btnReschedule = UI.getButton(null,
                i18n("complete_and_reschedule_selected_vaccinations"), 'r',
                IconManager.getIcon(
                    IconManager.SCREEN_EDITVACCINATIONS_RESCHEDULE),
                UI.fp(this, "actionReschedule"));
        addToolButton(btnReschedule, true);
    }

    public void actionAdd() {
        VaccinationEdit ea = new VaccinationEdit(this);

        try {
            ea.openForNew(animalid);
            Global.mainForm.addChild(ea);
            ea = null;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Create a new EditAnimalVaccination screen
        VaccinationEdit ea = new VaccinationEdit(this);

        // Kick it off into edit mode, passing the ID
        ea.openForEdit(id);

        // Attach it to the main screen
        Global.mainForm.addChild(ea);
        ea = null;
    }

    public void actionDelete() {
        // Make sure they are sure about this
        String message = "";

        if (getTable().getSelectedRowCount() == 1) {
            message = UI.messageDeleteConfirm();
        } else {
            message = Global.i18n("uianimal",
                    "you_are_about_to_permanently_delete_the_selected_medical_records_are_you_sure");
        }

        if (Dialog.showYesNo(message, UI.messageReallyDelete())) {
            // Read the highlighted table records and get the IDs
            int[] selrows = getTable().getSelectedRows();

            for (int i = 0; i < selrows.length; i++) {
                // Get the ID for this row
                String avID = vaccinationtabledata[selrows[i]][4];

                // Remove it from the database
                try {
                    String s = "DELETE FROM animalvaccination WHERE ID = " +
                        avID;
                    net.sourceforge.sheltermanager.cursorengine.DBConnection.executeAction(s);
                } catch (Exception e) {
                    Dialog.showError(UI.messageDeleteError() + e.getMessage());
                    Global.logException(e, getClass());
                }
            }

            updateList();
        }
    }

    public void actionComplete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            // Get the ID for the selected row
            String avID = (String) tablemodel.getValueAt(selrows[i], 4);

            String sql = "UPDATE animalvaccination SET DateOfVaccination = '" +
                Utils.getSQLDate(Calendar.getInstance()) + "' " +
                "WHERE ID = " + avID;

            // Update the onscreen value
            try {
                tablemodel.setValueAt(Utils.formatTableDate(
                        Calendar.getInstance()), selrows[i], 2);
            } catch (Exception e) {
            }

            getTable().updateRow(selrows[i]);

            try {
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionReschedule() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            // Get the ID for the selected row
            String avID = (String) tablemodel.getValueAt(selrows[i], 4);

            try {
                // Complete the selected row
                AnimalVaccination av = new AnimalVaccination();
                av.openRecordset("ID = " + avID);

                if (!av.getEOF()) {
                    av.setDateOfVaccination(new Date());
                    av.save(Global.currentUserName);
                }

                // Add a new rescheduled vacc for one year ahead
                AnimalVaccination v = new AnimalVaccination();
                v.openRecordset("ID = 0");
                v.addNew();
                v.setAnimalID(av.getAnimalID());

                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, 1);
                v.setDateRequired(c.getTime());
                v.setVaccinationID(av.getVaccinationID());
                v.setComments(av.getComments());
                v.setCost(av.getCost());
                v.save(Global.currentUserName);

                // Update the list
                updateList();
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }
}
