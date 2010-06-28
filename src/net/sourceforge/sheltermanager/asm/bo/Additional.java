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
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


public class Additional {
    public static Vector<Additional.Field> getFieldValues(int linkType,
        int linkID) throws Exception {
        try {
            SQLRecordset recs = new SQLRecordset(
                    "SELECT AdditionalFieldID, FieldName, FieldLabel, " +
                    "Tooltip, FieldType, Value " + "FROM additional " +
                    "INNER JOIN additionalfield" +
                    " ON additionalfield.ID = additional.AdditionalFieldID" +
                    " WHERE additional.LinkID = " + linkID +
                    " AND additional.LinkType = " + linkType +
                    " ORDER BY DisplayIndex", "additional");

            Vector<Additional.Field> v = new Vector<Additional.Field>();

            for (SQLRecordset r : recs) {
                Additional.Field af = new Additional.Field();
                af.fieldID = ((Integer) r.getField("AdditionalFieldID")).intValue();
                af.fieldType = ((Integer) r.getField("FieldType")).intValue();
                af.fieldName = r.getField("FieldName").toString();
                af.fieldLabel = r.getField("FieldLabel").toString();
                af.tooltip = r.getField("ToolTip").toString();
                af.value = r.getField("Value").toString();
                v.add(af);
            }

            return v;
        } catch (Exception e) {
            Global.logException(e, Additional.class);
            throw e;
        }
    }

    public static void setFieldValues(int linkType, int linkID,
        Vector<Additional.Field> v) throws Exception {
        try {
            // Set the values
            for (int i = 0; i < v.size(); i++) {
                Additional.Field f = (Additional.Field) v.get(i);

                try {
                    // Try an update first
                    int co = DBConnection.executeUpdate(
                            "UPDATE additional SET Value = '" + f.value +
                            "' WHERE LinkType = " + linkType +
                            " AND LinkID = " + linkID +
                            " AND AdditionalFieldID = " + f.fieldID);

                    // If no records were updated, do an insert instead
                    if (co == 0) {
                        DBConnection.executeAction("INSERT INTO additional " +
                            "(LinkType, LinkID, AdditionalFieldID, Value) VALUES (" +
                            linkType + ", " + linkID + ", " + f.fieldID +
                            ", '" + f.value + "')");
                    }
                } catch (Exception e) {
                    Global.logError("Failed adding field " + f.fieldName +
                        " with value '" + f.value + "'",
                        "Additional.setFieldValues");
                    Global.logException(e, Additional.class);
                }
            }
        } catch (Exception e) {
            Global.logException(e, Additional.class);
            throw e;
        }
    }

    public static class Field {
        public int fieldType;
        public int fieldID;
        public String fieldName;
        public String fieldLabel;
        public String tooltip;
        public String value;
    }
}
