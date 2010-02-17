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

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;


/**
 * Generates many different reports showing interesting stats. These reports
 * don't really have much to do with day-to-day running of the shelter, however
 * some of the information is useful.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class Stats extends Report {
    public static final int MOST_COMMON_NAME = 0;
    public static final int DECEASED_REASONS = 1;
    public static final int MOST_COMMON_ENTRY_AREA = 2;
    public static final int MOST_COMMON_ADOPTION_AREA = 3;
    public static final int AVERAGE_WAITING_LIST = 4;
    private int reportType = 0;

    /** Creates a new instance of TransferIn */
    public Stats(int reportType) {
        this.reportType = reportType;

        // Custom critieria
        switch (reportType) {
        }

        this.start();
    }

    public String getTitle() {
        switch (reportType) {
        case MOST_COMMON_NAME:
            return Global.i18n("reports", "Most_Common_Animal_Names");

        case DECEASED_REASONS:
            return Global.i18n("reports", "Deceased_Reasons");

        case MOST_COMMON_ENTRY_AREA:
            return Global.i18n("reports", "Most_Common_Animal_Entry_Areas");

        case MOST_COMMON_ADOPTION_AREA:
            return Global.i18n("reports", "Most_Common_Animal_Adoption_Areas");

        case AVERAGE_WAITING_LIST:
            return Global.i18n("reports", "Average_Time_On_Waiting_List");
        }

        return "";
    }

    public void generateReport() {
        try {
            switch (reportType) {
            case MOST_COMMON_NAME:
                statsMostCommonName();

                break;

            case DECEASED_REASONS:
                statsDeceasedReasons();

                break;

            case MOST_COMMON_ENTRY_AREA:
                statsMostCommonEntryArea();

                break;

            case MOST_COMMON_ADOPTION_AREA:
                statsMostCommonAdoptionArea();

                break;

            case AVERAGE_WAITING_LIST:
                statsAverageWaitingList();

                break;
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Calculates a list of the most common names on the shelter, along with the
     * number of animals that have them.
     */
    private void statsMostCommonName() throws Exception {
        tableNew();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Name")));
        tableAddCell(bold(Global.i18n("reports", "Occurrences")));
        tableAddCell(bold(Global.i18n("reports", "Rank")));
        tableFinishRow();

        SQLRecordset rs = new SQLRecordset();
        rs.openRecordset(
            "SELECT AnimalName, Count(AnimalName) AS Total FROM animal GROUP BY AnimalName " +
            "HAVING Count(AnimalName) > 1", "animal");

        // Read the results into an array. This is due to a limitation in the
        // MySQL grouping stuff (ie. You need to group by the name to get the
        // total, but
        // you need it ordering by the total in descending order).
        setStatusText(Global.i18n("reports", "Retrieving_data..."));

        String[] names = new String[(int) rs.getRecordCount()];
        int[] totals = new int[(int) rs.getRecordCount()];
        int i = 0;

        while (!rs.getEOF()) {
            names[i] = rs.getField("AnimalName").toString();
            totals[i] = Integer.parseInt(rs.getField("Total").toString());
            i++;
            rs.moveNext();
        }

        // We then bubblesort the array, ordering on the occurrences. If MySQL
        // supported subqueries then it wouldn't be a problem.
        setStatusText(Global.i18n("reports", "Sorting..."));

        boolean inOrder = false;
        int totBuff = 0;
        String nameBuff = "";

        while (!inOrder) {
            inOrder = true;

            for (i = 0; i < (names.length - 1); i++) {
                if (totals[i] < totals[i + 1]) {
                    totBuff = totals[i + 1];
                    nameBuff = names[i + 1];
                    totals[i + 1] = totals[i];
                    names[i + 1] = names[i];
                    totals[i] = totBuff;
                    names[i] = nameBuff;
                    inOrder = false;
                }
            }
        }

        // Output the information
        setStatusText(Global.i18n("reports", "Building_report..."));
        setStatusBarMax(names.length);

        for (i = 0; i < names.length; i++) {
            tableAddRow();
            tableAddCell(names[i]);
            tableAddCell(Integer.toString(totals[i]));
            tableAddCell(Integer.toString(i + 1));
            tableFinishRow();
            incrementStatusBar();
        }

        tableFinish();

        // Add totals at top
        addParagraph(Global.i18n("reports",
                "_names_have_been_allocated_to_more_than_one_animal.",
                Integer.toString(totals.length)));

        // Calculate remainder
        SQLRecordset rsT = new SQLRecordset();
        rsT.openRecordset("SELECT Count(ID) as Total From animal", "animal");

        int remainder = Integer.parseInt(rsT.getField("Total").toString());

        // Calculate total unique names
        rsT.openRecordset("SELECT DISTINCT AnimalName FROM animal", "animal");

        int totuniq = (int) rsT.getRecordCount();
        rsT = null;

        addParagraph(Global.i18n("reports", "of_names_in_the_system_unique",
                Integer.toString(remainder), Integer.toString(totuniq)));

        // Show table
        addTable();
    }

    private void statsDeceasedReasons() throws Exception {
        SQLRecordset er = LookupCache.getEntryReasonLookup();
        er.moveFirst();

        setStatusBarMax((int) er.getRecordCount());

        tableNew();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Reason")));
        tableAddCell(bold(Global.i18n("reports", "Total")));
        tableFinishRow();

        int cumtotal = 0;

        while (!er.getEOF()) {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset(
                "SELECT COUNT(*) AS Tot FROM animal WHERE DeceasedDate Is Not Null AND " +
                "DiedOffShelter = 0 AND PTSReasonID = " + er.getField("ID"),
                "animal");

            int total = 0;

            try {
                total = Integer.parseInt(rs.getField("Tot").toString());
            } catch (Exception e) {
            }

            rs.free();
            rs = null;

            cumtotal += total;

            tableAddRow();
            tableAddCell(er.getField("ReasonName").toString());
            tableAddCell(Integer.toString(total));
            tableFinishRow();

            er.moveNext();
            incrementStatusBar();
        }

        tableFinish();
        addTable();

        addParagraph(Global.i18n("reports",
                "_animals_have_died_on_the_shelter.", Integer.toString(cumtotal)));
    }

    public void statsMostCommonEntryArea() throws Exception {
        boolean includeBlanks = Dialog.showYesNo(Global.i18n("reports",
                    "Do_you_want_to_include_blank_postcodes_in_this_report?"),
                Global.i18n("reports", "Blank_Postcodes"));

        tableNew();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Postcode_Area")));
        tableAddCell(bold(Global.i18n("reports", "Occurrences")));
        tableFinishRow();

        // Get all non-blank postcode areas
        SQLRecordset a = new SQLRecordset();
        a.openRecordset("SELECT owner.OwnerPostcode FROM owner INNER JOIN animal ON animal.OriginalOwnerID = owner.ID WHERE owner.OwnerPostcode Is Not Null AND owner.OwnerPostcode <> ''",
            "owner");

        // Build a vector of unique postcode areas, along with the
        // number of times a particular area has occurred.
        Vector areas = new Vector((int) (a.getRecordCount() / 2));

        setStatusText(Global.i18n("reports", "Retrieving_data..."));
        setStatusBarMax((int) a.getRecordCount());

        while (!a.getEOF()) {
            String theArea = "";

            try {
                theArea = Utils.getAreaPostcode(a.getField("OwnerPostcode")
                                                 .toString());
            } catch (Exception e) {
            }

            // Exclude blanks
            if ((includeBlanks) ||
                    (!includeBlanks && !theArea.trim().equals(""))) {
                // Do we have an entry already for this particular
                // area?
                boolean alreadyGot = false;
                Iterator it = areas.iterator();

                while (it.hasNext()) {
                    PostcodeAreaCount pc = (PostcodeAreaCount) it.next();

                    if (pc.area.equalsIgnoreCase(theArea)) {
                        alreadyGot = true;
                        // Increment the count
                        pc.count++;
                    }
                }

                if (!alreadyGot) {
                    // Create a new one
                    PostcodeAreaCount pc = new PostcodeAreaCount();
                    pc.area = theArea;
                    pc.count = 1;
                    areas.add(pc);
                }
            }

            a.moveNext();
            incrementStatusBar();
        }

        // We then bubblesort the array, ordering on the occurrences.
        setStatusText(Global.i18n("reports", "Sorting..."));

        boolean inOrder = false;
        Object[] pc = areas.toArray();

        while (!inOrder) {
            inOrder = true;

            for (int i = 0; i < (pc.length - 1); i++) {
                PostcodeAreaCount pca = (PostcodeAreaCount) pc[i];
                PostcodeAreaCount pcb = (PostcodeAreaCount) pc[i + 1];

                if (pca.count < pcb.count) {
                    pc[i + 1] = pc[i];
                    pc[i] = pcb;
                    inOrder = false;
                }
            }
        }

        // Output the information
        setStatusText(Global.i18n("reports", "Building_report..."));
        setStatusBarMax(pc.length);

        for (int i = 0; i < pc.length; i++) {
            PostcodeAreaCount pcb = (PostcodeAreaCount) pc[i];
            tableAddRow();
            tableAddCell(pcb.area);
            tableAddCell(Integer.toString(pcb.count));
            tableFinishRow();
            incrementStatusBar();
        }

        tableFinish();

        // Show table
        addTable();
    }

    public void statsAverageWaitingList() throws Exception {
        AnimalWaitingList awl = new AnimalWaitingList();
        awl.openRecordset("DateRemovedFromList Is Not Null");

        setStatusBarMax((int) awl.getRecordCount());

        int totDays = 0;

        while (!awl.getEOF()) {
            // Work out the day difference between being put
            // on and being removed for this entry, then add
            // it to the cumulative total
            Calendar putOn = Utils.dateToCalendar(awl.getDatePutOnList());
            Calendar takenOff = Utils.dateToCalendar(awl.getDateRemovedFromList());
            double diff = (double) Utils.getDateDiff(takenOff, putOn);

            // Calculate difference in days, as that routine returns
            // it in minutes.
            diff = (diff / 60);
            diff = (diff / 24);

            int wholediff = (int) diff;

            totDays += wholediff;

            incrementStatusBar();
            awl.moveNext();
        }

        // Now calculate the real average
        int totRecords = (int) awl.getRecordCount();
        double average = (totDays / totRecords);

        addParagraph(Global.i18n("reports",
                "The_average_waiting_list_time_is_currently_",
                Double.toString(average)));

        addParagraph(Global.i18n("reports",
                "This_is_based_on_the_completed_waiting_list",
                Integer.toString(totRecords), Integer.toString(totDays)));
    }

    public void statsMostCommonAdoptionArea() throws Exception {
        boolean includeBlanks = Dialog.showYesNo(Global.i18n("reports",
                    "Do_you_want_to_include_blank_postcodes_in_this_report?"),
                Global.i18n("reports", "Blank_Postcodes"));

        tableNew();
        tableAddRow();
        tableAddCell(bold(Global.i18n("reports", "Postcode_Area")));
        tableAddCell(bold(Global.i18n("reports", "Occurrences")));
        tableFinishRow();

        // Get all non-blank postcode areas
        Adoption a = new Adoption();
        a.openRecordset("MovementDate Is Not Null AND MovementType = " +
            Adoption.MOVETYPE_ADOPTION);

        // Build a vector of unique postcode areas, along with the
        // number of times a particular area has occurred.
        Vector areas = new Vector((int) (a.getRecordCount() / 2));

        setStatusText(Global.i18n("reports", "Retrieving_data..."));
        setStatusBarMax((int) a.getRecordCount());

        while (!a.getEOF()) {
            String theArea = "";

            try {
                theArea = Utils.getAreaPostcode(a.getOwner().getOwnerPostcode());
            } catch (NullPointerException e) {
            } catch (Exception e) {
            }

            // Exclude blanks
            if ((includeBlanks) ||
                    (!includeBlanks && !theArea.trim().equals(""))) {
                // Do we have an entry already for this particular
                // area?
                boolean alreadyGot = false;
                Iterator it = areas.iterator();

                while (it.hasNext()) {
                    PostcodeAreaCount pc = (PostcodeAreaCount) it.next();

                    if (pc.area.equalsIgnoreCase(theArea)) {
                        alreadyGot = true;
                        // Increment the count
                        pc.count++;
                    }
                }

                if (!alreadyGot) {
                    // Create a new one
                    PostcodeAreaCount pc = new PostcodeAreaCount();
                    pc.area = theArea;
                    pc.count = 1;
                    areas.add(pc);
                }
            }

            a.moveNext();
            incrementStatusBar();
        }

        // We then bubblesort the array, ordering on the occurrences.
        setStatusText(Global.i18n("reports", "Sorting..."));

        boolean inOrder = false;
        Object[] pc = areas.toArray();

        while (!inOrder) {
            inOrder = true;

            for (int i = 0; i < (pc.length - 1); i++) {
                PostcodeAreaCount pca = (PostcodeAreaCount) pc[i];
                PostcodeAreaCount pcb = (PostcodeAreaCount) pc[i + 1];

                if (pca.count < pcb.count) {
                    pc[i + 1] = pc[i];
                    pc[i] = pcb;
                    inOrder = false;
                }
            }
        }

        // Output the information
        setStatusText(Global.i18n("reports", "Building_report..."));
        setStatusBarMax(pc.length);

        for (int i = 0; i < pc.length; i++) {
            PostcodeAreaCount pcb = (PostcodeAreaCount) pc[i];
            tableAddRow();
            tableAddCell(pcb.area);
            tableAddCell(Integer.toString(pcb.count));
            tableFinishRow();
            incrementStatusBar();
        }

        tableFinish();

        // Show table
        addTable();
    }
}


class PostcodeAreaCount {
    public String area = "";
    public int count = 0;
}
