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
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AccountTrx extends UserInfoBO<AccountTrx> {
    public AccountTrx() {
        tableName = "accountstrx";
    }

    public AccountTrx(String where) {
        this();
        openRecordset(where);
    }

    public void addNew() throws CursorEngineException {
        super.addNew();
        setTrxDate(new Date());
        setDescription("");
        setReconciled(new Integer(0));
        setAmount(new Double(0));
        setSourceAccountID(new Integer(0));
        setDestinationAccountID(new Integer(0));
        setOwnerDonationID(new Integer(0));
    }

    public AccountTrx copy() throws CursorEngineException {
        AccountTrx t = new AccountTrx();
        t.openRecordset("ID = 0");
        t.addNew();
        t.setTrxDate(getTrxDate());
        t.setDescription(getDescription());
        t.setReconciled(getReconciled());
        t.setAmount(getAmount());
        t.setSourceAccountID(getSourceAccountID());
        t.setDestinationAccountID(getDestinationAccountID());
        t.setOwnerDonationID(getOwnerDonationID());

        return t;
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Date getTrxDate() throws CursorEngineException {
        return (Date) rs.getField("TrxDate");
    }

    public void setTrxDate(Date newValue) throws CursorEngineException {
        rs.setField("TrxDate", newValue);
    }

    public String getDescription() throws CursorEngineException {
        return (String) rs.getField("Description");
    }

    public void setDescription(String newValue) throws CursorEngineException {
        rs.setField("Description", newValue);
    }

    public Integer getReconciled() throws CursorEngineException {
        return (Integer) rs.getField("Reconciled");
    }

    public void setReconciled(Integer newValue) throws CursorEngineException {
        rs.setField("Reconciled", newValue);
    }

    public Double getAmount() throws CursorEngineException {
        return (Double) rs.getField("Amount");
    }

    public void setAmount(Double newValue) throws CursorEngineException {
        rs.setField("Amount", newValue);
    }

    public Integer getSourceAccountID() throws CursorEngineException {
        return (Integer) rs.getField("SourceAccountID");
    }

    public void setSourceAccountID(Integer newValue)
        throws CursorEngineException {
        rs.setField("SourceAccountID", newValue);
    }

    public Integer getDestinationAccountID() throws CursorEngineException {
        return (Integer) rs.getField("DestinationAccountID");
    }

    public void setDestinationAccountID(Integer newValue)
        throws CursorEngineException {
        rs.setField("DestinationAccountID", newValue);
    }

    public Integer getOwnerDonationID() throws CursorEngineException {
        return (Integer) rs.getField("OwnerDonationID");
    }

    public void setOwnerDonationID(Integer newValue)
        throws CursorEngineException {
        rs.setField("OwnerDonationID", newValue);
    }

    public String getCreatedBy() throws CursorEngineException {
        return (String) rs.getField("CreatedBy");
    }

    public void setCreatedBy(String newValue) throws CursorEngineException {
        rs.setField("CreatedBy", newValue);
    }

    public Date getCreatedDate() throws CursorEngineException {
        return (Date) rs.getField("CreatedDate");
    }

    public void setCreatedDate(Date newValue) throws CursorEngineException {
        rs.setField("CreatedDate", newValue);
    }

    public String getLastChangedBy() throws CursorEngineException {
        return (String) rs.getField("LastChangedBy");
    }

    public void setLastChangedBy(String newValue) throws CursorEngineException {
        rs.setField("LastChangedBy", newValue);
    }

    public Date getLastChangedDate() throws CursorEngineException {
        return (Date) rs.getField("LastChangedDate");
    }

    public void setLastChangedDate(Date newValue) throws CursorEngineException {
        rs.setField("LastChangedDate", newValue);
    }

    /**
     * Return a single transaction as an AccountTrx.Trx object
     * @param transactionId
     * @param accountId
     * @return A Trx object
     */
    public static Trx getTransactionByID(Integer transactionId,
        Integer accountId) throws Exception {
        AccountTrx t = new AccountTrx();
        t.openRecordset("ID = " + transactionId);

        return new Trx(t, accountId, 0);
    }

    public static void markReconciled(Integer transactionId)
        throws Exception {
        DBConnection.executeAction(
            "UPDATE accountstrx SET Reconciled = 1 WHERE ID = " +
            transactionId);
    }

    public static void saveTransaction(Trx x) throws Exception {
        AccountTrx t = new AccountTrx();

        if (x.id == 0) {
            t.openRecordset("ID = 0");
            t.addNew();
        } else {
            t.openRecordset("ID = " + x.id);
        }

        t.setTrxDate(x.date);
        t.setDescription(x.description);
        t.setReconciled(new Integer(x.reconciled));
        t.setOwnerDonationID(new Integer(x.ownerDonationId));

        // Sort out direction
        if (x.deposit > 0) {
            t.setSourceAccountID(new Integer(x.otherAccountId));
            t.setDestinationAccountID(new Integer(x.accountId));
            t.setAmount(new Double(x.deposit));
        } else {
            t.setSourceAccountID(new Integer(x.accountId));
            t.setDestinationAccountID(new Integer(x.otherAccountId));
            t.setAmount(new Double(x.withdrawal));
        }

        // If there's a matching owner donation, update the amount 
        if (x.ownerDonationId > 0) {
            DBConnection.executeAction("UPDATE ownerdonation SET Donation = " +
                t.getAmount().doubleValue() + " WHERE ID = " +
                x.ownerDonationId);
        }

        // Save
        t.save(Global.currentUserName);
    }

    /**
     * Get a list of AccountTrx.Trx objects representing transactions for
     * the account given, between two dates
     * @param accountId The account ID
     * @param from The start date
     * @param to The to date
     */
    public static ArrayList<Trx> getTransactions(Integer accountId, Date from, Date to)
        throws Exception {
        ArrayList<Trx> v = new ArrayList<Trx>();

        // Get the rows
        AccountTrx t = new AccountTrx();
        t.openRecordset("TrxDate >= '" + Utils.getSQLDate(from) +
            "' AND TrxDate <= '" + Utils.getSQLDate(to) +
            "' AND (SourceAccountID = " + accountId +
            " OR DestinationAccountID = " + accountId + ") ORDER BY TrxDate");
        Global.logDebug("Identified " + t.size() + " transactions for account",
            "AccountTrx.getTransactions");

        // Get our starting balance
        double balance = Account.getAccountBalanceToDate(accountId, from);

        // Generate our list of transactions
        while (!t.getEOF()) {
            Trx x = new Trx(t, accountId, balance);
            v.add(x);
            balance = x.balance;
            t.moveNext();
        }

        return v;
    }
    
    /**
     * Get a list of AccountTrx.Trx objects representing transactions for
     * the account given, going back num number of transactions
     * @param accountId The account ID
     * @param num The number to get, or 0 for all
     */
    public static ArrayList<Trx> getTransactions(Integer accountId, int num)
        throws Exception {
        ArrayList<Trx> v = new ArrayList<Trx>();
        Date cutoff = new Date();

        // If the num is 0, we want to view all transactions, so set
        // a cutoff date far in the past
        if (num <= 0) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, 1990);
            cutoff = c.getTime();
        } else {
            cutoff = getCutOffDateForRows(accountId, num);
            Global.logDebug("Got cutoff date, " + num + " rows = " +
                Utils.formatDate(cutoff), "AccountTrx.getTransactions");
        }

        // Nothing to return if no cutoff
        if (cutoff == null) {
            Global.logDebug("No trx in account, bailing",
                "AccountTrx.getTransactions");

            return v;
        }

        // Get the rows
        AccountTrx t = new AccountTrx();
        t.openRecordset("TrxDate >= '" + Utils.getSQLDate(cutoff) +
            "' AND (SourceAccountID = " + accountId +
            " OR DestinationAccountID = " + accountId + ") ORDER BY TrxDate");
        Global.logDebug("Identified " + t.size() + " transactions for account",
            "AccountTrx.getTransactions");

        // Get our starting balance
        double balance = Account.getAccountBalanceToDate(accountId, cutoff);

        // Generate our list of transactions
        while (!t.getEOF()) {
            Trx x = new Trx(t, accountId, balance);
            v.add(x);
            balance = x.balance;
            t.moveNext();
        }

        return v;
    }

    /**
     * Goes back num rows for the account id and works out the cut off
     * date to use as numrowsago.date less one whole day. Returns null if there
     * are no transactions for the account given. It's this that allows us to
     * quickly calculate a starting balance by totalling upto cutoff
     */
    public static Date getCutOffDateForRows(Integer accountId, int num)
        throws Exception {
        SQLRecordset r = new SQLRecordset(
                "SELECT TrxDate FROM accountstrx WHERE (SourceAccountID=" +
                accountId + " OR DestinationAccountID=" + accountId +
                ") ORDER BY TrxDate DESC LIMIT " + num);

        // We have no cutoff dates, bail
        if (r.getEOF()) {
            return null;
        }

        // Grab the last date from the list
        r.moveLast();

        Date d = r.getDate("TrxDate");
        Global.logDebug("Cutoff date for " + num + " rows in account " +
            accountId + " = " + Utils.formatDate(d),
            "AccountTrx.getCutOffDateForRows");

        // Now, subtract a day
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_YEAR, -1);

        return c.getTime();
    }

    /** UI trx class, treats the transaction as this and other, and sets the
      * sign on the amount accordingly */
    public static class Trx {
        public int id = 0;
        public int accountId = 0;
        public int otherAccountId = 0;
        public int ownerDonationId = 0;
        public String otherAccountCode = "";
        public Date date = new Date();
        public String description = "";
        public int reconciled = 0;
        public double deposit = 0;
        public double withdrawal = 0;
        public double amount = 0;
        public double balance = 0;
        public String createdBy = "";
        public Date createdDate = null;
        public String lastChangedBy = "";
        public Date lastChangedDate = null;

        public Trx() {
        }

        public Trx(AccountTrx t, Integer thisAccount, double startbalance)
            throws Exception {
            id = t.getID().intValue();
            accountId = thisAccount.intValue();
            date = t.getTrxDate();
            description = t.getDescription();
            reconciled = t.getReconciled().intValue();
            ownerDonationId = t.getOwnerDonationID().intValue();
            amount = t.getAmount().doubleValue();
            balance = startbalance;
            createdBy = t.getCreatedBy();
            createdDate = t.getCreatedDate();
            lastChangedBy = t.getLastChangedBy();
            lastChangedDate = t.getLastChangedDate();

            // If this account is the source, then this is a withdrawal
            if (t.getSourceAccountID().equals(thisAccount)) {
                withdrawal = t.getAmount().doubleValue();
                deposit = 0;
                otherAccountId = t.getDestinationAccountID().intValue();
                otherAccountCode = DBConnection.executeForString(
                        "SELECT Code FROM accounts WHERE ID = " +
                        otherAccountId);
                balance -= withdrawal;
            }
            // it's a deposit
            else {
                deposit = t.getAmount().doubleValue();
                withdrawal = 0;
                otherAccountId = t.getSourceAccountID().intValue();
                otherAccountCode = DBConnection.executeForString(
                        "SELECT Code FROM accounts WHERE ID = " +
                        otherAccountId);
                balance += deposit;
            }
        }
    }
}
