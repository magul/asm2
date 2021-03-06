/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.GenerateDocument;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Properties;


/**
 * Manages file types and offers a Windows-style shell execute for all
 * platforms.
 * Since Java 6, we can use Desktop integration now. shellExecute in
 * here will detect the platform and what's available and only fall
 * back to the internal implementation if nothing better is available
 *
 * @author Robin rawson-Tetley
 */
public class FileTypeManager {
    private static Properties types = null;

    // Win32 paths for tree search
    private static String firefoxPath = null;
    private static String iePath = null;
    private static String ooPath = null;
    private static String msoPath = null;
    private static String acrobatPath = null;

    private static String i18n(String key) {
        return Global.i18n("uisystem", key);
    }

    /**
     * Checks whether the filetype manager actually has types registered. If
     * not, it checks the OS and scans intelligently for appropriate file types
     * and builds the registry
     */
    public static void initManager() {
        try {
            // Open the properties file
            File f = new File(Global.tempDirectory + File.separator +
                    "filetypes.properties");
            FileInputStream in = new FileInputStream(f);

            // Attempt to load it
            Properties p = new Properties();
            p.load(in);
            in.close();

            // If it loaded ok the file is there, so everything
            // is fine.
            types = p;

            return;
        } catch (Exception e) {
        }

        // We don't have a registry, scan for the file types based
        // on the OS.

        // Initialise
        types = new Properties();

        Global.logInfo("Scanning file types for " +
            System.getProperty("os.name"), "FileTypeManager.initManager");

        // Scan for the right platform
        if (UI.osIsWindows()) {
            scanWin32();
        } else {
            if (UI.osIsMacOSX()) {
                scanMacOSX();
            } else {
                scanUNIX();
            }
        }

        // Save the scan results
        saveTypes();
    }

    private static void add(String extension, String program) {
        types.setProperty(extension, program);
    }

    private static void add(String[] extensions, String program) {
        for (int i = 0; i < extensions.length; i++) {
            add(extensions[i], program);
        }
    }

    /**
     * Recursive scan of win32 directories looking for certain
     * applications - 2008/10/29 had to disable due to complaints
     * of hanging on various Windows machines
     */
    @SuppressWarnings("unused")
    private static void scanWin32Dir(File dir) throws Exception {
        if (Global.showDebug) {
            Global.logDebug("Scanning: " + checkWin32Path(dir),
                "FileTypeManager.scanWin32Dir");
        }

        String[] contents = dir.list();

        for (int i = 0; i < contents.length; i++) {
            File f = new File(checkWin32Path(dir) + contents[i]);

            if (f.isDirectory()) {
                scanWin32Dir(f);
            }

            if (contents[i].toLowerCase().equals("firefox.exe")) {
                firefoxPath = checkWin32Path(dir);
            }

            if (contents[i].toLowerCase().equals("iexplore.exe")) {
                iePath = checkWin32Path(dir);
            }

            if (contents[i].toLowerCase().equals("swriter.exe")) {
                ooPath = checkWin32Path(dir);
            }

            if (contents[i].toLowerCase().equals("winword.exe")) {
                msoPath = checkWin32Path(dir);
            }

            if (contents[i].toLowerCase().equals("acrord32.exe")) {
                acrobatPath = checkWin32Path(dir);
            }
        }
    }

    /**
     * Simpler function that looks in a few known windows
     * directories to find the applications we're looking
     * for - not as thorough, but should prevent hangs
     * @param d The path to program files
     */
    private static void checkKnownWin32Dirs(String d) throws Exception {
        d = checkWin32Path(d);

        String n = null;

        n = d + "Mozilla Firefox";

        File f = new File(d);

        if (f.exists()) {
            firefoxPath = checkWin32Path(n);
        }

        n = d + "Internet Explorer";
        f = new File(d);

        if (f.exists()) {
            iePath = checkWin32Path(n);
        }

        n = d + "Microsoft Office\\Office12";
        f = new File(d);

        if (f.exists()) {
            msoPath = checkWin32Path(n);
        }

        n = d + "Microsoft Office\\Office11";
        f = new File(d);

        if (f.exists() && (msoPath == null)) {
            msoPath = checkWin32Path(n);
        }

        n = d + "Microsoft Office\\Office10";
        f = new File(d);

        if (f.exists() && (msoPath == null)) {
            msoPath = checkWin32Path(n);
        }

        n = d + "Microsoft Office\\Office";
        f = new File(d);

        if (f.exists() && (msoPath == null)) {
            msoPath = checkWin32Path(n);
        }

        n = d + "OpenOffice.org 3\\program";
        f = new File(d);

        if (f.exists()) {
            ooPath = checkWin32Path(n);
        }

        n = d + "OpenOffice.org 2.4\\program";
        f = new File(d);

        if (f.exists() && (ooPath == null)) {
            ooPath = checkWin32Path(n);
        }

        n = d + "OpenOffice.org 2.3\\program";
        f = new File(d);

        if (f.exists() && (ooPath == null)) {
            ooPath = checkWin32Path(n);
        }

        n = d + "Adobe\\Reader 8.0\\Reader";
        f = new File(d);

        if (f.exists()) {
            acrobatPath = checkWin32Path(n);
        }
    }

