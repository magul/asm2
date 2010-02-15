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

import net.sourceforge.sheltermanager.asm.bo.AnimalType;
import net.sourceforge.sheltermanager.asm.bo.Diet;
import net.sourceforge.sheltermanager.asm.bo.InternalLocation;
import net.sourceforge.sheltermanager.asm.bo.MedicalProfile;
import net.sourceforge.sheltermanager.asm.bo.Species;
import net.sourceforge.sheltermanager.asm.bo.Voucher;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.awt.Frame;

import java.util.Calendar;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.UIManager;


public abstract class Dialog {
    /** The last date value returned by getDateInput */
    static String lastDate = "";

    /** The last JDBC URL returned by getJDBCUrl */
    static String lastJDBC = "";

    /** The last string returned by getInput */
    static String lastInput = "";

    /** If there's no form parent, the app is going to terminate the
     *  VM before dialogs are visible. This forces the thread to wait
     *  for a given amount of time in ms so the user can see the message. */
    final static long noParentWaitTime = 7000;

    /** Reference to the frame used as the parent for any messages */
    public static Frame theParent = null;

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

    private static void setOptionPaneStrings() {
        UIManager.put("OptionPane.yesButtonText", UI.messageYes());
        UIManager.put("OptionPane.noButtonText", UI.messageNo());
        UIManager.put("OptionPane.cancelButtonText", UI.messageCancel());
        UIManager.put("OptionPane.okButtonText", UI.messageOK());
    }

    /** Wraps a message by replacing spaces with line breaks */
    private static String wordWrap(String s) {
        final int WRAP = 100; // No chars to wrap at
        int lastpos = 0;

        while ((lastpos + WRAP) < s.length()) {
            int sp = s.lastIndexOf(" ", lastpos + WRAP);

            if (sp == -1) {
                break;
            }

            s = s.substring(0, sp) + "\n" + s.substring(sp + 1);
            lastpos = sp;
        }

        return s;
    }

    public static void showError(String message, String title) {
    	setOptionPaneStrings();
        JOptionPane.showMessageDialog(theParent, wordWrap(message), title,
            JOptionPane.ERROR_MESSAGE);

        if (theParent == null) {
            sleep(noParentWaitTime);
        }
    }

    public static void showError(String message) {
    	setOptionPaneStrings();
        JOptionPane.showMessageDialog(theParent, wordWrap(message),
            Global.i18n("uierror", "Error"), JOptionPane.ERROR_MESSAGE);

        if (theParent == null) {
            sleep(noParentWaitTime);
        }
    }

    public static void showInformation(String message) {
    	setOptionPaneStrings();
        showInformation(message, Global.i18n("uierror", "Information"));
    }

    public static void showInformation(String message, String title) {
    	setOptionPaneStrings();
        JOptionPane.showMessageDialog(theParent, wordWrap(message), title,
            JOptionPane.INFORMATION_MESSAGE);

        if (theParent == null) {
            sleep(noParentWaitTime);
        }
    }

    public static void showWarning(String message, String title) {
    	setOptionPaneStrings();
        JOptionPane.showMessageDialog(theParent, wordWrap(message), title,
            JOptionPane.WARNING_MESSAGE);

        if (theParent == null) {
            sleep(noParentWaitTime);
        }
    }

