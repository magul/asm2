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
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;


public class Animal extends UserInfoBO<Animal> {
    public static final int UNDERSIXMONTHS = 0;
    public static final int OVERSIXMONTHS = 1;
    public static final int ALLAGES = 2;
    public static final int ONSHELTER = 0;
    public static final int RECLAIMED = 1;
    public static final int DEAD = 2;
    public static final int ESCAPED = 3;
    public static final int STOLEN = 4;
    public static final int RELEASEDTOWILD = 5;
    public static final int TRANSFERRED = 6;
    public static final int FOSTERED = 7;
    public static final int ADOPTED = 8;
    public static final int RETAILER = 9;
    public static final int NONSHELTER = 99;

    /**
     * Determines whether report highlighting is in effect, and whether
     * this.getReportAnimalName() returns an HTML modified version of the name,
     * indicating certain flags.
     *
     * 0 = Not yet checked 1 = Yes 2 = No
     */
    private static int usingReportHighlighting = 0;
    private static boolean fosterOnShelter = false;
    private Media preferredWebMedia = null;
    private Media preferredDocMedia = null;
    private Owner originalowner = null;
    private Owner broughtinbyowner = null;
    private Owner ownersvet = null;

    public Animal() {
        tableName = "animal";
    }

    public Animal(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getAnimalTypeID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalTypeID");
    }

    public void setAnimalTypeID(Integer newValue) throws CursorEngineException {
        rs.setField("AnimalTypeID", newValue);
    }

    public String getAnimalTypeName() throws CursorEngineException {
        return LookupCache.getAnimalTypeName(getAnimalTypeID());
    }

    public String getAnimalName() throws CursorEngineException {
        return (String) rs.getField("AnimalName");
    }

    public String getReportAnimalName() throws CursorEngineException {
        return getReportAnimalName(getAnimalName(), getDateOfBirth(),
            getDeceasedDate(), getHasActiveReserve(), getActiveMovementType());
    }

    /**
     * Returns the animal's name, but with HTML tags. Names are adapted
     * according to the following:
     *
     * Aged under 6 months Blue/Italic Dead Red/Underlined Reserved Bold
     * Fostered + (<b>Fostered</b>) (only if foster = on shelter option)
     *
     * If the option is switched off at the configuration, this returns the same
     * as getAnimalName();
     */
    public static String getReportAnimalName(String animalName,
        Date dateOfBirth, Date deceasedDate, Integer hasActiveReserve,
        Integer activeMovementType) throws CursorEngineException {
        String name = animalName;
        String openTag = "";
        String closeTag = "";

        // Are we using report highlighting?
        if (usingReportHighlighting == 0) {
            // Not been initialised - this is a cache to save time
            usingReportHighlighting = Configuration.getBoolean(
                    "HighlightReportAnimals") ? 1 : 2;
            fosterOnShelter = Configuration.getBoolean("FosterOnShelter");
        }

        if (usingReportHighlighting == 2) {
            // We aren't using it - just return the name as normal.
            return name;
        }

        // Calculate 6 months before today
        Calendar sixMonthsBefore = Calendar.getInstance();
        sixMonthsBefore.add(Calendar.MONTH, -6);

        // Animals age
        Calendar animalDOB = Calendar.getInstance();

        if (dateOfBirth != null) {
            animalDOB = Utils.dateToCalendar(dateOfBirth);
        }

        // Aged under 6 months?
        if (animalDOB.after(sixMonthsBefore)) {
            openTag = "<font color=blue><i>";
            closeTag = "</i></font>";
        }

        // Dead? If so, overrides the 6 months
        if (deceasedDate != null) {
            openTag = "<font color=red><u>";
            closeTag = "</u></font>";
        }

        // Reserved
        if (hasActiveReserve.intValue() == 1) {
            openTag = "<b>" + openTag;
            closeTag = closeTag + "</b>";
        }

        if (activeMovementType != null) {
            // Fostered
            if (fosterOnShelter) {
                if (activeMovementType.intValue() == Adoption.MOVETYPE_FOSTER) {
                    closeTag += (" <b>(" + Global.i18n("reports", "fostered") +
                    ")</b>");
                }
            }

            // Retailer
            if (activeMovementType.intValue() == Adoption.MOVETYPE_RETAILER) {
                closeTag += (" <b>(" + Global.i18n("reports", "at_retailer") +
                ")</b>");
            }
        }

        return openTag + name + closeTag;
    }

    /**
     * Returns additional fields for the animal
     * @return a vector containing Additional.Field values
     */
    public Vector<Additional.Field> getAdditionalFields()
        throws Exception {
        return Additional.getFieldValues(AdditionalField.LINKTYPE_ANIMAL,
            getID().intValue());
    }

    /** Updates additional fields for the animal.
     * @param v Should contain a list of Additional.Field values
     * @throws Exception
     */
    public void setAdditionalFields(Vector<Additional.Field> v)
        throws Exception {
        Additional.setFieldValues(AdditionalField.LINKTYPE_ANIMAL,
            getID().intValue(), v);
    }

    /**
     * Provides compatibility with older code by looking up these dates from
     * their new position on the movement records.
     */
    public Date getReclaimedDate() throws CursorEngineException {
        Adoption ad = new Adoption();
        ad.openRecordset("AnimalID = " + this.getID() + " AND MovementType = " +
            Integer.toString(Adoption.MOVETYPE_RECLAIMED) +
            " AND ReturnDate Is Null");

        if (ad.getEOF()) {
            return null;
        } else {
            return (Date) ad.getMovementDate();
        }
    }

    /**
     * Provides compatibility with older code by looking up these dates from
     * their new position on the movement records.
     */
    public Date getStolenDate() throws CursorEngineException {
        Adoption ad = new Adoption();
        ad.openRecordset("AnimalID = " + this.getID() + " AND MovementType = " +
            Integer.toString(Adoption.MOVETYPE_STOLEN) +
            " AND ReturnDate Is Null");

        if (ad.getEOF()) {
            return null;
        } else {
            return (Date) ad.getMovementDate();
        }
    }

    /**
     * Provides compatibility with older code by looking up these dates from
     * their new position on the movement records.
     */
    public Date getEscapedDate() throws CursorEngineException {
        Adoption ad = new Adoption();
        ad.openRecordset("AnimalID = " + this.getID() + " AND MovementType = " +
            Integer.toString(Adoption.MOVETYPE_ESCAPED) +
            " AND ReturnDate Is Null");

        if (ad.getEOF()) {
            return null;
        } else {
            return (Date) ad.getMovementDate();
        }
    }

    /**
     * Provides compatibility with older code by looking up these dates from
     * their new position on the movement records.
     */
    public Date getDateReleasedToWild() throws CursorEngineException {
        Adoption ad = new Adoption();
        ad.openRecordset("AnimalID = " + this.getID() + " AND MovementType = " +
            Integer.toString(Adoption.MOVETYPE_RELEASED) +
            " AND ReturnDate Is Null");

        if (ad.getEOF()) {
            return null;
        } else {
            return (Date) ad.getMovementDate();
        }
    }

    public void setAnimalName(String newValue) throws CursorEngineException {
        rs.setField("AnimalName", newValue);
    }

    public Integer getCrueltyCase() throws CursorEngineException {
        return (Integer) rs.getField("CrueltyCase");
    }

    public void setCrueltyCase(Integer newValue) throws CursorEngineException {
        rs.setField("CrueltyCase", newValue);
    }

    public Integer getBaseColourID() throws CursorEngineException {
        return (Integer) rs.getField("BaseColourID");
    }

    public void setBaseColourID(Integer newValue) throws CursorEngineException {
        rs.setField("BaseColourID", newValue);
    }

    public Integer getBondedAnimalID() throws CursorEngineException {
        return (Integer) rs.getField("BondedAnimalID");
    }

    public void setBondedAnimalID(Integer newValue)
        throws CursorEngineException {
        rs.setField("BondedAnimalID", newValue);
    }

    public Integer getBondedAnimal2ID() throws CursorEngineException {
        return (Integer) rs.getField("BondedAnimal2ID");
    }

    public void setBondedAnimal2ID(Integer newValue)
        throws CursorEngineException {
        rs.setField("BondedAnimal2ID", newValue);
    }

    /**
     * Adds a bond to the animal ID given in one of the two slots
     * if available - if the id is already bonded, does nothing.
     */
    public void addBondedLink(Integer id) throws CursorEngineException {
        if (getBondedAnimalID() == null) {
            setBondedAnimalID(new Integer(0));
        }

        if (getBondedAnimal2ID() == null) {
            setBondedAnimal2ID(new Integer(0));
        }

        if (getBondedAnimalID().equals(id)) {
            return;
        }

        if (getBondedAnimal2ID().equals(id)) {
            return;
        }

        if (getBondedAnimalID().intValue() == 0) {
            setBondedAnimalID(id);

            return;
        }

        if (getBondedAnimal2ID().intValue() == 0) {
            setBondedAnimal2ID(id);
        }
    }

