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
package net.sourceforge.sheltermanager.asm.ui.customreport;

import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.CustomReportExecute;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * Handles editing of custom reports
 *
 * @author Robin Rawson-Tetley
 */
public class CustomReportEdit extends ASMForm {
    private CustomReportView parent = null;
    private CustomReport cr = null;
    private UI.Button btnAutogenHTML;
    private UI.Button btnCheckSQL;
    private UI.Button btnSave;
    private UI.ComboBox cboCategory;
    private UI.ComboBox cboTables;
    private UI.CheckBox chkOmitHeaderFooter;
    private UI.CheckBox chkOmitCriteria;
    private UI.Table tblFields;
    private UI.TextArea txtHTML;
    private UI.TextArea txtSQL;
    private UI.TextField txtTitle;
    private String audit = null;

    /** Creates new form EditCustomReport */
    public CustomReportEdit(CustomReportView parent) {
        this.parent = parent;
        init(Global.i18n("uicustomreport", "Edit_Custom_Report"),
            IconManager.getIcon(IconManager.SCREEN_EDITCUSTOMREPORT),
            "uicustomreport");
    }

    public void setSecurity() {
        btnSave.setEnabled(Global.currentUserObject.getSecChangeCustomReports());
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtTitle);
        ctl.add(cboCategory);
        ctl.add(chkOmitHeaderFooter);
        ctl.add(chkOmitCriteria);
        ctl.add(txtSQL);
        ctl.add(cboTables);
        ctl.add(tblFields);
        ctl.add(txtHTML);
        ctl.add(btnSave);
        ctl.add(btnCheckSQL);
        ctl.add(btnAutogenHTML);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtTitle;
    }

    public void dispose() {
        cr.free();
        parent = null;
        cr = null;
        super.dispose();
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public void openForEdit(CustomReport cr) {
        this.cr = cr;

        try {
            this.txtTitle.setText(cr.getTitle());
            this.cboCategory.setSelectedItem(Utils.nullToEmptyString(
                    cr.getCategory()));
            this.txtSQL.setText(cr.getSQLCommand());
            this.txtHTML.setText(cr.getHTMLBody());
            this.setTitle(i18n("Edit_Custom_Report") + "-" + cr.getTitle());
            this.chkOmitHeaderFooter.setSelected(cr.getOmitHeaderFooter()
                                                   .intValue() == 1);
            this.chkOmitCriteria.setSelected(cr.getOmitCriteria().intValue() == 1);
            audit = UI.messageAudit(cr.getCreatedDate(), cr.getCreatedBy(),
                    cr.getLastChangedDate(), cr.getLastChangedBy());
        } catch (Exception e) {
        }
    }

    /**
     * Given a string, removes all markers that start and end with a dollar $
     * sign and swaps them for a 0 to make sure it works when checking syntax
     * and building HTML
     */
    private String removeTags(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.substring(i, i + 1).equals("$")) {
                int tagstart = i;
                int tagend = s.indexOf("$", i + 1);

                // VAR tags disappear, so don't need a value
                if (s.substring(tagstart, tagstart + 4).equals("$VAR")) {
                    s = s.substring(0, tagstart) +
                        s.substring(tagend + 1, s.length());
                } else {
                    s = s.substring(0, tagstart) + "0" +
                        s.substring(tagend + 1, s.length());
                }
            }
        }

        return s;
    }

    public void openForNew() {
        try {
            this.cr = new CustomReport();
            cr.openRecordset("ID = 0");
            cr.addNew();
        } catch (Exception e) {
            Dialog.showError(i18n("Unable_to_create_new_custom_report:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        UI.Panel top = new UI.Panel(UI.getBorderLayout());
        UI.Panel fields = new UI.Panel(UI.getGridLayout(4));
        UI.Panel cols = new UI.Panel(UI.getGridLayout(3));
        UI.Panel tables = new UI.Panel(UI.getBorderLayout());

        // Toolbar =======================
        UI.ToolBar t = UI.getToolBar();

        btnSave = (UI.Button) t.add(UI.getButton(null, i18n("Save_changes"),
                    's',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITCUSTOMREPORT_SAVE),
                    UI.fp(this, "actionSave")));

        btnCheckSQL = (UI.Button) t.add(UI.getButton(null,
                    i18n("Syntax_check_the_SQL"), 'c',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITCUSTOMREPORT_CHECKSQL),
                    UI.fp(this, "actionCheckSQL")));

        btnAutogenHTML = (UI.Button) t.add(UI.getButton(null,
                    i18n("Auto-generate_HTML_from_SQL"), 'h',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITCUSTOMREPORT_GENERATEHTML),
                    UI.fp(this, "actionGenerateHTML")));

        // Fields =========================
        txtTitle = (UI.TextField) UI.addComponent(fields,
                i18n("Report_Title:_"),
                UI.getTextField(i18n("the_title_for_your_report")));

        cboCategory = UI.getCombo("SELECT DISTINCT Category FROM customreport " +
                "ORDER BY Category", "Category");
        cboCategory.setEditable(true);
        UI.addComponent(fields, i18n("category"), cboCategory);

        chkOmitHeaderFooter = (UI.CheckBox) fields.add(UI.getCheckBox(i18n("omit_headerfooter"),
                    i18n("omit_headerfooter_tt")));
        chkOmitCriteria = (UI.CheckBox) fields.add(UI.getCheckBox(i18n("omit_criteria"),
                    i18n("omit_criteria_tt")));

        top.add(t, UI.BorderLayout.NORTH);
        top.add(fields, UI.BorderLayout.CENTER);
        add(top, UI.BorderLayout.NORTH);

        // 3 Body columns ==========================

        // SQL
        txtSQL = (UI.TextArea) UI.addComponent(cols,
                UI.getTextArea(i18n("the_sql_command")));

        // Table selector and display
        cboTables = UI.getCombo((Vector) null, UI.fp(this, "changedTable"));
        cboTables.addItem("additional");
        cboTables.addItem("additionalfield");
        cboTables.addItem("adoption");
        cboTables.addItem("animal");
        cboTables.addItem("animaldiet");
        cboTables.addItem("animalfound");
        cboTables.addItem("animallitter");
        cboTables.addItem("animallost");
        cboTables.addItem("animalmedical");
        cboTables.addItem("animalmedicaltreatment");
        cboTables.addItem("animalname");
        cboTables.addItem("animaltype");
        cboTables.addItem("animalvaccination");
        cboTables.addItem("animalwaitinglist");
        cboTables.addItem("basecolour");
        cboTables.addItem("breed");
        cboTables.addItem("configuration");
        cboTables.addItem("customreport");
        cboTables.addItem("deathreason");
        cboTables.addItem("diary");
        cboTables.addItem("diarytaskdetail");
        cboTables.addItem("diarytaskhead");
        cboTables.addItem("diet");
        cboTables.addItem("donationtype");
        cboTables.addItem("entryreason");
        cboTables.addItem("internallocation");
        cboTables.addItem("lkcoattype");
        cboTables.addItem("lksdiarylink");
        cboTables.addItem("lksfieldlink");
        cboTables.addItem("lksfieldtype");
        cboTables.addItem("lksloglink");
        cboTables.addItem("lksmovementtype");
        cboTables.addItem("lksposneg");
        cboTables.addItem("lksex");
        cboTables.addItem("lksize");
        cboTables.addItem("lksmedialink");
        cboTables.addItem("lksyesno");
        cboTables.addItem("lksynun");
        cboTables.addItem("lkurgency");
        cboTables.addItem("log");
        cboTables.addItem("logtype");
        cboTables.addItem("media");
        cboTables.addItem("medicalprofile");
        cboTables.addItem("owner");
        cboTables.addItem("ownerdonation");
        cboTables.addItem("ownervoucher");
        cboTables.addItem("settings");
        cboTables.addItem("species");
        cboTables.addItem("vaccinationtype");
        cboTables.addItem("voucher");
        tables.add(cboTables, UI.BorderLayout.NORTH);
        tblFields = UI.getTable(null, UI.fp(this, "fieldDoubleClicked"));
        UI.addComponent(tables, tblFields);
        cols.add(tables);

        // HTML
        txtHTML = (UI.TextArea) UI.addComponent(cols,
                UI.getTextArea(i18n("the_html_for_your_report")));

        add(cols, UI.BorderLayout.CENTER);
    }

    public void changedTable() {
        // Read the list of fields for the selected table
        String tableName = (String) cboTables.getSelectedItem();
        SQLRecordset rs = null;
        Vector fields = null;

        try {
            UI.cursorToWait();
            rs = new SQLRecordset();
            tableName = tableName.toLowerCase();

            // Special case tables
            if (tableName.equals("additional")) {
                rs.openRecordset("SELECT * FROM additional WHERE LinkID = 0",
                    tableName);
            } else if (tableName.equals("configuration")) {
                rs.openRecordset("SELECT * FROM configuration WHERE ItemName Like ''",
                    tableName);
            } else {
                rs.openRecordset("SELECT * FROM " + tableName + " WHERE ID=0",
                    tableName);
            }

            // Get the fields and add them to the list
            String[] cols = new String[] { "Fields" };
            String[][] rows = new String[rs.getFieldCount()][1];

            for (int i = 1; i <= rs.getFieldCount(); i++) {
                rows[i - 1][0] = rs.getFieldName(i);
            }

            tblFields.setTableData(cols, rows, rs.getFieldCount(), 1, 0);
        } catch (Exception e) {
            UI.cursorToPointer();
            Global.logException(e, getClass());
        } finally {
            try {
                rs.free();
                fields.removeAllElements();
            } catch (Exception e) {
            }

            rs = null;
            fields = null;
            UI.cursorToPointer();
        }
    }

    public void fieldDoubleClicked() {
        if (tblFields.getSelectedRow() == -1) {
            return;
        }

        String field = tblFields.getModel()
                                .getValueAt(tblFields.getSelectedRow(), 0)
                                .toString();
        txtSQL.append(field + ", ");
    }

    public void actionGenerateHTML() {
        StringBuffer buf = new StringBuffer();
        SQLRecordset rs = null;
        String[] queries = null;

        try {
            UI.cursorToWait();

            queries = Utils.split(removeTags(txtSQL.getText()), ";");

            // Make sure the last query is a SELECT or (SELECT for UNION
            if ((!queries[queries.length - 1].toLowerCase().trim()
                                                 .startsWith("select")) &&
                    (!queries[queries.length - 1].toLowerCase().trim()
                                                     .startsWith("(select"))) {
                Dialog.showError(Global.i18n("reports",
                        "there_must_be_at_least_one_select_query_and_it_must_be_the_last_to_run"));

                return;
            }

            // Loop through the queries, executing them/running where necessary
            for (int i = 0; i < queries.length; i++) {
                // If it's an action query, execute it
                if (queries[i].trim().toLowerCase().startsWith("create") ||
                        queries[i].trim().toLowerCase().startsWith("drop") ||
                        queries[i].trim().toLowerCase().startsWith("insert") ||
                        queries[i].trim().toLowerCase().startsWith("update") ||
                        queries[i].trim().toLowerCase().startsWith("delete")) {
                    DBConnection.executeAction(queries[i]);
                } else {
                    rs = new SQLRecordset();
                    rs.openRecordset(removeTags(queries[i]), "animal");
                }
            }

            // Build header
            buf.append("$$HEADER\n<table border=1><tr>\n");

            // Create an entry in the table for each field
            for (int i = 1; i <= rs.getFieldCount(); i++) {
                buf.append("<td>");
                buf.append("<b>" + rs.getFieldName(i) + "</b></td>\n");
            }

            buf.append("</tr>\nHEADER$$\n\n");

            // Build body
            buf.append("$$BODY<tr>\n");

            // Create an entry in the table for each field
            for (int i = 1; i <= rs.getFieldCount(); i++) {
                buf.append("<td>");
                buf.append("$" + rs.getFieldName(i) + "</td>\n");
            }

            buf.append("</tr>\nBODY$$\n\n");

            // Build footer
            buf.append("$$FOOTER\n</table>\nFOOTER$$");

            txtHTML.setText(buf.toString());
        } catch (Exception e) {
            UI.cursorToPointer();
            Dialog.showError(i18n("Unable_to_generate_HTML:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        } finally {
            try {
                rs.free();
            } catch (Exception e) {
            }

            // Clean up temporary tables
            for (int i = 0; i < queries.length; i++) {
                if (queries[i].trim().toLowerCase()
                                  .startsWith("create temporary")) {
                    CustomReportExecute.dropTemporaryTable(queries[i]);
                }
            }

            rs = null;
            buf = null;
            UI.cursorToPointer();
        }
    }

    public void actionCheckSQL() {
        // Build a database query from the SQL queries given and
        // see if it executes.
        SQLRecordset rs = null;
        String[] queries = null;

        try {
            UI.cursorToWait();

            // Get the complete SQL and substitute any $USER$ tags
            String sql = txtSQL.getText();
            sql = Utils.replace(sql, "$USER$", Global.currentUserName);

            queries = Utils.split(removeTags(sql), ";");

            // Make sure the last query is a SELECT or (SELECT for UNION
            if (!queries[queries.length - 1].toLowerCase().trim()
                                                .startsWith("select") &&
                    !queries[queries.length - 1].toLowerCase().trim()
                                                    .startsWith("(select")) {
                Dialog.showError(Global.i18n("reports",
                        "there_must_be_at_least_one_select_query_and_it_must_be_the_last_to_run"));

                return;
            }

            // Loop through the queries, executing them/running where necessary
            for (int i = 0; i < queries.length; i++) {
                // If it's an action query, execute it
                if (queries[i].trim().toLowerCase().startsWith("create") ||
                        queries[i].trim().toLowerCase().startsWith("drop") ||
                        queries[i].trim().toLowerCase().startsWith("insert") ||
                        queries[i].trim().toLowerCase().startsWith("update") ||
                        queries[i].trim().toLowerCase().startsWith("delete")) {
                    DBConnection.executeAction(queries[i]);
                } else {
                    rs = new SQLRecordset();
                    rs.openRecordset(removeTags(queries[i]), "animal");
                }
            }

            UI.cursorToPointer();
            Dialog.showInformation(i18n("SQL_is_syntactically_correct."),
                i18n("Ok"));
        } catch (Exception e) {
            UI.cursorToPointer();
            Dialog.showError(i18n("There_is_an_error_in_your_SQL:\n") +
                e.getMessage());
        } finally {
            rs.free();
            rs = null;

            // Clean up temporary tables
            for (int i = 0; i < queries.length; i++) {
                if (queries[i].trim().toLowerCase()
                                  .startsWith("create temporary")) {
                    CustomReportExecute.dropTemporaryTable(queries[i]);
                }
            }
        }
    }

    public void actionSave() {
        // Save values
        try {
            cr.setTitle(txtTitle.getText());
            cr.setCategory((String) cboCategory.getSelectedItem());
            cr.setSQLCommand(txtSQL.getText());
            cr.setHTMLBody(txtHTML.getText());
            cr.setOmitHeaderFooter(chkOmitHeaderFooter.isSelected()
                ? new Integer(1) : new Integer(0));
            cr.setOmitCriteria(chkOmitCriteria.isSelected() ? new Integer(1)
                                                            : new Integer(0));
            cr.save(Global.currentUserName);

            // Update parent
            parent.updateList();
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_saving_the_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
