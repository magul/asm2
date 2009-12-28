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

import java.util.HashMap;
import java.util.Vector;


/**
 *
 * Special abstract class that handles caching of lookups for speedier access.
 *
 */
public abstract class LookupCache {
    private static SQLRecordset diet = null;
    private static SQLRecordset voucher = null;
    private static SQLRecordset donationtype = null;
    private static SQLRecordset vacctype = null;
    private static SQLRecordset sex = null;
    private static SQLRecordset size = null;
    private static SQLRecordset movementtype = null;
    private static SQLRecordset urgency = null;
    private static SQLRecordset breed = null;
    private static SQLRecordset species = null;
    private static SQLRecordset animaltype = null;
    private static SQLRecordset internallocation = null;
    private static SQLRecordset basecolour = null;
    private static SQLRecordset entryreason = null;
    private static SQLRecordset deathreason = null;
    private static SQLRecordset logtype = null;
    private static SQLRecordset additionalfield = null;
    private static SQLRecordset fieldtype = null;
    private static SQLRecordset fieldlink = null;
    private static SQLRecordset coattype = null;
    private static Animal activeanimals = null;
    private static HashMap animalextdata = null;
    private static HashMap breedspecies = null;
    private static HashMap donationspecies = null;
    private static Vector ownercounties = null;
    private static Vector ownertowns = null;
    private static String commoncounty = "";
    private static String commontown = "";
    private static final int LOOKUP_SEX = 0;
    private static final int LOOKUP_SIZE = 1;
    private static final int LOOKUP_MOVEMENTTYPE = 2;
    private static final int LOOKUP_URGENCY = 3;
    private static final int LOOKUP_ANIMALTYPE = 4;
    private static final int LOOKUP_SPECIES = 5;
    private static final int LOOKUP_BREED = 6;
    private static final int LOOKUP_INTERNALLOCATION = 7;
    private static final int LOOKUP_BASECOLOUR = 8;
    private static final int LOOKUP_DIET = 9;
    private static final int LOOKUP_VACCINATIONTYPE = 10;
    private static final int LOOKUP_VOUCHER = 11;
    private static final int LOOKUP_DONATIONTYPE = 12;
    private static final int LOOKUP_ENTRYREASON = 13;
    private static final int LOOKUP_DEATHREASON = 14;
    private static final int LOOKUP_LOGTYPES = 15;
    private static final int LOOKUP_ADDITIONALFIELD = 16;
    private static final int LOOKUP_FIELDTYPE = 17;
    private static final int LOOKUP_FIELDLINK = 18;
    private static final int LOOKUP_COATTYPE = 19;
    private static final int MAX_LOOKUPS = 19;

