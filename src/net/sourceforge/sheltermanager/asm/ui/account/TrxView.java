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
package net.sourceforge.sheltermanager.asm.ui.account;

import net.sourceforge.sheltermanager.asm.bo.Account;
import net.sourceforge.sheltermanager.asm.bo.AccountTrx;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.AccountRenderer;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Screen for displaying transactions
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class TrxView extends ASMView {
    private final static int THIS_MONTH = 0;
    private final static int THIS_WEEK = 1;
    private final static int THIS_YEAR = 2;
    private final static int LAST_MONTH = 3;
    private final static int LAST_WEEK = 4;
    private UI.Button btnRefresh;
    private UI.Button btnNew;
    private UI.Button btnEdit;
    private UI.Button btnDelete;
    private UI.Button btnReconcile;
    private UI.ComboBox cboNumTrx;
    private DateField dtFrom;
    private DateField dtTo;
    private UI.ComboBox cboReconciled;
    private Account account = null;
    private boolean hasRecords = false;

    // Visible list of transactions
    private ArrayList<AccountTrx.Trx> trx = null;

    public TrxView(Account account) {
        try {
            this.account = account;
            init(Global.i18n("uiaccount", "transactions_for", account.getCode()),
                IconManager.getIcon(IconManager.SCREEN_ACCOUNTTRX),
                "uiaccount", true, true,
                new AccountRenderer(new int[] { 4, 5, 6 }, 8, 1));

            cboNumTrx = UI.getCombo(new String[] {
                        "100", "200", "500", i18n("All")
                    });

            dtFrom = UI.getDateField();
            dtTo = UI.getDateField();

            int accountView = THIS_MONTH;
            accountView = Configuration.getInteger("DefaultAccountViewPeriod");

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            switch (accountView) {
            case (LAST_MONTH):
                from.add(Calendar.MONTH, -1);

                break;

            case (THIS_MONTH):
                from.set(Calendar.DAY_OF_MONTH, 1);
                to.add(Calendar.MONTH, 1);
                to.set(Calendar.DAY_OF_MONTH, 1);
                to.add(Calendar.DAY_OF_YEAR, -1);

                break;

            case (THIS_WEEK):
                from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

                break;

            case (LAST_WEEK):
                from.add(Calendar.DAY_OF_YEAR, -7);

                break;

            case (THIS_YEAR):
                from.set(Calendar.DAY_OF_YEAR, 1);
                to.add(Calendar.YEAR, 1);
                to.set(Calendar.DAY_OF_YEAR, 1);
                to.add(Calendar.DAY_OF_YEAR, -1);

                break;
            }

            dtFrom.setDate(Utils.calendarToDate(from));
            dtTo.setDate(Utils.calendarToDate(to));

            cboReconciled = UI.getCombo(new String[] {
                        i18n("Both"), i18n("Reconciled"), i18n("Non_reconciled")
                    });

            UI.addComponent(getTopPanel(), i18n("show_most_recent"), dtFrom);
            UI.addComponent(getTopPanel(), i18n("to"), dtTo);
            UI.addComponent(getTopPanel(), i18n("Reconciled:"), cboReconciled);

            updateList();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void dispose() {
        super.dispose();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(cboNumTrx);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    /** Deactivates on screen buttons according to what the user can do */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecChangeTransactions()) {
            btnNew.setEnabled(false);
            btnDelete.setEnabled(false);
            btnEdit.setEnabled(false);
            btnReconcile.setEnabled(false);
            disableDoubleClick = true;
        }
    }

    /** Fills the on screen list of tranasctions */
    public void updateList() {
        new Thread() {
                public void run() {
                    updateListThreaded();
                }
            }.start();
    }

    public synchronized void updateListThreaded() {
        try {
            // Get number of trx to display
            // Obsoleted by date display instead
            /*
            int num = 100;

            if ((cboNumTrx != null) && (cboNumTrx.getSelectedItem() != null)) {
            String nums = cboNumTrx.getSelectedItem().toString();

            if (nums.equals(i18n("All"))) {
                num = 9999999;
            } else {
                num = Integer.parseInt(nums);
            }
            }
            */
            Date from = new Date();

            if (dtFrom.getDate() != null) {
                from = dtFrom.getDate();
            }

            Date to = new Date();

            if (dtTo.getDate() != null) {
                to = dtTo.getDate();
            }

            // Start the throbber going so the user knows we're doing
            // something
            startThrobber();

            // Grab the list
            trx = AccountTrx.getTransactions(account.getID(), from, to,
                    cboReconciled.getSelectedIndex());

            String[][] datar = new String[trx.size()][9];

            // Create an array of headers for the table
            String[] columnheaders = {
                    i18n("Date"), i18n("Reconciled"), i18n("Description"),
                    i18n("Account"), i18n("Deposit"), i18n("Withdrawal"),
                    i18n("Balance")
                };

            int i = 0;
            initStatusBarMax(trx.size());

            for (AccountTrx.Trx t : trx) {
                datar[i][0] = Utils.formatTableDate(t.date);
                datar[i][1] = ((t.reconciled == 1) ? i18n("Yes") : " ");
                datar[i][2] = t.description;
                datar[i][3] = t.otherAccountCode;
                datar[i][4] = ((t.deposit > 0)
                    ? Utils.formatCurrency(t.deposit) : "");
                datar[i][5] = ((t.withdrawal > 0)
                    ? Utils.formatCurrency(t.withdrawal) : "");
                datar[i][6] = Utils.formatCurrency(t.balance);
                datar[i][7] = new Integer(t.id).toString();
                datar[i][8] = (t.balance < 0) ? "-" : "+";

                i++;
                incrementStatusBar();
                hasRecords = true;
            }

            resetStatusBar();

            getTable().setSortModel(new TrxView.AccountSortable());
            setTableData(columnheaders, datar, i, 9, 7);
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

    public ArrayList<AccountTrx.Trx> getData() {
        return trx;
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
        btnNew = UI.getButton(null, i18n("Add_a_transaction"), 'n',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNTTRX_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnEdit = UI.getButton(null, i18n("Edit_the_selected_transaction"),
                'e', IconManager.getIcon(IconManager.SCREEN_ACCOUNTTRX_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null,
                i18n("Delete_the_selected_transactions"), 'd',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNTTRX_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNTTRX_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnReconcile = UI.getButton(null,
                i18n("Reconcile_the_selected_transactions"), 'c',
                IconManager.getIcon(IconManager.SCREEN_ACCOUNTTRX_RECONCILE),
                UI.fp(this, "actionReconcile"));
        addToolButton(btnReconcile, true);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
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
                String sql = "DELETE FROM accountstrx WHERE ID = " + id;

                try {
                    DBConnection.executeAction(sql);

                    AuditTrail.deleted("accountstrx",
                        tablemodel.getValueAt(selrows[i], 0).toString() + " " +
                        tablemodel.getValueAt(selrows[i], 2).toString());
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

        try {
            AccountTrx.Trx t = AccountTrx.getTransactionByID(id, account.getID());
            TrxEdit ed = new TrxEdit(account.getCode(), this);
            ed.openForEdit(t);
            Global.mainForm.addChild(ed);
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }
    }

    public void actionNew() {
        try {
            TrxEdit ed = new TrxEdit(account.getCode(), this);
            ed.openForNew(account.getID());
            Global.mainForm.addChild(ed);
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }
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
                AccountTrx.markReconciled(new Integer(id));
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }

        updateList();
    }

    /** Sorts monetary columns by text length, then alphanumerically */
    private class AccountSortable extends SortableTableModel {
        @Override
        public int sortByColumnCompare(int col, int idx, String[][] dat) {
            int compared;
            int ll = dat[idx][col].length();
            int rl = dat[idx + 1][col].length();

            if ((ll != rl) && ((col >= 4) || (col <= 6))) {
                if (ll > rl) {
                    compared = 1;
                } else {
                    compared = -1;
                }
            } else {
                compared = dat[idx][col].compareToIgnoreCase(dat[idx + 1][col]);
            }

            return compared;
        }
    }
}
