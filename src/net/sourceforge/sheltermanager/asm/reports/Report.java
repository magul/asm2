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

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.reportviewer.*;
import net.sourceforge.sheltermanager.asm.ui.system.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.dbfs.*;

import java.io.*;

import java.util.*;


/**
 * Superclass which all reports must extend. Contains functionality
 * for creating HTML reports.
 *
 * To generate a report, extend this class and override void <code>generateReport()</code>
 * with your content. You should also override <code>getTitle()</code> to return
 * the title of your report.
 *
 * Note that reports are generated in a separate thread - this is because they
 * use the progress meter and cannot run on the Swing event dispatch thread.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public abstract class Report extends Thread {
    static final String TABLE_START = "<table rules=all border=1>";
    static final String TABLE_START_PADDED = "<table rules=all border=1 cellspacing=5 cellpadding=5>";
    static final String TABLE_NEW_ROW = "<tr>";
    static final String TABLE_FINISH_ROW = "</tr>";
    static final String TABLE_FINISH_TABLE = "</table>";
    static final String TABLE_NEW_CELL = "<td>";
    static final String TABLE_FINISH_CELL = "</td>";
    static final String NEW_H1 = "<h1>";
    static final String FIN_H1 = "</h1>";
    static final String NEW_H2 = "<h2>";
    static final String FIN_H2 = "</h2>";
    static final String NEW_H3 = "<h3>";
    static final String FIN_H3 = "</h3>";
    static final String NEW_PARAGRAPH = "<p>";
    static final String FIN_PARAGRAPH = "</p>";
    static final String HORIZONTAL_RULE = "<hr>";

    /** Open the report in the viewer after display */
    protected boolean displayAfter = true;

    /** The actual HTML of the report we are going to build */
    protected StringBuffer report = null;

    /** The path and filename of the report file */
    protected String filename = "";

    /** The most recent table */
    protected StringBuffer tablespec = null;

    /** Generates the report document, outputting status information
     * and finally displaying it to the user. Note, uses call to
     * overridden <code>generateReport()</code> to get content. You will need to
     * override the <code>getTitle()</code> method to the report title.*/
    public Report() {
        this(true);
    }

    public Report(boolean displayAfter) {
        this.displayAfter = displayAfter;
    }

    public void run() {
        setStatusText(Global.i18n("reports", "Generating_", getTitle()));
        report = new StringBuffer(getHeader());
        generateReport();
        report.append(getFooter());
        resetStatusBar();
        setStatusText(Global.i18n("reports", "Outputting_to_disk", filename));
        writeToDisk();
        setStatusText("");

        if (displayAfter) {
            display();
        }
    }

    /** Handles all the generating of the report. Subclasses should
     * override this method with the actual content */
    protected void generateReport() {
    }

    /** Gets the report title - override with your own
     * @return The title of the report.
     */
    protected String getTitle() {
        return Global.i18n("reports", "Untitled_Report");
    }

    public String getFilename() {
        return filename;
    }

    /** Writes the report out to a temporary
      * file, setting the filename in the process */
    protected void writeToDisk() {
        try {
            // Open an output stream and dump the HTML
            File f = Utils.getNewTempFile("html");
            filename = f.getAbsolutePath();

            FileOutputStream out = new FileOutputStream(f);
            out.write(report.toString().getBytes(Global.CHAR_ENCODING));
            out.close();

            // The write was successful - add an entry to
            // the cache.
            Global.localCache.addEntry(f.getName(), "Report - " + getTitle());
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "Unable_to_write_temporary_HTML_file_", filename,
                    e.getMessage()), Global.i18n("reports", "Error"));
        }
    }

    /** Adds a level one header to the report */
    protected void addLevelOneHeader(String text) {
        report.append(NEW_H1 + text + FIN_H1);
    }

    /** Adds a level two header to the report */
    protected void addLevelTwoHeader(String text) {
        report.append(NEW_H2 + text + FIN_H2);
    }

    /** Adds a level three header to the report */
    protected void addLevelThreeHeader(String text) {
        report.append(NEW_H3 + text + FIN_H3);
    }

    /** Adds a paragraph to the report */
    protected void addParagraph(String text) {
        report.append(NEW_PARAGRAPH + text + FIN_PARAGRAPH);
    }

    /** Adds a horizontal rule */
    protected void addHorizontalRule() {
        report.append(HORIZONTAL_RULE);
    }

    /** Adds a blob of HTML */
    protected void addHTML(String html) {
        report.append(html);
    }

    protected void addImage(String src) {
        addImage(src, "right");
    }

    protected void addImage(String src, String align) {
        report.append("<img src=\"" + src + "\" align=\"" + align + "\" />");
    }

    /** Adds the most recent table to the report. */
    protected void addTable() {
        report.append(tablespec.toString());
    }

    /** Adds a custom tablespec to the report. Here
     *  to add support for earlier releases whilst
     *  they are converted.
     */
    protected void addTable(String tableSpec) {
        report.append(tableSpec);
    }

    /** Return a bold version of a string */
    protected String bold(String t) {
        return "<b>" + t + "</b>";
    }

    /** Return an underline version of a string */
    protected String underline(String t) {
        return "<u>" + t + "</u>";
    }

    /** Return an italicised version of a string */
    protected String italic(String t) {
        return "<i>" + t + "</i>";
    }

    /** Starts a new table */
    protected void tableNew(int widthpct) {
        tablespec = new StringBuffer("<table border = 1 rules=all width = " +
                Integer.toString(widthpct) + "%>");
    }

    /** Starts a new table */
    protected void tableNew() {
        tableNew(true);
    }

    protected void tableNew(boolean padded) {
        if (padded) {
            tablespec = new StringBuffer(TABLE_START_PADDED);
        } else {
            tablespec = new StringBuffer(TABLE_START);
        }
    }

    protected void tableFinish() {
        tablespec.append(TABLE_FINISH_TABLE);
    }

    protected void tableAddRow() {
        tablespec.append(TABLE_NEW_ROW);
    }

    protected void tableFinishRow() {
        tablespec.append(TABLE_FINISH_ROW);
    }

    protected void tableAddCell(String value) {
        tablespec.append(TABLE_NEW_CELL);
        tablespec.append(value);
        tablespec.append(TABLE_FINISH_CELL);
    }

    protected void tableAddBoldCell(String value) {
        tablespec.append(TABLE_NEW_CELL);
        tablespec.append(bold(value));
        tablespec.append(TABLE_FINISH_CELL);
    }

    protected void tableAddCell(int value) {
        tablespec.append(TABLE_NEW_CELL);
        tablespec.append(value);
        tablespec.append(TABLE_FINISH_CELL);
    }

    public String money(Double d) {
        if (d == null) {
            d = new Double(0);
        }

        return Global.currencySymbol + d.toString();
    }

    public String date(Date d) {
        return Utils.formatDate(d);
    }

    public String datetime(Date d) {
        return Utils.formatDateTimeLong(d);
    }

    public String ntoe(String s) {
        if (s == null) {
            return "";
        }

        return s;
    }

    public String number(Number n) {
        if (n == null) {
            return "0";
        }

        return n.toString();
    }

    public String owner(Owner o) throws Exception {
        if (o == null) {
            return "";
        }

        return o.getOwnerName() + "<br />" + o.getOwnerAddress() + "<br />" +
        o.getOwnerPostcode() + "<br />" + o.getHomeTelephone();
    }

    public String yesNo(Integer v) {
        return (v.intValue() == 0) ? Global.i18n("reports", "No")
                                   : Global.i18n("reports", "Yes");
    }

    public String yesNoUnknown(Integer v) {
        switch (v.intValue()) {
        case 0:
            return Global.i18n("reports", "Yes");

        case 1:
            return Global.i18n("reports", "No");

        default:
            return Global.i18n("reports", "Unknown");
        }
    }

    public String yesNoNumberDate(Integer yn, String no, Date dt) {
        if (yn.intValue() == 0) {
            return Global.i18n("reports", "No");
        }

        return Utils.formatDate(dt) + " - " + no;
    }

    public String yesNoDate(Integer yn, Date dt) {
        if (yn.intValue() == 0) {
            return Global.i18n("reports", "No");
        }

        return Utils.formatDate(dt);
    }

    public String yesNoDatePos(Integer yn, Date dt, Integer posneg) {
        if (yn.intValue() == 0) {
            return Global.i18n("reports", "No");
        }

        Vector pn = new Vector();
        pn.add(Global.i18n("wordprocessor", "Unknown"));
        pn.add(Global.i18n("wordprocessor", "Negative"));
        pn.add(Global.i18n("wordprocessor", "Positive"));

        return Utils.formatDate(dt) + " - " +
        pn.get(posneg.intValue()).toString();
    }

    /** Reads the head.dat file from the DBFS server and
     *  returns it as a string. If no header is found, then
     *  a default header is returned
     * @return The header if one was found on the DBFS server, otherwise
     * returns a default hard-coded HTML header.
     */
    protected String getHeader() {
        try {
            DBFS dbfs = new DBFS();
            dbfs.chdir("reports");

            // Get a directory listing to see if head.dat is there
            String[] list = dbfs.list("head.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return substituteKeys(getDefaultHeader());
            }

            // Get it as a String
            String buffer = dbfs.readFileToString("head.dat");

            // Since we have our header file now, see if our
            // reports require any images.
            dbfs.saveAllImages(Global.tempDirectory);

            // Return it to caller after substituting keys
            return substituteKeys(buffer);
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "Error_occurred_retrieving_templates_", e.getMessage()),
                Global.i18n("reports", "Error"));
            Global.logException(e, getClass());

            return getDefaultHeader();
        }
    }

    /** Reads the foot.dat file from the DBFS server and
     *  returns it as a string. If no footer is found, then
     *  a default header is returned
     * @return The footer if one was found on the DBFS server, otherwise
     * returns a default hard-coded HTML footer.
     */
    protected String getFooter() {
        try {
            DBFS dbfs = new DBFS();
            dbfs.chdir("reports");

            // Get a directory listing to see if foot.dat is there
            String[] list = dbfs.list("foot.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return substituteKeys(getDefaultFooter());
            }

            // Get it as a String
            String buffer = dbfs.readFileToString("foot.dat");

            // Since we have our header file now, see if our
            // reports require any images.
            dbfs.saveAllImages(Global.tempDirectory);

            // Return it to caller after substituting keys
            return substituteKeys(buffer);
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "Error_occurred_retrieving_templates_", e.getMessage()),
                Global.i18n("reports", "Error"));
            Global.logException(e, getClass());

            return getDefaultFooter();
        }
    }

    /** Returns the default report header */
    protected String getDefaultHeader() {
        return "<html>\n" + "<head>\n" +
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
        "<title>" + getTitle() + "</title>\n\n" +
        "<!-- Embedded style sheet - required for internal zoom to work -->\n" +
        "<style>\n\n" + "</style>\n" + "</head>\n" +
        "<body bgcolor=\"white\" text=\"black\">\n" + "<center>\n" +
        "<h2>&nbsp;$$TITLE$$</h2>\n" + "</center>\n" + "<hr>\n";
    }

    /** Returns the default report footer */
    protected String getDefaultFooter() {
        return "<hr>\n" + "<p>\n" + "Report: <b>$$TITLE$$</b><br>\n" +
        "Generated by Animal Shelter Manager $$VERSION$$ at $$REGISTEREDTO$$ " +
        "on $$DATE$$ by $$USER$$\n" + "</p>\n" + "</body></html>";
    }

    /** Exchanges special tags in the passed string for information
     *  read from the system.
     * @param searchin The string to search for keys in
     * @return The string with keys replaced
     */
    protected String substituteKeys(String searchin) {
        // Make output string
        String output = new String(searchin);
        String todaysdate = Utils.getReadableTodaysDate();

        // $$TITLE$$ tag //
        output = Utils.replace(output, "$$TITLE$$", getTitle());
        // $$DATE$$ tag //
        output = Utils.replace(output, "$$DATE$$", todaysdate);
        // $$VERSION$$ tag //
        output = Utils.replace(output, "$$VERSION$$",
                net.sourceforge.sheltermanager.asm.globals.Global.productVersion);
        // $$USER$$ tag //
        output = Utils.replace(output, "$$USER$$",
                net.sourceforge.sheltermanager.asm.globals.Global.currentUserName);

        // $$REGISTEREDTO$$ tag //
        Settings settings = new Settings();

        try {
            output = Utils.replace(output, "$$REGISTEREDTO$$",
                    settings.getRegisteredTo());
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return output;
    }

    /** Sets the maximum value on the status bar to be used with this report,
     *  along with how often to update it.
     * @param max The maximum status bar value
      */
    protected void setStatusBarMax(int max) {
        if (displayAfter) {
            net.sourceforge.sheltermanager.asm.globals.Global.mainForm.initStatusBarMax(max);
        }
    }

    /** Sets the status bar text while generating the report
     * @param text The new status bar text
     */
    protected void setStatusText(String text) {
        if (displayAfter) {
            net.sourceforge.sheltermanager.asm.globals.Global.mainForm.setStatusText(text);
        }
    }

    /** Updates the value on the status bar to be used with this report.
     */
    protected void incrementStatusBar() {
        if (displayAfter) {
            net.sourceforge.sheltermanager.asm.globals.Global.mainForm.incrementStatusBar();
        }
    }

    /** Resets the status bar after the report is done */
    protected void resetStatusBar() {
        if (displayAfter) {
            net.sourceforge.sheltermanager.asm.globals.Global.mainForm.resetStatusBar();
        }
    }

    /** Displays the report to the user. Uses configuration settings to
     *  determine how to do it - either by using an internal viewer
     *  which relies on the Swing HTML EditorKit, or by using an
     *  external viewer, such as a web browser via a command line
     *  interface.
     */
    protected void display() {
        // See if the options say we are using our internal
        // browser to display the report
        if (Global.useInternalReportViewer) {
            ReportViewer rv = new ReportViewer(filename, getTitle());
            rv.setSize(UI.getDimension(512, 384));
            net.sourceforge.sheltermanager.asm.globals.Global.mainForm.addChild(rv);
            rv.setVisible(true);
        } else {
            FileTypeManager.shellExecute(filename);
        }
    }
}
