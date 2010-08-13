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
package net.sourceforge.sheltermanager.asm.ui.internet;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Email;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Date;
import java.util.Vector;


/**
 * UI for sending an email
 *
 * @author Robin Rawson-Tetley
 */
public class EmailForm extends ASMForm {
    private EmailFormListener parent = null;
    private int ownerid = 0;
    private OwnerEdit parentOwnerForm;
    private UI.Panel pnlFields;
    private UI.Panel pnlHead;
    private UI.Panel pnlBody;
    private UI.Panel pnlTo;
    private UI.Panel pnlTop;
    private UI.Panel pnlSubject;
    private UI.Panel pnlLog;
    private UI.Button btnCancel;
    private UI.Button btnSend;
    private UI.CheckBox chkHTML;
    private UI.CheckBox chkAddLog;
    private UI.ComboBox cboLog;
    private UI.List lstFields;
    private UI.ToolBar tlb;
    private UI.TextArea txtBody;
    private UI.TextField txtFrom;
    private UI.TextField txtSubject;
    private UI.TextField txtTo;
    private UI.TextField txtCC;
    private UI.Label lblTo;
    private UI.Label lblCC;

    public EmailForm() {
        init(Global.i18n("uiinternet", "send_email"),
            IconManager.getIcon(IconManager.SCREEN_EMAILFORM), "uiinternet");
    }