    private static SQLRecordset getLookup(int lookuptype) {
        try {
            switch (lookuptype) {
            case LOOKUP_SEX:

                if (sex == null) {
                    sex = new SQLRecordset();
                    sex.openRecordset("SELECT * FROM lksex ORDER BY ID", "lksex");
                }

                return sex;

            case LOOKUP_SIZE:

                if (size == null) {
                    size = new SQLRecordset();
                    size.openRecordset("SELECT * FROM lksize ORDER BY ID",
                        "lksize");
                }

                return size;

            case LOOKUP_MOVEMENTTYPE:

                if (movementtype == null) {
                    movementtype = new SQLRecordset();
                    movementtype.openRecordset("SELECT * FROM lksmovementtype ORDER BY ID",
                        "lksmovementtype");
                }

                return movementtype;

            case LOOKUP_URGENCY:

                if (urgency == null) {
                    urgency = new SQLRecordset();
                    urgency.openRecordset("SELECT * FROM lkurgency ORDER BY ID",
                        "lkurgency");
                }

                return urgency;

            case LOOKUP_ANIMALTYPE:

                if (animaltype == null) {
                    animaltype = new SQLRecordset();
                    animaltype.openRecordset("SELECT * FROM animaltype ORDER BY AnimalType",
                        "animaltype");
                }

                return animaltype;

            case LOOKUP_SPECIES:

                if (species == null) {
                    species = new SQLRecordset();
                    species.openRecordset("SELECT * FROM species ORDER BY SpeciesName",
                        "species");
                }

                return species;

            case LOOKUP_BREED:

                if (breed == null) {
                    breed = new SQLRecordset();
                    breed.openRecordset("SELECT * FROM breed ORDER BY BreedName",
                        "breed");
                }

                return breed;

            case LOOKUP_BASECOLOUR:

                if (basecolour == null) {
                    basecolour = new SQLRecordset();
                    basecolour.openRecordset("SELECT * FROM basecolour ORDER BY BaseColour",
                        "basecolour");
                }

                return basecolour;

            case LOOKUP_INTERNALLOCATION:

                if (internallocation == null) {
                    internallocation = new SQLRecordset();
                    internallocation.openRecordset("SELECT * FROM internallocation ORDER BY LocationName",
                        "internalllocation");
                }

                return internallocation;

            case LOOKUP_DIET:

                if (diet == null) {
                    diet = new SQLRecordset();
                    diet.openRecordset("SELECT * FROM diet ORDER BY DietName",
                        "diet");
                }

                return diet;

            case LOOKUP_VOUCHER:

                if (voucher == null) {
                    voucher = new SQLRecordset();
                    voucher.openRecordset("SELECT * FROM voucher ORDER BY VoucherName",
                        "voucher");
                }

                return voucher;

            case LOOKUP_DONATIONTYPE:

                if (donationtype == null) {
                    donationtype = new SQLRecordset();
                    donationtype.openRecordset("SELECT * FROM donationtype ORDER BY DonationName",
                        "donationtype");
                }

                return donationtype;

            case LOOKUP_VACCINATIONTYPE:

                if (vacctype == null) {
                    vacctype = new SQLRecordset();
                    vacctype.openRecordset("SELECT * FROM vaccinationtype ORDER BY VaccinationType",
                        "vacctype");
                }

                return vacctype;

            case LOOKUP_ENTRYREASON:

                if (entryreason == null) {
                    entryreason = new SQLRecordset();
                    entryreason.openRecordset("SELECT * FROM entryreason ORDER BY ReasonName",
                        "entryreason");
                }

                return entryreason;

            case LOOKUP_DEATHREASON:

                if (deathreason == null) {
                    deathreason = new SQLRecordset();
                    deathreason.openRecordset("SELECT * FROM deathreason ORDER BY ReasonName",
                        "deathreason");
                }

                return deathreason;

            case LOOKUP_LOGTYPES:

                if (logtype == null) {
                    logtype = new SQLRecordset();
                    logtype.openRecordset("SELECT * FROM logtype ORDER BY LogTypeName",
                        "logtype");
                }

                return logtype;

            case LOOKUP_ADDITIONALFIELD:

                if (additionalfield == null) {
                    additionalfield = new SQLRecordset();
                    additionalfield.openRecordset("SELECT * FROM additionalfield ORDER BY DisplayIndex",
                        "additionalfield");
                }

                return additionalfield;

            case LOOKUP_FIELDLINK:

                if (fieldlink == null) {
                    fieldlink = new SQLRecordset();
                    fieldlink.openRecordset("SELECT * FROM lksfieldlink ORDER BY ID",
                        "lksfieldlink");
                }

                return fieldlink;

            case LOOKUP_FIELDTYPE:

                if (fieldtype == null) {
                    fieldtype = new SQLRecordset();
                    fieldtype.openRecordset("SELECT * FROM lksfieldtype ORDER BY ID",
                        "lksfieldlink");
                }

                return fieldtype;

            case LOOKUP_COATTYPE:

                if (coattype == null) {
                    coattype = new SQLRecordset();
                    coattype.openRecordset("SELECT * FROM lkcoattype ORDER BY CoatType",
                        "lkcoattype");
                }

                return coattype;
            }
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }

        return null;
    }

