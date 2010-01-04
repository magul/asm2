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
package net.sourceforge.sheltermanager.asm.ui.login;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Users;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.main.Main;
import net.sourceforge.sheltermanager.asm.ui.main.ShutdownThread;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMWindow;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.LDAP;
import net.sourceforge.sheltermanager.asm.utility.MD5;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * This class contains all code surrounding system login.
 *
 * @see net.sourceforge.sheltermanager.asm.ui.splash.Splash
 * @author Robin Rawson-Tetley
 */
public class Login extends ASMWindow {
    /**
     * If set, the application will be killed when this screen is closed.
     */
    private boolean killOnClose = true;
    private UI.Button btnExit;
    private UI.Button btnLogin;
    private UI.PasswordField txtPassword;
    private UI.TextField txtUsername;

    /** Creates new form Loginn */
    public Login() {
        // Skip doing anything if auto login is on and we have 
        // a valid user
        if (autoLogUserIn()) {
            return;
        }

        init(Global.i18n("uilogin", "Animal_Shelter_Manager_Login"),
            IconManager.getIcon(IconManager.SCREEN_LOGIN), "uilogin", false);
    }

    public void windowOpened() {
        UI.invokeIn(new Runnable() {
                public void run() {
                    txtUsername.grabFocus();
                }
            }, 250);
    }

    public void setSecurity() {
    }

    public boolean windowCloseAttempt() {
        dispose();
        new ShutdownThread().start();

        return false;
    }

    public void dispose() {
        unregisterTabOrder();
        super.dispose();
    }

