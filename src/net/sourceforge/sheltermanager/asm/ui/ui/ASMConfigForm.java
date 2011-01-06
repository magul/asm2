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

import java.util.Vector;

import net.sourceforge.sheltermanager.asm.bo.Configuration;


/**
 * Superclass of forms that use text fields to edit
 * entries in the configuration table
 */
@SuppressWarnings("serial")
public class ASMConfigForm extends ASMForm {
    ConfigItem[] items = null;
    Vector<Object> taborder = new Vector<Object>();

    /**
     * @param items The list of items to show
     * @param title The form title
     * @param icon The form icon
     */
    public ASMConfigForm(ConfigItem[] items, String title, javax.swing.Icon icon) {
        this.items = items;
        init(title, icon, "");
        loadData();
    }

    public void loadData() {
        for (int i = 0; i < items.length; i++) {
            switch (items[i].type) {
            case ConfigItem.TEXTFIELD:
                items[i].text.setText(Configuration.getString(items[i].key));

                break;

            case ConfigItem.COMBOBOX:
                items[i].combo.setSelectedItem(Configuration.getString(
                        items[i].key));

                break;

            case ConfigItem.CHECKBOX:
                items[i].check.setSelected(Configuration.getBoolean(
                        items[i].key));

                break;
            }
        }
    }

    public boolean saveData() {
        for (int i = 0; i < items.length; i++) {
            switch (items[i].type) {
            case ConfigItem.TEXTFIELD:
                Configuration.setEntry(items[i].key, items[i].text.getText());

                break;

            case ConfigItem.COMBOBOX:
                Configuration.setEntry(items[i].key,
                    items[i].combo.getSelectedItem().toString());

                break;

            case ConfigItem.CHECKBOX:
                Configuration.setEntry(items[i].key,
                    items[i].check.isSelected() ? "Yes" : "No");

                break;
            }
        }

        dispose();

        return true;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());
        UI.Panel pn = UI.getPanel(UI.getBorderLayout());
        pn.add(pb, UI.BorderLayout.NORTH);

        for (int i = 0; i < items.length; i++) {
            switch (items[i].type) {
            case ConfigItem.TEXTFIELD:

                UI.TextField t = UI.getTextField(items[i].tooltip);
                UI.addComponent(p, items[i].displaytext, t);
                items[i].text = t;
                taborder.add(t);

                break;

            case ConfigItem.COMBOBOX:

                UI.ComboBox b = UI.getCombo(items[i].selections);

                if (items[i].tooltip != null) {
                    b.setToolTipText(items[i].tooltip);
                }

                b.setSelectedIndex(0);
                UI.addComponent(p, items[i].displaytext, b);
                items[i].combo = b;
                taborder.add(b);

                break;

            case ConfigItem.CHECKBOX:

                UI.CheckBox c = UI.getCheckBox(items[i].displaytext,
                        items[i].tooltip);
                p.add(UI.getLabel());
                p.add(c);
                items[i].check = c;
                taborder.add(c);

                break;
            }
        }

        pb.add(UI.getButton(UI.messageOK(), null, 'o', null,
                UI.fp(this, "saveData")));
        pb.add(UI.getButton(UI.messageCancel(), null, 'c', null,
                UI.fp(this, "dispose")));

        add(p, UI.BorderLayout.NORTH);
        add(pn, UI.BorderLayout.CENTER);
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public Vector<Object> getTabOrder() {
        return taborder;
    }

    public Object getDefaultFocusedComponent() {
        return taborder.get(0);
    }

    public static class ConfigItem {
        public final static int TEXTFIELD = 0;
        public final static int CHECKBOX = 1;
        public final static int COMBOBOX = 2;
        String displaytext = "";
        String key = "";
        String tooltip = null;
        UI.TextField text = null;
        UI.CheckBox check = null;
        UI.ComboBox combo = null;
        String[] selections = null;
        int type;

        public ConfigItem(String displaytext, String key) {
            this(displaytext, key, TEXTFIELD, null);
        }

        public ConfigItem(String displaytext, String key, String tooltip) {
            this(displaytext, key, TEXTFIELD, tooltip);
        }

        public ConfigItem(String displaytext, String key, int type) {
            this(displaytext, key, type, null);
        }

        public ConfigItem(String displaytext, String key, String tooltip,
            String[] selections) {
            this.selections = selections;
            this.type = COMBOBOX;
            this.displaytext = displaytext;
            this.key = key;
            this.tooltip = tooltip;
        }

        public ConfigItem(String displaytext, String key, int type,
            String tooltip) {
            this.displaytext = displaytext;
            this.key = key;
            this.type = type;
            this.tooltip = tooltip;
        }
    }
}
