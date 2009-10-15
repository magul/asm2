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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Vector;


public class DateInputDlg extends swingwtx.swing.JDialog {
    private net.sourceforge.sheltermanager.asm.ui.ui.DateField db;
    private swingwtx.swing.JLabel lblQuestion;
    private swingwtx.swing.JButton btnCancel;
    private swingwtx.swing.JPanel pnlButtons;
    private swingwtx.swing.JButton btnOk;
    private swingwtx.swing.JPanel pnlMain;

    /** Creates new form DateInputDlg */
    public DateInputDlg(swingwt.awt.Frame parent, boolean modal,
        String message, String title) {
        super(parent, modal);
        initComponents();
        this.setTitle(title);
        this.lblQuestion.setText(message);
        this.db.setToToday();

        this.setSize(359, 200);

        // Center it and display
        UI.centerWindow(this);

        Dialog.lastDate = "";

        Vector ctl = new Vector();
        ctl.add(db);
        ctl.add(btnOk);
        ctl.add(btnCancel);
        Global.focusManager.addComponentSet(ctl, this, db);

        this.show();
    }

    private void initComponents() {
        pnlMain = new swingwtx.swing.JPanel();
        lblQuestion = new swingwtx.swing.JLabel();
        db = new net.sourceforge.sheltermanager.asm.ui.ui.DateField();
        pnlButtons = new swingwtx.swing.JPanel();
        btnOk = new swingwtx.swing.JButton();
        btnCancel = new swingwtx.swing.JButton();

        setDefaultCloseOperation(swingwtx.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowListener(new swingwt.awt.event.WindowAdapter() {
                public void windowClosing(swingwt.awt.event.WindowEvent evt) {
                    closeDialog(evt);
                }
            });

        lblQuestion.setHorizontalAlignment(swingwtx.swing.SwingConstants.CENTER);
        lblQuestion.setText("lblQuestion");
        lblQuestion.setPreferredSize(new swingwt.awt.Dimension(350, 45));
        pnlMain.add(lblQuestion);

        db.setMinimumSize(UI.getDimension(200, 38));
        db.setPreferredSize(UI.getDimension(200, UI.getTextBoxHeight()));
        pnlMain.add(db);

        getContentPane().add(pnlMain, swingwt.awt.BorderLayout.CENTER);

        btnOk.setMnemonic('o');
        btnOk.setText(Global.i18n("uierror", "Ok"));
        btnOk.addActionListener(new swingwt.awt.event.ActionListener() {
                public void actionPerformed(swingwt.awt.event.ActionEvent evt) {
                    btnOkActionPerformed(evt);
                }
            });

        pnlButtons.add(btnOk);

        btnCancel.setMnemonic('c');
        btnCancel.setText(Global.i18n("uierror", "Cancel"));
        btnCancel.addActionListener(new swingwt.awt.event.ActionListener() {
                public void actionPerformed(swingwt.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });

        pnlButtons.add(btnCancel);

        getContentPane().add(pnlButtons, swingwt.awt.BorderLayout.SOUTH);

        pack();
    }

    private void btnCancelActionPerformed(swingwt.awt.event.ActionEvent evt) {
        closeDialog(null);
    }

    private void btnOkActionPerformed(swingwt.awt.event.ActionEvent evt) {
        Dialog.lastDate = db.getText();
        closeDialog(null);
    }

    /** Closes the dialog */
    private void closeDialog(swingwt.awt.event.WindowEvent evt) {
        Global.focusManager.removeComponentSet(this);
        setVisible(false);
        dispose();
    }
}
