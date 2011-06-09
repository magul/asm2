/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.OwnerDonationDocument;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Viewing of owner donations
 *
 * @author Robin Rawson-Tetley
 */
public class DonationSelector extends ASMSelector {
    private static final long serialVersionUID = -5160737061215643754L;
    private boolean hasDonations = false;
    private int animalID = 0;
    private int ownerID = 0;
    private int movementID = 0;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnEdit;
    private UI.Button btnDoc;
    private UI.Button btnReceive;

    /** A link to the parent form (if there is one)
     *  so we can update media lists when necessary)
     */
    private OwnerEdit ownerparent = null;

    public DonationSelector(OwnerEdit uiparent) {
        this.ownerparent = uiparent;
        init("uiowner", false);
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecDeleteOwnerDonation()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeOwnerDonation()) {
            btnEdit.setEnabled(false);
            btnReceive.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecAddOwnerDonation()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnDoc.setEnabled(false);
        }
    }

    public void setLink(int ownerID, int movementID) {
        this.animalID = 0;
        this.ownerID = ownerID;
        this.movementID = movementID;
    }

    public void setLink(int animalID, int ownerID, int movementID) {
        this.animalID = animalID;
        this.ownerID = ownerID;
        this.movementID = movementID;
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> v = new Vector<Object>();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    public void updateList() {
        OwnerDonation od = new OwnerDonation();

        // Get the data
        if (movementID != 0) {
            od.openRecordset("MovementID = " + movementID + " ORDER BY Date");
        } else if (animalID != 0) {
            od.openRecordset("AnimalID = " + animalID + " ORDER BY Date");
        } else {
            od.openRecordset("OwnerID = " + ownerID + " ORDER BY Date");
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) od.getRecordCount()][8];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("date_due"), i18n("date_received"), i18n("receipt_number"),
                i18n("donation"), i18n("type"), i18n("frequency"),
                i18n("comments")
            };

        // Build the data
        int i = 0;
        int runningTotal = 0;

        try {
            while (!od.getEOF()) {
                hasDonations = true;
                datar[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            od.getDateDue()));
                datar[i][1] = Utils.nullToEmptyString(Utils.formatTableDate(
                            od.getDateReceived()));
                datar[i][2] = Utils.nullToEmptyString(od.getReceiptNum());
                datar[i][3] = Utils.formatCurrency(od.getDonation().intValue());
                datar[i][4] = od.getDonationTypeName();
                datar[i][5] = LookupCache.getDonationFreqForID(od.getFrequency());

                // Build the comment string
                String c = od.getComments();

                if (animalID != 0) {
                    // We're on the animal details screen, show owner info
                    SQLRecordset rs = new SQLRecordset();
                    rs.openRecordset("SELECT OwnerName FROM owner WHERE ID = " +
                        od.getOwnerID(), "owner");

                    if (!rs.getEOF()) {
                        c = "[" + rs.getField("OwnerName") + "]" +
                            od.getComments();
                    }
                } else {
                    // We're on an owner or movement tab - show the animal info if we have some
                    if (od.getMovementID().intValue() != 0) {
                        SQLRecordset rs = new SQLRecordset();
                        rs.openRecordset(
                            "SELECT AnimalName, ShortCode, ShelterCode FROM animal " +
                            "INNER JOIN adoption ON adoption.AnimalID = animal.ID " +
                            "INNER JOIN ownerdonation ON ownerdonation.MovementID = adoption.ID " +
                            "WHERE ownerdonation.MovementID = " +
                            od.getMovementID(), "animal");

                        if (!rs.getEOF()) {
                            c = "[" + rs.getField("AnimalName") + " - " +
                                (Global.getShowShortCodes()
                                ? rs.getString("ShortCode")
                                : rs.getString("ShelterCode")) + "] " +
                                od.getComments();
                        }
                    } else if (od.getAnimalID().intValue() != 0) {
                        // Show the animal name/code for animal sponsorship too
                        SQLRecordset rs = new SQLRecordset();
                        rs.openRecordset(
                            "SELECT AnimalName, ShortCode, ShelterCode FROM animal " +
                            "WHERE ID = " + od.getAnimalID(), "animal");

                        if (!rs.getEOF()) {
                            c = "[" + rs.getField("AnimalName") + " - " +
                                (Global.getShowShortCodes()
                                ? rs.getString("ShortCode")
                                : rs.getString("ShelterCode")) + "] " +
                                od.getComments();
                        }
                    }
                }

                datar[i][6] = c;
                datar[i][7] = od.getID().toString();

                // Keep a running total of donations displayed
                runningTotal += od.getDonation().intValue();

                i++;
                od.moveNext();
            }

            od.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 7);
    }

    public boolean hasData() {
        return hasDonations;
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("create_a_new_donation"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWOWNERDONATIONS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnEdit = UI.getButton(null, i18n("edit_this_donation"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWOWNERDONATIONS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("delete_this_donation"), 'd',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWOWNERDONATIONS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnDoc = UI.getButton(null, i18n("generate_document_receipt"), 'g',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWOWNERDONATIONS_GENERATEDOC),
                UI.fp(this, "actionDocument"));
        addToolButton(btnDoc, true);

        btnReceive = UI.getButton(null, i18n("mark_this_donation_received"),
                'r',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWOWNERDONATIONS_RECEIVE),
                UI.fp(this, "actionReceive"));
        addToolButton(btnReceive, true);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                OwnerDonation od = new OwnerDonation("ID = " + id);

                String sql = "DELETE FROM ownerdonation WHERE ID = " + id;
                DBConnection.executeAction(sql);

                // If we're syncing transactions and there's a matching 
                // transaction, remove it
                if (Configuration.getBoolean("CreateDonationTrx")) {
                    DBConnection.executeAction(
                        "DELETE FROM accountstrx WHERE OwnerDonationID = " +
                        id);
                    AuditTrail.deleted("accountstrx",
                        od.getDonationTypeName() + " " +
                        od.getOwner().getOwnerName() + " " +
                        Utils.firstChars(od.getOwner().getOwnerAddress(), 20));
                }

                if (AuditTrail.enabled()) {
                    AuditTrail.changed("ownerdonation",
                        od.getDonationTypeName() + " " +
                        od.getOwner().getOwnerName() + " " +
                        Utils.firstChars(od.getOwner().getOwnerAddress(), 20));
                }

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
            DonationEdit eod = new DonationEdit(this, animalID, ownerID,
                    movementID);
            OwnerDonation od = new OwnerDonation();
            od.openRecordset("ID = " + id);
            eod.openForEdit(od);
            Global.mainForm.addChild(eod);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionNew() {
        try {
            DonationEdit eod = new DonationEdit(this, animalID, ownerID,
                    movementID);
            eod.openForNew();
            Global.mainForm.addChild(eod);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionDocument() {
        try {
            int id = getTable().getSelectedID();

            if (id == -1) {
                return;
            }

            OwnerDonation od = new OwnerDonation();
            od.openRecordset("ID = " + id);

            new OwnerDonationDocument(od,
                ((ownerparent != null) ? ownerparent.media : null));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionReceive() {
        try {
            int id = getTable().getSelectedID();

            if (id == -1) {
                return;
            }

            OwnerDonation od = new OwnerDonation();
            od.openRecordset("ID = " + id);

            // Does this donation already have a date received? If so, don't
            // do anything
            if (od.getDateReceived() != null) {
                return;
            }

            // Set the date received to today
            od.setDateReceived(Utils.getTodayNoTime());

            // Do we have a frequency > 0, the nextcreated flag isn't set
            // and there's a datereceived and a datedue?
            if ((od.getDateDue() != null) && (od.getDateReceived() != null) &&
                    (((Integer) od.getFrequency()).intValue() > 0) &&
                    (((Integer) od.getNextCreated()).intValue() == 0)) {
                OwnerDonation od2 = od.copy();

                // Clear the date received and update the date due
                Calendar c = Calendar.getInstance();
                c.setTime(((Date) od.getDateDue()));

                int freq = ((Integer) od.getFrequency()).intValue();

                switch (freq) {
                case 1: // weekly
                    c.add(Calendar.WEEK_OF_YEAR, 1);

                    break;

                case 2: // monthly
                    c.add(Calendar.MONTH, 1);

                    break;

                case 3: // quarterly
                    c.add(Calendar.MONTH, 3);

                    break;

                case 4: // half-yearly
                    c.add(Calendar.MONTH, 6);

                    break;

                case 5: // annually
                    c.add(Calendar.YEAR, 1);

                    break;
                }

                od2.setDateReceived(null);
                od2.setDateDue(c.getTime());

                // Save our next instalment
                od2.save(Global.currentUserName);

                if (AuditTrail.enabled()) {
                    AuditTrail.create("ownerdonation",
                        od2.getDonationTypeName() + " " +
                        od2.getOwner().getOwnerName() + " " +
                        Utils.firstChars(od2.getOwner().getOwnerAddress(), 20));
                }

                // Update the created flag for this donation
                od.setNextCreated(new Integer(1));
            }

            od.save(Global.currentUserName);
            od.updateAccountTrx();

            if (AuditTrail.enabled()) {
                AuditTrail.changed("ownerdonation",
                    od.getDonationTypeName() + " " +
                    od.getOwner().getOwnerName() + " " +
                    Utils.firstChars(od.getOwner().getOwnerAddress(), 20));
            }

            // Update our list
            updateList();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
