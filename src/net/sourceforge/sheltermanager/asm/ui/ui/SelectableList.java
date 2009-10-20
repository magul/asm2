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

import java.util.List;


/**
 * Allows selections from a list of items.
 * Can use one of two implementations - SelectablePane or
 * SelectableTable depending on platform.
 *
 * @author Robin Rawson-Tetley
 *
 */
public class SelectableList extends UI.Panel {
    private SelectableComponent component = null;

    public SelectableList(SelectableItem[] items) {
        getComponent();
        setItems(items);
    }

    public SelectableList(List l) {
        getComponent();
        setItems(l);
    }

    public SelectableComponent getComponent() {
        // No longer needed with Swing renderer
        //if (UI.osIsMacOSX()) {
        //    component = new SelectableTable();
        //} else {
        component = new SelectablePane();
        //}
        setLayout(UI.getBorderLayout());
        add((java.awt.Component) component, UI.BorderLayout.CENTER);

        return component;
    }

    public void setItems(SelectableItem[] items) {
        component.setItems(items);
    }

    public void setItems(List l) {
        component.setItems(l);
    }

    public SelectableItem[] getSelections() {
        return component.getSelections();
    }
}
