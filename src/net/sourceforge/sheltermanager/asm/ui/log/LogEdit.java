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

import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.text.ParseException;

import java.util.Vector;


/**
 * This class contains all code for editing individual log records.
 *
 * @author Robin Rawson-Tetley
 */
public class LogEdit extends ASMForm {
    private Log log = null;

    /** A reference back to the parent form that spawned this form */
    private LogSelector parent = null;
    private int linkID = 0;
    private int linkTypeID = 0;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboLogType;
    private UI.TextArea txtComments;
    private DateField txtDate;
    private String audit = null;

    public LogEdit(LogSelector parent, int linkID, int linkTypeID) {
        this.parent = parent;
        this.linkID = linkID;
        this.linkTypeID = linkTypeID;

        init("", IconManager.getIcon(IconManager.SCREEN_EDITLOG), "uilog");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboLogType);
        ctl.add(txtDate.getTextField());
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboLogType;
    }

    public void openForNew() {
        this.log = new Log();
        this.setTitle(i18n("New_Log"));
        log.openRecordset("ID = 0");

        try {
            log.addNew();
            log.setLinkID(new Integer(linkID));
            log.setLinkType(new Integer(linkTypeID));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        // Date required
        txtDate.setToToday();
    }

    public void openForEdit(int id) {
        this.log = new Log();
        log.openRecordset("ID = " + id);

        this.setTitle(i18n("Edit_Log"));
        loadData();
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public void loadData() {
        try {
            try {
                this.txtDate.setText(Utils.formatDate(log.getDate()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            Utils.setComboFromID(LookupCache.getLogTypeLookup(), "LogTypeName",
                log.getLogTypeID(), cboLogType);
            this.txtComments.setText(Utils.nullToEmptyString(log.getComments()));
            audit = UI.messageAudit(log.getCreatedDate(), log.getCreatedBy(),
                    log.getLastChangedDate(), log.getLastChangedBy());
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("Unable_to_read_log_record:") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        try {
            log.setComments(txtComments.getText());

            try {
                log.setDate(Utils.parseDate(txtDate.getText()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }

            log.setLogTypeID(Utils.getIDFromCombo(
                    LookupCache.getLogTypeLookup(), "LogTypeName", cboLogType));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            log.save(Global.currentUserName);

            // Update the edit animal form if successful
            parent.updateList();

            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage(),
                Global.i18n("uianimal", "Validation_Error"));
        }

        return false;
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel buttons = UI.getPanel(UI.getFlowLayout());

        cboLogType = UI.getCombo(i18n("Log_Type:"),
                LookupCache.getLogTypeLookup(), "LogTypeName");
        UI.addComponent(top, i18n("Log_Type:"), cboLogType);

        txtDate = (DateField) UI.addComponent(top, i18n("Date:"),
                UI.getDateField(i18n("The_date_of_log_the_entry")));

        txtComments = (UI.TextArea) UI.addComponent(mid,
                Global.i18n("uianimal", "Details:"),
                UI.getTextArea(i18n("The_text_of_the_log_entry")));

        btnOk = (UI.Button) buttons.add(UI.getButton(Global.i18n("uianimal",
                        "Ok"), null, 'o', null, UI.fp(this, "saveData")));

        btnCancel = (UI.Button) buttons.add(UI.getButton(Global.i18n(
                        "uianimal", "Cancel"), null, 'c', null,
                    UI.fp(this, "dispose")));

        add(top, UI.BorderLayout.NORTH);
        add(mid, UI.BorderLayout.CENTER);
        add(buttons, UI.BorderLayout.SOUTH);
    }
}
