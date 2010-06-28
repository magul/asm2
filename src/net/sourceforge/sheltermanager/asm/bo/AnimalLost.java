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
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.*;


public class AnimalLost extends UserInfoBO<AnimalLost> {
    /**
     * The number of match points needed for a 100% match when matching lost to
     * found
     */
    public static final int MATCHMAX = 30;

    /** Cached owner */
    private Owner owner = null;

    public AnimalLost() {
        tableName = "animallost";
    }

    public AnimalLost(String where) {
        this();
        openRecordset(where);
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

    public Date getDateLost() throws CursorEngineException {
        return (Date) rs.getField("DateLost");
    }

    public void setDateLost(Date newValue) throws CursorEngineException {
        rs.setField("DateLost", newValue);
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

    public String getAreaLost() throws CursorEngineException {
        return (String) rs.getField("AreaLost");
    }

    public void setAreaLost(String newValue) throws CursorEngineException {
        rs.setField("AreaLost", newValue);
    }

    public String getAreaPostcode() throws CursorEngineException {
        return (String) rs.getField("AreaPostcode");
    }

    public void setAreaPostcode(String newValue) throws CursorEngineException {
        rs.setField("AreaPostcode", newValue);
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
            // Is there anything there?
            if (!owner.getEOF()) {
                // Is it the correct one?
                if (owner.getID().equals(getOwnerID())) {
                    // It is - return it
                    return owner;
                }
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

    /**
     * Overridden from superclass - contains Lost Animal specific validation
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

            // Make sure we have a lost date
            if (getDateLost() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_date_the_animal_was_lost."));
            }

            // Make sure we have a reported date
            if (getDateReported() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_date_the_animal_was_reported_missing."));
            }

            // Make sure we have an area
            if ((getAreaLost() == null) || getAreaLost().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_enter_the_area_the_animal_went_missing_from."));
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }

    /**
     * Searches for a best match in the found animal and regular animal
     * database.<br>
     * <br>
     * Breakdown of match point system:<br>
     * <br>
     * Same species +5<br>
     * Area Lost +5 for 100% match, proportional to words<br>
     * Postcode +5<br>
     * Distinguishing Features +5 for 100% match, proportional to words<br>
     * Same Colour +5<br>
     * Found within 2 weeks of lost +5<br>
     * <br>
     * If a match is the wrong species though, we throw it away entirely. This
     * makes our final score out of a possible 30.
     *
     * @param matchPointFloor -
     *            The number of points required for the match to appear. The
     *            maximum number of valid points for a match is 30.
     * @param includeShelter -
     *            True if you want to check the animal database for possible
     *            matches.
     * @param filterByDate -
     *            True if you want to specify a date range (animals found
     *            between two dates) and ignore the rest.
     * @param mySQLFromDate -
     *            The from date to use in mySQL format if
     *            <code>filterByDate</code> is set.
     * @param mySQLToDate -
     *            The to date to use in mySQL format if
     *            <code>filterByDate</code> is set.
     * @param foundFilterID -
     *            If you only want to find lost animals that have a particular
     *            found animal on, set this to a value other than 0.
     * @param animalID -
     *            If you only want to find lost animals that have a particular
     *            shelter animal on, set this to a value other than 0.
     * @return A <code>java.util.Vector</code> containing all the possible
     *         matches for the animal. The array format is:<br>
     *         0 - Contact Name<br>
     *         1 - Contact Number<br>
     *         2 - Area Found<br>
     *         3 - Area Postcode<br>
     *         4 - SpeciesName<br>
     *         5 - DistinguishingFeatures<br>
     *         6 - Base Colour Name<br>
     *         7 - Date Found<br>
     *         8 - Match Points
     * @throws CursorEngineException
     *             If an error occurs accessing data.
     */
    public Vector<String[]> match(int matchPointFloor, boolean includeShelter,
        boolean filterByDate, Date fromDate, Date toDate, int foundFilterID,
        int animalID) throws CursorEngineException {
        Vector<String[]> returnedRows = new Vector<String[]>();
        int matchPoints = 0;

        // If we have an animalID then there's no point checking
        // found animals at all
        if (animalID == 0) {
            AnimalFound aflist = new AnimalFound();

            if (filterByDate) {
                aflist.openRecordset("DateFound >= '" +
                    SQLRecordset.getSQLRepresentationOfDate(fromDate) +
                    "' AND DateFound <= '" +
                    SQLRecordset.getSQLRepresentationOfDate(toDate) +
                    "' AND AnimalTypeID = " + getSpeciesID() +
                    ((foundFilterID != 0) ? (" AND ID = " + foundFilterID) : ""));
            } else {
                aflist.openRecordset("AnimalTypeID = " + getSpeciesID() +
                    ((foundFilterID != 0) ? (" AND ID = " + foundFilterID) : ""));
            }

            for (AnimalFound af : aflist) {
                // Start at 5 match points, because
                // species has to match for it to be included.
                matchPoints = 5;

                // Area Lost
                matchPoints += scoreMatchingWords(getAreaLost(),
                    af.getAreaFound(), 5);

                // Features
                matchPoints += scoreMatchingWords(getDistFeat(),
                    af.getDistFeat(), 5);

                // Postcode
                if (Utils.nullToEmptyString(getAreaPostcode()).trim()
                             .equalsIgnoreCase(Utils.nullToEmptyString(
                                af.getAreaPostcode()).trim())) {
                    matchPoints += 5;
                }

                // Colour
                if (getBaseColourID().equals(af.getBaseColourID())) {
                    matchPoints += 3;
                }

                // Date found within 2 weeks of lost
                try {
                    Calendar datelost = Utils.dateToCalendar(getDateLost());
                    Calendar datefound = Utils.dateToCalendar(af.getDateFound());
                    long minutediff = Utils.getDateDiff(datefound, datelost);
                    long daydiff = ((minutediff / 60) / 24);

                    if ((daydiff < 14) && (daydiff > -14)) {
                        matchPoints += 5;
                    }
                } catch (Exception e) {
                }

                // If the match is better than our top score,
                // cap it at that
                if (matchPoints > MATCHMAX) {
                    matchPoints = MATCHMAX;
                }

                // If the match is good enough, include it in
                // the list
                if (matchPoints >= matchPointFloor) {
                    String contactName = "";
                    String contactNumber = "";

                    try {
                        contactName = af.getOwner().getOwnerName();
                        contactNumber = af.getOwner().getHomeTelephone();
                    } catch (Exception e) {
                        // Leave blank if no contact number
                        contactName = "N/A";
                        contactNumber = "N/A";
                    }

                    String[] entry = {
                            contactName, contactNumber,
                            Utils.nullToEmptyString(af.getAreaFound()),
                            Utils.nullToEmptyString(af.getAreaPostcode()),
                            af.getSpeciesName(),
                            Utils.nullToEmptyString(af.getDistFeat()),
                            af.getBaseColourName(),
                            Utils.formatDateLong(af.getDateFound()),
                            Integer.toString(matchPoints), af.getID().toString()
                        };
                    returnedRows.add(entry);
                }
            }
        }

        // If we aren't checking the shelter, stop now
        if (!includeShelter) {
            return returnedRows;
        }

        // Do a scan of the shelter records, filtering
        // by some known value
        Animal an = new Animal();

        if (animalID == 0) {
            an.openRecordset("SpeciesID = " + getSpeciesID() +
                " AND BaseColourID = " + getBaseColourID());
        } else {
            an.openRecordset("ID = " + animalID);
        }

        while (!an.getEOF()) {
            // Start at 7 since they automatically are the same
            // species and colour
            matchPoints = 7;

            try {
                // Area Lost
                matchPoints += scoreMatchingWords(getAreaLost(),
                    an.getOriginalOwner().getOwnerAddress(), 5);

                // Postcode
                if (getAreaPostcode().trim()
                            .equalsIgnoreCase(an.getOriginalOwner()
                                                    .getOwnerPostcode().trim())) {
                    matchPoints += 5;
                }
            } catch (Exception e) {
                // No original owner
            }

            // Found within 2 weeks
            try {
                Calendar datelost = Utils.dateToCalendar(getDateLost());
                Calendar datefound = Utils.dateToCalendar(an.getDateBroughtIn());
                long minutediff = Utils.getDateDiff(datefound, datelost);
                long daydiff = ((minutediff / 60) / 24);

                if ((daydiff < 14) && (daydiff > -14)) {
                    matchPoints += 5;
                }
            } catch (Exception e) {
            }

            // Distinguishing Features
            matchPoints += scoreMatchingWords(getDistFeat(), an.getMarkings(), 5);

            // If the match is better than our top score,
            // cap it at that
            if (matchPoints > MATCHMAX) {
                matchPoints = MATCHMAX;
            }

            // If the match is good enough, include it in
            // the list
            if (matchPoints >= matchPointFloor) {
                String ownerPostcode = "";

                try {
                    ownerPostcode = Utils.nullToEmptyString(an.getOriginalOwner()
                                                              .getOwnerPostcode());
                } catch (Exception e) {
                }

                String[] entry = {
                        Global.i18n("bo", "Shelter_Animal_") +
                        an.getShelterCode() + " '" + an.getAnimalName() + "'",
                        an.getAnimalTypeName(), Global.i18n("bo", "On_Shelter"),
                        ownerPostcode, an.getSpeciesName(),
                        Utils.nullToEmptyString(an.getMarkings()),
                        an.getBaseColourName(),
                        Utils.formatDateLong(an.getDateBroughtIn()),
                        Integer.toString(matchPoints),
                        Global.i18n("reports", "n_a")
                    };
                returnedRows.add(entry);
            }

            an.moveNext();
        }

        return returnedRows;
    }

    /**
     * Scores two strings according to how well they match by checking the
     * number of words in the first string that are present in the second. All
     * words in the first appearing in the second is a 100% match. The final
     * result is expressed as a percentage of <code>maxScore</code>.
     *
     * @param ss1
     *            The first string
     * @param ss2
     *            The second string
     * @param maxScore
     *            The range to display the result as - if it was 100 for
     *            example, 3 words of string 1 appearing in string 2 would be a
     *            50% match.
     * @return The appropriate match value.
     */
    private int scoreMatchingWords(String ss1, String ss2, int maxScore) {
        // Make sure original strings aren't altered
        String s1 = null;
        String s2 = null;

        if (ss1 == null) {
            s1 = "";
        } else {
            s1 = new String(ss1);
        }

        if (ss2 == null) {
            s2 = "";
        } else {
            s2 = new String(ss2);
        }

        // Remove commas and turn line feeds into spaces
        s1 = Utils.replace(s1, ",", "");
        s2 = Utils.replace(s2, ",", "");
        s1 = Utils.replace(s1, "\n", "");
        s2 = Utils.replace(s2, "\n", "");

        // Remove whitespace
        s1 = s1.trim();
        s2 = s2.trim();

        // Ditch now if there isn't anything there
        if (s1.equals("") || s2.equals("")) {
            return 0;
        }

        // Split into arrays
        String[] v1 = Utils.split(s1, " ");
        String[] v2 = Utils.split(s2, " ");

        int noMatches = 0;

        // Loop through the words, seeing if any match. If
        // they do, give a point for each.
        for (int i = 0; i < v1.length; i++) {
            for (int z = 0; z < v2.length; z++) {
                if (v1[i].equalsIgnoreCase(v2[z])) {
                    noMatches++;
                }
            }
        }

        // Work out as a percentage of the first string the
        // match of the second, so all words in the first
        // present in the second is a 100% match, but instead
        // of using 100 to get the percentage, use the scoring
        // range given.
        int theMatch = (noMatches / v1.length) * maxScore;

        return theMatch;
    }
}
