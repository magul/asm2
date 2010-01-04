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
package net.sourceforge.sheltermanager.asm.ui.owner;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.mailmerge.OwnerMailMerge;
import net.sourceforge.sheltermanager.asm.reports.OwnerSearchResults;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMFind;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * This class contains all code for searching the owner database.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class OwnerFind extends ASMFind {
    public final static int FILTER_ALL = 0;
    public final static int FILTER_ADOPTERS = 1;
    public final static int FILTER_ACO = 2;
    public final static int FILTER_BANNED = 3;
    public final static int FILTER_DONORS = 4;
    public final static int FILTER_FOSTERERS = 5;
    public final static int FILTER_HOMECHECKED = 6;
    public final static int FILTER_HOMECHECKERS = 7;
    public final static int FILTER_MEMBERS = 8;
    public final static int FILTER_NOTHOMECHECKED = 9;
    public final static int FILTER_POTENTIALADOPTERS = 10;
    public final static int FILTER_RETAILERS = 11;
    public final static int FILTER_SHELTERS = 12;
    public final static int FILTER_STAFF = 13;
    public final static int FILTER_VETS = 14;
    public final static int FILTER_VOLUNTEERS = 15;

    /** The object listening for a selected response */
    private SearchListener listener = null;

    /** If the call wants us to fire the retailerSelectedEvent */
    private boolean useRetailerSelectedEvent = false;
    private boolean closeAfterSelection = true;
    private UI.Button btnClear;
    private UI.Button btnMailMerge;
    public UI.Button btnPrint;
    public UI.Button btnSimple;
    public UI.ComboBox cboFilter;
    public UI.TextField txtAddress;
    public UI.TextField txtComments;
    public UI.TextField txtCounty;
    public UI.TextField txtEmail;
    public UI.TextField txtHomeCheckAreas;
    public UI.TextField txtMediaNotes;
    public UI.TextField txtName;
    public UI.TextField txtPostcode;
    public UI.TextField txtTown;

    public OwnerFind() {
        this(null, true, false);
    }

    public OwnerFind(SearchListener listener, boolean closeAfterSelection,
        boolean useRetailerSelectedEvent) {
        this.closeAfterSelection = closeAfterSelection;
        this.useRetailerSelectedEvent = useRetailerSelectedEvent;
        this.listener = listener;
        init(Global.i18n("uiowner", "Find_Owner"),
            IconManager.getIcon(IconManager.SCREEN_FINDOWNER), "uiowner", 4,
            false, listener != null);

        // Set filter to retailer and don't allow changing if the flag is set
        if (useRetailerSelectedEvent) {
            cboFilter.setSelectedIndex(12);
            cboFilter.setEnabled(false);
        }
    }

    public void dispose() {
        listener = null;
        super.dispose();
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecMailMergeOwners()) {
            btnMailMerge.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewCustomReports()) {
            btnPrint.setEnabled(false);
        }
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtName);
        ctl.add(txtAddress);

        if (!Configuration.getBoolean("HideTownCounty")) {
            ctl.add(txtTown);
            ctl.add(txtCounty);
        }

        ctl.add(txtPostcode);
        ctl.add(txtHomeCheckAreas);
        ctl.add(txtComments);
        ctl.add(txtEmail);
        ctl.add(txtMediaNotes);
        ctl.add(cboFilter);
        ctl.add(getTable());
        ctl.add(btnSearch);
        ctl.add(btnOpen);
        ctl.add(btnClear);
        ctl.add(btnMailMerge);
        ctl.add(btnPrint);
        ctl.add(btnSimple);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtName;
    }

    public void initLeftbar() {
    }

    public void initToolbar() {
        btnClear = UI.getButton(i18n("Clear"), null, 'c',
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_CLEAR),
                UI.fp(this, "actionClear"));
        addToolbarItem(btnClear, false);

        btnMailMerge = UI.getButton(i18n("Mail_Merge"), null, 'm',
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_MAILMERGE),
                UI.fp(this, "actionMailMerge"));
        addToolbarItem(btnMailMerge, true);

        btnPrint = UI.getButton(i18n("Print"), null, 'p',
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_PRINT),
                UI.fp(this, "actionPrint"));
        addToolbarItem(btnPrint, true);

        btnSimple = UI.getButton(i18n("Simple"), null, 'i',
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_SIMPLE),
                UI.fp(this, "actionSimple"));
        addToolbarItem(btnSimple, false);
    }

    public void initCriteria(UI.Panel p) {
        boolean showTown = true;
        showTown = !Configuration.getBoolean("HideTownCounty");

        txtName = (UI.TextField) UI.addComponent(p, i18n("Name_contains:"),
                UI.getTextField());
        txtAddress = (UI.TextField) UI.addComponent(p,
                i18n("Address_contains:"), UI.getTextField());

        if (showTown) {
            txtTown = (UI.TextField) UI.addComponent(p, i18n("Town_Contains"),
                    UI.getTextField());
            txtCounty = (UI.TextField) UI.addComponent(p,
                    i18n("County_Contains"), UI.getTextField());
        }

        txtPostcode = (UI.TextField) UI.addComponent(p,
                i18n("Postcode_contains:"), UI.getTextField());
        txtHomeCheckAreas = (UI.TextField) UI.addComponent(p,
                i18n("Homecheck_Areas:"), UI.getTextField());
        txtComments = (UI.TextField) UI.addComponent(p,
                i18n("Comments_contain:"),
                UI.getTextField(i18n("Comments_contain._Use_spaces_to_separate_individual_words")));
        txtEmail = (UI.TextField) UI.addComponent(p, i18n("Email:"),
                UI.getTextField(i18n("A_full,_or_part_email_address_for_the_owner")));
        txtMediaNotes = (UI.TextField) UI.addComponent(p,
                i18n("Media_notes_contain:"),
                UI.getTextField(i18n("Media_notes_contain._Use_spaces_to_separate_individual_words")));

        cboFilter = UI.getCombo();
        cboFilter.addItem(i18n("_all_owners_"));
        cboFilter.addItem(i18n("Adopters"));
        cboFilter.addItem(i18n("Animal_Care_Officers"));
        cboFilter.addItem(i18n("Banned"));
        cboFilter.addItem(i18n("Donors"));
        cboFilter.addItem(i18n("Fosterers"));
        cboFilter.addItem(i18n("Homechecked"));
        cboFilter.addItem(i18n("Homecheckers"));
        cboFilter.addItem(i18n("Members"));
        cboFilter.addItem(i18n("Not_Homechecked"));
        cboFilter.addItem(i18n("Potential_Adopters"));
        cboFilter.addItem(i18n("Retailers"));
        cboFilter.addItem(i18n("Shelters"));
        cboFilter.addItem(i18n("Staff"));
        cboFilter.addItem(i18n("Vets"));
        cboFilter.addItem(i18n("Volunteers"));
        cboFilter.setSelectedIndex(FILTER_ALL);
        UI.addComponent(p, i18n("Show"), cboFilter);
    }

    public void actionPrint() {
        new OwnerSearchResults(((SortableTableModel) getTable().getModel()).getData(),
            getTable().getModel().getRowCount());
    }

    public void actionMailMerge() {
        if (getTable().getModel() == null) {
            return;
        }

        // Pass the data on to the Mail Merge class for this job
        new OwnerMailMerge((SortableTableModel) getTable().getModel());
    }

    public void actionSimple() {
        Global.mainForm.addChild(new OwnerFindText(listener,
                closeAfterSelection, useRetailerSelectedEvent));
        dispose();
    }

    public void actionClear() {
        try {
            txtName.setText("");
            txtAddress.setText("");
            txtHomeCheckAreas.setText("");
            txtPostcode.setText("");
            txtEmail.setText("");
            txtTown.setText("");
            txtCounty.setText("");
            txtMediaNotes.setText("");
            cboFilter.setSelectedIndex(FILTER_ALL);
            txtComments.setText("");
        } catch (Exception e) {
        }
    }

    public void itemSelected(int id) {
        // Open the owner object
        Owner owner = new Owner();
        owner.openRecordset("ID = " + id);

        // If we are in selection mode, return the event
        // with the object and destroy this form if
        // the flag is set
        if (selectionMode) {
            // If we are in retailer mode, fire the
            // correct callback event
            if (useRetailerSelectedEvent) {
                listener.retailerSelected(owner);
            } else {
                listener.ownerSelected(owner);
            }

            if (closeAfterSelection) {
                dispose();
            }
        } else {
            // Open the record for editing
            OwnerEdit eo = new OwnerEdit();
            eo.openForEdit(owner);
            Global.mainForm.addChild(eo);
            eo = null;
        }
    }

    public void runSearch() {
        SQLRecordset owner = new SQLRecordset();
        String sql = null;

        // If we have a media search, do a join to get the notes,
        // otherwise, there's no need to bother and we can do the
        // search faster with a straight SELECT on the owner table.
        if (!txtMediaNotes.getText().trim().equals("")) {
            sql = "SELECT DISTINCT owner.* FROM owner LEFT JOIN media ON media.LinkID = owner.ID WHERE ";
            addSqlCriteria("owner.ID > 0");
        }

        // If the user is searching for adopters, do a join onto the adoption
        // table
        // to find records where they adopted something
        if (cboFilter.getSelectedIndex() == FILTER_ADOPTERS) {
            sql = "SELECT DISTINCT owner.* FROM owner INNER JOIN adoption ON adoption.OwnerID = owner.ID WHERE ";
            addSqlCriteria("adoption.MovementType=" +
                Adoption.MOVETYPE_ADOPTION);
        }

        // Both joins
        if ((!txtMediaNotes.getText().trim().equals("")) &&
                (cboFilter.getSelectedIndex() == FILTER_ADOPTERS)) {
            sql = "SELECT DISTINCT owner.* FROM owner LEFT JOIN media ON media.LinkID = owner.ID " +
                "INNER JOIN adoption ON adoption.OwnerID = owner.ID WHERE ";
            addSqlCriteria("owner.ID > 0 AND adoption.MovementType=" +
                Adoption.MOVETYPE_ADOPTION);
        }

        // Bare search - using neither
        if (sql == null) {
            sql = "SELECT * FROM owner WHERE ";
            addSqlCriteria("owner.ID > 0");
        }

        if (!txtName.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("OwnerName Like '%" +
                    txtName.getText().replace('\'', '`') + "%'");
            } else {
                addSqlCriteria("UPPER(OwnerName) Like '%" +
                    Utils.upper(txtName.getText()).replace('\'', '`') + "%'");
            }
        }

        if (!txtAddress.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("OwnerAddress Like '%" + txtAddress.getText() +
                    "%'");
            } else {
                addSqlCriteria("UPPER(OwnerAddress) Like '%" +
                    Utils.upper(txtAddress.getText()) + "%'");
            }
        }

        if ((txtTown != null) && !txtTown.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("OwnerTown Like '%" + txtTown.getText() + "%'");
            } else {
                addSqlCriteria("UPPER(OwnerTown) Like '%" +
                    Utils.upper(txtTown.getText()) + "%'");
            }
        }

        if ((txtCounty != null) && !txtCounty.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("OwnerCounty Like '%" + txtCounty.getText() +
                    "%'");
            } else {
                addSqlCriteria("UPPER(OwnerCounty) Like '%" +
                    Utils.upper(txtCounty.getText()) + "%'");
            }
        }

        if (!txtPostcode.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("OwnerPostcode Like '%" + txtPostcode.getText() +
                    "%'");
            } else {
                addSqlCriteria("UPPER(OwnerPostcode) Like '%" +
                    Utils.upper(txtPostcode.getText()) + "%'");
            }
        }

        if (!txtHomeCheckAreas.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("HomeCheckAreas Like '%" +
                    txtHomeCheckAreas.getText() + "%'");
            } else {
                addSqlCriteria("UPPER(HomeCheckAreas) Like '%" +
                    Utils.upper(txtHomeCheckAreas.getText()) + "%'");
            }
        }

        // Filter box =====================================

        // Adopters
        // Taken care of in join above

        // Banned
        if (cboFilter.getSelectedIndex() == FILTER_BANNED) {
            addSqlCriteria("IsBanned = 1");
        }
        // Homechecked
        else if (cboFilter.getSelectedIndex() == FILTER_HOMECHECKED) {
            addSqlCriteria("IDCheck = 1");
        }
        // Not homechecked
        else if (cboFilter.getSelectedIndex() == FILTER_NOTHOMECHECKED) {
            addSqlCriteria("IDCheck = 0");
        }
        // Members
        else if (cboFilter.getSelectedIndex() == FILTER_MEMBERS) {
            addSqlCriteria("IsMember = 1");
        }
        // Donors
        else if (cboFilter.getSelectedIndex() == FILTER_DONORS) {
            addSqlCriteria("IsDonor = 1");
        }
        // Homecheckers
        else if (cboFilter.getSelectedIndex() == FILTER_HOMECHECKERS) {
            addSqlCriteria("IsHomeChecker = 1");
        }
        // Volunteers
        else if (cboFilter.getSelectedIndex() == FILTER_VOLUNTEERS) {
            addSqlCriteria("IsVolunteer = 1");
        }
        // Shelters
        else if (cboFilter.getSelectedIndex() == FILTER_SHELTERS) {
            addSqlCriteria("IsShelter = 1");
        }
        // ACOs
        else if (cboFilter.getSelectedIndex() == FILTER_ACO) {
            addSqlCriteria("IsACO = 1");
        }
        // Staff
        else if (cboFilter.getSelectedIndex() == FILTER_STAFF) {
            addSqlCriteria("IsStaff = 1");
        }
        // Retailers
        else if (cboFilter.getSelectedIndex() == FILTER_RETAILERS) {
            addSqlCriteria("IsRetailer = 1");
        }
        // Fosterers
        else if (cboFilter.getSelectedIndex() == FILTER_FOSTERERS) {
            addSqlCriteria("IsFosterer = 1");
        }
        // Vets
        else if (cboFilter.getSelectedIndex() == FILTER_VETS) {
            addSqlCriteria("IsVet = 1");
        }
        // Potential adopters
        else if (cboFilter.getSelectedIndex() == FILTER_POTENTIALADOPTERS) {
            addSqlCriteria("MatchActive = 1 AND 0 = " +
                "(SELECT COUNT(*) FROM adoption WHERE MovementType = 1 AND " +
                "OwnerID = owner.ID)");
        }

        // The comments search is a bunch of individual words, separated by
        // spaces.
        if (!txtComments.getText().equals("")) {
            String[] words = Utils.split(txtComments.getText(), " ");
            int i = 0;

            while (i < words.length) {
                if (Configuration.getBoolean("CaseSensitiveSearch")) {
                    addSqlCriteria("Comments Like '%" + words[i] + "%'");
                } else {
                    addSqlCriteria("UPPER(Comments) Like '%" +
                        Utils.upper(words[i]) + "%'");
                }

                i++;
            }
        }

        // The media note search is a bunch of individual words, separated by
        // spaces.
        if (!txtMediaNotes.getText().trim().equals("")) {
            addSqlCriteria("media.LinkTypeID = " + Media.LINKTYPE_OWNER);

            String[] words = Utils.split(txtMediaNotes.getText(), " ");
            int i = 0;

            while (i < words.length) {
                if (Configuration.getBoolean("CaseSensitiveSearch")) {
                    addSqlCriteria("MediaNotes Like '%" + words[i] + "%'");
                } else {
                    addSqlCriteria("UPPER(MediaNotes) Like '%" +
                        Utils.upper(words[i]) + "%'");
                }

                i++;
            }
        }

        if (!txtEmail.getText().equals("")) {
            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("EmailAddress Like '%" + txtEmail.getText() +
                    "%'");
            } else {
                addSqlCriteria("UPPER(EmailAddress) Like '%" +
                    Utils.upper(txtEmail.getText()) + "%'");
            }
        }

        // Search limit
        int limit = Global.getRecordSearchLimit();

        if (limit != 0) {
            sqlCriteria.append(" LIMIT " + limit);
        }

        try {
            Global.logDebug("Owner/Entity search: " + getSqlCriteria(),
                "FindOwnerSearch.run");
            owner.openRecordset(sql + getSqlCriteria(), "owner");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] tabledata = new String[(int) owner.getRecordCount()][13];

        // Initialise the progress meter
        initStatusBarMax((int) owner.getRecordCount());

        // Create an array of headers for the details
        String[] columnheaders = {
                i18n("Name"), i18n("Surname"), i18n("Banned"),
                i18n("Homechecked"), i18n("Address"), i18n("Town"),
                i18n("County"), i18n("Postcode"), i18n("Home_Tel"),
                i18n("Work_Tel"), i18n("Mobile_Te"), i18n("email")
            };

        int i = 0;

        while (!owner.getEOF()) {
            // Add this owner record to the table data
            try {
                tabledata[i][0] = owner.getField("OwnerName").toString();
                tabledata[i][1] = owner.getField("OwnerSurname").toString();

                if (owner.getField("IsBanned").equals(new Integer(0))) {
                    tabledata[i][2] = i18n("No");
                } else {
                    tabledata[i][2] = i18n("Yes");
                }

                if (owner.getField("IDCheck").equals(new Integer(0))) {
                    tabledata[i][3] = i18n("No");
                } else {
                    tabledata[i][3] = i18n("Yes");
                }

                tabledata[i][4] = Utils.formatAddress(Utils.nullToEmptyString(
                            (String) owner.getField("OwnerAddress")));
                tabledata[i][5] = Utils.nullToEmptyString((String) owner.getField(
                            "OwnerTown"));
                tabledata[i][6] = Utils.nullToEmptyString((String) owner.getField(
                            "OwnerCounty"));
                tabledata[i][7] = Utils.nullToEmptyString((String) owner.getField(
                            "OwnerPostcode"));
                tabledata[i][8] = Utils.nullToEmptyString((String) owner.getField(
                            "HomeTelephone"));
                tabledata[i][9] = Utils.nullToEmptyString((String) owner.getField(
                            "WorkTelephone"));
                tabledata[i][10] = Utils.nullToEmptyString((String) owner.getField(
                            "MobileTelephone"));
                tabledata[i][11] = Utils.nullToEmptyString((String) owner.getField(
                            "EmailAddress"));
                tabledata[i][12] = owner.getField("ID").toString();
            } catch (Exception e) {
            }

            // Next record
            try {
                owner.moveNext();
                i++;
            } catch (Exception e) {
            }

            incrementStatusBar();
        }

        setTableData(columnheaders, tabledata, i, 12);

        owner.free();
        owner = null;
        tabledata = null;
        columnheaders = null;
    }
}
