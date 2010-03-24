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

import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.OwnerVoucher;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.text.ParseException;

import java.util.Vector;


/**
 * This class contains all code for editing individual voucher records.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class VoucherEdit extends ASMForm {
    private OwnerVoucher voucher = null;

    /** A reference back to the parent form that spawned this form */
    private VoucherSelector ownervouchers = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboVoucher;
    private CurrencyField txtAmount;
    private UI.TextArea txtComments;
    private DateField txtExpiryDate;
    private DateField txtIssueDate;
    private String audit = null;
    private UI.Label lblID;

    /** Creates new form EditAnimalVaccination */
    public VoucherEdit(VoucherSelector ownervouchers) {
        this.ownervouchers = ownervouchers;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITOWNERVOUCHER),
            "uiowner");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboVoucher);
        ctl.add(txtIssueDate.getTextField());
        ctl.add(txtExpiryDate.getTextField());
        ctl.add(txtAmount.getTextField());
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboVoucher;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public void openForNew(int ownerid) {
        this.voucher = new OwnerVoucher();
        this.setTitle(i18n("New_Voucher"));
        voucher.openRecordset("ID = 0");

        try {
            voucher.addNew();
            voucher.setOwnerID(new Integer(ownerid));
            lblID.setText(voucher.getNumber());
        } catch (CursorEngineException e) {
            Dialog.showError(Global.i18n("uianimal",
                    "unable_to_create_new_record:") + e.getMessage(),
                Global.i18n("uianimal", "Failed_Create"));
            Global.logException(e, getClass());
        }

        // Date required
        txtIssueDate.setToToday();
    }

    public void openForEdit(int voucherid) {
        this.voucher = new OwnerVoucher();
        voucher.openRecordset("ID = " + voucherid);

        this.setTitle(i18n("Edit_Voucher"));
        loadData();
    }

    public void loadData() {
        try {
            try {
                this.txtIssueDate.setText(Utils.formatDate(
                        voucher.getDateIssued()));
                this.txtExpiryDate.setText(Utils.formatDate(
                        voucher.getDateExpired()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            Utils.setComboFromID(LookupCache.getVoucherLookup(), "VoucherName",
                voucher.getVoucherID(), cboVoucher);
            this.txtAmount.setText(voucher.getValue().toString());
            this.txtComments.setText(Utils.nullToEmptyString(
                    voucher.getComments()));
            this.lblID.setText(voucher.getNumber());
            audit = UI.messageAudit(voucher.getCreatedDate(),
                    voucher.getCreatedBy(), voucher.getLastChangedDate(),
                    voucher.getLastChangedBy());
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        try {
            voucher.setComments(txtComments.getText());

            try {
                voucher.setDateIssued(txtIssueDate.getDate());
                voucher.setDateExpired(txtExpiryDate.getDate());
                voucher.setValue(new Double(txtAmount.getValue()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }

            voucher.setVoucherID(Utils.getIDFromCombo(
                    LookupCache.getVoucherLookup(), "VoucherName", cboVoucher));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            voucher.save(Global.currentUserName);

            // Update the edit owner form if successful
            ownervouchers.updateList();

            // Close this form
            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
        }

        return false;
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel buttons = UI.getPanel(UI.getFlowLayout());

        lblID = UI.getLabel(UI.ALIGN_LEFT, "");
        lblID.setForeground(UI.getColor(255, 0, 0));
        UI.addComponent(top, i18n("Number:"), lblID);

        cboVoucher = UI.getCombo(i18n("Voucher_Type:"),
                LookupCache.getVoucherLookup(), "VoucherName");
        cboVoucher.setToolTipText(i18n("The_type_of_voucher"));
        UI.addComponent(top, i18n("Voucher_Type:"), cboVoucher);

        txtIssueDate = (DateField) UI.addComponent(top,
                Global.i18n("uianimal", "Issue_Date:"),
                UI.getDateField(i18n("The_date_the_voucher_was_issued")));

        txtExpiryDate = (DateField) UI.addComponent(top,
                Global.i18n("uianimal", "Expiry_Date:"),
                UI.getDateField(i18n("The_date_the_voucher_expires")));

        txtAmount = (CurrencyField) UI.addComponent(top,
                Global.i18n("uianimal", "Voucher_Amount:"),
                UI.getCurrencyField(i18n("The_amount_the_voucher_is_for")));

        txtComments = (UI.TextArea) UI.addComponent(mid,
                Global.i18n("uianimal", "Comments:"),
                UI.getTextArea(i18n("Any_comments_about_the_voucher")));

        btnOk = (UI.Button) buttons.add(UI.getButton(Global.i18n("uianimal",
                        "Ok"), null, 'o', null, UI.fp(this, "saveData")));

        btnCancel = (UI.Button) buttons.add(UI.getButton(Global.i18n(
                        "uianimal", "Cancel"), null, 'c', null,
                    UI.fp(this, "dispose")));

        add(top, UI.BorderLayout.NORTH);
        add(mid, UI.BorderLayout.CENTER);
        add(buttons, UI.BorderLayout.SOUTH);
    }
}
