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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteria;
import net.sourceforge.sheltermanager.asm.ui.criteria.DiaryCriteriaListener;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.TableData;
import net.sourceforge.sheltermanager.asm.ui.ui.TableRow;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Quick editing of vaccination records.
 *
 * @author Robin Rawson-Tetley
 */
public class VaccinationView extends ASMView implements VaccinationParent,
    DiaryCriteriaListener {
    public int type = 0;
    public Date dateUpto = new Date();
    public Date dateFrom = new Date();
    public Date dateTo = new Date();
    public String ourtitle = "";
    private UI.Button btnComplete;
    private UI.Button btnDeleteVacc;
    private UI.Button btnEditVacc;
    private UI.Button btnOpenAnimal;
    private UI.Button btnRefresh;
    private UI.Button btnReschedule;
    private UI.CheckBox chkDeceased;
    private UI.CheckBox chkOffShelter;

    /** Creates new form EditVaccinations */
    public VaccinationView() {
        Global.mainForm.addChild(new DiaryCriteria(this,
                Global.i18n("uianimal", "Vaccination_Diary_Criteria")));
    }

    public void startForm() {
        init(Global.i18n("uianimal", "Vaccination_Book"),
            IconManager.getIcon(IconManager.SCREEN_VACCINATIONBOOK),
            "uianimal", true, true, null);
        Global.mainForm.addChild(this);
        updateList();
    }

    public void dateChosen(Date from, Date to) {
        dateFrom = from;
        dateTo = to;
        type = DiaryCriteria.BETWEEN_TWO;
        startForm();
    }

    public void normalChosen() {
        type = DiaryCriteria.UPTO_TODAY;
        startForm();
    }

    public void uptoChosen(Date upto) {
        dateUpto = upto;
        type = DiaryCriteria.UPTO_SPECIFIED;
        startForm();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(chkDeceased);
        ctl.add(chkOffShelter);
        ctl.add(btnEditVacc);
        ctl.add(btnDeleteVacc);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return chkDeceased;
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
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

    public void dispose() {
        dateUpto = null;
        dateFrom = null;
        dateTo = null;
        ourtitle = null;
        super.dispose();
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecChangeAnimalVaccination()) {
            btnEditVacc.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteAnimalVaccination()) {
            btnDeleteVacc.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecBulkCompleteAnimalVaccination()) {
            btnComplete.setEnabled(false);
        }
    }

    public void updateList() {
        // Spawn the updateList code on a new thread as there can be quite a
        // lot of vaccinations
        new Thread() {
                public void run() {
                    updateListThreaded();
                }
            }.start();
    }

    public void updateListThreaded() {
        AnimalVaccination av = new AnimalVaccination();
        boolean deceased = chkDeceased.isSelected();
        boolean offshelter = chkOffShelter.isSelected();

        switch (type) {
        case DiaryCriteria.UPTO_TODAY:
            av.openRecordset("DateOfVaccination Is Null AND DateRequired <= '" +
                Utils.getSQLDateOnly(Calendar.getInstance()) + "'");

            break;

        case DiaryCriteria.UPTO_SPECIFIED:
            av.openRecordset("DateOfVaccination Is Null AND DateRequired <= '" +
                Utils.getSQLDateOnly(dateUpto) + "'");

            break;

        case DiaryCriteria.BETWEEN_TWO:
            av.openRecordset("DateOfVaccination Is Null AND DateRequired >= '" +
                Utils.getSQLDateOnly(dateFrom) + "' AND DateRequired <= '" +
                Utils.getSQLDateOnly(dateTo) + "'");

            break;
        }

        // Set progress meter
        initStatusBarMax((int) av.getRecordCount());
        setStatusText(i18n("Filling_list..."));

        // loop through the data
        int i = 0;
        TableData data = new TableData();

        try {
            while (!av.getEOF()) {
                TableRow row = new TableRow(9);

                if (av.getAnimal() == null) {
                    row.set(0, "...");
                    row.set(1, "...");
                    row.set(2, "...");
                } else {
                    row.set(0, av.getAnimal().getShelterCode());
                    row.set(1, av.getAnimal().getAnimalName());

                    // If the option is set, either show internal location
                    // or logical location if the animal is not on the shelter
                    if (Configuration.getBoolean("ShowILOffShelter")) {
                        // Get animal's logical location
                        String logicallocation = av.getAnimal()
                                                   .getAnimalLocationAtDateByName(new Date());

                        // If it is on the shelter, show the internal location
                        if (logicallocation.equals(i18n("On_Shelter"))) {
                            row.set(2, av.getAnimal().getShelterLocationName());
                        } else {
                            // Otherwise show the logical location
                            row.set(2, "[" + logicallocation + "]");
                        }
                    } else {
                        // Option is not set - show internal location
                        row.set(2, av.getAnimal().getShelterLocationName());
                    }
                }

                row.set(3, av.getVaccinationTypeName());

                try {
                    row.set(4, Utils.formatTableDate(av.getDateRequired()));
                    row.set(5, Utils.formatTableDate(av.getDateOfVaccination()));
                } catch (Exception e) {
                }

                row.set(6, Utils.nullToEmptyString(av.getComments()));
                row.set(7, av.getID().toString());
                row.set(8, av.getAnimalID().toString());

                // If the animal is off shelter or deceased and the box isn't
                // ticked, don't add it to the list
                boolean show = true;

                if ((av.getAnimal().getDeceasedDate() != null) && !deceased) {
                    show = false;
                }

                if (!av.getAnimal().fastIsAnimalOnShelter() && !offshelter) {
                    show = false;
                }

                if ((av.getAnimal().getDeceasedDate() != null) && deceased) {
                    show = true;
                }

                if (show) {
                    data.add(row);
                }

                i++;
                av.moveNext();
                incrementStatusBar();
            }

            resetStatusBar();

            // Create an array of headers for the accounts
            String[] columnheaders = {
                    i18n("Code"), i18n("Name"), i18n("Location"), i18n("Type"),
                    i18n("Required"), i18n("Given"), i18n("Comments")
                };

            String[][] vaccinationtabledata = data.toTableData();
            setTableData(columnheaders, vaccinationtabledata, data.size(), 7);
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        } finally {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }
    }

    public void addToolButtons() {
        btnEditVacc = UI.getButton(null,
                i18n("Edit_the_highlighted_vaccination_record"), 'e',
                IconManager.getIcon(IconManager.SCREEN_EDITVACCINATIONS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEditVacc, true);

        btnDeleteVacc = UI.getButton(null,
                i18n("Delete_the_highlighted_vaccination_record"), 'd',
                IconManager.getIcon(IconManager.SCREEN_EDITVACCINATIONS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDeleteVacc, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_EDITVACCINATIONS_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnOpenAnimal = UI.getButton(null,
                i18n("Open_the_animal_record_for_this_vaccination"), 'a',
                IconManager.getIcon(
                    IconManager.SCREEN_EDITVACCINATIONS_OPENANIMAL),
                UI.fp(this, "actionOpenAnimal"));
        addToolButton(btnOpenAnimal, true);

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

        UI.Panel top = getTopPanel();
        chkDeceased = (UI.CheckBox) top.add(UI.getCheckBox(i18n("include_deceased")));
        chkOffShelter = (UI.CheckBox) top.add(UI.getCheckBox(i18n("include_off_shelter")));
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
            String avID = (String) tablemodel.getValueAt(selrows[i], 7);

            String sql = "UPDATE animalvaccination SET DateOfVaccination = '" +
                Utils.getSQLDateOnly(Calendar.getInstance()) + "' " +
                "WHERE ID = " + avID;

            // Update the onscreen value
            try {
                tablemodel.setValueAt(Utils.formatTableDate(
                        Calendar.getInstance()), selrows[i], 5);
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
            String avID = (String) tablemodel.getValueAt(selrows[i], 7);

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

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
    }

    public void actionOpenAnimal() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        id = Integer.parseInt((String) getTable().getModel()
                                           .getValueAt(getTable()
                                                           .getSelectedRow(), 8));

        UI.cursorToWait();

        // Create a new EditAnimal screen
        AnimalEdit ea = new AnimalEdit();
        Animal animal = LookupCache.getAnimalByID(new Integer(id));

        // Kick it off into edit mode, passing the animal
        ea.openForEdit(animal);

        // Attach it to the main screen
        Global.mainForm.addChild(ea);
        ea = null;
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            for (int i = 0; i < selrows.length; i++) {
                // Get the ID for the selected row
                String avID = (String) tablemodel.getValueAt(selrows[i], 7);

                String sql = "Delete From animalvaccination Where ID = " +
                    avID;

                try {
                    DBConnection.executeAction(sql);
                } catch (Exception e) {
                    Dialog.showError(UI.messageDeleteError() + e.getMessage());
                    Global.logException(e, getClass());
                }
            }

            updateList();
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        UI.cursorToWait();

        // Create a new EditAnimalVaccination screen
        VaccinationEdit ea = new VaccinationEdit(this);

        // Kick it off into edit mode, passing the ID
        ea.openForEdit(id);

        // Attach it to the main screen
        Global.mainForm.addChild(ea);
        ea = null;
    }

    /** Tells the parent to update it's list of vaccinations - we don't do it
        on this screen because there can be so many */
    public void updateVaccinations() {
    }
}
