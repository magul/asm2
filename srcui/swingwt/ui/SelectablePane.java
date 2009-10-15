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

import swingwt.awt.Color;
import swingwt.awt.Component;
import swingwt.awt.Font;

import swingwt.awt.event.ActionListener;
import swingwt.awt.event.KeyAdapter;
import swingwt.awt.event.KeyEvent;
import swingwt.awt.event.MouseAdapter;
import swingwt.awt.event.MouseEvent;

import swingwtx.swing.BoxLayout;
import swingwtx.swing.DefaultCellEditor;
import swingwtx.swing.JCheckBox;
import swingwtx.swing.JScrollPane;
import swingwtx.swing.JTable;

import swingwtx.swing.border.EtchedBorder;

import swingwtx.swing.table.AbstractTableModel;
import swingwtx.swing.table.DefaultTableCellRenderer;

import java.util.List;


/**
 * Allows selections from a list of items.
 *
 * @author Robin Rawson-Tetley
 *
 */
public class SelectablePane extends UI.Panel implements SelectableComponent {
    SelectableItem[] items = null;

    public void setItems(List l) {
        SelectableItem[] its = new SelectableItem[l.size()];

        for (int i = 0; i < l.size(); i++)
            its[i] = (SelectableItem) l.get(i);

        setItems(its);
    }

    public void setItems(SelectableItem[] items) {
        this.items = items;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new EtchedBorder());

        UI.Panel p = UI.getPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        for (int i = 0; i < items.length; i++) {
            if (items[i].isHeader()) {
                p.add(new TitleLabel(items[i].getDisplay()));
            } else {
                final UI.CheckBox c = new UI.CheckBox();
                c.setText(items[i].getDisplay());

                final int idx = i;
                final String group = items[i].getGroup();
                c.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                            updateSelection(idx, c, group);
                        }
                    });
                c.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            updateSelection(idx, c, group);
                        }
                    });
                c.setSelected(items[i].isSelected());
                p.add(c);
                items[i].setCheckBox(c);
            }
        }

        // Add a blank to prevent scrollpane being too short
        UI.Label spacer = UI.getLabel("");
        spacer.setPreferredSize(UI.getDimension(Global.mainForm.getWidth() -
                50, UI.getTextBoxHeight()));
        p.add(spacer);

        JScrollPane s = new JScrollPane(p);
        add(s, UI.BorderLayout.CENTER);
    }

    public void updateSelection(int idx, UI.CheckBox c, String group) {
        // if the item isn't in a group, treat it as a normal
        // checkbox
        if (group == null) {
            items[idx].setSelected(c.isSelected());

            return;
        }

        // Unselect all items in the group
        for (int i = 0; i < items.length; i++) {
            if (items[i].getGroup() != null) {
                if (items[i].getGroup().equals(group)) {
                    items[i].setSelected(false);
                    items[i].getCheckBox().setSelected(false);
                }
            }
        }

        // Select this item
        items[idx].setSelected(true);
        c.setSelected(true);
    }

    public SelectableItem[] getSelections() {
        return items;
    }
}
