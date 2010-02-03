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
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Calendar;


/**
 * The actual class that does the Pets911 publishing work. Runs in a separate
 * process for progress meter reasons.
 *
 * @author Robin Rawson-Tetley
 * @version 2.0
 */
public class Pets911Publisher extends Thread {
    /** Reference to the parent */
    private InternetPublisher parent = null;
    private PublishCriteria publishCriteria = null;
    private final boolean debug = true;

    /** The upload socket if upload is on */
    private FTPClient uploadFTP = null;

    /** Animals born after this date will be excluded */
    private Calendar ageExclusion = null;

    public Pets911Publisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        this.parent = parent;
        this.publishCriteria = publishCriteria;
    }

    /** The main application thread */
    public void run() {
        /*
         * // Before we start, make sure that all species have been // mapped:
         * if (!checkMappedSpecies()) { if (parent != null) { // Re-enable
         * buttons parent.btnClose.setEnabled(true);
         * parent.btnPublish.setEnabled(true); } return; } // and breeds... if
         * (!checkMappedBreeds()) { if (parent != null) { // Re-enable buttons
         * parent.btnClose.setEnabled(true); parent.btnPublish.setEnabled(true); }
         * return; }
         */

        // Get the publish directory and clean it
        String publishDir = makePublishDirectory();
        String userName = "";

        userName = Configuration.getString("Pets911FTPUser");

        /** Make sure we have some settings for Pets911 */
        if (userName.trim().equalsIgnoreCase("")) {
            Global.logError(Global.i18n("uiinternet",
                    "You_need_to_set_your_Pets911_settings_before_publishing"),
                "Pets911Publisher.run");

            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "You_need_to_set_your_Pets911_settings_before_publishing"));
            }

            return;
        }

        // Connect to the remote host
        openUploadSocket();

        /*
         * // Change to the import/photos directory - we are going // to be
         * uploading photos until right at the end of this process try {
         * uploadFTP.chdir("import/photos"); } catch (Exception e) { if (parent !=
         * null) Dialog.showError(Global.i18n("uiinternet",
         * "an_error_occurred_contacting_petfinder.org" + e.getMessage())); else
         * System.out.println(Global.i18n("uiinternet",
         * "an_error_occurred_contacting_petfinder.org" + e.getMessage()));
         * Global.logException(e, getClass()); return; }
         */

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
        // for the x of x debug output
        int noAnimals = 0;

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
                Dialog.showError(Global.i18n("uiinternet",
                        "An_error_occurred_testing_the_animals:\n") +
                    e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

        // Start a new buffer - this is going to be the
        // CSV file required by Pets911.
        StringBuffer dataFile = new StringBuffer();

        // Add the header
        dataFile.append("SOURCE_ID,ANIMAL_ID,AGE_ID,SPECIES,PRIMARY_BREED," +
            "COLOR,GENDER,ALTERED_STATE,SIZE,ADDITIONAL_INFO," +
            "ANIMAL_NAME,LOST_FOUND,ADOPTABLE,IMAGE_FILENAME\n");

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
                            " of " + noAnimals + ")", "Pets911Publisher.run");
                    }

                    // Get the name of the animal's image file. We use the
                    // unique media references to cut upload times in
                    // the future.
                    String animalweb = an.getWebMedia();

                    // Copy the animal image to the publish folder and
                    // rename it as "<ShelterCode>.jpg"
                    if (!animalweb.equals("")) {
                        if (debug) {
                            Global.logInfo("Retrieving image.",
                                "Pets911Publisher.run");
                        }

                        try {
                            DBFS dbfs = Utils.getDBFSDirectoryForLink(0,
                                    an.getID().intValue());

                            try {
                                dbfs.readFile(animalweb,
                                    publishDir + an.getShelterCode() + ".jpg");

                                if (debug) {
                                    Global.logInfo("Retrieved image.",
                                        "Pets911Publisher.run");
                                }

                                // If scaling is on, scale the image
                                if (publishCriteria.scaleImages != 1) {
                                    scaleImage(publishDir +
                                        an.getShelterCode() + ".jpg",
                                        publishCriteria.scaleImages);
                                }

                                // Upload the file
                                if (debug) {
                                    Global.logInfo("Uploading image.",
                                        "Pets911Publisher.run");
                                }

                                upload(an.getShelterCode() + ".jpg");

                                if (debug) {
                                    Global.logInfo("Image uploaded.",
                                        "Pets911Publisher.run");
                                }
                            }
                            // If an IO Error occurs, the file is already in the
                            // publish
                            // directory.
                            catch (Exception e) {
                                // Upload the file
                                if (debug) {
                                    Global.logInfo("Uploading image.",
                                        "Pets911Publisher.run");
                                }

                                upload(an.getShelterCode() + ".jpg");

                                if (debug) {
                                    Global.logInfo("Image uploaded.",
                                        "Pets911Publisher.run");
                                }
                            }

                            dbfs = null;
                        }
                        // Ignore errors retrieving files from the media
                        // server.
                        catch (Exception e) {
                        }
                    } else {
                        if (debug) {
                            Global.logWarning("No image available.",
                                "Pets911Publisher.run");
                        }
                    }

                    // Build the CSV file entry for this animal:

                    // Source ID
                    dataFile.append("\"" +
                        Configuration.getString("Pets911FTPSourceID") + "\",");

                    // Animal ID
                    dataFile.append("\"" + an.getShelterCode() + "\",");

                    // Age ID
                    /*
                     * -- Enum of | Under 6 Months | Young Adult |
                     *    Adult | Mature
                     */

                    // Take the age of the animal in years - obviously
                    // this is going to be a bit out of context as some
                    // animals live far longer than others, but we can
                    // address that later. Probably need to hold age
                    // thresholds for different species somewhere.
                    Calendar today = Calendar.getInstance();
                    Calendar abday = Utils.dateToCalendar(an.getDateOfBirth());
                    double ageInYears = Utils.getDateDiff(today, abday);
                    ageInYears = ((((ageInYears / 60) / 24) / 7) / 52);

                    String ageName = "Unknown";

                    if (ageInYears < 0.5) {
                        ageName = "Under 6 Months";
                    } else if (ageInYears < 2) {
                        ageName = "Young Adult";
                    } else if (ageInYears < 9) {
                        ageName = "Adult";
                    } else {
                        ageName = "Mature";
                    }

                    dataFile.append("\"" + ageName + "\",");

                    // Species
                    dataFile.append("\"" + an.getSpeciesName() + "\",");

                    // Primary_Breed
                    dataFile.append("\"" + an.getBreedName() + "\",");

                    // Color
                    dataFile.append("\"" + an.getBaseColourName() + "\",");

                    // Gender
                    dataFile.append("\"" + an.getSexName() + "\",");

                    // Altered_State (Neutered)
                    dataFile.append("\"" +
                        ((an.getNeutered().intValue() == 1) ? "Yes" : "No") +
                        "\",");

                    // Size
                    // -- Enum of Small, Medium or Large
                    // These can map straight from ASMs default sizes
                    // (pretty lucky there!)
                    String anSize = "Medium";

                    if (an.getSize().intValue() == 0) {
                        anSize = "Large";
                    } else if (an.getSize().intValue() == 1) {
                        anSize = "Large";
                    } else if (an.getSize().intValue() == 2) {
                        anSize = "Medium";
                    } else if (an.getSize().intValue() == 3) {
                        anSize = "Small";
                    }

                    dataFile.append("\"" + anSize + "\",");

                    // Additional_Info
                    String comm = an.getWebMediaNotes();
                    
                    // No web media, use the animal comments instead
                    if (comm.equals("")) comm = an.getAnimalComments();
                    
                    // Strip CR/LF
                    comm = comm.replaceAll(new String(new byte[] { 13, 10 }),
                            "<br/>");
                    comm = comm.replaceAll("\n", "<br/>");
                    comm = comm.replaceAll("\r", "<br/>");
                    comm = comm.replaceAll("\"", "&ldquo;");
                    comm = comm.replaceAll("\'", "&lsquo;");
                    comm = comm.replaceAll("\\`", "&lsquo;");
                    dataFile.append("\"" + comm + "\",");

                    // Animal_Name
                    dataFile.append("\"" + an.getAnimalName() + "\",");

                    // Lost_Found
                    dataFile.append("\"False\",");

                    // Adoptable
                    dataFile.append("\"True\",");

                    // Image_FileName
                    dataFile.append("\"" + an.getShelterCode() + ".jpg" + "\"");

                    // Terminate
                    dataFile.append("\n");

                    if (debug) {
                        Global.logInfo("Finished processing " +
                            an.getShelterCode(), "Pets911Publisher.run");
                    }
                }

                an.moveNext();

                if (parent != null) {
                    Global.mainForm.incrementStatusBar();
                }
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "An_error_occurred_constructing_pages:\n") +
                    e.getMessage());
            }

            Global.logException(e, getClass());
        }

        // Remove animal object
        an.free();
        an = null;

        // Save the data file to our publish directory
        saveFile(publishDir + userName + ".txt", dataFile.toString());

        // Upload the files to the site
        try {
            if (debug) {
                Global.logInfo("Uploading data", "Pets911Publisher.run");
            }

            upload(userName + ".txt");

            if (debug) {
                Global.logInfo("Data uploaded", "Pets911Publisher.run");
            }
        } catch (Exception e) {
            Global.logError(Global.i18n("uiinternet", "An_error_occurred") +
                e.getMessage(), "Pets911Publisher.run");

            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet", "An_error_occurred") +
                    e.getMessage());
            }
        }

        if (parent != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }

        // Disconnect from the remote host
        closeUploadSocket();

        // Tell user it's finished
        Global.logInfo(Global.i18n("uiinternet", "Pets911_publishing_complete"),
            "Pets911Publisher.run");

        if (parent != null) {
            Dialog.showInformation(Global.i18n("uiinternet",
                    "Pets911_publishing_complete"),
                Global.i18n("uiinternet", "Pets911_upload_complete"));
        }

        // Re-enable buttons
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }

        // Clear up
        parent = null;
    }

    /**
     * Verifies that all system species have been mapped to PetFinder types
     * before running
     */

    /*
     * private boolean checkMappedSpecies() { boolean retval = false;
     *
     * try { Species s = new Species(); s.openRecordset("PetFinderSpecies Is
     * Null OR PetFinderSpecies = ''"); retval = s.getEOF(); s.free(); s = null; }
     * catch (Exception e) { Global.logException(e, getClass()); }
     *
     * if (!retval) { if (parent == null) {
     * System.out.println(Global.i18n("uiinternet",
     * "not_all_your_petfinder_types_are_mapped")); System.exit(1); } if
     * (Dialog.showYesNo(Global.i18n("uiinternet",
     * "unmapped_petfinder_species"), Global.i18n("uiinternet",
     * "Unmapped_Types"))) {
     *
     * Global.mainForm.addChild(new PetFinderMapSpecies()); } }
     *
     * return retval; }
     */

    /**
     * Verifies that all system breeds have been mapped to PetFinder breeds
     * before running
     */

    /*
     * private boolean checkMappedBreeds() { boolean retval = false;
     *
     * try { Breed b = new Breed(); b.openRecordset("PetFinderBreed Is Null OR
     * PetFinderBreed = ''"); retval = b.getEOF(); b.free(); b = null; } catch
     * (Exception e) { Global.logException(e, getClass()); } if (!retval) { if
     * (parent == null) { System.out.println(Global.i18n("uiinternet",
     * "not_all_your_petfinder_breeds_are_mapped")); System.exit(1); } if
     * (Dialog.showYesNo(Global.i18n("uiinternet",
     * "unmapped_petfinder_breeds"), Global.i18n("uiinternet",
     * "Unmapped_Types"))) {
     *
     * Global.mainForm.addChild(new PetFinderMapBreed()); } }
     *
     * return retval; }
     */
    private void outputStatusText(String text) {
        if (parent != null) {
            Global.mainForm.setStatusText(text);
        } else {
            Global.logInfo(text, "Pets911Publisher.outputStatusText");
        }
    }

    private String makePublishDirectory() {
        // Make sure we have a publish directory
        // in the temp folder and destroy it's
        // contents.
        File file = new File(Global.tempDirectory + File.separator + "pets911");

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }

        return Global.tempDirectory + File.separator + "pets911" +
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

        return sql.toString();
    }

    /**
     * Returns true if an animal is ok to be included in the publish
     */
    private boolean checkAnimal(Animal a) throws CursorEngineException {
        // Make sure it has a valid picture
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
                Dialog.showError(Global.i18n("uiinternet",
                        "An_error_occurred_writing_a_file:\n") +
                    e.getMessage());
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
            String host = Configuration.getString("Pets911FTPURL");
            String user = Configuration.getString("Pets911FTPUser");
            String password = Configuration.getString("Pets911FTPPassword");

            uploadFTP = new FTPClient(host, 21);
            uploadFTP.login(user, password);
            uploadFTP.setType(FTPTransferType.BINARY);
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "An_error_occurred_connecting_to_the_internet_provider:\n") +
                    e.getMessage());
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
                "pets911" + File.separator;

            // Make sure the local file actually exists - if it doesn't we
            // may as well drop out now and not risk blowing up the FTP
            // connection.
            File localfile = new File(publishDir + filename);

            if (!localfile.exists()) {
                return;
            }

            // If our file has no extension, or is .cfg then upload it over
            // the top
            try {
                if ((filename.indexOf(".") == -1) ||
                        (filename.indexOf(".cfg") != -1)) {
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
