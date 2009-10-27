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
package net.sourceforge.sheltermanager.asm.globals;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Users;
import net.sourceforge.sheltermanager.asm.ui.localcache.LocalCache;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMApplet;
import net.sourceforge.sheltermanager.asm.ui.ui.FlexibleFocusManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Global constants and values
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public abstract class Global {
    /** The direction to align label components on grid screens */
    public static int GRIDLABELALIGN = UI.ALIGN_RIGHT;

    /** The direction to align tabs on all screens */
    public static int TABALIGN = UI.ALIGN_TOP;

    /** Contains the name of the current system user */
    public static String currentUserName = "";

    /**
     * Contains an object referencing the active current user for security
     * settings
     */
    public static Users currentUserObject = null;

    /** Path to user's ASM temporary directory */
    public static String tempDirectory = "";

    /** Path to the ASM data directory */
    public static String dataDirectory = "";

    /** Path to log file */
    public static String logFile = "";

    /** Handle to log file */
    public static FileOutputStream log = null;

    /** Handle to the main MDI form */
    public static net.sourceforge.sheltermanager.asm.ui.main.Main mainForm = null;

    /** Whether we're running as an applet */
    public static boolean applet = false;
    public static ASMApplet appletHandle = null;
    public static String appletUser = null;

    /** Database speed test result (average round trip for db call in ms) */
    public static long speedTest = 0;

    /** Product version */
    public static String productVersion = "[Missing locale version]";
    public static String version = "0.0.0";

    /** Product name */
    public static String productName = "Animal Shelter Manager";

    /** Product copyright */
    public static String copyrightMessage = "Copyright(c) R.Rawson-Tetley";

    /** Date format to use for userdates */
    public static String dateFormat = "dd/MM/yyyy";

    /** Date format to use for table dates (sorting */
    public static String tableDateFormat = "yyyy-MM-dd";
    public static String tableDateTimeFormat = "yyyy-MM-dd HH:mm";

    /** Date format to use for times */
    public static String timeFormat = "HH:mm";

    /** Date format to use for long display of dates */
    public static String longDateFormat = "EEEE, d MMMM yyyy";

    /** Date format to use for long display of dates and times */
    public static String longDateTimeFormat = "EEEE, d MMMM yyyy HH:mm:ss";

    /** Currency symbol to use */
    public static String currencySymbol = "?";

    /** Reference to local cache object */
    public static LocalCache localCache = null;

    /** Focus management for application */
    public static FlexibleFocusManager focusManager = null;

    /** Whether the program should start up in a maximised window */
    public static boolean startMaximised = true;

    /** Whether ASM should use button hotkeys */
    public static boolean buttonHotkeys = false;

    /** Loaded locale */
    public static String settings_Locale = "ASK";

    /** Video capturing method */
    public static int videoCaptureMethod = 0;
    public final static int CAPTUREMETHOD_VGRABBJ = 0;
    public final static int CAPTUREMETHOD_HTTP = 1;
    public final static int CAPTUREMETHOD_COMMAND = 2;

    /** Video capture url/command */
    public static String videoCaptureCommand = "";

    /**
     * The number of minutes of inactivity before we automatically log a user
     * out. 0 means never logout
     */
    public static int autoLogout = 0;

    /**
     * The interval to make heartbeats to the database in ms. 0 means
     * don't heartbeat.
     */
    public static int heartbeatInterval = 30000;

    /**
     * Use a "soft" gc - the system requests garbage collection whenever you
     * close a form.
     */
    public static boolean settings_useSoftGC = false;

    /**
     * If the machine's screen resolution is greater than 1024x768, then we need
     * to make the child forms larger by a factor of 1.3 so components fit
     * correctly.
     */
    public static boolean screenResAbove1024 = false;

    /** True if the internal report viewer is to be used. */
    public static boolean useInternalReportViewer = true;

    /** True if ASM is to only allow one instance to be open at a time */
    public static boolean oneInstance = false;

    /** True if ASM is to show updates from the website after login */
    public static boolean showUpdates = true;

    /** True if ASM should show system debugging messages in the log */
    public static boolean showDebug = System.getProperty("asm.log.debug",
            "false").equals("true");

    /** Echo log messages to stdout */
    public static boolean echolog = true;

    /**
     * Pivot point at which 2 digit years are changed This could be made into a
     * user configurable option at some point.
     */
    public static int PIVOT_YEAR = 15;

    /** The 2 digit prefix if we are below the pivot */
    public static String BELOW_PIVOT = "20";

    /** The 2 digit prefix if we are after the pivot */
    public static String AFTER_PIVOT = "19";

    /** Whether or not to actually log messages sent */
    private static boolean usingLog = true;
    private static SimpleDateFormat logdf = null;
    public static StringBuffer errors = new StringBuffer();

    /** Returns an array of supported locales */
    public static String[] getSupportedLocales() {
        return new String[] {
            getLanguageCountry("en_US"), getLanguageCountry("en_GB"),
            getLanguageCountry("en_AU"), getLanguageCountry("en_IN"),
            getLanguageCountry("en_ZA"), getLanguageCountry("es_ES"),
	    getLanguageCountry("de_DE"), getLanguageCountry("fr_FR"), 
	    getLanguageCountry("lt_LT"), getLanguageCountry("nl_NL")
        };
    }

    /**
     * Returns a country code for the current locale
     */
    public static String getCountryForCurrentLocale() {
        String cc = Locale.getDefault().getCountry();
        logDebug("Country for current locale: " + cc,
            "Global.getCountryForCurrentLocale");

        return cc;
    }

    /**
     * For a given country code, returns it's position in our
     * list of countries or -1 if not found.
     */
    public static int getCountryIndex(String cc) {
        if ((cc == null) || cc.trim().equals("")) {
            return -1;
        }

        String[] c = getCountries();

        for (int i = 0; i < c.length; i++) {
            if (c[i].toLowerCase().startsWith(cc.toLowerCase())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the list of ISO 3166 countries
     */
    public static String[] getCountries() {
        String[] cc = Locale.getISOCountries();
        String[] c = new String[cc.length];

        for (int i = 0; i < cc.length; i++) {
            c[i] = cc[i] + " - " + new Locale("en", cc[i]).getDisplayCountry();
        }

        return c;
    }

    /**
     * Uses the system to return a readable version of a
     * locale (language and country) in something the user
     * can understand.
     */
    public static String getLanguageCountry(String locale) {
        Locale l = new Locale(locale.substring(0, locale.indexOf("_")),
                locale.substring(locale.indexOf("_") + 1, locale.length()));

        return locale + " - " + l.getDisplayName();
    }

    /** From one of our supported locales, extracts the
     *  ISO code lang_COUNTRY
     */
    public static String getLocaleFromString(String locale) {
        if (locale.length() < 5) {
            return locale;
        }

        return locale.substring(0, locale.indexOf(" ")).trim();
    }

    /**
     * Reads the current system locale and sets the currency symbol and date
     * format accordingly
     */
    public static void setDateCurrencyFromLocale() {
        // Read the CurrencySymbol and DateFormat strings
        Global.dateFormat = i18n("globals", "DateFormat");
        Global.longDateFormat = i18n("globals", "LongDateFormat");
        Global.longDateTimeFormat = i18n("globals", "LongDateTimeFormat");
        Global.currencySymbol = i18n("globals", "CurrencySymbol");
    }

    /**
     * Loads product info from locale specific resources
     */
    public static void setProduct() {
        try {
            Global.productVersion = i18n("globals", "Version");
            Global.productName = i18n("globals", "Product");
            Global.copyrightMessage = i18n("globals", "Copyright");
            Global.version = productVersion.substring(0, productVersion.indexOf(" "));
        } catch (Exception e) {
        }
    }

    /**
     * Verifies that the log is open for business
     */
    public static void checkLog() {
        if (log == null) {
            try {
                File f = new File(tempDirectory + File.separator + "asm.log");
                log = new FileOutputStream(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verifies that the user's temp directory exists
     */
    public static void checkTempDir() {
        try {
            Global.logDebug("Checking for temp dir: " + tempDirectory +
                File.separator, "Global.checkTempDir");

            File f = new File(tempDirectory + File.separator);
            boolean created = f.mkdirs();

            if (created) {
                Global.logDebug("Temp dir did not exist. Created " +
                    tempDirectory + File.separator, "Global.checkTempDir");
            }
        } catch (Exception e) {
            logException(e, Global.class);
        }
    }

    /**
     * Gets the limit for the number of search results allowed to be
     * returned in one go from the configuration/RecordSearchLimit
     * value. 0 = unlimited
     */
    public static int getRecordSearchLimit() {
        // local databases can be unlimited
        if (DBConnection.DBType == DBConnection.HSQLDB) {
            return 0;
        }

        return Configuration.getInteger("RecordSearchLimit");
    }

    public static boolean getUsingAutoLitterID() {
        return Configuration.getBoolean("AutoLitterIdentification");
    }

    public static String getVetsDiaryUser() {
        return Configuration.getString("VetsDiaryUser", "Vets");
    }

    public static String getCodingFormat() {
        return Configuration.getString("CodingFormat", "TYYYYNNN");
    }

    public static String getShortCodingFormat() {
        return Configuration.getString("ShortCodingFormat", "NNT");
    }

    public static boolean isSingleBreed() {
        return Configuration.getBoolean("UseSingleBreedField");
    }

    public static boolean isCacheActiveAnimals() {
        return Configuration.getBoolean("CacheActiveAnimals");
    }

    /**
     * Sets any MySQL database to use a 16Mb packet size so we can cope with
    * larger media files.
     */
    public static void setMaxAllowedPacket() {
        try {
            if (DBConnection.DBType == DBConnection.MYSQL) {
                logInfo("Setting MySQL max packet size to 16Mb",
                    "Global.setMaxAllowedPacket");
                DBConnection.executeAction(
                    "set global max_allowed_packet=16777216");
            }
        } catch (Exception e) {
            logException(e, Global.class);
        }
    }

    /** Determines whether the log is in use */
    public static void setUsingLog(boolean b) {
        usingLog = b;
    }

    /** Closes the log file handle */
    public static void closeLog() {
        try {
            if (log != null) {
                log.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Logs an error message */
    public static void logError(String message, String location) {
        logMessage(message, "ERROR", location);
    }

    /** Logs a warning */
    public static void logWarning(String message, String location) {
        logMessage(message, "WARNING", location);
    }

    /** Logs information */
    public static void logInfo(String message, String location) {
        logMessage(message, "INFO", location);

        if (applet) {
            appletHandle.showStatus(message);
        }
    }

    /** Logs debug */
    public static void logDebug(String message, String location) {
        if (!showDebug) {
            return;
        }

        logMessage(message, "DEBUG", location);
    }

    public static void logException(Exception e, Class location) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logMessage(sw.toString(), "ERROR", location.getName());
        sw = null;
    }

    /** Logs a message */
    private static void logMessage(String message, String type, String location) {
        if (logdf == null) {
            logdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        String m = logdf.format(new Date()) + " " + type + " [" + location +
            "] " + message + "\n";

        if (type.equals("ERROR")) {
            errors.append(m);
        }

        if (!usingLog) {
            return;
        }

        checkLog();

        try {
            log.write(m.getBytes());
            log.flush();

            if (echolog) {
                System.out.print(m);
            }

            m = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an i18n version of a string for the current locale.
     *
     * @param category
     *            The file (locale/file)
     * @param key
     *            The key to look up
     * @return The string
     */
    public static String i18n(String category, String key) {
        try {
            return ResourceBundle.getBundle("locale/" + category).getString(key);
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING";
        }
    }

    /**
     * Returns an i18n version of a parameterised string for the current locale.
     *
     * @param category
     *            The file (locale/file)
     * @param key
     *            The key to look up
     * @param arg1
     *            Parameter 1
     * @return The string
     */
    public static String i18n(String category, String key, String arg1) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key), new Object[] { arg1 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING";
        }
    }

    /**
     * Returns an i18n version of a parameterised string for the current locale.
     *
     * @param category
     *            The file (locale/file)
     * @param key
     *            The key to look up
     * @param arg1
     *            Parameter 1
     * @param arg2
     *            Parameter 2
     * @return The string
     */
    public static String i18n(String category, String key, String arg1,
        String arg2) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key), new Object[] { arg1, arg2 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING";
        }
    }

    /**
     * Returns an i18n version of a parameterised string for the current locale.
     *
     * @param category
     *            The file (locale/file)
     * @param key
     *            The key to look up
     * @param arg1
     *            Parameter 1
     * @param arg2
     *            Parameter 2
     * @param arg3
     *            Parameter 3
     * @return The string
     */
    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key), new Object[] { arg1, arg2, arg3 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] { arg1, arg2, arg3, arg4 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4, String arg5) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] { arg1, arg2, arg3, arg4, arg5 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4, String arg5, String arg6) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] { arg1, arg2, arg3, arg4, arg5, arg6 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4, String arg5, String arg6,
        String arg7) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4, String arg5, String arg6,
        String arg7, String arg8) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8 });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4, String arg5, String arg6,
        String arg7, String arg8, String arg9) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] {
                    arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9
                });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    public static String i18n(String category, String key, String arg1,
        String arg2, String arg3, String arg4, String arg5, String arg6,
        String arg7, String arg8, String arg9, String arg10) {
        try {
            return MessageFormat.format(ResourceBundle.getBundle("locale/" +
                    category).getString(key),
                new Object[] {
                    arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10
                });
        } catch (Exception e) {
            logError("Missing resource key: " + category + ":" + key,
                "Global.i18n");

            return "MISSING " + category + "." + key;
        }
    }

    /** Outputs the initial startup message for ASM */
    public static void welcomeLog() {
        logInfo(Global.productName + " - " + Global.productVersion, "Welcome");
        logInfo(Global.copyrightMessage, "Welcome");
        logInfo("", "Welcome");
        logInfo("Using: ", "Welcome");
        logInfo("   " + UI.getRendererName(), "Welcome");
        logInfo("   " +
            net.sourceforge.sheltermanager.cursorengine.SQLRecordset.getCursorVersion() +
            " http://sqlrecordset.sf.net", "Welcome");
        logInfo("On:", "Welcome");
        logInfo("   " + System.getProperty("os.name") + " " +
            System.getProperty("os.version") + "/" +
            System.getProperty("os.arch") + " (" + System.getProperty("user.name") + ")", "Welcome");
        logInfo("", "Welcome");
        logInfo("This program is distributed in the hope that it will be useful,",
            "Welcome");
        logInfo("but WITHOUT ANY WARRANTY; without even the implied warranty of",
            "Welcome");
        logInfo("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU",
            "Welcome");
        logInfo("General Public Licence for more details.", "Welcome");
        logInfo("", "Welcome");
    }
}
