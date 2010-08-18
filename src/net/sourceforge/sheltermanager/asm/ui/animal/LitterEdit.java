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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLitter;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for editing litters
 *
 * @author Robin Rawson-Tetley
 */
public class LitterEdit extends ASMForm {
    private AnimalLitter litter = null;
    private LitterView parent = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    public UI.ComboBox cboSpecies;
    public UI.TextField txtAcceptanceNumber;
    private UI.TextArea txtComments;
    public DateField txtDate;
    private DateField txtInvalidDate;
    public UI.TextField txtNumber;
    private AnimalLink alParentName;
    private boolean isNew = false;

    public LitterEdit(LitterView theparent) {
        parent = theparent;
        init(Global.i18n("uianimal", "Edit_Litter"),
            IconManager.getIcon(IconManager.SCREEN_EDITLITTER), "uianimal");
    }

    public void dispose() {
        litter.free();
        litter = null;
        parent = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboSpecies);
        ctl.add(txtDate);
        ctl.add(txtNumber);
        ctl.add(txtInvalidDate);
        ctl.add(txtAcceptanceNumber);
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboSpecies;
    }

    public boolean formClosing() {
        return false;
    }

    public void setSecurity() {
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public void openForNew() {
        try {
            this.setTitle(i18n("Create_New_Litter"));
            this.txtDate.setDate(new Date());

            litter = new AnimalLitter();
            litter.openRecordset("ID = 0");
            litter.addNew();

            if (Configuration.getBoolean("AutoLitterIdentification")) {
                // Generate the ID for this litter
                txtAcceptanceNumber.setText(Utils.zeroPad(litter.getID().intValue(), 6));
            }

            // Default the expiry date to 6 months in the future
            Calendar expiry = Calendar.getInstance();
            expiry.add(Calendar.MONTH, 6);
            this.txtInvalidDate.setDate(Utils.calendarToDate(expiry));

            isNew = true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void openForEdit(AnimalLitter thelitter) {
        try {
            litter = thelitter;

            alParentName.setID(litter.getParentAnimalID().intValue());
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                litter.getSpeciesID(), cboSpecies);
            txtDate.setText(Utils.formatDate(litter.getDate()));
            txtInvalidDate.setText(Utils.formatDate(litter.getInvalidDate()));
            txtNumber.setText(litter.getNumberInLitter().toString());
            txtComments.setText(Utils.nullToEmptyString(litter.getComments()));
            txtAcceptanceNumber.setText(Utils.nullToEmptyString(
                    litter.getAcceptanceNumber()));
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        try {
            litter.setParentAnimalID(new Integer(alParentName.getID()));
            litter.setSpeciesID(Utils.getIDFromCombo(
                    LookupCache.getSpeciesLookup(), "SpeciesName", cboSpecies));
            litter.setDate(Utils.parseDate(txtDate.getText()));
            litter.setInvalidDate(Utils.parseDate(txtInvalidDate.getText()));
            litter.setNumberInLitter(new Integer(txtNumber.getText()));
            litter.setComments(txtComments.getText());
            litter.setAcceptanceNumber(txtAcceptanceNumber.getText());
            litter.setCachedAnimalsLeft(new Integer(-1));

            try {
                litter.save();

                if (AuditTrail.enabled()) {
                    AuditTrail.updated(isNew, "animallitter",
                        litter.getSpeciesName() + " " +
                        litter.getNumberInLitter().toString());
                }

                if (parent != null) {
                    parent.updateList();
                }

                dispose();

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getBorderLayout());
        UI.Panel pt = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pc = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pb = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pbut = UI.getPanel(UI.getFlowLayout());

        alParentName = (AnimalLink) UI.addComponent(pt,
                i18n("Mother:"),
                new AnimalLink());

        cboSpecies = UI.getCombo(i18n("Species:"),
                LookupCache.getSpeciesLookup(), "SpeciesName");
        cboSpecies.setToolTipText(i18n("The_species_of_the_litter"));
        UI.addComponent(pt, i18n("Species:"), cboSpecies);

        txtDate = (DateField) UI.addComponent(pt, i18n("Date:"),
                UI.getDateField(i18n("The_date_the_litter_was_born_or_entered_the_shelter")));

        txtNumber = (UI.TextField) UI.addComponent(pt,
                i18n("Number_in_litter"),
                UI.getTextField(i18n("The_number_of_animals_in_the_litter")));

        txtComments = (UI.TextArea) UI.addComponent(pc, i18n("Comments:"),
                UI.getTextArea());

        txtInvalidDate = (DateField) UI.addComponent(pb, i18n("Expiry_Date"),
                UI.getDateField(i18n("The_date_this_litter_expires")));

        txtAcceptanceNumber = (UI.TextField) UI.addComponent(pb,
                Configuration.getBoolean("AutoLitterIdentification")
                ? i18n("litter_id") : i18n("Acceptance:"), UI.getTextField());
        txtAcceptanceNumber.setToolTipText(Configuration.getBoolean(
                "AutoLitterIdentification")
            ? i18n("unique_identifier_for_this_litter")
            : i18n("The_acceptance_number_for_all_the_animals_in_this_litter_-_the_system_can_automatically_remove_litters"));

        btnOk = (UI.Button) pbut.add(UI.getButton(i18n("Ok"),
                    i18n("Save_this_record"), 'o', null, UI.fp(this, "saveData")));

        btnCancel = (UI.Button) pbut.add(UI.getButton(i18n("Cancel"),
                    i18n("Abandon_this_record"), 'c', null,
                    UI.fp(this, "dispose")));

        p.add(pt, UI.BorderLayout.NORTH);
        p.add(pc, UI.BorderLayout.CENTER);
        p.add(pb, UI.BorderLayout.SOUTH);
        add(p, UI.BorderLayout.CENTER);
        add(pbut, UI.BorderLayout.SOUTH);
    }
}
