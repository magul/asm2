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
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.awt.*;

import java.text.ParseException;

import java.util.Calendar;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class AccountRenderer extends DefaultTableCellRenderer
    implements ASMCellRenderer {
    private Color odd = new Color(204, 229, 209);
    private Color even = new Color(229, 228, 204);
    private SortableTableModel tablemodel = null;
    private int[] currencycols = null;
    private int reconciledcolumn = 1;
    private int posnegcolumn = 6;

    public AccountRenderer(int[] currencycols, int posnegcolumn, int reconciledcolumn) {
        super();
        this.currencycols = currencycols;
        this.reconciledcolumn = reconciledcolumn;
        this.posnegcolumn = posnegcolumn;

        if (!UI.isLTR()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
    }

    public void setTableModel(SortableTableModel tablemodel) {
        this.tablemodel = tablemodel;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);

        // If we're rendering a monetary column, use a fixed-width font
        boolean wascurrency = false;

        for (int i = 0; i < currencycols.length; i++) {
            if (currencycols[i] == column) {
                setFont(new Font("monospaced", 0, table.getFont().getSize()));
                setHorizontalAlignment(UI.ALIGN_RIGHT);

                // If that currency is negative, show it in red
                if (tablemodel.getValueAt(row, posnegcolumn).toString()
                                  .equals("-")) {
                    setForeground(Color.RED);
                }
                wascurrency = true;
            }
        }

        if (!wascurrency) {
            setFont(table.getFont());
            setHorizontalAlignment(UI.ALIGN_LEFT);
            
            // Use blue for reconciled, black for not
            if (reconciledcolumn == 0)
            	setForeground(Color.BLACK);
            else if (tablemodel.getValueAt(row, reconciledcolumn).toString().trim().equals(""))
            	setForeground(Color.BLACK);
            else
            	setForeground(Color.BLUE);
        }

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            // Choose background colours based on odd/even rows
            if ((row % 2) == 0) {
                setBackground(even);
            } else {
                setBackground(odd);
            }
        }

        return this;
    }
}
