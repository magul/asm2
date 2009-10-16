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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


public class Additional {
    public static Vector getFieldValues(int linkType, int linkID)
        throws Exception {
        try {
            SQLRecordset r = new SQLRecordset();
            r.openRecordset("SELECT AdditionalFieldID, FieldName, FieldLabel, " +
                "Tooltip, FieldType, Value " + "FROM additional " +
                "INNER JOIN additionalfield" +
                " ON additionalfield.ID = additional.AdditionalFieldID" +
                " WHERE additional.LinkID = " + linkID +
                " AND additional.LinkType = " + linkType +
                " ORDER BY DisplayIndex", "additional");

            Vector v = new Vector();

            while (!r.getEOF()) {
                Additional.Field af = new Additional.Field();
                af.fieldID = ((Integer) r.getField("AdditionalFieldID")).intValue();
                af.fieldType = ((Integer) r.getField("FieldType")).intValue();
                af.fieldName = r.getField("FieldName").toString();
                af.fieldLabel = r.getField("FieldLabel").toString();
                af.tooltip = r.getField("ToolTip").toString();
                af.value = r.getField("Value").toString();
                v.add(af);
                r.moveNext();
            }

            return v;
        } catch (Exception e) {
            Global.logException(e, Additional.class);
            throw e;
        }
    }

    public static void setFieldValues(int linkType, int linkID, Vector v)
        throws Exception {
        try {
            // Remove old ones from the database first
            DBConnection.executeAction("DELETE FROM additional WHERE " +
                "LinkID = " + linkID + " AND LinkType = " + linkType);

            // Set the values
            for (int i = 0; i < v.size(); i++) {
                Additional.Field f = (Additional.Field) v.get(i);

                try {
                    DBConnection.executeAction("INSERT INTO additional " +
                        "(LinkType, LinkID, AdditionalFieldID, Value) VALUES (" +
                        linkType + ", " + linkID + ", " + f.fieldID + ", '" +
                        f.value + "')");
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