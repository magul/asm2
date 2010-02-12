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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Email;

import java.util.Vector;


/**
 * UI for sending an email
 *
 * @author Robin Rawson-Tetley
 */
public class EmailForm extends ASMForm {
    private EmailFormListener parent = null;
    private UI.Panel pnlFields;
    private UI.Panel pnlHead;
    private UI.Panel pnlBody;
    private UI.Panel pnlTo;
    private UI.Panel pnlTop;
    private UI.Panel pnlSubject;
    private UI.Button btnCancel;
    private UI.Button btnSend;
    private UI.List lstFields;
    private UI.ToolBar tlb;
    private UI.TextArea txtBody;
    private UI.TextField txtSubject;
    private UI.TextField txtTo;

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
        ctl.add(txtTo);
        ctl.add(txtSubject);
        ctl.add(txtBody);
        ctl.add(btnSend);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtSubject;
    }

    public void setTo(String to) {
        txtTo.setText(to);
    }

    public void removeFields() {
        pnlBody.remove(pnlFields);
    }

    public void addFields(Vector fields) {
        lstFields.setListData(fields);
        fields = null;
    }

    public void removeTo() {
        pnlHead.remove(pnlTo);
        setTitle(i18n("send_bulk_email"));
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
        pnlHead = UI.getPanel(UI.getGridLayout(1));
        pnlTo = UI.getPanel(UI.getGridLayout(2, new int[] { 10, 90 }));
        pnlSubject = UI.getPanel(UI.getGridLayout(2, new int[] { 10, 90 }));
        pnlBody = UI.getPanel(UI.getBorderLayout());
        pnlFields = UI.getPanel(UI.getBorderLayout());

        tlb = new UI.ToolBar();

        btnSend = (UI.Button) tlb.add(UI.getButton(i18n("send"), null, 's',
                    IconManager.getIcon(IconManager.EMAIL),
                    UI.fp(this, "actionSend")));

        btnCancel = (UI.Button) tlb.add(UI.getButton(i18n("cancel"), null, 'c',
                    IconManager.getIcon(IconManager.CLOSE),
                    UI.fp(this, "actionCancel")));

        txtTo = (UI.TextField) UI.addComponent(pnlTo, i18n("to"),
                UI.getTextField());
        txtSubject = (UI.TextField) UI.addComponent(pnlSubject,
                i18n("subject"), UI.getTextField());
        txtBody = (UI.TextArea) pnlBody.add(UI.getTextArea());
        lstFields = (UI.List) pnlFields.add(UI.getList(UI.fp(this,
                        "listDoubleClicked")));

        pnlTop.add(tlb, UI.BorderLayout.NORTH);
        pnlHead.add(pnlTo);
        pnlHead.add(pnlSubject);
        pnlTop.add(pnlHead, UI.BorderLayout.CENTER);

        add(pnlTop, UI.BorderLayout.NORTH);
        add(pnlBody, UI.BorderLayout.CENTER);
        add(pnlFields, UI.BorderLayout.EAST);
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
            parent.sendEmail(txtSubject.getText(), txtBody.getText());
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
            email.sendmsg(txtTo.getText(), txtSubject.getText(),
                txtBody.getText());
            email.close();

            // Clean up and close
            email = null;
            dispose();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
