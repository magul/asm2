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
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.awt.*;
import java.awt.event.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JDialog;


/**
 *
 * Allows editing of date fields.
 */
public class DateField extends UI.Panel {
    DateChangedListener dateListener = null;
    private String focusText = "";
    UI.TextField txt = new UI.TextField() {
            public void paste() {
                super.paste();

                if (dateListener != null) {
                    dateListener.dateChanged(txt.getText());
                }
            }
        };

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

    public void updateDate(Date d) {
        updateDate(Utils.dateToCalendar(d));
    }

    public void updateDate(final Calendar c) {
        UI.invokeLater(new Runnable() {
                public void run() {
                    String s = Utils.formatDate(c);
                    txt.setText(s);

                    if (dateListener != null) {
                        dateListener.dateChanged(s);
                    }
                }
            });
    }

    public UI.TextField getTextField() {
        return txt;
    }

    public void setEnabled(boolean b) {
        getTextField().setEnabled(b);
        btn.setEnabled(b);
    }

    /**
     * Returns true if the event should be consumed
     */
    public boolean handleKeyPress(java.awt.event.KeyEvent evt) {
        // Check for special hotkeys that allow us to do things

        // Show date picker
        if (evt.getKeyChar() == ' ') {
            showPicker();

            return true;
        }

        // Today
        if (evt.getKeyChar() == 't') {
            updateDate(new Date());

            return true;
        }

        // Make sure date is valid before we do any of the 
        // hotkey functions
        Calendar thisdate = null;
        boolean valid = false;

        try {
            thisdate = Utils.dateToCalendar(Utils.parseDate(txt.getText()));
            valid = true;
        } catch (Exception e) {
        }

        // Consume special hotkeys if our date is invalid, but don't do
        // anything
        if (!valid &&
                ((evt.getKeyChar() == 'd') || (evt.getKeyChar() == 'D') ||
                (evt.getKeyChar() == 'm') || (evt.getKeyChar() == 'M') ||
                (evt.getKeyChar() == 'y') || (evt.getKeyChar() == 'Y') ||
                (evt.getKeyChar() == 'b') || (evt.getKeyChar() == 'e'))) {
            return true;
        }

        // Day
        if (evt.getKeyChar() == 'd') {
            thisdate.add(Calendar.DAY_OF_MONTH, 1);
            updateDate(thisdate);

            return true;
        }

        if (evt.getKeyChar() == 'D') {
            thisdate.add(Calendar.DAY_OF_MONTH, -1);
            updateDate(thisdate);

            return true;
        }

        // Week
        if (evt.getKeyChar() == 'w') {
            thisdate.add(Calendar.DAY_OF_MONTH, 7);
            updateDate(thisdate);

            return true;
        }

        if (evt.getKeyChar() == 'W') {
            thisdate.add(Calendar.DAY_OF_MONTH, -7);
            updateDate(thisdate);

            return true;
        }

        // Month
        if (evt.getKeyChar() == 'm') {
            thisdate.add(Calendar.MONTH, 1);
            updateDate(thisdate);

            return true;
        }

        if (evt.getKeyChar() == 'M') {
            thisdate.add(Calendar.MONTH, -1);
            updateDate(thisdate);

            return true;
        }

        // Year
        if (evt.getKeyChar() == 'y') {
            thisdate.add(Calendar.YEAR, 1);
            updateDate(thisdate);

            return true;
        }

        if (evt.getKeyChar() == 'Y') {
            thisdate.add(Calendar.YEAR, -1);
            updateDate(thisdate);

            return true;
        }

        // Beginning of month
        if (evt.getKeyChar() == 'b') {
            thisdate.set(Calendar.DAY_OF_MONTH, 1);
            updateDate(thisdate);

            return true;
        }

        // End of month
        if (evt.getKeyChar() == 'e') {
            // Go to the first day of this month, add one month and then
            // go a day backwards to get to the end of the month.
            thisdate.set(Calendar.DAY_OF_MONTH, 1);
            thisdate.add(Calendar.MONTH, 1);
            thisdate.add(Calendar.DAY_OF_MONTH, -1);
            updateDate(thisdate);

            return true;
        }

        // Don't consume anything else
        return false;
    }

    public void setToToday() {
        updateDate(new Date());
    }

