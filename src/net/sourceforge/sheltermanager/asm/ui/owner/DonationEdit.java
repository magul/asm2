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
package net.sourceforge.sheltermanager.asm.ui.owner;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFind;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFindText;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * Edit a donation
 *
 * @author Robin Rawson-Tetley
 */
public class DonationEdit extends ASMForm implements SearchListener,
    OwnerLinkListener {
    private DonationSelector parent = null;
    private OwnerDonation od = null;
    private int animalID = 0;
    private int ownerID = 0;
    private int movementID = 0;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboFrequency;
    private UI.TextArea txtComments;
    private DateField txtDateDue;
    private DateField txtDateReceived;
    private CurrencyField txtDonation;
    private UI.ComboBox cboDonationType;
    private OwnerLink olOwner;
    private UI.SearchTextField alAnimal;
    private String audit = null;

    /** Creates new form EditOwnerDonation */
    public DonationEdit(DonationSelector parent, int animalID, int ownerID,
        int movementID) {
        this.animalID = animalID;
        this.ownerID = ownerID;
        this.movementID = movementID;
        this.parent = parent;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITOWNERDONATION),
            "uiowner");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtDateDue.getTextField());
        ctl.add(txtDateReceived.getTextField());
        ctl.add(cboFrequency);
        ctl.add(txtDonation.getTextField());
        ctl.add(cboDonationType);
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboDonationType;
    }

    public void dispose() {
        try {
            od.free();
        } catch (Exception e) {
        }

        parent = null;
        od = null;
        super.dispose();
    }

    public void openForEdit(OwnerDonation od) {
        this.od = od;

        try {
            txtDateDue.setText(Utils.formatDate(od.getDateDue()));
            txtDateReceived.setText(Utils.formatDate(od.getDateReceived()));
            txtDonation.setText(od.getDonation().toString());
            cboFrequency.setSelectedIndex(((Integer) od.getFrequency()).intValue());
            Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                "DonationName", od.getDonationTypeID(), cboDonationType);
            txtComments.setText(od.getComments());

            if ((od.getOwnerID() != null) && (olOwner != null)) {
                olOwner.setID(od.getOwnerID());
            }

            if ((od.getAnimalID().intValue() != 0) && (alAnimal != null)) {
                SQLRecordset a = new SQLRecordset();
                a.openRecordset(
                    "SELECT ShelterCode, AnimalName FROM animal WHERE ID = " +
                    od.getAnimalID(), "animal");

                if (!a.getEOF()) {
                    alAnimal.setText(a.getField("ShelterCode").toString() +
                        " - " + a.getField("AnimalName").toString());
                }

                a.free();
            }

            setTitle(i18n("edit_owner_donation"));
            audit = UI.messageAudit(od.getCreatedDate(), od.getCreatedBy(),
                    od.getLastChangedDate(), od.getLastChangedBy());
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void openForNew() {
        try {
            this.od = new OwnerDonation();
            od.openRecordset("ID = 0");
            od.addNew();
            od.setAnimalID(new Integer(animalID));
            od.setOwnerID(new Integer(ownerID));
            od.setMovementID(new Integer(movementID));

            if (movementID > 0) {
                this.txtDonation.setText(LookupCache.getDonationAmountForMovementSpecies(
                        movementID).toString());
            }

            // Set default donation type if we have one
            Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                "DonationName",
                new Integer(Configuration.getInteger("AFDefaultDonationType")),
                cboDonationType);

            this.setTitle(i18n("new_owner_donation"));
        } catch (Exception e) {
            Dialog.showError(i18n("unable_to_create_new_ownerdonation") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel but = UI.getPanel(UI.getFlowLayout());

        txtDateDue = (DateField) UI.addComponent(top, i18n("date_due"),
                UI.getDateField());

        txtDateReceived = (DateField) UI.addComponent(top,
                i18n("date_received"), UI.getDateField());

        txtDonation = (CurrencyField) UI.addComponent(top, i18n("donation"),
                UI.getCurrencyField());

        cboFrequency = UI.getCombo(LookupCache.getDonationFreqLookup(),
                "Frequency");
        cboFrequency.setSelectedIndex(0);
        UI.addComponent(top, i18n("frequency"), cboFrequency);

        cboDonationType = UI.getCombo(i18n("type"),
                LookupCache.getDonationTypeLookup(), "DonationName");
        UI.addComponent(top, i18n("type"), cboDonationType);

        // If we have no owner ID, allow box to choose it
        if (ownerID == 0) {
            olOwner = (OwnerLink) UI.addComponent(top, i18n("Owner"),
                    new OwnerLink(OwnerLink.MODE_ONELINE,
                        OwnerLink.FILTER_NONE, "OWNER"));
            olOwner.setParent(this);
        }

        // If we have no animal ID, allow box to choose it
        if (animalID == 0) {
            alAnimal = (UI.SearchTextField) UI.addComponent(top,
                    i18n("Animal"),
                    UI.getSearchTextField(i18n("Select_an_animal"),
                        UI.fp(this, "actionSelectAnimal")));
        }

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

    public void loadData() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public boolean saveData() {
        // Save values
        try {
            od.setDateReceived(Utils.parseDate(txtDateReceived.getText()));
            od.setDateDue(Utils.parseDate(txtDateDue.getText()));
            od.setDonation(new Double(txtDonation.getText()));
            od.setFrequency(new Integer(cboFrequency.getSelectedIndex()));
            od.setDonationTypeID(Utils.getIDFromCombo(
                    LookupCache.getDonationTypeLookup(), "DonationName",
                    cboDonationType));
            od.setComments(txtComments.getText());

            // Do we have a frequency > 0, the nextcreated flag isn't set
            // and there's a datereceived and a datedue?
            if ((od.getDateDue() != null) && (od.getDateReceived() != null) &&
                    (((Integer) od.getFrequency()).intValue() > 0) &&
                    (((Integer) od.getNextCreated()).intValue() == 0)) {
                OwnerDonation od2 = od.copy();

                // Clear the date received and update the date due
                Calendar c = Calendar.getInstance();
                c.setTime(((Date) od.getDateDue()));

                int freq = ((Integer) od.getFrequency()).intValue();

                switch (freq) {
                case 1: // weekly
                    c.add(Calendar.WEEK_OF_YEAR, 1);

                    break;

                case 2: // monthly
                    c.add(Calendar.MONTH, 1);

                    break;

                case 3: // quarterly
                    c.add(Calendar.MONTH, 3);

                    break;

                case 4: // half-yearly
                    c.add(Calendar.MONTH, 6);

                    break;

                case 5: // annually
                    c.add(Calendar.YEAR, 1);

                    break;
                }

                od2.setDateReceived(null);
                od2.setDateDue(c.getTime());

                // Save our next instalment
                od2.save(Global.currentUserName);

                // Update the created flag for this donation
                od.setNextCreated(new Integer(1));
            }

            od.save(Global.currentUserName);

            // Update parent
            parent.updateList();

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void actionSelectAnimal() {
        // Create and show a new find animal form and put it in selection mode
        // based on system default choice
        if (Configuration.getBoolean("AdvancedFindAnimal")) {
            Global.mainForm.addChild(new AnimalFind(this));
        } else {
            Global.mainForm.addChild(new AnimalFindText(this));
        }
    }

    /** Call back from the animal search screen when a selection is made */
    public void animalSelected(Animal theanimal) {
        try {
            animalID = theanimal.getID().intValue();
            od.setAnimalID(new Integer(animalID));
            alAnimal.setText(theanimal.getShelterCode() + " - " +
                theanimal.getAnimalName());
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void foundAnimalSelected(AnimalFound thefoundanimal) {
    }

    public void lostAnimalSelected(AnimalLost thelostanimal) {
    }

    public void ownerSelected(Owner theowner) {
    }

    public void retailerSelected(Owner theowner) {
    }

    public void ownerChanged(int ownerid, String id) {
        try {
            if (id.equals("OWNER")) {
                this.ownerID = ownerid;
                od.setOwnerID(new Integer(ownerid));
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
