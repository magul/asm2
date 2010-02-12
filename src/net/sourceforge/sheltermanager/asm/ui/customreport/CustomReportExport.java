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
import net.sourceforge.sheltermanager.asm.reports.CustomReportExecute;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.TablePrefs;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Vector;


/**
 * Allows a report to be generated and the data to be exported as a CSV file,
 * and to export a CSV file and open it with the registered CSV file viewer.
 *
 * @author Robin Rawson-Tetley
 */
public class CustomReportExport extends ASMView {
    private UI.Button btnGenCSV;
    private UI.Button btnView;

    /** Creates new form ViewCustomReports */
    public CustomReportExport() {
        init(Global.i18n("uicustomreport", "export_custom_report_data"),
            IconManager.getIcon(IconManager.SCREEN_EXPORTCUSTOMREPORTDATA),
            "uicustomreport");
        updateList();
    }

    public void tableDoubleClicked() {
        actionGenCSV();
    }

    public void tableClicked() {
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnGenCSV;
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public boolean formClosing() {
        return false;
    }

    /**
     * Refreshes the list with the current set of diary tasks on the system.
     */
    public void updateList() {
        CustomReport cr = new CustomReport();

        // Get the data
        cr.openRecordset("ID > 0 ORDER BY Title");

        // How many of those are actually custom reports?
        int totCustom = 0;

        try {
            while (!cr.getEOF()) {
                if (!cr.getSQLCommand().startsWith("0")) {
                    totCustom++;
                }

                cr.moveNext();
            }

            cr.moveFirst();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[totCustom][3];

        // Create an array of headers for the table
        String[] columnheaders = { i18n("Title"), i18n("category") };

        // Build the data
        int i = 0;

        try {
            while (!cr.getEOF()) {
                if (!cr.getSQLCommand().startsWith("0")) {
                    datar[i][0] = cr.getTitle();
                    datar[i][1] = Utils.nullToEmptyString(cr.getCategory());
                    datar[i][2] = cr.getID().toString();
                    i++;
                }

                cr.moveNext();
            }

            cr.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 2);
    }

    /**
     * Generates the CSV file from the report
     */
    private void generateCSV(CustomReport cr, File csv) {
        try {
            Global.logDebug("Substituting SQL tags in report",
                "ExportCustomReportData.generateCSV");

            String sql = CustomReportExecute.substituteSQLTagsForSQL(cr.getSQLCommand());

            Global.logDebug("Executing " + sql,
                "ExportCustomReportData.generateCSV");

            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset(sql, "animal");
            Global.logDebug("Got " + rs.getRecordCount() +
                " results, constructing CSV.",
                "ExportCustomReportData.generateCSV");

            // Build the CSV header
            FileOutputStream o = new FileOutputStream(csv);
            StringBuffer h = new StringBuffer();

            for (int i = 1; i <= rs.getFieldCount(); i++) {
                if (i > 1) {
                    h.append(",");
                }

                h.append("\"");
                h.append(rs.getFieldName(i));
                h.append("\"");
            }

            h.append("\n");
            o.write(h.toString().getBytes());
            Global.logDebug("Writing CSV header: " + h.toString(),
                "ExportCustomReportData.generateCSV");

            // Now build the actual CSV
            StringBuffer c = new StringBuffer();

            while (!rs.getEOF()) {
                for (int i = 1; i <= rs.getFieldCount(); i++) {
                    if (i > 1) {
                        c.append(",");
                    }

                    c.append("\"");
                    c.append(rs.getField(rs.getFieldName(i)));
                    c.append("\"");
                }

                c.append("\n");
                rs.moveNext();
            }

            Global.logDebug("Writing CSV file.",
                "ExportCustomReportData.generateCSV");
            o.write(c.toString().getBytes());
            o.flush();
            o.close();
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }
    }

    public void addToolButtons() {
        btnGenCSV = UI.getButton(null, i18n("generate_a_csv_file"), 'n',
                IconManager.getIcon(
                    IconManager.SCREEN_EXPORTCUSTOMREPORTDATA_GENCSV),
                UI.fp(this, "actionGenCSV"));
        addToolButton(btnGenCSV, true);

        btnView = UI.getButton(null,
                i18n("Create_a_csv_file_and_view_in_the_default_CSV_editor"),
                'e',
                IconManager.getIcon(
                    IconManager.SCREEN_EXPORTCUSTOMREPORTDATA_GENANDVIEWCSV),
                UI.fp(this, "actionView"));
        addToolButton(btnView, true);
    }

    public void actionView() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        CustomReport cr = null;

        try {
            // Get the record for the custom report
            cr = new CustomReport();
            cr.openRecordset("ID = " + id);

            // Make a file to put the CSV in and generate it
            String filename = Global.tempDirectory + File.separator +
                cr.getTitle().replace(' ', '_') + ".csv";
            File f = new File(filename);
            generateCSV(cr, f);

            // Open it
            FileTypeManager.shellExecute(filename);
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        } finally {
            if (cr != null) {
                cr.free();
            }
        }
    }

    public void actionGenCSV() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        CustomReport cr = null;

        try {
            // Get the record for the custom report
            cr = new CustomReport();
            cr.openRecordset("ID = " + id);

            // Make a file to put the CSV in and generate it
            String filename = Global.tempDirectory + File.separator +
                cr.getTitle().replace(' ', '_') + ".csv";
            File f = new File(filename);
            generateCSV(cr, f);

            // Tell the user
            Global.logInfo(i18n("CSV_file_generated_at") + filename,
                "ExportCustomReportData.btnGenCSVActionPerformed");
            Dialog.showInformation(i18n("CSV_file_generated_at") + filename,
                i18n("CSV_generation_complete"));
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        } finally {
            if (cr != null) {
                cr.free();
            }
        }
    }
}
