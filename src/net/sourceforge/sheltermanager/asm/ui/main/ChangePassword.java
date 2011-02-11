/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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
package net.sourceforge.sheltermanager.asm.ui.main;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMDialog;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.MD5;

import java.util.Vector;


/**
 * Screen for changing user passwords
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class ChangePassword extends ASMDialog {
    private UI.Button btnOk;
    private UI.Button btnCancel;
    private UI.PasswordField txtPass;
    private UI.PasswordField txtNew;
    private UI.PasswordField txtConf;

    public ChangePassword() {
        super();
        init(Global.i18n("uimain", "Change_Password"), null, "uimain", false);

        // Center us
        UI.centerWindow(this);
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel bot = UI.getPanel(UI.getFlowLayout());

        txtPass = (UI.PasswordField) UI.addComponent(p,
                i18n("Current_Password:"), UI.getPasswordField());
        txtNew = (UI.PasswordField) UI.addComponent(p, i18n("New_Password:"),
                UI.getPasswordField());
        txtConf = (UI.PasswordField) UI.addComponent(p,
                i18n("Confirm_Password:"), UI.getPasswordField());

        btnOk = (UI.Button) bot.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "actionOk")));

        btnCancel = (UI.Button) bot.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(p, UI.BorderLayout.NORTH);
        add(bot, UI.BorderLayout.SOUTH);

        setSize(400, 200);
        getRootPane().setDefaultButton(btnOk);
    }

    public void actionOk() {
        try {
            // Do the two they gave us match?
            if (!new String(txtNew.getPassword()).equals(
                        new String(txtConf.getPassword()))) {
                Dialog.showError(i18n("new_passwords_dont_match"));
            }
            // Verify old password is correct
            else if (MD5.hash(new String(txtPass.getPassword()))
                            .equals(Global.currentUserObject.getPassword())) {
                // It is, let's change it
                Global.currentUserObject.setPassword(MD5.hash(
                        new String(txtNew.getPassword())));
                Global.currentUserObject.save();
                dispose();
            } else {
                Dialog.showError(i18n("incorrect_password"));
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void windowOpened() {
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return txtPass;
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> v = new Vector<Object>();
        v.add(txtPass);
        v.add(txtNew);
        v.add(txtConf);
        v.add(btnOk);
        v.add(btnCancel);

        return v;
    }

    public boolean windowCloseAttempt() {
        return false;
    }
}
