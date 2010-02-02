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
package net.sourceforge.sheltermanager.asm.ui.movement;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.EntryReason;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFind;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFindText;
import net.sourceforge.sheltermanager.asm.ui.diary.DiarySelector;
import net.sourceforge.sheltermanager.asm.ui.log.LogSelector;
import net.sourceforge.sheltermanager.asm.ui.owner.DonationSelector;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLinkListener;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CustomUI;
import net.sourceforge.sheltermanager.asm.ui.ui.DateChangedListener;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.MovementDocument;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for editing individual movement records.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class MovementEdit extends ASMForm implements DateChangedListener,
    SearchListener, OwnerLinkListener {
    /** Reference to parent list */
    private MovementParent parent = null;

    /** The current record */
    public Adoption movement = null;

    /** Animal Link */
    private int animalID = 0;

    /** Owner Link */
    private int ownerID = 0;

    /** The selected Animal ID */
    private int selectedAnimalID = 0;

    /** The selected Owner ID */
    private int selectedOwnerID = 0;

    /** The selected Retailer ID */
    private int selectedRetailerID = 0;

    /**
     * The retailer movement that spawned this adoption if created from the
     * retailer book.
     */
    private int originalRetailerMovement = 0;

    /** Whether data has changed */
    private boolean isDirty = false;

    /** The log tab panel, upcast from the JPanel in the editor */
    private LogSelector log = null;

    /** The diarybean tab panel, upcast from the JPanel in the editor */
    private DiarySelector diary = null;

    /** Whether we've added any custom buttons to the screen */
    private boolean addedCustomButtons = false;

    /** The movement donation panel, upcast from the JPanel in the editor */
    private DonationSelector donations = null;
    private UI.Button btnDoc;
    private UI.Button btnSave;
    private UI.Button btnViewAnimal;
    private UI.Button btnViewOwner;
    private UI.ComboBox cboMovementType;
    private UI.ComboBox cboReturnReason;
    private UI.TabbedPane tabTabs;
    private UI.ToolBar tlbTools;
    private UI.SearchTextField txtAnimalName;
    private UI.TextArea txtComments;
    private DateField txtDateReturned;
    private UI.SearchTextField txtInsurance;
    private DateField txtMovementDate;
    private UI.TextField txtNumber;
    private OwnerLink olOwnerName;
    private UI.TextArea txtReason;
    private DateField txtReservationCancelled;
    private DateField txtReservationDate;
    private OwnerLink olRetailerName;
    private String audit = null;
    private boolean isNewRecord = false;

    /** Creates new form MovementEdit */
    public MovementEdit(MovementParent theparent) {
        this.parent = theparent;
        init(Global.i18n("uimovement", "Edit_Movement"),
            IconManager.getIcon(IconManager.SCREEN_EDITMOVEMENT), "uimovement");
    }

    public String getAuditInfo() {
        return audit;
    }

    /**
     * Sets the movement type based on an ID and disables the combo to prevent
     * changes. If MOVETYPE == NONE, then disable the movement date field as it
     * isn't valid.
     *
     * @param moveType
     *            A MOVETYPE from the <code>Adoption</code> class.
     * @param date
     *            The date to put in the box
     * @param enable
     *            A boolean representing whether or not the movement type should
     *            be disabled.
     */
    public void setMoveType(int moveType, String date, boolean enable) {
        cboMovementType.setSelectedIndex(moveType);
        cboMovementType.setEnabled(enable);

        if (moveType == Adoption.MOVETYPE_NONE) {
            txtMovementDate.getTextField().setEnabled(enable);
            txtReservationDate.setText(date);
        } else {
            txtMovementDate.setText(date);
        }
    }

    /**
     * Overloaded version of setMoveType - supplies todays date.
     */
    public void setMoveType(int moveType) {
        try {
            setMoveType(moveType, Utils.formatDate(Calendar.getInstance()),
                false);
        } catch (Exception e) {
        }
    }

    /**
     * Called from the Retailer book to stamp a movement with the retailer
     * movement that generated it. ASM doesn't use this information anywhere,
     * but it allows custom reports to analyse it where necessary.
     */
    public void setOriginalRetailerMovement(int movementID) {
        originalRetailerMovement = movementID;
    }

    public void dispose() {
        try {
            diary.dispose();
        } catch (Exception e) {
        }

        try {
            log.dispose();
        } catch (Exception e) {
        }

        diary = null;
        log = null;
        movement.free();
        parent = null;
        movement = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtNumber);
        ctl.add(txtInsurance);
        ctl.add(txtReservationDate);
        ctl.add(txtReservationCancelled);
        ctl.add(cboMovementType);
        ctl.add(txtMovementDate);
        ctl.add(txtComments);
        ctl.add(txtDateReturned);
        ctl.add(cboReturnReason);
        ctl.add(txtReason);
        ctl.add(btnSave);
        ctl.add(btnDoc);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtMovementDate.getTextField();
    }

    /**
     * Activates/deactivates bits according to what users are allowed to do.
     */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnDoc.setEnabled(false);
        }

        // Tabs/View
        if (!Global.currentUserObject.getSecViewDiaryNotes()) {
            tabTabs.setEnabledAt(1, false);
        }

        if (!Global.currentUserObject.getSecViewOwnerDonation()) {
            tabTabs.setEnabledAt(2, false);
        }

        if (!Global.currentUserObject.getSecViewLogEntry()) {
            tabTabs.setEnabledAt(3, false);
        }

        // See if auto insurance numbers are enabled
        txtInsurance.setButtonEnabled(Configuration.getBoolean(
                "UseAutoInsurance"));
    }

    public void dataChanged() {
        isDirty = true;
        btnSave.setEnabled(isDirty);
    }

    /**
     * Sets us into new adoption creation mode. Both parameters can be zero,
     * meaning they both must be selected.
     *
     * @param animal
     *            The ID of the animal it is for (or 0 for non-animal)
     * @param owner
     *            The ID of the owner it is for (or 0 for non-owner)
     */
    public void openForNew(int animal, int owner) {
        this.setTitle(i18n("Create_New_Movement"));
        movement = new Adoption();
        isNewRecord = true;

        try {
            movement.openRecordset("ID = 0");
            movement.addNew();
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("Unable_to_create_new_movement:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        enableExternalTabs(false);

        try {
            if (Configuration.getBoolean("AutoReservationDate")) {
                Calendar cal = Calendar.getInstance();
                this.txtReservationDate.setText(Utils.formatDate(cal));
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        this.cboMovementType.setSelectedIndex((int) 0);

        this.selectedOwnerID = owner;
        this.selectedAnimalID = animal;

        // Default ID as adoption number
        try {
            this.txtNumber.setText(movement.getID().toString());
        } catch (CursorEngineException e) {
        }

        // Fill default box and disable the selector for whereever
        // this record was created from -- if it was.
        if (owner != 0) {
            olOwnerName.setID(selectedOwnerID);
            olOwnerName.setEnabled(false);
            txtAnimalName.setButtonEnabled(true);
        } else if (animal != 0) {
            Animal obj = new Animal();

            try {
                obj.openRecordset("ID = " + selectedAnimalID);
                txtAnimalName.setText(obj.getShelterCode() + " - " +
                    obj.getAnimalName());
            } catch (Exception e) {
            }

            olOwnerName.setEnabled(true);
            txtAnimalName.setButtonEnabled(false);
        }

        if ((animal == 0) && (owner == 0)) {
            olOwnerName.setEnabled(true);
            txtAnimalName.setButtonEnabled(true);
        }

        // if this is an animal movement, was the last movement
        // performed for it a returned retailer? If so, ask if the
        // user wants to default it as a retailer for this record.
        if (animal != 0) {
            Adoption ad = new Adoption();
            ad.openRecordset("AnimalID = " + animal + " AND " +
                "MovementType = " + Adoption.MOVETYPE_RETAILER + " AND " +
                "ReturnDate Is Not Null");

            if (!ad.getEOF()) {
                if (Dialog.showYesNo(i18n("this_animal_was_previously_at_a_retailer_do_you_want_to_mark_this_movement_as_an_adoption_from_that_retailer?"),
                            i18n("retailer_adoption"))) {
                    try {
                        olRetailerName.setID(ad.getOwnerID().intValue());
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }
                }
            }
        }

        // Set the default return reason
        Utils.setComboFromID(LookupCache.getEntryReasonLookup(),
            "ReasonName",
            new Integer(Configuration.getInteger(
            "AFDefaultReturnReason")), cboReturnReason);
        updateReturn();

        enableButtons();
        dataChanged();

    }

    private void updateReturn() {
        cboReturnReason.setEnabled(!txtDateReturned.getText().equals(""));
        txtReason.setEnabled(!txtDateReturned.getText().equals(""));
    }

    /**
     * Sets us into editing mode.
     *
     * @param themovement
     *            The movement business object to edit.
     * @param linkType
     *            0 for neither, 1 for animal, 2 for owner.
     */
    public void openForEdit(Adoption themovement, int linkType) {
        try {
            movement = themovement;

            String ownername = "";

            if (movement.getOwnerID().intValue() == 0) {
                ownername = i18n("(none)");
            } else {
                ownername = movement.getOwner().getOwnerName();
                // Load local variable from object
                ownerID = movement.getOwnerID().intValue();
            }

            String retailername = "";

            if (movement.getRetailerID().intValue() == 0) {
                retailername = i18n("(none)");
            } else {
                retailername = movement.getRetailer().getOwnerName();
            }

            // Load local variable from object
            animalID = movement.getAnimalID().intValue();

            this.setTitle(i18n("edit_movement_title",
                    movement.getAdoptionNumber(),
                    movement.getAnimal().getAnimalName(),
                    movement.getAnimal().getShelterCode(), ownername));

            // Load the data into the controls

            // Created and Changed values
            audit = i18n("created_lastchange",
                    Utils.formatDateTimeLong(movement.getCreatedDate()),
                    movement.getCreatedBy(),
                    Utils.formatDateTimeLong(movement.getLastChangedDate()),
                    movement.getLastChangedBy());

            this.txtReason.setText(Utils.nullToEmptyString(
                    movement.getReasonForReturn()));
            this.txtAnimalName.setText(movement.getAnimal().getShelterCode() +
                " - " + movement.getAnimal().getAnimalName());
            this.txtInsurance.setText(Utils.nullToEmptyString(
                    movement.getInsuranceNumber()));
            this.txtNumber.setText(Utils.nullToEmptyString(
                    movement.getAdoptionNumber()));

            // Combos
            Utils.setComboFromID(LookupCache.getEntryReasonLookup(),
                "ReasonName", movement.getReturnedReasonID(), cboReturnReason);

            if (movement.getOwnerID().intValue() != 0) {
                this.olOwnerName.setID(movement.getOwnerID().intValue());
            }

            if (movement.getRetailerID().intValue() != 0) {
                this.olRetailerName.setID(movement.getRetailerID().intValue());
            }

            this.txtComments.setText(Utils.nullToEmptyString(
                    movement.getComments()));

            // Set links
            selectedAnimalID = movement.getAnimalID().intValue();
            selectedOwnerID = movement.getOwnerID().intValue();
            selectedRetailerID = movement.getRetailerID().intValue();

            // Deactivate selector buttons
            txtAnimalName.setButtonEnabled(linkType != 1);
            olOwnerName.setEnabled(linkType != 2);

            try {
                this.txtDateReturned.setText(Utils.formatDate(
                        movement.getReturnDate()));
                this.txtMovementDate.setText(Utils.formatDate(
                        movement.getMovementDate()));
                this.txtReservationCancelled.setText(Utils.formatDate(
                        movement.getReservationCancelledDate()));
                this.txtReservationDate.setText(Utils.formatDate(
                        movement.getReservationDate()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // If we have a valid movement date, don't allow entry
            // of the reservation fields - it's too late!
            if (movement.getMovementDate() != null) {
                txtReservationDate.getTextField().setEnabled(false);
                txtReservationCancelled.getTextField().setEnabled(false);
            }

            Utils.setComboFromID(LookupCache.getMovementTypeLookup(),
                "MovementType", movement.getMovementType(), cboMovementType);

            btnSave.setEnabled(false);
            isDirty = false;

            updateReturn();
            loadExternal();
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    /** Updates the buttons */
    public void enableButtons() {
        btnDoc.setEnabled(!isNewRecord);
        btnViewAnimal.setEnabled(animalID != 0);
        btnViewOwner.setEnabled(ownerID != 0);
        setSecurity();
    }

    /** Loads data into the external tabs and enables them */
    public void loadExternal() {
        // Add any custom buttons the user set
        try {
            if (!addedCustomButtons) {
                CustomUI.addCustomMovementButtons(tlbTools,
                    movement.getID().intValue());
                addedCustomButtons = true;
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Diary
        try {
            diary.setLink(movement.getID().intValue(), Diary.LINKTYPE_MOVEMENT);
            diary.updateList();

            // Flag the tab if there is content
            if (diary.hasData()) {
                tabTabs.setIconAt(1,
                    IconManager.getIcon(IconManager.SCREEN_EDITMOVEMENT_DIARY));
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Donations
        try {
            donations.setLink(animalID, ownerID, movement.getID().intValue());
            donations.updateList();

            // Flag the tab if there is content
            if (donations.hasData()) {
                tabTabs.setIconAt(2,
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITMOVEMENT_DONATIONS));
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Log
        try {
            log.setLink(movement.getID().intValue(), Log.LINKTYPE_MOVEMENT);
            log.updateList();

            // Flag the tab if there is content
            if (log.hasData()) {
                tabTabs.setIconAt(3,
                    IconManager.getIcon(IconManager.SCREEN_EDITMOVEMENT_LOG));
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        enableExternalTabs(true);
    }

    public void enableExternalTabs(boolean b) {
        tabTabs.setEnabledAt(1, b);
        tabTabs.setEnabledAt(2, b);
        tabTabs.setEnabledAt(3, b);
        setSecurity();
    }

    public boolean saveData() {
        if (!isDirty) {
            return false;
        }

        try {
            movement.setReasonForReturn(txtReason.getText());
            movement.setAnimalID(new Integer(selectedAnimalID));
            movement.setOwnerID(new Integer(selectedOwnerID));
            movement.setRetailerID(new Integer(selectedRetailerID));
            movement.setOriginalRetailerMovementID(new Integer(
                    originalRetailerMovement));
            movement.setInsuranceNumber(txtInsurance.getText());
            movement.setAdoptionNumber(txtNumber.getText());
            movement.setComments(txtComments.getText());
            movement.setMovementType(Utils.getIDFromCombo(
                    LookupCache.getMovementTypeLookup(), "MovementType",
                    cboMovementType));

            try {
                // Dates
                movement.setMovementDate(Utils.parseDate(
                        txtMovementDate.getText()));
                movement.setReturnDate(Utils.parseDate(
                        txtDateReturned.getText()));
                movement.setReservationDate(Utils.parseDate(
                        txtReservationDate.getText()));
                movement.setReservationCancelledDate(Utils.parseDate(
                        txtReservationCancelled.getText()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }

            // Combos
            movement.setReturnedReasonID(Utils.getIDFromCombo(
                    LookupCache.getEntryReasonLookup(), "ReasonName",
                    cboReturnReason));

            // Perform some front end validation before the save
            // in case certain other checks have to be made that require
            // user interaction
            if (!performFrontValidation()) {
                Global.logDebug("Frontend validation failed, dropping out.",
                    "EditMovement.saveData");

                return false;
            }

            // Attempt the save and update the parent
            try {
                movement.save(Global.currentUserName);

                // Update the animal's denormalised data
                // as a result of this save.
                Animal.updateAnimalStatus(movement.getAnimalID().intValue());

                // Record is clean
                isNewRecord = false;
                isDirty = false;
                btnSave.setEnabled(false);

                // Update parent
                if (parent != null) {
                    parent.updateList();
                }

                enableButtons();
                loadExternal();

                return true;
            } catch (CursorEngineException e) {
                // Show validation errors
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
            }
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_saving_the_record:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void loadData() {
        // See openForEdit
    }

    /**
     * Performs front end validation for other movement checks that require user
     * interaction
     *
     * @return true if validation was passed
     */
    public boolean performFrontValidation() {
        try {
            // If a movement date has been specified, but the movement
            // type is None, then make it null right now
            if ((movement.getMovementDate() != null) &&
                    (movement.getMovementType().intValue() == Adoption.MOVETYPE_NONE)) {
                movement.setMovementDate(null);
                this.txtMovementDate.setText("");
            }

            // If a movement type has been specified, but the date is
            // empty, fill it in as today's date
            if ((movement.getMovementDate() == null) &&
                    (movement.getMovementType().intValue() != Adoption.MOVETYPE_NONE)) {
                this.txtMovementDate.setToToday();
                movement.setMovementDate(new Date());
            }

            // Owners are not required for Escaped, Stolen, Reclaimed
            // and Released To Wild
            if ((movement.getOwnerID().intValue() == 0) &&
                    (movement.getMovementType().intValue() != Adoption.MOVETYPE_ESCAPED) &&
                    (movement.getMovementType().intValue() != Adoption.MOVETYPE_STOLEN) &&
                    (movement.getMovementType().intValue() != Adoption.MOVETYPE_RECLAIMED) &&
                    (movement.getMovementType().intValue() != Adoption.MOVETYPE_RELEASED)) {
                Dialog.showError(Global.i18n("bo",
                        "You_must_select_an_owner_for_this_type_of_movement."));
                return false;
            }

            // Check to see if the new owner lives in the same
            // postcode region as the original owner and warn if the
            // option is on
            if (movement.getOwnerID().intValue() != 0) {
                if (Configuration.getBoolean("WarnOOPostcode")) {
                    String nopcode = movement.getOwner().getOwnerPostcode();
                    String oopcode = "";

                    try {
                        oopcode = movement.getAnimal().getOriginalOwner()
                                          .getOwnerPostcode();
                    } catch (Exception e) {
                    }

                    if ((nopcode.length() >= 2) && (oopcode.length() >= 2)) {
                        if (nopcode.substring(0, 2)
                                       .equalsIgnoreCase(oopcode.substring(0, 2))) {
                            Dialog.showWarning(i18n("WARNING:_The_new_owner_lives_in_the_same_postcode_as_this_\nanimal's_original_owner_(") +
                                oopcode.toUpperCase() + ")",
                                i18n("Postcode_Warning"));
                        }
                    }
                }
            }

            // If this is a reservation adoption (ie. Date reserved is
            // completed, but
            // nothing else is). Then check to see if the same animal has
            // another
            // record with an uncancelled reservation and warn if so.
            if (Configuration.getBoolean("WarnMultipleReserves")) {
                if (!txtReservationDate.getText().equals("") &&
                        txtReservationCancelled.getText().equals("") &&
                        (cboMovementType.getSelectedIndex() == 0)) {
                    Adoption a = new Adoption();
                    a.openRecordset("AnimalID = " + selectedAnimalID +
                        " AND ReservationDate Is Not Null And ReservationCancelledDate Is Null And MovementDate Is Null And ID <> " +
                        movement.getID());

                    if (!a.getEOF()) {
                        if (!Dialog.showYesNoWarning(i18n("This_animal_already_has_an_uncancelled_reservation,_are_you_sure_you_wish_to_continue_with_this_record?\nClicking_NO_will_close_this_screen_and_abandon_the_current_record."),
                                    i18n("Existing_Reservation"))) {
                            dispose();

                            return false;
                        }
                    }
                }
            }

            // If this is an adoption record, see if the owner had active
            // criteria and was looking for an animal. If so, prompt
            // the user to disable the criteria
            if (movement.getMovementType().intValue() == Adoption.MOVETYPE_ADOPTION) {
                if (movement.getOwner().getMatchActive().intValue() == 1) {
                    if (Dialog.showYesNoWarning(i18n("This_owner_has_active_match_criteria"),
                                i18n("Active_Criteria"))) {
                        try {
                            DBConnection.executeAction(
                                "UPDATE owner SET MatchActive = 0, MatchExpires = '" +
                                Utils.getSQLDate(new Date()) + "' WHERE ID = " +
                                ownerID);
                        } catch (Exception e) {
                            Global.logException(e, getClass());
                            Dialog.showError(e.getMessage());
                        }
                    }
                }
            }

            // If this is an adoption record, see if another movement
            // for the same animal has it on reservation. If it does,
            // Prompt the user to see if they want to cancel it
            if (Configuration.getBoolean("CancelReservesOnAdoption")) {
                if (movement.getMovementType().intValue() == Adoption.MOVETYPE_ADOPTION) {
                    Adoption a = new Adoption();
                    a.openRecordset("AnimalID = " + selectedAnimalID +
                        " AND ReservationDate Is Not Null And ReservationCancelledDate Is Null And MovementDate Is Null And ID <> " +
                        movement.getID());

                    if (!a.getEOF()) {
                        if (Dialog.showYesNoWarning(i18n("This_animal_is_still_reserved_to_another_owner(s)._Would_you_like_to_cancel_these_reservations_now?"),
                                    i18n("Still_Reserved"))) {
                            try {
                                while (!a.getEOF()) {
                                    a.setReservationCancelledDate(new Date());
                                    a.save();
                                    a.moveNext();
                                }
                            } catch (Exception e) {
                                Dialog.showError(i18n("An_error_occurred_cancelling_the_existing_reservation(s)._The_records_were_not_saved_and_you_will_have_to_remove_these_reservations_manually."));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_validating_the_record:_") +
                e.getMessage());
            Global.logException(e, getClass());
            return false;
        }

        return true;
    }

    /**
     * This Routine is called whenever something attempts to close the form to
     * verify if it should be allowed to close, and whether or not to show any
     * warning messages for unsaved changes etc.
     */
    public boolean formClosing() {
        // Update aggregate donation total when the screen closes
        if (!isNewRecord) {
            try {
                Adoption.updateDonation(movement.getID().intValue());
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // Don't destroy if the user has unsaved changes and are not sure
        if (this.isDirty) {
            if (!Dialog.showYesNoWarning(Global.i18n("uianimal",
                            "You_have_unsaved_changes_-_are_you_sure_you_wish_to_close?"),
                        Global.i18n("uianimal", "Unsaved_Changes"))) {
                return true;
            }
        }

        return false;
    }

    public void initComponents() {
        tabTabs = UI.getTabbedPane();
        diary = new DiarySelector();
        donations = new DonationSelector(null);
        log = new LogSelector();

        // Toolbar ==============================================
        tlbTools = UI.getToolBar();

        btnSave = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Save_this_record"), 's',
                    IconManager.getIcon(IconManager.SCREEN_EDITMOVEMENT_SAVE),
                    UI.fp(this, "actionSave")));

        btnDoc = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Generate_a_document_for_this_movement"), 'w',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITMOVEMENT_GENERATEDOC),
                    UI.fp(this, "actionDocument")));

        btnViewAnimal = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("view_animal"), 'a',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITMOVEMENT_VIEWANIMAL),
                    UI.fp(this, "actionViewAnimal")));

        btnViewOwner = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("View_owner"), 'o',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITMOVEMENT_VIEWOWNER),
                    UI.fp(this, "actionViewOwner")));

        add(tlbTools, UI.BorderLayout.NORTH);

        // Details pane ============================================

        // The whole screen is split into a grid of 4 cells.
        UI.Panel pnlDetails = UI.getPanel(UI.getGridLayout(2));

        // Panel 1 - Animal/Owner (top/left)
        // ------------------------------------
        UI.Panel aoouter = UI.getPanel(UI.getBorderLayout());
        aoouter.setTitle(i18n("Details"));

        UI.Panel ao = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        aoouter.add(ao, UI.BorderLayout.NORTH);

        txtAnimalName = (UI.SearchTextField) UI.addComponent(ao,
                i18n("Animal:"),
                UI.getSearchTextField(i18n("Select_an_animal"),
                    UI.fp(this, "actionSelectAnimal")));

        olOwnerName = (OwnerLink) UI.addComponent(ao, i18n("Owner:"),
                new OwnerLink(OwnerLink.MODE_ONELINE, OwnerLink.FILTER_NONE,
                    "OWNER"));
        olOwnerName.setParent(this);

        olRetailerName = new OwnerLink(OwnerLink.MODE_ONELINE,
                OwnerLink.FILTER_RETAILERS, "RETAILER");
        olRetailerName.setParent(this);

        if (!Configuration.getBoolean("DisableRetailer")) {
            UI.addComponent(ao, i18n("from_retailer"), olRetailerName);
        }

        txtNumber = (UI.TextField) UI.addComponent(ao, i18n("Number:"),
                UI.getTextField(i18n("The_reference_number_of_this_movement"),
                    UI.fp(this, "dataChanged")));

        txtInsurance = (UI.SearchTextField) UI.addComponent(ao,
                i18n("Insurance:"),
                UI.getSearchTextField(i18n("The_insurance_reference_number"),
                    i18n("Click_here_if_you_have_automatic_insurance_numbers_to_generate_one"),
                    true, UI.fp(this, "actionAutoInsurance"),
                    UI.fp(this, "dataChanged")));

        pnlDetails.add(aoouter);

        // Panel 2 - Reservation (top right)
        // ------------------------------------
        UI.Panel reouter = UI.getPanel(UI.getBorderLayout());
        reouter.setTitle(i18n("Reservation"));

        UI.Panel re = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        reouter.add(re, UI.BorderLayout.NORTH);

        txtReservationDate = (DateField) UI.addComponent(re, i18n("Date:"),
                UI.getDateField(i18n("The_reservation_date"),
                    UI.fp(this, "dataChanged")));

        txtReservationCancelled = (DateField) UI.addComponent(re,
                i18n("Cancelled:"),
                UI.getDateField(i18n("The_date_the_reservation_was_cancelled"),
                    UI.fp(this, "dataChanged")));

        pnlDetails.add(reouter);

        // Panel 3 - bottom left (movement)
        // ------------------------------------
        UI.Panel moouter = UI.getPanel(UI.getBorderLayout());
        moouter.setTitle(i18n("Movement"));

        UI.Panel mo = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        moouter.add(mo, UI.BorderLayout.NORTH);

        cboMovementType = (UI.ComboBox) UI.addComponent(mo, i18n("Type:"),
                UI.getCombo(i18n("Type:"), UI.fp(this, "movementTypeChanged")));

        // Load the combo list of types - cut out reservation movements
        // and retailers if the functionality is disabled
        try {
            SQLRecordset lt = LookupCache.getMovementTypeLookup();
            lt.moveFirst();

            while (!lt.getEOF()) {
                int idval = ((Integer) lt.getField("ID")).intValue();

                // Skip reservations
                if ((idval == 9) || (idval == 10)) {
                    lt.moveNext();

                    continue;
                }

                // Skip retailer if disabled
                if ((idval == 8) &&
                        Configuration.getBoolean("DisableRetailer")) {
                    lt.moveNext();

                    continue;
                }

                cboMovementType.addItem(lt.getField("MovementType").toString());

                lt.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        //for (int i = 0; i < 9; i++) {
        //   cboMovementType.addItem(LookupCache.getMoveTypeNameForID(
        //            new Integer(i)));
        //}
        txtMovementDate = (DateField) UI.addComponent(mo, i18n("Date:"),
                UI.getDateField(i18n("The_movement_date"),
                    UI.fp(this, "dataChanged")));

        UI.Panel moc = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        moouter.add(moc, UI.BorderLayout.CENTER);
        txtComments = (UI.TextArea) UI.addComponent(moc, i18n("Comments:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        pnlDetails.add(moouter);

        // Panel 4 - Return (bottom right)
        // ----------------------------------
        UI.Panel rtouter = UI.getPanel(UI.getBorderLayout());
        rtouter.setTitle(i18n("Returning"));

        UI.Panel rt = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        rtouter.add(rt, UI.BorderLayout.NORTH);

        txtDateReturned = (DateField) UI.addComponent(rt, i18n("Date:"),
                UI.getDateField(i18n("The_date_the_animal_was_returned_to_the_shelter"),
                    UI.fp(this, "dataChanged")));
        txtDateReturned.setDateChangedListener(this);

        cboReturnReason = UI.getCombo(i18n("Return_Category"),
                LookupCache.getEntryReasonLookup(), "ReasonName",
                UI.fp(this, "dataChanged"));
        UI.addComponent(rt, i18n("Return_Category"), cboReturnReason);

        UI.Panel rtc = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        rtouter.add(rtc, UI.BorderLayout.CENTER);

        txtReason = (UI.TextArea) UI.addComponent(rtc, i18n("Reason:"),
                UI.getTextArea(i18n("The_reason_the_animal_was_returned"),
                    UI.fp(this, "dataChanged")));

        pnlDetails.add(rtouter);

        tabTabs.addTab(i18n("Movement_Details"), pnlDetails);

        tabTabs.addTab(i18n("Diary"), diary);

        tabTabs.addTab(i18n("Donations"), donations);

        tabTabs.addTab(i18n("Log"), log);

        add(tabTabs, UI.BorderLayout.CENTER);
    }

    public void actionViewOwner() {
        if (this.ownerID == 0) {
            return;
        }

        UI.cursorToWait();

        OwnerEdit eo = new OwnerEdit();
        eo.openForEdit(this.ownerID);
        Global.mainForm.addChild(eo);
    }

    public void actionViewAnimal() {
        if (animalID == 0) {
            return;
        }

        UI.cursorToWait();

        Animal animal = LookupCache.getAnimalByID(new Integer(animalID));
        AnimalEdit ea = new AnimalEdit();
        ea.openForEdit(animal);
        Global.mainForm.addChild(ea);
    }

    public void returnDateChanged() {
        dataChanged();
        updateReturn();
    }

    public void movementTypeChanged() {
        // Default the movement date to today's date, unless
        // none is selected in which case blank it
        try {
            if (cboMovementType.getSelectedIndex() == 0) {
                txtMovementDate.setText("");
                olOwnerName.setFilter(OwnerLink.FILTER_NONE);
            } else {
                // We have a movement type - if there's no date, default today
                if (txtMovementDate.getText().trim().equals("")) {
                    txtMovementDate.setText(Utils.formatDate(
                            Calendar.getInstance()));
                }
            }

            // Update the owner filter
            switch (cboMovementType.getSelectedIndex()) {
            case Adoption.MOVETYPE_FOSTER:
                olOwnerName.setFilter(OwnerLink.FILTER_FOSTERERS);

                break;

            case Adoption.MOVETYPE_RETAILER:
                olOwnerName.setFilter(OwnerLink.FILTER_RETAILERS);

                break;

            case Adoption.MOVETYPE_TRANSFER:
                olOwnerName.setFilter(OwnerLink.FILTER_SHELTERS);

                break;
            }

            dataChanged();
        } catch (Exception e) {
        }
    }

    public void actionAutoInsurance() {
        if (!txtInsurance.getText().equals("")) {
            Dialog.showWarning(i18n("This_movement_record_already_has_an_insurance_number._Remove_it_if_you_wish_to_generate_another."),
                i18n("Already_Insured"));

            return;
        } else {
            txtInsurance.setText(movement.generateInsuranceNumber());
            dataChanged();
        }
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

    public void actionDocument() {
        MovementDocument md = new MovementDocument(movement, parent);
    }

    public void actionSave() {
        saveData();
        txtNumber.requestFocus();
    }

    /// OwnerLinkListener INTERFACE

    /** Callback from the ownerlink forms */
    public void ownerChanged(int ownerid, String id) {
        if (id.equals("OWNER")) {
            // Store the ID
            try {
                this.selectedOwnerID = ownerid;
                this.ownerID = ownerid;

                // Reparent any donations for this movement to the
                // newly selected owner (as long as they weren't clearing, in
                // which case better to leave the donations attached to the
                // old owner until a new one is selected).
                if (donations.hasData() && (ownerID != 0)) {
                    Global.logDebug("Reparenting donations for movement " +
                        movement.getID() + " to owner " + ownerID,
                        "MovementEdit.ownerSelected");
                    DBConnection.executeAction(
                        "UPDATE ownerdonation SET OwnerID = " + ownerID +
                        "WHERE MovementID = " + movement.getID().intValue());
                }

                // Update the donation link on screen
                donations.setLink(animalID, ownerID, movement.getID().intValue());

                // If the options are switched on, check to see if the owner
                // is banned or not homechecked:
                if (ownerID != 0) {
                    if (Configuration.getBoolean("WarnBannedOwner")) {
                        int banned = DBConnection.executeForCount(
                                "SELECT IsBanned FROM owner WHERE ID = " +
                                ownerid);

                        if (banned == 1) {
                            Dialog.showWarning(i18n("WARNING:_This_owner_is_banned_from_adopting_animals."),
                                i18n("Banned_Owner"));
                        }
                    }

                    if (Configuration.getBoolean("WarnNoHomeCheck")) {
                        int homechecked = DBConnection.executeForCount(
                                "SELECT IDCheck FROM owner WHERE ID = " +
                                ownerid);

                        if (homechecked == 0) {
                            Dialog.showWarning(i18n("WARNING:_This_owner_has_not_passed_a_home_check."),
                                i18n("Unchecked_Owner"));
                        }
                    }
                }

                dataChanged();
                enableButtons();
            } catch (Exception e) {
                Dialog.showError(i18n("Unable_to_open_find_owner_screen:_") +
                    e.getMessage());
                Global.logException(e, getClass());
            }
        } else if (id.equals("RETAILER")) {
            try {
                // Store the ID
                selectedRetailerID = ownerid;
                dataChanged();
            } catch (Exception e) {
                Dialog.showError(i18n("Unable_to_open_find_owner_screen:_") +
                    e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    /// SearchListener INTERFACE

    /** Call back from the animal search screen when a selection is made */
    public void animalSelected(Animal theanimal) {
        try {
            // We can't move non-shelter animals
            if (theanimal.getNonShelterAnimal().intValue() == 1) {
                Dialog.showError(i18n("You_cannot_move_nonshelter_animals",
                        theanimal.getShelterCode(), theanimal.getAnimalName()));

                return;
            }

            selectedAnimalID = theanimal.getID().intValue();
            animalID = selectedAnimalID;
            txtAnimalName.setText(theanimal.getShelterCode() + " - " +
                theanimal.getAnimalName());
            dataChanged();
            enableButtons();
        } catch (Exception e) {
            Dialog.showError(i18n("Unable_to_open_find_animal_screen:_") +
                e.getMessage());
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

    public void dateChanged(String newDate) {
        updateReturn();
    }
}