    /** Empties the cache */
    public static void invalidate() {
        Global.logInfo("Clearing lookup cache", "LookupCache.invalidate");
        species = null;
        basecolour = null;
        internallocation = null;
        breed = null;
        diet = null;
        voucher = null;
        animaltype = null;
        donationtype = null;
        vacctype = null;
        sex = null;
        size = null;
        movementtype = null;
        urgency = null;
        breed = null;
        species = null;
        animaltype = null;
        internallocation = null;
        basecolour = null;
        entryreason = null;
        deathreason = null;
        logtype = null;
        additionalfield = null;
        fieldlink = null;
        fieldtype = null;
        coattype = null;
        animalextdata = null;

        if (activeanimals != null) {
            activeanimals.free();
            activeanimals = null;
        }

        if (ownercounties != null) {
            ownercounties.removeAllElements();
            ownercounties = null;
        }

        if (ownertowns != null) {
            ownertowns.removeAllElements();
            ownertowns = null;
        }

        if (breedspecies != null) {
            breedspecies.clear();
            breedspecies = null;
        }

        if (donationspecies != null) {
            donationspecies.clear();
            donationspecies = null;
        }
    }

    /** Fills the cache */
    public static void fill() {
        Global.logInfo("Filling lookup cache", "LookupCache.fill");

        for (int i = 0; i <= MAX_LOOKUPS; i++) {
            getLookup(i);
        }

        // List of active animals
        updateAnimalCache();

        // Fill owner counties/states list
        getOwnerCounties();
        getOwnerTowns();

        // Load the species/breed mapping for the default species
        // to save time opening the edit animal screen
        try {
            Integer defaultSpecies = new Integer(Configuration.getInteger(
                        "AFDefaultSpecies"));
            getBreedsForSpecies(defaultSpecies);
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }
    }

