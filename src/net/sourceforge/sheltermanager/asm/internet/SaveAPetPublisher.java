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
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Calendar;
import java.util.Date;


/**
 * The actual class that does the 1-800-Save-A-Pet publishing work
 * (now known as AdoptAPet.com)
 * process for progress meter reasons.
 *
 * @author Robin Rawson-Tetley
 * @version 2.0
 */
public class SaveAPetPublisher extends Thread {
    /** Reference to the parent */
    private InternetPublisher parent = null;
    private PublishCriteria publishCriteria = null;
    private final boolean debug = true;

    /** The upload socket if upload is on */
    private FTPClient uploadFTP = null;

    /** Animals born after this date will be excluded */
    private Calendar ageExclusion = null;

    public SaveAPetPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        this.parent = parent;
        this.publishCriteria = publishCriteria;
    }

    /** The main application thread */
    public void run() {
        // Get the publish directory and clean it
        String publishDir = makePublishDirectory();
        String shelterId = "";

        try {
            shelterId = Configuration.getString("SaveAPetFTPUser");
            ;
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }

        // Make sure we have some settings for AdoptAPet
        if (shelterId.trim().equalsIgnoreCase("")) {
            Global.logError(Global.i18n("uiinternet",
                    "You_need_to_set_your_save_a_pet_settings_before_publishing"),
                "SaveAPetPublisher.run");

            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "You_need_to_set_your_save_a_pet_settings_before_publishing"));
            }

            return;
        }

        // Connect to the remote host
        openUploadSocket();

        // Change to the photos directory - we are going
        // to be uploading photos until right at the end of this process
        try {
            uploadFTP.chdir("photos");
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
                            " of " + noAnimals + ")", "SaveAPetPublisher.run");
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
                                "SaveAPetPublisher.run");
                        }

                        try {
                            DBFS dbfs = Utils.getDBFSDirectoryForLink(0,
                                    an.getID().intValue());

                            try {
                                dbfs.readFile(animalweb,
                                    publishDir + an.getShelterCode() + ".jpg");

                                if (debug) {
                                    Global.logInfo("Retrieved image.",
                                        "SaveAPetPublisher.run");
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
                                        "SaveAPetPublisher.run");
                                }

                                upload(an.getShelterCode() + ".jpg");

                                if (debug) {
                                    Global.logInfo("Image uploaded.",
                                        "SaveAPetPublisher.run");
                                }
                            }
                            // If an IO Error occurs, the file is already in the
                            // publish
                            // directory.
                            catch (Exception e) {
                                // Upload the file
                                if (debug) {
                                    Global.logInfo("Uploading image.",
                                        "SaveAPetPublisher.run");
                                }

                                upload(an.getShelterCode() + ".jpg");

                                if (debug) {
                                    Global.logInfo("Image uploaded.",
                                        "SaveAPetPublisher.run");
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
                                "SaveAPetPublisher.run");
                        }
                    }

                    // Build the CSV file entry for this animal:

                    // - Use the petfinder mapping since it's already set
                    // as standard to find out whether it's a dog or cat.
                    String pfMap = LookupCache.getSpeciesPetFinderMapping(an.getSpeciesID());

                    // If it isn't a dog or cat, don't bother with this record
                    // as 1-800-Save-A-Pet doesn't do anything else and skip
                    // to the next one.
                    if (!pfMap.equalsIgnoreCase("Dog") &&
                            !pfMap.equalsIgnoreCase("Cat")) {
                        an.moveNext();

                        continue;
                    }

                    // Id
                    dataFile.append("\"" + an.getShelterCode() + "\",");

                    // Animal
                    dataFile.append("\"" + pfMap + "\",");

                    // Breed
                    String pfBMap = LookupCache.getBreedPetFinderMapping(an.getBreedID());
                    dataFile.append("\"" + pfBMap + "\",");

                    // Breed2
                    String pfBMap2 = LookupCache.getBreedPetFinderMapping(an.getBreed2ID());
                    dataFile.append("\"" + pfBMap2 + "\",");

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
                    dataFile.append("\"" + an.getSexName().substring(0, 1) +
                        "\",");

                    // Colour
                    // -- Relies on map created by the shelter
                    if (publishCriteria.includeColours) {
                        dataFile.append("\"" + an.getBaseColourName() + "\",");
                    }

                    // Description
                    String comm = an.getWebMediaNotes();

                    // No web media, use the animal comments instead
                    if (comm.equals("")) {
                        comm = an.getAnimalComments();
                    }

                    // Strip CR/LF
                    comm = Utils.replace(comm, new String(new byte[] { 13 }), "");
                    comm = Utils.replace(comm, new String(new byte[] { 10 }), "");

                    // Switch quotes
                    comm = comm.replace('"', '\'');
                    dataFile.append("\"" + comm + "\",");
                    comm = null;

                    // Status
                    // One of Available | Adopted | Deleted
                    // Since we only deal in adoptable animals, everything is available
                    dataFile.append("\"Available\",");

                    // Purebred = No longer used as a bit of
                    // a fudge. It was position #10 previously
                    /*
                    if ((pfMap.toLowerCase().indexOf("cross") != -1) ||
                            (pfMap.toLowerCase().indexOf("mixed") != -1)) {
                        dataFile.append("\"0\",");
                    } else {
                        dataFile.append("\"1\",");
                    }
                    */

                    // Good with kids
                    dataFile.append((an.isGoodWithKids().intValue() != 1)
                        ? "\"1\"," : "\"0\",");

                    // Good with cats
                    dataFile.append((an.isGoodWithCats().intValue() != 1)
                        ? "\"1\"," : "\"0\",");

                    // Good with dogs
                    dataFile.append((an.isGoodWithDogs().intValue() != 1)
                        ? "\"1\"," : "\"0\",");

                    // Spayed/Neutered
                    dataFile.append((an.getNeutered().intValue() == 1)
                        ? "\"1\"," : "\"0\",");

                    // Shots current
                    Configuration conf = null;
                    AnimalVaccination av = new AnimalVaccination();
                    av.openRecordset("AnimalID = " + an.getID() +
                        " AND DateOfVaccination Is Not Null");
                    dataFile.append((!av.getEOF()) ? "\"1\"," : "\"0\",");
                    av.free();
                    av = null;

                    // Housetrained
                    dataFile.append((an.isHouseTrained().intValue() == 0)
                        ? "\"1\"," : "\"0\",");

                    // Declawed
                    dataFile.append((an.getDeclawed().intValue() == 1)
                        ? "\"1\"," : "\"0\",");

                    // Special needs
                    if (an.getCrueltyCase().intValue() == 1) {
                        dataFile.append("\"Y\"");
                    } else if (an.isHasSpecialNeeds().intValue() == 1) {
                        dataFile.append("\"Y\"");
                    } else {
                        dataFile.append("\"N\"");
                    }

                    // Terminate
                    dataFile.append("\n");

                    // Mark media records for this animal as published
                    if (debug) {
                        Global.logInfo(
                            "Marking media records published for animal " +
                            an.getID(), "SaveAPetPublisher");
                    }

                    DBConnection.executeAction(
                        "UPDATE media SET LastPublishedAP = '" +
                        Utils.getSQLDate(new Date()) + "' WHERE LinkID = " +
                        an.getID() + " AND LinkTypeID = 0");

                    if (debug) {
                        Global.logInfo("Finished processing " +
                            an.getShelterCode(), "SaveAPetPublisher.run");
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

        // Generate the map file for 1-800-Save-A-Pet to transform necessary data
        String cfg = getMappings(publishCriteria.includeColours);

        saveFile(publishDir + "import.cfg", cfg);

        // Save the data file to our publish directory
        saveFile(publishDir + "pets.csv", dataFile.toString());

        // Upload the files to the site
        try {
            uploadFTP.chdir("..");

            if (debug) {
                Global.logInfo("Uploading data", "SaveAPetPublisher.run");
            }

            upload("pets.csv");

            if (debug) {
                Global.logInfo("Data uploaded", "SaveAPetPublisher.run");
            }

            if (debug) {
                Global.logInfo("Uploading data map", "SaveAPetPublisher.run");
            }

            upload("import.cfg");

            if (debug) {
                Global.logInfo("Data map uploaded.", "SaveAPetPublisher.run");
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
                    "saveapet_publishing_complete"),
                Global.i18n("uiinternet", "saveapet_upload_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet",
                    "saveapet_publishing_complete"), "SaveAPetPublisher.run");
        }

        // Re-enable buttons
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }

        // Clear up
        parent = null;
    }

    private void outputStatusText(String text) {
        if (parent != null) {
            Global.mainForm.setStatusText(text);
        } else {
            Global.logInfo(text, "SaveAPetPublisher");
        }
    }

    private String makePublishDirectory() {
        // Make sure we have a publish directory
        // in the temp folder and destroy it's
        // contents.
        File file = new File(Global.tempDirectory + File.separator +
                "1800saveapet");

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }

        return Global.tempDirectory + File.separator + "1800saveapet" +
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
            String host = Configuration.getString("SaveAPetFTPURL");
            String user = Configuration.getString("SaveAPetFTPUser");
            String password = Configuration.getString("SaveAPetFTPPassword");

            uploadFTP = new FTPClient(host, 21);
            uploadFTP.login(user, password);
            uploadFTP.setType(FTPTransferType.BINARY);

            // Attempt to create the photos directory and fail silently
            // if it already exists.
            try {
                uploadFTP.mkdir("photos");
            } catch (Exception e) {
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
                "1800saveapet" + File.separator;

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

    /** Returns the contents of the mappings file (import.cfg) for
     *  1-800-Save-A-Pet
     * @return
     */
    public String getMappings(boolean includeColours) {
        String defmap = "; AdoptAPet.com import map. This file was autogenerated by\n" +
            "; Animal Shelter Manager. http://sheltermanager.sf.net\n" +
            "; The FREE, open source solution for animal sanctuaries and rescue shelters.\n\n" +
            "#1:Id=Id\n" + "#2:Animal=Animal\n" + "#3:Breed=Breed\n" +
            "Appenzell Mountain Dog=Shepherd (Unknown Type)\n" +
            "Australian Cattle Dog/Blue Heeler=Australian Cattle Dog\n" +
            "Belgian Shepherd Dog Sheepdog=Belgian Shepherd\n" +
            "Belgian Shepherd Tervuren=Belgian Tervuren\n" +
            "Belgian Shepherd Malinois=Belgian Malinois\n" +
            "Black Labrador Retriever=Labrador Retriever\n" +
            "Brittany Spaniel=Brittany\n" + "Cane Corso Mastiff=Cane Corso\n" +
            "Chinese Crested Dog=Chinese Crested\n" +
            "Chinese Foo Dog=Shepherd (Unknown Type)\n" +
            "Dandi Dinmont Terrier=Dandie Dinmont Terrier\n" +
            "English Cocker Spaniel=Cocker Spaniel\n" +
            "English Coonhound=English (Redtick) Coonhound\n" +
            "Flat-coated Retriever=Flat-Coated Retriever\n" +
            "Fox Terrier=Fox Terrier (Smooth)\n" +
            "Hound=Hound (Unknown Type)\n" +
            "Illyrian Sheepdog=Shepherd (Unknown Type)\n" +
            "McNab =Shepherd (Unknown Type)\n" +
            "New Guinea Singing Dog=Shepherd (Unknown Type)\n" +
            "Newfoundland Dog=Newfoundland\n" +
            "Norweigan Lundehund=Shepherd (Unknown Type)\n" +
            "Peruvian Inca Orchid=Shepherd (Unknown Type)\n" +
            "Poodle=Poodle (Standard)\n" +
            "Retriever=Retriever (Unknown Type)\n" +
            "Saint Bernard St. Bernard=St. Bernard\n" +
            "Schipperkev=Schipperke\n" + "Schnauzer=Schnauzer (Standard)\n" +
            "Scottish Terrier Scottie=Scottie, Scottish Terrier\n" +
            "Setter=Setter (Unknown Type)\n" +
            "Sheep Dog=Old English Sheepdog\n" +
            "Shepherd=Shepherd (Unknown Type)\n" +
            "Shetland Sheepdog Sheltie=Sheltie, Shetland Sheepdog\n" +
            "Spaniel=Spaniel (Unknown Type)\n" +
            "Spitz=Spitz (Unknown Type, Medium)\n" +
            "South Russian Ovcharka=Shepherd (Unknown Type)\n" +
            "Terrier=Terrier (Unknown Type, Small)\n" +
            "West Highland White Terrier Westie=Westie, West Highland White Terrier\n" +
            "White German Shepherd=German Shepherd Dog\n" +
            "Wire-haired Pointing Griffon=Wirehaired Pointing Griffon\n" +
            "Wirehaired Terrier=Terrier (Unknown Type, Medium)\n" +
            "Yellow Labrador Retriever=Labrador Retriever\n" +
            "Yorkshire Terrier Yorkie=Yorkie, Yorkshire Terrier\n" +
            "American Siamese=Siamese\n" + "Bobtail=American Bobtail\n" +
            "Burmilla=Burmese\n" + "Canadian Hairless=Sphynx\n" +
            "Dilute Calico=Calico\n" +
            "Dilute Tortoiseshell=Domestic Shorthair\n" +
            "Domestic Long Hair=Domestic Longhair\n" +
            "Domestic Long Hair-black=Domestic Longhair\n" +
            "Domestic Long Hair - buff=Domestic Longhair\n" +
            "Domestic Long Hair-gray=Domestic Longhair\n" +
            "Domestic Long Hair - orange=Domestic Longhair\n" +
            "Domestic Long Hair - orange and white=Domestic Longhair\n" +
            "Domestic Long Hair - gray and white=Domestic Longhair\n" +
            "Domestic Long Hair-white=Domestic Longhair\n" +
            "Domestic Long Hair-black and white=Domestic Longhair\n" +
            "Domestic Medium Hair=Domestic Mediumhair\n" +
            "Domestic Medium Hair - buff=Domestic Mediumhair\n" +
            "Domestic Medium Hair - gray and white=Domestic Mediumhair\n" +
            "Domestic Medium Hair-white=Domestic Mediumhair\n" +
            "Domestic Medium Hair-orange=Domestic Mediumhair\n" +
            "Domestic Medium Hair - orange and white=Domestic Mediumhair\n" +
            "Domestic Medium Hair -black and white=Domestic Mediumhair\n" +
            "Domestic Short Hair=Domestic Shorthair\n" +
            "Domestic Short Hair - buff=Domestic Shorthair\n" +
            "Domestic Short Hair - gray and white=Domestic Shorthair\n" +
            "Domestic Short Hair-white=Domestic Shorthair\n" +
            "Domestic Short Hair-orange=Domestic Shorthair\n" +
            "Domestic Short Hair - orange and white=Domestic Shorthair\n" +
            "Domestic Short Hair -black and white=Domestic Shorthair\n" +
            "Exotic Shorthair=Exotic\n" +
            "Extra-Toes Cat (Hemingway Polydactyl)=Hemingway/Polydactyl\n" +
            "Havana=Havana Brown\n" + "Oriental Long Hair=Oriental\n" +
            "Oriental Short Hair=Oriental\n" + "Oriental Tabby=Oriental\n" +
            "Pixie-Bob=Domestic Shorthair\n" +
            "Sphynx (hairless cat)=Sphynx\n" + "Tabby=Domestic Shorthair\n" +
            "Tabby - Orange=Domestic Shorthair\n" +
            "Tabby - Grey=Domestic Shorthair\n" +
            "Tabby - Brown=Domestic Shorthair\n" +
            "Tabby - white=Domestic Shorthair\n" +
            "Tabby - buff=Domestic Shorthair\n" +
            "Tabby - black=Domestic Shorthair\n" +
            "Tiger=Domestic Shorthair\n" + "Torbie=Domestic Shorthair\n" +
            "Tortoiseshell=Domestic Shorthair\n" +
            "Tuxedo=Domestic Shorthair\n" + "#4:Breed2=Breed2\n" +
            "Appenzell Mountain Dog=Shepherd (Unknown Type)\n" +
            "Australian Cattle Dog/Blue Heeler=Australian Cattle Dog\n" +
            "Belgian Shepherd Dog Sheepdog=Belgian Shepherd\n" +
            "Belgian Shepherd Tervuren=Belgian Tervuren\n" +
            "Belgian Shepherd Malinois=Belgian Malinois\n" +
            "Black Labrador Retriever=Labrador Retriever\n" +
            "Brittany Spaniel=Brittany\n" + "Cane Corso Mastiff=Cane Corso\n" +
            "Chinese Crested Dog=Chinese Crested\n" +
            "Chinese Foo Dog=Shepherd (Unknown Type)\n" +
            "Dandi Dinmont Terrier=Dandie Dinmont Terrier\n" +
            "English Cocker Spaniel=Cocker Spaniel\n" +
            "English Coonhound=English (Redtick) Coonhound\n" +
            "Flat-coated Retriever=Flat-Coated Retriever\n" +
            "Fox Terrier=Fox Terrier (Smooth)\n" +
            "Hound=Hound (Unknown Type)\n" +
            "Illyrian Sheepdog=Shepherd (Unknown Type)\n" +
            "McNab =Shepherd (Unknown Type)\n" +
            "New Guinea Singing Dog=Shepherd (Unknown Type)\n" +
            "Newfoundland Dog=Newfoundland\n" +
            "Norweigan Lundehund=Shepherd (Unknown Type)\n" +
            "Peruvian Inca Orchid=Shepherd (Unknown Type)\n" +
            "Poodle=Poodle (Standard)\n" +
            "Retriever=Retriever (Unknown Type)\n" +
            "Saint Bernard St. Bernard=St. Bernard\n" +
            "Schipperkev=Schipperke\n" + "Schnauzer=Schnauzer (Standard)\n" +
            "Scottish Terrier Scottie=Scottie, Scottish Terrier\n" +
            "Setter=Setter (Unknown Type)\n" +
            "Sheep Dog=Old English Sheepdog\n" +
            "Shepherd=Shepherd (Unknown Type)\n" +
            "Shetland Sheepdog Sheltie=Sheltie, Shetland Sheepdog\n" +
            "Spaniel=Spaniel (Unknown Type)\n" +
            "Spitz=Spitz (Unknown Type, Medium)\n" +
            "South Russian Ovcharka=Shepherd (Unknown Type)\n" +
            "Terrier=Terrier (Unknown Type, Small)\n" +
            "West Highland White Terrier Westie=Westie, West Highland White Terrier\n" +
            "White German Shepherd=German Shepherd Dog\n" +
            "Wire-haired Pointing Griffon=Wirehaired Pointing Griffon\n" +
            "Wirehaired Terrier=Terrier (Unknown Type, Medium)\n" +
            "Yellow Labrador Retriever=Labrador Retriever\n" +
            "Yorkshire Terrier Yorkie=Yorkie, Yorkshire Terrier\n" +
            "American Siamese=Siamese\n" + "Bobtail=American Bobtail\n" +
            "Burmilla=Burmese\n" + "Canadian Hairless=Sphynx\n" +
            "Dilute Calico=Calico\n" +
            "Dilute Tortoiseshell=Domestic Shorthair\n" +
            "Domestic Long Hair=Domestic Longhair\n" +
            "Domestic Long Hair-black=Domestic Longhair\n" +
            "Domestic Long Hair - buff=Domestic Longhair\n" +
            "Domestic Long Hair-gray=Domestic Longhair\n" +
            "Domestic Long Hair - orange=Domestic Longhair\n" +
            "Domestic Long Hair - orange and white=Domestic Longhair\n" +
            "Domestic Long Hair - gray and white=Domestic Longhair\n" +
            "Domestic Long Hair-white=Domestic Longhair\n" +
            "Domestic Long Hair-black and white=Domestic Longhair\n" +
            "Domestic Medium Hair=Domestic Mediumhair\n" +
            "Domestic Medium Hair - buff=Domestic Mediumhair\n" +
            "Domestic Medium Hair - gray and white=Domestic Mediumhair\n" +
            "Domestic Medium Hair-white=Domestic Mediumhair\n" +
            "Domestic Medium Hair-orange=Domestic Mediumhair\n" +
            "Domestic Medium Hair - orange and white=Domestic Mediumhair\n" +
            "Domestic Medium Hair -black and white=Domestic Mediumhair\n" +
            "Domestic Short Hair=Domestic Shorthair\n" +
            "Domestic Short Hair - buff=Domestic Shorthair\n" +
            "Domestic Short Hair - gray and white=Domestic Shorthair\n" +
            "Domestic Short Hair-white=Domestic Shorthair\n" +
            "Domestic Short Hair-orange=Domestic Shorthair\n" +
            "Domestic Short Hair - orange and white=Domestic Shorthair\n" +
            "Domestic Short Hair -black and white=Domestic Shorthair\n" +
            "Exotic Shorthair=Exotic\n" +
            "Extra-Toes Cat (Hemingway Polydactyl)=Hemingway/Polydactyl\n" +
            "Havana=Havana Brown\n" + "Oriental Long Hair=Oriental\n" +
            "Oriental Short Hair=Oriental\n" + "Oriental Tabby=Oriental\n" +
            "Pixie-Bob=Domestic Shorthair\n" +
            "Sphynx (hairless cat)=Sphynx\n" + "Tabby=Domestic Shorthair\n" +
            "Tabby - Orange=Domestic Shorthair\n" +
            "Tabby - Grey=Domestic Shorthair\n" +
            "Tabby - Brown=Domestic Shorthair\n" +
            "Tabby - white=Domestic Shorthair\n" +
            "Tabby - buff=Domestic Shorthair\n" +
            "Tabby - black=Domestic Shorthair\n" +
            "Tiger=Domestic Shorthair\n" + "Torbie=Domestic Shorthair\n" +
            "Tortoiseshell=Domestic Shorthair\n" +
            "Tuxedo=Domestic Shorthair\n" + "#5:Age=Age\n" + "#6:Name=Name\n" +
            "#7:Size=Size\n" + "#8:Sex=Sex\n";

        if (!includeColours) {
            defmap += ("#9:Description=Description\n" + "#10:Status=Status\n" +
            "#11:GoodWKids=GoodWKids\n" + "#12:GoodWCats=GoodWCats\n" +
            "#13:GoodWDogs=GoodWDogs\n" +
            "#14:SpayedNeutered=SpayedNeutered\n" +
            "#15:ShotsCurrent=ShotsCurrent\n" +
            "#16:Housetrained=Housetrained\n" + "#17:Declawed=Declawed\n" +
            "#18:SpecialNeeds=SpecialNeeds");
        } else {
            defmap += ("#9:Color=Color\n" + "#10:Description=Description\n" +
            "#11:Status=Status\n" + "#12:GoodWKids=GoodWKids\n" +
            "#13:GoodWCats=GoodWCats\n" + "#14:GoodWDogs=GoodWDogs\n" +
            "#15:SpayedNeutered=SpayedNeutered\n" +
            "#16:ShotsCurrent=ShotsCurrent\n" +
            "#17:Housetrained=Housetrained\n" + "#18:Declawed=Declawed\n" +
            "#19:SpecialNeeds=SpecialNeeds");
        }

        // Try to read the mappings from the file
        // saveapet_mappings.txt in the temp directory.
        String SAVE_A_PET_MAPPINGS = Global.tempDirectory + File.separator +
            "saveapet_mappings.txt";
        File f = new File(SAVE_A_PET_MAPPINGS);

        // If the file doesn't exist, write the defaults to it and use
        // those.
        if (!f.exists()) {
            try {
                FileOutputStream o = new FileOutputStream(f);
                o.write(defmap.getBytes());
                o.flush();
                o.close();

                return defmap;
            } catch (Exception e) {
                Global.logException(e, SaveAPetPublisher.class);
            }
        } else {
            // Read the mappings from the file instead.
            try {
                FileInputStream in = new FileInputStream(SAVE_A_PET_MAPPINGS);
                int sz = in.available();
                byte[] b = new byte[sz];
                in.read(b);

                String buffer = new String(b);
                in.close();

                return buffer;
            } catch (Exception e) {
                Global.logException(e, SaveAPetPublisher.class);
            }
        }

        return defmap;
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
