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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.charts.AdoptionsPerBlock;
import net.sourceforge.sheltermanager.asm.charts.AdoptionsPerSpecies;
import net.sourceforge.sheltermanager.asm.charts.CommonReasonsEntry;
import net.sourceforge.sheltermanager.asm.charts.CommonReasonsReturn;
import net.sourceforge.sheltermanager.asm.charts.CustomChart;
import net.sourceforge.sheltermanager.asm.charts.DonationsPerSpecies;
import net.sourceforge.sheltermanager.asm.charts.MonthlyDonations;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.criteria.DateFromTo;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;

import java.math.BigDecimal;

import java.text.DecimalFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;


/**
 * Generates a custom report from it's ID
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class CustomReportExecute extends Report {
    private CustomReport cr = null;
    private String theTitle = null;
    private String crit = "";
    private boolean toStdOut = false;
    private String seldate = "";
    private String selnumber = "";
    private String replaceWith = "";

    // ========= SUBREPORT FUNCTIONALITY ==================
    private boolean isSubReport = false;

    /**
     * Flag used to block the thread when calling a subreport to wait for it to
     * finish
     */
    private boolean waitingOnSubReport = false;

    /** Output from the last subreport */
    private String lastSubReport = "";

    /** Monitor waiting for this subreport to finish if it's a subreport */
    private ReportDoneListener monitor = null;
    private String subReportParentFieldValue = "";

    /**
     * Executes a custom report, but sends it to STDOUT instead of going through
     * the usual stuff.
     */
    public CustomReportExecute(String customReportId, boolean toStdOut) {
        this.toStdOut = toStdOut;
        startReport(customReportId);
    }

    public CustomReportExecute(String customReportId) {
        startReport(customReportId);
    }

    /**
     * Executes a custom report, but sends a callback to an event interface when
     * it's finished.
     */
    public CustomReportExecute(String customReportId, String parentFieldValue,
        ReportDoneListener l) {
        subReportParentFieldValue = parentFieldValue;
        isSubReport = true;
        monitor = l;
        startReport(customReportId);
    }

    /**
     * Private constructor used by substituteSQLTagsForSQL() routine to generate
     * an instance so we can call substituteSQLTags
     */
    private CustomReportExecute() {
    }

    private synchronized void setWaitingOnSubReport(boolean b) {
        waitingOnSubReport = b;
    }

    private synchronized boolean isWaitingOnSubReport() {
        return waitingOnSubReport;
    }

    /**
     *  Overridden getHeader - we return plain HTML if the custom report
     *  says to omit the Header/Footer
     */
    protected String getHeader() {
        try {
            if (cr.getOmitHeaderFooter().intValue() == 1) {
                // Plain HTML
                return "<html><head><title>" + getTitle() +
                "</title></head><body>";
            } else {
                // Return the standard header
                return super.getHeader();
            }
        } catch (Exception e) {
            Global.logException(e, CustomReportExecute.class);

            return "<html><body>";
        }
    }

    /**
     *  Overridden getFooter - we return plain HTML if the custom report
     *  says to omit the Header/Footer
     */
    protected String getFooter() {
        final String PLAIN_HTML = "</body></html>";

        try {
            if (cr.getOmitHeaderFooter().intValue() == 1) {
                // Plain HTML
                return PLAIN_HTML;
            } else {
                // Return the standard footer
                return super.getFooter();
            }
        } catch (Exception e) {
            Global.logException(e, CustomReportExecute.class);

            return PLAIN_HTML;
        }
    }

    public void startReport(String customReportId) {
        try {
            cr = new CustomReport();
            cr.openRecordset("ID = " + customReportId);
            theTitle = cr.getTitle();

            // Is it a standard report? If so, then
            // look it up and run the correct one:
            if (cr.getSQLCommand().length() == 3) {
                // If it's to STDOUT or a subreport, we can't redirect, so
                // bomb
                if (toStdOut || isSubReport) {
                    Global.logError(Global.i18n("reports",
                            "internal_reports_cannot_be_redirected_to_stdout"),
                        "CustomReportExecute.startReport");
                    System.exit(1);
                }

                // Not on StdOut so run - we're ok!
                runStandardReport(cr.getSQLCommand());

                return;
            }
            // Is it a custom graph?
            else if (cr.getHTMLBody().toUpperCase().trim().startsWith("GRAPH")) {
                // Graphs can't be subreports, or run at the command line
                if (toStdOut || isSubReport) {
                    Global.logError(Global.i18n("reports",
                            "graphs_cant_be_sent_to_stdout"),
                        "CustomReportExecute.startReport");
                    System.exit(1);
                }

                // Do any magic needed on the SQL
                String sql = substituteSQLTags(cr.getSQLCommand());

                // Feed it it to the custom graph generator and we're done
                new CustomChart(sql, cr.getTitle());

                return;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        this.start();
    }

    public void run() {
        setStatusText(Global.i18n("reports", "Generating_", getTitle()));

        if (!isSubReport) {
            report = new StringBuffer(getHeader());
        } else {
            report = new StringBuffer();
        }

        generateReport();

        // If the report length is zero, there's nothing to display, so
        // finish gracefully and stop. This occurs when a user cancels input
        if (report.length() == 0) {
            setStatusText("");
            report = null;
            filename = null;
            tablespec = null;

            return;
        }

        if (!isSubReport) {
            report.append(getFooter());
        }

        resetStatusBar();
        setStatusText(Global.i18n("reports", "Outputting_to_disk", filename));
        writeToDisk();
        setStatusText("");
        display();

        cr.free();
        cr = null;
        report = null;
        filename = null;
        tablespec = null;
    }

    public void setStatusText(String text) {
        if (toStdOut) {
            Global.logInfo(text, "CustomReportExecute.setStatusText");
        } else {
            super.setStatusText(text);
        }
    }

    public void setStatusBarMax(int max) {
        if (!toStdOut) {
            super.setStatusBarMax(max);
        }
    }

    public void resetStatusBar() {
        if (!toStdOut) {
            super.resetStatusBar();
        }
    }

    public void incrementStatusBar() {
        if (!toStdOut) {
            super.incrementStatusBar();
        }
    }

    public void runStandardReport(String reportID) {
        int i = 0;

        try {
            i = Integer.parseInt(reportID);

            if ((i < 0) || (i > 31)) {
                Dialog.showError(Global.i18n("reports",
                        "Invalid_standard_report", reportID));

                return;
            }
        } catch (NumberFormatException e) {
            Dialog.showError(Global.i18n("reports", "Invalid_standard_report",
                    reportID));

            return;
        }

        switch (i) {
        case 1:
            new NotMicroChipped();

            break;

        case 2:
            new NoMediaAttached();

            break;

        case 3:
            new NeverVaccinated();

            break;

        case 4:
            new UnneuteredSixMonths();

            break;

        case 5:
            new UnCombiTestedCats();

            break;

        case 6:
            new AnimalFigures();

            break;

        case 7:
            new LongTermAnimals();

            break;

        case 8:
            new ShelterInventory(false);

            break;

        case 9:
            new ShelterInventory(true);

            break;

        case 10:
            new UnderSixNotLitter();

            break;

        case 11:
            Global.mainForm.addChild(new DateFromTo(DateFromTo.REPORT_INOUT));

            break;

        case 12:
            Global.mainForm.addChild(new DateFromTo(
                    DateFromTo.REPORT_INOUT_SUMMARY));

            break;

        case 13:
            Global.mainForm.addChild(new DateFromTo(
                    DateFromTo.REPORT_TRANSFERIN));

            break;

        case 14:
            Global.mainForm.addChild(new DateFromTo(
                    DateFromTo.REPORT_RETAILER_VOLUME_ADOPTIONS));

            break;

        case 15:
            new Retailer(Retailer.AVERAGE_TIME_AT_RETAILER, new Date(),
                new Date());

            break;

        case 16:
            new Retailer(Retailer.RETAILER_INVENTORY, new Date(), new Date());

            break;

        case 17:
            Global.mainForm.addChild(new DateFromTo(
                    DateFromTo.REPORT_RETURNEDANIMALS));

            break;

        case 18:
            Global.mainForm.addChild(new DateFromTo(
                    DateFromTo.REPORT_RETURNED_PRESIX));

            break;

        case 19:
            Global.mainForm.addChild(new DateFromTo(
                    DateFromTo.REPORT_RETURNED_POSTSIX));

            break;

        case 20:
            new Stats(Stats.MOST_COMMON_NAME);

            break;

        case 21:
            new Stats(Stats.DECEASED_REASONS);

            break;

        case 22:
            new Stats(Stats.MOST_COMMON_ENTRY_AREA);

            break;

        case 23:
            new Stats(Stats.MOST_COMMON_ADOPTION_AREA);

            break;

        case 24:
            new Stats(Stats.AVERAGE_WAITING_LIST);

            break;

        case 25:
            new MonthlyDonations();

            break;

        case 26:
            new DonationsPerSpecies();

            break;

        case 27:
            new AdoptionsPerSpecies();

            break;

        case 28:
            new AdoptionsPerBlock();

            break;

        case 29:
            new CommonReasonsEntry();

            break;

        case 30:
            new CommonReasonsReturn();

            break;

        case 31:
            new OwnerCriteriaSearch();

            break;
        }
    }

    public String getTitle() {
        return theTitle;
    }

    public void generateReport() {
        SQLRecordset rs = new SQLRecordset();
        String tempbody = null;

        int headerstart = 0;
        int headerend = 0;
        int bodystart = 0;
        int bodyend = 0;
        int footerstart = 0;
        int footerend = 0;
        int groupstart = 0;
        int groupend = 0;

        String cheader = null;
        String cbody = null;
        String cfooter = null;

        try {
            // Take the HTML body and split it into header, body and
            // footer sections
            String html = cr.getHTMLBody();

            headerstart = html.indexOf("$$HEADER");
            headerend = html.indexOf("HEADER$$");

            if ((headerstart == -1) || (headerend == -1)) {
                addParagraph("The header block of your report is invalid.");

                return;
            }

            cheader = html.substring(headerstart + 8, headerend);

            bodystart = html.indexOf("$$BODY");
            bodyend = html.indexOf("BODY$$");

            if ((bodystart == -1) || (bodyend == -1)) {
                addParagraph("The body block of your report is invalid.");

                return;
            }

            cbody = html.substring(bodystart + 6, bodyend);

            footerstart = html.indexOf("$$FOOTER");
            footerend = html.indexOf("FOOTER$$");

            if ((footerstart == -1) || (footerend == -1)) {
                addParagraph("The footer block of your report is invalid.");

                return;
            }

            cfooter = html.substring(footerstart + 8, footerend);

            // Grab any grouping levels in the HTML to
            // a vector
            Vector groups = new Vector();
            groupstart = html.indexOf("$$GROUP_");

            while (groupstart != -1) {
                groupend = html.indexOf("GROUP$$", groupstart);

                if (groupend == -1) {
                    addParagraph(
                        "A group block of your report is invalid (missing GROUP$$ closing tag)");

                    return;
                }

                String ghtml = html.substring(groupstart, groupend);
                int ghstart = ghtml.indexOf("$$HEAD");

                if (ghstart == -1) {
                    addParagraph(
                        "A group block of your report is invalid (no group $$HEAD)");

                    return;
                }

                ghstart += 6;

                int ghend = ghtml.indexOf("$$FOOT", ghstart);

                if (ghend == -1) {
                    addParagraph(
                        "A group block of your report is invalid (no group $$FOOT)");

                    return;
                }

                GroupDescriptor gd = new GroupDescriptor();
                gd.header = ghtml.substring(ghstart, ghend);
                gd.footer = ghtml.substring(ghend + 6, ghtml.length());
                gd.fieldName = ghtml.substring(8, ghstart - 6).trim();

                groups.add(gd);

                groupstart = html.indexOf("$$GROUP_", groupend);
            }

            // Substitute any keys in the SQL Command
            String sql = substituteSQLTags(cr.getSQLCommand());

            // If the SQL has been altered for the word "CANCEL", then
            // a user prompt has caused us to abandon the report - make
            // the report empty so we know not to display it and can
            // close gracefully.
            if (sql.equals("CANCEL")) {
                report = new StringBuffer();

                return;
            }

            // Grab the groups as an array to
            // make it easier to count with
            Object[] group = groups.toArray();

            // Dispose of the vector
            groups.removeAllElements();
            groups = null;

            // Scan the ORDER BY clause to make sure the order
            // matches the grouping levels. This applies
            // even if a GROUP BY clause is specified (I think!)
            if (group.length > 0) {
                String lsql = sql.toLowerCase();
                int startOrder = lsql.indexOf("order by");

                if (startOrder == -1) {
                    Dialog.showWarning(
                        "You have grouping levels on this report without\n" +
                        "an ORDER BY clause. You may experience unpredictable\n" +
                        "results.", "No ORDER BY Clause");
                    lsql = null;
                } else {
                    String orderBy = lsql.substring(startOrder, lsql.length());
                    boolean okSoFar = true;
                    int lastSort = 0;

                    for (int i = 0; i < group.length; i++) {
                        GroupDescriptor gd = (GroupDescriptor) group[i];
                        lastSort = orderBy.indexOf(gd.fieldName.toLowerCase(),
                                lastSort);
                        gd = null;
                        okSoFar = (lastSort != -1);

                        if (!okSoFar) {
                            break;
                        }
                    }

                    if (!okSoFar) {
                        Dialog.showWarning(
                            "Your ORDER BY clause does not match the order of\n" +
                            "your grouping levels. You may experience unpredictable\n" +
                            "results.", "Incorrect ORDER BY Clause");
                    }

                    orderBy = null;
                    lsql = null;
                }
            }

            // Display criteria if there is some and the option is on
            if (!crit.equals("")) {
                if (cr.getOmitCriteria().intValue() == 0) {
                    addParagraph(bold(Global.i18n("reports", "criteria")));
                    addParagraph(crit);
                    addHorizontalRule();
                }
            }

            // Now get the results - make an array of queries, split by the
            // semi-colon operator
            String[] queries = Utils.split(sql, ";");

            // Make sure the last query is a SELECT or (SELECT for UNION
            if (!queries[queries.length - 1].toLowerCase().trim()
                                                .startsWith("select") &&
                    !queries[queries.length - 1].toLowerCase().trim()
                                                    .startsWith("(select")) {
                Dialog.showError(Global.i18n("reports",
                        "there_must_be_at_least_one_select_query_and_it_must_be_the_last_to_run"));

                return;
            }

            // Loop through the queries, executing them/running where necessary
            for (int i = 0; i < queries.length; i++) {
                try {
                    // If it's an action query, execute it
                    if (queries[i].trim().toLowerCase().startsWith("create") ||
                            queries[i].trim().toLowerCase().startsWith("drop") ||
                            queries[i].trim().toLowerCase().startsWith("insert") ||
                            queries[i].trim().toLowerCase().startsWith("update") ||
                            queries[i].trim().toLowerCase().startsWith("delete")) {
                        Global.logDebug("EXECUTE: " + queries[i],
                            "generateReport");
                        DBConnection.executeAction(queries[i]);
                    } else {
                        Global.logDebug("OPEN: " + queries[i], "generateReport");
                        rs.openRecordset(queries[i], "animal");
                    }
                } catch (Exception e) {
                    Dialog.showError(Global.i18n("reports",
                            "An_error_occurred_generating_the_report",
                            e.getMessage()));
                    Global.logException(e, getClass());
                }
            }

            // Add header to the report
            substituteHFValues(0, cheader, rs);

            setStatusBarMax((int) rs.getRecordCount());

            boolean firstRecord = true;

            Global.logInfo("Custom report found: " + rs.getRecordCount() +
                " records.", "CustomReport.generateReport");

            while (!rs.getEOF()) {
                // Add each group footer in reverse order, unless
                // this is the first record, in which case we haven't
                // started yet!

                // If an outer group has changed, we need to end the
                // inner groups first.
                if (!firstRecord) {
                    // Loop through the groups in ascending order. If
                    // the switch value for an outer group changes, we
                    // need to force finishing of it's inner groups

                    // This same flag is used to determine whether or
                    // not to update the header
                    boolean cascade = false;

                    for (int i = 0; i < group.length; i++) {
                        GroupDescriptor gd = (GroupDescriptor) group[i];

                        if (gd.lastFieldValue == null) {
                            gd.lastFieldValue = "";
                        }

                        if (!gd.lastFieldValue.equals(rs.getField(gd.fieldName)) ||
                                cascade) {
                            // Mark this one for update
                            gd.forceFinish = true;
                            gd.lastGroupEndPosition = rs.getAbsolutePosition() -
                                1;
                            cascade = true;
                        } else {
                            gd.forceFinish = false;
                        }
                    }

                    for (int i = group.length - 1; i >= 0; i--) {
                        GroupDescriptor gd = (GroupDescriptor) group[i];

                        if (gd.forceFinish) {
                            // Output the footer, switching
                            // field values and calculating totals
                            // where necessary
                            outputGroupBlock(gd, 1, rs);
                        }
                    }
                }

                // Now do each header in ascending order
                for (int i = 0; i < group.length; i++) {
                    GroupDescriptor gd = (GroupDescriptor) group[i];

                    if (gd.forceFinish || firstRecord) {
                        // Mark the position
                        gd.lastGroupStartPosition = rs.getAbsolutePosition();
                        gd.lastGroupEndPosition = rs.getAbsolutePosition();

                        // Output the header, switching
                        // field values and calculating totals
                        // where necessary
                        outputGroupBlock(gd, 0, rs);
                    }
                }

                firstRecord = false;

                // Make a temp string to hold the body while
                // we substitute fields for tags
                tempbody = new String(cbody);

                for (int i = 1; i <= rs.getFieldCount(); i++) {
                    tempbody = Utils.replace(tempbody,
                            "$" + rs.getFieldName(i),
                            displayValue(rs.getField(rs.getFieldName(i))));
                }

                // Update the last value for each group
                for (int i = 0; i < group.length; i++) {
                    GroupDescriptor gd = (GroupDescriptor) group[i];
                    gd.lastFieldValue = rs.getField(gd.fieldName);
                }

                // Find any non-field keys
                // Look for calculation keys
                int startKey = tempbody.indexOf("{");

                while (startKey != -1) {
                    int endKey = tempbody.indexOf("}", startKey);
                    String key = tempbody.substring(startKey + 1, endKey);
                    String value = "";

                    // {SQL.[sql]} - arbitrary sql command
                    if (key.toLowerCase().startsWith("sql")) {
                        String field = key.substring(4, key.length());

                        try {
                            // If it's an action query, execute it
                            if (field.toLowerCase().startsWith("create") ||
                                    field.toLowerCase().startsWith("drop") ||
                                    field.toLowerCase().startsWith("insert") ||
                                    field.toLowerCase().startsWith("update") ||
                                    field.toLowerCase().startsWith("delete")) {
                                DBConnection.executeAction(field);
                                value = "";
                            } else {
                                SQLRecordset rs2 = new SQLRecordset();
                                rs2.openRecordset(field, "animal");

                                if (rs2.getEOF()) {
                                    value = "[EOF]";
                                } else {
                                    if (rs2.getField(rs2.getFieldName(1)) == null) {
                                        value = "null";
                                    } else {
                                        value = rs2.getField(rs2.getFieldName(1))
                                                   .toString();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            value = "[" + e.getMessage() + "]";
                            Global.logException(e, getClass());
                        }
                    }

                    // {IMAGE.[animalid]} - retreives an animal's image from
                    // the database, saves it in the temp folder and then
                    // inserts the filename
                    if (key.toLowerCase().startsWith("image")) {
                        try {
                            String body = key.substring(6, key.length());
                            String animalid = key.substring(key.indexOf(".") +
                                    1);
                            Global.logDebug("IMAGE tag, got animal id: " +
                                animalid, "CustomReportExecute.run");

                            Animal a = new Animal();
                            a.openRecordset("ID = " + animalid);

                            String mediaName = a.getWebMedia();

                            // If we got a blank, return a link to nopic.jpg instead
                            if (mediaName.equals("")) {
                                mediaName = "nopic.jpg";
                            }

                            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                                    Integer.parseInt(animalid));
                            dbfs.readFile(mediaName,
                                net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                                File.separator + mediaName);
                            value = mediaName;
                        } catch (Exception e) {
                            value = "[" + e.getMessage() + "]";
                            Global.logException(e, getClass());
                        }
                    }

                    // {SUBREPORT.[title].[parentField]} - embed
                    // a subreport.
                    if (key.toLowerCase().startsWith("subreport")) {
                        String body = key.substring(10, key.length());

                        // Break it up
                        String[] bits = Utils.split(body, ".");
                        String title = bits[0];
                        String parent = bits[1];

                        // Lookup custom report ID from title
                        CustomReport subr = new CustomReport();
                        subr.openRecordset("Title Like '" + title + "'");

                        String id = subr.getID().toString();

                        // Call the report
                        setWaitingOnSubReport(true);
                        new CustomReportExecute(id,
                            rs.getField(parent).toString(),
                            new ReportDoneListener() {
                                public void reportCompleted(String rep) {
                                    lastSubReport = rep;

                                    // If it has a <BODY> tag, then
                                    // chop either side since it will be
                                    // embedded
                                    int bodys = lastSubReport.indexOf("<body");

                                    if (bodys == -1) {
                                        bodys = lastSubReport.indexOf("<BODY");
                                    }

                                    if (bodys != -1) {
                                        int bodye = lastSubReport.indexOf(
                                                "</body>");

                                        if (bodye == -1) {
                                            bodye = lastSubReport.indexOf(
                                                    "</BODY>");
                                        }

                                        if (bodye != -1) {
                                            lastSubReport = lastSubReport.substring(lastSubReport.indexOf(
                                                        ">", bodys) + 1, bodye);
                                        }
                                    }

                                    setWaitingOnSubReport(false);
                                }
                            });

                        // Wait for it to finish
                        while (isWaitingOnSubReport()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                        }

                        // Substitute the report value
                        value = lastSubReport;
                    }

                    tempbody = tempbody.substring(0, startKey) + value +
                        tempbody.substring(endKey + 1, tempbody.length());
                    startKey = tempbody.indexOf("{");
                }

                // Append into the report
                addHTML(tempbody);

                // Ditch string reference
                tempbody = null;

                rs.moveNext();
                incrementStatusBar();
            }

            // Add the final group footers if there are any
            for (int i = group.length - 1; i >= 0; i--) {
                GroupDescriptor gd = (GroupDescriptor) group[i];
                // Output the footer, switching
                // field values and calculating totals
                // where necessary
                gd.lastGroupEndPosition = (int) rs.getRecordCount();
                outputGroupBlock(gd, 1, rs);
            }

            // Add the report footer
            substituteHFValues(1, cfooter, rs);

            // Scan the queries created - if any of them made a
            // temporary table, then we should drop it now:
            for (int i = 0; i < queries.length; i++) {
                if (queries[i].trim().toLowerCase()
                                  .startsWith("create temporary")) {
                    dropTemporaryTable(queries[i]);
                }
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        } finally {
            rs.free();
            rs = null;
            cheader = null;
            cbody = null;
            cfooter = null;
            resetStatusBar();
        }
    }

    public static void dropTemporaryTable(String sqlCreate) {
        // Extract the table name
        int tstart = sqlCreate.toLowerCase().indexOf("table");

        if (tstart != -1) {
            tstart += 6;

            int tend = sqlCreate.toLowerCase().indexOf("(", tstart);
            String tname = sqlCreate.substring(tstart, tend).trim();

            try {
                DBConnection.executeAction("DROP TABLE " + tname);
            } catch (Exception e) {
                Dialog.showError(Global.i18n("reports",
                        "An_error_occurred_dropping_temporary_table_", tname,
                        e.getMessage()));
            }
        }
    }

    /**
     * Handles group block (header/footer) output and calculations and things.
     *
     * @param gd
     *            The GroupDescriptor
     * @param headfoot
     *            0 for header, 1 for footer
     * @param rs
     *            The Recordset we are retrieving data from
     */
    private void outputGroupBlock(GroupDescriptor gd, int headfoot,
        SQLRecordset rs) {
        int lastPos = rs.getAbsolutePosition();

        try {
            String out = ((headfoot == 0) ? gd.header : gd.footer);

            // If there aren't any records in the set, then there's no point
            // trying to do any calculations
            if (rs.getRecordCount() == 0) {
                addHTML(out);

                return;
            }

            // Move to the last record in the group 
            rs.setAbsolutePosition(gd.lastGroupEndPosition);

            for (int i = 1; i <= rs.getFieldCount(); i++) {
                out = Utils.replace(out, "$" + rs.getFieldName(i),
                        displayValue(rs.getField(rs.getFieldName(i))));
            }

            // Look for calculation keys
            int startKey = out.indexOf("{");

            while (startKey != -1) {
                int endKey = out.indexOf("}", startKey);
                String key = out.substring(startKey + 1, endKey);
                String value = "";

                // Move to the last record in the group
                rs.setAbsolutePosition(gd.lastGroupEndPosition);

                // {SUM.field}
                if (key.toLowerCase().startsWith("sum")) {
                    String[] fields = Utils.split(key.toLowerCase(), ".");

                    // Sort out rounding
                    int roundTo = 0;

                    if (fields.length > 2) {
                        roundTo = Integer.parseInt(fields[2]);
                    }

                    BigDecimal total = new BigDecimal((double) 0);

                    // Count backwards to start of group
                    while (rs.getAbsolutePosition() >= gd.lastGroupStartPosition) {
                        try {
                            total = total.add(new BigDecimal((rs.getField(
                                            fields[1]).toString())));
                        } catch (Exception e) {
                            // Ignore non-numbers
                        }

                        rs.movePrevious();

                        if (rs.getBOF()) {
                            break;
                        }
                    }

                    value = roundToDP(total, roundTo);
                }

                // {COUNT.field}
                if (key.toLowerCase().startsWith("count")) {
                    int total = 0;

                    // Count backwards until the key field
                    // changes from it's current last value or we
                    // hit the beginning of the recordset
                    while (rs.getAbsolutePosition() >= gd.lastGroupStartPosition) {
                        total++;
                        rs.movePrevious();

                        if (rs.getBOF()) {
                            break;
                        }
                    }

                    value = Integer.toString(total);
                }

                // {AVG.field}
                if (key.toLowerCase().startsWith("avg")) {
                    String[] fields = Utils.split(key.toLowerCase(), ".");

                    // Sort out rounding
                    int roundTo = 0;

                    if (fields.length > 2) {
                        roundTo = Integer.parseInt(fields[2]);
                    }

                    BigDecimal total = new BigDecimal("0");
                    int num = 0;

                    // Count backwards until the key field
                    // changes from it's current last value or we
                    // hit the beginning of the recordset
                    while (rs.getAbsolutePosition() >= gd.lastGroupStartPosition) {
                        try {
                            total = total.add(new BigDecimal(
                                        rs.getField(fields[1]).toString()));
                            num++;
                        } catch (Exception e) {
                            // Ignore non-numbers
                        }

                        rs.movePrevious();

                        if (rs.getBOF()) {
                            break;
                        }
                    }

                    value = roundToDP(total.divide(new BigDecimal(num),
                                BigDecimal.ROUND_CEILING), roundTo);
                }

                // {PCT.field}
                if (key.toLowerCase().startsWith("pct")) {
                    String[] fields = Utils.split(key.toLowerCase(), ".");

                    // Sort out rounding
                    int roundTo = 0;

                    if (fields.length > 3) {
                        roundTo = Integer.parseInt(fields[3]);
                    }

                    int totRecords = (int) (rs.getAbsolutePosition() -
                        gd.lastGroupStartPosition) + 1;
                    int matchRecords = 0;

                    // Count backwards and find all values matching
                    // the check value
                    while (rs.getAbsolutePosition() >= gd.lastGroupStartPosition) {
                        if (rs.getField(fields[1]).toString().trim()
                                  .equalsIgnoreCase(fields[2])) {
                            matchRecords++;
                        }

                        rs.movePrevious();

                        if (rs.getBOF()) {
                            break;
                        }
                    }

                    double match = (double) matchRecords;
                    double outOf = (double) totRecords;
                    double pct = (match / outOf) * 100;

                    value = roundToDP(new BigDecimal(pct), roundTo);
                }

                // {MIN.field}
                if (key.toLowerCase().startsWith("min")) {
                    double min = 0;

                    // Count backwards until the key field
                    // changes from it's current last value or we
                    // hit the beginning of the recordset
                    while (rs.getAbsolutePosition() >= gd.lastGroupStartPosition) {
                        try {
                            double cur = Double.parseDouble(rs.getField(
                                        gd.fieldName).toString());

                            if ((min == 0) || (min > cur)) {
                                min = cur;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore non-numbers
                        }

                        rs.movePrevious();

                        if (rs.getBOF()) {
                            break;
                        }
                    }

                    value = Double.toString(min);
                }

                // {MAX.field}
                if (key.toLowerCase().startsWith("max")) {
                    double max = 0;

                    // Count backwards until the key field
                    // changes from it's current last value or we
                    // hit the beginning of the recordset
                    while (rs.getAbsolutePosition() >= gd.lastGroupStartPosition) {
                        try {
                            double cur = Double.parseDouble(rs.getField(
                                        gd.fieldName).toString());

                            if ((max == 0) || (max < cur)) {
                                max = cur;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore non-numbers
                        }

                        rs.movePrevious();

                        if (rs.getBOF()) {
                            break;
                        }
                    }

                    value = Double.toString(max);
                }

                // {FIRST.field}
                if (key.toLowerCase().startsWith("first")) {
                    String field = key.substring(6, key.length());

                    try {
                        rs.setAbsolutePosition(gd.lastGroupStartPosition);
                        value = rs.getField(field).toString();
                    } catch (Exception e) {
                    }
                }

                // {LAST.field}
                if (key.toLowerCase().startsWith("last")) {
                    String field = key.substring(5, key.length());

                    try {
                        rs.setAbsolutePosition(gd.lastGroupEndPosition);
                        value = rs.getField(field).toString();
                    } catch (Exception e) {
                    }
                }

                // {SQL.[sql]} - arbitrary sql command
                if (key.toLowerCase().startsWith("sql")) {
                    String field = key.substring(4, key.length());

                    try {
                        // If it's an action query, execute it
                        if (field.toLowerCase().startsWith("create") ||
                                field.toLowerCase().startsWith("drop") ||
                                field.toLowerCase().startsWith("insert") ||
                                field.toLowerCase().startsWith("update") ||
                                field.toLowerCase().startsWith("delete")) {
                            DBConnection.executeAction(field);
                            value = "";
                        } else {
                            SQLRecordset rs2 = new SQLRecordset();
                            rs2.openRecordset(field, "animal");

                            if (rs2.getEOF()) {
                                value = "[EOF]";
                            } else {
                                value = rs2.getField(rs2.getFieldName(1))
                                           .toString();
                            }
                        }
                    } catch (Exception e) {
                        value = "[" + e.getMessage() + "]";
                        Global.logException(e, getClass());
                    }
                }

                // {SUBREPORT.[title].[parentField]} - embed
                // a subreport.
                if (key.toLowerCase().startsWith("subreport")) {
                    String body = key.substring(10, key.length());

                    // Break it up
                    String[] bits = Utils.split(body, ".");
                    String title = bits[0];
                    String parent = bits[1];

                    // Lookup custom report ID from title
                    CustomReport subr = new CustomReport();
                    subr.openRecordset("Title Like \"" + title + "\"");

                    String id = subr.getID().toString();

                    // Call the report
                    setWaitingOnSubReport(true);
                    new CustomReportExecute(id, rs.getField(parent).toString(),
                        new ReportDoneListener() {
                            public void reportCompleted(String rep) {
                                lastSubReport = rep;

                                // If it has a <BODY> tag, then
                                // chop either side since it will be
                                // embedded
                                int bodys = lastSubReport.indexOf("<body");

                                if (bodys == -1) {
                                    bodys = lastSubReport.indexOf("<BODY");
                                }

                                if (bodys != -1) {
                                    int bodye = lastSubReport.indexOf("</body>");

                                    if (bodye == -1) {
                                        bodye = lastSubReport.indexOf("</BODY>");
                                    }

                                    if (bodye != -1) {
                                        lastSubReport = lastSubReport.substring(lastSubReport.indexOf(
                                                    ">", bodys) + 1, bodye);
                                    }
                                }

                                setWaitingOnSubReport(false);
                            }
                        });

                    // Wait for it to finish
                    while (isWaitingOnSubReport()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }

                    // Substitute the report value
                    value = lastSubReport;
                }

                // Substitute. If we are in a header, then output
                // a value to show aggregation can't be done here.
                if (headfoot == 0) {
                    out = out.substring(0, startKey) +
                        "[N/A in group headers]" +
                        out.substring(endKey + 1, out.length());
                } else {
                    out = out.substring(0, startKey) + value +
                        out.substring(endKey + 1, out.length());
                }

                startKey = out.indexOf("{");
            }

            // Output the HTML to the report
            addHTML(out);
        } catch (Exception e) {
            Global.logException(e, getClass());
        } finally {
            // Set the recordset back to where it was
            try {
                rs.setAbsolutePosition(lastPos);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Rounds a number to the set number of decimal places and
     * returns it as a readable string.
     */
    private String roundToDP(BigDecimal value, int dp) {
        final String zeroes = "00000000000000000000000000000000000000000";
        value.setScale(dp, BigDecimal.ROUND_HALF_UP);

        DecimalFormat df = new DecimalFormat("0." + zeroes.substring(0, dp));

        return df.format(value.doubleValue());
    }

    /**
     * Looks for header/footer group tags, as well as arbitrary query tags
     */
    public void substituteHFValues(int headfoot, String text, SQLRecordset rs) {
        GroupDescriptor gd = new GroupDescriptor();
        gd.lastGroupEndPosition = (int) rs.getRecordCount();
        gd.lastGroupStartPosition = 1;
        gd.footer = text;
        gd.header = text;
        outputGroupBlock(gd, headfoot, rs);
        gd = null;
        rs = null;
    }

    /**
     * Allows the substituteSQLTags method to be called from a static context.
     * Used by the ExportCustomReportData class.
     */
    public static String substituteSQLTagsForSQL(String sql) {
        CustomReportExecute crexec = new CustomReportExecute();

        return crexec.substituteSQLTags(sql);
    }

    /** Converts a recordset value for display - nulls become empty strings */
    public String displayValue(Object o) {
        if (o == null) {
            return "";
        }

        if (o instanceof Date) {
            return Utils.formatDate((Date) o);
        }

        return o.toString();
    }

    /**
     * Scans the SQL code for any of our keys to substitute and processes them
     * accordingly.
     *
     *
     */
    public String substituteSQLTags(String sql) {
        // Hunt through the sql, looking for a start
        // marker to a tag
        HashMap vars = new HashMap();

        for (int i = 0; i < sql.length(); i++) {
            if (sql.substring(i, i + 1).equals("$")) {
                int tagstart = i;
                int tagend = sql.indexOf("$", i + 1);

                // Default string to replace the tag with
                replaceWith = "";

                // Grab the whole tag and split it into pieces
                String ftag = sql.substring(i + 1, tagend);
                String[] tagbits = Utils.split(ftag, " ");
                String tagtype = tagbits[0];

                // Mark the spaces for variable length message tags
                int firstspace = ftag.indexOf(" ");
                int secondspace = ftag.indexOf(" ", firstspace + 1);
                int thirdspace = -1;

                if (secondspace != -1) {
                    thirdspace = ftag.indexOf(" ", secondspace + 1);
                }

                // USER tag
                if (tagtype.equalsIgnoreCase("USER")) {
                    replaceWith = Global.currentUserName;
                }

                // PARENTKEY tag
                if (tagtype.equalsIgnoreCase("PARENTKEY")) {
                    replaceWith = subReportParentFieldValue;
                }

                // ASK tag
                if (tagtype.equalsIgnoreCase("ASK")) {
                    // Check what they are asking for
                    String askedFor = tagbits[1];
                    String mess = "";

                    if (secondspace != -1) {
                        mess = ftag.substring(secondspace + 1, ftag.length());
                    }

                    replaceWith = handleAskTag(askedFor, mess);
                }

                // VAR tag
                if (tagtype.equalsIgnoreCase("VAR")) {
                    // Var is just like ASK, except it has a variable
                    // name before the type and message
                    String varname = tagbits[1];
                    String askedFor = tagbits[2];
                    String mess = "";

                    if (thirdspace != -1) {
                        mess = ftag.substring(thirdspace + 1, ftag.length());
                    }

                    String varvalue = handleAskTag(askedFor, mess);
                    vars.put(varname, varvalue);
                    // Var tags don't get replaced, we access the variable
                    // with a $@VARNAME$ tag
                    replaceWith = "";
                }

                // @ (variable output tag)
                if (tagtype.startsWith("@")) {
                    replaceWith = (String) vars.get(tagtype.substring(1));

                    if (replaceWith == null) {
                        replaceWith = "";
                    }
                }

                // Throw away the tag and replace it with the substitution
                // string
                sql = sql.substring(0, tagstart) + replaceWith +
                    sql.substring(tagend + 1, sql.length());
            }
        }

        return sql;
    }

    /**
     * Handles asking the user for something from an ASK or VAR tag
     * @param askedFor the kind of prompt to display
     * @param message The message to display if appropriate
     */
    private String handleAskTag(String askedFor, final String message) {
        // DATE
        if (askedFor.equalsIgnoreCase("DATE")) {
            seldate = Dialog.getDateInput(message, "Enter Date");

            if (seldate.equals("")) {
                return "CANCEL";
            }

            // Format it for replacement
            try {
                replaceWith = Utils.getSQLDate(seldate);
            } catch (Exception e) {
                replaceWith = Utils.getSQLDate(Calendar.getInstance());
            }

            // Add it to the crit list
            crit += (message + ": " + seldate + "<br/>");
        }

        // SPECIES
        if (askedFor.equalsIgnoreCase("SPECIES")) {
            replaceWith = Integer.toString(Dialog.getSpecies());

            try {
                crit += (Global.i18n("reports", "Species") + ": " +
                LookupCache.getSpeciesName(new Integer(replaceWith)) + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // ANIMAL TYPE
        if (askedFor.equalsIgnoreCase("TYPE")) {
            replaceWith = Integer.toString(Dialog.getAnimalType());

            try {
                crit += (Global.i18n("reports", "animaltype") + ": " +
                LookupCache.getAnimalTypeName(new Integer(replaceWith)) +
                "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // INTERNAL LOCATION
        if (askedFor.equalsIgnoreCase("LOCATION")) {
            replaceWith = Integer.toString(Dialog.getInternalLocation());

            try {
                crit += (Global.i18n("reports", "Internal_Location") + ": " +
                LookupCache.getInternalLocationName(new Integer(replaceWith)) +
                "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // DIET
        if (askedFor.equalsIgnoreCase("DIET")) {
            replaceWith = Integer.toString(Dialog.getDiet());

            try {
                crit += (Global.i18n("reports", "Diet") + ": " +
                LookupCache.getDietName(new Integer(replaceWith)) + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // VOUCHER
        if (askedFor.equalsIgnoreCase("VOUCHER")) {
            replaceWith = Integer.toString(Dialog.getVoucher());

            try {
                crit += (Global.i18n("reports", "Voucher") + ": " +
                LookupCache.getVoucherName(new Integer(replaceWith)) + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // NUMBER
        if (askedFor.equalsIgnoreCase("NUMBER")) {
            // Take the message string and use it to
            // prompt the user for the number
            selnumber = Dialog.getInput(message, "Input Number");

            // Validate that it is a number
            try {
                double d = Double.parseDouble(selnumber);
            } catch (NumberFormatException e) {
                selnumber = "0";
            }

            // return it
            replaceWith = selnumber;
            crit += (message + ": " + selnumber + "<br/>");
        }

        // STRING
        if (askedFor.equalsIgnoreCase("STRING")) {
            // Take the message string and use it to
            // prompt the user for the string
            replaceWith = Dialog.getInput(message, "Input String");
            crit += (message + ": " + replaceWith + "<br/>");
        }

        return replaceWith;
    }

    /**
     * Overriden here to return the report to STDOUT or as a String to the
     * monitor for subreports
     */
    public void writeToDisk() {
        // If we are reflecting to stdout, then do it
        // now and complete.
        if (toStdOut) {
            System.out.println(report.toString());
            System.exit(1);
        }
        // If it's a subreport, return it
        else if (isSubReport) {
            monitor.reportCompleted(report.toString());
        } else {
            super.writeToDisk();
        }
    }

    /** Don't do anything for sub reports and stdout */
    public void display() {
        if (!toStdOut && !isSubReport) {
            super.display();
        }
    }

    /** Event interface to call back when the report has finished */
    private interface ReportDoneListener extends java.util.EventListener {
        /** Called when the report has finished */
        void reportCompleted(String rep);
    }
}


class GroupDescriptor {
    public String fieldName = "";
    public Object lastFieldValue = "";
    public String header = "";
    public String footer = "";
    public boolean forceFinish = false;

    /** Position where this group last started and ended in the recordset */
    public int lastGroupStartPosition = 1;
    public int lastGroupEndPosition = 1;
}
