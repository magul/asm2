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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Vector;


/**
 * Shows a list for selection input
 */
@SuppressWarnings("serial")
public class InputListDialog extends ASMDialog {
    String message = "";
    UI.List lst = null;
    UI.Button btnOk = null;
    UI.Button btnCancel = null;
    SearchBar search = null;
    Object[] values = null;
    Object selected = null;

    public InputListDialog(String message, String title, Object[] values,
        Object selected) {
        this.message = message;
        this.values = values;
        this.selected = selected;
        init(title, IconManager.getIcon(IconManager.QUESTION), "uierror", false);
        lst.setListData(values);

        if (selected != null) {
            lst.setSelectedValue(selected, true);
        }

        UI.centerWindow(this);
        setVisible(true);
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getFlowLayout());
        UI.Panel b = UI.getPanel(UI.getFlowLayout());

        p.add(UI.getLabel(IconManager.getIcon(IconManager.QUESTION)));
        p.add(UI.getLabel(message));

        if (showSearch()) {
            search = (SearchBar) p.add(new SearchBar(UI.fp(this, "onSearch")));
        }

        lst = UI.getList(UI.fp(this, "actionOk"));

        btnOk = UI.getButton(i18n("Ok"), null, 'o', null,
                UI.fp(this, "actionOk"));
        btnCancel = UI.getButton(i18n("Cancel"), null, 'c', null,
                UI.fp(this, "actionCancel"));
        b.add(btnOk);
        b.add(btnCancel);

        add(p, UI.BorderLayout.NORTH);
        UI.addComponent(this, lst);
        add(b, UI.BorderLayout.SOUTH);

        setSize(600, 300);

        // Only have enter default to ok if there's no search box
        if (!showSearch()) {
            getRootPane().setDefaultButton(btnOk);
        }
    }

    public Object getDefaultFocusedComponent() {
        if (showSearch()) {
            return search.getTextField();
        } else {
            return lst;
        }
    }

    public void onSearch() {
        String s = search.getSearchText();

        if ((s == null) || s.equals("")) {
            lst.setListData(values);
        }

        // Make subset of data
        Vector<Object> v = new Vector<Object>();

        for (int i = 0; i < values.length; i++) {
            if (values[i].toString().toLowerCase().indexOf(s.toLowerCase()) != -1) {
                v.add(values[i]);
            }
        }

        lst.setListData(v);

        // If there was some values, select the first item
        if (v.size() > 0) {
            lst.setSelectedIndex(0);
        }
    }

    /** Whether or not to show the search bar */
    public boolean showSearch() {
        return values.length > UI.listSearchThreshold();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> v = new Vector<Object>();

        if (showSearch()) {
            v.add(search.getTextField());
        }

        v.add(lst);
        v.add(btnOk);
        v.add(btnCancel);

        return v;
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        Dialog.lastInput = null;

        return false;
    }

    public void setSecurity() {
    }

    public void actionCancel() {
        Dialog.lastInput = null;
        dispose();
    }

    public void actionOk() {
        Dialog.lastInput = (String) lst.getSelectedValue();
        dispose();
    }

    public class SearchBar extends UI.Panel {
        UI.TextField txt = null;
        FunctionPointer onSearch = null;

        public SearchBar(FunctionPointer onSearch) {
            super(UI.getBorderLayout());
            this.onSearch = onSearch;
            txt = UI.getTextField();
            txt.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            SearchBar.this.onSearch.call();
                        }
                    }
                });
            add(txt, UI.BorderLayout.CENTER);

            UI.ToolBar t = UI.getToolBar();
            t.add(UI.getButton(null, null, 's',
                    IconManager.getIcon(IconManager.SEARCHSMALL), onSearch));
            add(t, UI.BorderLayout.EAST);
        }

        public String getSearchText() {
            return txt.getText();
        }

        public UI.TextField getTextField() {
            return txt;
        }
    }
}
