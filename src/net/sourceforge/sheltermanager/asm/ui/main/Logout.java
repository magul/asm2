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

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMDialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 * Screen for prompting user when they exit to determine whether they meant to
 * logout or quit the program.
 *
 * @author Robin Rawson-Tetley
 */
public class Logout extends ASMDialog {
    private UI.Button btnCancel;
    private UI.Button btnClose;
    private UI.Button btnLogout;
    private UI.CheckBox chkStop;

    /** Creates new form Logout */
    public Logout() {
        super();
        init(Global.i18n("uimain", "Logout_or_close"),
            IconManager.getIcon(IconManager.SCREEN_LOGOUT_QUESTION), "uimain",
            false);
        UI.centerWindow(this);
        this.setVisible(true);
    }

    public void storeSetting() {
        Configuration.setEntry(Global.currentUserName + "_PromptLogout",
            (chkStop.isSelected() ? "No" : "Yes"));
    }

    public boolean windowCloseAttempt() {
        return false;
    }

    public void windowOpened() {
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return chkStop;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(chkStop);

        if (!Configuration.getBoolean("AutoLoginOSUsers")) {
            v.add(btnLogout);
        }

        v.add(btnClose);
        v.add(btnCancel);

        return v;
    }

    public void initComponents() {
        UI.Panel pnlMain = UI.getPanel(UI.getGridLayout(1));
        UI.Panel pnlMessage = UI.getPanel(UI.getFlowLayout());
        UI.Panel pnlStopIt = UI.getPanel(UI.getFlowLayout());
        UI.Panel pnlButtons = UI.getPanel(UI.getFlowLayout());

        UI.Label lblImage = UI.getLabel(IconManager.getIcon(
                    IconManager.SCREEN_LOGOUT_QUESTION));
        pnlMessage.add(lblImage);

        UI.Label lblText = UI.getLabel(i18n("Do_you_want_to_logout_or_close_ASM?"));
        lblText.setHorizontalAlignment(UI.ALIGN_CENTER);
        pnlMessage.add(lblText);

        chkStop = UI.getCheckBox(i18n("Dont_ask_me_again"));
        pnlStopIt.add(chkStop);

        if (!Configuration.getBoolean("AutoLoginOSUsers")) {
            btnLogout = (UI.Button) pnlButtons.add(UI.getButton(i18n("Logout"),
                        null, 'l', null, UI.fp(this, "actionLogout")));
        }

        btnClose = (UI.Button) pnlButtons.add(UI.getButton(i18n("Close_ASM"),
                    null, 'c', null, UI.fp(this, "actionQuit")));

        btnCancel = (UI.Button) pnlButtons.add(UI.getButton(i18n("Cancel"),
                    null, 'a', null, UI.fp(this, "actionCancel")));

        pnlMain.add(pnlMessage);
        pnlMain.add(pnlStopIt);
        add(pnlMain, UI.BorderLayout.CENTER);
        add(pnlButtons, UI.BorderLayout.SOUTH);

        setSize(378, 175);
        getRootPane().setDefaultButton(btnCancel);
    }

    public void actionCancel() {
        setVisible(false);
        dispose();
    }

    public void actionQuit() {
        storeSetting();
        setVisible(false);
        dispose();
        Global.mainForm.closeApplication();
    }

    public void actionLogout() {
        storeSetting();
        // Reload the login form, destroy this frame and the main frame
        setVisible(false);
        dispose();
        Global.mainForm.logout();
    }
}
