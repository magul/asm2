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

import java.util.Vector;

import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;


/**
 * This class contains the interface for displaying similar owners
 * when a name/address clash is found
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class OwnerCheck extends ASMForm {
    private OwnerEdit parent = null;
    private Owner ownerlist = null;
    private UI.Button btnIgnore;
    private UI.Button btnAbandon;
    private UI.Table tbl;

    public OwnerCheck(Owner theownerlist, OwnerEdit theparent) {
        ownerlist = theownerlist;
        parent = theparent;
        init(Global.i18n("uiowner", "Possible_Matching_Owners"),
            IconManager.getIcon(IconManager.SCREEN_CHECKOWNER), "uiowner");
        updateList();
    }

    public void dispose() {
        unregisterTabOrder();
        parent = null;
        ownerlist = null;
        super.dispose();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(tbl);
        ctl.add(btnIgnore);
        ctl.add(btnAbandon);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return tbl;
    }

    public void updateList() {
        // Create an array to hold the results for the table
        String[][] datar = new String[(int) ownerlist.getRecordCount()][4];

        // Create an array of headers for the table
        String[] columnheaders = { i18n("Name"), i18n("Address"), i18n("Banned") };

        // Build the data
        int i = 0;

        try {
            while (!ownerlist.getEOF()) {
                datar[i][0] = ownerlist.getOwnerName();
                datar[i][1] = Utils.formatAddress(ownerlist.getOwnerAddress());
                datar[i][2] = (ownerlist.getIsBanned().intValue() == 1) ? "Yes"
                                                                        : "No";
                datar[i][3] = ownerlist.getID().toString();

                i++;
                ownerlist.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create our new table model and shove it into the table
        tbl.setTableData(columnheaders, datar, i, 3);
    }

    public void initComponents() {
        UI.Label lbl = UI.getLabel(i18n("following_owners_similar"));
        lbl.setPreferredSize(UI.getDimension(487, UI.getTextBoxHeight() * 4));
        add(lbl, UI.BorderLayout.NORTH);

        tbl = UI.getTable(UI.fp(this, "tableClicked"),
                UI.fp(this, "tableDoubleClicked"));
        add(tbl, UI.BorderLayout.CENTER);

        UI.Panel but = UI.getPanel(UI.getFlowLayout());
        btnIgnore = (UI.Button) but.add(UI.getButton(i18n("Ignore"), null, 'i',
                    null, UI.fp(this, "dispose")));
        btnAbandon = (UI.Button) but.add(UI.getButton(i18n("Abandon"), null,
                    'a', null, UI.fp(this, "actionAbandon")));
        add(but, UI.BorderLayout.SOUTH);
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        // Make sure a row is selected
        int id = tbl.getSelectedID();

        if (id == -1) {
            return;
        }

        Owner owner = new Owner();
        owner.openRecordset("ID = " + id);

        // Edit it
        OwnerEdit eo = new OwnerEdit();
        eo.openForEdit(owner);
        Global.mainForm.addChild(eo);
    }

    public void actionAbandon() {
        parent.dispose();
        dispose();
    }
}
