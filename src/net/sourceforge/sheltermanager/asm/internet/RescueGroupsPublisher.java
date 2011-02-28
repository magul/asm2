/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.internet.PetFinderMapBreed;
import net.sourceforge.sheltermanager.asm.ui.internet.PetFinderMapSpecies;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;


/**
 * The actual class that does the RescueGroups.org publishing work.
 * NB: 01/01/2011, RG only accept active FTP connections
 *
 * @author Robin Rawson-Tetley
 * @version 3.0
 */
public class RescueGroupsPublisher extends FTPPublisher {
    public RescueGroupsPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        // Override certain values for rescuegroups
        publishCriteria.uploadDirectly = true;
        publishCriteria.ftpRoot = "";
        publishCriteria.thumbnails = false;

        init("rescuegroups", parent, publishCriteria,
            Configuration.getString("RescueGroupsFTPURL"),
            Configuration.getString("RescueGroupsFTPUser"),
            Configuration.getString("RescueGroupsFTPPassword"), "21", "", false);
    }

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

        // Check settings
        String shelterId = Configuration.getString("RescueGroupsFTPUser");

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

        // Get a list of animals
        setStatusText(Global.i18n("uiinternet", "retrieving_animal_list"));

        Animal an = null;

        try {
            an = getMatchingAnimals();
        } catch (Exception e) {
            Global.logException(e, getClass());

            if (parent != null) {
                Dialog.showError(e.getMessage());
            } else {
                System.exit(1);
            }
        }

        // If there aren't any animals, there's no point do
        // anything
        if (an.size() == 0) {
            if (parent != null) {
                Dialog.showInformation(Global.i18n("uiinternet",
                        "No_matching_animals_were_found_to_publish"));

                return;
            } else {
                Global.logError(Global.i18n("uiinternet",
                        "No_matching_animals_were_found_to_publish"),
                    "RescueGroupsPublisher.run");
                System.exit(1);
            }
        }

        // Open the socket
        if (!openFTPSocket()) {
            if (parent == null) {
                System.exit(1);
            } else {
                return;
            }
        }

        // Attempt to create the import/pictures directory and fail silently
        // if it already exists.
        mkdir("import");
        chdir("import");
        mkdir("pictures");
        chdir("pictures", "import/pictures");

        // Start the progress meter
        initStatusBarMax(an.size());

        // Start a new buffer - this is going to be the
        // CSV file required by RescueGroups.
        StringBuffer dataFile = new StringBuffer();
        setStatusText(Global.i18n("uiinternet", "Publishing..."));

        try {
            an.moveFirst();

            int anCount = 0;

            while (!an.getEOF()) {
                anCount++;

                Global.logInfo("Processing: " + an.getShelterCode() + ": " +
                    an.getAnimalName() + " (" + anCount + " of " + an.size() +
                    ")", "RescueGroupsPublisher.run");

                // Rescuegroups allows upto a total of 4 images, so
                // if we have uploadAll on, stop at 4
                int totalimages = uploadImages(an, 4);

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
                dataFile.append("\"" + Configuration.getString("Organisation") +
                    "\", ");

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
                dataFile.append("\"" + pfBreed1 + "\", ");

                // secondary breed - send a blank if it's not a crossbreed
                if (an.getCrossBreed().intValue() == 1) {
                    dataFile.append("\"" + pfBreed2 + "\", ");
                } else {
                    dataFile.append("\"\", ");
                }

                // sex
                dataFile.append("\"" +
                    LookupCache.getSexNameForID(an.getSex()) + "\", ");

                // mixed
                dataFile.append("\"" +
                    ((an.getCrossBreed().intValue() == 1) ? "Yes" : "No") +
                    "\", ");

                // dogs (good with)
                dataFile.append("\"" + yesNoUnknownBlank(an.isGoodWithDogs()) +
                    "\", ");

                // cats
                dataFile.append("\"" + yesNoUnknownBlank(an.isGoodWithCats()) +
                    "\", ");

                // kids
                dataFile.append("\"" + yesNoUnknownBlank(an.isGoodWithKids()) +
                    "\", ");

                // declawed
                dataFile.append((an.getDeclawed().intValue() == 1)
                    ? "\"Yes\", " : "\"No\", ");

                // housetrained
                dataFile.append("\"" + yesNoUnknownBlank(an.isHouseTrained()) +
                    "\", ");

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

                // No web media, use the animal comments instead
                if (comm.equals("")) {
                    comm = an.getAnimalComments();
                }

                // Add any standard extra text
                comm += Configuration.getString("TPPublisherSig");

                // Strip CR/LF
                comm = Utils.replace(comm, new String(new byte[] { 13 }), "");
                comm = Utils.replace(comm, new String(new byte[] { 10 }), "");

                // Switch quotes
                comm = comm.replace('"', '\'');
                dataFile.append("\"" + comm + "\"");
                comm = null;

                // pic1 - pic4
                if (totalimages > 0) {
                	// if uploadAll isn't on, there was just one image
                	// with the sheltercode as the name
                	if (!publishCriteria.uploadAllImages) {
                		dataFile.append("\"" + an.getShelterCode() + ".jpg\", \"\", \"\", \"\"");
                	}
                	else {
                		// Upload is on, output an entry for each image
                		// we uploaded upto a maximum of 4
		                for (int i = 1; i <= 4; i++) {
		                    dataFile.append(", ");
		
		                    if (totalimages >= i) {
		                        dataFile.append("\"" + an.getShelterCode() + "-" + i +
		                            ".jpg" + "\"");
		                    } else {
		                        dataFile.append("\"\"");
		                    }
		                }
                	}
                }
                else {
                	// Leave it blank
                	dataFile.append("\"\", \"\", \"\", \"\"");
                }

                // Terminate
                dataFile.append("\n");

                // Mark media records for this animal as published
                markAnimalPublished("LastPublishedRG", an.getID());

                Global.logInfo("Finished processing " + an.getShelterCode(),
                    "RescueGroupsPublisher.run");

                an.moveNext();
                incrementStatusBar();
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

        // Save the data file to our publish directory
        String csvhead = "orgID, animalID, status, lastUpdated, rescueID, name, summary, species, breed, " +
            "primaryBreed, secondaryBreed, sex, mixed, dogs, cats, kids, declawed, housetrained, age, " +
            "specialNeeds, altered, size, uptodate, color, coatLength, pattern, courtesy, description, pic1, " +
            "pic2, pic3, pic4\n";

        saveFile(publishDir + "pets.csv", csvhead + dataFile.toString());

        // Upload the files to the site
        chdir("..", "import");
        Global.logInfo("Uploading data", "RescueGroupsPublisher.run");
        upload("pets.csv");
        Global.logInfo("Data uploaded", "RescueGroupsPublisher.run");

        if (parent != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }

        // Disconnect from the remote host
        closeFTPSocket();

        // Tell user it's finished
        if (parent != null) {
            Dialog.showInformation(Global.i18n("uiinternet",
                    "rescue_groups_publishing_complete"),
                Global.i18n("uiinternet", "rescue_groups_upload_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet",
                    "rescue_groups_publishing_complete"),
                "RescueGroupsPublisher.run");
            System.exit(0);
        }

        // Re-enable buttons
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }
    }

    /**
     * Verifies that all system species have been mapped to PetFinder types
     * before running
     */
    private boolean checkMappedSpecies() {
        boolean retval = false;

        try {
            SQLRecordset s = new SQLRecordset();
            s.openRecordset("SELECT * FROM species WHERE PetFinderSpecies Is Null OR PetFinderSpecies = ''",
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
                    "RescueGroupsPublisher.checkMappedSpecies");
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
            b.openRecordset("SELECT * FROM breed WHERE PetFinderBreed Is Null OR PetFinderBreed = ''",
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
                    "RescueGroupsPublisher.checkMappedBreeds");
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
}