    /**
     * Returns a win32 path from a File with a trailing separator
     */
    private static String checkWin32Path(File f) {
        String s = f.getAbsolutePath();

        if (!s.endsWith(File.separator)) {
            s += File.separator;
        }

        return s;
    }

    private static String checkWin32Path(String s) {
        if (!s.endsWith(File.separator)) {
            s += File.separator;
        }

        return s;
    }

    /**
     * Scans a Windows box looking for helper applications
     */
    private static void scanWin32() {
        // For windows, we look for a number of helper programs and
        // scan the Program Files directory.
        try {
            String sysDrive = System.getProperty("user.home").substring(0, 1);
            String d = sysDrive + ":" + File.separator + "Program Files";
            // File f = new File(d);
            // scanWin32Dir(f);
            checkKnownWin32Dirs(d);
        } catch (Exception e) {
            Global.logException(e, FileTypeManager.class);
        }

        String blank = i18n("[no_entry]");

        // Web browser
        // ========================================
        String browser = blank;

        if (firefoxPath != null) {
            browser = firefoxPath + "firefox.exe";
        } else if (iePath != null) {
            browser = iePath + "iexplore.exe";
        }

        add(new String[] {
                "png", "jpg", "gif", "bmp", "tif", "jpeg", "tiff", "html", "htm",
                "css", "xml", "xsl", "ram", "rm", "mov", "mpg", "mpeg", "avi",
                "asf", "wmv", "wav", "aiff"
            }, browser);

        // Office
        // ========================================
        if (ooPath != null) {
            add(new String[] { "sxw", "odt", "rtf", "doc", "docx" },
                ooPath + "swriter.exe");
            add(new String[] { "sxc", "ods", "csv", "xls", "xlsx" },
                ooPath + "scalc.exe");
            add(new String[] { "sxi", "odp", "ppt", "pps" },
                ooPath + "simpress.exe");
            add(new String[] { "mdb", "mda" }, ooPath + "sbase.exe");
        }

        if (msoPath != null) {
            add(new String[] { "doc", "docx", "rtf" }, msoPath + "winword.exe");
            add(new String[] { "xls", "xlsx", "csv" }, msoPath + "excel.exe");
            add(new String[] { "ppt", "pps" }, msoPath + "powerpnt.exe");
            add(new String[] { "mda", "mdb" }, msoPath + "msaccess.exe");
        }

        // Set the default word processor based on what we
        // scanned - prefer MSO2007 for Windows, falling back to
        // OpenOffice if available
        // ========================================
        if (msoPath != null) {
            setWP(GenerateDocument.MICROSOFT_OFFICE_2007);
        } else if (ooPath != null) {
            setWP(GenerateDocument.OPENOFFICE_2);
        }

        // Warn if no office suite found
        // ========================================
        if ((msoPath == null) && (ooPath == null)) {
            Global.logWarning("ASM could not find an office suite.",
                "FileTypeManager.scanWin32");
        }

        // PDF Viewer
        // =========================================
        if (acrobatPath != null) {
            add(new String[] { "pdf", "ps" }, acrobatPath + "acrord32.exe");
        }

        if (acrobatPath == null) {
            Global.logWarning("ASM could not find a PDF viewer",
                "FileTypeManager.scanWin32");
        }
    }

    /**
     * Updates the system-wide word processor - this only works
     * for local HSQLDB databases (single user installations)
     */
    private static void setWP(String wp) {
        if (DBConnection.DBType == DBConnection.HSQLDB) {
            Configuration.setEntry("DocumentWordProcessor", wp);
        }
    }

    private static void scanMacOSX() {
        // We can map all the filetypes we'll need straight into the
        // Finder through /usr/bin/open on OSX - so easy! Apart from
        // gnome-open under Linux, there's nothing else so easy
        // out there. Only downer is, without detection we can't auto
        // choose the word processor for Mac users.
        String open = "/usr/bin/open";
        add(new String[] {
                "sxw", "sxc", "sxi", "csv", "png", "jpg", "gif", "bmp", "tif",
                "jpeg", "mpg", "mpeg", "avi", "rm", "ram", "asf", "wmv", "mov",
                "aiff", "wav", "mp3", "doc", "rtf", "xls", "ppt", "sxw", "sxc",
                "sxi", "odt", "ods", "odp", "csv", "html", "htm", "xls", "css",
                "xsl", "pdf", "ps", "abw"
            }, open);
    }

