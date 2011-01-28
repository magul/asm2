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
package net.sourceforge.sheltermanager.asm.ui.users;

import net.sourceforge.sheltermanager.asm.bo.Users;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableItem;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableList;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.MD5;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;


/**
 * Contains all functionality for editing a user and their permissions.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class UserEdit extends ASMForm {
    private Users user = null;
    private UserView parent = null;
    private Vector<Object> ctl = null;
    private ArrayList<SelectableItem> items = new ArrayList<SelectableItem>();
    public boolean isNew = false;
    private UI.Button btnCancel;
    private UI.Button btnClone;
    private UI.Button btnOk;
    private UI.CheckBox chkIsSuper;
    private SelectableList tblOptions;
    private UI.Panel pnlTop;
    private UI.Label lblPassword;
    private UI.TextField txtPassword;
    private UI.TextField txtUserName;
    private UI.TextField txtRealName;
    private OwnerLink olOwnerRecord;

    /** Creates new form EditUser */
    public UserEdit(UserView theparent) {
        parent = theparent;
        ctl = new Vector<Object>();
        init(Global.i18n("uiusers", "Edit_User"),
            IconManager.getIcon(IconManager.SCREEN_EDITUSER), "uiusers");
    }

    public void dispose() {
        user.free();
        user = null;
        parent = null;
        items.clear();
        items = null;
        ctl.removeAllElements();
        ctl = null;
        super.dispose();
    }

    public Vector<Object> getTabOrder() {
        ctl.add(txtUserName);
        ctl.add(txtRealName);
        ctl.add(txtPassword);
        ctl.add(chkIsSuper);
        ctl.add(btnOk);
        ctl.add(btnCancel);
        ctl.add(btnClone);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtUserName;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
        // If we're using applet users, then we shouldn't be able
        // to edit usernames as there will be a matching username
        // somewhere else - divorcing the two will break things
        if (Global.appletUser != null) {
            txtUserName.setEnabled(false);
        }
    }

    public void loadData() {
    }

    /**
     * Opens the users for a new record. Really just creates one and then
     * delegates to the <code>openForEdit</code> method
     */
    public void openForNew() {
        try {
            user = new Users();
            user.openRecordset("ID = 0");
            user.addNew();
            user.setSuperUser(new Integer(0));
            user.setRealName("");
            user.setUserName("");
            user.setSecurityMap("");

            // Mark the screen as creating a new record
            isNew = true;
            openForEdit(user);
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Reads the user object and fills the onscreen controls.
     *
     * @param theuser
     *            The user object to use.
     */
    public void openForEdit(Users theuser) {
        user = theuser;

        // Load values for username, realname and is super fields
        try {
            txtUserName.setText(user.getUserName());
            txtRealName.setText(Utils.nullToEmptyString(user.getRealName()));
            chkIsSuper.setSelected(user.getSuperUser().equals(new Integer(1)));

            if (user.getOwnerID() != null) {
                olOwnerRecord.setID(user.getOwnerID().intValue());
            }

            // If it's an existing record, hide the password field - has to
            // be set externally once the user is created.
            if (!isNew) {
                pnlTop.remove(lblPassword);
                pnlTop.remove(txtPassword);
                ctl.remove(txtPassword);
            }

            // Create checkboxes and load them with appropriate value
            createLabel(i18n("Accounts:"));
            createBox(i18n("Add_Accounts"), "aac");
            createBox(i18n("View_Accounts"), "vac");
            createBox(i18n("Change_Accounts"), "cac");
            createBox(i18n("Change_Transactions"), "ctrx");
            createBox(i18n("Delete_Accounts"), "dac");
            createLabel(i18n("Animals:"));
            createBox(i18n("Add_Animals"), "aa");
            createBox(i18n("Change_Animals"), "ca");
            createBox(i18n("View_Animals"), "va");
            createBox(i18n("View_Animal_Vet"), "vavet");
            createBox(i18n("Delete_Animals"), "da");
            createBox(i18n("Clone_Animals"), "cloa");
            createBox(i18n("Generate_Animal_Forms"), "gaf");
            createBox(i18n("Modify_Animal_Name_Database"), "mand");
            createLabel(i18n("Vaccinations:"));
            createBox(i18n("Add_Animal_Vaccinations"), "aav");
            createBox(i18n("View_Animal_Vaccinations"), "vav");
            createBox(i18n("Change_Animal_Vaccinations"), "cav");
            createBox(i18n("Delete_Animal_Vaccinations"), "dav");
            createBox(i18n("Bulk_Complete_Animal_Vaccinations"), "bcav");
            createLabel(i18n("Medical:"));
            createBox(i18n("Add_Animal_Medical_Records"), "maam");
            createBox(i18n("Change_Animal_Medical_Records"), "mcam");
            createBox(i18n("Delete_Animal_Medical_Records"), "mdam");
            createBox(i18n("View_Animal_Medical_Records"), "mvam");
            createBox(i18n("Bulk_Complete_Animal_Medical_Records"), "bcam");
            createLabel(i18n("Media:"));
            createBox(i18n("Add_Animal_Media"), "aam");
            createBox(i18n("Change_Animal_Media"), "cam");
            createBox(i18n("View_Animal_Media"), "vam");
            createBox(i18n("Delete_Animal_Media"), "dam");
            createLabel(i18n("Diets:"));
            createBox(i18n("Add_Animal_Diets"), "daad");
            createBox(i18n("Change_Animal_Diets"), "dcad");
            createBox(i18n("Delete_Animal_Diets:"), "ddad");
            createBox(i18n("View_Animal_Diets"), "dvad");
            createLabel(i18n("Costs:"));
            createBox(i18n("Add_Animal_Cost"), "caad");
            createBox(i18n("Change_Animal_Cost"), "ccad");
            createBox(i18n("Delete_Animal_Cost"), "cdad");
            createBox(i18n("View_Animal_Cost"), "cvad");
            createLabel(i18n("Movements:"));
            createBox(i18n("Add_Animal_Movements"), "aamv");
            createBox(i18n("Change_Animal_Movements"), "camv");
            createBox(i18n("View_Animal_Movements"), "vamv");
            createBox(i18n("Delete_Animal_Movements"), "damv");
            createLabel(i18n("Owners:"));
            createBox(i18n("Add_Owners"), "ao");
            createBox(i18n("Change_Owners"), "co");
            createBox(i18n("View_Owners"), "vo");
            createBox(i18n("View_Staff_Owner_Records"), "vso");
            createBox(i18n("Delete_Owners"), "do");
            createBox(i18n("Merge_Owners"), "mo");
            createBox(i18n("View_Owner_Links"), "volk");
            createLabel(i18n("Log_Entries:"));
            createBox(i18n("Add_Log_Entry"), "ale");
            createBox(i18n("Change_Log_Entry"), "cle");
            createBox(i18n("Delete_Log_Entry"), "dle");
            createBox(i18n("View_Log_Entries"), "vle");
            createLabel(i18n("Owner_Vouchers"));
            createBox(i18n("Add_Owner_Vouchers"), "vaov");
            createBox(i18n("Change_Owner_Vouchers"), "vcov");
            createBox(i18n("Delete_Owner_Vouchers"), "vdov");
            createBox(i18n("View_Owner_Vouchers"), "vvov");
            createLabel(i18n("Donations"));
            createBox(i18n("Add_Owner_Donations"), "oaod");
            createBox(i18n("Change_Owner_Donations"), "ocod");
            createBox(i18n("Delete_Owner_Donations"), "odod");
            createBox(i18n("View_Owner_Donations"), "ovod");
            createLabel(i18n("System:"));
            createBox(i18n("Access_System_Menu"), "asm");
            createBox(i18n("Configure_System_Options"), "cso");
            createBox(i18n("Modify_Lookups"), "ml");
            createBox(i18n("Use_SQL_Interface"), "usi");
            createBox(i18n("Run_Database_Updates"), "rdbu");
            createBox(i18n("Run_Database_Diagnostics"), "rdbd");
            createBox(i18n("Add_System_Users"), "asu");
            createBox(i18n("Edit_System_Users"), "esu");
            createLabel(i18n("Diary:"));
            createBox(i18n("View_Diary_Notes"), "vdn");
            createBox(i18n("Edit_Diary_Tasks"), "edt");
            createBox(i18n("Add_Diary_Notes"), "adn");
            createBox(i18n("Edit_All_Diary_Notes"), "eadn");
            createBox(i18n("Edit_My_Diary_Notes"), "emdn");
            createBox(i18n("Edit_Completed_Diary_Notes"), "ecdn");
            createBox(i18n("Bulk_Complete_Diary_Notes"), "bcn");
            createBox(i18n("Delete_Diary_Notes"), "ddn");
            createBox(i18n("Print_Diary_Notes"), "pdn");
            createBox(i18n("Print_Vaccination_Diary"), "pvd");
            createLabel(i18n("Lost_and_Found_Animals:"));
            createBox(i18n("Add_Lost_Animals"), "ala");
            createBox(i18n("Change_Lost_Animals"), "cla");
            createBox(i18n("Delete_Lost_Animals"), "dla");
            createBox(i18n("Add_Found_Animals"), "afa");
            createBox(i18n("Change_Found_Animals"), "cfa");
            createBox(i18n("Delete_Found_Animals"), "dfa");
            createBox(i18n("Match_Lost_and_Found_Animals"), "mlaf");
            createLabel(i18n("Waiting_List:"));
            createBox(i18n("View_Waiting_List"), "vwl");
            createBox(i18n("Add_to_Waiting_List"), "awl");
            createBox(i18n("Change_Waiting_List_Entries"), "cwl");
            createBox(i18n("Delete_Waiting_List_Entries"), "dwl");
            createBox(i18n("Bulk_Complete_Waiting_List_Entries"), "bcwl");
            createLabel(i18n("Custom_Reports"));
            createBox(i18n("Add_Custom_Report"), "ccr");
            createBox(i18n("View_Custom_Reports"), "vcr");
            createBox(i18n("Change_Custom_Reports"), "hcr");
            createBox(i18n("Delete_Custom_Reports"), "dcr");
            createLabel(i18n("Litter_Logging:"));
            createBox(i18n("Add_Litter_Log"), "all");
            createBox(i18n("Change_Litter_Log"), "cll");
            createBox(i18n("View_Litter_Log"), "vll");
            createBox(i18n("Delete_Litter_Log"), "dll");
            createLabel(i18n("Internet_Publishing:"));
            createBox(i18n("Use_Internet_Publisher"), "uipb");
            createLabel(i18n("Mail_Merging:"));
            createBox(i18n("Mail_Merge_(Owner_Data)"), "mmeo");
            createBox(i18n("Mail_Merge_(Adoption_Data)"), "mmea");
            tblOptions = new SelectableList(items);
            add(tblOptions, UI.BorderLayout.CENTER);

            // Add the tab order set
            Global.focusManager.addComponentSet(ctl, this, txtUserName);
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Creates a label in the scroll pane area. This is handy for separating the
     * security elements into different categories.
     */
    private void createLabel(String labeltext) {
        items.add(new SelectableItem(labeltext, null, false, true));
    }

    /**
     * Creates a checkbox in the scroll pane area, reads the flag in the user
     * object and sets it if it should be.
     *
     * @param securename
     *            The name of the permission (eg: Add Animals)
     * @param secureflag
     *            The corresponding security flag.
     */
    private void createBox(String securename, String secureflag) {
        try {
            items.add(new SelectableItem(securename, secureflag,
                    user.getSecurityFlag(secureflag), false));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Saves all the information back to the user control and calls the save
     * method.
     */
    public boolean saveData() {
        try {
            // Username, password and super
            user.setUserName(txtUserName.getText());

            if (isNew) {
                user.setPassword(MD5.hash(txtPassword.getText()));
            }

            user.setRealName(txtRealName.getText());

            if (chkIsSuper.isSelected()) {
                user.setSuperUser(new Integer(1));
            } else {
                user.setSuperUser(new Integer(0));
            }

            user.setOwnerID(new Integer(olOwnerRecord.getID()));

            // Enumerate the check box controls and set the
            // appropriate flags.

            // Clear the map before we start
            user.setSecurityMap("");

            Iterator<SelectableItem> i = items.iterator();

            while (i.hasNext()) {
                SelectableItem it = i.next();

                if (it.isSelected() && !it.isHeader()) {
                    user.setSecurityFlag(it.getValue().toString());
                }
            }

            // Try and save it now, and update the parent list
            // finally, close this.
            try {
                user.save();
                parent.updateList();
                dispose();

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        pnlTop = UI.getPanel(UI.getGridLayout(4));

        UI.Panel p = pnlTop;

        txtUserName = (UI.TextField) UI.addComponent(p, i18n("Username:_"),
                UI.getTextField());

        txtRealName = (UI.TextField) UI.addComponent(p, i18n("Real_Name"),
                UI.getTextField());

        lblPassword = (UI.Label) p.add(UI.getLabel(i18n("Password:_")));
        txtPassword = (UI.TextField) p.add(UI.getTextField());

        olOwnerRecord = (OwnerLink) UI.addComponent(p, i18n("Owner_Record"),
                new OwnerLink(OwnerLink.MODE_ONELINE, OwnerLink.FILTER_NONE,
                    "LINK"));

        chkIsSuper = (UI.CheckBox) p.add(UI.getCheckBox(i18n("Super_User:_")));

        add(p, UI.BorderLayout.NORTH);

        UI.Panel pb = UI.getPanel(UI.getFlowLayout());
        btnOk = UI.getButton(i18n("Ok"), i18n("Save_this_record"), 'o', null,
                UI.fp(this, "saveData"));
        btnCancel = UI.getButton(i18n("Cancel"),
                i18n("Abandon_changes_to_this_record"), 'c', null,
                UI.fp(this, "dispose"));
        btnClone = UI.getButton(i18n("clone"),
                i18n("create_a_new_user_by_cloning_this_user"), 'l', null,
                UI.fp(this, "actionClone"));

        pb.add(btnOk);
        pb.add(btnCancel);
        pb.add(btnClone);

        add(pb, UI.BorderLayout.SOUTH);
    }

    public void actionClone() {
        try {
            Users nu = new Users();
            nu.openRecordset("ID = 0");
            nu.addNew();
            nu.setUserName("");
            nu.setPassword("");
            nu.setSuperUser(user.getSuperUser());
            nu.setSecurityMap(user.getSecurityMap());

            // Create a new form to display it
            UI.cursorToWait();

            UserEdit eu = new UserEdit(this.parent);
            eu.isNew = true;
            eu.openForEdit(nu);
            Global.mainForm.addChild(eu);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
