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
package net.sourceforge.sheltermanager.asm.ui.main;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMDialog;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.LDAP;
import net.sourceforge.sheltermanager.asm.utility.MD5;

import java.util.Vector;


/**
 * Screen for locking ASM whilst away from the keyboard.
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class Locked extends ASMDialog {
    private UI.Button btnOk;
    private UI.PasswordField txtPass;

    /** Creates new form Locked */
    public Locked() {
        super();
        init(Global.i18n("uimain", "Locked"), null, "uimain", false);

        // Center us
        UI.centerWindow(this);
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getGridLayout(1));
        UI.Panel top = UI.getPanel(UI.getFlowLayout());
        UI.Panel bot = UI.getPanel(UI.getFlowLayout());

        top.add(UI.getLabel(IconManager.getIcon(
                    IconManager.SCREEN_LOCKED_ASMLOGO)));
        top.add(UI.getLabel(i18n("please_enter_password_for")));
        top.add(UI.getLabel(Global.currentUserName));

        txtPass = (UI.PasswordField) bot.add(UI.getPasswordField());
        btnOk = (UI.Button) bot.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "actionOk")));

        p.add(top);
        p.add(bot);
        add(p);

        setSize(378, 200);
        getRootPane().setDefaultButton(btnOk);
    }

    public void actionOk() {
        // Verify password is correct
        try {
            if (LDAP.isConfigured()) {
                if (LDAP.authenticate(Global.currentUserObject.getUserName(),
                            new String(txtPass.getPassword()))) {
                    dispose();

                    return;
                }
            } else {
                if (MD5.hash(new String(txtPass.getPassword()))
                           .equals(Global.currentUserObject.getPassword())) {
                    dispose();

                    return;
                }
            }

            Dialog.showError(i18n("incorrect_password"));
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
        v.add(btnOk);

        return v;
    }

    public boolean windowCloseAttempt() {
        return true; // Must use Ok button to close
    }
}
