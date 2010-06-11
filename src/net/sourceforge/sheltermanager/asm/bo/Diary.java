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


public class Diary extends UserInfoBO {
    public static final int LINKTYPE_NONE = 0;
    public static final int LINKTYPE_ANIMAL = 1;
    public static final int LINKTYPE_OWNER = 2;
    public static final int LINKTYPE_LOSTANIMAL = 3;
    public static final int LINKTYPE_FOUNDANIMAL = 4;
    public static final int LINKTYPE_WAITINGLIST = 5;
    public static final int LINKTYPE_MOVEMENT = 6;

    public Diary() {
        tableName = "diary";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Diary copy() throws CursorEngineException {
        Diary d = new Diary();
        d.openRecordset("ID = 0");
        d.addNew();
        d.setLinkID(getLinkID());
        d.setLinkType(getLinkType());
        d.setDiaryDateTime(getDiaryDateTime());
        d.setDiaryForName(getDiaryForName());
        d.setSubject(getSubject());
        d.setNote(getNote());

        return d;
    }

    public Integer getLinkID() throws CursorEngineException {
        return (Integer) rs.getField("LinkID");
    }

    public void setLinkID(Integer newValue) throws CursorEngineException {
        rs.setField("LinkID", newValue);
    }

    public Integer getLinkType() throws CursorEngineException {
        return (Integer) rs.getField("LinkType");
    }

    public void setLinkType(Integer newValue) throws CursorEngineException {
        rs.setField("LinkType", newValue);
    }

    public Date getDiaryDateTime() throws CursorEngineException {
        return (Date) rs.getField("DiaryDateTime");
    }

    public void setDiaryDateTime(Date newValue) throws CursorEngineException {
        rs.setField("DiaryDateTime", newValue);
    }

    public String getDiaryForName() throws CursorEngineException {
        return (String) rs.getField("DiaryForName");
    }

    public void setDiaryForName(String newValue) throws CursorEngineException {
        rs.setField("DiaryForName", newValue);
    }

    public String getSubject() throws CursorEngineException {
        return (String) rs.getField("Subject");
    }

    public void setSubject(String newValue) throws CursorEngineException {
        rs.setField("Subject", newValue);
    }

    public String getNote() throws CursorEngineException {
        return (String) rs.getField("Note");
    }

    public void setNote(String newValue) throws CursorEngineException {
        rs.setField("Note", newValue);
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

    public Date getDateCompleted() throws CursorEngineException {
        return (Date) rs.getField("DateCompleted");
    }

    public void setDateCompleted(Date newValue) throws CursorEngineException {
        rs.setField("DateCompleted", newValue);
    }

    /**
     * Returns the link information for this diary note
     */
    public String getLinkInfoThis() {
        try {
            if (getLinkID().intValue() == 0) {
                return "";
            } else {
                return Diary.getLinkInfo(getLinkID().intValue(),
                    getLinkType().intValue());
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns useful info about a diary link.
     *
     * @param linkID
     *            The ID of the record in the link
     * @param linkType
     *            The type of link
     * @return A string containing human-readable info
     */
    public static String getLinkInfo(int linkID, int linkType) {
        String output = null;

        try {
            switch (linkType) {
            case LINKTYPE_NONE:
                output = Global.i18n("bo", "No_link.");

                break;

            case LINKTYPE_ANIMAL:

                Animal a = new Animal();
                a.openRecordset("ID = " + linkID);

                if (!a.getEOF()) {
                    if (a.getAnimalLocationAtDate(new Date()) == Animal.ONSHELTER) {
                        output = a.getShortCode() + " " + a.getAnimalName() +
                            " (" + a.getShelterLocationName() + ")";
                    } else {
                        output = a.getShortCode() + " " + a.getAnimalName() +
                            " [" + a.getAnimalLocationAtDateByName(new Date()) +
                            "]";
                    }
                } else {
                    output = Global.i18n("bo", "Bad_animal_link");
                }

                break;

            case LINKTYPE_OWNER:

                Owner o = new Owner();
                o.openRecordset("ID = " + linkID);

                if (!o.getEOF()) {
                    output = Global.i18n("bo", "owner_link", o.getOwnerName());
                } else {
                    output = Global.i18n("bo", "Bad_owner_link");
                }

                break;

            case LINKTYPE_LOSTANIMAL:

                AnimalLost al = new AnimalLost();
                al.openRecordset("ID = " + linkID);

                if (!al.getEOF()) {
                    output = Global.i18n("bo", "lost_animal_link",
                            al.getOwner().getOwnerName());
                } else {
                    output = Global.i18n("bo", "Bad_lost_animal_link");
                }

                break;

            case LINKTYPE_FOUNDANIMAL:

                AnimalFound af = new AnimalFound();
                af.openRecordset("ID = " + linkID);

                if (!af.getEOF()) {
                    output = Global.i18n("bo", "found_animal_link",
                            af.getOwner().getOwnerName());
                } else {
                    output = Global.i18n("bo", "Bad_found_animal_link");
                }

                break;

            case LINKTYPE_WAITINGLIST:

                AnimalWaitingList aw = new AnimalWaitingList();
                aw.openRecordset("ID = " + linkID);

                if (!aw.getEOF()) {
                    output = Global.i18n("bo", "waiting_list_link",
                            aw.getOwner().getOwnerName());
                } else {
                    output = Global.i18n("bo", "Bad_waiting_list_link");
                }

                break;

            case LINKTYPE_MOVEMENT:

                Adoption ad = new Adoption();
                ad.openRecordset("ID = " + linkID);

                if (!ad.getEOF()) {
                    output = Global.i18n("bo", "movement_to",
                            ad.getAnimal().getShelterCode(),
                            ad.getAnimal().getAnimalName(),
                            ad.getOwner().getOwnerName());
                } else {
                    output = Global.i18n("bo", "Bad_movement_link");
                }

                break;

            default:
                output = Global.i18n("bo", "Unrecognised_link.");
            }
        } catch (Exception e) {
            Global.logException(e, Diary.class);
        }

        return output;
    }

    /**
     * Overridden from superclass - contains Diary specific validation routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            // Don't bother if we have multiple records - only diary
            // tasks do that and they are validated elsewhere
            if ((this.getRecordCount() > 1) || this.getEOF() || this.getBOF()) {
                return;
            }

            if (getDiaryDateTime() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_at_least_a_date_for_the_diary_note."));
            }

            if ((getDiaryForName() == null) || getDiaryForName().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_set_the_diary_note_for_someone's_attention."));
            }

            if ((getSubject() == null) || getSubject().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_a_subject_for_the_diary_note."));
            }

            if ((getNote() == null) || getNote().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "The_diary_note_text_cannot_be_blank."));
            }
        } catch (Exception e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
