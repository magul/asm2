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

import java.util.Date;

public class Account extends UserInfoBO {

    public static final int BANK = 1;
    public static final int CREDITCARD = 2;
    public static final int LOAN = 3;
    public static final int EXPENSE = 4;
    public static final int INCOME = 5;
    public static final int PENSION = 6;
    public static final int SHARES = 7;
    public static final int ASSET = 8;
    public static final int LIABILITY = 9;

   public Account() {
       tableName = "accounts";
   }

    public void addNew() throws CursorEngineException {
        super.addNew();
        setCode("");
        setDescription("");
        setAccountTypeID(new Integer(1));
        setDonationTypeID(new Integer(1));
   }

   public Account copy() throws CursorEngineException {
        Account a = new Account();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setCode(getCode());
        a.setDescription(getDescription());
        a.setAccountTypeID(getAccountTypeID());
        a.setDonationTypeID(getDonationTypeID());
        return a;
   }

   public Integer getID() throws CursorEngineException {
       return (Integer) rs.getField("ID");
   }

   public void setID(Integer newValue) throws CursorEngineException {
       rs.setField("ID", newValue);
   }

   public String getCode() throws CursorEngineException {
        return (String) rs.getField("Code");
   }    

   public void setCode(String newValue) throws CursorEngineException {
        rs.setField("Code", newValue);
   }

    public String getDescription() throws CursorEngineException {
        return (String) rs.getField("Description");
   }    

   public void setDescription(String newValue) throws CursorEngineException {
        rs.setField("Description", newValue);
   }

   public Integer getAccountTypeID() throws CursorEngineException {
       return (Integer) rs.getField("AccountTypeID");
   }

   public void setAccountTypeID(Integer newValue) throws CursorEngineException {
       rs.setField("AccountTypeID", newValue);
   }

   public Integer getDonationTypeID() throws CursorEngineException {
       return (Integer) rs.getField("DonationTypeID");
   }

   public void setDonationTypeID(Integer newValue) throws CursorEngineException {
       rs.setField("DonationTypeID", newValue);
   }

   /** Returns a list of all accounts, ordered by type and code */
   public Account getAllAccounts() throws CursorEngineException {
        Account a = new Account();
        a.openRecordset("ID > 0 ORDER BY AccountType, Code");
        return a;
   }

   /** Returns an account with a specific ID */
   public Account getAccountByID(Integer id) throws CursorEngineException {
        Account a = new Account();
        a.openRecordset("ID = " + id);
        return a;
   }

   /** Returns an account with a specific code */
   public Account getAccountByID(String code) throws CursorEngineException {
        Account a = new Account();
        a.openRecordset("Code = '" + code + "'");
        return a;
   }


   /** Calculates the balance for this account */
   public double getAccountBalance() throws Exception {
        // Withdrawals
        double withdrawal = 
            DBConnection.executeForDouble("SELECT SUM(Amount) FROM accountstrx WHERE SourceAccountID = " + getID());
        // Deposits
        double deposit = 
            DBConnection.executeForDouble("SELECT SUM(Amount) FROM accountstrx WHERE DestinationAccountID = " + getID());
        double rounded = Utils.round(deposit - withdrawal, 2);
        
        // Income and expense accounts should be positive
        if (getAccountTypeID().intValue() == INCOME || getAccountTypeID().intValue() == EXPENSE) {
            rounded = Math.abs(rounded);
        }

        return rounded;
   }

   /** Calculates the reconciled amount for this account */
   public double getReconciled() throws Exception {
        // Withdrawals
        double withdrawal = 
            DBConnection.executeForDouble("SELECT SUM(Amount) FROM accountstrx WHERE Reconciled = 1 AND SourceAccountID = " + getID());
        // Deposits
        double deposit = 
            DBConnection.executeForDouble("SELECT SUM(Amount) FROM accountstrx WHERE Reconciled = 1 AND DestinationAccountID = " + getID());
        double rounded = Utils.round(deposit - withdrawal, 2);
        
        // Income and expense accounts should be positive
        if (getAccountTypeID().intValue() == INCOME || getAccountTypeID().intValue() == EXPENSE) {
            rounded = Math.abs(rounded);
        }

        return rounded;
   }


   /** Calculates the balance for this account to a certain date */
   public static double getAccountBalanceToDate(Integer accountId, Date limit) throws Exception {
        // Withdrawals
        double withdrawal = 
            DBConnection.executeForDouble("SELECT SUM(Amount) FROM accountstrx WHERE SourceAccountID = " + accountId + " AND TrxDate < '" + Utils.getSQLDate(limit) + "'");
        // Deposits
        double deposit = 
            DBConnection.executeForDouble("SELECT SUM(Amount) FROM accountstrx WHERE DestinationAccountID = " + accountId + " AND TrxDate < '" + Utils.getSQLDate(limit) + "'");
        double rounded = Utils.round(deposit - withdrawal, 2);
        
        int accountType = DBConnection.executeForInt("SELECT AccountTypeID FROM accounts WHERE ID = " + accountId);

        // Income and expense accounts should always be positive
        if (accountType == INCOME || accountType == EXPENSE) {
            rounded = Math.abs(rounded);
        }

        return rounded;
   }


}
