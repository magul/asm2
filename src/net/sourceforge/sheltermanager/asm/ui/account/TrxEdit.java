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
package net.sourceforge.sheltermanager.asm.ui.account;

import net.sourceforge.sheltermanager.asm.bo.Account;
import net.sourceforge.sheltermanager.asm.bo.AccountTrx;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
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
 * This class contains all code for editing accounts
 * @author Robin Rawson-Tetley
 */
public class TrxEdit extends ASMForm {
    private AccountTrx.Trx trx = null;
    private TrxView parent = null;
    private UI.Button btnOk;
    private UI.Button btnCancel;
    private DateField txtTrxDate;
    private UI.TextField txtDescription;
    private UI.ComboBox cboAccount;
    private CurrencyField txtDeposit;
    private CurrencyField txtWithdrawal;
    private String audit = null;

    public TrxEdit() {
        init("", IconManager.getIcon(IconManager.SCREEN_EDITTRX), "uiaccount");
    }

    public void dispose() {
        parent = null;
        super.dispose();
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    /**
     * Sets the tab ordering for the screen using the FlexibleFocusManager class
     */
    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(txtTrxDate);
        ctl.add(txtDescription);
        ctl.add(cboAccount);
        ctl.add(txtDeposit);
        ctl.add(txtWithdrawal);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtTrxDate.getTextField();
    }

    public void openForNew(TrxView parent, Integer accountId) {
        this.parent = parent;
        openForNew(accountId);
    }

    /** Sets the screen into creation mode of a new account */
    public void openForNew(Integer accountId) {
        trx = new AccountTrx.Trx();
        trx.accountId = accountId.intValue();
        setTitle(i18n("Create_Transaction"));
        txtTrxDate.setToToday();
    }

    /**
     * Sets the screen into editing mode of the passed trx object.
     *
     * @param trx The Trx object to edit.
     */
    public void openForEdit(AccountTrx.Trx trx, TrxView parent) {
        this.trx = trx;
        this.parent = parent;
        loadData();
    }

    /**
     * Loads the data from the object into the controls.
     */
    public void loadData() {
        try {
            // Auditing information
            audit = UI.messageAudit(trx.createdDate, trx.createdBy,
                    trx.lastChangedDate, trx.lastChangedBy);

            txtTrxDate.setDate(trx.date);
            txtDescription.setText(trx.description);
            txtWithdrawal.setValue(trx.withdrawal);
            txtDeposit.setValue(trx.deposit);
            Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
                trx.otherAccountId, cboAccount);

            setTitle(i18n("Edit_Transaction"));
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Saves the controls back to the business object and saves it
     * @return true if the save was successful.
     */
    public boolean saveData() {
        try {
            trx.otherAccountId = Utils.getIDFromCombo(LookupCache.getAccountsLookup(),
                    "Code", cboAccount).intValue();
            trx.description = txtDescription.getText();
            trx.withdrawal = txtWithdrawal.getValue();
            trx.deposit = txtDeposit.getValue();
            trx.date = txtTrxDate.getDate();

            AccountTrx.saveTransaction(trx);

            if (parent != null) {
                parent.updateList();
            }

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.Panel pnlTop = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pnlMid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pnlBot = UI.getPanel(UI.getFlowLayout());

        add(pnlTop, UI.BorderLayout.NORTH);
        add(pnlMid, UI.BorderLayout.CENTER);
        add(pnlBot, UI.BorderLayout.SOUTH);

        txtTrxDate = UI.getDateField();
        UI.addComponent(pnlTop, i18n("Date"), txtTrxDate);

        txtDescription = UI.getTextField();
        UI.addComponent(pnlTop, i18n("Description"), txtDescription);

        cboAccount = UI.getCombo(LookupCache.getAccountsLookup(), "Code");
        UI.addComponent(pnlTop, i18n("Account"), cboAccount);

        txtDeposit = UI.getCurrencyField();
        UI.addComponent(pnlTop, i18n("Deposit"), txtDeposit);

        txtWithdrawal = UI.getCurrencyField();
        UI.addComponent(pnlTop, i18n("Withdrawal"), txtWithdrawal);

        btnOk = UI.getButton(UI.messageOK(),
                i18n("Save_this_transaction_and_exit"), 'o', null,
                UI.fp(this, "saveData"));
        pnlBot.add(btnOk);

        btnCancel = UI.getButton(UI.messageCancel(),
                i18n("Close_without_saving"), 'c', null, UI.fp(this, "dispose"));
        pnlBot.add(btnCancel);
    }
}
