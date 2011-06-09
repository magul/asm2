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
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;


/**
 *
 * Allows editing of currency fields.
 */
@SuppressWarnings("serial")
public class CurrencyField extends UI.Panel {
    private UI.TextField txt = new UI.TextField();

    public CurrencyField() {
        super(true); // No panel border
        initComponents();
        // Set a default value
        txt.setText(Global.currencySymbol + "0.00");
    }

    /**
     * Returns the contents of the text box without all the currency bits so you
     * can put it straight into a database
     */
    public String getText() {
        return toNumber();
    }

    public UI.TextField getTextField() {
        return txt;
    }

    public String toNumber() {
        String outtext = txt.getText();

        // Remove any currency symbols
        if (outtext.startsWith(Global.currencySymbol)) {
            outtext = outtext.substring(Global.currencySymbol.length(),
                    outtext.length());
        }

        if (outtext.startsWith("-" + Global.currencySymbol)) {
            outtext = outtext.substring(Global.currencySymbol.length() + 1,
                    outtext.length());
        }

        // Make sure it is formattable as a number
        try {
            Float.parseFloat(outtext);
        } catch (NumberFormatException e) {
            Dialog.showError(Global.i18n("uibeans",
                    "The_currency_figure_you_entered_was_invalid."));

            return "0.00";
        }

        // Round it off to two decimal places. Do this by hunting
        // for a decimal point. If there isn't one, just stick .00
        // on the end. If there is, do a hard-round down by
        // throw anything away after the number of decimal places
        // we want.
        int dp = outtext.indexOf(".");

        if (dp == -1) {
            outtext = outtext + ".00";
        } else if (dp < (outtext.length() - 3)) {
            outtext = outtext.substring(0, dp + 3);
        } else
        // If we are short by 1 or 2 chars, make up those zeroes
        if (dp == (outtext.length() - 2)) {
            outtext = outtext + "0";
        } else if (dp == (outtext.length() - 1)) {
            outtext = outtext + "00";
        }

        // Return it
        return outtext;
    }

    public int getValue() {
        String num = toNumber();
        num = Utils.replace(num, ".", "");

        return Integer.parseInt(num);
    }

    public void setValue(int i) {
        String num = Integer.toString(i);

        if (num.length() > 2) {
            num = num.substring(0, num.length() - 2) + "." +
                num.substring(num.length() - 2);
            txt.setText(Global.currencySymbol + num);
        }
    }

    public void setToolTipText(String newvalue) {
        txt.setToolTipText(newvalue);
    }

    public String getToolTipText() {
        return txt.getToolTipText();
    }

    private void initComponents() {
        setLayout(UI.getBorderLayout());

        addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    panel_focusgained(evt);
                }
            });

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    txt_focusLost(evt);
                }
            });

        txt.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    txt_keyPressed(evt);
                }
            });

        add(txt, java.awt.BorderLayout.CENTER);
    }

    private void panel_focusgained(java.awt.event.FocusEvent evt) {
        txt.requestFocus();
    }

    private void txt_keyPressed(java.awt.event.KeyEvent evt) {
        this.processKeyEvent(evt);
    }

    private void txt_focusLost(java.awt.event.FocusEvent evt) {
        // Check what we have and format it correctly
        // do this by getting the text of the box as a number
        // and then re-setting it.
        txt.setText(Global.currencySymbol + toNumber());
    }
}
