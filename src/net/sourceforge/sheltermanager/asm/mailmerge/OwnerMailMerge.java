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
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;


/**
 * Produces a mail merge from the owner find screen results.
 *
 * @author  Robin Rawson-Tetley
 */
public class OwnerMailMerge extends MailMerge {
    SortableTableModel model = null;
    String[] colnames = null;

    public OwnerMailMerge(SortableTableModel model, String[] colnames) {
        this.model = model;
        this.colnames = colnames;
        this.start();
    }

    protected String getFileName() {
        return "OwnerSearch.csv";
    }

    protected void getData() throws CursorEngineException, NoDataException {
        if (model.getRowCount() == 0) {
            throw new NoDataException(Global.i18n("uiowner",
                    "there_are_no_owners_matching_your_criteria"));
        }

        // Set array bounds - add a row for the CSV header
        rows = model.getRowCount() + 1;
        cols = colnames.length;
        theData = new String[(int) rows][cols];
        setStatusBarMax(rows);

        // Set header
        for (int i = 0; i < cols; i++) {
            theData[0][i] = colnames[i];

            // If there's an email column, flag it
            if (colnames[i].toLowerCase().indexOf("email") != -1) {
                emailColumn = i;
            }
        }

        // Build data
        for (int i = 1; i < rows; i++) {
            // Fill out the entry
            for (int z = 0; z < cols; z++) {
                theData[i][z] = Utils.nullToEmptyString((String) model.getValueAt(i -
                            1, z));

                // If it's an address, convert commas to breaks
                if (colnames[z].toLowerCase().indexOf("address") != -1) {
                    theData[i][z] = model.getValueAt(i - 1, z).toString()
                                         .replace(',', '\n');
                }
            }

            incrementStatusBar();
        }

        resetStatusBar();
    }
}
