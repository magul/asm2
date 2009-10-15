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
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;

import java.sql.Connection;


public class DatabaseImporter implements Runnable {
    private String url = null;
    private Connection c = null;
    private byte dbType = 0;
    private boolean allTables = false;
    private boolean clearBeforeCopy = false;

    /** For the UI - asks for the target interactively */
    public void start() {
        String s = Dialog.getJDBCUrl(Global.i18n("db", "import_database"));

        if (s.equals("")) {
            return;
        }

        try {
            // Connect and do the import
            c = DBConnection.getConnection(s);
            dbType = DBConnection.getDBTypeForUrl(s);
            url = s;
            new Thread(this).start();
        } catch (Exception e) {
            Global.logException(e, DatabaseImporter.class);
        }
    }

    /** For single-threaded callers */
    public void start(String url, Connection c, byte dbType, boolean allTables,
        boolean clearBeforeCopy) {
        this.url = url;
        this.c = c;
        this.dbType = dbType;
        this.allTables = allTables;
        this.clearBeforeCopy = clearBeforeCopy;
        run();
    }

    public void run() {
        if (Global.mainForm != null) {
            Global.mainForm.initStatusBarMax(42);
        }

        importTable(c, dbType, "adoption", true, "importtool");
        importTable(c, dbType, "animaldiet", true, "importtool");
        importTable(c, dbType, "animalfound", true, "importtool");
        importTable(c, dbType, "animallitter", false, "importtool");
        importTable(c, dbType, "animallost", true, "importtool");
        importTable(c, dbType, "animalmedical", true, "importtool");
        importTable(c, dbType, "animalmedicaltreatment", true, "importtool");
        importTable(c, dbType, "animal", true, "importtool");
        importTable(c, dbType, "animalname", false, "importtool");
        importTable(c, dbType, "animaltype", false, "importtool");
        importTable(c, dbType, "animalvaccination", true, "importtool");
        importTable(c, dbType, "animalwaitinglist", true, "importtool");
        importTable(c, dbType, "basecolour", false, "importtool");
        importTable(c, dbType, "breed", false, "importtool");

        if (allTables) {
            importTable(c, dbType, "configuration", false, "importtool");
        }

        importTable(c, dbType, "customreport", false, "importtool");
        importTable(c, dbType, "deathreason", false, "importtool");
        importTable(c, dbType, "diary", true, "importtool");
        importTable(c, dbType, "diarytaskdetail", false, "importtool");
        importTable(c, dbType, "diarytaskhead", false, "importtool");
        importTable(c, dbType, "diet", false, "importtool");
        importTable(c, dbType, "donationtype", false, "importtool");
        importTable(c, dbType, "entryreason", false, "importtool");
        importTable(c, dbType, "internallocation", false, "importtool");
        importTable(c, dbType, "lksdiarylink", false, "importtool");
        importTable(c, dbType, "lksex", false, "importtool");
        importTable(c, dbType, "lksize", false, "importtool");
        importTable(c, dbType, "lksloglink", false, "importtool");
        importTable(c, dbType, "lksmedialink", false, "importtool");
        importTable(c, dbType, "lksmovementtype", false, "importtool");
        importTable(c, dbType, "lkurgency", false, "importtool");
        importTable(c, dbType, "log", true, "importtool");
        importTable(c, dbType, "logtype", false, "importtool");
        importTable(c, dbType, "media", false, "importtool");
        importTable(c, dbType, "medicalprofile", true, "importtool");
        importTable(c, dbType, "medicalpayment", true, "importtool");
        importTable(c, dbType, "medicalpaymenttype", false, "importtool");
        importTable(c, dbType, "ownerdonation", false, "importtool");
        importTable(c, dbType, "owner", true, "importtool");
        importTable(c, dbType, "ownervoucher", false, "importtool");
        importTable(c, dbType, "species", false, "importtool");

        if (allTables) {
            importTable(c, dbType, "users", false, "importtool");
        }

        importTable(c, dbType, "vaccinationtype", false, "importtool");
        importTable(c, dbType, "voucher", false, "importtool");
        DBConnection.importDBFS(c);

        // Run checkpoint
        DBConnection.checkpoint();

        // Dump the primary key table since we've imported lots of records
        try {
            DBConnection.executeAction("DELETE FROM primarykey");
        } catch (Exception e) {
        }

        try {
            c.close();
        } catch (Exception e) {
        }

        if (Global.mainForm != null) {
            Global.mainForm.resetStatusBar();
            Global.mainForm.setStatusText("");
        }

        if (Global.mainForm != null) {
            Dialog.showInformation(Global.i18n("db", "import_successful"),
                Global.i18n("db", "import_complete"));
        }

        Global.logInfo(Global.i18n("db", "import_complete"),
            "DatabaseImporter.run");
    }

    public void importTable(Connection c, byte dbType, String tableName,
        boolean userInfo, String userName) {
        try {
            if (Global.mainForm != null) {
                Global.mainForm.setStatusText(Global.i18n("db",
                        "importing_table", tableName));
                Global.mainForm.incrementStatusBar();
            }

            Global.logInfo(Global.i18n("db", "importing_table", tableName),
                "DatabaseImporter.run");

            if (clearBeforeCopy) {
                DBConnection.executeAction("DELETE FROM " + tableName);
            }

            DBConnection.importTable(c, dbType, tableName, userInfo, userName,
                false);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
