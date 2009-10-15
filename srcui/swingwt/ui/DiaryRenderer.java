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
import net.sourceforge.sheltermanager.asm.utility.Utils;

import swingwt.awt.Color;
import swingwt.awt.Component;

import swingwtx.swing.JTable;

import swingwtx.swing.table.DefaultTableCellRenderer;

import java.text.ParseException;

import java.util.Calendar;


public class DiaryRenderer extends DefaultTableCellRenderer
    implements ASMCellRenderer {
    private Color col = new Color(0, 0, 88);
    private SortableTableModel tablemodel = null;
    private int datecolumn = 2;

    public DiaryRenderer(int datecolumn) {
        super();
        this.datecolumn = datecolumn;
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

            // setFont(table.getFont().deriveFont(Font.ITALIC)
            // .deriveFont(12.0f));
        } else {
            setToolTipText(value.toString());

            // If the date on this value is after today, then
            // show it in reversed font.
            try {
                Calendar diarydate = Utils.dateToCalendar(Utils.parseTableDate(
                            (String) tablemodel.getValueAt(row, datecolumn)));

                Calendar today = Calendar.getInstance();

                if (diarydate.after(today)) {
                    setBackground(col);
                    setForeground(Color.white);
                } else {
                    setBackground(null);
                    setForeground(null);
                }
            } catch (ParseException e) {
                Global.logException(e, DiaryRenderer.class);
            }

            /*
             * setBackground(((row & 1) == 1 ? col : Color.white));
             * setForeground(((row & 1) == 0 ? col : Color.white));
             */
        }

        return this;
    }
}
