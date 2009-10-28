/*
   Animal Shelter Manager
   Copyright(c)2000-2009, R. Rawson-Tetley

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

import java.util.Vector;


/**
 * Shows a box for text input
 */
public class InputTextDialog extends ASMDialog {
    String message = "";
    UI.TextField txt = null;
    UI.Button btnOk = null;
    UI.Button btnCancel = null;

    public InputTextDialog(String message, String title) {
        this.message = message;
        init(title, IconManager.getIcon(IconManager.QUESTION), "uierror", false);
        UI.centerWindow(this);
        show();
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getFlowLayout());
        UI.Panel b = UI.getPanel(UI.getFlowLayout());

        p.add(UI.getLabel(IconManager.getIcon(IconManager.QUESTION)));

        UI.Label lblQuestion = UI.getLabel("<html>" + message + "</html>");
        lblQuestion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuestion.setPreferredSize(new java.awt.Dimension(350, 45));
        p.add(lblQuestion);

        txt = (UI.TextField) p.add(UI.getTextField());

        btnOk = UI.getButton(i18n("Ok"), null, 'o', null,
                UI.fp(this, "actionOk"));
        btnCancel = UI.getButton(i18n("Cancel"), null, 'c', null,
                UI.fp(this, "actionCancel"));
        b.add(btnOk);
        b.add(btnCancel);

        add(p, UI.BorderLayout.CENTER);
        add(b, UI.BorderLayout.SOUTH);

        setSize(400, 200);
        getRootPane().setDefaultButton(btnOk);
    }

    public Object getDefaultFocusedComponent() {
        return txt;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(txt);
        v.add(btnOk);
        v.add(btnCancel);

        return v;
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        Dialog.lastInput = null;

        return false;
    }

    public void setSecurity() {
    }

    public void actionCancel() {
        Dialog.lastInput = null;
        dispose();
    }

    public void actionOk() {
        Dialog.lastInput = txt.getText();
        dispose();
    }
}
