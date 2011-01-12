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
package net.sourceforge.sheltermanager.asm.ui.owner;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;


public class OwnerFindColumns {
    public static final String DEFAULT_COLUMNS = "OwnerName," +
    	"OwnerSurname,MembershipNumber,IsBanned,IDCheck,OwnerAddress,\n" +
    	"OwnerTown,OwnerCounty,OwnerPostcode,HomeTelephone,WorkTelephone,\n" +
    	"MobileTelephone,EmailAddress";

    private static String i18n(String key) {
        return Global.i18n("uiowner", key);
    }

    /** Returns a count of the number of columns to display */
    public static int getColumnCount() {
        return getColumnNames().length;
    }

    /** Returns true if it's the default set of search result columns */
    public static boolean isDefaultColumns() {
        return Configuration.getString("OwnerSearchColumns", DEFAULT_COLUMNS)
                            .equalsIgnoreCase(DEFAULT_COLUMNS);
    }

    /** Returns an array of column names */
    public static String[] getColumnNames() {
        String[] names = Utils.split(Configuration.getString("OwnerSearchColumns",
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

        if (n.equalsIgnoreCase("OwnerTitle")) {
            return i18n("NameTitle:");
        }

        if (n.equalsIgnoreCase("OwnerInitials")) {
            return i18n("NameInitials:");
        }

        if (n.equalsIgnoreCase("OwnerForenames")) {
            return i18n("NameForenames:");
        }

        if (n.equalsIgnoreCase("OwnerSurname")) {
            return i18n("NameSurname:");
        }

        if (n.equalsIgnoreCase("OwnerName")) {
            return i18n("Name:");
        }

        if (n.equalsIgnoreCase("OwnerAddress")) {
            return i18n("Address:");
        }

        if (n.equalsIgnoreCase("OwnerTown")) {
            return i18n("town");
        }

        if (n.equalsIgnoreCase("OwnerCounty")) {
            return i18n("county");
        }

        if (n.equalsIgnoreCase("OwnerPostcode")) {
            return i18n("Postcode");
        }

        if (n.equalsIgnoreCase("HomeTelephone")) {
            return i18n("Telephone:");
        }

        if (n.equalsIgnoreCase("WorkTelephone")) {
            return i18n("Work_Tel");
        }

        if (n.equalsIgnoreCase("MobileTelephone")) {
            return i18n("Mobile_Tel");
        }

        if (n.equalsIgnoreCase("EmailAddress")) {
            return i18n("Email:");
        }

        if (n.equalsIgnoreCase("IDCheck")) {
            return i18n("ID_Check");
        }

        if (n.equalsIgnoreCase("Comments")) {
            return i18n("Comments:");
        }

        if (n.equalsIgnoreCase("IsBanned")) {
            return i18n("Banned");
        }

        if (n.equalsIgnoreCase("IsVolunteer")) {
            return i18n("Volunteer");
        }

        if (n.equalsIgnoreCase("IsHomeChecker")) {
            return i18n("Homechecker");
        }

        if (n.equalsIgnoreCase("IsMember")) {
            return i18n("Member");
        }

        if (n.equalsIgnoreCase("MembershipExpiryDate")) {
            return i18n("Expiry_Date");
        }

        if (n.equalsIgnoreCase("MembershipNumber")) {
            return i18n("Number");
        }

        if (n.equalsIgnoreCase("IsDonor")) {
            return i18n("Donor");
        }

        if (n.equalsIgnoreCase("IsShelter")) {
            return i18n("other_animal_shelter");
        }

        if (n.equalsIgnoreCase("IsACO")) {
            return i18n("is_animal_care_officer");
        }

        if (n.equalsIgnoreCase("IsStaff")) {
            return i18n("is_staff");
        }

        if (n.equalsIgnoreCase("IsFosterer")) {
            return i18n("Fosters_Animals");
        }

        if (n.equalsIgnoreCase("IsRetailer")) {
            return i18n("retailer");
        }

        if (n.equalsIgnoreCase("IsVet")) {
            return i18n("Vet");
        }

        if (n.equalsIgnoreCase("IsGiftAid")) {
            return i18n("Gift_Aid");
        }

        if (n.equalsIgnoreCase("HomeCheckAreas")) {
            return i18n("Homecheck_Areas:");
        }

        if (n.equalsIgnoreCase("DateLastHomeChecked")) {
            return i18n("Homechecked");
        }

        if (n.equalsIgnoreCase("HomeCheckedBy")) {
            return i18n("Checked_By");
        }

        if (n.equalsIgnoreCase("MatchAdded")) {
            return i18n("Match_added");
        }

        if (n.equalsIgnoreCase("MatchExpires")) {
            return i18n("Match_expires");
        }

        if (n.equalsIgnoreCase("MatchActive")) {
            return i18n("Active");
        }

        if (n.equalsIgnoreCase("MatchSex")) {
            return i18n("Sex");
        }

        if (n.equalsIgnoreCase("MatchSize")) {
            return i18n("Size");
        }

        if (n.equalsIgnoreCase("MatchAgeFrom")) {
            return i18n("Age_From");
        }

        if (n.equalsIgnoreCase("MatchAgeTo")) {
            return i18n("Age_To");
        }

        if (n.equalsIgnoreCase("MatchAnimalType")) {
            return i18n("type");
        }

        if (n.equalsIgnoreCase("MatchSpecies")) {
            return i18n("Species");
        }

        if (n.equalsIgnoreCase("MatchBreed")) {
            return i18n("Breed");
        }

        if (n.equalsIgnoreCase("MatchBreed2")) {
            return i18n("Breed");
        }

        if (n.equalsIgnoreCase("MatchGoodWithCats")) {
            return i18n("Good_with_cats");
        }

        if (n.equalsIgnoreCase("MatchGoodWithDogs")) {
            return i18n("Good_with_dogs");
        }

        if (n.equalsIgnoreCase("MatchGoodWithChildren")) {
            return i18n("Good_with_children");
        }

        if (n.equalsIgnoreCase("MatchHouseTrained")) {
            return i18n("Housetrained");
        }

        if (n.equalsIgnoreCase("MatchCommentsContain")) {
            return i18n("Comments_contain");
        }

        // Ok, it must be an additional field
        return n;
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
     * @param r The owner recordset, at the correct row
     * @param add The additional fields recordset containing LinkID and Value fields
     *
     */
    public static String format(String colname, SQLRecordset r, SQLRecordset add)
        throws CursorEngineException {
        String n = colname;

        if (n.equalsIgnoreCase("OwnerTitle")) {
            return r.getString("OwnerTitle");
        }

        if (n.equalsIgnoreCase("OwnerInitials")) {
            return r.getString("OwnerInitials");
        }

        if (n.equalsIgnoreCase("OwnerForenames")) {
            return r.getString("OwnerForenames");
        }

        if (n.equalsIgnoreCase("OwnerSurname")) {
            return r.getString("OwnerSurname");
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

        if (n.equalsIgnoreCase("EmailAddress")) {
            return r.getString("EmailAddress");
        }

        if (n.equalsIgnoreCase("IDCheck")) {
            return yesNo(r.getInt("IDCheck"));
        }

        if (n.equalsIgnoreCase("Comments")) {
            return r.getString("Comments");
        }

        if (n.equalsIgnoreCase("IsBanned")) {
            return yesNo(r.getInt("IsBanned"));
        }

        if (n.equalsIgnoreCase("IsVolunteer")) {
            return yesNo(r.getInt("IsVolunteer"));
        }

        if (n.equalsIgnoreCase("IsHomeChecker")) {
            return yesNo(r.getInt("IsHomeChecker"));
        }

        if (n.equalsIgnoreCase("IsMember")) {
            return yesNo(r.getInt("IsMember"));
        }

        if (n.equalsIgnoreCase("MembershipExpiryDate")) {
            return Utils.formatTableDate(r.getDate("MembershipExpiryDate"));
        }

        if (n.equalsIgnoreCase("MembershipNumber")) {
            return r.getString("MembershipNumber");
        }

        if (n.equalsIgnoreCase("IsDonor")) {
            return yesNo(r.getInt("IsDonor"));
        }

        if (n.equalsIgnoreCase("IsShelter")) {
            return yesNo(r.getInt("IsShelter"));
        }

        if (n.equalsIgnoreCase("IsACO")) {
            return yesNo(r.getInt("IsACO"));
        }

        if (n.equalsIgnoreCase("IsStaff")) {
            return yesNo(r.getInt("IsStaff"));
        }

        if (n.equalsIgnoreCase("IsFosterer")) {
            return yesNo(r.getInt("IsFosterer"));
        }

        if (n.equalsIgnoreCase("IsRetailer")) {
            return yesNo(r.getInt("IsRetailer"));
        }

        if (n.equalsIgnoreCase("IsVet")) {
            return yesNo(r.getInt("IsVet"));
        }

        if (n.equalsIgnoreCase("IsGiftAid")) {
            return yesNo(r.getInt("IsGiftAid"));
        }

        if (n.equalsIgnoreCase("HomeCheckAreas")) {
            return r.getString("HomeCheckAreas");
        }

        if (n.equalsIgnoreCase("DateLastHomeChecked")) {
            return Utils.formatTableDate(r.getDate("DateLastHomeChecked"));
        }

        if (n.equalsIgnoreCase("HomeCheckedBy")) {
            return r.getString("HomeCheckedBy");
        }

        if (n.equalsIgnoreCase("MatchAdded")) {
            return Utils.formatTableDate(r.getDate("MatchAdded"));
        }

        if (n.equalsIgnoreCase("MatchExpires")) {
            return Utils.formatTableDate(r.getDate("MatchExpires"));
        }

        if (n.equalsIgnoreCase("MatchActive")) {
            return yesNo(r.getInt("MatchActive"));
        }

        if (n.equalsIgnoreCase("MatchSex")) {
            return LookupCache.getSexName(r.getInt("MatchSex"));
        }

        if (n.equalsIgnoreCase("MatchSize")) {
        	return LookupCache.getSizeNameForID(r.getInt("MatchSize"));
        }

        if (n.equalsIgnoreCase("MatchAgeFrom")) {
            return r.getString("MatchAgeFrom");
        }

        if (n.equalsIgnoreCase("MatchAgeTo")) {
            return r.getString("MatchAgeTo");
        }

        if (n.equalsIgnoreCase("MatchAnimalType")) {
            return LookupCache.getAnimalTypeName(r.getInt("MatchAnimalType"));
        }

        if (n.equalsIgnoreCase("MatchSpecies")) {
            return LookupCache.getSpeciesName(r.getInt("MatchSpecies"));
        }

        if (n.equalsIgnoreCase("MatchBreed")) {
            return LookupCache.getBreedName(r.getInt("MatchBreed"));
        }

        if (n.equalsIgnoreCase("MatchBreed2")) {
        	return LookupCache.getBreedName(r.getInt("MatchBreed2"));
        }

        if (n.equalsIgnoreCase("MatchGoodWithCats")) {
            return yesNoUnknown(r.getInt("MatchGoodWithCats"));
        }

        if (n.equalsIgnoreCase("MatchGoodWithDogs")) {
        	return yesNoUnknown(r.getInt("MatchGoodWithDogs"));
        }

        if (n.equalsIgnoreCase("MatchGoodWithChildren")) {
        	return yesNoUnknown(r.getInt("MatchGoodWithChildren"));
        }

        if (n.equalsIgnoreCase("MatchHouseTrained")) {
        	return yesNoUnknown(r.getInt("MatchHouseTrained"));
        }

        if (n.equalsIgnoreCase("MatchCommentsContain")) {
            return r.getString("MatchCommentsContain");
        }

        // Must be an additional field - find it and return the value
        for (SQLRecordset a : add) {
            if ((a.getInt("LinkID") == r.getInt("ID")) &&
                    a.getString("FieldName").equalsIgnoreCase(colname)) {
                return a.getString("Value");
            }
        }

        // Give up
        return "";
    }
}
