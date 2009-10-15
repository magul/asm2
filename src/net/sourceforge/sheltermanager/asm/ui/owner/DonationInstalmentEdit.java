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
package net.sourceforge.sheltermanager.asm.ui.owner;

import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Creates an instalment donation
 *
 * @author Robin Rawson-Tetley
 */
public class DonationInstalmentEdit extends ASMForm {
    private DonationSelector parent = null;
    private int ownerID = 0;
    private int movementID = 0;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboFrequency;
    private UI.TextArea txtComments;
    private CurrencyField txtDonation;
    private UI.TextField txtNumber;
    private DateField txtStartDate;

    public DonationInstalmentEdit(DonationSelector parent, int ownerID,
        int movementID) {
        this.ownerID = ownerID;
        this.movementID = movementID;
        this.parent = parent;
        init(Global.i18n("uiowner", "create_instalment_donation"),
            IconManager.getIcon(IconManager.SCREEN_OWNERDONATIONINSTALMENT),
            "uiowner");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtStartDate);
        ctl.add(cboFrequency);
        ctl.add(txtDonation);
        ctl.add(txtNumber);
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtStartDate;
    }

    public void dispose() {
        parent = null;
        super.dispose();
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2));
        UI.Panel but = UI.getPanel(UI.getFlowLayout());

        txtStartDate = (DateField) UI.addComponent(top, i18n("Start_date"),
                UI.getDateField());

        cboFrequency = UI.getCombo();
        cboFrequency.addItem(i18n("weekly"));
        cboFrequency.addItem(i18n("monthly"));
        cboFrequency.addItem(i18n("quarterly"));
        cboFrequency.addItem(i18n("half-yearly"));
        cboFrequency.addItem(i18n("annually"));
        cboFrequency.setSelectedIndex(1);
        UI.addComponent(top, i18n("instalment_frequency"), cboFrequency);

        txtDonation = (CurrencyField) UI.addComponent(top,
                i18n("instalment_amount"), UI.getCurrencyField());

        txtNumber = (UI.TextField) UI.addComponent(top,
                i18n("number_of_instalments"), UI.getTextField());

        txtComments = (UI.TextArea) UI.addComponent(mid, i18n("comments"),
                UI.getTextArea());

        btnOk = (UI.Button) but.add(UI.getButton(i18n("ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) but.add(UI.getButton(i18n("cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(top, UI.BorderLayout.NORTH);
        add(mid, UI.BorderLayout.CENTER);
        add(but, UI.BorderLayout.SOUTH);
    }

    public void setSecurity() {
    }

    public void loadData() {
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean formClosing() {
        return false;
    }

    public boolean saveData() {
        // Generate instalments
        try {
            OwnerDonation od = new OwnerDonation();
            od.openRecordset("ID = 0");

            int noInstalments = 0;

            try {
                noInstalments = Integer.parseInt(txtNumber.getText());
            } catch (NumberFormatException e) {
                Dialog.showError(i18n("you_must_enter_a_valid_number_for_the_number_of_instalments"));

                return false;
            }

            if (txtStartDate.equals("")) {
                Dialog.showError(i18n("you_must_supply_a_start_date"));

                return false;
            }

            // Convert the start date
            Date date = Utils.parseDate(txtStartDate.getText());

            for (int i = 0; i < noInstalments; i++) {
                od.addNew();
                od.setOwnerID(new Integer(ownerID));
                od.setMovementID(new Integer(movementID));
                od.setDateReceived(null);
                od.setDateDue(date);
                od.setDonation(new Double(txtDonation.getText()));
                od.setDonationTypeID(new Integer(1));
                od.setComments(txtComments.getText());

                // Add the frequency period on to the current date
                Calendar curdate = Utils.dateToCalendar(date);

                switch (cboFrequency.getSelectedIndex()) {
                case 0: // week
                    curdate.add(Calendar.WEEK_OF_YEAR, 1);

                    break;

                case 1: // month
                    curdate.add(Calendar.MONTH, 1);

                    break;

                case 2: // quarter
                    curdate.add(Calendar.MONTH, 3);

                    break;

                case 3: // half-yearly
                    curdate.add(Calendar.MONTH, 6);

                    break;

                case 4: // annually
                    curdate.add(Calendar.MONTH, 12);

                    break;
                }

                date = Utils.calendarToDate(curdate);
            }

            // Save records back
            od.save(Global.currentUserName);

            // Update parent
            parent.updateList();

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(i18n("an_error_occurred_saving_data") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }
}
