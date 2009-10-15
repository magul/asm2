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
package net.sourceforge.sheltermanager.asm.db;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.*;


/**
 * Generates a business object stub from a database table
 *
 * @author Robin Rawson-Tetley
 */
public class MakeClassFromTable {
    public static void main(String[] args) {
        try {
            DBConnection.quiet = true;
            DBConnection.url = "jdbc:mysql://localhost/asm?user=root";
            DBConnection.getConnection();

            // Get the table
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT * FROM " + args[0].toLowerCase(),
                args[0].toLowerCase());

            String cName = args[0].substring(0, 1).toUpperCase() +
                args[0].substring(1, args[0].length());

            out("package net.sourceforge.sheltermanager.asm.bo;\n");
            out("import net.sourceforge.sheltermanager.cursorengine.*;");
            out("import net.sourceforge.sheltermanager.asm.utility.*;");
            out("import net.sourceforge.sheltermanager.asm.globals.*;\n");
            out("import java.util.*;\n");

            out("public class " + cName + " extends UserInfoBO {\n");
            out("    public " + cName + "() { tableName = \"" + cName +
                "\"; }\n");

            for (int i = 1; i <= rs.getFieldCount(); i++) {
                out("    public String get" + rs.getFieldName(i) +
                    "() throws CursorEngineException {");
                out("        return rs.getField(\"" + rs.getFieldName(i) +
                    "\");");
                out("    };\n");
                out("    public void set" + rs.getFieldName(i) +
                    "(String newValue) throws CursorEngineException {");
                out("        rs.setField(\"" + rs.getFieldName(i) +
                    "\", newValue);");
                out("    };\n");
            }

            out("     public void validate() throws BOValidationException {");
            out("     }\n");
            out("}");
        } catch (Exception e) {
            Global.logException(e, MakeClassFromTable.class);
            System.exit(1);
        }

        System.exit(0);
    }

    public static void out(String s) {
        System.out.println(s);
    }
}
