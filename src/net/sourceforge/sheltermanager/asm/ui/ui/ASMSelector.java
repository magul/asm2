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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.util.Vector;


/**
 * Base class for embedded forms with a toolbar around a table.
 * Also has an optional panel below the toolbar
 */
public abstract class ASMSelector extends UI.Panel {
    /** The default i18n key to use for this tableview */
    protected String i18nKey = "";
    protected UI.ToolBar toolbar = null;
    protected UI.Table table = null;
    protected UI.Panel toppanel = null;
    protected Vector selectionButtons = new Vector();
    protected boolean hasTopPanel = false;
    protected boolean multiselect = true;
    protected ASMCellRenderer renderer = null;
    protected boolean disableDoubleClick = false;

    public abstract Vector getTabOrder();

    public abstract Object getDefaultFocusedComponent();

    public abstract void addToolButtons();

    public abstract void setSecurity();

    public abstract void setLink(int linkID, int linkType);

    public abstract boolean hasData();

    public abstract void updateList();

    public void init(String i18nKey) {
        init(i18nKey, false);
    }

    public void init(String i18nKey, boolean hasTopPanel) {
        init(i18nKey, hasTopPanel, false, null);
    }

    public void init(String i18nKey, boolean hasTopPanel, boolean multiselect,
        ASMCellRenderer renderer) {
        this.i18nKey = i18nKey;
        this.hasTopPanel = hasTopPanel;
        this.multiselect = multiselect;
        this.renderer = renderer;
        initComponents();
        addToolButtons();
        registerTabOrder(getTabOrder(), getDefaultFocusedComponent());
        updateToolButtons(false);
    }

    /**
     * Adds a toolbar button
     * @param b The button to add
     * @param disableNoSelection True if the button should be disabled if
     *        nothing in the table is selected
     */
    public UI.Button addToolButton(UI.Button b, boolean disableNoSelection) {
        toolbar.add(b);

        if (disableNoSelection) {
            selectionButtons.add(b);
        }

        return b;
    }

    public UI.Table getTable() {
        return table;
    }

    public void setTableData(String[] columns, String[][] data, int rows,
        int idColumn) {
        // Truncate any fields that are longer than 80 chars
        // to stop them shoving other fields off screen
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                String s = data[row][col];

                if ((s != null) && (s.length() >= 80)) {
                    data[row][col] = s.substring(0, 78) + "...";
                }
            }
        }

        table.setTableData(columns, data, rows, idColumn);
        updateToolButtons(false);
    }

    private void updateToolButtons(boolean enable) {
        for (int i = 0; i < selectionButtons.size(); i++) {
            ((UI.Button) selectionButtons.get(i)).setEnabled(enable);
        }

        setSecurity();
    }

    protected void registerTabOrder(Vector components, Object focusedComponent) {
        if ((components == null) || (focusedComponent == null)) {
            return;
        }

        UI.registerTabOrder(components, this, focusedComponent);
    }

    protected void unregisterTabOrder() {
        UI.unregisterTabOrder(this);
    }

    public void dispose() {
        unregisterTabOrder();
        super.dispose();
    }

    public UI.Panel getTopPanel() {
        return toppanel;
    }

    public void initComponents() {
        // Layout
        setLayout(UI.getBorderLayout());

        // Toolbar and panel
        UI.Panel top = (UI.Panel) UI.getPanel(UI.getBorderLayout());
        toolbar = UI.getToolBar();
        top.add(toolbar, UI.BorderLayout.NORTH);

        if (hasTopPanel) {
            toppanel = (UI.Panel) top.add(UI.getPanel(UI.getFlowLayout()));
        }

        add(top, UI.BorderLayout.NORTH);

        // Table
        table = UI.getTable(UI.fp(this, "tableClick"),
                UI.fp(this, "tableDoubleClick"), toolbar, renderer, multiselect);
        //add(table, UI.BorderLayout.CENTER);
        UI.addComponent(this, table);
    }

    public void tableClick() {
        updateToolButtons(table.getSelectedRow() != -1);
        tableClicked();
    }

    public void tableDoubleClick() {
        updateToolButtons(table.getSelectedRow() != -1);

        if (!disableDoubleClick) {
            tableDoubleClicked();
        }
    }

    public abstract void tableClicked();

    public abstract void tableDoubleClicked();

    public String i18n(String key) {
        return Global.i18n(i18nKey, key);
    }

    public String i18n(String key, String arg1) {
        return Global.i18n(i18nKey, key, arg1);
    }

    public String i18n(String key, String arg1, String arg2) {
        return Global.i18n(i18nKey, key, arg1, arg2);
    }

    public String i18n(String key, String arg1, String arg2, String arg3) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3);
    }

    public String i18n(String key, String arg1, String arg2, String arg3,
        String arg4) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3, arg4);
    }

    public String i18n(String key, String arg1, String arg2, String arg3,
        String arg4, String arg5) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3, arg4, arg5);
    }

    public String i18n(String key, String arg1, String arg2, String arg3,
        String arg4, String arg5, String arg6) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3, arg4, arg5, arg6);
    }
}
