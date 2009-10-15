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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Date;


/**
 * Generates a report showing animals transferred into the shelter between two
 * dates
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class TransferIn extends Report {
    private Date from = new Date();
    private Date to = new Date();
    private String sqlFrom = "";
    private String sqlTo = "";

    /** Creates a new instance of TransferIn */
    public TransferIn(Date from, Date to) {
        this.from = from;
        this.to = to;
        sqlFrom = Utils.getSQLDateOnly(from);
        sqlTo = Utils.getSQLDateOnly(to);
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "Animals_Transferred_In_between_",
            Utils.formatDateLong(from), Utils.formatDateLong(to));
    }

    public void generateReport() {
        try {
            Animal theA = new Animal();
            theA.openRecordset("DateBroughtIn >= '" + sqlFrom +
                "' AND DateBroughtIn <= '" + sqlTo +
                "' AND IsTransfer = 1 ORDER BY DateBroughtIn");

            setStatusBarMax((int) theA.getRecordCount());

            if (!theA.getEOF()) {
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Brought_In")));
                tableAddCell(bold(Global.i18n("reports", "Code")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Brought_In_By")));
                tableFinishRow();

                while (!theA.getEOF()) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theA.getDateBroughtIn()));
                    tableAddCell(theA.getShelterCode());
                    tableAddCell(theA.getReportAnimalName());
                    tableAddCell(theA.getAnimalTypeName());
                    tableAddCell(theA.getSpeciesName());
                    tableAddCell(theA.getShelterLocationName());

                    try {
                        tableAddCell(theA.getBroughtInByOwner().getOwnerName());
                    } catch (Exception e) {
                        tableAddCell("");
                    }

                    tableFinishRow();

                    incrementStatusBar();
                    theA.moveNext();
                }

                tableFinish();
                addTable();
                addParagraph(bold(Global.i18n("reports", "Total__",
                            Long.toString(theA.getRecordCount()))));
            } else {
                addParagraph(Global.i18n("reports", "No_animals_found."));
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "An_error_occurred_generating_the_report", e.getMessage()));
            Global.logException(e, getClass());
        }
    }
}
