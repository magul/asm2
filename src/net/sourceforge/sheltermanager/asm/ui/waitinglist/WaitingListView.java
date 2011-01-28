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
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.WaitingList;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.ui.WaitingListRenderer;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Vector;


/**
 * This class contains all code for viewing waiting list entries.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class WaitingListView extends ASMView {
    private UI.Button btnComplete;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnPrint;
    private UI.Button btnRefresh;
    private UI.Button btnView;
    private UI.Button btnHighlight;
    private UI.ComboBox cboPriority;
    private UI.ComboBox cboSpecies;
    private UI.CheckBox chkShowRemoved;
    private UI.TextField txtAddress;
    private UI.TextField txtContactName;
    private UI.TextField txtDescription;

    /** Creates new form ViewWaitingList */
    public WaitingListView() {
        updateWaitingList();
        init(Global.i18n("uiwaitinglist", "Waiting_List"),
            IconManager.getIcon(IconManager.SCREEN_VIEWWAITINGLIST),
            "uiwaitinglist", true, true,
            new WaitingListRenderer(WaitingListViewColumns.getColumnCount() +
                1, WaitingListViewColumns.getColumnCount() + 2));
        updateList();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
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
                "SELECT animalwaitinglist.*, owner.OwnerName, owner.OwnerAddress, " +
                "owner.OwnerTown, owner.OwnerCounty, owner.OwnerPostcode, " +
                "owner.HomeTelephone, owner.WorkTelephone, owner.MobileTelephone " +
                "FROM animalwaitinglist INNER JOIN owner ON " +
                "owner.ID = animalwaitinglist.OwnerID WHERE " + crit,
                "animalwaitinglist");
        } catch (Exception e) {
            Global.logException(e, getClass());

            return;
        }

        // Create an array to hold the results for the table
        int cols = WaitingListViewColumns.getColumnCount();
        String[][] datar = new String[rs.size()][cols + 3];
        int idColumn = cols;
        int urgencyColumn = cols + 1;
        int hiliteColumn = cols + 2;

        // Build the data
        int i = 0;

        try {
            while (!rs.getEOF()) {
                for (int z = 0; z < WaitingListViewColumns.getColumnCount();
                        z++) {
                    datar[i][z] = WaitingListViewColumns.format(WaitingListViewColumns.getColumnName(
                                z), rs);
                }

                datar[i][idColumn] = rs.getString("ID");
                datar[i][urgencyColumn] = rs.getString("Urgency");
                datar[i][hiliteColumn] = (Configuration.getString(
                        "WaitingListHighlights")
                                                       .indexOf(rs.getString(
                            "ID") + " ") != -1) ? "1" : "0";

                i++;
                rs.moveNext();
            }

            rs.free();
            rs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(WaitingListViewColumns.getColumnLabels(), datar, i,
            cols + 3, idColumn);

        // Go to previous selection
        if (lastSel != -1) {
            getTable().changeSelection(lastSel, 0, false, false);
            getTable().grabFocus();
        }
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

    @SuppressWarnings("unused")
    private String formatUrgencyNumberAsString(int urgency) {
        return LookupCache.getUrgencyNameForID(new Integer(urgency));
    }

    private int formatUrgencyNameAsNumber(String urgency) {
        return LookupCache.getUrgencyIDForName(urgency).intValue();
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

        btnHighlight = UI.getButton(null,
                i18n("toggle_highlighting_of_the_selected_waiting_list_entries"),
                'h',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWWAITINGLIST_HIGHLIGHT),
                UI.fp(this, "actionHighlight"));
        addToolButton(btnHighlight, true);

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
                Utils.getSQLDate(Calendar.getInstance()) + "' " +
                "WHERE ID = " + wlID;

            try {
                DBConnection.executeAction(sql);

                if (AuditTrail.enabled()) {
                    AuditTrail.changed("animalwaitinglist", getAuditTableInfo(i));
                }
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                Global.logException(e, getClass());
            }
        }

        updateList();
    }

    public void actionHighlight() {
        // Get each selected waiting list and either add it to
        // our list of highlighted entries, or remove it if its
        // already on
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        // Grab our list of IDs
        String[] hd = Configuration.getString("WaitingListHighlights").split(" ");

        // Put them in a set
        HashSet<String> hl = new HashSet<String>(hd.length);

        for (int i = 0; i < hd.length; i++) {
            hl.add(hd[i]);
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            // Get the ID for the selected row
            String wlID = tablemodel.getIDAt(selrows[i]);

            // If it's already in the set, remove it
            if (hl.contains(wlID)) {
                hl.remove(wlID);
            } else {
                // Otherwise, add it
                hl.add(wlID);
            }
        }

        // Join the set back together and update the config
        StringBuffer highlights = new StringBuffer(hd.length * 3);

        for (String s : hl) {
            highlights.append(s).append(" ");
        }

        Configuration.setEntry("WaitingListHighlights", highlights.toString());

        updateList();
    }

    public void actionPrint() {
        // Send the table model over to the
        // waiting list report.
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();
        new WaitingList(tablemodel);
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

                    if (AuditTrail.enabled()) {
                        AuditTrail.deleted("animalwaitinglist",
                            getAuditTableInfo(i));
                    }
                } catch (Exception e) {
                    Dialog.showError(UI.messageDeleteError() + e.getMessage());
                    Global.logException(e, getClass());
                }
            }

            updateList();
        }
    }

    public String getAuditTableInfo(int row) {
        String s = "";

        for (int i = 0; i < WaitingListViewColumns.getColumnCount(); i++) {
            s += (table.getModel().getValueAt(row, i).toString() + " ");
        }

        return s;
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
