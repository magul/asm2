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
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.*;


public class AnimalLitter extends NormalBO<AnimalLitter> {
    private Animal animal = null;

    public AnimalLitter() {
        tableName = "animallitter";
    }

    public AnimalLitter(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getParentAnimalID() throws CursorEngineException {
        return (Integer) rs.getField("ParentAnimalID");
    }

    public void setParentAnimalID(Integer newValue)
        throws CursorEngineException {
        rs.setField("ParentAnimalID", newValue);
    }

    public Animal getAnimal() throws CursorEngineException {
        if (animal == null) {
            animal = new Animal();
            animal.openRecordset("ID = " + getParentAnimalID());
        } else {
            if (animal.getEOF()) {
                animal.openRecordset("ID = " + getParentAnimalID());

                if (animal.getEOF()) {
                    return null;
                }
            }

            if (animal.getID().equals(getParentAnimalID())) {
                return animal;
            } else {
                animal.openRecordset("ID = " + getParentAnimalID());
            }
        }

        if (animal.getEOF()) {
            return null;
        } else {
            return animal;
        }
    }

    public String getParentName() throws CursorEngineException {
        Animal a = getAnimal();

        if (a == null) {
            return "";
        } else {
            return a.getAnimalName();
        }
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

    public Date getDate() throws CursorEngineException {
        return (Date) rs.getField("Date");
    }

    public void setDate(Date newValue) throws CursorEngineException {
        rs.setField("Date", newValue);
    }

    public Integer getNumberInLitter() throws CursorEngineException {
        return (Integer) rs.getField("NumberInLitter");
    }

    public void setNumberInLitter(Integer newValue)
        throws CursorEngineException {
        rs.setField("NumberInLitter", newValue);
    }

    public Date getInvalidDate() throws CursorEngineException {
        return (Date) rs.getField("InvalidDate");
    }

    public void setInvalidDate(Date newValue) throws CursorEngineException {
        rs.setField("InvalidDate", newValue);
    }

    public Integer getTimeoutMonths() throws CursorEngineException {
        return (Integer) rs.getField("TimeoutMonths");
    }

    public void setTimeoutMonths(Integer newValue) throws CursorEngineException {
        rs.setField("TimeoutMonths", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    public String getAcceptanceNumber() throws CursorEngineException {
        return (String) rs.getField("AcceptanceNumber");
    }

    public void setAcceptanceNumber(String newValue)
        throws CursorEngineException {
        rs.setField("AcceptanceNumber", newValue);
    }

    public Integer getCachedAnimalsLeft() throws CursorEngineException {
        return (Integer) rs.getField("CachedAnimalsLeft");
    }

    public void setCachedAnimalsLeft(Integer newValue)
        throws CursorEngineException {
        rs.setField("CachedAnimalsLeft", newValue);
    }

    /**
     * If the litter has an acceptance number, returns the number of animals
     * with that acceptance number who are still on the shelter. -1 is returned
     * if no acceptance number.
     */
    public Integer getAnimalsRemaining() throws CursorEngineException {
        if ((getAcceptanceNumber() == null) ||
                getAcceptanceNumber().equals("")) {
            return new Integer(-1);
        }

        // Get a list of all animals with that acceptance number
        Animal a = new Animal();
        a.openRecordset("AcceptanceNumber Like '" +
            getAcceptanceNumber().trim() + "'");

        int onShelter = 0;

        while (!a.getEOF()) {
            if (!a.isOverSixMonths()) {
                int loc = a.getAnimalLocationAtDate(new Date());

                if ((loc == Animal.ONSHELTER) || (loc == Animal.FOSTERED)) {
                    onShelter++;
                }
            }

            a.moveNext();
        }

        // Update the cached value of this record. The
        // reason we do this is to speed up the procedure
        // that recalculates whether litters need to be cancelled by
        // ignoring all those with a cached 0 or null value.
        setCachedAnimalsLeft(new Integer(onShelter));
        save();

        return new Integer(onShelter);
    }

    /**
     * Loops through all litters on the system that have an acceptance number
     * and some animals left on the shelter and recalculates them. If all the
     * animals on that litter have gone, the litter is cancelled as at todays
     * date. This should be safe since this routine will be called quite often.
     */
    public static void updateLitters() throws CursorEngineException {
        AnimalLitter al = new AnimalLitter();
        al.openRecordset(
            "CachedAnimalsLeft Is Not Null And (InvalidDate Is Null)");

        Global.logInfo(Global.i18n("bo", "updating_and_cancelling_litters"),
            "AnimalLitter.updateLitters");

        int noCancelled = 0;

        while (!al.getEOF()) {
            if (al.getAnimalsRemaining().intValue() == 0) {
                // Cancel the litter
                try {
                    DBConnection.executeAction(
                        "UPDATE animallitter SET InvalidDate = '" +
                        SQLRecordset.getSQLRepresentationOfDateOnly(new Date()) +
                        "' WHERE ID = " + al.getID());
                } catch (Exception e) {
                    throw new CursorEngineException(e.getMessage());
                }

                noCancelled++;
            }

            al.moveNext();
            System.out.print(".");
        }

        System.out.println("");

        al.free();
        al = null;

        if (noCancelled > 0) {
            Global.logInfo(Global.i18n("bo", "litters_cancelled",
                    Integer.toString(noCancelled)), "AnimalLitter.updateLitters");
        } else {
            Global.logInfo(Global.i18n("bo", "No_litters_cancelled."),
                "AnimalLitter.updateLitters");
        }
    }

    /**
     * Overridden from superclass - contains Animal Litter specific validation
     * routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            // Make sure we have a date
            if (getDate() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_a_date."));
            }

            // Make sure we have number
            if ((getNumberInLitter() == null)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_number_of_animals_in_the_litter."));
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }

    /**
     * Returns true/false depending on whether this litter is still valid on the
     * date specified. To accomplish this, the start date on the record must be:
     * 1. Less than or equal to atDate 2. If you add <timeout> months to the
     * start date, it must be greater than or equal to atDate unless it is 0, in
     * which case this condition is ignored. 3. atDate must be before the
     * invalid date if the record has one.
     *
     * @param atDate
     *            A Date representing the date to check.
     * @return True if the litter has expired.
     */
    public boolean hasExpired(Date atDate) throws CursorEngineException {
        Calendar calAtDate = Utils.dateToCalendar(atDate);

        // The date takes precedence if we have one
        if (getInvalidDate() != null) {
            // Is the invalid date after atDate? If it is, then our
            // litter has not expired and should be included
            Calendar invDate = Utils.dateToCalendar(getInvalidDate());

            return !invDate.after(calAtDate);
        }

        // See if we have a valid timeout value
        // to cancel the litter - if so, work out the time out period
        // and see if we are within it and should include this litter
        if (getTimeoutMonths().intValue() != 0) {
            Calendar litterDate = Utils.dateToCalendar(getDate());
            Calendar datePlust = (Calendar) litterDate.clone();
            datePlust.add(Calendar.MONTH, getTimeoutMonths().intValue());

            // Is litterDate + timeout months after atDate - if so,
            // the litter has not expired
            return !datePlust.after(calAtDate);
        }

        // The litter can't have expired.
        return false;
    }

    /**
     * Filters the animallitter table down by only returning records that have
     * not expired before today, or are older than six months.
     *
     * @return All animal litters that are less than six months old and have no
     *         invalid date or a date after today.
     */
    public static AnimalLitter getRecentLitters() {
        Calendar sixmonths = Calendar.getInstance();
        sixmonths.add(Calendar.MONTH, -6);

        String sixmy = SQLRecordset.getSQLRepresentationOfDateOnly(Utils.calendarToDate(
                    sixmonths));

        Calendar today = Calendar.getInstance();
        String todaymy = SQLRecordset.getSQLRepresentationOfDateOnly(Utils.calendarToDate(
                    today));

        AnimalLitter al = new AnimalLitter();
        al.openRecordset("(InvalidDate Is Null Or InvalidDate > '" + todaymy +
            "') " + "And Date > '" + sixmy + "' ORDER BY Date DESC");

        return al;
    }

    /**
     * Filters the animallitter table down by only returning records that have
     * not expired before today, or are older than six months and belong to a
     * particular species.
     *
     * @return All animal litters that are less than six months old and have no
     *         invalid date or a date after today.
     */
    public static AnimalLitter getRecentLittersForSpecies(int speciesID) {
        Calendar sixmonths = Calendar.getInstance();
        sixmonths.add(Calendar.MONTH, -6);

        String sixmy = SQLRecordset.getSQLRepresentationOfDateOnly(Utils.calendarToDate(
                    sixmonths));

        Calendar today = Calendar.getInstance();
        String todaymy = SQLRecordset.getSQLRepresentationOfDateOnly(Utils.calendarToDate(
                    today));

        AnimalLitter al = new AnimalLitter();
        al.openRecordset("(InvalidDate Is Null Or InvalidDate > '" + todaymy +
            "') " + "And Date > '" + sixmy + "' AND SpeciesID = " + speciesID +
            " ORDER BY Date DESC");

        return al;
    }

    /**
     * Returns the number of litters (not the number of animals within the
     * litters) at a given date.
     *
     * @param atDate
     *            The date you want to check
     * @param speciesID
     *            The species of the litters you want to count
     * @return The total number of litters
     */
    public static int getNumberOfLittersOnShelter(Date atDate, int speciesID)
        throws CursorEngineException {
        AnimalLitter al = new AnimalLitter();
        al.openRecordset("Date <= '" +
            SQLRecordset.getSQLRepresentationOfDateOnly(atDate) +
            "' AND SpeciesID = " + speciesID);

        // Loop through the records, working out whether they have expired
        int totalLitters = 0;

        while (!al.getEOF()) {
            if (!al.hasExpired(atDate)) {
                totalLitters++;
            }

            al.moveNext();
        }

        return totalLitters;
    }

    /**
     * Tests if a given litter ID is currently active.
     */
    public static boolean isLitterActive(String litterID) {
        try {
            AnimalLitter al = getRecentLitters();

            while (!al.getEOF()) {
                if (al.getAcceptanceNumber().trim()
                          .equalsIgnoreCase(litterID.trim())) {
                    return true;
                }

                al.moveNext();
            }

            al.free();
            al = null;
        } catch (Exception e) {
            Global.logException(e, AnimalLitter.class);
        }

        return false;
    }
}
