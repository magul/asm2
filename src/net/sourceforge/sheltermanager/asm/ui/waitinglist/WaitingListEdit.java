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
package net.sourceforge.sheltermanager.asm.ui.waitinglist;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.diary.DiarySelector;
import net.sourceforge.sheltermanager.asm.ui.log.LogSelector;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLinkListener;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for editing waiting list entries.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class WaitingListEdit extends ASMForm implements OwnerLinkListener {
    private AnimalWaitingList awl = null;
    private WaitingListView parent = null;
    private DiarySelector diary = null;
    private MediaSelector media = null;
    private LogSelector log = null;
    private boolean isDirty = false;
    private UI.Button btnClone;
    private UI.Button btnSave;
    private UI.Button btnCreateAnimal;
    private UI.Button btnDelete;
    private UI.ComboBox cboSpecies;
    private UI.ComboBox cboUrgency;
    private UI.CheckBox chkCanAffordDonation;
    private OwnerLink embOwner;
    private UI.TabbedPane tabTabs;
    private UI.ToolBar tlbTools;
    private UI.Spinner spnAutoRemovePolicy;
    private UI.TextArea txtComments;
    private DateField txtDateOfLastOwnerContact;
    private DateField txtDatePutOn;
    private DateField txtDateRemoved;
    private UI.TextArea txtDescription;
    private UI.TextArea txtReason;
    private UI.TextArea txtReasonForRemoval;
    private String audit = null;
    private boolean isNew = false;

    public WaitingListEdit(WaitingListView theparent) {
        parent = theparent;
        init(Global.i18n("uiwaitinglist", "Edit_Waiting_List"),
            IconManager.getIcon(IconManager.SCREEN_EDITWAITINGLIST),
            "uiwaitinglist");
    }

    public void dispose() {
        try {
            log.dispose();
            log = null;
            awl.free();
            diary.dispose();
            media.dispose();
            parent = null;
            awl = null;
            diary = null;
            media = null;
        } catch (Exception e) {
            Global.logException(e, WaitingListEdit.class);
        } finally {
            super.dispose();
        }
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboSpecies);
        ctl.add(txtDatePutOn.getTextField());
        ctl.add(txtDescription);
        ctl.add(txtReason);
        ctl.add(chkCanAffordDonation);
        ctl.add(cboUrgency);
        ctl.add(txtDateRemoved.getTextField());
        ctl.add(txtReasonForRemoval);
        ctl.add(txtComments);
        ctl.add(spnAutoRemovePolicy);
        ctl.add(txtDateOfLastOwnerContact.getTextField());
        ctl.add(btnSave);
        ctl.add(btnClone);
        ctl.add(btnCreateAnimal);
        ctl.add(btnDelete);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboSpecies;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddWaitingList()) {
            btnClone.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimal()) {
            btnCreateAnimal.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeWaitingList()) {
            btnSave.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteWaitingList()) {
            btnDelete.setEnabled(false);
        }

        // Tabs/View
        if (!Global.currentUserObject.getSecViewDiaryNotes()) {
            tabTabs.setEnabledAt(2, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalMedia()) {
            tabTabs.setEnabledAt(3, false);
        }

        if (!Global.currentUserObject.getSecViewLogEntry()) {
            tabTabs.setEnabledAt(4, false);
        }
    }

    public boolean formClosing() {
        // Don't destroy if the user has unsaved changes and are not sure
        if (isDirty) {
            if (!Dialog.showYesNoWarning(i18n("You_have_unsaved_changes_-_are_you_sure_you_wish_to_close?"),
                        i18n("Unsaved_Changes"))) {
                return true;
            }
        }

        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void loadExternal() {
        try {
            // Diary
            diary.setLink(awl.getID().intValue(), Diary.LINKTYPE_WAITINGLIST);
            diary.updateList();

            // Flag the tab if there is content
            if (diary.hasData()) {
                tabTabs.setIconAt(2,
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITWAITINGLIST_DIARY));
            }

            // Make sure diary is enabled
            tabTabs.setEnabledAt(2, true);

            media.setLink(Media.LINKTYPE_WAITINGLIST, awl.getID().intValue());
            media.updateList();

            if (media.hasData()) {
                tabTabs.setIconAt(3,
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITWAITINGLIST_MEDIA));
            }

            // Make sure media is enabled
            tabTabs.setEnabledAt(3, true);

            // Log
            log.setLink(awl.getID().intValue(), Log.LINKTYPE_WAITINGLIST);
            log.updateList();

            // Flag the tab if there is content
            if (log.hasData()) {
                tabTabs.setIconAt(4,
                    IconManager.getIcon(IconManager.SCREEN_EDITWAITINGLIST_LOG));
            }

            // Make sure log is enabled
            tabTabs.setEnabledAt(4, true);

            setSecurity();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void dataChanged() {
        isDirty = true;
        btnSave.setEnabled(isDirty);
        setSecurity();
    }

    public void openForNew() {
        try {
            this.setTitle(i18n("New_Waiting_List_Entry"));
            awl = new AnimalWaitingList();
            awl.openRecordset("ID = 0");
            awl.addNew();

            // Set screen defaults
            txtDatePutOn.setText(Utils.formatDate(Calendar.getInstance()));
            txtDateOfLastOwnerContact.setText(Utils.formatDate(
                    Calendar.getInstance()));
            spnAutoRemovePolicy.setValue(new Integer(0));
            cboUrgency.setSelectedIndex(Configuration.getInteger(
                    "WaitingListDefaultUrgency"));

            // Set species from default
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                new Integer(Configuration.getInteger("AFDefaultSpecies")),
                cboSpecies);

            // Mark screen as dirty for new record
            dataChanged();

            // Make sure diary/media/log are deactivated until saved
            tabTabs.setEnabledAt(2, false);
            tabTabs.setEnabledAt(3, false);
            tabTabs.setEnabledAt(4, false);

            // Can't clone, create an animal or delete
            btnClone.setEnabled(false);
            btnCreateAnimal.setEnabled(false);
            btnDelete.setEnabled(false);

            isNew = true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void openForEdit(AnimalWaitingList theawl) {
        awl = theawl;
        loadData();
        isDirty = false;
        btnSave.setEnabled(isDirty);
    }

    public void refreshData() {
        if (isNew) return;
        if (formClosing() == false)
            openForEdit(awl);
    }

    public void loadData() {
        try {
            // Auditing information
            if (awl.getCreatedDate() != null) {
                audit = i18n("created_lastchange",
                        Utils.formatDateTimeLong(awl.getCreatedDate()),
                        awl.getCreatedBy(),
                        Utils.formatDateTimeLong(awl.getLastChangedDate()),
                        awl.getLastChangedBy());
            }

            this.setTitle(i18n("edit_waiting_list_title",
                    awl.getOwner().getOwnerName()));

            embOwner.loadFromID(awl.getOwnerID().intValue());

            chkCanAffordDonation.setSelected(awl.getCanAffordDonation()
                                                .equals(new Integer(1)));
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                awl.getSpeciesID(), cboSpecies);
            cboUrgency.setSelectedItem(formatUrgencyNumberAsString(
                    awl.getUrgency().intValue()));
            txtComments.setText(Utils.nullToEmptyString(awl.getComments()));
            txtDescription.setText(Utils.nullToEmptyString(
                    awl.getAnimalDescription()));
            txtReason.setText(Utils.nullToEmptyString(
                    awl.getReasonForWantingToPart()));
            txtReasonForRemoval.setText(Utils.nullToEmptyString(
                    awl.getReasonForRemoval()));

            if (awl.getAutoRemovePolicy() == null) {
                spnAutoRemovePolicy.setValue(new Integer(0));
            } else {
                spnAutoRemovePolicy.setValue(awl.getAutoRemovePolicy());
            }

            try {
                txtDatePutOn.setText(Utils.formatDate(awl.getDatePutOnList()));
                txtDateRemoved.setText(Utils.formatDate(
                        awl.getDateRemovedFromList()));
                txtDateOfLastOwnerContact.setText(Utils.formatDate(
                        awl.getDateOfLastOwnerContact()));
            } catch (Exception e) {
            }

            // Enable editing functions if security allows
            btnClone.setEnabled(true);
            btnCreateAnimal.setEnabled(true);
            btnDelete.setEnabled(true);
            setSecurity();

            // Load satellite data
            loadExternal();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    private String formatUrgencyNumberAsString(int urgency) {
        return LookupCache.getUrgencyNameForID(new Integer(urgency));
    }

    private int formatUrgencyNameAsNumber(String urgency) {
        return LookupCache.getUrgencyIDForName(urgency).intValue();
    }

    public boolean saveData() {
        if (!isDirty) {
            return false;
        }

        if (!Global.currentUserObject.getSecChangeWaitingList()) {
            Dialog.showError(UI.messageNoSavePermission());

            return false;
        }

        try {
            awl.setOwnerID(new Integer(embOwner.getID()));

            awl.setCanAffordDonation(chkCanAffordDonation.isSelected()
                ? new Integer(1) : new Integer(0));
            awl.setSpeciesID(Utils.getIDFromCombo(
                    LookupCache.getSpeciesLookup(), "SpeciesName", cboSpecies));
            awl.setUrgency(new Integer(formatUrgencyNameAsNumber(
                        (String) cboUrgency.getSelectedItem())));
            awl.setComments(txtComments.getText());
            awl.setAnimalDescription(txtDescription.getText());
            awl.setReasonForWantingToPart(txtReason.getText());
            awl.setReasonForRemoval(txtReasonForRemoval.getText());
            awl.setAutoRemovePolicy((Integer) spnAutoRemovePolicy.getValue());

            try {
                awl.setDatePutOnList(Utils.parseDate(txtDatePutOn.getText()));
                awl.setDateRemovedFromList(Utils.parseDate(
                        txtDateRemoved.getText()));
                awl.setDateOfLastOwnerContact(Utils.parseDate(
                        txtDateOfLastOwnerContact.getText()));
            } catch (ParseException e) {
                Dialog.showError(i18n("A_date_you_entered_was_not_valid:_\n") +
                    e.getMessage());
                Global.logException(e, getClass());
            }

            // Sort out the urgency rating stuff
            // Last updated to today
            awl.setUrgencyLastUpdatedDate(new Date());

            // Start the next update to the next scheduled period
            if (awl.getUrgencyUpdateDate() == null) {
                int updatePeriod = Configuration.getInteger(
                        "WaitingListUrgencyUpdatePeriod");
                Calendar nextUpdate = Utils.dateToCalendar(awl.getDatePutOnList());
                nextUpdate.add(Calendar.DAY_OF_YEAR, updatePeriod);
                awl.setUrgencyUpdateDate(Utils.calendarToDate(nextUpdate));
            }

            // Now do the save
            try {
                awl.save(Global.currentUserName);

                if (AuditTrail.enabled()) {
                    AuditTrail.updated(isNew, "animalwaitinglist",
                        awl.getSpeciesName() + " " +
                        awl.getAnimalDescription() + " " +
                        awl.getOwner().getOwnerName());
                }

                if (parent != null) {
                    parent.updateList();
                }

                // Reload to show audit info
                loadData();
                loadExternal();

                // Screen is no longer dirty
                this.isDirty = false;
                btnSave.setEnabled(isDirty);

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(e.getMessage(), i18n("Validation_Error"));
                Global.logException(e, getClass());
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void initComponents() {
        diary = new DiarySelector();
        log = new LogSelector();
        media = new MediaSelector();

        // Toolbar
        UI.ToolBar t = UI.getToolBar();

        btnSave = (UI.Button) t.add(UI.getButton(null, null, 's',
                    IconManager.getIcon(IconManager.SCREEN_EDITWAITINGLIST_SAVE),
                    UI.fp(this, "actionSave")));

        btnDelete = (UI.Button) t.add(UI.getButton(null, null, 'd',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITWAITINGLIST_DELETE),
                    UI.fp(this, "actionDelete")));

        btnClone = (UI.Button) t.add(UI.getButton(null,
                    i18n("Clone_this_record"), 'c',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITWAITINGLIST_CLONE),
                    UI.fp(this, "actionClone")));

        btnCreateAnimal = (UI.Button) t.add(UI.getButton(null,
                    i18n("Create_an_animal_from_this_record"), 'a',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITWAITINGLIST_CREATEANIMAL),
                    UI.fp(this, "actionCreateAnimal")));

        add(t, UI.BorderLayout.NORTH);

        // Details Pane =========================================
        UI.Panel pnlDetails = UI.getPanel(UI.getGridLayout(2));

        // Details Left ===
        UI.Panel pnlLeft = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlLeftTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlLeftMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));

        cboSpecies = UI.getCombo(i18n("Species:"),
                LookupCache.getSpeciesLookup(), "SpeciesName",
                UI.fp(this, "dataChanged"));
        UI.addComponent(pnlLeftTop, i18n("Species:"), cboSpecies);

        txtDatePutOn = (DateField) UI.addComponent(pnlLeftTop,
                i18n("Date_Put_On:"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        pnlLeft.add(pnlLeftTop, UI.BorderLayout.NORTH);

        txtDescription = (UI.TextArea) UI.addComponent(pnlLeftMid,
                i18n("Description:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        txtReason = (UI.TextArea) UI.addComponent(pnlLeftMid,
                i18n("Entry_Reason:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        pnlLeft.add(pnlLeftMid, UI.BorderLayout.CENTER);

        embOwner = new OwnerLink();
        embOwner.setTitle(i18n("Contact_Details:"));
        embOwner.setParent(this);
        pnlLeft.add(embOwner, UI.BorderLayout.SOUTH);

        pnlDetails.add(pnlLeft);

        // Details Right ===
        UI.Panel pnlRight = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlRightTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlRightMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));

        chkCanAffordDonation = (UI.CheckBox) UI.addComponent(pnlRightTop, "",
                UI.getCheckBox(i18n("Donation?"),
                    i18n("Tick_this_box_if_the_owner_made_a_donation"),
                    UI.fp(this, "dataChanged")));

        cboUrgency = UI.getCombo(i18n("Urgency:"),
                LookupCache.getUrgencyLookup(), "Urgency",
                UI.fp(this, "dataChanged"));
        UI.addComponent(pnlRightTop, i18n("Urgency:"), cboUrgency);

        txtDateRemoved = (DateField) UI.addComponent(pnlRightTop,
                i18n("Date_Removed:"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        pnlRight.add(pnlRightTop, UI.BorderLayout.NORTH);

        txtReasonForRemoval = (UI.TextArea) UI.addComponent(pnlRightMid,
                i18n("Removal_Reason:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        txtComments = (UI.TextArea) UI.addComponent(pnlRightMid,
                i18n("Comments:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        pnlRight.add(pnlRightMid, UI.BorderLayout.CENTER);

        pnlDetails.add(pnlRight);

        // Removal ==================================
        UI.Panel pnlRemoval = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlRemovalTop = UI.getPanel(UI.getGridLayout(3));

        spnAutoRemovePolicy = (UI.Spinner) UI.addComponent(pnlRemovalTop,
                i18n("remove_this_entry_after"),
                UI.getSpinner(0, 52,
                    i18n("automatically_remove_this_entry_after_x_weeks_without_owner_contact"),
                    UI.fp(this, "dataChanged")));
        pnlRemovalTop.add(UI.getLabel(UI.ALIGN_LEFT, i18n("weeks")));

        txtDateOfLastOwnerContact = (DateField) UI.addComponent(pnlRemovalTop,
                i18n("date_of_last_owner_contact"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        pnlRemoval.add(pnlRemovalTop, UI.BorderLayout.NORTH);
        // Add an empty label as the main widget on this panel so 
        // that SwingWT sizes top correctly
        pnlRemoval.add(UI.getLabel(), UI.BorderLayout.CENTER);

        // Tabs
        tabTabs = UI.getTabbedPane();
        tabTabs.addTab(i18n("Details"), pnlDetails);
        tabTabs.addTab(i18n("Removal"), pnlRemoval);
        tabTabs.addTab(i18n("Diary"), diary);
        tabTabs.addTab(i18n("Media"), media);
        tabTabs.addTab(i18n("Log"), log);

        add(tabTabs, UI.BorderLayout.CENTER);
    }

    public void actionDelete() {
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            try {
                String s = "Delete From animalwaitinglist Where ID = " +
                    awl.getID().intValue();
                net.sourceforge.sheltermanager.cursorengine.DBConnection.executeAction(s);

                if (parent != null) {
                    parent.updateList();
                }

                dispose();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionClone() {
        // Create a brand new AnimalWaitingList
        try {
            AnimalWaitingList naw = new AnimalWaitingList();
            naw.openRecordset("ID = 0");
            naw.addNew();

            // Copy the properties across
            naw.setOwnerID(awl.getOwnerID());
            naw.setAnimalDescription(awl.getAnimalDescription());
            naw.setReasonForWantingToPart(awl.getReasonForWantingToPart());
            naw.setReasonForRemoval(awl.getReasonForRemoval());
            naw.setDatePutOnList(awl.getDatePutOnList());
            naw.setDateRemovedFromList(awl.getDateRemovedFromList());
            naw.setCanAffordDonation(awl.getCanAffordDonation());
            naw.setSpeciesID(awl.getSpeciesID());
            naw.setUrgency(awl.getUrgency());
            naw.setComments(awl.getComments());

            // Create a new instance of this form and open it
            // up with the cloned object
            WaitingListEdit ewl = new WaitingListEdit(parent);
            ewl.openForEdit(naw);
            Global.mainForm.addChild(ewl);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionSave() {
        saveData();
        cboSpecies.grabFocus();
    }

    public void actionCreateAnimal() {
        // Make sure this record is saved
        saveData();

        try {
            // Make a new animal record
            Animal a = new Animal();
            a.openRecordset("ID = 0");
            a.addNew();

            a.setAnimalName("Waiting List " + awl.getID());
            a.setMarkings(awl.getAnimalDescription());
            a.setReasonForEntry(awl.getReasonForWantingToPart());
            a.setSpeciesID(awl.getSpeciesID());
            a.setAnimalComments(awl.getComments());
            a.setBroughtInByOwnerID(awl.getOwnerID());
            a.setOriginalOwnerID(awl.getOwnerID());

            // Make a new animal screen and use it
            AnimalEdit ea = new AnimalEdit();
            ea.openForEdit(a, false);

            // Load the default lookup values
            ea.setDefaults();

            // Reapply the species
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                awl.getSpeciesID(), ea.cboSpecies);

            Global.mainForm.addChild(ea);

            a = null;
            ea = null;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void ownerChanged(int ownerid, String id) {
        dataChanged();
    }
}
