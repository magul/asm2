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

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.*;


public class AnimalMedical extends UserInfoBO<AnimalMedical> {
    public final static int STATUS_ACTIVE = 0;
    public final static int STATUS_HELD = 1;
    public final static int STATUS_COMPLETED = 2;
    public final static int TIMING_RULE_DAILY = 0;
    public final static int TIMING_RULE_WEEKLY = 1;
    public final static int TIMING_RULE_MONTHLY = 2;
    public final static int TIMING_RULE_YEARLY = 3;
    Animal animal = null;

    public AnimalMedical() {
        tableName = "animalmedical";
    }

    public AnimalMedical(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getAnimalID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalID");
    }

    public void setAnimalID(Integer newValue) throws CursorEngineException {
        rs.setField("AnimalID", newValue);
    }

    public Animal getAnimal() throws CursorEngineException {
        // Do we have a cached animal?
        if (animal != null) {
            try {
                // Is it the right one for this record?
                if (animal.getID().intValue() == getAnimalID().intValue()) {
                    // It is! Return it!
                    return animal;
                }
            } catch (Exception e) {
                // Something is wrong with the animal record - ignore it.
            }
        }

        // We don't have one, or the one we do have is wrong,
        // look it up again.
        animal = new Animal();
        animal.openRecordset("ID = " + getAnimalID());

        if (animal.getEOF()) {
            return null;
        } else {
            return animal;
        }
    }

    public AnimalMedical copy() throws CursorEngineException {
        AnimalMedical a = new AnimalMedical();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setAnimalID(getAnimalID());
        a.setMedicalProfileID(getMedicalProfileID());
        a.setTreatmentName(getTreatmentName());
        a.setStartDate(getStartDate());
        a.setDosage(getDosage());
        a.setCost(getCost());
        a.setTimingRule(getTimingRule());
        a.setTimingRuleFrequency(getTimingRuleFrequency());
        a.setTimingRuleNoFrequencies(getTimingRuleNoFrequencies());
        a.setTreatmentRule(getTreatmentRule());
        a.setTotalNumberOfTreatments(getTotalNumberOfTreatments());
        a.setTreatmentsGiven(getTreatmentsGiven());
        a.setTreatmentsRemaining(getTreatmentsRemaining());
        a.setStatus(getStatus());
        a.setComments(getComments());

        return a;
    }

    public Integer getMedicalProfileID() throws CursorEngineException {
        return (Integer) rs.getField("MedicalProfileID");
    }

    public void setMedicalProfileID(Integer newValue)
        throws CursorEngineException {
        rs.setField("MedicalProfileID", newValue);
    }

    public String getTreatmentName() throws CursorEngineException {
        return (String) rs.getField("TreatmentName");
    }

    public void setTreatmentName(String newValue) throws CursorEngineException {
        rs.setField("TreatmentName", newValue);
    }

    public Date getStartDate() throws CursorEngineException {
        return (Date) rs.getField("StartDate");
    }

    public void setStartDate(Date newValue) throws CursorEngineException {
        rs.setField("StartDate", newValue);
    }

    public String getDosage() throws CursorEngineException {
        return (String) rs.getField("Dosage");
    }

    public void setDosage(String newValue) throws CursorEngineException {
        rs.setField("Dosage", newValue);
    }

    /** 0 for a one-off, or a number per TimingRuleFrequency */
    public Integer getTimingRule() throws CursorEngineException {
        return (Integer) rs.getField("TimingRule");
    }

    public void setTimingRule(Integer newValue) throws CursorEngineException {
        rs.setField("TimingRule", newValue);
    }

    /**
     * 0 = Daily 1 = Weekly 2 = Monthly 3 = Annually
     */
    public Integer getTimingRuleFrequency() throws CursorEngineException {
        return (Integer) rs.getField("TimingRuleFrequency");
    }

    public void setTimingRuleFrequency(Integer newValue)
        throws CursorEngineException {
        rs.setField("TimingRuleFrequency", newValue);
    }

