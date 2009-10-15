/*
 Animal Shelter Manager
 Copyright(c)2000-2009, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.Date;


public class AnimalMedicalTreatment extends UserInfoBO {
    Animal animal = null;
    AnimalMedical animalmedical = null;

    public AnimalMedicalTreatment() {
        tableName = "animalmedicaltreatment";
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

    public Integer getAnimalMedicalID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalMedicalID");
    }

    public void setAnimalMedicalID(Integer newValue)
        throws CursorEngineException {
        rs.setField("AnimalMedicalID", newValue);
    }

    public AnimalMedical getAnimalMedical() throws CursorEngineException {
        // Do we have a cached animal medical?
        if (animalmedical != null) {
            try {
                // Is it the right one for this record?
                if (animalmedical.getID().equals(getAnimalMedicalID())) {
                    // It is! Return it!
                    return animalmedical;
                }
            } catch (Exception e) {
                // Something is wrong with the animal record - ignore it.
            }
        }

        // We don't have one, or the one we do have is wrong,
        // look it up again.
        animalmedical = new AnimalMedical();
        animalmedical.openRecordset("ID = " + getAnimalMedicalID());

        if (animalmedical.getEOF()) {
            return null;
        } else {
            return animalmedical;
        }
    }

    public Date getDateRequired() throws CursorEngineException {
        return (Date) rs.getField("DateRequired");
    }

    public void setDateRequired(Date newValue) throws CursorEngineException {
        rs.setField("DateRequired", newValue);
    }

    public Date getDateGiven() throws CursorEngineException {
        return (Date) rs.getField("DateGiven");
    }

    public void setDateGiven(Date newValue) throws CursorEngineException {
        rs.setField("DateGiven", newValue);
    }

    public String getGivenBy() throws CursorEngineException {
        return (String) rs.getField("GivenBy");
    }

    public void setGivenBy(String newValue) throws CursorEngineException {
        rs.setField("GivenBy", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    public Integer getTreatmentNumber() throws CursorEngineException {
        return (Integer) rs.getField("TreatmentNumber");
    }

    public void setTreatmentNumber(Integer newValue)
        throws CursorEngineException {
        rs.setField("TreatmentNumber", newValue);
    }

    public Integer getTotalTreatments() throws CursorEngineException {
        return (Integer) rs.getField("TotalTreatments");
    }

    public void setTotalTreatments(Integer newValue)
        throws CursorEngineException {
        rs.setField("TotalTreatments", newValue);
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
    }
}
