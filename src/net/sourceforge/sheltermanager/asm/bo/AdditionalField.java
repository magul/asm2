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
import net.sourceforge.sheltermanager.cursorengine.NormalBO;


public class AdditionalField extends NormalBO<AdditionalField> {
    public final static int LINKTYPE_ANIMAL = 0;
    public final static int LINKTYPE_OWNER = 1;
    public final static int FIELDTYPE_YESNO = 0;
    public final static int FIELDTYPE_TEXT = 1;
    public final static int FIELDTYPE_NOTES = 2;
    public final static int FIELDTYPE_NUMBER = 3;
    public final static int FIELDTYPE_DATE = 4;
    public final static int FIELDTYPE_MONEY = 5;
    public final static int FIELDTYPE_LOOKUP = 6;

    public AdditionalField() {
        tableName = "additionalfield";
    }

    public AdditionalField(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public AdditionalField copy() throws CursorEngineException {
        AdditionalField a = new AdditionalField();
        a.openRecordset("ID = 0");
        a.addNew();
        a.setDisplayIndex(getDisplayIndex());
        a.setFieldName(getFieldName());
        a.setFieldType(getFieldType());
        a.setLinkType(getLinkType());
        a.setLookupValues(getLookupValues());
        a.setMandatory(getMandatory());

        return a;
    }

    public Integer getLinkType() throws CursorEngineException {
        return (Integer) rs.getField("LinkType");
    }

    public void setLinkType(Integer newValue) throws CursorEngineException {
        rs.setField("LinkType", newValue);
    }

    public String getLinkTypeName() throws CursorEngineException {
        return LookupCache.getFieldLinkForID(getLinkType());
    }

    public Integer getFieldType() throws CursorEngineException {
        return (Integer) rs.getField("FieldType");
    }

    public void setFieldType(Integer newValue) throws CursorEngineException {
        rs.setField("FieldType", newValue);
    }

    public String getFieldTypeName() throws CursorEngineException {
        return LookupCache.getFieldTypeForID(getFieldType());
    }

    public String getFieldLabel() throws CursorEngineException {
        return (String) rs.getField("FieldLabel");
    }

    public void setFieldLabel(String newValue) throws CursorEngineException {
        rs.setField("FieldLabel", newValue);
    }

    public String getToolTip() throws CursorEngineException {
        return (String) rs.getField("ToolTip");
    }

    public void setToolTip(String newValue) throws CursorEngineException {
        rs.setField("ToolTip", newValue);
    }

    public Integer getDisplayIndex() throws CursorEngineException {
        return (Integer) rs.getField("DisplayIndex");
    }

    public void setDisplayIndex(Integer newValue) throws CursorEngineException {
        rs.setField("DisplayIndex", newValue);
    }

    public String getFieldName() throws CursorEngineException {
        return (String) rs.getField("FieldName");
    }

    public void setFieldName(String newValue) throws CursorEngineException {
        rs.setField("FieldName", newValue);
    }

    public String getLookupValues() throws CursorEngineException {
        return (String) rs.getField("LookupValues");
    }

    public void setLookupValues(String newValue) throws CursorEngineException {
        rs.setField("LookupValues", newValue);
    }

    public Integer getMandatory() throws CursorEngineException {
        return (Integer) rs.getField("Mandatory");
    }

    public void setMandatory(Integer newValue) throws CursorEngineException {
        rs.setField("Mandatory", newValue);
    }

    public void validate() throws BOValidationException {
        try {
            if ((getFieldName() == null) || (getFieldLabel() == null) ||
                    getFieldName().equals("") || getFieldLabel().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "field_must_have_name_and_label"));
            }

            if (getFieldName().indexOf(" ") != -1) {
                throw new BOValidationException(Global.i18n("bo",
                        "field_name_cannot_contain_spaces"));
            }

            if ((getFieldType().intValue() == FIELDTYPE_LOOKUP) &&
                    ((getLookupValues() == null) ||
                    getLookupValues().equals(""))) {
                throw new BOValidationException(Global.i18n("bo",
                        "lookup_type_needs_values"));
            }
        } catch (Exception e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
