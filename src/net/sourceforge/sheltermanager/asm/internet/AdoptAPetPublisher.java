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
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.io.File;

import java.util.Calendar;


/**
 * The actual class that does the AdoptAPet.com publishing work
 *
 * @author Robin Rawson-Tetley
 * @version 3.0
 */
public class AdoptAPetPublisher extends FTPPublisher {
    public AdoptAPetPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        publishCriteria.uploadDirectly = true;
        publishCriteria.ftpRoot = "";
        publishCriteria.thumbnails = false;

        init("adoptapet", parent, publishCriteria,
            Configuration.getString("SaveAPetFTPURL"),
            Configuration.getString("SaveAPetFTPUser"),
            Configuration.getString("SaveAPetFTPPassword"), "21", "");
    }

    public void run() {
        String shelterId = Configuration.getString("SaveAPetFTPUser");

        // Make sure we have some settings for AdoptAPet
        if (shelterId.trim().equalsIgnoreCase("")) {
            Global.logError(Global.i18n("uiinternet",
                    "You_need_to_set_your_save_a_pet_settings_before_publishing"),
                "SaveAPetPublisher.run");

            if (parent == null) {
                System.exit(1);
            } else {
                Dialog.showError(Global.i18n("uiinternet",
                        "You_need_to_set_your_save_a_pet_settings_before_publishing"));
            }

            enableParentButtons();

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

                enableParentButtons();

                return;
            } else {
                Global.logError(Global.i18n("uiinternet",
                        "No_matching_animals_were_found_to_publish"),
                    "AdoptAPetPublisher.run");
                System.exit(1);
            }
        }

        // Open the socket
        if (!openFTPSocket()) {
            if (parent == null) {
                System.exit(1);
            } else {
                enableParentButtons();

                return;
            }
        }

        // Go to the photos directory first
        mkdir("photos");
        chdir("photos");

        // Start the progress meter
        initStatusBarMax(an.size());

        // Start a new buffer - this is going to be the
        // CSV file required by AdoptAPet.
        StringBuffer dataFile = new StringBuffer();
        setStatusText(Global.i18n("uiinternet", "Publishing..."));

        try {
            int anCount = 0;

            while (!an.getEOF()) {
                anCount++;

                Global.logInfo("Processing: " + an.getShelterCode() + ": " +
                    an.getAnimalName() + " (" + anCount + " of " + an.size() +
                    ")", "SaveAPetPublisher.run");

                uploadImages(an, false);

                // Build the CSV file entry for this animal:

                // Id
                dataFile.append("\"" + an.getShelterCode() + "\",");

                // Species
                dataFile.append("\"" +
                    LookupCache.getSpeciesName(an.getSpeciesID()) + "\",");

                // Breed
                dataFile.append("\"" +
                    LookupCache.getBreedName(an.getBreedID()) + "\",");

                // Breed2
                dataFile.append("\"" +
                    LookupCache.getBreedName(an.getBreed2ID()) + "\",");

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
                dataFile.append("\"" +
                    an.getAnimalName().replaceAll("\"", "\"\"") + "\",");

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
                dataFile.append("\"" + an.getSexName().substring(0, 1) + "\",");

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

                // Add any standard extra text
                comm += Configuration.getString("TPPublisherSig");

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
                dataFile.append((an.getNeutered().intValue() == 1) ? "\"1\","
                                                                   : "\"0\",");

                // Shots current
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
                dataFile.append((an.getDeclawed().intValue() == 1) ? "\"1\","
                                                                   : "\"0\",");

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
                markAnimalPublished("LastPublishedAP", an.getID());

                Global.logInfo("Finished processing " + an.getShelterCode(),
                    "SaveAPetPublisher.run");

                an.moveNext();
                incrementStatusBar();
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }

        // Generate the map file for AdoptAPet to transform necessary data
        String cfg = getMappings(publishCriteria.includeColours);

        saveFile(publishDir + "import.cfg", cfg);

        // Save the data file to our publish directory
        saveFile(publishDir + "pets.csv", dataFile.toString());

        // Upload the data files to the site
        chdir("..", "");
        Global.logInfo("Uploading data", "SaveAPetPublisher.run");
        upload("pets.csv");
        Global.logInfo("Data uploaded", "SaveAPetPublisher.run");

        if (!publishCriteria.noImportFile) {
            Global.logInfo("Uploading data map", "SaveAPetPublisher.run");
            upload("import.cfg");
            Global.logInfo("Data map uploaded.", "SaveAPetPublisher.run");
        } else {
            Global.logInfo("Data map upload disabled.", "SaveAPetPublisher.run");
        }

        if (parent != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }

        // Disconnect from the remote host
        closeFTPSocket();

        // Tell user it's finished
        if (parent != null) {
            Dialog.showInformation(Global.i18n("uiinternet",
                    "saveapet_publishing_complete"),
                Global.i18n("uiinternet", "saveapet_upload_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet",
                    "saveapet_publishing_complete"), "SaveAPetPublisher.run");
            System.exit(0);
        }

        // Re-enable buttons
        enableParentButtons();
    }

    /** Returns the contents of the mappings file (import.cfg) for
     *  AdoptAPet.com
     * @return
     */
    public String getMappings(boolean includeColours) {
        String defmap = "; AdoptAPet.com import map. This file was autogenerated by\n" +
            "; Animal Shelter Manager. http://sheltermanager.com\n" +
            "; The FREE, open source solution for animal sanctuaries and rescue shelters.\n\n" +
            "#1:Id=Id\n" + "#2:Animal=Animal\n" +
            "Sugar Glider=Small Animal\n" + "Mouse=Small Animal\n" +
            "Rat=Small Animal\n" + "Hedgehog=Small Animal\n" + "Dove=Bird\n" +
            "Ferret=Small Animal\n" + "Chinchilla=Small Animal\n" +
            "Snake=Reptile\n" + "Tortoise=Reptile\n" + "Terrapin=Reptile\n" +
            "Chicken=Farm Animal\n" + "Owl=Bird\n" + "Goat=Farm Animal\n" +
            "Goose=Bird\n" + "Gerbil=Small Animal\n" + "Cockatiel=Bird\n" +
            "Guinea Pig=Small Animal\n" + "Hamster=Small Animal\n" +
            "Camel=Horse\n" + "Pony=Horse\n" + "Donkey=Horse\n" +
            "Llama=Horse\n" + "Pig=Farm Animal\n" + "#3:Breed=Breed\n" +
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
            defmap += ("#9:Color=Color\n" +
            "Amber=Red/Golden/Orange/Chestnut\n" +
            "Black Tortie=Tortoiseshell\n" +
            "Black and Brindle=Black - with Tan, Yellow or Fawn\n" +
            "Black and Brown=Black - with Tan, Yellow or Fawn\n" +
            "Black and Tan=Black - with Tan, Yellow or Fawn\n" +
            "Black and White=Black - with White\n" + "Blue=Gray or Blue\n" +
            "Blue Tortie=Tortoiseshell\n" + "Brindle and Black=Brindle\n" +
            "Brindle and White=Brindle - with White\n" +
            "Brown=Brown/Chocolate\n" +
            "Brown and Black=Brown/Chocolate - with Black\n" +
            "Brown and White=Brown/Chocolate - with White\n" +
            "Chocolate=Brown/Chocolate\n" + "Chocolate Tortie=Tortoiseshell\n" +
            "Cinnamon=Red/Golden/Orange/Chestnut\n" +
            "Cinnamon Tortoiseshell=Tortoiseshell\n" +
            "Cream=White - with Tan, Yellow or Fawn\n" +
            "Fawn=Tan/Yellow/Fawn\n" + "Fawn Tortoise=Tortoiseshell\n" +
            "Ginger=Red/Golden/Orange/Chestnut\n" +
            "Ginger and White=Red/Golden/Orange/Chestnut - with White\n" +
            "Golden=Tan/Yellow/Fawn\n" +
            "Grey=Gray/Blue/Silver/Salt & Pepper\n" +
            "Grey and White=Gray/Silver/Salt & Pepper - with White\n" +
            "Light Amber=Tan/Yellow/Fawn\n" +
            "Lilac=Gray/Blue/Silver/Salt & Pepper\n" +
            "Lilac Tortie=Tortoiseshell\n" + "Liver=Brown/Chocolate\n" +
            "Liver and White=Brown/Chocolate - with White\n" +
            "Red=Red/Golden/Orange/Chestnut\n" +
            "Ruddy=Red/Golden/Orange/Chestnut\n" +
            "Seal=Gray/Blue/Silver/Salt & Pepper\n" +
            "Silver=Gray/Blue/Silver/Salt & Pepper\n" +
            "Sorrel=Red/Golden/Orange/Chestnut\n" +
            "Sorrel Tortoiseshell=Tortoiseshell\n" + "Tabby=Brown Tabby\n" +
            "Tabby and White=Brown Tabby\n" + "Tan=Tan/Yellow/Fawn\n" +
            "Tan and Black=Tan/Yellow/Fawn - with Black\n" +
            "Tan and White=Tan/Yellow/Fawn - with White\n" +
            "Tortie=Tortoiseshell\n" + "Tortie and White=Tortoiseshell\n" +
            "Tricolour=Tricolor (Tan/Brown & Black & White)\n" +
            "Various=Tricolor (Tan/Brown & Black & White)\n" +
            "White and Black=White - with Black\n" +
            "White and Brindle=White - with Black\n" +
            "White and Brown=White - with Brown or Chocolate\n" +
            "White and Grey=White - with Gray or Silver\n" +
            "White and Liver=White - with Brown or Chocolate\n" +
            "#10:Description=Description\n" + "#11:Status=Status\n" +
            "#12:GoodWKids=GoodWKids\n" + "#13:GoodWCats=GoodWCats\n" +
            "#14:GoodWDogs=GoodWDogs\n" +
            "#15:SpayedNeutered=SpayedNeutered\n" +
            "#16:ShotsCurrent=ShotsCurrent\n" +
            "#17:Housetrained=Housetrained\n" + "#18:Declawed=Declawed\n" +
            "#19:SpecialNeeds=SpecialNeeds");
        }

        // Try to read the mappings from the file
        // saveapet_mappings.txt in the temp directory.
        String ADOPT_A_PET_MAPPINGS = Global.tempDirectory + File.separator +
            "adoptapet_mappings.txt";
        File f = new File(ADOPT_A_PET_MAPPINGS);

        // If the file doesn't exist, use the defaults
        if (!f.exists()) {
            try {
                return defmap;
            } catch (Exception e) {
                Global.logException(e, AdoptAPetPublisher.class);
            }
        } else {
            // Read the mappings from the file instead.
            try {
                return Utils.readFile(ADOPT_A_PET_MAPPINGS);
            } catch (Exception e) {
                Global.logException(e, AdoptAPetPublisher.class);
            }
        }

        return defmap;
    }
}
