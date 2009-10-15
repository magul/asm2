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

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/** Represents the treatment view pane */
public class MedicalRegimeSelector extends ASMSelector {
    private int animalID = 0;
    private UI.Button btnNew;
    private UI.Button btnNewFromProfile;
    private UI.Button btnEdit;
    private UI.Button btnDelete;
    private UI.Button btnRefresh;
    private MedicalSelector mparent;

    public MedicalRegimeSelector(MedicalSelector mparent) {
        this.mparent = mparent;
        init("uimedical", false);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null,
                i18n("Create_a_new_regime_for_this_animal"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWMEDICALS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnNewFromProfile = UI.getButton(null,
                i18n("Create_a_new_regime_from_a_medical_profile"), 'p',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWMEDICALS_NEWFROMPROFILE),
                UI.fp(this, "actionNewFromProfile"));
        addToolButton(btnNewFromProfile, false);

        btnEdit = UI.getButton(null,
                i18n("Edit_the_highlighted_medical_regime"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWMEDICALS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnRefresh = UI.getButton(null, i18n("Refresh"), 'r',
                IconManager.getIcon(IconManager.SCREEN_VIEWMEDICALS_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnDelete = UI.getButton(null, i18n("Delete_the_highlighted_regime"),
                'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWMEDICALS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
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
        if (!Global.currentUserObject.getSecAddAnimalMedical()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimalMedical()) {
            btnNewFromProfile.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMedical()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteAnimalMedical()) {
            btnDelete.setEnabled(false);
        }
    }

    public void tableClicked() {
        mparent.regimetview.setLink(getTable().getSelectedID(), 0);
        mparent.tview.setLink(animalID, 0);
        mparent.regimetview.updateList();
        mparent.tview.updateList();
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    /**
     * Refreshes the list with the current set of animal medical records
     */
    public void updateList() {
        // If we have no animal link, get rid of the
        // create buttons - the medical book can only
        // be used for existing animals.
        btnNew.setVisible(animalID != 0);
        btnNewFromProfile.setVisible(animalID != 0);

        AnimalMedical am = new AnimalMedical();

        // Get the data

        // If we have a link, use the animal id
        if (animalID != 0) {
            am.openRecordset("AnimalID = " + animalID);
        } else {
            // Show all active entries with a start date before or = today
            am.openRecordset("Status = " + AnimalMedical.STATUS_ACTIVE +
                " AND " + "StartDate <= '" +
                Utils.getSQLDateOnly(Calendar.getInstance()) + "'");
        }

        // Create an array to hold the results for the table
        String[][] datar = null;

        if (animalID != 0) {
            datar = new String[(int) am.getRecordCount()][8];
        } else {
            // If there is no animal link, we need to show animals in the list
            datar = new String[(int) am.getRecordCount()][10];
        }

        // Create an array of headers for the table
        String[] columnheaders = null;

        if (animalID != 0) {
            columnheaders = new String[] {
                    i18n("Treatment_Name"), i18n("Dosage"), i18n("Status"),
                    i18n("Frequency"), i18n("Finishes_After"),
                    i18n("Start_Date"), i18n("Comments")
                };
        } else {
            columnheaders = new String[] {
                    i18n("Code"), i18n("Name"), i18n("Treatment_Name"),
                    i18n("Dosage"), i18n("Status"), i18n("Frequency"),
                    i18n("Finishes_After"), i18n("Start_Date"), i18n("Comments")
                };
        }

        // Build the data
        int i = 0;

        try {
            while (!am.getEOF()) {
                if (animalID == 0) {
                    datar[i][0] = am.getAnimal().getShelterCode();
                    datar[i][1] = am.getAnimal().getAnimalName();
                    datar[i][2] = am.getTreatmentName();
                    datar[i][3] = am.getDosage();
                    datar[i][4] = am.getNamedStatus();
                    datar[i][5] = am.getNamedFrequency();
                    datar[i][6] = am.getNamedNumberOfTreatments();
                    datar[i][7] = Utils.nullToEmptyString(Utils.formatTableDate(
                                am.getStartDate()));
                    datar[i][8] = am.getComments();
                    datar[i][9] = am.getID().toString();
                } else {
                    datar[i][0] = am.getTreatmentName();
                    datar[i][1] = am.getDosage();
                    datar[i][2] = am.getNamedStatus();
                    datar[i][3] = am.getNamedFrequency();
                    datar[i][4] = am.getNamedNumberOfTreatments();
                    datar[i][5] = Utils.nullToEmptyString(Utils.formatTableDate(
                                am.getStartDate()));
                    datar[i][6] = am.getComments();
                    datar[i][7] = am.getID().toString();
                }

                i++;
                am.moveNext();
            }

            am.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        if (animalID != 0) {
            setTableData(columnheaders, datar, i, 7);
        } else {
            setTableData(columnheaders, datar, i, 9);

            // Go to the first record, so that any records we have get
            // their treatments shown immediately (it just looks better)
            UI.invokeIn(new Runnable() {
                    public void run() {
                        if (getTable().getRowCount() > 0) {
                            getTable().changeSelection(0, 0, false, false);
                            mparent.regimetview.setLink(getTable()
                                                            .getSelectedID(), 0);
                            mparent.regimetview.updateList();
                            mparent.tview.updateList();
                        } else {
                            mparent.regimetview.setLink(getTable()
                                                            .getSelectedID(), 0);
                            mparent.regimetview.updateList();
                            mparent.tview.updateList();
                        }
                    }
                }, 100);
        }
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM animalmedicaltreatment WHERE AnimalMedicalID = " +
                    id;
                DBConnection.executeAction(sql);

                sql = "DELETE FROM animalmedical WHERE ID = " + id;
                DBConnection.executeAction(sql);
                updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        if (!Global.currentUserObject.getSecChangeAnimalMedical()) {
            return;
        }

        // Get the selected ID
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        MedicalEdit em = new MedicalEdit(mparent);
        AnimalMedical am = new AnimalMedical();
        am.openRecordset("ID = " + id);
        em.openForEdit(am);
        Global.mainForm.addChild(em);
        em = null;
    }

    public void actionNew() {
        MedicalEdit em = new MedicalEdit(mparent);
        em.openForNew(animalID);
        Global.mainForm.addChild(em);
    }

    public void actionNewFromProfile() {
        int profileID = 0;

        try {
            // Prompt for a profile - cancel on error or invalid
            // selection.
            profileID = Dialog.getMedicalProfile();

            if (profileID == 0) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        // Create a new medical screen
        MedicalEdit em = new MedicalEdit(mparent);

        // Give it the profile and tell it to copy data from it
        em.openForNewFromProfile(animalID, profileID);

        Global.mainForm.addChild(em);
    }
}
