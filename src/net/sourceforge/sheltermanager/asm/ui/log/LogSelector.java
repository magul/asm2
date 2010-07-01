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
package net.sourceforge.sheltermanager.asm.ui.log;

import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Panel class for embedding log facilities in a frame.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class LogSelector extends ASMSelector {
    /** The ID for the link */
    public int linkID = 0;

    /** The link type */
    public int linkTypeID = 0;

    /** The array of data to fill the table */
    public String[][] tabledata;

    /**
     * A flag to say whether there is anything interesting in this control
     */
    private boolean hasLog = false;
    private UI.Button btnAdd;
    private UI.Button btnDelete;
    private UI.Button btnEdit;
    private UI.ComboBox cboLogType;

    /** Creates new form BeanForm */
    public LogSelector() {
        init("uilog", true);
    }

    /**
     * Reads current user's security settings and deactivates things they can't
     * do.
     */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddLogEntry()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeLogEntry()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteLogEntry()) {
            btnDelete.setEnabled(false);
        }
    }

    /**
     * Sets the appropriate link
     */
    public void setLink(int linkID, int linkTypeID) {
        this.linkID = linkID;
        this.linkTypeID = linkTypeID;
    }

    public void dispose() {
        tabledata = null;
        super.dispose();
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboLogType);
        ctl.add(getTable());

        return ctl;
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    /**
     * Fills the table with the media entries for the passed link.
     */
    public void updateList() {
        Log log = new Log();

        String logTypeFilter = (String) cboLogType.getSelectedItem();

        if (logTypeFilter.equals(i18n("(all)"))) {
            log.openRecordset("LinkID = " + linkID + " AND LinkType = " +
                linkTypeID);
        } else {
            int logTypeID = Utils.getIDFromCombo(LookupCache.getLogTypeLookup(),
                    "LogTypeName", cboLogType).intValue();
            log.openRecordset("LinkID = " + linkID + " AND LinkType = " +
                linkTypeID + " AND LogTypeID = " + logTypeID);
        }

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        tabledata = new String[(int) log.getRecordCount()][4];

        // Create an array of headers for the accounts (one less than
        // array because 4th col will hold ID
        String[] columnheaders = {
                Global.i18n("uilog", "Date"), Global.i18n("uilog", "Type"),
                Global.i18n("uilog", "Details")
            };

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!log.getEOF()) {
                tabledata[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            log.getDate()));
                tabledata[i][1] = log.getLogTypeName() + " - " +
                    log.getLogTypeDescription();
                tabledata[i][2] = log.getComments();
                tabledata[i][3] = log.getID().toString();
                hasLog = true;
                i++;
                log.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 3);
    }

    /** Returns true if there is some content in the list */
    public boolean hasData() {
        return hasLog;
    }

    public void addToolButtons() {
        btnAdd = UI.getButton(null, i18n("Create_new_log_entry"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWLOG_NEW),
                UI.fp(this, "actionAdd"));
        addToolButton(btnAdd, false);

        btnEdit = UI.getButton(null, i18n("Edit_this_log_entry"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWLOG_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_log_entry"), 'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWLOG_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        cboLogType = UI.getCombo(i18n("Show:_"),
                LookupCache.getLogTypeLookup(), "LogTypeName",
                UI.fp(this, "updateList"), i18n("(all)"));
        UI.addComponent(getTopPanel(), i18n("Show:_"), cboLogType);

        Utils.setComboFromID(LookupCache.getLogTypeLookup(), "LogTypeName",
            new Integer(Configuration.getInteger("AFDefaultLogFilter")),
            cboLogType);
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
                String s = "Delete From log Where ID = " + id;
                DBConnection.executeAction(s);

                if (AuditTrail.enabled()) {
                    AuditTrail.deleted("log",
                        getTable().getValueAt(getTable().getSelectedRow(), 0)
                            .toString() + " " +
                        getTable().getValueAt(getTable().getSelectedRow(), 1)
                            .toString() + " " +
                        Utils.firstChars(getTable()
                                             .getValueAt(getTable()
                                                             .getSelectedRow(),
                                2).toString(), 20));
                }

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
            LogEdit el = new LogEdit(this, linkID, linkTypeID);
            el.openForEdit(id);
            Global.mainForm.addChild(el);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionAdd() {
        LogEdit el = new LogEdit(this, linkID, linkTypeID);
        el.openForNew();
        Global.mainForm.addChild(el);
    }
}
