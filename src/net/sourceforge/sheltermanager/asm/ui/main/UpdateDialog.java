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
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureLocal;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMDialog;
import net.sourceforge.sheltermanager.asm.ui.ui.HTMLViewer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Vector;


/**
 * Shows updates from the sheltermanager website
 */
public class UpdateDialog extends ASMDialog {
    private UI.Button btnClose;
    private UI.CheckBox chkShowUpdates;
    private Vector updates = null;

    public UpdateDialog(Vector updates) {
        this.updates = updates;
        init(Global.i18n("uimain", "Updates"),
            IconManager.getIcon(IconManager.SCREEN_UPDATES), "uimain", false);
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(chkShowUpdates);
        v.add(btnClose);

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return chkShowUpdates;
    }

    public void setSecurity() {
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        return false;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel();
        p.setLayout(UI.getBoxLayout(p, UI.BoxLayout.Y_AXIS));

        // Create items for each update
        for (int i = 0; i < updates.size(); i++) {
            UpdateEntry e = (UpdateEntry) updates.get(i);
            UI.Panel x = UI.getPanel(UI.getFlowLayout(true));
            x.add(UI.getLabel(e.date + ": " + e.text + " - "),
                UI.BorderLayout.CENTER);
            x.add(UI.getURLLabel(e.urltext, e.url, e.url), UI.BorderLayout.EAST);
            p.add(x);
        }

        UI.ScrollPane s = UI.getScrollPane(p);

        UI.Panel b = UI.getPanel(UI.getFlowLayout());
        chkShowUpdates = (UI.CheckBox) b.add(UI.getCheckBox(i18n("show_updates_in_future")));
        chkShowUpdates.setSelected(true);

        btnClose = (UI.Button) b.add(UI.getButton(i18n("Close"),
                    UI.fp(this, "actionClose")));

        setSize(600, 300);
        UI.centerWindow(this);
        add(s, UI.BorderLayout.CENTER);
        add(b, UI.BorderLayout.SOUTH);
    }

    public void actionClose() {
        Global.showUpdates = chkShowUpdates.isSelected();

        ConfigureLocal cl = new ConfigureLocal();
        cl.saveData();
        cl.dispose();
        dispose();
    }
}
