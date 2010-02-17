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
package net.sourceforge.sheltermanager.asm.internet;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.ftp.FTPClient;
import net.sourceforge.sheltermanager.asm.ftp.FTPException;
import net.sourceforge.sheltermanager.asm.ftp.FTPTransferType;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.*;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.HTMLViewer;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.Vector;


/**
 * The actual class that does the publishing work. Runs in a separate process
 * for progress meter reasons.
 *
 * @author Robin Rawson-Tetley
 * @version 2.0
 */
public class Publisher extends Thread {
    /** Reference to the parent */
    private InternetPublisher parent = null;
    private PublishCriteria publishCriteria = null;
    private final boolean debug = true;

    /** The upload socket if upload is on */
    private FTPClient uploadFTP = null;

    /** The javaScript file */
    private String javaScript = "";
    private int javaArrayElement = 0;

    /** The navigation bar HTML */
    private String navBar = "";

    /**
     * Date representing age that animals must be older than (dob must be
     * before) to qualify
     */
    private Calendar ageExclusion = null;

    public Publisher(InternetPublisher parent, PublishCriteria publishCriteria) {
        this.parent = parent;
        this.publishCriteria = publishCriteria;
    }

    /** The main application thread */
    public void run() {
        // Get the publish directory and clean it
        String publishDir = makePublishDirectory();

        // If uploading is on, connect to the remote host
        if (publishCriteria.uploadDirectly) {
            openUploadSocket();
        }

        // Flush any HTML files already present on the host
        if (publishCriteria.uploadDirectly && publishCriteria.clearExisting) {
            clearExistingHTML();
        }

        // Get the header, footer and body information
        outputStatusText(Global.i18n("uiinternet", "retrieving_page_templates"));

        String normHeader = getHeader();
        String normFooter = getFooter();
        String header = ""; // These are updated every page
        String footer = ""; // because the navbar changes every page
        String body = getBody();

        // Get a list of animals
        outputStatusText(Global.i18n("uiinternet", "retrieving_animal_list"));

        Animal an = new Animal();

        // Get list of animals, filtering where possible now
        an.openRecordset(buildWhereFilter());

        // Start the progress meter
        if (parent != null) {
            Global.mainForm.initStatusBarMax((int) an.getRecordCount());
        }

        outputStatusText(Global.i18n("uiinternet", "Determining_Workload..."));

        // Calculate how many animal records will match
        // for the NavBar information and thus how many
        // pages will be generated
        int noAnimals = 0;
        int noPages = 0;

        try {
            while (!an.getEOF()) {
                if (checkAnimal(an)) {
                    noAnimals++;
                }

                an.moveNext();

                if (parent != null) {
                    Global.mainForm.incrementStatusBar();
                }
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

        int animalsPerPage = publishCriteria.animalsPerPage;

        // Calculate the number of pages required
        if (noAnimals <= animalsPerPage) {
            noPages = 1;
        } else {
            double calc = ((double) noAnimals / (double) animalsPerPage);
            double wholevalue = (double) Math.round(calc);

            // If there is a remainder (ie. calc > wholevalue),
            // then we need wholevalue + 1 pages - eg. 5.2 pages
            // required means 6 are needed.
            if (calc > wholevalue) {
                noPages = (int) wholevalue;
                noPages++;
            } else {
                noPages = (int) wholevalue;
            }
        }

        // Build the page navigation bar
        if (noPages > 1) {
            StringBuffer theNav = new StringBuffer("");

            for (int i = 1; i <= noPages; i++) {
                theNav.append("<a href=" + Integer.toString(i) + "." +
                    publishCriteria.extension + ">" + Integer.toString(i) +
                    "</a>&nbsp;");
            }

            navBar = theNav.toString();
        }

        // Start a new page with a header
        String thisPageName = "1." + publishCriteria.extension;
        int currentPage = 1;
        int itemsOnPage = 0;

        // Substitute tags in the header and footer for this page
        header = substituteHFTag(normHeader, currentPage);
        footer = substituteHFTag(normFooter, currentPage);

        StringBuffer thisPage = new StringBuffer(header);

        // Cycle through the animals
        if (parent != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.initStatusBarMax((int) an.getRecordCount());
        }

        outputStatusText(Global.i18n("uiinternet", "Publishing..."));

        try {
            an.moveFirst();

            int anCount = 0;

            while (!an.getEOF()) {
                // Make sure that animal is valid
                if (checkAnimal(an)) {
                    anCount++;

                    if (debug) {
                        Global.logInfo("Processing: " + an.getShelterCode() +
                            ": " + an.getAnimalName() + " (" + anCount +
                            " of " + noAnimals + ")", "Publisher.run");
                    }

                    // Only do the file handling if the animal actually has
                    // photo media
                    if (an.hasValidMedia()) {
                        // Get the name of the animal's image file. We use the
                        // animal's code as the filename
                        String animalweb = an.getWebMedia();
                        String animalcode = an.getShelterCode();
                        String animalpic = animalcode + "-1.jpg";

                        // Copy the animal image to the publish folder
                        if (debug) {
                            Global.logInfo("Retrieving image.", "Publisher.run");
                        }

                        try {
                            DBFS dbfs = Utils.getDBFSDirectoryForLink(0,
                                    an.getID().intValue());

                            try {
                                dbfs.readFile(animalweb, publishDir +
                                    animalweb);

                                // If scaling is on, scale the image
                                if (publishCriteria.scaleImages != 1) {
                                    scaleImage(publishDir + animalweb,
                                        publishCriteria.scaleImages);
                                }

                                // If upload all was set, the user wants the
                                // image filename to map to the animal's sheltercode
                                // instead of the media filename (plus index the
                                // images code-1.jpg, code-2.jpg). We won't
                                // bother uploading the main media file in this event
                                // either.
                                if (publishCriteria.uploadAllImages) {
                                    dbfs.readFile(animalweb,
                                        publishDir + animalpic);

                                    if (publishCriteria.scaleImages != 1) {
                                        scaleImage(publishDir + animalpic,
                                            publishCriteria.scaleImages);
                                    }

                                    if (publishCriteria.uploadDirectly) {
                                        upload(animalpic);
                                    }
                                } else {
                                    // If we're not uploading all images, just do the
                                    // main media file in the old way
                                    if (publishCriteria.uploadDirectly) {
                                        upload(animalweb);
                                    }
                                }
                            }
                            // If an IO Error occurs, the file is already in the
                            // publish directory.
                            catch (Exception e) {
                            }

                            // If the upload all option is set, grab all of
                            // the images this animal has and save them
                            if (publishCriteria.uploadAllImages) {
                                int idx = 1;
                                String[] images = dbfs.list();

                                if (debug) {
                                    Global.logInfo("Animal has " +
                                        images.length + " media files",
                                        "Publisher.run");
                                }

                                for (int i = 0; i < images.length; i++) {
                                    // Ignore the main web media - we used that
                                    if (!animalweb.equals(images[i]) &&
                                            isImage(images[i])) {
                                        idx++;

                                        String otherpic = animalcode + "-" +
                                            idx + ".jpg";

                                        if (debug) {
                                            Global.logInfo(
                                                "Retrieving additional image: " +
                                                otherpic + " (" + images[i] +
                                                ")", "Publisher.run");
                                        }

                                        dbfs.readFile(images[i],
                                            publishDir + otherpic);

                                        // If scaling is on, scale the image
                                        if (publishCriteria.scaleImages != 1) {
                                            scaleImage(publishDir + otherpic,
                                                publishCriteria.scaleImages);
                                        }

                                        // If uploading is switched on, upload the file
                                        if (debug) {
                                            Global.logInfo(
                                                "Uploading additional image: " +
                                                otherpic + " (" + images[i] +
                                                ")", "Publisher.run");
                                        }

                                        if (publishCriteria.uploadDirectly) {
                                            upload(otherpic);
                                        }
                                    }
                                }
                            }

                            dbfs = null;
                        }
                        // Ignore errors retrieving files from the media
                        // server.
                        catch (Exception e) {
                        }

                        if (debug) {
                            Global.logInfo("Retrieved image.", "Publisher.run");
                        }

                        // If the upload option is on, upload this file
                        if (publishCriteria.uploadDirectly) {
                            if (debug) {
                                Global.logInfo("Uploading image.",
                                    "Publisher.run");
                            }

                            upload(animalweb);

                            if (debug) {
                                Global.logInfo("Image uploaded.",
                                    "Publisher.run");
                            }
                        }
                    }

                    // Update our ongoing JavaScript database if
                    // the option is switched on
                    if (publishCriteria.generateJavascriptDB) {
                        updateJavaScript(an);
                    }

                    // Is there a slot free to put it on this page?
                    if (itemsOnPage < animalsPerPage) {
                        // Yes - do so
                        thisPage.append(substituteBodyTags(body, an));
                        itemsOnPage++;

                        // Clear the new/updated flags for the animal
                        // we just published
                        an.clearWebMediaFlags();

                        if (debug) {
                            Global.logInfo("Finished processing " +
                                an.getShelterCode(), "Publisher.run");
                        }
                    } else {
                        if (debug) {
                            Global.logInfo("Current page complete.",
                                "Publisher.run");
                        }

                        // No, append the footer, flush the page
                        // out to the publish folder and optionally
                        // upload it to the internet site
                        thisPage.append(footer);

                        if (debug) {
                            Global.logInfo("Saving page to disk.",
                                "Publisher.run");
                        }

                        saveFile(publishDir + thisPageName, thisPage.toString());

                        if (debug) {
                            Global.logInfo("Page saved to disk.",
                                "Publisher.run");
                        }

                        // Upload if the option is set
                        if (publishCriteria.uploadDirectly) {
                            if (debug) {
                                Global.logInfo("Uploading page.",
                                    "Publisher.run");
                            }

                            upload(thisPageName);

                            if (debug) {
                                Global.logInfo("Page uploaded.", "Publisher.run");
                            }
                        }

                        // Start a new page
                        currentPage++;
                        thisPageName = Integer.toString(currentPage) + "." +
                            publishCriteria.extension;

                        // Substitute tags in the header and footer for this
                        // page
                        header = substituteHFTag(normHeader, currentPage);
                        footer = substituteHFTag(normFooter, currentPage);

                        thisPage = new StringBuffer(header);
                        itemsOnPage = 0;

                        // append this animal
                        thisPage.append(substituteBodyTags(body, an));
                        itemsOnPage++;

                        if (debug) {
                            Global.logInfo("Finished processing" +
                                an.getShelterCode(), "Publisher.run");
                        }
                    }
                }

                an.moveNext();

                if (parent != null) {
                    Global.mainForm.incrementStatusBar();
                }
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }

        // Remove animal object
        an.free();
        an = null;

        // Animals have now finished. Append a footer, Flush the final
        // page to disk and clean up
        thisPage.append(footer);
        saveFile(publishDir + thisPageName, thisPage.toString());

        if (publishCriteria.uploadDirectly) {
            if (debug) {
                Global.logInfo("Uploading final page.", "Publisher.run");
            }

            upload(thisPageName);

            if (debug) {
                Global.logInfo("Final page uploaded.", "Publisher.run");
            }
        }

        // Upload the JavaScript database if we are doing that
        if (publishCriteria.generateJavascriptDB) {
            writeJavaScript();
        }

        if (parent != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }

        // If uploading was on, disconnect from the remote host
        if (publishCriteria.uploadDirectly) {
            closeUploadSocket();
        }

        // Save any additional images required by the template
        saveTemplateImages(publishDir);

        // Ask them if they want to view the completed pages
        if (parent != null) {
            if (Dialog.showYesNo(Global.i18n("uiinternet",
                            "Web_site_creation_completed.\nWould_you_like_to_view_the_pages_now?"),
                        Global.i18n("uiinternet", "View_Site?"))) {
                display();
            }
        } else {
            if (debug) {
                Global.logInfo("Publishing complete.", "Publisher.run");
            }
        }

        // Re-enable buttons
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }

        // Clear up
        parent = null;
    }

    private boolean isImage(String s) {
        s = s.toLowerCase();

        return s.endsWith("jpg") || s.endsWith("jpeg") || s.endsWith("png");
    }

    private String getHeader() {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);

            // Get a directory listing to see if pih.dat is there
            String[] list = dbfs.list("pih.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return getDefaultHeader();
            }

            // Get it as a String
            return dbfs.readFileToString("pih.dat");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return getDefaultHeader();
        }
    }

