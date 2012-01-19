/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;


public class AnimalFindColumns {
    public static final String DEFAULT_COLUMNS = "AnimalName, ShelterCode, ShelterLocation, SpeciesID,\n" +
        "BreedName, Sex, AnimalAge, Size, BaseColourID, Markings,\n" +
        "IdentichipNumber, DateBroughtIn";

    private static String i18n(String key) {
        return Global.i18n("uianimal", key);
    }

    /** Returns a count of the number of columns to display */
    public static int getColumnCount() {
        return getColumnNames().length;
    }

    /** Returns true if it's the default set of search result columns */
    public static boolean isDefaultColumns() {
        return Configuration.getString("SearchColumns", DEFAULT_COLUMNS)
                            .equalsIgnoreCase(DEFAULT_COLUMNS);
    }

    /** Returns an array of column names */
    public static String[] getColumnNames() {
        String[] names = Utils.split(Configuration.getString("SearchColumns",
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

        if (n.equalsIgnoreCase("AnimalTypeID")) {
            return i18n("Type");
        }

        if (n.equalsIgnoreCase("AnimalName")) {
            return i18n("Name");
        }

        if (n.equalsIgnoreCase("BaseColourID")) {
            return i18n("Colour");
        }

        if (n.equalsIgnoreCase("SpeciesID")) {
            return i18n("Species");
        }

        if (n.equalsIgnoreCase("BreedName")) {
            return i18n("Breed");
        }

        if (n.equalsIgnoreCase("CoatType")) {
            return i18n("Coat_Type");
        }

        if (n.equalsIgnoreCase("Markings")) {
            return i18n("Features");
        }

        if (n.equalsIgnoreCase("ShelterCode")) {
            return i18n("Code");
        }

        if (n.equalsIgnoreCase("AcceptanceNumber")) {
            return i18n("Acceptance");
        }

        if (n.equalsIgnoreCase("DateOfBirth")) {
            return i18n("Date_Of_Birth:");
        }

        if (n.equalsIgnoreCase("AgeGroup")) {
            return i18n("Age");
        }

        if (n.equalsIgnoreCase("DeceasedDate")) {
            return i18n("Dead");
        }

        if (n.equalsIgnoreCase("Sex")) {
            return i18n("Sex");
        }

        if (n.equalsIgnoreCase("IdentichipNumber")) {
            return i18n("Identichip_No");
        }

        if (n.equalsIgnoreCase("IdentichipDate")) {
            return i18n("Date:");
        }

        if (n.equalsIgnoreCase("TattooNumber")) {
            return i18n("Tattoo:");
        }

        if (n.equalsIgnoreCase("TattooDate")) {
            return i18n("Tattoo:");
        }

        if (n.equalsIgnoreCase("Neutered")) {
            return i18n("Neutered:");
        }

        if (n.equalsIgnoreCase("NeuteredDate")) {
            return i18n("Neutered:");
        }

        if (n.equalsIgnoreCase("CombiTested")) {
            return i18n("Combi-Tested:");
        }

        if (n.equalsIgnoreCase("CombiTestDate")) {
            return i18n("Combi-Tested:");
        }

        if (n.equalsIgnoreCase("CombiTestResult")) {
            return i18n("Combi-Tested:");
        }

        if (n.equalsIgnoreCase("HeartwormTested")) {
            return i18n("Heartworm_Tested:");
        }

        if (n.equalsIgnoreCase("HeartwormTestDate")) {
            return i18n("Heartworm_Tested:");
        }

        if (n.equalsIgnoreCase("HeartwormTestResult")) {
            return i18n("Heartworm_Tested:");
        }

        if (n.equalsIgnoreCase("FLVResult")) {
            return i18n("FLVResult");
        }

        if (n.equalsIgnoreCase("Declawed")) {
            return i18n("Declawed:");
        }

        if (n.equalsIgnoreCase("HiddenAnimalDetails")) {
            return i18n("Hidden_Comments:");
        }

        if (n.equalsIgnoreCase("AnimalComments")) {
            return i18n("Comments");
        }

        if (n.equalsIgnoreCase("ReasonForEntry")) {
            return i18n("Reason_for_Entry:");
        }

        if (n.equalsIgnoreCase("ReasonNO")) {
            return i18n("Reason_not_by_owner");
        }

        if (n.equalsIgnoreCase("DateBroughtIn")) {
            return i18n("Date_Brought_In");
        }

        if (n.equalsIgnoreCase("EntryReasonID")) {
            return i18n("Entry_Category");
        }

        if (n.equalsIgnoreCase("HealthProblems")) {
            return i18n("Health_Problems:");
        }

        if (n.equalsIgnoreCase("PTSReason")) {
            return i18n("PTS_Reason:");
        }

        if (n.equalsIgnoreCase("PTSReasonID")) {
            return i18n("PTS_Reason:");
        }

        if (n.equalsIgnoreCase("IsGoodWithCats")) {
            return i18n("good_with_cats");
        }

        if (n.equalsIgnoreCase("IsGoodWithDogs")) {
            return i18n("good_with_dogs");
        }

        if (n.equalsIgnoreCase("IsGoodWithChildren")) {
            return i18n("good_with_kids");
        }

        if (n.equalsIgnoreCase("IsHouseTrained")) {
            return i18n("housetrained");
        }

        if (n.equalsIgnoreCase("IsNotAvailableForAdoption")) {
            return i18n("Not_Available_For_Adoption");
        }

        if (n.equalsIgnoreCase("HasSpecialNeeds")) {
            return i18n("special_needs");
        }

        if (n.equalsIgnoreCase("ShelterLocation")) {
            return i18n("Internal_Loc");
        }

        if (n.equalsIgnoreCase("Size")) {
            return i18n("Size");
        }

        if (n.equalsIgnoreCase("RabiesTag")) {
            return i18n("Rabies_Tag");
        }

        if (n.equalsIgnoreCase("TimeOnShelter")) {
            return i18n("On_Shelter");
        }

        if (n.equalsIgnoreCase("DaysOnShelter")) {
            return i18n("On_Shelter");
        }

        if (n.equalsIgnoreCase("AnimalAge")) {
            return i18n("Age");
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
     * @param r The animal recordset, at the correct row
     * @param add The additional fields recordset containing LinkID and Value fields
     *
     */
    public static String format(String colname, SQLRecordset r, SQLRecordset add)
        throws CursorEngineException {
        String n = colname;

        if (n.equalsIgnoreCase("AnimalTypeID")) {
            return LookupCache.getAnimalTypeName(r.getInt("AnimalTypeID"));
        }

        if (n.equalsIgnoreCase("AnimalName")) {
            return r.getString("AnimalName");
        }

        if (n.equalsIgnoreCase("BaseColourID")) {
            return LookupCache.getBaseColourName(r.getInt("BaseColourID"));
        }

        if (n.equalsIgnoreCase("SpeciesID")) {
            return LookupCache.getSpeciesName(r.getInt("SpeciesID"));
        }

        if (n.equalsIgnoreCase("BreedName")) {
            return r.getString("BreedName");
        }

        if (n.equalsIgnoreCase("CoatType")) {
            return LookupCache.getCoatTypeForID(r.getInt("CoatType"));
        }

        if (n.equalsIgnoreCase("Markings")) {
            return r.getString("Markings");
        }

        if (n.equalsIgnoreCase("ShelterCode")) {
            return Animal.getAnimalCode(r.getString("ShelterCode"),
                r.getString("ShortCode"));
        }

        if (n.equalsIgnoreCase("AcceptanceNumber")) {
            return r.getString("AcceptanceNumber");
        }

        if (n.equalsIgnoreCase("DateOfBirth")) {
            return Utils.formatTableDate(r.getDate("DateOfBirth"));
        }

        if (n.equalsIgnoreCase("AgeGroup")) {
            return r.getString("AgeGroup");
        }

        if (n.equalsIgnoreCase("DeceasedDate")) {
            return Utils.formatTableDate(r.getDate("DeceasedDate"));
        }

        if (n.equalsIgnoreCase("Sex")) {
            return LookupCache.getSexName(r.getInt("Sex"));
        }

        if (n.equalsIgnoreCase("IdentichipNumber")) {
            return r.getString("IdentichipNumber");
        }

        if (n.equalsIgnoreCase("IdentichipDate")) {
            return Utils.formatTableDate(r.getDate("IdentichipDate"));
        }

        if (n.equalsIgnoreCase("TattooNumber")) {
            return r.getString("TattooNumber");
        }

        if (n.equalsIgnoreCase("TattooDate")) {
            return Utils.formatTableDate(r.getDate("TattooDate"));
        }

        if (n.equalsIgnoreCase("Neutered")) {
            return yesNo(r.getInt("Neutered"));
        }

        if (n.equalsIgnoreCase("NeuteredDate")) {
            return Utils.formatTableDate(r.getDate("NeuteredDate"));
        }

        if (n.equalsIgnoreCase("CombiTested")) {
            return yesNo(r.getInt("CombiTested"));
        }

        if (n.equalsIgnoreCase("CombiTestDate")) {
            return Utils.formatTableDate(r.getDate("CombiTestDate"));
        }

        if (n.equalsIgnoreCase("CombiTestResult")) {
            return posNegUnknown(r.getInt("CombiTestResult"));
        }

        if (n.equalsIgnoreCase("HeartwormTested")) {
            return yesNo(r.getInt("HeartwormTested"));
        }

        if (n.equalsIgnoreCase("HeartwormTestDate")) {
            return Utils.formatTableDate(r.getDate("HeartwormTestDate"));
        }

        if (n.equalsIgnoreCase("HeartwormTestResult")) {
            return posNegUnknown(r.getInt("HeartwormTestResult"));
        }

        if (n.equalsIgnoreCase("FLVResult")) {
            return posNegUnknown(r.getInt("FLVResult"));
        }

        if (n.equalsIgnoreCase("Declawed")) {
            return yesNo(r.getInt("Declawed"));
        }

        if (n.equalsIgnoreCase("HiddenAnimalDetails")) {
            return r.getString("HiddenAnimalDetails");
        }

        if (n.equalsIgnoreCase("AnimalComments")) {
            return r.getString("AnimalComments");
        }

        if (n.equalsIgnoreCase("ReasonForEntry")) {
            return r.getString("ReasonForEntry");
        }

        if (n.equalsIgnoreCase("ReasonNO")) {
            return r.getString("ReasonNO");
        }

        if (n.equalsIgnoreCase("DateBroughtIn")) {
            return Utils.formatTableDate(r.getDate("DateBroughtIn"));
        }

        if (n.equalsIgnoreCase("EntryReasonID")) {
            return LookupCache.getEntryReasonNameForID(r.getInt("EntryReasonID"));
        }

        if (n.equalsIgnoreCase("HealthProblems")) {
            return r.getString("HealthProblems");
        }

        if (n.equalsIgnoreCase("PTSReason")) {
            return r.getString("PTSReason");
        }

        if (n.equalsIgnoreCase("PTSReasonID")) {
            return LookupCache.getDeathReasonNameForID(r.getInt("PTSReasonID"));
        }

        if (n.equalsIgnoreCase("IsGoodWithCats")) {
            return yesNoUnknown(r.getInt("IsGoodWithCats"));
        }

        if (n.equalsIgnoreCase("IsGoodWithDogs")) {
            return yesNoUnknown(r.getInt("IsGoodWithDogs"));
        }

        if (n.equalsIgnoreCase("IsGoodWithChildren")) {
            return yesNoUnknown(r.getInt("IsGoodWithChildren"));
        }

        if (n.equalsIgnoreCase("IsHouseTrained")) {
            return yesNoUnknown(r.getInt("IsHouseTrained"));
        }

        if (n.equalsIgnoreCase("IsNotAvailableForAdoption")) {
            return yesNo(r.getInt("IsNotAvailableForAdoption"));
        }

        if (n.equalsIgnoreCase("HasSpecialNeeds")) {
            return yesNo(r.getInt("HasSpecialNeeds"));
        }

        if (n.equalsIgnoreCase("ShelterLocation")) {
            return Animal.getDisplayLocation(r.getInt("ShelterLocation"),
                r.getInt("NonShelterAnimal"), r.getInt("ActiveMovementID"),
                r.getInt("ActiveMovementType"), r.getDate("DeceasedDate"));
        }

        if (n.equalsIgnoreCase("Size")) {
            return LookupCache.getSizeNameForID(r.getInt("Size"));
        }

        if (n.equalsIgnoreCase("RabiesTag")) {
            return r.getString("RabiesTag");
        }

        if (n.equalsIgnoreCase("TimeOnShelter")) {
            return r.getString("TimeOnShelter");
        }

        if (n.equalsIgnoreCase("DaysOnShelter")) {
            return r.getString("DaysOnShelter");
        }

        if (n.equalsIgnoreCase("AnimalAge")) {
            return Animal.getAge(r.getDate("DateOfBirth"),
                r.getDate("DeceasedDate"));
        }

        // Must be an additional field - find it and return the value
        for (SQLRecordset a : add) {
            if ((a.getInt("LinkID") == r.getInt("ID")) &&
                    a.getString("FieldName").equalsIgnoreCase(colname)) {
                if (a.getInt("FieldType") == AdditionalField.FIELDTYPE_YESNO)
                    return yesNo(Integer.parseInt(a.getString("Value")));
                else
                    return a.getString("Value");
            }
        }

        // Give up
        return "";
    }
}
