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

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.internet.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.io.*;

import java.util.*;


/**
 * Mail merge superclass. Handles details of showing progress meter and file
 * manipulation etc. Also handles saving of CSV files and informing user of
 * actions.
 *
 * At this stage, we do not go all the way with mail merge, we simply supply
 * them with CSV data sources, which can be loaded into anything. OpenOffice is
 * particularly good at this, having an SQL text driver for further filtering.
 *
 * To create a new mail merge source, extend this class and override the getData
 * method. This method should set fill a 2D Array (theData) with the information
 * for the merge. You should also override the "getFileName" method to return a
 * suitable filename for the data.
 *
 * @author Robin Rawson-Tetley
 */
public class MailMerge extends Thread implements EmailFormListener {
    /** Data to be used for mail merge source - assumed as [row][col] */
    protected String[][] theData = null;

    /** Number of columns in data */
    protected int cols = 0;

    /** Number of rows in data */
    protected int rows = 0;

    /** Col containing email address */
    protected int emailColumn = 0;

    public void run() {
        // Make sure the temp/mailmerge directory exists
        checkMailMergeDirectory();

        try {
            // Get the data
            setStatusText(Global.i18n("mailmerge",
                    "Reading_data,_please_wait..."));
            emailColumn = getEmailColumn();
            getData();
            resetStatusBar();

            // Write the data to disk or bulk email - ask
            String[] choices = {
                    Global.i18n("mailmerge", "produce_cvs"),
                    Global.i18n("mailmerge", "do_bulk")
                };
            String choice = Global.i18n("mailmerge", "produce_cvs");
            choice = (String) Dialog.getInput(Global.i18n("mailmerge",
                        "how_to_merge"),
                    Global.i18n("mailmerge", "merge_type"), choices, choice);

            if (choice == null) {
                return;
            }

            if (choice.equals(Global.i18n("mailmerge", "produce_cvs"))) {
                // Output the CSV file
                setStatusText(Global.i18n("mailmerge", "Outputting_to_disk..."));
                writeToDisk();
                setStatusText("");
                theData = null;
            } else {
                // Verify we are set up
                if (!Email.isSetup()) {
                    theData = null;

                    return;
                }

                // Verify there is an email column
                if (emailColumn == 0) {
                    Global.logError("Internal error - no email column in data",
                        "MailMerge.run");
                    Dialog.showError(
                        "Internal error - no email column in data.");

                    return;
                }

                // Build a vector of available fields for this source
                Vector fields = new Vector();

                for (int i = 0; i < cols; i++) {
                    Global.logDebug("Got column: " + theData[0][i],
                        "MailMerge.run");
                    fields.add("<<" + theData[0][i] + ">>");

                    // Fix any null values in the data
                    for (int z = 0; z < rows; z++)
                        if (theData[z][i] == null) {
                            theData[z][i] = "";
                        }
                }

                // Request email content from the user
                Email.multiEmailForm(this, fields);
                fields = null;
            }
        } catch (NoDataException e) {
            Global.logError(Global.i18n("mailmerge",
                    "There_are_no_records_to_add_to_the_mail_merge_source."),
                "MailMerge.run");
            Dialog.showError(Global.i18n("mailmerge",
                    "There_are_no_records_to_add_to_the_mail_merge_source."));
            resetStatusBar();
            setStatusText("");
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
            resetStatusBar();
            setStatusText("");
            theData = null;
        }
    }

    /** Override this method in subclasses to get data */
    protected void getData() throws CursorEngineException, NoDataException {
    }

    /** Override this method in subclasses to return appropriate file name */
    protected String getFileName() {
        return "";
    }

    /** Override this method in subclass to return column with email address */
    protected int getEmailColumn() {
        return 0;
    }

    /**
     * Sets the maximum value on the status bar to be used with this report,
     * along with how often to update it.
     *
     * @param max
     *            The maximum status bar value
     */
    protected void setStatusBarMax(int max) {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.initStatusBarMax(max);
    }

    /**
     * Sets the status bar text while generating the report
     *
     * @param text
     *            The new status bar text
     */
    protected void setStatusText(String text) {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.setStatusText(text);
    }

    /**
     * Updates the value on the status bar to be used with this report.
     */
    protected void incrementStatusBar() {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.incrementStatusBar();
    }

    /** Resets the status bar after the report is done */
    protected void resetStatusBar() {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.resetStatusBar();
    }

