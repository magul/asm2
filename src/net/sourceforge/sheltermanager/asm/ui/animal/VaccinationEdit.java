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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Vector;


/**
 * This class contains all code for editing individual vaccination records.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class VaccinationEdit extends ASMForm {
    private AnimalVaccination anivacc = null;
    private VaccinationParent editanimal = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboVaccinationType;
    private CurrencyField txtCost;
    private UI.TextArea txtComments;
    private DateField txtDateGiven;
    private DateField txtDateRequired;
    private String audit = null;

    public VaccinationEdit(VaccinationParent ea) {
        editanimal = ea;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_VACCINATION),
            "uianimal");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboVaccinationType);
        ctl.add(txtDateRequired.getTextField());
        ctl.add(txtDateGiven.getTextField());
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboVaccinationType;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    /**
     * Sets the form into creating a new vaccination record
     *
     * @param animalid
     *            The Animal to create the new record for.
     */
    public void openForNew(int animalid) {
        this.anivacc = new AnimalVaccination();
        this.setTitle(i18n("New_Vaccination"));
        anivacc.openRecordset("ID = 0");

        try {
            anivacc.addNew();
            anivacc.setAnimalID(new Integer(animalid));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            Utils.setComboFromID(LookupCache.getVaccinationTypeLookup(),
	        "VaccinationType", new Integer(Configuration.getInteger("AFDefaultVaccinationType")),
		cboVaccinationType);
	}
	catch (Exception e) {
            Global.logException(e, getClass());
	}

        try {
            // Date required
            Calendar cal = Calendar.getInstance();
            txtDateRequired.setText(Utils.formatDate(cal));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Default cost for first type
        typeChanged();
    }

    /**
     * Opens a given vaccination record for editing.
     *
     * @param vaccinationid
     *            The ID of the vaccination to open
     */
    public void openForEdit(int vaccinationid) {
        this.anivacc = new AnimalVaccination();
        anivacc.openRecordset("ID = " + vaccinationid);

        this.setTitle(i18n("Edit_Vaccination"));
        loadData();
    }

    /**
     * Called when the type is changed - look up the most recent
     * cost of this vaccination type
     */
    public void typeChanged() {
        try {
            int vid = Utils.getIDFromCombo(LookupCache.getVaccinationTypeLookup(),
                    "VaccinationType", cboVaccinationType).intValue();
            double lastcost = DBConnection.executeForDouble(
                    "SELECT Cost FROM animalvaccination WHERE VaccinationID = " +
                    vid + " ORDER BY DateOfVaccination DESC LIMIT 1");
            txtCost.setValue(lastcost);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** Loads data into the controls */
    public void loadData() {
        try {
            try {
                this.txtDateRequired.setText(Utils.formatDate(
                        anivacc.getDateRequired()));
                this.txtDateGiven.setText(Utils.formatDate(
                        anivacc.getDateOfVaccination()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            Utils.setComboFromID(LookupCache.getVaccinationTypeLookup(),
                "VaccinationType", anivacc.getVaccinationID(),
                cboVaccinationType);
            this.txtCost.setValue(anivacc.getCost().doubleValue());
            this.txtComments.setText(Utils.nullToEmptyString(
                    anivacc.getComments()));
            audit = UI.messageAudit(anivacc.getCreatedDate(),
                    anivacc.getCreatedBy(), anivacc.getLastChangedDate(),
                    anivacc.getLastChangedBy());
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("Unable_to_read_vaccination_record_from_the_database:_") +
                e.getMessage(), i18n("Error"));
            Global.logException(e, getClass());
        }
    }

    /**
     * Saves data from the controls to the database and updates the parent
     */
    public boolean saveData() {
        try {
            anivacc.setComments(txtComments.getText());

            try {
                anivacc.setDateRequired(Utils.parseDate(
                        txtDateRequired.getText()));
                anivacc.setDateOfVaccination(Utils.parseDate(
                        txtDateGiven.getText()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }

            anivacc.setCost(new Double(txtCost.getValue()));
            anivacc.setVaccinationID(Utils.getIDFromCombo(
                    LookupCache.getVaccinationTypeLookup(), "VaccinationType",
                    cboVaccinationType));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            anivacc.save(Global.currentUserName);

            // Update the parent form if successful
            editanimal.updateVaccinations();

            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage(), i18n("Validation_Error"));
        }

        return false;
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel comments = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel buttons = UI.getPanel(UI.getFlowLayout());

        cboVaccinationType = UI.getCombo(i18n("Vaccination_Type:"),
                LookupCache.getVaccinationTypeLookup(), "VaccinationType",
                UI.fp(this, "typeChanged"));
        UI.addComponent(top, i18n("Vaccination_Type:"), cboVaccinationType);

        txtDateRequired = (DateField) UI.addComponent(top,
                i18n("Date_Required:"),
                UI.getDateField(i18n("The_date_the_animal_should_be_given_this_vaccination")));

        txtDateGiven = (DateField) UI.addComponent(top, i18n("Date_Given:"),
                UI.getDateField(i18n("The_date_the_animal_was_actually_given_this_vaccination")));

        txtCost = (CurrencyField) UI.addComponent(top, i18n("Cost:"),
                UI.getCurrencyField());

        txtComments = (UI.TextArea) UI.addComponent(comments,
                i18n("Comments:"),
                UI.getTextArea(i18n("Any_comments_about_the_vaccination")));

        btnOk = (UI.Button) buttons.add(UI.getButton(i18n("Ok"),
                    i18n("Save_this_record"), 'o', null, UI.fp(this, "saveData")));
        btnCancel = (UI.Button) buttons.add(UI.getButton(i18n("Cancel"),
                    i18n("Abandon_changes_to_this_record"), 'c', null,
                    UI.fp(this, "dispose")));

        add(top, UI.BorderLayout.NORTH);
        add(comments, UI.BorderLayout.CENTER);
        add(buttons, UI.BorderLayout.SOUTH);
    }
}
