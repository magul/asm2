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
 MA 02111-130, USA.

 Contact me by electronic mail: bobintetley@users.sourceforge.net
 */
package net.sourceforge.sheltermanager.asm.ui.waitinglist;

import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for viewing waiting list entries.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class WaitingListView extends ASMView {
    private UI.Button btnComplete;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnPrint;
    private UI.Button btnRefresh;
    private UI.Button btnView;
    private UI.ComboBox cboPriority;
    private UI.ComboBox cboSpecies;
    private UI.CheckBox chkShowRemoved;
    private UI.TextField txtAddress;
    private UI.TextField txtContactName;
    private UI.TextField txtDescription;
    private SQLRecordset ranks = null;

    /** Creates new form ViewWaitingList */
    public WaitingListView() {
        updateWaitingList();
        init(Global.i18n("uiwaitinglist", "Waiting_List"),
            IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST),
            "uiwaitinglist", true, true, null);
        updateList();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(getTable());
        ctl.add(btnNew);
        ctl.add(btnView);
        ctl.add(btnDelete);
        ctl.add(btnRefresh);
        ctl.add(btnPrint);
        ctl.add(cboPriority);
        ctl.add(cboSpecies);
        ctl.add(chkShowRemoved);
        ctl.add(txtContactName);
        ctl.add(txtAddress);
        ctl.add(txtDescription);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboPriority;
    }

    /** Deactivates screen components according to user permissions */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddWaitingList()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeWaitingList()) {
            btnView.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteWaitingList()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecBulkCompleteWaitingList()) {
            btnComplete.setEnabled(false);
        }
    }

    /** Fills the list according to the criteria selected */
    public void updateList() {
        // Clear our cache of the waiting list, used to figure out rankings
        if (ranks != null) {
            ranks.free();
            ranks = null;
        }

        // Record any existing selection
        int lastSel = getTable().getSelectedRow();

        SQLRecordset rs = new SQLRecordset();

        // Sort out criteria
        String crit = "";

        // Urgency
        crit = crit + "Urgency <= " +
            formatUrgencyNameAsNumber((String) cboPriority.getSelectedItem());

        // Removed entries
        if (!chkShowRemoved.isSelected()) {
            crit = crit + " AND DateRemovedFromList Is Null";
        }

        // Species
        String selspecies = (String) cboSpecies.getSelectedItem();

        if ((selspecies != null) &&
                !selspecies.equalsIgnoreCase(i18n("(all)"))) {
            String speciesid = Utils.getID("species", "SpeciesName", selspecies)
                                    .toString();
            crit = crit + " AND SpeciesID = " + speciesid;
        }

        // Contact name
        if (Configuration.getBoolean("CaseSensitiveSearch")) {
            crit = crit + " AND OwnerName Like '%" + txtContactName.getText() +
                "%'";
        } else {
            crit = crit + " AND UPPER(OwnerName) Like '%" +
                txtContactName.getText().toUpperCase() + "%'";
        }

        // Contact address
        if (Configuration.getBoolean("CaseSensitiveSearch")) {
            crit = crit + " AND OwnerAddress Like '%" + txtAddress.getText() +
                "%'";
        } else {
            crit = crit + " AND UPPER(OwnerAddress) Like '%" +
                txtAddress.getText().toUpperCase() + "%'";
        }

        // Description
        if (Configuration.getBoolean("CaseSensitiveSearch")) {
            crit = crit + " AND AnimalDescription Like '%" +
                txtDescription.getText() + "%'";
        } else {
            crit = crit + " AND UPPER(AnimalDescription) Like '%" +
                txtDescription.getText().toUpperCase() + "%'";
        }

        // Sort order
        crit = crit + " ORDER BY Urgency, DatePutOnList";

        try {
            // Get the data
            rs.openRecordset(
                "SELECT animalwaitinglist.*, owner.OwnerName, owner.OwnerAddress, owner.HomeTelephone FROM animalwaitinglist INNER JOIN owner ON " +
                "owner.ID = animalwaitinglist.OwnerID WHERE " + crit,
                "animalwaitinglist");
        } catch (Exception e) {
            Global.logException(e, getClass());

            return;
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) rs.getRecordCount()][10];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Rank"), i18n("Name"), i18n("Address"), i18n("Telephone"),
                i18n("Date_Put_On"), i18n("Date_Removed"), i18n("Urgency"),
                i18n("Species"), i18n("Description:")
            };

        // Build the data
        int i = 0;

        try {
            while (!rs.getEOF()) {
                datar[i][0] = getRank((Integer) rs.getField("ID"));
                datar[i][1] = (String) rs.getField("OwnerName");
                datar[i][2] = Utils.formatAddress((String) rs.getField(
                            "OwnerAddress"));
                datar[i][3] = (String) rs.getField("HomeTelephone");
                datar[i][4] = Utils.formatTableDate((Date) rs.getField(
                            "DatePutOnList"));
                datar[i][5] = Utils.nullToEmptyString(Utils.formatTableDate(
                            (Date) rs.getField("DateRemovedFromList")));
                datar[i][6] = formatUrgencyNumberAsString(((Integer) rs.getField(
                            "Urgency")).intValue());
                datar[i][7] = LookupCache.getSpeciesName((Integer) rs.getField(
                            "SpeciesID"));
                datar[i][8] = (String) rs.getField("AnimalDescription");
                datar[i][9] = rs.getField("ID").toString();

                i++;
                rs.moveNext();
            }

            rs.free();
            rs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 9);

        // Go to previous selection
        if (lastSel != -1) {
            getTable().changeSelection(lastSel, 0, false, false);
            getTable().grabFocus();
        }
    }

    /**
     * Calculates the rank (order) of any item on the waiting list
     * - if the item is no longer on the list, an empty string is returned
     */
    public String getRank(Integer id) {
        try {
            if (ranks == null) {
                ranks = new SQLRecordset();

                if (!Configuration.getBoolean("WaitingListRankBySpecies"))
                    ranks.openRecordset("SELECT ID FROM animalwaitinglist " +
                        "WHERE DateRemovedFromList Is Null ORDER BY Urgency, DatePutOnList",
                        "animalwaitinglist");
                else
                    ranks.openRecordset("SELECT SpeciesID, ID FROM animalwaitinglist " +
                        "WHERE DateRemovedFromList Is Null ORDER BY SpeciesID, Urgency, DatePutOnList",
                        "animalwaitinglist");

            } else {
                ranks.moveFirst();
            }

            int i = 1;
            Integer lastspecies = new Integer(0);

            while (!ranks.getEOF()) {

                // If we're ranking by species, reset when the species changes
                if (Configuration.getBoolean("WaitingListRankBySpecies")) {
                    if (!lastspecies.equals(ranks.getField("SpeciesID"))) {
                        lastspecies = (Integer) ranks.getField("SpeciesID");
                        i = 1;
                    }
                }

                if (ranks.getField("ID").equals(id)) {
                    return Integer.toString(i);
                }

                i++;
                ranks.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return "";
    }

    private String formatUrgencyNumberAsString(int urgency) {
        return LookupCache.getUrgencyNameForID(new Integer(urgency));
    }

    private int formatUrgencyNameAsNumber(String urgency) {
        return LookupCache.getUrgencyIDForName(urgency).intValue();
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
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

    /**
     * This routine handles all updating of the urgencies. This means that
     * animals on the list for a set amount of time can have their urgency
     * bumped up. This value is configurable in the table by changing
     * "WaitingListUrgencyUpdatePeriod"
     *
     * This routine also calls the autoRemoveItems method to close waiting list
     * entries that have gone so long without owner contact.
     */
    private void updateWaitingList() {
        // Auto remove entries
        AnimalWaitingList.autoRemoveItems();

        // Update urgencies
        AnimalWaitingList.updateUrgencies();
    }

    public void addToolButtons() {
        UI.Panel p = getTopPanel();

        btnNew = UI.getButton(null, i18n("New_Waiting_List_Entry"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit_this_entry"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_entry"), 'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnPrint = UI.getButton(null,
                i18n("Print_the_current_view_of_the_waiting_list"), 'p',
                IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST_PRINT),
                UI.fp(this, "actionPrint"));
        addToolButton(btnPrint, true);

        btnComplete = UI.getButton(null,
                i18n("complete_the_selected_waiting_list_entries"), 'c',
                IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST_COMPLETE),
                UI.fp(this, "actionComplete"));
        addToolButton(btnComplete, true);

        p.setLayout(UI.getGridLayout(4));

        cboPriority = UI.getCombo(LookupCache.getUrgencyLookup(), "Urgency");
        UI.addComponent(p, i18n("Priority_Floor:"), cboPriority);
        cboPriority.setSelectedIndex((int) (LookupCache.getUrgencyLookup()
                                                       .getRecordCount() - 1));

        chkShowRemoved = (UI.CheckBox) UI.addComponent(p,
                i18n("Show_Removed:"), UI.getCheckBox());

        cboSpecies = UI.getCombo(i18n("Species:"),
                LookupCache.getSpeciesLookup(), "SpeciesName", null,
                i18n("(all)"));
        UI.addComponent(p, i18n("Species:"), cboSpecies);
        Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
            new Integer(Configuration.getInteger("AFDefaultSpecies")),
            cboSpecies);

        txtContactName = (UI.TextField) UI.addComponent(p,
                i18n("Name_Contains:"), UI.getTextField());

        txtAddress = (UI.TextField) UI.addComponent(p,
                i18n("Address_Contains:"), UI.getTextField());

        txtDescription = (UI.TextField) UI.addComponent(p,
                i18n("Description_Contains:"), UI.getTextField());
    }

    public void actionComplete() {
        // Get each selected waiting list and complete it with today's
        // date and time.
        // Make sure a row is selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            // Get the ID for the selected row
            String wlID = tablemodel.getIDAt(selrows[i]);

            String sql = "UPDATE animalwaitinglist SET DateRemovedFromList = '" +
                Utils.getSQLDateOnly(Calendar.getInstance()) + "' " +
                "WHERE ID = " + wlID;

            try {
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                Global.logException(e, getClass());
            }
        }

        updateList();
    }

    public void actionPrint() {
        // Send the table model over to the
        // waiting list report.
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();
        new net.sourceforge.sheltermanager.asm.reports.WaitingList(tablemodel);
    }

    public void actionDelete() {
        // Make sure a row is selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        // Ask if they are sure they want to delete the row(s)
        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            int[] selrows = getTable().getSelectedRows();
            SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                     .getModel();

            for (int i = 0; i < selrows.length; i++) {
                // Get the ID for the selected row
                String wlID = tablemodel.getIDAt(selrows[i]);

                String sql = "DELETE FROM animalwaitinglist WHERE ID = " +
                    wlID;

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

        // Open an object
        AnimalWaitingList awl = new AnimalWaitingList();
        awl.openRecordset("ID = " + id);

        // Create the form
        WaitingListEdit ewl = new WaitingListEdit(this);
        ewl.openForEdit(awl);
        Global.mainForm.addChild(ewl);
    }

    public void actionNew() {
        WaitingListEdit ewl = new WaitingListEdit(this);
        ewl.openForNew();
        Global.mainForm.addChild(ewl);
    }
}
