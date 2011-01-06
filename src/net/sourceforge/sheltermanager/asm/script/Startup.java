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
package net.sourceforge.sheltermanager.asm.script;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;


/**
 * This class contains the startup for
 * any scripts wanting to run commands over ASM.
 * All script commands run non-interactively
 * and no UI is ever presented.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class Startup {
    public final static String usage = "ASM Command Line Interface\nUsage: asmcmd <command> [options]";

    /**
     * Program entry point.
     */
    public Startup(String[] args) {
        // Turn the log off
        Global.setUsingLog(false);

        // Default to a tmp dir in user's home
        String tempDir = System.getProperty("user.home") + File.separator +
            ".asm";

        // Assign the global temp directory
        Global.tempDirectory = tempDir;

        // Make sure it exists (create if not)
        Global.checkTempDir();

        // Read locale info and things from settings
        net.sourceforge.sheltermanager.asm.startup.Startup.readSettings();

        // Set the currency symbol and date formats from
        // the current locale
        Global.setDateCurrencyFromLocale();

        // Set the locale specific versions of the Product Name, Version
        // and Copyright
        Global.setProduct();

        // Load database drivers
        DBConnection.loadJDBCDrivers();

        // Get the database
        File props = new File(Global.tempDirectory + File.separator +
                "jdbc.properties");

        // No file - no URL!
        if (!props.exists()) {
            System.err.println("No jdbc.properties file.");
            System.exit(1);
        }

        // Read it
        String jdbcURL = "";

        try {
            FileInputStream in = new FileInputStream(props);
            Properties p = new Properties();
            p.load(in);
            jdbcURL = p.getProperty("JDBCURL");
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (jdbcURL.equals("")) {
            System.err.println("Blank JDBC URL");
            System.exit(1);
        }

        // Set the JDBC URL in the cursor engine (quietly).
        DBConnection.url = jdbcURL;
        DBConnection.quiet = true;

        // Make sure the connection is valid
        if (!DBConnection.getConnection()) {
            System.err.println(
                "A connection to the database server could not be made.\nThe error was: " +
                DBConnection.lastError);
            System.exit(1);
        }

        // Update the max packet size so big media files don't upset
        // MySQL
        Global.setMaxAllowedPacket();

        // Lets parse that script!
        new ScriptParser(args);
    }

    public static void main(String[] args) {
        new Startup(args);
    }
}