    private String getFooter() {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);

            // Get a directory listing to see if pif.dat is there
            String[] list = dbfs.list("pif.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return getDefaultFooter();
            }

            // Get it as a String
            return dbfs.readFileToString("pif.dat");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return getDefaultFooter();
        }
    }

    private String getBody() {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);

            // Get a directory listing to see if pib.dat is there
            String[] list = dbfs.list("pib.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return getDefaultBody();
            }

            // Get it as a String
            return dbfs.readFileToString("pib.dat");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return getDefaultBody();
        }
    }

    /** Changes to the correct directory for
     *  our selected style */
    private void changeForStyle(DBFS dbfs) {
        try {
            dbfs.chdir("internet");

            String d = publishCriteria.style;

            // Done if we're on the base style
            if (d.equals(".")) {
                return;
            }

            // Change to style dir
            dbfs.chdir(d);
        } catch (Exception e) {
            Global.logException(e, Publisher.class);
        }
    }

    private String getDefaultHeader() {
        String s = "<html><head><title>Animals Available for Adoption</title></head>";
        s += "<body>";
        s += "<p>$$NAV$$</p>";
        s += "<p><table width=100%>";

        return s;
    }

    private String getDefaultFooter() {
        String s = "</table></body></html>";

        return s;
    }

    private String getDefaultBody() {
        return "<tr><td><img height=200 width=320 src=$$IMAGE$$></td><td><b>$$ShelterCode$$ - $$AnimalName$$</b><br>$$BreedName$$ $$SpeciesName$$ aged $$Age$$<br><br><b>Details</b><br><br>$$WebMediaNotes$$<hr></td></tr>";
    }

    /**
     * Exchanges special tags in the passed string for information read from the
     * system.
     *
     * @param searchin
     *            The string to search for keys in
     * @return The string with keys replaced
     */
    private String substituteHFTag(String searchin, int page) {
        // Make output string
        String output = new String(searchin);
        String todaysdate = Utils.getReadableTodaysDate();

        // $$NAV$$ tag
        String navThisPage = Utils.replace(navBar,
                "<a href=" + page + "." + publishCriteria.extension + ">" +
                page + "</a>", Integer.toString(page));
        output = Utils.replace(output, "$$NAV$$", navThisPage);

        // $$DATE$$ tag //
        output = Utils.replace(output, "$$DATE$$", todaysdate);
        // $$VERSION$$ tag //
        output = Utils.replace(output, "$$VERSION$$", Global.productVersion);

        // $$REGISTEREDTO$$ tag //
        output = Utils.replace(output, "$$REGISTEREDTO$$",
                Configuration.getString("Organisation"));

        try {
            // $$USER$$ tag //
            if ((Global.currentUserName != null) &&
                    (Global.currentUserObject != null)) {
                output = Utils.replace(output, "$$USER$$",
                        Global.currentUserName + " (" +
                        Global.currentUserObject.getRealName() + ")");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // $$ORGNAME$$ tag //
        output = Utils.replace(output, "$$ORGNAME$$",
                Configuration.getString("Organisation"));

        // $$ORGADDRESS$$ tag //
        output = Utils.replace(output, "$$ORGADDRESS$$",
                Configuration.getString("OrganisationAddress"));

        // $$ORGTEL$$ tag //
        output = Utils.replace(output, "$$ORGTEL$$",
                Configuration.getString("OrganisationTelephone"));

        // $$ORGEMAIL$$ tag //
        output = Utils.replace(output, "$$ORGEMAIL$$",
                Configuration.getString("EmailAddress"));

        return output;
    }

    private String substituteBodyTags(String findin, Animal a) {
        // Create a new animal document so we can use the tags
        AnimalDocument ad = new AnimalDocument(a, true);

        // Replace them and return
        return ad.replaceInTextInternet(findin);
    }

    private void outputStatusText(String text) {
        if (parent != null) {
            Global.mainForm.setStatusText(text);
        } else {
            Global.logInfo(text, "Publisher");
        }
    }

    private String makePublishDirectory() {
        // Make sure we have a publish directory
        // in the temp folder and destroy it's
        // contents.
        File file = new File(Global.tempDirectory + File.separator + "publish");

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }

        return Global.tempDirectory + File.separator + "publish" +
        File.separator;
    }

    /**
     * Filters the animal list according to selected criteria in the initial
     * openRecordset to save memory space in having to retrieve the entire set
     * of animals
     */
    private String buildWhereFilter() {
        StringBuffer sql = new StringBuffer("");

        // If the include case animals option is off, make
        // sure the animal isn't a case one
        if (!publishCriteria.includeCase) {
            sql.append("CrueltyCase = 0");
        }

        // Only include animals in the selected internal
        // locations - there must be one selected, so don't
        // worry about bad SQL with the AND here.
        // If no locations are specified, then don't do a filter
        // ie. do all
        if (publishCriteria.internalLocations != null) {
            if (!sql.toString().equals("")) {
                sql.append(" AND ");
            }

            sql.append("ShelterLocation in (");

            boolean firstLoc = true;

            for (int i = 0; i < publishCriteria.internalLocations.length;
                    i++) {
                if (firstLoc) {
                    firstLoc = false;
                } else {
                    sql.append(",");
                }

                sql.append(publishCriteria.internalLocations[i].toString());
            }

            sql.append(")");
        }

        // Check that the animal is old enough to be adopted
        // according to our exclusion. Calculate the age
        // exclusion before today and filter to only include
        // animals with a date of birth before it.
        Calendar today = Calendar.getInstance();
        int noWeeks = publishCriteria.excludeUnderWeeks;
        int noDays = (noWeeks * 7);
        today.add(Calendar.DAY_OF_YEAR, (noDays * -1));

        if (!sql.toString().equals("")) {
            sql.append(" AND ");
        }

        sql.append("DateOfBirth <= '" + Utils.getSQLDate(today) + "'");

        // Filter out dead animals, and ones not for adoption
        if (!sql.toString().equals("")) {
            sql.append(" AND ");
        }

        sql.append("DeceasedDate Is Null AND IsNotAvailableForAdoption = 0");

        // If including fosterers is on, allow ones with an active movement type
        // of foster as well as on shelter
        if (publishCriteria.includeFosters) {
            sql.append(" AND (Archived = 0 OR ActiveMovementType = " +
                Adoption.MOVETYPE_FOSTER + ")");
        } else {
            // Make sure we are on-shelter only (filter out fosters where
            // foster on shelter set)
            sql.append(
                " AND Archived = 0 AND (ActiveMovementType Is Null OR ActiveMovementType <> " +
                Adoption.MOVETYPE_FOSTER + ")");
        }

        sql.append(" ORDER BY MostRecentEntryDate");

        return sql.toString();
    }

    /**
     * Returns true if an animal is ok to be included in the publish
     */
    private boolean checkAnimal(Animal a) throws CursorEngineException {
        // Make sure it has a valid picture if the option is off
        if (!publishCriteria.includeWithoutImage) {
            if (!a.hasValidMedia()) {
                return false;
            }
        }

        // If the include reserves option is off, make
        // sure it isn't reserved
        if (!publishCriteria.includeReserved) {
            if (a.isAnimalReserved()) {
                return false;
            }
        }

        // If we got here, all must be ok
        return true;
    }

    /**
     * Looks at the directory the upload socket is pointing to and removes any
     * existing HTML files.
     */
    public void clearExistingHTML() {
        try {
            String existing = uploadFTP.list("*." + publishCriteria.extension);
            String[] files = Utils.split(existing, "\n");

            for (int i = 0; i < files.length; i++) {
                if (!(files[i].trim()
                                  .equalsIgnoreCase("search." +
                            publishCriteria.extension))) {
                    uploadFTP.delete(files[i].trim());
                }
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Flushes the given content to the named file.
     */
    private void saveFile(String filepath, String content) {
        try {
            File file = new File(filepath);
            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes(Global.CHAR_ENCODING));
            out.flush();
            out.close();
        } catch (IOException e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }
    }

    /**
     * Reads the configuration settings and opens a connection to the users
     * remote internet FTP server
     */
    private void openUploadSocket() {
        try {
            String host = Configuration.getString("FTPURL");
            String user = Configuration.getString("FTPUser");
            String password = Configuration.getString("FTPPassword");
            String port = Configuration.getString("FTPPort");
            String root = Configuration.getString("FTPRootDirectory");

            // Override the FTP root if we were given one
            // with the criteria
            if (publishCriteria.ftpRoot != null) {
                root = publishCriteria.ftpRoot;
            }

            uploadFTP = new FTPClient(host, Integer.parseInt(port));
            uploadFTP.login(user, password);
            uploadFTP.setType(FTPTransferType.BINARY);

            if (!root.trim().equals("")) {
                uploadFTP.chdir(root);
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }
    }

    /**
     * Destroys the users internet FTP socket
     */
    private void closeUploadSocket() {
        try {
            uploadFTP.quit();
            uploadFTP = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Called before each upload - verifies that the socket is still active by
     * request a directory. If it cannot get one, the socket is reopened.
     */
    private void checkUploadSocket() {
        // Verify that the upload socket is still live by requesting
        // a directory for the current file - we are looking for an
        // error - who cares what comes back (mental note - allow
        // access to the FTP socket publically).
        try {
            String ignoreResponse = uploadFTP.list("*");
            // Make sure transfers are back to binary
            uploadFTP.setType(FTPTransferType.BINARY);
        } catch (FTPException e) {
            // Destroy the current socket
            closeUploadSocket();
            // Open a new one
            openUploadSocket();
        } catch (IOException e) {
            // Destroy the current socket
            closeUploadSocket();
            // Open a new one
            openUploadSocket();
        }
    }

    /**
     * Uploads a file from the publish directory in the temp folder to the
     * internet site according to the FTP settings. If the file already exists
     * (and it is not HTML or JavaScript), it is not uploaded again.
     */
    private void upload(String filename) {
        try {
            String publishDir = Global.tempDirectory + File.separator +
                "publish" + File.separator;

            // Make sure the local file actually exists - if it doesn't we
            // may as well drop out now and not risk blowing up the FTP
            // connection.
            File localfile = new File(publishDir + filename);

            if (!localfile.exists()) {
                return;
            }

            checkUploadSocket();

            // If our file is HTML or JavaScript, then just upload it over
            // the top
            try {
                if ((filename.indexOf("." + publishCriteria.extension) != -1) ||
                        (filename.indexOf(".js") != -1)) {
                    uploadFTP.put(publishDir + filename, filename);

                    return;
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Does the file already exist? If so, return
            // and don't upload
            try {
                String alreadyThere = uploadFTP.list(filename);

                if ((alreadyThere.indexOf(filename) != -1) &&
                        !publishCriteria.forceReupload) {
                    return;
                }
            } catch (FTPException e) {
                // Do nothing and carry on. The error occurs
                // because the list command returned no matching files -
                // hence we need to upload.
            }

            // Make sure transfers are back to binary
            uploadFTP.setType(FTPTransferType.BINARY);

            // Upload the file
            uploadFTP.put(publishDir + filename, filename);
        }
        // Ignore errors - if a file already exists then don't try to
        // upload it again.
        catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Displays the site to the user
     */
    private void display() {
        String filename = Global.tempDirectory + File.separator + "publish" +
            File.separator + "1." + publishCriteria.extension;

        if (Global.useInternalReportViewer) {
            Global.mainForm.addChild(new HTMLViewer("file:///" + filename));
        } else {
            FileTypeManager.shellExecute(filename);
        }
    }

    /**
     * Writes the JavaScript database out to a file called db.js
     */
    private void writeJavaScript() {
        // Calculate the publish date

        // Add declarations to the front
        String decs = "";
        String idx = Integer.toString(javaArrayElement);

        decs += ("var publishDate = \"" + Utils.getReadableTodaysDate() +
        "\";\n");
        decs += ("var name = new Array(" + idx + ");\n");
        decs += ("var age = new Array(" + idx + ");\n");
        decs += ("var image = new Array(" + idx + ");\n");
        decs += ("var breed = new Array(" + idx + ");\n");
        decs += ("var species = new Array(" + idx + ");\n");
        decs += ("var type = new Array(" + idx + ");\n");
        decs += ("var colour = new Array(" + idx + ");\n");
        decs += ("var shelterlocation = new Array(" + idx + ");\n");
        decs += ("var markings = new Array(" + idx + ");\n");
        decs += ("var details = new Array(" + idx + ");\n");
        decs += ("var sheltercode = new Array(" + idx + ");\n");
        decs += ("var dateofbirth = new Array(" + idx + ");\n");
        decs += ("var sex = new Array(" + idx + ");\n");
        decs += ("var size = new Array(" + idx + ");\n");
        decs += ("var goodwithkids = new Array(" + idx + ");\n");
        decs += ("var goodwithcats = new Array(" + idx + ");\n");
        decs += ("var goodwithdogs = new Array(" + idx + ");\n");
        decs += ("var housetrained = new Array(" + idx + ");\n");
        decs += ("var comments = new Array(" + idx + ");\n");

        javaScript = decs + javaScript;

        saveFile(Global.tempDirectory + File.separator + "publish" +
            File.separator + "db.js", javaScript);

        // If uploading is on, upload it too
        if (publishCriteria.uploadDirectly) {
            if (debug) {
                Global.logInfo("Uploading javascript database.", "Publisher.run");
            }

            upload("db.js");

            if (debug) {
                Global.logInfo("Uploaded javascript database.", "Publisher.run");
            }
        }
    }

    /**
     * Updates the JavaScript database while building for use with internet site
     * search facilities.
     */
    private void updateJavaScript(Animal a) {
        // Builds up javascript instructions to create
        // multiple arrays reflecting our data. A clever frontend
        // program can then use the file to allow interactive
        // searching by a client-side browser script.
        try {
            String idx = Integer.toString(javaArrayElement);

            javaScript += ("name[" + idx + "] = \"" + a.getAnimalName() +
            "\";\n");
            javaScript += ("image[" + idx + "] = \"" +
            (a.getWebMedia().equals("") ? "nopic.jpg" : a.getWebMedia()) +
            "\";\n");
            javaScript += ("age[" + idx + "] = \"" + a.getAge() + "\";\n");
            javaScript += ("breed[" + idx + "] = \"" + a.getBreedName() +
            "\";\n");
            javaScript += ("species[" + idx + "] = \"" + a.getSpeciesName() +
            "\";\n");
            javaScript += ("type[" + idx + "] = \"" + a.getAnimalTypeName() +
            "\";\n");
            javaScript += ("colour[" + idx + "] = \"" + a.getBaseColourName() +
            "\";\n");
            javaScript += ("shelterlocation[" + idx + "] = \"" +
            a.getShelterLocationName() + "\";\n");
            javaScript += ("markings[" + idx + "] = \"" +
            fixJSText(a.getMarkings()) + "\";\n");
            javaScript += ("details[" + idx + "] = \"" +
            fixJSText(a.getWebMediaNotes()) + "\";\n");
            javaScript += ("sheltercode[" + idx + "] = \"" +
            a.getShelterCode() + "\";\n");
            javaScript += ("dateofbirth[" + idx + "] = \"" +
            Utils.formatDateLong(a.getDateOfBirth()) + "\";\n");
            javaScript += ("sex[" + idx + "] = \"" + a.getSexName() + "\";\n");
            javaScript += ("size[" + idx + "] = \"" + a.getSizeName() +
            "\";\n");
            javaScript += ("goodwithkids[" + idx + "] = \"" +
            ((a.isGoodWithKids().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("goodwithcats[" + idx + "] = \"" +
            ((a.isGoodWithCats().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("goodwithdogs[" + idx + "] = \"" +
            ((a.isGoodWithDogs().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("housetrained[" + idx + "] = \"" +
            ((a.isHouseTrained().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("comments[" + idx + "] = \"" +
            fixJSText(a.getAnimalComments()) + "\";\n");

            javaArrayElement++;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Used by javascript outputter - it checks the given text for line breaks
     * and speech marks -- speechmarks become apostrophes, and line breaks
     * become spaces.
     *
     * @param text
     *            The text to check
     * @return the text with apostrophes and line breaks converted.
     */
    public String fixJSText(String text) {
        text = text.replace('"', '\'');
        text = Utils.replace(text, new String(new byte[] { 13, 10 }), " ");
        text = text.replace('\n', ' ');

        return text;
    }

    /** Return the list of available publishing styles (directories
     *  in dbfs/internet/
     */
    public static Vector getStyles() {
        Vector v = new Vector();

        try {
            DBFS dbfs = new DBFS();
            dbfs.chdir("internet");

            String[] d = dbfs.list();

            for (int i = 0; i < d.length; i++) {
                if (d[i].equalsIgnoreCase("pib.dat")) {
                    v.add(".");
                }

                if (dbfs.isDir(d[i])) {
                    v.add(d[i]);
                }
            }

            return v;
        } catch (Exception e) {
            Global.logException(e, Publisher.class);
        }

        return null;
    }

    /**
     * Saves all the images from a template to the publish
     * directory.
     */
    public void saveTemplateImages(String publishDir) {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);
            dbfs.saveAllImages(publishDir);
        } catch (Exception e) {
            Global.logException(e, Publisher.class);
        }
    }

    /**
     * Scales an image to a particular size, keeping the aspect ratio.
     *
     * @param pathToImage
     *            The absolute path and name of the image to scale.
     * @param scalesize
     *            A string representing the new image size. Your choice is
     *            300x200, 640x400, 800x600 or 1024x768.
     */
    public void scaleImage(String pathToImage, int scalesize) {
        int width = 320;
        int height = 200;

        // Work out from the string what the widths and
        // heights should be
        if (scalesize == 2) {
            width = 320;
            height = 200;
        }

        if (scalesize == 3) {
            width = 640;
            height = 400;
        }

        if (scalesize == 4) {
            width = 800;
            height = 600;
        }

        if (scalesize == 5) {
            width = 1024;
            height = 768;
        }

        if (scalesize == 6) {
            width = 300;
            height = 300;
        }

        if (scalesize == 7) {
            width = 95;
            height = 95;
        }

        int maxDim = width;
        UI.scaleImage(pathToImage, pathToImage, width, height);
    }
}
