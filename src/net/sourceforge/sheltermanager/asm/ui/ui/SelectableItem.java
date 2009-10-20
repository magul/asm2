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

public class SelectableItem {
    /** Display text */
    private String display;

    /** Value - useful for callers */
    private Object value;

    /** Whether it is selected */
    private boolean selected = false;

    /**
     * Whether or not this is a header and should not be included when returning
     * selection results
     */
    private boolean header = false;

    /**
     * If this item is part of a mutually exclusive group (or null
     * for no group
     */
    private String group = null;

    /**
     * A reference to the checkbox
     */
    private UI.CheckBox checkbox = null;

    /**
     *
     * @param display The text to output in the box
     * @param value A value to attach to the text
     * @param selected Whether the item is selected
     * @param header Where this is a heading rather than an item
     */
    public SelectableItem(String display, Object value, boolean selected,
        boolean header) {
        this(display, value, selected, header, null);
    }

    public SelectableItem(String display, Object value, boolean selected,
        boolean header, String group) {
        this.display = display;
        this.value = value;
        this.selected = selected;
        this.header = header;
        this.group = group;
    }

    /**
     * @return Returns the display.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @param display
     *            The display to set.
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * @return Returns the selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            The selected to set.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return Returns the header.
     */
    public boolean isHeader() {
        return header;
    }

    /**
     * @param header
     *            The header to set.
     */
    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    void setCheckBox(UI.CheckBox checkbox) {
        this.checkbox = checkbox;
    }

    UI.CheckBox getCheckBox() {
        return checkbox;
    }
}
