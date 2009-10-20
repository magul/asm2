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


public class DateInputDlg extends javax.swing.JDialog {
    private net.sourceforge.sheltermanager.asm.ui.ui.DateField db;
    private javax.swing.JLabel lblQuestion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JButton btnOk;
    private javax.swing.JPanel pnlMain;

    /** Creates new form DateInputDlg */
    public DateInputDlg(java.awt.Frame parent, boolean modal, String message,
        String title) {
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
        pnlMain = new javax.swing.JPanel();
        lblQuestion = new javax.swing.JLabel();
        db = new net.sourceforge.sheltermanager.asm.ui.ui.DateField();
        pnlButtons = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    closeDialog(evt);
                }
            });

        lblQuestion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuestion.setText("lblQuestion");
        lblQuestion.setPreferredSize(new java.awt.Dimension(350, 45));
        pnlMain.add(lblQuestion);

        db.setMinimumSize(UI.getDimension(200, 38));
        db.setPreferredSize(UI.getDimension(200, UI.getTextBoxHeight()));
        pnlMain.add(db);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        btnOk.setMnemonic('o');
        btnOk.setText(Global.i18n("uierror", "Ok"));
        btnOk.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnOkActionPerformed(evt);
                }
            });

        pnlButtons.add(btnOk);

        btnCancel.setMnemonic('c');
        btnCancel.setText(Global.i18n("uierror", "Cancel"));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });

        pnlButtons.add(btnCancel);

        getContentPane().add(pnlButtons, java.awt.BorderLayout.SOUTH);

        pack();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        closeDialog(null);
    }

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {
        Dialog.lastDate = db.getText();
        closeDialog(null);
    }

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        Global.focusManager.removeComponentSet(this);
        setVisible(false);
        dispose();
    }
}
