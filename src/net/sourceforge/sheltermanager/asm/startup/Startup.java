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
package net.sourceforge.sheltermanager.asm.startup;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Users;
import net.sourceforge.sheltermanager.asm.db.AutoDBUpdates;
import net.sourceforge.sheltermanager.asm.db.LocateDatabase;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.localcache.LocalCache;
import net.sourceforge.sheltermanager.asm.ui.login.Login;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMApplet;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMWindow;
import net.sourceforge.sheltermanager.asm.ui.ui.CustomUI;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.FlexibleFocusManager;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.Throbber;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Locale;
import java.util.Properties;
import java.util.Vector;


/**
 * This class contains the program entry point and startup code.
 *
 * @see net.sourceforge.sheltermanager.asm.ui.login.Login
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class Startup implements Runnable {
    public static boolean applet = false;
    public static ASMApplet appletHandle = null;
    public static String appletUser = null;
    private String[] args = null;
    private String jdbcurl = null;
    private StartupProgress sp = null;

    public Startup(String[] args) {
        this.args = args;
    }

    public Startup(String[] args, String jdbcurl, String user, ASMApplet a) {
        this.args = args;
        this.jdbcurl = jdbcurl;
        this.applet = true;
        this.appletHandle = a;
        this.appletUser = user;
        Global.applet = true;
        Global.appletHandle = a;
        Global.appletUser = user;
    }

    public static void terminateVM() {
        if (!applet) {
            System.exit(0);
        }
    }

    public static void terminateVM(int returncode) {
        if (!applet) {
            System.exit(returncode);
        }
    }

    public static void terminateVM(boolean halt) {
        if (!applet) {
            if (halt) {
                Runtime.getRuntime().halt(-1);
            } else {
                System.exit(0);
            }
        }
    }

    public void run() {
        try {
            // Should run on UI thread to load window before we start
            if (!applet) {
                UI.invokeAndWait(new Runnable() {
                        public void run() {
                            sp = new StartupProgress();
                            sp.init("ASM",
                                IconManager.getIcon(IconManager.SCREEN_MAIN),
                                "", false);
                        }
                    });
            } else {
                sp = new StartupProgress(appletHandle);
            }

            String tempDir = System.getProperty("user.home") + File.separator +
                ".asm";
            String dataDir = "";

            // Applet assumes nothing needed from data directory
            // as it will all be in the database
            if (!applet) {
                // No arguments specified - we need our data directory
                if (args.length == 0) {
                    Dialog.showError(
                        "ASM requires a path to the data directory to be passed.");
                    Global.logError("ASM requires a path to the data directory to be passed.",
                        "Startup");
                    terminateVM(1);
                }

                // We have a data directory
                if (args.length == 1) {
                    dataDir = args[0];
                }

                // This is an old style start script which has JDBC URL then
                // temp (data) directory. Use the 2nd argument as the temp directory
                if (args.length == 2) {
                    dataDir = args[1];
                }

                // Set the data folder, making sure it doesn't end with a path separator
                if (dataDir.endsWith(File.separator)) {
                    Global.dataDirectory = dataDir.substring(0,
                            dataDir.length() - 1);
                } else {
                    Global.dataDirectory = dataDir;
                }

                // Somehow, some Windows boxes end up with a superfluous quotation
                // mark that screws things up:
                Global.dataDirectory = Utils.replace(Global.dataDirectory,
                        "\"", "");
            }

            // Assign the global temp directory
            Global.tempDirectory = tempDir;

            // Make sure it exists (create if not)
            Global.checkTempDir();

            // Set maximum steps to startup (3 are skipped in applet mode)
            if (applet) {
                sp.setMax(17);
            } else {
                sp.setMax(20);
            }

            // Set the log going
            sp.setStatus("Starting Log...");
            sp.incrementBar();
            Global.checkLog();

            // Read info from the config file
            sp.setStatus("Reading Settings...");
            sp.incrementBar();
            readSettings();

            // Check whether we need to ask the user for their locale (ASK is default
            // for first run)
            sp.setStatus("Checking Locale...");
            sp.incrementBar();
            checkLocale();

            // Set the locale specific versions of the Product Name, Version
            // and Copyright
            sp.setStatus("Setting product information");
            Global.setProduct();

            // Check for a lock flag if only a single instance is allowed
            if (!applet) {
                sp.setStatus("Checking for other ASM instance...");
                sp.incrementBar();
                checkForOtherInstance();
            }

            // Set the currency symbol and date formats from
            // the current locale
            sp.setStatus("Getting date/time and currency from locale...");
            Global.setDateCurrencyFromLocale();

            // Welcome message
            sp.setStatus("Starting system log");
            Global.welcomeLog();

            // Load database drivers
            sp.setStatus("Loading Database drivers");
            sp.incrementBar();

            if (!applet) {
                // all for non applets
                DBConnection.loadJDBCDrivers();
            } else {
                // just the correct one for applets based on 
                // JDBC URL supplied to us
                if (jdbcurl.indexOf("postgres") != -1) {
                    DBConnection.loadJDBCDrivers(true, false, false);
                } else if (jdbcurl.indexOf("mysql") != -1) {
                    DBConnection.loadJDBCDrivers(false, true, false);
                } else if (jdbcurl.indexOf("hsqldb") != -1) {
                    DBConnection.loadJDBCDrivers(false, false, true);
                }
            }

            // Find the database from the jdbcurl.properties file
            // (for standalone installs)
            if (!applet) {
                sp.setStatus("Locating database...");
                sp.incrementBar();

                // Get the database
                LocateDatabase ldb = new LocateDatabase();

                // Set the JDBC URL in the cursor engine.
                Global.logDebug("Setting database URL: " + ldb.getJDBCURL(),
                    "Startup.run");
                DBConnection.url = ldb.getJDBCURL();
                ldb = null;
            } else {
                // Just use the URL we were given
                sp.setStatus("Using applet JDBC URL");
                DBConnection.url = jdbcurl;
            }

            // Make sure the connection is valid
            sp.setStatus("Testing database connection...");
            sp.incrementBar();

            if (!DBConnection.getConnection()) {
                Dialog.showError(
                    "A connection to the database server could not be made.\nThe error was: " +
                    DBConnection.lastError);
                Global.logError(
                    "A connection to the database server could not be made.\nThe error was: " +
                    DBConnection.lastError, "Startup.Startup");
                clearLock();
                terminateVM(1);
            }

            // Now do a quick speed test - take two samples and average
            long result1 = System.currentTimeMillis();
            SQLRecordset st = new SQLRecordset();
            st.openRecordset("SELECT * FROM animal WHERE ID = 0", "animal");
            st.free();
            result1 = System.currentTimeMillis() - result1;

            long result2 = System.currentTimeMillis();
            st = new SQLRecordset();
            st.openRecordset("SELECT * FROM animal WHERE ID = 0", "animal");
            st.free();
            result2 = System.currentTimeMillis() - result2;
            Global.speedTest = result1 + (result2 / 2);

            Global.logInfo("Database speed test (av. 2 samples): " +
                Global.speedTest + "ms", "Startup.Startup");

            // Verify that we actually have an ASM database
            sp.setStatus("Verifying ASM database...");
            sp.incrementBar();

            try {
                SQLRecordset rs = new SQLRecordset();
                rs.openRecordset("SELECT COUNT(*) FROM animal", "animal");
                rs.free();
                rs = null;
                Global.logInfo("Found what looks to be a valid ASM database.",
                    "Startup.Startup");
            } catch (Exception e) {
                String msg = Global.i18n("db", "asmdatastructuremissing",
                        Global.dataDirectory + File.separator + "sql");
                Dialog.showError(msg);
                Global.logError(msg, "Startup.Startup");
                clearLock();
                terminateVM(1);
            }

            // Update the max packet size so big media files don't upset
            // MySQL (if we're using MySQL that is)
            sp.setStatus("Setting maximum packet size...");
            sp.incrementBar();
            Global.setMaxAllowedPacket();

            // Switch to UTF8 mode if the server needs it
            sp.setStatus("Switching to UTF8 input/output...");
            Global.setUTF8();

            // Check the screen resolution
            Global.screenResAbove1024 = UI.getScreenSize().width > 1024;

            // Initialise the local file cache
            sp.setStatus("Initialising local cache...");
            sp.incrementBar();
            Global.localCache = new LocalCache();

            // Initialise the File Types manager
            sp.setStatus("Discovering helper applications - please wait...");
            sp.incrementBar();
            FileTypeManager.initManager();

            // Apply our custom focus manager
            Global.focusManager = new FlexibleFocusManager();
            UI.setFocusManager(Global.focusManager);

            // Make sure our client is upto date and is either newer or the
            // same as the database version
            sp.setStatus("Checking ASM database version...");
            sp.incrementBar();

            if (!AutoDBUpdates.checkDatabaseVersion()) {
                String dbVer = Configuration.getString("DatabaseVersion");
                Dialog.showError(
                    "Your database is newer than this client and will not work correctly with it. You must update your client to version " +
                    dbVer + " to continue.");
                Global.logError(
                    "Your database is newer than this client and will not work correctly with it.\nYou must update your client to version " +
                    dbVer + " to continue.", "Startup");
                clearLock();
                terminateVM(1);
            }

            // Run any outstanding database updates
            sp.setStatus("Updating database...");
            sp.incrementBar();

            if (!new AutoDBUpdates().runUpdates()) {
                clearLock();
                terminateVM(1);
            }

            // See if we need to import the media from the data directory
            if (!applet) {
                sp.setStatus("Creating DBFS...");
                sp.incrementBar();
                checkMedia();
            }

            // If we're using OS-level security, we'd better check there's a
            // matching
            // ASM user.
            if (!checkOSUser()) {
                Dialog.showError(
                    "Your operating system user does not have a matching ASM user.");
                Global.logError("No matching user found for OS user " +
                    System.getProperty("user.name"), "Login.autoLogUserIn");
                clearLock();
                terminateVM(1);
            }

            // Download default .asm files from dbfs
            sp.setStatus("Download default files...");
            sp.incrementBar();
            downloadDefaultFiles();

            // Load any custom buttons/menu items
            sp.setStatus("Loading custom buttons...");
            sp.incrementBar();
            loadCustomButtons();

            // Fill the local data cache
            sp.setStatus("Filling lookup cache...");
            sp.incrementBar();
            LookupCache.fill();

            // Update variable data
            sp.setStatus("Updating variable animal data...");
            sp.incrementBar();
            Animal.updateOnShelterVariableAnimalData();

            // Automatically cancel old reservations
            sp.setStatus("Auto cancelling reservations...");
            sp.incrementBar();
            Adoption.autoCancelReservations();

            // Preload some classes
            sp.setStatus("Preloading UI classes...");
            sp.incrementBar();
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.animal.AnimalFind");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.animal.AnimalFindText");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.animal.DietSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.animal.VaccinationSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.diary.DiarySelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.log.LogSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.medical.MedicalSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.movement.MovementSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.movement.MovementEdit");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.DonationSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.LinkSelector");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.OwnerFind");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.OwnerFindText");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink");
            Class.forName(
                "net.sourceforge.sheltermanager.asm.ui.owner.VoucherSelector");

            // Login
            sp.dispose();
            new Login();
        } catch (Exception e) {
            Global.logException(e, Startup.class);
        }
    }

    /**
     * Application entry point
     */
    public static void main(final String[] args) {
        UI.disableRendererMessages();
        new Startup(args).run();
    }

    /**
     * Check to see whether the DBFS is empty, and if so, import the contents of
     * the media directory in the data dir.
     */
    public void checkMedia() {
        if (DBFS.isEmpty()) {
            Global.logInfo("You do not have a DBFS, I'm going to import the default.",
                "Startup.checkMedia");
            DBFS.importFromFileSystem(new File(Global.dataDirectory +
                    File.separator + "media"));
        } else {
            Global.logDebug("DBFS found.", "Startup.checkMedia");
        }
    }

    /**
     * Verifies whether there is a matching ASM user for the OS user (and OS
     * security is on).
     *
     * @return true if we're ok to go to the login screen, or false to fail.
     */
    public boolean checkOSUser() {
        try {
            if (Configuration.getBoolean("AutoLoginOSUsers")) {
                Users u = new Users();
                u.openRecordset("UserName Like '" +
                    System.getProperty("user.name") + "'");

                return !u.getEOF();
            } else {
                return true;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());

            return false;
        }
    }

    /**
     * Checks if ASM is only allowing one instance to be open. If it is, tests
     * for a ~/.asm/lock file. If that exists, it reports an error and closes.
     */
    public static void checkForOtherInstance() {
        // Locking to a single instance?
        if (!Global.oneInstance) {
            Global.logDebug("Not using single instance lock.",
                "Startup.checkForOtherInstance");

            return;
        }

        // Is there a lock file?
        File f = new File(Global.tempDirectory + File.separator + "lock");

        if (f.exists()) {
            Global.logDebug("Found lock file, aborting.",
                "Startup.checkForOtherInstance");
            // Yep, better bail out
            Global.logError(
                "There is already another instance of ASM open or a dead " +
                "lock file hanging around at" + f.getAbsolutePath(),
                "Startup.checkForOtherInstance");
            Dialog.showError("There is already another instance of ASM open.");
            terminateVM(1);
        }

        // No, write the lock file
        try {
            Global.logDebug("Using single instance lock. Writing: " +
                f.getAbsolutePath(), "Startup.checkForOtherInstance");

            FileOutputStream o = new FileOutputStream(f);
            o.write("lock".getBytes());
            o.flush();
            o.close();
        } catch (Exception e) {
            Global.logException(e, Startup.class);
        }
    }

    /** Removes the instance lock file */
    public static void clearLock() {
        Global.logDebug("Removing instance lock file.", "ShutdownThread.Run");

        File f = new File(Global.tempDirectory + File.separator + "lock");

        if (f.exists()) {
            f.delete();
        }
    }

    public static void loadCustomButtons() {
        Properties p = new Properties();
        File f = new File(Global.tempDirectory + File.separator +
                "custom.properties");

        if (!f.exists()) {
            Global.logDebug("No custom.properties file found.",
                "Startup.loadCustomButtons");

            return;
        } else {
            Global.logDebug("Loading custom buttons from " +
                f.getAbsolutePath(), "Startup.loadCustomButtons");
        }

        try {
            FileInputStream in = new FileInputStream(f);
            p.load(in);
            CustomUI.readCustomAnimalButtons(p);
            CustomUI.readCustomOwnerButtons(p);
            CustomUI.readCustomMovementButtons(p);
            CustomUI.readCustomMenu(p);
            in.close();
        } catch (Exception e) {
            Global.logException(e, Startup.class);
        }
    }

    /**
     * Looks in the DBFS directory /dotasm and downloads any files
     * to the local machine's .asm folder - good for distributing
     * systemwide custom.properties and things
     */
    public static void downloadDefaultFiles() {
        try {
            Global.logInfo("Downloading default .asm files from dbfs",
                "downloadDefaultFiles");

            DBFS d = new DBFS();

            if (!d.exists("dotasm")) {
                Global.logInfo("No default files found in dbfs.",
                    "downloadDefaultFiles");

                return;
            }

            d.chdir("dotasm");

            String[] l = d.list();

            if (l.length > 0) {
                Global.logInfo("Found " + l.length +
                    " default files in dbfs, downloading...",
                    "downloadDefaultFiles");
            }

            for (int i = 0; i < l.length; i++) {
                Global.logDebug("Downloading new .asm file: " + l[i],
                    "downloadDefaultFiles");
                d.readFile(l[i]);
            }
        } catch (Exception e) {
            Global.logException(e, Startup.class);
        }
    }

    public static void checkLocale() {
        if (Global.settings_Locale.equals("ASK")) {
            // We need to ask for the locale
            String locale = Dialog.getLocale();

            try {
                Locale.setDefault(new Locale(locale.substring(0,
                            locale.indexOf("_")),
                        locale.substring(locale.indexOf("_") + 1,
                            locale.length())));
                Global.settings_Locale = locale;
            } catch (Exception e) {
                Global.logError("An error occurred switching to locale '" +
                    locale + "':\n" + e.getMessage(), "Startup.checkLocale");
                locale = Locale.getDefault().toString();
                Global.logInfo("The default locale of " + locale +
                    " will be used instead.", "Startup.checkLocale");
            }

            // Save a new asm.properties - the file couldn't have 
            // existed for us to be asking for a locale
            try {
                String s = "Locale=" + locale + "\n";
                File file = new File(Global.tempDirectory + File.separator +
                        "asm.properties");
                FileOutputStream out = new FileOutputStream(file);
                out.write(s.getBytes(Global.CHAR_ENCODING));
                out.flush();
                out.close();
            } catch (Exception e) {
                Global.logException(e, Startup.class);
            }
        }
    }

    /**
     * Reads the configuration file and stores settings required. No error is
     * thrown if the config file does not exist - it does not have to and values
     * will be defaulted otherwise.
     */
    public static void readSettings() {
        FileInputStream in = null;

        try {
            Properties p = new Properties();
            File file = new File(Global.tempDirectory + File.separator +
                    "asm.properties");

            if (file.exists()) {
                in = new FileInputStream(file);
                p.load(in);
            }

            // Whether or not to start maximised
            // =========================================================
            Global.startMaximised = p.getProperty("StartMaximised", "false")
                                     .equalsIgnoreCase("true");

            // Label alignment for screens with grid layouts
            // =========================================================
            String alignstring = p.getProperty("LabelAlignment", "0");

            if (alignstring.equals("0")) {
                Global.GRIDLABELALIGN = UI.ALIGN_RIGHT;
            } else {
                Global.GRIDLABELALIGN = UI.ALIGN_LEFT;
            }

            // Whether to use button accelerators
            // =========================================================
            Global.buttonHotkeys = p.getProperty("ButtonHotkeys", "false")
                                    .equalsIgnoreCase("true");

            // TabAlignment - can be TOP, BOTTOM, LEFT, RIGHT
            // =========================================================
            alignstring = p.getProperty("TabAlignment", "0");

            if (alignstring.equals("1")) {
                Global.TABALIGN = UI.TabbedPane.BOTTOM;
            } else if (alignstring.equals("2")) {
                Global.TABALIGN = UI.TabbedPane.LEFT;
            } else if (alignstring.equals("3")) {
                Global.TABALIGN = UI.TabbedPane.RIGHT;
            } else {
                Global.TABALIGN = UI.TabbedPane.TOP;
            }

            // Internal reportviewer setting
            // =========================================================
            Global.useInternalReportViewer = p.getProperty("InternalReportViewer",
                    "true").equalsIgnoreCase("true");

            // Only allow one instance
            // ==========================================================
            Global.oneInstance = p.getProperty("OnlyAllowOneInstance", "false")
                                  .equalsIgnoreCase("true");

            // Display updates from website
            // ==========================================================
            Global.showUpdates = p.getProperty("ShowUpdates", "true")
                                  .equalsIgnoreCase("true");

            // Autologout
            // =========================================================
            Global.autoLogout = Integer.parseInt(p.getProperty("AutoLogout", "0"));

            // HeartbeatInterval
            // =========================================================
            Global.heartbeatInterval = Integer.parseInt(p.getProperty(
                        "HeartbeatInterval", "30000"));

            // Video capture method
            // =========================================================
            Global.videoCaptureMethod = Integer.parseInt(p.getProperty(
                        "VideoCaptureMethod", "0"));

            // Video capture command/url
            // =========================================================
            Global.videoCaptureCommand = p.getProperty("VideoCaptureCommand", "");

            // Toolbar size (0 = off, 16, 24, 32 - default=32)
            Global.toolbarSize = Integer.parseInt(p.getProperty("ToolbarSize",
                        "32"));

            // Skin
            // =========================================================
            int skin = Integer.parseInt(p.getProperty("Skin", "1"));
            Global.skin = skin;
            UI.swingSetLAF(skin);

            // Locale
            // =========================================================
            String locale = p.getProperty("Locale", "ASK");
            Global.settings_Locale = locale;

            if (!locale.equalsIgnoreCase("DETECT") &&
                    !locale.equalsIgnoreCase("ASK")) {
                // Parse the constant and set the
                // system locale accordingly.
                try {
                    String lang = locale.substring(0, locale.indexOf("_"));
                    String country = locale.substring(locale.indexOf("_") + 1,
                            locale.length());
                    Locale.setDefault(new Locale(lang, country));
                } catch (Exception e) {
                    Global.logError("An error occurred switching to locale '" +
                        locale + "':\n" + e.getMessage(), "Startup.readSettings");
                    Global.logInfo("The default locale of " +
                        Locale.getDefault().toString() +
                        " will be used instead.", "Startup.readSettings");
                }
            }
        } catch (Exception e) {
            Global.logError("Problem reading local configuration. Using default values.",
                "Startup.readSettings");
            Global.logException(e, Startup.class);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }
}


