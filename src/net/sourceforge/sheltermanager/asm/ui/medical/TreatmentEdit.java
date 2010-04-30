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
package net.sourceforge.sheltermanager.asm.ui.medical;

import net.sourceforge.sheltermanager.asm.bo.AnimalMedical;
import net.sourceforge.sheltermanager.asm.bo.AnimalMedicalTreatment;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.text.ParseException;

import java.util.Vector;


/**
 * Handles editing of individual treatments under a regime
 *
 * @author Robin Rawson-Tetley
 */
public class TreatmentEdit extends ASMForm {
    private MedicalSelector parent = null;
    private AnimalMedicalTreatment amt = null;
    private boolean isNew = false;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboGivenBy;
    private UI.TextArea txtComments;
    private DateField txtDateGiven;
    private DateField txtDateRequired;
    private String audit = null;

    /** Creates new form Edit Treatment */
    public TreatmentEdit(MedicalSelector parent) {
        this.parent = parent;
        init(Global.i18n("uimedical", "Edit_Medical_Treatment"),
            IconManager.getIcon(IconManager.SCREEN_EDITTREATMENT), "uimedical");
    }

    /**
     * Sets the tab ordering for the screen using the FlexibleFocusManager class
     */
    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtDateRequired.getTextField());
        ctl.add(txtDateGiven.getTextField());
        ctl.add(cboGivenBy);
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtDateRequired.getTextField();
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void dispose() {
        amt.free();
        parent = null;
        amt = null;
        super.dispose();
    }

    public void openForNew(AnimalMedical am) {
        try {
            amt = new AnimalMedicalTreatment();
            amt.openRecordset("ID = 0");
            amt.addNew();

            amt.setAnimalID(am.getAnimalID());
            amt.setAnimalMedicalID(am.getID());
            amt.setTreatmentNumber(new Integer(0));
            amt.setTotalTreatments(new Integer(0));
            amt.setGivenBy(Global.currentUserName);
            amt.setComments(am.getComments());

            txtDateRequired.setToToday();
            txtComments.setText(am.getComments());
            cboGivenBy.setSelectedItem(Global.currentUserName);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void openForEdit(AnimalMedicalTreatment amt) {
        this.amt = amt;
        loadData();
    }

    public void initComponents() {
        UI.Panel pnlEntry = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));

        txtDateRequired = new DateField();
        UI.addComponent(pnlEntry, i18n("Date_Required:"), txtDateRequired);

        txtDateGiven = new DateField();
        UI.addComponent(pnlEntry, i18n("Date_Given:"), txtDateGiven);

        cboGivenBy = UI.getCombo("SELECT UserName FROM users ORDER BY UserName",
                "UserName");
        UI.addComponent(pnlEntry, i18n("Given_By:"), cboGivenBy);

        add(pnlEntry, UI.BorderLayout.NORTH);

        UI.Panel pnlComments = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));

        txtComments = UI.getTextArea();
        UI.addComponent(pnlComments, i18n("Comments:"), txtComments);

        add(pnlComments, UI.BorderLayout.CENTER);

        UI.Panel pnlButtons = UI.getPanel(UI.getFlowLayout());
        btnOk = (UI.Button) pnlButtons.add(UI.getButton(UI.messageOK(), 'o',
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) pnlButtons.add(UI.getButton(
                    UI.messageCancel(), 'c', UI.fp(this, "dispose")));
        add(pnlButtons, UI.BorderLayout.SOUTH);
    }

    public void loadData() {
        try {
            txtDateRequired.setText(Utils.formatDate(amt.getDateRequired()));
            txtDateGiven.setText(Utils.formatDate(amt.getDateGiven()));
            cboGivenBy.setSelectedItem(amt.getGivenBy());
            txtComments.setText(Utils.nullToEmptyString(amt.getComments()));

            // If the treatment hasn't been given, default the current user
            // as the given by
            if (amt.getDateGiven() == null) {
                cboGivenBy.setSelectedItem(Global.currentUserName);
            }

            audit = UI.messageAudit(amt.getCreatedDate(), amt.getCreatedBy(),
                    amt.getLastChangedDate(), amt.getLastChangedBy());
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        // Save values
        try {
            try {
                amt.setDateRequired(Utils.parseDate(txtDateRequired.getText()));
                amt.setDateGiven(Utils.parseDate(txtDateGiven.getText()));
            } catch (ParseException e) {
                Global.logException(e, this.getClass());
            }

            amt.setGivenBy((String) cboGivenBy.getSelectedItem());
            amt.setComments(txtComments.getText());

            try {
                amt.save(Global.currentUserName);
            } catch (Exception e) {
                // Validation
                Dialog.showError(e.getMessage());

                return false;
            }

            // Get the master medical record for these treatments
            AnimalMedical am = new AnimalMedical();
            am.openRecordset("ID = " + amt.getAnimalMedicalID());

            // See if we need to generate more treatments
            am.generateTreatments();

            // Update parent
            // If our regime is complete as a result,
            // we need to update the whole list
            if (am.getStatus().intValue() ==
                      AnimalMedical.STATUS_COMPLETED) {
                parent.regimeview.updateList();
            } else {
                parent.regimetview.updateList();
            }

            dispose();

            return true;
        } catch (Exception e) {
            Global.logException(e, getClass());

            return false;
        }
    }
}
