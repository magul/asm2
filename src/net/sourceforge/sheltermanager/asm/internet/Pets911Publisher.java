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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Calendar;


/**
 * The actual class that does the Pets911 publishing work.
 *
 * @author Robin Rawson-Tetley
 * @version 3.0
 */
public class Pets911Publisher extends FTPPublisher {
    public Pets911Publisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        // Override certain values for pets911
        publishCriteria.uploadDirectly = true;
        publishCriteria.ftpRoot = "";
        publishCriteria.thumbnails = false;

        init("pets911", parent, publishCriteria,
            Configuration.getString("Pets911FTPURL"),
            Configuration.getString("Pets911FTPUser"),
            Configuration.getString("Pets911FTPPassword"), "21", "");
    }

    public void run() {
        String userName = Configuration.getString("Pets911FTPUser");

        /** Make sure we have some settings for Pets911 */
        if (userName.trim().equalsIgnoreCase("")) {
            Global.logError(Global.i18n("uiinternet",
                    "You_need_to_set_your_Pets911_settings_before_publishing"),
                "Pets911Publisher.run");

            if (parent == null) {
                System.exit(1);
            }

            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "You_need_to_set_your_Pets911_settings_before_publishing"));
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
                    "Pets911Publisher.run");
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

        // Start the progress meter
        initStatusBarMax(an.size());

        // Start a new buffer - this is going to be the
        // CSV file required by Pets911.
        StringBuffer dataFile = new StringBuffer();
        setStatusText(Global.i18n("uiinternet", "Publishing..."));

        // Add the header
        dataFile.append("SOURCE_ID,ANIMAL_ID,AGE_ID,SPECIES,PRIMARY_BREED," +
            "COLOR,GENDER,ALTERED_STATE,SIZE,ADDITIONAL_INFO," +
            "ANIMAL_NAME,LOST_FOUND,ADOPTABLE,IMAGE_FILENAME\n");

        try {
            int anCount = 0;

            while (!an.getEOF()) {
                anCount++;

                Global.logInfo("Processing: " + an.getShelterCode() + ": " +
                    an.getAnimalName() + " (" + anCount + " of " + an.size() +
                    ")", "Pets911Publisher.run");

                uploadImage(an, an.getShelterCode() + ".jpg");

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
                if (comm.equals("")) {
                    comm = an.getAnimalComments();
                }

                // Add any standard extra text
                comm += Configuration.getString("TPPublisherSig");

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

                // Mark media records for this animal as published
                markAnimalPublished("LastPublishedP911", an.getID());

                Global.logInfo("Finished processing " + an.getShelterCode(),
                    "Pets911Publisher.run");

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
        saveFile(publishDir + userName + ".txt", dataFile.toString());

        // Upload the files to the site
        Global.logInfo("Uploading data", "Pets911Publisher.run");
        upload(userName + ".txt");
        Global.logInfo("Data uploaded", "Pets911Publisher.run");

        resetStatusBar();
        setStatusText("");

        // Disconnect from the remote host
        closeFTPSocket();

        // Tell user it's finished
        Global.logInfo(Global.i18n("uiinternet", "Pets911_publishing_complete"),
            "Pets911Publisher.run");

        if (parent != null) {
            Dialog.showInformation(Global.i18n("uiinternet",
                    "Pets911_publishing_complete"),
                Global.i18n("uiinternet", "Pets911_upload_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet", "Pets911_upload_complete"),
                "Pets911Publisher.run");
            System.exit(0);
        }

        // Re-enable buttons
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }
    }
}
