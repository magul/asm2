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
package net.sourceforge.sheltermanager.asm.db;

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.*;

import java.net.*;

import java.sql.Connection;

import java.util.*;


/**
 * This class is responsible for finding an ASM database server, reading the
 * location of an existing one from a file and prompting the user.
 *
 * @author Robin Rawson-Tetley
 */
public class LocateDatabase {
    private static String propFileName = Global.tempDirectory + File.separator +
        "jdbc.properties";
    private String jdbcURL = null;
    private boolean justCreatedLocal = false;

    /** Creates a new instance of LocateDatabase */
    public LocateDatabase() {
        // Check we have a local database
        checkLocalDatabase();

        // If we just created the local database, it's a new install - switch
        // straight to it
        if (justCreatedLocal) {
            Global.logInfo("First time use, defaulting to local database.",
                "LocateDatabase.LocateDatabase");
            jdbcURL = "jdbc:hsqldb:file:" + Global.tempDirectory +
                File.separator + "localdb";
            saveJDBCUrlToConfig();

            return;
        }

        // Read the db config
        Global.logInfo("Attempting to read JDBC URL from configuration file",
            "LocateDatabase.LocateDatabase");
        readJDBCUrlFromConfig();

        // If we're using a local database and its locked, switch
        // to use HSQL client on the localhost (this is to prevent users
        // who hit the "share my data" button not being able to access
        // their data).
        if (jdbcURL != null) {
            checkForLockedLocal();
        }

        // If we couldn't find the database, ask
        // the user where it is.
        if (jdbcURL == null) {
            Global.logInfo("Could not find a database server, prompting user.",
                "LocateDatabase.LocateDatabase");
            askUserForDatabase();
            Global.logInfo("User selected: " + jdbcURL,
                "LocateDatabase.LocateDatabase");
        }
    }

    public String getJDBCURL() {
        return jdbcURL;
    }

    public void saveJDBCUrlToConfig() {
        saveJDBCUrl(jdbcURL);
    }

    public static void saveJDBCUrl(String url) {
        try {
            Global.logInfo("Saving new JDBC URL: " + url,
                "LocateDatabase.saveJDBCUrlToConfig");

            File props = new File(propFileName);
            FileOutputStream out = new FileOutputStream(props);
            Properties p = new Properties();
            p.setProperty("JDBCURL", url);
            p.store(out, null);
            out.flush();
            out.close();
        } catch (Exception ex) {
        }
    }

    public void readJDBCUrlFromConfig() {
        // Read it from the file to the local jdbcURL
        try {
            // Get the properties file
            File props = new File(propFileName);

            // No file - no URL!
            if (!props.exists()) {
                jdbcURL = null;

                return;
            }

            Global.logInfo("Loading JDBC URL from: " + propFileName,
                "LocateDatabase.readJDBCUrlFromConfig");

            FileInputStream in = new FileInputStream(props);
            Properties p = new Properties();
            p.load(in);

            jdbcURL = p.getProperty("JDBCURL");

            in.close();
        } catch (Exception ex) {
            Global.logError("Failed to read from config file: " +
                ex.getMessage(), "LocateDatabase.readJDBCUrlFromConfig");
            ex.printStackTrace();
        }
    }

    public void askUserForDatabase() {
        jdbcURL = Dialog.getJDBCUrl(Global.i18n("db", "locate_database"));

        // if the user cancelled, we fall back to the default database.
        if (jdbcURL.equals("")) {
            jdbcURL = "jdbc:hsqldb:file:" + Global.tempDirectory +
                File.separator + "localdb";
        }

        saveJDBCUrlToConfig();
    }

    public static void switchDatabase() {
        String url = Dialog.getJDBCUrl(Global.i18n("uimain", "switch_database"));

        // if the user cancelled, we do nothing
        if (url.equals("")) {
            return;
        }

        saveJDBCUrl(url);

        Dialog.showInformation(Global.i18n("uimain",
                "switch_database_asm_restart"),
            Global.i18n("uimain", "switch_database"));
    }

    /** Checks for a local jdbc URL and switches it to
     *  localhost if the database is locked - this prevents people who
     *  just shared their local database over the network being
     *  locked out
     */
    public void checkForLockedLocal() {
        // Bail out if it's not a local database
        if (jdbcURL.indexOf("jdbc:hsqldb:file") == -1) {
            return;
        }

        String lock = Global.tempDirectory + File.separator + "localdb.lck";

        Global.logDebug("Checking for locked local database at " + lock,
            "LocateDatabase.checkForLockedLocal");

        try {
            DBConnection.url = jdbcURL;
            DBConnection.con = null;
            DBConnection.getConnection();
            DBConnection.con.close();
        } catch (Exception e) {
            // It's locked, switch it for this session
            Global.logInfo("Local database is locked, assuming share and trying hsql/localhost",
                "LocateDatabase.checkForLockedLocal");
            jdbcURL = "jdbc:hsqldb:hsql://localhost/asm";
        }
    }

