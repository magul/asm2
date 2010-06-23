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
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
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
public class AccountEdit extends ASMForm {
    private Account account = null;
    private AccountView parent = null;
    private UI.Button btnOk;
    private UI.Button btnCancel;
    private UI.ComboBox cboAccountType;
    private UI.TextField txtCode;
    private UI.TextArea txtDescription;
    private String audit = null;

    public AccountEdit() {
        init("", IconManager.getIcon(IconManager.SCREEN_EDITACCOUNT), "uiaccount");
    }

    public void dispose() {
        account.free();
        parent = null;
        account = null;
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
    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtCode);
        ctl.add(cboAccountType);
        ctl.add(txtDescription);
        ctl.add(btnOk);
        ctl.add(btnCancel);
        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtCode;
    }

    public void openForNew(AccountView parent) {
        this.parent = parent;
        openForNew();
    }

    /** Sets the screen into creation mode of a new account */
    public void openForNew() {

        try {
            account = new Account("ID = 0");
            account.addNew();
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
        }
        setTitle(i18n("Create_New_Account"));
    }

    /**
     * Sets the screen into editing mode of the passed diary object.
     *
     * @param thediary
     *            The Diary object to edit.
     */
    public void openForEdit(Account account, AccountView parent) {
        this.account = account;
        this.parent = parent;
        loadData();
    }

    /**
     * Loads the data from the object into the controls.
     */
    public void loadData() {
        try {
            // Auditing information
            audit = UI.messageAudit(
                    account.getCreatedDate(),
                    account.getCreatedBy(),
                    account.getLastChangedDate(),
                    account.getLastChangedBy());

            Utils.setComboFromID(LookupCache.getAccountTypeLookup(), "AccountType", account.getAccountType(), cboAccountType);
            txtCode.setText(account.getCode());
            txtDescription.setText(account.getDescription());

            setTitle(i18n("edit_account", account.getCode()));

        } catch (CursorEngineException e) {
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
            account.setCode(txtCode.getText());
            account.setDescription(txtDescription.getText());
            account.setAccountType(Utils.getIDFromCombo(LookupCache.getAccountTypeLookup(), "AccountType", cboAccountType));

            try {
                account.save(Global.currentUserName);

                if (parent != null) {
                    parent.updateList();
                }

                dispose();

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(e.getMessage());
            }
        } catch (CursorEngineException e) {
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

        txtCode = UI.getTextField();
        UI.addComponent(pnlTop, i18n("Code"), txtCode);

        cboAccountType = UI.getCombo(LookupCache.getAccountTypeLookup(), "AccountType");
        UI.addComponent(pnlTop, i18n("Type"), cboAccountType);

        txtDescription = (UI.TextArea) UI.addComponent(pnlMid, i18n("Description"),
                UI.getTextArea());

        btnOk = UI.getButton(UI.messageOK(), i18n("Save_this_account_and_exit"),
                'o', null, UI.fp(this, "saveData"));
        pnlBot.add(btnOk);

        btnCancel = UI.getButton(UI.messageCancel(), i18n("Close_without_saving"),
                'c', null, UI.fp(this, "dispose"));
        pnlBot.add(btnCancel);
    }
}
