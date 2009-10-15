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
import net.sourceforge.sheltermanager.asm.utility.Utils;

import swingwt.awt.Color;
import swingwt.awt.Cursor;
import swingwt.awt.Dimension;
import swingwt.awt.Font;

import swingwt.awt.event.ActionEvent;
import swingwt.awt.event.ActionListener;
import swingwt.awt.event.MouseAdapter;
import swingwt.awt.event.MouseEvent;

import swingwtx.swing.JDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;


/**
 *
 * Allows editing of date fields.
 */
public class DateField extends UI.Panel {
    DateChangedListener dateListener = null;
    UI.TextField txt = new UI.TextField();
    UI.Button btn = null;
    DatePicker picker = new DatePicker(this);

    public DateField() {
        super(true); // No panel border
        initComponents();
    }

    public void setDateChangedListener(DateChangedListener dateListener) {
        this.dateListener = dateListener;
    }

    public void fireDateChangedListener() {
        if (dateListener != null) {
            dateListener.dateChanged(txt.getText());
        }
    }

    public void setToolTipText(String newvalue) {
        txt.setToolTipText(newvalue);
        btn.setToolTipText(newvalue);
    }

    public String getToolTipText() {
        return txt.getToolTipText();
    }

    public void setText(String newvalue) {
        txt.setText(newvalue);
    }

    public String getText() {
        return txt.getText();
    }

    public Date getDate() throws ParseException {
        return Utils.parseDate(getText());
    }

    public void setDate(Date d) {
        setText(Utils.formatDate(d));
    }

    public UI.TextField getTextField() {
        return txt;
    }

