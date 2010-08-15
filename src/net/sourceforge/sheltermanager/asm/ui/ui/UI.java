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
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.print.*;

import java.io.*;

import java.util.Date;
import java.util.Vector;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.*;
import javax.swing.text.*;


/**
 * Helper functions for creating components and keeping things consistent
 * across the ASM UI
 */
public final class UI {
    private final static String TOOLTIP_DEFAULT = "";
    private final static boolean useDefaultTooltip = false;
    public final static int ALIGN_CENTER = SwingConstants.CENTER;
    public final static int ALIGN_LEFT = SwingConstants.LEADING;
    public final static int ALIGN_RIGHT = SwingConstants.TRAILING;
    public final static int ALIGN_TOP = SwingConstants.TOP;
    public final static int ALIGN_BOTTOM = SwingConstants.BOTTOM;

    public final static int LAF_DEFAULT = 0;
    public final static int LAF_PLATFORM = 1;
    public final static int LAF_METAL = 2;
    public final static int LAF_METAL_GTK = 3;

    static {
        // Make sure AWT honours font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
    }

    private UI() {
    }

    public static void swingSetLAF(int laf) {
        switch (laf) {
	    case LAF_DEFAULT: return;
            case LAF_PLATFORM: swingSetPlatformLAF(); break;
	    case LAF_METAL: swingSetMetalLAF(); break;
	    case LAF_METAL_GTK: swingSetMetalGTKLAF(); break;
	    default: swingSetMetalLAF(); break;
	}
	try {
	    if (Dialog.theParent != null)
	        SwingUtilities.updateComponentTreeUI(Dialog.theParent);
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void swingSetMetalLAF() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void swingSetMetalGTKLAF() {
        final int REGULAR = 0;
	try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Try a better font - Sun Java default is hideous and everything
            // is in bold, which is nasty. OpenJDK uses better fonts, but still
            // has the bold problem.
            if (swingIsFontAvailable("Bitstream Vera Sans")) {
                swingSetDefaultFontName("Bitstream Vera Sans", REGULAR);
            } else if (swingIsFontAvailable("DejaVu Sans")) {
                swingSetDefaultFontName("DejaVu Sans", REGULAR);
            } else if (swingIsFontAvailable("Sans")) {
                swingSetDefaultFontName("Sans", REGULAR);
            } else if (swingIsFontAvailable("Arial")) {
                swingSetDefaultFontName("Arial", REGULAR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void swingSetPlatformLAF() {
        // Force GTK when choosing platform for Linux and Solaris users
        if (osIsLinux() || osIsSolaris()) {
            swingSetGTKLAF();
	    return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void swingSetGTKLAF() {
        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean swingIsFontAvailable(String fontname) {
        try {
            Font f = new Font(fontname, 12, 0);

            return f.getFontName().equals(fontname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Goes through the default font for every swing component and changes
     * the face to match
     * @param fontname The name of the font to use
     * @param style The font style to set - use a negative number to leave unchanged
     */
    public static void swingSetDefaultFontName(String fontname, int style) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);

            if (value instanceof FontUIResource) {
                FontUIResource f = (FontUIResource) UIManager.get(key);
                UIManager.put(key,
                    new FontUIResource(fontname,
                        (style < 0) ? f.getStyle() : style, f.getSize()));
            }
        }
    }

    public static FunctionPointer fp(Object instance, String method) {
        return new FunctionPointer(instance, method);
    }

    public static Frame getParentFrame(Component c) {
        Container cont = c.getParent();

        if (cont instanceof Frame) {
            return (Frame) cont;
        } else {
            return getParentFrame((Component) cont);
        }
    }

    /**
     * Reads a mnemonic from the text string given (&c) or mnemonic if
     * none is found.
     */
    public static char mnemonicFromText(String text, char mnemonic) {
        int i = text.indexOf("&");

        if ((i == -1) || (i == (text.length() - 1))) {
            return mnemonic;
        } else {
            return text.charAt(i + 1);
        }
    }

    public static String mnemonicRemove(String text) {
        return Utils.replace(text, "&", "");
    }

    // Component helpers
    public static Button getButton(String text, FunctionPointer onClick) {
        return getButton(text, null, ' ', null, onClick);
    }

    public static Button getButton(String text, char mnemonic,
        FunctionPointer onClick) {
        return getButton(text, null, mnemonic, null, onClick);
    }

    public static Button getButton(String text, String tooltiptext,
        FunctionPointer onClick) {
        return getButton(text, tooltiptext, ' ', null, onClick);
    }

    public static Button getButton(String text, String tooltiptext, Icon icon,
        FunctionPointer onClick) {
        return getButton(text, tooltiptext, ' ', icon, onClick);
    }

    public static Button getButton(String text, String tooltiptext,
        char mnemonic, Icon icon, final FunctionPointer onClick) {
        Button b = new Button();

        if (!isLTR()) {
            b.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (text != null) {
            b.setText(mnemonicRemove(text));
        }

        if (icon != null) {
            b.setIcon(icon);
        }

        if (((mnemonic != ' ') && Global.buttonHotkeys) || (text != null)) {
            if (text != null) {
                b.setMnemonic(mnemonicFromText(text, mnemonic));
            } else {
                b.setMnemonic(mnemonic);
            }

            if (text == null) {
                b.setText(Character.toString(mnemonic));
            }
        }

        if (tooltiptext != null) {
            b.setToolTipText(mnemonicRemove(tooltiptext));
        }

        if (onClick != null) {
            b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            onClick.call();
                        } catch (Exception e) {
                            Global.logException(e, UI.class);
                        }
                    }
                });
        }

        return b;
    }

    public static CheckBox getCheckBox() {
        return getCheckBox(null, null, null);
    }

    public static CheckBox getCheckBox(String text) {
        return getCheckBox(text, null, null);
    }

    public static CheckBox getCheckBox(String text, String tooltip) {
        return getCheckBox(text, tooltip, null);
    }

    public static CheckBox getCheckBox(String text, String tooltip,
        final FunctionPointer onChange) {
        return getCheckBox(text, tooltip, ALIGN_RIGHT, onChange);
    }

    public static CheckBox getCheckBox(String text, String tooltip,
        int textalign, final FunctionPointer onChange) {
        CheckBox c = new CheckBox();

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (text != null) {
            c.setText(text);
        }

        if (tooltip != null) {
            c.setToolTipText(tooltip);
        }

        if (textalign == ALIGN_LEFT) {
            c.setHorizontalTextPosition(SwingConstants.LEADING);
        }

        if (onChange != null) {
	    c.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
		    onChange.call();
		}
	    });
        }

        // There are more than a few places where we have trailing
        // colons that look crap with checkboxes
        if ((text != null) && text.endsWith(":")) {
            c.setText(text.substring(0, text.length() - 1));
        }

        return c;
    }

    public static RadioButton getRadioButton() {
        return getRadioButton(null, null, ' ', null);
    }

    public static RadioButton getRadioButton(String text) {
        return getRadioButton(text, null, ' ', null);
    }

    public static RadioButton getRadioButton(String text, String tooltip) {
        return getRadioButton(text, tooltip, ' ', null);
    }

    public static RadioButton getRadioButton(String text, String tooltip,
        FunctionPointer onChange) {
        return getRadioButton(text, tooltip, ' ', onChange);
    }

    public static RadioButton getRadioButton(String text, String tooltip,
        char mnemonic, final FunctionPointer onChange) {
        RadioButton b = new RadioButton();

        if (!isLTR()) {
            b.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (text != null) {
            b.setText(text);
        }

        if (tooltip != null) {
            b.setToolTipText(tooltip);
        }

        if (mnemonic != ' ') {
            b.setMnemonic(mnemonic);
        }

        if (onChange != null) {
            b.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            onChange.call();
                        }
                    }
                });
        }