    /** Verify that the local database exists and create it if not */
    public void checkLocalDatabase() {
        // Do we have a TEXT table style local database that we're phasing out?
        // Convert it if it is.
        File f = new File(Global.tempDirectory + File.separator +
                "localdb.animal.csv");

        if (f.exists()) {
            convertTextDatabase();

            return;
        }

        // Do we already have a local database? Don't create one if we do
        f = new File(Global.tempDirectory + File.separator + "localdb.script");

        if (f.exists()) {
            return;
        }

        // Connect to the new database
        DBConnection.url = "jdbc:hsqldb:file:" + Global.tempDirectory +
            File.separator + "localdb";
        DBConnection.con = null;
        DBConnection.getConnection();

        try {
            // Load the schema for the new local database
            DBConnection.executeFile(new File(Global.dataDirectory +
                    File.separator + "sql" + File.separator + "hsqldb.sql"));

            // Do we have a translation patch to apply?
            applyTranslationPatch();

            // Close the connection to the database afterwards, since
            // we could be switching elsewhere and back again
            DBConnection.con.close();
            DBConnection.con = null;

            // Note that we just created it, so we can default to it for
            // first time users
            justCreatedLocal = true;
        } catch (Exception e) {
            Global.logError("Failed creating local db: " + e.getMessage(),
                "LocateDatabase.askUserForDatabase");
            Global.logException(e, getClass());
        }
    }

    /**
     * Finds any HSQLDB, MySQL or PostgreSQL (in that order) on the
     * local subnet and returns a string array containing the
     * ip (0) and type (1) as a number where 1 = mysql, 2 = postgresql, 3 = hsqldb
     * both elements will be null if no database was found
     **/
    public static String[] scanDatabase() {
        String[] rv = new String[2];

        // Get local IP
        String local = "";
        boolean hasValidLan = true;

        try {
            local = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            // An error occurred trying to obtain an IP address for
            // the local machine - use the loopback adapter.
            Global.logWarning("Could not find a network device. Scanning loopback.",
                "LocateDatabase.scanDatabase");
            hasValidLan = false;
        }

        Global.logDebug("This machine's IP address was detected as " + local,
            "LocateDatabase.scanDatabase");

        // Work out if there actually is a LAN based on the IP address
        if (local.equals("127.0.0.1")) {
            Global.logInfo("No local area network discovered.",
                "LocateDatabase.scanDatabase");
            hasValidLan = false;
        }

        // Verify the network is one of the 3 private netblocks
        if (local.startsWith("192.168.") || local.startsWith("10.") ||
                local.startsWith("172.16.")) {
            // It's a "proper" private network - that's
            // good.
        } else {
            Global.logInfo("The local IP of " + local +
                " is not one of the private netblocks.",
                "LocateDatabase.scanDatabase");
            hasValidLan = false;
        }

        // Scan the subnet
        boolean foundOne = false;
        final int MYSQL_PORT = 3306;
        final String MYSQL_TYPE = "1";
        final int POSTGRESQL_PORT = 5432;
        final String POSTGRESQL_TYPE = "2";
        final int HSQLDB_PORT = 9001;
        final String HSQLDB_TYPE = "3";

        Socket sc = null;

        // Pre-generate a list of addresses to scan, starting
        // with the loopback adapter
        String network = local.substring(0, local.lastIndexOf(".")) + ".";
        Vector v = new Vector(256);
        v.add("127.0.0.1");

        // Add the subnet range to scan if the LAN was valid
        if (hasValidLan) {
            for (int i = 1; i < 255; i++)
                v.add(network + i);
        }

        // Scan them
        for (int i = 0; i < v.size(); i++) {
            String testIP = v.get(i).toString();

            // Check for HSQLDB
            try {
                Global.logDebug("Scanning " + testIP + ":" + HSQLDB_PORT,
                    "LocateDatabase.scanDatabase");

                SocketAddress socketAddress = new InetSocketAddress(testIP,
                        HSQLDB_PORT);
                sc = new Socket();
                sc.connect(socketAddress, 500);

                rv[0] = testIP;
                rv[1] = HSQLDB_TYPE;

                return rv;
            } catch (Exception e) {
            }

            // Check for MySQL
            try {
                Global.logDebug("Scanning " + testIP + ":" + MYSQL_PORT,
                    "LocateDatabase.scanDatabase");

                SocketAddress socketAddress = new InetSocketAddress(testIP,
                        MYSQL_PORT);
                sc = new Socket();
                sc.connect(socketAddress, 500);

                rv[0] = testIP;
                rv[1] = MYSQL_TYPE;

                return rv;
            } catch (Exception e) {
            }

            // Check for PostgreSQL
            try {
                Global.logDebug("Scanning " + testIP + ":" + POSTGRESQL_PORT,
                    "LocateDatabase.scanDatabase");

                SocketAddress socketAddress = new InetSocketAddress(testIP,
                        POSTGRESQL_PORT);
                sc = new Socket();
                sc.connect(socketAddress, 500);

                rv[0] = testIP;
                rv[1] = POSTGRESQL_TYPE;

                return rv;
            } catch (Exception e) {
            }
        }

        return rv;
    }