    /** Returns the ID of the first record in the lookup */
    public static Integer getFirstID(SQLRecordset lookup) {
        try {
            lookup.moveFirst();

            return (Integer) lookup.getField("ID");
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);

            return new Integer(0);
        }
    }

    public static String getRealName(String user) {
        try {
            Users u = new Users();
	    u.openRecordset("");
	    while (!u.getEOF()) {
	        if (u.getUserName().equals(user))
		    return u.getRealName();
                u.moveNext();
	    }
	}
	catch (Exception e) {
           Global.logException(e, LookupCache.class);
	}
	return "";
    }

    public static void updateAnimalCache() {
        if (Global.isCacheActiveAnimals()) {
            if (activeanimals != null) {
                activeanimals.free();
            }

            animalextdata = new HashMap();
            activeanimals = new Animal();
            activeanimals.openRecordset("Archived = 0");

            try {
                // Check all animal satellite data in one query and build
                // a map from it - much faster than testing each individual
                // active animal
                SQLRecordset r = new SQLRecordset();
                r.openRecordset(
                    "SELECT animal.ID, (SELECT COUNT(*) FROM animalvaccination WHERE AnimalID = animal.ID) AS vacc, (SELECT COUNT(*) FROM animalmedical WHERE AnimalID = animal.ID) AS medi, (SELECT COUNT(*) FROM animaldiet WHERE AnimalID = animal.ID) AS diet, (SELECT COUNT(*) FROM media WHERE LinkID = animal.ID AND LinkTypeID = " +
                    Media.LINKTYPE_ANIMAL +
                    ") AS pics, (SELECT COUNT(*) FROM diary WHERE LinkID = animal.ID AND LinkType = " +
                    Diary.LINKTYPE_ANIMAL +
                    ") AS diar, (SELECT COUNT(*) FROM adoption WHERE AnimalID = animal.ID) AS move, (SELECT COUNT(*) FROM log WHERE LinkID = animal.ID AND LinkType = " +
                    Log.LINKTYPE_ANIMAL +
                    ") AS logs FROM animal WHERE animal.Archived = 0", "animal");

                while (!r.getEOF()) {
                    animalextdata.put((Integer) r.getField("ID"),
                        new Animal.AnimalMarkers((Integer) r.getField("vacc"),
                            (Integer) r.getField("medi"),
                            (Integer) r.getField("diet"),
                            (Integer) r.getField("pics"),
                            (Integer) r.getField("diar"),
                            (Integer) r.getField("move"),
                            (Integer) r.getField("logs")));
                    r.moveNext();
                }
            } catch (Exception e) {
                Global.logException(e, LookupCache.class);
            }
        }
    }

    /** Looks up an animal cached in the active list - if the
      * animal wasn't found in the cache, it's looked up
      * from the database instead.
      */
    public static Animal getAnimalByID(Integer id) {
        if (!Global.isCacheActiveAnimals()) {
            return loadAnimal(id);
        }

        if (activeanimals == null) {
            return loadAnimal(id);
        }

        try {
            activeanimals.moveFirst();

            while (!activeanimals.getEOF()) {
                if (activeanimals.getID().equals(id)) {
                    Global.logDebug("CACHE: HIT for animal id " + id,
                        "LookupCache.getAnimalByID");

                    return activeanimals;
                }

                activeanimals.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }

        Global.logDebug("CACHE: MISS for animal id " + id,
            "LookupCache.getAnimalByID");

        return loadAnimal(id);
    }

    /** Returns the number of each type of satellite record an
      * animal has.
      */
    public static Animal.AnimalMarkers getAnimalNumExtRecs(Integer id) {
        if ((animalextdata == null) || !Global.isCacheActiveAnimals()) {
            return Animal.getNumExternalRecords(id);
        }

        Animal.AnimalMarkers a = (Animal.AnimalMarkers) animalextdata.get(id);

        if (a == null) {
            Global.logDebug("EXTCACHE: MISS for animal id " + id,
                "LookupCache.getAnimalNumExtRecs");
            a = Animal.getNumExternalRecords(id);
            animalextdata.put(id, a);
        } else {
            Global.logDebug("EXTCACHE: HIT for animal id " + id,
                "LookupCache.getAnimalNumExtRecs");
        }

        return a;
    }

    private static Animal loadAnimal(Integer id) {
        try {
            Animal a = new Animal();
            a.openRecordset("ID = " + id.intValue());

            return a;
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }

        return null;
    }

    public static SQLRecordset getBreedLookup() {
        return getLookup(LOOKUP_BREED);
    }

    public static SQLRecordset getSpeciesLookup() {
        return getLookup(LOOKUP_SPECIES);
    }

    public static SQLRecordset getInternalLocationLookup() {
        return getLookup(LOOKUP_INTERNALLOCATION);
    }

    public static SQLRecordset getBaseColourLookup() {
        return getLookup(LOOKUP_BASECOLOUR);
    }

    public static SQLRecordset getAnimalTypeLookup() {
        return getLookup(LOOKUP_ANIMALTYPE);
    }

    public static SQLRecordset getCoatTypeLookup() {
        return getLookup(LOOKUP_COATTYPE);
    }

    public static SQLRecordset getSexLookup() {
        return getLookup(LOOKUP_SEX);
    }

    public static SQLRecordset getSizeLookup() {
        return getLookup(LOOKUP_SIZE);
    }

    public static SQLRecordset getMovementTypeLookup() {
        return getLookup(LOOKUP_MOVEMENTTYPE);
    }

    public static SQLRecordset getUrgencyLookup() {
        return getLookup(LOOKUP_URGENCY);
    }

    public static SQLRecordset getDietLookup() {
        return getLookup(LOOKUP_DIET);
    }

    public static SQLRecordset getVaccinationTypeLookup() {
        return getLookup(LOOKUP_VACCINATIONTYPE);
    }

    public static SQLRecordset getVoucherLookup() {
        return getLookup(LOOKUP_VOUCHER);
    }

    public static SQLRecordset getDonationTypeLookup() {
        return getLookup(LOOKUP_DONATIONTYPE);
    }

    public static SQLRecordset getEntryReasonLookup() {
        return getLookup(LOOKUP_ENTRYREASON);
    }

    public static SQLRecordset getDeathReasonLookup() {
        return getLookup(LOOKUP_DEATHREASON);
    }

    public static SQLRecordset getLogTypeLookup() {
        return getLookup(LOOKUP_LOGTYPES);
    }

    public static SQLRecordset getAdditionalFieldLookup() {
        return getLookup(LOOKUP_ADDITIONALFIELD);
    }

    public static SQLRecordset getFieldLinkLookup() {
        return getLookup(LOOKUP_FIELDLINK);
    }

    public static SQLRecordset getFieldTypeLookup() {
        return getLookup(LOOKUP_FIELDTYPE);
    }

    public static String getCoatTypeForID(Integer ID) {
        getLookup(LOOKUP_COATTYPE);

        return getNameForID(coattype, "CoatType", ID);
    }

    public static String getFieldTypeForID(Integer ID) {
        getLookup(LOOKUP_FIELDTYPE);

        return getNameForID(fieldtype, "FieldType", ID);
    }

    public static Integer getFieldTypeIDForName(String name) {
        getLookup(LOOKUP_FIELDTYPE);

        return getIDForName(fieldtype, "FieldType", name);
    }

    public static String getFieldLinkForID(Integer ID) {
        getLookup(LOOKUP_FIELDLINK);

        return getNameForID(fieldlink, "LinkType", ID);
    }

    public static Integer getFieldLinkIDForName(String name) {
        getLookup(LOOKUP_FIELDLINK);

        return getIDForName(fieldlink, "LinkType", name);
    }

    public static String getSexNameForID(Integer ID) {
        getLookup(LOOKUP_SEX);

        return getNameForID(sex, "Sex", ID);
    }

    public static Integer getSexIDForName(String Name) {
        getLookup(LOOKUP_SEX);

        return getIDForName(sex, "Sex", Name);
    }

    public static String getSizeNameForID(Integer ID) {
        getLookup(LOOKUP_SIZE);

        return getNameForID(size, "Size", ID);
    }

    public static Integer getSizeIDForName(String Name) {
        getLookup(LOOKUP_SIZE);

        return getIDForName(size, "Size", Name);
    }

    public static String getUrgencyNameForID(Integer ID) {
        getLookup(LOOKUP_URGENCY);

        return getNameForID(urgency, "Urgency", ID);
    }

    public static String getEntryReasonNameForID(Integer ID) {
        getLookup(LOOKUP_ENTRYREASON);

        return getNameForID(entryreason, "ReasonName", ID);
    }

    public static String getDeathReasonNameForID(Integer ID) {
        getLookup(LOOKUP_DEATHREASON);

        return getNameForID(deathreason, "ReasonName", ID);
    }

    public static Integer getUrgencyIDForName(String Name) {
        getLookup(LOOKUP_URGENCY);

        return getIDForName(urgency, "Urgency", Name);
    }

    public static String getMoveTypeNameForID(Integer ID) {
        getLookup(LOOKUP_MOVEMENTTYPE);

        return getNameForID(movementtype, "MovementType", ID);
    }

    public static Integer getMoveTypeIDForName(String Name) {
        getLookup(LOOKUP_MOVEMENTTYPE);

        return getIDForName(movementtype, "MovementType", Name);
    }

    public static Integer getAnimalTypeIDForName(String Name) {
        getLookup(LOOKUP_ANIMALTYPE);

        return getIDForName(animaltype, "AnimalType", Name);
    }

    private static String getNameForID(SQLRecordset rs, String nameFieldName,
        Integer ID) {
        try {
            rs.moveFirst();

            while (!rs.getEOF()) {
                if (rs.getField("ID").equals(ID)) {
                    return (String) rs.getField(nameFieldName);
                }

                rs.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }

        return "";
    }

    private static Integer getIDForName(SQLRecordset rs, String nameFieldName,
        String name) {
        try {
            rs.moveFirst();

            while (!rs.getEOF()) {
                if (rs.getField(nameFieldName).toString().equalsIgnoreCase(name)) {
                    return (Integer) rs.getField("ID");
                }

                rs.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }

        return null;
    }

    public static String getSpeciesPetFinderMapping(Integer ID)
        throws CursorEngineException {
        return getNameForID(getSpeciesLookup(), "PetFinderSpecies", ID);
    }

    public static String getBreedPetFinderMapping(Integer ID)
        throws CursorEngineException {
        return getNameForID(getBreedLookup(), "PetFinderBreed", ID);
    }

    public static String getSpeciesName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getSpeciesLookup(), "SpeciesName", ID);
    }

    public static Integer getSpeciesID(String name)
        throws CursorEngineException {
        return getIDForName(getSpeciesLookup(), "SpeciesName", name);
    }

    /**
     * Looks up the species and determines the baby name from the species name.
     * If it can't find one, it returns "baby" in the appropriate language
     */
    public static String getSpeciesNameBaby(Integer ID)
        throws CursorEngineException {
        String speciesName = getSpeciesName(ID);

        if (speciesName.startsWith(Global.i18n("bo", "Cat"))) {
            return Global.i18n("bo", "Kittens");
        } else if (speciesName.startsWith(Global.i18n("bo", "Dog"))) {
            return Global.i18n("bo", "Puppies");
        } else {
            return Global.i18n("bo", "Babies");
        }
    }

    public static String getBaseColourName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getBaseColourLookup(), "BaseColour", ID);
    }

    public static String getAnimalTypeName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getAnimalTypeLookup(), "AnimalType", ID);
    }

    public static String getDietName(Integer ID) throws CursorEngineException {
        return getNameForID(getDietLookup(), "DietName", ID);
    }

    public static String getDonationTypeName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getDonationTypeLookup(), "DonationName", ID);
    }

    public static String getDietDescription(Integer ID)
        throws CursorEngineException {
        return getNameForID(getDietLookup(), "DietDescription", ID);
    }

    public static String getVoucherName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getVoucherLookup(), "VoucherName", ID);
    }

    public static String getInternalLocationName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getInternalLocationLookup(), "LocationName", ID);
    }

    public static String getBreedName(Integer ID) throws CursorEngineException {
        return getNameForID(getBreedLookup(), "BreedName", ID);
    }

    public static Integer getBreedID(String name) throws CursorEngineException {
        return getIDForName(getBreedLookup(), "BreedName", name);
    }

    public static String getVaccinationTypeName(Integer ID)
        throws CursorEngineException {
        return getNameForID(getVaccinationTypeLookup(), "VaccinationType", ID);
    }

    /**
     * Returns a list of unique ownercounty records (states in the US)
     * in order - this is to keep county/state names consistent on
     * the owner screen
     */
    public static Vector getOwnerCounties() {
        if (ownercounties == null) {
            try {
                SQLRecordset r = new SQLRecordset();
                r.openRecordset("SELECT DISTINCT OwnerCounty FROM owner WHERE OwnerCounty Is Not Null ORDER BY OwnerCounty",
                    "owner");
                ownercounties = new Vector();

                while (!r.getEOF()) {
                    ownercounties.add(r.getField("OwnerCounty").toString());
                    r.moveNext();
                }

                // Find most common county
                r = new SQLRecordset();
                r.openRecordset("SELECT OwnerCounty, COUNT(OwnerCounty) FROM owner GROUP BY OwnerCounty HAVING OwnerCounty Is Not Null AND OwnerCounty <> '' ORDER BY COUNT(OwnerCounty) DESC",
                    "owner");

                if (!r.getEOF()) {
                    commoncounty = (String) r.getField("OwnerCounty");
                }
            } catch (Exception e) {
                Global.logException(e, LookupCache.class);
            }
        }

        return ownercounties;
    }

    /**
     * Returns a list of unique ownertown records (cities in the US)
     * in order - this is to keep town/city names consistent on
     * the owner screen. TownCounty objects are returned, containing
     * the town and county.
     */
    public static Vector getOwnerTowns() {
        if (ownertowns == null) {
            try {
                SQLRecordset r = new SQLRecordset();
                r.openRecordset("SELECT DISTINCT OwnerTown, OwnerCounty FROM owner WHERE OwnerTown Is Not Null ORDER BY OwnerTown",
                    "owner");
                ownertowns = new Vector();

                while (!r.getEOF()) {
                    ownertowns.add(new TownCounty(r.getField("OwnerTown")
                                                   .toString(),
                            r.getField("OwnerCounty").toString()));
                    r.moveNext();
                }

                // Find most common town
                r = new SQLRecordset();
                r.openRecordset("SELECT OwnerTown, COUNT(OwnerTown) FROM owner GROUP BY OwnerTown HAVING OwnerTown Is Not Null AND OwnerTown <> '' ORDER BY COUNT(OwnerTown) DESC",
                    "owner");

                if (!r.getEOF()) {
                    commontown = (String) r.getField("OwnerTown");
                }
            } catch (Exception e) {
                Global.logException(e, LookupCache.class);
            }
        }

        return ownertowns;
    }

    public static String getMostCommonTown() {
        return commontown;
    }

    public static String getMostCommonCounty() {
        return commoncounty;
    }

    /**
     * Returns the counties for a given town (typically
     * there'll be just one).
     */
    public static Vector getCountiesForTown(String town) {
        Vector out = new Vector();
        Vector v = getOwnerTowns();

        for (int i = 0; i < v.size(); i++) {
            TownCounty t = (TownCounty) v.get(i);

            if (t.town.equals(town)) {
                out.add(t.county);
            }
        }

        return out;
    }

    /**
     * Returns a list of breeds that have been used for the selected
     * species in the past, ordered by popularity - uses a cached Map
     * based on species we've seen before
     * @param speciesID The species ID
     * @return a list of breed names (strings)
     */
    public static Vector getBreedsForSpecies(Integer speciesID) {
        if (breedspecies == null) {
            breedspecies = new HashMap();
        }

        Vector v = (Vector) breedspecies.get(speciesID);

        if (v == null) {
            v = new Vector();

            try {
                SQLRecordset s = new SQLRecordset();
                s.openRecordset(
                    "SELECT breed.BreedName, COUNT(breed.BreedName) AS Popularity " +
                    "FROM animal INNER JOIN " +
                    "breed ON animal.BreedID = breed.ID WHERE " +
                    "animal.SpeciesID = " + speciesID.intValue() + " " +
                    "GROUP BY breed.BreedName ORDER BY Popularity DESC", "breed");

                while (!s.getEOF()) {
                    v.add(s.getField("BreedName"));
                    s.moveNext();
                }

                s.free();
                s = null;
                breedspecies.put(speciesID, v);
            } catch (Exception e) {
                Global.logException(e, Animal.class);
            }
        }

        return v;
    }

    /**
     * Finds the species for the animal on the given movement, then
     * looks for the most recent donation received for a movement on
     * an animal of that species and returns it - The species to donation
     * amount mapping is cached.
     * @param movementID The movement
     * @return An amount or 0 if no similar movements are recorded
     */
    public static Double getDonationAmountForMovementSpecies(int movementID) {
        if (donationspecies == null) {
            donationspecies = new HashMap();
        }

        // Get the species for the animal on this movement first
        Integer speciesID = new Integer(0);

        try {
            SQLRecordset s = new SQLRecordset();
            s.openRecordset("SELECT SpeciesID FROM animal, adoption WHERE " +
                "adoption.AnimalID = animal.ID AND adoption.ID " + " = " +
                movementID, "animal");

            if (s.getEOF()) {
                Global.logError("No animal found for movement.",
                    "LookupCache.getDonationAmountForMovementSpecies");

                return new Double(0); // Badly wrong, no animal?
            }

            s.moveFirst();
            speciesID = (Integer) s.getField("SpeciesID");
            s.free();
            s = null;
        } catch (Exception e) {
            Global.logException(e, LookupCache.class);
        }

        // Ok, we've got the species now, look up an existing mapping
        // if there is one
        Double f = (Double) donationspecies.get(speciesID);

        if (f == null) {
            try {
                // None found, so find the latest donation record we've
                // seen for a movement for an animal of this species
                // NB: PostgreSQL, HSQLDB AND MySQL ALL support the LIMIT clause
                SQLRecordset r = new SQLRecordset();
                r.openRecordset(
                    "SELECT ownerdonation.Donation FROM ownerdonation " +
                    "INNER JOIN adoption ON ownerdonation.MovementID = adoption.ID " +
                    "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                    "WHERE animal.SpeciesID = " + speciesID.intValue() + " " +
                    "ORDER BY ownerdonation.Date DESC LIMIT 1", "ownerdonation");

                if (!r.getEOF()) {
                    f = (Double) r.getField("Donation");
                    donationspecies.put(speciesID, f);
                } else {
                    f = new Double(0);
                }

                r.free();
                r = null;
            } catch (Exception e) {
                Global.logException(e, LookupCache.class);
                f = new Double(0);
            }
        }

        return f;
    }

    /**
     * Class to hold a town/county pair
     */
    public static class TownCounty {
        public String town;
        public String county;

        public TownCounty(String town, String county) {
            this.town = town;
            this.county = county;
        }

        public String toString() {
            return town;
        }
    }
}
