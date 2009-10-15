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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import swingwt.awt.Component;

import swingwtx.swing.Icon;

import java.util.Vector;


/**
 * Superclass of all search forms
 * @author Robin Rawson-Tetley
 */
public abstract class ASMFind extends ASMForm {
    protected UI.Button btnSearch;
    protected UI.Button btnOpen;
    protected UI.Table table;
    protected UI.ToolBar leftbar;
    protected UI.ToolBar toolbar;
    protected UI.Panel criteria;
    protected int criteriacols = 2;
    protected boolean hasleftbar = false;
    protected boolean selectionMode = false;
    protected Vector disableButtons = new Vector();
    protected StringBuffer sqlCriteria = null;

    public abstract void itemSelected(int id);

    public abstract void initCriteria(UI.Panel p);

    public abstract void initToolbar();

    public abstract void initLeftbar();

    public abstract void runSearch();

    public void init(String title, Icon icon, String i18nKey, int criteriacols,
        boolean hasleftbar, boolean selectionMode) {
        this.hasleftbar = hasleftbar;
        this.selectionMode = selectionMode;
        this.i18nKey = i18nKey;
        this.criteriacols = criteriacols;
        setTitle(title);
        setFrameIcon(icon);
        initComponents();
        initCriteria(criteria);
        initToolbar();

        if (hasleftbar) {
            initLeftbar();
        }

        setSecurity();
        registerTabOrder(getTabOrder(), (Component) getDefaultFocusedComponent());
        updateButtons(false);
    }

    public void addSqlCriteria(String expression) {
        if (sqlCriteria == null) {
            sqlCriteria = new StringBuffer();
        }

        if (sqlCriteria.length() != 0) {
            sqlCriteria.append(" AND ");
        }

        sqlCriteria.append(expression);
    }

    public String getSqlCriteria() {
        return sqlCriteria.toString();
    }

    public void addLeftbarItem(UI.Button b, boolean disableIfNoSelection) {
        if (!hasleftbar) {
            return;
        }

        leftbar.add(b);

        if (disableIfNoSelection) {
            disableButtons.add(b);
        }
    }

    public void addToolbarItem(UI.Button b, boolean disableIfNoSelection) {
        toolbar.add(b);

        if (disableIfNoSelection) {
            disableButtons.add(b);
        }
    }

    public void initComponents() {
        toolbar = UI.getToolBar();

        UI.Panel top = UI.getPanel(UI.getBorderLayout());
        criteria = UI.getPanel(UI.getGridLayout(criteriacols));
        top.add(toolbar, UI.BorderLayout.NORTH);
        top.add(criteria, UI.BorderLayout.CENTER);

        btnSearch = UI.getButton(UI.messageSearch(), null, 's',
                UI.iconSearch(), UI.fp(this, "actionSearch"));
        btnOpen = UI.getButton((selectionMode ? UI.messageSelect()
                                              : UI.messageOpen()), null,
                (selectionMode ? 'l' : 'o'), UI.iconOpen(),
                UI.fp(this, "actionDoubleClick"));
        addToolbarItem(btnSearch, false);
        addToolbarItem(btnOpen, true);
        this.getRootPane().setDefaultButton(btnSearch);

        UI.Panel main = UI.getPanel(UI.getBorderLayout());

        leftbar = UI.getToolBar(true);

        if (hasleftbar) {
            main.add(leftbar, UI.BorderLayout.WEST);
        }

        table = UI.getTable(UI.fp(this, "actionClick"),
                UI.fp(this, "actionDoubleClick"), hasleftbar ? leftbar : null);
        main.add(table, UI.BorderLayout.CENTER);

        add(top, UI.BorderLayout.NORTH);
        add(main, UI.BorderLayout.CENTER);
    }

    public UI.Table getTable() {
        return table;
    }

    public void setTableData(String[] cols, String[][] data, int rows, int IDcol) {
        table.setTableData(cols, data, rows, IDcol);
        updateButtons(false);
        this.getRootPane().setDefaultButton(btnOpen);
    }

    public void setTableData(String[] cols, String[][] data, int rows,
        int maxcols, int IDcol) {
        table.setTableData(cols, data, rows, maxcols, IDcol);
        updateButtons(false);
        this.getRootPane().setDefaultButton(btnOpen);
    }

    public void updateButtons(boolean enabled) {
        for (int i = 0; i < disableButtons.size(); i++) {
            ((UI.Button) disableButtons.get(i)).setEnabled(enabled);
        }

        setSecurity();
    }

    public void actionClick() {
        updateButtons(table.getSelectedRow() != -1);
    }

    public void actionDoubleClick() {
        int id = table.getSelectedID();

        if (id != -1) {
            UI.cursorToWait();
            itemSelected(id);
            UI.cursorToPointer();
        }
    }

    public void loadData() {
        // Not relevant for search
    }

    public boolean saveData() {
        // Not relevant for search
        return true;
    }

    public void actionSearch() {
        new Thread() {
                public void run() {
                    // Disable searching
                    btnSearch.setEnabled(false);

                    // Make the cursor an hourglass
                    UI.cursorToWait();

                    // Tell user we are searching
                    setStatusText(Global.i18n("uilostandfound",
                            "Searching_the_database,_please_wait..."));

                    // Clear the sql criteria
                    sqlCriteria = new StringBuffer();

                    // Do the search
                    runSearch();

                    // Output how many there were
                    int i = table.getRowCount();

                    if (i > 0) {
                        String no = Integer.toString(i);

                        if (i == Global.getRecordSearchLimit()) {
                            no += "+";
                        }

                        setStatusText(Global.i18n("uilostandfound",
                                "search_complete", no));
                    } else {
                        setStatusText(Global.i18n("uilostandfound",
                                "Search_complete_-_no_matches_found."));
                    }

                    // Reset the status meter back to 0
                    resetStatusBar();

                    // Enable searching
                    btnSearch.setEnabled(true);

                    // If there were some results, highlight the first row in the table
                    if (i > 0) {
                        table.changeSelection(0, 0, false, false);
                        updateButtons(true);
                    }

                    UI.cursorToPointer();
                }
            }.start();
    }
}