    /**
     * The number of timing rule frequencies. Eg: Timing rule = 4 (4 treatments
     * on frequency) TimingRuleNoFrequencies = 5 TimingRuleFrequency = Week (so
     * 4 treatments every 5 weeks)
     */
    public Integer getTimingRuleNoFrequencies() throws CursorEngineException {
        return (Integer) rs.getField("TimingRuleNoFrequencies");
    }

    public void setTimingRuleNoFrequencies(Integer newValue)
        throws CursorEngineException {
        rs.setField("TimingRuleNoFrequencies", newValue);
    }

    /**
     * 0 = Set Length 1 = Unspecified (goes on until manually completed)
     */
    public Integer getTreatmentRule() throws CursorEngineException {
        return (Integer) rs.getField("TreatmentRule");
    }

    public void setTreatmentRule(Integer newValue) throws CursorEngineException {
        rs.setField("TreatmentRule", newValue);
    }

    /** If Treatment Rule == 0 (fixed length), the number of treatments required */
    public Integer getTotalNumberOfTreatments() throws CursorEngineException {
        return (Integer) rs.getField("TotalNumberOfTreatments");
    }

    public void setTotalNumberOfTreatments(Integer newValue)
        throws CursorEngineException {
        rs.setField("TotalNumberOfTreatments", newValue);
    }

    public Integer getTreatmentsGiven() throws CursorEngineException {
        return (Integer) rs.getField("TreatmentsGiven");
    }

    public void setTreatmentsGiven(Integer newValue)
        throws CursorEngineException {
        rs.setField("TreatmentsGiven", newValue);
    }

    public Integer getTreatmentsRemaining() throws CursorEngineException {
        return (Integer) rs.getField("TreatmentsRemaining");
    }

    public void setTreatmentsRemaining(Integer newValue)
        throws CursorEngineException {
        rs.setField("TreatmentsRemaining", newValue);
    }

    /**
     * 0 = Active 1 = Held 2 = Completed
     */
    public Integer getStatus() throws CursorEngineException {
        return (Integer) rs.getField("Status");
    }

    public void setStatus(Integer newValue) throws CursorEngineException {
        rs.setField("Status", newValue);
    }

    public Double getCost() throws CursorEngineException {
        return new Double(rs.getDouble("Cost"));
    }