    /**
     * Checks the locale and runs any translation patches against
     * the current database.
     */
    public void applyTranslationPatch() {
        String lang = Locale.getDefault().getLanguage();

        try {
            // Don't bother for english
            if (lang.equals("en")) {
                return;
            }

            Global.logInfo("Applying translation patch for language '" + lang +
                "'", "LocateDatabase.applyTranslationPatch");

            DBConnection.executeFile(new File(Global.dataDirectory +
                    File.separator + "sql" + File.separator + "translate_" +
                    lang + ".sql"));
        } catch (Exception e) {
            Global.logError("Failed to apply translation patch for language '" +
                lang + "'", "LocateDatabase.applyTranslationPatch");
            Global.logException(e, getClass());
        }
    }

    /**
     * Converts a TEXT database to whatever we're using now by
     * creating a new database and importing the old one into it,
     * then renaming it back.
     */
    public void convertTextDatabase() {
        Dialog.showInformation("ASM needs to convert your TEXT local database to MEMORY. Hit Ok and please wait.",
            "Conversion Required");
        Global.logInfo("Converting TEXT style HSQLDB to MEMORY...",
            "LocateDatabase.convertTextDatabase");

        String newurl = "jdbc:hsqldb:file:" + Global.tempDirectory +
            File.separator + "newdb";
        String oldurl = "jdbc:hsqldb:file:" + Global.tempDirectory +
            File.separator + "localdb";
        String olddb = Global.tempDirectory + File.separator + "olddb";

        // Connect to the new database
        DBConnection.url = newurl;
        DBConnection.con = null;
        DBConnection.getConnection();

        try {
            // Load the schema for the new local database
            DBConnection.executeFile(new File(Global.dataDirectory +
                    File.separator + "sql" + File.separator + "hsqldb.sql"));

            // Do we have a translation patch to apply?
            applyTranslationPatch();
        } catch (Exception e) {
            Global.logError("Failed creating local db: " + e.getMessage(),
                "LocateDatabase.convertTextDatabase");
            Global.logException(e, getClass());
        }

        // Open a connection to the old database and then
        // import the data from it, closing afterwards
        try {
            Connection c = DBConnection.getConnection(oldurl);
            DatabaseImporter imp = new DatabaseImporter();
            imp.start(oldurl, c, DBConnection.HSQLDB, true, true);
            c.close();
            c = null;
            // Wait for lock to release
            Thread.sleep(1000);
        } catch (Exception e) {
            Global.logError("Failed importing from old db",
                "LocateDatabase.convertTextDatabase");
            Global.logException(e, getClass());
        }

        try {
            // Close the connection to the new database and
            // make sure all data is written.
            DBConnection.checkpoint();
            DBConnection.con.close();
            DBConnection.con = null;
            // Wait for lock to release
            Thread.sleep(1000);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Backup the old database files and rename the new ones
        try {
            // Create a new folder called olddb
            Global.logInfo("Creating .asm/olddb folder",
                "LocateDatabase.convertTextDatabase");

            File olddir = new File(olddb);
            olddir.mkdir();

            // Find all files prefixed localdb and move them into olddb
            Global.logInfo("Renaming localdb files into olddb folder",
                "LocateDatabase.convertTextDatabase");

            File tempdir = new File(Global.tempDirectory);
            String[] contents = tempdir.list();

            for (int i = 0; i < contents.length; i++) {
                if (contents[i].startsWith("localdb.")) {
                    String old = Global.tempDirectory + File.separator +
                        contents[i];
                    String nu = olddb + File.separator + contents[i];
                    File oldfile = new File(old);
                    File newfile = new File(nu);
                    Global.logInfo("Rename: " + old + " to " + nu,
                        "LocateDatabase.convertTextDatabase");

                    if (!oldfile.renameTo(newfile)) {
                        Global.logError("Failed renaming " + old + " to " + nu,
                            "LocateDatabase.convertTextDatabase");
                    }
                }
            }

            // Rename newdb prefixed files to localdb
            Global.logInfo("Renaming newdb files to localdb",
                "LocateDatabase.convertTextDatabase");
            contents = tempdir.list();

            for (int i = 0; i < contents.length; i++) {
                if (contents[i].startsWith("newdb.")) {
                    String old = Global.tempDirectory + File.separator +
                        contents[i];
                    String nu = Global.tempDirectory + File.separator +
                        "localdb" +
                        contents[i].substring(contents[i].indexOf("."));
                    File oldfile = new File(old);
                    File newfile = new File(nu);
                    Global.logInfo("Rename: " + old + " to " + nu,
                        "LocateDatabase.convertTextDatabase");

                    if (!oldfile.renameTo(newfile)) {
                        Global.logError("Failed renaming " + old + " to " + nu,
                            "LocateDatabase.convertTextDatabase");
                    }
                }
            }

            Global.logInfo("Conversion complete. ASM needs to restart.",
                "LocateDatabase.convertTextDatabase");
            Dialog.showInformation("TEXT->MEMORY Conversion complete. ASM will now exit. Note that on your next restart, you can safely ignore any messages about update errors.",
                "ASM Exiting");
            Thread.sleep(5000);
            System.exit(0);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