    public static boolean showYesNo(String message, String title) {
    	setOptionPaneStrings();
        int answer = JOptionPane.showConfirmDialog(theParent,
                wordWrap(message), title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        return answer == JOptionPane.YES_OPTION;
    }

    public static boolean showYesNoWarning(String message, String title) {
    	setOptionPaneStrings();
        int answer = JOptionPane.showConfirmDialog(theParent,
                wordWrap(message), title, JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return answer == JOptionPane.YES_OPTION;
    }

    public static String getInput(String message, String title) {
        new InputTextDialog(message, title);

        return lastInput;
    }

    public static Object getInput(String message, String title,
        Object[] values, Object selected) {
        new InputListDialog(message, title, values, selected);

        return lastInput;
    }

    public static String getYear(String message) {
        int thisyear = Calendar.getInstance().get(Calendar.YEAR);

        // Go 10 years either side
        String[] years = new String[20];
        thisyear -= 10;

        for (int i = 0; i < 20; i++)
            years[i] = Integer.toString(thisyear + i);

        return (String) getInput(message,
            Global.i18n("uierror", "Select_Year"), years,
            Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
    }

    public static String getDateInput(String message, String title) {
        // net.sourceforge.sheltermanager.asm.ui.beans.DateBox theDate = new
        // net.sourceforge.sheltermanager.asm.ui.beans.DateBox();
        // theDate.setToToday();
        // Object[] messages = { message, theDate };
        // JOptionPane.showMessageDialog(null, messages, title,
        // JOptionPane.QUESTION_MESSAGE);
        // return theDate.getText();
        DateInputDlg did = new DateInputDlg(theParent, true, message, title);

        return lastDate;
    }

    public static String getJDBCUrl(String title) {
        JDBCDlg jd = new JDBCDlg(title);

        return lastJDBC;
    }

    /** Uses a JOptionPane to request a species from the user */
    public static int getSpecies() {
        Vector theList = new Vector();
        theList.add(Global.i18n("uierror", "(all)"));

        try {
            Species species = new Species();
            species.openRecordset("ID > 0 ORDER BY SpeciesName");

            while (!species.getEOF()) {
                theList.add(species.getSpeciesName());
                species.moveNext();
            }

            species.free();
            species = null;
        } catch (Exception e) {
            return 0;
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "Select_species:"),
                Global.i18n("uierror", "Select_Species"), theList.toArray(),
                Global.i18n("uierror", "(all)"));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // If (all) was returned, return none selected with a 0
        if (chosenName.equals(Global.i18n("uierror", "(all)"))) {
            return 0;
        }

        // Otherwise, find the species ID and return it
        return Utils.getID("species", "SpeciesName", chosenName).intValue();
    }

    /** Uses a JOptionPane to request a supported locale from the user */
    public static String getLocale() {
        String[] locales = Global.getSupportedLocales();

        // Ask the user
        String chosenLocale = (String) getInput(Global.i18n("uierror",
                    "select_locale"), Global.i18n("uierror", "select_locale"),
                locales, locales[0]);

        // Default to DETECT if nothing selected
        if ((chosenLocale == null) || chosenLocale.equals("")) {
            chosenLocale = locales[0];
        }

        return Global.getLocaleFromString(chosenLocale);
    }

    /**
     * Uses a JOptionPane to request a diary user from the user - returns 0 in a
     * string if all was selected.
     */
    public static String getDiaryUser() {
        Vector theList = new Vector();
        theList.add(Global.i18n("uierror", "(all)"));

        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT DISTINCT DiaryForName FROM diary ORDER BY DiaryForName",
                "diary");

            while (!rs.getEOF()) {
                theList.add(rs.getField("DiaryForName"));
                rs.moveNext();
            }

            rs.free();
            rs = null;
        } catch (Exception e) {
            return "0";
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "select_user"),
                Global.i18n("uierror", "Select_User_title"), theList.toArray(),
                Global.i18n("uierror", "(all)"));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // If (all) was returned, return none selected with a 0
        if (chosenName.equals(Global.i18n("uierror", "(all)"))) {
            return "0";
        }

