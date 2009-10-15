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

import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.Date;
import java.util.Vector;


public class Owner extends UserInfoBO {
    public Owner() {
        tableName = "owner";
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
    public Vector getAdditionalFields() throws Exception {
        return Additional.getFieldValues(AdditionalField.LINKTYPE_OWNER,
            getID().intValue());
    }

    /** Updates additional fields for the owner.
     * @param v Should contain a list of Additional.Field values
     * @throws Exception
     */
    public void setAdditionalFields(Vector v) throws Exception {
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

    public Date getDatePerformedLastHomeCheck() throws CursorEngineException {
        return (Date) rs.getField("DatePerformedLastHomeCheck");
    }

    public void setDatePerformedLastHomeCheck(Date newValue)
        throws CursorEngineException {
        rs.setField("DatePerformedLastHomeCheck", newValue);
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
}
