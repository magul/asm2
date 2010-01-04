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

import net.sourceforge.sheltermanager.asm.bo.DonationType;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.util.Vector;


/**
 * Edit a donation
 *
 * @author Robin Rawson-Tetley
 */
public class DonationEdit extends ASMForm {
    private DonationSelector parent = null;
    private OwnerDonation od = null;
    private int ownerID = 0;
    private int movementID = 0;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.TextArea txtComments;
    private DateField txtDateDue;
    private DateField txtDateReceived;
    private CurrencyField txtDonation;
    private UI.ComboBox cboDonationType;
    private String audit = null;

    /** Creates new form EditOwnerDonation */
    public DonationEdit(DonationSelector parent, int ownerID, int movementID) {
        this.ownerID = ownerID;
        this.movementID = movementID;
        this.parent = parent;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITOWNERDONATION),
            "uiowner");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtDateDue.getTextField());
        ctl.add(txtDateReceived.getTextField());
        ctl.add(txtDonation.getTextField());
        ctl.add(cboDonationType);
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboDonationType;
    }

    public void dispose() {
        try {
            od.free();
        } catch (Exception e) {
        }

        parent = null;
        od = null;
        super.dispose();
    }

    public void openForEdit(OwnerDonation od) {
        this.od = od;

        try {
            this.txtDateDue.setText(Utils.formatDate(od.getDateDue()));
            this.txtDateReceived.setText(Utils.formatDate(od.getDateReceived()));
            this.txtDonation.setText(od.getDonation().toString());
            Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                "DonationName", od.getDonationTypeID(), cboDonationType);
            this.txtComments.setText(od.getComments());
            this.setTitle(i18n("edit_owner_donation"));
            audit = UI.messageAudit(od.getCreatedDate(), od.getCreatedBy(),
                    od.getLastChangedDate(), od.getLastChangedBy());
        } catch (Exception e) {
        }
    }

    public void openForNew() {
        try {
            this.od = new OwnerDonation();
            od.openRecordset("ID = 0");
            od.addNew();
            od.setOwnerID(new Integer(ownerID));
            od.setMovementID(new Integer(movementID));

            if (movementID > 0) {
                this.txtDonation.setText(LookupCache.getDonationAmountForMovementSpecies(
                        movementID).toString());
            }

            this.setTitle(i18n("new_owner_donation"));
        } catch (Exception e) {
            Dialog.showError(i18n("unable_to_create_new_ownerdonation") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel but = UI.getPanel(UI.getFlowLayout());

        txtDateDue = (DateField) UI.addComponent(top, i18n("date_due"),
                UI.getDateField());

        txtDateReceived = (DateField) UI.addComponent(top,
                i18n("date_received"), UI.getDateField());

        txtDonation = (CurrencyField) UI.addComponent(top, i18n("donation"),
                UI.getCurrencyField());

        cboDonationType = UI.getCombo(i18n("type"),
                LookupCache.getDonationTypeLookup(), "DonationName");
        UI.addComponent(top, i18n("type"), cboDonationType);

        txtComments = (UI.TextArea) UI.addComponent(mid, i18n("comments"),
                UI.getTextArea());

        btnOk = (UI.Button) but.add(UI.getButton(i18n("ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) but.add(UI.getButton(i18n("cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(top, UI.BorderLayout.NORTH);
        add(mid, UI.BorderLayout.CENTER);
        add(but, UI.BorderLayout.SOUTH);
    }

    public void loadData() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public boolean saveData() {
        // Save values
        try {
            od.setDateReceived(Utils.parseDate(txtDateReceived.getText()));
            od.setDateDue(Utils.parseDate(txtDateDue.getText()));
            od.setDonation(new Double(txtDonation.getText()));
            od.setDonationTypeID(Utils.getIDFromCombo(
                    LookupCache.getDonationTypeLookup(), "DonationName",
                    cboDonationType));
            od.setComments(txtComments.getText());
            od.save(Global.currentUserName);

            // Update parent
            parent.updateList();

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(i18n("an_error_occurred_saving_data") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }
}
