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

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.bo.Animal.AnimalCode;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Handles and runs database diagnostics.
 *
 * @author Robin Rawson-Tetley
 */
public class Diagnostic extends Thread {
    public Diagnostic() {
        this.start();
    }

    public void run() {
        try {
            int orepair = 0;
            int mdate = 0;

            // REPAIR and MDATE is only valid for MySQL
            if (DBConnection.DBType == DBConnection.MYSQL) {
                orepair = repairDB();
                mdate = fixMySQLDates();
            }

            // Recalculate all adoption donations
            recalculateMovementDonations();

            // Check for orphaned records and remove
            int omove = orphanedMovements();
            int omedia = orphanedMedia();
            int ovacc = orphanedVaccinations();
            int omed = orphanedMedicals();
            int onm = returnedNonMovements();
            int icode = invalidCodesThisYear();

            if ((orepair == 0) && (omove == 0) && (omedia == 0) &&
                    (ovacc == 0) && (icode == 0) && (omed == 0)) {
                Dialog.showInformation(Global.i18n("db",
                        "No_errors_were_found_during_diagnostics."),
                    Global.i18n("db", "Scan_complete"));
            } else {
                Dialog.showInformation(Global.i18n("db", "diagnostic_output",
                        Integer.toString(orepair), Integer.toString(omove),
                        Integer.toString(omedia), Integer.toString(ovacc),
                        Integer.toString(omed), Integer.toString(onm),
                        Integer.toString(mdate), Integer.toString(icode)),
                    Global.i18n("db", "Errors_Found_and_Repaired"));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public int fixMySQLDates() throws Exception {
        final int COLS = 39;
        setStatusText(Global.i18n("db", "fixing_mysql_dates"));
        setStatusBarMax(COLS);
        fixMySQLDate("adoption", "MovementDate");
        incrementStatusBar();
        fixMySQLDate("adoption", "ReturnDate");
        incrementStatusBar();
        fixMySQLDate("adoption", "ReservationDate");
        incrementStatusBar();
        fixMySQLDate("adoption", "ReservationCancelledDate");
        incrementStatusBar();
        fixMySQLDateNonNull("animal", "DateOfBirth");
        incrementStatusBar();
        fixMySQLDate("animal", "DeceasedDate");
        incrementStatusBar();
        fixMySQLDate("animal", "IdentichipDate");
        incrementStatusBar();
        fixMySQLDate("animal", "TattooDate");
        incrementStatusBar();
        fixMySQLDate("animal", "NeuteredDate");
        incrementStatusBar();
        fixMySQLDate("animal", "CombiTestDate");
        incrementStatusBar();
        fixMySQLDate("animal", "HeartwormTestDate");
        incrementStatusBar();
        fixMySQLDateNonNull("animal", "DateBroughtIn");
        incrementStatusBar();
        fixMySQLDate("animal", "ActiveMovementDate");
        incrementStatusBar();
        fixMySQLDate("animal", "ActiveMovementReturn");
        incrementStatusBar();
        fixMySQLDateNonNull("animal", "MostRecentEntryDate");
        incrementStatusBar();
        fixMySQLDateNonNull("animaldiet", "DateStarted");
        incrementStatusBar();
        fixMySQLDateNonNull("animalfound", "DateReported");
        incrementStatusBar();
        fixMySQLDateNonNull("animalfound", "DateFound");
        incrementStatusBar();
        fixMySQLDate("animalfound", "ReturnToOwnerDate");
        incrementStatusBar();
        fixMySQLDateNonNull("animallitter", "Date");
        incrementStatusBar();
        fixMySQLDate("animallitter", "InvalidDate");
        incrementStatusBar();
        fixMySQLDateNonNull("animallost", "DateReported");
        incrementStatusBar();
        fixMySQLDateNonNull("animallost", "DateLost");
        incrementStatusBar();
        fixMySQLDate("animallost", "DateFound");
        incrementStatusBar();
        fixMySQLDateNonNull("animalmedical", "StartDate");
        fixMySQLDateNonNull("animalmedicaltreatment", "DateRequired");
        fixMySQLDate("animalmedicaltreatment", "DateGiven");
        incrementStatusBar();
        fixMySQLDate("animalvaccination", "DateOfVaccination");
        incrementStatusBar();
        fixMySQLDateNonNull("animalvaccination", "DateRequired");
        incrementStatusBar();
        fixMySQLDateNonNull("animalwaitinglist", "DatePutOnList");
        fixMySQLDate("animalwaitinglist", "DateRemovedFromList");
        incrementStatusBar();
        fixMySQLDate("animalwaitinglist", "DateOfLastOwnerContact");
        incrementStatusBar();
        fixMySQLDate("animalwaitinglist", "UrgencyUpdateDate");
        incrementStatusBar();
        fixMySQLDate("animalwaitinglist", "UrgencyLastUpdatedDate");
        incrementStatusBar();
        fixMySQLDateNonNull("diary", "DiaryDateTime");
        fixMySQLDate("diary", "DateCompleted");
        incrementStatusBar();
        fixMySQLDateNonNull("log", "Date");
        incrementStatusBar();
        fixMySQLDateNonNull("media", "Date");
        incrementStatusBar();
        fixMySQLDate("owner", "MembershipExpiryDate");
        incrementStatusBar();
        fixMySQLDate("owner", "DateLastHomeChecked");
        incrementStatusBar();
        fixMySQLDate("ownerdonation", "Date");
        incrementStatusBar();
        fixMySQLDate("ownerdonation", "DateDue");
        incrementStatusBar();
        fixMySQLDateNonNull("ownervoucher", "DateIssued");
        incrementStatusBar();
        fixMySQLDateNonNull("ownervoucher", "DateExpired");
        resetStatusBar();
        setStatusText("");

        return COLS;
    }

    /** Sets MySQL 0000 date fields to null */
    public void fixMySQLDate(String table, String field) {
        String sql = "UPDATE " + table + " SET " + field + " = null " +
            "WHERE " + field + " = '0000-00-00 00:00:00'";

        try {
            DBConnection.executeAction(sql);
        } catch (Exception e) {
            Global.logError(sql, "fixMySQLDate");
            Global.logException(e, getClass());
        }
    }

    /** Sets MySQL 0000 date fields to today */
    public void fixMySQLDateNonNull(String table, String field) {
        String sql = "UPDATE " + table + " SET " + field + " = '" +
            Utils.getSQLDate(new Date()) + "' WHERE " + field +
            " = '0000-00-00 00:00:00'";

        try {
            DBConnection.executeAction(sql);
        } catch (Exception e) {
            Global.logError(sql, "fixMySQLDateNonNull");
            Global.logException(e, getClass());
        }
    }

    public int repairDB() throws Exception {
        int badTables = 0;
        String[] tables = {
                "additional", "additionalfield", "adoption", "animal",
                "animaldiet", "animalfound", "animallitter", "animallost",
                "animalmedical", "animalmedicaltreatment", "animalname",
                "animaltype", "animalvaccination", "animalwaitinglist",
                "basecolour", "breed", "configuration", "customreport",
                "deathreason", "diary", "diarytaskdetail", "diarytaskhead",
                "diet", "entryreason", "internallocation", "lkcoattype",
                "lksmovementtype", "lksdiarylink", "lksloglink", "lksposneg",
                "lksex", "lksize", "lksmedialink", "lksyesno", "lksynun",
                "lkurgency", "log", "logtype", "media", "medicalprofile",
                "owner", "ownerdonation", "ownervoucher", "primarykey",
                "species", "users", "vaccinationtype", "voucher"
            };

        setStatusText(Global.i18n("db", "checking_for_corrupted_tables"));
        setStatusBarMax(tables.length);

        for (int i = 0; i < tables.length; i++) {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("CHECK TABLE " + tables[i], "none");

            if (!rs.getEOF()) {
                if (!Utils.englishLower(rs.getField("Msg_text").toString().trim())
                              .equals("ok")) {
                    badTables++;

                    Global.logInfo("MySQL says table '" + tables[i] +
                        "' needs to be repaired...", "Diagnostic.repairDB");

                    SQLRecordset rs2 = new SQLRecordset();
                    rs2.openRecordset("REPAIR TABLE " + tables[i], "none");
                    rs2.free();
                    rs2 = null;
                }
            }

            rs.free();
            rs = null;
            incrementStatusBar();
        }

        resetStatusBar();
        setStatusText("");

        return badTables;
    }

    public int orphanedMovements() throws CursorEngineException, Exception {
        Adoption ad = new Adoption();
        Animal a = new Animal();

        int badFound = 0;
        ad.openRecordset("");

        setStatusText(Global.i18n("db",
                "Checking_for_orphaned_movement_records..."));
        setStatusBarMax((int) ad.getRecordCount());

        while (!ad.getEOF()) {
            a.openRecordset("ID = " + ad.getAnimalID());

            if (a.getEOF()) {
                badFound++;
                DBConnection.executeAction("DELETE FROM adoption WHERE ID = " +
                    ad.getID());
            }

            incrementStatusBar();
            ad.moveNext();
        }

        resetStatusBar();
        setStatusText("");

        return badFound;
    }

    public int orphanedMedia() throws CursorEngineException, Exception {
        Media med = new Media();
        Animal a = new Animal();

        int badFound = 0;
        med.openRecordset("LinkTypeID = " +
            Integer.toString(Media.LINKTYPE_ANIMAL));

        setStatusText(Global.i18n("db", "Checking_for_orphaned_media_records..."));
        setStatusBarMax((int) med.getRecordCount());

        while (!med.getEOF()) {
            a.openRecordset("ID = " + med.getLinkID());

            if (a.getEOF()) {
                badFound++;
                DBConnection.executeAction("DELETE FROM media WHERE ID = " +
                    med.getID());
            }

            incrementStatusBar();
            med.moveNext();
        }

        resetStatusBar();
        setStatusText("");

        return badFound;
    }

    public int orphanedVaccinations() throws CursorEngineException, Exception {
        AnimalVaccination av = new AnimalVaccination();
        Animal a = new Animal();

        int badFound = 0;
        av.openRecordset("");

        setStatusText(Global.i18n("db",
                "Checking_for_orphaned_vaccination_records..."));
        setStatusBarMax((int) av.getRecordCount());

        while (!av.getEOF()) {
            a.openRecordset("ID = " + av.getAnimalID());

            if (a.getEOF()) {
                badFound++;
                DBConnection.executeAction(
                    "DELETE FROM animalvaccination WHERE ID = " + av.getID());
            }

            incrementStatusBar();
            av.moveNext();
        }

        resetStatusBar();
        setStatusText("");

        return badFound;
    }

    public int orphanedMedicals() throws CursorEngineException, Exception {
        AnimalMedical am = new AnimalMedical();
        Animal a = new Animal();

        int badFound = 0;
        am.openRecordset("");

        setStatusText(Global.i18n("db",
                "Checking_for_orphaned_medical_records..."));
        setStatusBarMax((int) am.getRecordCount());

        while (!am.getEOF()) {
            a.openRecordset("ID = " + am.getAnimalID());

            if (a.getEOF()) {
                badFound++;
                DBConnection.executeAction(
                    "DELETE FROM animalmedical WHERE ID = " + am.getID());
                DBConnection.executeAction(
                    "DELETE FROM animalmedicaltreatment WHERE AnimalMedicalID = " +
                    am.getID());
            }

            incrementStatusBar();
            am.moveNext();
        }

        resetStatusBar();
        setStatusText("");

        return badFound;
    }

    public void recalculateMovementDonations() throws Exception {
        setStatusText(Global.i18n("db", "Recalculating_adoption_donations"));
        DBConnection.executeAction(
            "UPDATE adoption SET Donation = ( SELECT SUM(Donation) FROM ownerdonation WHERE MovementID = adoption.ID)");
    }

    public int returnedNonMovements() throws CursorEngineException, Exception {
        Adoption ad = new Adoption();

        int badFound = 0;
        ad.openRecordset("");

        setStatusText(Global.i18n("db",
                "Checking_for_returned_non-movement_records..."));
        setStatusBarMax((int) ad.getRecordCount());

        while (!ad.getEOF()) {
            if ((ad.getReturnDate() != null) && (ad.getMovementDate() == null)) {
                badFound++;
                DBConnection.executeAction("DELETE FROM adoption WHERE ID = " +
                    ad.getID());
            }

            incrementStatusBar();
            ad.moveNext();
        }

        resetStatusBar();
        setStatusText("");

        return badFound;
    }

    /**
     * Generates new codes for animals that have an invalid one
     * for the year. This fixes the problem of duplicate codes
     * when one has sneaked in. This only looks at type codes
     * and four digit years - if a datebroughtin is used with
     * a 2 digit year, or month/day code then this won't work.
     *
     * Any version of ASM that allows those coding schemes is
     * new enough that checks are performed at editing time to
     * stop this happening though, so this is only really
     * applicable to old data from previous versions of ASM.
     *
     * This code works in 2 passes to clear all the cached year
     * codes out of broken ones prior to regeneration.
     *
     * @return
     * @throws CursorEngineException
     * @throws Exception
     */
    public int invalidCodesThisYear() throws CursorEngineException, Exception {
        // If the codes are locked, don't do anything
        if (Configuration.getBoolean("LockCodes")) {
            return 0;
        }

        // Type based system
        String format = Configuration.getString("CodingFormat");
        boolean typeBased = format.indexOf("T") != -1;

        // Year based system
        boolean yearBased = format.indexOf("YYYY") != -1;

        // If we aren't using a year or type based 
        // coding system, bail out now
        if (!typeBased && !yearBased) {
            return 0;
        }

        // Figure out the start of the year
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);

        SQLRecordset r = new SQLRecordset();
        r.openRecordset(
            "SELECT animal.ID, ShelterCode, animaltype.AnimalType, " +
            "DateBroughtIn " +
            "FROM animal INNER JOIN animaltype ON animaltype.ID = animal.AnimalTypeID " +
            "WHERE DateBroughtIn > '" + Utils.getSQLDate(c) + "'", "animal");

        setStatusText(Global.i18n("db", "Checking_for_bad_animal_codes..."));
        setStatusBarMax((int) r.getRecordCount());

        int bad = 0;
        Vector v = new Vector();

        while (!r.getEOF()) {
            int id = ((Integer) r.getField("ID")).intValue();
            String code = r.getField("ShelterCode").toString();
            String type = r.getField("AnimalType").toString();
            Date broughtin = (Date) r.getField("DateBroughtIn");
            c.setTime(broughtin);

            int year = c.get(Calendar.YEAR);

            // Does the type match?
            if (typeBased) {
                if (code.indexOf(type.substring(0, 1)) == -1) {
                    // No, type doesn't match
                    Global.logDebug("Type " + type + " not found in " + code,
                        "Diagnostic.invalidCodesThisYear");
                    clearCachedCode(id);
                    v.add(r.getField("ID"));
                    bad++;
                }
            }

            if (yearBased) {
                if (code.indexOf(Integer.toString(year)) == -1) {
                    // No, year doesn't match
                    Global.logDebug("Year " + year + " not found in " + code,
                        "Diagnostic.invalidCodesThisYear");
                    clearCachedCode(id);
                    v.add(r.getField("ID"));
                    bad++;
                }
            }

            incrementStatusBar();
            r.moveNext();
        }

        // If we had some broken ones, run back through and regenerate
        // their codes now that we cleared all the incorrect cached
        // numbers
        if (bad > 0) {
            r.moveFirst();
            setStatusBarMax((int) r.getRecordCount());

            while (!r.getEOF()) {
                for (int i = 0; i < v.size(); i++) {
                    if (r.getField("ID").equals(v.get(i))) {
                        // Got one, regenerate the code
                        int id = ((Integer) r.getField("ID")).intValue();
                        String type = r.getField("AnimalType").toString();
                        Date broughtin = (Date) r.getField("DateBroughtIn");

                        AnimalCode newcode = Animal.fastGenerateAnimalCode(type,
                                broughtin);
                        int yearid = Animal.parseAnimalCode(newcode.code);

                        DBConnection.executeAction("UPDATE animal SET " +
                            "ShelterCode = '" + newcode.code + "', " +
                            "ShortCode = '" + newcode.shortcode + "', " +
                            "YearCodeId = " + yearid + " WHERE ID = " + id);
                    }
                }

                incrementStatusBar();
                r.moveNext();
            }
        }

        resetStatusBar();
        setStatusText("");

        return bad;
    }

    /**
     * Clears an animals cached code
     * @param id
     * @throws CursorEngineException
     */
    protected void clearCachedCode(int id)
        throws CursorEngineException, Exception {
        DBConnection.executeAction("UPDATE animal SET YearCodeId = 0 " +
            "WHERE ID = " + id);
    }

    /**
     * Sets the maximum value on the status bar to be used with this report,
     * along with how often to update it.
     *
     * @param max
     *            The maximum status bar value
     */
    protected void setStatusBarMax(int max) {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.initStatusBarMax(max);
    }

    /**
     * Sets the status bar text while generating the report
     *
     * @param text
     *            The new status bar text
     */
    protected void setStatusText(String text) {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.setStatusText(text);
    }

    /**
     * Updates the value on the status bar to be used with this report.
     */
    protected void incrementStatusBar() {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.incrementStatusBar();
    }

    /** Resets the status bar after the report is done */
    protected void resetStatusBar() {
        net.sourceforge.sheltermanager.asm.globals.Global.mainForm.resetStatusBar();
    }
}
