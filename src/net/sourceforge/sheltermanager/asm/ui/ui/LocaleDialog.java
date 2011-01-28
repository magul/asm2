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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.util.Vector;


@SuppressWarnings("serial")
public class LocaleDialog extends ASMDialog {
    private LocaleSwitcher ls;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    @SuppressWarnings("unused")
    private UI.Button btnScan;

    public LocaleDialog() {
        super();
        Dialog.lastLocale = "";
        init(Global.i18n("uierror", "select_locale"),
            IconManager.getIcon(IconManager.SCREEN_LOCALE), "uierror", false);
        setVisible(true);
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return btnOk;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());

        ls = (LocaleSwitcher) UI.addComponent(p,
                Global.i18n("uisystem", "System_Locale:"),
                new LocaleSwitcher(LocaleSwitcher.FULL));

        btnOk = UI.getButton(Global.i18n("uierror", "Ok"), 'o',
                UI.fp(this, "actionOk"));
        btnCancel = UI.getButton(Global.i18n("uierror", "Cancel"), 'c',
                UI.fp(this, "actionCancel"));

        pb.add(btnOk);
        pb.add(btnCancel);

        add(p, UI.BorderLayout.CENTER);
        add(pb, UI.BorderLayout.SOUTH);

        setSize(450, 90);
        getRootPane().setDefaultButton(btnOk);

        // Center it and display
        UI.centerWindow(this);
    }

    public void actionCancel() {
        Dialog.lastLocale = "";
        dispose();
    }

    public void actionOk() {
        Dialog.lastLocale = ls.getSelectedLocale();
        dispose();
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        return false;
    }
}
