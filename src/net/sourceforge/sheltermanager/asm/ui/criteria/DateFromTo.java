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

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.reports.*;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.util.*;


/**
 *
 * Launches reports that require a date from/to
 * argument.
 *
 */
public class DateFromTo extends ASMForm {
    /** Constants that determine which report to open
     *  when OK is pressed. This means that criteria screens
     *  do not have to be modal and each know what to do, even
     *  if there are an unlimited amount open.
     */
    public static final int REPORT_INOUT = 0;
    public static final int REPORT_INOUT_SUMMARY = 1;
    public static final int REPORT_RETURNED_PRESIX = 2;
    public static final int REPORT_RETURNED_POSTSIX = 3;
    public static final int REPORT_LOSTFOUND = 4;
    public static final int REPORT_TRANSFERIN = 5;
    public static final int REPORT_RETURNEDANIMALS = 6;
    public static final int REPORT_RETAILER_VOLUME_ADOPTIONS = 7;

    /** The report to open when OK is pressed */
    private int repNo = 0;
    private FromToListener listener = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private DateField txtFrom;
    private DateField txtTo;

    public DateFromTo(int reportNumber) {
        this(reportNumber, null);
    }

    /** Creates new form DateFromTo */
    public DateFromTo(int reportNumber, FromToListener theListener) {
        listener = theListener;
        repNo = reportNumber;

        init("", IconManager.getIcon(IconManager.SCREEN_CRITERIA_DATEFROMTO),
            "reports");

        txtFrom.setText(Utils.formatDate(new Date()));
        txtTo.setText(Utils.formatDate(new Date()));

        // Set the title according to the report
        switch (repNo) {
        case REPORT_INOUT:
            setTitle(i18n("In/Out_Report"));

            break;

        case REPORT_INOUT_SUMMARY:
            setTitle(i18n("In/Out_Summary_Report"));

            break;

        case REPORT_RETURNED_PRESIX:
            setTitle(i18n("Animals_Returned_Within_Six_Months"));

            break;

        case REPORT_RETURNED_POSTSIX:
            setTitle(i18n("Animals_Returned_After_Six_Months"));

            break;

        case REPORT_LOSTFOUND:
            setTitle(i18n("Lost/Found_Matching_Report"));

            break;

        case REPORT_TRANSFERIN:
            setTitle(i18n("Transfer_In_Report"));

            break;

        case REPORT_RETURNEDANIMALS:
            setTitle(i18n("Returned_Animals_Report"));

            break;

        case REPORT_RETAILER_VOLUME_ADOPTIONS:
            setTitle(i18n("volume_of_adoptions_per_retailer"));

            break;

        default:
            break;
        }
    }

    public void dispose() {
        listener = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtFrom.getTextField());
        ctl.add(txtTo.getTextField());
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtFrom;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(2));

        txtFrom = (DateField) UI.addComponent(p, i18n("Date_From:"),
                UI.getDateField());
        txtTo = (DateField) UI.addComponent(p, i18n("Date_To:"),
                UI.getDateField());
        btnOk = (UI.Button) p.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "actionOk")));
        btnCancel = (UI.Button) p.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(p, UI.BorderLayout.CENTER);
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public boolean formClosing() {
        return false;
    }

    public void setSecurity() {
    }

    public void actionOk() {
        // If a listener was supplied, then we aren't opening a
        // report -- we just need to fire the callback method
        // and dispose of ourselves.
        if (listener != null) {
            try {
                Date from = Utils.parseDate(txtFrom.getText());
                Date to = Utils.parseDate(txtTo.getText());
                listener.dateChosen(from, to);
                dispose();
            } catch (Exception e) {
                Dialog.showError(i18n("There_was_a_problem_with_one_of_the_dates_you_entered."));
            }

            return;
        }

        // Check the report to open and do it
        try {
            switch (repNo) {
            case REPORT_INOUT:
                new InOut(false, Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            case REPORT_INOUT_SUMMARY:
                new InOut(true, Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            case REPORT_RETURNED_PRESIX:
                new AnimalReturnedPreSix(Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            case REPORT_RETURNED_POSTSIX:
                new AnimalReturnedPostSix(Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            case REPORT_TRANSFERIN:
                new TransferIn(Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            case REPORT_RETURNEDANIMALS:
                new ReturnedAnimals(Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            case REPORT_RETAILER_VOLUME_ADOPTIONS:
                new Retailer(Retailer.VOLUME_ADOPTIONS_PER_RETAILER,
                    Utils.parseDate(txtFrom.getText()),
                    Utils.parseDate(txtTo.getText()));

                break;

            default:
                break;
            }

            // Destroy this form 
            dispose();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
