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

import java.awt.Component;

import java.util.Vector;

import javax.swing.Icon;


/**
 * Base class for View forms with a toolbar around a table.
 * Also has an optional panel below the toolbar
 */
public abstract class ASMView extends ASMForm {
    protected UI.ToolBar toolbar = null;
    protected UI.Table table = null;
    protected UI.Panel toppanel = null;
    protected Vector selectionButtons = new Vector();
    protected boolean hasTopPanel = false;
    protected boolean multiselect = true;
    protected ASMCellRenderer renderer = null;

    public abstract Vector getTabOrder();

    public abstract Object getDefaultFocusedComponent();

    public abstract void addToolButtons();

    public abstract void setSecurity();

    public abstract void setLink(int linkID, int linkType);

    public abstract boolean hasData();

    public abstract void updateList();

    public void init(String title, Icon icon, String i18nKey) {
        init(title, icon, i18nKey, false);
    }

    public void init(String title, Icon icon, String i18nKey,
        boolean hasTopPanel) {
        init(title, icon, i18nKey, hasTopPanel, false, null);
    }

    public void init(String title, Icon icon, String i18nKey,
        boolean hasTopPanel, boolean multiselect, ASMCellRenderer renderer) {
        this.i18nKey = i18nKey;
        this.hasTopPanel = hasTopPanel;
        this.multiselect = multiselect;
        this.renderer = renderer;
        setTitle(title);
        setFrameIcon(icon);
        initComponents();
        addToolButtons();
        registerTabOrder(getTabOrder(), (Component) getDefaultFocusedComponent());
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
        table.setTableData(columns, data, rows, idColumn);
        updateToolButtons(false);
    }

    private void updateToolButtons(boolean enable) {
        for (int i = 0; i < selectionButtons.size(); i++) {
            ((UI.Button) selectionButtons.get(i)).setEnabled(enable);
        }

        setSecurity();
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
        UI.addComponent(this, table);
    }

    public void tableClick() {
        updateToolButtons(table.getSelectedRow() != -1);
        tableClicked();
    }

    public void tableDoubleClick() {
        updateToolButtons(table.getSelectedRow() != -1);
        tableDoubleClicked();
    }

    public abstract void tableClicked();

    public abstract void tableDoubleClicked();
}
