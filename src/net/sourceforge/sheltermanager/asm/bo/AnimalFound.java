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
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.Date;


public class AnimalFound extends UserInfoBO {
    /** Cached owner */
    private Owner owner = null;

    public AnimalFound() {
        tableName = "animalfound";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getSpeciesID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalTypeID");
    }

    public void setSpeciesID(Integer newValue) throws CursorEngineException {
        rs.setField("AnimalTypeID", newValue);
    }

    public String getSpeciesName() throws CursorEngineException {
        return LookupCache.getSpeciesName(getSpeciesID());
    }

    public Date getDateReported() throws CursorEngineException {
        return (Date) rs.getField("DateReported");
    }

    public void setDateReported(Date newValue) throws CursorEngineException {
        rs.setField("DateReported", newValue);
    }

    public Date getDateFound() throws CursorEngineException {
        return (Date) rs.getField("DateFound");
    }

    public void setDateFound(Date newValue) throws CursorEngineException {
        rs.setField("DateFound", newValue);
    }

    public Integer getBaseColourID() throws CursorEngineException {
        return (Integer) rs.getField("BaseColourID");
    }

    public void setBaseColourID(Integer newValue) throws CursorEngineException {
        rs.setField("BaseColourID", newValue);
    }

    public String getBaseColourName() throws CursorEngineException {
        return LookupCache.getBaseColourName(getBaseColourID());
    }

    public String getDistFeat() throws CursorEngineException {
        return (String) rs.getField("DistFeat");
    }

    public void setDistFeat(String newValue) throws CursorEngineException {
        rs.setField("DistFeat", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
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

    public String getAreaFound() throws CursorEngineException {
        return (String) rs.getField("AreaFound");
    }

    public void setAreaFound(String newValue) throws CursorEngineException {
        rs.setField("AreaFound", newValue);
    }

    public String getAreaPostcode() throws CursorEngineException {
        return (String) rs.getField("AreaPostcode");
    }

    public void setAreaPostcode(String newValue) throws CursorEngineException {
        rs.setField("AreaPostcode", newValue);
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

    public Date getReturnToOwnerDate() throws CursorEngineException {
        return (Date) rs.getField("ReturnToOwnerDate");
    }

    public void setReturnToOwnerDate(Date newValue)
        throws CursorEngineException {
        rs.setField("ReturnToOwnerDate", newValue);
    }

    /**
     * Overridden from superclass - contains Found Animal specific validation
     * routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            // Make sure we have a contact name
            if ((getOwnerID() == null) || (getOwnerID().intValue() == 0)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_a_contact_name."));
            }

            // Make sure we have a found date
            if (getDateFound() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_date_the_animal_was_found."));
            }

            // Make sure we have a reported date
            if (getDateReported() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_date_the_animal_was_reported_found."));
            }

            // Make sure we have an area
            if ((getAreaFound() == null) || getAreaFound().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_area_the_animal_was_found_in."));
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
