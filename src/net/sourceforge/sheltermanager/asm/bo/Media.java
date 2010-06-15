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

import net.sourceforge.sheltermanager.cursorengine.*;

import java.util.Date;


public class Media extends NormalBO<Media> {
    /** Constant for a link to an Animal record */
    public static final int LINKTYPE_ANIMAL = 0;

    /** Constant for a link to a Lost Animal record */
    public static final int LINKTYPE_LOSTANIMAL = 1;

    /** Constant for a link to a Found Animal record */
    public static final int LINKTYPE_FOUNDANIMAL = 2;

    /** Constant for a link to an owner record */
    public static final int LINKTYPE_OWNER = 3;

    /** Constant for a link to a movement record */
    public static final int LINKTYPE_MOVEMENT = 4;

    /** Constant for a link to a waiting list record */
    public static final int LINKTYPE_WAITINGLIST = 5;

    public Media() {
        tableName = "media";
    }

    public Media(String where) {
        this();
        openRecordset(where);
    }

    /**
     * Overloaded openRecordset method - allows a recordset to be opened based
     * on the link type and the ID
     *
     * @param linkType
     *            The link type to use
     * @param linkID
     *            The ID to use
     */
    public void openRecordset(int linkType, int linkID) {
        openRecordset("LinkTypeID = " + Integer.toString(linkType) + " AND " +
            "LinkID = " + Integer.toString(linkID));
    }

    public void addNew() throws CursorEngineException {
        super.addNew();

        Integer z = new Integer(0);
        setLinkID(z);
        setLinkTypeID(z);
        setMediaName("");
        setMediaNotes("");
        setWebSitePhoto(z);
        setDocPhoto(z);
        setNewSinceLastPublish(z);
        setUpdatedSinceLastPublish(z);
        setDate(new Date());
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getLinkID() throws CursorEngineException {
        return (Integer) rs.getField("LinkID");
    }

    public void setLinkID(Integer newValue) throws CursorEngineException {
        rs.setField("LinkID", newValue);
    }

    public Integer getLinkTypeID() throws CursorEngineException {
        return (Integer) rs.getField("LinkTypeID");
    }

    public void setLinkTypeID(Integer newValue) throws CursorEngineException {
        rs.setField("LinkTypeID", newValue);
    }

    public String getMediaName() throws CursorEngineException {
        return (String) rs.getField("MediaName");
    }

    public void setMediaName(String newValue) throws CursorEngineException {
        rs.setField("MediaName", newValue);
    }

    public String getMediaNotes() throws CursorEngineException {
        return (String) rs.getField("MediaNotes");
    }

    public void setMediaNotes(String newValue) throws CursorEngineException {
        rs.setField("MediaNotes", newValue);
    }

    public Integer getWebSitePhoto() throws CursorEngineException {
        return (Integer) rs.getField("WebsitePhoto");
    }

    public void setWebSitePhoto(Integer newValue) throws CursorEngineException {
        rs.setField("WebsitePhoto", newValue);
    }

    public Integer getDocPhoto() throws CursorEngineException {
        return (Integer) rs.getField("DocPhoto");
    }

    public void setDocPhoto(Integer newValue) throws CursorEngineException {
        rs.setField("DocPhoto", newValue);
    }

    public Integer getNewSinceLastPublish() throws CursorEngineException {
        return (Integer) rs.getField("NewSinceLastPublish");
    }

    public void setNewSinceLastPublish(Integer newValue)
        throws CursorEngineException {
        rs.setField("NewSinceLastPublish", newValue);
    }

    public Integer getUpdatedSinceLastPublish() throws CursorEngineException {
        return (Integer) rs.getField("UpdatedSinceLastPublish");
    }

    public void setUpdatedSinceLastPublish(Integer newValue)
        throws CursorEngineException {
        rs.setField("UpdatedSinceLastPublish", newValue);
    }

    public Date getDate() throws CursorEngineException {
        return (Date) rs.getField("Date");
    }

    public void setDate(Date newValue) throws CursorEngineException {
        rs.setField("Date", newValue);
    }
}
