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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


public class SQLInterface extends ASMForm {
    UI.Table table = null;
    UI.TextArea sql = null;

    public SQLInterface() {
        init(Global.i18n("uisystem", "sql"),
            IconManager.getIcon(IconManager.SCREEN_SQLINTERFACE), "uisystem");
    }

    public void setSecurity() {
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(sql);

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return sql;
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.ToolBar t = UI.getToolBar();
        sql = UI.getTextArea();
        table = UI.getTable(UI.fp(this, "onClick"), UI.fp(this, "onDoubleClick"));
        t.add(UI.getButton(i18n("sql"), i18n("execute_sql"), 's',
                IconManager.getIcon(IconManager.SCREEN_SQLINTERFACE_EXECUTE),
                UI.fp(this, "execute")));

        add(t, UI.BorderLayout.NORTH);

        UI.Panel p = UI.getPanel(UI.getGridLayout(1));
        UI.addComponent(p, sql);
        UI.addComponent(p, table);
        add(p, UI.BorderLayout.CENTER);
    }

    public void execute() {
        // Split it into separate queries
        String[] queries = Utils.split(sql.getText(), ";");

        // Run each
        for (int i = 0; i < queries.length; i++) {
            try {
                String query = queries[i].trim();

                // Is it a select?
                if (query.toLowerCase().startsWith("select")) {
                    SQLRecordset r = new SQLRecordset();
                    r.openRecordset(query, "primarykey");

                    // Get the columns first
                    String[] columns = new String[r.getFieldCount()];

                    for (int x = 0; x < r.getFieldCount(); x++) {
                        columns[x] = r.getFieldName(x + 1);
                    }

                    // Now do the row data
                    String[][] data = new String[(int) r.getRecordCount()][columns.length];
                    int row = 0;

                    while (!r.getEOF()) {
                        for (int x = 0; x < columns.length; x++) {
                            Object o = (Object) r.getField(columns[x]);

                            if (o == null) {
                                data[row][x] = "null";
                            } else {
                                data[row][x] = o.toString();
                            }
                        }

                        row++;
                        r.moveNext();
                    }

                    // Get rid of any existing sort model in case the
                    // number of columns has changed
                    table.setSortModel(null);
                    table.setTableData(columns, data, (int) r.size(), 
                        1, columns.length);
                } else {
                    // It's an action query, run it
                    DBConnection.executeAction(query);
                    Dialog.showInformation(query + ": OK", "OK");
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
                Dialog.showError(e.getMessage(), i18n("sql"));
            }
        }
    }

    public void onClick() {
    }

    public void onDoubleClick() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }
}
