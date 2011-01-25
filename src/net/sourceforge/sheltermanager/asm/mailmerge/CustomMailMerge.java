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
package net.sourceforge.sheltermanager.asm.mailmerge;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;


/**
 * Produces a mail merge source of all members
 * @author  Robin Rawson-Tetley
 */
public class CustomMailMerge extends MailMerge {
    private String title = null;
    private String sql = null;

    public CustomMailMerge(String sql, String title) {
        this.sql = sql;
        this.title = title;
        this.start();
    }

    protected String getFileName() {
        String t = title;
        t = Utils.replace(t, " ", "");
        t = Utils.replace(t, "-", "");
        t += ".csv";

        return t;
    }

    protected void getData() throws CursorEngineException, NoDataException {
        SQLRecordset rs = new SQLRecordset();

        try {
            rs.openRecordset(sql, "owner");
        } catch (Exception e) {
            throw new CursorEngineException(e.getMessage());
        }

        if (rs.getEOF()) {
            throw new NoDataException(Global.i18n("mailmerge",
                    "There_are_no_records_to_add_to_the_mail_merge_source."));
        }

        // Set array bounds
        cols = rs.getFieldCount();
        rows = rs.size() + 1;
        theData = new String[rows][cols];

        setStatusBarMax(rows);

        // Set header
        for (int i = 1; i <= cols; i++) {
            theData[0][i - 1] = rs.getFieldName(i);

            // If there's an email column, flag it
            if (rs.getFieldName(i).toLowerCase().indexOf("email") != -1) {
                emailColumn = i;
            }
        }

        int row = 1;

        // Build data
        while (!rs.getEOF()) {
            // Fill out an entry
            for (int i = 1; i <= cols; i++) {
                String v = "";

                if (rs.getField(i) != null) {
                    v = rs.getField(i).toString();
                }

                theData[row][i - 1] = v;
            }

            row++;
            incrementStatusBar();
            rs.moveNext();
        }

        // Set highest row
        rows = row;
        resetStatusBar();
    }
}
