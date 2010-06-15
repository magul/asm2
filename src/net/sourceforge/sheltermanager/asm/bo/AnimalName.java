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
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.NormalBO;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;


public class AnimalName extends NormalBO<AnimalName> {
    public AnimalName() {
        tableName = "animalname";
    }

    public AnimalName(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getName() throws CursorEngineException {
        return (String) rs.getField("Name");
    }

    public void setName(String newValue) throws CursorEngineException {
        rs.setField("Name", newValue);
    }

    public Integer getSex() throws CursorEngineException {
        return (Integer) rs.getField("Sex");
    }

    public String getSexName() throws CursorEngineException {
        return LookupCache.getSexNameForID(getSex());
    }

    public void setSex(Integer newValue) throws CursorEngineException {
        rs.setField("Sex", newValue);
    }

    /**
     * Gets a random name based on the sex supplied from either the animal
     * database or the name database
     */
    public static String getRandomName(String sexID) {
        try {
            // Load all names from the animal database into a recordset
            SQLRecordset rsa = new SQLRecordset();
            rsa.openRecordset(
                "SELECT DISTINCT AnimalName FROM animal WHERE Sex=" + sexID,
                "animal");

            // Load all names from the animal name db into a recordset
            SQLRecordset rsb = new SQLRecordset();
            rsb.openRecordset("SELECT DISTINCT Name FROM animalname WHERE Sex=" +
                sexID, "animalname");

            int tot = (int) (rsa.getRecordCount() + rsb.getRecordCount());

            // Return nothing if there aren't any names
            if (tot == 0) {
                return "";
            }

            // Dump all names into an array
            String[] names = new String[tot];
            int i = 0;

            while (!rsa.getEOF()) {
                names[i] = (String) rsa.getField("AnimalName");
                rsa.moveNext();
                i++;
            }

            while (!rsb.getEOF()) {
                names[i] = (String) rsb.getField("Name");
                rsb.moveNext();
                i++;
            }

            // Choose a number between 0 and total
            double choice = (Math.random() * (double) tot);

            // Get the name
            return names[(int) choice];
        } catch (Exception e) {
            Global.logException(e, AnimalName.class);
        }

        // Otherwise, there aren't any names on file
        return "";
    }
}