    /** Returns the names/codes of the animals this animal is bonded with or
      * an empty string if it is not bonded - for checking, the direct bonds
      * are checked, then the bonds of all other animals to see if they link
      * back to this one */
    public String getBondedAnimalDisplay() throws CursorEngineException {
        HashSet<Integer> bonded = new HashSet<Integer>();

        // Add the direct bond links
        if ((getBondedAnimalID() != null) &&
                (getBondedAnimalID().intValue() > 0)) {
            bonded.add(getBondedAnimalID());
        }

        if ((getBondedAnimal2ID() != null) &&
                (getBondedAnimal2ID().intValue() > 0)) {
            bonded.add(getBondedAnimal2ID());
        }

        // Look up other animals that are bonded back to this one
        try {
            for (SQLRecordset r : new SQLRecordset(
                    "SELECT ID FROM animal WHERE BondedAnimalID = " + getID() +
                    " OR BondedAnimal2ID = " + getID(), "animal")) {
                bonded.add(new Integer(r.getInteger("ID")));
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Get the names for the bonded animals
        String names = "";

        for (Integer id : bonded) {
            Animal a = LookupCache.getAnimalByID(id);

            if (names.length() > 0) {
                names += ", ";
            }

            names += ((Global.getShowShortCodes() ? a.getShortCode()
                                                  : a.getShelterCode()) + " " +
            a.getAnimalName());
        }

        return names;
    }

    public String getBaseColourName() throws CursorEngineException {
        return LookupCache.getBaseColourName(getBaseColourID());
    }

    public Integer getSpeciesID() throws CursorEngineException {
        return (Integer) rs.getField("SpeciesID");
    }

    public void setSpeciesID(Integer newValue) throws CursorEngineException {
        rs.setField("SpeciesID", newValue);
    }

    public String getSpeciesName() throws CursorEngineException {
        return LookupCache.getSpeciesName(getSpeciesID());
    }

    public String getBreedName() throws CursorEngineException {
        return (String) rs.getField("BreedName");
    }

    public void setBreedName(String newValue) throws CursorEngineException {
        rs.setField("BreedName", newValue);
    }

    public Integer getBreedID() throws CursorEngineException {
        return (Integer) rs.getField("BreedID");
    }

    public Integer getBreed2ID() throws CursorEngineException {
        return (Integer) rs.getField("Breed2ID");
    }

    public Integer getCoatType() throws CursorEngineException {
        return (Integer) rs.getField("CoatType");
    }

    public void setCoatType(Integer newValue) throws CursorEngineException {
        rs.setField("CoatType", newValue);
    }

    public String getCoatTypeName() throws CursorEngineException {
        return LookupCache.getCoatTypeForID(getCoatType());
    }

    public Integer getCrossBreed() throws CursorEngineException {
        return (Integer) rs.getField("CrossBreed");
    }

    public void setBreed2ID(Integer newValue) throws CursorEngineException {
        rs.setField("Breed2ID", newValue);
    }

    public void setCrossBreed(Integer newValue) throws CursorEngineException {
        rs.setField("CrossBreed", newValue);
    }

    public void setBreedID(Integer newValue) throws CursorEngineException {
        rs.setField("BreedID", newValue);
    }

    public String getMarkings() throws CursorEngineException {
        return (String) rs.getField("Markings");
    }

    public void setMarkings(String newValue) throws CursorEngineException {
        rs.setField("Markings", newValue);
    }

    public String getShelterCode() throws CursorEngineException {
        return (String) rs.getField("ShelterCode");
    }

    public void setShelterCode(String newValue) throws CursorEngineException {
        rs.setField("ShelterCode", newValue);
    }

    public String getShortCode() throws CursorEngineException {
        return (String) rs.getField("ShortCode");
    }

    public void setShortCode(String newValue) throws CursorEngineException {
        rs.setField("ShortCode", newValue);
    }

    public String getCode() throws CursorEngineException {
        return Global.getShowShortCodes() ? getShortCode() : getShelterCode();
    }

    public Integer getYearCodeID() throws CursorEngineException {
        return (Integer) rs.getField("YearCodeID");
    }

    public void setYearCodeID(Integer newValue) throws CursorEngineException {
        rs.setField("YearCodeID", newValue);
    }

    public Integer getUniqueCodeID() throws CursorEngineException {
        return (Integer) rs.getField("UniqueCodeID");
    }

    public void setUniqueCodeID(Integer newValue) throws CursorEngineException {
        rs.setField("UniqueCodeID", newValue);
    }

    public String getAcceptanceNumber() throws CursorEngineException {
        return (String) rs.getField("AcceptanceNumber");
    }

    public void setAcceptanceNumber(String newValue)
        throws CursorEngineException {
        rs.setField("AcceptanceNumber", newValue);
    }

    public Integer getArchived() throws CursorEngineException {
        return (Integer) rs.getField("Archived");
    }

    public void setArchived(Integer newValue) throws CursorEngineException {
        rs.setField("Archived", newValue);
    }

    public Integer getActiveMovementID() throws CursorEngineException {
        return (Integer) rs.getField("ActiveMovementID");
    }

    public void setActiveMovementID(Integer newValue)
        throws CursorEngineException {
        rs.setField("ActiveMovementID", newValue);
    }

    public Integer getHasActiveReserve() throws CursorEngineException {
        return (Integer) rs.getField("HasActiveReserve");
    }

    public void setHasActiveReserve(Integer newValue)
        throws CursorEngineException {
        rs.setField("HasActiveReserve", newValue);
    }

    public Integer getActiveMovementType() throws CursorEngineException {
        return (Integer) rs.getField("ActiveMovementType");
    }

    public void setActiveMovementType(Integer newValue)
        throws CursorEngineException {
        rs.setField("ActiveMovementType", newValue);
    }

    public Date getActiveMovementDate() throws CursorEngineException {
        return (Date) rs.getField("ActiveMovementDate");
    }

    public void setActiveMovementDate(Date newValue)
        throws CursorEngineException {
        rs.setField("ActiveMovementDate", newValue);
    }

    public Date getActiveMovementReturn() throws CursorEngineException {
        return (Date) rs.getField("ActiveMovementReturn");
    }

    public void setActiveMovementReturn(Date newValue)
        throws CursorEngineException {
        rs.setField("ActiveMovementReturn", newValue);
    }

    public Date getDateOfBirth() throws CursorEngineException {
        return (Date) rs.getField("DateOfBirth");
    }

    public void setDateOfBirth(Date newValue) throws CursorEngineException {
        rs.setField("DateOfBirth", newValue);
    }

    public Integer getEstimatedDOB() throws CursorEngineException {
        return (Integer) rs.getField("EstimatedDOB");
    }

    public void setEstimatedDOB(Integer newValue) throws CursorEngineException {
        rs.setField("EstimatedDOB", newValue);
    }

    public String getAgeGroup() throws CursorEngineException {
        return (String) rs.getField("AgeGroup");
    }

    public void setAgeGroup(String newValue) throws CursorEngineException {
        rs.setField("AgeGroup", newValue);
    }

    /** Calculates the animal's age group from the AgeGroupX bands in
      * configuration and returns the group as a string
      * @return The age group
      */
    public String calculateAgeGroup() {
        try {
            int i = 1;
            double lastband = 0;

            // Calculate how old the animal is in days
            Calendar c = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            c.setTime(getDateOfBirth());

            long mins = Utils.getDateDiff(today, c);
            double days = (mins / 60) / 24;

            while (true) {
                // Get the next band
                double band = Configuration.getDouble("AgeGroup" + i);

                if (band == 0) {
                    break;
                }

                // Our band figure will be in years, convert it to days
                band = band * 365;

                // Does the animal's current age fall into this band?
                if ((days >= lastband) && (days <= band)) {
                    return Configuration.getString("AgeGroup" + i + "Name");
                }

                // Update the last band and go round again
                lastband = band;
                i++;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // The age didn't fit a band
        return "";
    }

    public Date getDeceasedDate() throws CursorEngineException {
        return (Date) rs.getField("DeceasedDate");
    }

    public void setDeceasedDate(Date newValue) throws CursorEngineException {
        rs.setField("DeceasedDate", newValue);
    }

    public Integer getDeclawed() throws CursorEngineException {
        return (Integer) rs.getField("Declawed");
    }

    public void setDeclawed(Integer newValue) throws CursorEngineException {
        rs.setField("Declawed", newValue);
    }

    public Integer getSex() throws CursorEngineException {
        return (Integer) rs.getField("Sex");
    }

    public String getSexName() throws CursorEngineException {
        return LookupCache.getSexNameForID(getSex());
    }

    public void setSex(Integer newValue) throws CursorEngineException {
        rs.setField("Sex", newValue);
    }

    public Integer getSize() throws CursorEngineException {
        return (Integer) rs.getField("Size");
    }

    public String getSizeName() throws CursorEngineException {
        return LookupCache.getSizeNameForID(getSize());
    }

    public void setSize(Integer newValue) throws CursorEngineException {
        rs.setField("Size", newValue);
    }

    public Integer getDiedOffShelter() throws CursorEngineException {
        return (Integer) rs.getField("DiedOffShelter");
    }

    public void setDiedOffShelter(Integer newValue)
        throws CursorEngineException {
        rs.setField("DiedOffShelter", newValue);
    }

    public Integer getNonShelterAnimal() throws CursorEngineException {
        return (Integer) rs.getField("NonShelterAnimal");
    }

    public void setNonShelterAnimal(Integer newValue)
        throws CursorEngineException {
        rs.setField("NonShelterAnimal", newValue);
    }

    public Integer getMicrochipped() throws CursorEngineException {
        return (Integer) rs.getField("Identichipped");
    }

    public Integer getIdentichipped() throws CursorEngineException {
        return (Integer) rs.getField("Identichipped");
    }

    public void setMicrochipped(Integer newValue) throws CursorEngineException {
        rs.setField("Identichipped", newValue);
    }

    public void setIdentichipped(Integer newValue) throws CursorEngineException {
        rs.setField("Identichipped", newValue);
    }

    public String getMicrochipNumber() throws CursorEngineException {
        return (String) rs.getField("IdentichipNumber");
    }

    public String getIdentichipNumber() throws CursorEngineException {
        return (String) rs.getField("IdentichipNumber");
    }

    public void setMicrochipNumber(String newValue)
        throws CursorEngineException {
        rs.setField("IdentichipNumber", newValue);
    }

    public void setIdentichipNumber(String newValue)
        throws CursorEngineException {
        rs.setField("IdentichipNumber", newValue);
    }

    public Date getMicrochipDate() throws CursorEngineException {
        return (Date) rs.getField("IdentichipDate");
    }

    public Date getIdentichipDate() throws CursorEngineException {
        return (Date) rs.getField("IdentichipDate");
    }

    public void setMicrochipDate(Date newValue) throws CursorEngineException {
        rs.setField("IdentichipDate", newValue);
    }

    public void setIdentichipDate(Date newValue) throws CursorEngineException {
        rs.setField("IdentichipDate", newValue);
    }

    public Integer getTattoo() throws CursorEngineException {
        return (Integer) rs.getField("Tattoo");
    }

    public void setTattoo(Integer newValue) throws CursorEngineException {
        rs.setField("Tattoo", newValue);
    }

    public String getTattooNumber() throws CursorEngineException {
        return (String) rs.getField("TattooNumber");
    }

    public void setTattooNumber(String newValue) throws CursorEngineException {
        rs.setField("TattooNumber", newValue);
    }

    public Date getTattooDate() throws CursorEngineException {
        return (Date) rs.getField("TattooDate");
    }

    public void setTattooDate(Date newValue) throws CursorEngineException {
        rs.setField("TattooDate", newValue);
    }

    public Integer getNeutered() throws CursorEngineException {
        return (Integer) rs.getField("Neutered");
    }

    public void setNeutered(Integer newValue) throws CursorEngineException {
        rs.setField("Neutered", newValue);
    }

    public Date getNeuteredDate() throws CursorEngineException {
        return (Date) rs.getField("NeuteredDate");
    }

    public void setNeuteredDate(Date newValue) throws CursorEngineException {
        rs.setField("NeuteredDate", newValue);
    }

    public void setCombiTested(Integer newValue) throws CursorEngineException {
        rs.setField("CombiTested", newValue);
    }

    public Integer getCombiTested() throws CursorEngineException {
        return (Integer) rs.getField("CombiTested");
    }

    public void setCombiTestResult(Integer newValue)
        throws CursorEngineException {
        rs.setField("CombiTestResult", newValue);
    }

    public Integer getCombiTestResult() throws CursorEngineException {
        return (Integer) rs.getField("CombiTestResult");
    }

    public void setFLVTestResult(Integer newValue) throws CursorEngineException {
        rs.setField("FLVResult", newValue);
    }

    public Integer getFLVTestResult() throws CursorEngineException {
        return (Integer) rs.getField("FLVResult");
    }

    public Date getCombiTestDate() throws CursorEngineException {
        return (Date) rs.getField("CombiTestDate");
    }

    public void setCombiTestDate(Date newValue) throws CursorEngineException {
        rs.setField("CombiTestDate", newValue);
    }

    public void setHeartwormTested(Integer newValue)
        throws CursorEngineException {
        rs.setField("HeartwormTested", newValue);
    }

    public Integer getHeartwormTested() throws CursorEngineException {
        return (Integer) rs.getField("HeartwormTested");
    }

    public void setHeartwormTestResult(Integer newValue)
        throws CursorEngineException {
        rs.setField("HeartwormTestResult", newValue);
    }

    public Integer getHeartwormTestResult() throws CursorEngineException {
        return (Integer) rs.getField("HeartwormTestResult");
    }

    public void setHeartwormTestDate(Date newValue)
        throws CursorEngineException {
        rs.setField("HeartwormTestDate", newValue);
    }

    public Date getHeartwormTestDate() throws CursorEngineException {
        return (Date) rs.getField("HeartwormTestDate");
    }

    public String getHiddenAnimalDetails() throws CursorEngineException {
        return (String) rs.getField("HiddenAnimalDetails");
    }

    public void setHiddenAnimalDetails(String newValue)
        throws CursorEngineException {
        rs.setField("HiddenAnimalDetails", newValue);
    }

    public String getAnimalComments() throws CursorEngineException {
        return (String) rs.getField("AnimalComments");
    }

    public void setAnimalComments(String newValue) throws CursorEngineException {
        rs.setField("AnimalComments", newValue);
    }

    public Integer getOwnersVetID() throws CursorEngineException {
        return (Integer) rs.getField("OwnersVetID");
    }

    public void setOwnersVetID(Integer newValue) throws CursorEngineException {
        rs.setField("OwnersVetID", newValue);
    }

    public Integer getCurrentVetID() throws CursorEngineException {
        return (Integer) rs.getField("CurrentVetID");
    }

    public void setCurrentVetID(Integer newValue) throws CursorEngineException {
        rs.setField("CurrentVetID", newValue);
    }

    public String getReasonForEntry() throws CursorEngineException {
        return (String) rs.getField("ReasonForEntry");
    }

    public void setReasonForEntry(String newValue) throws CursorEngineException {
        rs.setField("ReasonForEntry", newValue);
    }

    public String getReasonNO() throws CursorEngineException {
        return (String) rs.getField("ReasonNO");
    }

    public void setReasonNO(String newValue) throws CursorEngineException {
        rs.setField("ReasonNO", newValue);
    }

    public Date getDateBroughtIn() throws CursorEngineException {
        return (Date) rs.getField("DateBroughtIn");
    }

    public Date getMostRecentEntryDate() throws CursorEngineException {
        return (Date) rs.getField("MostRecentEntryDate");
    }

    public void setMostRecentEntryDate(Date newValue)
        throws CursorEngineException {
        rs.setField("MostRecentEntryDate", newValue);
    }

    public Double getDailyBoardingCost() throws CursorEngineException {
        return (Double) rs.getField("DailyBoardingCost");
    }

    public void setDailyBoardingCost(Double newValue)
        throws CursorEngineException {
        rs.setField("DailyBoardingCost", newValue);
    }

    public void setDateBroughtIn(Date newValue) throws CursorEngineException {
        rs.setField("DateBroughtIn", newValue);
    }

    public String getHealthProblems() throws CursorEngineException {
        return (String) rs.getField("HealthProblems");
    }

    public void setHealthProblems(String newValue) throws CursorEngineException {
        rs.setField("HealthProblems", newValue);
    }

    public String getCreatedBy() throws CursorEngineException {
        return (String) rs.getField("CreatedBy");
    }

    public void setCreatedBy(String newValue) throws CursorEngineException {
        rs.setField("CreatedBy", newValue);
    }

    public Date getCreatedDate() throws CursorEngineException {
        return (Date) rs.getField("CreatedDate");
    }

    public void setCreatedDate(Date newValue) throws CursorEngineException {
        rs.setField("CreatedDate", newValue);
    }

    public String getLastChangedBy() throws CursorEngineException {
        return (String) rs.getField("LastChangedBy");
    }

    public void setLastChangedBy(String newValue) throws CursorEngineException {
        rs.setField("LastChangedBy", newValue);
    }

    public Date getLastChangedDate() throws CursorEngineException {
        return (Date) rs.getField("LastChangedDate");
    }

    public void setLastChangedDate(Date newValue) throws CursorEngineException {
        rs.setField("LastChangedDate", newValue);
    }

    public Integer getEntryReasonID() throws CursorEngineException {
        return (Integer) rs.getField("EntryReasonID");
    }

    public void setEntryReasonID(Integer newValue) throws CursorEngineException {
        rs.setField("EntryReasonID", newValue);
    }

    public Integer getPTSReasonID() throws CursorEngineException {
        return (Integer) rs.getField("PTSReasonID");
    }

    public void setPTSReasonID(Integer newValue) throws CursorEngineException {
        rs.setField("PTSReasonID", newValue);
    }

    public Integer getPutToSleep() throws CursorEngineException {
        return (Integer) rs.getField("PutToSleep");
    }

    public void setPutToSleep(Integer newValue) throws CursorEngineException {
        rs.setField("PutToSleep", newValue);
    }

    public String getPTSReason() throws CursorEngineException {
        return (String) rs.getField("PTSReason");
    }

    public void setPTSReason(String newValue) throws CursorEngineException {
        rs.setField("PTSReason", newValue);
    }

    public Integer getIsDOA() throws CursorEngineException {
        return (Integer) rs.getField("IsDOA");
    }

    public void setIsDOA(Integer newValue) throws CursorEngineException {
        rs.setField("IsDOA", newValue);
    }

    public Integer getIsTransfer() throws CursorEngineException {
        return (Integer) rs.getField("IsTransfer");
    }

    public void setIsTransfer(Integer newValue) throws CursorEngineException {
        rs.setField("IsTransfer", newValue);
    }

    public Integer getIsNotAvailableForAdoption() throws CursorEngineException {
        return (Integer) rs.getField("IsNotAvailableForAdoption");
    }

    public void setIsNotAvailableForAdoption(Integer newValue)
        throws CursorEngineException {
        rs.setField("IsNotAvailableForAdoption", newValue);
    }

    public Integer getShelterLocation() throws CursorEngineException {
        return (Integer) rs.getField("ShelterLocation");
    }

    public void setShelterLocation(Integer newValue)
        throws CursorEngineException {
        rs.setField("ShelterLocation", newValue);
    }

    public Integer isGoodWithCats() throws CursorEngineException {
        return (Integer) rs.getField("IsGoodWithCats");
    }

    public void setGoodWithCats(Integer newValue) throws CursorEngineException {
        rs.setField("IsGoodWithCats", newValue);
    }

    public Integer isGoodWithDogs() throws CursorEngineException {
        return (Integer) rs.getField("IsGoodWithDogs");
    }

    public void setGoodWithDogs(Integer newValue) throws CursorEngineException {
        rs.setField("IsGoodWithDogs", newValue);
    }

    public Integer isGoodWithKids() throws CursorEngineException {
        return (Integer) rs.getField("IsGoodWithChildren");
    }

    public void setGoodWithKids(Integer newValue) throws CursorEngineException {
        rs.setField("IsGoodWithChildren", newValue);
    }

    public Integer isHouseTrained() throws CursorEngineException {
        return (Integer) rs.getField("IsHouseTrained");
    }

    public void setHouseTrained(Integer newValue) throws CursorEngineException {
        rs.setField("IsHouseTrained", newValue);
    }

    public Integer isHasSpecialNeeds() throws CursorEngineException {
        return (Integer) rs.getField("HasSpecialNeeds");
    }

    public void setHasSpecialNeeds(Integer newValue)
        throws CursorEngineException {
        rs.setField("HasSpecialNeeds", newValue);
    }

    public String getShelterLocationName() throws CursorEngineException {
        return LookupCache.getInternalLocationName(getShelterLocation());
    }

    public Integer getOriginalOwnerID() throws CursorEngineException {
        return (Integer) rs.getField("OriginalOwnerID");
    }

    public void setOriginalOwnerID(Integer newValue)
        throws CursorEngineException {
        rs.setField("OriginalOwnerID", newValue);
    }

    public Owner getOwnersVet() throws CursorEngineException {
        // Do we have a vet?
        if (ownersvet != null) {
            // Is it the correct one?
            if (ownersvet.getID().equals(getOwnersVetID())) {
                // It is - return it
                return ownersvet;
            }
        }

        // We don't have one or it isn't valid, look it up
        ownersvet = new Owner();
        ownersvet.openRecordset("ID = " + getOwnersVetID());

        if (ownersvet.getEOF()) {
            ownersvet = null;

            return null;
        } else {
            return ownersvet;
        }
    }

    public Owner getOriginalOwner() throws CursorEngineException {
        // Do we have an owner?
        if (originalowner != null) {
            // Is it the correct one?
            if (originalowner.getID().equals(getOriginalOwnerID())) {
                // It is - return it
                return originalowner;
            }
        }

        // We don't have one or it isn't valid, look it up
        originalowner = new Owner();
        originalowner.openRecordset("ID = " + getOriginalOwnerID());

        if (originalowner.getEOF()) {
            originalowner = null;

            return null;
        } else {
            return originalowner;
        }
    }

    public Integer getBroughtInByOwnerID() throws CursorEngineException {
        return (Integer) rs.getField("BroughtInByOwnerID");
    }

    public void setBroughtInByOwnerID(Integer newValue)
        throws CursorEngineException {
        rs.setField("BroughtInByOwnerID", newValue);
    }

    public Owner getBroughtInByOwner() throws CursorEngineException {
        // Do we have an owner?
        if (broughtinbyowner != null) {
            // Is it the correct one?
            if (broughtinbyowner.getID().equals(getBroughtInByOwnerID())) {
                // It is - return it
                return broughtinbyowner;
            }
        }

        // We don't have one or it isn't valid, look it up
        broughtinbyowner = new Owner();
        broughtinbyowner.openRecordset("ID = " + getBroughtInByOwnerID());

        if (broughtinbyowner.getEOF()) {
            broughtinbyowner = null;

            return null;
        } else {
            return broughtinbyowner;
        }
    }

    public String getRabiesTag() throws CursorEngineException {
        return (String) rs.getField("RabiesTag");
    }

    public void setRabiesTag(String newValue) throws CursorEngineException {
        rs.setField("RabiesTag", newValue);
    }

    public Integer getRecordVersion() throws CursorEngineException {
        return (Integer) rs.getField("RecordVersion");
    }

    public void setRecordVersion(Integer newValue) throws CursorEngineException {
        rs.setField("RecordVersion", newValue);
    }

    /**
     * Overridden from superclass - contains Animal specific validation
     * routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            // Make sure we have a name
            if (getAnimalName().equals("") || (getAnimalName() == null)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_a_name_for_this_animal."));
            }

            // Make sure we have a brought in date
            if (getDateBroughtIn() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_a_date_the_animal_was_brought_into_the_shelter."));
            }

            // Make sure we have a date of birth
            if (getDateOfBirth() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_a_date_of_birth_for_the_animal."));
            }

            // Make sure the shelter code is unique
            Animal tanimal = new Animal();
            tanimal.openRecordset("ShelterCode Like '" + getShelterCode() +
                "'");

            // If the animal is flagged as a crossbreed, make sure they aren't
            // the same
            if ((getCrossBreed().intValue() == 1) &&
                    getBreedID().equals(getBreed2ID())) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_select_two_different_breeds_for_a_crossbreed_animal."));
            }

            // Any similar records?
            if (!tanimal.getEOF()) {
                // Make sure it isn't the one we are looking at
                if (!tanimal.getID().equals(getID())) {
                    // It isn't - throw an error
                    throw new BOValidationException(Global.i18n("bo",
                            "sheltercode_has_already_been_used",
                            getShelterCode()));
                }
            }

            tanimal = null;

            // If strict checking is being enforced, make sure that the
            // code conforms to the standard set (ie. We can parse it):
            try {
                if (Configuration.getBoolean("StrictAutoCodes")) {
                    int no = parseAnimalCode(getShelterCode());

                    if (no == 0) {
                        throw new BOValidationException(Global.i18n("bo",
                                "code_does_not_match_your_selected_coding_scheme_of",
                                getShelterCode(), Global.getCodingFormat()));
                    }
                }
            } catch (Exception e) {
                throw new BOValidationException(e.getMessage());
            }

            // Check the animal wasn't deceased before it was brought in
            // (if it is deceased that is)
            if (getDeceasedDate() != null) {
                // Convert it to a calendar
                Calendar deceased = Utils.dateToCalendar(getDeceasedDate());
                Calendar brought = Utils.dateToCalendar(getDateBroughtIn());

                // Was it deceased before it was brought in?
                if (deceased.before(brought)) {
                    throw new BOValidationException(Global.i18n("bo",
                            "An_animal_cannot_be_marked_as_deceased_before_it_was_brought_into_the_shelter."));
                }
            }

            // If the animal has been marked as DOA or PTS without a deceased
            // date, default todays.
            if (getDeceasedDate() == null) {
                if ((getIsDOA().intValue() == 1) ||
                        (getPutToSleep().intValue() == 1)) {
                    setDeceasedDate(new Date());
                }
            }
        } catch (Exception e) {
            Global.logException(e, this.getClass());
            throw new BOValidationException(e.getMessage());
        }
    }

    /**
     * Determines if this animal is over six months old. Used by litter logger
     * calculation to only include babies and not parents.
     *
     * @return True if the animal is over six months old
     */
    public boolean isOverSixMonths() throws CursorEngineException {
        Calendar dob = Utils.dateToCalendar(getDateOfBirth());
        Calendar sixmonths = Calendar.getInstance();
        sixmonths.add(Calendar.MONTH, -6);

        return dob.before(sixmonths);
    }

    /**
     * Calculates the old AmountDonatedOnEntry field - this is done by totalling
     * all donations that are within a few days of the brought in date
     * and attached to this animal
     */
    public double getAmountDonatedOnEntry() throws Exception {
        return DBConnection.executeForDouble(
            "SELECT SUM(Donation) FROM ownerdonation " + "WHERE AnimalID = " +
            getID() + " AND " + "Date = '" +
            Utils.getSQLDate(getDateBroughtIn()) + "'");
    }

    /**
     * Determines if this animal is on the shelter.
     *
     * @return True if the animal is on the shelter.
     */
    public boolean isAnimalOnShelter() {
        try {
            int loc = getAnimalLocationAtDate(new Date());

            // If the non-shelter animal flag is set, it is
            // automatically off the shelter
            if (getNonShelterAnimal().intValue() == 1) {
                return false;
            }

            // If the option is switched on, the animal is on the shelter
            // if it is fostered.
            if (Configuration.getBoolean("FosterOnShelter")) {
                return (loc == ONSHELTER) || (loc == FOSTERED) ||
                (loc == RETAILER);
            } else {
                return (loc == ONSHELTER) || (loc == RETAILER);
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    public boolean fastIsAnimalOnShelter() {
        try {
            return getArchived().intValue() == 0;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    public boolean fastIsAnimalOnFoster() {
        try {
            return ((getActiveMovementID().intValue() != 0) &&
            (getActiveMovementType().intValue() == Adoption.MOVETYPE_FOSTER));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    public boolean fastIsAnimalAtRetailer() {
        try {
            return ((getActiveMovementID().intValue() != 0) &&
            (getActiveMovementType().intValue() == Adoption.MOVETYPE_RETAILER));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    public boolean fastIsAnimalReserved() {
        try {
            return ((getActiveMovementID().intValue() != 0) &&
            (getActiveMovementType().intValue() == Adoption.MOVETYPE_RESERVATION));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    public boolean isAnimalOnFoster() {
        return FOSTERED == getAnimalLocationAtDate(new Date());
    }

    public boolean isAnimalAtRetailer() {
        return RETAILER == getAnimalLocationAtDate(new Date());
    }

    /**
     * Returns the number of animals on the shelter of a given type/species at a
     * particular date and optionally in a particular location for animals of a
     * particular age.
     *
     * @param atdate
     *            The date you want to count animals on in MySQL format.
     * @param speciesid
     *            The species of animal you are interested in, or 0 to use the
     *            type instead.
     * @param animaltypeid
     *            The type of animal you are interested in, or 0 to use the
     *            species.
     * @param internallocationid
     *            The internal location you want to count animals at, or 0 for
     *            all locations.
     * @param ageselection
     *            The age of the animals you are interested in - either
     *            Animal.UNDERSIXMONTHS, Animal.OVERSIXMONTHS or Animal.ALLAGES
     * @return The number of animals as an integer.
     */
    public static int getNumberOfAnimalsOnShelter(Date datdate, int speciesid,
        int animaltypeid, int internallocationid, int ageselection) {
        try {
            String atdate = SQLRecordset.getSQLRepresentationOfDate(datdate);
            String sql = "SELECT COUNT(ID) FROM animal WHERE ";

            if (speciesid != 0) {
                sql = sql + "SpeciesID = " + speciesid;
            } else {
                sql = sql + "AnimalTypeID = " + animaltypeid;
            }

            sql = sql + " AND DateBroughtIn <= '" + atdate +
                "' AND NonShelterAnimal = 0";
            sql = sql + " AND (DeceasedDate > '" + atdate +
                "' OR DeceasedDate Is Null)";

            if (internallocationid != 0) {
                sql = sql + " AND ShelterLocation = " + internallocationid;
            }

            if (ageselection == UNDERSIXMONTHS) {
                sql = sql + " AND DateOfBirth >= '" +
                    SQLRecordset.getSQLRepresentationOfDate(new Date(
                            Utils.subtractMonths(Utils.dateToCalendar(datdate),
                                6).getTimeInMillis())) + "'";
            }

            if (ageselection == OVERSIXMONTHS) {
                sql = sql + " AND DateOfBirth < '" +
                    SQLRecordset.getSQLRepresentationOfDate(new Date(
                            Utils.subtractMonths(Utils.dateToCalendar(datdate),
                                6).getTimeInMillis())) + "'";
            }

            // Find movement records that would exclude this
            // animal - ie. returned after our date
            sql = sql + " AND 0 = " +
                "(SELECT COUNT(adoption.ID) FROM adoption " +
                "WHERE AnimalID = animal.ID AND " +
                "MovementDate Is Not Null AND MovementDate < '" + atdate + "'" +
                " AND (ReturnDate Is Null Or ReturnDate > '" + atdate + "'))";

            return DBConnection.executeForCount(sql);
        } catch (Exception e) {
            Global.logException(e, Animal.class);

            return 0;
        }
    }

    /** OLD CODE
     * public static int getNumberOfAnimalsOnShelter(Date datdate, int speciesid,
           int animaltypeid, int internallocationid, int ageselection) {

       try {

           String atdate = SQLRecordset.getSQLRepresentationOfDate(datdate);

           SQLRecordset rs = new SQLRecordset();
           String sql = "SELECT ID FROM animal WHERE ";

           if (speciesid != 0)
               sql = sql + "SpeciesID = " + speciesid;
           else
               sql = sql + "AnimalTypeID = " + animaltypeid;

           sql = sql + " AND DateBroughtIn <= '" + atdate
                   + "' AND NonShelterAnimal = 0";
           sql = sql + " AND (DeceasedDate > '" + atdate
                   + "' OR DeceasedDate Is Null)";

           if (internallocationid != 0)
               sql = sql + " AND ShelterLocation = " + internallocationid;

           if (ageselection == UNDERSIXMONTHS)
               sql = sql
                       + " AND DateOfBirth >= '"
                       + SQLRecordset.getSQLRepresentationOfDate(new Date(
                               Utils.subtractMonths(
                                       Utils.dateToCalendar(datdate), 6)
                                       .getTimeInMillis())) + "'";

           if (ageselection == OVERSIXMONTHS)
               sql = sql
                       + " AND DateOfBirth < '"
                       + SQLRecordset.getSQLRepresentationOfDate(new Date(
                               Utils.subtractMonths(
                                       Utils.dateToCalendar(datdate), 6)
                                       .getTimeInMillis())) + "'";

           rs.openRecordset(sql, "animal");
           int totanimals = 0;

           while (!rs.getEOF()) {

               // Find any adoption records the animal has where they aren't
               // returned or returned after this date.
               // We also need to filter to make sure there is an exit of
               // some type on the record, otherwise cancelled reservations
               // could cause
               // an animal to simultaneously be on and off the shelter.
               int excluders = DBConnection.executeForCount(
                       "SELECT COUNT(ID) FROM adoption WHERE AnimalID = "
                       + rs.getField("ID")
                       + " AND (ReturnDate Is Null Or ReturnDate > '" + atdate
                       + "') AND (MovementDate <= '" + atdate + "'"
                       + " AND MovementDate Is Not Null)");

               // If no records were found, the animal must be on the shelter.
               if (excluders == 0)
                   totanimals++;

               rs.moveNext();
           }

           return totanimals;
       }
       catch (Exception e) {
           Global.logException(e, Animal.class);
           return 0;
       }
    }
     */

    /**
     * Returns the number of animals on foster.
     *
     * @param atdate
     *            The date as a MySQL date to find at.
     * @param speciesid
     *            The species you are interested in, or 0 to use animaltypeid
     * @param animaltypeid
     *            The animal type you are interested in or 0 to use speciesid
     * @return The number of animals on foster.
     */
    public static int getNumberOfAnimalsOnFoster(Date datdate, int speciesid,
        int animaltypeid) {
        String atdate = SQLRecordset.getSQLRepresentationOfDate(datdate);

        String sql = "SELECT COUNT(adoption.ID) FROM adoption " +
            "INNER JOIN animal ON animal.ID = adoption.AnimalID WHERE ";

        if (speciesid != 0) {
            sql = sql + "SpeciesID = " + speciesid;
        } else {
            sql = sql + "AnimalTypeID = " + animaltypeid;
        }

        sql = sql + " AND DateBroughtIn <= '" + atdate + "'";
        sql = sql + " AND (DeceasedDate > '" + atdate +
            "' OR DeceasedDate Is Null)";
        sql = sql + " AND MovementType = " +
            Integer.toString(Adoption.MOVETYPE_FOSTER);
        sql = sql + " AND MovementDate <= '" + atdate +
            "' AND (ReturnDate > '" + atdate + "' OR ReturnDate Is Null)";

        try {
            return DBConnection.executeForCount(sql);
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }

        return 0;
    }

    /**
     * Identical to getAnimalLocationAtDate, except this routine returns the
     * name of the location instead of a constant Use empty string for today's
     * date.
     *
     * @param atdate
     *            A valid MySQL date or an empty string for today
     * @return A string representing the name of a logical location
     */
    public String getAnimalLocationAtDateByName(Date atdate) {
        int theloc = getAnimalLocationAtDate(atdate);

        switch (theloc) {
        case ADOPTED:
            return Global.i18n("bo", "Adopted");

        case DEAD:
            return Global.i18n("bo", "Dead");

        case ESCAPED:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_ESCAPED));

        case FOSTERED:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_FOSTER));

        case ONSHELTER:
            return Global.i18n("bo", "On_Shelter");

        case RECLAIMED:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_RECLAIMED));

        case RELEASEDTOWILD:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_RELEASED));

        case STOLEN:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_STOLEN));

        case TRANSFERRED:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_TRANSFER));

        case RETAILER:
            return LookupCache.getMoveTypeNameForID(new Integer(
                    Adoption.MOVETYPE_RETAILER));

        case NONSHELTER:
            return Global.i18n("bo", "Non-Shelter_Animal");

        default:
            return Global.i18n("bo", "Unknown");
        }
    }

    public static String fastGetAnimalLocationNowByName(
        Integer nonShelterAnimal, Integer activeMovementID,
        Integer activeMovementType, Date deceasedDate) {
        try {
            if (nonShelterAnimal.intValue() == 1) {
                return Global.i18n("bo", "Non-Shelter_Animal");
            }

            if (deceasedDate != null) {
                return Global.i18n("bo", "Dead");
            } else if ((activeMovementID.intValue() == 0) ||
                    (activeMovementType.intValue() == Adoption.MOVETYPE_RESERVATION) ||
                    (activeMovementType.intValue() == Adoption.MOVETYPE_CANCRESERVATION)) {
                return Global.i18n("bo", "On_Shelter");
            } else {
                switch (activeMovementType.intValue()) {
                case Adoption.MOVETYPE_ADOPTION:
                    return Global.i18n("bo", "Adopted");

                case Adoption.MOVETYPE_ESCAPED:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_ESCAPED));

                case Adoption.MOVETYPE_FOSTER:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_FOSTER));

                case Adoption.MOVETYPE_RECLAIMED:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_RECLAIMED));

                case Adoption.MOVETYPE_RELEASED:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_RELEASED));

                case Adoption.MOVETYPE_STOLEN:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_STOLEN));

                case Adoption.MOVETYPE_TRANSFER:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_TRANSFER));

                case Adoption.MOVETYPE_RETAILER:
                    return LookupCache.getMoveTypeNameForID(new Integer(
                            Adoption.MOVETYPE_RETAILER));

                default:
                    return LookupCache.getMoveTypeNameForID(activeMovementType);

                    // return Global.i18n("bo", "Unknown");
                }
            }
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }

        return "";
    }

    public String fastGetAnimalLocationNowByName() throws CursorEngineException {
        return fastGetAnimalLocationNowByName(getNonShelterAnimal(),
            getActiveMovementID(), getActiveMovementType(), getDeceasedDate());
    }

    public int fastGetAnimalLocationNow() {
        try {
            if (getNonShelterAnimal().intValue() == 1) {
                return NONSHELTER;
            }

            if (getDeceasedDate() != null) {
                return DEAD;
            } else if ((getActiveMovementID().intValue() == 0) ||
                    (getActiveMovementType().intValue() == Adoption.MOVETYPE_RESERVATION) ||
                    (getActiveMovementType().intValue() == Adoption.MOVETYPE_CANCRESERVATION)) {
                return ONSHELTER;
            } else {
                switch (getActiveMovementType().intValue()) {
                case Adoption.MOVETYPE_ADOPTION:
                    return ADOPTED;

                case Adoption.MOVETYPE_ESCAPED:
                    return ESCAPED;

                case Adoption.MOVETYPE_FOSTER:
                    return FOSTERED;

                case Adoption.MOVETYPE_RECLAIMED:
                    return RECLAIMED;

                case Adoption.MOVETYPE_RELEASED:
                    return RELEASEDTOWILD;

                case Adoption.MOVETYPE_STOLEN:
                    return STOLEN;

                case Adoption.MOVETYPE_TRANSFER:
                    return TRANSFERRED;

                case Adoption.MOVETYPE_RETAILER:
                    return RETAILER;

                default:
                    return -1;
                }
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return -1;
    }

    /**
     * Given a list of movement records, this routine will find the latest,
     * based on the reservation/movement dates and leave the cursor pointing to
     * it.
     */
    public static void findLatestMovementFromList(Adoption ad)
        throws CursorEngineException {
        int highestID = 0;
        Calendar highestDate = null;
        Calendar testDate = null;

        boolean hasMovement = false;

        while (!ad.getEOF()) {
            if (ad.getMovementDate() != null) {
                hasMovement = true;
                testDate = Utils.dateToCalendar(ad.getMovementDate());
            } else if (ad.getReservationDate() != null) {
                testDate = Utils.dateToCalendar(ad.getReservationDate());
            } else {
                testDate = null;
            }

            if (testDate != null) {
                if (highestDate == null) {
                    highestDate = testDate;
                    highestID = ad.getID().intValue();
                } else if (testDate.after(highestDate)) {
                    highestDate = testDate;
                    highestID = ad.getID().intValue();
                }
            }

            ad.moveNext();
        }

        if (highestID != 0) {
            ad.moveFirst();

            while (!ad.getEOF()) {
                if (ad.getID().intValue() == highestID) {
                    break;
                }

                ad.moveNext();
            }
        }

        // This is a special case - if the last record is a reservation
        // and we know there's a real movement, count backwards to the real
        // movement.
        if (!ad.getEOF()) {
            if ((ad.getReservationDate() != null) &&
                    (ad.getMovementDate() == null) && hasMovement) {
                while (!ad.getBOF()) {
                    if (ad.getMovementDate() != null) {
                        return;
                    }

                    ad.movePrevious();
                }
            }
        }
    }

    /**
     * Updates the following fields on an animal's record according to current
     * data.
     *
     * ActiveMovement* HasActiveReserve MostRecentEntryDate Archived ( ! On
     * shelter)
     *
     */
    public static void updateAnimalStatus(int animalID) {
        try {
            // Find the highest return date from the list if
            // there is one. This is handy so we know the last
            // date the animal came into the shelter. We make
            // it null if the animal isn't on the shelter anyway.
            Adoption ad = new Adoption();
            ad.openRecordset("AnimalID = " + animalID +
                " AND ReturnDate Is Not Null ORDER BY ReturnDate DESC");

            if (ad.getEOF()) {
                String sql = "UPDATE animal SET ActiveMovementReturn = Null WHERE ID = " +
                    animalID;
                DBConnection.executeAction(sql);
            } else {
                String sql = "UPDATE animal SET ActiveMovementReturn = '" +
                    Utils.getSQLDate(ad.getReturnDate()) + "' WHERE ID = " +
                    animalID;
                DBConnection.executeAction(sql);
            }

            ad.free();
            ad = null;

            // Filter so we only get animals that are off the shelter today for
            // the active movement list.
            String myToday = SQLRecordset.getSQLRepresentationOfDate(new Date());
            ad = new Adoption();
            ad.openRecordset("AnimalID = " + animalID +
                " AND (ReturnDate Is Null OR " + "ReturnDate > '" + myToday +
                "') ORDER BY ID"); // Fixes MySQL bug that can records out of order

            if (ad.getEOF()) {
                String sql = "UPDATE animal SET ActiveMovementID = 0, ActiveMovementDate = Null, " +
                    "ActiveMovementType = Null WHERE ID = " + animalID;
                DBConnection.executeAction(sql);
            } else {
                findLatestMovementFromList(ad);

                // Resort to last record if EOF returned - this occurs
                // if the only records are empty (no movement or
                // reservation)
                if (ad.getEOF()) {
                    ad.moveLast();
                }

                int movetype = 0;
                int aid = 0;
                String amd = "null";
                String rd = "null";

                // Don't bother looking up movetype/dates if there are no
                // movements
                if (!ad.getEOF() && !ad.getBOF()) {
                    movetype = ad.getMovementType().intValue();

                    if (movetype == 0) {
                        if ((ad.getReservationDate() != null) &&
                                (ad.getReservationCancelledDate() == null)) {
                            movetype = Adoption.MOVETYPE_RESERVATION;
                        }

                        if ((ad.getReservationDate() != null) &&
                                (ad.getReservationCancelledDate() != null)) {
                            movetype = Adoption.MOVETYPE_CANCRESERVATION;
                        }
                    }

                    // Make sure we handle null dates
                    if (ad.getMovementDate() != null) {
                        amd = "'" +
                            SQLRecordset.getSQLRepresentationOfDate(ad.getMovementDate()) +
                            "'";
                    }

                    if (ad.getReturnDate() != null) {
                        rd = "'" +
                            SQLRecordset.getSQLRepresentationOfDate(ad.getReturnDate()) +
                            "'";
                    }

                    aid = ad.getID().intValue();
                }

                String sql = "UPDATE animal SET ActiveMovementID = " + aid +
                    ", ActiveMovementDate = " + amd + ", " +
                    "ActiveMovementType = " + movetype +
                    ", ActiveMovementReturn = " + rd + " WHERE ID = " +
                    animalID;
                DBConnection.executeAction(sql);
            }

            ad.free();
            ad = null;

            // Check whether the animal is archived based on the on shelter
            // and update it accordingly.
            Animal an = new Animal();
            an.openRecordset("ID = " + animalID);

            String sql = "UPDATE animal SET Archived = " +
                (an.isAnimalOnShelter() ? "0" : "1") + " WHERE ID = " +
                animalID;
            DBConnection.executeAction(sql);

            // Check whether the animal has an active reserve
            sql = "UPDATE animal SET HasActiveReserve = " +
                (an.isAnimalReserved() ? "1" : "0") + " WHERE ID = " +
                animalID;
            DBConnection.executeAction(sql);

            // Update age, time on shelter, etc.
            updateVariableAnimalData(an);

            an.free();
            an = null;
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }
    }

    /** Updates variable animal data (time on shelter, age, etc)
      * for all on shelter animals.
      */
    public static void updateOnShelterVariableAnimalData() {
        try {
            // This only needs to be checked once per day, so see when it was
            // last run
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String today = df.format(new Date());
            String lastrun = Configuration.getString("VariableAnimalDataUpdated",
                    "0");

            if (today.equals(lastrun)) {
                return;
            }

            Configuration.setEntry("VariableAnimalDataUpdated", today);

            Global.logInfo("Updating variable animal data...",
                "Animal.updateOnShelterVariableAnimalData");

            Animal a = new Animal();
            a.openRecordset("Archived = 0");

            while (!a.getEOF()) {
                updateVariableAnimalData(a);
                a.moveNext();
            }

            a.free();
            a = null;
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }
    }

    /** Updates various cached animal dates that can change daily, such
      * as time on shelter and current age
      * @param an The animal record to update
      */
    public static void updateVariableAnimalData(Animal an) {
        try {
            String sql = "UPDATE animal SET " + "MostRecentEntryDate = '" +
                Utils.getSQLDate(an.getMostRecentEntry()) + "', " +
                "TimeOnShelter = '" + an.getTimeOnShelter() + "', " +
                "AgeGroup = '" + an.calculateAgeGroup() + "', " +
                "AnimalAge = '" + an.getAge() + "', " + "DaysOnShelter = '" +
                an.getDaysOnShelter() + "' " + "WHERE ID = " + an.getID();

            DBConnection.executeAction(sql);
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }
    }

    /**
     * Returns a constant representing where the animal is currently located.
     *
     * @param atdate
     *            The date you are interested in on the animal or an empty
     *            string for today's date.
     * @return A constant representing the animal's location.
     */
    public int getAnimalLocationAtDate(Date atdate) {
        try {
            // If it's a non-shelter animal, say so now
            if (getNonShelterAnimal().intValue() == 1) {
                return NONSHELTER;
            }

            if (atdate == null) {
                atdate = new Date();
            }

            Calendar catdate = Utils.dateToCalendar(atdate);

            if (getDeceasedDate() != null) {
                if (Utils.dateToCalendar(getDeceasedDate()).before(catdate)) {
                    return DEAD;
                }
            }

            Adoption adoption = new Adoption();

            /*
             * Find any movement records the animal has where they aren't '
             * returned or returned after this date. ' We also need to filter to
             * make sure there is an exit of ' some type on the record,
             * otherwise cancelled reservations could cause ' an animal to
             * simultaneously be on and off the shelter.
             */
            adoption.openRecordset("AnimalID = " + getID() +
                " AND (ReturnDate Is Null Or ReturnDate > '" +
                Utils.getSQLDate(atdate) + "')" +
                " AND (MovementType > 0 AND MovementDate <= '" +
                Utils.getSQLDate(atdate) + "' AND MovementDate Is Not Null)");

            if (!adoption.getEOF()) {
                int moveType = adoption.getMovementType().intValue();

                adoption.free();
                adoption = null;

                switch (moveType) {
                case Adoption.MOVETYPE_ADOPTION:
                    return ADOPTED;

                case Adoption.MOVETYPE_FOSTER:
                    return FOSTERED;

                case Adoption.MOVETYPE_TRANSFER:
                    return TRANSFERRED;

                case Adoption.MOVETYPE_STOLEN:
                    return STOLEN;

                case Adoption.MOVETYPE_ESCAPED:
                    return ESCAPED;

                case Adoption.MOVETYPE_RELEASED:
                    return RELEASEDTOWILD;

                case Adoption.MOVETYPE_RECLAIMED:
                    return RECLAIMED;

                case Adoption.MOVETYPE_RETAILER:
                    return RETAILER;
                }
            }

            // Animal must be on shelter
            return ONSHELTER;
        } catch (net.sourceforge.sheltermanager.cursorengine.CursorEngineException e) {
            Global.logException(e, getClass());

            return ONSHELTER;
        }
    }

    /**
     * Determines if the animal is currently reserved.
     *
     * @return true if the animal is reserved.
     */
    public boolean isAnimalReserved() {
        try {
            Adoption adoption = new Adoption();
            adoption.openRecordset("AnimalID = " + getID() +
                " AND ReturnDate Is Null" +
                " AND MovementType = 0 AND MovementDate Is Null" +
                " AND ReservationCancelledDate Is Null AND ReservationDate Is Not Null");

            boolean isReserved = !adoption.getEOF();
            adoption.free();
            adoption = null;

            return isReserved;
        } catch (net.sourceforge.sheltermanager.cursorengine.CursorEngineException e) {
            Global.logException(e, getClass());

            return false;
        }
    }

    /**
     * Returns true if this animal is part of a litter.
     *
     * @return True if the animal is part of a litter.
     */
    public boolean isPartOfLitter() {
        try {
            AnimalLitter li = new AnimalLitter();
            li.openRecordset("AcceptanceNumber Like '" +
                this.getAcceptanceNumber() + "'");

            boolean isPart = !li.getEOF();
            li.free();
            li = null;

            return isPart;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the most recent date the animal came back into the shelter -
     * either the last return from an adoption record or the date brought in.
     *
     * @return The most recent entry date in MySQL format.
     */
    public Date getMostRecentEntry() {
        try {
            Adoption adoption = new Adoption();
            adoption.openRecordset("AnimalID = " + getID() +
                (Configuration.getBoolean("FosterOnShelter")
                ? (" AND MovementType <> " + Adoption.MOVETYPE_FOSTER) : "") +
                " ORDER BY ReturnDate");

            if (!adoption.getEOF()) {
                adoption.moveLast();

                if (adoption.getReturnDate() != null) {
                    return adoption.getReturnDate();
                } else {
                    return getDateBroughtIn();
                }
            } else {
                return getDateBroughtIn();
            }
        } catch (net.sourceforge.sheltermanager.cursorengine.CursorEngineException e) {
            Global.logException(e, getClass());

            return null;
        }
    }

    /**
     * Returns the animal's time on shelter as a formatted string. If it is
     * under 6 months, the age is returned in weeks, otherwise it is
     * returned in years and months
     */
    public String getTimeOnShelter() throws CursorEngineException {
        // Get animal's most recent entry date
        Calendar mre = Utils.dateToCalendar(getMostRecentEntry());

        // Stop counting at today
        Calendar stopat = Calendar.getInstance();

        // If the animal is dead, stop counting at the date of death instead
        if (getDeceasedDate() != null) {
            stopat.setTime(getDeceasedDate());
        }

        // Work out what 16 weeks from stop point was
        Calendar sixteenweeks = (Calendar) stopat.clone();
        sixteenweeks.add(Calendar.WEEK_OF_YEAR, -16);

        // If most recent entry is after 16 weeks ago,
        // format time in weeks
        if (mre.after(sixteenweeks)) {
            long diff = Utils.getDateDiff(stopat, mre);

            // It's currently returned in minutes, so calculate weeks
            // by dividing by 60 (hours), 24 (day), 7 (week)
            diff = (diff / 60);
            diff = (diff / 24);
            diff = (diff / 7);

            return Global.i18n("bo", "x_weeks", Long.toString(diff));
        } else {
            // Otherwise format in months and years
            long diff = Utils.getDateDiff(stopat, mre);
            // It's currently returned in minutes, so calculate weeks
            // by dividing by 60 (hours), 24 (day), 7 (week)
            diff = (diff / 60);
            diff = (diff / 24);
            diff = (diff / 7);

            // Calculate how many years and months this is
            long years = (diff / 52);
            long months = diff - (years * 52); // Remainder in weeks
            double mo = ((double) months / 52) * 12;
            months = (long) mo; // Calculate weeks as a 12-based percentage
                                // of 52 to get accurate months

            return Global.i18n("bo", "years_and_months", Long.toString(years),
                Long.toString(months));
        }
    }

    /**
     * Returns the animal's time on shelter in days
     */
    public int getDaysOnShelter() throws CursorEngineException {
        // Get animal's most recent entry date
        Calendar mre = Utils.dateToCalendar(getMostRecentEntry());

        // Stop counting at today
        Calendar stopat = Calendar.getInstance();

        // If the animal is dead, stop counting at the date of death instead
        if (getDeceasedDate() != null) {
            stopat.setTime(getDeceasedDate());
        }

        long diff = Utils.getDateDiff(stopat, mre);

        // Diff is returned in minutes, so turn it into days
        return (int) ((diff / 60) / 24);
    }

    /**
     * Returns the animal's age as a formatted string. If it is under 6 months,
     * the age is returned in weeks, otherwise it is returned in years and
     * months. If the animal is dead, the age stops there.
     * @param dateOfBirth The animal's date of birth
     * @param deceasedDate The date the animal died
     */
    public static String getAge(Date dateOfBirth, Date deceasedDate) {
        // Get animal's date of birth on calendar
        Calendar adob = Utils.dateToCalendar(dateOfBirth);

        // Stop counting at today
        Calendar stopat = Calendar.getInstance();

        // If the animal is dead, stop counting at the date of death instead
        if (deceasedDate != null) {
            stopat.setTime(deceasedDate);
        }

        // Work out what 16 weeks from stop point was
        Calendar sixteenweeks = (Calendar) stopat.clone();
        sixteenweeks.add(Calendar.WEEK_OF_YEAR, -16);

        // If date of birth is after 16 weeks ago,
        // format age in weeks
        if (adob.after(sixteenweeks)) {
            long diff = Utils.getDateDiff(stopat, adob);

            // It's currently returned in minutes, so calculate weeks
            // by dividing by 60 (hours), 24 (day), 7 (week)
            diff = (diff / 60);
            diff = (diff / 24);
            diff = (diff / 7);

            return Global.i18n("bo", "x_weeks", Long.toString(diff));
        } else {
            // Otherwise format in months and years
            long diff = Utils.getDateDiff(stopat, adob);
            // It's currently returned in minutes, so calculate weeks
            // by dividing by 60 (hours), 24 (day), 7 (week)
            diff = (diff / 60);
            diff = (diff / 24);
            diff = (diff / 7);

            // Calculate how many years and months this is
            long years = (diff / 52);
            long months = diff - (years * 52); // Remainder in weeks
            double mo = ((double) months / 52) * 12;
            months = (long) mo; // Calculate weeks as a 12-based percentage
                                // of 52 to get accurate months

            return Global.i18n("bo", "years_and_months", Long.toString(years),
                Long.toString(months));
        }
    }

    /**
     * Returns the animal's age as a formatted string. If it is under 6 months,
     * the age is returned in weeks, otherwise it is returned in years and
     * months
     */
    public String getAge() throws CursorEngineException {
        return getAge(getDateOfBirth(), getDeceasedDate());
    }

    /**
     * Returns true if this animal has a valid media record with a picture
     */
    public boolean hasValidMedia() throws CursorEngineException {
        Media med = new Media();
        med.openRecordset("LinkID = " + getID() + " AND LinkTypeID = " +
            Integer.toString(Media.LINKTYPE_ANIMAL) +
            " AND (UPPER(MediaName) Like '%.JPG' " +
            " OR UPPER(MediaName) Like '%.JPEG'" +
            " OR UPPER(MediaName) Like '%.GIF' " +
            " OR UPPER(MediaName) Like '%.PNG')");

        boolean hasValid = !med.getEOF();
        med.free();
        med = null;

        return hasValid;
    }

    /**
     * Calculates and returns the number of times an animal has been returned
     * from adoption.
     *
     * @return The number of returned adoptions on file for this animal.
     * @throws CursorEngineException
     *             If an error occurs reading data.
     */
    public String getNoTimesReturned() throws CursorEngineException {
        Adoption ad = new Adoption();
        ad.openRecordset("AnimalID = " + getID() + " AND MovementType = " +
            Integer.toString(Adoption.MOVETYPE_ADOPTION) +
            " AND MovementDate Is Not Null AND ReturnDate Is Not Null");

        return Long.toString(ad.getRecordCount());
    }

    /**
     * Returns the name of the file in their media directory that the animal
     * should use for documents. If none has the doc preferred option set,
     * the first image will be used. This information is cached within the
     * client object
     */
    public String getDocMedia() throws CursorEngineException {
        Media med = new Media();
        med.openRecordset("LinkID = " + getID() + " AND LinkTypeID = " +
            Integer.toString(Media.LINKTYPE_ANIMAL) +
            " AND (UPPER(MediaName) Like '%.JPG' " +
            " OR UPPER(MediaName) Like '%.JPEG'" +
            " OR UPPER(MediaName) Like '%.GIF' " +
            " OR UPPER(MediaName) Like '%.PNG')");

        if (med.getEOF()) {
            return "";
        }

        while (!med.getEOF()) {
            if (med.getDocPhoto().intValue() == 1) {
                preferredDocMedia = med;

                return med.getMediaName();
            }

            med.moveNext();
        }

        // No preferred was found - use the first
        med.moveFirst();
        preferredDocMedia = med;

        return med.getMediaName();
    }

    /**
     * Returns the name of the file in their media directory that the animal
     * should use for web publishing. If none has the web preferred option set,
     * the first image will be used. This information is cached within the
     * client object, so calling this, followed by getWebMediaNotes will return
     * the correct notes as well
     */
    public String getWebMedia() throws CursorEngineException {
        Media med = new Media();
        med.openRecordset("LinkID = " + getID() + " AND LinkTypeID = " +
            Integer.toString(Media.LINKTYPE_ANIMAL) +
            " AND (UPPER(MediaName) Like '%.JPG' " +
            " OR UPPER(MediaName) Like '%.JPEG'" +
            " OR UPPER(MediaName) Like '%.GIF' " +
            " OR UPPER(MediaName) Like '%.PNG')");

        if (med.getEOF()) {
            return "";
        }

        while (!med.getEOF()) {
            if (med.getWebSitePhoto().intValue() == 1) {
                preferredWebMedia = med;

                return med.getMediaName();
            }

            med.moveNext();
        }

        // No preferred was found - use the first
        med.moveFirst();
        preferredWebMedia = med;

        return med.getMediaName();
    }

    /**
     * Updates the notes for the animals web media
     */
    public void updateWebMediaNotes(String newNotes)
        throws CursorEngineException {
        // Get the web media and drop out if there isn't one
        if (getWebMedia().equals("")) {
            return;
        }

        // Update the notes
        preferredWebMedia.setMediaNotes(newNotes);
        preferredWebMedia.save();
    }

    /**
     * Returns the notes to accompany the last call to getWebMedia
     */
    public String getWebMediaNotes() throws CursorEngineException {
        if (preferredWebMedia == null) {
            return "";
        }

        return preferredWebMedia.getMediaNotes();
    }

    public boolean isWebMediaNew() throws CursorEngineException {
        if (preferredWebMedia == null) {
            return false;
        }

        return preferredWebMedia.getNewSinceLastPublish().intValue() == 1;
    }

    public boolean isWebMediaUpdated() throws CursorEngineException {
        if (preferredWebMedia == null) {
            return false;
        }

        return preferredWebMedia.getUpdatedSinceLastPublish().intValue() == 1;
    }

    public void clearWebMediaFlags() throws CursorEngineException {
        if (preferredWebMedia == null) {
            return;
        }

        preferredWebMedia.setNewSinceLastPublish(new Integer(0));
        preferredWebMedia.setUpdatedSinceLastPublish(new Integer(0));
        preferredWebMedia.save();
    }

    public void addNew() throws CursorEngineException {
        super.addNew();

        final Integer z = new Integer(0);

        setAnimalName("");
        setMarkings("");
        setReasonForEntry("");
        setAnimalComments("");
        setBroughtInByOwnerID(z);
        setOriginalOwnerID(z);
        setCrossBreed(z);
        setCoatType(z);

        setCurrentVetID(z);
        setOwnersVetID(z);
        setDateOfBirth(new Date());
        setEstimatedDOB(z);
        setAgeGroup("");
        setDateBroughtIn(new Date());
        setMostRecentEntryDate(new Date());

        setSpeciesID(LookupCache.getFirstID(LookupCache.getAnimalTypeLookup()));
        setBreedID(LookupCache.getFirstID(LookupCache.getBreedLookup()));
        setBreed2ID(LookupCache.getFirstID(LookupCache.getBreedLookup()));
        setBreedName(LookupCache.getBreedName(getBreedID()));
        setAnimalTypeID(LookupCache.getFirstID(
                LookupCache.getAnimalTypeLookup()));
        setShelterLocation(LookupCache.getFirstID(
                LookupCache.getInternalLocationLookup()));
        setBaseColourID(LookupCache.getFirstID(
                LookupCache.getBaseColourLookup()));
        setPTSReasonID(LookupCache.getFirstID(
                LookupCache.getDeathReasonLookup()));
        setEntryReasonID(LookupCache.getFirstID(
                LookupCache.getEntryReasonLookup()));

        // Generate default code for animal type
        AnimalCode ac = generateAnimalCode(LookupCache.getAnimalTypeName(
                    getAnimalTypeID()), getDateBroughtIn());
        setShelterCode(ac.code);
        setShortCode(ac.shortcode);

        setCombiTestResult(z);
        setFLVTestResult(z);
        setGoodWithCats(z);
        setGoodWithDogs(z);
        setGoodWithKids(z);
        setHouseTrained(z);
        setHeartwormTestResult(z);
        setCombiTested(z);
        setIsDOA(z);
        setDiedOffShelter(z);
        setMicrochipped(z);
        setTattoo(z);
        setDeclawed(z);
        setNeutered(z);
        setPutToSleep(z);
        setIsTransfer(z);
        setNonShelterAnimal(z);
        setCrueltyCase(z);
        setIsNotAvailableForAdoption(z);
        setHeartwormTested(z);
        setHasSpecialNeeds(z);

        setArchived(z);
        setDailyBoardingCost(new Double(0));
        setActiveMovementID(z);
        setHasActiveReserve(z);
    }

    /** Clones an animal and all its satellite records (with the exception
     *  of media). Because of this, the cloned animal has to be saved
     *  before it is returned from this method.
     */
    public Animal copy() throws Exception {
        Animal a = new Animal();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setAcceptanceNumber(getAcceptanceNumber());
        a.setAnimalComments(getAnimalComments());
        a.setAnimalName(Global.i18n("bo", "copy_of", getAnimalName()));
        a.setAnimalTypeID(getAnimalTypeID());
        a.setBaseColourID(getBaseColourID());
        a.setBreedID(getBreedID());
        a.setBreed2ID(getBreed2ID());
        a.setBreedName(getBreedName());
        a.setCoatType(getCoatType());
        a.setCombiTestDate(getCombiTestDate());
        a.setCombiTested(getCombiTested());
        a.setCrossBreed(getCrossBreed());
        a.setCrueltyCase(getCrueltyCase());
        a.setDailyBoardingCost(getDailyBoardingCost());
        a.setDateBroughtIn(getDateBroughtIn());
        a.setDateOfBirth(getDateOfBirth());
        a.setEstimatedDOB(getEstimatedDOB());
        a.setAgeGroup(getAgeGroup());
        a.setHealthProblems(getHealthProblems());
        a.setHiddenAnimalDetails(getHiddenAnimalDetails());
        a.setMarkings(getMarkings());
        a.setCurrentVetID(getCurrentVetID());
        a.setOwnersVetID(getOwnersVetID());
        a.setNeutered(getNeutered());
        a.setNeuteredDate(getNeuteredDate());
        a.setReasonForEntry(getReasonForEntry());
        a.setReasonNO(getReasonNO());
        a.setSex(getSex());
        a.setSize(getSize());
        a.setShelterLocation(getShelterLocation());
        a.setSpeciesID(getSpeciesID());
        a.setOriginalOwnerID(getOriginalOwnerID());
        a.setBroughtInByOwnerID(getBroughtInByOwnerID());
        a.setEntryReasonID(getEntryReasonID());
        a.setShelterLocation(getShelterLocation());
        a.setIsTransfer(getIsTransfer());
        a.setDeceasedDate(null);
        a.setNonShelterAnimal(getNonShelterAnimal());
        a.setPTSReasonID(getPTSReasonID());
        a.setCombiTestResult(getCombiTestResult());
        a.setFLVTestResult(getFLVTestResult());
        a.setHeartwormTestResult(getHeartwormTestResult());
        a.setHeartwormTested(getHeartwormTested());
        a.setHeartwormTestDate(getHeartwormTestDate());
        a.setGoodWithCats(isGoodWithCats());
        a.setGoodWithDogs(isGoodWithDogs());
        a.setGoodWithKids(isGoodWithKids());
        a.setHouseTrained(isHouseTrained());
        a.setIsNotAvailableForAdoption(getIsNotAvailableForAdoption());
        a.setHasSpecialNeeds(isHasSpecialNeeds());
        a.setMostRecentEntryDate(getMostRecentEntryDate());
        a.setIsDOA(new Integer(0));
        a.setDiedOffShelter(new Integer(0));
        a.setMicrochipped(new Integer(0));
        a.setTattoo(new Integer(0));
        a.setNeutered(getNeutered());
        a.setDeclawed(getDeclawed());
        a.setPutToSleep(getPutToSleep());
        a.setNonShelterAnimal(getNonShelterAnimal());
        a.setArchived(new Integer(0));
        a.setActiveMovementID(new Integer(0));
        a.setActiveMovementType(new Integer(0));
        a.setHasActiveReserve(new Integer(0));

        // Generate codes
        AnimalCode ac = generateAnimalCode(LookupCache.getAnimalTypeName(
                    a.getAnimalTypeID()), a.getDateBroughtIn());
        a.setShelterCode(ac.code);
        a.setShortCode(ac.shortcode);

        // Save the animal
        a.save(Global.currentUserName);

        // Update its status
        updateAnimalStatus(a.getID().intValue());

        // Now do the satellite records

        // vaccinations
        AnimalVaccination vacc = new AnimalVaccination();
        vacc.openRecordset("AnimalID = " + getID());

        while (!vacc.getEOF()) {
            AnimalVaccination v = vacc.copy();
            v.setAnimalID(a.getID());
            v.save(Global.currentUserName);
            vacc.moveNext();
        }

        // medical
        AnimalMedical med = new AnimalMedical();
        med.openRecordset("AnimalID = " + getID());

        while (!med.getEOF()) {
            AnimalMedical m = med.copy();
            m.setAnimalID(a.getID());
            m.save(Global.currentUserName);

            AnimalMedicalTreatment medt = new AnimalMedicalTreatment();
            medt.openRecordset("AnimalMedicalID = " + med.getID());

            while (!medt.getEOF()) {
                AnimalMedicalTreatment mt = medt.copy();
                mt.setAnimalID(a.getID());
                mt.setAnimalMedicalID(m.getID());
                mt.save(Global.currentUserName);
                medt.moveNext();
            }

            med.moveNext();
        }

        // costs
        AnimalCost cost = new AnimalCost();
        cost.openRecordset("AnimalID = " + getID());

        while (!cost.getEOF()) {
            AnimalCost c = cost.copy();
            c.setAnimalID(a.getID());
            c.save(Global.currentUserName);
            cost.moveNext();
        }

        // diet
        AnimalDiet diet = new AnimalDiet();
        diet.openRecordset("AnimalID = " + getID());

        while (!diet.getEOF()) {
            AnimalDiet d = diet.copy();
            d.setAnimalID(a.getID());
            d.save(Global.currentUserName);
            diet.moveNext();
        }

        // movements
        Adoption move = new Adoption();
        move.openRecordset("AnimalID = " + getID());

        while (!move.getEOF()) {
            Adoption mo = move.copy();
            mo.setAnimalID(a.getID());
            mo.save(Global.currentUserName);
            move.moveNext();
        }

        // log
        Log log = new Log();
        log.openRecordset("LinkID = " + getID() + " AND LinkType = " +
            Log.LINKTYPE_ANIMAL);

        while (!log.getEOF()) {
            Log l = log.copy();
            l.setLinkID(a.getID());
            l.save(Global.currentUserName);
            log.moveNext();
        }

        // diary
        Diary diary = new Diary();
        diary.openRecordset("LinkID = " + getID() + " AND LinkType = " +
            Diary.LINKTYPE_ANIMAL);

        while (!diary.getEOF()) {
            Diary d = diary.copy();
            d.setLinkID(a.getID());
            d.save(Global.currentUserName);
            diary.moveNext();
        }

        return a;
    }

    /**
     * Returns the latest open movement record for an animal. If none is found,
     * then NULL is returned.
     *
     * @return An <code>Adoption</code> object containing the information.
     */
    public Adoption getLatestMovement() throws CursorEngineException {
        Adoption ad = new Adoption();
        ad.openRecordset("AnimalID = " + getID() + " AND " +
            "(MovementDate Is Not Null)" + " AND ReturnDate Is Null");

        if (ad.getEOF()) {
            return null;
        } else {
            ad.moveLast();

            return ad;
        }
    }

    /**
     * Parses the year from a code with a YYYY or YY portion
     */
    public static int parseAnimalCodeYear(String code) {
        String cf = Configuration.getString("CodingFormat");
        int year = 0;

        // 4 digit year
        int npos = cf.indexOf("YYYY");

        if ((npos != -1) && ((npos + 4) <= code.length())) {
            try {
                year = Integer.parseInt(code.substring(npos, npos + 4));
                Global.logDebug("Parsed year " + year + " from code " + code,
                    "Animal.parseAnimalCodeYear");

                return year;
            } catch (Exception e) {
                Global.logError("Failed parsing YYYY portion of " + code,
                    "Animal.parseAnimalCodeYear");
            }
        }

        // 2 digit year
        npos = cf.indexOf("YY");

        if ((npos != -1) && ((npos + 2) <= code.length())) {
            try {
                year = Integer.parseInt(code.substring(npos, npos + 2));
                // Use the pivot to modify the year
                year += ((year < Global.PIVOT_YEAR) ? Global.BELOW_PIVOT
                                                    : Global.AFTER_PIVOT);
                Global.logDebug("Parsed year " + year + " from code " + code,
                    "Animal.parseAnimalCodeYear");
            } catch (Exception e) {
                Global.logError("Failed parsing YY portion of " + code,
                    "Animal.parseAnimalCodeYear");
            }
        }

        return year;
    }

    /**
     * Parses an animal code to get the numeric portion. Uses the system
     * defined coding scheme to parse a code and retrieve the number
     * portion.
     */
    public static int parseAnimalCode(String code) {
        final boolean DEBUG = false;

        if (DEBUG) {
            Global.logDebug("Parsing animal code '" + code + "'",
                "parseAnimalCode");
        }

        String format = Global.getCodingFormat();
        String digits = "0123456789";

        int npos = format.indexOf("N");

        if (npos == -1) {
            npos = format.indexOf("U");
        }

        if (npos == -1) {
            if (DEBUG) {
                Global.logDebug("No numeric portion found - format is invalid.",
                    "parseAnimalCode");
            }

            return 0;
        }

        if (DEBUG) {
            Global.logDebug("Found numeric portion in " + format +
                ", starting at " + npos, "parseAnimalCode");
        }

        // Stop now if the format position is longer than the code -
        // we can't parse it.
        if (npos > code.length()) {
            if (DEBUG) {
                Global.logDebug("N position " + npos + " is longer than code " +
                    code + ", aborting.", "parseAnimalCode");
            }

            return 0;
        }

        String portion = "";
        int end = code.length();

        for (int i = npos; i < code.length(); i++) {
            String digit = code.substring(i, i + 1);

            if (digits.indexOf(digit) == -1) {
                end = i;

                break;
            }
        }

        portion = code.substring(npos, end);

        if (portion.equals("")) {
            return 0;
        }

        if (DEBUG) {
            Global.logDebug("Numeric portion = " + portion, "parseAnimalCode");
        }

        try {
            return Integer.parseInt(portion);
        } catch (Exception e) {
            if (DEBUG) {
                Global.logException(e, Animal.class);
            }

            return 0;
        }
    }

    /**
     * Given a query, finds the highest animal code by parsing each
     * shelter code field in the results.
     */
    public static int getHighest(String sql) {
        int current = 0;
        int maximum = 0;

        try {
            SQLRecordset r = new SQLRecordset();
            r.openRecordset(sql, "animal");

            while (!r.getEOF()) {
                current = parseAnimalCode((String) r.getField("ShelterCode"));

                if (current > maximum) {
                    maximum = current;
                }

                r.moveNext();
            }

            r.free();
            r = null;
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }

        return maximum;
    }

    /**
     * For a given shelter code, figures out the type and returns it,
     * or a blank string if the code wasn't parseable.
     */
    public static String getShelterCodeType(String code) {
        String format = Global.getCodingFormat();
        int tpos = format.indexOf("T");

        if ((code.length() == 0) || (code.length() < format.length())) {
            return "";
        }

        return code.substring(tpos, tpos + 1);
    }

    /**
     * Generates a code for an animal. This is a fast variant that uses
     * pre-parsed codes for speed and to reduce the query data needed
     * to generate a code. Looks up the main system coding scheme
     * (if there is one, returns an empty string if not). The following
     * tokens are valid:
     *
     * T         - First char of animal's type
     * YYYY      - 4 digit year
     * YY        - 2 digit year
     * MM        - 2 digit month
     * DD        - 2 digit day
     * NNNNNNNNNN - 10 (or more) digit code for next animal of all time
     * NNNN      - 4 (or more) digit code for next animal of all time
     * NNN       - 3 (or more) digit code for next animal of this type
     *
     * @param animalType        An animal type name
     * @param broughtInDate     The date the animal was brought to the shelter
     */
    public static AnimalCode fastGenerateAnimalCode(String animalType,
        Date broughtInDate) {
        // Get the coding formats
        String format = Global.getCodingFormat();
        String shortformat = Global.getShortCodingFormat();
        int highestyear = 0;
        int highestever = 0;
        Global.logDebug("FAST: Generating animal code, format=" + format +
            ", shortformat=" + shortformat, "Animal.generateAnimalCode");

        // Get the animal type ID
        int typeID = LookupCache.getAnimalTypeIDForName(animalType).intValue();

        // Get the brought in date in usable form
        Calendar cal = Calendar.getInstance();

        try {
            cal = Utils.dateToCalendar(broughtInDate);
        } catch (Exception e) {
        }

        // Work out the beginning and end of the brought in year
        Calendar beginningofyear = Calendar.getInstance();
        beginningofyear.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        beginningofyear.set(Calendar.DAY_OF_MONTH, 1);
        beginningofyear.set(Calendar.MONTH, 0);

        Calendar endofyear = Calendar.getInstance();
        endofyear.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        endofyear.set(Calendar.DAY_OF_MONTH, 31);
        endofyear.set(Calendar.MONTH, 11);

        // Calculate the highest year portion for the type
        if ((format.indexOf("N") != -1) || (shortformat.indexOf("N") != -1)) {
            try {
                highestyear = 1 +
                    DBConnection.executeForCount(
                        "SELECT MAX(YearCodeId) FROM animal WHERE DateBroughtIn >='" +
                        Utils.getSQLDate(beginningofyear) +
                        "' AND DateBroughtIn <= '" +
                        Utils.getSQLDate(endofyear) + "' AND AnimalTypeID = " +
                        typeID);
            } catch (Exception e) {
                Global.logException(e, Animal.class);
            }
        }

        // Get the highest numbered animal on the shelter for all time
        // (if either format has a token for it). 
        if ((format.indexOf("U") != -1) || (shortformat.indexOf("U") != -1)) {
            try {
                highestever = 1 +
                    DBConnection.executeForCount(
                        "SELECT MAX(UniqueCodeId) FROM animal");
            } catch (Exception e) {
                Global.logException(e, Animal.class);
            }
        }

        return generateAnimalCode(animalType, broughtInDate, highestyear,
            highestever);
    }

    /**
     * Overloaded variant that sets highestyear/ever to zero so
     * it will be autodiscovered from the data
     */
    public static AnimalCode generateAnimalCode(String animalType,
        Date broughtInDate) {
        return generateAnimalCode(animalType, broughtInDate, 0, 0);
    }

    /**
     * Generates a code for an animal. Looks up the main system coding scheme
     * (if there is one, returns an empty string if not). The following
     * tokens are valid:
     *
     * T         - First char of animal's type
     * YYYY      - 4 digit year
     * YY        - 2 digit year
     * MM        - 2 digit month
     * DD        - 2 digit day
     * NNNNNNNNNN - 10 (or more) digit code for next animal of all time
     * NNNN      - 4 (or more) digit code for next animal of all time
     * NNN       - 3 (or more) digit code for next animal of this type
     *
     * @param animalType        An animal type name
     * @param broughtInDate     The date the animal was brought to the shelter
     * @param highestyear       The highest code used in the year (or 0 to discover)
     * @param highestever       The highest code ever used (or 0 to discover)
     */
    public static AnimalCode generateAnimalCode(String animalType,
        Date broughtInDate, int highestyear, int highestever) {
        // Get the coding formats
        String format = Global.getCodingFormat();
        String shortformat = Global.getShortCodingFormat();
        Global.logDebug("Generating animal code, format=" + format +
            ", shortformat=" + shortformat, "Animal.generateAnimalCode");

        // Get the animal type ID
        int typeID = LookupCache.getAnimalTypeIDForName(animalType).intValue();

        // Get the brought in date in usable form
        Calendar cal = Calendar.getInstance();

        try {
            cal = Utils.dateToCalendar(broughtInDate);
        } catch (Exception e) {
            cal = Calendar.getInstance();
        }

        if (cal == null) {
            cal = Calendar.getInstance();
        }

        // Work out the beginning and end of the brought in year
        Calendar beginningofyear = Calendar.getInstance();
        beginningofyear.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        beginningofyear.set(Calendar.DAY_OF_MONTH, 1);
        beginningofyear.set(Calendar.MONTH, 0);

        Calendar endofyear = Calendar.getInstance();
        endofyear.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        endofyear.set(Calendar.DAY_OF_MONTH, 31);
        endofyear.set(Calendar.MONTH, 11);

        Calendar oneyearago = Calendar.getInstance();
        oneyearago.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);

        // Get the highest numbered animal on the shelter for that year
        // and the selected type (if either format has a token for it)
        if ((format.indexOf("N") != -1) || (shortformat.indexOf("N") != -1)) {
            if (highestyear == 0) {
                highestyear = 1 +
                    getHighest(
                        "SELECT ShelterCode FROM animal WHERE DateBroughtIn >= '" +
                        Utils.getSQLDate(beginningofyear) +
                        "' AND DateBroughtIn <= '" +
                        Utils.getSQLDate(endofyear) + "' AND AnimalTypeID = " +
                        typeID);
            }
        }

        // Get the highest numbered animal on the shelter for all time
        // (if either format has a token for it). But to save a bit
        // of processing time, only look at codes on animal records created
        // upto 1 year ago.
        if ((format.indexOf("U") != -1) || (shortformat.indexOf("U") != -1)) {
            if (highestever == 0) {
                highestever = 1 +
                    getHighest(
                        "SELECT ShelterCode FROM animal WHERE CreatedDate >= '" +
                        Utils.getSQLDate(oneyearago) + "'");
            }
        }

        // Construct the code, then verify it's unique in the db. If it isn't, 
        // increment the code by an extra one and try again. Keep going until we
        // get a unique code.
        boolean isUnique = false;
        String code = "";
        String shortcode = "";

        while (!isUnique) {
            String padyear = Utils.zeroPad(highestyear, 3);
            String nopadyear = Integer.toString(highestyear);
            String padever = Utils.zeroPad(highestever, 4);
            String padbig = Utils.zeroPad(highestever, 10);

            Global.logDebug("Got code portions, type=" +
                animalType.substring(0, 1) + ", highestyear=" + highestyear +
                ", highestever=" + highestever + ", padyear=" + padyear +
                ", padever=" + padever + ", padbig=" + padbig,
                "Animal.generateAnimalCode");

            // Now format the data
            code = substituteCodeTokens(format, cal, padbig, padever, padyear,
                    nopadyear, animalType);
            shortcode = substituteCodeTokens(shortformat, cal, padbig, padever,
                    padyear, nopadyear, animalType);

            Global.logDebug("Code generated: code=" + code + ", shortcode=" +
                shortcode, "Animal.generateAnimalCode");

            // Test for uniqueness
            try {
                if (0 == DBConnection.executeForCount(
                            "SELECT COUNT(*) FROM animal WHERE ShelterCode Like '" +
                            code + "'")) {
                    isUnique = true;
                } else {
                    Global.logDebug("Code already exists in database, regenerating...",
                        "Animal.generateAnimalCode");

                    if (highestyear > 0) {
                        highestyear++;
                    }

                    if (highestever > 0) {
                        highestever++;
                    }
                }
            } catch (Exception e) {
                Global.logException(e, Animal.class);

                break;
            }
        }

        return new AnimalCode(code, shortcode);
    }

    private static String substituteCodeTokens(String format, Calendar c,
        String padbig, String padever, String padyear, String nopadyear,
        String animalType) {
        String code = format;
        code = Utils.replace(code, "YYYY",
                Utils.zeroPad(c.get(Calendar.YEAR), 4));
        code = Utils.replace(code, "YY",
                Utils.zeroPad(c.get(Calendar.YEAR) - 2000, 2));
        code = Utils.replace(code, "MM",
                Utils.zeroPad(c.get(Calendar.MONTH) + 1, 2));
        code = Utils.replace(code, "DD",
                Utils.zeroPad(c.get(Calendar.DAY_OF_MONTH), 2));
        code = Utils.replace(code, "UUUUUUUUUU", padbig);
        code = Utils.replace(code, "UUUU", padever);
        code = Utils.replace(code, "NNN", padyear);
        code = Utils.replace(code, "NN", nopadyear);
        code = Utils.replace(code, "T", animalType.substring(0, 1));

        return code;
    }

    /**
     * Update denormalised data for on shelter animals
     */
    public static void updateOnShelterAnimalStatuses()
        throws Exception {
        // Recheck archive flags for on shelter animal records based
        // on whether they have left the shelter.
        Global.logInfo(Global.i18n("bo", "auto_archiving_records"),
            "Animal.updateOnShelterAnimalStatuses");

        try {
            Animal a = new Animal();
            a.openRecordset("Archived = 0");

            int doneRecords = 0;
            String totalRecords = Long.toString(a.getRecordCount());

            if (Global.mainForm != null) {
                Global.mainForm.initStatusBarMax((int) a.getRecordCount());
                Global.mainForm.setStatusText(Global.i18n("bo",
                        "auto_archiving_records"));
            }

            while (!a.getEOF()) {
                updateAnimalStatus(a.getID().intValue());
                a.moveNext();

                doneRecords++;

                if (Integer.toString(doneRecords).endsWith("00") ||
                        (doneRecords == 1)) {
                    Global.logInfo(doneRecords + " / " + totalRecords,
                        "Animal.updateOnShelterAnimalStatuses");
                }

                if (Global.mainForm != null) {
                    Global.mainForm.incrementStatusBar();
                }
            }

            Global.logInfo(Global.i18n("bo", "Done.") + " (" + totalRecords +
                ")", "Animal.updateOnShelterAnimalStatuses");
            a.free();
            a = null;

            if (Global.mainForm != null) {
                Global.mainForm.resetStatusBar();
                Global.mainForm.setStatusText("");
            }
        } catch (Exception e) {
            Global.logException(e, Animal.class);
            throw e;
        }
    }

    /**
     * Update denormalised data for all animals
     */
    public static void updateAllAnimalStatuses() throws Exception {
        // Set the archive flag for existing animal records based
        // on whether they have left the shelter.
        Global.logInfo(Global.i18n("bo", "auto_archiving_records"),
            "Animal.updateAllAnimalStatuses");

        try {
            Animal a = new Animal();
            a.openRecordset("");

            int doneRecords = 0;
            String totalRecords = Long.toString(a.getRecordCount());

            if (Global.mainForm != null) {
                Global.mainForm.initStatusBarMax((int) a.getRecordCount());
                Global.mainForm.setStatusText(Global.i18n("bo",
                        "auto_archiving_records"));
            }

            while (!a.getEOF()) {
                updateAnimalStatus(a.getID().intValue());
                a.moveNext();

                doneRecords++;

                if (Integer.toString(doneRecords).endsWith("00") ||
                        (doneRecords == 1)) {
                    Global.logInfo(doneRecords + " / " + totalRecords,
                        "Animal.updateAllAnimalStatuses");
                }

                if (Global.mainForm != null) {
                    Global.mainForm.incrementStatusBar();
                }
            }

            Global.logInfo(Global.i18n("bo", "Done.") + " (" + totalRecords +
                ")", "Animal.updateAllAnimalStatuses");
            a.free();
            a = null;

            if (Global.mainForm != null) {
                Global.mainForm.resetStatusBar();
                Global.mainForm.setStatusText("");
            }
        } catch (Exception e) {
            Global.logException(e, Animal.class);
            throw e;
        }
    }

    /** Returns the number of each type of external record for a given animal ID */
    public static Animal.AnimalMarkers getNumExternalRecords(Integer id) {
        try {
            // Check all satellite data in one query
            SQLRecordset r = new SQLRecordset();
            r.openRecordset(
                "SELECT animal.ID, (SELECT COUNT(*) FROM animalvaccination WHERE AnimalID = animal.ID) AS vacc, (SELECT COUNT(*) FROM animalmedical WHERE AnimalID = animal.ID) AS medi, (SELECT COUNT(*) FROM animaldiet WHERE AnimalID = animal.ID) AS diet, (SELECT COUNT(*) FROM media WHERE LinkID = animal.ID AND LinkTypeID = " +
                Media.LINKTYPE_ANIMAL +
                ") AS pics, (SELECT COUNT(*) FROM diary WHERE LinkID = animal.ID AND LinkType = " +
                Diary.LINKTYPE_ANIMAL +
                ") AS diar, (SELECT COUNT(*) FROM adoption WHERE AnimalID = animal.ID) AS move, (SELECT COUNT(*) FROM log WHERE LinkID = animal.ID AND LinkType = " +
                Log.LINKTYPE_ANIMAL +
                ") AS logs, (SELECT COUNT(*) FROM ownerdonation WHERE AnimalID = animal.ID) AS dona, " +
                "(SELECT COUNT(*) FROM animalcost WHERE AnimalID = animal.ID) AS cost FROM animal WHERE animal.ID = " +
                id, "animal");

            return new AnimalMarkers((Integer) r.getField("vacc"),
                (Integer) r.getField("medi"), (Integer) r.getField("diet"),
                (Integer) r.getField("cost"), (Integer) r.getField("dona"),
                (Integer) r.getField("pics"), (Integer) r.getField("diar"),
                (Integer) r.getField("move"), (Integer) r.getField("logs"));
        } catch (Exception e) {
            Global.logException(e, Animal.class);
        }

        return new AnimalMarkers();
    }

    public void free() {
        try {
            preferredWebMedia.free();
            preferredWebMedia = null;
        } catch (Exception e) {
        }

        try {
            preferredDocMedia.free();
            preferredDocMedia = null;
        } catch (Exception e) {
        }

        super.free();
    }

    public static class AnimalCode {
        public String code;
        public String shortcode;

        public AnimalCode(String code, String shortcode) {
            this.code = code;
            this.shortcode = shortcode;
        }
    }

    public static class AnimalMarkers {
        public int vaccination = 0;
        public int medical = 0;
        public int diet = 0;
        public int costs = 0;
        public int donations = 0;
        public int media = 0;
        public int diary = 0;
        public int movement = 0;
        public int log = 0;

        public AnimalMarkers() {
        }

        public AnimalMarkers(Integer vaccination, Integer medical,
            Integer diet, Integer costs, Integer donations, Integer media,
            Integer diary, Integer movement, Integer log) {
            this.vaccination = vaccination.intValue();
            this.medical = medical.intValue();
            this.diet = diet.intValue();
            this.costs = costs.intValue();
            this.donations = donations.intValue();
            this.media = media.intValue();
            this.diary = diary.intValue();
            this.movement = movement.intValue();
            this.log = log.intValue();
        }
    }
}