class StartupProgress extends ASMWindow {
    private UI.Label status = null;
    private UI.ProgressBar bar = null;
    private Throbber throbber = null;
    private ASMApplet appletHandle = null;
    private boolean applet = false;

    public StartupProgress() {
        super();
    }

    public StartupProgress(ASMApplet appletHandle) {
        this.appletHandle = appletHandle;
        applet = true;
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.Panel progress = UI.getPanel(UI.getGridLayout(0, 1));
        UI.Label lblSplash = UI.getSplashLabel();
        lblSplash.setHorizontalAlignment(UI.Label.CENTER);
        add(lblSplash, UI.BorderLayout.NORTH);

        status = UI.getLabel("");
        bar = UI.getProgressBar();
        throbber = new Throbber();
        progress.add(throbber);
        progress.add(bar);
        progress.add(status);
        add(progress, UI.BorderLayout.SOUTH);

        this.setSize(UI.getDimension(400, 340));
        UI.centerWindow(this);
        Dialog.theParent = this;
        setVisible(true);
        throbber.start();
    }

    public void dispose() {
        if (!applet) {
            throbber.stop();
            super.dispose();
        }
    }

    public void setMax(int i) {
        if (!applet) {
            bar.setMaximum(i);
        }
    }

    public void incrementBar() {
        if (!applet) {
            bar.setValue(bar.getValue() + 1);
        }
    }

    public void setStatus(String s) {
        if (!applet) {
            status.setText(s);
        }

        Global.logInfo(s, "Startup.setStatus");
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        return true; // Don't allow closing
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return null;
    }

    public Vector getTabOrder() {
        return null;
    }
}
