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
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFindColumns;
import net.sourceforge.sheltermanager.asm.utility.Utils;


/**
 * Generates a report from animal search results.
 *
 */
public class SearchResults extends Report {
    private String[][] searchResults = null;
    private String searchTerm;
    private int max = 0;
    private int lastcol = 15;
    private int namecol = 14;

    public SearchResults(String[][] searchResults, int max, int lastcol, int namecol, String searchTerm) {
        this.max = max;
        this.lastcol = lastcol;
        this.namecol = namecol;
        this.searchResults = searchResults;
        this.searchTerm = searchTerm;

        if (searchResults == null) {
            return;
        }

        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "Animal_Search_Results_",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        if ((searchTerm != null) && !searchTerm.equals("")) {
            addParagraph(bold(Global.i18n("reports", "criteria") + ": <br />") +
                searchTerm);
            addHorizontalRule();
        }

        tableNew();

        String[] headers = AnimalFindColumns.getColumnLabels();
        tableAddRow();
        for (int i = 0; i < headers.length; i++) {
            tableAddCell(bold(headers[i]));
        }

        tableFinishRow();

        setStatusBarMax(max);

        for (int i = 0; i < max; i++) {
            tableAddRow();

            for (int z = 0; z < lastcol; z++) {
                if (AnimalFindColumns.getColumnName(z).equals("AnimalName")) {
                    tableAddCell(searchResults[i][namecol]);
                } else {
                    tableAddCell(searchResults[i][z]);
                }
            }

            tableFinishRow();
            incrementStatusBar();
        }

        tableFinish();
        addTable();

        addParagraph(Global.i18n("reports", "Total__", Integer.toString(max)));
    }
}