        return b;
    }

    public static CurrencyField getCurrencyField() {
        return getCurrencyField(null);
    }

    public static CurrencyField getCurrencyField(String tooltiptext) {
        return getCurrencyField(tooltiptext, null);
    }

    public static CurrencyField getCurrencyField(String tooltiptext,
        final FunctionPointer onChange) {
        CurrencyField d = new CurrencyField();

        if (!isLTR()) {
            d.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (tooltiptext != null) {
            d.setToolTipText(tooltiptext);
        }

        if (onChange != null) {
            d.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        if (isTypedEvent(evt)) {
                            onChange.call();
                        }
                    }
                });
        }

        return d;
    }

    public static DateField getDateField() {
        return getDateField(null);
    }

    public static DateField getDateField(String tooltiptext) {
        return getDateField(tooltiptext, null, null);
    }

    public static DateField getDateField(String tooltiptext,
        final FunctionPointer onChange) {
        return getDateField(tooltiptext, onChange, null);
    }

    public static DateField getDateField(String tooltiptext,
        final FunctionPointer onChange, final FunctionPointer onLostFocus) {
        final DateField d = new DateField();

        if (!isLTR()) {
            d.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (tooltiptext != null) {
            d.setToolTipText(tooltiptext);
        }

        if (onChange != null) {
            d.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        onChange.call();
                    }
                });
            d.setDateChangedListener(new DateChangedListener() {
                    public void dateChanged(String newdate) {
                        onChange.call();
                        d.setText(newdate);
                    }
                });
        }

        if (onLostFocus != null) {
            d.getTextField().addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        onLostFocus.call();
                    }
                });
        }

        return d;
    }

    public static Label getLabel(String text) {
        return getLabel(Global.GRIDLABELALIGN, text);
    }

    public static Label getLabel(int align, String text) {
        Label l = new Label("<html><p align=\"" +
                ((Global.GRIDLABELALIGN == ALIGN_RIGHT) ? "right" : "left") +
                "\">" + mnemonicRemove(text) + "</p></html>");
        l.setHorizontalAlignment(align);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return l;
    }

    public static Label getLabel(Icon icon) {
        Label l = new Label(icon);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        l.setHorizontalAlignment(Global.GRIDLABELALIGN);

        return l;
    }

    public static Label getLabel(Icon icon, String tooltiptext) {
        Label l = new Label();
        l.setIcon(icon);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        l.setToolTipText(mnemonicRemove(tooltiptext));
        l.setHorizontalAlignment(Global.GRIDLABELALIGN);

        return l;
    }

    public static Label getLabel() {
        Label l = new Label();
        l.setHorizontalAlignment(Global.GRIDLABELALIGN);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return l;
    }

    public static Label getTitleLabel(String text) {
        Label l = getLabel(text);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        l.setHorizontalAlignment(ALIGN_LEFT);
        l.setFont(l.getFont().deriveFont(Font.BOLD));

        return l;
    }

    public static Panel getTitleLabelPanel(String text) {
        Label l = getLabel(text);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        l.setHorizontalAlignment(ALIGN_LEFT);
        l.setFont(l.getFont().deriveFont(Font.BOLD));

        Panel p = getPanel(getBorderLayout());
        p.add(l);

        return p;
    }

    public static Label getHintLabel(String text) {
        Label l = new Label("<html><center>" + mnemonicRemove(text) +
                "</center></html>");

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        l.setBackground(getColor(255, 251, 192));
        l.setForeground(Color.BLACK);
        l.setBorder(new LineBorder(Color.BLACK));
        l.setHorizontalAlignment(ALIGN_CENTER);
        l.setOpaque(true);

        return l;
    }

    public static Label getURLLabel(String text, String tooltiptext,
        final FunctionPointer onClick) {
        Label l = new Label(text);
        l.setToolTipText(tooltiptext);
        l.setForeground(getColor(0, 0, 255));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (onClick != null) {
            l.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        onClick.call();
                    }
                });
        }

        return l;
    }

    public static Label getURLLabel(String text, String tooltiptext,
        final String url) {
        Label l = new Label(text);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        l.setToolTipText(tooltiptext);
        l.setForeground(getColor(0, 0, 255));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        l.addMouseListener(new MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    FileTypeManager.shellExecute(url);
                }
            });

        return l;
    }

    public static List getList() {
        return getList(null, null);
    }

    public static List getList(FunctionPointer onDoubleClick) {
        return getList(onDoubleClick, null);
    }

    public static List getList(FunctionPointer onDoubleClick,
        FunctionPointer onClick) {
        List l = new List(onDoubleClick, onClick);

        if (!isLTR()) {
            l.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return l;
    }

    public static Panel getPanel(int bordersize) {
        Panel p = new Panel(bordersize);

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (useDefaultTooltip) {
            p.setToolTipText(TOOLTIP_DEFAULT);
        }

        return p;
    }

    public static Panel getPanel() {
        Panel p = new Panel(4);

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (useDefaultTooltip) {
            p.setToolTipText(TOOLTIP_DEFAULT);
        }

        return p;
    }

    public static Panel getPanel(boolean noborder) {
        Panel p = new Panel(noborder);

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (useDefaultTooltip) {
            p.setToolTipText(TOOLTIP_DEFAULT);
        }

        return p;
    }

    public static Panel getPanel(LayoutManager l) {
        Panel p = new Panel(l);

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (useDefaultTooltip) {
            p.setToolTipText(TOOLTIP_DEFAULT);
        }

        return p;
    }

    public static Panel getPanel(LayoutManager l, int bordersize) {
        Panel p = new Panel(l, bordersize);

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (useDefaultTooltip) {
            p.setToolTipText(TOOLTIP_DEFAULT);
        }

        return p;
    }

    public static Panel getPanel(LayoutManager l, boolean noborder) {
        Panel p = new Panel(l, noborder);

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (useDefaultTooltip) {
            p.setToolTipText(TOOLTIP_DEFAULT);
        }

        return p;
    }

    public static PCTGridLayout getGridLayout(int cols) {
        return new PCTGridLayout(0, cols, 4, 4);
    }

    public static PCTGridLayout getGridLayout(int cols, int[] widths) {
        return new PCTGridLayout(0, cols, 4, 4, widths);
    }

    public static PCTGridLayout getGridLayout(int rows, int cols) {
        return new PCTGridLayout(rows, cols, 4, 4);
    }

    public static PCTGridLayout getGridLayout(int rows, int cols, int hgap,
        int vgap) {
        return new PCTGridLayout(rows, cols, hgap, vgap);
    }

    public static TableLayout getTableLayout(int cols) {
        return new TableLayout(0, cols, 4, 4);
    }

    public static FlowLayout getFlowLayout() {
        return new FlowLayout();
    }

    public static FlowLayout getFlowLayout(boolean left) {
        return new FlowLayout(FlowLayout.LEADING);
    }

    public static FlowLayout getFlowLayout(int orientation) {
        return new FlowLayout(orientation);
    }

    public static FlowLayout getFlowLayout(int orientation, boolean nogap) {
        return new FlowLayout(orientation, 0, 0);
    }

    public static BorderLayout getBorderLayout() {
        return new BorderLayout();
    }

    public static ComboBox getCombo() {
        ComboBox c = new ComboBox();
        c.setEditable(false);

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return c;
    }

    public static ComboBox getCombo(String description) {
        ComboBox c = new ComboBox(description, null);
        c.setEditable(false);

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return c;
    }

    public static ComboBox getCombo(FunctionPointer onChange) {
        return getCombo((Vector) null, onChange);
    }

    public static ComboBox getCombo(String description, FunctionPointer onChange) {
        return getCombo(description, (Vector) null, onChange);
    }

    public static ComboBox getCombo(String sql, String field) {
        return getCombo("", sql, field, null);
    }

    public static ComboBox getCombo(String description, String sql, String field) {
        return getCombo(description, sql, field, null);
    }

    public static ComboBox getCombo(String sql, String field,
        final FunctionPointer onChange) {
        return getCombo("", sql, field, onChange);
    }

    public static ComboBox getCombo(String description, String sql,
        String field, final FunctionPointer onChange) {
        final ComboBox c = new ComboBox(description, onChange);
        c.setEditable(false);

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        try {
            SQLRecordset r = new SQLRecordset();
            r.openRecordset(sql, "no_table");

            while (!r.getEOF()) {
                if ((r.getField(field) != null) &&
                        !r.getField(field).toString().equals("")) {
                    c.addItem(r.getField(field).toString());
                }

                r.moveNext();
            }

            r.free();
            r = null;
        } catch (Exception e) {
            Global.logException(e, UI.class);
        }

        if (onChange != null) {
            c.addPopupMenuListener(new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                        if (!c.noEvents) {
                            onChange.call();
                        }
                    }

                    public void popupMenuCanceled(PopupMenuEvent evt) {
                    }
                });
            c.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        if (!c.noEvents) {
                            if (isTypedEvent(evt)) {
                                onChange.call();
                            }
                        }
                    }
                });
        }

        return c;
    }

    public static ComboBox getCombo(Vector items) {
        return getCombo(items, null);
    }

    public static ComboBox getCombo(String description, Vector items) {
        return getCombo(description, items, null, null);
    }

    public static ComboBox getCombo(Vector items, final FunctionPointer onChange) {
        return getCombo("", items, onChange);
    }

    public static ComboBox getCombo(String description, Vector items, 
        final FunctionPointer onChange) {
        return getCombo(description, items, onChange, null);
    }

    public static ComboBox getCombo(String description, Vector items, String allstring) {
        return getCombo(description, items, null, allstring);
    }

    public static ComboBox getCombo(String description, Vector items,
        final FunctionPointer onChange, String allstring) {
        final ComboBox c = new ComboBox(description, onChange);
        c.setEditable(false);

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (allstring != null) {
            c.addItem(allstring);
        }

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                c.addItem(items.get(i).toString());
            }
        }

        if (onChange != null) {
            c.addPopupMenuListener(new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                        if (!c.noEvents) {
                            onChange.call();
                        }
                    }

                    public void popupMenuCanceled(PopupMenuEvent evt) {
                    }
                });
            c.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        if (!c.noEvents) {
                            if (isTypedEvent(evt)) {
                                onChange.call();
                            }
                        }
                    }
                });
        }

        return c;
    }

    public static ComboBox getCombo(String[] items) {
        return getCombo(items, null);
    }

    public static ComboBox getCombo(String description, String[] items) {
        return getCombo(description, items, null);
    }

    public static ComboBox getCombo(String[] items,
        final FunctionPointer onChange) {
        return getCombo("", items, onChange);
    }

    public static ComboBox getCombo(String description, String[] items,
        final FunctionPointer onChange) {
        final ComboBox c = new ComboBox(description, onChange);
        c.setEditable(false);

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                c.addItem(items[i]);
            }
        }

        if (onChange != null) {
            c.addPopupMenuListener(new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                        if (!c.noEvents) {
                            onChange.call();
                        }
                    }

                    public void popupMenuCanceled(PopupMenuEvent evt) {
                    }
                });
            c.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        if (!c.noEvents) {
                            if (isTypedEvent(evt)) {
                                onChange.call();
                            }
                        }
                    }
                });
        }

        return c;
    }

    public static ComboBox getCombo(SQLRecordset r, String field) {
        return getCombo("", r, field, null, null, null);
    }

    public static ComboBox getCombo(SQLRecordset r, String field,
        String allstring) {
        return getCombo("", r, field, null, null, allstring);
    }

    public static ComboBox getCombo(SQLRecordset r, String field,
        final FunctionPointer onChange) {
        return getCombo("", r, field, onChange, null, null);
    }

    public static ComboBox getCombo(SQLRecordset r, String field,
        final FunctionPointer onChange, String allstring) {
        return getCombo("", r, field, onChange, null, allstring);
    }

    public static ComboBox getCombo(SQLRecordset r, String field,
        final FunctionPointer onChange, FunctionPointer onLostFocus) {
        return getCombo("", r, field, onChange, onLostFocus, null);
    }

    public static ComboBox getCombo(String description, SQLRecordset r,
        String field) {
        return getCombo(description, r, field, null, null, null);
    }

    public static ComboBox getCombo(String description, SQLRecordset r,
        String field, String allstring) {
        return getCombo(description, r, field, null, null, allstring);
    }

    public static ComboBox getCombo(String description, SQLRecordset r,
        String field, final FunctionPointer onChange) {
        return getCombo(description, r, field, onChange, null, null);
    }

    public static ComboBox getCombo(String description, SQLRecordset r,
        String field, final FunctionPointer onChange, String allstring) {
        return getCombo(description, r, field, onChange, null, allstring);
    }

    public static ComboBox getCombo(String description, SQLRecordset r,
        String field, final FunctionPointer onChange,
        FunctionPointer onLostFocus) {
        return getCombo(description, r, field, onChange, onLostFocus, null);
    }

    /**
     * Creates a combo from a recordset
     * @param description A description for use in search popups
     * @param r The recordset
     * @param field The field to use for display
     * @param onChange Function pointer to an event when the value changes
     * @param allstring If an "all" parameter should be in the list, the text
     * @return
     */
    public static ComboBox getCombo(String description, SQLRecordset r,
        String field, final FunctionPointer onChange,
        final FunctionPointer onLostFocus, String allstring) {
        final ComboBox c = new ComboBox(description, onChange);
        c.setEditable(false);

        if (!isLTR()) {
            c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (allstring != null) {
            c.addItem(allstring);
        }

        try {
            r.moveFirst();

            while (!r.getEOF()) {
                c.addItem(r.getField(field).toString());
                r.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, UI.class);
        }

        if (onChange != null) {
            c.addPopupMenuListener(new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                        if (!c.noEvents) {
                            onChange.call();
                        }
                    }

                    public void popupMenuCanceled(PopupMenuEvent evt) {
                    }
                });
            c.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        if (isTypedEvent(evt)) {
                            onChange.call();
                        }
                    }
                });
        }

        if (onLostFocus != null) {
            c.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        if (!c.noEvents) {
                            onLostFocus.call();
                        }
                    }
                });
        }

        return c;
    }

    public static DesktopPane getDesktopPane() {
        return new DesktopPane();
    }

    public static Dimension getDimension(int width, int height) {
        Dimension d = new UI.Dimension();
        d.setSize(width, height);

        return d;
    }

    public static FileChooser getFileChooser() {
        return new FileChooser();
    }

    public static FileChooser getFileChooser(String startpath) {
        return new FileChooser(startpath);
    }

    /** Returns a file chooser for browing for images */
    public static FileChooser getImageFileChooser(String startpath) {
        FileChooser f = new FileChooser(startpath);
        f.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }

                    String ext = f.getAbsolutePath().toLowerCase()
                                  .substring(f.getAbsolutePath().lastIndexOf(".") +
                            1);

                    return ext.equals("png") || ext.equals("gif") ||
                    ext.equals("jpg") || ext.equals("jpeg");
                }

                public String getDescription() {
                    return "Image Files (png, gif, jpg, jpeg)";
                }
            });

        // Set up the preview pane
        f.setAccessory(new FilePreviewPane(f));

        return f;
    }

    public static HTMLBrowser getHTMLBrowser() {
        return getHTMLBrowser(null, null);
    }

    /**
     * @param onHyperlinkClick must accept a string argument for the URL
     * @return
     */
    public static HTMLBrowser getHTMLBrowser(FunctionPointer onHyperlinkClick) {
        return getHTMLBrowser(null, onHyperlinkClick);
    }

    /**
     * @param status a Label where the hovering URL can be set
     * @param onHyperlinkClick must accept a string argument for the URL
     * @return
     */
    public static HTMLBrowser getHTMLBrowser(Label status,
        FunctionPointer onHyperlinkClick) {
        HTMLBrowser h = new HTMLBrowser(status, onHyperlinkClick);

        if (!isLTR()) {
            h.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return h;
    }

    public static MenuBar getMenuBar() {
        MenuBar m = new MenuBar();

        if (!isLTR()) {
            m.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return m;
    }

    public static Menu getMenu(String text) {
        return getMenu(text, ' ', null);
    }

    public static Menu getMenu(String text, char mnemonic, Icon icon) {
        Menu m = new Menu();

        if (!isLTR()) {
            m.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        m.setText(mnemonicRemove(text));

        char mn = mnemonicFromText(text, mnemonic);

        if (mn != ' ') {
            m.setMnemonic(mn);
        }

        if (icon != null) {
            m.setIcon(icon);
        }

        return m;
    }

    public static MenuItem getMenuItem(String text, FunctionPointer onClick) {
        return getMenuItem(text, ' ', null, null, onClick);
    }

    public static MenuItem getMenuItem(String text, char mnemonic,
        FunctionPointer onClick) {
        return getMenuItem(text, mnemonic, null, null, onClick);
    }

    public static MenuItem getMenuItem(String text, char mnemonic, Icon icon,
        final FunctionPointer onClick) {
        return getMenuItem(text, mnemonic, icon, null, onClick);
    }

    public static MenuItem getMenuItem(String text, char mnemonic, Icon icon,
        ASMAccelerator hotkey, final FunctionPointer onClick) {
        MenuItem m = new MenuItem();

        if (!isLTR()) {
            m.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        m.setText(mnemonicRemove(text));

        char mn = mnemonicFromText(text, mnemonic);

        if (mn != ' ') {
            m.setMnemonic(mn);
        }

        if (icon != null) {
            m.setIcon(icon);
        }

        if (hotkey != null) {
            m.setAccelerator(hotkey.getKey());
        }

        if (onClick != null) {
            m.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onClick.call();
                    }
                });
        }

        return m;
    }

    public static Separator getSeparator() {
        return new Separator();
    }

    public static ProgressBar getProgressBar() {
        ProgressBar p = new ProgressBar();

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return p;
    }

    public static PasswordField getPasswordField() {
        PasswordField p = new PasswordField();

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return p;
    }

    public static PasswordField getPasswordField(String tooltip) {
        PasswordField p = new PasswordField();

        if (!isLTR()) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        p.setToolTipText(tooltip);

        return p;
    }

    public static SearchTextField getSearchTextField(String tooltip,
        FunctionPointer onSearch) {
        return getSearchTextField(tooltip, false, onSearch);
    }

    public static SearchTextField getSearchTextField(String tooltip,
        boolean enabled, FunctionPointer onSearch) {
        return getSearchTextField(tooltip, null, enabled, onSearch, null);
    }

    public static SearchTextField getSearchTextField(String tooltip,
        String buttontooltip, boolean enabled, FunctionPointer onSearch,
        FunctionPointer onChange) {
        return getSearchTextField(tooltip, buttontooltip, enabled, null,
            onSearch, onChange);
    }

    public static SearchTextField getSearchTextField(String tooltip,
        String buttontooltip, boolean enabled, Icon icon,
        FunctionPointer onSearch, FunctionPointer onChange) {
        SearchTextField s = new SearchTextField(tooltip, buttontooltip,
                enabled, icon, onSearch, onChange);

        if (!isLTR()) {
            s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return s;
    }

    public static TextField getTextField() {
        TextField t = new TextField();

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static TextField getTextField(String tooltip) {
        TextField t = new TextField();

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        t.setToolTipText(tooltip);

        return t;
    }

    public static TextField getTextField(FunctionPointer onChange) {
        return getTextField(null, onChange);
    }

    public static TextField getTextField(String tooltip,
        final FunctionPointer onChange) {
        return getTextField(tooltip, onChange, null);
    }

    public static TextField getTextField(String tooltip,
        final FunctionPointer onChange, final FunctionPointer onLeave) {
        return getTextField(tooltip, onChange, onLeave, null);
    }

    public static TextField getTextField(String tooltip,
        final FunctionPointer onChange, final FunctionPointer onLeave,
        final FunctionPointer onEnterPressed) {
        TextField t = new TextField() {
                public void paste() {
                    super.paste();

                    if (onChange != null) {
                        onChange.call();
                    }
                }
            };

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (tooltip != null) {
            t.setToolTipText(tooltip);
        }

        if (onChange != null) {
            t.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (isTypedEvent(e)) {
                            onChange.call();
                        }
                    }
                });
        }

        if (onEnterPressed != null) {
            t.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            onEnterPressed.call();
                        }
                    }
                });
        }

        if (onLeave != null) {
            t.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        onLeave.call();
                    }
                });
        }

        return t;
    }

    public static TextArea getTextArea() {
        return getTextArea(null);
    }

    public static TextArea getTextArea(String tooltip) {
        return getTextArea(tooltip, null);
    }

    public static TextArea getTextArea(String tooltip,
        final FunctionPointer onChange) {
        return getTextArea(tooltip, onChange, null);
    }

    public static TextArea getTextArea(String tooltip,
        final FunctionPointer onChange, final FunctionPointer onLeave) {
        TextArea t = new TextArea() {
                public void paste() {
                    super.paste();

                    if (onChange != null) {
                        onChange.call();
                    }
                }
            };

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        if (tooltip != null) {
            t.setToolTipText(tooltip);
        }

        if (!System.getProperty("asm.fontsize.textarea", "UNSET").equals("UNSET")) {
            try {
                float newsize = Float.parseFloat(System.getProperty(
                            "asm.fontsize.textarea"));
                t.setFont(t.getFont().deriveFont(newsize));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        t.setLineWrap(true);
        t.setWrapStyleWord(true);

        if (onChange != null) {
            t.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (isTypedEvent(e)) {
                            onChange.call();
                        }
                    }
                });
        }

        if (onLeave != null) {
            t.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        onLeave.call();
                    }
                });
        }

        return t;
    }

    public static TabbedPane getTabbedPane() {
        TabbedPane t = new TabbedPane();

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static TabbedPane getTabbedPane(final FunctionPointer onChange) {
        TabbedPane t = new TabbedPane();
        t.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    onChange.call();
                }
            });

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static ToolBar getToolBar() {
        ToolBar t = new ToolBar();

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static ToolBar getToolBar(boolean vertical) {
        ToolBar t = new ToolBar(vertical);

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static Table getTable(FunctionPointer onClick,
        FunctionPointer onDoubleClick) {
        return getTable(onClick, onDoubleClick, null, null, false);
    }

    public static Table getTable(FunctionPointer onClick,
        FunctionPointer onDoubleClick, ToolBar toolbar) {
        return getTable(onClick, onDoubleClick, toolbar, false);
    }

    public static Table getTable(FunctionPointer onClick,
        FunctionPointer onDoubleClick, FunctionPointer onEnter, ToolBar toolbar) {
        Table t = new Table(onClick, onDoubleClick, onEnter, toolbar, null,
                false);

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static Table getTable(FunctionPointer onClick,
        FunctionPointer onDoubleClick, ToolBar toolbar, boolean multiselect) {
        Table t = new Table(onClick, onDoubleClick, null, toolbar, null,
                multiselect);

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static Table getTable(FunctionPointer onClick,
        FunctionPointer onDoubleClick, ToolBar toolbar,
        ASMCellRenderer renderer, boolean multiselect) {
        Table t = new Table(onClick, onDoubleClick, null, toolbar, renderer,
                multiselect);

        if (!isLTR()) {
            t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return t;
    }

    public static void registerTabOrder(Vector components, Object parent,
        Object focusedComponent) {
        if (Global.focusManager != null) {
            Global.focusManager.addComponentSet(components, parent,
                focusedComponent);
        }
    }

    /** TODO: Delegate to UI.unregisterTabOrder() and in other components */
    public static void unregisterTabOrder(Object parent) {
        if (Global.focusManager != null) {
            Global.focusManager.removeComponentSet(parent);
        }
    }

    public static boolean isLTR() {
        return !Global.isLanguageRTL();
    }

    public static void setFocusManager(FlexibleFocusManager f) {
        javax.swing.FocusManager.setCurrentManager(f);
    }

    /** Adds a component to a container
     * @param container
     * @param c
     * @return
     */
    public static Component addComponent(Container container, Component c) {
        // Put components in scrollpanes
        if (c instanceof TextArea || c instanceof HTMLBrowser ||
                c instanceof List) {
            ScrollPane s = UI.getScrollPane(c);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            container.add(s);
        } else if (c instanceof Table) {
            ScrollPane s = UI.getScrollPane(c);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            container.add(s);
        } else {
            container.add(c);
        }

        return c;
    }

    /** Adds a component with a label to the container
     * @param container
     * @param labeltext
     * @param c
     * @return
     */
    public static Component addComponent(Container container, String labeltext,
        Component c) {
        container.add(getLabel(labeltext));

        // Put components in scrollpanes
        if (c instanceof TextArea || c instanceof HTMLBrowser ||
                c instanceof List) {
            ScrollPane s = UI.getScrollPane(c);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            container.add(s);
        } else if (c instanceof Table) {
            ScrollPane s = UI.getScrollPane(c);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            container.add(s);
        } else {
            container.add(c);
        }

        return c;
    }

    /** Adds a component and label to the container
     * @param container
     * @param labeltext
     * @param c
     * @return
     */
    public static Component addComponent(Container container, Component label,
        Component c) {
        container.add(label);

        // Put components in scrollpanes
        if (c instanceof TextArea || c instanceof HTMLBrowser ||
                c instanceof List) {
            ScrollPane s = UI.getScrollPane(c);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            container.add(s);
        } else if (c instanceof Table) {
            ScrollPane s = UI.getScrollPane(c);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            container.add(s);
        } else {
            container.add(c);
        }

        return c;
    }

    public static boolean osIsMacOSX() {
        return Utils.englishLower(System.getProperty("os.name"))
                    .indexOf("mac os") != -1;
    }

    public static boolean osIsLinux() {
        return Utils.englishLower(System.getProperty("os.name")).indexOf("linux") != -1;
    }

    public static boolean osIsSolaris() {
        return (Utils.englishLower(System.getProperty("os.name"))
                     .indexOf("solaris") != -1) ||
        (Utils.englishLower(System.getProperty("os.name")).indexOf("sunos") != -1);
    }

    public static boolean osIsWindows() {
        return Utils.englishLower(System.getProperty("os.name"))
                    .indexOf("windows") != -1;
    }

    /**
     * Returns true if java.awt.Desktop support is available
     */
    public static boolean osShellExecuteAvailable() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop d = Desktop.getDesktop();

                return d.isSupported(Desktop.Action.BROWSE) &&
                d.isSupported(Desktop.Action.OPEN);
            }
        } catch (Throwable t) {
        }

        return false;
    }

    public static void osBrowse(String uri) throws Exception {
        Desktop d = Desktop.getDesktop();
        d.browse(new java.net.URI(uri));
    }

    public static void osOpen(String file) throws Exception {
        Desktop d = Desktop.getDesktop();

        try {
            d.edit(new java.io.File(file));
        } catch (Exception e) {
            e.printStackTrace();
            // Fall back to Open if we can't edit
            d.open(new java.io.File(file));
        }
    }

    public static void cursorToWait() {
        if (Dialog.theParent != null) {
            Dialog.theParent.setCursor(Cursor.getPredefinedCursor(
                    Cursor.WAIT_CURSOR));
        }
    }

    public static void cursorToPointer() {
        if (Dialog.theParent != null) {
            Dialog.theParent.setCursor(Cursor.getPredefinedCursor(
                    Cursor.DEFAULT_CURSOR));
        }
    }

    /** Given a toolbar, generates a JPopupMenu which can be used as
     *  a context menu.
     * @param b
     * @return
     */
    public static JPopupMenu toolbarToPopupMenu(JToolBar t) {
        JPopupMenu p = new JPopupMenu();
        Vector dupes = new Vector();

        for (int i = 0; i < t.getComponentCount(); i++) {
            if (t.getComponent(i) instanceof JButton) {
                JButton b = (JButton) t.getComponent(i);

                if (!dupes.contains(b.getToolTipText())) {
                    dupes.add(b.getToolTipText());

                    JMenuItem m = new JMenuItem();
                    m.setText(b.getToolTipText());
                    m.setIcon(b.getIcon());
                    m.setEnabled(b.isEnabled());
                    m.addActionListener((ActionListener) b.getListeners(
                            ActionListener.class)[0]);
                    p.add(m);
                }
            } else {
                p.addSeparator();
            }
        }

        return p;
    }

    public static java.awt.Dimension getScreenSize() {
        return java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    }

    /** Centers a window on the screen - will also check if the
     *  window is too big for the screen and scale it accordingly.
     *  @param w The Window descendant to scale
     */
    public static void centerWindow(Window w) {
        // Get framesize and screen size
        java.awt.Dimension frameSize = w.getSize();
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit()
                                               .getScreenSize();
        int sw = screenSize.width;
        int sh = screenSize.height;

        // In dual monitor setups, the width will be reported as
        // both screens - if we spot this (width is greater than 
        // height * 2), we half the screen width. This is a bit
        // hacky, but works for the most common dual monitor setups.
        if (sw > (sh * 2)) {
            sw = sw / 2;
        }

        // Reduce the size of our window if it is bigger than the 
        // screen estate
        if ((frameSize.height > sh) || (frameSize.width > sw)) {
            frameSize.height = sh;
            frameSize.width = sw;
            w.setSize(frameSize);
        }

        // Center the window on screen
        w.setLocation((sw - frameSize.width) / 2, (sh - frameSize.height) / 2);
    }

    /** Converts an image to a buffered image */
    public static BufferedImage toBufferedImage(Image i) {
        if (i instanceof BufferedImage) {
            return (BufferedImage) i;
        }

        BufferedImage b = new BufferedImage(i.getWidth(null),
                i.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.createGraphics();
        g.drawImage(i, 0, 0, null);
        g.dispose();

        return b;
    }

    /** Given a standard image, this routine will scale it to the
     *  width/height of choosing, retaining aspect ratio, but
     *  never going over the largest dimension.
     */
    public static Image scaleImageOld(Image inImage, int width, int height) {
        int w = inImage.getWidth(null);
        int h = inImage.getHeight(null);

        if (w < h) {
            double aspectRatio = ((double) w) / ((double) h);
            width = (int) ((double) height * (double) aspectRatio);
        } else {
            double aspectRatio = ((double) h) / ((double) w);
            height = (int) ((double) width * (double) aspectRatio);
        }

        // Render our old image onto a new buffered image, scaling
        // as we go.
        BufferedImage out = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) out.getGraphics();
        g2.drawImage(inImage, 0, 0, width, height, null);
        g2.dispose();

        Global.logDebug("Original: " + w + "x" + h + ", New: " +
            out.getWidth(null) + "x" + out.getHeight(null), "UI.scaleImage");

        return out;
    }

    /** New image scaling - uses multi step bilinear technique to obtain
      * much smoother downscaled images
      * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
      */
    public static Image scaleImage(Image inImage, int width, int height) {
        int w = inImage.getWidth(null);
        int h = inImage.getHeight(null);
        int wo = w;
        int ho = h;

        // If the target size is bigger than the original, then
        // we aren't scaling the image down and should use the
        // old system
        if ((width > w) || (height > h)) {
            return scaleImageOld(inImage, width, height);
        }

        if (w < h) {
            double aspectRatio = ((double) w) / ((double) h);
            width = (int) ((double) height * (double) aspectRatio);
        } else {
            double aspectRatio = ((double) h) / ((double) w);
            height = (int) ((double) width * (double) aspectRatio);
        }

        BufferedImage ret = toBufferedImage(inImage);

        do {
            if (w > width) {
                w /= 2;

                if (w < width) {
                    w = width;
                }
            }

            if (h > height) {
                h /= 2;

                if (h < height) {
                    h = height;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();
            ret = tmp;
        } while ((w != width) || (h != height));

        Global.logDebug("Original: " + wo + "x" + ho + ", New: " +
            ret.getWidth(null) + "x" + ret.getHeight(null), "UI.scaleImage");

        return ret;
    }

    public static void scaleImage(String inputfile, String outputfile,
        int width, int height) {
        try {
            Image inImage = UI.loadImage(inputfile);
            Image outImage = UI.scaleImage(inImage, width, height);
            UI.saveImageAsJpeg(outputfile, outImage);
            inImage.flush();
            inImage = null;
            outImage = null;
        } catch (Exception e) {
            Global.logException(e, UI.class);
        }
    }

    public static Image loadImage(String file) {
        return new ImageIcon(file).getImage();
    }

    /** Determines if a key event qualifies as the user typing
      * something.
      */
    public static boolean isTypedEvent(KeyEvent e) {
        // If we don't have a char, that's not typing
        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            return false;
        }

        // In fact, if anything but SHIFT is used as a modifier, that's
        // not typing
        if (e.isControlDown() || e.isAltDown() || e.isMetaDown() ||
                e.isAltGraphDown()) {
            return false;
        }

        return true;
    }

    /** Returns the height a text component should be */
    public static int getTextBoxHeight() {
        return 23;
    }

    /** Returns the height a combo box should be */
    public static int getComboBoxHeight() {
        return 29;
    }

    /** Returns the default width of a text component */
    public static int getTextBoxWidth() {
        return 200;
    }

    public static Color getColor(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public static String getRendererName() {
        return "ASMSwing 1.20";
    }

    public static String getRendererVersion() {
        return "1.10";
    }

    public static void disableRendererMessages() {
    }

    public static void invokeIn(final Runnable run, int ms) {
        java.util.Timer timer = new java.util.Timer();
        java.util.TimerTask task = new java.util.TimerTask() {
                public void run() {
                    UI.invokeLater(run);
                }
            };

        timer.schedule(task, (long) ms);
    }

    public static void invokeLater(Runnable run) {
        try {
            SwingUtilities.invokeLater(run);
        } catch (Exception e) {
        }
    }

    public static void invokeNow(Runnable run) {
        try {
            SwingUtilities.invokeAndWait(run);
        } catch (Exception e) {
        }
    }

    public static void invokeAndWait(Runnable run) {
        try {
            SwingUtilities.invokeAndWait(run);
        } catch (Exception e) {
        }
    }

    public static void saveImageAsJpeg(String file, Image image) {
        try {
            ImageIO.write(toBufferedImage(image), "jpg", new File(file));
        } catch (Exception e) {
            Global.logException(e, UI.class);
        }
    }

    /*
     * Old code that relied on com.sun.image.jpeg.JPEGImageEncoder
    public static void saveImageAsJpeg(String file, Image image) {
        try {
            BufferedImage bimage = toBufferedImage(image);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out);
            enc.encode(bimage);

            FileOutputStream f = new FileOutputStream(file);
            out.writeTo(f);
            f.close();
        } catch (Exception e) {
            Global.logException(e, UI.class);
        }
    }
    */
    public static String messageOK() {
        return Global.i18n("uianimalname", "Ok");
    }

    public static String messageCancel() {
        return Global.i18n("uianimalname", "Cancel");
    }

    public static String messageYes() {
        return Global.i18n("uianimalname", "Yes");
    }

    public static String messageNo() {
        return Global.i18n("uianimalname", "No");
    }

    public static String messageDeleteConfirm() {
        return Global.i18n("uianimalname", "Really_delete_this_record?");
    }

    public static String messageReallyDelete() {
        return Global.i18n("uianimalname", "Really_Delete");
    }

    public static String messageDeleteError() {
        return Global.i18n("uianimalname",
            "An_error_occurred_deleting_the_record:\n");
    }

    public static String messageSearch() {
        return Global.i18n("uilostandfound", "Search");
    }

    public static String messageOpen() {
        return Global.i18n("uilostandfound", "Open");
    }

    public static String messageSelect() {
        return Global.i18n("uiowner", "Select");
    }

    public static String messageNoSavePermission() {
        return Global.i18n("uianimal", "Permission_Denied_Save");
    }

    public static String messageAudit(Date createdDate, String createdBy,
        Date lastChangedDate, String lastChangedBy) {
        return Global.i18n("uianimal", "created_lastchange",
            Utils.formatDateTimeLong(createdDate), createdBy,
            Utils.formatDateTimeLong(lastChangedDate), lastChangedBy);
    }

    public static Icon iconSearch() {
        return IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_SEARCH);
    }

    public static Icon iconOpen() {
        return IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_OPEN);
    }

    /** The threshold at which to show a search box based on number
     *  of items in the list.
     */
    public static int listSearchThreshold() {
        return 20;
    }

    public static Spinner getSpinner(int min, int max, String tooltip,
        FunctionPointer onChange) {
        Spinner s = new Spinner(min, max, tooltip, onChange);

        if (!isLTR()) {
            s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return s;
    }

    public static Spinner getSpinner(int min, int max) {
        return getSpinner(min, max, null, null);
    }

    public static Spinner getSpinner(int min, int max, FunctionPointer onChange) {
        return getSpinner(min, max, null, onChange);
    }

    public static ScrollPane getScrollPane(UI.Panel p) {
        ScrollPane s = new ScrollPane(p);

        if (!isLTR()) {
            s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return s;
    }

    public static ScrollPane getScrollPane(Component c) {
        ScrollPane s = new ScrollPane(c);

        if (!isLTR()) {
            s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        return s;
    }

    public static VerticalSplitPane getVerticalSplitPane(Component c1, Component c2) {
        return new VerticalSplitPane(c1, c2);
    }

    public static HorizontalSplitPane getHorizontalSplitPane(Component c1, Component c2) {
        if (isLTR()) 
            return new HorizontalSplitPane(c1, c2);
        else
            return new HorizontalSplitPane(c2, c1);
    }

    public static BoxLayout getBoxLayout(UI.Panel p, int axis) {
        return new BoxLayout(p, axis);
    }

    public static class VerticalSplitPane extends JSplitPane {
        public VerticalSplitPane(Component c1, Component c2) {
            super(JSplitPane.VERTICAL_SPLIT, true, c1, c2);
        }
    }

    public static class HorizontalSplitPane extends JSplitPane {
        public HorizontalSplitPane(Component c1, Component c2) {
            super(JSplitPane.HORIZONTAL_SPLIT, true, c1, c2);
        }
    }

    public static class ScrollPane extends JScrollPane {
        public ScrollPane(UI.Panel p) {
            super(p);
        }

        public ScrollPane(Component c) {
            super(c);
        }
    }

    public static class HTMLBrowser extends JEditorPane
        implements HyperlinkListener, Printable, Serializable {
        private Label status = null;
        private FunctionPointer onHyperlinkClick = null;

        public HTMLBrowser(Label status, FunctionPointer onHyperlinkClick) {
            super();
            this.status = status;
            this.onHyperlinkClick = onHyperlinkClick;
            this.setContentType("text/html");
            this.setEditable(false);
            this.setOpaque(true);
            addHyperlinkListener(this);
        }

        public void hyperlinkUpdate(HyperlinkEvent event) {
            if ((event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) &&
                    (onHyperlinkClick != null)) {
                onHyperlinkClick.call(new Object[] { event.getURL().toString() });
            } else if (event.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                if ((status != null) && (event.getURL() != null)) {
                    status.setText(event.getURL().toString());
                }
            } else if (event.getEventType() == HyperlinkEvent.EventType.EXITED) {
                if (status != null) {
                    status.setText("");
                }
            }
        }

        public void setContent(String content) {
            setText(content);
            setContentType("text/html");
        }

        public void setURL(String url) throws IOException {
            setPage(url);
        }

        public void setPage(final String iurl) throws IOException {
            // Mac and Windows go a bit weird with file URLs if they
            // aren't prefixed with exactly three forward slashes.
            // I can understand the Mac since the third is the root
            // of the filesystem, but Windows??? Anyway, we do a
            // check here and make sure there are enough.
            String url = iurl;

            if ((url.startsWith("file://") || url.startsWith("file:/")) &&
                    !url.startsWith("file:///")) {
                if (url.startsWith("file://")) {
                    url = "file:///" + url.substring("file://".length());
                } else if (url.startsWith("file:/")) {
                    url = "file:///" + url.substring("file:/".length());
                }
            }

            // This is needed to prevent URL caching if we want to update
            // the page we're looking at by going there again
            getDocument().putProperty(Document.StreamDescriptionProperty, null);
            HTMLBrowser.super.setPage(url);
        }

        public int print(Graphics g, PageFormat pf, int pageIndex)
            throws PrinterException {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.black); //set default foreground color to black

            RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

            java.awt.Dimension d = this.getSize(); //get size of document
            double panelWidth = d.width; //width in pixels
            double panelHeight = d.height; //height in pixels
            double pageHeight = pf.getImageableHeight(); //height of printer page
            double pageWidth = pf.getImageableWidth(); //width of printer page
            double scale = pageWidth / panelWidth;
            int totalNumPages = (int) Math.ceil((scale * panelHeight) / pageHeight);

            // Make sure not print empty pages
            if (pageIndex >= totalNumPages) {
                return Printable.NO_SUCH_PAGE;
            }

            // Shift Graphic to line up with beginning of print-imageable region
            g2.translate(pf.getImageableX(), pf.getImageableY());
            // Shift Graphic to line up with beginning of next page to print
            g2.translate(0f, -pageIndex * pageHeight);
            // Scale the page so the width fits...
            g2.scale(scale, scale);
            this.paint(g2); //repaint the page for printing

            return Printable.PAGE_EXISTS;
        }

        public boolean print() {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintable(this);
            pj.printDialog();

            try {
                pj.print();
            } catch (Exception PrintException) {
                return false;
            }

            return true;
        }
    }

    /** Class to handle ASM tables */
    public static class Table extends JTable implements MouseListener,
        KeyListener {
        private boolean multiselect = false;
        private FunctionPointer onClick = null;
        private FunctionPointer onDoubleClick = null;
        private FunctionPointer onEnter = null;
        private ASMCellRenderer renderer = null;
        private UI.ToolBar toolbar = null;
        private SortableTableModel model = null;

        public Table(FunctionPointer onClick, FunctionPointer onDoubleClick,
            FunctionPointer onEnter, UI.ToolBar toolbar,
            ASMCellRenderer renderer, boolean isMultiSelect) {
            multiselect = isMultiSelect;
            this.onClick = onClick;
            this.onDoubleClick = onDoubleClick;
            this.onEnter = onEnter;
            this.renderer = renderer;
            this.toolbar = toolbar;
            addMouseListener(this);
            addKeyListener(this);
            setShowGrid(false);

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public void keyPressed(KeyEvent evt) {
            if ((onEnter != null) && (evt.getKeyCode() == KeyEvent.VK_ENTER)) {
                onEnter.call();
            }
        }

        public void keyReleased(KeyEvent evt) {
            onClick.call();
        }

        public void keyTyped(KeyEvent evt) {
        }

        public TableColumn getColumn(int index) {
            // TODO: Valid?
            return getColumn(getColumnName(index));
        }

        public void mouseClicked(MouseEvent evt) {
            // Double click?
            if (evt.getClickCount() == 2) {
                if (onDoubleClick != null) {
                    onDoubleClick.call();

                    return;
                }
            }

            // Single click?
            if (onClick != null) {
                onClick.call();
            }

            // Is it a right click? Show the popup menu of the toolbar
            // if we have one.
            if (evt.getButton() != 1) {
                if (toolbar != null) {
                    UI.toolbarToPopupMenu(toolbar)
                      .show(this, evt.getX(), evt.getY());
                }

                return;
            }
        }

        public void mouseEntered(MouseEvent arg0) {
        }

        public void mouseExited(MouseEvent arg0) {
        }

        public void mousePressed(MouseEvent arg0) {
        }

        public void mouseReleased(MouseEvent arg0) {
        }

        public void setTableData(String[] columnheaders, String[][] data,
            int rows, int idColumn) {
            setTableData(columnheaders, data, rows, -1, idColumn);
        }

        /**
         * Sets the data for the table
         * @param columnheaders The column headers
         * @param data The data
         * @param rows The number of rows in the data
         * @param idColumn The index of the column containing the ID field
         */
        public void setTableData(final String[] columnheaders,
            final String[][] data, final int rows, final int maxcols,
            final int idColumn) {
            // Shunt this onto the dispatch thread - seems to hang on windows sometimes
            UI.invokeLater(new Runnable() {
                    public void run() {
                        // Set the new model for the data
                        model = new SortableTableModel();

                        if (maxcols != -1) {
                            model.setData(columnheaders, data, rows, maxcols,
                                idColumn);
                        } else {
                            model.setData(columnheaders, data, rows, idColumn);
                        }

                        setModel(model);

                        if (multiselect) {
                            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                        }

                        model.addMouseListenerToHeaderInTable(Table.this);
                        model.packColumns(5);
                        model.fireTableDataChanged();

                        // If we have a renderer, use it
                        if (renderer != null) {
                            renderer.setTableModel(model);

                            int columnCount = model.getColumnCount();
                            TableColumnModel colmod = getColumnModel();
                            TableColumn column = colmod.getColumn(columnCount -
                                    1);

                            for (int i = 0; i < columnCount; i++) {
                                column = colmod.getColumn(i);
                                column.setCellRenderer((TableCellRenderer) renderer);
                            }
                        }
                    }
                });
        }

        /** Returns the selected ID field in the table */
        public int getSelectedID() {
            if (getSelectedRow() == -1) {
                return -1;
            }

            return Integer.parseInt(model.getIDAt(getSelectedRow()));
        }

        /**
         * Updates a given row in the table for view
         * @param row
         */
        public void updateRow(int row) {
            int from = row;
            from--;

            if (from < 0) {
                from = 0;
            }

            int to = row;
            to++;

            if (to > (getRowCount() - 1)) {
                to = getRowCount() - 1;
            }

            // TODO: Need to fire swing tablemodel events?
            // setModelDirty(true);
            // refreshTable(from, to);
        }

        /**
         * Updates the selected row in the table viewer - for use when
         * you alter the model for the highlighted row.
         */
        public void updateSelectedRow() {
            updateRow(getSelectedRow());
        }
    }

    public static class Button extends JButton {
        public Button() {
            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }
    }

    public static class CheckBox extends JCheckBox {
        public CheckBox() {
            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }
    }

    public static class ComboBox extends JPanel implements KeyListener {
        public boolean noEvents = false;
        FunctionPointer onChange = null;
        JComboBox cbo = new JComboBox();
        UI.Button b = UI.getButton(null, null, ' ',
                IconManager.getIcon(IconManager.SEARCHSMALL),
                UI.fp(this, "actionSearch"));
        boolean shownSearch = false;
        String description = "";
        PopupMenuListener popupMenuListener = null;

        public ComboBox() {
            this("", null);
        }

        public ComboBox(String description, FunctionPointer onChange) {
            this.description = description;
            this.onChange = onChange;

            if (useDefaultTooltip) {
                cbo.setToolTipText(TOOLTIP_DEFAULT);
            }

            cbo.setPreferredSize(UI.getDimension(UI.getTextBoxWidth(),
                    UI.getComboBoxHeight()));

            cbo.addKeyListener(this);

            if (!UI.isLTR()) {
                cbo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            }

            setLayout(getBorderLayout());
            add(cbo, UI.BorderLayout.CENTER);

            // Add space at the right side - the same size as the button that
            // appears for lots of items. This is so multiple combo boxes
            // line up properly - this only looks good on animal details screen,
            // so disabled
            /*
            Label l = UI.getLabel(IconManager.getIcon(IconManager.MENUBLANK));
            if (osIsWindows())
                l.setPreferredSize(UI.getDimension(24, 16));
            else
                l.setPreferredSize(UI.getDimension(28, 16));
            add(l, UI.BorderLayout.EAST);
            */
        }

        public void keyPressed(KeyEvent e) {
            // Is it a space? Are we showing the
            // search button? Is the box not
            // editable? If so, fire the search
            if ((e.getKeyCode() == e.VK_SPACE) && shownSearch &&
                    !cbo.isEditable()) {
                actionSearch();
            }
        }

        public void stopEvents() {
            noEvents = true;
        }

        public void startEvents() {
            noEvents = false;
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void addPopupMenuListener(PopupMenuListener l) {
            popupMenuListener = l;
            cbo.addPopupMenuListener(l);
        }

        public void addKeyListener(KeyListener l) {
            cbo.addKeyListener(l);
        }

        public void setEditable(boolean b) {
            cbo.setEditable(b);

            // Make sure key presses are picked up for changing the combo text
            ((JTextField) cbo.getEditor().getEditorComponent()).addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        // Look for a normal typed event, or the V key for a paste
                        if (isTypedEvent(e) ||
                                (e.getKeyCode() == KeyEvent.VK_V)) {
                            if ((onChange != null) && !noEvents) {
                                onChange.call();
                            }
                        }
                    }
                });
        }

        public boolean isEditable() {
            return cbo.isEditable();
        }

        public int getSelectedIndex() {
            return cbo.getSelectedIndex();
        }

        public void setSelectedIndex(int i) {
            cbo.setSelectedIndex(i);
        }

        public Object getSelectedItem() {
            return cbo.getSelectedItem();
        }

        public void setSelectedItem(Object o) {
            cbo.setSelectedItem(o);
        }

        public Object getItemAt(int i) {
            return cbo.getItemAt(i);
        }

        public int getItemCount() {
            return cbo.getItemCount();
        }

        public void removeAllItems() {
            cbo.removeAllItems();
        }

        public void setToolTipText(String s) {
            cbo.setToolTipText(s);
        }

        public boolean isEnabled() {
            return cbo.isEnabled();
        }

        public void addItem(Object o) {
            cbo.addItem(o);

            if ((cbo.getItemCount() > listSearchThreshold()) && !shownSearch) {
                shownSearch = true;

                UI.ToolBar t = UI.getToolBar();
                t.add(b);
                add(t, UI.isLTR() ? UI.BorderLayout.EAST : UI.BorderLayout.WEST);
            }
        }

        public JComboBox getCombo() {
            return cbo;
        }

        public void setEnabled(boolean e) {
            cbo.setEnabled(e);
            b.setEnabled(e);
        }

        public void actionSearch() {
            Object[] o = new Object[(cbo.getModel().getSize())];

            for (int i = 0; i < o.length; i++)
                o[i] = cbo.getModel().getElementAt(i);

            String s = (String) Dialog.getInput(description, description, o,
                    getSelectedItem());

            if (s != null) {
                cbo.setSelectedItem(s);

                if (popupMenuListener != null) {
                    popupMenuListener.popupMenuWillBecomeInvisible(null);
                }
            }
        }
    }

    public static class Spinner extends JSpinner {
        public Spinner(int min, int max, String tooltip,
            final FunctionPointer onChange) {
            super(new SpinnerNumberModel(min, min, max, 1));

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }

            if (tooltip != null) {
                setToolTipText(tooltip);
            }

            setWidth(UI.getTextBoxWidth());

            if (onChange != null) {
                addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            onChange.call();
                        }
                    });
            }
        }

        public void setWidth(int w) {
            setPreferredSize(UI.getDimension(w, UI.getTextBoxHeight()));
        }
    }

    public static class Label extends JLabel {
        public Label() {
            super();

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public Label(String text) {
            super();
            setText(text);

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public Label(Icon icon) {
            super(icon);

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public void setText(String text) {
            // Swap line breaks for HTML breaks
            if (text == null) {
                text = "";
            }

            text = text.replaceAll("\n", "<br />");

            // If we replaced one, add HTML tags
            if (text.indexOf("<br") != -1) {
                text = "<html><p>" + text + "</p></html>";
            }

            super.setText(text);
        }
    }

    public static class List extends JList implements MouseListener {
        FunctionPointer onClick = null;
        FunctionPointer onDoubleClick = null;

        public List() {
            this(null, null);
        }

        public List(FunctionPointer onDoubleClick) {
            this(onDoubleClick, null);
        }

        public List(FunctionPointer onDoubleClick, FunctionPointer onClick) {
            this.onClick = onClick;
            this.onDoubleClick = onDoubleClick;
            this.addMouseListener(this);
        }

        public void mouseClicked(MouseEvent evt) {
            // Double click?
            if (evt.getClickCount() == 2) {
                if (onDoubleClick != null) {
                    onDoubleClick.call();

                    return;
                }
            }

            // Single click?
            if (onClick != null) {
                onClick.call();
            }
        }

        public void mouseEntered(MouseEvent arg0) {
        }

        public void mouseExited(MouseEvent arg0) {
        }

        public void mousePressed(MouseEvent arg0) {
        }

        public void mouseReleased(MouseEvent arg0) {
        }
    }

    public static class Panel extends JPanel {
        public Panel() {
            this(4);
        }

        public Panel(int bs) {
            super();
            setBorder(new EmptyBorder(bs, bs, bs, bs));
        }

        public Panel(boolean noborder) {
            super();
        }

        public Panel(LayoutManager l) {
            this(l, 4);
        }

        public Panel(LayoutManager l, int bs) {
            super(l);
            setBorder(new EmptyBorder(bs, bs, bs, bs));
        }

        public Panel(LayoutManager l, boolean noborder) {
            super(l);
        }

        public void setTitle(String title) {
            TitledBorder b = new TitledBorder(title);
            b.setTitleFont(b.getTitleFont().deriveFont(Font.BOLD));
            // Some Swing themes always default text to black
            b.setTitleColor(new JLabel().getForeground());
            setBorder(b);
        }

        public void dispose() {
        }

        public Component add(Component c) {
            // Put components in scrollpanes
            if (c instanceof TextArea || c instanceof HTMLBrowser ||
                    c instanceof Table || c instanceof List) {
                JScrollPane s = new JScrollPane(c);
                s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                super.add(s);
            } else {
                super.add(c);
            }

            return c;
        }

        public Component add(Component c, int i) {
            // Put components in scrollpanes
            if (c instanceof TextArea || c instanceof HTMLBrowser ||
                    c instanceof Table || c instanceof List) {
                JScrollPane s = new JScrollPane(c);
                s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                super.add(s, i);
            } else {
                super.add(c, i);
            }

            return c;
        }
    }

    public static class MenuBar extends JMenuBar {
    }

    public static class Menu extends JMenu {
    }

    public static class MenuItem extends JMenuItem {
        public MenuItem() {
            super();
        }

        public MenuItem(FunctionPointer onClick) {
            super();
            setOnClick(onClick);
        }

        public void setOnClick(final FunctionPointer onClick) {
            if (onClick != null) {
                addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            onClick.call();
                        }
                    });
            }
        }
    }

    public static class Separator extends JSeparator {
    }

    public static class PasswordField extends JPasswordField {
        public PasswordField() {
            setWidth(UI.getTextBoxWidth());

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public String getText() {
            return new String(getPassword());
        }

        public void setWidth(int w) {
            setPreferredSize(UI.getDimension(w, UI.getTextBoxHeight()));
        }
    }

    public static class ProgressBar extends JProgressBar {
    }

    public static class RadioButton extends JRadioButton {
        public RadioButton() {
            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }
    }

    public static class ToolBar extends JToolBar {
        private boolean platformToolbar = !Global.buttonHotkeys;

        public ToolBar() {
            super();
            setFloatable(false);

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public ToolBar(boolean vertical) {
            super(SwingConstants.VERTICAL);
            setFloatable(false);

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public void setPlatformToolbar(boolean b) {
            platformToolbar = b;
        }

        public Component add(JButton b) {
            return super.add(b);
        }
    }

    public static class TextField extends JTextField {
        public TextField() {
            setWidth(UI.getTextBoxWidth());

            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }

        public void setWidth(int w) {
            setPreferredSize(UI.getDimension(w, UI.getTextBoxHeight()));
        }
    }

    public static class SearchTextField extends UI.Panel {
        UI.TextField t = null;
        UI.Button btn = null;

        public SearchTextField(String tooltip, String buttontooltip,
            boolean enabled, FunctionPointer onSearch, FunctionPointer onChange) {
            this(tooltip, buttontooltip, enabled,
                IconManager.getIcon(IconManager.SEARCHSMALL), onSearch, onChange);
        }

        public SearchTextField(String tooltip, String buttontooltip,
            boolean enabled, Icon icon, FunctionPointer onSearch,
            FunctionPointer onChange) {
            super(UI.getBorderLayout(), true);
            t = UI.getTextField(tooltip, onChange);
            t.setEnabled(enabled);
            add(t, UI.BorderLayout.CENTER);
            btn = UI.getButton(null,
                    ((buttontooltip != null) ? buttontooltip : tooltip), ' ',
                    (icon != null) ? icon
                                   : IconManager.getIcon(
                        IconManager.SEARCHSMALL), onSearch);

            UI.ToolBar t = UI.getToolBar();
            t.add(btn);
            add(t, UI.isLTR() ? UI.BorderLayout.EAST : UI.BorderLayout.WEST);
        }

        public UI.TextField getTextField() {
            return t;
        }

        public void setForeground(Color c) {
            if (t == null) {
                return;
            }

            t.setForeground(c);
        }

        public String getText() {
            return t.getText();
        }

        public void setText(String s) {
            t.setText(s);
        }

        public void setButtonEnabled(boolean b) {
            btn.setEnabled(b);
        }
    }

    public static class TabbedPane extends JTabbedPane {
        public TabbedPane() {
            setTabPlacement(Global.TABALIGN);
        }

        public void addTab(String title, Component c) {
            addTab(title, null, c, null);
        }

        public void addTab(String title, Icon icon, Component c) {
            addTab(title, icon, c, null);
        }

        public void addTab(String title, Icon icon, Component c, String tip) {
            String s = title.trim();

            if (s.endsWith(":")) {
                s = s.substring(0, s.length() - 1);
            }

            s = UI.mnemonicRemove(s);
            super.addTab(s, icon, c, tip);
        }
    }

    public static class TextArea extends JTextArea {
        public TextArea() {
            super(6, 18);

            // Screws up scrollbars with Swing
            //setPreferredSize(UI.getDimension(UI.getTextBoxWidth(),
            //   UI.getTextBoxHeight() * 4));
            if (useDefaultTooltip) {
                setToolTipText(TOOLTIP_DEFAULT);
            }
        }
    }

    public static class FileChooser extends JFileChooser {
        public FileChooser() {
            super();
        }

        public FileChooser(String path) {
            super(path);
        }

        public void dispose() {
        }

        public String showDirectorySelectDialog(java.awt.Window parent,
            String title) {
            setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            //setTitle(title);
            int rv = showOpenDialog(parent);

            if (JFileChooser.APPROVE_OPTION == rv) {
                return getSelectedFile().getAbsolutePath();
            } else {
                return "";
            }
        }
    }

    public static class FlowLayout extends java.awt.FlowLayout {
        public FlowLayout() {
            super();
        }

        public FlowLayout(int align) {
            super(align);
        }

        public FlowLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }
    }

    public static class BorderLayout extends java.awt.BorderLayout {
    }

    public static class TableLayout extends SWTTableLayout {
        public TableLayout(int rows, int cols, int hgap, int vgap) {
            super(rows, cols, hgap, vgap);
        }
    }

    public static class DesktopPane extends ASMDesktop {
        /** When the tab changes, update the audit info icon
         *  on the main screen */
        protected void newTabSelected(ASMForm f) {
            Global.mainForm.updateAuditInfo(f);
        }
    }

    public static class BoxLayout extends javax.swing.BoxLayout {
        public BoxLayout(UI.Panel p, int axis) {
            super(p, axis);
        }
    }

    public static class Dimension extends java.awt.Dimension {
    }
}
