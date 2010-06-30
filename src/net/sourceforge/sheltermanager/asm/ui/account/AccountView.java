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
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.AccountRenderer;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Screen for displaying and editing accounts
 * @author Robin Rawson-Tetley
 */
public class AccountView extends ASMView {
    private UI.Button btnRefresh;
    private UI.Button btnNew;
    private UI.Button btnEdit;
    private UI.Button btnDelete;
    private UI.Button btnReconcile;
    private UI.Button btnTrx;
    private boolean hasRecords = false;

    public AccountView() {
        init(Global.i18n("uiaccount", "Accounts"),
            IconManager.getIcon(IconManager.SCREEN_ACCOUNT), "uiaccount",
            false, true, new AccountRenderer(new int[] { 3, 4 }, 6, 0));
        updateList();
    }

    public void dispose() {
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    /** Deactivates on screen buttons according to what the user can do */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAccount()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteAccount()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAccount()) {
            btnEdit.setEnabled(false);
            btnReconcile.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeTransactions()) {
            btnTrx.setEnabled(false);
            disableDoubleClick = true;
        }
    }

    /** Fills the on screen list of diary notes */
    public void updateList() {
        try {
            // Create an array to hold the results for the table
            Account accounts = Account.getAllAccounts();
            String[][] datar = new String[accounts.size()][7];

            // Create an array of headers for the table
            String[] columnheaders = {
                    i18n("Code"), i18n("Type"), i18n("Description"),
                    i18n("Reconciled"), i18n("Balance")
                };

            int i = 0;

            for (Account a : accounts) {
                datar[i][0] = a.getCode();
                datar[i][1] = a.getAccountTypeName();
                datar[i][2] = a.getDescription();
                datar[i][3] = Utils.formatCurrency(a.getReconciled());
                datar[i][4] = Utils.formatCurrency(a.getAccountBalance());
                datar[i][5] = a.getID().toString();
                datar[i][6] = (a.getAccountBalance() < 0) ? "-" : "+";
                i++;
                hasRecords = true;
            }

            setTableData(columnheaders, datar, i, 5);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void setLink(int a, int b) {
    }

    public boolean hasData() {
        return hasRecords;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return false;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_account"), 'n',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNT_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnEdit = UI.getButton(null, i18n("Edit_the_selected_account"), 'e',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNT_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_the_selected_accounts"),
                'd', IconManager.getIcon(IconManager.SCREEN_ACCOUNT_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNT_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnReconcile = UI.getButton(null,
                i18n("Mark_everything_in_the_selected_accounts_reconciled_to_today"),
                'c', IconManager.getIcon(IconManager.SCREEN_ACCOUNT_RECONCILE),
                UI.fp(this, "actionReconcile"));
        addToolButton(btnReconcile, true);

        btnTrx = UI.getButton(null,
                i18n("Edit_the_transactions_for_this_account"), 't',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNT_TRX),
                UI.fp(this, "actionTrx"));
        addToolButton(btnTrx, true);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionTrx();
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
                String id = tablemodel.getIDAt(selrows[i]);

                try {
                    DBConnection.executeAction(
                        "DELETE FROM accountstrx WHERE SourceAccountID = " +
                        id + " OR DestinationAccountID = " + id);
                    DBConnection.executeAction(
                        "DELETE FROM accounts WHERE ID = " + id);
                    AuditTrail.deleted("accounts", tablemodel.getValueAt(selrows[i], 0).toString());
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

        // Get an account object for it
        Account a = new Account("ID = " + id);

        // Open the edit form and edit it
        AccountEdit ed = new AccountEdit();
        ed.openForEdit(a, this);
        Global.mainForm.addChild(ed);
    }

    public void actionNew() {
        // Create a new edit form
        AccountEdit ed = new AccountEdit();
        ed.openForNew(this);
        Global.mainForm.addChild(ed);
    }

    public void actionReconcile() {
        // Make sure at least one row is selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int i = 0; i < selrows.length; i++) {
            try {
                // Get the ID for the selected row
                String id = tablemodel.getIDAt(selrows[i]);
                Account a = Account.getAccountByID(new Integer(id));
                a.markReconciledToDate();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }

        updateList();
    }

    public void actionTrx() {
        // Make sure a row is selected
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get an account object for it
        Account a = new Account("ID = " + id);

        // Create/open the transaction screen
        TrxView tv = new TrxView(a);
        Global.mainForm.addChild(tv);
    }
}
