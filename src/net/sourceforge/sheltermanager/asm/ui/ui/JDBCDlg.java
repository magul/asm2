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

import net.sourceforge.sheltermanager.asm.db.LocateDatabase;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.io.File;

import java.sql.Connection;

import java.util.Vector;


public class JDBCDlg extends ASMDialog {
    private UI.ComboBox cboType;
    private UI.TextField txtHostname;
    private UI.TextField txtUser;
    private UI.TextField txtDatabase;
    private UI.PasswordField txtPassword;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.Button btnScan;
    private ThrobberSmall thrThrob;
    private UI.Panel pb;

    public JDBCDlg(String title) {
        super();
        Dialog.lastJDBC = "";
        init(title, IconManager.getIcon(IconManager.SCREEN_JDBCDLG), "db", false);
        cboType.setSelectedIndex(0);
        changeType();
        show();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboType);
        ctl.add(txtHostname);
        ctl.add(txtDatabase);
        ctl.add(txtUser);
        ctl.add(txtPassword);
        ctl.add(btnOk);
        ctl.add(btnCancel);
        ctl.add(btnScan);

        return ctl;
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return cboType;
    }

    public void initComponents() {
        UI.TableLayout t = UI.getTableLayout(2);
        t.setMargin(5);

        UI.Panel p = UI.getPanel(t);

        cboType = (UI.ComboBox) UI.addComponent(p, i18n("databasetype"),
                UI.getCombo(UI.fp(this, "changeType")));

        cboType.addItem(i18n("local"));
        cboType.addItem(i18n("mysql"));
        cboType.addItem(i18n("postgresql"));
        cboType.addItem(i18n("hsqldb"));
        cboType.addItem(i18n("smcom"));

        txtHostname = (UI.TextField) UI.addComponent(p, i18n("hostname"),
                UI.getTextField());

        txtDatabase = (UI.TextField) UI.addComponent(p, i18n("database"),
                UI.getTextField());

        txtUser = (UI.TextField) UI.addComponent(p, i18n("username"),
                UI.getTextField());

        txtPassword = (UI.PasswordField) UI.addComponent(p, i18n("password"),
                UI.getPasswordField());

        btnOk = UI.getButton(Global.i18n("uierror", "Ok"), 'o',
                UI.fp(this, "actionOk"));
        btnCancel = UI.getButton(Global.i18n("uierror", "Cancel"), 'c',
                UI.fp(this, "actionCancel"));
        btnScan = UI.getButton(i18n("scan"), 's', UI.fp(this, "actionScan"));
        thrThrob = new ThrobberSmall();
        thrThrob.setVisible(false);

        pb = UI.getPanel(UI.getFlowLayout());
        pb.add(btnOk);
        pb.add(btnCancel);
        pb.add(btnScan);
        pb.add(thrThrob);

        add(p, UI.BorderLayout.CENTER);
        add(pb, UI.BorderLayout.SOUTH);

        // Put a database icon down the side
        UI.Label pic = UI.getLabel(IconManager.getIcon(
                    IconManager.SCREEN_JDBCDLG_DATABASE));
        add(pic, UI.BorderLayout.WEST);

        setSize(450, 240);
        getRootPane().setDefaultButton(btnOk);

        // Center it and display
        UI.centerWindow(this);
    }

    public void actionCancel() {
        Dialog.lastJDBC = "";
        dispose();
    }

    public void actionOk() {
        // Construct JDBC URL
        String pass = new String(txtPassword.getPassword());
        boolean hasPass = !pass.trim().equals("");
        String db = txtDatabase.getText();
        String host = txtHostname.getText();
        String user = txtUser.getText();

        switch (cboType.getSelectedIndex()) {
        case 0:
            Dialog.lastJDBC = "jdbc:hsqldb:file:" + Global.tempDirectory +
                File.separator + "localdb";

            break;

        case 1:
            Dialog.lastJDBC = "jdbc:mysql://" + host + "/" + db + "?user=" +
                user + (hasPass ? ("&password=" + pass) : "") +
                "&zeroDateTimeBehaviour=convertToNull&useUnicode=true&characterEncoding=UTF8";

            break;

        case 2:
            Dialog.lastJDBC = "jdbc:postgresql://" + host + "/" + db +
                "?user=" + user + (hasPass ? ("&password=" + pass) : "");

            break;

        case 3:
            Dialog.lastJDBC = "jdbc:hsqldb:hsql://" + host + "/" + db +
                ((!user.equals("")) ? ("?user=" + user) : "") +
                (hasPass ? ("&password=" + pass) : "");

            break;

        case 4:
            Dialog.lastJDBC = "jdbc:postgresql://db.sheltermanager.com:10678/" +
                user + "?user=" + user + "&password=" + pass;

            break;
        }

        // Verify the JDBC connection before returning
        Connection c = DBConnection.getConnection(Dialog.lastJDBC);

        if (c == null) {
            String e = Global.i18n("db", "couldntconnectmess", Dialog.lastJDBC);
            Dialog.showError(e, Global.i18n("db", "couldntconnect"));
            Global.logError(e, "JDBCDlg.btnOkActionPerformed");

            return;
        }

        // Verify that there's an ASM database in there
        try {
            SQLRecordset r = new SQLRecordset();
            r.openRecordset(c, "SELECT COUNT(*) FROM animal", "animal");
            r.free();
            r = null;
        } catch (Exception e) {
            String msg = Global.i18n("db", "asmdatastructuremissing",
                    Global.dataDirectory + File.separator + "sql");
            Dialog.showError(msg, Global.i18n("db", "couldntconnect"));
            Global.logException(e, JDBCDlg.class);

            return;
        }

        // Dump the connection we opened
        try {
            c.close();
        } catch (Exception e) {
        }

        dispose();
    }

    public void actionScan() {
        // Separate thread so we don't block UI
        new Thread() {
                public void run() {
                    // Do the scan
                    thrThrob.setVisible(true);
                    thrThrob.start();
                    pb.revalidate();

                    String[] db = LocateDatabase.scanDatabase();

                    thrThrob.stop();
                    thrThrob.setVisible(false);
                    pb.revalidate();

                    // Nothing returned, bail out
                    if (db[0] == null) {
                        return;
                    }

                    // Set the found database type
                    cboType.setSelectedIndex(Integer.parseInt(db[1]));
                    changeType();

                    // And the address
                    txtHostname.setText(db[0]);
                    txtDatabase.setText("asm");
                }
            }.start();
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        return false;
    }

    /** Fires when the database type is changed */
    public void changeType() {
        boolean isLocal = cboType.getSelectedIndex() == 0;
        boolean isSM = cboType.getSelectedIndex() == 4;

        if (isLocal || isSM) {
            txtHostname.setEnabled(false);
            txtDatabase.setEnabled(false);
        } else {
            txtHostname.setEnabled(true);
            txtDatabase.setEnabled(true);
        }

        txtUser.setEnabled(!isLocal);
        txtPassword.setEnabled(!isLocal);
        txtDatabase.setText("asm");
    }
}
