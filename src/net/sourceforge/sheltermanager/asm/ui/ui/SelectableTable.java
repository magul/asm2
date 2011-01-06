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

import java.awt.Color;
import java.awt.Component;

import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;


/**
 * Allows selections from a list of items.
 *
 * @author Robin Rawson-Tetley
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("serial")
public class SelectableTable extends JTable implements SelectableComponent {
    SelectableItem[] items = null;

    public void setItems(SelectableItem[] items) {
        this.items = items;
        setModel(new SelectableModel(items));
        getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        getColumn(0)
            .setCellRenderer(new SelectableRenderer(
                (SelectableModel) getModel()));
        getColumn(1)
            .setCellRenderer(new SelectableDisplayRenderer(
                (SelectableModel) getModel()));
    }

    public void setItems(List<SelectableItem> l) {
        SelectableItem[] its = new SelectableItem[l.size()];

        for (int i = 0; i < l.size(); i++)
            its[i] = (SelectableItem) l.get(i);

        setItems(its);
    }

    public SelectableItem[] getSelections() {
        return items;
    }

    public TableColumn getColumn(int index) {
        // TODO: Valid?
        return getColumn(getColumnName(index));
    }
}


@SuppressWarnings("serial")
class SelectableModel extends AbstractTableModel {
    private SelectableItem[] items = null;

    public SelectableModel(SelectableItem[] items) {
        this.items = items;
    }

    public Object getValueAt(int arg0, int arg1) {
        // Selected
        if (arg1 == 0) {
            return new Boolean(items[arg0].isSelected());
        }

        // Display
        if (arg1 == 1) {
            return items[arg0].getDisplay();
        }

        // We have a problem
        return "Eh?";
    }

    public boolean isCellEditable(int arg0, int arg1) {
        // Selection column is editable
        if (arg1 == 0) {
            return true;
        }

        return false;
    }

    public void setValueAt(Object arg0, int arg1, int arg2) {
        int firstrow = 0;
        int lastrow = 0;

        // Only set if we're not a header
        if (!items[arg1].isHeader()) {
            // If we're in a group, unset all the items in the
            // group - as long as we aren't unsetting.
            String group = items[arg1].getGroup();

            if ((group != null) && ((Boolean) arg0).booleanValue()) {
                for (int i = 0; i < items.length; i++) {
                    if (group.equals(items[i].getGroup())) {
                        if (firstrow == 0) {
                            firstrow = i;
                        }

                        items[i].setSelected(false);

                        if (i > lastrow) {
                            lastrow = i;
                        }
                    }
                }
            }

            // Set our item as selected/unselected
            items[arg1].setSelected(((Boolean) arg0).booleanValue());

            // Update the table
            //if (firstrow != 0) fireTableRowsUpdated(firstrow, lastrow);
        }
    }

    public int getRowCount() {
        return items.length;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int arg0) {
        return "";
    }

    public SelectableItem getRow(int row) {
        return items[row];
    }
}


@SuppressWarnings("serial")
class SelectableRenderer extends DefaultTableCellRenderer {
    private SelectableModel tablemodel = null;

    public SelectableRenderer(SelectableModel tablemodel) {
        super();
        this.tablemodel = tablemodel;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);

        SelectableItem s = tablemodel.getRow(row);

        // If the value is set, show an asterisk, otherwise show
        // an empty space.
        if (s.isSelected()) {
            setText("*");
        } else {
            setText("");
        }

        return this;
    }
}


@SuppressWarnings("serial")
class SelectableDisplayRenderer extends DefaultTableCellRenderer {
    private SelectableModel tablemodel = null;

    public SelectableDisplayRenderer(SelectableModel tablemodel) {
        super();
        this.tablemodel = tablemodel;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);

        SelectableItem s = tablemodel.getRow(row);

        // If it's a header, highlight it with a dark background
        if (s.isHeader()) {
            setBackground(new Color(0, 0, 88));
            setForeground(Color.WHITE);
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        setText(s.getDisplay());

        return this;
    }
}
