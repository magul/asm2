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


public class AnimalWaitingList extends UserInfoBO<AnimalWaitingList> {
    /** Cached owner */
    private Owner owner = null;

    public AnimalWaitingList() {
        tableName = "animalwaitinglist";
    }

    public AnimalWaitingList(String where) {
        this();
        openRecordset(where);
    }
    
    public void addNew() throws CursorEngineException {
    	super.addNew();
    	setAnimalDescription("");
    	setAutoRemovePolicy(new Integer(0));
    	setCanAffordDonation(new Integer(0));
    	setComments("");
    	setDateOfLastOwnerContact(new Date());
    	setDatePutOnList(new Date());
    	setDateRemovedFromList(null);
    	setOwnerID(new Integer(0));
    	setReasonForRemoval("");
    	setReasonForWantingToPart("");
    	setSpeciesID(new Integer(1));
    	setUrgency(new Integer(Configuration.getInteger("WaitingListDefaultUrgency") + 1));
    	setUrgencyUpdateDate(null);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
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

    public Date getDatePutOnList() throws CursorEngineException {
        return (Date) rs.getField("DatePutOnList");
    }

    public void setDatePutOnList(Date newValue) throws CursorEngineException {
        rs.setField("DatePutOnList", newValue);
    }

    public Integer getOwnerID() throws CursorEngineException {
        return (Integer) rs.getField("OwnerID");
    }

    public void setOwnerID(Integer newValue) throws CursorEngineException {
        rs.setField("OwnerID", newValue);
    }

    /**
     * Returns the owner for this record. If none is set, a NULL is returned.
     *
     * @return The owner on this record
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

    public String getAnimalDescription() throws CursorEngineException {
        return (String) rs.getField("AnimalDescription");
    }

    public void setAnimalDescription(String newValue)
        throws CursorEngineException {
        rs.setField("AnimalDescription", newValue);
    }

    public String getReasonForWantingToPart() throws CursorEngineException {
        return (String) rs.getField("ReasonForWantingToPart");
    }

    public void setReasonForWantingToPart(String newValue)
        throws CursorEngineException {
        rs.setField("ReasonForWantingToPart", newValue);
    }

    public Integer getCanAffordDonation() throws CursorEngineException {
        return (Integer) rs.getField("CanAffordDonation");
    }

    public void setCanAffordDonation(Integer newValue)
        throws CursorEngineException {
        rs.setField("CanAffordDonation", newValue);
    }

    public Integer getUrgency() throws CursorEngineException {
        return (Integer) rs.getField("Urgency");
    }

    public void setUrgency(Integer newValue) throws CursorEngineException {
        rs.setField("Urgency", newValue);
    }

    public Date getDateRemovedFromList() throws CursorEngineException {
        return (Date) rs.getField("DateRemovedFromList");
    }

    public void setDateRemovedFromList(Date newValue)
        throws CursorEngineException {
        rs.setField("DateRemovedFromList", newValue);
    }

    public String getReasonForRemoval() throws CursorEngineException {
        return (String) rs.getField("ReasonForRemoval");
    }

    public void setReasonForRemoval(String newValue)
        throws CursorEngineException {
        rs.setField("ReasonForRemoval", newValue);
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

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    public Date getUrgencyUpdateDate() throws CursorEngineException {
        return (Date) rs.getField("UrgencyUpdateDate");
    }

    public void setUrgencyUpdateDate(Date newValue)
        throws CursorEngineException {
        rs.setField("UrgencyUpdateDate", newValue);
    }

    public Date getUrgencyLastUpdatedDate() throws CursorEngineException {
        return (Date) rs.getField("UrgencyLastUpdatedDate");
    }

    public void setUrgencyLastUpdatedDate(Date newValue)
        throws CursorEngineException {
        rs.setField("UrgencyLastUpdatedDate", newValue);
    }

    /**
     * NB: Autoremove policy is actually the number of weeks without owner
     * contact to auto remove the record. 0 means don't use auto removal.
     */
    public Integer getAutoRemovePolicy() throws CursorEngineException {
        return (Integer) rs.getField("AutoRemovePolicy");
    }

    public void setAutoRemovePolicy(Integer newValue)
        throws CursorEngineException {
        rs.setField("AutoRemovePolicy", newValue);
    }

    public Date getDateOfLastOwnerContact() throws CursorEngineException {
        return (Date) rs.getField("DateOfLastOwnerContact");
    }

    public void setDateOfLastOwnerContact(Date newValue)
        throws CursorEngineException {
        rs.setField("DateOfLastOwnerContact", newValue);
    }

    /**
     * Overridden from superclass - contains Waiting List specific validation
     * routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            // If we have no current record, forget it
            if (getEOF()) {
                return;
            }

            if (getDatePutOnList() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_the_date_this_entry_was_put_on_the_list."));
            }

            if ((getOwnerID() == null) || (getOwnerID().intValue() == 0)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_contact_name."));
            }
        } catch (Exception e) {
            Global.logException(e, this.getClass());
            throw new BOValidationException(e.getMessage());
        }
    }

    /**
     * Finds and removes waiting list entries that have gone past the auto
     * removal date without owner contact.
     */
    public static void autoRemoveItems() {
        try {
            AnimalWaitingList awl = new AnimalWaitingList();
            awl.openRecordset("DateRemovedFromList Is Null AND " +
                "AutoRemovePolicy > 0 AND " +
                "DateOfLastOwnerContact Is Not Null");

            while (!awl.getEOF()) {
                Calendar lastDate = Utils.dateToCalendar(awl.getDateOfLastOwnerContact());
                lastDate.add(Calendar.WEEK_OF_YEAR,
                    awl.getAutoRemovePolicy().intValue());

                if (lastDate.before(Calendar.getInstance())) {
                    DBConnection.executeAction(
                        "UPDATE animalwaitinglist SET DateRemovedFromList = '" +
                        SQLRecordset.getSQLRepresentationOfDate(
                            Utils.calendarToDate(lastDate)) + "', " +
                        "ReasonForRemoval = '" +
                        Global.i18n("bo", "auto_removed_due_to_owner_contact") +
                        "' " + "WHERE " + "ID = " + awl.getID());
                }

                awl.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, AnimalWaitingList.class);
        }
    }

    /**
     * Updates the urgency rating of waiting list entries after a configurable
     * period of time.
     */
    public static void updateUrgencies() {
        try {
            AnimalWaitingList awl = new AnimalWaitingList();
            boolean saveRequired = false;

            // Find all the animals where the next UrgencyUpdateDate field
            // is greater than or equal to today and the urgency is larger than
            // 2
            // (this means that only manual intervention can bump the urgency up
            // to
            // "Urgent" ).
            awl.openRecordset("UrgencyUpdateDate <= '" +
                SQLRecordset.getSQLRepresentationOfDate(new Date()) +
                "' AND Urgency > 2");

            while (!awl.getEOF()) {
                // Update the last updated date to today
                awl.setUrgencyLastUpdatedDate(new Date());

                // Update the next update to the next scheduled period
                int updatePeriod = Configuration.getInteger(
                        "WaitingListUrgencyUpdatePeriod");
                Calendar nextUpdate = Utils.dateToCalendar(awl.getUrgencyUpdateDate());
                nextUpdate.add(Calendar.DAY_OF_YEAR, updatePeriod);
                awl.setUrgencyUpdateDate(Utils.calendarToDate(nextUpdate));

                // Bump up the urgency
                awl.setUrgency(new Integer(awl.getUrgency().intValue() - 1));

                // Mark us as requiring a save
                saveRequired = true;

                awl.moveNext();
            }

            // Save the records back
            if (saveRequired) {
                awl.save(Global.currentUserName);
            }
        } catch (Exception e) {
            Global.logException(e, AnimalWaitingList.class);
        }
    }
}
