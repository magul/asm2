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
package net.sourceforge.sheltermanager.asm.ui.owner;

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

import java.util.Vector;


/**
 * Viewing of owner donations
 *
 * @author Robin Rawson-Tetley
 */
public class DonationSelector extends ASMSelector {
    private int ownerID = 0;
    private int movementID = 0;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnNewInstalment;
    private UI.Button btnEdit;
    private UI.Button btnDoc;

    /** A link to the parent owner form (if there is one)
     *  so we can update media lists when necessary)
     */
    private OwnerEdit uiparent;

    public DonationSelector(OwnerEdit uiparent) {
        this.uiparent = uiparent;
        init("uiowner", false);
    }

    public void setSecurity() {
        // If we don't have an owner ID, grey out the new buttons
        btnNew.setEnabled(ownerID != 0);
        btnNewInstalment.setEnabled(ownerID != 0);

        if (!Global.currentUserObject.getSecDeleteOwnerDonation()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeOwnerDonation()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecAddOwnerDonation()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddOwnerDonation()) {
            btnNewInstalment.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnDoc.setEnabled(false);
        }
    }

    public void setLink(int ownerID, int movementID) {
        this.ownerID = ownerID;
        this.movementID = movementID;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
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
        } else {
            od.openRecordset("OwnerID = " + ownerID + " ORDER BY Date");
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) od.getRecordCount()][7];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("date_due"), i18n("date_received"), i18n("receipt_number"),
                i18n("donation"), i18n("type"), i18n("comments")
            };

        // Build the data
        int i = 0;
        double runningTotal = 0;

        try {
            while (!od.getEOF()) {
                datar[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            od.getDateDue()));
                datar[i][1] = Utils.nullToEmptyString(Utils.formatTableDate(
                            od.getDateReceived()));
                datar[i][2] = Utils.nullToEmptyString(od.getReceiptNum());
                datar[i][3] = Utils.formatCurrency(od.getDonation().doubleValue());
                datar[i][4] = od.getDonationTypeName();

                // Build the comment string - if it's an adoption donation, add
                // a bit
                // in the front showing the animal name/code
                String c = "";

                if (od.getMovementID().intValue() != 0) {
                    SQLRecordset rs = new SQLRecordset();
                    rs.openRecordset(
                        "SELECT AnimalName, ShelterCode FROM animal " +
                        "INNER JOIN adoption ON adoption.AnimalID = animal.ID " +
                        "INNER JOIN ownerdonation ON ownerdonation.MovementID = adoption.ID " +
                        "WHERE ownerdonation.MovementID = " +
                        od.getMovementID(), "animal");

                    if (!rs.getEOF()) {
                        c = "[" + rs.getField("AnimalName") + " - " +
                            rs.getField("ShelterCode") + "] " +
                            od.getComments();
                    }
                } else {
                    c = od.getComments();
                }

                datar[i][5] = c;
                datar[i][6] = od.getID().toString();

                // Keep a running total of donations displayed
                runningTotal += od.getDonation().doubleValue();

                i++;
                od.moveNext();
            }

            od.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 6);
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("create_a_new_donation"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWOWNERDONATIONS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnNewInstalment = UI.getButton(null,
                i18n("create_a_new_installment_donation"), 'i',
                IconManager.getIcon(
                    IconManager.SCREEN_VIEWOWNERDONATIONS_NEWINSTALMENT),
                UI.fp(this, "actionNewInstalment"));
        addToolButton(btnNewInstalment, false);

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
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionNewInstalment() {
        Global.mainForm.addChild(new DonationInstalmentEdit(this, ownerID,
                movementID));
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM ownerdonation WHERE ID = " + id;
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
            DonationEdit eod = new DonationEdit(this, ownerID, movementID);
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
            DonationEdit eod = new DonationEdit(this, ownerID, movementID);
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

            OwnerDonationDocument ownerdonationdoc = new OwnerDonationDocument(od,
                    uiparent.media);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