    /** Writes the report out to the named file */
    protected void writeToDisk() {
        File file = null;

        try {
            // The default name for the file
            String defaultFile = Utils.getDefaultDocumentPath() +
                File.separator + getFileName();

            // Prompt user for where they'd like to save it to
            UI.FileChooser fc = UI.getFileChooser();
            fc.setSelectedFile(new File(defaultFile));

            int result = fc.showSaveDialog(Global.mainForm);

            // Cancel if they cancelled
            if (result != UI.FileChooser.APPROVE_OPTION) {
                return;
            }

            // Get the location
            String path = fc.getSelectedFile().getAbsolutePath();

            // Create file handle and output stream
            file = new File(path);

            FileOutputStream out = new FileOutputStream(file);

            // Now output the data ------------
            boolean firstField = false;
            byte[] comma = new String(",").getBytes();

            setStatusBarMax(rows);

            for (int z = 0; z < rows; z++) {
                firstField = true;

                for (int i = 0; i < cols; i++) {
                    if (!firstField) {
                        out.write(comma);
                    } else {
                        firstField = false;
                    }

                    ;
                    out.write(new String("\"" + theData[z][i] + "\"").getBytes(
                            Global.CHAR_ENCODING));
                }

                // Terminate row
                out.write(new String("\n").getBytes(Global.CHAR_ENCODING));

                incrementStatusBar();
            }

            resetStatusBar();

            out.close();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Ensures there is a "mailmerge" directory off the local machine's temp
     * folder to store output from the mail merge (although it's down to the
     * user's choice now).
     */
    private void checkMailMergeDirectory() {
        // Make sure we have a mailmerge directory
        // in the temp folder
        File file = new File(Global.tempDirectory + File.separator +
                "mailmerge");

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }
    }

    /** Clean up action if bulk email cancelled */
    public void cancelEmail() {
        theData = null;
        setStatusText("");
        resetStatusBar();
    }

    public void sendEmail(String subject, String body, String content_type) {
        new BulkEmail(subject, body, content_type, theData, emailColumn, cols, rows);
    }
}


class BulkEmail extends Thread {
    private String subject;
    private String body;
    private String content_type;
    private String[][] theData;
    private int emailColumn;
    private int cols;
    private int rows;

    public BulkEmail(String subject, String body, String content_type, 
        String[][] theData, int emailColumn, int cols, int rows) {

        this.subject = subject;
        this.body = body;
        this.content_type = content_type;
        this.theData = theData;
        this.emailColumn = emailColumn;
        this.cols = cols;
        this.rows = rows;

        this.start();
    }

    public void run() {
        try {
            Global.mainForm.initStatusBarMax(rows - 1);

            Email email = new Email();

            // 1-based as row 0 holds headers
            for (int i = 1; i < rows; i++) {
                Global.mainForm.setStatusText(Integer.toString(i) + " / " +
                    Integer.toString(rows - 1));

                // Make sure there is an address
                try {
                    if (!theData[i][emailColumn].equals("") &&
                            (theData[i][emailColumn] != null)) {
                        email.sendmsg(theData[i][emailColumn], subject,
                            replaceInText(body, i), Email.getLocalEmail(),
                            content_type);
                    }
                } catch (Exception e) {
                    Global.logError(Global.i18n("mailmerge",
                            "error_occurred_sending_mail_to",
                            theData[i][emailColumn], e.getMessage()),
                        "BulkMail.run");
                    Global.logException(e, getClass());
                }

                Global.mainForm.incrementStatusBar();
            }

            email.close();
            email = null;

            Dialog.showInformation(Global.i18n("mailmerge", "bulk_mail_complete"),
                Global.i18n("mailmerge", "bulk_mail_complete"));
        } catch (Exception e) {
            Global.logException(e, getClass());
        } finally {
            theData = null;
            subject = null;
            body = null;
            Global.mainForm.setStatusText("");
            Global.mainForm.resetStatusBar();
        }
    }

    /**
     * Exchanges keys for real data
     */
    public String replaceInText(String thetext, int rowIndex) {
        // Replace all those tags in our string file buffer
        String output = new String(thetext);

        for (int i = 0; i < cols; i++) {
            output = Utils.replace(output, "<<" + theData[0][i] + ">>",
                    theData[rowIndex][i]);
        }

        return output;
    }
}
