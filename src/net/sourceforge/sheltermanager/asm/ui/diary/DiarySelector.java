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
package net.sourceforge.sheltermanager.asm.ui.diary;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.FoundAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.LostAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.DiaryRenderer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.waitinglist.WaitingListEdit;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Calendar;
import java.util.Vector;


/**
 * Bean panel for displaying and editing linked diary notes
 *
 * @author Robin Rawson-Tetley
 */
public class DiarySelector extends ASMSelector {
    public int linkID = 0;
    public int linkType = 0;
    private boolean showLinks = false;
    private boolean hasRecords = false;
    private UI.Button btnComplete;
    private UI.Button btnDelete;
    private UI.Button btnLink;
    private UI.Button btnNew;
    private UI.Button btnRefresh;
    private UI.Button btnView;
    private UI.TextField txtName;
    private UI.CheckBox chkShowFuture;
    private UI.CheckBox chkShowCompleted;

    public DiarySelector() {
        this(false);
    }

    /**
     * @param showLinks Whether to show the link button
     */
    public DiarySelector(boolean showLinks) {
        this.showLinks = showLinks;
        init("uidiary", true, true, new DiaryRenderer(2));
    }

    public void dispose() {
        super.dispose();
    }

    public void setLink(int linkID, int linkType) {
        this.linkID = linkID;
        this.linkType = linkType;
        // Don't filter names for linked diaries
        txtName.setText("");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtName);
        ctl.add(chkShowFuture);
        ctl.add(chkShowCompleted);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    public boolean hasData() {
        return hasRecords;
    }

    /** Deactivates on screen buttons according to what the user can do */
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

        if (!Global.currentUserObject.getSecEditAllDiaryNotes()) {
            txtName.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecEditCompletedNotes()) {
            chkShowCompleted.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecEditMyDiaryNotes()) {
            btnView.setEnabled(false);
            disableDoubleClick = true;
        }
    }

    /** Fills the on screen list of diary notes */
    public void updateList() {
        Diary diary = new Diary();

        StringBuffer crit = new StringBuffer();

        // If we have a link, filter on it
        if (linkID != 0) {
            crit.append("LinkID = " + Integer.toString(linkID) +
                " AND LinkType = " + Integer.toString(linkType));
        } else {
            // Always want some criteria
            crit.append("ID > 0");
        }

        if (!txtName.getText().equals("")) {
            if (crit.length() > 0) {
                crit.append(" AND ");
            }

            crit.append("DiaryForName Like '%" + txtName.getText() + "%'");
        }

        if (!chkShowFuture.isSelected()) {
            if (crit.length() > 0) {
                crit.append(" AND ");
            }

            Calendar futureDiary = Calendar.getInstance();
            futureDiary.set(Calendar.HOUR_OF_DAY, 23);
            futureDiary.set(Calendar.MINUTE, 59);
            futureDiary.set(Calendar.SECOND, 59);
            crit.append("DiaryDateTime <= '" + Utils.getSQLDate(futureDiary) +
                "'");
        }

        if (!chkShowCompleted.isSelected()) {
            if (crit.length() > 0) {
                crit.append(" AND ");
            }

            crit.append("DateCompleted Is Null");
        }

        // Sort
        crit.append(" ORDER BY DiaryDateTime");

        diary.openRecordset(crit.toString());

        // Assign whether we have records or not
        hasRecords = !diary.getEOF();

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) diary.getRecordCount()][8];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Created_By"), i18n("For"), i18n("Date"), i18n("Link"),
                i18n("Completed"), i18n("Subject"), i18n("Note")
            };

        // Build the data
        int i = 0;

        try {
            while (!diary.getEOF()) {
                datar[i][0] = diary.getCreatedBy();
                datar[i][1] = diary.getDiaryForName();
                datar[i][2] = Utils.formatTableDateTime(diary.getDiaryDateTime());
                datar[i][3] = diary.getLinkInfoThis();
                datar[i][4] = Utils.nullToEmptyString(Utils.formatTableDate(
                            diary.getDateCompleted()));
                datar[i][5] = diary.getSubject();
                datar[i][6] = diary.getNote();
                datar[i][7] = diary.getID().toString();

                i++;
                diary.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 7);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_diary_note"), 'n',
                IconManager.getIcon(IconManager.SCREEN_DIARYBEAN_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit_the_selected_diary_note"), 'e',
                IconManager.getIcon(IconManager.SCREEN_DIARYBEAN_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_DIARYBEAN_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnDelete = UI.getButton(null, i18n("Delete_the_selected_diary_note"),
                'd', IconManager.getIcon(IconManager.SCREEN_DIARYBEAN_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        if (showLinks) {
            btnLink = UI.getButton(null,
                    i18n("View_the_record_this_diary_note_relates_to"), 'l',
                    IconManager.getIcon(IconManager.SCREEN_VIEWDIARY_VIEWLINK),
                    UI.fp(this, "actionFollowLink"));
            addToolButton(btnLink, true);
        }

        btnComplete = UI.getButton(null,
                i18n("complete_the_selected_diary_notes"), 'd',
                IconManager.getIcon(IconManager.SCREEN_DIARYBEAN_COMPLETE),
                UI.fp(this, "actionComplete"));
        addToolButton(btnComplete, true);

        UI.Panel p = getTopPanel();

        txtName = UI.getTextField();
        txtName.setText(Global.currentUserName);
        UI.addComponent(p, i18n("Name_like:"), txtName);

        chkShowFuture = UI.getCheckBox(i18n("Show_Future:"));
        p.add(chkShowFuture);

        chkShowCompleted = UI.getCheckBox(i18n("Show_Completed:"));
        p.add(chkShowCompleted);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionFollowLink() {
        try {
            int diaryID = getTable().getSelectedID();

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
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_accessing_linked_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionComplete() {
        // Get each selected diary and complete it with today's
        // date and time
        // Make sure a row is selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            // Get the ID for the selected row
            String diaryID = tablemodel.getIDAt(selrows[i]);

            String sql = "UPDATE diary SET DateCompleted = '" +
                Utils.getSQLDate(Calendar.getInstance()) + "' " +
                "WHERE ID = " + diaryID;

            try {
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                Dialog.showError(Global.i18n("uidiary",
                        "An_error_occurred_removing_the_record:_") +
                    e.getMessage());
                Global.logException(e, getClass());
            }
        }

        updateList();
    }

    public void actionDelete() {
        // Make sure a row is selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        // Ask if they are sure they want to delete the row(s)
        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            for (int i = 0; i < selrows.length; i++) {
                // Get the ID for the selected row
                String diaryID = tablemodel.getIDAt(selrows[i]);

                String sql = "DELETE FROM diary WHERE ID = " + diaryID;

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
        // Make sure a row is selected
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get a diary object for it
        Diary diary = new Diary();
        diary.openRecordset("ID = " + id);

        // Open the edit form and edit it
        DiaryEdit ed = new DiaryEdit();
        ed.openForEdit(diary, this);
        Global.mainForm.addChild(ed);
    }

    public void actionNew() {
        // Create a new edit form
        DiaryEdit ed = new DiaryEdit();
        ed.openForNew(linkID, linkType, this);
        Global.mainForm.addChild(ed);
    }
}
