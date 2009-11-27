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

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;


/**
  * Small component that shows the current locale and allows a popup
  * menu to choose another locale
  */
public class LocaleSwitcher extends JLabel {
    JPopupMenu pop = new JPopupMenu();

    public LocaleSwitcher() {
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

        setText(Global.getLanguage(Global.settings_Locale));
        setToolTipText(Global.getLanguageCountry(Global.settings_Locale));
    }

    public void loadLocales() {
        String[] locales = Global.getSupportedLocales();

        for (int i = 0; i < locales.length; i++) {
            JMenuItem m = new JMenuItem();
            final String locale = Global.getLocaleFromString(locales[i]);
            m.setText(locales[i]);
            m.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Change switcher text
                        LocaleSwitcher.this.setText(Global.getLanguage(locale));
                        LocaleSwitcher.this.setToolTipText(Global.getLanguageCountry(
                                locale));

                        // Change the locale and updated the menu/toolbar
                        String lang = locale.substring(0, locale.indexOf("_"));
                        String country = locale.substring(locale.indexOf("_") +
                                1, locale.length());
                        Locale.setDefault(new Locale(lang, country));
                        Global.settings_Locale = locale;
                        Global.mainForm.reloadToolsAndMenu();
                    }
                });
            pop.add(m);
        }
    }

    public void showMenu(int x, int y) {
        pop.show(this, x, y);
    }
}