    /**
     * Looks for any of the binaries given and returns the full path
     * to them if found, or null if none were found
     */
    private static String scanUnixBinary(String[] names) {
        File f = null;
        String spath = "/usr/bin/";

        for (int i = 0; i < names.length; i++) {
            f = new File(spath + names[i]);

            if (f.exists()) {
                return spath + names[i];
            }
        }

        return null;
    }

    /**
     * Scans for Unix helper applications - this is a bit Linux-centric
     * since I don't really use commercial unixes
     */
    private static void scanUNIX() {
        String blank = i18n("[no_entry]");

        // Images
        // ========================================
        String imageViewer = scanUnixBinary(new String[] { "gqview", "eog", "ee" });
        add(new String[] { "png", "jpg", "jpeg", "gif", "bmp", "tif", "tiff" },
            ((imageViewer != null) ? imageViewer : blank));

        // Movies
        // ========================================
        String moviePlayer = scanUnixBinary(new String[] {
                    "mplayer", "xine", "totem"
                });
        add(new String[] { "mpg", "mpeg", "avi", "rm", "ram", "asf", "wmv", "mov" },
            ((moviePlayer != null) ? moviePlayer : blank));

        // Sounds
        // ========================================
        String soundPlayer = scanUnixBinary(new String[] {
                    "rhythmbox", "amarok", "xmms", "gnome-sound-recorder"
                });
        add(new String[] { "aiff", "wav", "mp3", "flac", "ogg" },
            ((soundPlayer != null) ? soundPlayer : blank));

        // OpenOffice
        // ========================================
        String writer = scanUnixBinary(new String[] { "oowriter", "swriter" });
        String calc = scanUnixBinary(new String[] { "oocalc", "scalc" });
        String impress = scanUnixBinary(new String[] { "ooimpress", "simpress" });

        add(new String[] { "doc", "rtf", "sxw", "odt" },
            ((writer != null) ? writer : blank));
        add(new String[] { "xls", "csv", "ods", "sxc" },
            ((calc != null) ? calc : blank));
        add(new String[] { "ppt", "pps", "sxi", "odp" },
            ((impress != null) ? impress : blank));

        // Abiword
        // ===========================================
        String abiword = scanUnixBinary(new String[] { "abiword" });
        add(new String[] { "abw" }, ((abiword != null) ? abiword : blank));

        // Set the default word processor according to what we have -
        // OpenOffice if available, or Abiword if that's what we've got
        // ===========================================
        if (writer != null) {
            setWP(GenerateDocument.OPENOFFICE_2);
        }

        if ((abiword != null) && (writer == null)) {
            setWP(GenerateDocument.ABIWORD);
        }

        // Warn the user if no word processor found
        // ===========================================
        if ((writer == null) && (abiword == null)) {
            Global.logWarning("Couldn't find an office suite, searched for openoffice and abiword",
                "FileTypeManager.scanUNIX");
        }

        // HTML and Internet document files
        // ====================================================
        String browser = scanUnixBinary(new String[] {
                    "firefox", "galeon", "epiphany", "mozilla", "opera",
                    "netscape"
                });
        add(new String[] { "html", "html", "css", "xsl" },
            ((browser != null) ? browser : blank));

        if (browser == null) {
            Global.logWarning("ASM could not find an HTML viewer",
                "FileTypeManager.scanUNIX");
        }

        // PDF, PS files
        // =====================================================
        String pdf = scanUnixBinary(new String[] {
                    "evince", "gpdf", "xpdf", "ggv", "gv"
                });
        add(new String[] { "pdf", "ps" }, ((pdf != null) ? pdf : blank));

        if (pdf == null) {
            Global.logWarning("ASM could not find a PDF viewer",
                "FileTypeManager.scanUNIX");
        }
    }

    /**
     * Given a file, this will find the type and attempt to open it with the
     * correct program.
     * @param file The file to open
     * @return non-zero error code
     */
    public static int shellExecute(String file) {
        if (System.getProperty("asm.shellexecute", "guess").equals("internal")) {
            Global.logDebug("Command line forced internal ShellExecute",
                "FileTypeManager.shellExecute");

            return shellExecuteInternal(file);
        } else if (UI.osIsMacOSX()) {
            Global.logDebug("Using MacOS ShellExecute",
                "FileTypeManager.shellExecute");

            return shellExecuteMacOS(file);
        } else if (UI.osShellExecuteAvailable()) {
            Global.logDebug("Using OS integrated ShellExecute",
                "FileTypeManager.shellExecute");

            return shellExecuteOS(file);
        } else {
            Global.logDebug("Using internal ShellExecute",
                "FileTypeManager.shellExecute");

            return shellExecuteInternal(file);
        }
    }

