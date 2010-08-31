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

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;


/**
  * Small component that shows the current locale with a flag 
  * and allows a popup menu to choose another locale
  */
public class LocaleSwitcher extends JLabel {
    
    JPopupMenu pop = new JPopupMenu();
    public final static int LANG_ONLY = 0;
    public final static int FULL = 1;
    private int mode = LANG_ONLY;

    public LocaleSwitcher() {
        this(LANG_ONLY);
    }

    public LocaleSwitcher(int mode) {
        this.mode = mode;
        setBackground(new Color(0, 0, 82));
        setForeground(Color.WHITE);
        setOpaque(true);
        setFont(getFont().deriveFont(Font.BOLD));
        setBorder(new LineBorder(Color.WHITE));

        addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showMenu(e.getX(), e.getY());
                }
            });

        loadLocales();
        String d = (mode == LANG_ONLY ? 
            Global.getLanguage(Global.settings_Locale) : 
            Global.getLocaleName(Global.getLanguageCountry(Global.settings_Locale)));
        setText(d);
        setIcon(IconManager.getFlag(Global.settings_Locale));
        setToolTipText(Global.getLanguageCountry(Global.settings_Locale));
    }

    public void loadLocales() {
        String[] locales = Global.getSupportedLocales();

        for (int i = 0; i < locales.length; i++) {
            JMenuItem m = new JMenuItem();

            if (locales[i].equals("-")) {
                pop.add(UI.getSeparator());
                continue;
            }

            final String locale = Global.getLocaleFromString(locales[i]);
            m.setText(Global.getLocaleName(locales[i]));
            m.setIcon(IconManager.getFlag(locale));
            m.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Change switcher text
                        LocaleSwitcher.this.setIcon(IconManager.getFlag(locale));

                        if (mode == LANG_ONLY) {
                            LocaleSwitcher.this.setText(Global.getLanguage(locale));
                        }
                        else {
                            LocaleSwitcher.this.setText(Global.getLocaleName(Global.getLanguageCountry(locale)));
                        }
                        LocaleSwitcher.this.setToolTipText(Global.getLanguageCountry(
                                locale));

                        // Change the locale and update the menu/toolbar
                        String lang = locale.substring(0, locale.indexOf("_"));
                        String country = locale.substring(locale.indexOf("_") +
                                1, locale.length());
                        Locale.setDefault(new Locale(lang, country));
                        Global.settings_Locale = locale;
                        if (Global.mainForm != null) Global.mainForm.reloadToolsAndMenu();
                    }
                });
            pop.add(m);
        }
    }

    public String getSelectedLocale() {
        return Global.settings_Locale;
    }

    public void showMenu(int x, int y) {
        pop.show(this, x, y);
    }
}