    public void setCost(Double newValue) throws CursorEngineException {
        rs.setField("Cost", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    public Integer getRecordVersion() throws CursorEngineException {
        return (Integer) rs.getField("RecordVersion");
    }

    public void setRecordVersion(Integer newValue) throws CursorEngineException {
        rs.setField("RecordVersion", newValue);
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

    public void validate() throws BOValidationException {
        try {
            // if (getDosage().equals("") ||
            // getDosage().equals(SQLRecordset.NULL_VALUE))
            // throw new BOValidationException(Global.i18n("bo",
            // "You_must_supply_a_dosage"));
            if ((getTreatmentName() == null) || getTreatmentName().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_a_treatment_name"));
            }

            if (getTimingRule() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Number_of_treatments_must_be_numeric"));
            }

            if (getTimingRuleNoFrequencies() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Number_of_frequencies_must_be_numeric"));
            }

            if (getTotalNumberOfTreatments() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Total_number_of_treatments_must_be_numeric"));
            }

            // Set defaults for given and remaining
            setTreatmentsGiven(new Integer(0));
            setTreatmentsRemaining(getTotalNumberOfTreatments());
        } catch (Exception e) {
            Global.logException(e, this.getClass());
            throw new BOValidationException(e.getMessage());
        }
    }

    /**
     * Generates the next treatment record for this medical record. This should
     * be called after creating an AnimalMedical record the first time, and then
     * after the saving of every AnimalMedicalTreatment record related to it.
     * This means no need for any kind of batch process.
     *
     * It works like this:
     *
     * 1. Check if the record is still active, but has all its treatments given.
     * Mark it complete if so.
     *
     * 2. Ignore completed records.
     *
     * 3. If the record has no treatment records, generate one from the master
     * record.
     *
     * 4. If the record has no outstanding treatment records, generate one from
     * the last administered record
     *
     * 5. If we generated a record, increment the tally of given and reduce the
     * tally of remaining. If the TreatmentRule is unspecified, then ignore this
     * step
     */
    public void generateTreatments() throws Exception {
        // See if it needs completing instead of new treatments
        // generated (this only applies to medical records with
        // a finite number of treatments - ie. Not unspecified)
        if (getTreatmentRule().intValue() != 1) {
            checkForComplete();
        }

        // Don't bother with non-active records
        if (getStatus().intValue() != STATUS_ACTIVE) {
            return;
        }

        // If there aren't any treatment records, create
        // one now.
        AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
        amt.openRecordset("AnimalMedicalID = " + getID());

        if (amt.getEOF()) {
            Global.logDebug("No treatment records, creating",
                "AnimalMedical.generateTreatments");
            generateTreatment(getStartDate(), true);
        } else {
            Global.logDebug("Got treatments, looking up last",
                "AnimalMedical.generateTreatments");
            // Go to the latest treatment for the animal
            amt.moveLast();

            // Is it still outstanding? If so, don't bother
            // doing anything right now.
            if (amt.getDateGiven() == null) {
                return;
            }

            // Otherwise, generate a new treatment from the
            // last one on file (when it was given to prevent
            // overdosing).
            generateTreatment(amt.getDateGiven(), false);
        }

        // Add up the tallies now where appropriate

        // If set to unspecified, then don't bother as it will
        // never finish
        if (getTreatmentRule().intValue() == 1) {
            return;
        }

        // Add up the tallies
        setTreatmentsGiven(new Integer(getTreatmentsGiven().intValue() + 1));
        setTreatmentsRemaining(new Integer(getTreatmentsRemaining().intValue() -
                1));

        // We need to save the record to keep the tallies
        String sql = "UPDATE animalmedical SET TreatmentsGiven = " +
            getTreatmentsGiven() + ", TreatmentsRemaining = " +
            getTreatmentsRemaining() + " WHERE ID = " + getID();
        DBConnection.executeAction(sql);
    }

    /**
     * Determines if the treatment has any outstanding treatments left. If it
     * doesn't, then the tally of given - remaining is checked. If all
     * treatments have been given, the record is marked as complete.
     */
    private void checkForComplete() throws Exception {
        // Don't bother with non-active records
        if (getStatus().intValue() != STATUS_ACTIVE) {
            Global.logDebug("Medical not active",
                "AnimalMedical.checkForComplete");

            return;
        }

        // Does this record have no outstanding treatments?
        AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
        amt.openRecordset("AnimalMedicalID = " + getID());

        if (!amt.getEOF()) {
            amt.openRecordset("AnimalMedicalID = " + getID() +
                " AND DateGiven Is Null");

            if (!amt.getEOF()) {
                // We still have an outstanding one - drop out
                return;
            }
        }
        // Drop out if there aren't any treatments at all
        else {
            return;
        }

        // If Given == Total, then we can mark this as completed
        if (getTreatmentsGiven().intValue() == getTotalNumberOfTreatments()
                                                       .intValue()) {
            setStatus(new Integer(STATUS_COMPLETED));

            // We need to save the record to keep the status
            String sql = "UPDATE animalmedical SET Status = " + getStatus() +
                " WHERE ID = " + getID();
            DBConnection.executeAction(sql);
        }
    }

    /**
     * Generates the new treatment record given a start point and the
     * information from the master record.
     *
     * @param date
     *            The date to start from when calculating the next
     * @param isStart
     *            The date supplied IS the date to use - don't calculate
     */
    private void generateTreatment(Date date, boolean isStart)
        throws CursorEngineException {
        // If isStart is set, we already have the date - just
        // create the record
        if (!isStart) {
            // Calculate the next date from the frequency/etc information
            // on this record
            Calendar datenow = Utils.dateToCalendar(date);

            // Get the frequency type
            int type = 0;

            if (getTimingRuleFrequency().intValue() == TIMING_RULE_DAILY) {
                type = Calendar.DAY_OF_YEAR;
            } else if (getTimingRuleFrequency().intValue() == TIMING_RULE_WEEKLY) {
                type = Calendar.WEEK_OF_YEAR;
            } else if (getTimingRuleFrequency().intValue() == TIMING_RULE_MONTHLY) {
                type = Calendar.MONTH;
            } else if (getTimingRuleFrequency().intValue() == TIMING_RULE_YEARLY) {
                type = Calendar.YEAR;
            }

            // Get the number of frequencies
            int no = getTimingRuleNoFrequencies().intValue();

            // Add it up
            datenow.add(type, no);

            // Turn it back to a date for putting on the record
            date = Utils.calendarToDate(datenow);
        }

        // Create the correct number of records according to
        // the TimingRule
        int noRecs = getTimingRule().intValue();

        if (noRecs == 0) {
            noRecs = 1;
        }

        for (int i = 1; i <= noRecs; i++) {
            AnimalMedicalTreatment amt = new AnimalMedicalTreatment();
            amt.openRecordset("ID = 0");
            amt.addNew();
            amt.setAnimalID(getAnimalID());
            amt.setAnimalMedicalID(getID());
            amt.setDateRequired(date);
            amt.setDateGiven(null);
            amt.setGivenBy(""); // Defaulted on save
            amt.setTreatmentNumber(new Integer(i));
            amt.setTotalTreatments(new Integer(noRecs));
            amt.setComments("");
            amt.save(Global.currentUserName);
            amt.free();
            amt = null;
        }
    }

    /**
     * Pulls together the TimingRule information to produce a string, like "One
     * Off" or "1 treatment every 5 weeks".
     */
    public String getNamedFrequency() throws CursorEngineException {
        if (getTimingRule().intValue() == 0) {
            return Global.i18n("bo", "One_Off");
        }

        String out = getTimingRule().toString();
        out += (" " + Global.i18n("bo", "treatment_every") + " ");
        out += (getTimingRuleNoFrequencies() + " ");

        String freq = "";

        if (getTimingRuleFrequency().intValue() == 0) {
            freq = Global.i18n("bo", "days");
        }

        if (getTimingRuleFrequency().intValue() == 1) {
            freq = Global.i18n("bo", "weeks");
        }

        if (getTimingRuleFrequency().intValue() == 2) {
            freq = Global.i18n("bo", "months");
        }

        if (getTimingRuleFrequency().intValue() == 3) {
            freq = Global.i18n("bo", "years");
        }

        return out + freq;
    }

    /**
     * Pulls together the treatment rule information to return a string like
     * "Unspecified" or "21 Treatment Periods (52 treatments)" or
     * "1 treatment" for one-offs
     */
    public String getNamedNumberOfTreatments() throws CursorEngineException {
        if (getTimingRule().intValue() == 0) {
            return "1 " + Global.i18n("uimedical", "treatment");
        }

        if (getTreatmentRule().intValue() == 1) {
            return Global.i18n("bo", "Unspecified");
        }

        String out = getTotalNumberOfTreatments() + " " +
            Global.i18n("uimedical", "treatment_periods");

        int no = getTimingRule().intValue();
        int fr = getTotalNumberOfTreatments().intValue();
        int tot = (no * fr);

        out += (" (" + tot + " ");

        if (tot == 1) {
            out += Global.i18n("uimedical", "treatment");
        } else {
            out += Global.i18n("uimedical", "treatments");
        }

        out += ")";

        return out;
    }

    public String getNamedStatus() throws CursorEngineException {
        if (getStatus().intValue() == STATUS_ACTIVE) {
            Global.i18n("uimedical", "Active");
        }

        if (getStatus().intValue() == STATUS_COMPLETED) {
            return Global.i18n("uimedical", "Completed");
        }

        if (getStatus().intValue() == STATUS_HELD) {
            return Global.i18n("uimedical", "Held");
        }

        return Global.i18n("uimedical", "Active");
    }
}
