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

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.border.EtchedBorder;


/**
 * Allows selections from a list of items.
 *
 * @author Robin Rawson-Tetley
 *
 */
@SuppressWarnings("serial")
public class SelectablePane extends UI.Panel implements SelectableComponent {
    SelectableItem[] items = null;

    public SelectablePane() {
        super();

        if (!UI.isLTR()) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
    }

    public void setItems(List<SelectableItem> l) {
        SelectableItem[] its = new SelectableItem[l.size()];

        for (int i = 0; i < l.size(); i++)
            its[i] = (SelectableItem) l.get(i);

        setItems(its);
    }

    public void setItems(SelectableItem[] items) {
        this.items = items;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EtchedBorder());

        ScrollPanel p = new ScrollPanel();

        if (!UI.isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        for (int i = 0; i < items.length; i++) {
            if (items[i].isHeader()) {
                p.add(UI.getTitleLabelPanel(items[i].getDisplay()));
            } else {
                final UI.CheckBox c = UI.getCheckBox(items[i].getDisplay());
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

        UI.ScrollPane s = UI.getScrollPane(p);
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


@SuppressWarnings("serial")
class ScrollPanel extends JPanel implements Scrollable {
    public ScrollPanel() {
        super();
    }

    public int getScrollableUnitIncrement(Rectangle rect, int orientation,
        int direction) {
        return 15;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
        int orientation, int direction) {
        return 50;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
