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
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.ArrayList;
import java.util.Date;


public class Owner extends UserInfoBO<Owner> {
    public Owner() {
        tableName = "owner";
    }

    public Owner(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getOwnerTitle() throws CursorEngineException {
        return (String) rs.getField("OwnerTitle");
    }

    public String getOwnerInitials() throws CursorEngineException {
        return (String) rs.getField("OwnerInitials");
    }

    public String getOwnerForenames() throws CursorEngineException {
        return (String) rs.getField("OwnerForenames");
    }

    public String getOwnerSurname() throws CursorEngineException {
        return (String) rs.getField("OwnerSurname");
    }

    public String getOwnerName() throws CursorEngineException {
        return (String) rs.getField("OwnerName");
    }

    public void setOwnerName(String title, String initials, String forenames,
        String surname) throws CursorEngineException {
        rs.setField("OwnerTitle", title);
        rs.setField("OwnerInitials", initials);
        rs.setField("OwnerForeNames", forenames);
        rs.setField("OwnerSurname", surname);

        String name = new String(title + " " + forenames + " " + surname);
        rs.setField("OwnerName", (name.trim()).replaceAll(" {2,}", " "));
    }

    public String getOwnerAddress() throws CursorEngineException {
        return (String) rs.getField("OwnerAddress");
    }

    public void setOwnerAddress(String newValue) throws CursorEngineException {
        rs.setField("OwnerAddress", newValue);
    }

    public String getOwnerPostcode() throws CursorEngineException {
        return (String) rs.getField("OwnerPostcode");
    }

    public void setOwnerPostcode(String newValue) throws CursorEngineException {
        rs.setField("OwnerPostcode", newValue);
    }

    public String getOwnerTown() throws CursorEngineException {
        return (String) rs.getField("OwnerTown");
    }

    public void setOwnerTown(String newValue) throws CursorEngineException {
        rs.setField("OwnerTown", newValue);
    }

    public String getOwnerCounty() throws CursorEngineException {
        return (String) rs.getField("OwnerCounty");
    }

    public void setOwnerCounty(String newValue) throws CursorEngineException {
        rs.setField("OwnerCounty", newValue);
    }

    public String getHomeTelephone() throws CursorEngineException {
        return (String) rs.getField("HomeTelephone");
    }

    public void setHomeTelephone(String newValue) throws CursorEngineException {
        rs.setField("HomeTelephone", newValue);
    }

    public String getMobileTelephone() throws CursorEngineException {
        return (String) rs.getField("MobileTelephone");
    }

    public void setMobileTelephone(String newValue)
        throws CursorEngineException {
        rs.setField("MobileTelephone", newValue);
    }

    public String getWorkTelephone() throws CursorEngineException {
        return (String) rs.getField("WorkTelephone");
    }

    public void setWorkTelephone(String newValue) throws CursorEngineException {
        rs.setField("WorkTelephone", newValue);
    }

    public Integer getIDCheck() throws CursorEngineException {
        return (Integer) rs.getField("IDCheck");
    }

    public void setIDCheck(Integer newValue) throws CursorEngineException {
        rs.setField("IDCheck", newValue);
    }

    public Integer getIsMember() throws CursorEngineException {
        return (Integer) rs.getField("IsMember");
    }

    public void setIsMember(Integer newValue) throws CursorEngineException {
        rs.setField("IsMember", newValue);
    }

    public Date getMembershipExpiryDate() throws CursorEngineException {
        return (Date) rs.getField("MembershipExpiryDate");
    }

    public void setMembershipExpiryDate(Date newValue)
        throws CursorEngineException {
        rs.setField("MembershipExpiryDate", newValue);
    }

    public String getMembershipNumber() throws CursorEngineException {
        return (String) rs.getField("MembershipNumber");
    }

    public void setMembershipNumber(String newValue)
        throws CursorEngineException {
        rs.setField("MembershipNumber", newValue);
    }

    public Integer getIsDonor() throws CursorEngineException {
        return (Integer) rs.getField("IsDonor");
    }

    public void setIsDonor(Integer newValue) throws CursorEngineException {
        rs.setField("IsDonor", newValue);
    }

    public Integer getIsShelter() throws CursorEngineException {
        return (Integer) rs.getField("IsShelter");
    }

    public void setIsShelter(Integer newValue) throws CursorEngineException {
        rs.setField("IsShelter", newValue);
    }

    public Integer getIsACO() throws CursorEngineException {
        return (Integer) rs.getField("IsACO");
    }

    public void setIsACO(Integer newValue) throws CursorEngineException {
        rs.setField("IsACO", newValue);
    }

    public Integer getIsGiftAid() throws CursorEngineException {
        return (Integer) rs.getField("IsGiftAid");
    }

    public void setIsGiftAid(Integer newValue) throws CursorEngineException {
        rs.setField("IsGiftAid", newValue);
    }

    public Integer getIsStaff() throws CursorEngineException {
        return (Integer) rs.getField("IsStaff");
    }

    public void setIsStaff(Integer newValue) throws CursorEngineException {
        rs.setField("IsStaff", newValue);
    }

    public Integer getIsVet() throws CursorEngineException {
        return (Integer) rs.getField("IsVet");
    }

    public void setIsVet(Integer newValue) throws CursorEngineException {
        rs.setField("IsVet", newValue);
    }

    public Date getMatchAdded() throws CursorEngineException {
        return (Date) rs.getField("MatchAdded");
    }

    public void setMatchAdded(Date newValue) throws CursorEngineException {
        rs.setField("MatchAdded", newValue);
    }

    public Date getMatchExpires() throws CursorEngineException {
        return (Date) rs.getField("MatchExpires");
    }

    public void setMatchExpires(Date newValue) throws CursorEngineException {
        rs.setField("MatchExpires", newValue);
    }

    public Integer getMatchActive() throws CursorEngineException {
        return (Integer) rs.getField("MatchActive");
    }

    public void setMatchActive(Integer newValue) throws CursorEngineException {
        rs.setField("MatchActive", newValue);
    }

    public Integer getMatchSex() throws CursorEngineException {
        return (Integer) rs.getField("MatchSex");
    }

    public void setMatchSex(Integer newValue) throws CursorEngineException {
        rs.setField("MatchSex", newValue);
    }

    public Integer getMatchSize() throws CursorEngineException {
        return (Integer) rs.getField("MatchSize");
    }

    public void setMatchSize(Integer newValue) throws CursorEngineException {
        rs.setField("MatchSize", newValue);
    }

    public Double getMatchAgeFrom() throws CursorEngineException {
        return (Double) rs.getField("MatchAgeFrom");
    }

    public void setMatchAgeFrom(Double newValue) throws CursorEngineException {
        rs.setField("MatchAgeFrom", newValue);
    }

    public Double getMatchAgeTo() throws CursorEngineException {
        return (Double) rs.getField("MatchAgeTo");
    }

    public void setMatchAgeTo(Double newValue) throws CursorEngineException {
        rs.setField("MatchAgeTo", newValue);
    }

    public Integer getMatchAnimalType() throws CursorEngineException {
        return (Integer) rs.getField("MatchAnimalType");
    }

    public void setMatchAnimalType(Integer newValue)
        throws CursorEngineException {
        rs.setField("MatchAnimalType", newValue);
    }

    public Integer getMatchSpecies() throws CursorEngineException {
        return (Integer) rs.getField("MatchSpecies");
    }

    public void setMatchSpecies(Integer newValue) throws CursorEngineException {
        rs.setField("MatchSpecies", newValue);
    }

    public Integer getMatchBreed() throws CursorEngineException {
        return (Integer) rs.getField("MatchBreed");
    }

    public void setMatchBreed(Integer newValue) throws CursorEngineException {
        rs.setField("MatchBreed", newValue);
    }

    public Integer getMatchBreed2() throws CursorEngineException {
        return (Integer) rs.getField("MatchBreed2");
    }

    public void setMatchBreed2(Integer newValue) throws CursorEngineException {
        rs.setField("MatchBreed2", newValue);
    }

    public Integer getMatchGoodWithCats() throws CursorEngineException {
        return (Integer) rs.getField("MatchGoodWithCats");
    }

    public void setMatchGoodWithCats(Integer newValue)
        throws CursorEngineException {
        rs.setField("MatchGoodWithCats", newValue);
    }

    public Integer getMatchGoodWithDogs() throws CursorEngineException {
        return (Integer) rs.getField("MatchGoodWithDogs");
    }

    public void setMatchGoodWithDogs(Integer newValue)
        throws CursorEngineException {
        rs.setField("MatchGoodWithDogs", newValue);
    }

    public Integer getMatchGoodWithChildren() throws CursorEngineException {
        return (Integer) rs.getField("MatchGoodWithChildren");
    }

    public void setMatchGoodWithChildren(Integer newValue)
        throws CursorEngineException {
        rs.setField("MatchGoodWithChildren", newValue);
    }

    public Integer getMatchHouseTrained() throws CursorEngineException {
        return (Integer) rs.getField("MatchHouseTrained");
    }

    public void setMatchHouseTrained(Integer newValue)
        throws CursorEngineException {
        rs.setField("MatchHouseTrained", newValue);
    }

    public String getMatchCommentsContain() throws CursorEngineException {
        return (String) rs.getField("MatchCommentsContain");
    }

    public void setMatchCommentsContain(String newValue)
        throws CursorEngineException {
        rs.setField("MatchCommentsContain", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    /**
     * Returns additional fields for the owner
     * @return a vector containing Additional.Field values
     */
    public ArrayList<Additional.Field> getAdditionalFields()
        throws Exception {
        return Additional.getFieldValues(AdditionalField.LINKTYPE_OWNER,
            getID().intValue());
    }

    /** Updates additional fields for the owner.
     * @param v Should contain a list of Additional.Field values
     * @throws Exception
     */
    public void setAdditionalFields(ArrayList<Additional.Field> v)
        throws Exception {
        Additional.setFieldValues(AdditionalField.LINKTYPE_OWNER,
            getID().intValue(), v);
    }

    public Date getDateLastHomeChecked() throws CursorEngineException {
        return (Date) rs.getField("DateLastHomeChecked");
    }

    public void setDateLastHomeChecked(Date newValue)
        throws CursorEngineException {
        rs.setField("DateLastHomeChecked", newValue);
    }

    public Integer getHomeCheckedBy() throws CursorEngineException {
        return (Integer) rs.getField("HomeCheckedBy");
    }

    public void setHomeCheckedBy(Integer newValue) throws CursorEngineException {
        rs.setField("HomeCheckedBy", newValue);
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

    public Integer getIsBanned() throws CursorEngineException {
        return (Integer) rs.getField("IsBanned");
    }

    public void setIsBanned(Integer newValue) throws CursorEngineException {
        rs.setField("IsBanned", newValue);
    }

    public Integer getIsVolunteer() throws CursorEngineException {
        return (Integer) rs.getField("IsVolunteer");
    }

    public void setIsVolunteer(Integer newValue) throws CursorEngineException {
        rs.setField("IsVolunteer", newValue);
    }

    public Integer getIsHomeChecker() throws CursorEngineException {
        return (Integer) rs.getField("IsHomeChecker");
    }

    public void setIsHomeChecker(Integer newValue) throws CursorEngineException {
        rs.setField("IsHomeChecker", newValue);
    }

    public Integer getIsRetailer() throws CursorEngineException {
        return (Integer) rs.getField("IsRetailer");
    }

    public void setIsRetailer(Integer newValue) throws CursorEngineException {
        rs.setField("IsRetailer", newValue);
    }

    public Integer getIsFosterer() throws CursorEngineException {
        return (Integer) rs.getField("IsFosterer");
    }

    public void setIsFosterer(Integer newValue) throws CursorEngineException {
        rs.setField("IsFosterer", newValue);
    }

    public String getHomeCheckAreas() throws CursorEngineException {
        return (String) rs.getField("HomeCheckAreas");
    }

    public void setHomeCheckAreas(String newValue) throws CursorEngineException {
        rs.setField("HomeCheckAreas", newValue);
    }

    public String getEmailAddress() throws CursorEngineException {
        return (String) rs.getField("EmailAddress");
    }

    public void setEmailAddress(String newValue) throws CursorEngineException {
        rs.setField("EmailAddress", newValue);
    }

    /**
     * Returns true if this owner has a valid media record with a picture
     */
    public boolean hasImage() throws CursorEngineException {
        Media med = new Media();
        med.openRecordset("LinkID = " + getID() + " AND LinkTypeID = " +
            Integer.toString(Media.LINKTYPE_OWNER) +
            " AND (MediaName Like '%.jpg' Or MediaName Like '%.gif' Or " +
            "MediaName Like '%.jpeg' Or MediaName Like '%.png')");

        boolean hasValid = !med.getEOF();
        med.free();
        med = null;

        return hasValid;
    }

    /**
     * Returns the name of the file in their media directory that the owner
     * should use for the thumbnail
     */
    public String getThumbnailImage() throws CursorEngineException {
        Media med = new Media();
        med.openRecordset("LinkID = " + getID() + " AND LinkTypeID = " +
            Integer.toString(Media.LINKTYPE_OWNER) +
            " AND (MediaName Like '%.jpg' OR MediaName Like '%.jpeg'" +
            " OR MediaName Like '%.gif' OR MediaName Like '%.png')");

        if (med.getEOF()) {
            return "";
        }

        while (!med.getEOF()) {
            if (med.getWebSitePhoto().intValue() == 1) {
                return med.getMediaName();
            }

            med.moveNext();
        }

        // No preferred was found - use the first
        med.moveFirst();

        return med.getMediaName();
    }

    /** Returns the number of each type of external record for a given owner ID */
    public static Owner.OwnerMarkers getNumExternalRecords(Integer id) {
        try {
            // Check all satellite data in one query
            SQLRecordset r = new SQLRecordset(
                    "SELECT owner.ID, (SELECT COUNT(*) FROM ownerdonation WHERE OwnerID = owner.ID) AS dona, " +
                    "(SELECT COUNT(*) FROM ownervoucher WHERE OwnerID = owner.ID) AS vouc, " +
                    "(SELECT COUNT(*) FROM media WHERE LinkID = owner.ID AND LinkTypeID = " +
                    Media.LINKTYPE_OWNER + ") AS pics, " +
                    "(SELECT COUNT(*) FROM diary WHERE LinkID = owner.ID AND LinkType = " +
                    Diary.LINKTYPE_OWNER + ") AS diar, " +
                    "(SELECT COUNT(*) FROM adoption WHERE OwnerID = owner.ID) AS move, " +
                    "((SELECT COUNT(*) FROM animal WHERE OriginalOwnerID = owner.ID OR " +
                    "BroughtInByOwnerID = owner.ID OR OwnersVetID = owner.ID " +
                    "OR CurrentVetID = owner.ID) + " +
                    "(SELECT COUNT(*) FROM animalwaitinglist " +
                    "WHERE OwnerID = owner.ID) + " +
                    "(SELECT COUNT(*) FROM animallost WHERE " +
                    "OwnerID = owner.ID) + " +
                    "(SELECT COUNT(*) FROM animalfound WHERE " +
                    "OwnerID = owner.ID)) AS link, " +
                    "(SELECT COUNT(*) FROM log WHERE LinkID = owner.ID AND LinkType = " +
                    Log.LINKTYPE_OWNER +
                    ") AS logs FROM owner WHERE owner.ID = " + id, "owner");

            Global.logDebug("Owner markers: donations=" + r.getInt("dona") +
                ", vouchers=" + r.getInt("vouc") + ", media=" +
                r.getInt("pics") + ", diary=" + r.getInt("diar") +
                ", movements=" + r.getInt("move") + ", link=" +
                r.getInt("link") + ", logs=" + r.getInt("logs"),
                "OwnerMarkers.getNumExternalRecords");

            return new OwnerMarkers(r.getInt("dona"), r.getInt("vouc"),
                r.getInt("pics"), r.getInt("diar"), r.getInt("move"),
                r.getInt("link"), r.getInt("logs"));
        } catch (Exception e) {
            Global.logException(e, Owner.class);
        }

        return new OwnerMarkers();
    }

    public static class OwnerMarkers {
        public int donations = 0;
        public int vouchers = 0;
        public int media = 0;
        public int diary = 0;
        public int movement = 0;
        public int links = 0;
        public int log = 0;

        public OwnerMarkers() {
        }

        public OwnerMarkers(int donations, int vouchers, int media, int diary,
            int movement, int links, int log) {
            this.donations = donations;
            this.vouchers = vouchers;
            this.media = media;
            this.diary = diary;
            this.movement = movement;
            this.links = links;
            this.log = log;
        }
    }
}
