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
package net.sourceforge.sheltermanager.asm.ui.waitinglist;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;


public class WaitingListViewColumns {
    
    public static final String DEFAULT_COLUMNS = "Rank,OwnerName,OwnerAddress,HomeTelephone,DatePutOnList,\n" +
    		"DateRemovedFromList,Urgency,SpeciesID,AnimalDescription";
    
    private static SQLRecordset ranks = null;

    private static String i18n(String key) {
        return Global.i18n("uiwaitinglist", key);
    }

    /** Returns a count of the number of columns to display */
    public static int getColumnCount() {
        return getColumnNames().length;
    }

    /** Returns true if it's the default set of search result columns */
    public static boolean isDefaultColumns() {
        return Configuration.getString("WaitingListViewColumns", DEFAULT_COLUMNS)
                            .equalsIgnoreCase(DEFAULT_COLUMNS);
    }

    /** Returns an array of column names */
    public static String[] getColumnNames() {
        String[] names = Utils.split(Configuration.getString("WaitingListViewColumns",
                    DEFAULT_COLUMNS), ",");

        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].trim();
        }

        return names;
    }

    /** Returns an array of column labels */
    public static String[] getColumnLabels() {
        String[] names = getColumnNames();
        String[] labels = new String[names.length];

        for (int i = 0; i < names.length; i++) {
            labels[i] = getColumnLabel(i);
        }

        return labels;
    }

    /** Returns the name of a column */
    public static String getColumnName(int index) {
        return getColumnNames()[index];
    }
    
    /** Returns the label for a given column */
    public static String getColumnLabel(int index) {
        String n = getColumnName(index);

        if (n.equalsIgnoreCase("Rank")) {
            return i18n("Rank");
        }
        
        if (n.equalsIgnoreCase("SpeciesID")) {
            return i18n("Species");
        }

        if (n.equalsIgnoreCase("DatePutOnList")) {
            return i18n("Date_Put_On:");
        }

        if (n.equalsIgnoreCase("OwnerName")) {
            return i18n("Name:");
        }

        if (n.equalsIgnoreCase("OwnerAddress")) {
            return i18n("Address");
        }
        
        if (n.equalsIgnoreCase("OwnerTown")) {
            return Global.i18n("uiowner", "town");
        }
        
        if (n.equalsIgnoreCase("OwnerCounty")) {
        	return Global.i18n("uiowner", "county");
        }
        
        if (n.equalsIgnoreCase("OwnerPostcode")) {
        	return Global.i18n("uiowner", "Postcode");
        }

        if (n.equalsIgnoreCase("HomeTelephone")) {
        	return Global.i18n("uiowner", "Telephone:");
        }

        if (n.equalsIgnoreCase("WorkTelephone")) {
        	return Global.i18n("uiowner", "Work_Tel");
        }

        if (n.equalsIgnoreCase("MobileTelephone")) {
        	return Global.i18n("uiowner", "Mobile_Te");
        }

        if (n.equalsIgnoreCase("AnimalDescription")) {
            return i18n("Description:");
        }

        if (n.equalsIgnoreCase("ReasonForWantingToPart")) {
            return i18n("Reason:");
        }

        if (n.equalsIgnoreCase("CanAffordDonation")) {
            return i18n("Donation?");
        }

        if (n.equalsIgnoreCase("Urgency")) {
            return i18n("Urgency");
        }

        if (n.equalsIgnoreCase("DateRemovedFromList")) {
            return i18n("Date_Removed");
        }

        if (n.equalsIgnoreCase("DateOfLastOwnerContact")) {
            return i18n("date_of_last_owner_contact");
        }

        if (n.equalsIgnoreCase("ReasonForRemoval")) {
            return i18n("Removal_Reason:");
        }

        if (n.equalsIgnoreCase("Comments")) {
            return i18n("Comments:");
        }

        return "";
    }

    public static String yesNo(int v) {
        return (v == 0) ? i18n("No") : i18n("Yes");
    }

    public static String yesNoUnknown(int v) {
        switch (v) {
        case 0:
            return i18n("Yes");

        case 1:
            return i18n("No");

        default:
            return i18n("Unknown");
        }
    }

    public static String posNegUnknown(int v) {
        switch (v) {
        case 0:
            return i18n("Unknown");

        case 1:
            return Global.i18n("uiwordprocessor", "Negative");

        default:
            return Global.i18n("uiwordprocessor", "Postive");
        }
    }

    /** Formats the value given for a particular column
     * @param r The animal recordset, at the correct row
     * @param add The additional fields recordset containing LinkID and Value fields
     *
     */
    public static String format(String colname, SQLRecordset r)
        throws CursorEngineException {
        String n = colname;
        
        if (n.equalsIgnoreCase("Rank")) {
        	return getRank(r.getInt("ID"));
        }
        
        if (n.equalsIgnoreCase("SpeciesID")) {
            return LookupCache.getSpeciesName(r.getInt("SpeciesID"));
        }

        if (n.equalsIgnoreCase("DatePutOnList")) {
            return Utils.formatTableDate(r.getDate("DatePutOnList"));
        }

        if (n.equalsIgnoreCase("OwnerName")) {
            return r.getString("OwnerName");
        }

        if (n.equalsIgnoreCase("OwnerAddress")) {
            return r.getString("OwnerAddress");
        }
        
        if (n.equalsIgnoreCase("OwnerTown")) {
            return r.getString("OwnerTown");
        }
        
        if (n.equalsIgnoreCase("OwnerCounty")) {
            return r.getString("OwnerCounty");
        }
        
        if (n.equalsIgnoreCase("OwnerPostcode")) {
            return r.getString("OwnerPostcode");
        }

        if (n.equalsIgnoreCase("HomeTelephone")) {
            return r.getString("HomeTelephone");
        }

        if (n.equalsIgnoreCase("WorkTelephone")) {
            return r.getString("WorkTelephone");
        }

        if (n.equalsIgnoreCase("MobileTelephone")) {
            return r.getString("MobileTelephone");
        }

        if (n.equalsIgnoreCase("AnimalDescription")) {
            return r.getString("AnimalDescription");
        }

        if (n.equalsIgnoreCase("ReasonForWantingToPart")) {
            return r.getString("ReasonForWantingToPart");
        }

        if (n.equalsIgnoreCase("CanAffordDonation")) {
            return yesNo(r.getInt("CanAffordDonation"));
        }

        if (n.equalsIgnoreCase("Urgency")) {
            return LookupCache.getUrgencyNameForID(r.getInt("Urgency"));
        }

        if (n.equalsIgnoreCase("DateRemovedFromList")) {
            return Utils.formatTableDate(r.getDate("DateRemovedFromList"));
        }

        if (n.equalsIgnoreCase("DateOfLastOwnerContact")) {
            return Utils.formatTableDate(r.getDate("DateOfLastOwnerContact"));
        }

        if (n.equalsIgnoreCase("ReasonForRemoval")) {
            return r.getString("ReasonForRemoval");
        }

        if (n.equalsIgnoreCase("Comments")) {
            return r.getString("Comments");
        }

        // Give up
        return "";
    }
    
    /**
     * Calculates the rank (order) of any item on the waiting list
     * - if the item is no longer on the list, an empty string is returned
     */
    public static String getRank(Integer id) {
        try {
            if (ranks == null) {
                ranks = new SQLRecordset();

                if (!Configuration.getBoolean("WaitingListRankBySpecies")) {
                    ranks.openRecordset("SELECT ID FROM animalwaitinglist " +
                        "WHERE DateRemovedFromList Is Null ORDER BY Urgency, DatePutOnList",
                        "animalwaitinglist");
                } else {
                    ranks.openRecordset(
                        "SELECT SpeciesID, ID FROM animalwaitinglist " +
                        "WHERE DateRemovedFromList Is Null ORDER BY SpeciesID, Urgency, DatePutOnList",
                        "animalwaitinglist");
                }
            } else {
                ranks.moveFirst();
            }

            int i = 1;
            Integer lastspecies = new Integer(0);

            while (!ranks.getEOF()) {
                // If we're ranking by species, reset when the species changes
                if (Configuration.getBoolean("WaitingListRankBySpecies")) {
                    if (!lastspecies.equals(ranks.getField("SpeciesID"))) {
                        lastspecies = (Integer) ranks.getField("SpeciesID");
                        i = 1;
                    }
                }

                if (ranks.getField("ID").equals(id)) {
                    return Integer.toString(i);
                }

                i++;
                ranks.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, WaitingListViewColumns.class);
        }

        return "";
    }
    
}
