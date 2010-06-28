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
package net.sourceforge.sheltermanager.asm.db;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.bo.Users;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.MD5;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 * This class handles all database updates via a version key.
 *
 * @author Robin Rawson-Tetley
 */
public class AutoDBUpdates {
    public final static int[] updates = new int[] {
            105, 1051, 1052, 1100, 1101, 1102, 1103, 1111, 1112, 1114, 1122,
            1123, 1124, 1125, 1131, 1202, 1203, 1204, 1205, 1211, 1212, 1213,
            1221, 1321, 1341, 1351, 1352, 1361, 1362, 1363, 1364, 1371, 1372,
            1381, 1382, 1383, 1391, 1392, 1393, 1394, 1401, 1402, 1411, 2001,
            2021, 2023, 2100, 2102, 2210, 2301, 2302, 2303, 2310, 2350, 2390,
            2500, 2600, 2601, 2610, 2611, 2621, 2641, 2700, 2701, 2702, 2703,
            2704, 2705, 2706, 2707, 2708, 2720
        };

    /**
     * The latest database version this version of the program can deal with.
     */
    public final static int LATEST_DB_VERSION = updates[updates.length - 1];

    /** Collection of errors occurred during update */
    private ErrorVector errors = null;

    public AutoDBUpdates() {
    }

    /** Checks whether the config switch is enabled to allow
     *  database updates - if it isn't, an exception is thrown
     *  (which causes false to be returned by run() and stops
     *  ASM loading with an error)
     */
    public void checkUpdateAllowed() throws Exception {
        if (!Configuration.getString("AllowDBAutoUpdates", "Yes").equals("Yes")) {
            throw new Exception(Global.i18n("db", "no_allowdbautoupdates"));
        }
    }

