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
package net.sourceforge.sheltermanager.asm.ui.diary;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.animal.VaccinationEdit;
import net.sourceforge.sheltermanager.asm.ui.animal.VaccinationParent;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.FoundAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.LostAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.DiaryRenderer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.waitinglist.WaitingListEdit;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for viewing vet diary/vaccination
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class VetBookView extends ASMView implements VaccinationParent {
    final static int ID_FIELD = 7;
    final static int TYPE_FIELD = 8;
    private UI.Button btnComplete;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnRefresh;
    private UI.Button btnView;
    private UI.Button btnViewMatching;
    private UI.CheckBox chkShowCompleted;
    private UI.CheckBox chkShowFuture;

    public VetBookView() {
        init(Global.i18n("uidiary", "View_Vet_Diary"),
            IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK), "uidiary",
            true, true, new DiaryRenderer(0));
        updateList();
    }

    /**
     * Sets the tab ordering for the screen using the FlexibleFocusManager class
     */
    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(chkShowFuture);
        ctl.add(chkShowCompleted);
        ctl.add(btnNew);
        ctl.add(btnView);
        ctl.add(btnRefresh);
        ctl.add(btnDelete);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return chkShowFuture;
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

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteDiaryNotes()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecBulkCompleteNotes()) {
            btnComplete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecEditCompletedNotes()) {
            chkShowCompleted.setEnabled(false);
        }
    }

    /** Fills the on screen list of diary notes */
    public void updateList() {
        StringBuffer critDiary = new StringBuffer();
        StringBuffer critVacc = new StringBuffer();

        critDiary.append("UPPER(DiaryForName) Like '" +
            Global.getVetsDiaryUser().toUpperCase() + "%'");

        // Filter to only include on shelter animals or fostered animals in the
        // vacc list
        critVacc.append(
            "animal.DeceasedDate Is Null AND (animal.Archived = 0 OR animal.ActiveMovementType = " +
            Adoption.MOVETYPE_FOSTER + ")");

        if (!chkShowFuture.isSelected()) {
            if (critDiary.length() > 0) {
                critDiary.append(" AND ");
            }

            if (critVacc.length() > 0) {
                critVacc.append(" AND ");
            }

            Calendar futureDiary = Calendar.getInstance();
            futureDiary.set(Calendar.HOUR_OF_DAY, 23);
            futureDiary.set(Calendar.MINUTE, 59);
            futureDiary.set(Calendar.SECOND, 59);
            critDiary.append("DiaryDateTime <= '" +
                Utils.getSQLDate(futureDiary) + "'");
            critVacc.append("DateRequired <= '" +
                Utils.getSQLDate(futureDiary) + "'");
        }

        if (!chkShowCompleted.isSelected()) {
            if (critDiary.length() > 0) {
                critDiary.append(" AND ");
            }

            if (critVacc.length() > 0) {
                critVacc.append(" AND ");
            }

            critDiary.append("DateCompleted Is Null");
            critVacc.append("DateOfVaccination Is Null");
        } else {
            if (critDiary.length() > 0) {
                critDiary.append(" AND ");
            }

            if (critVacc.length() > 0) {
                critVacc.append(" AND ");
            }

            Calendar threeAgo = Calendar.getInstance();
            threeAgo.add(Calendar.MONTH, -1);
            critDiary.append("(DateCompleted Is Null OR DateCompleted >= '" +
                Utils.getSQLDateOnly(threeAgo) + "')");
            critVacc.append(
                "(DateOfVaccination Is Null OR DateOfVaccination >= '" +
                Utils.getSQLDateOnly(threeAgo) + "')");
        }

        String sql = "SELECT diary.CreatedBy, DiaryDateTime, DateCompleted, " +
            "LinkID, LinkType, internallocation.LocationName As Location, Subject, Note, " +
            "diary.ID As UID, 'd' As Type FROM diary " +
            "INNER JOIN animal ON animal.ID = diary.LinkID " +
            "INNER JOIN internallocation ON internallocation.ID = animal.ShelterLocation WHERE " +
            critDiary.toString() + " UNION " +
            "SELECT animalvaccination.CreatedBy, animalvaccination.DateRequired As DiaryDateTime, " +
            "animalvaccination.DateOfVaccination AS DateComplated, animalvaccination.AnimalID As LinkID, " +
            Diary.LINKTYPE_ANIMAL +
            " AS LinkType, internallocation.LocationName AS Location, vaccinationtype.VaccinationType As Subject, vaccinationtype.VaccinationDescription As Note, animalvaccination.ID As UID, " +
            "'v' As Type FROM animalvaccination " +
            "INNER JOIN vaccinationtype ON vaccinationtype.ID=animalvaccination.VaccinationID " +
            "INNER JOIN animal ON animal.ID=animalvaccination.AnimalID " +
            "INNER JOIN internallocation ON internallocation.ID = animal.ShelterLocation " +
            "WHERE " + critVacc.toString() + " ORDER BY DiaryDateTime";
        SQLRecordset uq = new SQLRecordset();

        try {
            Global.logDebug("Vets sql: " + sql, "VetBookView.updateList");
            uq.openRecordset(sql, "diary");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) uq.getRecordCount()][9];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Created_By"), i18n("Date"), i18n("Link"), i18n("Location"),
                i18n("Completed"), i18n("Subject"), i18n("Note")
            };

        // Build the data
        int i = 0;

        try {
            while (!uq.getEOF()) {
                datar[i][0] = (String) uq.getField("CreatedBy");
                datar[i][1] = Utils.formatTableDateTime((Date) uq.getField(
                            "DiaryDateTime"));
                datar[i][2] = Diary.getLinkInfo(((Integer) uq.getField("LinkID")).intValue(),
                        ((Integer) uq.getField("LinkType")).intValue());
                datar[i][3] = Utils.nullToEmptyString((String) uq.getField(
                            "Location"));
                datar[i][4] = Utils.formatTableDate((Date) uq.getField(
                            "DateCompleted"));
                datar[i][5] = (String) uq.getField("Subject");
                datar[i][6] = (String) uq.getField("Note");
                datar[i][7] = uq.getField("UID").toString();
                datar[i][8] = uq.getField("Type").toString();

                i++;
                uq.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        uq.free();
        uq = null;

        setTableData(columnheaders, datar, i, 7);
    }

    /**
     * Checks that the view button should be enabled for use by making sure that
     * 1. A Diary is highlighted in the window 2. It has a link to external data
     */
    public void tableClicked() {
        try {
            SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                     .getModel();

            if (tablemodel.getValueAt(getTable().getSelectedRow(), TYPE_FIELD)
                              .toString().equals("d")) {
                // Get the diary ID for the row
                int id = getTable().getSelectedID();
                Diary d = new Diary();
                d.openRecordset("ID = " + id);
                btnViewMatching.setEnabled(d.getLinkType().intValue() != Diary.LINKTYPE_NONE);
            } else {
                btnViewMatching.setEnabled(true);
            }
        } catch (Exception e) {
            btnViewMatching.setEnabled(false);
        }
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_diary_note"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit_the_selected_diary_note"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnDelete = UI.getButton(null, i18n("Delete_the_selected_diary_note"),
                'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnViewMatching = UI.getButton(null,
                i18n("View_the_record_this_diary_note_relates_to"), 'l',
                IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK_VIEWLINK),
                UI.fp(this, "actionLink"));
        addToolButton(btnViewMatching, true);

        btnComplete = UI.getButton(null,
                i18n("complete_the_selected_diary_notes"), 'c',
                IconManager.getIcon(IconManager.SCREEN_VIEWVETBOOK_COMPLETE),
                UI.fp(this, "actionComplete"));
        addToolButton(btnComplete, true);

        UI.Panel p = getTopPanel();
        p.setLayout(UI.getFlowLayout());

        chkShowFuture = (UI.CheckBox) p.add(UI.getCheckBox(i18n("Show_Future:")));
        chkShowCompleted = (UI.CheckBox) p.add(UI.getCheckBox(i18n("Show_Completed:")));
    }

    public void actionComplete() {
        // Get each selected diary and complete it with today's
        // date and time
        // Make sure a row is selected
        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            // Get the ID for the selected row
            String diaryID = tablemodel.getIDAt(selrows[i]);

            // Determine what type it is
            String sql = "";

            if (tablemodel.getValueAt(getTable().getSelectedRow(), TYPE_FIELD)
                              .toString().equals("d")) {
                sql = "UPDATE diary SET DateCompleted = '" +
                    Utils.getSQLDate(Calendar.getInstance()) + "' " +
                    "WHERE ID = " + diaryID;
            } else {
                sql = "UPDATE animalvaccination SET DateOfVaccination = '" +
                    Utils.getSQLDate(Calendar.getInstance()) + "' " +
                    "WHERE ID = " + diaryID;
            }

            try {
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                Dialog.showError(i18n("An_error_occurred_removing_the_record:_") +
                    e.getMessage());
                Global.logException(e, getClass());
            }
        }

        updateList();
    }

    public void actionLink() {
        // Don't need to check for row and things because
        // the button deactivates if nothing is selected.
        try {
            // Is this a diary or vacc?
            SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                     .getModel();
            String type = tablemodel.getValueAt(getTable().getSelectedRow(),
                    TYPE_FIELD).toString();

            if (type.equals("d")) {
                // Diary
                String diaryID = tablemodel.getIDAt(getTable().getSelectedRow());
                Diary d = new Diary();
                d.openRecordset("ID = " + diaryID);

                // Decide which screen to open based on the link type:
                switch (d.getLinkType().intValue()) {
                case Diary.LINKTYPE_ANIMAL:

                    Animal an = LookupCache.getAnimalByID(d.getLinkID());
                    AnimalEdit ea = new AnimalEdit();
                    ea.openForEdit(an);
                    Global.mainForm.addChild(ea);

                    break;

                case Diary.LINKTYPE_FOUNDANIMAL:

                    AnimalFound af = new AnimalFound();
                    af.openRecordset("ID = " + d.getLinkID());

                    FoundAnimalEdit efa = new FoundAnimalEdit();
                    efa.openForEdit(af);
                    Global.mainForm.addChild(efa);

                    break;

                case Diary.LINKTYPE_LOSTANIMAL:

                    AnimalLost al = new AnimalLost();
                    al.openRecordset("ID = " + d.getLinkID());

                    LostAnimalEdit ela = new LostAnimalEdit();
                    ela.openForEdit(al);
                    Global.mainForm.addChild(ela);

                    break;

                case Diary.LINKTYPE_MOVEMENT:

                    Adoption ad = new Adoption();
                    ad.openRecordset("ID = " + d.getLinkID());

                    MovementEdit em = new MovementEdit(null);
                    em.openForEdit(ad, 0);
                    Global.mainForm.addChild(em);

                    break;

                case Diary.LINKTYPE_OWNER:

                    Owner o = new Owner();
                    o.openRecordset("ID = " + d.getLinkID());

                    OwnerEdit eo = new OwnerEdit();
                    eo.openForEdit(o);
                    Global.mainForm.addChild(eo);

                    break;

                case Diary.LINKTYPE_WAITINGLIST:

                    AnimalWaitingList awl = new AnimalWaitingList();
                    awl.openRecordset("ID = " + d.getLinkID());

                    WaitingListEdit ewl = new WaitingListEdit(null);
                    ewl.openForEdit(awl);
                    Global.mainForm.addChild(ewl);

                    break;
                }
            } else {
                // Vaccination
                String vaccID = tablemodel.getIDAt(getTable().getSelectedRow());
                AnimalVaccination v = new AnimalVaccination();
                v.openRecordset("ID = " + vaccID);

                Animal an = new Animal();
                an.openRecordset("ID = " + v.getAnimalID());

                AnimalEdit ea = new AnimalEdit();
                ea.openForEdit(an);
                Global.mainForm.addChild(ea);
            }
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_accessing_linked_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionDelete() {
        // Ask if they are sure they want to delete the row(s)
        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                     .getModel();
            int[] selrows = getTable().getSelectedRows();

            for (int i = 0; i < selrows.length; i++) {
                // Get the ID for the selected row
                int id = getTable().getSelectedID();

                // Determine what type it is
                String sql = "";

                if (tablemodel.getValueAt(getTable().getSelectedRow(),
                            TYPE_FIELD).toString().equals("d")) {
                    sql = "DELETE FROM diary WHERE ID = " + id;
                } else {
                    sql = "DELETE FROM animalvaccination WHERE ID = " + id;
                }

                try {
                    DBConnection.executeAction(sql);
                } catch (Exception e) {
                    Dialog.showError(UI.messageDeleteError() +
                        e.getMessage());
                    Global.logException(e, getClass());
                }
            }

            updateList();
        }
    }

    public void actionNew() {
        // Create a new edit form
        DiaryEdit ed = new DiaryEdit();
        ed.openForNew(Global.getVetsDiaryUser());
        Global.mainForm.addChild(ed);
    }

    public void actionEdit() {
        // Get the ID for the row
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        // Determine what type it is
        if (tablemodel.getValueAt(getTable().getSelectedRow(), TYPE_FIELD)
                          .toString().equals("d")) {
            // Get a diary object for it
            Diary diary = new Diary();
            diary.openRecordset("ID = " + id);

            // Open the edit form and edit it
            DiaryEdit ed = new DiaryEdit();
            ed.openForEdit(diary);
            Global.mainForm.addChild(ed);
        } else {
            // Create a new EditAnimalVaccination screen
            VaccinationEdit ea = new VaccinationEdit(this);

            // Kick it off into edit mode, passing the ID
            ea.openForEdit(new Integer(id).intValue());

            // Attach it to the main screen
            Global.mainForm.addChild(ea);
            ea = null;
        }
    }

    public void updateVaccinations() {
        updateList();
    }
}
