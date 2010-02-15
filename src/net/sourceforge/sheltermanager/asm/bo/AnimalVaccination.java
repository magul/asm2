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


public class AnimalVaccination extends UserInfoBO {
    public final static String VACCINATION = "0";
    public final static String TREATMENT = "1";

    /** Cached copy of animal the current vaccination belongs to */
    private Animal animal = null;

    public AnimalVaccination() {
        tableName = "animalvaccination";
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

    public AnimalVaccination copy() throws CursorEngineException {
        AnimalVaccination a = new AnimalVaccination();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setAnimalID(getAnimalID());
        a.setVaccinationID(getVaccinationID());
        a.setDateOfVaccination(getDateOfVaccination());
        a.setDateRequired(getDateRequired());
        a.setCost(getCost());
        a.setComments(getComments());

        return a;
    }

    public Animal getAnimal() throws CursorEngineException {
        // Do we have a cached animal?
        if (animal != null) {
            try {
                // Is it the right one for this record?
                if (animal.getID().equals(getAnimalID())) {
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

    public void setAnimalID(Integer newValue) throws CursorEngineException {
        rs.setField("AnimalID", newValue);
    }

    public Integer getVaccinationID() throws CursorEngineException {
        return (Integer) rs.getField("VaccinationID");
    }

    public void setVaccinationID(Integer newValue) throws CursorEngineException {
        rs.setField("VaccinationID", newValue);
    }

    /**
     * Reads the vaccination name from the foreign key to the
     * <code>VaccinationType</code> object.
     *
     * @throws CursorEngineException
     *             If the <code>VaccinationType</code> object fails.
     */
    public String getVaccinationTypeName() throws CursorEngineException {
        return LookupCache.getVaccinationTypeName(getVaccinationID());
    }
    

    public String getVaccinationName() {
        try {
        	return getVaccinationTypeName();
        } catch (Exception e) {
        	return "";
        }
    }

    public Date getDateOfVaccination() throws CursorEngineException {
        return (Date) rs.getField("DateOfVaccination");
    }

    public void setDateOfVaccination(Date newValue)
        throws CursorEngineException {
        rs.setField("DateOfVaccination", newValue);
    }

    public Date getDateRequired() throws CursorEngineException {
        return (Date) rs.getField("DateRequired");
    }

    public void setDateRequired(Date newValue) throws CursorEngineException {
        rs.setField("DateRequired", newValue);
    }

    public Double getCost() throws CursorEngineException {
        return (Double) rs.getField("Cost");
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
            if (getDateRequired() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Date_Required_is_mandatory."));
            }
        } catch (Exception e) {
            Global.logException(e, this.getClass());
            throw new BOValidationException(e.getMessage());
        }
    }

}
