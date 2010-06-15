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
import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.Date;


public class CustomReport extends UserInfoBO<CustomReport> {
    public CustomReport() {
        tableName = "customreport";
    }

    public CustomReport(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getTitle() throws CursorEngineException {
        return (String) rs.getField("Title");
    }

    public void setTitle(String newValue) throws CursorEngineException {
        rs.setField("Title", newValue);
    }

    public String getSQLCommand() throws CursorEngineException {
        return (String) rs.getField("SQLCommand");
    }

    public void setSQLCommand(String newValue) throws CursorEngineException {
        rs.setField("SQLCommand", newValue);
    }

    public String getHTMLBody() throws CursorEngineException {
        return (String) rs.getField("HTMLBody");
    }

    public void setHTMLBody(String newValue) throws CursorEngineException {
        rs.setField("HTMLBody", newValue);
    }

    public String getCategory() throws CursorEngineException {
        return (String) rs.getField("Category");
    }

    public void setCategory(String newValue) throws CursorEngineException {
        rs.setField("Category", newValue);
    }

    public String getDescription() throws CursorEngineException {
        return (String) rs.getField("Description");
    }

    public void setDescription(String newValue) throws CursorEngineException {
        rs.setField("Description", newValue);
    }

    public Integer getOmitHeaderFooter() throws CursorEngineException {
        return (Integer) rs.getField("OmitHeaderFooter");
    }

    public void setOmitHeaderFooter(Integer newValue)
        throws CursorEngineException {
        rs.setField("OmitHeaderFooter", newValue);
    }

    public Integer getOmitCriteria() throws CursorEngineException {
        return (Integer) rs.getField("OmitCriteria");
    }

    public void setOmitCriteria(Integer newValue) throws CursorEngineException {
        rs.setField("OmitCriteria", newValue);
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
     * Returns true if this is a subreport. Subreports are identified by the
     * presence of a $PARENTKEY$ tag in the SQL.
     */
    public boolean isSubReport() throws CursorEngineException {
        return getSQLCommand().indexOf("$PARENTKEY$") != -1;
    }

    public void validate() throws BOValidationException {
        try {
            if ((getTitle() == null) || getTitle().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "give_report_title"));
            }

            if ((getSQLCommand() == null) || getSQLCommand().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "give_report_sql"));
            }

            if (getSQLCommand().length() != 3) {
                if ((getHTMLBody() == null) || getHTMLBody().equals("")) {
                    throw new BOValidationException(Global.i18n("bo",
                            "give_report_body"));
                }
            } else {
                setHTMLBody("");
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
