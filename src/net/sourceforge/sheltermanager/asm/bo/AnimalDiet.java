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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.Date;


public class AnimalDiet extends UserInfoBO {
    /** Cached copy of animal the current diet belongs to */
    private Animal animal = null;

    public AnimalDiet() {
        tableName = "animaldiet";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public AnimalDiet copy() throws CursorEngineException {
        AnimalDiet a = new AnimalDiet();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setAnimalID(getAnimalID());
        a.setDietID(getDietID());
        a.setDateStarted(getDateStarted());
        a.setComments(getComments());

        return a;
    }

    public Integer getAnimalID() throws CursorEngineException {
        return (Integer) rs.getField("AnimalID");
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

    public Integer getDietID() throws CursorEngineException {
        return (Integer) rs.getField("DietID");
    }

    public void setDietID(Integer newValue) throws CursorEngineException {
        rs.setField("DietID", newValue);
    }

    /**
     * Reads the vaccination name from the foreign key to the
     * <code>VaccinationType</code> object.
     *
     * @throws CursorEngineException
     *             If the <code>VaccinationType</code> object fails.
     */
    public String getDietName() throws CursorEngineException {
        return LookupCache.getDietName(getDietID());
    }

    public Date getDateStarted() throws CursorEngineException {
        return (Date) rs.getField("DateStarted");
    }

    public void setDateStarted(Date newValue) throws CursorEngineException {
        rs.setField("DateStarted", newValue);
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
            if (getDateStarted() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "you_must_supply_a_start_date_for_the_diet"));
            }

            if ((getDietID() == null) || (getDietID().intValue() == 0)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_select_a_diet_type"));
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(Global.i18n("bo",
                    "An_error_occurred_accessing_the_object:_") +
                e.getMessage());
        }
    }
}
