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


public class AnimalCost extends UserInfoBO<AnimalCost> {
    /** Cached copy of animal the current cost belongs to */
    private Animal animal = null;

    public AnimalCost() {
        tableName = "animalcost";
    }

    public AnimalCost(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public AnimalCost copy() throws CursorEngineException {
        AnimalCost a = new AnimalCost();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setAnimalID(getAnimalID());
        a.setCostTypeID(getCostTypeID());
        a.setCostDate(getCostDate());
        a.setCostAmount(getCostAmount());
        a.setDescription(getDescription());

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

    public Integer getCostTypeID() throws CursorEngineException {
        return (Integer) rs.getField("CostTypeID");
    }

    public void setCostTypeID(Integer newValue) throws CursorEngineException {
        rs.setField("CostTypeID", newValue);
    }

    public String getCostTypeName() throws CursorEngineException {
        return LookupCache.getCostTypeName(getCostTypeID());
    }

    public Date getCostDate() throws CursorEngineException {
        return (Date) rs.getField("CostDate");
    }

    public void setCostDate(Date newValue) throws CursorEngineException {
        rs.setField("CostDate", newValue);
    }

    public Double getCostAmount() throws CursorEngineException {
        return (Double) rs.getField("CostAmount");
    }

    public void setCostAmount(Double newValue) throws CursorEngineException {
        rs.setField("CostAmount", newValue);
    }

    public String getDescription() throws CursorEngineException {
        return (String) rs.getField("Description");
    }

    public void setDescription(String newValue) throws CursorEngineException {
        rs.setField("Description", newValue);
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
            if (getCostDate() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "you_must_supply_a_date_for_the_cost"));
            }

            if ((getDescription() == null) || getDescription().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "you_must_supply_a_description_for_the_cost"));
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
