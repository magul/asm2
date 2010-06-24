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
package net.sourceforge.sheltermanager.asm.ui.lookups;

import net.sourceforge.sheltermanager.asm.bo.Account;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * This class contains all code for editing and creating a lookup
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class LookupEdit extends ASMForm {
    private String tableName = "";
    private String nameField = "";
    private String descField = "";
    private String lookupDisplay = "";
    private String nameDisplay = "";
    private String descDisplay = "";
    private LookupView parent = null;
    private SQLRecordset rs = null;
    private UI.Button btnCancel;
    private UI.TextArea txtDesc;
    private UI.TextField txtName;
    private UI.Button btnOk;

    /** Creates new form EditLookup */
    public LookupEdit(String tableName, String nameField, String descField,
        String nameDisplay, String descDisplay, String lookupDisplay,
        LookupView parent) {
        this.tableName = tableName;
        this.nameField = nameField;
        this.descField = descField;
        this.nameDisplay = nameDisplay;
        this.descDisplay = descDisplay;
        this.lookupDisplay = lookupDisplay;
        this.parent = parent;

        init("", IconManager.getIcon(IconManager.SCREEN_EDITLOOKUP), "uilookups");
    }

    public void dispose() {
        rs.free();
        tableName = null;
        nameField = null;
        descField = null;
        lookupDisplay = null;
        parent = null;
        rs = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtName);
        ctl.add(txtDesc);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtName;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    /** Opens the lookup editor for a new record */
    public void openForNew() {
        try {
            rs = new SQLRecordset();
            rs.openRecordset("SELECT * FROM " + tableName + " WHERE ID = 0",
                tableName);
            rs.addNew();
            rs.setField("ID", new Integer(DBConnection.getPrimaryKey(tableName)));

            this.setTitle(i18n("Create_New_") + lookupDisplay);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /** Opens the lookup editor on a specific record. */
    public void openForEdit(int theID) {
        try {
            rs = new SQLRecordset();
            rs.openRecordset("SELECT * FROM " + tableName + " WHERE ID = " +
                theID, tableName);
            this.setTitle(i18n("edit_x", lookupDisplay));

            txtName.setText((String) rs.getField(nameField));
            txtDesc.setText(Utils.nullToEmptyString(
                    (String) rs.getField(descField)));
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.Panel p = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pc = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());

        txtName = (UI.TextField) UI.addComponent(p, nameDisplay,
                UI.getTextField());
        txtDesc = (UI.TextArea) UI.addComponent(pc, descDisplay,
                UI.getTextArea());

        btnOk = UI.getButton(i18n("Ok"), null, 'o', null,
                UI.fp(this, "saveData"));
        btnCancel = UI.getButton(i18n("Cancel"), null, 'c', null,
                UI.fp(this, "actionCancel"));
        pb.add(btnOk);
        pb.add(btnCancel);

        add(p, UI.BorderLayout.NORTH);
        add(pc, UI.BorderLayout.CENTER);
        add(pb, UI.BorderLayout.SOUTH);
    }

    public void actionCancel() {
        dispose();
    }

    public boolean saveData() {
    	
        if (txtName.getText().equals("")) {
            Dialog.showError(i18n("cannot_be_empty", nameDisplay),
                i18n("Validation_Error"));

            return false;
        }

        // Save the recordset back to the database
        try {
            rs.setField(nameField, txtName.getText());
            rs.setField(descField, txtDesc.getText());

            rs.save(false, "");
            parent.updateList();
            
            catchSave();
            dispose();

            return true;
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }

        return false;
    }

    /**
     * This routine gets called after our lookup record has
     * been saved to the database. Hooks to do things based
     * on saving a record in a given lookup table can be added 
     * here.
     */
    public void catchSave() {
    	
    	try {
    	
	    	// If we're saving a donation type, make sure there's a
	    	// matching income account in the accounts package
	    	if (tableName.equals("donationtype") && Configuration.getBoolean("CreateDonationTrx")) {
	    		
	    		// Do we already have a matching account?
	    		Account a = new Account("DonationTypeID = " + rs.getInt("ID"));
	    		if (a.size() == 0) {
	    			// We don't, so let's create one
	    			a.add();
	    			a.setAccountType(Account.INCOME);
	    			a.setCode(Global.i18n("uiaccount", "income_prefix") + 
	                        Utils.replace(rs.getString("DonationName"), " ", ""));
	    			a.setDescription(rs.getString("DonationDescription"));
	    			a.setDonationTypeID(new Integer(rs.getInt("ID")));
	    			a.save(Global.currentUserName);
	    		}
	    		
	    	}
    	
    	}
    	catch (Exception e) {
    		Global.logException(e, getClass());
    		Dialog.showError(e.getMessage());
    	}
    }

}
