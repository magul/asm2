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

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.*;


/**
 * A special kind of table model that allows
 * sorting in a transparent manner. It also allows
 * ID looking up and whole bunch of other clever
 * things to save me some work.
 *
 * Written by R.Rawson-Tetley, June, 2002.
 */
public class SortableTableModel extends AbstractTableModel {
    private String[] columnNames;
    private String[][] data;
    private int maxrows;
    private int maxcols;
    private int idcol;
    private JTable table = null;

    public String[][] getData() {
        return data;
    }

    /**
     * Sets the data for the table model.
     * This one allows you to specify the number of columns
     * to be used - not for display as they use cols.length, but
     * for sorting purposes to maintain data integrity.
     */
    public void setData(String[] cols, String[][] thedata, int maximumrows,
        int maximumcols, int theIDcol) {
        columnNames = cols;
        maxcols = maximumcols;
        data = thedata;
        maxrows = maximumrows;
        idcol = theIDcol;
        cleanCols();
    }

    /**
     * Sets the data for the table model.
     * This one assumes the number of columns to sort
     * is cols.length + 1 (ie. The cols + one hidden ID field).
     */
    public void setData(String[] cols, String[][] thedata, int maximumrows,
        int theIDcol) {
        columnNames = cols;
        data = thedata;
        maxrows = maximumrows;
        maxcols = cols.length + 1;
        idcol = theIDcol;
        cleanCols();
    }

    /** Checks that the column headers don't have colons on the end where
      * translation strings have been shared and that there's no ampersands
      * in the string */
    public void cleanCols() {
        for (int i = 0; i < columnNames.length; i++) {
            String s = columnNames[i].trim();

            if (s.endsWith(":")) {
                s = s.substring(0, s.length() - 1);
            }

            s = UI.mnemonicRemove(s);
            columnNames[i] = s;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return maxrows;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public String getIDAt(int row) {
        return data[row][idcol];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public void setValueAt(Object o, int row, int col) {
        data[row][col] = (String) o;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void sortByColumn(int column, boolean ascending) {
        // This routine could be faster, but it uses a standard
        // bubble sort routine that works through the
        // data, swapping whole rows.

        // Cycle through the string, swapping until
        // we are in order:
        boolean orderedYet = false;

        while (!orderedYet) {
            // Assume we are ordered until we swap something
            orderedYet = true;

            for (int i = 0; i < (maxrows - 1); i++) {
                // Compare the two as strings
                int compared = data[i][column].compareToIgnoreCase(data[i + 1][column]);

                // If i > i + 1 and ascending is on, then they need to be swapped,
                // also, if i < i + 1 and ascending is off, then they also need to be swapped
                if (((compared > 0) && ascending) ||
                        ((compared < 0) && !ascending)) {
                    // We are not ordered because we need to swap a row
                    orderedYet = false;

                    // Single pass copy
                    String buffer = "";

                    for (int z = 0; z < maxcols; z++) {
                        // We move each value in this one loop in this fashion:
                        // value at row i -> buffer
                        // value at row i + 1 -> row i
                        // value in buffer -> row i + 1
                        buffer = data[i][z];
                        data[i][z] = data[i + 1][z];
                        data[i + 1][z] = buffer;
                    }
                }
            }
        }
    }

    public void addMouseListenerToHeaderInTable(JTable table) {
        final SortableTableModel sorter = this;
        final JTable tableView = table;
        this.table = table;
        tableView.setColumnSelectionAllowed(false);

        MouseAdapter listMouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    TableColumnModel columnModel = tableView.getColumnModel();
                    int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                    int column = tableView.convertColumnIndexToModel(viewColumn);

                    if ((e.getClickCount() == 1) && (column != -1)) {
                        int shiftPressed = e.getModifiers() &
                            InputEvent.SHIFT_MASK;
                        boolean ascending = (shiftPressed == 0);
                        sorter.sortByColumn(column, ascending);
                        sorter.fireTableDataChanged();
                    }
                }
            };

        JTableHeader th = tableView.getTableHeader();
        th.addMouseListener(listMouseListener);
    }

    public void packColumns(int margin) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int c = 0; c < table.getColumnCount(); c++) {
            packColumn(c, 2);
        }
    }

    // Sets the preferred width of the visible column specified by vColIndex. The column
    // will be just wide enough to show the column head and the widest cell in the column.
    // margin pixels are added to the left and right
    // (resulting in an additional width of 2*margin pixels).
    public void packColumn(int vColIndex, int margin) {
        TableModel model = this;
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();

        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        Component comp = renderer.getTableCellRendererComponent(table,
                col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(table,
                    table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += (2 * margin);

        // Set the width
        col.setPreferredWidth(width);
    }

    public void finalize() throws Throwable {
        free();
    }

    public void free() {
        columnNames = null;
        data = null;
    }
}
