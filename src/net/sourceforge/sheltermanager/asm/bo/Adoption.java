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


public class Adoption extends UserInfoBO<Adoption> {
    public static final int MOVETYPE_NONE = 0;
    public static final int MOVETYPE_ADOPTION = 1;
    public static final int MOVETYPE_FOSTER = 2;
    public static final int MOVETYPE_TRANSFER = 3;
    public static final int MOVETYPE_ESCAPED = 4;
    public static final int MOVETYPE_RECLAIMED = 5;
    public static final int MOVETYPE_STOLEN = 6;
    public static final int MOVETYPE_RELEASED = 7;
    public static final int MOVETYPE_RETAILER = 8;
    public static final int MOVETYPE_RESERVATION = 9;
    public static final int MOVETYPE_CANCRESERVATION = 10;

    /** Cached animal for current movement */
    private Animal animal = null;

    /** Cached owner for current movement */
    private Owner owner = null;

    /** Cached retailer for current movement */
    private Owner retailer = null;

    /** True if we are validating records */
    private boolean useValidation = true;

    public Adoption() {
        tableName = "adoption";
    }

    public Adoption(String where) {
        this();
        openRecordset(where);
    }

    public void free() {
        try {
            // Destroy caches
            if (owner != null) {
                owner.free();
            }

            if (retailer != null) {
                retailer.free();
            }

            if (animal != null) {
                animal.free();
            }

            owner = null;
            animal = null;
            retailer = null;
            super.free();
        } catch (Exception e) {
        }
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Adoption copy() throws CursorEngineException {
        Adoption a = new Adoption();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setAdoptionNumber(a.getID().toString());
        a.setAnimalID(getAnimalID());
        a.setOwnerID(getOwnerID());
        a.setRetailerID(getRetailerID());
        a.setOriginalRetailerMovementID(new Integer(0));
        a.setReturnDate(getReturnDate());
        a.setReturnedReasonID(getReturnedReasonID());
        a.setComments(getComments());
        a.setReservationDate(getReservationDate());
        a.setDonation(getDonation());
        a.setReservationCancelledDate(getReservationCancelledDate());
        a.setInsuranceNumber(getInsuranceNumber());
        a.setReasonForReturn(getReasonForReturn());
        a.setMovementDate(getMovementDate());
        a.setMovementType(getMovementType());

        return a;
    }

    public String getAdoptionNumber() throws CursorEngineException {
        return (String) rs.getField("AdoptionNumber");
    }

    public void setAdoptionNumber(String newValue) throws CursorEngineException {
        rs.setField("AdoptionNumber", newValue);
    }

    public Integer getAnimalID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalID");
    }

    public void setAnimalID(Integer newValue) throws CursorEngineException {
        rs.setField("AnimalID", newValue);
    }

    public Integer getOwnerID() throws CursorEngineException {
        Integer ownid = (Integer) rs.getField("OwnerID");
        Owner own = new Owner();
        own.openRecordset("ID = " + ownid);

        if (own.getEOF()) {
            return new Integer(0);
        } else {
            return ownid;
        }
    }

    public void setOwnerID(Integer newValue) throws CursorEngineException {
        rs.setField("OwnerID", newValue);
    }

    public Integer getRetailerID() throws CursorEngineException {
        Integer ownid = (Integer) rs.getField("RetailerID");

        if (ownid == null) {
            return new Integer(0);
        }

        Owner own = new Owner();
        own.openRecordset("ID = " + ownid);

        if (own.getEOF()) {
            return new Integer(0);
        } else {
            return ownid;
        }
    }

    public void setRetailerID(Integer newValue) throws CursorEngineException {
        rs.setField("RetailerID", newValue);
    }

    /**
     * This field represents the adoption record representing a retailer
     * movement for when an animal is bought from a retailer. This field allows
     * custom reports to track which animals went from which retailer.
     */
    public Integer getOriginalRetailerMovementID() throws CursorEngineException {
        Integer retid = (Integer) rs.getField("OriginalRetailerMovementID");

        if (retid == null) {
            return new Integer(0);
        }

        return retid;
    }

