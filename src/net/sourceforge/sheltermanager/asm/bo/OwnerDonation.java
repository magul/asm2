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
import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.Date;


public class OwnerDonation extends UserInfoBO {
    private Owner owner = null;

    public OwnerDonation() {
        tableName = "ownerdonation";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getOwnerID() throws CursorEngineException {
        return (Integer) rs.getField("OwnerID");
    }

    public void setOwnerID(Integer newValue) throws CursorEngineException {
        rs.setField("OwnerID", newValue);
    }

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

    public Integer getAnimalID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalID");
    }

    public void setAnimalID(Integer newValue) throws CursorEngineException {
        rs.setField("AnimalID", newValue);
    }

    public Integer getMovementID() throws CursorEngineException {
        return (Integer) rs.getField("MovementID");
    }

    public void setMovementID(Integer newValue) throws CursorEngineException {
        rs.setField("MovementID", newValue);
    }

    public Integer getFrequency() throws CursorEngineException {
        return (Integer) rs.getField("Frequency");
    }

    public void setFrequency(Integer newValue) throws CursorEngineException {
        rs.setField("Frequency", newValue);
    }

    public Integer getNextCreated() throws CursorEngineException {
        return (Integer) rs.getField("NextCreated");
    }

    public void setNextCreated(Integer newValue) throws CursorEngineException {
        rs.setField("NextCreated", newValue);
    }

    public Integer getDonationTypeID() throws CursorEngineException {
        return (Integer) rs.getField("DonationTypeID");
    }

    public String getDonationTypeName() throws CursorEngineException {
        return LookupCache.getDonationTypeName(getDonationTypeID());
    }

    public void setDonationTypeID(Integer newValue)
        throws CursorEngineException {
        rs.setField("DonationTypeID", newValue);
    }

    public Date getDateReceived() throws CursorEngineException {
        return (Date) rs.getField("Date");
    }

    public void setDateReceived(Date newValue) throws CursorEngineException {
        rs.setField("Date", newValue);
    }

    public Date getDateDue() throws CursorEngineException {
        return (Date) rs.getField("DateDue");
    }

    public void setDateDue(Date newValue) throws CursorEngineException {
        rs.setField("DateDue", newValue);
    }

    public Double getDonation() throws CursorEngineException {
        return new Double(rs.getDouble("Donation"));
    }

    public void setDonation(Double newValue) throws CursorEngineException {
        rs.setField("Donation", newValue);
    }

    /**
     * Returns the ID formatted with padded zeroes to make a receipt number.
     */
    public String getReceiptNum() throws CursorEngineException {
        String pad = "0000000000";
        String newnum = getID().toString();
        newnum = pad.substring(0, 9 - newnum.length()) + newnum;

        return newnum;
    }

    public Integer getIsGiftAid() throws CursorEngineException {
        return (Integer) rs.getField("IsGiftAid");
    }

    public void setIsGiftAid(Integer newValue) throws CursorEngineException {
        rs.setField("IsGiftAid", newValue);
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

    public void addNew() throws CursorEngineException {
        super.addNew();

        final Integer z = new Integer(0);

        setOwnerID(z);
        setAnimalID(z);
        setMovementID(z);
        setDonationTypeID(z);
        setFrequency(z);
        setNextCreated(z);
        setDonation(new Double(0));
        setComments("");
        setIsGiftAid(z);
    }

    public OwnerDonation copy() throws Exception {
        OwnerDonation o = new OwnerDonation();
        o.openRecordset("ID = 0");
        o.addNew();

        o.setOwnerID(getOwnerID());
        o.setAnimalID(getAnimalID());
        o.setMovementID(getMovementID());
        o.setDonationTypeID(getDonationTypeID());
        o.setFrequency(getFrequency());
        o.setNextCreated(getNextCreated());
        o.setDateReceived(getDateReceived());
        o.setDateDue(getDateDue());
        o.setDonation(getDonation());
	o.setIsGiftAid(getIsGiftAid());
        o.setComments(getComments());

        return o;
    }

    public void validate() throws BOValidationException {
        try {
            // Check for blank or null donation and change it to 0
            if (getDonation() == null) {
                setDonation(new Double(0));
            }

            if ((getDateDue() == null) && (getDateReceived() == null)) {
                throw new BOValidationException(Global.i18n("bo",
                        "ownerdonation_must_have_at_least_one_date"));
            }

            if ((getOwnerID() == null) || (getOwnerID().intValue() == 0)) {
                throw new BOValidationException(Global.i18n("bo",
                        "donation_must_have_an_owner"));
            }
        } catch (Exception e) {
            Global.logException(e, this.getClass());
            throw new BOValidationException(e.getMessage());
        }
    }
}
