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
package net.sourceforge.sheltermanager.asm.script;

import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.CustomReportExecute;


/**
 * This class wraps up the runreport command and calls various bits of the
 * system to actually do it.
 *
 * Note that this call is incapable of running interactive reports and will drop
 * with errors if you attempt to.
 *
 * Command: runreport
 * Options: "reportname=<title>".
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class RunReport {
    public RunReport(String[] args) {
        try {
            if (args.length < 1) {
                Global.setUsingLog(true);
                Global.logError("No report name specified.",
                    "RunReport.RunReport");
                System.exit(1);
            }

            CustomReport cr = new CustomReport();
            cr.openRecordset("Title Like '%" + args[1] + "%'");

            if (cr.getEOF()) {
                Global.setUsingLog(true);
                Global.logError("Invalid report name or part name specified.",
                    "RunReport.RunReport");
                System.exit(1);
            }

            // Run the report
            new CustomReportExecute(cr.getID().toString(), true);
        } catch (Exception e) {
            Global.logException(e, getClass());
            System.exit(1);
        }
    }
}
