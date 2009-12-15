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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.utility.Utils;


/**
 * Generates a report from the waiting list screen table model.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class WaitingList extends Report {
    private SortableTableModel tablemodel = null;

    public WaitingList(SortableTableModel model) {
        tablemodel = model;
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "Waiting_List_generated_on_",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        // Generate table header
        tableNew();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Rank")));
        tableAddCell(bold(Global.i18n("reports", "Name")));
        tableAddCell(bold(Global.i18n("reports", "Address")));
        tableAddCell(bold(Global.i18n("reports", "Telephone")));
        tableAddCell(bold(Global.i18n("reports", "Date_Put_On")));
        tableAddCell(bold(Global.i18n("reports", "Date_Removed")));
        tableAddCell(bold(Global.i18n("reports", "Urgency")));
        tableAddCell(bold(Global.i18n("reports", "Animal_Species")));
        tableFinishRow();

        // Loop through every row in the model
        setStatusBarMax(tablemodel.getRowCount());

        for (int i = 0; i < (tablemodel.getRowCount()); i++) {
            tableAddRow();

            for (int z = 0; z < (tablemodel.getColumnCount() - 1); z++) {
                String val = (String) tablemodel.getValueAt(i, z);
                tableAddCell(val);
            }

            tableFinishRow();
            incrementStatusBar();
        }

        tableFinish();

        addTable();
    }
}
