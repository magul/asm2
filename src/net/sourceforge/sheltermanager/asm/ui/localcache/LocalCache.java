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
package net.sourceforge.sheltermanager.asm.ui.localcache;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 * This class wraps up the functionality of logging information about files
 * copied/generated and stored in the local cache. It allows destruction of
 * items, as well as enumeration, allowing the user a much more meaningful view
 * of what they have stored locally.
 */
public class LocalCache {
    private Vector theCache = new Vector();
    private boolean fileEof = false;

    /**
     * Creates a new instance of LocalCache. It also checks the temporary
     * directory we have been given and searches for the existence of
     * "cache.data" - the index file storing all the information.
     */
    public LocalCache() {
        loadDataFromIndexFile();
    }

    public void addEntry(String filename, String description) {
        // Calculate the date and time
        Calendar cal = Calendar.getInstance();
        String showdate = Integer.toString(cal.get(Calendar.YEAR));
        showdate += ("-" + Integer.toString(cal.get(Calendar.MONTH) + 1));
        showdate += ("-" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        showdate += (" " + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));

        // If minutes are below 10, then pad them with a zero
        if (cal.get(Calendar.MINUTE) < 10) {
            showdate += ":0";
        } else {
            showdate += ":";
        }

        showdate += Integer.toString(cal.get(Calendar.MINUTE));

        theCache.add(new CacheEntry(showdate, filename, description));
        saveDataToIndexFile();
    }

    public void removeEntry(String filename) {
        for (int i = 0; i < theCache.size(); i++) {
            CacheEntry c = (CacheEntry) theCache.get(i);

            if (c.filename.equalsIgnoreCase(filename)) {
                try {
                    File f = new File(Global.tempDirectory + File.separator +
                            filename);
                    Global.logDebug("Deleting " + f.getAbsolutePath(), "LocalCache.removeAllEntries");
                    f.delete();
                    theCache.remove(c);

                    break;
                } catch (Exception e) {
                    // Ignore errors
                }
            }
        }

        saveDataToIndexFile();
    }

    public void removeAllEntries() {
        Iterator i = theCache.iterator();

        while (i.hasNext()) {
            CacheEntry c = (CacheEntry) i.next();

            try {
                if (!c.filename.trim().equals("")) {
                    File f = new File(Global.tempDirectory + File.separator +
                            c.filename);
                    Global.logDebug("Deleting " + f.getAbsolutePath(), "LocalCache.removeAllEntries");
                    f.delete();
                }
            } catch (Exception e) {
                // Ignore errors
            }
        }

        theCache.removeAllElements();
        saveDataToIndexFile();
    }

    /**
     * Loads cached data from the index file "cache.data" If none is found, the
     * cache is left blank.
     */
    public void loadDataFromIndexFile() {
        FileInputStream in = null;

        try {
            fileEof = false;
            in = new FileInputStream(new File(Global.tempDirectory +
                        File.separator + "cache.data"));

            String s = "";

            while (!fileEof) {
                // All data should be written in sets of
                // three lines -
                // 1. Date and Time
                // 2. Filename
                // 3. File description
                String date = readline(in);
                String filename = readline(in);
                String description = readline(in);

                theCache.add(new CacheEntry(date, filename, description));
            }
        } catch (FileNotFoundException e) {
            Global.logWarning(Global.i18n("uilocalcache",
                    "Warning:_Local_cache_index_not_found."),
                "LocalCache.loadDataFromIndexFile");
        } catch (Exception e) {
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    public void saveDataToIndexFile() {
        try {
            FileOutputStream out = new FileOutputStream(new File(Global.tempDirectory +
                        File.separator + "cache.data"));

            Iterator i = theCache.iterator();

            while (i.hasNext()) {
                CacheEntry c = (CacheEntry) i.next();
                writeline(out, c.datetime);
                writeline(out, c.filename);
                writeline(out, c.description);
            }

            out.close();
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uilocalcache",
                    "An_error_occurred_dumping_cache_information:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public Vector getEntries() {
        return theCache;
    }

    /**
     * Reads a complete line of data by reading from a passed file handle one
     * byte at a time until it finds a Chr(13) followed by a Chr(10).
     */
    private String readline(FileInputStream fs) {
        // Reads a complete line of data from a file, throwing away the
        // Chr(13) and Chr(10)s.
        StringBuffer sb = new StringBuffer("");
        Integer iob;
        int nb = -1;
        boolean readabyte = false;

        while (true) {
            // Read next byte from stream
            try {
                nb = fs.read();
            } catch (Exception e) {
                Global.logError(Global.i18n("uilocalcache",
                        "Error_reading_from_file_-_") + e.getMessage(),
                    "LocalCache.readline");

                return sb.toString();
            }

            // if we have a 10 and nothing else so far,
            // simply ignore it and carry on
            if ((nb == 10) && !readabyte) {
                try {
                    nb = fs.read();
                } catch (Exception e) {
                    return sb.toString();
                }
            }

            // if it's a 13 or a 10, better quit and return
            if ((nb == 13) || (nb == 10)) {
                return sb.toString();
            }

            if (nb == -1) {
                // no more data - return what we have and
                // set the eof flag.
                fileEof = true;

                return sb.toString();
            }

            // Otherwise, append our jobbie into the string buffer
            readabyte = true;
            iob = new Integer(nb);

            byte[] by = { iob.byteValue() };
            sb.append(new String(by));
        }
    }

    /** * Writes a line of text to the outputstream specified */
    private void writeline(FileOutputStream out, String s) {
        byte[] ba = s.getBytes();

        try {
            out.write(ba);

            byte[] bb = { 13, 10 };
            out.write(bb);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
