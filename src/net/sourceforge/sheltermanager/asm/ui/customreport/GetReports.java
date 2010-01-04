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
package net.sourceforge.sheltermanager.asm.ui.customreport;

import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Gets additional reports from the sheltermanager website and allows
 * installation into the database.
 *
 * @author Robin Rawson-Tetley
 */
public class GetReports extends ASMView {
    private UI.Button btnInstall;
    private Vector reports = null;

    public GetReports() {
        init(Global.i18n("uicustomreport", "Install_additional_reports"),
            IconManager.getIcon(IconManager.SCREEN_INSTALLCUSTOMREPORTS),
            "uicustomreport");
        updateList();
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecCreateCustomReports()) {
            btnInstall.setEnabled(false);
        }
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnInstall;
    }

    public boolean formClosing() {
        return false;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    /**
     * Gets the list of installable reports from the sheltermanager
     * website.
     */
    public void updateList() {
        new Thread() {
                public void run() {
                    updateListThread();
                }
            }.start();
    }

    public void updateListThread() {
        try {
            if (reports == null) {
                // Open the list of custom reports so we can omit reports
                // that are already installed
                CustomReport cr = new CustomReport();
                cr.openRecordset("ID > 0 ORDER BY Title");

                // Grab the reports
                reports = new Vector();

                String rs = Utils.getURL(
                        "http://sheltermanager.sf.net/reports.txt");
                String[] reps = Utils.split(rs, "&&&");

                for (int i = 0; i < reps.length; i++) {
                    String[] b = Utils.split(reps[i], "###");

                    InstallableReport r = new InstallableReport();
                    r.name = b[0].trim();
                    Global.logDebug("Found installable report: " + r.name,
                        "GetReports.updateListThread");

                    // Skip if we've installed this one already
                    boolean installed = false;
                    cr.moveFirst();

                    while (!cr.getEOF()) {
                        if (cr.getTitle().equalsIgnoreCase(r.name)) {
                            Global.logDebug("Skipping, already installed.",
                                "GetReports.updateListThread");
                            installed = true;

                            break;
                        }

                        cr.moveNext();
                    }

                    if (installed) {
                        continue;
                    }

                    r.category = b[1].trim();
                    r.database = b[2].trim();
                    r.description = b[3].trim();
                    r.locale = b[4].trim();

                    // Skip if the locale isn't valid for ours
                    if (Global.settings_Locale.indexOf(r.locale) == -1) {
                        Global.logDebug("Skipping, wrong locale (" + r.locale +
                            ")", "GetReports.updateListThread");

                        continue;
                    }

                    r.sql = b[5].trim();
                    r.html = b[6].trim();
                    reports.add(r);
                }

                // Done with custom report list now
                cr.free();
            }

            // Create an array to hold the results for the table
            String[][] datar = new String[reports.size()][7];

            // Create an array of headers for the table
            String[] columnheaders = {
                    i18n("Type"), i18n("Title"), i18n("category"),
                    i18n("Database"), i18n("Locale"), i18n("Description")
                };

            // Build the data
            for (int i = 0; i < reports.size(); i++) {
                InstallableReport r = (InstallableReport) reports.get(i);
                datar[i][0] = r.html.trim().equalsIgnoreCase("GRAPH")
                    ? i18n("Graph") : i18n("Report");
                datar[i][1] = r.name;
                datar[i][2] = r.category;
                datar[i][3] = r.database;
                datar[i][4] = r.locale;
                datar[i][5] = r.description;
                datar[i][6] = Integer.toString(i);
            }

            setTableData(columnheaders, datar, reports.size(), 6);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void addToolButtons() {
        btnInstall = UI.getButton(null, i18n("Install_this_report"), 'i',
                IconManager.getIcon(
                    IconManager.SCREEN_INSTALLCUSTOMREPORTS_INSTALL),
                UI.fp(this, "actionInstall"));
        addToolButton(btnInstall, true);
    }

    public void tableDoubleClicked() {
        actionInstall();
    }

    public void tableClicked() {
    }

    public void actionInstall() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Grab the report
        InstallableReport r = (InstallableReport) reports.get(id);

        // Install it
        try {
            CustomReport cr = new CustomReport();
            cr.openRecordset("ID = 0");
            cr.addNew();
            cr.setTitle(r.name);
            cr.setCategory(r.category);
            cr.setDescription(r.description);
            cr.setSQLCommand(r.sql);
            cr.setHTMLBody(r.html);
            cr.setOmitHeaderFooter(new Integer(0));
            cr.setOmitCriteria(new Integer(0));
            cr.save(Global.currentUserName);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Remove the current line from the table
        reports.remove(id);
        updateList();

        // Tell the main menu to update since this routine is
        // usually called following a change
        Global.mainForm.refreshCustomReports();
    }
}


class InstallableReport {
    public String name;
    public String category;
    public String database;
    public String description;
    public String locale;
    public String sql;
    public String html;
}
