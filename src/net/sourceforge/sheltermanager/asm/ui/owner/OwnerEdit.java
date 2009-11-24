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

import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AdditionalFieldView;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.diary.DiarySelector;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryTaskExecute;
import net.sourceforge.sheltermanager.asm.ui.log.LogSelector;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CustomUI;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Email;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.OwnerDocument;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;
import net.sourceforge.sheltermanager.dbfs.DBFSException;

import java.io.File;

import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for editing owner records.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class OwnerEdit extends ASMForm implements SearchListener,
    OwnerLinkListener {
    private Owner owner = null;
    public MediaSelector media = null;
    private MovementSelector movement = null;
    private DonationSelector ownerdonations = null;
    private LinkSelector ownerlinks = null;
    private VoucherSelector ownervouchers = null;
    private DiarySelector diary = null;
    private LogSelector log = null;
    private AdditionalFieldView additional = null;
    private boolean isNewRecord = false;
    private boolean hasBeenSaved = false;
    private OwnerLink embeddedParent = null;
    private boolean isDirty = false;
    private UI.Button btnDelete;
    private UI.Button btnDiaryTask;
    private UI.Button btnDoc;
    private UI.Button btnEmail;
    private UI.Button btnMerge;
    private UI.Button btnSave;
    private UI.CheckBox chkBanned;
    private UI.CheckBox chkHomeCheck;
    private UI.CheckBox chkHomeChecker;
    private UI.CheckBox chkIsACO;
    private UI.CheckBox chkIsDonor;
    private UI.CheckBox chkIsFosterer;
    private UI.CheckBox chkIsMember;
    private UI.CheckBox chkIsRetailer;
    private UI.CheckBox chkIsShelter;
    private UI.CheckBox chkIsStaff;
    private UI.CheckBox chkIsVet;
    private UI.CheckBox chkVolunteer;
    private UI.TabbedPane tabTabs;
    private UI.ToolBar tlbTools;
    private UI.TextArea txtAddress;
    private UI.TextArea txtAudit;
    private UI.TextArea txtComments;
    private UI.ComboBox cboCounty;
    private UI.TextField txtEmail;
    private UI.TextArea txtHomeCheckArea;
    private UI.TextField txtHomeTelephone;
    private UI.TextField txtMobileTelephone;
    private DateField txtMembershipExpiryDate;
    private DateField txtLastHadHomecheckDate;
    private DateField txtMatchAdded;
    private DateField txtMatchExpires;
    private OwnerLink embHomeCheckedBy;
    private UI.TextField txtPostcode;
    private UI.ComboBox cboTown;
    private UI.TextField txtWorkTelephone;
    private UI.CheckBox chkCheckNames;
    private UI.CheckBox chkCheckAddresses;
    private UI.TextField txtNameTitle;
    private UI.TextField txtNameInitials;
    private UI.TextField txtNameForenames;
    private UI.TextField txtNameSurname;
    private UI.CheckBox chkMatchActive;
    private UI.ComboBox cboMatchSex;
    private UI.ComboBox cboMatchSize;
    private UI.TextField txtMatchAgeFrom;
    private UI.TextField txtMatchAgeTo;
    private UI.ComboBox cboMatchAnimalType;
    private UI.ComboBox cboMatchSpecies;
    private UI.ComboBox cboMatchBreed;
    private UI.ComboBox cboMatchBreed2;
    private UI.CheckBox chkMatchGoodWithCats;
    private UI.CheckBox chkMatchGoodWithDogs;
    private UI.CheckBox chkMatchGoodWithChildren;
    private UI.CheckBox chkMatchHouseTrained;
    private UI.TextArea txtMatchCommentsContain;
    private UI.Panel pnlTop;
    private UI.Label lblThumbnail;
    private UI.Table tblAreas;
    private UI.Table tblCheckHistory;

    /** Whether or not we've added any custom buttons */
    private boolean addedCustomButtons = false;

    /** Audit info */
    private String audit = null;

    /** Creates new form EditOwner */
    public OwnerEdit() {
        init(Global.i18n("uiowner", "Edit_Owner"),
            IconManager.getIcon(IconManager.SCREEN_EDITOWNER), "uiowner");
    }

    public void dispose() {
        try {
            owner.free();
        } catch (Exception e) {
        }

        try {
            media.dispose();
        } catch (Exception e) {
        }

        try {
            movement.dispose();
        } catch (Exception e) {
        }

        try {
            diary.dispose();
        } catch (Exception e) {
        }

        try {
            ownerdonations.dispose();
        } catch (Exception e) {
        }

        try {
            ownervouchers.dispose();
        } catch (Exception e) {
        }

        try {
            ownerlinks.dispose();
        } catch (Exception e) {
        }

        try {
            log.dispose();
        } catch (Exception e) {
        }

        try {
            additional.dispose();
        } catch (Exception e) {
        }

        owner = null;
        media = null;
        diary = null;
        movement = null;
        ownerdonations = null;
        ownervouchers = null;
        ownerlinks = null;
        embeddedParent = null;
        log = null;
        additional = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtNameTitle);
        ctl.add(txtNameInitials);
        ctl.add(txtNameForenames);
        ctl.add(txtNameSurname);
        ctl.add(txtAddress);

        if (!Configuration.getBoolean("HideTownCounty")) {
            ctl.add(cboTown);
            ctl.add(cboCounty);
        }

        ctl.add(txtPostcode);
        ctl.add(txtHomeTelephone);
        ctl.add(txtMobileTelephone);
        ctl.add(txtWorkTelephone);
        ctl.add(txtEmail);
        ctl.add(chkIsMember);
        ctl.add(txtMembershipExpiryDate.getTextField());
        ctl.add(chkBanned);
        ctl.add(chkIsDonor);
        ctl.add(chkVolunteer);
        ctl.add(chkIsShelter);
        ctl.add(chkIsACO);
        ctl.add(chkIsStaff);
        ctl.add(chkIsRetailer);
        ctl.add(chkIsFosterer);
        ctl.add(chkIsVet);
        ctl.add(chkHomeChecker);
        ctl.add(chkHomeCheck);
        ctl.add(txtLastHadHomecheckDate.getTextField());
        ctl.add(txtComments);
        ctl.add(txtHomeCheckArea);
        ctl.add(txtMatchAdded);
        ctl.add(txtMatchExpires);
        ctl.add(chkMatchActive);
        ctl.add(cboMatchSex);
        ctl.add(cboMatchSize);
        ctl.add(txtMatchAgeFrom);
        ctl.add(txtMatchAgeTo);
        ctl.add(cboMatchAnimalType);
        ctl.add(cboMatchSpecies);
        ctl.add(cboMatchBreed);
        ctl.add(cboMatchBreed2);
        ctl.add(chkMatchGoodWithCats);
        ctl.add(chkMatchGoodWithDogs);
        ctl.add(chkMatchGoodWithChildren);
        ctl.add(chkMatchHouseTrained);
        ctl.add(txtMatchCommentsContain);
        ctl.add(btnSave);
        ctl.add(btnDelete);
        ctl.add(btnDoc);

        ctl.addAll(additional.getAdditionalComponents());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtNameTitle;
    }

    public boolean needsScroll() {
        return true;
    }

    public int getScrollHeight() {
        return 600;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnDoc.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteOwner()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecMergeOwner()) {
            btnMerge.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnDiaryTask.setEnabled(false);
        }

        // Tabs/View
        if (!Global.currentUserObject.getSecViewOwnerDonation()) {
            tabTabs.setEnabledAt(3, false);
        }

        if (!Global.currentUserObject.getSecViewOwnerVoucher()) {
            tabTabs.setEnabledAt(4, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalMedia()) {
            tabTabs.setEnabledAt(5, false);
        }

        if (!Global.currentUserObject.getSecViewDiaryNotes()) {
            tabTabs.setEnabledAt(6, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalMovements()) {
            tabTabs.setEnabledAt(7, false);
        }

        if (!Global.currentUserObject.getSecViewOwnerLinks()) {
            tabTabs.setEnabledAt(8, false);
        }

        if (!Global.currentUserObject.getSecViewLogEntry()) {
            tabTabs.setEnabledAt(9, false);
        }

        // If this isn't an addition of a new owner, make sure they aren't
        // allowed to change the details
        if (!isNewRecord) {
            if (!Global.currentUserObject.getSecChangeOwner()) {
                btnSave.setEnabled(false);
            }
        }
    }

    public boolean formClosing() {
        // Don't destroy if the user has unsaved changes and are not sure
        if (this.isDirty) {
            if (!Dialog.showYesNoWarning(i18n("You_have_unsaved_changes_-_are_you_sure_you_wish_to_close?"),
                        i18n("Unsaved_Changes"))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Opens the screen for a new record and updates an embedded owner record
     * with each save.
     */
    public void openForNew(OwnerLink embeddedParent) {
        this.embeddedParent = embeddedParent;
        openForNew();
    }

    /** Opens the screen for a new record. */
    public void openForNew() {
        this.setTitle(i18n("Create_New_Owner"));

        try {
            // Additional components for turning on/off name and address
            // checking --
            // only show them if the option is on for checking (this is so users
            // can turn them off ad-hoc for some owners)
            if (Configuration.getBoolean("OwnerNameCheck")) {
                chkCheckNames = UI.getCheckBox(i18n("check_names"),
                        i18n("check_for_owners_with_similar_names"));
                chkCheckNames.setSelected(true);
                pnlTop.add(chkCheckNames);
            }

            if (Configuration.getBoolean("OwnerAddressCheck")) {
                chkCheckAddresses = UI.getCheckBox(i18n("check_addresses"),
                        i18n("check_for_owners_with_similar_addresses"));
                chkCheckAddresses.setSelected(true);
                pnlTop.add(chkCheckAddresses);
            }

            owner = new Owner();
            owner.openRecordset("ID = 0");
            owner.addNew();
            setNonOwnerEnabled(false);
            isNewRecord = true;
            this.isDirty = true;

            // Set initial values
            txtMatchAgeFrom.setText("0");
            txtMatchAgeTo.setText("0");

            // Default the most common town/county for convenience
            if (!Configuration.getBoolean("HideTownCounty")) {
                cboTown.setSelectedItem(LookupCache.getMostCommonTown());
                cboCounty.setSelectedItem(LookupCache.getMostCommonCounty());
            }

            btnSave.setEnabled(isDirty);

            enableButtons();
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_creating_the_owner_record:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Edit mode from an embedded control. Saves update the component.
     */
    public void openForEdit(int ownerid, OwnerLink embeddedParent) {
        this.embeddedParent = embeddedParent;
        openForEdit(ownerid);
    }

    /**
     * Opens the screen and puts it into editing mode on the given owner from
     * it's ID.
     *
     * @param ownerid
     *            The ID of the owner to edit
     */
    public void openForEdit(int ownerid) {
        try {
            owner = new Owner();
            owner.openRecordset("ID = " + ownerid);
            loadData();
        } catch (Exception e) {
        }
    }

    public void openForEdit(Owner o) {
        owner = o;
        loadData();
    }

    public void showTitle() {
        try {
            this.setTitle(i18n("edit_owner_title", owner.getOwnerName()));
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    public void loadData() {
        showTitle();

        // Load the owner's information in
        try {
            if (!addedCustomButtons) {
                CustomUI.addCustomOwnerButtons(tlbTools,
                    owner.getID().intValue());
                addedCustomButtons = true;
            }

            txtAddress.setText(Utils.nullToEmptyString(owner.getOwnerAddress()));

            Integer y = new Integer(1);
            chkBanned.setSelected(owner.getIsBanned().equals(y));
            chkHomeCheck.setSelected(owner.getIDCheck().equals(y));
            chkVolunteer.setSelected(owner.getIsVolunteer().equals(y));
            chkIsACO.setSelected(owner.getIsACO().equals(y));
            chkIsShelter.setSelected(owner.getIsShelter().equals(y));
            chkIsStaff.setSelected(owner.getIsStaff().equals(y));
            chkIsVet.setSelected(owner.getIsVet().equals(y));

            try {
                txtLastHadHomecheckDate.setText(Utils.formatDate(
                        owner.getDateLastHomeChecked()));
            } catch (Exception e) {
            }

            embHomeCheckedBy.setID(owner.getHomeCheckedBy());

            chkIsMember.setSelected(owner.getIsMember().equals(y));

            try {
                txtMembershipExpiryDate.setText(Utils.formatDate(
                        owner.getMembershipExpiryDate()));
            } catch (Exception e) {
            }

            txtWorkTelephone.setText(Utils.nullToEmptyString(
                    owner.getWorkTelephone()));
            txtMobileTelephone.setText(Utils.nullToEmptyString(
                    owner.getMobileTelephone()));
            txtHomeTelephone.setText(Utils.nullToEmptyString(
                    owner.getHomeTelephone()));
            txtEmail.setText(Utils.nullToEmptyString(owner.getEmailAddress()));
            txtPostcode.setText(Utils.nullToEmptyString(
                    owner.getOwnerPostcode()));
            cboTown.setSelectedItem(Utils.nullToEmptyString(
                    owner.getOwnerTown()));
            cboCounty.setSelectedItem(Utils.nullToEmptyString(
                    owner.getOwnerCounty()));
            txtNameTitle.setText(owner.getOwnerTitle());
            txtNameInitials.setText(owner.getOwnerInitials());
            txtNameForenames.setText(owner.getOwnerForenames());
            txtNameSurname.setText(owner.getOwnerSurname());
            txtComments.setText(Utils.nullToEmptyString(owner.getComments()));
            chkHomeChecker.setSelected(owner.getIsHomeChecker().equals(y));
            chkIsDonor.setSelected(owner.getIsDonor().equals(y));
            chkIsRetailer.setSelected(owner.getIsRetailer().equals(y));
            chkIsFosterer.setSelected(owner.getIsFosterer().equals(y));

            try {
                txtMatchAdded.setText(Utils.formatDate(owner.getMatchAdded()));
                txtMatchExpires.setText(Utils.formatDate(
                        owner.getMatchExpires()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            chkMatchActive.setSelected(owner.getMatchActive().equals(y));
            cboMatchSex.setSelectedIndex(owner.getMatchSex().intValue());
            cboMatchSize.setSelectedIndex(owner.getMatchSize().intValue());
            txtMatchAgeFrom.setText(Utils.nullToEmptyString(
                    owner.getMatchAgeFrom().toString()));
            txtMatchAgeTo.setText(Utils.nullToEmptyString(
                    owner.getMatchAgeTo().toString()));
            Utils.setComboFromID(LookupCache.getAnimalTypeLookup(),
                "AnimalType", owner.getMatchAnimalType(), cboMatchAnimalType);
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                owner.getMatchSpecies(), cboMatchSpecies);
            Utils.setComboFromID(LookupCache.getBreedLookup(), "BreedName",
                owner.getMatchBreed(), cboMatchBreed);
            Utils.setComboFromID(LookupCache.getBreedLookup(), "BreedName",
                owner.getMatchBreed2(), cboMatchBreed2);

            chkMatchGoodWithCats.setSelected(owner.getMatchGoodWithCats()
                                                  .equals(y));
            chkMatchGoodWithDogs.setSelected(owner.getMatchGoodWithDogs()
                                                  .equals(y));
            chkMatchGoodWithChildren.setSelected(owner.getMatchGoodWithChildren()
                                                      .equals(y));
            chkMatchHouseTrained.setSelected(owner.getMatchHouseTrained()
                                                  .equals(y));
            txtMatchCommentsContain.setText(Utils.nullToEmptyString(
                    owner.getMatchCommentsContain()));

            loadHomecheckerAreas();
            loadHomecheckerHistory();
            loadExternal();
            actionHomecheckerChanged();

            this.isDirty = false;
            btnSave.setEnabled(isDirty);

            showThumbnail();
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("An_error_occurred_reading_owner_information:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void loadHomecheckerAreas() {
        String s = "";

        try {
            s = Utils.nullToEmptyString(owner.getHomeCheckAreas()).trim();
            s = Utils.replace(s, "\n", " ");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        String[] areas = Utils.split(s, " ");

        String[] colhead = { i18n("Area") };
        String[][] data = new String[areas.length][1];

        for (int i = 0; i < areas.length; i++)
            data[i][0] = areas[i];

        tblAreas.setTableData(colhead, data, areas.length, 0);
    }

    public void loadHomecheckerHistory() {
        try {
            SQLRecordset r = new SQLRecordset();
            r.openRecordset(
                "SELECT ID, OwnerName, Comments, DateLastHomeChecked FROM owner WHERE HomeCheckedBy = " +
                owner.getID() + " ORDER BY DateLastHomeChecked DESC", "owner");

            String[] cols = { i18n("Date"), i18n("Owner"), i18n("Comments") };
            String[][] data = new String[(int) r.getRecordCount()][4];

            int i = 0;

            while (!r.getEOF()) {
                data[i][0] = Utils.formatTableDate((Date) r.getField(
                            "DateLastHomeChecked"));
                data[i][1] = r.getField("OwnerName").toString();
                data[i][2] = Utils.nullToEmptyString((String) r.getField(
                            "Comments"));
                data[i][3] = r.getField("ID").toString();
                i++;
                r.moveNext();
            }

            tblCheckHistory.setTableData(cols, data, i, 4, 3);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** Loads satellite data */
    public void loadExternal() {
        try {
            // Audit info
            audit = i18n("created_lastchange",
                    Utils.formatDateTimeLong(owner.getCreatedDate()),
                    owner.getCreatedBy(),
                    Utils.formatDateTimeLong(owner.getLastChangedDate()),
                    owner.getLastChangedBy());

            // Donations
            ownerdonations.setLink(owner.getID().intValue(), 0);
            ownerdonations.updateList();

            // Flag the tab if there is content
            if (ownerdonations.hasData()) {
                tabTabs.setIconAt(3,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_DONATIONS));
            }

            // Vouchers
            ownervouchers.setLink(owner.getID().intValue(), 0);
            ownervouchers.updateList();

            // Flag the tab if there is content
            if (ownervouchers.hasData()) {
                tabTabs.setIconAt(4,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_VOUCHERS));
            }

            // Media
            media.setLink(Media.LINKTYPE_OWNER, owner.getID().intValue());
            media.updateList();

            // Flag the tab if there is content
            if (media.hasData()) {
                tabTabs.setIconAt(5,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_MEDIA));
            }

            // Diary
            diary.setLink(owner.getID().intValue(), Diary.LINKTYPE_OWNER);
            diary.updateList();

            // Flag the tab if there is content
            if (diary.hasData()) {
                tabTabs.setIconAt(6,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_DIARY));
            }

            // Movements
            movement.setLink(owner.getID().intValue(), 0);
            movement.updateList();

            // Flag the tab if there is content
            if (movement.hasData()) {
                tabTabs.setIconAt(7,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_MOVEMENT));
            }

            // Links
            ownerlinks.setLink(owner.getID().intValue(), 0);
            ownerlinks.updateList();

            // Flag the tab if there is content
            if (ownerlinks.hasData()) {
                tabTabs.setIconAt(8,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_LINKS));
            }

            // Log
            log.setLink(owner.getID().intValue(), Log.LINKTYPE_OWNER);
            log.updateList();

            // Flag the tab if there is content
            if (log.hasData()) {
                tabTabs.setIconAt(9,
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_LOG));
            }

            // Additional
            if (additional.hasFields()) {
                additional.loadFields(owner.getID().intValue(),
                    AdditionalField.LINKTYPE_OWNER);
            }

            setNonOwnerEnabled(true);
            enableButtons();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void enableButtons() {
        btnDelete.setEnabled(!isNewRecord);
        btnDiaryTask.setEnabled(!isNewRecord);
        btnDoc.setEnabled(!isNewRecord);
        btnMerge.setEnabled(!isNewRecord);
        setSecurity();
    }

    /**
     * Retrieves the first available photo media record and uses it to draw a
     * thumbnail image in the bottom left corner of the details tab.
     *
     */
    public void showThumbnail() {
        if (Configuration.getBoolean("NoMediaThumbnails")) {
            return;
        }

        new Thread() {
                public void run() {
                    showThumbnailImpl();
                }
            }.start();
    }

    private void showThumbnailImpl() {
        try {
            // Find the media for this owner and show it if there is one
            if (owner.hasImage()) {
                String imagename = owner.getThumbnailImage();

                // Grab the remote file and copy it to the local temp directory
                String tempdir = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                    File.separator;

                try {
                    DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_OWNER,
                            owner.getID().intValue());

                    try {
                        dbfs.readFile(imagename, tempdir + imagename);
                    } catch (DBFSException e) {
                        // File already exists - who cares?
                    }
                } catch (Exception e) {
                    Global.logError("Error occurred retrieving image: " +
                        e.getMessage(), "EditAnimal.showThumbnailImpl");

                    return;
                }

                // Now display it in the thumbnailer
                try {
                    lblThumbnail.setIcon(IconManager.getThumbnail(tempdir +
                            imagename, 100, 50));
                    lblThumbnail.repaint();

                    // Delete the temporary file
                    Utils.deleteTemporaryFile(imagename);
                } catch (Exception e) {
                    Global.logError("Error occurred displaying thumbnail: " +
                        e.getMessage(), "EditOwner.showThumbnailImpl");

                    return;
                }
            } else {
                // no image
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    public void ownerChanged(int ownerid, String id) {
        dataChanged();
    }

    /** Allows external calls to force a reload of the owner's diary */
    public void updateDiary() {
        diary.updateList();

        // Flag the tab if there is content
        if (diary.hasData()) {
            tabTabs.setIconAt(5,
                IconManager.getIcon(IconManager.SCREEN_EDITOWNER_DIARY));
        }
    }

    /**
     * Deactivates/Actives non owner tabs
     *
     * @param b
     *            true to activate, false to deactivate.
     */
    public void setNonOwnerEnabled(boolean b) {
        tabTabs.setEnabledAt(3, b);
        tabTabs.setEnabledAt(4, b);
        tabTabs.setEnabledAt(5, b);
        tabTabs.setEnabledAt(6, b);
        tabTabs.setEnabledAt(7, b);
        tabTabs.setEnabledAt(8, b);
        tabTabs.setEnabledAt(9, b);

        if (additional.hasFields()) {
            tabTabs.setEnabledAt(10, b);
        }
    }

    /**
     * Saves the controls back to the record and attempts to submit it to the
     * database.
     */
    public boolean saveData() {

        if (!Global.currentUserObject.getSecChangeOwner()) {
            Dialog.showError(UI.messageNoSavePermission());
            return false;
        }

        try {
            owner.setOwnerName(txtNameTitle.getText(),
                txtNameInitials.getText(), txtNameForenames.getText(),
                txtNameSurname.getText());
            owner.setOwnerAddress(txtAddress.getText());
            owner.setOwnerTown(cboTown.getSelectedItem().toString());
            owner.setOwnerCounty(cboCounty.getSelectedItem().toString());
            owner.setOwnerPostcode(txtPostcode.getText());

            try {
                owner.getRecordset()
                     .setField("OwnerTitle", txtNameTitle.getText());
                owner.getRecordset()
                     .setField("OwnerInitials", txtNameInitials.getText());
                owner.getRecordset()
                     .setField("OwnerForenames", txtNameForenames.getText());
                owner.getRecordset()
                     .setField("OwnerSurname", txtNameSurname.getText());
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            Integer y = new Integer(1);
            Integer n = new Integer(0);

            owner.setIsBanned(chkBanned.isSelected() ? y : n);
            owner.setIDCheck(chkHomeCheck.isSelected() ? y : n);

            try {
                owner.setDateLastHomeChecked(Utils.parseDate(
                        txtLastHadHomecheckDate.getText()));
                owner.setMatchAdded(Utils.parseDate(txtMatchAdded.getText()));
                owner.setMatchExpires(Utils.parseDate(txtMatchExpires.getText()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            owner.setHomeCheckedBy(new Integer(embHomeCheckedBy.getID()));
            owner.setIsVolunteer(chkVolunteer.isSelected() ? y : n);
            owner.setIsHomeChecker(chkHomeChecker.isSelected() ? y : n);
            owner.setIsMember(chkIsMember.isSelected() ? y : n);
            owner.setIsVet(chkIsVet.isSelected() ? y : n);

            try {
                owner.setMembershipExpiryDate(Utils.parseDate(
                        txtMembershipExpiryDate.getText()));
            } catch (Exception e) {
            }

            owner.setIsDonor(chkIsDonor.isSelected() ? y : n);
            owner.setIsShelter(chkIsShelter.isSelected() ? y : n);
            owner.setIsACO(chkIsACO.isSelected() ? y : n);
            owner.setIsStaff(chkIsStaff.isSelected() ? y : n);
            owner.setIsRetailer(chkIsRetailer.isSelected() ? y : n);
            owner.setIsFosterer(chkIsFosterer.isSelected() ? y : n);

            owner.setWorkTelephone(txtWorkTelephone.getText());
            owner.setMobileTelephone(txtMobileTelephone.getText());
            owner.setHomeTelephone(txtHomeTelephone.getText());
            owner.setEmailAddress(txtEmail.getText());
            owner.setComments(txtComments.getText());

            owner.setMatchActive(chkMatchActive.isSelected() ? y : n);
            owner.setMatchSex(new Integer(cboMatchSex.getSelectedIndex()));
            owner.setMatchSize(new Integer(cboMatchSize.getSelectedIndex()));
            owner.setMatchAgeFrom(new Double(txtMatchAgeFrom.getText()));
            owner.setMatchAgeTo(new Double(txtMatchAgeTo.getText()));
            owner.setMatchAnimalType(Utils.getIDFromCombo(
                    LookupCache.getAnimalTypeLookup(), "AnimalType",
                    cboMatchAnimalType));
            owner.setMatchSpecies(Utils.getIDFromCombo(
                    LookupCache.getSpeciesLookup(), "SpeciesName",
                    cboMatchSpecies));
            owner.setMatchBreed(Utils.getIDFromCombo(
                    LookupCache.getBreedLookup(), "BreedName", cboMatchBreed));
            owner.setMatchBreed2(Utils.getIDFromCombo(
                    LookupCache.getBreedLookup(), "BreedName", cboMatchBreed2));
            owner.setMatchGoodWithCats(chkMatchGoodWithCats.isSelected() ? y : n);
            owner.setMatchGoodWithDogs(chkMatchGoodWithDogs.isSelected() ? y : n);
            owner.setMatchGoodWithChildren(chkMatchGoodWithChildren.isSelected()
                ? y : n);
            owner.setMatchHouseTrained(chkMatchHouseTrained.isSelected() ? y : n);
            owner.setMatchCommentsContain(txtMatchCommentsContain.getText());

            try {
                owner.save(Global.currentUserName);

                // If we're not a new record, save the additional fields
                if (!isNewRecord) {
                    additional.saveFields(owner.getID().intValue(),
                        AdditionalField.LINKTYPE_OWNER);
                }

                loadExternal();
                hasBeenSaved = true;
                isNewRecord = false;
                this.isDirty = false;
                btnSave.setEnabled(isDirty);

                // If an embedded parent is watching this,
                // update it
                if (embeddedParent != null) {
                    embeddedParent.receiveData(owner);
                }

                // Update title
                showTitle();
                enableButtons();

                return true;
            } catch (CursorEngineException e) {
                Dialog.showError(i18n("An_error_occurred_saving_the_record_to_the_database:_") +
                    e.getMessage());
                Global.logException(e, getClass());
            }
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("An_error_occurred_reading_owner_information:_") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    /**
     * Checks to see if this owner is new and if it is, whether there are any
     * similar ones on file. It also optionally checks the original owner fields
     * of the animal database to see if there could be a match there.
     *
     * @param onName
     *            true if we are checking on the name, or false for the address.
     */
    public void checkOwner(boolean onName) {
        // Abandon if the checkbox is available and unticked
        if (onName) {
            if (chkCheckNames != null) {
                if (!chkCheckNames.isSelected()) {
                    return;
                }
            }
        }

        if (!onName) {
            if (chkCheckAddresses != null) {
                if (!chkCheckAddresses.isSelected()) {
                    return;
                }
            }
        }

        // Checking on name or address? Abandon if
        // we don't have the relevant entry
        if (!hasBeenSaved && isNewRecord && onName) {
            if (txtNameSurname.getText().trim().equals("")) {
                return; // IJPE ToDo
            }
        }

        if (!hasBeenSaved && isNewRecord && !onName) {
            if (txtAddress.getText().trim().equals("")) {
                return;
            }
        }

        // Abandon if option is switched off for name or address
        try {
            if (onName && !Configuration.getBoolean("OwnerNameCheck")) {
                return;
            }

            if (!onName && !Configuration.getBoolean("OwnerAddressCheck")) {
                return;
            }
        } catch (Exception e) {
            Dialog.showError(i18n("Error_reading_configuration:\n") +
                e.getMessage());
            Global.logException(e, getClass());

            return;
        }

        try {
            // Checking on Name --------------------------------
            if (onName) {
                // Grab a list of possible owner matches
                Owner test = new Owner();
                test.openRecordset("UPPER(OwnerName) Like '%" +
                    txtNameTitle.getText().toUpperCase().replace('\'', '`') +
                    "%' AND UPPER(OwnerName) Like '%" +
                    txtNameSurname.getText().toUpperCase().replace('\'', '`') +
                    "%'");

                // If we got some, display them
                if (!test.getEOF()) {
                    OwnerCheck co = new OwnerCheck(test, this);
                    Global.mainForm.addChild(co);
                    co = null;
                }

                test = null;

                // Check to see if this owner brought one in the animal
                // database to see if there is anything there so we can
                // warn the user if they are about to rehome an animal
                // to someone who has brought an animal in.
                if (Configuration.getBoolean("WarnBroughtIn")) {
                    SQLRecordset testanimal = new SQLRecordset();
                    testanimal.openRecordset(
                        "SELECT animal.ID, animal.AnimalName, animal.ShelterCode FROM animal INNER JOIN owner ON " +
                        "animal.OriginalOwnerID = owner.ID WHERE owner.OwnerName Like '%" +
                        txtNameSurname.getText().replace('\'', '`') + "%'",
                        "animal");

                    // txtName.getText() + "%'", "animal"); // orig.; IJPE
                    if (!testanimal.getEOF()) {
                        if (Dialog.showYesNoWarning(i18n("warning_name_animalbroughtin",
                                        testanimal.getField("ShelterCode")
                                                      .toString(),
                                        testanimal.getField("AnimalName")
                                                      .toString()),
                                    i18n("Suspect_Owner"))) {
                            Integer id = (Integer) testanimal.getField("ID");
                            Animal na = LookupCache.getAnimalByID(id);
                            AnimalEdit ea = new AnimalEdit();
                            ea.openForEdit(na);
                            Global.mainForm.addChild(ea);
                            ea = null;
                            na = null;
                        }
                    }

                    try {
                        testanimal.free();
                    } catch (Exception e) {
                    }

                    testanimal = null;
                }
            } else // Checking on address ------------------------------------------
             {
                // Get the first line of the address - upto a comma
                // or linebreak. If neither is found, the whole thing
                // is used as it must be just one line.
                String add = txtAddress.getText();
                int i = add.indexOf(",");

                if (i == -1) {
                    i = add.indexOf("\n");
                }

                if (i != -1) {
                    add = add.substring(0, i);
                }

                // Grab a list of possible owner matches
                Owner test = new Owner();
                test.openRecordset("UPPER(OwnerAddress) Like '%" +
                    add.toUpperCase().replace('\'', '`') + "%'");

                // If we got some, display them
                if (!test.getEOF()) {
                    OwnerCheck co = new OwnerCheck(test, this);
                    Global.mainForm.addChild(co);
                    co = null;
                }

                test = null;

                // Check the list of Original Owner Names in the animal
                // database to see if there is anything there so we can
                // warn the user if they are about to rehome an animal
                // to someone who has brought an animal in.
                if (Configuration.getBoolean("WarnBroughtIn")) {
                    SQLRecordset testanimal = new SQLRecordset();
                    testanimal.openRecordset("SELECT animal.* FROM animal " +
                        "INNER JOIN owner ON animal.OriginalOwnerID = owner.ID " +
                        "WHERE OwnerAddress Like '%" + add.replace('\'', '`') +
                        "%'", "animal");

                    if (!testanimal.getEOF()) {
                        if (Dialog.showYesNoWarning(i18n("WARNING:_This_owner_has_the_same_address_as_a_person_that_brought_an_animal_in_\n(Animal_") +
                                    testanimal.getField("ShelterCode") + " - " +
                                    testanimal.getField("AnimalName") +
                                    i18n(")_-_would_you_like_to_view_the_animal_record?"),
                                    i18n("Suspect_Owner"))) {
                            Integer id = (Integer) testanimal.getField("ID");
                            Animal ta = LookupCache.getAnimalByID(id);
                            AnimalEdit ea = new AnimalEdit();
                            ea.openForEdit(ta);
                            Global.mainForm.addChild(ea);
                            ea = null;
                        }
                    }

                    testanimal.free();
                    testanimal = null;
                }
            }
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_checking_the_owner:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /** Notifies the form that the data has been changed. */
    public void dataChanged() {
        isDirty = true;
        btnSave.setEnabled(isDirty);
        setSecurity();
    }

    public void initComponents() {
        media = new MediaSelector();
        movement = new MovementSelector();
        ownerdonations = new DonationSelector(this);
        ownerlinks = new LinkSelector();
        ownervouchers = new VoucherSelector();
        diary = new DiarySelector();
        log = new LogSelector();
        additional = new AdditionalFieldView(AdditionalField.LINKTYPE_OWNER,
                UI.fp(this, "dataChanged"));
        tabTabs = UI.getTabbedPane();

        UI.Panel pnlDetails = UI.getPanel(UI.getGridLayout(2));

        // Left pane ======================================
        UI.Panel pnlLeft = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlLeftTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 35, 65 }));
        UI.Panel pnlLeftMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 35, 65 }));
        UI.Panel pnlLeftBot = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 35, 65 }));

        txtNameTitle = (UI.TextField) UI.addComponent(pnlLeftTop,
                i18n("NameTitle:"), UI.getTextField(UI.fp(this, "dataChanged")));
        txtNameInitials = (UI.TextField) UI.addComponent(pnlLeftTop,
                i18n("NameInitials:"),
                UI.getTextField(UI.fp(this, "dataChanged")));
        txtNameForenames = (UI.TextField) UI.addComponent(pnlLeftTop,
                i18n("NameForenames:"),
                UI.getTextField(UI.fp(this, "dataChanged")));
        txtNameSurname = (UI.TextField) UI.addComponent(pnlLeftTop,
                i18n("NameSurname:"),
                UI.getTextField(null, UI.fp(this, "dataChanged"),
                    UI.fp(this, "changedName")));

        txtAddress = (UI.TextArea) UI.addComponent(pnlLeftMid,
                i18n("Address:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged"),
                    UI.fp(this, "changedAddress")));

        cboTown = UI.getCombo(i18n("town"), LookupCache.getOwnerTowns(),
                UI.fp(this, "changedTown"));
        cboTown.setEditable(true);
        cboCounty = UI.getCombo(i18n("county"), LookupCache.getOwnerCounties(),
                UI.fp(this, "dataChanged"));
        cboCounty.setEditable(true);

        boolean showTownCounty = !Configuration.getBoolean("HideTownCounty");

        if (showTownCounty) {
            UI.addComponent(pnlLeftBot, i18n("town"), cboTown);
            UI.addComponent(pnlLeftBot, i18n("county"), cboCounty);
        }

        txtPostcode = (UI.TextField) UI.addComponent(pnlLeftBot,
                i18n("Postcode:"), UI.getTextField(UI.fp(this, "dataChanged")));

        txtHomeTelephone = (UI.TextField) UI.addComponent(pnlLeftBot,
                i18n("Home_Tel:"), UI.getTextField(UI.fp(this, "dataChanged")));

        txtMobileTelephone = (UI.TextField) UI.addComponent(pnlLeftBot,
                i18n("Mobile_Tel"), UI.getTextField(UI.fp(this, "dataChanged")));

        txtWorkTelephone = (UI.TextField) UI.addComponent(pnlLeftBot,
                i18n("Work_Tel:"), UI.getTextField(UI.fp(this, "dataChanged")));

        txtEmail = (UI.TextField) UI.addComponent(pnlLeftBot, i18n("Email:"),
                UI.getTextField(UI.fp(this, "dataChanged")));

        pnlLeft.add(pnlLeftTop, UI.BorderLayout.NORTH);
        pnlLeft.add(pnlLeftMid, UI.BorderLayout.CENTER);
        pnlLeft.add(pnlLeftBot, UI.BorderLayout.SOUTH);
        pnlDetails.add(pnlLeft);

        // Right pane ======================================
        UI.Panel pnlRight = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlThumbnail = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlRightTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 45, 55 }));
        UI.Panel pnlRightMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 45, 55 }));

        lblThumbnail = UI.getLabel();
        lblThumbnail.setPreferredSize(UI.getDimension(100, 50));
        pnlThumbnail.add(lblThumbnail, UI.BorderLayout.NORTH);

        chkIsMember = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Member"),
                    i18n("Check_this_box_if_this_owner_is_a_member_of_your_organisation"),
                    UI.fp(this, "dataChanged")));
        txtMembershipExpiryDate = (DateField) pnlRightTop.add(UI.getDateField(
                    i18n("if_this_owner_is_a_member_the_date_that_membership_expires"),
                    UI.fp(this, "dataChanged")));

        chkBanned = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Banned"),
                    null, UI.fp(this, "dataChanged")));

        chkIsDonor = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Donor"),
                    i18n("check_this_box_if_this_owner_makes_donations"),
                    UI.fp(this, "dataChanged")));

        chkVolunteer = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Volunteer/Dog_Walker"),
                    i18n("Check_this_box_if_this_owner_is_a_volunteer_or_dog_walker"),
                    UI.fp(this, "dataChanged")));

        chkIsShelter = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("other_animal_shelter"),
                    null, UI.fp(this, "dataChanged")));

        chkIsACO = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("is_animal_care_officer"),
                    null, UI.fp(this, "dataChanged")));

        chkIsStaff = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("is_staff"),
                    null, UI.fp(this, "dataChanged")));

        chkIsRetailer = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("retailer"),
                    i18n("check_if_owner_is_retailer"),
                    UI.fp(this, "dataChanged")));

        chkIsFosterer = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Fosters_Animals"),
                    i18n("check_this_box_if_this_owner_can_foster_animals"),
                    UI.fp(this, "dataChanged")));

        chkIsVet = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Vet"),
                    i18n("check_this_box_if_this_owner_is_a_veterinary_surgery_or_clinic"),
                    UI.fp(this, "dataChanged")));

        chkHomeChecker = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("Homechecker"),
                    i18n("Tick_this_box_if_this_owner_is_a_home_checker"),
                    UI.fp(this, "actionHomecheckerChanged")));

        chkHomeCheck = (UI.CheckBox) pnlRightTop.add(UI.getCheckBox(i18n("ID_Check"),
                    i18n("has_this_owner_ever_been_homechecked"),
                    UI.fp(this, "dataChanged")));

        txtLastHadHomecheckDate = (DateField) pnlRightTop.add(UI.getDateField(
                    i18n("The_date_this_owner_was_last_homechecked"),
                    UI.fp(this, "dataChanged")));

        embHomeCheckedBy = (OwnerLink) UI.addComponent(pnlRightTop,
                i18n("Checked_By"),
                new OwnerLink(OwnerLink.MODE_ONELINE,
                    OwnerLink.FILTER_HOMECHECKERS, "LINK"));
        embHomeCheckedBy.setParent(this);

        txtComments = (UI.TextArea) UI.addComponent(pnlRightMid,
                i18n("Comments:"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        pnlThumbnail.add(pnlRightTop, UI.BorderLayout.CENTER);
        pnlRight.add(pnlThumbnail, UI.BorderLayout.NORTH);
        pnlRight.add(pnlRightMid, UI.BorderLayout.CENTER);
        pnlDetails.add(pnlRight);

        tabTabs.addTab(i18n("Details"), null, pnlDetails,
            i18n("basic_owner_information"));

        // Homechecker Tab =========================================================
        UI.Panel pnlHomeChecker = UI.getPanel(UI.getGridLayout(2));
        UI.Panel pnlHomeCheckerAreas = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlHomeCheckerHistory = UI.getPanel(UI.getBorderLayout());

        // Areas pane
        pnlHomeCheckerAreas.add(UI.getTitleLabel(i18n("Homecheck_Areas:")),
            UI.BorderLayout.NORTH);

        UI.Panel pnlHomeCheckerAreasInner = UI.getPanel(UI.getBorderLayout());

        UI.ToolBar tlbAreas = UI.getToolBar();
        tlbAreas.add(UI.getButton(null, i18n("New_homecheck_area"),
                IconManager.getIcon(IconManager.NEW),
                UI.fp(this, "actionNewHomecheckArea")));
        tlbAreas.add(UI.getButton(null, i18n("Delete_homecheck_area"),
                IconManager.getIcon(IconManager.DELETE),
                UI.fp(this, "actionDeleteHomecheckArea")));
        tblAreas = UI.getTable(null, null, tlbAreas);

        pnlHomeCheckerAreasInner.add(tlbAreas, UI.BorderLayout.NORTH);
        UI.addComponent(pnlHomeCheckerAreasInner, tblAreas);
        pnlHomeCheckerAreas.add(pnlHomeCheckerAreasInner, UI.BorderLayout.CENTER);

        // History pane
        pnlHomeCheckerHistory.add(UI.getTitleLabel(i18n("Homecheck_History")),
            UI.BorderLayout.NORTH);
        tblCheckHistory = UI.getTable(null,
                UI.fp(this, "actionHistoryDoubleClick"));
        UI.addComponent(pnlHomeCheckerHistory, tblCheckHistory);

        pnlHomeChecker.add(pnlHomeCheckerAreas);
        pnlHomeChecker.add(pnlHomeCheckerHistory);

        tabTabs.addTab(i18n("Homechecker"), null, pnlHomeChecker,
            i18n("details_if_this_owner_is_a_homechecker"));

        // Criteria tab =====================================================
        UI.Panel pnlAnimalCriteria = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 60, 40 }));
        UI.Panel pnlACLeft = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlACLeftTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlACLeftMid = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));
        UI.Panel pnlACRight = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlACRightTop = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 30, 70 }));

        pnlACLeft.add(pnlACLeftTop, UI.BorderLayout.NORTH);
        pnlACLeft.add(pnlACLeftMid, UI.BorderLayout.CENTER);
        pnlACRight.add(pnlACRightTop, UI.BorderLayout.NORTH);
        pnlAnimalCriteria.add(pnlACLeft);
        pnlAnimalCriteria.add(pnlACRight);

        txtMatchAdded = (DateField) UI.addComponent(pnlACLeftTop,
                i18n("Match_added"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        txtMatchExpires = (DateField) UI.addComponent(pnlACLeftTop,
                i18n("Match_expires"),
                UI.getDateField(null, UI.fp(this, "dataChanged")));

        pnlACLeftTop.add(UI.getLabel());
        chkMatchActive = (UI.CheckBox) pnlACLeftTop.add(UI.getCheckBox(i18n("Active"),
                    null, UI.fp(this, "dataChanged")));

        cboMatchSex = UI.getCombo(LookupCache.getSexLookup(), "Sex",
                UI.fp(this, "dataChanged"), i18n("(all)"));
        UI.addComponent(pnlACRightTop, i18n("Sex"), cboMatchSex);

        cboMatchSize = UI.getCombo(LookupCache.getSizeLookup(), "Size",
                UI.fp(this, "dataChanged"), i18n("(all)"));
        UI.addComponent(pnlACRightTop, i18n("Size"), cboMatchSize);

        txtMatchAgeFrom = (UI.TextField) UI.addComponent(pnlACRightTop,
                i18n("Age_From"),
                UI.getTextField(null, UI.fp(this, "dataChanged")));

        txtMatchAgeTo = (UI.TextField) UI.addComponent(pnlACRightTop,
                i18n("Age_To"),
                UI.getTextField(null, UI.fp(this, "dataChanged")));

        cboMatchAnimalType = UI.getCombo(LookupCache.getAnimalTypeLookup(),
                "AnimalType", UI.fp(this, "dataChanged"), i18n("(all)"));
        UI.addComponent(pnlACRightTop, i18n("type"), cboMatchAnimalType);

        cboMatchSpecies = UI.getCombo(LookupCache.getSpeciesLookup(),
                "SpeciesName", UI.fp(this, "dataChanged"), i18n("(all)"));
        UI.addComponent(pnlACRightTop, i18n("Species"), cboMatchSpecies);

        cboMatchBreed = UI.getCombo(LookupCache.getBreedLookup(), "BreedName",
                UI.fp(this, "dataChanged"), i18n("(all)"));
        UI.addComponent(pnlACRightTop, i18n("Breed"), cboMatchBreed);

        cboMatchBreed2 = UI.getCombo(LookupCache.getBreedLookup(), "BreedName",
                UI.fp(this, "dataChanged"), i18n("(all)"));
        UI.addComponent(pnlACRightTop, i18n("Or"), cboMatchBreed2);

        pnlACRightTop.add(UI.getLabel());
        chkMatchGoodWithCats = (UI.CheckBox) UI.addComponent(pnlACRightTop,
                UI.getCheckBox(i18n("Good_with_cats"), null,
                    UI.fp(this, "dataChanged")));

        pnlACRightTop.add(UI.getLabel());
        chkMatchGoodWithDogs = (UI.CheckBox) UI.addComponent(pnlACRightTop,
                UI.getCheckBox(i18n("Good_with_dogs"), null,
                    UI.fp(this, "dataChanged")));

        pnlACRightTop.add(UI.getLabel());
        chkMatchGoodWithChildren = (UI.CheckBox) UI.addComponent(pnlACRightTop,
                UI.getCheckBox(i18n("Good_with_children"), null,
                    UI.fp(this, "dataChanged")));

        pnlACRightTop.add(UI.getLabel());
        chkMatchHouseTrained = (UI.CheckBox) UI.addComponent(pnlACRightTop,
                UI.getCheckBox(i18n("Housetrained"), null,
                    UI.fp(this, "dataChanged")));

        txtMatchCommentsContain = (UI.TextArea) UI.addComponent(pnlACLeftMid,
                i18n("Comments_contain"),
                UI.getTextArea(null, UI.fp(this, "dataChanged")));

        tabTabs.addTab(i18n("criteria"), null, pnlAnimalCriteria,
            i18n("animals_this_owner_is_interested_in"));

        // Tabs for satellite data
        tabTabs.addTab(i18n("Donations"), ownerdonations);

        tabTabs.addTab(i18n("Vouchers"), null, ownervouchers,
            i18n("details_of_vouchers_given_by_the_shelter"));

        tabTabs.addTab(i18n("media"), media);

        tabTabs.addTab(i18n("diary"), diary);

        tabTabs.addTab(i18n("movements"), null, movement,
            i18n("movements_connected_with_this_owner"));

        tabTabs.addTab(i18n("links"), ownerlinks);

        tabTabs.addTab(i18n("log"), null, log, i18n("other_owner_information"));

        if (additional.hasFields()) {
            tabTabs.addTab(i18n("additional"), null, additional,
                i18n("additional_info"));
        }

        add(tabTabs, UI.BorderLayout.CENTER);

        // Toolbar ================================================================
        pnlTop = UI.getPanel(UI.getFlowLayout(true));

        tlbTools = UI.getToolBar();

        btnSave = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Save_this_record"), 's',
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_SAVE),
                    UI.fp(this, "actionSave")));

        btnDelete = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Delete_this_record"), 'z',
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_DELETE),
                    UI.fp(this, "actionDelete")));

        btnDoc = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Generate_a_document_for_this_owner"), 'w',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITOWNER_GENERATEDOC),
                    UI.fp(this, "actionDocument")));

        btnDiaryTask = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("create_a_diary_task_for_this_owner"), 't',
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_DIARYTASK),
                    UI.fp(this, "actionDiaryTask")));

        btnEmail = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("send_an_email_to_this_owner"), 'm',
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_EMAIL),
                    UI.fp(this, "actionEmail")));

        btnMerge = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Merge_another_owner_into_this_one"), 'g',
                    IconManager.getIcon(IconManager.SCREEN_EDITOWNER_MERGE),
                    UI.fp(this, "actionMerge")));

        pnlTop.add(tlbTools);
        add(pnlTop, UI.BorderLayout.NORTH);
    }

    public void actionDiaryTask() {
        DiaryTaskExecute edt = new DiaryTaskExecute(owner, this);
        Global.mainForm.addChild(edt);
        edt = null;
    }

    public void actionMerge() {
        // Prompt for the owner record
        OwnerFind fo = new OwnerFind(this, false, false);
        Global.mainForm.addChild(fo);
    }

    public void actionEmail() {
        // Verify this owner has an email address
        if (txtEmail.getText().trim().equals("")) {
            Dialog.showError(i18n("no_email_address"));

            return;
        }

        // Make sure ours is set up
        if (!Email.isSetup()) {
            return;
        }

        // Open email form
        Email.singleEmailForm(txtEmail.getText());
    }

    public void changedTown() {
        // Filter the county selection box
        Vector v = LookupCache.getCountiesForTown(cboTown.getSelectedItem()
                                                         .toString());

        if (v.size() == 0) {
            v = LookupCache.getOwnerCounties();
        }

        cboCounty.removeAllItems();

        for (int i = 0; i < v.size(); i++) {
            cboCounty.addItem(v.get(i).toString());
        }

        cboCounty.setSelectedItem(v.get(0).toString());
        dataChanged();
    }

    public void changedName() {
        if (isNewRecord) {
            txtAddress.grabFocus();
            checkOwner(true);
        }
    }

    public void changedAddress() {
        if (isNewRecord) {
            txtPostcode.grabFocus();
            checkOwner(false);
        }
    }

    public void actionHomecheckerChanged() {
        tabTabs.setEnabledAt(1, chkHomeChecker.isSelected());
        dataChanged();
    }

    public void actionDocument() {
        OwnerDocument od = new OwnerDocument(owner, media);
    }

    public void actionHistoryDoubleClick() {
        int id = tblCheckHistory.getSelectedID();

        if (id == -1) {
            return;
        }

        OwnerEdit oe = new OwnerEdit();
        oe.openForEdit(id);
        Global.mainForm.addChild(oe);
    }

    public void actionNewHomecheckArea() {
        try {
            String area = Dialog.getInput(i18n("The_new_homecheck_area"), "");

            if ((area == null) || area.equals("")) {
                return;
            }

            String x = Utils.nullToEmptyString(owner.getHomeCheckAreas());
            x += (" " + area);
            owner.setHomeCheckAreas(x);
            dataChanged();
            loadHomecheckerAreas();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionDeleteHomecheckArea() {
        try {
            String x = Utils.nullToEmptyString(owner.getHomeCheckAreas());
            String y = (String) tblAreas.getModel()
                                        .getValueAt(tblAreas.getSelectedRow(), 0);

            if ((y == null) || y.equals("")) {
                return;
            }

            x = Utils.replace(x, " " + y, "");
            x = Utils.replace(x, y, "");
            owner.setHomeCheckAreas(x);
            dataChanged();
            loadHomecheckerAreas();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionDelete() {
        // Make sure this owner is not on any movements
        Adoption ad = new Adoption();

        try {
            ad.openRecordset("OwnerID = " + owner.getID());

            if (!ad.getEOF()) {
                Dialog.showError(i18n("You_cannot_remove_this_record,_as_it_appears_on_animal_movements."));

                return;
            }
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_checking_the_owner_record_for_movements."));
            Global.logException(e, getClass());

            return;
        }

        // Make sure this owner is not on any animal records,
        // lost/found or waiting list entries
        AnimalWaitingList awl = new AnimalWaitingList();
        AnimalLost al = new AnimalLost();
        AnimalFound af = new AnimalFound();
        Animal a = new Animal();

        try {
            awl.openRecordset("OwnerID = " + owner.getID());

            if (!awl.getEOF()) {
                Dialog.showError(i18n("you_cannot_delete_this_owner_as_it_is_referenced_from_other_records"));
                awl.free();
                awl = null;

                return;
            }

            awl.free();
            awl = null;
            al.openRecordset("OwnerID = " + owner.getID());

            if (!al.getEOF()) {
                Dialog.showError(i18n("you_cannot_delete_this_owner_as_it_is_referenced_from_other_records"));
                al.free();
                al = null;

                return;
            }

            al.free();
            al = null;
            af.openRecordset("OwnerID = " + owner.getID());

            if (!af.getEOF()) {
                Dialog.showError(i18n("you_cannot_delete_this_owner_as_it_is_referenced_from_other_records"));
                af.free();
                af = null;

                return;
            }

            af.free();
            af = null;
            a.openRecordset("OriginalOwnerID = " + owner.getID());

            if (!a.getEOF()) {
                Dialog.showError(i18n("you_cannot_delete_this_owner_as_it_is_referenced_from_other_records"));
                a.free();
                a = null;

                return;
            }

            a.openRecordset("BroughtInByOwnerID = " + owner.getID());

            if (!a.getEOF()) {
                Dialog.showError(i18n("you_cannot_delete_this_owner_as_it_is_referenced_from_other_records"));
                a.free();
                a = null;

                return;
            }

            a.free();
            a = null;
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_checking_the_owner_record_for_movements."));
            Global.logException(e, getClass());

            return;
        }

        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM diary WHERE LinkID = " +
                    owner.getID() + " AND LinkType = " + Diary.LINKTYPE_OWNER;
                DBConnection.executeAction(sql);
                sql = "DELETE FROM media WHERE LinkID = " + owner.getID() +
                    " AND LinkTypeID = " + Media.LINKTYPE_OWNER;
                DBConnection.executeAction(sql);
                sql = "DELETE FROM owner WHERE ID = " + owner.getID();
                DBConnection.executeAction(sql);
                sql = "DELETE FROM additional Where LinkID = " + owner.getID() +
                    " AND LinkType = " +
                    Integer.toString(AdditionalField.LINKTYPE_OWNER);
                DBConnection.executeAction(sql);
                sql = "DELETE FROM ownerdonation WHERE OwnerID = " +
                    owner.getID();
                DBConnection.executeAction(sql);
                sql = "DELETE FROM ownervoucher WHERE OwnerID = " +
                    owner.getID();
                DBConnection.executeAction(sql);
                dispose();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionSave() {
        saveData();
        txtNameTitle.grabFocus();
    }

    public void animalSelected(Animal theanimal) {
    }

    public void foundAnimalSelected(AnimalFound thefoundanimal) {
    }

    public void lostAnimalSelected(AnimalLost thelostanimal) {
    }

    public void ownerSelected(Owner theowner) {
        // Merge the selected owner's record into this one.
        try {
            // Make sure that we aren't trying to merge the
            // same record
            if (owner.getID().intValue() == theowner.getID().intValue()) {
                Dialog.showError(i18n("You_cannot_merge_an_owner_into_itself"));

                return;
            }

            String sql = "UPDATE animal SET OriginalOwnerID = " +
                owner.getID() + " WHERE OriginalOwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE animal SET BroughtInByOwnerID = " + owner.getID() +
                " WHERE BroughtInByOwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE animal SET OwnersVetID = " + owner.getID() +
                " WHERE BroughtInByOwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE animallost SET OwnerID = " + owner.getID() +
                " WHERE OwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE animalfound SET OwnerID = " + owner.getID() +
                " WHERE OwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE animalwaitinglist SET OwnerID = " + owner.getID() +
                " WHERE OwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE ownerdonation SET OwnerID = " + owner.getID() +
                " WHERE OwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE ownervoucher SET OwnerID = " + owner.getID() +
                " WHERE OwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE adoption SET OwnerID = " + owner.getID() +
                " WHERE OwnerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE adoption SET RetailerID = " + owner.getID() +
                " WHERE RetailerID = " + theowner.getID();
            DBConnection.executeAction(sql);
            sql = "UPDATE media SET LinkID = " + owner.getID() +
                " WHERE LinkID = " + theowner.getID() + " AND LinkTypeID = " +
                Media.LINKTYPE_OWNER;
            DBConnection.executeAction(sql);
            sql = "UPDATE diary SET LinkID = " + owner.getID() +
                " WHERE LinkID = " + theowner.getID() + " AND LinkTypeID = " +
                Diary.LINKTYPE_OWNER;
            DBConnection.executeAction(sql);
            sql = "UPDATE log SET LinkID = " + owner.getID() +
                " WHERE LinkID = " + theowner.getID() + " AND LinkType = " +
                Log.LINKTYPE_OWNER;
            DBConnection.executeAction(sql);

            // Remove the original owner record
            sql = "DELETE FROM owner WHERE ID = " + theowner.getID();
            DBConnection.executeAction(sql);

            // Reload the record
            this.openForEdit(owner.getID().intValue());

            // Give a message
            Dialog.showInformation(i18n("Owner_record_successfully_merged"),
                i18n("Successful_merge"));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void retailerSelected(Owner theowner) {
    }
}
