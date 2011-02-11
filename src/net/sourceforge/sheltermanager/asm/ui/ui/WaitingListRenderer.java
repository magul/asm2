/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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
import java.awt.ComponentOrientation;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


@SuppressWarnings("serial")
public class WaitingListRenderer extends DefaultTableCellRenderer
    implements ASMCellRenderer {
    /** Urgency colours for background */
    private Color[] bg_cols = new Color[] {
            Color.RED, Color.RED, new Color(255, 65, 65),
            new Color(255, 115, 115), new Color(255, 165, 165), Color.WHITE
        };

    /** Urgency colours for foreground */
    private Color[] fg_cols = new Color[] {
            Color.WHITE, Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK,
            Color.BLACK
        };
    private Color highlighted_bg = new Color(255, 242, 132); // Pale yellow
    private Color highlighted_fg = Color.BLACK;
    private SortableTableModel tablemodel = null;
    private int urgencycolumn = 10;
    private int highlightedcolumn = 11;

    public WaitingListRenderer(int urgencycolumn, int highlightedcolumn) {
        super();
        this.urgencycolumn = urgencycolumn;
        this.highlightedcolumn = highlightedcolumn;

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

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            // Highlight according to urgency/manually highlighted
            String hl = tablemodel.getValueAt(row, highlightedcolumn).toString();
            String ur = tablemodel.getValueAt(row, urgencycolumn).toString();

            if ((hl != null) && hl.equals("1")) {
                setForeground(highlighted_fg);
                setBackground(highlighted_bg);
            } else {
                int urgency = Integer.parseInt(ur);

                if (urgency > 5) {
                    urgency = 5; // Make sure one of our 5 urgencies
                }

                setForeground(fg_cols[urgency]);
                setBackground(bg_cols[urgency]);
            }
        }

        return this;
    }
}