    public void unregisterTabOrder() {
        Global.focusManager.removeComponentSet(this);
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtUsername);
        ctl.add(txtPassword);
        ctl.add(btnLogin);
        ctl.add(btnExit);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtUsername;
    }

    /**
     * Opens the main screen
     */
    public void openMainForm(Users user) {
        // Remember the current user
        try {
            net.sourceforge.sheltermanager.asm.globals.Global.currentUserName = user.getUserName();
            net.sourceforge.sheltermanager.asm.globals.Global.currentUserObject = user;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create a new main form
        Main main = new Main();

        // Set a handle to the main form
        Global.mainForm = main;
        Dialog.theParent = main;

        // Close this screen
        killOnClose = false;
        this.dispose();

        // Display the main screen
        main.display();
    }

    /**
     * If the option is on to automatically log OS users in, then check if the
     * current user is a valid user on the system. If they are, log them in
     * without ever showing the logon screen.
     * If an applet user was given, use that instead.
     */
    public boolean autoLogUserIn() {
        try {
            if (Configuration.getBoolean("AutoLoginOSUsers")) {
                Global.logInfo("OS Security. Looking for matching user '" +
                    System.getProperty("user.name") + "'", "Login.autoLogUserIn");

                Users u = new Users();
                u.openRecordset("UserName Like '" +
                    System.getProperty("user.name") + "'");

                if (!u.getEOF()) {
                    Global.logInfo("PASS: Found matching ASM user '" +
                        System.getProperty("user.name") + "', logging in...",
                        "Login.autoLogUserIn");
                    openMainForm(u);

                    return true;
                }

                Global.logInfo("FAIL: ASM user '" +
                    System.getProperty("user.name") + "' doesn't exist.",
                    "Login.autoLogUserIn");
            }

            if (Global.appletUser != null) {
                Global.logInfo("Applet Security. Looking for matching user '" +
                    Global.appletUser + "'", "Login.autoLogUserIn");

                Users u = new Users();
                u.openRecordset("UserName Like '" + Global.appletUser + "'");

                if (!u.getEOF()) {
                    Global.logInfo("PASS: Found matching ASM user '" +
                        Global.appletUser + "', logging in...",
                        "Login.autoLogUserIn");
                    openMainForm(u);

                    return true;
                }

                Global.logInfo("FAIL: ASM user '" + Global.appletUser +
                    "' doesn't exist.", "Login.autoLogUserIn");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        UI.Panel pnlCenter = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlUserAndPass = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }), 20);

        // Show the user/pass boxes
        pnlUserAndPass.add(UI.getLabel(i18n("Username")));
        txtUsername = UI.getTextField();
        pnlUserAndPass.add(txtUsername);

        pnlUserAndPass.add(UI.getLabel(i18n("Password")));
        txtPassword = UI.getPasswordField();
        pnlUserAndPass.add(txtPassword);

        pnlCenter.add(pnlUserAndPass, UI.BorderLayout.NORTH);
        add(pnlCenter, UI.BorderLayout.CENTER);

        // Show the ASM splash centered at the top
        UI.Label lblSplash = UI.getLabel(IconManager.getSplashScreen());
        lblSplash.setHorizontalAlignment(UI.Label.CENTER);
        add(lblSplash, UI.BorderLayout.NORTH);

        // Buttons
        UI.Panel pnlButtons = UI.getPanel(UI.getFlowLayout());
        btnLogin = UI.getButton(i18n("Login"), null, 'o', null,
                UI.fp(this, "actionLogin"));
        pnlButtons.add(btnLogin);
        btnExit = UI.getButton(i18n("Exit"), null, 'c', null,
                UI.fp(this, "actionExit"));
        pnlButtons.add(btnExit);
        add(pnlButtons, UI.BorderLayout.SOUTH);

        // Show the username and password hint if there are no animals
        // in the database - doesn't matter hinting if we aren't
        // protecting anything.
        // Having the hint needs more space, so we make the form slightly
        // larger.
        try {
            if (DBConnection.executeForCount("SELECT COUNT(*) FROM animal") == 0) {
                pnlCenter.add(UI.getHintLabel(i18n("default_username_password_hint")),
                    UI.BorderLayout.CENTER);
                this.setSize(UI.getDimension(436, 406));
            } else {
                this.setSize(UI.getDimension(436, 376));
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
            this.setSize(UI.getDimension(436, 376));
        }

        // Set default button
        this.getRootPane().setDefaultButton(btnLogin);

        // Center window and display
        UI.centerWindow(this);

        setVisible(true);
        Dialog.theParent = this;
    }

    public void actionExit() {
        windowCloseAttempt();
    }

    public void actionLogin() {
        boolean passOk = false;

        // Some people have experienced problems with dropped connections
        // at this point, so we might as well make sure it's fresh on
        // the way in:
        try {
            DBConnection.con.close();
            DBConnection.con = null;
            DBConnection.getConnection();
        } catch (Exception e) {
        }

        // Create a new users object to test against
        Users users = new Users();
        users.openRecordset("");

        try {
            // We scan the userlist, rather than doing a query to 1: Reduce SQL
            // injection attacks and 2: Some database LIKE operators aren't case
            // insensitive (which we want usernames to be).
            while (!users.getEOF()) {
                if (users.getUserName().equalsIgnoreCase(txtUsername.getText())) {
                    // Found a matching user in the database, see if the password 
                    // is correct.

                    // Are we using LDAP for authentication?
                    if (!LDAP.isConfigured()) {
                        // No, verify the password against the database
                        if (users.getPassword()
                                     .equals(MD5.hash(
                                        new String(txtPassword.getPassword())))) {
                            passOk = true;
                        }
                    } else {
                        // Use LDAP to authenticate the user/password
                        if (LDAP.authenticate(txtUsername.getText(),
                                    txtPassword.getText())) {
                            passOk = true;
                        }
                    }

                    // Whether the password was right or not, might as well stop
                    // looking now since we found the user.
                    break;
                }

                users.moveNext();
            }

            if (!passOk) {
                // Tell the user it was bad.
                Dialog.showError(Global.i18n("uilogin",
                        "The_username_and/or_password_you_supplied_were_not_recognised."),
                    Global.i18n("uilogin", "Invalid_Password"));
                users = null;
            } else {
                // Login was successful - grab that single
                // user object to conserve resources
                Users user = new Users();
                user.openRecordset("ID = " + users.getID());
                openMainForm(user);
                users = null;
                user = null;
            }
        } catch (Exception ex) {
            // Dump a stack trace to the log
            Global.logException(ex, Login.class);

            // Show the error to the user
            Dialog.showError(Global.i18n("uilogin",
                    "An_error_occurred_obtaining_a_connection_to_the_database_-_") +
                ex.getMessage(), Global.i18n("uilogin", "Error"));
        }
    }
}
