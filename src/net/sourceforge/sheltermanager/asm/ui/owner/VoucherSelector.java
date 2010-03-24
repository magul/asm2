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
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Panel class for embedding voucher facilities in a frame.
 *
 * @author Robin Rawson-Tetley
 */
public class VoucherSelector extends ASMSelector {
    public int ownerID = 0;
    public String[][] tabledata;
    private boolean hasVoucher = false;
    private UI.Button btnAdd;
    private UI.Button btnDelete;
    private UI.Button btnEdit;

    public VoucherSelector() {
        init("uiowner", false);
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddOwnerVoucher()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeOwnerVoucher()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteOwnerVoucher()) {
            btnDelete.setEnabled(false);
        }
    }

    public void setLink(int ownerID, int linkType) {
        this.ownerID = ownerID;
    }

    public void dispose() {
        tabledata = null;
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    public void updateList() {
        OwnerVoucher ov = new OwnerVoucher();
        ov.openRecordset("OwnerID = " + ownerID);

        tabledata = new String[(int) ov.getRecordCount()][6];
        String[] columnheaders = {
                i18n("Issue_Date"), i18n("Expiry_Date"), i18n("Number"), 
                i18n("Voucher_Type"), i18n("comments")
            };

        int i = 0;
        try {
            while (!ov.getEOF()) {
                tabledata[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            ov.getDateIssued()));
                tabledata[i][1] = Utils.nullToEmptyString(Utils.formatTableDate(
                            ov.getDateExpired()));
                tabledata[i][2] = ov.getNumber();
                tabledata[i][3] = LookupCache.getVoucherName(ov.getVoucherID());
                tabledata[i][4] = Utils.nullToEmptyString(ov.getComments());
                tabledata[i][5] = ov.getID().toString();
                hasVoucher = true;
                i++;
                ov.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 5);
    }

    public boolean hasData() {
        return hasVoucher;
    }

    public void addToolButtons() {
        btnAdd = UI.getButton(null, i18n("Create_new_voucher"), 'n',
                IconManager.getIcon(IconManager.SCREEN_OWNERVOUCHERS_NEW),
                UI.fp(this, "actionAdd"));
        addToolButton(btnAdd, false);

        btnEdit = UI.getButton(null, i18n("Edit_this_voucher"), 'e',
                IconManager.getIcon(IconManager.SCREEN_OWNERVOUCHERS_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_voucher"), 'd',
                IconManager.getIcon(IconManager.SCREEN_OWNERVOUCHERS_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            // Remove it from the database
            try {
                String s = "Delete From ownervoucher Where ID = " + id;
                DBConnection.executeAction(s);
                // update the list
                this.updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        try {
            VoucherEdit ev = new VoucherEdit(this);
            ev.openForEdit(id);
            Global.mainForm.addChild(ev);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionAdd() {
        VoucherEdit ev = new VoucherEdit(this);
        ev.openForNew(ownerID);
        Global.mainForm.addChild(ev);
    }
}
