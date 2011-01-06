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
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.MD5;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Allows viewing of the system users, with facilities to manipulate them.
 */
@SuppressWarnings("serial")
public class UserView extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnEdit;
    private UI.Button btnNew;
    private UI.Button btnPassword;

    /** Creates new form ViewUsers */
    public UserView() {
        init(Global.i18n("uiusers", "System_Users"),
            IconManager.getIcon(IconManager.SCREEN_VIEWUSERS), "uiusers");
        updateList();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(getTable());
        ctl.add(btnNew);
        ctl.add(btnEdit);
        ctl.add(btnDelete);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public void updateList() {
        try {
            Users user = new Users();
            user.openRecordset("ID > 0 ORDER BY UserName");

            String[][] data = new String[(int) user.getRecordCount()][3];
            String[] columnheaders = new String[] {
                    i18n("Username:_"), i18n("Real_Name")
                };

            int i = 0;

            while (!user.getEOF()) {
                data[i][0] = user.getUserName();
                data[i][1] = Utils.nullToEmptyString(user.getRealName());
                data[i][2] = user.getID().toString();
                user.moveNext();
                i++;
            }

            user = null;

            setTableData(columnheaders, data, i, 2);
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddSystemUsers()) {
            btnNew.setEnabled(false);
        }

        // If we're using applet users, then it makes no sense to
        // be able to create and delete users as it's handled
        // externally
        if (Global.appletUser != null) {
            btnDelete.setEnabled(false);
            btnNew.setEnabled(false);
            btnPassword.setEnabled(false);
        }

        // EditSystemUsers is covered on the Main form - you cannot get into
        // this screen without that access.
    }

    public void tableClicked() {
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_user"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWUSERS_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnEdit = UI.getButton(null, i18n("Edit_the_highlighted_user"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWUSERS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_the_highlighted_user"),
                'd', IconManager.getIcon(IconManager.SCREEN_VIEWUSERS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnPassword = UI.getButton(null, i18n("Reset_Password"), 'p',
                IconManager.getIcon(IconManager.SCREEN_VIEWUSERS_PASSWORD),
                UI.fp(this, "actionPassword"));
        addToolButton(btnPassword, true);
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionDelete() {
        // Delete the user represented by the selected list item
        try {
            if (!Dialog.showYesNo(UI.messageDeleteConfirm(),
                        UI.messageReallyDelete())) {
                return;
            }

            String sql = "DELETE FROM users WHERE UserName LIKE '" +
                getSelectedUser() + "'";
            DBConnection.executeAction(sql);

            updateList();
        } catch (Exception e) {
            Dialog.showError(UI.messageDeleteError() + e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionEdit() {
        try {
            Users u = new Users();
            u.openRecordset("UserName Like '" + getSelectedUser() + "'");

            if (!u.getEOF()) {
                // Open the edit user form with the user details
                UserEdit eu = new UserEdit(this);
                eu.openForEdit(u);
                Global.mainForm.addChild(eu);
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionNew() {
        UserEdit eu = new UserEdit(this);
        eu.openForNew();
        Global.mainForm.addChild(eu);
    }

    private String getSelectedUser() {
        SortableTableModel model = (SortableTableModel) getTable().getModel();
        String username = (String) model.getValueAt(getTable().getSelectedRow(),
                0);

        return username;
    }

    public void actionPassword() {
        try {
            Users u = new Users();
            u.openRecordset("UserName Like '" + getSelectedUser() + "'");

            if (!u.getEOF()) {
                // Prompt for new password
                String newpass = Dialog.getInput(i18n("Enter_new_password"),
                        i18n("Enter_new_password"));

                if ((newpass != null) && !newpass.equals("")) {
                    u.setPassword(MD5.hash(newpass));
                    u.save();
                    Dialog.showInformation(i18n("Password_updated"),
                        i18n("Password_updated"));
                }
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
