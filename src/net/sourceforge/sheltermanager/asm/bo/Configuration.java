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
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Hashtable;


/**
 * Class for handling configuration from the database - when
 * first accessed, an in memory cache of the table is created
 * and used thereafter. Updating an entry updates the database
 * straight away and the cache.
 */
public class Configuration {
    private static Hashtable conf = null;

    static {
        loadFromDatabase();
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
            conf = new Hashtable();

            SQLRecordset r = new SQLRecordset();
            r.openRecordset("SELECT * FROM configuration", "configuration");

            while (!r.getEOF()) {
                conf.put((String) r.getField("ItemName"),
                    (String) r.getField("ItemValue"));
                r.moveNext();
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
        SQLRecordset rs = new SQLRecordset();
        conf.put(key, value);

        try {
            rs.openRecordset(
                "SELECT * FROM configuration WHERE ItemName LIKE '" + key +
                "'", "configuration");
        } catch (Exception e) {
            rs.free();
            rs = null;
            Global.logException(e, Configuration.class);
        }

        if (!rs.getEOF()) {
            String sql = "UPDATE configuration SET ItemValue = '" + value +
                "' WHERE ItemName LIKE '" + key + "'";

            try {
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                Global.logException(e, Configuration.class);
            }
        } else {
            String sql = "INSERT INTO configuration (ItemName, ItemValue) VALUES (";
            sql += ("'" + key + "', '" + value + "')");

            try {
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                Global.logException(e, Configuration.class);
            }
        }
    }
}
