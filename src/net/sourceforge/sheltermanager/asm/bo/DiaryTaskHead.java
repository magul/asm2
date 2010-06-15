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


public class DiaryTaskHead extends NormalBO<DiaryTaskHead> {
    public final static int RECORDTYPE_ANIMAL = 0;
    public final static int RECORDTYPE_OWNER = 1;

    public DiaryTaskHead() {
        tableName = "diarytaskhead";
    }

    public DiaryTaskHead(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getName() throws CursorEngineException {
        return (String) rs.getField("Name");
    }

    public void setName(String newValue) throws CursorEngineException {
        rs.setField("Name", newValue);
    }

    public Integer getRecordType() throws CursorEngineException {
        return (Integer) rs.getField("RecordType");
    }

    public void setRecordType(Integer newValue) throws CursorEngineException {
        rs.setField("RecordType", newValue);
    }

    /**
     * Overridden from superclass - contains Diary specific validation routines.
     *
     * @throws BOValidationException
     *             if there is a validation problem.
     */
    public void validate() throws BOValidationException {
        try {
            if ((getName() == null) || getName().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_give_the_diary_task_a_name."));
            }
        } catch (Exception e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
