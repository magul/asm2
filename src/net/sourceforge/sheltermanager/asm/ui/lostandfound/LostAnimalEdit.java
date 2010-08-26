/*
 Animal Shelter Manager
 Copyright(c)2000-2010, R. Rawson-Tetley

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Lostation; either version 2 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTIBILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the
 Free Software Lostation, Inc., 59 Temple Place - Suite 330, Boston
 MA 02111-1307, USA.

 Contact me by electronic mail: bobintetley@users.sourceforge.net
 */
package net.sourceforge.sheltermanager.asm.ui.lostandfound;

import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.LostFoundMatch;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.diary.DiarySelector;
import net.sourceforge.sheltermanager.asm.ui.log.LogSelector;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLinkListener;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
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
 * This class contains all code for editing found animal records.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class LostAnimalEdit extends ASMForm implements OwnerLinkListener {
    private MediaSelector media = null;
    private AnimalLost animal = null;
    private DiarySelector diary = null;
    private LogSelector log = null;
    private boolean isDirty = false;
    private UI.Button btnDelete;
    private UI.Button btnMatch;
    private UI.Button btnSave;
    private UI.ComboBox cboColour;
    private UI.ComboBox cboSex;
    private UI.ComboBox cboBreed;
    private UI.ComboBox cboAgeGroup;
    private UI.ComboBox cboSpecies;
    private net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink embOwner;
    private UI.Label lblNumber;
    private UI.TabbedPane tabTabs;
    private UI.ToolBar tlbTools;
    private UI.TextArea txtArea;
    private UI.TextArea txtComments;
    private DateField txtDate;
    private DateField txtDateFound;
    private UI.TextArea txtFeatures;
    private UI.TextField txtPostcode;
    private DateField txtReported;
    private String audit = null;
    private boolean isNewRecord = false;

    /** Creates new form EditLostAnimal */
    public LostAnimalEdit() {
        init("", IconManager.getIcon(IconManager.SCREEN_EDITLOSTANIMAL),
            "uilostandfound");
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecDeleteLostAnimals()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeLostAnimals()) {
            btnSave.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecMatchLostAndFoundAnimals()) {
            btnMatch.setEnabled(false);
        }

        // Tabs/View
        if (!Global.currentUserObject.getSecViewAnimalMedia()) {
            tabTabs.setEnabledAt(1, false);
        }

        if (!Global.currentUserObject.getSecViewDiaryNotes()) {
            tabTabs.setEnabledAt(2, false);
        }

        if (!Global.currentUserObject.getSecViewLogEntry()) {
            tabTabs.setEnabledAt(3, false);
        }
    }

    public void dispose() {
        log.dispose();
        log = null;
        animal.free();
        unregisterTabOrder();
        diary.dispose();
        media.dispose();
        diary = null;
        media = null;
        animal = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtDate.getTextField());
        ctl.add(txtReported.getTextField());
        ctl.add(cboAgeGroup);
        ctl.add(cboSex);
        ctl.add(cboSpecies);
        ctl.add(cboBreed);
        ctl.add(cboColour);
        ctl.add(txtFeatures);
        ctl.add(txtArea);
        ctl.add(txtPostcode);
        ctl.add(txtDateFound.getTextField());
        ctl.add(txtComments);
        ctl.add(btnSave);
        ctl.add(btnDelete);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtDate.getTextField();
    }

    /**
     * Loads the appropriate date from the passed animal object and fills the
     * controls
     */
    public void openForEdit(AnimalLost theanimal) {
        this.animal = theanimal;
        loadData();
    }

    /** Sets the form into creating a new animal record */
    public void openForNew() {
        this.animal = new AnimalLost();
        this.setTitle(i18n("Create_New_Lost_Animal"));
        isNewRecord = true;
        animal.openRecordset("ID = 0");

        try {
            animal.addNew();
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        // Set some default values

        // Date Lost and Reported
        Calendar cal = Calendar.getInstance();

        try {
            txtDate.setText(Utils.formatDate(cal));
            txtReported.setText(Utils.formatDate(cal));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Show ID
        try {
            lblNumber.setText(animal.getID().toString());
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Deactivate media until they have saved
        enableNonAnimalTabs(false);
        enableButtons();
    }

    public void refreshData() {
        if (isNewRecord) return;
        if (formClosing() == false)
            openForEdit(animal);
    }

    /** Notifies the form that the data has been changed. */
    public void dataChanged() {
        isDirty = true;
        btnSave.setEnabled(isDirty);
        setSecurity();
    }

    public void enableButtons() {
        btnMatch.setEnabled(!isNewRecord);
        btnDelete.setEnabled(!isNewRecord);
        btnSave.setEnabled(isDirty);
        setSecurity();
    }

    /** Reads the data in the animal object and fills the boxes */
    public void loadData() {
        try {
            embOwner.loadFromID(animal.getOwnerID().intValue());

            lblNumber.setText(animal.getID().toString());
            this.txtComments.setText(Utils.nullToEmptyString(
                    animal.getComments()));
            this.txtArea.setText(Utils.nullToEmptyString(animal.getAreaLost()));
            this.txtFeatures.setText(Utils.nullToEmptyString(
                    animal.getDistFeat()));
            this.txtPostcode.setText(animal.getAreaPostcode());

            try {
                this.txtDate.setText(Utils.formatDate(animal.getDateLost()));
                this.txtReported.setText(Utils.formatDate(
                        animal.getDateReported()));
                this.txtDateFound.setText(Utils.formatDate(
                        animal.getDateFound()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Now we have to sort out the combo boxes and
            // select the correct item:
            Utils.setComboFromID(LookupCache.getBaseColourLookup(),
                "BaseColour", animal.getBaseColourID(), cboColour);
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                animal.getSpeciesID(), cboSpecies);
            Utils.setComboFromID(LookupCache.getBreedLookup(), "BreedName",
                animal.getBreedID(), cboBreed);
            Utils.setComboFromID(LookupCache.getSexLookup(), "Sex",
                animal.getSex(), cboSex);
            cboAgeGroup.setSelectedItem(animal.getAgeGroup());
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        loadExternal();

        this.isDirty = false;
        enableButtons();
    }

    /** Loads satellite non-animal data and sets various bits of the screen */
    public void loadExternal() {
        try {
            // Created and Changed values
            audit = i18n("created_lastchange",
                    Utils.formatDateTimeLong(animal.getCreatedDate()),
                    animal.getCreatedBy(),
                    Utils.formatDateTimeLong(animal.getLastChangedDate()),
                    animal.getLastChangedBy());

            this.setTitle(i18n("edit_lost_animal_title",
                    animal.getOwner().getOwnerName()));
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Media and Diary
        try {
            media.setLink(Media.LINKTYPE_LOSTANIMAL, animal.getID().intValue());
            media.updateList();

            // Flag the tab if there is content
            if (media.hasData()) {
                tabTabs.setIconAt(1,
                    IconManager.getIcon(IconManager.SCREEN_EDITLOSTANIMAL_MEDIA));
            }

            // Diary
            diary.setLink(animal.getID().intValue(), Diary.LINKTYPE_LOSTANIMAL);
            diary.updateList();

            // Flag the tab if there is content
            if (diary.hasData()) {
                tabTabs.setIconAt(2,
                    IconManager.getIcon(IconManager.SCREEN_EDITLOSTANIMAL_DIARY));
            }

            // Log
            log.setLink(animal.getID().intValue(), Log.LINKTYPE_LOSTANIMAL);
            log.updateList();

            // Flag the tab if there is content
            if (log.hasData()) {
                tabTabs.setIconAt(3,
                    IconManager.getIcon(IconManager.SCREEN_EDITLOSTANIMAL_LOG));
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Enable satellite tabs
        enableNonAnimalTabs(true);
    }

    public void enableNonAnimalTabs(boolean b) {
        tabTabs.setEnabledAt(1, b);
        tabTabs.setEnabledAt(2, b);
        tabTabs.setEnabledAt(3, b);
        setSecurity();
    }

    public boolean saveData() {
        if (!isDirty) {
            return false;
        }

        if (!Global.currentUserObject.getSecChangeLostAnimals()) {
            Dialog.showError(UI.messageNoSavePermission());

            return false;
        }

        try {
            animal.setOwnerID(new Integer(embOwner.getID()));
            animal.setComments(txtComments.getText());
            animal.setAreaLost(txtArea.getText());
            animal.setDistFeat(txtFeatures.getText());
            animal.setAreaPostcode(txtPostcode.getText());

            try {
                animal.setDateLost(Utils.parseDate(txtDate.getText()));
                animal.setDateReported(Utils.parseDate(txtReported.getText()));
                animal.setDateFound(Utils.parseDate(txtDateFound.getText()));
            } catch (ParseException e) {
                Dialog.showError(i18n("A_date_you_entered_was_not_valid:_") +
                    e.getMessage(), i18n("Invalid_Date"));
                Global.logException(e, getClass());
            }

            // Now we have to sort out the combo boxes and
            // select the correct item:
            animal.setSpeciesID(Utils.getIDFromCombo(
                    LookupCache.getSpeciesLookup(), "SpeciesName", cboSpecies));
            animal.setBaseColourID(Utils.getIDFromCombo(
                    LookupCache.getBaseColourLookup(), "BaseColour", cboColour));
            animal.setBreedID(Utils.getIDFromCombo(
                    LookupCache.getBreedLookup(), "BreedName", cboBreed));
            animal.setSex(Utils.getIDFromCombo(LookupCache.getSexLookup(),
                    "Sex", cboSex));
            animal.setAgeGroup(cboAgeGroup.getSelectedItem().toString());
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("Error_saving_to_local_SQLRecordset:_") +
                e.getMessage(), i18n("Save_Error"));
            Global.logException(e, getClass());
        }

        try {
            animal.save(Global.currentUserName);

            if (AuditTrail.enabled()) {
                AuditTrail.updated(isNewRecord, "animallost",
                    animal.getSpeciesName() + " " +
                    animal.getOwner().getOwnerName());
            }

            isNewRecord = false;
            isDirty = false;
            btnSave.setEnabled(isDirty);
            // Allow editing of satellite data if this was a new record
            loadExternal();
            enableButtons();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage(), i18n("Validation_Error"));
        }

        return false;
    }

    /**
     * If Area and Postcode are blank, it defaults them from the owner's record.
     *
     * @param ownerID
     *            The ID of the owner to use
     */
    public void defaultAreaFromOwner(int ownerID) {
        if (!txtArea.getText().equals("")) {
            return;
        }

        try {
            Owner own = new Owner();
            own.openRecordset("ID = " + ownerID);

            if (own.getEOF()) {
                return;
            }

            String add = own.getOwnerAddress();
            String secondline = Utils.formatAddress(add);
            String[] addBits = Utils.split(secondline, ",");

            if (addBits.length >= 2) {
                txtArea.setText(addBits[1].trim());
            }

            txtPostcode.setText(Utils.getAreaPostcode(own.getOwnerPostcode()));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        tabTabs = UI.getTabbedPane();
        embOwner = new OwnerLink();
        media = new MediaSelector();
        diary = new DiarySelector();
        log = new LogSelector();
        tlbTools = UI.getToolBar();

        // Details panel (2 cols)
        // =========================================================
        UI.Panel pnlDetails = UI.getPanel(UI.getGridLayout(2));
        UI.Panel pnlLeft = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlLeftTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlLeftMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlRight = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlRightTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlRightGrid = UI.getPanel(UI.getGridLayout(1));
        UI.Panel pnlRightMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));

        // Left column
        // ======================
        lblNumber = UI.getLabel(UI.ALIGN_LEFT, "");
        lblNumber.setForeground(UI.getColor(255, 0, 0));
        UI.addComponent(pnlLeftTop, i18n("Number:"), lblNumber);

        txtDate = (DateField) UI.addComponent(pnlLeftTop, i18n("Date_Lost:"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        txtReported = (DateField) UI.addComponent(pnlLeftTop,
                i18n("Date_Reported:"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        cboAgeGroup = UI.getCombo(i18n("Age_Group:"),
                LookupCache.getAgeGroupNames(), UI.fp(this, "dataChanged"));
        UI.addComponent(pnlLeftTop, i18n("Age_Group:"), cboAgeGroup);

        cboSex = UI.getCombo(i18n("Sex:"), LookupCache.getSexLookup(), "Sex",
                UI.fp(this, "dataChanged"));
        UI.addComponent(pnlLeftTop, i18n("Sex:"), cboSex);

        cboSpecies = UI.getCombo(i18n("Species:"),
                LookupCache.getSpeciesLookup(), "SpeciesName",
                UI.fp(this, "dataChanged"));
        UI.addComponent(pnlLeftTop, i18n("Species:"), cboSpecies);

        cboBreed = UI.getCombo(i18n("Breed:"), LookupCache.getBreedLookup(),
                "BreedName", UI.fp(this, "dataChanged"));
        UI.addComponent(pnlLeftTop, i18n("Breed:"), cboBreed);

        cboColour = UI.getCombo(i18n("Colour:"),
                LookupCache.getBaseColourLookup(), "BaseColour",
                UI.fp(this, "dataChanged"));
        UI.addComponent(pnlLeftTop, i18n("Colour:"), cboColour);

        txtFeatures = (UI.TextArea) UI.addComponent(pnlLeftMid,
                i18n("Features:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        txtArea = (UI.TextArea) UI.addComponent(pnlLeftMid, i18n("Area_Lost:"),
                UI.getTextArea(i18n("The_area_the_animal_was_lost_in,_eg:_Millhouses,_Sheffield"),
                    UI.fp(this, "dataChanged")));

        pnlLeft.add(pnlLeftTop, UI.BorderLayout.NORTH);
        pnlLeft.add(pnlLeftMid, UI.BorderLayout.CENTER);
        pnlDetails.add(pnlLeft);

        // Right column
        // =========================
        txtPostcode = (UI.TextField) UI.addComponent(pnlRightTop,
                i18n("Postcode:"),
                UI.getTextField(i18n("The_postcode_of_the_area_the_animal_was_lost_in"),
                    UI.fp(this, "dataChanged")));

        txtDateFound = (DateField) UI.addComponent(pnlRightTop,
                i18n("Date_Found:"),
                UI.getDateField(i18n("The_date_this_animal_was_returned_to_its_owner"),
                    UI.fp(this, "dataChanged")));

        txtComments = (UI.TextArea) UI.addComponent(pnlRightMid,
                i18n("Comments:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));
        UI.addComponent(pnlRightGrid, pnlRightMid);

        embOwner.setTitle(i18n("Contact_Details:"));
        embOwner.setParent(this);
        UI.addComponent(pnlRightGrid, embOwner);

        pnlRight.add(pnlRightTop, UI.BorderLayout.NORTH);
        pnlRight.add(pnlRightGrid, UI.BorderLayout.CENTER);
        pnlDetails.add(pnlRight);

        // Tabbed pane
        // =============================================
        tabTabs.addTab(i18n("Details"), pnlDetails);
        tabTabs.addTab(i18n("Media"), media);
        tabTabs.addTab(i18n("Diary"), diary);
        tabTabs.addTab(i18n("Log"), log);
        add(tabTabs, UI.BorderLayout.CENTER);

        // Toolbar
        // ==============================================
        btnSave = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Save_this_record"), 's',
                    IconManager.getIcon(IconManager.SCREEN_EDITLOSTANIMAL_SAVE),
                    UI.fp(this, "saveData")));

        btnDelete = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Delete_this_record"), 'z',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITLOSTANIMAL_DELETE),
                    UI.fp(this, "actionDelete")));

        btnMatch = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Attempt_to_find_possible_matches_for_this_lost_animal"),
                    'm',
                    IconManager.getIcon(IconManager.SCREEN_EDITLOSTANIMAL_MATCH),
                    UI.fp(this, "actionMatch")));

        add(tlbTools, UI.BorderLayout.NORTH);
    }

    public boolean formClosing() {
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

    public void actionMatch() {
        try {
            new LostFoundMatch(0, animal.getID().intValue());
        } catch (Exception e) {
        }
    }

    public void actionDelete() {
        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            // Remove it from the database, along with any associated Media
            try {
                String s = "DELETE FROM media WHERE LinkID = " +
                    animal.getID() + " AND LinkTypeID = " +
                    Integer.toString(Media.LINKTYPE_LOSTANIMAL);
                DBConnection.executeAction(s);
                s = "DELETE FROM animallost WHERE ID = " + animal.getID();
                DBConnection.executeAction(s);

                if (AuditTrail.enabled()) {
                    AuditTrail.deleted("animallost",
                        animal.getSpeciesName() + " " +
                        animal.getOwner().getOwnerName());
                }

                dispose();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void ownerChanged(int ownerid, String id) {
        dataChanged();
        defaultAreaFromOwner(ownerid);
    }
}
