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
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Vector;


public class DateInputDlg extends ASMDialog {
    private DateField db;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private String message;

    /** Creates new form DateInputDlg */
    public DateInputDlg(String message, String title) {
        this.message = message;
        init(title, IconManager.getIcon(IconManager.QUESTION), "uierror", false);
        db.setToToday();
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

        db = (DateField) p.add(UI.getDateField());

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
        return db;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(db);
        v.add(btnOk);
        v.add(btnCancel);

        return v;
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        Dialog.lastDate = "";

        return false;
    }

    public void setSecurity() {
    }

    public void actionCancel() {
        Dialog.lastDate = "";
        dispose();
    }

    public void actionOk() {
        Dialog.lastDate = db.getText();
        dispose();
    }
}