    public void setOriginalRetailerMovementID(Integer newValue)
        throws CursorEngineException {
        rs.setField("OriginalRetailerMovementID", newValue);
    }

    public Date getReturnDate() throws CursorEngineException {
        return (Date) rs.getField("ReturnDate");
    }

    public void setReturnDate(Date newValue) throws CursorEngineException {
        rs.setField("ReturnDate", newValue);
    }

    public Integer getReturnedReasonID() throws CursorEngineException {
        return (Integer) rs.getField("ReturnedReasonID");
    }

    public void setReturnedReasonID(Integer newValue)
        throws CursorEngineException {
        rs.setField("ReturnedReasonID", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
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

    public Date getReservationDate() throws CursorEngineException {
        return (Date) rs.getField("ReservationDate");
    }

    public void setReservationDate(Date newValue) throws CursorEngineException {
        rs.setField("ReservationDate", newValue);
    }

    public Double getDonation() throws CursorEngineException {
        return new Double(rs.getDouble("Donation"));
    }

    public void setDonation(Double newValue) throws CursorEngineException {
        rs.setField("Donation", newValue);
    }

    /** Updates the aggregate donation field on an adoption record */
    public static void updateDonation(int ID) throws Exception {
        Global.logDebug("Updating aggregate donation field for movement " + ID,
            "Adoption.updateDonation");
        DBConnection.executeAction(
            "UPDATE adoption SET Donation = (SELECT SUM(Donation) " +
            "FROM ownerdonation WHERE MovementID = " + ID + ") WHERE " +
            "ID = " + ID);
    }

    public Date getReservationCancelledDate() throws CursorEngineException {
        return (Date) rs.getField("ReservationCancelledDate");
    }

    public void setReservationCancelledDate(Date newValue)
        throws CursorEngineException {
        rs.setField("ReservationCancelledDate", newValue);
    }

    public String getInsuranceNumber() throws CursorEngineException {
        return (String) rs.getField("InsuranceNumber");
    }

    public void setInsuranceNumber(String newValue)
        throws CursorEngineException {
        rs.setField("InsuranceNumber", newValue);
    }

    public String getReasonForReturn() throws CursorEngineException {
        return (String) rs.getField("ReasonForReturn");
    }

    public void setReasonForReturn(String newValue)
        throws CursorEngineException {
        rs.setField("ReasonForReturn", newValue);
    }

    /** Turns off validation of saved records. */
    public void setNoValidation() {
        useValidation = false;
    }

    /**
     * Returns a textual name of the type this record represents
     *
     * @return A string containing a movement type
     */
    public String getReadableMovementType() throws CursorEngineException {
        // Check for reservation/cancelled reservation
        if (getMovementType().intValue() == 0) {
            if ((getReservationDate() != null) &&
                    (getReservationCancelledDate() == null)) {
                return LookupCache.getMoveTypeNameForID(new Integer(
                        MOVETYPE_RESERVATION));
            }

            if ((getReservationDate() != null) &&
                    (getReservationCancelledDate() != null)) {
                return LookupCache.getMoveTypeNameForID(new Integer(
                        MOVETYPE_CANCRESERVATION));
            }
        }

        // Return the type
        return LookupCache.getMoveTypeNameForID((Integer) getMovementType());
    }

    /**
     * Returns the movement date from the record
     *
     * @return The movement date as a database string or
     *         <code>SQLRecordset.NULL_VALUE</code> if there is no movement.
     */
    public Date getMovementDate() throws CursorEngineException {
        return (Date) rs.getField("MovementDate");
    }

    public void setMovementDate(Date newValue) throws CursorEngineException {
        rs.setField("MovementDate", newValue);
    }

    public Integer getMovementType() throws CursorEngineException {
        return (Integer) rs.getField("MovementType");
    }

    public void setMovementType(Integer newValue) throws CursorEngineException {
        rs.setField("MovementType", newValue);
    }

    /**
     * Returns adoption date if movement has one (for legacy code)
     *
     * @return Adoption date as a string or SQLRecordset.NULL_VALUE if no
     *         adoption date on the record.
     */
    public Date getAdoptionDate() throws CursorEngineException {
        if (((Integer) rs.getField("MovementType")).intValue() == MOVETYPE_ADOPTION) {
            return (Date) rs.getField("MovementDate");
        } else {
            return null;
        }
    }

    /**
     * Returns foster date if movement has one (for legacy code)
     *
     * @return Fostered date as a string or SQLRecordset.NULL_VALUE if no foster
     *         date on the record.
     */
    public Date getFosteredDate() throws CursorEngineException {
        if (((Integer) rs.getField("MovementType")).intValue() == MOVETYPE_FOSTER) {
            return (Date) rs.getField("MovementDate");
        } else {
            return null;
        }
    }

    /**
     * Returns transfer date if movement has one (for legacy code)
     *
     * @return Transfer date as a string or SQLRecordset.NULL_VALUE if no
     *         transfer date on the record.
     */
    public Date getTransferDate() throws CursorEngineException {
        if (((Integer) rs.getField("MovementType")).intValue() == MOVETYPE_TRANSFER) {
            return (Date) rs.getField("MovementDate");
        } else {
            return null;
        }
    }

    /**
     * Returns the owner for this movement. If none is set, a NULL is returned.
     *
     * @return The owner on this movement
     */
    public Owner getOwner() throws CursorEngineException {
        // Do we have an owner?
        if (owner != null) {
            // Is it the correct one?
            if (owner.getID().equals(getOwnerID())) {
                // It is - return it
                return owner;
            }
        }

        // We don't have one or it isn't valid, look it up
        owner = new Owner();
        owner.openRecordset("ID = " + getOwnerID());

        if (owner.getEOF()) {
            return null;
        } else {
            return owner;
        }
    }

    /**
     * Returns the retailer for this movement. If none is set, a NULL is
     * returned.
     *
     * @return The owner record for the retailer on this movement
     */
    public Owner getRetailer() throws CursorEngineException {
        // Do we have an owner?
        if ((retailer != null) && !retailer.getEOF()) {
            // Is it the correct one?
            if (retailer.getID().equals(getRetailerID())) {
                // It is - return it
                return retailer;
            }
        }

        // We don't have one or it isn't valid, look it up
        retailer = new Owner();
        retailer.openRecordset("ID = " + getRetailerID());

        if (retailer.getEOF()) {
            return null;
        } else {
            return retailer;
        }
    }

    /**
     * Returns the animal for this movement. If none is set, a NULL is returned.
     *
     * @return The animal on this movement
     */
    public Animal getAnimal() throws CursorEngineException {
        // Do we have a cached animal?
        if (animal != null) {
            // Is it the right one for this record?
            if (animal.getID().equals(getAnimalID())) {
                // It is! Return it!
                return animal;
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

    public static void autoCancelReservations() {
        try {
            int cancelAfter = Configuration.getInteger("AutoCancelReservesDays");

            if (cancelAfter <= 0) {
                Global.logDebug("Auto cancellation of reservations is disabled.",
                    "Adoption.autoCancelReservations");

                return;
            }

            Date today = new Date();
            Calendar cancel = Calendar.getInstance();
            cancel.add(Calendar.DAY_OF_YEAR, cancelAfter * -1);

            // Blanket update every reservation on the system that doesn't have a
            // movement date and is older than our cancel after period
            int r = DBConnection.executeUpdate(
                    "UPDATE adoption SET ReservationCancelledDate = '" +
                    Utils.getSQLDate(today) +
                    "' WHERE MovementDate Is Null AND ReservationDate <= '" +
                    Utils.getSQLDate(cancel) + "'");

            Global.logDebug("Cancelled " + r + " reservations.",
                "Adoption.autoCancelReservations");
        } catch (Exception e) {
            Global.logException(e, Adoption.class);
        }
    }

    /**
     * Generates a new insurance number for the movement.
     *
     * @return The next insurance number in the sequence.
     */
    public String generateInsuranceNumber() {
        try {
            long insno = Configuration.getLong("AutoInsuranceNext");
            long nextno = insno + 1;
            Configuration.setEntry("AutoInsuranceNext", Long.toString(nextno));
            setInsuranceNumber(Long.toString(insno));

            return Long.toString(insno);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return "";
    }

    public void validate() throws BOValidationException {
        // If validation is deactivated, don't bother doing this
        if (!useValidation) {
            return;
        }

        try {
            // Check if the only thing different between this and the saved
            // record is the comments and reason for return text.
            // If only the these comments have changed, allow the save through
            // without validating anything else - this prevents checks disallowing
            // saves that only updated comments
            Adoption co = new Adoption();
            co.openRecordset("ID=" + getID());

            if (!co.getEOF()) {
                int match = 0;
                Date d1 = co.getMovementDate();
                Date d2 = getMovementDate();

                if (d1 == null) {
                    d1 = new Date();
                }

                if (d2 == null) {
                    d2 = new Date();
                }

                if (d1.equals(d2)) {
                    match++;
                }

                d1 = co.getReturnDate();
                d2 = getReturnDate();

                if (d1 == null) {
                    d1 = new Date();
                }

                if (d2 == null) {
                    d2 = new Date();
                }

                if (d1.equals(d2)) {
                    match++;
                }

                if (co.getAnimalID().equals(getAnimalID())) {
                    match++;
                }

                if (co.getOwnerID().equals(getOwnerID())) {
                    match++;
                }

                if (co.getRetailerID().equals(getRetailerID())) {
                    match++;
                }

                if (match == 5) {
                    return;
                }
            }

            // If this is a foster movement, make sure the owner
            // is a fosterer
            if (getMovementType().intValue() == MOVETYPE_FOSTER) {
                if (getOwnerID().intValue() != 0) {
                    if (getOwner().getIsFosterer().intValue() != 1) {
                        throw new BOValidationException(Global.i18n("bo",
                                "foster_movement_no_foster_owner"));
                    }
                }
            }

            // If a retailer owner has been selected, make sure that
            // this movement is a retailer movement.
            if (getOwnerID().intValue() != 0) {
                if (getOwner().getIsRetailer().intValue() == 1) {
                    if (getMovementType().intValue() != MOVETYPE_RETAILER) {
                        throw new BOValidationException(Global.i18n("bo",
                                "retailer_owner_non_retailer_movement"));
                    }
                }
            }

            // If a retailer movement is selected, make sure the owner
            // is a retailer owner
            if (getMovementType().intValue() == MOVETYPE_RETAILER) {
                if (getOwnerID().intValue() != 0) {
                    if (getOwner().getIsRetailer().intValue() != 1) {
                        throw new BOValidationException(Global.i18n("bo",
                                "retailer_movement_non_retailer_owner"));
                    }
                }
            }

            // If we have a from retailer, make sure that the movement
            // type is adoption.
            if ((getRetailerID().intValue() != 0) &&
                    (getMovementType().intValue() != MOVETYPE_ADOPTION)) {
                throw new BOValidationException(Global.i18n("bo",
                        "you_cant_have_a_from_retailer_on_a_non_adoption_movement"));
            }

            // Check for blank or null donation and change it to 0
            if (getDonation() == null) {
                setDonation(new Double(0));
            }

            if (getAnimalID().intValue() == 0) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_select_an_animal."));
            }

            // Owners are not required for Escaped, Stolen, Reclaimed
            // and Released To Wild
            if ((getOwnerID().intValue() == 0) &&
                    (getMovementType().intValue() != MOVETYPE_ESCAPED) &&
                    (getMovementType().intValue() != MOVETYPE_STOLEN) &&
                    (getMovementType().intValue() != MOVETYPE_RECLAIMED) &&
                    (getMovementType().intValue() != MOVETYPE_RELEASED)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_select_an_owner_for_this_type_of_movement."));
            }

            // You can't return a movement that isn't going anywhere
            if ((getMovementDate() == null) && (getReturnDate() != null)) {
                throw new BOValidationException(Global.i18n("bo",
                        "return_without_movement"));
            }

            // Return must come after or same day as movement
            if ((getReturnDate() != null) && (getMovementDate() != null)) {
                Date cmov = (Date) getMovementDate();
                Date cret = (Date) getReturnDate();

                if (cmov.getTime() > cret.getTime()) {
                    throw new BOValidationException(Global.i18n("bo",
                            "cannot_return_before_movement"));
                }
            }

            // Make sure there aren't multiple open movement records:
            // This is only valid if this particular movement record is
            // not returned - in which case, there is no need to check.
            if ((getMovementDate() != null) && (getReturnDate() == null)) {
                Adoption adt = new Adoption();
                adt.openRecordset("AnimalID = " + getAnimalID() +
                    " AND MovementType > 0 AND MovementDate Is Not Null AND ReturnDate Is Null AND ID <> " +
                    getID());

                if (!adt.getEOF()) {
                    adt.free();
                    adt = null;
                    throw new BOValidationException(Global.i18n("bo",
                            "You_are_not_permitted_multiple_open_movement_records."));
                }

                adt.free();
                adt = null;
            }

            // If this movement has a retailer set on it, then make sure that
            // the animal has a retailer movement at some point in it's history.
            if (getRetailerID().intValue() != 0) {
                Adoption adt = new Adoption();
                adt.openRecordset("AnimalID = " + getAnimalID() +
                    " AND MovementType = " + MOVETYPE_RETAILER + " AND ID <> " +
                    getID());

                if (adt.getEOF()) {
                    adt.free();
                    adt = null;
                    throw new BOValidationException(Global.i18n("bo",
                            "You_have_marked_this_movement_as_from_a_retailer_but_this_animal_has_no_prior_retailer_movements"));
                }

                adt.free();
                adt = null;
            }

            // Date range check
            // (Movement and Return dates supplied)
            // Is there another record:
            // With a movementdate or returndate BETWEEN this.movement and
            // this.return
            // - exclude same day so < and >
            if ((getMovementDate() != null) && (getReturnDate() != null)) {
                Adoption adt = new Adoption();
                adt.openRecordset("AnimalID = " + getAnimalID() +
                    " AND ID <> " + getID() + " AND ((ReturnDate > '" +
                    SQLRecordset.getSQLRepresentationOfDate(getMovementDate()) +
                    "' AND ReturnDate < '" +
                    SQLRecordset.getSQLRepresentationOfDate(getReturnDate()) +
                    "')" + " OR (MovementDate < '" +
                    SQLRecordset.getSQLRepresentationOfDate(getReturnDate()) +
                    "' AND MovementDate > '" +
                    SQLRecordset.getSQLRepresentationOfDate(getMovementDate()) +
                    "'))");

                if (!adt.getEOF()) {
                    adt.free();
                    adt = null;
                    throw new BOValidationException(Global.i18n("bo",
                            "movement_clashes_with_existing"));
                }

                adt.free();
                adt = null;
            }

            // (Movement only)
            // Does this movement date fall within the date range of an already
            // returned movement for the same animal? Loop through animal
            // movement
            // records, excluding this one, and convert their movementdates and
            // returndates to calendars. if this is after() movementdate and
            // before() returndate, then we have a problem!
            if ((getMovementDate() != null) && (getReturnDate() == null)) {
                Adoption adt = new Adoption();
                adt.openRecordset("AnimalID = " + getAnimalID() +
                    " AND ID <> " + getID());

                while (!adt.getEOF()) {
                    // Ignore records without both movement and return as
                    // they will be picked up by dual open movement check
                    if ((adt.getMovementDate() != null) &&
                            (adt.getReturnDate() != null)) {
                        Date move = (Date) adt.getMovementDate();
                        Date ret = (Date) adt.getReturnDate();
                        Date thismove = (Date) getMovementDate();

                        if ((thismove.getTime() > move.getTime()) &&
                                (thismove.getTime() < ret.getTime())) {
                            adt.free();
                            adt = null;
                            throw new BOValidationException(Global.i18n("bo",
                                    "movement_clashes_with_existing"));
                        }
                    }

                    adt.moveNext();
                }

                adt.free();
                adt = null;
            }

            /*
             * ========= OLD CODE - didn't allow historic non-clashing records
             * ========= but it might come in useful at some point. // Check to
             * see if this movement clashes into the date range // of another.
             * Same day movements are allowed (one can start // the same day
             * another finishes) // Need to find another movement record on this
             * animal which is // a) Returned after this one started // b)
             * Started before this one returned if
             * (!getMovementDate().equals(SQLRecordset.NULL_VALUE)) { Adoption
             * adt = new Adoption(); adt.openRecordset("AnimalID = " +
             * getAnimalID() + " AND ReturnDate > '" + getMovementDate() + "'
             * AND ID <> " + getID()); if (!adt.getEOF()) { adt.free(); adt =
             * null; throw new BOValidationException(Global.i18n("bo",
             * "movement_returned_after_this_starts")); } adt.free(); adt =
             * null; } if (!getReturnDate().equals(SQLRecordset.NULL_VALUE)) {
             * Adoption adt = new Adoption(); adt.openRecordset("AnimalID = " +
             * getAnimalID() + " AND MovementDate < '" + getReturnDate() + "'
             * AND ReturnDate Is Null AND ID <> " + getID()); if (!adt.getEOF()) {
             * adt.free(); adt = null; throw new
             * BOValidationException(Global.i18n("bo",
             * "movement_starts_before_this_returned")); } adt.free(); adt =
             * null; }
             */

            // If there is a cancelled reservation date, make sure
            // we have a reserve date, and if we do that it isn't
            // after the cancelled date.
            if (getReservationCancelledDate() != null) {
                if (getReservationDate() == null) {
                    throw new BOValidationException(Global.i18n("bo",
                            "cancel_reserve_without_reserve"));
                }

                Date reserveDate = (Date) getReservationDate();
                Date resCancDate = (Date) getReservationCancelledDate();

                if (reserveDate.getTime() > resCancDate.getTime()) {
                    throw new BOValidationException(Global.i18n("bo",
                            "reserve_after_cancellation"));
                }
            }

            // If this is a reservation record of some type, make sure
            // there isn't an open movement. This only applies if the
            // record didn't already exist, or the reservation date
            // has changed.
            if ((getReservationDate() != null) && (getMovementDate() == null)) {
                Adoption oldrec = new Adoption();
                oldrec.openRecordset("ID = " + getID());

                boolean isNew = oldrec.getEOF(); // isNew determines whether
                                                 // the record is new or
                                                 // reservation date changed
                                                 // if !isNew then we don't need to do this validation test

                if (!isNew) {
                    isNew = !oldrec.getReservationDate()
                                   .equals(getReservationDate());
                }

                if (isNew) {
                    Adoption adt = new Adoption();
                    adt.openRecordset("AnimalID = " + getAnimalID() +
                        " AND MovementDate Is Not Null AND ReturnDate Is Null AND ID <> " +
                        getID());

                    if (!adt.getEOF()) {
                        // Make sure we're looking at the last movement
                        adt.moveLast();

                        // Only complain about the reservation if the animal
                        // isn't on
                        // foster - fostering is ok as lots of shelters put
                        // animals on
                        // foster where they don't have facilities.
                        if (adt.getMovementType().intValue() != MOVETYPE_FOSTER) {
                            adt.free();
                            adt = null;
                            throw new BOValidationException(Global.i18n("bo",
                                    "cant_reserve_with_open_movement"));
                        }
                    }
                }
            }

            // Check to see if the adoption number is unique
            Adoption na = new Adoption();
            na.openRecordset("AdoptionNumber LIKE '" + getAdoptionNumber() +
                "'");

            if (!na.getEOF()) {
                if (!na.getID().equals(getID())) {
                    na.free();
                    na = null;
                    throw new BOValidationException(Global.i18n("bo",
                            "reference_number_used", getAdoptionNumber()));
                }
            }

            na.free();
            na = null;
        } catch (Exception e) {
            Global.logException(e, this.getClass());
            throw new BOValidationException(e.getMessage());
        }
    }

    /**
     * Creates a boarding cost record for this adoption.
     * Does nothing if:
     *    System options are disable
     *    it's not an adoption
     *    We already created this record
     */
    public void createAdoptionBoardingCost() throws Exception {
        // Bail if system option isn't on
        if (!Configuration.getBoolean("CreateBoardingCostOnAdoption")) {
            return;
        }

        // Is this an adoption? Bail if not
        if (getMovementType().intValue() != MOVETYPE_ADOPTION) {
            return;
        }

        // We need a movement date
        if (getMovementDate() == null) {
            return;
        }

        // Have we already created this record?
        int costtype = Configuration.getInteger("BoardingCostType");

        if (0 == DBConnection.executeForCount(
                    "SELECT COUNT(*) FROM animalcost WHERE AnimalID = " +
                    getAnimalID() + " AND CostDate = '" +
                    Utils.getSQLDate(getMovementDate()) + "' AND " +
                    "CostTypeID = " + costtype)) {
            // Nope - let's create it
            AnimalCost c = new AnimalCost();
            c.openRecordset("ID = 0");
            c.addNew();

            // Calculate on shelter cost for this animal
            double cost = DBConnection.executeForSum(
                    "SELECT (DaysOnShelter * DailyBoardingCost) " +
                    "FROM animal WHERE ID = " + getAnimalID());

            // Is the cost zero? Bail out if so
            if (cost == 0) {
                return;
            }

            c.setAnimalID(getAnimalID());
            c.setCostDate(getMovementDate());
            c.setCostAmount(new Double(cost));
            c.setCostTypeID(new Integer(costtype));
            c.setDescription(getAnimal().getTimeOnShelter());
            c.save(Global.currentUserName);
        }
    }

    /**
     * Returns the length of time an animal was adopted for, before being
     * returned. If the animal has not been returned, it returns an empty
     * string.
     */
    public String getAdoptionLength() throws CursorEngineException {
        // If the adoption date or return date are null, exit now
        if ((getMovementType().intValue() != MOVETYPE_ADOPTION) ||
                (getMovementDate() == null) || (getReturnDate() == null)) {
            return "";
        }

        // Get animal's adoption date
        Calendar adoptiondate = Calendar.getInstance();
        adoptiondate.setTime((Date) getMovementDate());

        Calendar returndate = Calendar.getInstance();
        returndate.setTime((Date) getReturnDate());

        // Work out what 6 weeks ago was
        Calendar sixweeks = Calendar.getInstance();
        sixweeks.add(Calendar.WEEK_OF_YEAR, -6);

        // If adoption date is after 6 weeks ago,
        // format in weeks
        if (adoptiondate.after(sixweeks)) {
            long diff = Utils.getDateDiff(returndate, adoptiondate);

            // It's currently returned in minutes, so calculate weeks
            // by dividing by 60 (hours), 24 (day), 7 (week)
            diff = (diff / 60);
            diff = (diff / 24);
            diff = (diff / 7);

            return Global.i18n("bo", "x_weeks", Long.toString(diff));
        } else {
            // Otherwise format in months and years
            long diff = Utils.getDateDiff(returndate, adoptiondate);
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
}
