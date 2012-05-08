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
package net.sourceforge.sheltermanager.asm.db;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;

import java.sql.Connection;


public class DatabaseDumper {
    public void start() {
        boolean doDBFS = Dialog.showYesNo(Global.i18n("db",
                    "want_to_include_dbfs"), Global.i18n("db", "include_dbfs"));

        try {
            // Do the dump
            Dumper dump = new Dumper(doDBFS);
            new Thread(dump).start();
        } catch (Exception e) {
            Global.logException(e, DatabaseCopier.class);
        }
    }
}


class Dumper implements Runnable {
    private boolean doDBFS = true;
    private StringBuffer s = new StringBuffer();

    public Dumper(boolean doDBFS) {
        this.doDBFS = doDBFS;
    }

    /**
     * Perform the copy
     */
    public void run() {
        Global.mainForm.initStatusBarMax(50);
        dumpTable("accounts");
        dumpTable("accountstrx");
        dumpTable("additional");
        dumpTable("additionalfield");
        dumpTable("adoption");
        dumpTable("animalcost");
        dumpTable("animaldiet");
        dumpTable("animalfound");
        dumpTable("animallitter");
        dumpTable("animallost");
        dumpTable("animalmedical");
        dumpTable("animalmedicaltreatment");
        dumpTable("animal");
        dumpTable("animalname");
        dumpTable("animaltype");
        dumpTable("animalvaccination");
        dumpTable("animalwaitinglist");
        dumpTable("basecolour");
        dumpTable("breed");
        dumpTable("configuration");
        dumpTable("costtype");
        dumpTable("customreport");
        dumpTable("deathreason");
        dumpTable("diary");
        dumpTable("diarytaskdetail");
        dumpTable("diarytaskhead");
        dumpTable("diet");
        dumpTable("donationtype");
        dumpTable("entryreason");
        dumpTable("internallocation");
        dumpTable("lkcoattype");
        dumpTable("lksdiarylink");
        dumpTable("lksex");
        dumpTable("lksize");
        dumpTable("lksloglink");
        dumpTable("lksmedialink");
        dumpTable("lksmovementtype");
        dumpTable("lkurgency");
        dumpTable("log");
        dumpTable("logtype");
        dumpTable("media");
        dumpTable("medicalprofile");
        dumpTable("medicalpayment");
        dumpTable("medicalpaymenttype");
        dumpTable("ownerdonation");
        dumpTable("owner");
        dumpTable("ownervoucher");
        dumpTable("species");
        dumpTable("users");
        dumpTable("vaccinationtype");
        dumpTable("voucher");

        if (doDBFS) {
            Global.mainForm.setStatusText("Dumping DBFS");
            DBConnection.dumpTable("dbfs");
        }

        Global.mainForm.resetStatusBar();
        Global.mainForm.setStatusText("");

        try {
            Utils.writeFile(Global.tempDirectory + File.separator + "dump.sql",
                s.toString().getBytes());
        } catch (Exception e) {
            Global.logException(e, this.getClass());
        }

        Dialog.showInformation("Data dump complete. Output file is at $HOME/.asm/dump.sql",
            "Finished");
    }

    public void dumpTable(String tableName) {
        Global.mainForm.setStatusText("Dumping table: " + tableName);
        Global.mainForm.incrementStatusBar();
        s.append(DBConnection.dumpTable(tableName));
    }
}
