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
package net.sourceforge.sheltermanager.asm.ui.criteria;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.DateFormatException;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.text.ParseException;

import java.util.Vector;


/**
 * Used by diary reports to allow selection of date criteria.
 * Reports must implement a callback interface.
 *
 * @author  Robin Rawson-Tetley
 */
public class DiaryCriteria extends ASMForm {
    public static final int UPTO_TODAY = 0;
    public static final int UPTO_SPECIFIED = 1;
    public static final int BETWEEN_TWO = 2;
    private DiaryCriteriaListener caller = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.RadioButton radBetween;
    private UI.RadioButton radNormal;
    private UI.RadioButton radUpto;
    private DateField txtFrom;
    private DateField txtTo;
    private DateField txtUpto;

    public DiaryCriteria(DiaryCriteriaListener caller, String formCaption) {
        this.caller = caller;
        init(Global.i18n("reports", "Diary_Criteria"),
            IconManager.getIcon(IconManager.SCREEN_CRITERIA_DIARY), "reports");
        radNormal.setSelected(true);
        this.setTitle(formCaption);
        txtFrom.setToToday();
        txtTo.setToToday();
    }

    public void dispose() {
        caller = null;
        unregisterTabOrder();
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(radNormal);
        ctl.add(radUpto);
        ctl.add(txtUpto.getTextField());
        ctl.add(radBetween);
        ctl.add(txtFrom.getTextField());
        ctl.add(txtTo.getTextField());
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return radNormal;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(1));
        UI.Panel today = UI.getPanel(UI.getFlowLayout(true));
        UI.Panel upto = UI.getPanel(UI.getFlowLayout(true));
        UI.Panel between = UI.getPanel(UI.getFlowLayout(true));
        UI.Panel buttons = UI.getPanel(UI.getFlowLayout());

        radNormal = (UI.RadioButton) today.add(UI.getRadioButton(i18n("Show_everything_before_and_including_today"),
                    null, 'v', UI.fp(this, "selectedNormal")));

        radUpto = (UI.RadioButton) upto.add(UI.getRadioButton(i18n("Show_everything_upto_this_date:"),
                    null, 'u', UI.fp(this, "selectedUpto")));
        txtUpto = (DateField) upto.add(UI.getDateField());

        radBetween = (UI.RadioButton) between.add(UI.getRadioButton(i18n("Show_everything_between"),
                    null, 'b', UI.fp(this, "selectedBetween")));
        txtFrom = (DateField) between.add(UI.getDateField());
        between.add(UI.getLabel(" "));
        txtTo = (DateField) between.add(UI.getDateField());

        btnOk = (UI.Button) buttons.add(UI.getButton(i18n("Ok"), null, 'o',
                    null, UI.fp(this, "actionOk")));
        btnCancel = (UI.Button) buttons.add(UI.getButton(i18n("Cancel"), null,
                    'c', null, UI.fp(this, "dispose")));

        p.add(today);
        p.add(upto);
        p.add(between);
        p.add(buttons);

        add(p, UI.BorderLayout.CENTER);
    }

    public boolean formClosing() {
        return false;
    }

    public void setSecurity() {
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public String getAuditInfo() {
        return null;
    }

    public void selectedNormal() {
        radBetween.setSelected(false);
        radUpto.setSelected(false);
    }

    public void selectedBetween() {
        radNormal.setSelected(false);
        radUpto.setSelected(false);
    }

    public void selectedUpto() {
        radBetween.setSelected(false);
        radNormal.setSelected(false);
    }

    public void actionOk() {
        // Decide which method to call back based on options
        // selected.
        int type = 0;

        if (radNormal.isSelected()) {
            type = UPTO_TODAY;
        }

        if (radUpto.isSelected()) {
            type = UPTO_SPECIFIED;
        }

        if (radBetween.isSelected()) {
            type = BETWEEN_TWO;
        }

        /*
         * Validate the choice made based on the data we have
         */
        switch (type) {
        case UPTO_TODAY:
            // No validation required - just do it
            caller.normalChosen();

            break;

        case UPTO_SPECIFIED:

            // Make sure we have a specified date
            if (txtUpto.getText().equals("")) {
                Dialog.showError(i18n("You_must_specify_a_date_to_show_entries_up_to."));

                return;
            }

            try {
                caller.uptoChosen(Utils.parseDate(txtUpto.getText()));

                break;
            } catch (ParseException e) {
                Dialog.showError(i18n("The_date_you_entered_was_invalid."));

                return;
            }

        case BETWEEN_TWO:

            // Make sure we have both dates
            if (txtFrom.getText().equals("")) {
                Dialog.showError(i18n("You_must_specify_a_date_to_show_entries_from."));

                return;
            }

            if (txtTo.getText().equals("")) {
                Dialog.showError(i18n("You_must_specify_a_date_to_show_entries_to."));

                return;
            }

            try {
                caller.dateChosen(Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;
            } catch (ParseException e) {
                Dialog.showError(i18n("One_of_the_dates_you_entered_was_invalid."));

                return;
            }
        }

        dispose();
    }
}
