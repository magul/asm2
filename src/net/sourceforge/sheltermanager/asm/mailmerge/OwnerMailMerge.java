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
package net.sourceforge.sheltermanager.asm.mailmerge;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;


/**
 * Produces a mail merge source of all animals
 * who have been adopted in the last 3 months and
 * not returned.
 *
 * @author  Robin Rawson-Tetley
 */
public class OwnerMailMerge extends MailMerge {
    SortableTableModel model = null;

    public OwnerMailMerge(SortableTableModel tablemodel) {
        model = tablemodel;
        this.start();
    }

    protected String getFileName() {
        return "OwnerSearch.csv";
    }

    protected int getEmailColumn() {
        return 1;
    }

    protected void getData() throws CursorEngineException, NoDataException {
        if (model.getRowCount() == 0) {
            throw new NoDataException(Global.i18n("uiowner",
                    "there_are_no_owners_matching_your_criteria"));
        }

        // Set array bounds
        cols = 8;
        rows = model.getRowCount() + 1;
        theData = new String[(int) rows][cols];

        setStatusBarMax(rows);

        // Set header
        theData[0][0] = Global.i18n("mailmerge", "Name");
        theData[0][1] = Global.i18n("mailmerge", "Email_Address");
        theData[0][2] = Global.i18n("mailmerge", "Address");
        theData[0][3] = Global.i18n("mailmerge", "Town");
        theData[0][4] = Global.i18n("mailmerge", "County");
        theData[0][5] = Global.i18n("mailmerge", "Postcode");
        theData[0][6] = Global.i18n("mailmerge", "Home_Telephone");
        theData[0][7] = Global.i18n("mailmerge", "Work_Telephone");

        // Build data
        for (int i = 0; i < (rows - 1); i++) {
            // Fill out the entry

            // Convert owner address back to breaks
            String address = (String) model.getValueAt(i, 4);
            address = address.replace(',', '\n');

            theData[i + 1][0] = (String) model.getValueAt(i, 0);
            theData[i + 1][1] = (String) model.getValueAt(i, 11);
            theData[i + 1][2] = address;
            theData[i + 1][3] = (String) model.getValueAt(i, 5);
            theData[i + 1][4] = (String) model.getValueAt(i, 6);
            theData[i + 1][5] = (String) model.getValueAt(i, 7);
            theData[i + 1][6] = (String) model.getValueAt(i, 8);
            theData[i + 1][7] = (String) model.getValueAt(i, 9);

            incrementStatusBar();
        }

        resetStatusBar();
        model = null;
    }
}