    private void initComponents() {
        setLayout(new UI.BorderLayout());

        addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    txt.requestFocus();
                }
            });

        btn = UI.getButton(null, null, ' ',
                IconManager.getIcon(IconManager.DATEPICKER),
                UI.fp(this, "showPicker"));

        if (!UI.isLTR()) {
            txt.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    // Remember what was in the box as it got the focus
                    focusText = txt.getText();
                }

                public void focusLost(java.awt.event.FocusEvent evt) {
                    // Do pivot date stuff and validate when we leave the field
                    checkDateBoundaries();

                    // If the date changed while we were focused, fire a change
                    // event as we leave
                    if ((dateListener != null) &&
                            !txt.getText().equals(focusText)) {
                        dateListener.dateChanged(txt.getText());
                    }
                }
            });

        txt.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    if (handleKeyPress(evt)) {
                        evt.consume();
                    }
                }

                public void keyTyped(java.awt.event.KeyEvent evt) {
                }

                public void keyReleased(final java.awt.event.KeyEvent evt) {
                }
            });

        add(txt, UI.BorderLayout.CENTER);

        UI.ToolBar t = UI.getToolBar();
        t.add(btn);
        add(t, UI.isLTR() ? UI.BorderLayout.EAST : UI.BorderLayout.WEST);
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
        // Blank date - don't do anything
        if (txt.getText().equals("")) {
            return;
        }

        Calendar c = null;
        Calendar now = Calendar.getInstance();

        try {
            c = Utils.dateToCalendar(Utils.parseDate(txt.getText()));
        } catch (Exception ex) {
            // The date doesn't parse, default it to today instead
            txt.setText(Utils.formatDate(now));
            c = now;
        }

        if (c != null) {
            // If our date is below the year 100, then a two-digit date was
            // entered and we need to work some pivot magic on it
            int year = c.get(Calendar.YEAR);

            if (year < 100) {
                if (year < Global.PIVOT_YEAR) {
                    year += Global.BELOW_PIVOT;
                } else {
                    year += Global.AFTER_PIVOT;
                }

                c.set(Calendar.YEAR, year);
                txt.setText(Utils.formatDate(c));
            }

            // The date is over 100 years in the past, or 10 in the
            // future. Reset the year to this year.
            if ((c.get(Calendar.YEAR) > (now.get(Calendar.YEAR) + 10)) ||
                    (c.get(Calendar.YEAR) < (now.get(Calendar.YEAR) - 100))) {
                c.set(Calendar.YEAR, now.get(Calendar.YEAR));
                txt.setText(Utils.formatDate(c));
            }
        }
    }
}


class DatePicker extends JDialog {
    static Color WHITE = Color.WHITE;
    static Color GRAY = new Color(230, 230, 230);
    static Color LIGHTGRAY = new Color(240, 240, 240);
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
        midPanel.setBackground(GRAY);

        for (int x = 0; x < btn.length; x++) {
            final int selection = x;
            btn[x] = new UI.Label();
            btn[x].setOpaque(true);
            btn[x].setBackground(Color.WHITE);
            btn[x].setHorizontalAlignment(UI.Label.CENTER);

            if (x > 6) {
                btn[x].addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            displayDatePicked(btn[selection].getText());
                        }

                        public void mouseEntered(MouseEvent e) {
                            btn[selection].setBackground(LIGHTGRAY);
                        }

                        public void mouseExited(MouseEvent e) {
                            btn[selection].setBackground(Color.WHITE);
                        }
                    });
            }

            if (x < 7) {
                btn[x].setText(header[x]);
                btn[x].setForeground(Color.RED);
                btn[x].setBackground(GRAY);
            }

            midPanel.add(btn[x]);
        }

        UI.Panel lowPanel = UI.getPanel(UI.getGridLayout(5));
        lowPanel.setBackground(Color.WHITE);

        UI.Button prevYear = UI.getButton(null, UI.messagePreviousYear(), ' ',
                IconManager.getIcon(IconManager.DATEPICKER_PREVYEAR), null);

        prevYear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    year--;
                    setDates();
                }
            });
        lowPanel.add(prevYear);

        UI.Button prevMonth = UI.getButton(null, UI.messagePreviousMonth(),
                ' ', IconManager.getIcon(IconManager.DATEPICKER_PREVMONTH), null);

        prevMonth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    month--;
                    setDates();
                }
            });
        lowPanel.add(prevMonth);

        UI.Button thisMonth = UI.getButton(null, UI.messageThisMonth(), ' ',
                IconManager.getIcon(IconManager.DATEPICKER_THISMONTH), null);

        thisMonth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Calendar c = Calendar.getInstance();
                    month = c.get(Calendar.MONTH);
                    year = c.get(Calendar.YEAR);
                    setDates();
                }
            });
        lowPanel.add(thisMonth);

        UI.Button nextMonth = UI.getButton(null, UI.messageNextMonth(), ' ',
                IconManager.getIcon(IconManager.DATEPICKER_NEXTMONTH), null);

        nextMonth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    month++;
                    setDates();
                }
            });
        lowPanel.add(nextMonth);

        UI.Button nextYear = UI.getButton(null, UI.messageNextYear(), ' ',
                IconManager.getIcon(IconManager.DATEPICKER_NEXTYEAR), null);

        nextYear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    year++;
                    setDates();
                }
            });
        lowPanel.add(nextYear);

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
        }

        // Mark today if appropriate
        Calendar now = Calendar.getInstance();

        if ((year == now.get(Calendar.YEAR)) &&
                (month == now.get(Calendar.MONTH))) {
            int today = 5 + dayOfWeek + now.get(Calendar.DAY_OF_MONTH);
            btn[today].setForeground(Color.RED);

            //btn[today].setFont(bold);
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
