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
import net.sourceforge.sheltermanager.asm.bo.Breed;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Species;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Calendar;


/**
 * The actual class that does the RescueGroups.org publishing work
 *
 * @author Robin Rawson-Tetley
 */
public class RescueGroupsPublisher extends Thread {
    /** Reference to the parent */
    private InternetPublisher parent = null;
    private PublishCriteria publishCriteria = null;
    private final boolean debug = true;

    /** The upload socket if upload is on */
    private FTPClient uploadFTP = null;

    /** Animals born after this date will be excluded */
    private Calendar ageExclusion = null;

    public RescueGroupsPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        this.parent = parent;
        this.publishCriteria = publishCriteria;
    }

    /** The main application thread */
    public void run() {
        // Before we start, make sure that all species have been
        // mapped - we reuse the PetFinder mappings for RescueGroups
        // so they know the limits of what they can expect from
        // us.
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
            shelterId = Configuration.getString("RescueGroupsFTPUser");

            if ((shelterId == null) || shelterId.trim().equals("")) {
                Global.logError(Global.i18n("uiinternet",
                        "You_need_to_set_your_RescueGroups_settings_before_publishing"),
                    "RescueGroupsPublisher.run");

                if (parent != null) {
                    Dialog.showError(Global.i18n("uiinternet",
                            "You_need_to_set_your_RescueGroups_settings_before_publishing"));
                    // Re-enable parent buttons
                    parent.btnClose.setEnabled(true);
                    parent.btnPublish.setEnabled(true);
                }

                return;
            }
        } catch (Exception e) {
            Global.logError(Global.i18n("uiinternet",
                    "An_error_occurred_reading_the_rescue_groups_user") +
                e.getMessage(), "RescueGroupsPublisher.run");

            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "An_error_occurred_reading_the_rescue_groups_user") +
                    e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

        // Connect to the remote host and switch to the correct directory
        openUploadSocket();

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
        // CSV file required by 1-800-Save-A-Pet.
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
                            " of " + noAnimals + ")",
                            "RescueGroupsPublisher.run");
                    }

                    // Get the name of the animal's image file. We use the
                    // unique media references to cut upload times in
                    // the future.
                    String animalweb = an.getWebMedia();
                    String animalcode = an.getShelterCode();
                    String animalpic = animalcode + "-1.jpg";
                    int totalimages = 0;

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
                                totalimages++;

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

                                for (int i = 0; i < images.length && totalimages <= 4; i++) {
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
                                        totalimages++;
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

                    // Get Petfinder mapped species and breeds
                    String pfSpecies = LookupCache.getSpeciesPetFinderMapping(an.getSpeciesID());
                    String pfBreed1 = LookupCache.getBreedPetFinderMapping(an.getBreedID());
                    String pfBreed2 = LookupCache.getBreedPetFinderMapping(an.getBreed2ID());

                    // Build the CSV file entry for this animal

                    // orgID
                    dataFile.append(shelterId + ", ");

                    // animalID
                    dataFile.append(an.getID().toString() + ", ");

                    // status
                    dataFile.append("\"Available\", ");

                    // lastUpdated (Unix timestamp field)
                    dataFile.append(an.getLastChangedDate().getTime() + ", ");

                    // rescueID (org name)
                    dataFile.append("\"" +
                        Configuration.getString("Organisation") + "\", ");

                    // name
                    dataFile.append("\"" + an.getAnimalName() + "\", ");

                    // summary TODO:
                    dataFile.append("\"\", ");

                    // species
                    // dataFile.append("\"" + an.getSpeciesName() + "\", ");
                    dataFile.append("\"" + pfSpecies + "\", ");

                    // breed
                    dataFile.append("\"" + an.getBreedName() + "\", ");

                    // primary breed
                    // dataFile.append("\"" + LookupCache.getBreedName(an.getBreedID()) + "\", ");
                    dataFile.append("\"" + pfBreed1 + "\", ");

                    // secondary breed
                    // dataFile.append("\"" + LookupCache.getBreedName(an.getBreed2ID()) + "\", ");
                    dataFile.append("\"" + pfBreed2 + "\", ");

                    // sex
                    dataFile.append("\"" +
                        LookupCache.getSexNameForID(an.getSex()) + "\", ");

                    // mixed
                    dataFile.append("\"" +
                        ((an.getCrossBreed().intValue() == 1) ? "No" : "Yes") +
                        "\", ");

                    // dogs (good with)
                    dataFile.append((an.isGoodWithDogs().intValue() != 1)
                        ? "\"Yes\", " : "\"No\", ");

                    // cats
                    dataFile.append((an.isGoodWithCats().intValue() != 1)
                        ? "\"Yes\", " : "\"No\", ");

                    // kids
                    dataFile.append((an.isGoodWithKids().intValue() != 1)
                        ? "\"Yes\", " : "\"No\", ");

                    // declawed
                    dataFile.append((an.getDeclawed().intValue() == 1)
                        ? "\"Yes\", " : "\"No\", ");

                    // housetrained
                    dataFile.append((an.getDeclawed().intValue() != 1)
                        ? "\"Yes\", " : "\"No\", ");

                    // age
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

                    dataFile.append("\"" + ageName + "\", ");

                    // specialNeeds
                    if (an.getCrueltyCase().intValue() == 1) {
                        dataFile.append("\"Yes\", ");
                    } else if (an.isHasSpecialNeeds().intValue() == 1) {
                        dataFile.append("\"Yes\", ");
                    } else {
                        dataFile.append("\"No\", ");
                    }

                    // altered
                    dataFile.append((an.getNeutered().intValue() == 1)
                        ? "\"Yes\", " : "\"No\", ");

                    // size
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

                    dataFile.append("\"" + anSize + "\", ");

                    // uptodate (refers to shots)
                    Configuration conf = null;
                    AnimalVaccination av = new AnimalVaccination();
                    av.openRecordset("AnimalID = " + an.getID() +
                        " AND DateOfVaccination Is Not Null");
                    dataFile.append((!av.getEOF()) ? "\"Yes\", " : "\"No\", ");
                    av.free();
                    av = null;

                    // color
                    dataFile.append("\"" + an.getBaseColourName() + "\", ");

                    // coatLength TODO:
                    dataFile.append("\"\", ");

                    // pattern TODO:
                    dataFile.append("\"\", ");

                    // courtesy (what is this?)
                    dataFile.append("\"Yes\", ");

                    // description
                    String comm = an.getWebMediaNotes();
                    // Strip CR/LF
                    comm = Utils.replace(comm, new String(new byte[] { 13 }), "");
                    comm = Utils.replace(comm, new String(new byte[] { 10 }), "");
                    // Switch quotes
                    comm = comm.replace('"', '\'');
                    dataFile.append("\"" + comm + "\"");
                    comm = null;

                    // pic1 - pic4 - if we don't have a picture for it, just leave the
                    // field blank instead
                    for (int i = 1; i <= 4; i++) {
                        dataFile.append(", ");
                        if (totalimages >= i)
                            dataFile.append("\"" + an.getShelterCode() + "-" + i + ".jpg" + "\"");
                        else
                            dataFile.append("\"\"");
                    }


                    // Terminate
                    dataFile.append("\n");

                    if (debug) {
                        Global.logInfo("Finished processing " +
                            an.getShelterCode(), "RescueGroupsPublisher.run");
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
        String csvhead = "orgID, animalID, status, lastUpdated, rescueID, name, summary, species, breed, " +
            "primaryBreed, secondaryBreed, sex, mixed, dogs, cats, kids, declawed, housetrained, age, " +
            "specialNeeds, altered, size, uptodate, color, coatLength, pattern, courtesy, description, pic1, " +
            "pic2, pic3, pic4\n";

        saveFile(publishDir + "pets.csv", csvhead + dataFile.toString());

        // Upload the files to the site
        try {
            uploadFTP.chdir("..");

            if (debug) {
                Global.logInfo("Uploading data", "RescueGroupsPublisher.run");
            }

            upload("pets.csv");

            if (debug) {
                Global.logInfo("Data uploaded", "RescueGroupsPublisher.run");
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "an_error_occurred_contacting_rescue_groups") +
                    e.getMessage());
            } else {
                Global.logError(Global.i18n("uiinternet",
                        "an_error_occurred_contacting_rescue_groups") +
                    e.getMessage(), "RescueGroupsPublisher.run");
            }
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
                    "rescue_groups_publishing_complete"),
                Global.i18n("uiinternet", "rescue_groups_upload_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet",
                    "rescue_groups_publishing_complete"),
                "RescueGroupsPublisher.run");
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

    private void outputStatusText(String text) {
        if (parent != null) {
            Global.mainForm.setStatusText(text);
        } else {
            Global.logInfo(text, "RescueGroupsPublisher");
        }
    }

    private String makePublishDirectory() {
        // Make sure we have a publish directory
        // in the temp folder and destroy it's
        // contents.
        File file = new File(Global.tempDirectory + File.separator +
                "rescuegroups");

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }

        return Global.tempDirectory + File.separator + "rescuegroups" +
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
            out.write(content.getBytes("UTF-8"));
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
     * Verifies that all system species have been mapped to PetFinder types
     * before running
     */
    private boolean checkMappedSpecies() {
        boolean retval = false;

        try {
            Species s = new Species();
            s.openRecordset("PetFinderSpecies Is Null OR PetFinderSpecies = ''");
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
            Breed b = new Breed();
            b.openRecordset("PetFinderBreed Is Null OR PetFinderBreed = ''");
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

    /**
     * Reads the configuration settings and opens a connection to the users
     * remote internet FTP server
     */
    private void openUploadSocket() {
        try {
            String host = Configuration.getString("RescueGroupsFTPURL");
            String user = Configuration.getString("RescueGroupsFTPUser");
            String password = Configuration.getString("RescueGroupsFTPPassword");

            uploadFTP = new FTPClient(host, 21);
            uploadFTP.login(user, password);
            uploadFTP.setType(FTPTransferType.BINARY);

            // Attempt to create the import/pictures directory and fail silently
            // if it already exists.
            try {
                uploadFTP.mkdir("import");
                uploadFTP.chdir("import");
                uploadFTP.mkdir("pictures");
                uploadFTP.chdir("pictures");
            } catch (Exception e) {
            }

            try {
                uploadFTP.chdir("import/pictures");
            } catch (Exception e) {
            }
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
                "rescuegroups" + File.separator;

            // Make sure the local file actually exists - if it doesn't we
            // may as well drop out now and not risk blowing up the FTP
            // connection.
            File localfile = new File(publishDir + filename);

            if (!localfile.exists()) {
                return;
            }

            // If our file has no extension, or is .cfg or .csv then upload it 
            // over the top regardless of if a file exists
            try {
                if ((filename.indexOf(".") == -1) ||
                        (filename.indexOf(".cfg") != -1) ||
                        (filename.indexOf(".csv") != -1)) {
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