    /**
     * Shellexecute implementation that uses whatever OS integration we have
     */
    public static int shellExecuteOS(String file) {
        try {
            // Is our file a URL? If so, open it with the system browser
            if (file.indexOf(":/") != -1) {
                // If we're using Windows XP, remove the escaped spaces
                // or it will barf when it hits IE6
                if (file.indexOf("Documents%20and") != -1) {
                    file = Utils.replace(file, "%20", " ");
                }

                Global.logInfo("Browsing to: " + file,
                    "FileTypeManager.shellExecuteOS");
                UI.osBrowse(file);
            } else {
                Global.logInfo("Opening: " + file,
                    "FileTypeManager.shellExecuteOS");
                UI.osOpen(file);
            }

            return 0;
        } catch (Exception e) {
            Global.logException(e, FileTypeManager.class);

            return 1;
        }
    }

    /**
     * Shellexecute implementation for MacOS - hardcoded to use the /usr/bin/open binary
     */
    public static int shellExecuteMacOS(String file) {
        // We always use the open binary on Mac
        String command = "/usr/bin/open";

        try {
            // Does the file have a space in it? It shouldn't
            // ever really, but people can move their temp
            // folders, so check for the same thing
            if (file.indexOf(" ") != -1) {
                if (!file.startsWith("\"")) {
                    file = "\"" + file + "\"";
                }
            }

            Global.logInfo("Executing: " + command + " " + file,
                "FileTypeManager.shellExecuteMacOS");

            Utils.execAsync(new String[] { command, file });

            return 0; // success
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, FileTypeManager.class);
        }

        return 1; // error
    }

    /**
     * Shellexecute implementation that uses an internally stored list of filetypes
     */
    public static int shellExecuteInternal(String file) {
        // Get the filetype from the end
        if (file.indexOf(".") == -1) {
            Dialog.showError(i18n("This_file_has_no_type_extension._Animal_Shelter_Manager_cannot_open_it."),
                i18n("No_type"));

            return 1;
        }

        // If we are dealing with URLs of some type, then
        // transform to the correct file type
        String filetype = "";
        boolean isLink = false;

        // HTTP, HTTPS and FTP URLs
        if ((Utils.englishLower(file).indexOf("http://") != -1) ||
                (Utils.englishLower(file).indexOf("https://") != -1) ||
                (Utils.englishLower(file).indexOf("ftp://") != -1)) {
            filetype = "html";
            isLink = true;
        } else {
            // Really is a file - process it
            filetype = file.substring(file.lastIndexOf(".") + 1, file.length());
            filetype = Utils.englishLower(filetype);
        }

        // Look up the type
        String command = types.getProperty(filetype, "");

        // FILE URLs - only do this if it is an HTML or HTM file and
        // isn't an HTTP, HTTPS or FTP link
        if ((filetype.equals("html") || filetype.equals("htm")) && !isLink) {
            file = "file:///" + file;
        }

        // Bomb if none found
        if ((command.equals("")) || (command.equals(i18n("[no_entry]")))) {
            Global.logError(i18n("Animal_Shelter_Manager_has_no_registered_type_for_'") +
                filetype + "' files.", "FileTypeManager.shellExecuteInternal");
            Dialog.showError(i18n("Animal_Shelter_Manager_has_no_registered_type_for_'") +
                filetype + "' files.");

            return 1;
        }

        try {
            // Does the command have a space in it? If so, does
            // it already have speechmarks around it? If not,
            // put them on.
            if (command.indexOf(" ") != -1) {
                if (!command.startsWith("\"")) {
                    command = "\"" + command + "\"";
                }
            }

            // Does the file have a space in it? It shouldn't
            // ever really, but people can move their temp
            // folders, so check for the same thing
            if (file.indexOf(" ") != -1) {
                if (!file.startsWith("\"")) {
                    file = "\"" + file + "\"";
                }
            }

            Global.logInfo("Executing: " + command + " " + file,
                "FileTypeManager.shellExecuteInternal");

            Utils.execAsync(new String[] { command, file });

            return 0; // success
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, FileTypeManager.class);
        }

        return 1; // error
    }

    public static Properties getTypes() {
        return types;
    }

    public static void setTypes(Properties types) {
        FileTypeManager.types = types;
    }

    public static void saveTypes() {
        try {
            File f = new File(Global.tempDirectory + File.separator +
                    "filetypes.properties");
            FileOutputStream out = new FileOutputStream(f);
            types.store(out, null);
            out.close();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, FileTypeManager.class);
        }
    }
}