    public void dispose() {
        parent = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtFrom);
        ctl.add(txtTo);
        ctl.add(txtCC);
        ctl.add(txtSubject);
        ctl.add(chkHTML);
        ctl.add(txtBody);
        ctl.add(chkAddLog);
        ctl.add(cboLog);
        ctl.add(btnSend);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtSubject;
    }

    public void setTo(String to) {
        txtTo.setText(to);
        String s = Configuration.getString("EmailSignature");
        if (!s.equals(""))
            txtBody.setText("\n--\n" + s);
    }

    public void setOwnerID(int ownerid) {
        this.ownerid = ownerid;
    }

    public void setParentOwnerForm(OwnerEdit form) {
        this.parentOwnerForm = form;
    }

    public void removeFields() {
        pnlBody.remove(pnlFields);
    }

    public void addFields(Vector fields) {
        lstFields.setListData(fields);
        fields = null;
    }

    /** Put the screen into bulk email mode */
    public void setBulkEmail() {
        // Change title
        setTitle(i18n("send_bulk_email"));

        // Remove to and CC fields
        pnlHead.remove(lblTo);    
        pnlHead.remove(txtTo);    
        pnlHead.remove(lblCC);    
        pnlHead.remove(txtCC);    

        // Get rid of log controls
        remove(pnlLog);

        // Show bulk email fields
        add(pnlFields, UI.BorderLayout.EAST);
    }

    public void setParent(EmailFormListener parent) {
        this.parent = parent;
    }

    public boolean formClosing() {
        if (parent != null) {
            parent.cancelEmail();
        }

        return false;
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

    public void initComponents() {
        pnlTop = UI.getPanel(UI.getBorderLayout());
        pnlHead = UI.getPanel(UI.getGridLayout(2, new int[] { 10, 90 }));
        pnlBody = UI.getPanel(UI.getBorderLayout());
        pnlFields = UI.getPanel(UI.getBorderLayout());
        pnlLog = UI.getPanel(UI.getFlowLayout());

        tlb = new UI.ToolBar();

        btnSend = (UI.Button) tlb.add(UI.getButton(i18n("send"), null, 's',
                    IconManager.getIcon(IconManager.EMAIL),
                    UI.fp(this, "actionSend")));

        btnCancel = (UI.Button) tlb.add(UI.getButton(i18n("cancel"), null, 'c',
                    IconManager.getIcon(IconManager.CLOSE),
                    UI.fp(this, "actionCancel")));

        txtFrom = (UI.TextField) UI.addComponent(pnlHead, i18n("from"),
                UI.getTextField());
        txtFrom.setText(Configuration.getString("Organisation") + " <" + Configuration.getString("EmailAddress") + ">");

        lblTo = (UI.Label) UI.addComponent(pnlHead, UI.getLabel(i18n("to")));
        txtTo = (UI.TextField) UI.addComponent(pnlHead, UI.getTextField());

        lblCC = (UI.Label) UI.addComponent(pnlHead, UI.getLabel(i18n("cc")));
        txtCC = (UI.TextField) UI.addComponent(pnlHead, UI.getTextField());

        txtSubject = (UI.TextField) UI.addComponent(pnlHead,
                i18n("subject"), UI.getTextField());
        chkHTML = (UI.CheckBox) UI.addComponent(pnlHead,
                "", UI.getCheckBox(i18n("HTML")));

        txtBody = (UI.TextArea) pnlBody.add(UI.getTextArea());
        lstFields = (UI.List) pnlFields.add(UI.getList(UI.fp(this,
                        "listDoubleClicked")));

        pnlTop.add(tlb, UI.BorderLayout.NORTH);
        pnlTop.add(pnlHead, UI.BorderLayout.CENTER);

        chkAddLog = (UI.CheckBox) UI.addComponent(pnlLog,
            UI.getCheckBox(i18n("add_to_log"), i18n("store_this_email_in_the_log"), 
            UI.fp(this, "actionLogCheckChanged")));

        cboLog = UI.getCombo(LookupCache.getLogTypeLookup(), "LogTypeName");
        Utils.setComboFromID(LookupCache.getLogTypeLookup(), "LogTypeName", 
            Configuration.getInteger("AFDefaultLogFilter"), cboLog);
        cboLog.setEnabled(false);
        UI.addComponent(pnlLog, cboLog);

        add(pnlTop, UI.BorderLayout.NORTH);
        add(pnlBody, UI.BorderLayout.CENTER);
        add(pnlLog, UI.BorderLayout.SOUTH);
    }

    public void listDoubleClicked() {
        if (lstFields.getSelectedIndex() >= 0) {
            txtBody.append((String) lstFields.getSelectedValue());
        }
    }

    public void actionCancel() {
        if (parent != null) {
            parent.cancelEmail();
        }

        dispose();
    }

    public void actionLogCheckChanged() {
        cboLog.setEnabled(chkAddLog.isSelected());
    }

    public void actionSend() {
        
        // Make sure there is a subject
        if (txtSubject.getText().equals("")) {
            Dialog.showError(i18n("you_must_supply_a_subject"));

            return;
        }

        // Make sure there is some text
        if (txtBody.getText().equals("")) {
            Dialog.showError(i18n("you_must_supply_a_body"));

            return;
        }

        // If there is a parent, fire the event and stop now
        if (parent != null) {
            parent.sendEmail(txtFrom.getText(), txtSubject.getText(), txtBody.getText(), 
                chkHTML.isSelected() ? "text/html" : "text/plain");
            dispose();

            return;
        }

        // Make sure we have a to
        if (txtTo.getText().equals("")) {
            Dialog.showError(i18n("you_must_supply_a_to"));

            return;
        }

        // Send it
        try {

            Email email = new Email();
            email.sendmsg(txtTo.getText(), txtCC.getText(), txtSubject.getText(),
                txtBody.getText(), txtFrom.getText(), 
                chkHTML.isSelected() ? "text/html" : "text/plain");
            email.close();

            // Store it in the log if the option is set
            if (chkAddLog.isSelected() && ownerid != 0) {
                Log l = new Log("ID=0");
                l.addNew();
                l.setLogTypeID(Utils.getIDFromCombo(LookupCache.getLogTypeLookup(), "LogTypeName", cboLog));
                l.setLinkID(new Integer(ownerid));
                l.setLinkType(new Integer(Log.LINKTYPE_OWNER));
                l.setDate(new Date());
                l.setComments(txtSubject.getText() + "\n \n" + txtBody.getText());
                l.save(Global.currentUserName);
                if (parentOwnerForm != null) {
                    parentOwnerForm.log.updateList();
                }
            }

            // Clean up and close
            email = null;
            dispose();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
