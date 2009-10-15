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
package net.sourceforge.sheltermanager.asm.ui.ui;


/*
import swingwtx.swing.table.TableColumnModel;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.*;
*/
import swingwtx.swing.JTable;


/**
 * Stores information about JTables in the database for a particular
 * screen and user.
 *
 * NO LONGER USED
 *
 * @version 1.0
 * @author Robin Rawson-Tetley
 */
public abstract class TablePrefs {
    /**
     * Stores user preferences for a given table in the database.
     * @param table The table we are storing coloumn pos/widths for
     * @param userName The user to store it against
     * @param tableID A unique identifier for this table
     */
    public static void storePrefs(JTable table, String userName, String tableID) {
        /*
        try {
            // Make a string, containing all the widths for this table,
            // comma separated
            TableColumnModel tcm = table.getTableHeader().getColumnModel();

            // Drop out if there is no data
            if (tcm.getColumnCount() == 0) {
                return;
            }

            String widths = "";

            for (int i = 0; i < tcm.getColumnCount(); i++) {
                if (!widths.equals("")) {
                    widths += ",";
                }

                //widths += tcm.getColumn(i).getWidth();
            }

            // Make another string, containing the index positions of
            // each column in the table - also comma separated.
            String positions = "";

            for (int i = 0; i < tcm.getColumnCount(); i++) {
                if (!positions.equals("")) {
                    positions += ",";
                }

                positions += tcm.getColumn(i).getModelIndex();
            }

            // Yet another string, containing the column values for
            // each column in the table
            String names = "";

            for (int i = 0; i < tcm.getColumnCount(); i++) {
                if (!names.equals("")) {
                    names += ",";
                }

                //names += (String) tcm.getColumn(i).getHeaderValue();
            }

            // Now store them
            Configuration.setEntry(userName + "_" + tableID,
                widths + "|" + positions + "|" + names);
        } catch (Exception e) {
            Global.logException(e, Email.class);
        }
         * */
    }

    /**
     * Restores user preferences for a given table in the database.
     * @param table The table we are retreiving coloumn pos/widths for
     * @param userName The user part of the key
     * @param tableID A unique identifier for this table
     */
    public static void restorePrefs(JTable table, String userName,
        String tableID) {
        /*
        try {
            // Retrieve any existing setting
            String existing = Configuration.getString(userName + "_" + tableID);

            // If there aren't any, forget it
            if (existing.equals("")) {
                return;
            }

            // Get the widths and positions
            String[] bits = Utils.split(existing, "|");
            String widths = bits[0];
            String positions = bits[1];
            String names = bits[2];

            String[] w = Utils.split(widths, ",");
            String[] p = Utils.split(positions, ",");
            String[] n = Utils.split(names, ",");

            // Get the model
            TableColumnModel tcm = table.getTableHeader().getColumnModel();

            // If we have a mismatch (more prefs than table), forget
            // it, or we risk shafting the viewer
            if (w.length != tcm.getColumnCount()) {
                return;
            }

            // Restore them onto the table
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                tcm.getColumn(i).setModelIndex(Integer.parseInt(p[i]));

                //tcm.getColumn(i).setHeaderValue(n[i]);
                //tcm.getColumn(i).setPreferredWidth(Integer.parseInt(w[i]));
            }

            // Tell the table to repaint
            table.repaint();
        } catch (Exception e) {
            Global.logException(e, Email.class);
        }
         * */
    }
}
