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

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;

import java.util.Calendar;
import java.util.Date;


/**
 * Contains basic functionality for internet publishing
 * @author Robin Rawson-Tetley
 */
public abstract class AbstractPublisher extends Thread {
    /** Reference to the UI if we were started from one */
    protected InternetPublisher parent = null;

    /** The publishing criteria */
    protected PublishCriteria publishCriteria = null;

    /** The number of animals to be published */
    protected int totalAnimals = 0;

    /** The name of the publisher, used for generating the temp folder */
    protected String publisherName = "";

    /** The path to the temp folder */
    protected String publishDirectory = "";

    /** Same as publishDirectory, but with trailing separator */
    protected String publishDir = "";

    /**
     * Sets publisher values
     * @param parent
     * @param publishCriteria
     * @param name
     */
    protected void init(String publisherName, InternetPublisher parent,
        PublishCriteria publishCriteria) {
        this.publisherName = publisherName;
        this.parent = parent;
        this.publishCriteria = publishCriteria;
        makePublishDirectory();
    }

    /**
     * Creates the publish temporary directory
     * @param name The name of the target temp folder
     * @return The full path to the temp folder with trailing separator
     */
    protected String makePublishDirectory() {
        publishDirectory = Global.tempDirectory + File.separator +
            publisherName;

        File file = new File(publishDirectory);

        if (file.exists()) {
        } else {
            // Create the directory
            file.mkdirs();
        }

        publishDir = publishDirectory + File.separator;

        return publishDir;
    }

    /**
     * Re-enables the publish buttons on the parent
     * form if we have one.
     */
    protected void enableParentButtons() {
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }
    }

    /**
     * For a yes/no/unknown value, returns the string
     * @param v The value
     * @return The string name
     */
    protected String yesNoUnknown(Integer v) {
        switch (v.intValue()) {
        case 0:
            return Global.i18n("reports", "Yes");

        case 1:
            return Global.i18n("reports", "No");

        default:
            return Global.i18n("reports", "Unknown");
        }
    }

    /**
     * For a yes/no/unknown value, returns the string
     * or empty string for Unknown
     * @param v The value
     * @return The string name
     */
    protected String yesNoUnknownBlank(Integer v) {
        switch (v.intValue()) {
        case 0:
            return Global.i18n("reports", "Yes");

        case 1:
            return Global.i18n("reports", "No");

        default:
            return "";
        }
    }

    /**
     * Updates the media fields for an animal to mark last published
     * @param field The field name
     * @param id The Animal ID
     * @return true if the update was successful
     */
    protected boolean markAnimalPublished(String field, int id) {
        try {
            Global.logDebug("Marking media records published for animal " + id +
                " in field " + field,
                getClass().getName() + ".markAnimalPublished");
            DBConnection.executeAction("UPDATE media SET " + field + " = '" +
                Utils.getSQLDate(new Date()) + "' WHERE LinkID = " + id +
                " AND LinkTypeID = 0");

            return true;
        } catch (Exception e) {
            Global.logException(e, getClass());

            return false;
        }
    }

    /**
     * Gets the list of matching animals for the criteria
     * given.
     */
    protected Animal getMatchingAnimals() throws Exception {
        StringBuffer sql = new StringBuffer("");

        // If the include case animals option is off, make
        // sure the animal isn't a case one
        if (!publishCriteria.includeCase) {
            sql.append("CrueltyCase = 0");
        }

        // Make sure it has a valid picture if the option is off
        if (!publishCriteria.includeWithoutImage) {
            if (sql.length() != 0) {
                sql.append(" AND ");
            }

            sql.append("EXISTS(SELECT ID FROM media " +
                "WHERE WebsitePhoto = 1 AND LinkID = animal.ID " +
                "AND LinkTypeID = 0)");
        }

        // If the include reserves option is off, make
        // sure it isn't reserved
        if (!publishCriteria.includeReserved) {
            if (sql.length() != 0) {
                sql.append(" AND ");
            }

            sql.append("HasActiveReserve = 0");
        }

        // Only include animals in the selected internal
        // locations - there must be one selected, so don't
        // worry about bad SQL with the AND here.
        // If no locations are specified, then don't do a filter
        // ie. do all
        if (publishCriteria.internalLocations != null) {
            if (sql.length() != 0) {
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

        if (sql.length() != 0) {
            sql.append(" AND ");
        }

        sql.append("DateOfBirth <= '" + Utils.getSQLDate(today) + "'");

        // Filter out dead animals, and ones not for adoption
        if (sql.length() != 0) {
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

        // Ordering mode
        switch (publishCriteria.order) {
        case 0:
            sql.append(" ORDER BY MostRecentEntryDate");

            break;

        case 1:
            sql.append(" ORDER BY MostRecentEntryDate DESC");

            break;

        case 2:
            sql.append(" ORDER BY AnimalName");

            break;

        default:
            sql.append(" ORDER BY MostRecentEntryDate");

            break;
        }

        // Grab the set and return it
        return new Animal(sql.toString());
    }

    /**
     * Flushes the given content to the named file.
     */
    protected void saveFile(String filepath, String content) {
        try {
            Utils.writeFile(filepath, content.getBytes(Global.CHAR_ENCODING));
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }
    }

    /**
     * Returns true if the filename given has a valid image extension
     * @param s
     * @return true if the filename given has a valid image extension
     */
    protected boolean isImage(String s) {
        s = s.toLowerCase();

        return s.endsWith("jpg") || s.endsWith("jpeg") || s.endsWith("png");
    }

    /**
     * Generates a thumbnail (70px on longest side)
     * @param pathToImage Publishing directory
     * @param imagename The name of the image to make a thumbnail from
     * @param thumbnail The name of the thumbnail output file
     */
    protected void generateThumbnail(String pathToImage, String imagename,
        String thumbnail) {
        UI.scaleImage(pathToImage + imagename, pathToImage + thumbnail, 70, 70);
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
    protected void scaleImage(String pathToImage, int scalesize) {
        int width = 320;
        int height = 200;

        switch (scalesize) {
        case 2:
            width = 320;
            height = 200;

            break;

        case 3:
            width = 640;
            height = 400;

            break;

        case 4:
            width = 800;
            height = 600;

            break;

        case 5:
            width = 1024;
            height = 768;

            break;

        case 6:
            width = 300;
            height = 300;

            break;

        case 7:
            width = 95;
            height = 95;

            break;
        }

        if (Configuration.getBoolean("UseOldScaling")) {
            UI.scaleImageOld(pathToImage, pathToImage, width, height);
        } else {
            UI.scaleImage(pathToImage, pathToImage, width, height);
        }
    }

    protected void initStatusBarMax(int max) {
        if (parent != null) {
            Global.mainForm.initStatusBarMax(max);
        }
    }

    protected void setStatusText(String s) {
        if (parent != null) {
            Global.mainForm.setStatusText(s);
        }
    }

    protected void resetStatusBar() {
        if (parent != null) {
            Global.mainForm.resetStatusBar();
        }
    }

    protected void incrementStatusBar() {
        if (parent != null) {
            Global.mainForm.incrementStatusBar();
        }
    }
}
