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

import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.db.AutoDBUpdates;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.TableData;
import net.sourceforge.sheltermanager.asm.ui.ui.TableRow;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;


/**
 * Gets additional reports from the sheltermanager website and allows
 * installation into the database.
 *
 * @author Robin Rawson-Tetley
 */
public class GetReports extends ASMView {
    private final static String REPORTS_URL = System.getProperty("asm.reportsurl",
            "http://www.sheltermanager.com/repo/reports.txt");
    private UI.Button btnInstall;
    private UI.CheckBox chkMyLocale;
    private ArrayList<InstallableReport> reports = null;

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
        v.add(chkMyLocale);
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

    public synchronized void updateListThread() {
        try {
            if (reports == null) {
                // Open the list of custom reports so we can omit reports
                // that are already installed
                CustomReport cr = new CustomReport();
                cr.openRecordset("ID > 0 ORDER BY Title");

                // Grab the reports
                reports = new ArrayList<InstallableReport>();

                startThrobber();

                String rs = Utils.getURL(REPORTS_URL);
                stopThrobber();

                String[] reps = Utils.split(rs, "&&&");

                for (int i = 0; i < reps.length; i++) {
                    String[] b = Utils.split(reps[i], "###");

                    InstallableReport r = new InstallableReport();
                    r.name = b[0].trim();
                    Global.logDebug("Found installable report: " + r.name,
                        "GetReports.updateListThread");

                    // Skip if we've installed this one already
                    boolean installed = false;

                    if (cr.getRecordCount() > 0) {
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
                    }

                    if (installed) {
                        continue;
                    }

                    r.category = b[1].trim();
                    r.database = b[2].trim();
                    r.description = b[3].trim();
                    r.locale = b[4].trim();

                    // Skip if the database version is too new for us
                    int dbver = 0;

                    if (r.database.indexOf("/") != -1) {
                        dbver = Integer.parseInt(r.database.substring(0,
                                    r.database.indexOf("/")));
                    }

                    if (dbver > AutoDBUpdates.LATEST_DB_VERSION) {
                        Global.logDebug(
                            "Skipping, database too old for this report (" +
                            r.database + ")", "GetReports.updateListThread");

                        continue;
                    }

                    // Skip if the database type is wrong
                    if (r.database.indexOf("Any") == -1) {
                        if (DBConnection.DBType == DBConnection.HSQLDB) {
                            if (r.database.indexOf("HSQLDB") == -1) {
                                Global.logDebug("Skipping, report not HSQLDB",
                                    "GetReports.updateListThread");

                                continue;
                            }
                        }

                        if (DBConnection.DBType == DBConnection.MYSQL) {
                            if (r.database.indexOf("MySQL") == -1) {
                                Global.logDebug("Skipping, report not MySQL",
                                    "GetReports.updateListThread");

                                continue;
                            }
                        }

                        if (DBConnection.DBType == DBConnection.POSTGRESQL) {
                            if (r.database.indexOf("PostgreSQL") == -1) {
                                Global.logDebug("Skipping, report not HSQLDB",
                                    "GetReports.updateListThread");

                                continue;
                            }
                        }
                    }

                    r.sql = b[5].trim();
                    r.html = b[6].trim();

                    if (b.length == 8) {
                        r.subreports = b[7].trim();
                    }

                    reports.add(r);
                }

                // Done with custom report list now
                cr.free();

                // Sort our final list of reports
                Collections.sort(reports);
            }

            // Create an array of headers for the table
            String[] columnheaders = {
                    i18n("Type"), i18n("Title"), i18n("category"),
                    i18n("Locale"), i18n("Description")
                };

            // Build the data
            TableData td = new TableData();

            for (int i = 0; i < reports.size(); i++) {
                TableRow row = new TableRow(6);
                InstallableReport r = (InstallableReport) reports.get(i);

                // Skip if the locale isn't ours and the filter box is checked
                if ((Global.settings_Locale.indexOf(r.locale) == -1) &&
                        chkMyLocale.isSelected()) {
                    Global.logDebug("Skipping, wrong locale (" + r.locale +
                        ")", "GetReports.updateListThread");

                    continue;
                }

                row.set(0, CustomReport.getReportType(r.html));
                row.set(1, r.name);
                row.set(2, r.category);
                row.set(3, r.locale);
                row.set(4, r.description);
                row.set(5, Integer.toString(i));

                td.add(row);
            }

            setTableData(columnheaders, td.toTableData(), td.size(), 5);
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
        chkMyLocale = UI.getCheckBox(i18n("my_locale", Global.settings_Locale),
                null, UI.fp(this, "updateList"));
        chkMyLocale.setOpaque(false);
        chkMyLocale.setSelected(true);
        addToolButton(chkMyLocale, false);
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

            if (AuditTrail.enabled()) {
                AuditTrail.create("customreport", r.name);
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Handle any subreports attached to this report
        if (r.subreports != null) {
            String[] sr = Utils.split(r.subreports, "+++");

            for (int i = 0; i < sr.length;) {
                String srtitle = sr[i++].trim();
                String srsql = sr[i++].trim();
                String srhtml = sr[i++].trim();

                try {
                    DBConnection.executeAction(
                        "DELETE FROM customreport WHERE Title Like '" +
                        srtitle.replace('\'', '`') + "'");

                    CustomReport cr = new CustomReport();
                    cr.openRecordset("ID = 0");
                    cr.addNew();
                    cr.setTitle(srtitle);
                    cr.setCategory(r.category);
                    cr.setDescription(r.description);
                    cr.setSQLCommand(srsql);
                    cr.setHTMLBody(srhtml);
                    cr.setOmitHeaderFooter(new Integer(1));
                    cr.setOmitCriteria(new Integer(1));
                    cr.save(Global.currentUserName);

                    if (AuditTrail.enabled()) {
                        AuditTrail.create("customreport", srtitle);
                    }
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }
            }
        }

        // Remove the current line from the table
        reports.remove(id);
        updateList();

        // Tell the main menu to update since this routine is
        // usually called following a change
        Global.mainForm.refreshCustomReports();
        Global.mainForm.refreshMailMerge();
    }
}


class InstallableReport implements Comparable {
    public String name;
    public String category;
    public String database;
    public String description;
    public String locale;
    public String sql;
    public String html;
    public String subreports;

    public int compareTo(Object o) {
        return name.compareTo(((InstallableReport) o).name);
    }

    public boolean equals(Object o) {
        return name.equals(((InstallableReport) o).name);
    }
}
