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
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.ftp.FTPClient;
import net.sourceforge.sheltermanager.asm.ftp.FTPException;
import net.sourceforge.sheltermanager.asm.ftp.FTPTransferType;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Calendar;


/**
 * The actual class that does the PetFinder publishing work. Runs in a separate
 * process for progress meter reasons.
 *
 * @author Robin Rawson-Tetley
 * @version 2.0
 */
public class PetFinderPublisher extends Thread {
    /** Reference to the parent */
    private InternetPublisher parent = null;
    private PublishCriteria publishCriteria = null;
    private final boolean debug = true;

    /** The current directory we changed to so we can switch
     *  back to it if an upload fails. */
    private String currentFTPDirectory = "";

    /** The upload socket if upload is on */
    private FTPClient uploadFTP = null;

    /** Animals born after this date will be excluded */
    private Calendar ageExclusion = null;

    public PetFinderPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        this.parent = parent;
        this.publishCriteria = publishCriteria;
    }

    /** The main application thread */
    public void run() {
        // Before we start, make sure that all species have been
        // mapped:
        if (!checkMappedSpecies()) {
            if (parent != null) {
                // Re-enable buttons
                parent.btnClose.setEnabled(true);
                parent.btnPublish.setEnabled(true);
            }

            return;
        }

        // and breeds...
        if (!checkMappedBreeds()) {
            if (parent != null) {
                // Re-enable buttons
                parent.btnClose.setEnabled(true);
                parent.btnPublish.setEnabled(true);
            }

            return;
        }

        // Get the publish directory and clean it
        String publishDir = makePublishDirectory();
        String shelterId = "";

        try {
            shelterId = Configuration.getString("PetFinderFTPUser");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

        // Connect to the remote host
        openUploadSocket();

        // Change to the import/photos directory - we are going
        // to be uploading photos until right at the end of this process
        try {
            uploadFTP.chdir("import/photos");
            currentFTPDirectory = "import/photos";
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

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
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

        // Start a new buffer - this is going to be the
        // CSV file required by PetFinder.
        StringBuffer dataFile = new StringBuffer();

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
                            " of " + noAnimals + ")", "PetFinderPublisher.run");
                    }

                    // Get the name of the animal's image file. We use the
                    // unique media references to cut upload times in
                    // the future.
                    String animalweb = an.getWebMedia();
                    String animalcode = an.getShelterCode();
                    String animalpic = animalcode + "-1.jpg";

                    // Copy the animal image to the publish folder and
                    // rename it as "<ShelterCode>-1.jpg"
                    if (!animalweb.equals("")) {
                        if (debug) {
                            Global.logInfo("Retrieving image.",
                                "PetFinderPublisher.run");
                        }

                        try {
                            DBFS dbfs = Utils.getDBFSDirectoryForLink(0,
                                    an.getID().intValue());

                            try {
                                dbfs.readFile(animalweb, publishDir +
                                    animalpic);

                                if (debug) {
                                    Global.logInfo("Retrieved image.",
                                        "PetFinderPublisher.run");
                                }

                                // If scaling is on, scale the image
                                if (publishCriteria.scaleImages != 1) {
                                    scaleImage(publishDir + animalpic,
                                        publishCriteria.scaleImages);
                                }

                                // Upload the file
                                if (debug) {
                                    Global.logInfo("Uploading image.",
                                        "PetFinderPublisher.run");
                                }

                                upload(animalpic);

                                if (debug) {
                                    Global.logInfo("Image uploaded.",
                                        "PetFinderPublisher.run");
                                }
                            }
                            // If an IO Error occurs, the file is already in the
                            // publish
                            // directory.
                            catch (Exception e) {
                                // Upload the file
                                if (debug) {
                                    Global.logInfo("Uploading image.",
                                        "PetFinderPublisher.run");
                                }

                                upload(animalpic);

                                if (debug) {
                                    Global.logInfo("Image uploaded.",
                                        "PetFinderPublisher.run");
                                }
                            }

                            // If the upload all option is set, grab all of
                            // the images this animal has and save/upload them too
                            if (publishCriteria.uploadAllImages) {
                                int idx = 1;
                                String[] images = dbfs.list();

                                if (debug) {
                                    Global.logInfo("Animal has " +
                                        images.length + " media files",
                                        "PetFinderPublisher.run");
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
                                                ")", "PetFinderPublisher.run");
                                        }

                                        dbfs.readFile(images[i],
                                            publishDir + otherpic);

                                        // If scaling is on, scale the image
                                        if (publishCriteria.scaleImages != 1) {
                                            scaleImage(publishDir + otherpic,
                                                publishCriteria.scaleImages);
                                        }

                                        if (debug) {
                                            Global.logInfo(
                                                "Uploading additional image: " +
                                                otherpic + " (" + images[i] +
                                                ")", "PetFinderPublisher.run");
                                        }

                                        upload(otherpic);
                                    }
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
                            Global.logInfo("No image available.",
                                "PetFinderPublisher.run");
                        }
                    }

                    // Build the CSV file entry for this animal:

                    // Animal
                    /*
                     * Enumeration of following: | BarnYard | | Bird | | Cat | |
                     * Dog | | Horse | | Pig | | Rabbit | | Reptile | |
                     * Small&Furry |
                     */

                    // Look up appropriate mapping from species table
                    String pfMap = LookupCache.getSpeciesPetFinderMapping(an.getSpeciesID());
                    dataFile.append("\"" + pfMap + "\",");

                    // Breed
                    String pfBMap = LookupCache.getBreedPetFinderMapping(an.getBreedID());
                    dataFile.append("\"" + pfBMap + "\",");

                    // Age
                    /*
                     * -- Enum of | Adult | | Baby | | Senior | | Young |
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

                    String ageName = "Adult";

                    if (ageInYears < 0.5) {
                        ageName = "Baby";
                    } else if (ageInYears < 2) {
                        ageName = "Young";
                    } else if (ageInYears < 9) {
                        ageName = "Adult";
                    } else {
                        ageName = "Senior";
                    }

                    dataFile.append("\"" + ageName + "\",");

                    // Name
                    dataFile.append("\"" + an.getAnimalName() + "\",");

                    // Size
                    // -- Enum of S M L XL
                    // These can map straight from ASMs default sizes
                    // (pretty lucky there!)
                    String anSize = "M";

                    if (an.getSize().intValue() == 0) {
                        anSize = "XL";
                    } else if (an.getSize().intValue() == 1) {
                        anSize = "L";
                    } else if (an.getSize().intValue() == 2) {
                        anSize = "M";
                    } else if (an.getSize().intValue() == 3) {
                        anSize = "S";
                    }

                    dataFile.append("\"" + anSize + "\",");

                    // Sex
                    // -- Can only be M/F
                    String sexname = an.getSexName();

                    if ((sexname == null) || sexname.trim().equals("")) {
                        sexname = "M";
                    } else {
                        sexname = sexname.substring(0, 1);
                    }

                    dataFile.append("\"" + sexname + "\",");

                    // Description
                    String comm = an.getWebMediaNotes();

                    // No web media, use the animal comments instead
                    if (comm.equals("")) {
                        comm = an.getAnimalComments();
                    }

                    // Strip CR/LF
                    comm = comm.replaceAll(new String(new byte[] { 13, 10 }),
                            "<br/>");
                    comm = comm.replaceAll("\n", "<br/>");
                    comm = comm.replaceAll("\r", "<br/>");
                    comm = comm.replaceAll("\"", "&ldquo;");
                    comm = comm.replaceAll("\'", "&lsquo;");
                    comm = comm.replaceAll("\\`", "&lsquo;");
                    dataFile.append("\"" + comm + "\",");
                    comm = null;

                    // Special Needs
                    if (an.getCrueltyCase().intValue() == 1) {
                        dataFile.append("\"1\",");
                    } else if (an.isHasSpecialNeeds().intValue() == 1) {
                        dataFile.append("\"1\",");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // Has Shots
                    AnimalVaccination av = new AnimalVaccination();
                    av.openRecordset("AnimalID = " + an.getID() +
                        " AND DateOfVaccination Is Not Null");

                    if (!av.getEOF()) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    av.free();
                    av = null;

                    // Altered
                    if (an.getNeutered().intValue() == 1) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // No Dogs
                    if (an.isGoodWithDogs().intValue() == 1) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // No Cats
                    if (an.isGoodWithCats().intValue() == 1) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // No Kids
                    if (an.isGoodWithKids().intValue() == 1) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // No Claws
                    if (an.getDeclawed().intValue() == 1) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // Housebroken
                    if (an.isHouseTrained().intValue() == 0) {
                        dataFile.append("1,");
                    } else {
                        dataFile.append("\"\",");
                    }

                    // Id
                    dataFile.append("\"" + an.getShelterCode() + "\"");

                    // Terminate
                    dataFile.append("\n");

                    if (debug) {
                        Global.logInfo("Finished processing " +
                            an.getShelterCode(), "PetFinderPublisher.run");
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

        // Generate the map file for PetFinder to transform the data
        String petFinderMap = "; PetFinder import map. This file was autogenerated by\n" +
            "; Animal Shelter Manager. http://sheltermanager.sf.net\n" +
            "; The FREE, open source solution for animal sanctuaries and rescue shelters.\n\n" +
            "#SHELTERID:" + shelterId + "\n" + "#0:Animal=Animal\n" +
            "#1:Breed=Breed\n" + "#2:Age=Age\n" + "#3:Name=Name\n" +
            "#4:Size=Size\n" + "#5:Sex=Sex\n" + "Female=F\n" + "Male=M\n" +
            "#6:Description=Dsc\n" + "#7:SpecialNeeds=SpecialNeeds\n" +
            "#8:HasShots=HasShots\n" + "#9:Altered=Altered\n" +
            "#10:NoDogs=NoDogs\n" + "#11:NoCats=NoCats\n" +
            "#12:NoKids=NoKids\n" + "#13:Declawed=Declawed\n" +
            "#14:HouseBroken=HouseBroken\n" + "#15:Id=Id\n" +
            "#ALLOWUPDATE:Y\n" + "#HEADER:N";
        saveFile(publishDir + shelterId + "import.cfg", petFinderMap);

        // Save the data file to our publish directory
        saveFile(publishDir + shelterId, dataFile.toString());

        // Upload the files to the site
        try {
            uploadFTP.chdir("..");
            currentFTPDirectory = "import";

            if (debug) {
                Global.logInfo("Uploading data", "PetFinderPublisher.run");
            }

            upload(shelterId);

            if (debug) {
                Global.logInfo("Data uploaded", "PetFinderPublisher.run");
            }

            if (debug) {
                Global.logInfo("Uploading data map", "PetFinderPublisher.run");
            }

            upload(shelterId + "import.cfg");

            if (debug) {
                Global.logInfo("Data map uploaded.", "PetFinderPublisher.run");
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }

        if (parent != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }

        // Disconnect from the remote host
        closeUploadSocket();

        // Tell user it's finished
        if (parent != null) {
            Dialog.showInformation(Global.i18n("uiinternet",
                    "petfinder_publishing_complete"),
                Global.i18n("uiinternet", "petfinder_upload_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet",
                    "petfinder_publishing_complete"), "PetFinderPublisher.run");
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

    /**
     * Verifies that all system species have been mapped to PetFinder types
     * before running
     */
    private boolean checkMappedSpecies() {
        boolean retval = false;

        try {
            SQLRecordset s = new SQLRecordset();
            s.openRecordset("PetFinderSpecies Is Null OR PetFinderSpecies = ''",
                "species");
            retval = s.getEOF();
            s.free();
            s = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        if (!retval) {
            if (parent == null) {
                Global.logError(Global.i18n("uiinternet",
                        "not_all_your_petfinder_types_are_mapped"),
                    "PetFinderPublisher.checkMappedSpecies");
                System.exit(1);
            }

            if (Dialog.showYesNo(Global.i18n("uiinternet",
                            "unmapped_petfinder_species"),
                        Global.i18n("uiinternet", "Unmapped_Types"))) {
                Global.mainForm.addChild(new PetFinderMapSpecies());
            }
        }

        return retval;
    }

    /**
     * Verifies that all system breeds have been mapped to PetFinder breeds
     * before running
     */
    private boolean checkMappedBreeds() {
        boolean retval = false;

        try {
            SQLRecordset b = new SQLRecordset();
            b.openRecordset("PetFinderBreed Is Null OR PetFinderBreed = ''",
                "breed");
            retval = b.getEOF();
            b.free();
            b = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        if (!retval) {
            if (parent == null) {
                Global.logError(Global.i18n("uiinternet",
                        "not_all_your_petfinder_breeds_are_mapped"),
                    "PetFinderPublisher.checkMappedBreeds");
                System.exit(1);
            }

            if (Dialog.showYesNo(Global.i18n("uiinternet",
                            "unmapped_petfinder_breeds"),
                        Global.i18n("uiinternet", "Unmapped_Types"))) {
                Global.mainForm.addChild(new PetFinderMapBreed());
            }
        }

        return retval;
    }

    private void outputStatusText(String text) {
        if (parent != null) {
            Global.mainForm.setStatusText(text);
        } else {
            Global.logInfo(text, "PetFinderPublisher");
        }
    }

    private String makePublishDirectory() {
        // Make sure we have a publish directory
        // in the temp folder and destroy it's
        // contents.
        File file = new File(Global.tempDirectory + File.separator +
                "petfinder");

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }

        return Global.tempDirectory + File.separator + "petfinder" +
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
            String host = Configuration.getString("PetFinderFTPURL");
            String user = Configuration.getString("PetFinderFTPUser");
            String password = Configuration.getString("PetFinderFTPPassword");

            uploadFTP = new FTPClient(host, 21);
            uploadFTP.login(user, password);

            // If we had a directory set, change back to it
            if (!currentFTPDirectory.equals("")) {
                uploadFTP.chdir(currentFTPDirectory);
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
        }
    }

    /**
     * Verifies that the socket is still active by
     * requesting a directory. If it cannot get one, the socket is reopened.
     */
    private void checkUploadSocket() {
        // Verify that the upload socket is still live by requesting
        // a directory for the current file - we are looking for an
        // error - who cares what comes back (mental note - allow
        // access to the FTP socket publically).
        try {
            if (uploadFTP == null) {
                closeUploadSocket();
                openUploadSocket();
            }

            setBinary();

            String ignoreResponse = uploadFTP.list("*");
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
     * (and it is not a .cfg file or has no extension), it is not uploaded again.
     */
    private void upload(String filename) {
        try {
            String publishDir = Global.tempDirectory + File.separator +
                "petfinder" + File.separator;

            // Make sure the local file actually exists - if it doesn't we
            // may as well drop out now and not risk blowing up the FTP
            // connection.
            File localfile = new File(publishDir + filename);

            if (!localfile.exists()) {
                return;
            }

            // Is the socket itself valid?
            checkUploadSocket();

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
            setBinary();

            // Upload the file
            try {
                uploadFTP.put(publishDir + filename, filename);
            } catch (Exception e) {
                Global.logException(e, PetFinderPublisher.class);
                Global.logInfo("Failed upload - forcing reconnect and retry...",
                    "PetFinderPublisher.upload");

                try {
                    closeUploadSocket();
                    openUploadSocket();
                    setBinary();
                    uploadFTP.put(publishDir + filename, filename);
                } catch (Exception ex) {
                    Global.logException(e, PetFinderPublisher.class);
                    Global.logError(
                        "Failed to upload after reconnect, skipping file " +
                        filename + " and reconnecting...",
                        "PetFinderPublisher.upload");
                    closeUploadSocket();
                    openUploadSocket();

                    return;
                }
            }
        }
        // Ignore errors - if a file already exists then don't try to
        // upload it again.
        catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void setBinary() {
        try {
            uploadFTP.setType(FTPTransferType.BINARY);
        } catch (Exception e) {
            Global.logException(e, PetFinderPublisher.class);
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