    /**
     *  Executes any database updates
     *  @return true if all is ok, false if an error occurred
     */
    public boolean runUpdates() {
        int v = 0;
        errors = new ErrorVector();

        try {
            v = Configuration.getInteger("DatabaseVersion");

            // Go through every update below our current version, in order and
            // run it:
            for (int i = 0; i < updates.length; i++) {
                int dbv = updates[i];

                if (v < dbv) {
                    // Our current database version is below this update,
                    // execute it if we're allowed:
                    Global.logInfo("Updating database to " + dbv +
                        ", please wait...", "AutoDBUpdates");
                    checkUpdateAllowed();
                    getClass().getMethod("update" + Integer.toString(dbv), null)
                        .invoke(this, null);

                    // Update our database version to the update we just ran
                    v = dbv;
                    Configuration.setEntry("DatabaseVersion",
                        Integer.toString(v));
                }
            }

            // All successful
            finish();

            return true;
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());

            return false;
        }
    }

    /**
     * Checks LATEST_DB_VERSION against the tag on the database. If the database
     * has a new version than we know about, then this client is too old for the
     * database.
     *
     * @return True if the client is newer than or equal to the database version
     *         (ie. All is ok)
     */
    public static boolean checkDatabaseVersion() {
        try {
            int iver = Configuration.getInteger("DatabaseVersion");
            Global.logDebug("Client DB Ver: " + LATEST_DB_VERSION +
                ", Server DB Ver: " + iver, "AutoDBUpdates.checkDatabaseVersion");

            return LATEST_DB_VERSION >= iver;
        } catch (Exception e) {
            Global.logException(e, AutoDBUpdates.class);

            return false;
        }
    }

    public void finish() {
        try {
            // Check for errors and warn user
            // if there were some.
            if (errors.size() > 0) {
                String err = "";

                for (int i = 0; i < errors.size(); i++) {
                    err += (errors.get(i).toString() + "\n");
                }

                try {
                    Dialog.showError("Errors occurred updating:\n\n" + err +
                        "\nYou may experience some problems.");
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }
            }

            errors.removeAllElements();
            errors = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        } finally {
        }
    }

    public void update105() {
        try {
            // Add flag for archived animals
            try {
                String sql = "ALTER TABLE animal ADD Archived tinyint NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: Adding Archived flag");
            }

            // Set the archive flag for existing animal records based
            // on whether they have left the shelter.
            Global.logInfo("Archiving old records...", "AutoDBUpdates");

            Animal a = new Animal();
            a.openRecordset("");

            while (!a.getEOF()) {
                try {
                    String sql = "UPDATE animal SET Archived = " +
                        (a.isAnimalOnShelter() ? "0" : "1") + " WHERE ID = " +
                        a.getID();
                    DBConnection.executeAction(sql);
                } catch (Exception e) {
                    errors.add("updating animal: " + a.getID());
                }

                a.moveNext();
            }

            a.free();
            a = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1051() {
        try {
            try {
                // Add flag for owner donors
                String sql = "ALTER TABLE owner ADD IsDonor tinyint NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("owner: Adding IsDonor flag");
            }

            // Add ownerdonation table
            try {
                String sql = "CREATE TABLE ownerdonation (" +
                    "ID int(11) NOT NULL default '0'," +
                    "OwnerID int(11) NOT NULL default '0'," +
                    "Date datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "Donation double NOT NULL default '0'," +
                    "Comments text default NULL," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + "KEY IX_Date (Date)" +
                    ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("ownerdonation: Table creation");
            }

            // Add custom report table
            try {
                String sql = "CREATE TABLE customreport (" +
                    "ID int(11) NOT NULL default '0'," +
                    "Title varchar(255) NOT NULL," +
                    "SQLCommand text NOT NULL," + "HTMLBody text NOT NULL," +
                    "Description text default NULL," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + "KEY IX_Title (Title)" +
                    ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("customreport: Table creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1052() {
        try {
            // Add flags for owners
            try {
                String sql = "ALTER TABLE owner ADD IsACO tinyint NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD IsShelter tinyint NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD IsStaff tinyint NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "owner: Addition of IsACO, IsShelter and IsStaff flags");
            }

            // New fields for waiting list (probably not used in this beta)
            try {
                String sql = "ALTER TABLE animalwaitinglist ADD AutoRemovePolicy int DEFAULT '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalwaitinglist ADD DateOfLastOwnerContact datetime DEFAULT NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "animalwaitinglist: Addition of AutoRemovePolicy and DateOfLastOwnerContact");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1100() {
        try {
            try {
                // Lookup table for Sex
                String sql = "CREATE TABLE lksex (" +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "Sex varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ") Type=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksex VALUES (0, 'Female')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksex VALUES (1, 'Male')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksex VALUES (2, 'Unknown')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("lksex: Table creation");
            }

            try {
                // Lookup table for Size
                String sql = "CREATE TABLE lksize (" +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "Size varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ") Type=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksize VALUES (0, 'Very Large')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksize VALUES (1, 'Large')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksize VALUES (2, 'Medium')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksize VALUES (3, 'Small')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("lksize: Table creation");
            }

            try {
                // Lookup table for movement type
                String sql = "CREATE TABLE lksmovementtype (" +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "MovementType varchar(40) NOT NULL, " +
                    "PRIMARY KEY  (ID) " + ") Type=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (0, 'None')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (1, 'Adoption')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (2, 'Foster')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (3, 'Transfer')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (4, 'Escaped')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (5, 'Reclaimed')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (6, 'Stolen')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (7, 'Released To Wild')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (8, 'Retailer')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (9, 'Reservation')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmovementtype VALUES (10, 'Cancelled Reservation')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("lksmovementtype: Table creation");
            }

            try {
                // Lookup table for media link type
                String sql = "CREATE TABLE lksmedialink (" +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "LinkType varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ") Type=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmedialink VALUES (0, 'Animal')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmedialink VALUES (1, 'Lost Animal')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmedialink VALUES (2, 'Found Animal')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmedialink VALUES (3, 'Owner')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksmedialink VALUES (4, 'Movement')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("lksmedialink: Table creation");
            }

            try {
                // Lookup table for diary link type
                String sql = "CREATE TABLE lksdiarylink (" +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "LinkType varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ") Type=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (0, 'None')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (1, 'Animal')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (2, 'Owner')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (3, 'Lost Animal')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (4, 'Found Animal')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (5, 'Waiting List')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksdiarylink VALUES (6, 'Movement')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("lksdiarylink: Table creation");
            }

            try {
                // Lookup table for urgency
                String sql = "CREATE TABLE lkurgency (" +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "Urgency varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ") Type=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lkurgency VALUES (1, 'Urgent')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lkurgency VALUES (2, 'High')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lkurgency VALUES (3, 'Medium')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lkurgency VALUES (4, 'Low')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lkurgency VALUES (5, 'Lowest')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("lkurgency: Table creation");
            }

            // Retailer flag for owners
            try {
                String sql = "ALTER TABLE owner ADD IsRetailer tinyint NOT NULL DEFAULT '0';";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("owner: Addition of IsRetailer flag");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1101() {
        try {
            // Add custom report category
            try {
                String sql = "ALTER TABLE customreport ADD Category varchar(100) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("customreport: Addition of category field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1102() {
        try {
            // Add custom report category
            try {
                String sql = "ALTER TABLE animal ADD ActiveMovementID int(11) NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD ActiveMovementDate datetime NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD ActiveMovementType smallint NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD ActiveMovementReturn datetime NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD RabiesTag varchar(20) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "animal: Adding movement cache fields and rabies tag");
            }

            Animal a = new Animal();
            a.openRecordset("");
            Global.logInfo("Updating animal records...", "AutoDBUpdates");

            while (!a.getEOF()) {
                Animal.updateAnimalStatus(a.getID().intValue());
                a.moveNext();
            }

            a.free();
            a = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1103() {
        try {
            // Change config entries for cat/dogs to primary
            // and secondary.
            try {
                Configuration.changeKeyName("AFCatSpecies", "AFPrimarySpecies");
                Configuration.changeKeyName("AFUnwantedCatAnimalType",
                    "AFPrimaryUnwantedType");
                Configuration.changeKeyName("AFStrayCatAnimalType",
                    "AFPrimaryStrayType");
                Configuration.changeKeyName("AFDogSpecies", "AFSecondarySpecies");
                Configuration.changeKeyName("AFUnwantedDogAnimalType",
                    "AFSecondaryUnwantedType");
                Configuration.changeKeyName("AFStrayDogAnimalType",
                    "AFSecondaryStrayType");
            } catch (Exception e) {
                errors.add("configuration: Changing report keys");
            }

            // Add IsFosterer flag
            try {
                String sql = "ALTER TABLE owner ADD IsFosterer tinyint NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);

                Owner o = new Owner();
                o.openRecordset("");
                Global.logInfo("Updating owner foster records...",
                    "AutoDBUpdates");

                while (!o.getEOF()) {
                    try {
                        Adoption ad = new Adoption();
                        ad.openRecordset("OwnerID = " + o.getID() +
                            " AND MovementType = " + Adoption.MOVETYPE_FOSTER);

                        if (!ad.getEOF()) {
                            DBConnection.executeAction(
                                "UPDATE owner SET IsFosterer = 1 WHERE ID = " +
                                o.getID());
                        }

                        ad.free();
                        ad = null;
                    } catch (Exception e) {
                        errors.add("owner " + o.getID() +
                            ": checking fostered status");
                    }

                    o.moveNext();
                }

                o.free();
                o = null;
            } catch (Exception e) {
                errors.add("owner: Addition of IsFosterer flag");
            }

            // Extend Original Owner telephone field (OOPhone)
            try {
                String sql = "ALTER TABLE animal MODIFY OOPhone varchar(50) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: Extension of Original Owner Phone");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1111() {
        try {
            // Add entry reason to animal and return reason to
            // owner.
            try {
                String sql = "ALTER TABLE animal ADD EntryReasonID int NOT NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE adoption ADD ReturnedReasonID int NOT NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD PTSReasonID int NOT NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "animal/movement: Brought In/Returned reason and PTS Reason");
            }

            // Create the entry reason table
            try {
                String sql = "CREATE TABLE entryreason ( " +
                    "ID int(11) NOT NULL default '0'," +
                    "ReasonName varchar(255) NOT NULL default ''," +
                    "ReasonDescription varchar(255) default NULL," +
                    "PRIMARY KEY  (ID)" + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (1, 'Marriage/Relationship split', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (2, 'Allergies', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (3, 'Biting', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (4, 'Unable to Cope', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (5, 'Unsuitable Accomodation', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (6, 'Died', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (7, 'Stray', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (8, 'Sick/Injured', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (9, 'Unable to Afford', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO entryreason VALUES (10, 'Abuse', '')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("entryreason: Initial table creation");
            }

            // Death reason table
            try {
                String sql = "CREATE TABLE deathreason (" +
                    "  ID int(11) NOT NULL default '0'," +
                    "  ReasonName varchar(255) NOT NULL default ''," +
                    "  ReasonDescription varchar(255) default NULL," +
                    "  PRIMARY KEY  (ID)" + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (1, 'Dead On Arrival', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (2, 'Died', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (3, 'Healthy', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (4, 'Sick/Injured', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (5, 'Requested', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (6, 'Culling', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (7, 'Feral', '')";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO deathreason VALUES (8, 'Biting', '')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("deathreason: Initial table creation");
            }

            // Update animal and movement records to use the
            // new scheme.
            try {
                Animal a = new Animal();
                a.openRecordset("");
                Global.logInfo("Updating animal death and entry reasons...",
                    "AutoDBUpdates");

                while (!a.getEOF()) {
                    try {
                        // Check the animal's pts reasons against the list
                        int animalDeathReasonID = 0;

                        if (a.getDeceasedDate() != null) {
                            if (a.getIsDOA().intValue() == 1) {
                                animalDeathReasonID = 1;
                            } else if (a.getPutToSleep().intValue() == 0) {
                                animalDeathReasonID = 2;
                            } else if (a.getPTSReason().indexOf("healthy") != -1) {
                                animalDeathReasonID = 3;
                            } else if ((a.getPTSReason().indexOf("injured") != -1) ||
                                    (a.getPTSReason().indexOf("sick") != -1)) {
                                animalDeathReasonID = 4;
                            } else if (a.getPTSReason().indexOf("requested") != -1) {
                                animalDeathReasonID = 5;
                            } else if (a.getPTSReason().indexOf("culling") != -1) {
                                animalDeathReasonID = 6;
                            } else if (a.getPTSReason().indexOf("feral") != -1) {
                                animalDeathReasonID = 7;
                            } else if (a.getPTSReason().indexOf("bit") != -1) {
                                animalDeathReasonID = 8;
                            } else {
                                animalDeathReasonID = 4;
                            }
                        }

                        // Update the animal record
                        String sql = "UPDATE animal SET PTSReasonID = " +
                            animalDeathReasonID + " WHERE ID = " + a.getID();
                        DBConnection.executeAction(sql);

                        // Check the animal's entry reasons against the list
                        int entryReasonID = 0;
                        String fld = a.getReasonForEntry();

                        if (a.getAnimalTypeID().intValue() == Configuration.getInteger(
                                    "AFCaseAnimalType")) {
                            entryReasonID = 10;
                        } else if ((fld.indexOf("marriage") != -1) ||
                                (fld.indexOf("relation") != -1)) {
                            entryReasonID = 1;
                        } else if (fld.indexOf("allerg") != -1) {
                            entryReasonID = 2;
                        } else if (fld.indexOf("_bit") != -1) {
                            entryReasonID = 3;
                        } else if (fld.indexOf("cope") != -1) {
                            entryReasonID = 4;
                        } else if (fld.indexOf("accomodation") != -1) {
                            entryReasonID = 5;
                        } else if ((fld.indexOf("died") != -1) ||
                                (fld.indexOf("death") != -1)) {
                            entryReasonID = 6;
                        } else if (fld.indexOf("stray") != -1) {
                            entryReasonID = 7;
                        } else if (fld.indexOf("injur") != -1) {
                            entryReasonID = 8;
                        } else if (fld.indexOf("afford") != -1) {
                            entryReasonID = 9;
                        } else {
                            entryReasonID = 4;
                        }

                        // Update the animal record
                        sql = "UPDATE animal SET EntryReasonID = " +
                            entryReasonID + " WHERE ID = " + a.getID();
                        DBConnection.executeAction(sql);

                        // Look for returned movements and flag those
                        Adoption ad = new Adoption();
                        ad.openRecordset("AnimalID = " + a.getID() +
                            " AND ReturnDate Is Not Null");

                        while (!ad.getEOF()) {
                            // Check the movement's return reasons against the
                            // list
                            entryReasonID = 0;
                            fld = ad.getReasonForReturn();

                            if (a.getAnimalTypeID()
                                     .equals(Configuration.getString(
                                            "AFCaseAnimalType"))) {
                                entryReasonID = 10;
                            } else if ((fld.indexOf("marriage") != -1) ||
                                    (fld.indexOf("relation") != -1)) {
                                entryReasonID = 1;
                            } else if (fld.indexOf("allerg") != -1) {
                                entryReasonID = 2;
                            } else if (fld.indexOf("_bit") != -1) {
                                entryReasonID = 3;
                            } else if (fld.indexOf("cope") != -1) {
                                entryReasonID = 4;
                            } else if (fld.indexOf("accomodation") != -1) {
                                entryReasonID = 5;
                            } else if ((fld.indexOf("died") != -1) ||
                                    (fld.indexOf("death") != -1)) {
                                entryReasonID = 6;
                            } else if (fld.indexOf("stray") != -1) {
                                entryReasonID = 7;
                            } else if (fld.indexOf("injur") != -1) {
                                entryReasonID = 8;
                            } else if (fld.indexOf("afford") != -1) {
                                entryReasonID = 9;
                            } else {
                                entryReasonID = 4;
                            }

                            // Update the movement record
                            sql = "UPDATE adoption SET ReturnedReasonID = " +
                                entryReasonID + " WHERE ID = " + ad.getID();
                            DBConnection.executeAction(sql);

                            ad.moveNext();
                        }

                        ad.free();
                        ad = null;
                    } catch (Exception e) {
                        errors.add("animal " + a.getID() +
                            ": checking and upating entry/death reasons.");
                        Global.logException(e, getClass());
                    }

                    a.moveNext();
                }

                a.free();
                a = null;
            } catch (Exception e) {
                errors.add("animal: Bulk update reason codes.");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1112() {
        try {
            // Add town and county fields to owner table.
            // Also, add denormalised fields for OwnerTitle,
            // OwnerInitials, OwnerForenames and OwnerSurname
            try {
                String sql = "ALTER TABLE owner ADD OwnerTitle varchar(50) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD OwnerInitials varchar(50) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD OwnerForenames varchar(200) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD OwnerSurname varchar(100) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD OwnerTown varchar(100) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD OwnerCounty varchar(100) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("owner: Addition of name elements and town/county");
            }

            // Update owner records to cache name element
            try {
                Owner o = new Owner();
                o.openRecordset("");
                Global.logInfo("Updating owner cached name elements...",
                    "AutoDBUpdates");

                while (!o.getEOF()) {
                    // Update the owner record
                    try {
                        String sql = "UPDATE owner SET " + "OwnerTitle = '" +
                            o.getOwnerTitle() + "', " + "OwnerInitials = '" +
                            o.getOwnerInitials() + "', " +
                            "OwnerForenames = '" + o.getOwnerForenames() +
                            "', " + "OwnerSurname = '" + o.getOwnerSurname() +
                            "' " + "WHERE ID = " + o.getID();
                        DBConnection.executeAction(sql);
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }

                    o.moveNext();
                }

                o.free();
                o = null;
            } catch (Exception e) {
                errors.add("owner: Bulk update owner name elements.");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1113() {
        try {
            try {
                // Add date due to owner donation table
                String sql = "ALTER TABLE ownerdonation ADD DateDue datetime NULL";
                DBConnection.executeAction(sql);
                // Add make existing date received on owner donation
                // nullable.
                sql = "ALTER TABLE ownerdonation MODIFY Date datetime NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "ownerdonation: Addition of date due and nulling of date");
            }

            try {
                String sql = "ALTER TABLE species ADD PetFinderSpecies varchar(100) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("species: Addition of Petfinder mapping");
            }

            // Map the breeds across if the user wants it
            if (Dialog.showYesNo("From this version, ASM defaults to use the " +
                        "PetFinder breed listings,\nwhich are more complete than ASM's original set. \n" +
                        "I can merge them in to your existing breeds - \nwould you like to do this?",
                        "Additional Breeds")) {
                ArrayList<String> pfBreeds = DBPetFinder.getBreeds();

                Global.logInfo("Merging breeds...", "AutoDBUpdates");

                Iterator i = pfBreeds.iterator();
                SQLRecordset b = new SQLRecordset();
                b.openRecordset("SELECT * FROM breed WHERE ID = 0", "breed");

                for (String breed : pfBreeds) {
                    breed = breed.replace('\'', '`');

                    SQLRecordset rs = new SQLRecordset();
                    rs.openRecordset(
                        "SELECT * FROM breed WHERE BreedName Like '" + breed +
                        "'", "breed");

                    if (rs.getEOF()) {
                        b.addNew();
                        b.setField("ID",
                            new Integer(DBConnection.getPrimaryKey("breed")));
                        b.setField("BreedName", breed);
                    }

                    rs.free();
                    rs = null;
                }

                b.save(false, "");
                b.free();
                b = null;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1114() {
        try {
            // Add new field to breed table for petfinder mapping
            try {
                String sql = "ALTER TABLE breed ADD PetFinderBreed varchar(100) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("breed: Addition of Petfinder mapping");
            }

            // Map PetFinder Breeds to Existing Breeds
            ArrayList<String> pfBreeds = DBPetFinder.getBreeds();

            Global.logInfo("Mapping breeds...", "AutoDBUpdates");

            for (String breed : pfBreeds) {
                DBConnection.executeAction(
                    "UPDATE breed SET PetFinderBreed = 'breed' " +
                    "WHERE BreedName Like '" + breed.replace('\'', '`') + "'");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1121() {
        try {
            // Add custom report entries for all the standard
            // reports:
            CustomReport cr = new CustomReport();
            cr.openRecordset("ID = 0");

            cr.addNew();
            cr.setCategory("Auditing");
            cr.setTitle("Non-Microchipped Animals");
            cr.setSQLCommand("001");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Auditing");
            cr.setTitle("Animals Without Photo Media");
            cr.setSQLCommand("002");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Auditing");
            cr.setTitle("Animals Never Vaccinated");
            cr.setSQLCommand("003");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Auditing");
            cr.setTitle("Non-Neutered/Spayed Animals Aged Over 6 Months");
            cr.setSQLCommand("004");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Auditing");
            cr.setTitle("Cats Not Combi-Tested");
            cr.setSQLCommand("005");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Figures");
            cr.setTitle("Monthly Animal Figures");
            cr.setSQLCommand("006");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Figures");
            cr.setTitle("Long Term Animals");
            cr.setSQLCommand("007");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Inventories");
            cr.setTitle("Shelter Inventory");
            cr.setSQLCommand("008");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Inventories");
            cr.setTitle("Detailed Shelter Inventory");
            cr.setSQLCommand("009");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Litters");
            cr.setTitle("Animals Not Part Of A Litter");
            cr.setSQLCommand("010");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movements");
            cr.setTitle("In/Out");
            cr.setSQLCommand("011");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movements");
            cr.setTitle("In/Out Summary");
            cr.setSQLCommand("012");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movements");
            cr.setTitle("Transfer In Report");
            cr.setSQLCommand("013");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Retailers");
            cr.setTitle("Volume Of Adoptions Per Retailer");
            cr.setSQLCommand("014");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Retailers");
            cr.setTitle("Average Time At Retailer Before Adoption");
            cr.setSQLCommand("015");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Retailers");
            cr.setTitle("Retailer Inventory");
            cr.setSQLCommand("016");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Returns");
            cr.setTitle("Returned Animals Report");
            cr.setSQLCommand("017");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Returns");
            cr.setTitle("Animals Returned Within 6 Months");
            cr.setSQLCommand("018");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Returns");
            cr.setTitle("Animals Returned After 6 Months");
            cr.setSQLCommand("019");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Statistics");
            cr.setTitle("Most Common Name");
            cr.setSQLCommand("020");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Statistics");
            cr.setTitle("Animal Death Reasons");
            cr.setSQLCommand("021");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Statistics");
            cr.setTitle("Common Animal Entry Areas");
            cr.setSQLCommand("022");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Statistics");
            cr.setTitle("Common Animal Adoption Areas");
            cr.setSQLCommand("023");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Statistics");
            cr.setTitle("Average Time On Waiting List");
            cr.setSQLCommand("024");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Financial Graphs");
            cr.setTitle("Monthly Donations");
            cr.setSQLCommand("025");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Financial Graphs");
            cr.setTitle("Monthly Donations By Species");
            cr.setSQLCommand("026");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movement Graphs");
            cr.setTitle("Monthly Adoptions By Species");
            cr.setSQLCommand("027");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movement Graphs");
            cr.setTitle("Monthly Adoptions By Location");
            cr.setSQLCommand("028");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movement Graphs");
            cr.setTitle("Animal Entry Reasons");
            cr.setSQLCommand("029");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.addNew();
            cr.setCategory("Movement Graphs");
            cr.setTitle("Animal Return Reasons");
            cr.setSQLCommand("030");
            cr.setHTMLBody("");
            cr.setDescription("");

            cr.save("asmupdate");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1122() {
        try {
            try {
                // Add retailer ID to movement table
                String sql = "ALTER TABLE adoption ADD RetailerID int NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("adoption: Addition of RetailerID");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1123() {
        try {
            try {
                // Add new fields to vaccination table for medical
                String sql = "ALTER TABLE animalvaccination ADD AnimalLocation varchar(255) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination ADD MedicalLocation varchar(255) NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination ADD NumberOfTreatmentsRequired smallint NOT NULL default '1'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination ADD NumberOfTreatmentsGiven smallint NOT NULL default '1'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animalvaccination: Addition of medical fields");
            }

            try {
                String sql = "UPDATE animalvaccination SET NumberOfTreatmentsGiven = 0 WHERE DateOfVaccination Is Null";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "animalvaccination: Updating existing medical records");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1124() {
        try {
            try {
                // Add new fields to vaccination table for medical
                String sql = "ALTER TABLE animalvaccination ADD MedicalType smallint NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animalvaccination: Addition of medical fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1125() {
        try {
            try {
                // Drop all extra fields from vaccination
                String sql = "ALTER TABLE animalvaccination DROP COLUMN AnimalLocation";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination DROP COLUMN MedicalLocation";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination DROP COLUMN NumberOfTreatmentsRequired";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination DROP COLUMN NumberOfTreatmentsGiven";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalvaccination DROP COLUMN MedicalType";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animalvaccination: Removal of medical fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1131() {
        try {
            try {
                // Comment fields to lost/found
                String sql = "ALTER TABLE animallost ADD Comments TEXT NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalfound ADD Comments TEXT NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animallost/animalfound: Addition of comment fields");
            }

            try {
                // Add owner ID fields for places in system
                String sql = "ALTER TABLE animal ADD OriginalOwnerID int NOT NULL Default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD BroughtInByOwnerID int NOT NULL Default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalwaitinglist ADD OwnerID int NOT NULL Default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animallost ADD OwnerID int NOT NULL Default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalfound ADD OwnerID int NOT NULL Default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "animal, lost/found and waitinglist: creation of owner link fields to oo and np");
            }

            try {
                // Run through records in following places, creating owner
                // records and stamping links

                // Get all data together first so we can show the
                // user a total progress
                SQLRecordset a = new SQLRecordset();
                a.openRecordset("SELECT * FROM animal", "animal");

                SQLRecordset awl = new SQLRecordset();
                awl.openRecordset("SELECT * FROM animalwaitinglist",
                    "animalwaitinglist");

                SQLRecordset al = new SQLRecordset();
                al.openRecordset("SELECT * FROM animallost", "animallost");

                SQLRecordset af = new SQLRecordset();
                af.openRecordset("SELECT * FROM animalfound", "animalfound");

                int total = (int) (a.getRecordCount() + awl.getRecordCount() +
                    al.getRecordCount() + af.getRecordCount());
                Global.logInfo("Centralising entity records...", "AutoDBUpdates");

                // ANIMAL RECORDS - OO and NP
                while (!a.getEOF()) {
                    a.setField("OriginalOwnerID",
                        addOwner(a.getField("OOName").toString(),
                            a.getField("OOAddress").toString(),
                            a.getField("OOPostcode").toString(),
                            a.getField("OOPhone").toString()));
                    a.setField("BroughtInByOwnerID",
                        addOwner(a.getField("NPBroughtIn").toString(),
                            a.getField("NPAddress").toString(),
                            a.getField("NPPostcode").toString(), ""));
                    a.moveNext();
                }

                // Save
                a.save(true, "update");

                // WAITING LIST RECORDS
                while (!awl.getEOF()) {
                    awl.setField("OwnerID",
                        addOwner(awl.getField("Name").toString(),
                            awl.getField("Address").toString(), "",
                            awl.getField("PhoneNumber").toString()));
                    awl.moveNext();
                }

                // Save
                awl.save(true, "update");

                // LOST ANIMAL RECORDS
                while (!al.getEOF()) {
                    al.setField("OwnerID",
                        addOwner(al.getField("ContactName").toString(), "", "",
                            al.getField("ContactNumber").toString()));
                    al.moveNext();
                }

                // Save
                al.save(true, "update");

                // FOUND ANIMAL RECORDS
                while (!af.getEOF()) {
                    af.setField("OwnerID",
                        addOwner(af.getField("ContactName").toString(), "", "",
                            af.getField("ContactNumber").toString()));
                    af.moveNext();
                }

                // Save
                af.save(true, "update");

                // Free up recordsets
                a.free();
                awl.free();
                al.free();
                af.free();
                a = null;
                awl = null;
                al = null;
                af = null;
            } catch (Exception e) {
                errors.add(
                    "owner: creating owner records from waiting list, lost/found and animal");
            }

            try {
                // Drop original columns being replaced
                String sql = "ALTER TABLE animal DROP COLUMN OOName";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal DROP COLUMN OOAddress";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal DROP COLUMN OOPostcode";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal DROP COLUMN OOPhone";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal DROP COLUMN NPBroughtIn";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal DROP COLUMN NPAddress";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal DROP COLUMN NPPostcode";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalwaitinglist DROP COLUMN Name";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalwaitinglist DROP COLUMN Address";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalwaitinglist DROP COLUMN PhoneNumber";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animallost DROP COLUMN ContactName";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animallost DROP COLUMN ContactNumber";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalfound DROP COLUMN ContactName";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalfound DROP COLUMN ContactNumber";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal lost/found wl: Removal of address fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Adds a new owner record given a name, address, postcode and phone no
     *
     * @param name
     * @param address
     * @param postcode
     * @param phone
     * @return The ID of the created record as a string, or a string containing
     *         "0" if the owner name was blank or null.
     */
    private String addOwner(String name, String address, String postcode,
        String phone) {
        String id = "0";

        if ((name == null) || name.trim().equals("")) {
            return "0";
        }

        // Replace apostrophes with backwards apostrophes for the
        // database
        name = Utils.replace(name, "'", "`");

        try {
            SQLRecordset rs = new SQLRecordset();

            // Check for an existing owner with these details
            // to try and prevent duplicates
            rs.openRecordset("SELECT ID FROM owner WHERE " +
                "TRIM(OwnerName) Like '" + name.trim() + "'", "owner");

            if (!rs.getEOF()) {
                return rs.getField("ID").toString();
            }

            // Doesn't exist - create it
            rs.openRecordset("SELECT * FROM owner WHERE ID = 0", "owner");
            rs.addNew();
            rs.setField("ID", new Integer(DBConnection.getPrimaryKey("owner")));
            id = rs.getField("ID").toString();
            rs.setField("OwnerName", name);
            rs.setField("OwnerAddress", address);
            rs.setField("OwnerPostcode", postcode);
            rs.setField("HomeTelephone", phone);
            rs.setField("IDCheck", "0");
            rs.setField("IsAco", "0");
            rs.setField("IsBanned", "0");
            rs.setField("IsDonor", "0");
            rs.setField("IsFosterer", "0");
            rs.setField("IsHomeChecker", "0");
            rs.setField("IsMember", "0");
            rs.setField("IsRetailer", "0");
            rs.setField("IsShelter", "0");
            rs.setField("IsStaff", "0");
            rs.setField("IsVolunteer", "0");
            rs.setField("RecordVersion", "1");
            rs.save(true, "update");
            rs.free();
            rs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return id;
    }

    public void update1201() {
        try {
            try {
                // Add diet lookup table
                String sql = "CREATE TABLE diet (" +
                    "ID int(11) NOT NULL default '0', " +
                    "DietName varchar(255) NOT NULL default '', " +
                    "DietDescription varchar(255) default NULL, " +
                    "PRIMARY KEY  (ID) " + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "INSERT INTO diet VALUES (1, 'Standard', '')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("diet: table creation.");
            }

            try {
                // Add animaldiet table
                String sql = "CREATE TABLE animaldiet ( " +
                    "ID int(11) NOT NULL default '0', " +
                    "AnimalID int(11) NOT NULL default '0', " +
                    "DietID int(11) NOT NULL default '0', " +
                    "DateStarted datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "Comments TEXT NULL default '', " +
                    "RecordVersion int NOT NULL default '0', " +
                    "CreatedBy varchar(255) NOT NULL default '', " +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "LastChangedBy varchar(255) NOT NULL default '', " +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "PRIMARY KEY  (ID), " + "KEY IX_AnimalID (AnimalID), " +
                    "KEY IX_DietID (DietID) " + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animaldiet: table creation");
            }

            try {
                // voucher table
                String sql = "CREATE TABLE voucher ( " +
                    "ID int(11) NOT NULL default '0', " +
                    "VoucherName varchar(255) NOT NULL default '', " +
                    "VoucherDescription varchar(255) default NULL, " +
                    "PRIMARY KEY  (ID) " + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "INSERT INTO voucher VALUES (1, 'Neuter/Spay', '')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("voucher: table creation");
            }

            try {
                // ownervoucher table
                String sql = "CREATE TABLE ownervoucher (" +
                    "ID int(11) NOT NULL default '0', " +
                    "OwnerID int(11) NOT NULL default '0', " +
                    "VoucherID int(11) NOT NULL default '0', " +
                    "DateIssued datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "DateExpired datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "Value double NOT NULL default '0', " +
                    "Comments text default NULL, " +
                    "RecordVersion int NOT NULL default '0', " +
                    "CreatedBy varchar(255) NOT NULL default '', " +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "LastChangedBy varchar(255) NOT NULL default '', " +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00', " +
                    "PRIMARY KEY  (ID), " + "KEY IX_OwnerID (OwnerID), " +
                    "KEY IX_VoucherID (VoucherID), " +
                    "KEY IX_DateExpired (DateExpired) " + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("ownervoucher: table creation");
            }

            try {
                // Add non-shelter flag to animal
                String sql = "ALTER TABLE animal ADD NonShelterAnimal tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: Addition of nonshelteranimal flag");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1202() {
        try {
            // All this update does is check to see if you installed
            // on 1.12 (and hence had a bad custom report set). If you
            // did, this wipes your custom report table and reruns
            // update 1.121
            try {
                // Look for a year in a category
                CustomReport cr = new CustomReport();
                cr.openRecordset("Category Like '2003-%'");

                if (!cr.getEOF()) {
                    cr.free();
                    cr = null;

                    // Only kill standard reports
                    String sql = "DELETE FROM customreport WHERE SQLCommand Like '0%'";
                    DBConnection.executeAction(sql);

                    // Recreate them
                    update1121();
                }
            } catch (Exception e) {
                errors.add("customreport: repair from bad 1.12");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1203() {
        try {
            try {
                String sql = "INSERT INTO animaltype VALUES (40, 'N (Non-Shelter Animal)', NULL)";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO configuration VALUES ('AFNonShelterType', '40')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("config: Creation and setting of non-shelter type");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1204() {
        try {
            try {
                String sql = "CREATE TABLE animalmedical ( " +
                    "ID int(11) NOT NULL default '0'," +
                    "AnimalID int(11) NOT NULL default '0'," +
                    "MedicalProfileID int(11) NOT NULL default '0'," +
                    "TreatmentName varchar(255) NOT NULL default ''," +
                    "StartDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "Dosage varchar(255) NULL," +
                    "TimingRule tinyint NOT NULL default '0'," +
                    "TimingRuleFrequency smallint NOT NULL default '0'," +
                    "TimingRuleNoFrequencies smallint NOT NULL default '0'," +
                    "TreatmentRule tinyint NOT NULL default '0'," +
                    "TotalNumberOfTreatments smallint NOT NULL default '0'," +
                    "TreatmentsGiven smallint NOT NULL default '0'," +
                    "TreatmentsRemaining smallint NOT NULL default '0'," +
                    "Status smallint NOT NULL default '0'," +
                    "Comments TEXT NULL," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + "KEY IX_AnimalID (AnimalID)," +
                    "KEY IX_MedicalProfileID (MedicalProfileID)" +
                    ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "CREATE TABLE animalmedicaltreatment (" +
                    "ID int(11) NOT NULL default '0'," +
                    "AnimalID int(11) NOT NULL default '0'," +
                    "AnimalMedicalID int(11) NOT NULL default '0'," +
                    "DateRequired datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "DateGiven datetime NULL," +
                    "GivenBy varchar(100) NOT NULL," + "Comments TEXT NULL," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + "KEY IX_AnimalID (AnimalID)," +
                    "KEY IX_AnimalMedicalID (AnimalMedicalID)," +
                    "KEY IX_DateRequired (DateRequired)" + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "CREATE TABLE medicalprofile (" +
                    "ID int(11) NOT NULL default '0'," +
                    "ProfileName varchar(255) NOT NULL default ''," +
                    "TreatmentName varchar(255) NOT NULL default ''," +
                    "Dosage varchar(255) NULL," +
                    "TimingRule tinyint NOT NULL default '0'," +
                    "TimingRuleFrequency smallint NOT NULL default '0'," +
                    "TimingRuleNoFrequencies smallint NOT NULL default '0'," +
                    "TreatmentRule tinyint NOT NULL default '0'," +
                    "TotalNumberOfTreatments smallint NOT NULL default '0'," +
                    "Comments TEXT NULL," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("medical tables: creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1205() {
        try {
            try {
                // For some bizarre reason, this table didn't
                // create on some shelters.
                String sql = "CREATE TABLE medicalprofile (" +
                    "ID int(11) NOT NULL default '0'," +
                    "ProfileName varchar(255) NOT NULL default ''," +
                    "TreatmentName varchar(255) NOT NULL default ''," +
                    "Dosage varchar(255) NULL," +
                    "TimingRule tinyint NOT NULL default '0'," +
                    "TimingRuleFrequency smallint NOT NULL default '0'," +
                    "TimingRuleNoFrequencies smallint NOT NULL default '0'," +
                    "TreatmentRule tinyint NOT NULL default '0'," +
                    "TotalNumberOfTreatments smallint NOT NULL default '0'," +
                    "Comments TEXT NULL," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                // Ignore errors - some people will already have it
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1211() {
        try {
            try {
                // Add TreatmentNumber and TotalTreatments
                // to AnimalMedicalTreatment table
                String sql = "ALTER TABLE animalmedicaltreatment ADD TreatmentNumber int NOT NULL default '1'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animalmedicaltreatment ADD TotalTreatments int NOT NULL default '1'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animalmedicaltreatment: x of x fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1212() {
        try {
            try {
                String sql = "CREATE TABLE log (" +
                    "ID int(11) NOT NULL default '0'," +
                    "LogTypeID int(11) NOT NULL default '0'," +
                    "LinkID int(11) NOT NULL default '0'," +
                    "LinkType smallint NOT NULL default '0'," +
                    "Date datetime NOT NULL," +
                    "Comments TEXT NOT NULL default ''," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," + "KEY IX_LogTypeID (LogTypeID)," +
                    "KEY IX_LinkID (LinkID)" + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "CREATE TABLE logtype (" +
                    "ID int(11) NOT NULL default '0'," +
                    "LogTypeName varchar(255) NOT NULL default ''," +
                    "LogTypeDescription varchar(255) NULL," +
                    "PRIMARY KEY  (ID)" + ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "INSERT INTO logtype VALUES (1, 'Bite', '');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO logtype VALUES (2, 'Complaint', '');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO logtype VALUES (3, 'History', '');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO logtype VALUES (4, 'Weight', '');";
                DBConnection.executeAction(sql);

                sql = "CREATE TABLE lksloglink (" +
                    "ID smallint NOT NULL DEFAULT '0'," +
                    "LinkType varchar(40) NOT NULL," + "PRIMARY KEY  (ID)" +
                    ") Type=MyISAM;";
                DBConnection.executeAction(sql);

                sql = "INSERT INTO lksloglink VALUES (0, 'Animal');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksloglink VALUES (1, 'Owner');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksloglink VALUES (2, 'Lost Animal');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksloglink VALUES (3, 'Found Animal');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksloglink VALUES (4, 'Waiting List');";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO lksloglink VALUES (5, 'Movement');";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("log: table creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1213() {
        try {
            try {
                // Filter field for diary tasks - owner/animal
                String sql = "ALTER TABLE diarytaskhead ADD RecordType smallint NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("diarytaskhead: addition of RecordType field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1221() {
        try {
            try {
                Global.logInfo("Setting preferred media...", "AutoDBUpdates");

                // Update every media record and make sure each
                // animal has a preferred media if it has
                // a photo
                Animal a = new Animal();
                a.openRecordset("");

                while (!a.getEOF()) {
                    // Animal have a web preferred media?
                    Media m = new Media();
                    m.openRecordset("LinkTypeID = " + Media.LINKTYPE_ANIMAL +
                        " AND LinkID = " + a.getID() + " AND WebsitePhoto = 1");

                    // If not, does it have any media?
                    if (m.getEOF()) {
                        m.openRecordset("LinkTypeID = " +
                            Media.LINKTYPE_ANIMAL + " AND LinkID = " +
                            a.getID());

                        // If so, find the first image and mark it as web
                        // preferred
                        while (!m.getEOF()) {
                            if (Utils.englishLower(m.getMediaName())
                                         .indexOf(".jpg") != -1) {
                                try {
                                    DBConnection.executeAction(
                                        "UPDATE media SET WebsitePhoto=1 " +
                                        "WHERE ID=" + m.getID());
                                } catch (Exception e) {
                                    errors.add("Updating media ID=" +
                                        m.getID());
                                }
                            }

                            m.moveNext();
                        }
                    }

                    a.moveNext();
                }
            } catch (Exception e) {
                errors.add("website media: forcing of preferred field");
            }

            // Add "HasActiveReserve" field to animal table
            try {
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD HasActiveReserve tinyint NOT NULL default 0");
            } catch (Exception e) {
                errors.add("animal: Addition of HasActiveReserve field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1321() {
        try {
            try {
                // Declawed field
                String sql = "ALTER TABLE animal ADD Declawed tinyint(4) NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: addition of declawed field");
            }

            try {
                // Membership expiry date
                String sql = "ALTER TABLE owner ADD MembershipExpiryDate datetime NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("owner: addition of MembershipExpiryDate field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1341() {
        try {
            try {
                // Declawed field
                String sql = "ALTER TABLE media ADD Date datetime NOT NULL default '0000-00-00 00:00'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("media: addition of Date field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1351() {
        try {
            try {
                // Field to track the original retailer movement that generated
                // an adoption
                String sql = "ALTER TABLE adoption ADD OriginalRetailerMovementID int NOT NULL DEFAULT '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "adoption: addition of OriginalRetailerMovementID field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1352() {
        try {
            try {
                // Mobile phone field
                String sql = "ALTER TABLE owner ADD MobileTelephone varchar(255) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("owner: addition of MobileTelephone field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1361() {
        try {
            try {
                // Mobile phone field
                String sql = "ALTER TABLE animal ADD CombiTestResult tinyint NOT NULL Default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: addition of CombiTestResult field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1362() {
        try {
            // Copy the organisation name from the settings table to
            // the configuration table
            String org = "";

            try {
                SQLRecordset rs = new SQLRecordset();
                rs.openRecordset("Select * From settings", "settings");
                org = rs.getField("RegisteredTo").toString();
            } catch (Exception e) {
                errors.add("settings: reading organisation name");
            }

            try {
                Configuration.setEntry("Organisation", org);
            } catch (Exception e) {
                errors.add("configuration: setting organisation");
            }

            try {
                String sql = "DROP TABLE settings";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("settings: removal of table");
            }

            try {
                String sql = "ALTER TABLE animal ADD Tattoo tinyint(4) NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD TattooNumber varchar(255) default NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: addition of tattoo fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1363() {
        try {
            try {
                // FLV tracking field
                String sql = "ALTER TABLE animal ADD FLVResult tinyint NOT NULL Default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: addition of FLVResult field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1364() {
        try {
            try {
                // AdoptionDonation table
                String sql = "CREATE TABLE adoptiondonation (" +
                    "ID int(11) NOT NULL default '0'," +
                    "AdoptionID int(11) NOT NULL default '0'," +
                    "Date datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "Donation double NOT NULL default '0'," +
                    "Comments text default NULL," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY (ID)," + "KEY IX_AdoptionID (AdoptionID)" +
                    ") TYPE=MyISAM;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("creation of adoptiondonation table");
            }

            // Copy existing adoption figures into this new table
            try {
                Global.logInfo("Creating adoption donation records...",
                    "AutoDBUpdates");

                Adoption a = new Adoption();
                String today = SQLRecordset.getSQLRepresentationOfDate(new Date());
                a.openRecordset("");

                while (!a.getEOF()) {
                    if (a.getDonation() != null) {
                        if (a.getDonation().doubleValue() > 0) {
                            DBConnection.executeAction(
                                "INSERT INTO adoptiondonation (ID, AdoptionID, Date, Donation, Comments, RecordVersion, " +
                                "CreatedBy, CreatedDate, LastChangedBy, LastChangedDate) VALUES (" +
                                DBConnection.getPrimaryKey("adoptiondonation") +
                                ", " + a.getID() + ", " + "'" +
                                ((a.getMovementDate() == null) ? today
                                                               : SQLRecordset.getSQLRepresentationOfDate(
                                    a.getMovementDate())) + "', " +
                                a.getDonation() + ", " +
                                "'Adoption donation', " + "1, 'upgrade', '" +
                                today + "', 'upgrade', '" + today + "')");
                            System.out.print(".");
                        }
                    }

                    a.moveNext();
                }

                a.free();
                System.out.println(" done.");
            } catch (Exception e) {
                errors.add("transferring adoption values");
            }

            try {
                // Identichip date field and Tattoo date field
                String sql = "ALTER TABLE animal ADD IdentichipDate datetime default NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD TattooDate datetime default NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: identichipdate and tattoodate fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1371() {
        try {
            try {
                String sql = "ALTER TABLE animal ADD IsGoodWithCats tinyint(4) default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD IsGoodWithDogs tinyint(4) default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD IsGoodWithChildren tinyint(4) default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD IsHouseTrained tinyint(4) default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "animal: Addition of good with cats/dogs/kids/housetrained flags");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1372() {
        try {
            try {
                String sql = "ALTER TABLE animal ADD HeartwormTested tinyint(4) NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD HeartwormTestDate datetime default NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE animal ADD HeartwormTestResult tinyint(4) NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: Addition of heartworm test fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1381() {
        try {
            try {
                String sql = "ALTER TABLE adoptiondonation ADD DonationTypeID int(11) NOT NULL default '1'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE ownerdonation ADD DonationTypeID int(11) NOT NULL default '1'";
                DBConnection.executeAction(sql);
                sql = "CREATE TABLE donationtype ( ID int(11) NOT NULL default '0', DonationName varchar(255) NOT NULL default '', DonationDescription varchar(255) default NULL,  PRIMARY KEY  (ID) ) TYPE=MyISAM;";
                DBConnection.executeAction(sql);
                sql = "INSERT INTO donationtype VALUES (1, 'Donation', '')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("donations: category fields and lookup table");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1382() {
        try {
            try {
                // We only need to fix those fields if the user hasn't already
                // had this version
                // and done it themselves (cheers Ad).
                SQLRecordset rs = new SQLRecordset();
                rs.openRecordset("SELECT ID FROM animal WHERE IsGoodWithCats = 2 OR IsGoodWithDogs = 2 OR IsGoodWithChildren = 2 OR IsHouseTrained = 2",
                    "animal");

                if (rs.getEOF()) {
                    // Fix the broken ordering on the tri-state Good With.. and
                    // Housetrained... fields
                    // These four queries switch the old yes (a 1) for the new
                    // yes (a 0)
                    String sql = "UPDATE animal SET IsGoodWithCats = 0 WHERE IsGoodWithCats = 1";
                    DBConnection.executeAction(sql);
                    sql = "UPDATE animal SET IsGoodWithDogs = 0 WHERE IsGoodWithDogs = 1";
                    DBConnection.executeAction(sql);
                    sql = "UPDATE animal SET IsGoodWithChildren = 0 WHERE IsGoodWithChildren = 1";
                    DBConnection.executeAction(sql);
                    sql = "UPDATE animal SET IsHouseTrained = 0 WHERE IsHouseTrained = 1";
                    DBConnection.executeAction(sql);
                }

                rs.free();
                rs = null;
            } catch (Exception e) {
                errors.add("animal: Fixing broken tri-state fields.");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1383() {
        try {
            try {
                String sql = "ALTER TABLE owner ADD MatchSex tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchActive tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchSize tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchAgeFrom double NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchAgeTo double NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchSpecies int(11) NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchBreed int(11) NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchGoodWithCats tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchGoodWithDogs tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchGoodWithChildren tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchHouseTrained tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD MatchCommentsContain varchar(255) default NULL";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE owner ADD IsVet tinyint(4) NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("owner: Adding match fields");
            }

            try {
                String sql = "ALTER TABLE animal ADD IsNotAvailableForAdoption tinyint NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: Adding unavailable for adoption field");
            }

            try {
                String sql = "CREATE TABLE medicalpayment (" +
                    "ID int(11) NOT NULL default '0'," +
                    "AnimalMedicalID int(11) NOT NULL default '0'," +
                    "MedicalPaymentTypeID int(11) NOT NULL default '0'," +
                    "OwnerDonationID int(11) NOT NULL default '0'," +
                    "VetOwnerID int(11) NOT NULL default '0'," +
                    "Amount double NOT NULL default '0'," +
                    "Comments TEXT NULL," +
                    "RecordVersion int NOT NULL default '0'," +
                    "CreatedBy varchar(255) NOT NULL default ''," +
                    "CreatedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "LastChangedBy varchar(255) NOT NULL default ''," +
                    "LastChangedDate datetime NOT NULL default '0000-00-00 00:00:00'," +
                    "PRIMARY KEY  (ID)," +
                    "KEY IX_MedicalPaymentTypeID (MedicalPaymentTypeID)," +
                    "KEY IX_AnimalMedicalID (AnimalMedicalID)," +
                    "KEY IX_OwnerDonationID (OwnerDonationID)" +
                    ") TYPE=MyISAM";
                DBConnection.executeAction(sql);

                sql = "CREATE TABLE medicalpaymenttype (" +
                    "ID int(11) NOT NULL default '0'," +
                    "MedicalPaymentTypeName varchar(255) NOT NULL," +
                    "MedicalPaymentTypeDescription varchar(255) NULL," +
                    "PRIMARY KEY  (ID)" + ") TYPE=MyISAM";
                DBConnection.executeAction(sql);

                sql = "INSERT INTO medicalpaymenttype VALUES ('1', 'Fee', '')";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("medicalpayment/type: Table creation");
            }

            try {
                CustomReport cr = new CustomReport();
                cr.openRecordset("ID = 0");
                cr.addNew();
                cr.setTitle("Owner Criteria Matching");
                cr.setSQLCommand("031");
                cr.setHTMLBody("");
                cr.setDescription(
                    "Matches owner criteria to animals on the shelter");
                cr.setCategory("Inventories");
                cr.save("asmupdate");
                cr.free();
                cr = null;
            } catch (Exception e) {
                errors.add("customreport: Addition of new ownercriteria");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1391() {
        try {
            try {
                String sql = "ALTER TABLE animal ADD MostRecentEntryDate datetime NOT NULL default '0000-00-00 00:00:00'";
                DBConnection.executeAction(sql);

                // Need to update cached data for all animals now
                Global.logInfo("This database update needs to refresh cached data...",
                    "AutoDBUpdates.update1391");
                Animal.updateAllAnimalStatuses();
            } catch (Exception e) {
                errors.add("animal: Addition of MostRecentEntryDate field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1392() {
        try {
            try {
                String sql = "CREATE TABLE dbfs ( " +
                    "ID int(11) NOT NULL default '0', " +
                    "Path varchar(255) NOT NULL default '/', " +
                    "Name varchar(255) NOT NULL default '', " +
                    "Content LONGTEXT NULL, " + "PRIMARY KEY  (ID), " +
                    "KEY IX_Path (Path), " + "KEY IX_Name (Name) " +
                    ") TYPE=MyISAM";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("dbfs: table creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1393() {
        try {
            try {
                String sql = "ALTER TABLE dbfs MODIFY Content LONGTEXT";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("dbfs: Extension of data field to 4GB");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1394() {
        try {
            try {
                String sql = "ALTER TABLE ownerdonation ADD MovementID int NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("ownerdonation: Addition of movementid field");
            }

            try {
                Global.logInfo("Merging ownerdonation and adoptiondonation records...",
                    "AutoDBUpdates");

                SQLRecordset ad = new SQLRecordset();
                ad.openRecordset(
                    "SELECT adoptiondonation.*, adoption.OwnerID AS TheOwner " +
                    "FROM adoptiondonation INNER JOIN adoption ON " +
                    "adoption.ID = adoptiondonation.AdoptionID",
                    "adoptiondonation");

                while (!ad.getEOF()) {
                    DBConnection.executeAction(
                        "INSERT INTO ownerdonation (ID, MovementID, OwnerID, DonationTypeID, Date, Donation, Comments, " +
                        "CreatedBy, CreatedDate, LastChangedBy, LastChangedDate) VALUES (" +
                        DBConnection.getPrimaryKey("ownerdonation") + ", " +
                        ad.getField("AdoptionID") + ", " +
                        ad.getField("TheOwner") + ", " +
                        ad.getField("DonationTypeID") + ", " + "'" +
                        ad.getField("Date") + "', " + ad.getField("Donation") +
                        ", " + "'" + ad.getField("Comments") + "', " + "'" +
                        ad.getField("CreatedBy") + "', " + "'" +
                        ad.getField("CreatedDate") + "', " + "'" +
                        ad.getField("LastChangedBy") + "', " + "'" +
                        ad.getField("LastChangedDate") + "')");

                    System.out.print(".");
                    ad.moveNext();
                }

                ad.free();
                System.out.println(" done.");

                String sql = "DROP TABLE adoptiondonation";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add(
                    "ownerdonation: Merging with adoptiondonation records");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1401() {
        try {
            try {
                String sql = "ALTER TABLE animal MODIFY HeartwormTestDate datetime NULL;";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("animal: Changing of HeartwormTestDate");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1402() {
        // Update owner records to cache name element
        try {
            try {
                Owner o = new Owner();
                o.openRecordset("");
                Global.logInfo("Fixing owner name elements...", "AutoDBUpdates");

                while (!o.getEOF()) {
                    // Update the owner record
                    try {
                        String[] nb = Utils.getNameElements(o.getOwnerName()
                                                             .replace('\'', '`'));
                        String sql = "UPDATE owner SET " + "OwnerTitle = '" +
                            nb[0] + "', " + "OwnerInitials = '" + nb[1] +
                            "', " + "OwnerForenames = '" + nb[2] + "', " +
                            "OwnerSurname = '" + nb[3] + "' " + "WHERE ID = " +
                            o.getID();
                        DBConnection.executeAction(sql);
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }

                    o.moveNext();
                }

                o.free();
                o = null;
            } catch (Exception e) {
                errors.add("owner: Bulk update owner name elements.");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update1411() {
        // Remove whitespace from media files to stop
        // new decoder being upset
        try {
            Global.logInfo("Fixing Base64 encoded media files for new decoder...",
                "AutoDBUpdates.update1411");

            SQLRecordset r = new SQLRecordset();
            r.openRecordset("SELECT ID, Path, Name FROM dbfs WHERE Content IS NOT NULL",
                "dbfs");

            int total = (int) r.getRecordCount();
            int done = 1;

            while (!r.getEOF()) {
                try {
                    Global.logInfo("Re-encoding " + r.getField("Path") +
                        r.getField("Name") + " (" + done + "/" + total +
                        ")...", "AutoDBUpdates.update1411");

                    SQLRecordset r2 = new SQLRecordset();
                    r2.openRecordset("SELECT ID, Content FROM dbfs WHERE ID=" +
                        r.getField("ID"), "dbfs");

                    byte[] c = r2.getField("Content").toString().getBytes();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(c.length);

                    for (int i = 0; i < c.length; i++) {
                        if ((c[i] != 13) && (c[i] != 10) && (c[i] != 32)) {
                            baos.write((int) c[i]);
                        }
                    }

                    r2.setField("Content", baos.toString());
                    r2.save(false, "");
                    r2.addNew();
                    r2 = null;

                    r.moveNext();
                    done++;
                } catch (Exception e) {
                    errors.add("dbfs: Failed to re-encode file");
                    Global.logException(e, AutoDBUpdates.class);
                }
            }

            r.free();
            r = null;

            Global.logInfo("Re-encoding complete.", "AutoDBUpdates.update1411");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2001() {
        try {
            try {
                // Only MySQL users can upgrade to 2.001 because that was all we
                // supplied before.
                String sql = "ALTER TABLE users ADD RealName varchar(255) NULL";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("users: Addition of RealName field");
            }

            try {
                String sql = "UPDATE animal SET MostRecentEntryDate = DateBroughtIn WHERE MostRecentEntryDate = '0000-00-00 00:00'";
                DBConnection.executeAction(sql);
                sql = "UPDATE animallost SET DateLost = CURRENT_DATE WHERE DateLost = '0000-00-00 00:00'";
                DBConnection.executeAction(sql);
                sql = "UPDATE animalfound SET DateReported = CURRENT_DATE WHERE DateReported = '0000-00-00 00:00'";
                DBConnection.executeAction(sql);
                sql = "UPDATE media SET Date = CURRENT_DATE WHERE Date = '0000-00-00 00:00'";
                DBConnection.executeAction(sql);
                sql = "UPDATE ownerdonation SET CreatedDate = CURRENT_DATE WHERE CreatedDate = '0000-00-00 00:00'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                errors.add("db: Replacing of zero dates");
            }

            try {
                String sql = "ALTER TABLE animallitter ADD RecordVersion int(11) NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE ownerdonation ADD RecordVersion int(11) NOT NULL default '0'";
                DBConnection.executeAction(sql);
                sql = "ALTER TABLE media ADD RecordVersion int(11) NOT NULL default '0'";
                DBConnection.executeAction(sql);
            } catch (Exception e) {
                // This may fail on some installs
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2021() {
        try {
            try {
                // MD5 Hash all user passwords for additional security
                Users u = new Users();
                u.openRecordset("");

                while (!u.getEOF()) {
                    u.setPassword(MD5.hash(u.getPassword()));
                    u.moveNext();
                }

                u.save();
            } catch (Exception e) {
                errors.add("users: Password hashing");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2023() {
        try {
            try {
                // Add two new fields to the custom report table (format ok
                // for MySQL, HSQLDB and PostgreSQL).
                DBConnection.executeAction(
                    "ALTER TABLE customreport ADD OmitHeaderFooter INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE customreport ADD OmitCriteria INTEGER NULL");
                DBConnection.executeAction(
                    "UPDATE customreport SET OmitHeaderFooter = 0, OmitCriteria = 0");
            } catch (Exception e) {
                errors.add("customreport: Omit fields");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2100() {
        try {
            try {
                // Add new field to the animal table (format ok
                // for MySQL, HSQLDB and PostgreSQL).
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD OwnersVetID INTEGER NULL");
                DBConnection.executeAction("UPDATE animal SET OwnersVetID = 0");
            } catch (Exception e) {
                errors.add("animal: Add OwnersVetID");
            }

            // Copy the old field values into the health problems field - it's
            // not perfect, but beats creating loads of nonsense owner records
            // and we can't throw the info away
            try {
                Global.logInfo("Moving vet name to HealthProblems", "update2100");

                SQLRecordset a = new SQLRecordset();
                a.openRecordset("SELECT ID, NameOfOwnersVet, HealthProblems FROM animal",
                    "animal");

                while (!a.getEOF()) {
                    if ((a.getField("NameOfOwnersVet") != null) &&
                            (!a.getField("NameOfOwnersVet").toString().trim()
                                   .equals(""))) {
                        String nv = "(vet name: " +
                            a.getField("NameOfOwnersVet").toString() + ")\n";

                        if ((a.getField("HealthProblems") != null) &&
                                (!a.getField("HealthProblems").toString().trim()
                                       .equals(""))) {
                            nv += a.getField("HealthProblems").toString();
                        }

                        a.setField("HealthProblems", nv);
                    }

                    a.moveNext();
                }

                a.save(false, "");
                a.free();
                a = null;
            } catch (Exception e) {
                errors.add("animal: Save owners vet name");
            }

            try {
                // Remove the old field
                DBConnection.executeAction(
                    "ALTER TABLE animal DROP COLUMN NameOfOwnersVet");
            } catch (Exception e) {
                errors.add("animal: Remove NameOfOwnersVet");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2102() {
        try {
            try {
                // Set a default search limit of 100 rows
                DBConnection.executeAction(
                    "INSERT INTO configuration VALUES ('RecordSearchLimit', '100')");
            } catch (Exception e) {
                errors.add("configuration: Set RecordSearchLimit = 100");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2210() {
        try {
            try {
                // Set some new configuration values with defaults
                // that we introduced in 2.1.0
                Configuration.setEntry("OrganisationAddress", "Address");
                Configuration.setEntry("OrganisationTelephone", "Telephone");
                Configuration.setEntry("IncomingMediaScaling", "320x200");
                Configuration.setEntry("MaxMediaFileSize", "1000");
                Configuration.setEntry("AllowDBAutoUpdates", "Yes");
            } catch (Exception e) {
                errors.add("configuration: Set new defaults");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2301() {
        try {
            try {
                // Add the special needs flag
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD HasSpecialNeeds INTEGER NULL");
            } catch (Exception e) {
                errors.add("animal: Add HasSpecialNeeds field");
            }

            try {
                DBConnection.executeAction(
                    "UPDATE animal SET HasSpecialNeeds = 0");
            } catch (Exception e) {
                errors.add("animal: set default for HasSpecialNeeds");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2302() {
        try {
            String format = null;
            String shortformat = null;

            if (Configuration.getBoolean("UseAutoIncrementCodes")) {
                format = "UUUUUUUUUU";
                shortformat = "UUUU";
            } else if (Configuration.getBoolean("UniqueAutoCodes")) {
                format = "TYYYYUUUU";
                shortformat = "UUUUT";
            } else if (Configuration.getBoolean("UseAutoCodes")) {
                format = "TYYYYNNN";
                shortformat = "NNT";
            } else {
                format = "";
                shortformat = "";
            }

            Configuration.setEntry("CodingFormat", format);
            Configuration.setEntry("ShortCodingFormat", shortformat);

            // Add the shortcode field
            try {
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD ShortCode VARCHAR(255) NULL");
            } catch (Exception e) {
                errors.add("animal: Add ShortCode field");
                Global.logException(e, getClass());
            }

            try {
                // Default to the normal code first
                DBConnection.executeAction(
                    "UPDATE animal SET ShortCode = ShelterCode");
            } catch (Exception e) {
                errors.add("animal: Default ShortCode field");
                Global.logException(e, getClass());
            }

            // Go and retroactively set the short code for every animal
            Global.logInfo("Setting shortcode for all animals", "update2302");

            SQLRecordset r = new SQLRecordset();
            r.openRecordset(
                "SELECT animal.ID, ShelterCode, animaltype.AnimalType FROM animal " +
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID",
                "animal");

            while (!r.getEOF()) {
                String code = (String) r.getField("ShelterCode");
                int num = Animal.parseAnimalCode(code);

                // If we can't parse the code, leave the short one alone
                if (num != 0) {
                    try {
                        DBConnection.executeAction(
                            "UPDATE animal SET ShortCode = '" +
                            Animal.generateAnimalCode(
                                (String) r.getField("AnimalType"), new Date(),
                                num, num).shortcode + "' WHERE ID = " +
                            Integer.toString(
                                ((Integer) r.getField("ID")).intValue()));
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }
                }

                r.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2303() {
        try {
            try {
                // Add the publish flags
                DBConnection.executeAction(
                    "ALTER TABLE media ADD NewSinceLastPublish INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE media ADD UpdatedSinceLastPublish INTEGER NULL");
            } catch (Exception e) {
                errors.add(
                    "media: Add NewSinceLastPublish/UpdatedSinceLastPublish fields");
                Global.logException(e, getClass());
            }

            try {
                DBConnection.executeAction(
                    "UPDATE media SET NewSinceLastPublish = 0, UpdatedSinceLastPublish = 0");
            } catch (Exception e) {
                errors.add(
                    "media: Default NewSinceLastPublish, UpdatedSinceLastPublish");
                Global.logException(e, getClass());
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2310() {
        try {
            try {
                // Add the lookup tables
                DBConnection.executeAction(
                    "CREATE TABLE lksyesno (ID INTEGER NOT NULL PRIMARY KEY, Name VARCHAR(40) NOT NULL)");
                DBConnection.executeAction(
                    "INSERT INTO lksyesno VALUES (0, 'No')");
                DBConnection.executeAction(
                    "INSERT INTO lksyesno VALUES (1, 'Yes')");
                DBConnection.executeAction(
                    "CREATE TABLE lksynun (ID INTEGER NOT NULL PRIMARY KEY, Name VARCHAR(40) NOT NULL);");
                DBConnection.executeAction(
                    "INSERT INTO lksynun VALUES (0, 'Yes')");
                DBConnection.executeAction(
                    "INSERT INTO lksynun VALUES (1, 'No')");
                DBConnection.executeAction(
                    "INSERT INTO lksynun VALUES (2, 'Unknown')");
                DBConnection.executeAction(
                    "CREATE TABLE lksposneg (ID INTEGER NOT NULL PRIMARY KEY, Name VARCHAR(40) NOT NULL);");
                DBConnection.executeAction(
                    "INSERT INTO lksposneg VALUES (0, 'Unknown')");
                DBConnection.executeAction(
                    "INSERT INTO lksposneg VALUES (1, 'Negative')");
                DBConnection.executeAction(
                    "INSERT INTO lksposneg VALUES (2, 'Positive')");
            } catch (Exception e) {
                errors.add("Add lksyesno lookup tables");
                Global.logException(e, getClass());
            }

            try {
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD TimeOnShelter VARCHAR(255) NULL");
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD AnimalAge VARCHAR(255) NULL");
            } catch (Exception e) {
                errors.add("animal: Add age/time on shelter fields");
                Global.logException(e, getClass());
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2350() {
        try {
            try {
                // Add fields for the parsed code portions
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD UniqueCodeId INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD YearCodeId INTEGER NULL");
            } catch (Exception e) {
                errors.add("animal: Add UniqueCodeId/YearCodeId fields");
            }

            // Now, go and parse every single animal code to populate the
            // UniqueCodeId and YearCodeId fields.
            Global.logInfo("Setting uniquecodeid/yearcodeid for all animals",
                "update2350");

            SQLRecordset r = new SQLRecordset();
            r.openRecordset(
                "SELECT animal.ID, ShelterCode, animaltype.AnimalType FROM animal " +
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID",
                "animal");

            while (!r.getEOF()) {
                String code = (String) r.getField("ShelterCode");

                int yearid = Animal.parseAnimalCode(code);
                int uniqueid = yearid;

                // Reset whichever type we aren't using to zero
                if (Configuration.getString("CodingFormat").indexOf("UUU") != -1) {
                    yearid = 0;
                } else {
                    uniqueid = 0;
                }

                try {
                    DBConnection.executeAction(
                        "UPDATE animal SET UniqueCodeId = " + uniqueid +
                        ", YearCodeId = " + yearid + " WHERE ID = " +
                        Integer.toString(
                            ((Integer) r.getField("ID")).intValue()));
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }

                r.moveNext();
            }

            try {
                DBConnection.executeAction(
                    "CREATE INDEX animal_UniqueCodeID ON animal (UniqueCodeID)");
                DBConnection.executeAction(
                    "CREATE INDEX animal_YearCodeID ON animal (YearCodeID)");

                // MySQL always had an index for this, others didn't
                if (DBConnection.DBType != DBConnection.MYSQL) {
                    DBConnection.executeAction(
                        "CREATE INDEX animal_DateBroughtIn ON animal (DateBroughtIn)");
                }
            } catch (Exception e) {
                errors.add(
                    "animal: Add code indexes and datebroughtin for non-mysql");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2390() {
        try {
            // New additional field tables - different commands for
            // different databases:
            String[] hsqldb = {
                    "CREATE MEMORY TABLE additionalfield ( " +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "LinkType INTEGER NOT NULL, " +
                    "FieldName VARCHAR(255) NOT NULL, " +
                    "FieldLabel VARCHAR(255) NOT NULL, " +
                    "ToolTip VARCHAR(255) NULL, " +
                    "FieldType INTEGER NOT NULL, " +
                    "DisplayIndex INTEGER NOT NULL " + ")",
                    "CREATE INDEX additionalfield_LinkType ON additionalfield (LinkType)",
                    
                    "CREATE MEMORY TABLE additional ( " +
                    "LinkType INTEGER NOT NULL, " +
                    "LinkID INTEGER NOT NULL, " +
                    "AdditionalFieldID INTEGER NOT NULL, " +
                    "Value VARCHAR(16384) " + ")",
                    "CREATE UNIQUE INDEX additional_LinkTypeIDAdd ON additional (LinkType, LinkID, AdditionalFieldID)",
                    "CREATE INDEX additional_LinkTypeID ON additional (LinkType, LinkID)",
                    
                    "CREATE MEMORY TABLE lksfieldlink ( " +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "LinkType VARCHAR(40) NOT NULL " + ")",
                    "INSERT INTO lksfieldlink VALUES (0, 'Animal')",
                    "INSERT INTO lksfieldlink VALUES (1, 'Owner')",
                    
                    "CREATE MEMORY TABLE lksfieldtype ( " +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "FieldType VARCHAR(40) NOT NULL " + ")",
                    "INSERT INTO lksfieldtype VALUES (0, 'Yes/No')",
                    "INSERT INTO lksfieldtype VALUES (1, 'Text')",
                    "INSERT INTO lksfieldtype VALUES (2, 'Notes')",
                    "INSERT INTO lksfieldtype VALUES (3, 'Number')",
                    "INSERT INTO lksfieldtype VALUES (4, 'Date')",
                    "INSERT INTO lksfieldtype VALUES (5, 'Money')"
                };

            String[] postgresql = {
                    "CREATE TABLE additionalfield ( " +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "LinkType INTEGER NOT NULL, " +
                    "FieldName VARCHAR(255) NOT NULL, " +
                    "FieldLabel VARCHAR(255) NOT NULL, " +
                    "ToolTip VARCHAR(255) NULL, " +
                    "FieldType INTEGER NOT NULL, " +
                    "DisplayIndex INTEGER NOT NULL " + ")",
                    "CREATE INDEX additionalfield_LinkType ON additionalfield (LinkType)",
                    
                    "CREATE TABLE additional ( " +
                    "LinkType INTEGER NOT NULL, " +
                    "LinkID INTEGER NOT NULL, " +
                    "AdditionalFieldID INTEGER NOT NULL, " +
                    "Value VARCHAR(16384) " + ")",
                    "CREATE UNIQUE INDEX additional_LinkTypeIDAdd ON additional (LinkType, LinkID, AdditionalFieldID)",
                    "CREATE INDEX additional_LinkTypeID ON additional (LinkType, LinkID)",
                    
                    "CREATE TABLE lksfieldlink ( " +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "LinkType VARCHAR(40) NOT NULL " + ")",
                    "INSERT INTO lksfieldlink VALUES (0, 'Animal')",
                    "INSERT INTO lksfieldlink VALUES (1, 'Owner')",
                    
                    "CREATE TABLE lksfieldtype ( " +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "FieldType VARCHAR(40) NOT NULL " + ")",
                    "INSERT INTO lksfieldtype VALUES (0, 'Yes/No')",
                    "INSERT INTO lksfieldtype VALUES (1, 'Text')",
                    "INSERT INTO lksfieldtype VALUES (2, 'Notes')",
                    "INSERT INTO lksfieldtype VALUES (3, 'Number')",
                    "INSERT INTO lksfieldtype VALUES (4, 'Date')",
                    "INSERT INTO lksfieldtype VALUES (5, 'Money')"
                };

            String[] mysql = {
                    "CREATE TABLE additionalfield ( " +
                    "ID int(11) NOT NULL, " + "LinkType int(11) NOT NULL, " +
                    "FieldName varchar(255) NOT NULL, " +
                    "FieldLabel varchar(255) NOT NULL, " +
                    "ToolTip varchar(255) NULL, " +
                    "FieldType int(11) NOT NULL, " +
                    "DisplayIndex int(11) NOT NULL, " + "PRIMARY KEY (ID), " +
                    "KEY IX_LinkType(LinkType) " + ")",
                    
                    "CREATE TABLE additional ( " +
                    "LinkType int(11) NOT NULL, " +
                    "LinkID int(11) NOT NULL, " +
                    "AdditionalFieldID int(11) NOT NULL, " + "Value TEXT, " +
                    "PRIMARY KEY (LinkType, LinkID, AdditionalFieldID), " +
                    "KEY IX_LinkTypeID (LinkType, LinkID) " + ")",
                    
                    "CREATE TABLE lksfieldlink ( " +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "LinkType varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ")", "INSERT INTO lksfieldlink VALUES (0, 'Animal')",
                    "INSERT INTO lksfieldlink VALUES (1, 'Owner')",
                    
                    "CREATE TABLE lksfieldtype ( " +
                    "ID smallint NOT NULL DEFAULT '0', " +
                    "FieldType varchar(40) NOT NULL, " + "PRIMARY KEY  (ID) " +
                    ")", "INSERT INTO lksfieldtype VALUES (0, 'Yes/No')",
                    "INSERT INTO lksfieldtype VALUES (1, 'Text')",
                    "INSERT INTO lksfieldtype VALUES (2, 'Notes')",
                    "INSERT INTO lksfieldtype VALUES (3, 'Number')",
                    "INSERT INTO lksfieldtype VALUES (4, 'Date')",
                    "INSERT INTO lksfieldtype VALUES (5, 'Money')"
                };

            try {
                // Create the new tables
                if (DBConnection.DBType == DBConnection.MYSQL) {
                    for (int i = 0; i < mysql.length; i++) {
                        DBConnection.executeAction(mysql[i]);
                    }
                }

                if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                    for (int i = 0; i < postgresql.length; i++) {
                        DBConnection.executeAction(postgresql[i]);
                    }
                }

                if (DBConnection.DBType == DBConnection.HSQLDB) {
                    for (int i = 0; i < hsqldb.length; i++) {
                        DBConnection.executeAction(hsqldb[i]);
                    }
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
                errors.add("additionalfield/additional: Table creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2500() {
        try {
            try {
                // Add the second breed ID field and Crossbreed
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD Breed2ID INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD CrossBreed INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD BreedName VARCHAR(255) NULL");
                DBConnection.executeAction(
                    "UPDATE animal SET Breed2ID = BreedID, CrossBreed = 0, " +
                    "BreedName = (SELECT BreedName FROM breed WHERE ID = animal.BreedID)");
            } catch (Exception e) {
                errors.add("animal: Add Breed2ID/CrossBreed and BreedName");
            }

            try {
                // Add match added/expiry for owner records
                String timestamp = ((DBConnection.DBType == DBConnection.MYSQL)
                    ? "datetime" : "TIMESTAMP");
                DBConnection.executeAction("ALTER TABLE owner ADD MatchAdded " +
                    timestamp + " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE owner ADD MatchExpires " + timestamp +
                    " NULL");
                DBConnection.executeAction(
                    "UPDATE owner SET MatchAdded = LastChangedDate, MatchExpires = Null");
            } catch (Exception e) {
                errors.add("owner: Add MatchAdded/Expiry");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2600() {
        try {
            try {
                // Add the match criteria for a second breed and type
                DBConnection.executeAction(
                    "ALTER TABLE owner ADD MatchBreed2 INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE owner ADD MatchAnimalType INTEGER NULL");
                DBConnection.executeAction(
                    "UPDATE owner SET MatchBreed2 = 0, MatchAnimalType = 0");
            } catch (Exception e) {
                errors.add("owner: Add MatchAnimalType/MatchBreed2");
            }

            try {
                // Add the lookupvalues field
                if (DBConnection.DBType != DBConnection.MYSQL) {
                    DBConnection.executeAction(
                        "ALTER TABLE additionalfield ADD LookupValues VARCHAR(16384) NULL");
                } else {
                    DBConnection.executeAction(
                        "ALTER TABLE additionalfield ADD LookupValues text NULL");
                }
            } catch (Exception e) {
                errors.add("additionalfield: Add LookupValues");
            }

            try {
                // Add the new field type
                DBConnection.executeAction(
                    "INSERT INTO lksfieldtype VALUES (6, 'Lookup')");
            } catch (Exception e) {
                errors.add("lksfieldtype: Add Lookup element");
            }

            try {
                // Add the species to breed mapping, even if we don't do anything
                // with it in this release
                DBConnection.executeAction(
                    "ALTER TABLE breed ADD SpeciesID INTEGER NULL");
                // Try and map the standard ones automatically 
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 1 WHERE ID <= 220");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 2 WHERE ID >= 221 AND ID <= 313");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 7 WHERE ID >= 314 AND ID <= 356");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 24 WHERE ID >= 357 AND ID <= 378");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 4 WHERE ID >= 379 AND ID <= 389");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 28 WHERE ID >= 390 AND ID <= 391");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 11 WHERE ID >= 392 AND ID <= 397");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 3 WHERE ID >= 398 AND ID <= 436");
                DBConnection.executeAction(
                    "UPDATE breed SET SpeciesID = 14 WHERE ID >= 437 AND ID <= 441");
            } catch (Exception e) {
                errors.add("breed: Add species id");
            }

            try {
                DBConnection.executeAction(
                    "INSERT INTO configuration (ItemName, ItemValue) VALUES ('DontFilterBreedList', 'Yes')");
            } catch (Exception e) {
                errors.add(
                    "configuration: dont filter breed list by default for upgrades");
            }

            try {
                // Add coat type field
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD CoatType INTEGER NULL");
                DBConnection.executeAction("UPDATE animal SET CoatType = 0");
                DBConnection.executeAction(
                    "CREATE TABLE lkcoattype (ID INTEGER NOT NULL PRIMARY KEY, CoatType VARCHAR(40) NOT NULL )");
                DBConnection.executeAction(
                    "INSERT INTO lkcoattype VALUES (0, 'Short')");
                DBConnection.executeAction(
                    "INSERT INTO lkcoattype VALUES (1, 'Long')");
                DBConnection.executeAction(
                    "INSERT INTO lkcoattype VALUES (2, 'Rough')");
                DBConnection.executeAction(
                    "INSERT INTO lkcoattype VALUES (3, 'Curly')");
                DBConnection.executeAction(
                    "INSERT INTO lkcoattype VALUES (4, 'Corded')");
                DBConnection.executeAction(
                    "INSERT INTO lkcoattype VALUES (5, 'Hairless')");
            } catch (Exception e) {
                errors.add("animal: Add coat type");
            }

            try {
                // Add dates for last published to different services
                String timestamp = ((DBConnection.DBType == DBConnection.MYSQL)
                    ? "datetime" : "TIMESTAMP");
                DBConnection.executeAction(
                    "ALTER TABLE media ADD LastPublished " + timestamp +
                    " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE media ADD LastPublishedPF " + timestamp +
                    " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE media ADD LastPublishedAP " + timestamp +
                    " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE media ADD LastPublishedP911 " + timestamp +
                    " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE media ADD LastPublishedRG " + timestamp +
                    " NULL");
            } catch (Exception e) {
                errors.add("media: Add published dates");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2601() {
        try {
            try {
                // Add the cruelty case flag and set it from the old case type
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD CrueltyCase INTEGER NULL");
                DBConnection.executeAction("UPDATE animal SET CrueltyCase = 0");
                DBConnection.executeAction(
                    "UPDATE animal SET CrueltyCase = 1 WHERE AnimalTypeID = " +
                    Configuration.getString("AFCaseAnimalType"));
            } catch (Exception e) {
                errors.add("animal: Add CrueltyCase field");
            }

            try {
                // Make primary type/species the new default
                Configuration.changeKeyName("AFPrimaryUnwantedType",
                    "AFDefaultType");
                Configuration.changeKeyName("AFPrimarySpecies",
                    "AFDefaultSpecies");
                Configuration.loadFromDatabase();
            } catch (Exception e) {
                errors.add("animal: Set default type/species");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2610() {
        try {
            try {
                // Add the homecheckedby field
                DBConnection.executeAction(
                    "ALTER TABLE owner ADD HomeCheckedBy INTEGER NULL");
                DBConnection.executeAction("UPDATE owner SET HomeCheckedBy = 0");
            } catch (Exception e) {
                errors.add("owner: Add HomeCheckedBy field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2611() {
        try {
            try {
                // Delete the old DatePerformedLastHomeCheck field
                DBConnection.executeAction(
                    "ALTER TABLE owner DROP DatePerformedLastHomeCheck");
            } catch (Exception e) {
                errors.add("owner: DROP DatePerformedLastHomeCheck field");
            }

            try {
                // Add the current vet field
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD CurrentVetID INTEGER NULL");
                DBConnection.executeAction("UPDATE animal SET CurrentVetID = 0");
            } catch (Exception e) {
                errors.add("owner: DROP DatePerformedLastHomeCheck field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2621() {
        try {
            try {
                // Add the EstimatedDOB field
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD EstimatedDOB INTEGER NULL");
                DBConnection.executeAction("UPDATE animal SET EstimatedDOB = 0");
            } catch (Exception e) {
                errors.add("animal: ADD EstimatedDOB");
            }

            try {
                // Add the age group field
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD AgeGroup VARCHAR(255) NULL");
            } catch (Exception e) {
                errors.add("animal: ADD AgeGroup field");
            }

            try {
                Configuration.setEntry("AgeGroup1", "0.5");
                Configuration.setEntry("AgeGroup1Name", "Baby");
                Configuration.setEntry("AgeGroup2", "2");
                Configuration.setEntry("AgeGroup2Name", "Young Adult");
                Configuration.setEntry("AgeGroup3", "7");
                Configuration.setEntry("AgeGroup3Name", "Adult");
                Configuration.setEntry("AgeGroup4", "50");
                Configuration.setEntry("AgeGroup4Name", "Senior");
            } catch (Exception e) {
                errors.add("configuration: set agegroup thresholds");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2641() {
        try {
            try {
                // Add the AnimalID field to ownerdonation
                DBConnection.executeAction(
                    "ALTER TABLE ownerdonation ADD AnimalID INTEGER NULL");
                DBConnection.executeAction(
                    "UPDATE ownerdonation SET AnimalID = 0");

                // Find all adoption records with donations and update the donations
                // to link to the animal as well
                DBConnection.executeAction(
                    "UPDATE ownerdonation SET AnimalID = (SELECT adoption.AnimalID FROM adoption WHERE ID = ownerdonation.MovementID) WHERE MovementID > 0");
            } catch (Exception e) {
                errors.add("ownerdonation: ADD AnimalID");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2700() {
        try {
            // Turn this one on - this is what ASM historically has always
            // done, so keep behaviour the same 
            Configuration.setEntry("DefaultDateBroughtIn", "Yes");

            // DB Default is 14 days, but for people who upgraded, leave this option
            // off just in case
            Configuration.setEntry("AutoCancelReservesDays", "0");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2701() {
        try {
            try {
                // Add fields necessary for correct handling of donation instalments
                DBConnection.executeAction(
                    "ALTER TABLE ownerdonation ADD Frequency INTEGER NULL");
                DBConnection.executeAction(
                    "ALTER TABLE ownerdonation ADD NextCreated INTEGER NULL");
                DBConnection.executeAction(
                    "UPDATE ownerdonation SET Frequency = 0");
                DBConnection.executeAction(
                    "UPDATE ownerdonation SET NextCreated = 0");
            } catch (Exception e) {
                errors.add("ownerdonation: ADD Frequency and NextCreated");
            }

            try {
                // Add lookup table for the frequencies
                if (DBConnection.DBType == DBConnection.MYSQL) {
                    DBConnection.executeAction(
                        "CREATE TABLE lksdonationfreq ( ID smallint NOT NULL DEFAULT '0', Frequency varchar(50) NOT NULL, PRIMARY KEY  (ID) )");
                } else {
                    DBConnection.executeAction(
                        "CREATE TABLE lksdonationfreq ( ID INTEGER NOT NULL PRIMARY KEY, Frequency VARCHAR(50) NOT NULL )");
                }

                DBConnection.executeAction(
                    "INSERT INTO lksdonationfreq VALUES (0, 'One-Off')");
                DBConnection.executeAction(
                    "INSERT INTO lksdonationfreq VALUES (1, 'Weekly')");
                DBConnection.executeAction(
                    "INSERT INTO lksdonationfreq VALUES (2, 'Monthly')");
                DBConnection.executeAction(
                    "INSERT INTO lksdonationfreq VALUES (3, 'Quarterly')");
                DBConnection.executeAction(
                    "INSERT INTO lksdonationfreq VALUES (4, 'Half-Yearly')");
                DBConnection.executeAction(
                    "INSERT INTO lksdonationfreq VALUES (5, 'Annually')");
            } catch (Exception e) {
                errors.add("lksdonationfreq: Creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2702() {
        try {
            try {
                String currencyType = "FLOAT";

                if (DBConnection.DBType == DBConnection.HSQLDB) {
                    currencyType = "FLOAT";
                }

                if (DBConnection.DBType == DBConnection.MYSQL) {
                    currencyType = "double";
                }

                if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                    currencyType = "REAL";
                }

                // Add medical cost tracking fields
                DBConnection.executeAction(
                    "ALTER TABLE animalmedical ADD Cost " + currencyType +
                    " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE medicalprofile ADD Cost " + currencyType +
                    " NULL");
                DBConnection.executeAction("UPDATE animalmedical SET Cost = 0");
                DBConnection.executeAction("UPDATE medicalprofile SET Cost = 0");
            } catch (Exception e) {
                errors.add("animalmedical/medicalprofile: ADD Cost");
            }

            int entrydonation = 0;
            int wldonation = 0;

            try {
                // Create our donation types for entry donation and waiting list donation
                entrydonation = DBConnection.getPrimaryKey("donationtype");
                wldonation = DBConnection.getPrimaryKey("donationtype");
                DBConnection.executeAction(
                    "INSERT INTO donationtype ( ID, DonationName ) VALUES ( " +
                    entrydonation + ", 'Entry Donation')");
                DBConnection.executeAction(
                    "INSERT INTO donationtype ( ID, DonationName ) VALUES ( " +
                    wldonation + ", 'Waiting List Donation')");
            } catch (Exception e) {
                errors.add(
                    "donationtype: Create defaults for Entry Donation and Waiting List Donation");
            }

            try {
                Global.logInfo("Moving old donation records to unified ownerdonation table...",
                    "update2702");

                SQLRecordset ra = new SQLRecordset();
                ra.openRecordset(
                    "SELECT ID, BroughtInByOwnerID, DateBroughtIn, AmountDonatedOnEntry " +
                    "FROM animal WHERE BroughtInByOwnerID Is Not Null AND BroughtInByOwnerID > 0 AND " +
                    "AmountDonatedOnEntry > 0", "animal");

                SQLRecordset o = new SQLRecordset();
                o.openRecordset("SELECT * FROM ownerdonation WHERE ID = 0",
                    "ownerdonation");

                boolean entrydonations = !ra.getEOF();

                while (!ra.getEOF()) {
                    try {
                        o.addNew();
                        o.setField("ID",
                            new Integer(DBConnection.getPrimaryKey(
                                    "ownerdonation")));
                        o.setField("AnimalID", ra.getField("ID"));
                        o.setField("OwnerID", ra.getField("BroughtInByOwnerID"));
                        o.setField("Donation",
                            ra.getField("AmountDonatedOnEntry"));
                        o.setField("Date", ra.getField("DateBroughtIn"));
                        o.setField("DonationTypeID", new Integer(entrydonation));
                        o.setField("Comments", "");
                        o.setField("RecordVersion", new Integer(0));
                        o.setField("NextCreated", new Integer(0));
                        o.setField("Frequency", new Integer(0));
                        o.setField("MovementID", new Integer(0));
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }

                    ra.moveNext();
                }

                if (entrydonations) {
                    o.save(true, "update");
                    o.free();
                }

                o = null;

                ra = new SQLRecordset();
                ra.openRecordset(
                    "SELECT DatePutOnList, OwnerID, DonationSize FROM animalwaitinglist " +
                    "WHERE OwnerID Is Not Null AND OwnerID > 0 AND DonationSize > 0",
                    "animalwaitinglist");

                o = new SQLRecordset();
                o.openRecordset("SELECT * FROM ownerdonation WHERE ID = 0",
                    "ownerdonation");

                boolean wldonations = !ra.getEOF();

                while (!ra.getEOF()) {
                    try {
                        o.addNew();
                        o.setField("ID",
                            new Integer(DBConnection.getPrimaryKey(
                                    "ownerdonation")));
                        o.setField("OwnerID", ra.getField("OwnerID"));
                        o.setField("Donation", ra.getField("DonationSize"));
                        o.setField("Date", ra.getField("DatePutOnList"));
                        o.setField("DonationTypeID", new Integer(wldonation));
                        o.setField("Comments", "");
                        o.setField("RecordVersion", new Integer(0));
                        o.setField("NextCreated", new Integer(0));
                        o.setField("Frequency", new Integer(0));
                        o.setField("MovementID", new Integer(0));
                        o.setField("AnimalID", new Integer(0));
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }

                    ra.moveNext();
                }

                if (wldonations) {
                    o.save(true, "update");
                    o.free();
                }

                o = null;
            } catch (Exception e) {
                Global.logException(e, getClass());
                errors.add("ownerdonation: Creating from old fields");
            }

            try {
                DBConnection.executeAction(
                    "ALTER TABLE animalwaitinglist DROP DonationSize");
            } catch (Exception e) {
                errors.add("animalwaitinglist: Remove DonationSize field");
            }

            try {
                DBConnection.executeAction(
                    "ALTER TABLE animal DROP AmountDonatedOnEntry");
            } catch (Exception e) {
                errors.add("animal: Remove AmountDonatedOnEntry field");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2703() {
        try {
            String currencyType = "FLOAT";

            if (DBConnection.DBType == DBConnection.HSQLDB) {
                currencyType = "FLOAT";
            }

            if (DBConnection.DBType == DBConnection.MYSQL) {
                currencyType = "double";
            }

            if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                currencyType = "REAL";
            }

            try {
                // Add DaysOnShelter and DailyBoardingCost field to animal
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD DailyBoardingCost " + currencyType +
                    " NULL");
                DBConnection.executeAction(
                    "ALTER TABLE animal ADD DaysOnShelter INTEGER NULL");
                DBConnection.executeAction(
                    "UPDATE animal SET DailyBoardingCost = 0, DaysOnShelter = 0");
            } catch (Exception e) {
                errors.add("animal: Add BoardingCost and DaysOnShelter");
            }

            try {
                // Add animalcost table and costtype lookup
                if (DBConnection.DBType == DBConnection.HSQLDB) {
                    DBConnection.executeAction(
                        "CREATE MEMORY TABLE animalcost (" +
                        "ID INTEGER NOT NULL PRIMARY KEY, " +
                        "AnimalID INTEGER NOT NULL, " +
                        "CostTypeID INTEGER NOT NULL, " +
                        "CostDate TIMESTAMP NOT NULL, " +
                        "CostAmount FLOAT NOT NULL, " +
                        "Description VARCHAR(16384) NULL, " +
                        "RecordVersion INTEGER NOT NULL, " +
                        "CreatedBy VARCHAR(255) NOT NULL, " +
                        "CreatedDate TIMESTAMP NOT NULL, " +
                        "LastChangedBy VARCHAR(255) NOT NULL, " +
                        "LastChangedDate TIMESTAMP NOT NULL" + ")");
                    DBConnection.executeAction(
                        "CREATE INDEX animalcost_AnimalID ON animalcost (AnimalID)");
                    DBConnection.executeAction(
                        "CREATE INDEX animalcost_CostTypeID ON animalcost (CostTypeID)");
                    DBConnection.executeAction(
                        "CREATE INDEX animalcost_CostDate ON animalcost (CostDate)");
                    DBConnection.executeAction("CREATE MEMORY TABLE costtype (" +
                        "ID INTEGER NOT NULL PRIMARY KEY, " +
                        "CostTypeName VARCHAR(255) NOT NULL, " +
                        "CostTypeDescription VARCHAR(255) NULL)");
                    DBConnection.executeAction(
                        "INSERT INTO costtype VALUES (1, 'Microchip', '')");
                } else if (DBConnection.DBType == DBConnection.MYSQL) {
                    DBConnection.executeAction("CREATE TABLE animalcost (" +
                        "ID int(11) NOT NULL, " +
                        "AnimalID int(11) NOT NULL, " +
                        "CostDate datetime NOT NULL, " +
                        "CostTypeID int(11) NOT NULL, " +
                        "CostAmount double NOT NULL, " +
                        "Description varchar(255) NULL, " +
                        "RecordVersion int NOT NULL, " +
                        "CreatedBy varchar(255) NOT NULL, " +
                        "CreatedDate datetime NOT NULL, " +
                        "LastChangedBy varchar(255) NOT NULL, " +
                        "LastChangedDate datetime NOT NULL, " +
                        "PRIMARY KEY (ID), " +
                        "KEY IX_animalcost_AnimalID (AnimalID), " +
                        "KEY IX_animalcost_CostTypeID (CostTypeID), " +
                        "KEY IX_animalcost_CostDate (CostDate) " + ")");
                    DBConnection.executeAction("CREATE TABLE costtype (" +
                        "ID int(11) NOT NULL, " +
                        "CostTypeName VARCHAR(255) NOT NULL, " +
                        "CostTypeDescription VARCHAR(255) NULL, " +
                        "PRIMARY KEY (ID) )");
                    DBConnection.executeAction(
                        "INSERT INTO costtype VALUES (1, 'Microchip', '')");
                } else if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                    DBConnection.executeAction("CREATE TABLE animalcost (" +
                        "ID INTEGER NOT NULL PRIMARY KEY, " +
                        "AnimalID INTEGER NOT NULL, " +
                        "CostTypeID INTEGER NOT NULL, " +
                        "CostDate TIMESTAMP NOT NULL, " +
                        "CostAmount REAL NOT NULL, " +
                        "Description VARCHAR(16384) NULL, " +
                        "RecordVersion INTEGER NOT NULL, " +
                        "CreatedBy VARCHAR(255) NOT NULL, " +
                        "CreatedDate TIMESTAMP NOT NULL, " +
                        "LastChangedBy VARCHAR(255) NOT NULL, " +
                        "LastChangedDate TIMESTAMP NOT NULL" + ")");
                    DBConnection.executeAction(
                        "CREATE INDEX animalcost_AnimalID ON animalcost (AnimalID)");
                    DBConnection.executeAction(
                        "CREATE INDEX animalcost_CostTypeID ON animalcost (CostTypeID)");
                    DBConnection.executeAction(
                        "CREATE INDEX animalcost_CostDate ON animalcost (CostDate)");
                    DBConnection.executeAction("CREATE TABLE costtype (" +
                        "ID INTEGER NOT NULL PRIMARY KEY, " +
                        "CostTypeName VARCHAR(255) NOT NULL, " +
                        "CostTypeDescription VARCHAR(255) NULL)");
                    DBConnection.executeAction(
                        "INSERT INTO costtype VALUES (1, 'Microchip', '')");
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
                errors.add("animalcost + costtype: creation");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2704() {
        try {
            try {
                String currencyType = "FLOAT";

                if (DBConnection.DBType == DBConnection.HSQLDB) {
                    currencyType = "FLOAT";
                }

                if (DBConnection.DBType == DBConnection.MYSQL) {
                    currencyType = "double";
                }

                if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                    currencyType = "REAL";
                }

                // Add vaccination cost tracking fields
                DBConnection.executeAction(
                    "ALTER TABLE animalvaccination ADD Cost " + currencyType +
                    " NULL");
                DBConnection.executeAction(
                    "UPDATE animalvaccination SET Cost = 0");
            } catch (Exception e) {
                errors.add("animalvaccination: ADD Cost");
            }

            try {
                DBConnection.executeAction(
                    "ALTER TABLE owner ADD MembershipNumber VARCHAR(255) NULL");
                DBConnection.executeAction(
                    "UPDATE owner SET MembershipNumber = ''");
            } catch (Exception e) {
                errors.add("owner: ADD MembershipNumber");
            }

            // Fields for built in double entry book keeping package
            String[] hsqldb = {
                    "CREATE MEMORY TABLE accounts (" +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "Code VARCHAR(255) NOT NULL, " +
                    "Description VARCHAR(255) NOT NULL, " +
                    "AccountType INTEGER NOT NULL, " +
                    "DonationTypeID INTEGER NULL, " +
                    "RecordVersion INTEGER NOT NULL, " +
                    "CreatedBy VARCHAR(255) NOT NULL, " +
                    "CreatedDate TIMESTAMP NOT NULL, " +
                    "LastChangedBy VARCHAR(255) NOT NULL, " +
                    "LastChangedDate TIMESTAMP NOT NULL " + ")",
                    "CREATE UNIQUE INDEX accounts_Code ON accounts (Code)",
                    
                    "CREATE MEMORY TABLE accountstrx (" +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "TrxDate TIMESTAMP NOT NULL, " +
                    "Description VARCHAR(255) NULL, " +
                    "Reconciled INTEGER NOT NULL, " +
                    "Amount FLOAT NOT NULL, " +
                    "SourceAccountID INTEGER NOT NULL, " +
                    "DestinationAccountID INTEGER NOT NULL, " +
                    "OwnerDonationID INTEGER NULL, " +
                    "RecordVersion INTEGER NOT NULL, " +
                    "CreatedBy VARCHAR(255) NOT NULL, " +
                    "CreatedDate TIMESTAMP NOT NULL, " +
                    "LastChangedBy VARCHAR(255) NOT NULL, " +
                    "LastChangedDate TIMESTAMP NOT NULL " + ")",
                    "CREATE INDEX accountstrx_TrxDate ON accountstrx (TrxDate)",
                    "CREATE INDEX accountstrx_Source ON accountstrx (SourceAccountID)",
                    "CREATE INDEX accountstrx_Dest ON accountstrx (DestinationAccountID)",
                    
                    "CREATE MEMORY TABLE lksaccounttype (" +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "AccountType VARCHAR(255) NOT NULL " + ")",
                    "INSERT INTO lksaccounttype VALUES (1, 'Bank')",
                    "INSERT INTO lksaccounttype VALUES (2, 'Credit Card')",
                    "INSERT INTO lksaccounttype VALUES (3, 'Loan')",
                    "INSERT INTO lksaccounttype VALUES (4, 'Expense')",
                    "INSERT INTO lksaccounttype VALUES (5, 'Income')",
                    "INSERT INTO lksaccounttype VALUES (6, 'Pension')",
                    "INSERT INTO lksaccounttype VALUES (7, 'Shares')",
                    "INSERT INTO lksaccounttype VALUES (8, 'Asset')",
                    "INSERT INTO lksaccounttype VALUES (9, 'Liability')"
                };
            String[] postgresql = {
                    "CREATE TABLE accounts (" +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "Code VARCHAR(255) NOT NULL, " +
                    "Description VARCHAR(255) NOT NULL, " +
                    "AccountType INTEGER NOT NULL, " +
                    "DonationTypeID INTEGER NULL, " +
                    "RecordVersion INTEGER NOT NULL, " +
                    "CreatedBy VARCHAR(255) NOT NULL, " +
                    "CreatedDate TIMESTAMP NOT NULL, " +
                    "LastChangedBy VARCHAR(255) NOT NULL, " +
                    "LastChangedDate TIMESTAMP NOT NULL " + ")",
                    "CREATE UNIQUE INDEX accounts_Code ON accounts (Code)",
                    
                    "CREATE TABLE accountstrx (" +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "TrxDate TIMESTAMP NOT NULL, " +
                    "Description VARCHAR(255) NULL, " +
                    "Reconciled INTEGER NOT NULL, " + "Amount REAL NOT NULL, " +
                    "SourceAccountID INTEGER NOT NULL, " +
                    "DestinationAccountID INTEGER NOT NULL, " +
                    "OwnerDonationID INTEGER NULL, " +
                    "RecordVersion INTEGER NOT NULL, " +
                    "CreatedBy VARCHAR(255) NOT NULL, " +
                    "CreatedDate TIMESTAMP NOT NULL, " +
                    "LastChangedBy VARCHAR(255) NOT NULL, " +
                    "LastChangedDate TIMESTAMP NOT NULL " + ")",
                    "CREATE INDEX accountstrx_TrxDate ON accountstrx (TrxDate)",
                    "CREATE INDEX accountstrx_Source ON accountstrx (SourceAccountID)",
                    "CREATE INDEX accountstrx_Dest ON accountstrx (DestinationAccountID)",
                    
                    "CREATE TABLE lksaccounttype (" +
                    "ID INTEGER NOT NULL PRIMARY KEY, " +
                    "AccountType VARCHAR(255) NOT NULL " + ")",
                    "INSERT INTO lksaccounttype VALUES (1, 'Bank')",
                    "INSERT INTO lksaccounttype VALUES (2, 'Credit Card')",
                    "INSERT INTO lksaccounttype VALUES (3, 'Loan')",
                    "INSERT INTO lksaccounttype VALUES (4, 'Expense')",
                    "INSERT INTO lksaccounttype VALUES (5, 'Income')",
                    "INSERT INTO lksaccounttype VALUES (6, 'Pension')",
                    "INSERT INTO lksaccounttype VALUES (7, 'Shares')",
                    "INSERT INTO lksaccounttype VALUES (8, 'Asset')",
                    "INSERT INTO lksaccounttype VALUES (9, 'Liability')"
                };
            String[] mysql = {
                    "CREATE TABLE accounts (" + "ID int(11) NOT NULL, " +
                    "Code varchar(255) NOT NULL, " +
                    "Description varchar(255) NOT NULL, " +
                    "AccountType smallint NOT NULL, " +
                    "DonationTypeID int(11) NULL, " +
                    "RecordVersion int(11) NOT NULL, " +
                    "CreatedBy varchar(255) NOT NULL, " +
                    "CreatedDate datetime NOT NULL, " +
                    "LastChangedBy varchar(255) NOT NULL, " +
                    "LastChangedDate datetime NOT NULL, " +
                    "PRIMARY KEY (ID), " + "KEY IX_AccountsCode(Code) " + ")",
                    
                    "CREATE TABLE accountstrx (" + "ID int(11) NOT NULL," +
                    "TrxDate datetime NOT NULL," +
                    "Description varchar(255) NULL," +
                    "Reconciled smallint NOT NULL," +
                    "Amount double NOT NULL," +
                    "SourceAccountID int(11) NOT NULL," +
                    "DestinationAccountID int(11) NOT NULL," +
                    "OwnerDonationID int(11) NULL," +
                    "RecordVersion int(11) NOT NULL," +
                    "CreatedBy varchar(255) NOT NULL," +
                    "CreatedDate datetime NOT NULL," +
                    "LastChangedBy varchar(255) NOT NULL," +
                    "LastChangedDate datetime NOT NULL," + "PRIMARY KEY (ID)," +
                    "KEY IX_TrxDate(TrxDate)," +
                    "KEY IX_TrxSource(SourceAccountID)," +
                    "KEY IX_TrxDest(DestinationAccountID)" + ")",
                    
                    "CREATE TABLE lksaccounttype (" + "ID int(11) NOT NULL," +
                    "AccountType VARCHAR(255) NOT NULL," + "PRIMARY KEY (ID)" +
                    ")", "INSERT INTO lksaccounttype VALUES (1, 'Bank')",
                    "INSERT INTO lksaccounttype VALUES (2, 'Credit Card')",
                    "INSERT INTO lksaccounttype VALUES (3, 'Loan')",
                    "INSERT INTO lksaccounttype VALUES (4, 'Expense')",
                    "INSERT INTO lksaccounttype VALUES (5, 'Income')",
                    "INSERT INTO lksaccounttype VALUES (6, 'Pension')",
                    "INSERT INTO lksaccounttype VALUES (7, 'Shares')",
                    "INSERT INTO lksaccounttype VALUES (8, 'Asset')",
                    "INSERT INTO lksaccounttype VALUES (9, 'Liability')"
                };
            String[] use = hsqldb;

            if (DBConnection.DBType == DBConnection.MYSQL) {
                use = mysql;
            }

            if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                use = postgresql;
            }

            for (int i = 0; i < use.length; i++) {
                try {
                    DBConnection.executeAction(use[i]);
                } catch (Exception e) {
                    Global.logError("Failed executing: " + use[i],
                        "AutoDBUpdates.update2704");
                    Global.logException(e, getClass());
                }
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void update2705() {
        try {
            // Add the media DocPhoto field
            DBConnection.executeAction(
                "ALTER TABLE media ADD DocPhoto INTEGER NULL");
            DBConnection.executeAction("UPDATE media SET DocPhoto = 0");
        } catch (Exception e) {
            errors.add("media: ADD DocPhoto");
            Global.logException(e, getClass());
        }
    }

    public void update2706() {
        try {
            // Change that default cost type to Board and Food
            DBConnection.executeAction(
                "UPDATE costtype SET CostTypeName = 'Board and Food' WHERE ID = 1");

            // Set some default options for animal costs
            Configuration.setEntry("CreateBoardingCostOnAdoption", "Yes");
            Configuration.setEntry("BoardingCostType", "1");
            Configuration.setEntry("DefaultDailyBoardingCost", "20");
        } catch (Exception e) {
            errors.add("media: ADD DocPhoto");
            Global.logException(e, getClass());
        }
    }

    public void update2707() {
        try {
            // Change that default cost type to Board and Food
            DBConnection.executeAction(
                "ALTER TABLE owner ADD IsGiftAid INTEGER NULL");
            DBConnection.executeAction(
                "ALTER TABLE ownerdonation ADD IsGiftAid INTEGER NULL");
            DBConnection.executeAction("UPDATE owner SET IsGiftAid = 0");
            DBConnection.executeAction("UPDATE ownerdonation SET IsGiftAid = 0");
        } catch (Exception e) {
            errors.add("owner/ownerdonation: ADD IsGiftAid");
            Global.logException(e, getClass());
        }
    }

    public void update2708() {
        try {
            // Add the OwnerID field to the users table
            DBConnection.executeAction(
                "ALTER TABLE users ADD OwnerID INTEGER NULL");
            DBConnection.executeAction("UPDATE users SET OwnerID = 0");
        } catch (Exception e) {
            errors.add("owner/ownerdonation: ADD IsGiftAid");
            Global.logException(e, getClass());
        }
    }

    public void update2720() {
        try {
            try {
                // Add the default accounts to the table
                Global.logInfo("Creating default account set...",
                    "AutoDBUpdates");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (6, 'Income::Shop', 'Income from an on-site shop', 5, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (7, 'Income::Interest', 'Bank account interest', 5, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (8, 'Income::OpeningBalances', 'Opening balances', 5, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (9, 'Bank::Current', 'Bank current account', 1, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (10, 'Bank::Deposit', 'Bank deposit account', 1, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (11, 'Bank::Savings', 'Bank savings account', 1, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (12, 'Asset::Premises', 'Premises', 8, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (13, 'Expenses::Phone', 'Telephone Bills', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (14, 'Expenses::Electricity', 'Electricity Bills', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (15, 'Expenses::Water', 'Water Bills', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (16, 'Expenses::Gas', 'Gas Bills', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (17, 'Expenses::Postage', 'Postage costs', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (18, 'Expenses::Stationary', 'Stationary costs', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
                DBConnection.executeAction(
                    "INSERT INTO accounts VALUES (19, 'Expenses::Food', 'Animal food costs', 4, 0, 0, 'asmupdate', '2010-06-14 00:00:00', 'asmupdate', '2010-06-14 00:00:00')");
            } catch (Exception e) {
                errors.add("accounts: add default accounts");
                Global.logException(e, getClass());
            }

            // Enable automatic transaction creation
            Configuration.setEntry("CreateDonationTrx", "Yes");

            // Generate new accounts from the existing donation types
            SQLRecordset dt = new SQLRecordset();
            dt.openRecordset("SELECT * FROM donationtype", "donationtype");
            Global.logInfo("Creating trx from donations (" +
                dt.getRecordCount() + " accounts, " +
                DBConnection.executeForInt(
                    "SELECT COUNT(ID) FROM ownerdonation") + " trx)...",
                "AutoDBUpdates");

            while (!dt.getEOF()) {
                int aid = DBConnection.getPrimaryKey(DBConnection.con,
                        "accounts");
                DBConnection.executeAction("INSERT INTO accounts VALUES (" +
                    aid + ", '" + Global.i18n("uiaccount", "income_prefix") +
                    Utils.replace(dt.getString("DonationName"), " ", "") +
                    "', '" + dt.getString("DonationDescription") + "', 5, " +
                    dt.getInt("ID") + ", 0, " + "'asmupdate', '" +
                    Utils.getSQLDate(new Date()) + "', 'asmupdate', '" +
                    Utils.getSQLDate(new Date()) + "')");

                // Generate new transactions for the donations with bank current account
                // set as being the target
                SQLRecordset d = new SQLRecordset();
                d.openRecordset(
                    "SELECT * FROM ownerdonation WHERE Date Is Not Null AND DonationTypeID = " +
                    dt.getField("ID"), "ownerdonation");

                while (!d.getEOF()) {
                    int id = DBConnection.getPrimaryKey(DBConnection.con,
                            "accountstrx");
                    DBConnection.executeAction(
                        "INSERT INTO accountstrx VALUES (" + id + ", '" +
                        Utils.getSQLDate(d.getDate("Date")) + "', '', 0, " +
                        d.getDouble("Donation") + ", " + aid + ", 9, " +
                        d.getInt("ID") + ", 0, " + "'asmupdate', '" +
                        Utils.getSQLDate(new Date()) + "', 'asmupdate', '" +
                        Utils.getSQLDate(new Date()) + "')");

                    d.moveNext();
                }

                dt.moveNext();
            }
        } catch (Exception e) {
            errors.add("ownerdonation: add trx for donations");
            Global.logException(e, getClass());
        }
    }
}


class ErrorVector extends Vector {
    public boolean add(Object o) {
        Global.logError(o.toString(), "ErrorVector.add");

        return super.add(o);
    }
}
