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
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Class for handling configuration from the database - when
 * first accessed, an in memory cache of the table is created
 * and used thereafter. Updating an entry updates the database
 * straight away and the cache.
 */
public class Configuration {
    private static HashMap<String, String> conf = null;

    /** The time to perform the next save (since epoch) */
    private static long nextSave = 0;
    private static Timer batchSave;
    private static TimerTask batchSaveTask;

    static {
        loadFromDatabase();

        // Batch update the configuration table when told on a timer
        // so that many changes can be rolled into one batch
        batchSave = new Timer();
        batchSaveTask = new TimerTask() {
                    public void run() {
                        if ((nextSave > 0) &&
                                (nextSave < System.currentTimeMillis())) {
                            nextSave = 0;
                            saveToDatabase();
                        }
                    }
                };
        batchSave.schedule(batchSaveTask, 5000, 5000);
    }

    /** Can't create this class */
    private Configuration() {
    }

    public static String getString(String key) {
        try {
            String s = (String) conf.get(key);

            if (s == null) {
                return "";
            }

            return s;
        } catch (Exception e) {
            Global.logException(e, Configuration.class);
        }

        return "";
    }

    public static String getString(String key, String defaultValue) {
        String val = getString(key);

        if (val.equals("")) {
            return defaultValue;
        }

        return val;
    }

    public static boolean getBoolean(String key) {
        String val = getString(key);

        if (Utils.englishLower(val).equals("yes") ||
                Utils.englishLower(val).equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static int getInteger(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getInteger(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double getDouble(String key) {
        try {
            return Double.parseDouble(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getLong(String key) {
        try {
            return Long.parseLong(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }

    public static void loadFromDatabase() {
        try {
            Global.logDebug("Loading configuration...",
                "Configuration.loadFromDatabase");
            conf = new HashMap<String, String>();

            SQLRecordset recs = new SQLRecordset("SELECT * FROM configuration",
                    "configuration");

            for (SQLRecordset r : recs) {
                conf.put((String) r.getField("ItemName"),
                    (String) r.getField("ItemValue"));
            }
        } catch (Exception e) {
            Global.logException(e, Configuration.class);
        }
    }

    public static void changeKeyName(String oldkey, String newkey) {
        try {
            String sql = "UPDATE configuration SET ItemName='" + newkey +
                "' WHERE " + "ItemName Like '" + oldkey + "'";
            DBConnection.executeAction(sql);
        } catch (Exception e) {
            Global.logException(e, Configuration.class);
        }
    }

    public static void removeUserPreferences(String username) {
        try {
            String sql = "DELETE FROM configuration WHERE ItemName Like '" +
                username + "_%'";
            DBConnection.executeAction(sql);
        } catch (Exception e) {
            Global.logException(e, Configuration.class);
        }
    }

    public static void setEntry(String key, String value) {
        conf.put(key, value);
        scheduleSave();
    }

    public static void saveToDatabase() {
        Global.logDebug("Batch saving new configuration to db.",
            "Configuration.saveToDatabase");

        ArrayList<String> batch = new ArrayList<String>(conf.size());

        for (String key : conf.keySet()) {
            batch.add("DELETE FROM configuration WHERE ItemName Like '" + key +
                "'");
            batch.add("INSERT INTO configuration VALUES ('" + key + "', '" +
                conf.get(key).replace('\'', '`') + "')");
        }

        try {
            DBConnection.executeAction(batch);
            Global.logDebug("Batch save complete.",
                "Configuration.saveToDatabase");
        } catch (Exception e) {
            Global.logException(e, Configuration.class);
        }
    }

    /** Schedule the next save of the configuration table for 5 seconds
      * from now (keep moving it every time a value is changed) */
    public static void scheduleSave() {
        nextSave = System.currentTimeMillis() + 5000;
    }
}
