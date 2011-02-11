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
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.*;


public class DiaryTaskDetail extends NormalBO<DiaryTaskDetail> {
    public DiaryTaskDetail() {
        tableName = "diarytaskdetail";
    }

    public DiaryTaskDetail(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Integer getDiaryTaskHeadID() throws CursorEngineException {
        return (Integer) rs.getField("DiaryTaskHeadID");
    }

    public void setDiaryTaskHeadID(Integer newValue)
        throws CursorEngineException {
        rs.setField("DiaryTaskHeadID", newValue);
    }

    public Integer getDayPivot() throws CursorEngineException {
        return (Integer) rs.getField("DayPivot");
    }

    public void setDayPivot(Integer newValue) throws CursorEngineException {
        rs.setField("DayPivot", newValue);
    }

    public String getWhoFor() throws CursorEngineException {
        return (String) rs.getField("WhoFor");
    }

    public void setWhoFor(String newValue) throws CursorEngineException {
        rs.setField("WhoFor", newValue);
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

    /**
     * Overridden from superclass - contains Diary specific validation routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            if (getDayPivot() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_set_a_day_pivot."));
            }

            if ((getWhoFor() == null) || getWhoFor().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_set_who_the_task_is_for"));
            }

            if ((getSubject() == null) || getSubject().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_set_a_subject."));
            }

            if ((getNote() == null) || getNote().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_set_the_note_text."));
            }
        } catch (Exception e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