    public void handleKeyPress(swingwt.awt.event.KeyEvent evt) {
        // Let this composite control process any keystrokes for listeners
        processKeyEvent(evt);

        // Check for special hotkeys that allow us to do things

        // Today
        if (evt.getKeyChar() == 't') {
            try {
                txt.setText(Utils.formatDate(Calendar.getInstance()));

                if (dateListener != null) {
                    dateListener.dateChanged(txt.getText());
                }

                evt.consume();

                return;
            } catch (Exception e) {
            }
        }

        // Add/subtract functions

        // Make sure date is valid before starting
        Calendar thisdate = null;

        try {
            thisdate = Utils.dateToCalendar(Utils.parseDate(txt.getText()));
        } catch (ParseException e) {
            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            return;
        } catch (NumberFormatException e) {
            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            return;
        }

        // Day
        if (evt.getKeyChar() == 'd') {
            thisdate.add(Calendar.DAY_OF_MONTH, 1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        if (evt.getKeyChar() == 'D') {
            thisdate.add(Calendar.DAY_OF_MONTH, -1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        // Week
        if (evt.getKeyChar() == 'w') {
            thisdate.add(Calendar.DAY_OF_MONTH, 7);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        if (evt.getKeyChar() == 'W') {
            thisdate.add(Calendar.DAY_OF_MONTH, -7);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        // Month
        if (evt.getKeyChar() == 'm') {
            thisdate.add(Calendar.MONTH, 1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        if (evt.getKeyChar() == 'M') {
            thisdate.add(Calendar.MONTH, -1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        // Year
        if (evt.getKeyChar() == 'y') {
            thisdate.add(Calendar.YEAR, 1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        if (evt.getKeyChar() == 'Y') {
            thisdate.add(Calendar.YEAR, -1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        // Beginning of month
        if (evt.getKeyChar() == 'b') {
            thisdate.set(Calendar.DAY_OF_MONTH, 1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }

        // End of month
        if (evt.getKeyChar() == 'e') {
            // Go to the first day of this month, add one month and then
            // go a day backwards to get to the end of the month.
            thisdate.set(Calendar.DAY_OF_MONTH, 1);
            thisdate.add(Calendar.MONTH, 1);
            thisdate.add(Calendar.DAY_OF_MONTH, -1);

            try {
                txt.setText(Utils.formatDate(thisdate));
            } catch (Exception e) {
            }

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }

            evt.consume();

            return;
        }
    }

    public void setToToday() {
        try {
            txt.setText(Utils.formatDate(Calendar.getInstance()));

            if (dateListener != null) {
                dateListener.dateChanged(txt.getText());
            }
        } catch (Exception e) {
        }
    }

    private void initComponents() {
        setLayout(new UI.BorderLayout());

        addFocusListener(new swingwt.awt.event.FocusAdapter() {
                public void focusGained(swingwt.awt.event.FocusEvent evt) {
                    panel_focusgained(evt);
                }
            });

        /*
        UI.Label lbl = UI.getLabel(IconManager.getIcon(IconManager.DATEPICKER));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showPicker();
            }
        });
         * */
        btn = UI.getButton(null, null, ' ',
                IconManager.getIcon(IconManager.DATEPICKER),
                UI.fp(this, "showPicker"));

        txt.addFocusListener(new swingwt.awt.event.FocusAdapter() {
                public void focusLost(swingwt.awt.event.FocusEvent evt) {
                    txt_focusLost(evt);
                }
            });

        txt.addKeyListener(new swingwt.awt.event.KeyAdapter() {
                public void keyPressed(swingwt.awt.event.KeyEvent evt) {
                    handleKeyPress(evt);
                }
            });

        add(txt, UI.BorderLayout.CENTER);
        add(btn, UI.BorderLayout.EAST);
    }

    public void showPicker() {
        UI.centerWindow(picker);

        Calendar c = null;

        try {
            c = Utils.dateToCalendar(Utils.parseDate(txt.getText()));
        } catch (Exception ex) {
            c = null;
        }

        if (c != null) {
            picker.year = c.get(Calendar.YEAR);
            picker.month = c.get(Calendar.MONTH);
        }

        picker.setDates();
        picker.show();
    }

    /**
     * If we have a date, checks to make sure that it's nether
     * more than 10 years in the future, or 100 years in the past.
     */
    public void checkDateBoundaries() {
        Calendar c = null;
        Calendar now = Calendar.getInstance();

        try {
            c = Utils.dateToCalendar(Utils.parseDate(txt.getText()));
        } catch (Exception ex) {
            c = null;
        }

        if (c != null) {
            if ((c.get(Calendar.YEAR) > (now.get(Calendar.YEAR) + 10)) ||
                    (c.get(Calendar.YEAR) < (now.get(Calendar.YEAR) - 100))) {
                // The date is over 100 years in the past, or 10 in the
                // future. Reset the year to this year.
                c.set(Calendar.YEAR, now.get(Calendar.YEAR));
                txt.setText(Utils.formatDate(c));
            }
        }
    }

    private void txt_focusLost(swingwt.awt.event.FocusEvent evt) {
        checkDateBoundaries();
        this.processFocusEvent(evt);
    }

    private void panel_focusgained(swingwt.awt.event.FocusEvent evt) {
        txt.requestFocus();
    }
}


class DatePicker extends JDialog {
    static Font bold = Font.getStubFont().deriveFont(Font.BOLD);
    static Font plain = Font.getStubFont();
    static Color gray = new Color(230, 230, 230);
    static Color lightgray = new Color(240, 240, 240);
    private static String[] cachedHeader = null;
    UI.Label[] btn = new UI.Label[49];
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int year = Calendar.getInstance().get(Calendar.YEAR);
    DateField parent = null;

    public DatePicker(DateField parent) {
        super(Dialog.theParent, true);
        this.parent = parent;
        buildGUI();
        setDates();
    }

    public void buildGUI() {
        setSize(275, 250);
        setBackground(Color.WHITE);

        String[] header = generateHeader();
        UI.Panel midPanel = UI.getPanel(UI.getGridLayout(7, 7));
        midPanel.setPreferredSize(new Dimension(100, 100));
        midPanel.setBackground(gray);

        for (int x = 0; x < btn.length; x++) {
            final int selection = x;
            btn[x] = new UI.Label();
            btn[x].setBackground(Color.WHITE);
            btn[x].setHorizontalAlignment(UI.Label.CENTER);

            if (x > 6) {
                btn[x].addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            displayDatePicked(btn[selection].getText());
                        }

                        public void mouseEntered(MouseEvent e) {
                            btn[selection].setBackground(lightgray);
                        }

                        public void mouseExited(MouseEvent e) {
                            btn[selection].setBackground(Color.WHITE);
                        }
                    });
            }

            if (x < 7) {
                btn[x].setText(header[x]);
                btn[x].setForeground(Color.RED);
                btn[x].setFont(bold);
                btn[x].setBackground(gray);
            }

            midPanel.add(btn[x]);
        }

        UI.Panel lowPanel = UI.getPanel(UI.getGridLayout(1, 3));
        lowPanel.setBackground(Color.WHITE);

        UI.Button prevBtn = UI.getButton("<<", null);
        prevBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    month--;
                    setDates();
                }
            });
        lowPanel.add(prevBtn);
        lowPanel.add(UI.getLabel());

        UI.Button nextBtn = UI.getButton(">>", null);
        nextBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    month++;
                    setDates();
                }
            });
        lowPanel.add(nextBtn);
        add(midPanel, UI.BorderLayout.CENTER);
        add(lowPanel, UI.BorderLayout.SOUTH);
    }

    public String[] generateHeader() {
        if (cachedHeader == null) {
            // Trick java locale into giving us Sun-Sat for lang, why
            // we just can't get them is a bit of a failing if you ask
            // me.
            Calendar c = Calendar.getInstance();

            // Find a sunday
            int today = c.get(Calendar.DAY_OF_WEEK);
            c.set(Calendar.DAY_OF_WEEK, 1);

            String[] h = new String[7];

            for (int i = 0; i < 7; i++) {
                h[i] = new SimpleDateFormat("E").format(c.getTime());
                c.add(Calendar.DAY_OF_MONTH, 1);
            }

            cachedHeader = h;
        }

        return cachedHeader;
    }

    public void setDates() {
        // Clear boxes
        for (int x = 7; x < btn.length; x++) {
            btn[x].setText("");
            btn[x].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        // Get the starting day of the month
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // Draw the numbers
        for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++) {
            btn[x].setText("" + day);
            btn[x].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn[x].setForeground(Color.BLACK);
            btn[x].setFont(plain);
        }

        // Mark today if appropriate
        Calendar now = Calendar.getInstance();

        if ((year == now.get(Calendar.YEAR)) &&
                (month == now.get(Calendar.MONTH))) {
            int today = 5 + dayOfWeek + now.get(Calendar.DAY_OF_MONTH);
            btn[today].setForeground(Color.RED);
            btn[today].setFont(bold);
        }

        setTitle(sdf.format(cal.getTime()));
    }

    public void displayDatePicked(String day) {
        if (!day.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat(Global.dateFormat);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, Integer.parseInt(day));
            parent.setText(sdf.format(cal.getTime()));
            hide();
            parent.txt.grabFocus();
            parent.fireDateChangedListener();
        }
    }
}
