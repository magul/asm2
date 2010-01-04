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
package net.sourceforge.sheltermanager.asm.utility;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.ftp.FTPClient;
import net.sourceforge.sheltermanager.asm.ftp.FTPException;
import net.sourceforge.sheltermanager.asm.ftp.FTPTransferType;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;


/**
 * A bunch of useful utilities used throughout the system
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public abstract class Utils {
    public static boolean testedSM = false;
    public static boolean isSM = false;

    /** Looks in findin for all occurrences of find and replaces them with replacewith
     * @param findin The string to find occurrences in
     * @param find The string to find
     * @param replacewith The string to replace found occurrences with
     * @return A string with all occurrences of find replaced.
     */
    public static String replace(String findin, String find, String replacewith) {
        StringBuffer sb = new StringBuffer(findin);
        int i = 0;

        try {
            while (i <= (sb.length() - find.length())) {
                if (sb.substring(i, i + find.length()).equalsIgnoreCase(find)) {
                    sb.replace(i, i + find.length(), replacewith);
                    i += replacewith.length();

                    continue;
                }

                i++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            // We hit the end of the string - do nothing and carry on
        }

        return sb.toString();
    }

    /*** Removes HTML tags from a string
     * @param s The string to remove HTML tags from
       */
    public static String removeHTML(String s) {
        int i = 0;
        boolean oktoadd = true;
        String output = "";

        while (i <= (s.length() - 1)) {
            if (s.substring(i, i + 1).equals("<")) {
                oktoadd = false;
                i++;

                continue;
            }

            if (s.substring(i, i + 1).equals(">")) {
                i++;
                oktoadd = true;

                continue;
            }

            if (oktoadd && (i <= (s.length() - 1))) {
                output = output + s.substring(i, i + 1);
            }

            i++;
        }

        return output;
    }

    /** Returns todays date as a nicely formatted string.
     * @return A string representing today in the format dddd, dxx mmmm yyyy
     */
    public static String getReadableTodaysDate() {
        return formatDateLong(new Date());
    }

    /**
     * Returns the amount as a formatted currency amount for the locale.
     * @param amount
     * @return A formatted currency amount for the locale.
     */
    public static String formatCurrency(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        return nf.format(amount);
    }

    /**
     * Returns the currency string as a double value
     * @param currency
     * @return a double value
     */
    public static double parseCurrency(String currency)
        throws ParseException {
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        return nf.parse(currency).doubleValue();
    }

    /** Formats a date in long display format */
    public static String formatDateLong(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.longDateFormat);

        return df.format(d);
    }

    /** Formats a date in long date/time display format */
    public static String formatDateTimeLong(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.longDateTimeFormat);

        return df.format(d);
    }

    /** Formats a date to show just the month */
    public static String formatDateMonth(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat("MMMM");

        return df.format(d);
    }

    /** Formats a date in display format */
    public static String formatDate(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.dateFormat);

        return df.format(d);
    }

    /** Formats a date in table format for sorting */
    public static String formatTableDate(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.tableDateFormat);

        return df.format(d);
    }

    /** Formats a date in table format for sorting */
    public static String formatTableDateTime(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.tableDateTimeFormat);

        return df.format(d);
    }

    /** Formats a date in table format for sorting */
    public static String formatTableDate(Calendar c) {
        if (c == null) {
            return "";
        }

        Date d = calendarToDate(c);
        SimpleDateFormat df = new SimpleDateFormat(Global.tableDateFormat);

        return df.format(d);
    }

    /** Formats a date in time format */
    public static String formatTime(Date d) {
        if (d == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.timeFormat);

        return df.format(d);
    }

    /** Formats a calendar in display format */
    public static String formatDate(Calendar c) {
        if (c == null) {
            return "";
        }

        Date d = calendarToDate(c);
        SimpleDateFormat df = new SimpleDateFormat(Global.dateFormat);

        return df.format(d);
    }

    /** Parses a Date from table display format */
    public static Date parseTableDate(String d) throws ParseException {
        if ((d == null) || d.equals("")) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.tableDateFormat);

        return df.parse(d);
    }

    /** Parses a Date from display format */
    public static Date parseDate(String d) throws ParseException {
        if ((d == null) || d.equals("")) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat(Global.dateFormat);

        return df.parse(d);
    }

    /** Parses a Date from the display date and time given */
    public static Date parseDate(String d, String t) throws ParseException {
        if ((d == null) || d.equals("")) {
            return null;
        }

        String fs = Global.dateFormat + " " + Global.timeFormat;
        SimpleDateFormat df = new SimpleDateFormat(fs);
        String s = d + " " + t;
        Date dd = df.parse(s);

        return dd;
    }

    /** Returns an SQL date from the date given */
    public static String getSQLDate(Date d) {
        return SQLRecordset.getSQLRepresentationOfDate(d);
    }

    public static String getSQLDateOnly(Date d) {
        return SQLRecordset.getSQLRepresentationOfDateOnly(d);
    }

    public static String getSQLDateOnly(Calendar c) {
        return SQLRecordset.getSQLRepresentationOfDateOnly(calendarToDate(c));
    }

    public static String getSQLDateOnly(String d) throws ParseException {
        return SQLRecordset.getSQLRepresentationOfDateOnly(parseDate(d));
    }

    /** Returns an SQL date from the Calendar given */
    public static String getSQLDate(Calendar c) {
        return SQLRecordset.getSQLRepresentationOfDate(calendarToDate(c));
    }

    /** Returns an SQL date from the display date given */
    public static String getSQLDate(String d) throws ParseException {
        return SQLRecordset.getSQLRepresentationOfDate(parseDate(d));
    }

    /** Returns a java Date as a calendar. Returns null
     *  if the date is null.
     * @param d
     * @return
     */
    public static Calendar dateToCalendar(Date d) {
        if (d == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        return c;
    }

    /**
     * Returns a java Calendar as a Date. Returns null
     * if the calendar is null.
     * @param c
     * @return
     */
    public static Date calendarToDate(Calendar c) {
        if (c == null) {
            return null;
        }

        return new Date(c.getTimeInMillis());
    }

    /**
     * Provides pivot date services to 2 digit years.
     * @param year The year to check. 4 digit years are returned as is.
     * @return The 2 digit year as an appropriate 4 digit year.
     */
    public static String pivotYear(String year) {
        // Abandon if it's already 4 digits
        if (year.length() > 2) {
            return year;
        }

        // If it's only one digit, pad a 0
        if (year.length() == 1) {
            year = "0" + year;
        }

        int y = Integer.parseInt(year);

        if (y < Global.PIVOT_YEAR) {
            return Global.BELOW_PIVOT + year;
        } else {
            return Global.AFTER_PIVOT + year;
        }
    }

    /**
    * Copies images from the media FTP server to the local temp folder.
    * @param ftp The FTP client at the dir you want to copy from.
    * @param localtempdir The directory on the local disk relative to the
    * temp folder. Use an empty string for the temp folder itself.
    */
    public static void ftpCopyImages(FTPClient ftp, String localtempdir) {
        try {
            // Transfer any jpegs, gifs or pngs
            ftpTransferImages(ftp, localtempdir, "*.jpg");
            ftpTransferImages(ftp, localtempdir, "*.gif");
            ftpTransferImages(ftp, localtempdir, "*.png");
        } catch (Exception e) {
            Dialog.showError("Error occurred accessing media server: " +
                e.getMessage(), "Error");
            Global.logException(e, Utils.class);
        }
    }

    /**
     * Copies images from the media FTP server to the local temp folder.
     * @param ftpsourcedir The directory on the FTP server relative to the
     * root folder. Use an empty string for the root folder.
     * @param localtempdir The directory on the local disk relative to the
     * temp folder. Use an empty string for the temp folder itself.
     */
    public static void ftpCopyImages(String ftpsourcedir, String localtempdir) {
        String ftpserver = "";
        String ftplogin = "";
        String ftppassword = "";
        String ftpport = "";

        ftpserver = Configuration.getString("MediaServer");
        ftplogin = Configuration.getString("MediaLogin");
        ftppassword = Configuration.getString("MediaPassword");
        ftpport = Configuration.getString("MediaIPPort");

        // Connect and login
        try {
            FTPClient ftp = new FTPClient(ftpserver, Integer.parseInt(ftpport));
            ftp.login(ftplogin, ftppassword);

            // Binary transfers
            ftp.setType(FTPTransferType.BINARY);

            // Change to source directory
            if (!ftpsourcedir.equals("")) {
                ftp.chdir(ftpsourcedir);
            }

            // Transfer any jpegs, gifs or pngs
            ftpTransferImages(ftp, localtempdir, "*.jpg");
            ftpTransferImages(ftp, localtempdir, "*.gif");
            ftpTransferImages(ftp, localtempdir, "*.png");

            // Close the connection
            ftp.quit();
        } catch (Exception e) {
            Dialog.showError("Error occurred accessing media server: " +
                e.getMessage(), "Error");
            Global.logException(e, Utils.class);
        }
    }

    /**
     * Transfers images matching the mask to the temp directory
     * using the passed FTPClient object
     * @param ftp An open FTPClient socket, set to the correct directory
     * @param localtempdir A directory relative to the local temp directory to
     * store the images in
     * @param A directory mask of files you want to copy - eg: *.jpg
     */
    private static void ftpTransferImages(FTPClient ftp, String localtempdir,
        String filemask) {
        // Make the relative temp dir into an absolute path
        String abspath = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
            File.separator;

        if (!localtempdir.equals("")) {
            abspath = abspath + localtempdir + File.separator;
        }

        try {
            String list = "";

            try {
                list = ftp.list(filemask);
            } catch (FTPException e) {
            }

            // Make sure it isn't empty - if it is, there's nothing
            // for us to do, so exit now
            if (list.equals("")) {
                return;
            }

            // Once the list has been performed, make sure we are back
            // to binary transfers - this fixes a bug using
            // ProFTPD in Linux
            ftp.setType(FTPTransferType.BINARY);

            // list should hold a list of files found now - break it down
            // by carriage returns.
            int i = 0;
            int spos = 0;
            String workingfile = "";

            i = list.indexOf("\n");

            while (i != -1) {
                workingfile = list.substring(spos, i).trim();
                spos = i + 1;
                i = list.indexOf("\n", spos);
                ftpCopyAFile(ftp, workingfile, abspath + workingfile);
            }

            // Copy any final file
            ftpCopyAFile(ftp, workingfile, abspath + workingfile);
        } catch (Exception e) {
            Global.logException(e, Utils.class);
        }
    }

    /**
     * Copies a file from the FTP server to the absolute location specified.
     * @param ftp An open FTPClient object at the correct directory
     * @param filename The name of the file to copy
     * @param locallocation The absolute location and filename to store the file
     */
    private static void ftpCopyAFile(FTPClient ftp, String filename,
        String locallocation) {
        try {
            // If the filename is a blank, quit now
            if (filename.trim().equals("")) {
                return;
            }

            // Get the file as a stream of bytes from the FTP server
            byte[] thefile = ftp.get(filename);

            // Attempt to create a file output stream. If it fails, who cares -
            // assume the file is already there
            FileOutputStream out = new FileOutputStream(locallocation);
            out.write(thefile);
            out.close();
        } catch (IOException e) {
        } catch (Exception e) {
            Global.logException(e, Utils.class);
        }
    }

    /**
     * Returns the ID for a given text field within a table.
     * @param tableName The name of the table to search
     * @param fieldName The name of the field to search in
     * @param fieldValue The value in the field to search for
     * @return The ID of that row in the table
     */
    public static Integer getID(String tableName, String fieldName,
        String fieldValue) {
        try {
            SQLRecordset rs = new SQLRecordset();

            rs.openRecordset("Select ID From " + tableName + " Where " +
                fieldName + " Like '" + fieldValue.replace('\'', '`') + "'",
                tableName);

            if (rs.getEOF()) {
                return new Integer(0);
            } else {
                return (Integer) rs.getField("ID");
            }
        } catch (Exception e) {
            Global.logException(e, Utils.class);

            return new Integer(0);
        }
    }

    /**
     * Marks a temporary file for deletion when the VM exits
     * @param filename The filename to delete
     */
    public static void deleteTemporaryFile(String name) {
        File f = new File(Global.tempDirectory + File.separator + name);
        f.deleteOnExit();
    }

    /** Renames a file
     *
     * @param source The source file
     * @param destination The destination file
     * @throws java.io.IOException
     */
    public static void renameFile(File source, File destination)
        throws IOException {
        if (Global.showDebug) {
            Global.logDebug("Rename " + source.getAbsolutePath() + " to " +
                destination.getAbsolutePath(), "Utils.renameFile");
        }

        source.renameTo(destination);
    }

    /**
     * Creates a temporary file with the contents given and extension
     * - the return value is the full path to the file. The file is
     *   marked for deletion when the VM terminates.
     * @param content The contents of the file
     * @param extension The file extension (without a dot prefix)
     * @return The full path to the temporary file
     */
    public static String createTemporaryFile(String content, String extension) {
        String tempFileName = "";

        try {
            tempFileName = Global.tempDirectory + File.separator + "tf";

            boolean fileExists = true;
            File f = null;

            while (fileExists) {
                int rand = (int) (Math.random() * 10000000);
                tempFileName += (Integer.toString(rand) + "." + extension);
                f = new File(tempFileName);
                fileExists = f.exists();
            }

            FileOutputStream out = new FileOutputStream(f);
            out.write(content.getBytes());
            out.flush();
            out.close();
            out = null;
            f.deleteOnExit();
            f = null;
        } catch (Exception e) {
            Global.logException(e, Utils.class);
        }

        return tempFileName;
    }

    /**
     * From a passed id, lookup field, table and combo, this
     * routine will search for the row with ID in tableName
     * and when it finds it, it will read fieldName and then
     * look for this value in the list of the combobox
     * passed. If it finds it, that item will be selected
     * in the combo. This is useful for loading data in screens.
     *
     * If no match could be found, the combo is left alone.
     *
     * @param lookup The lookup to search in
     * @param fieldName The name of the field to check against the list
     * @param idValue The ID of the row to pull out of the table
     * @param theCombo A reference to the combo box to set
     */
    public static void setComboFromID(SQLRecordset lookup, String fieldName,
        Integer idValue, UI.ComboBox theCombo) {
        try {
            // Drop out if we have an empty string
            if (idValue.equals("")) {
                return;
            }

            // Find the row for the ID
            lookup.moveFirst();

            while (!lookup.getEOF()) {
                if (((Integer) lookup.getField("ID")).equals(idValue)) {
                    break;
                }

                lookup.moveNext();
            }

            // If we didn't find the ID in the set, abandon
            if (lookup.getEOF()) {
                return;
            }

            // We have our row, see if we can find it in the list data
            int i = 0;

            while (i < theCombo.getItemCount()) {
                String theitem = (String) theCombo.getItemAt(i);

                if (theitem.equals(lookup.getField(fieldName))) {
                    // We have a match - select it and break
                    theCombo.setSelectedIndex(i);

                    return;
                }

                i++;
            }
        } catch (Exception e) {
            Global.logException(e, Utils.class);

            return;
        }
    }

    /**
     * From a passed lookup field, lookup and combo, this
     * routine will search fieldName in the lookup for
     * the selected item in the combo box passed.
     * When it finds it, it will return the ID field value.
     *
     * If it could not be found, a zero will be returned.
     *
     * @param lookup The lookup to search in
     * @param fieldName The name of the field to check against the list
     * @param theCombo A reference to the combo box containing the selected item
     */
    public static Integer getIDFromCombo(SQLRecordset lookup, String fieldName,
        UI.ComboBox theCombo) {
        try {
            String comboselection = (String) theCombo.getSelectedItem();

            lookup.moveFirst();

            while (!lookup.getEOF()) {
                if (lookup.getField(fieldName).toString().equals(comboselection)) {
                    return (Integer) lookup.getField("ID");
                }

                lookup.moveNext();
            }

            return new Integer(0);
        } catch (Exception e) {
            Global.logException(e, Utils.class);

            return new Integer(0);
        }
    }

    /**
     * Subtracts a given number of years from a date, allowing
     * fractional date subtractions (it does this by calculating
     * the number of years as weeks, so 0.5 * 52 = 26).
     * @param date The starting date
     * @param years The number of years to subtract
     * @return A java.util.Calendar object with the result
     */
    public static Calendar subtractYears(Calendar date, float years) {
        // Convert years to weeks
        float weeks = (years * 52);
        date.add(Calendar.WEEK_OF_YEAR, (int) weeks * -1);

        return date;
    }

    /**
     * Subtracts a given number of months from a date.
     * @param date The starting date
     * @param months The number of months to subtract
     * @return A java.util.Calendar object with the result
     */
    public static Calendar subtractMonths(Calendar date, int months) {
        date.add(Calendar.MONTH, (int) months * -1);

        return date;
    }

    /**
     * Splits a string by a particular char and returns an array of
     * strings. If there are no occurrences of the split char, the
     * original string is returned in an array of 1 item.
     * @param splitstring The string to be split
     * @param splitchar The char to split on
     * @return An array of strings
     */
    public static String[] split(String splitstring, String splitchar) {
        splitstring = splitstring.trim();

        // If there is only one element, just return that
        if (splitstring.indexOf(splitchar) == -1) {
            String[] rets = new String[1];
            rets[0] = splitstring;

            return rets;
        }

        // Find how many there are
        int tot = 0;
        int lpos = splitstring.indexOf(splitchar);

        while (lpos != -1) {
            tot++;
            lpos = splitstring.indexOf(splitchar, lpos + splitchar.length());
        }

        tot++;

        // Create our new array
        String[] rets = new String[tot];
        tot = 0;
        lpos = 0;

        int spos = splitstring.indexOf(splitchar);

        while (spos != -1) {
            // Add into the array
            rets[tot] = splitstring.substring(lpos, spos);
            tot++;
            lpos = spos + splitchar.length();
            spos = splitstring.indexOf(splitchar, lpos);
        }

        // Include last word
        rets[tot] = splitstring.substring(lpos, splitstring.length());

        // Return it
        return rets;
    }

    /**
     * Returns an empty string if a value is the NULL value.
     * @param str The string to test
     * @return The original string, or an empty string if it was null.
     */
    public static String nullToEmptyString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * Pads a number with leading zeroes and returns it as a string.
     * @param number The number to pad
     * @param places The desired length of the number
     */
    public static String zeroPad(int number, int places) {
        String zeroes = "000000000000000";
        String n = Integer.toString(number);

        if (n.length() >= places) {
            return n;
        }

        return zeroes.substring(0, places - n.length()) + n;
    }

    /**
     * Returns a new temporary file name (a random number,
     * suffixed with extension)
     * @param extension The file extension without a leading dot
     * @return A new temporary file reference
     */
    public static File getNewTempFile(String extension) {
        String justFilename = null;
        String filename = null;
        int rand = 0;
        File file = null;

        while (true) {
            // Generate random key
            rand = (int) (Math.random() * 10000000);
            // Generate filename from it
            justFilename = Integer.toString(rand) + "." + extension;
            filename = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                File.separator + justFilename;
            // Does it exist?
            file = new File(filename);

            // If not, carry on, otherwise keep looping around
            // until we find one that does not exist.
            if (!file.exists()) {
                break;
            }
        }

        return file;
    }

    /**
     * Gets a DBFS in the correct directory for the media link type
     * and ID given, creating them if necessary.
     */
    public static DBFS getDBFSDirectoryForLink(int linkType, int linkID)
        throws Exception {
        Global.logDebug("Obtaining DBFS dir for type: " + linkType + ", ID: " +
            linkID, "Utils.getDBFSDirectoryForLink");

        DBFS dbfs = new DBFS();

        // Test the link type to change to the correct directory
        String rootdir = "";

        switch (linkType) {
        case (Media.LINKTYPE_ANIMAL):
            rootdir = "animal";

            break;

        case (Media.LINKTYPE_LOSTANIMAL):
            rootdir = "lostanimal";

            break;

        case (Media.LINKTYPE_FOUNDANIMAL):
            rootdir = "foundanimal";

            break;

        case (Media.LINKTYPE_OWNER):
            rootdir = "owner";

            break;

        case (Media.LINKTYPE_MOVEMENT):
            rootdir = "movement";

            break;

        case (Media.LINKTYPE_WAITINGLIST):
            rootdir = "waitinglist";

            break;
        }

        // Try to create the root in case it isn't there
        try {
            dbfs.mkdir(rootdir);
        } catch (Exception e) {
        }

        dbfs.chdir(rootdir);

        // Attempt to create the ID directory for this link, ignoring errors
        try {
            dbfs.mkdir(Integer.toString(linkID));
        } catch (Exception e) {
        }

        dbfs.chdir(Integer.toString(linkID));

        // Return the open link
        return dbfs;
    }

    /**
     * Gets an open FTP client socket on the correct directory for the
     * media link type and ID given, creating them if necessary.
     */
    public static FTPClient getFTPDirectoryForLink(int linkType, int linkID)
        throws Exception {
        // Read FTP server configuration
        String ftpserver = "";
        String ftplogin = "";
        String ftppassword = "";
        String ftpport = "";

        ftpserver = Configuration.getString("MediaServer");
        ftplogin = Configuration.getString("MediaLogin");
        ftppassword = Configuration.getString("MediaPassword");
        ftpport = Configuration.getString("MediaIPPort");

        // Connect and login
        FTPClient ftp = new FTPClient(ftpserver, Integer.parseInt(ftpport));
        ftp.login(ftplogin, ftppassword);

        // Binary transfers
        ftp.setType(FTPTransferType.BINARY);

        String rootdir = "";

        // Test the link type to change to the correct directory
        switch (linkType) {
        case (Media.LINKTYPE_ANIMAL):
            rootdir = "animal";

            break;

        case (Media.LINKTYPE_LOSTANIMAL):
            rootdir = "lostanimal";

            break;

        case (Media.LINKTYPE_FOUNDANIMAL):
            rootdir = "foundanimal";

            break;

        case (Media.LINKTYPE_OWNER):
            rootdir = "owner";

            break;

        case (Media.LINKTYPE_MOVEMENT):
            rootdir = "movement";

            break;
        }

        // Attempt to create our root directory, ignoring errors
        try {
            ftp.mkdir(rootdir);
        } catch (Exception e) {
        }

        // Change into it. If an error occurs here, it breaks
        ftp.chdir(rootdir);

        // Attempt to create the ID directory for this link, ignoring errors
        try {
            ftp.mkdir(Integer.toString(linkID));
        } catch (Exception e) {
        }

        // Change into it. Again, any error occurs and we break
        ftp.chdir(Integer.toString(linkID));

        // Return the open socket
        return ftp;
    }

    /**
     * Returns a vector of FTPDirEntry objects representing
     * the files/directories in the current directory of the
     * FTPClient passed in.
     * @param ftp The FTP client
     * @return A Vector of FTPDirEntry objects
     */
    public Vector ftpListDirectory(FTPClient ftp) {
        try {
            // Get a full directory listing
            String list = ftp.list("*", true);

            // Parse it by splitting it down on line breaks
            String[] entries = Utils.split(list, "\n");

            // Read each one and make an entry in the list
            Vector en = new Vector();

            for (int i = 0; i < entries.length; i++) {
                // See if we have been returned parent information (starts with a "..:")
                // If we do, it is safe to ignore
                if (entries[i].startsWith("..")) {
                    continue;
                }

                // Make sure the entry is not already in our list. If it is,
                // discard it.
                Iterator it = en.iterator();
                FTPDirEntry testEntry = null;
                boolean alreadyInList = false;

                while (it.hasNext()) {
                    testEntry = (FTPDirEntry) it.next();

                    if (entries[i].substring(55, entries[i].length())
                                      .equals(testEntry.name)) {
                        alreadyInList = true;
                    }
                }

                // See if it is a directory (first char of permissions is "d")
                if (!alreadyInList) {
                    if (entries[i].startsWith("d")) {
                        FTPDirEntry ent = new FTPDirEntry();
                        ent.raw = entries[i];
                        ent.name = entries[i].substring(55, entries[i].length());
                        ent.isDirectory = true;
                        en.add(ent);
                    } else {
                        FTPDirEntry ent = new FTPDirEntry();
                        ent.raw = entries[i];
                        ent.name = entries[i].substring(55, entries[i].length());
                        en.add(ent);
                    }
                }
            }

            return en;
        } catch (Exception e) {
            Global.logException(e, Utils.class);

            return null;
        }
    }

    /**
     * Formats an address to print on one line by converting all
     * line breaks to a comma and a space. All char 13s are
     * converted, and any char 10s are stripped (Windows legacy)
     * @param address The address to format
     * @return The formatted address
     */
    public static String formatAddress(String address) {
        // Has to deal with the following possible combinations
        // 13s, 10s and 13+10s together.
        String output = "";

        if (address.indexOf(new String(new byte[] { 13, 10 })) != -1) {
            // Windows style break 13 + 10
            output = replace(address, new String(new byte[] { 13, 10 }), ", ");
        } else if (address.indexOf(new String(new byte[] { 10 })) != -1) {
            // UNIX style break 10
            output = replace(address, new String(new byte[] { 10 }), ", ");
        } else if (address.indexOf(new String(new byte[] { 13 })) != -1) {
            // DOS break 13
            output = replace(address, new String(new byte[] { 13 }), ", ");
        } else {
            // Can't find any breaks - just return whatever it was to start with
            output = address;
        }

        return output;
    }

    /**
     * Breaks a person's name String into title, initials,
     * forenames and surname.
     * @param name The name to break
     * @return An array of 4 elements containing (in order) the
     * title, initials, forenames and surname of the passed entry.
     */
    public static String[] getNameElements(String name) {
        // Create return array
        String[] ret = new String[4];
        ret[0] = "";
        ret[1] = "";
        ret[2] = "";
        ret[3] = "";

        // Split the string down by spaces
        String[] bits = split(name, " ");

        // See how many entries we have:

        // 1 - Assume Surname
        if (bits.length == 1) {
            ret[3] = bits[0];

            return ret;
        }

        // 2 - Assume Title and Surname, unless
        // we don't have a real title, in which case assume forename and surname
        if (bits.length == 2) {
            if (isTitle(bits[0])) {
                // Title
                ret[0] = bits[0];
            } else {
                // Forenames
                ret[2] = bits[0];

                // Initials
                if (bits[0].length() > 0) {
                    ret[1] = bits[0].substring(0, 1);
                }
            }

            ret[3] = bits[1];

            return ret;
        }

        // 3 - Assume Title, Forename and Surname
        // unless it isn't a valid title, in which
        // case assume Forename, forename, surname
        if (bits.length == 3) {
            // Get the title
            if (isTitle(bits[0])) {
                // Title
                ret[0] = bits[0];

                // Forename
                ret[2] = bits[1];

                // Make an initial from the forename
                if (bits[1].length() > 0) {
                    ret[1] = bits[1].substring(0, 1);
                }
            } else {
                // Forenames
                ret[2] = bits[0] + " " + bits[1];

                // Make initials from forenames
                if ((bits[1].length() > 0) && (bits[0].length() > 0)) {
                    ret[1] = bits[0].substring(0, 1) + " " +
                        bits[1].substring(0, 1);
                }
            }

            // Now get the surname
            ret[3] = bits[2];

            return ret;
        }

        // 4 - Assume Title, Forenames X 2 and Surname
        // if not a valid title, assume forenames x 3 and surname
        if (bits.length >= 4) {
            // Get the title
            if (isTitle(bits[0])) {
                // Title
                ret[0] = bits[0];

                // Forenames
                ret[2] = bits[1] + " " + bits[2];

                // Make initials from the forenames
                if ((bits[1].length() > 0) && (bits[2].length() > 0)) {
                    ret[1] = bits[1].substring(0, 1) + " " +
                        bits[2].substring(0, 1);
                }
            } else {
                // Forenames
                ret[2] = bits[0] + " " + bits[1] + " " + bits[2];

                // Make initials from the forenames
                if ((bits[1].length() > 0) && (bits[2].length() > 0) &&
                        (bits[0].length() > 0)) {
                    ret[1] = bits[0].substring(0, 1) + " " +
                        bits[1].substring(0, 1) + " " +
                        bits[2].substring(0, 1);
                }
            }

            // Now get the surname
            ret[3] = bits[3];

            return ret;
        }

        return ret;
    }

    /**
     * Determines whether a given string contains
     * a valid title (Mr, Mrs etc.)
     * @param title The string to test
     * @return True if the string is a valid title
     */
    public static boolean isTitle(String title) {
        // Strip trailing dot if they have used Dr. or Mr. etc.
        if (title.endsWith(".")) {
            title = title.substring(0, title.length() - 1);
        }

        // Check titles
        if (title.equalsIgnoreCase("Mr")) {
            return true;
        }

        if (title.equalsIgnoreCase("Master")) {
            return true;
        }

        if (title.equalsIgnoreCase("Mrs")) {
            return true;
        }

        if (title.equalsIgnoreCase("Miss")) {
            return true;
        }

        if (title.equalsIgnoreCase("Ms")) {
            return true;
        }

        if (title.equalsIgnoreCase("Dr")) {
            return true;
        }

        if (title.equalsIgnoreCase("Doctor")) {
            return true;
        }

        if (title.equalsIgnoreCase("Captain")) {
            return true;
        }

        if (title.equalsIgnoreCase("Cpt")) {
            return true;
        }

        if (title.equalsIgnoreCase("Corporal")) {
            return true;
        }

        if (title.equalsIgnoreCase("Cpl")) {
            return true;
        }

        if (title.equalsIgnoreCase("Lieutenant")) {
            return true;
        }

        if (title.equalsIgnoreCase("Lt")) {
            return true;
        }

        if (title.equalsIgnoreCase("Chief")) {
            return true;
        }

        if (title.equalsIgnoreCase("Detective")) {
            return true;
        }

        if (title.equalsIgnoreCase("Dt")) {
            return true;
        }

        if (title.equalsIgnoreCase("Sergeant")) {
            return true;
        }

        if (title.equalsIgnoreCase("Sgt")) {
            return true;
        }

        if (title.equalsIgnoreCase("Major")) {
            return true;
        }

        if (title.equalsIgnoreCase("Mjr")) {
            return true;
        }

        if (title.equalsIgnoreCase("Aco")) {
            return true;
        }

        return false;
    }

    /**
     * Determines if this box is connected to the internet by
     * initiating a connection to google
     * @return true if this machine is connected to the internet
     */
    public static boolean isConnectedToInternet() {
        try {
            java.net.Socket s = new java.net.Socket("www.google.com", 80);
            s.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Computes the number of minutes between
     * two Dates.
     *
     * @param  d1  the first Date
     * @param  d2  the second Date
     *
     * @return the absolute value of the number
     * of minutes
     *
     * @exception  NullPointerException if either
     * Date null
     */
    public static long getDateDiff(Calendar d1, Calendar d2) {
        //assert d1 != null && d2 != null;
        if ((d1 == null) || (d2 == null)) {
            throw new NullPointerException("d1 or d2 null");
        }

        // get the difference in milliseconds and
        // take the absolute value
        long diff = d1.getTime().getTime() - d2.getTime().getTime();

        // convert milliseconds to seconds 
        // and then to minutes
        long res = diff / (1000 * 60);

        return res;
    }

    /**
     * Because Windows uses the backslash \ as it's
     * file system separator, it causes problems with
     * Java in general. Instead, everywhere that
     * previously used a backslash for Windows paths should
     * use a double-forward slash (//) instead.
     * This routine converts them back to backslashes and
     * is used by the media, reporting and word processor routines.
     */
    public static String getWindowsFilePath(String path) {
        return replace(path, "//", "\\");
    }

    /**
     * Rounds a given double to any number of decimal places.
     * Note that the decimal places supplied are only used
     * for a cut off limit. This routine will not pad
     * zeroes for long decimal places.
     * @param val The value to round
     * @param decimalPlaces The number of decimal places
     * @return The rounded figure
     */
    public static double round(double val, int decimalPlaces) {
        val = val * (Math.pow(10, (double) decimalPlaces));

        long intval = Math.round(val);
        val = (double) intval;
        val = val / (Math.pow(10, (double) decimalPlaces));

        return val;
    }

    /**
      * Separates an address into component parts by
     *  splitting them into a separate array by
     *  line breaks.
     *  No matter how many entries there are in the
     *  address, it always returns an array of 5 elements.
     *  @param address The address
     *  @return An array containing the address
     */
    public static String[] separateAddress(String address) {
        String[] output = new String[5];

        // Remove superfluous commas
        address = replace(address, ",", "");

        // Get the address with commas instead (easier to break)
        String add = formatAddress(address);

        // Now split it on the commas
        String[] spl = split(add, ",");

        // Copy it into the output array
        int i = 0;

        for (i = 0; i < spl.length; i++) {
            // If it already had commas or something strange like that,
            // then we could run out of array.
            if (i < 5) {
                output[i] = spl[i];
            }
        }

        // Pad out remaining
        while (i < 5) {
            output[i] = "";
            i++;
        }

        // Remove all white space
        for (i = 0; i < 5; i++) {
            output[i] = output[i].trim();
        }

        return output;
    }

    /** Writes the given contents to the given file */
    public static void writeFile(String filename, byte[] contents)
        throws IOException {
        FileOutputStream out = new FileOutputStream(filename);
        out.write(contents);
        out.flush();
        out.close();
    }

    /**
     * Reads a complete file into a string and returns it
     */
    public static String readFile(String filename)
        throws IOException, FileNotFoundException {
        File file = new File(filename);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                    file));
        byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();

        return new String(bytes, "UTF8");
    }

    /**
     * Unescapes unicode \u0000 sequences
     */
    public static String unescapeUnicode(String s) {
        int i = 0;
        int len = s.length();
        char c;
        StringBuffer sb = new StringBuffer(len);

        while (i < len) {
            c = s.charAt(i++);

            if (c == '\\') {
                if (i < len) {
                    c = s.charAt(i++);

                    if (c == 'u') {
                        try {
                            c = (char) Integer.parseInt(s.substring(i, i + 4),
                                    16);
                        } catch (Exception e) {
                            Global.logError(
                                "Failed to parse unicode escape sequence: " +
                                s.substring(i, i + 4), "Utils.unescapeUnicode");
                        }

                        i += 4;
                    } // add other cases here as desired...
                }
            } // fall through: \ escapes itself, quotes any character but u

            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Reads a complete file into memory from a FileInputStream
     * and returns it as an array of strings, broken by carriage
     * returns - It will unescape any \u0000 sequences of characters,
     * but the file cannot contain anything but 7-bit ASCII.
     * @param fs The <code>FileInputStream</code> to read from.
     */
    public static String[] readFile(FileInputStream fs) {
        // Reads a complete file of data from a file, throwing away the
        // Chr(13) and Chr(10)s.
        Vector output = new Vector();
        StringBuffer sb = new StringBuffer("");
        Integer iob;
        int nb = -1;
        boolean readabyte = false;

        while (true) {
            // Read next byte from stream
            try {
                nb = fs.read();
            } catch (Exception e) {
                return null;
            }

            // if we have a 10 and nothing else so far,
            // simply ignore it and carry on
            if ((nb == 10) && !readabyte) {
                try {
                    nb = fs.read();
                } catch (Exception e) {
                    return null;
                }
            }

            // if it's a 13 or a 10, add the line
            // and clear the buffer
            if ((nb == 13) || (nb == 10)) {
                output.add(sb.toString());
                sb = new StringBuffer("");
            }

            if (nb == -1) {
                // no more data - return what we have
                output.add(sb.toString());

                Object[] obs = output.toArray();
                String[] str = new String[obs.length];

                for (int i = 0; i < obs.length; i++) {
                    str[i] = (String) obs[i];
                }

                return str;
            }

            // Otherwise, append into the string buffer
            readabyte = true;
            iob = new Integer(nb);

            byte[] by = { iob.byteValue() };
            sb.append(new String(by));
        }
    }

    /**
     * UK =============
     * Returns the area (first portion) of a given
     * postcode field. If the postcode is not valid
     * (ie. is more than 4 chars long, or does not
     * contain any numbers), then an empty string
     * is returned.
     *
     * US =============
     * Returns an empty string if the zipcode is not
     * 5 digits (ie. valid), otherwise returns the
     * zip code.
     *
     * Other ==========
     * Returns the input string.
     *
     * @param fld The field data to check
     * @return The area code.
     */
    public static String getAreaPostcode(String fld) {
        // Return empty string if the postcode
        // is empty or null
        if ((fld == null) || fld.equals("")) {
            return "";
        }

        // =====================================
        // BRITISH POSTCODES
        // =====================================
        if (Locale.getDefault().equals(Locale.UK)) {
            // Get the first bit if there is a space
            if (fld.indexOf(" ") > -1) {
                fld = fld.substring(0, fld.indexOf(" "));
            }

            // Check the length of the area code - is it larger than
            // four characters? Crap out if it is
            if (fld.length() > 4) {
                return "";
            }

            // Does it contain at least one number?
            boolean containsNum = ((fld.indexOf("0") != -1) ||
                (fld.indexOf("1") != -1) || (fld.indexOf("2") != -1) ||
                (fld.indexOf("3") != -1) || (fld.indexOf("4") != -1) ||
                (fld.indexOf("5") != -1) || (fld.indexOf("6") != -1) ||
                (fld.indexOf("7") != -1) || (fld.indexOf("8") != -1) ||
                (fld.indexOf("9") != -1));

            // If so, return the area code - it is valid
            if (containsNum) {
                return fld;
            } else {
                // It isn't valid, return an empty string
                return "";
            }
        } else if (Locale.getDefault().equals(Locale.US)) {
            // ===============================================
            // US ZipCodes
            // ===============================================

            // Bomb if it's anything other than 5 digits
            if (fld.length() != 5) {
                return "";
            }

            // Return the valid zip code
            return fld;
        } else {
            // ===============================================
            // Anything else
            // ===============================================
            return fld;
        }
    }

    /** Sorts a vector of strings into alphabetical order */
    public static void sortVectorOfStrings(Vector v) {
        boolean done = false;

        while (!done) {
            done = true;

            for (int i = 0; i < (v.size() - 1); i++) {
                if (v.get(i).toString().compareTo(v.get(i + 1).toString()) > 0) {
                    String s1 = v.get(i).toString();
                    String s2 = v.get(i + 1).toString();
                    v.set(i, s2);
                    v.set(i + 1, s1);
                    done = false;
                }
            }
        }
    }

    /** Returns the default document path for a platform */
    public static String getDefaultDocumentPath() {
        // Default the last path according to platform:
        // Linux = $HOME
        // MacOSX = $HOME/Pictures
        // Windows = $HOME/My Pictures
        if (UI.osIsMacOSX()) {
            return System.getProperty("user.home") + File.separator +
            "Pictures";
        } else if (UI.osIsWindows()) {
            return System.getProperty("user.home") + File.separator +
            "My Pictures";
        } else {
            return System.getProperty("user.home");
        }
    }

    /** Sanitises a URL */
    public static String urlEncode(String url) {
        url = Utils.replace(url, "%", "%25");
        url = Utils.replace(url, ",", "%2C");
        url = Utils.replace(url, "\"", "%22");
        url = Utils.replace(url, "<", "%3C");
        url = Utils.replace(url, ">", "%3E");
        url = Utils.replace(url, "#", "%23");
        url = Utils.replace(url, "{", "%7B");
        url = Utils.replace(url, "}", "%7D");
        url = Utils.replace(url, "|", "%7C");
        url = Utils.replace(url, "\\", "%5C");
        url = Utils.replace(url, "^", "%5E");
        url = Utils.replace(url, "~", "%7E");
        url = Utils.replace(url, "[", "%5B");
        url = Utils.replace(url, "]", "%5D");
        url = Utils.replace(url, "`", "%60");
        url = url.replace(' ', '+');

        return url;
    }

    /** Returns the contents of a URL as a string */
    public static String getURL(String url) throws Exception {
        Global.logDebug("Requesting " + url + " ...", "Utils.getURL");

        URL u = new URL(url);
        InputStream is = u.openStream();
        DataInputStream d = new DataInputStream(new BufferedInputStream(is));
        String s;
        StringBuffer c = new StringBuffer();

        while ((s = d.readLine()) != null) {
            c.append(s).append("\n");
        }

        try {
            is.close();
        } catch (Exception e) {
        }

        Global.logDebug("Response: " + c.length() + " characters",
            "Utils.getURL");

        return c.toString();
    }

    /** Returns the upper case form of a string - If the database is a sheltermanager
      * account we know the encoding is ascii, so we should only upper case the
      * characters present in ascii and leave the rest alone - this prevents problems
      * with people doing searches over data containing extended characters
      */
    public static String upper(String s) {
        if (!testedSM) {
            isSM = DBConnection.url.indexOf("sheltermanager.com") != -1;
            testedSM = true;
        }

        if (!isSM) {
            return s.toUpperCase();
        }

        StringBuffer b = new StringBuffer();
        String asciiLower = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < s.length(); i++) {
            String ch = s.substring(i, i + 1);

            if (asciiLower.indexOf(ch) != -1) {
                b.append(ch.toUpperCase());
            } else {
                b.append(ch);
            }
        }

        return b.toString();
    }

    /**
     * Executes a command
     * @param args An array of the command and its args
     * @return The process exit code
     */
    public static int exec(String[] args) {
        return exec(args, null);
    }

    /**
     * Executes a command.
     * @param args An array of the command and its arguments
     * @param wd The working directory as a java File
     * @return The process exit code
     */
    public static int exec(String[] args, File wd) {
        try {
            Global.logDebug("Exec: " + args, "Utils.exec");

            Runtime r = Runtime.getRuntime();
            Process p = null;

            if (wd != null) {
                p = r.exec(args, null, wd);
            } else {
                p = r.exec(args);
            }

            StreamConsumer stdin = new StreamConsumer("stdin",
                    p.getInputStream());
            StreamConsumer stderr = new StreamConsumer("stderr",
                    p.getErrorStream());
            stdin.start();
            stderr.start();
            p.waitFor();

            return p.exitValue();
        } catch (Exception e) {
            Global.logException(e, Utils.class);
        }

        return 1;
    }

    /**
     * Executes a command asynchronously and does not wait
     * for completion or an exit code
     * @param args The command arguments
     */
    public static void execAsync(String[] args) {
        try {
            Global.logDebug("Exec: " + args, "Utils.execAsync");

            Runtime r = Runtime.getRuntime();
            r.exec(args);
        } catch (Exception e) {
            Global.logException(e, Utils.class);
        }
    }

    public class FTPDirEntry {
        public String raw;
        public String name;
        public boolean isDirectory = false;
    }

    /**
     * Class to read and dump output on a new thread
     */
    public static class StreamConsumer implements Runnable {
        String name;
        InputStream is;
        Thread t;

        public StreamConsumer(String name, InputStream is) {
            this.name = name;
            this.is = is;
        }

        public void start() {
            t = new Thread(this);
            t.start();
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                while (true) {
                    String s = br.readLine();

                    if (s == null) {
                        break;
                    }

                    Global.logDebug("[" + name + "] " + s, "StreamConsumer.run");
                }

                is.close();
            } catch (Exception e) {
                Global.logException(e, StreamConsumer.class);
            }
        }
    }
}