        // Otherwise, return the user name
        return chosenName;
    }

    /** Uses a JOptionPane to request an animal type from the user */
    public static int getAnimalType() {
        return getAnimalType(true);
    }

    public static int getAnimalType(boolean includeall) {
        Vector theList = new Vector();

        if (includeall) {
            theList.add(Global.i18n("uierror", "(all)"));
        }

        try {
            AnimalType at = new AnimalType();
            at.openRecordset("ID > 0 ORDER BY AnimalType");

            while (!at.getEOF()) {
                theList.add(at.getAnimalType());
                at.moveNext();
            }

            at.free();
            at = null;
        } catch (Exception e) {
            return 0;
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "select_animal_type"),
                Global.i18n("uierror", "select_animal_type"),
                theList.toArray(), Global.i18n("uierror", "(all)"));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // If (all) was returned, return none selected with a 0
        if (chosenName.equals(Global.i18n("uierror", "(all)"))) {
            return 0;
        }

        // Otherwise, find the animal type ID and return it
        return Utils.getID("animaltype", "AnimalType", chosenName).intValue();
    }

    /** Uses a JOptionPane to request an internal location from the user */
    public static int getInternalLocation() {
        Vector theList = new Vector();
        theList.add(Global.i18n("uierror", "(all)"));

        try {
            InternalLocation il = new InternalLocation();
            il.openRecordset("ID > 0 ORDER BY LocationName");

            while (!il.getEOF()) {
                theList.add(il.getLocationName());
                il.moveNext();
            }

            il.free();
            il = null;
        } catch (Exception e) {
            return 0;
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "select_internal_location"),
                Global.i18n("uierror", "select_internal_location"),
                theList.toArray(), Global.i18n("uierror", "(all)"));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // If (all) was returned, return none selected with a 0
        if (chosenName.equals(Global.i18n("uierror", "(all)"))) {
            return 0;
        }

        // Otherwise, find the species ID and return it
        return Utils.getID("internallocation", "LocationName", chosenName)
                    .intValue();
    }

    /** Uses a JOptionPane to request a diet from the user */
    public static int getDiet() {
        Vector theList = new Vector();
        theList.add(Global.i18n("uierror", "(all)"));

        try {
            Diet d = new Diet();
            d.openRecordset("ID > 0 ORDER BY DietName");

            while (!d.getEOF()) {
                theList.add(d.getDietName());
                d.moveNext();
            }

            d.free();
            d = null;
        } catch (Exception e) {
            return 0;
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "select_diet"), Global.i18n("uierror", "select_diet"),
                theList.toArray(), Global.i18n("uierror", "(all)"));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // If (all) was returned, return none selected with a 0
        if (chosenName.equals(Global.i18n("uierror", "(all)"))) {
            return 0;
        }

        // Otherwise, find the diet ID and return it
        return Utils.getID("diet", "DietName", chosenName).intValue();
    }

    /** Uses a JOptionPane to request a voucher from the user */
    public static int getVoucher() {
        Vector theList = new Vector();
        theList.add(Global.i18n("uierror", "(all)"));

        try {
            Voucher v = new Voucher();
            v.openRecordset("ID > 0 ORDER BY VoucherName");

            while (!v.getEOF()) {
                theList.add(v.getVoucherName());
                v.moveNext();
            }

            v.free();
            v = null;
        } catch (Exception e) {
            return 0;
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "Select_a_voucher"),
                Global.i18n("uierror", "Select_a_voucher"), theList.toArray(),
                Global.i18n("uierror", "(all)"));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // If (all) was returned, return none selected with a 0
        if (chosenName.equals(Global.i18n("uierror", "(all)"))) {
            return 0;
        }

        // Otherwise, find the diet ID and return it
        return Utils.getID("voucher", "VoucherName", chosenName).intValue();
    }

    /** Uses a JOptionPane to request a medical profile from the user */
    public static int getMedicalProfile() {
        Vector theList = new Vector();

        try {
            MedicalProfile mp = new MedicalProfile();
            mp.openRecordset("ID > 0 ORDER BY ProfileName");

            while (!mp.getEOF()) {
                theList.add(mp.getProfileName());
                mp.moveNext();
            }

            mp.free();
            mp = null;
        } catch (Exception e) {
            return 0;
        }

        // Ask the user
        String chosenName = (String) getInput(Global.i18n("uierror",
                    "Select_a_medical_profile"),
                Global.i18n("uierror", "Select_a_medical_profile"),
                theList.toArray(), theList.get(0));

        // Clean up
        theList.removeAllElements();
        theList = null;

        // Otherwise, find the diet ID and return it
        return Utils.getID("medicalprofile", "ProfileName", chosenName)
                    .intValue();
    }
}
