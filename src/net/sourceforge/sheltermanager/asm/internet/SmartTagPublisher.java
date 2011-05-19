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

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;


/**
 * Publishes Smart tag PETID data to the smart tag service.
 *
 * @author Robin Rawson-Tetley
 */
public class SmartTagPublisher extends FTPPublisher {
    public SmartTagPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        // Override certain values for smarttag
        publishCriteria.ftpRoot = "";
        publishCriteria.thumbnails = false;

        init("smarttag", parent, publishCriteria,
            Configuration.getString("SmartTagFTPURL"),
            Configuration.getString("SmartTagFTPUser"),
            Configuration.getString("SmartTagFTPPassword"), "21", "");
    }

    public void run() {
        String userName = Configuration.getString("SmartTagFTPUser");

        /** Make sure we have some settings for SmartTag */
        if (userName.trim().equalsIgnoreCase("")) {
            Global.logError(Global.i18n("uiinternet",
                    "You_need_to_set_your_SmartTag_settings_before_publishing"),
                "SmartTagPublisher.run");

            if (parent == null) {
                System.exit(1);
            }

            if (parent != null) {
                Dialog.showError(Global.i18n("uiinternet",
                        "You_need_to_set_your_SmartTag_settings_before_publishing"));
            }

            enableParentButtons();
            return;
        }

        // Get a list of animals
        setStatusText(Global.i18n("uiinternet", "retrieving_animal_list"));

        Animal an = null;
        String filter = "SmartTag = 1 AND SmartTagNumber <> '' AND " + 
        	"SmartTagSentDate Is Null AND " +
        	"ActiveMovementID > 0";
        
        try {
            an = new Animal(filter);
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
                    "Pets911Publisher.run");
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

        // Start the progress meter
        initStatusBarMax(an.size());

        // Start a new buffer - this is going to be the
        // CSV file required by SmartTag.
        StringBuffer dataFile = new StringBuffer();
        setStatusText(Global.i18n("uiinternet", "Publishing..."));

        // Add the header
        dataFile.append("accountid,sourcesystem,sourcesystemidkey,sourcesystemanimalkey," +
        	"sourcesystemownerkey,signupidassigned,signuptype,signupeffectivedate,signupbatchpostdt," +
        	"feecharged,feecollected,ownerfname,ownermname,ownerlname,addressstreetnumber," +
        	"addressstreetdir,addressstreetname,addressstreettype,addresscity,addressstate," +
        	"addresspostal,addressctry,owneremail,owneremail2,owneremail3,ownerhomephone,ownerworkphone," +
        	"ownerthirdphone,petname,species,primarybreed,crossbreed,purebred,gender,sterilized," +
        	"primarycolor,secondcolor,sizecategory,agecategory,declawed,animalstatus\n");

        try {
            int anCount = 0;

            while (!an.getEOF()) {
                anCount++;

                Global.logInfo("Processing: " + an.getShelterCode() + ": " +
                    an.getAnimalName() + " (" + anCount + " of " + an.size() +
                    ")", "SmartTagPublisher.run");

                uploadImage(an, an.getShelterCode() + ".jpg");

                // Build the CSV file entry for this animal:

                // accountid
                dataFile.append("\"" + userName + "\",");
                
                // sourcesystem
                dataFile.append("\"ASM\",");
                
                // sourcesystemanimalkey (also corresponds to image name)
                dataFile.append("\"" + an.getShelterCode() + "\", ");
                
                // sourcesystemidkey
                dataFile.append("\"" + an.getShelterCode() + "\", ");
                
                // signupidassigned
                dataFile.append("\"" + an.getSmartTagNumber() + "\", ");
                
                // signuptype
                String sttype = "IDTAG-ANNUAL";
                switch (an.getSmartTagType().intValue()) {
	                case 0: sttype = "IDTAG-ANNUAL"; break;
	                case 1: sttype = "IDTAG-5 YEAR"; break;
	                case 2: sttype = "IDTAG-LIFETIME"; break;
                }
                dataFile.append("\"" + sttype + "\", ");
                
                // signupeffectivedate
                dataFile.append("\"" + an.getSmartTagDate() + "\", ");
                
                // signupbatchpostdt - only used by resending mechanism and we don't do that
                dataFile.append("\"\", ");
                
                // feecharged
                dataFile.append("\"\",");
                
                // feecollected
                dataFile.append("\"\",");
                
                Owner o = an.getCurrentOwner();
                String add = o.getOwnerAddress();
                String fline = add;
                if (fline.indexOf("\n") != -1) fline = add.substring(0, add.indexOf("\n"));
                String[] street = Utils.split(fline, " ");
                String houseno = "";
                String streetname = fline;
                if (street.length > 1) {
                	houseno = street[0];
                	streetname = fline.substring(fline.indexOf(" ") + 1);
                }
                
                // ownerfname
                dataFile.append("\"" + o.getOwnerForenames() + "\", ");
                 
                // ownermname
                dataFile.append("\"\", ");
                
                // ownerlname
                dataFile.append("\"" + o.getOwnerSurname() + "\", ");
                
                // addressstreetnumber
                dataFile.append("\"" + houseno + "\", ");
                
                // addressstreetdir
                dataFile.append("\"\", ");
                
                // addressstreetname
                dataFile.append("\"" + streetname + "\", ");
                
                // addressstreettype
                dataFile.append("\"\", ");
                
                // addresscity
                dataFile.append("\"" + o.getOwnerTown() + "\", ");
                
                // addressstate
                dataFile.append("\"" + o.getOwnerCounty() + "\", ");
                
                // addresspostal
                dataFile.append("\"" + o.getOwnerPostcode() + "\", ");
                
                // addressctry
                dataFile.append("\"USA\", ");
                
                // owneremail
                dataFile.append("\"" + o.getEmailAddress() + "\", ");
                
                // owneremail2
                dataFile.append("\"\", ");
                
                // owneremail3
                dataFile.append("\"\", ");
                
                // ownerhomephone
                dataFile.append("\"" + o.getHomeTelephone() + "\", ");
                
                // ownerworkphone
                dataFile.append("\"" + o.getWorkTelephone() + "\", ");
                
                // ownerthirdphone
                dataFile.append("\"" + o.getMobileTelephone() + "\", ");
                
                // petname
                dataFile.append("\"" + an.getAnimalName() + "\", ");
                
                // species
                dataFile.append("\"" + LookupCache.getSpeciesName(an.getSpeciesID()) + "\", ");
                
                // primarybreed
                dataFile.append("\"" + LookupCache.getBreedName(an.getBreedID()) + "\", ");
                
                // crossbreed (second breed)
                if (an.getCrossBreed().intValue() == 1)
                	dataFile.append("\"" + LookupCache.getBreedName(an.getBreed2ID()) + "\", ");
                else
                	dataFile.append("\"\", ");	
                
                // purebred (Y or N)
                dataFile.append("\"" + (an.getCrossBreed().intValue() == 0 ? "Y" : "N") + "\", ");
                
                // gender
                dataFile.append("\"" + an.getSexName() + "\", ");
                
                // sterilized (Y or N)
                dataFile.append("\"" + (an.getNeutered().intValue() == 1 ? "Y" : "N") + "\", ");
                
                // primarycolor
                dataFile.append("\"" + an.getBaseColourName() + "\", ");
                
                // secondcolor
                dataFile.append("\"\", ");
                
                // sizecategory
                dataFile.append("\"" + an.getSizeName() + "\", ");
                
                // agecategory
                dataFile.append("\"" + an.getAgeGroup() + "\", ");
                
                // declawed (Y or N)
                dataFile.append("\"" + (an.getDeclawed().intValue() == 1 ? "Y" : "N") + "\", ");
                
                // animalstatus (blank or D for Deceased)
                dataFile.append("\"" + (an.getDeceasedDate() != null ? "D" : "") + "\"");

                // Terminate
                dataFile.append("\n");

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

        // Save the data file to our publish directory with the name
        // shelterid_mmddyyyy_HHMMSS.csv
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy_HHmmss");
        String filename = userName + "_" + sdf.format(new Date()) + ".csv";
        saveFile(publishDir + filename, dataFile.toString());

        // Upload the files to the site
        Global.logInfo("Uploading data", "SmartTagPublisher.run");
        upload(filename);
        Global.logInfo("Data uploaded", "SmartTagPublisher.run");
        
        try {
	        // Flag our batch as sent
	        DBConnection.executeAction("UPDATE animal SET SmartTagSentDate = '" + Utils.getSQLDate(new Date()) +
	        	"' WHERE " + filter);
        }
        catch (Exception e) {
        	Global.logException(e, getClass());
        }

        resetStatusBar();
        setStatusText("");

        // Disconnect from the remote host
        closeFTPSocket();

        // Tell user it's finished
        if (parent != null) {
        	Global.logInfo(Global.i18n("uiinternet", "SmartTag_publishing_complete"),
            	"SmartTagPublisher.run");
            Dialog.showInformation(Global.i18n("uiinternet",
                    "SmartTag_publishing_complete"),
                Global.i18n("uiinternet", "SmartTag_publishing_complete"));
        } else {
            Global.logInfo(Global.i18n("uiinternet", "SmartTag_publishing_complete"),
                "SmartTagPublisher.run");
            System.exit(0);
        }

        // Re-enable buttons
        enableParentButtons();
    }
}
