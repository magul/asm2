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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;


/**
 * Generates a report from owner
 * search results.
 *
 * @author Robin Rawson-Tetley
 */
public class OwnerSearchResults extends Report {
    private String[][] searchResults = null;
    private String searchTerm;
    private int max = 0;

    /** Creates a new instance of SearchResults */
    public OwnerSearchResults(String[][] searchResults, int max,
        String searchTerm) {
        this.max = max;
        this.searchResults = searchResults;
        this.searchTerm = searchTerm;

        if (searchResults == null) {
            return;
        }

        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "owner_search_results",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
    	if ((searchTerm != null) && !searchTerm.equals("")) {
            addParagraph(bold(Global.i18n("reports", "criteria") + ": <br />") +
                searchTerm);
            addHorizontalRule();
        }

        tableNew();

        String[] headers = {
                Global.i18n("reports", "Name"),
                Global.i18n("uiowner", "Surname"),
                Global.i18n("uiowner", "Banned"),
                Global.i18n("uiowner", "Homechecked"),
                Global.i18n("uiowner", "Address"),
                Global.i18n("uiowner", "Town"), Global.i18n("uiowner", "County"),
                Global.i18n("uiowner", "Postcode"),
                Global.i18n("uiowner", "Home_Tel"),
                Global.i18n("uiowner", "Work_Tel"),
                Global.i18n("uiowner", "Mobile_Te"),
                Global.i18n("uiowner", "email")
            };

        tableAddRow();

        for (int i = 0; i < headers.length; i++) {
            tableAddCell(bold(headers[i]));
        }

        tableFinishRow();
        setStatusBarMax(max);

        for (int i = 0; i < max; i++) {
            tableAddRow();

            for (int z = 0; z < 12; z++) {
                tableAddCell(searchResults[i][z]);
            }

            tableFinishRow();
            incrementStatusBar();
        }

        tableFinish();
        addTable();

        addParagraph(Global.i18n("reports", "Total__", Integer.toString(max)));
    }
}
