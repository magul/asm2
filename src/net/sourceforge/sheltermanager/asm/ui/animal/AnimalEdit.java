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

import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Animal.AnimalCode;
import net.sourceforge.sheltermanager.asm.bo.AnimalLitter;
import net.sourceforge.sheltermanager.asm.bo.AnimalName;
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.AnimalPrint;
import net.sourceforge.sheltermanager.asm.reports.LostFoundMatch;
import net.sourceforge.sheltermanager.asm.ui.diary.DiarySelector;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryTaskExecute;
import net.sourceforge.sheltermanager.asm.ui.log.LogSelector;
import net.sourceforge.sheltermanager.asm.ui.medical.MedicalSelector;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementSelector;
import net.sourceforge.sheltermanager.asm.ui.owner.DonationSelector;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLinkListener;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.CustomUI;
import net.sourceforge.sheltermanager.asm.ui.ui.DateChangedListener;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.FunctionPointer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;


/**
 * This class contains all code for editing animal records.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class AnimalEdit extends ASMForm implements DateChangedListener,
    OwnerLinkListener {
    /** Tab indexes */
    private final static int TAB_DETAILS = 0;
    private final static int TAB_ENTRY = 1;
    private final static int TAB_VET = 2;
    private final static int TAB_DEATH = 3;
    private final static int TAB_VACCINATION = 4;
    private final static int TAB_MEDICAL = 5;
    private final static int TAB_DIET = 6;
    private final static int TAB_COSTS = 7;
    private final static int TAB_DONATIONS = 8;
    private final static int TAB_MEDIA = 9;
    private final static int TAB_DIARY = 10;
    private final static int TAB_MOVEMENT = 11;
    private final static int TAB_LOG = 12;
    private final static int TAB_ADDITIONAL = 13;
    public Animal animal = null;
    public MediaSelector animalmedia = null;
    public VaccinationSelector animalvaccinations = null;
    public CostSelector animalcosts = null;
    private MovementSelector animalmovement = null;
    private DiarySelector animaldiary = null;
    private DietSelector animaldiets = null;
    private MedicalSelector medicals = null;
    private DonationSelector donations = null;
    private LogSelector log = null;
    private AdditionalFieldView additional = null;

    /** True if this is a new record */
    private boolean isNewRecord = false;

    /** True if a logical instead of internal location is displayed */
    private boolean showingLogicalLocation = false;
    private Vector ctl = null;

    /**
     * Determines whether the data on the form has changed since it was loaded.
     */
    private boolean isDirty = false;

    /** Is the form still loading */
    private boolean isLoading = false;
    private UI.Label lblThumbnail;
    private UI.Button btnClone;
    private UI.Button btnCopyNotes;
    private UI.Button btnCreateLitter;
    private UI.Button btnDelete;
    private UI.Button btnDiaryTask;
    private UI.Button btnFindLitter;
    private UI.Button btnMatchLost;
    private UI.Button btnOwner;
    private UI.Button btnSave;
    private UI.Button btnViewLitterMates;
    private UI.Button btnWrite;
    private UI.Button btnPrint;
    private UI.ComboBox cboBreed;
    private UI.ComboBox cboBreed2;
    private UI.ComboBox cboColour;
    private UI.ComboBox cboCoatType;
    private UI.ComboBox cboCombiTestResult;
    private UI.ComboBox cboHeartwormTestResult;
    private UI.ComboBox cboFLVTestResult;
    private UI.ComboBox cboEntryReason;
    private UI.ComboBox cboLocation;
    private UI.ComboBox cboPTSReason;
    private UI.ComboBox cboSex;
    private UI.ComboBox cboSize;
    private UI.ComboBox cboSpecies;
    private UI.ComboBox cboType;
    private UI.ComboBox cboGoodCats;
    private UI.ComboBox cboGoodDogs;
    private UI.ComboBox cboGoodKids;
    private UI.ComboBox cboHouseTrained;
    private UI.CheckBox chkCombiTested;
    private UI.CheckBox chkCrueltyCase;
    private UI.CheckBox chkDOA;
    private UI.CheckBox chkDiedOffShelter;
    private UI.CheckBox chkIdentichipped;
    private UI.CheckBox chkTattoo;
    private UI.CheckBox chkNeutered;
    private UI.CheckBox chkDeclawed;
    private UI.CheckBox chkNonShelter;
    private UI.CheckBox chkNotForAdoption;
    private UI.CheckBox chkPTS;
    private UI.CheckBox chkTransferIn;
    private UI.CheckBox chkHeartwormTested;
    private UI.CheckBox chkHasSpecialNeeds;
    private UI.CheckBox chkCrossBreed;
    private OwnerLink embBroughtInBy;
    private OwnerLink embOriginalOwner;
    private OwnerLink embVet;
    private OwnerLink embCurrentVet;
    private UI.Label lblLastLocation;
    private UI.Label lblLocationText;
    private UI.Panel pnlDetails;
    private UI.Panel pnlLeftFields;
    private UI.Label lblNonShelter;
    private UI.Label lblNonShelterSpace;
    private UI.Label lblNonShelterSpace2;
    private UI.TabbedPane tabTabs;
    private UI.ToolBar tlbTools;
    private UI.TextField txtAcceptanceNumber;
    private UI.SearchTextField txtAnimalName;
    private DateField txtCombiTested;
    private UI.TextArea txtComments;
    private DateField txtDateBroughtIn;
    private DateField txtDateDeceased;
    private DateField txtDateOfBirth;
    private UI.CheckBox chkEstimatedDOB;
    private UI.TextArea txtHealthProblems;
    private UI.TextArea txtHiddenAnimalComments;
    private UI.TextField txtIdentichipNo;
    private DateField txtIdentichipDate;
    private UI.TextField txtTattooNumber;
    private DateField txtTattooDate;
    private UI.TextArea txtMarkings;
    private DateField txtNeutered;
    private DateField txtHeartwormTestDate;
    private UI.TextArea txtOperator;
    private UI.TextArea txtPTSReason;
    private UI.TextField txtRabiesTag;
    private UI.TextArea txtReasonForEntry;
    private UI.TextArea txtReasonNotBroughtByOwner;
    private AnimalCodeField txtShelterCode;
    private String audit = null;

    /** Whether we've added any custom buttons to the screen */
    private boolean addedCustomButtons = false;
    private long lastTypeCheck = 0;
    boolean loadedCosts = false;
    boolean loadedDiary = false;
    boolean loadedDiets = false;
    boolean loadedDonations = false;
    boolean loadedEntry = false;
    boolean loadedLogs = false;
    boolean loadedMedia = false;
    boolean loadedMedical = false;
    boolean loadedMovements = false;
    boolean loadedVaccinations = false;
    boolean loadedVet = false;
    private long lastCheckedDate = 0;

    /** Creates new form EditAnimal */
    public AnimalEdit() {
        init(Global.i18n("uianimal", "Edit_Animal"),
            IconManager.getIcon(IconManager.SCREEN_EDITANIMAL), "uianimal");
    }

    /**
     * Deallocate class-level object references
     */
    public void dispose() {
        if (!Global.isCacheActiveAnimals()) {
            try {
                animal.free();
            } catch (Exception e) {
            }
        }

        try {
            animalmedia.dispose();
        } catch (Exception e) {
        }

        try {
            animaldiary.dispose();
        } catch (Exception e) {
        }

        try {
            animalmovement.dispose();
        } catch (Exception e) {
        }

        try {
            animalcosts.dispose();
        } catch (Exception e) {
        }

        try {
            animaldiets.dispose();
        } catch (Exception e) {
        }

        try {
            animalvaccinations.dispose();
        } catch (Exception e) {
        }

        try {
            medicals.dispose();
        } catch (Exception e) {
        }

        try {
            log.dispose();
        } catch (Exception e) {
        }

        animal = null;
        animalvaccinations = null;
        animalmedia = null;
        animalcosts = null;
        animaldiary = null;
        animalmovement = null;
        animaldiets = null;
        medicals = null;
        log = null;
        additional = null;
        unregisterTabOrder();
        super.dispose();
    }

    public Vector getTabOrder() {
        ctl = new Vector();
        ctl.add(chkNonShelter);
        ctl.add(chkNotForAdoption);
        ctl.add(chkCrueltyCase);
        ctl.add(txtShelterCode.getCodeField());
        ctl.add(txtShelterCode.getShortCodeField());
        ctl.add(txtAcceptanceNumber);
        ctl.add(txtAnimalName.getTextField());
        ctl.add(cboSex);
        ctl.add(cboType);
        ctl.add(cboColour);

        if (!Configuration.getBoolean("DontShowCoatType")) {
            ctl.add(cboCoatType);
        }

        ctl.add(cboSize);
        ctl.add(cboSpecies);
        ctl.add(cboBreed);

        if (!Global.isSingleBreed()) {
            ctl.add(chkCrossBreed);
            ctl.add(cboBreed2);
        }

        ctl.add(cboLocation);
        ctl.add(txtDateOfBirth);
        ctl.add(chkEstimatedDOB);
        ctl.add(chkIdentichipped);
        ctl.add(txtIdentichipDate);
        ctl.add(txtIdentichipNo);
        ctl.add(chkTattoo);
        ctl.add(txtTattooDate);
        ctl.add(txtTattooNumber);
        ctl.add(chkNeutered);
        ctl.add(txtNeutered);
        ctl.add(chkDeclawed);
        ctl.add(chkHeartwormTested);
        ctl.add(txtHeartwormTestDate);
        ctl.add(cboHeartwormTestResult);
        ctl.add(chkCombiTested);
        ctl.add(txtCombiTested);
        ctl.add(cboCombiTestResult);

        if (Locale.getDefault().equals(Locale.US)) {
            ctl.add(cboFLVTestResult);
        }

        ctl.add(txtMarkings);
        ctl.add(txtHiddenAnimalComments);
        ctl.add(txtComments);
        ctl.add(cboGoodCats);
        ctl.add(cboGoodDogs);
        ctl.add(cboGoodKids);
        ctl.add(cboHouseTrained);
        ctl.add(txtReasonNotBroughtByOwner);
        ctl.add(txtReasonForEntry);
        ctl.add(cboEntryReason);
        ctl.add(chkTransferIn);
        ctl.add(txtDateBroughtIn);
        ctl.add(txtOperator);
        ctl.add(txtHealthProblems);
        ctl.add(txtRabiesTag);
        ctl.add(chkHasSpecialNeeds);
        ctl.add(txtDateDeceased);
        ctl.add(chkDOA);
        ctl.add(chkPTS);
        ctl.add(chkDiedOffShelter);
        ctl.add(cboPTSReason);
        ctl.add(txtPTSReason);

        ctl.addAll(additional.getAdditionalComponents());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtAcceptanceNumber;
    }

    public boolean needsScroll() {
        return true;
    }

    public int getScrollHeight() {
        return 600;
    }

    /** Deactivates screen elements based on security settings */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecDeleteAnimal()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecCloneAnimal()) {
            btnClone.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecMatchLostAndFoundAnimals()) {
            btnMatchLost.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewAnimalVaccination()) {
            animalvaccinations.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddLitterLog()) {
            btnCreateLitter.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnDiaryTask.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnWrite.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMedia()) {
            btnCopyNotes.setEnabled(false);
        }

        // Tabs/View
        if (!Global.currentUserObject.getSecViewAnimalVet()) {
            tabTabs.setEnabledAt(TAB_VET, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalVaccination()) {
            tabTabs.setEnabledAt(TAB_VACCINATION, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalMedical()) {
            tabTabs.setEnabledAt(TAB_MEDICAL, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalDiet()) {
            tabTabs.setEnabledAt(TAB_DIET, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalCost()) {
            tabTabs.setEnabledAt(TAB_COSTS, false);
        }

        if (!Global.currentUserObject.getSecViewOwnerDonation()) {
            tabTabs.setEnabledAt(TAB_DONATIONS, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalMedia()) {
            tabTabs.setEnabledAt(TAB_MEDIA, false);
        }

        if (!Global.currentUserObject.getSecViewDiaryNotes()) {
            tabTabs.setEnabledAt(TAB_DIARY, false);
        }

        if (!Global.currentUserObject.getSecViewAnimalMovements()) {
            tabTabs.setEnabledAt(TAB_MOVEMENT, false);
        }

        if (!Global.currentUserObject.getSecViewLogEntry()) {
            tabTabs.setEnabledAt(TAB_LOG, false);
        }

        // If this isn't an addition of a new animal, and there's no
        // change permission for the user - make sure they aren't
        // allowed to change the details 
        if (!isNewRecord) {
            if (!Global.currentUserObject.getSecChangeAnimal()) {
                btnSave.setEnabled(false);
            }
        }
    }

    public String getAuditInfo() {
        return audit;
    }

    /**
     * Checks whether the deceased date box is blank and if it is, deactivates
     * the death-related fields.
     */
    public void updateDeath() {
        boolean b = !txtDateDeceased.getText().equals("");
        chkDOA.setEnabled(b);
        chkPTS.setEnabled(b);
        chkDiedOffShelter.setEnabled(b);
        cboPTSReason.setEnabled(b);
        txtPTSReason.setEnabled(b);
        dataChanged();
    }

    /**
     * Loads the appropriate date from the passed animal object and fills the
     * controls
     */
    public void openForEdit(Animal animal) {
        isNewRecord = false;
        isLoading = true;
        this.animal = animal;
        loadData();
        enableButtons();
        enableOnShelterTabs();
        updateDeath();
        showThumbnail();
        setSecurity();
        isLoading = false;
        setDirty(false);
    }

    public void setDirty(final boolean dirty) {
        UI.invokeLater(new Runnable() {
                public void run() {
                    isDirty = dirty;
                    btnSave.setEnabled(isDirty);

                    if (!Global.currentUserObject.getSecChangeAnimal()) {
                        btnSave.setEnabled(false);
                    }
                }
            });
    }

    /**
     * Identical to openForEdit, except accepts a parameter that tells the
     * system the record has not been saved, so the animal tabs should not be
     * enabled.
     */
    public void openForEdit(Animal animal, boolean unsaved) {
        isNewRecord = true;
        isLoading = true;
        this.animal = animal;
        loadData(false);
        enableOnShelterTabs();
        enableNonAnimalTabs(false);
        enableButtons();
        updateDeath();
        setSecurity();
        isLoading = false;
        setDirty(false);
    }

    /**
     * Same as open for edit, but instead, the animal object is created here
     * from the ID
     */
    public void openForEdit(int animalID) {
        Animal a = new Animal();
        a.openRecordset("ID = " + animalID);
        openForEdit(a);
        a = null;
    }

    /** Sets the form into creating a new animal record */
    public void openForNew() {
        isNewRecord = true;

        // Don't need last location field
        removeLastLocationField();

        setTitle(Global.i18n("uianimal", "Create_New_Animal"));

        animal = new Animal();
        animal.openRecordset("ID = 0");

        try {
            animal.addNew();
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        // Set some default values

        // Date of birth and date brought in
        Calendar cal = Calendar.getInstance();

        if (Configuration.getBoolean("DefaultDateBroughtIn")) {
            try {
                txtDateBroughtIn.setText(Utils.formatDate(cal));
                animal.setDateBroughtIn(Utils.calendarToDate(cal));
                animal.setMostRecentEntryDate(Utils.calendarToDate(cal));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // Default species and type
        Utils.setComboFromID(LookupCache.getAnimalTypeLookup(), "AnimalType",
            new Integer(Configuration.getInteger("AFDefaultType")), cboType);
        Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
            new Integer(Configuration.getInteger("AFDefaultSpecies")),
            cboSpecies);

        // Default daily boarding cost
        try {
            animal.setDailyBoardingCost(new Double(Configuration.getDouble(
                        "DefaultDailyBoardingCost")));
            animalcosts.setDailyBoardingCost(Configuration.getDouble(
                    "DefaultDailyBoardingCost"));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Set the breed choices based on default species
        checkBreed();

        // Default internal location, entry reason, death reason, breed,
        // colour
        // and size
        cboLocation.setSelectedIndex(0);
        cboEntryReason.setSelectedIndex(0);
        cboPTSReason.setSelectedIndex(0);
        cboColour.setSelectedIndex(0);
        cboCoatType.setSelectedIndex(0);
        cboSize.setSelectedIndex(0);

        // Default good with ... flags
        cboGoodCats.setSelectedIndex(2);
        cboGoodDogs.setSelectedIndex(2);
        cboGoodKids.setSelectedIndex(2);
        cboHouseTrained.setSelectedIndex(2);

        Utils.setComboFromID(LookupCache.getInternalLocationLookup(),
            "LocationName",
            new Integer(Configuration.getInteger("AFDefaultLocation")),
            cboLocation);
        Utils.setComboFromID(LookupCache.getEntryReasonLookup(), "ReasonName",
            new Integer(Configuration.getInteger("AFDefaultEntryReason")),
            cboEntryReason);
        Utils.setComboFromID(LookupCache.getDeathReasonLookup(), "ReasonName",
            new Integer(Configuration.getInteger("AFDefaultDeathReason")),
            cboPTSReason);
        Utils.setComboFromID(LookupCache.getBaseColourLookup(), "BaseColour",
            new Integer(Configuration.getInteger("AFDefaultColour")), cboColour);
        cboSize.setSelectedIndex(Configuration.getInteger("AFDefaultSize"));
        Utils.setComboFromID(LookupCache.getCoatTypeLookup(), "CoatType",
            new Integer(Configuration.getInteger("AFDefaultCoatType")),
            cboCoatType);

        // Default shelter code (if option set)
        if (Configuration.getBoolean("AutoDefaultShelterCode")) {
            try {
                generateAnimalCode(animal, (String) cboType.getSelectedItem());
            } catch (Exception e) {
                // If we fail to generate a code, don't stop the screen
                // opening, just silently fail and log the error
                Global.logException(e, getClass());
            }
        }

        // Flag as not for adoption (if option set)
        if (Configuration.getBoolean("AutoNotForAdoption")) {
            chkNotForAdoption.setSelected(true);
        }

        // Shouldn't be dirty until some data is entered
        setDirty(false);

        // Deactivate vaccinations, media and movements until they
        // have saved
        enableNonAnimalTabs(false);

        // Update the death panel
        updateDeath();

        // Set buttons
        enableButtons();
    }

    public void loadData() {
        loadData(true);
    }

    /** Reads the data in the animal object and fills the boxes */
    public void loadData(boolean loadExternalData) {
        try {
            // Add any custom buttons the user set
            if (!addedCustomButtons) {
                CustomUI.addCustomAnimalButtons(tlbTools,
                    animal.getID().intValue());
                addedCustomButtons = true;
            }

            this.txtComments.setText(Utils.nullToEmptyString(
                    animal.getAnimalComments()));
            this.txtHealthProblems.setText(Utils.nullToEmptyString(
                    animal.getHealthProblems()));
            this.txtHiddenAnimalComments.setText(Utils.nullToEmptyString(
                    animal.getHiddenAnimalDetails()));
            this.txtMarkings.setText(Utils.nullToEmptyString(
                    animal.getMarkings()));
            this.txtPTSReason.setText(Utils.nullToEmptyString(
                    animal.getPTSReason()));
            this.txtReasonForEntry.setText(Utils.nullToEmptyString(
                    animal.getReasonForEntry()));
            this.txtReasonNotBroughtByOwner.setText(Utils.nullToEmptyString(
                    animal.getReasonNO()));
            this.txtAcceptanceNumber.setText(Utils.nullToEmptyString(
                    animal.getAcceptanceNumber()));
            this.txtAnimalName.setText(Utils.nullToEmptyString(
                    animal.getAnimalName()));
            this.txtRabiesTag.setText(Utils.nullToEmptyString(
                    animal.getRabiesTag()));

            try {
                this.txtCombiTested.setText(Utils.formatDate(
                        animal.getCombiTestDate()));
                this.txtDateBroughtIn.setText(Utils.formatDate(
                        animal.getDateBroughtIn()));
                this.txtDateDeceased.setText(Utils.formatDate(
                        animal.getDeceasedDate()));
                this.txtNeutered.setText(Utils.formatDate(
                        animal.getNeuteredDate()));
                this.txtDateOfBirth.setText(Utils.formatDate(
                        animal.getDateOfBirth()));
                this.txtIdentichipDate.setText(Utils.formatDate(
                        animal.getIdentichipDate()));
                this.txtTattooDate.setText(Utils.formatDate(
                        animal.getTattooDate()));
                this.txtHeartwormTestDate.setText(Utils.formatDate(
                        animal.getHeartwormTestDate()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Flag the death tab if the animal is dead
            if (!txtDateDeceased.getText().equals("")) {
                tabTabs.setIconAt(TAB_DEATH,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_DEATH));
            }

            this.txtIdentichipNo.setText(Utils.nullToEmptyString(
                    animal.getIdentichipNumber()));
            this.txtTattooNumber.setText(Utils.nullToEmptyString(
                    animal.getTattooNumber()));
            this.txtShelterCode.setCode(Utils.nullToEmptyString(
                    animal.getShelterCode()));
            this.txtShelterCode.setShortCode(Utils.nullToEmptyString(
                    animal.getShortCode()));

            // Now we have to sort out the combo boxes and
            // select the correct item:
            Utils.setComboFromID(LookupCache.getBaseColourLookup(),
                "BaseColour", animal.getBaseColourID(), cboColour);
            Utils.setComboFromID(LookupCache.getCoatTypeLookup(), "CoatType",
                animal.getCoatType(), cboCoatType);
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                animal.getSpeciesID(), cboSpecies);

            // Now that species is selected, populate the breeds
            checkBreed();
            Utils.setComboFromID(LookupCache.getBreedLookup(), "BreedName",
                animal.getBreedID(), cboBreed);

            if (!Global.isSingleBreed()) {
                Utils.setComboFromID(LookupCache.getBreedLookup(), "BreedName",
                    animal.getBreed2ID(), cboBreed2);
                chkCrossBreed.setSelected(((Integer) animal.getCrossBreed()).intValue() == 1);
                cboBreed2.setEnabled(chkCrossBreed.isSelected());
            }

            Utils.setComboFromID(LookupCache.getAnimalTypeLookup(),
                "AnimalType", animal.getAnimalTypeID(), cboType);
            Utils.setComboFromID(LookupCache.getEntryReasonLookup(),
                "ReasonName", animal.getEntryReasonID(), cboEntryReason);
            Utils.setComboFromID(LookupCache.getDeathReasonLookup(),
                "ReasonName", animal.getPTSReasonID(), cboPTSReason);

            cboSex.setSelectedItem(LookupCache.getSexNameForID(animal.getSex()));
            cboSize.setSelectedItem(LookupCache.getSizeNameForID(
                    animal.getSize()));
            cboCombiTestResult.setSelectedIndex(animal.getCombiTestResult()
                                                      .intValue());
            cboHeartwormTestResult.setSelectedIndex(animal.getHeartwormTestResult()
                                                          .intValue());
            this.cboGoodCats.setSelectedIndex(animal.isGoodWithCats().intValue());
            this.cboGoodDogs.setSelectedIndex(animal.isGoodWithDogs().intValue());
            this.cboGoodKids.setSelectedIndex(animal.isGoodWithKids().intValue());
            this.cboHouseTrained.setSelectedIndex(animal.isHouseTrained()
                                                        .intValue());

            if (Locale.getDefault().equals(Locale.US)) {
                cboFLVTestResult.setSelectedIndex(animal.getFLVTestResult()
                                                        .intValue());
            }

            Integer y = new Integer(1);
            this.chkEstimatedDOB.setSelected(animal.getEstimatedDOB().equals(y));
            this.chkCombiTested.setSelected(animal.getCombiTested().equals(y));
            this.chkDOA.setSelected(animal.getIsDOA().equals(y));
            this.chkDiedOffShelter.setSelected(animal.getDiedOffShelter()
                                                     .equals(y));
            this.chkIdentichipped.setSelected(animal.getIdentichipped().equals(y));
            this.chkTattoo.setSelected(animal.getTattoo().equals(y));
            this.chkNeutered.setSelected(animal.getNeutered().equals(y));
            this.chkDeclawed.setSelected(animal.getDeclawed().equals(y));
            this.chkPTS.setSelected(animal.getPutToSleep().equals(y));
            this.chkTransferIn.setSelected(animal.getIsTransfer().equals(y));
            this.chkNonShelter.setSelected(animal.getNonShelterAnimal().equals(y));
            this.chkCrueltyCase.setSelected(animal.getCrueltyCase().equals(y));
            this.chkNotForAdoption.setSelected(animal.getIsNotAvailableForAdoption()
                                                     .equals(y));
            this.chkHeartwormTested.setSelected(animal.getHeartwormTested()
                                                      .equals(y));
            this.chkHasSpecialNeeds.setSelected(animal.isHasSpecialNeeds()
                                                      .equals(y));

            // Set icon for vet data if we have some
            try {
                if (animal.isHasSpecialNeeds().equals(y) ||
                        (animal.getCurrentVetID().intValue() > 0) ||
                        (animal.getOwnersVetID().intValue() > 0) ||
                        (Utils.nullToEmptyString(
                            (String) animal.getHealthProblems()).length() > 0) ||
                        (Utils.nullToEmptyString((String) animal.getRabiesTag())
                                  .length() > 0)) {
                    tabTabs.setIconAt(TAB_VET,
                        IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_VET));
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // sort out location
            checkLocation();

            // and age
            updateAge();
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        if (loadExternalData) {
            loadExternal();
        }
    }

    /**
     * Updates and loads the animal's internal location appropriately into the
     * list. Is called back from the AnimalMovement class.
     */
    public void checkLocation() {
        try {
            // Reload the internal location lookup, so this can be
            // called from anywhere
            SQLRecordset intloc = LookupCache.getInternalLocationLookup();
            intloc.moveFirst();
            cboLocation.removeAllItems();

            while (!intloc.getEOF()) {
                try {
                    cboLocation.addItem((String) intloc.getField("LocationName"));
                    intloc.moveNext();
                } catch (CursorEngineException e) {
                    Global.logException(e, getClass());

                    break;
                }
            }

            intloc = null;

            // If the animal is no longer on the shelter, add a new
            // value to the location listbox showing where the animal is,
            // setting the combo box to look at it and disabling it.
            // Only do this though if the configuration option
            // "ShowILOffShelter" is set.
            if (Configuration.getBoolean("ShowILOffShelter")) {
                // If the animal is off shelter, or the "foster as shelter" option is turned
                // on and the animal is fostered, then display the animal as off shelter
                if ((animal.getArchived().intValue() == 1) ||
                        (Configuration.getBoolean("FosterOnShelter") &&
                        ((animal.getActiveMovementType() != null) &&
                        (animal.getActiveMovementType().intValue() == Adoption.MOVETYPE_FOSTER)))) {
                    cboLocation.addItem(animal.getAnimalLocationAtDateByName(
                            new Date()));
                    cboLocation.setSelectedIndex(cboLocation.getItemCount() -
                        1);
                    cboLocation.setEnabled(false);
                    // Show the last known internal location
                    lblLocationText.setText(LookupCache.getInternalLocationName(
                            animal.getShelterLocation()));
                    showingLogicalLocation = true;
                } else {
                    showingLogicalLocation = false;
                    removeLastLocationField();
                    cboLocation.setEnabled(true);
                    Utils.setComboFromID(LookupCache.getInternalLocationLookup(),
                        "LocationName", animal.getShelterLocation(), cboLocation);
                }
            } else {
                // No need for it if we aren't showing logical anyway
                removeLastLocationField();
                cboLocation.setEnabled(true);
                Utils.setComboFromID(LookupCache.getInternalLocationLookup(),
                    "LocationName", animal.getShelterLocation(), cboLocation);
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void removeLastLocationField() {
        // Remove last known location entirely from form to
        // save space.
        pnlLeftFields.remove(lblLastLocation);
        pnlLeftFields.remove(lblLocationText);
        pnlLeftFields.invalidate();
    }

    public void showTitle() {
        try {
            // Outputs the title for the frame
            this.setTitle(i18n("edit_animal_title", animal.getShelterCode(),
                    animal.getAnimalName(), animal.calculateAgeGroup(),
                    animal.getSpeciesName(), animal.getAge()));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Retrieves the first available photo media record (or web preferred) and
     * uses it to draw a thumbnail image in the bottom left corner of the
     * details tab - Does it on a separate tab to not block the UI thread
     * while the form displays
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
            // Find the media for this animal and show it if there is one
            if (animal.hasValidMedia()) {
                String imagename = animal.getWebMedia();

                // Grab the remote file and copy it to the local temp directory
                // if it isn't already there.
                String tempdir = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                    File.separator;
                File f = new File(tempdir + imagename);

                if (!f.exists()) {
                    try {
                        DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                                animal.getID().intValue());
                        dbfs.readFile(imagename, tempdir + imagename);
                        dbfs = null;
                    } catch (Exception e) {
                        Global.logError("Error occurred retrieving image: " +
                            e.getMessage(), "EditAnimal.showThumbnailImpl");

                        return;
                    }
                }

                // Now display it in the thumbnailer
                try {
                    lblThumbnail.setIcon(IconManager.getThumbnail(tempdir +
                            imagename, 100, 50));
                    lblThumbnail.repaint();
                } catch (Exception e) {
                    Global.logError("Error occurred displaying thumbnail: " +
                        e.getMessage(), "EditAnimal.showThumbnailImpl");

                    return;
                }
            } else {
                // no image
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
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

            showTitle();
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        try {
            // Check satellite data to display icons
            Animal.AnimalMarkers ext = LookupCache.getAnimalNumExtRecs(animal.getID());

            // Vaccinations
            animalvaccinations.setLink(animal.getID().intValue(), 0);

            if (ext.vaccination > 0) {
                tabTabs.setIconAt(TAB_VACCINATION,
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITANIMAL_VACCINATION));
            }

            // Medicals
            medicals.setLink(animal.getID().intValue());

            if (ext.medical > 0) {
                tabTabs.setIconAt(TAB_MEDICAL,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_MEDICAL));
            }

            // Costs
            animalcosts.setLink(animal.getID().intValue(), 0);

            if (ext.costs > 0) {
                tabTabs.setIconAt(TAB_COSTS,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_COSTS));
            }

            // Diet
            animaldiets.setLink(animal.getID().intValue(), 0);

            if (ext.diet > 0) {
                tabTabs.setIconAt(TAB_DIET,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_DIET));
            }

            // Donations
            donations.setLink(animal.getID().intValue(), 0, 0);

            if (ext.donations > 0) {
                tabTabs.setIconAt(TAB_DONATIONS,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_DONATIONS));
            }

            // Media
            animalmedia.setLink(Media.LINKTYPE_ANIMAL, animal.getID().intValue());

            if (ext.media > 0) {
                tabTabs.setIconAt(TAB_MEDIA,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_MEDIA));
            }

            // Diary
            animaldiary.setLink(animal.getID().intValue(), Diary.LINKTYPE_ANIMAL);

            if (ext.diary > 0) {
                tabTabs.setIconAt(TAB_DIARY,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_DIARY));
            }

            // Adoptions
            animalmovement.setLink(0, animal.getID().intValue());

            if (ext.movement > 0) {
                tabTabs.setIconAt(TAB_MOVEMENT,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_MOVEMENT));
            }

            // If it has movements, it can't be a non-shelter animal
            if (ext.movement > 0) {
                chkNonShelter.setEnabled(false);
            }

            // Log
            log.setLink(animal.getID().intValue(), Log.LINKTYPE_ANIMAL);

            if (ext.log > 0) {
                tabTabs.setIconAt(TAB_LOG,
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_LOG));
            }

            // Additional
            try {
                additional.loadFields(animal.getID().intValue(),
                    AdditionalField.LINKTYPE_ANIMAL);
            } catch (CursorEngineException e) {
                Global.logException(e, getClass());
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Load external data in the background instead of lazy loading
        // Depending on options set
        if (!Configuration.getBoolean("LazyLoadAnimalTabs")) {
            new Thread() {
                    public void run() {
                        Global.logDebug("Loading satellite data in background thread",
                            "Animal.loadExternal");
                        loadAllTabs();
                    }
                }.start();
        }

        // Enable satellite tabs
        enableNonAnimalTabs(true);
        enableOnShelterTabs();
        enableButtons();
    }

    /**
     * Disables/Enables the non-animal record tabs
     *
     * @param b
     *            true to enable, false to disable
     */
    public void enableNonAnimalTabs(boolean b) {
        tabTabs.setEnabledAt(TAB_VACCINATION, b);
        tabTabs.setEnabledAt(TAB_MEDICAL, b);
        tabTabs.setEnabledAt(TAB_DIET, b);
        tabTabs.setEnabledAt(TAB_COSTS, b);
        tabTabs.setEnabledAt(TAB_DONATIONS, b);
        tabTabs.setEnabledAt(TAB_MEDIA, b);
        tabTabs.setEnabledAt(TAB_DIARY, b);
        tabTabs.setEnabledAt(TAB_MOVEMENT, b);
        tabTabs.setEnabledAt(TAB_LOG, b);

        if (additional.hasFields()) {
            tabTabs.setEnabledAt(TAB_ADDITIONAL, b);
        }

        // If animal tabs aren't enabled, then
        // neither should the match and doc buttons
        btnMatchLost.setEnabled(b);
        btnClone.setEnabled(b);
        btnDelete.setEnabled(b);
        btnWrite.setEnabled(b);
        btnPrint.setEnabled(b);

        // Litter buttons should be deactivated
        // entirely if no tabs, and then
        // judged accordingly when there are
        if (!b) {
            btnCreateLitter.setEnabled(false);
            btnViewLitterMates.setEnabled(false);
            btnFindLitter.setEnabled(false);
        } else {
            enableButtons();
        }

        setSecurity();
    }

    /**
     * Checks if the configuration option for this is set, and if it is, checks
     * to see if the animal has an outstanding vaccination. If the animal has no
     * outstanding vaccinations, the user is warned and asked if they wish to
     * close.
     *
     * @return true if it is ok for the form to close.
     */
    public boolean checkVaccinations() {
        AnimalVaccination vacc = new AnimalVaccination();

        try {
            // If the configuration option for this check is off, don't do it
            if (!Configuration.getBoolean("WarnNoPendingVacc")) {
                vacc = null;

                return true;
            }

            // Check vaccinations for main animal group
            if (!animal.getSpeciesID()
                           .equals(new Integer(Configuration.getInteger(
                                "AFDefaultSpecies")))) {
                vacc = null;

                return true;
            }

            // Look for vaccination records for this animal where they
            // have not been given a vaccination and the pending date is
            // after today.
            Calendar cal = Calendar.getInstance();
            vacc.openRecordset("AnimalID = " + animal.getID() +
                " AND DateOfVaccination Is Null And DateRequired > '" +
                Utils.getSQLDateOnly(cal) + "'");

            if (vacc.getEOF()) {
                vacc = null;

                return Dialog.showYesNoWarning(Global.i18n("uianimal",
                        "WARNING:_This_animal_has_no_pending_vaccinations_on_file._The_system_will_\nsave_the_record_anyway,_but_you_should_complete_vaccination_information_\nfor_this_animal._Are_you_sure_you_wish_to_close?"),
                    Global.i18n("uianimal", "Invalid_Vaccination_Details"));
            }
        } catch (NullPointerException e) {
            // We are still on a new record - ignore the error
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        vacc = null;

        return true;
    }

    public void litterIDChanged() {
        dataChanged();
        enableButtons();
    }

    /**
     * Checks the screen settings and disallows button functionality where
     * appropriate.
     */
    public void enableButtons() {
        // If the animal doesn't have a movement, disable the owner link
        // button
        try {
            btnOwner.setEnabled(animal.getActiveMovementDate() != null);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // If the animal is over 6 months, then it
        // can't be part of a litter anyway, so we may
        // as well disable all the litter functionality
        try {
            if (animal.isOverSixMonths()) {
                btnCreateLitter.setEnabled(false);
                btnViewLitterMates.setEnabled(false);
                btnFindLitter.setEnabled(false);

                return;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        /*
         * We need to identify 2 things:
         *
         * 1. Does it have a litter ID? 2. Is it valid and active?
         *
         * If it is valid, then viewing litter mates should be enabled, but
         * creating a new one and attaching to an existing one should be
         * disabled. The reverse is true if it is not part of an active litter.
         * Creating a litter is not valid if the animal has no manually entered
         * litter ID
         */
        boolean isLitter = (!txtAcceptanceNumber.getText().trim().equals(""));

        if (isLitter) {
            isLitter = AnimalLitter.isLitterActive(txtAcceptanceNumber.getText());
        }

        btnCreateLitter.setEnabled(!isLitter &&
            !txtAcceptanceNumber.getText().trim().equals(""));
        btnViewLitterMates.setEnabled(isLitter);
        btnFindLitter.setEnabled(!isLitter);

        // If this is a new animal, disable non-relevant buttons
        btnDelete.setEnabled(!isNewRecord);
        btnClone.setEnabled(!isNewRecord);
        btnWrite.setEnabled(!isNewRecord);
        btnCopyNotes.setEnabled(!isNewRecord);

        if (isNewRecord) {
            btnDiaryTask.setEnabled(false);
            btnOwner.setEnabled(false);
        }

        // Security overrides these things
        setSecurity();
    }

    /**
     * Monitors the Non-Shelter flag and disables appropriate areas of the
     * screen.
     */
    public void enableOnShelterTabs() {
        // If we have some movements, there's no point doing the checks
        // as it can never be an off-shelter animal
        if (animalmovement.hasData()) {
            return;
        }

        // Entry detail tabs
        txtReasonNotBroughtByOwner.setEnabled(!chkNonShelter.isSelected());
        txtReasonForEntry.setEnabled(!chkNonShelter.isSelected());
        cboEntryReason.setEnabled(!chkNonShelter.isSelected());
        chkTransferIn.setEnabled(!chkNonShelter.isSelected());
        txtDateBroughtIn.getTextField().setEnabled(!chkNonShelter.isSelected());
        embBroughtInBy.setVisible(!chkNonShelter.isSelected());

        // Disable movement tab for non-shelter
        tabTabs.setEnabledAt(TAB_MOVEMENT, !chkNonShelter.isSelected());
        cboLocation.setEnabled(!chkNonShelter.isSelected());

        // Disable cost tabs for non-shelter
        tabTabs.setEnabledAt(TAB_COSTS, !chkNonShelter.isSelected());

        // If it is off the shelter, then we should automatically
        // choose the non-shelter animal type for it and disable
        // it - assuming we have a non shelter animal type, if not, the
        // user can do what they want
        if (chkNonShelter.isSelected()) {
            int nsat = Configuration.getInteger("AFNonShelterType");

            if (nsat != 0) {
                Utils.setComboFromID(LookupCache.getAnimalTypeLookup(),
                    "AnimalType", new Integer(nsat), cboType);
                cboType.setEnabled(false);

                // If we aren't loading the screen right now, generate a new code
                // for the non-shelter animal - the box must have just been ticked
                if (!isLoading) {
                    generateAnimalCode(animal,
                        (String) cboType.getSelectedItem());
                }
            } else {
                cboType.setEnabled(true);
            }
        } else {
            cboType.setEnabled(true);
        }

        // If this is a non-shelter animal, then it MUST
        // die off the shelter - the flag should be set
        // and disabled
        if (chkNonShelter.isSelected()) {
            chkDiedOffShelter.setSelected(true);
            chkDiedOffShelter.setEnabled(false);
        } else {
            chkDiedOffShelter.setEnabled(true);
        }

        setSecurity();
    }

    /**
     * Called when the animal's species has changed - looks up what breeds
     * have been used with that species in the past and repopulates the
     * breed combo with most common choices first, then the full list
     */
    public void checkBreed() {
        try {
            // Clear the breed list
            cboBreed.removeAllItems();
            cboBreed2.removeAllItems();

            // Get the suggested list and add them
            if (Configuration.getBoolean("SuggestPopularBreeds")) {
                Integer speciesID = Utils.getIDFromCombo(LookupCache.getSpeciesLookup(),
                        "SpeciesName", cboSpecies);
                Vector v = LookupCache.getBreedsForSpecies(speciesID);

                for (int i = 0; i < v.size(); i++) {
                    cboBreed.addItem(v.get(i).toString());
                    cboBreed2.addItem(v.get(i).toString());
                }

                v = null;
            }

            // Add the full list according to selected species
            SQLRecordset breed = LookupCache.getBreedLookup();
            breed.moveFirst();

            if (Configuration.getBoolean("DontFilterBreedList")) {
                while (!breed.getEOF()) {
                    cboBreed.addItem(breed.getField("BreedName"));
                    cboBreed2.addItem(breed.getField("BreedName"));
                    breed.moveNext();
                }
            } else {
                Integer speciesID = Utils.getIDFromCombo(LookupCache.getSpeciesLookup(),
                        "SpeciesName", cboSpecies);

                while (!breed.getEOF()) {
                    Integer breedSpeciesID = (Integer) breed.getField(
                            "SpeciesID");

                    // Only include the breed if the species matches
                    if ((breedSpeciesID == null) ||
                            (breedSpeciesID.equals(speciesID))) {
                        cboBreed.addItem(breed.getField("BreedName"));
                        cboBreed2.addItem(breed.getField("BreedName"));
                    }

                    breed.moveNext();
                }
            }

            breed = null;
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        // Select the top item
        cboBreed.setSelectedIndex(0);
        cboBreed2.setSelectedIndex(0);
    }

    public void typeFocusLost() {
        // If we leave the type box and there's no code - 
        // default one.
        if (txtShelterCode.getCode().trim().equals("")) {
            checkType();
        }
    }

    /**
     * Called when the animal's type has changed. Uses appropriate logic to
     * generate an animal code and to question if a type has changed and the
     * animal has a code.
     */
    public void checkType() {
        // Wait half a second before checking the type so that the SwingWT
        // combo can settle and getSelectedItem() returns the new type
        // (this not an issue for Swing, but it doesn't do any harm)
        UI.invokeIn(new Runnable() {
                public void run() {
                    // Are we actually using type based codes?
                    if (Configuration.getString("CodingFormat").indexOf("T") == -1) {
                        // No, forget it
                        return;
                    }

                    // When did we last fire? if it was less than a second
                    // ago, forget it.
                    if (lastTypeCheck > (System.currentTimeMillis() - 1000)) {
                        return;
                    }

                    lastTypeCheck = System.currentTimeMillis();

                    // Get the type
                    String selectedtype = (String) cboType.getSelectedItem();

                    // If the code is blank, generate a new one
                    if (txtShelterCode.getCode().trim().equals("")) {
                        try {
                            generateAnimalCode(animal, selectedtype);
                        } catch (Exception e) {
                            // Fail quietly if we couldn't generate a code
                            Global.logException(e, getClass());
                        }

                        return;
                    }

                    // We always have to change the code now as it can cause
                    // duplicates and other problems

                    // See if the code is different to the type now selected
                    if (!Animal.getShelterCodeType(txtShelterCode.getCode())
                                   .equalsIgnoreCase(selectedtype.substring(0, 1))) {
                        // It's different - ask the user if they want to change it.
                        // Wait .5 of a second (for the combo to close/screen redraw)
                        // and
                        // then pop up the dialog asking if they want to change
                        UI.invokeIn(new Runnable() {
                                public void run() {
                                    String selectedtype = (String) cboType.getSelectedItem();

                                    Dialog.showInformation(i18n("You_have_changed_this_animals_type_generate_a_new_one",
                                            selectedtype),
                                        i18n("Generate_New_Code"));
                                    generateAnimalCode(animal, selectedtype);
                                }
                            }, 500);
                    }
                }
            }, 500);
    }

    /**
     * Called when the animal's brought in date has changed
     */
    public void checkDateBroughtIn() {
        // It's a change
        dataChanged();

        // Have we done one of these very recently? Like in the last 5 seconds?
        if ((System.currentTimeMillis() - lastCheckedDate) <= 5000) {
            return;
        }

        lastCheckedDate = System.currentTimeMillis();

        // Are we actually using the year in codes?
        if ((Configuration.getString("CodingFormat").indexOf("Y") == -1) &&
                (Configuration.getString("ShortCodingFormat").indexOf("Y") == -1)) {
            // No, forget it
            return;
        }

        // Get the type
        String selectedtype = (String) cboType.getSelectedItem();

        // If the code is blank, generate a new one
        if (txtShelterCode.getCode().trim().equals("")) {
            generateAnimalCode(animal, selectedtype);

            return;
        }

        // Unlike checkType() we always prompt for the date, because
        // an incorrect date can end up generating duplicate codes

        // See if the year in the code is different to the date now selected
        Date broughtIn = null;

        try {
            broughtIn = txtDateBroughtIn.getDate();
        } catch (ParseException e) {
            // Failed to parse the brought in date, bail out
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(broughtIn);

        int selectedyear = c.get(Calendar.YEAR);
        int codeyear = Animal.parseAnimalCodeYear(txtShelterCode.getCode());

        if (selectedyear != codeyear) {
            Dialog.showInformation(i18n("You_have_changed_the_brought_in_date_making_the_code_invalid_ASM_will_generate_a_new_one",
                    txtShelterCode.getCode()), i18n("Generate_New_Code"));
            generateAnimalCode(animal, selectedtype);
        }
    }

    /**
     * Call with an animal type and this will generate the next code
     * and shortcode in the sequence for the animal
     *
     * @param selectedtype
     *            The name of the animal type to generate for
     * @return The new animal code.
     */
    public void generateAnimalCode(Animal a, String selectedtype) {
        Date dbin = new Date();

        if (!txtDateBroughtIn.getText().equals("")) {
            try {
                dbin = Utils.parseDate(txtDateBroughtIn.getText());
            } catch (ParseException e) {
            }
        }

        try {
            // Special case - if the code is using unique values,
            // then we only need to calculate it the first time and we
            // can keep reusing it after that since it's unique
            AnimalCode ac = null;

            if ((Configuration.getString("CodingFormat").indexOf("U") != -1) &&
                    (Configuration.getString("CodingFormat").indexOf("N") == -1) &&
                    (Configuration.getString("ShortCodingFormat").indexOf("N") == -1)) {
                Global.logDebug("Coding format is unique only, reusing existing where possible.",
                    "AnimalEdit.generateAnimalCode");

                if ((a.getUniqueCodeID() != null) &&
                        !a.getUniqueCodeID().equals(new Integer(0))) {
                    Global.logDebug("Got existing unique code: " +
                        a.getUniqueCodeID() + " - reusing",
                        "AnimalEdit.generateAnimalCode");
                    ac = Animal.generateAnimalCode(selectedtype, dbin, 0,
                            a.getUniqueCodeID().intValue());
                } else {
                    Global.logDebug("No existing unique code found for this animal, generating...",
                        "AnimalEdit.generateAnimalCode");
                    // We don't have a unique code yet - generate it
                    ac = Animal.fastGenerateAnimalCode(selectedtype, dbin);
                }
            } else {
                // Not using unique codes, just generate as normal
                ac = Animal.fastGenerateAnimalCode(selectedtype, dbin);
            }

            // Update on-screen controls with the new code
            txtShelterCode.setCode(ac.code);
            txtShelterCode.setShortCode(ac.shortcode);
            a.setShelterCode(ac.code);
            a.setShortCode(ac.shortcode);

            // Reparse the code and store the numeric portion in the
            // id fields for later quick calculations
            int yearid = Animal.parseAnimalCode(txtShelterCode.getCode());
            int uniqueid = yearid;

            // Reset whichever type we aren't using to zero
            if (Configuration.getString("CodingFormat").indexOf("U") != -1) {
                yearid = 0;
            } else {
                uniqueid = 0;
            }

            animal.setYearCodeID(new Integer(yearid));
            animal.setUniqueCodeID(new Integer(uniqueid));

            // Update the title for existing records
            if (!isNewRecord) {
                showTitle();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        if (!isDirty) {
            return false;
        }

        if (!Global.currentUserObject.getSecChangeAnimal()) {
            Dialog.showError(UI.messageNoSavePermission());

            return false;
        }

        try {
            animal.setBroughtInByOwnerID(new Integer(embBroughtInBy.getID()));
            animal.setOriginalOwnerID(new Integer(embOriginalOwner.getID()));
            animal.setOwnersVetID(new Integer(embVet.getID()));
            animal.setCurrentVetID(new Integer(embCurrentVet.getID()));
            animal.setAnimalComments(txtComments.getText());
            animal.setHealthProblems(txtHealthProblems.getText());
            animal.setHiddenAnimalDetails(txtHiddenAnimalComments.getText());
            animal.setMarkings(txtMarkings.getText());
            animal.setPTSReason(txtPTSReason.getText());
            animal.setReasonForEntry(txtReasonForEntry.getText());
            animal.setReasonNO(txtReasonNotBroughtByOwner.getText());
            animal.setAcceptanceNumber(txtAcceptanceNumber.getText());
            animal.setAnimalName(txtAnimalName.getText());
            animal.setRabiesTag(txtRabiesTag.getText());

            if (cboCombiTestResult.getSelectedIndex() >= 0) {
                animal.setCombiTestResult(new Integer(
                        cboCombiTestResult.getSelectedIndex()));
            } else {
                animal.setCombiTestResult(new Integer(0));
            }

            if (cboHeartwormTestResult.getSelectedIndex() >= 0) {
                animal.setHeartwormTestResult(new Integer(
                        cboHeartwormTestResult.getSelectedIndex()));
            } else {
                animal.setHeartwormTestResult(new Integer(0));
            }

            if (Locale.getDefault().equals(Locale.US)) {
                if (cboFLVTestResult.getSelectedIndex() >= 0) {
                    animal.setFLVTestResult(new Integer(
                            cboFLVTestResult.getSelectedIndex()));
                } else {
                    animal.setFLVTestResult(new Integer(0));
                }
            } else {
                animal.setFLVTestResult(new Integer(0));
            }

            try {
                animal.setCombiTestDate(Utils.parseDate(
                        txtCombiTested.getText()));
                animal.setDateBroughtIn(Utils.parseDate(
                        txtDateBroughtIn.getText()));
                animal.setDeceasedDate(Utils.parseDate(
                        txtDateDeceased.getText()));
                animal.setNeuteredDate(Utils.parseDate(txtNeutered.getText()));
                animal.setDateOfBirth(Utils.parseDate(txtDateOfBirth.getText()));
                animal.setIdentichipDate(Utils.parseDate(
                        txtIdentichipDate.getText()));
                animal.setTattooDate(Utils.parseDate(txtTattooDate.getText()));
                animal.setHeartwormTestDate(Utils.parseDate(
                        txtHeartwormTestDate.getText()));
            } catch (ParseException e) {
                Dialog.showError(Global.i18n("uianimal",
                        "A_date_you_entered_was_not_valid:_") + e.getMessage(),
                    Global.i18n("uianimal", "Invalid_Date"));
                Global.logException(e, getClass());
            }

            animal.setIdentichipNumber(txtIdentichipNo.getText());
            animal.setTattooNumber(txtTattooNumber.getText());
            animal.setShelterCode(txtShelterCode.getCode());
            animal.setShortCode(txtShelterCode.getShortCode());

            // Reparse the code and store the numeric portion in the
            // id fields for later quick calculations
            int yearid = Animal.parseAnimalCode(txtShelterCode.getCode());
            int uniqueid = yearid;

            // Reset whichever type we aren't using to zero
            if (Configuration.getString("CodingFormat").indexOf("UUU") != -1) {
                yearid = 0;
            } else {
                uniqueid = 0;
            }

            animal.setYearCodeID(new Integer(yearid));
            animal.setUniqueCodeID(new Integer(uniqueid));

            // Now we have to sort out the combo boxes and
            // get the correct IDs.
            animal.setBreedID(Utils.getIDFromCombo(
                    LookupCache.getBreedLookup(), "BreedName", cboBreed));
            animal.setBaseColourID(Utils.getIDFromCombo(
                    LookupCache.getBaseColourLookup(), "BaseColour", cboColour));
            animal.setCoatType(Utils.getIDFromCombo(
                    LookupCache.getCoatTypeLookup(), "CoatType", cboCoatType));
            animal.setPTSReasonID(Utils.getIDFromCombo(
                    LookupCache.getDeathReasonLookup(), "ReasonName",
                    cboPTSReason));
            animal.setEntryReasonID(Utils.getIDFromCombo(
                    LookupCache.getEntryReasonLookup(), "ReasonName",
                    cboEntryReason));

            // Second breed/crossbreed
            if (Global.isSingleBreed()) {
                // This is for a single breed field - copy info to the
                // second breed field, duplicating it
                animal.setBreed2ID(animal.getBreedID());
                animal.setBreedName(LookupCache.getBreedName(
                        animal.getBreedID()));
                animal.setCrossBreed(new Integer(0));
            } else {
                // If cross was selected, store the crossbreed info
                animal.setCrossBreed(chkCrossBreed.isSelected()
                    ? new Integer(1) : new Integer(0));

                if (chkCrossBreed.isSelected()) {
                    animal.setBreed2ID(Utils.getIDFromCombo(
                            LookupCache.getBreedLookup(), "BreedName", cboBreed2));
                    animal.setBreedName(LookupCache.getBreedName(
                            animal.getBreedID()) + " / " +
                        LookupCache.getBreedName(animal.getBreed2ID()));
                } else {
                    // Cross wasn't selected, duplicate primary breed
                    animal.setBreed2ID(animal.getBreedID());
                    animal.setBreedName(LookupCache.getBreedName(
                            animal.getBreedID()));
                }
            }

            // Only store internal location if a valid one is currently selected
            // (ie. Not logical location)
            if (!showingLogicalLocation) {
                animal.setShelterLocation(Utils.getIDFromCombo(
                        LookupCache.getInternalLocationLookup(),
                        "LocationName", cboLocation));
            }

            animal.setSpeciesID(Utils.getIDFromCombo(
                    LookupCache.getSpeciesLookup(), "SpeciesName", cboSpecies));
            animal.setAnimalTypeID(Utils.getIDFromCombo(
                    LookupCache.getAnimalTypeLookup(), "AnimalType", cboType));
            animal.setSex(LookupCache.getSexIDForName(
                    (String) cboSex.getSelectedItem()));
            animal.setSize(LookupCache.getSizeIDForName(
                    (String) cboSize.getSelectedItem()));

            // Check boxes
            Integer y = new Integer(1);
            Integer n = new Integer(0);

            if (chkEstimatedDOB.isSelected()) {
                animal.setEstimatedDOB(y);
            } else {
                animal.setEstimatedDOB(n);
            }

            if (chkCombiTested.isSelected()) {
                animal.setCombiTested(y);
            } else {
                animal.setCombiTested(n);
            }

            if (chkDOA.isSelected()) {
                animal.setIsDOA(y);
            } else {
                animal.setIsDOA(n);
            }

            if (chkIdentichipped.isSelected()) {
                animal.setIdentichipped(y);
            } else {
                animal.setIdentichipped(n);
            }

            if (chkTattoo.isSelected()) {
                animal.setTattoo(y);
            } else {
                animal.setTattoo(n);
            }

            if (chkNeutered.isSelected()) {
                animal.setNeutered(y);
            } else {
                animal.setNeutered(n);
            }

            if (chkDeclawed.isSelected()) {
                animal.setDeclawed(y);
            } else {
                animal.setDeclawed(n);
            }

            if (chkPTS.isSelected()) {
                animal.setPutToSleep(y);
            } else {
                animal.setPutToSleep(n);
            }

            if (chkTransferIn.isSelected()) {
                animal.setIsTransfer(y);
            } else {
                animal.setIsTransfer(n);
            }

            if (chkDiedOffShelter.isSelected()) {
                animal.setDiedOffShelter(y);
            } else {
                animal.setDiedOffShelter(n);
            }

            if (chkNonShelter.isSelected()) {
                animal.setNonShelterAnimal(y);
            } else {
                animal.setNonShelterAnimal(n);
            }

            if (chkCrueltyCase.isSelected()) {
                animal.setCrueltyCase(y);
            } else {
                animal.setCrueltyCase(n);
            }

            if (chkNotForAdoption.isSelected()) {
                animal.setIsNotAvailableForAdoption(y);
            } else {
                animal.setIsNotAvailableForAdoption(n);
            }

            if (chkHasSpecialNeeds.isSelected()) {
                animal.setHasSpecialNeeds(y);
            } else {
                animal.setHasSpecialNeeds(n);
            }

            animal.setGoodWithCats(new Integer(cboGoodCats.getSelectedIndex()));
            animal.setGoodWithDogs(new Integer(cboGoodDogs.getSelectedIndex()));
            animal.setGoodWithKids(new Integer(cboGoodKids.getSelectedIndex()));
            animal.setHouseTrained(new Integer(
                    cboHouseTrained.getSelectedIndex()));

            if (chkHeartwormTested.isSelected()) {
                animal.setHeartwormTested(y);
            } else {
                animal.setHeartwormTested(n);
            }

            // Defaults before updating
            animal.setArchived(n);
            animal.setActiveMovementID(n);
            animal.setHasActiveReserve(n);
        } catch (CursorEngineException e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Error_saving_to_local_SQLRecordset:_") + e.getMessage(),
                Global.i18n("uianimal", "Save_Error"));
            Global.logException(e, getClass());

            return false;
        }

        try {
            animal.save(Global.currentUserName);

            // If it wasn't a new record, save the boarding cost
            if (!isNewRecord) {
                try {
                    animalcosts.saveBoardingCost();
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }
            }

            // If it wasn't a new record, try and save the additional fields
            if (!isNewRecord) {
                additional.saveFields(animal.getID().intValue(),
                    AdditionalField.LINKTYPE_ANIMAL);
            }

            // Update denormalised data
            Animal.updateAnimalStatus(animal.getID().intValue());

            // Allow editing of satellite data if this was a new record
            if (isNewRecord) {
                loadExternal();
                isNewRecord = false;
            }

            // Shelter codes can change during a save if enforcing
            // of strict check is on - redisplay the code
            txtShelterCode.setCode(animal.getShelterCode());
            txtShelterCode.setShortCode(animal.getShortCode());

            // Update the frame title where necessary
            showTitle();

            // Disable the save button
            setDirty(false);
            txtAnimalName.getTextField().grabFocus();
            enableButtons();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage(),
                Global.i18n("uianimal", "Validation_Error"));
        }

        return false;
    }

    /** Clones the current animal and opens a new screen on it */
    public void cloneAnimal() {
        // Is user sure?
        if (!Dialog.showYesNoWarning(Global.i18n("uianimal",
                        "are_you_sure_you_want_to_clone_this_animal"),
                    Global.i18n("uianimal", "sure"))) {
            return;
        }

        // Make sure our visual controls have been saved
        saveData();

        try {
            Animal newanimal = animal.copy();

            // Open a new edit animal screen on our cloned animal
            AnimalEdit ea = new AnimalEdit();
            ea.openForEdit(newanimal);
            Global.mainForm.addChild(ea);

            newanimal = null;
            ea = null;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /** Notifies the form that the data has been changed. */
    public void dataChanged() {
        if (!isLoading) {
            setDirty(true);
        }
    }

    /** Runs when the crossbreed checkbox is toggled and enables the
        secondary breed field for crossbreeds */
    public void toggleSecondBreed() {
        cboBreed2.setEnabled(chkCrossBreed.isSelected());
        dataChanged();
    }

    /**
     * Checks if the DOA, DOS or PTS boxes have been ticked, but the date
     * deceased is blank. If so, it fills in todays date.
     */
    public void fillDateDeceased() {
        if ((chkPTS.isSelected() || chkDOA.isSelected() ||
                chkDiedOffShelter.isSelected()) &&
                txtDateDeceased.getText().equals("")) {
            try {
                txtDateDeceased.setText(Utils.formatDate(Calendar.getInstance()));
            } catch (Exception e) {
            }
        }
    }

    /**
     * This Routine is called whenever something attempts to close the form to
     * verify if it should be allowed to close, and whether or not to show any
     * warning messages for unsaved changes etc.
     */
    public boolean formClosing() {
        // Don't destroy if the user has unsaved changes and are not sure
        if (this.isDirty) {
            if (!Dialog.showYesNoWarning(Global.i18n("uianimal",
                            "You_have_unsaved_changes_-_are_you_sure_you_wish_to_close?"),
                        Global.i18n("uianimal", "Unsaved_Changes"))) {
                return true;
            }
        }

        // Don't destroy if the vaccination check returns false
        if (!checkVaccinations()) {
            return true;
        }

        return false;
    }

    /**
     * Updates the diary list
     */
    public void updateDiary() {
        animaldiary.updateList();
    }

    public void initComponents() {
        Vector testresults = new Vector();
        testresults.add(Global.i18n("uiwordprocessor", "Unknown"));
        testresults.add(Global.i18n("uiwordprocessor", "Negative"));
        testresults.add(Global.i18n("uiwordprocessor", "Positive"));

        Vector yesnounknown = new Vector();
        yesnounknown.add(i18n("Yes"));
        yesnounknown.add(i18n("No"));
        yesnounknown.add(i18n("Unknown"));

        // Toolbar =============================
        tlbTools = new UI.ToolBar();

        btnSave = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Save_this_record"), 's',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_SAVE),
                    UI.fp(this, "saveData")));

        btnClone = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Create_a_new_animal_by_copying_this_one"), 'c',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_CLONE),
                    UI.fp(this, "cloneAnimal")));

        btnDelete = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Delete_this_animal"), 'z',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_DELETE),
                    UI.fp(this, "actionDelete")));

        btnPrint = (UI.Button) tlbTools.add(UI.getButton(null, i18n("Print"),
                    'p',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_PRINT),
                    UI.fp(this, "actionPrint")));

        btnWrite = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Generate_an_animal_letter/form"), 'w',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITANIMAL_GENERATEDOC),
                    UI.fp(this, "actionDocument")));

        btnDiaryTask = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Generate_a_diary_task_for_this_animal"), 't',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_DIARYTASK),
                    UI.fp(this, "actionDiaryTask")));

        btnOwner = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("view_the_current_owner_for_this_animal"), 'o',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_VIEWOWNER),
                    UI.fp(this, "actionViewOwner")));

        btnMatchLost = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("look_in_lost_animal_database"), 'm',
                    IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_LOSTFOUND),
                    UI.fp(this, "actionMatchLost")));

        btnCreateLitter = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("Create_a_litter_from_this_animal"), 'u',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITANIMAL_CREATELITTER),
                    UI.fp(this, "actionCreateLitter")));

        btnFindLitter = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("find_the_litter_this_animal_belongs_to"), 'f',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITANIMAL_FINDLITTER),
                    UI.fp(this, "actionFindLitter")));

        btnViewLitterMates = (UI.Button) tlbTools.add(UI.getButton(null,
                    i18n("find_this_animals_littermates"), 'l',
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITANIMAL_VIEWLITTERMATES),
                    UI.fp(this, "actionViewLitterMates")));

        add(tlbTools, UI.BorderLayout.NORTH);

        // Details panel ===========================================
        pnlDetails = UI.getPanel(UI.getGridLayout(2, new int[] { 45, 55 }));

        // Details left pane ========================================
        UI.Panel pnlLeft = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlLeftTop = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlThumbnail = UI.getPanel(UI.getGridLayout(4));
        pnlLeftFields = UI.getPanel(UI.getGridLayout(2, new int[] { 40, 60 }));

        // Thumbnail panel
        lblThumbnail = UI.getLabel();
        lblThumbnail.setPreferredSize(UI.getDimension(100, 50));
        pnlThumbnail.add(lblThumbnail);

        chkNonShelter = (UI.CheckBox) pnlThumbnail.add(UI.getCheckBox(i18n("Non-Shelter:"),
                    i18n("check_this_box_if_this_animal_is_not_a_shelter_animal"),
                    UI.fp(this, "nonShelterChanged")));
        chkNonShelter.setForeground(UI.getColor(255, 0, 0));

        chkNotForAdoption = (UI.CheckBox) UI.addComponent(pnlThumbnail,
                UI.getCheckBox(i18n("Not_for_adoption"),
                    i18n("tick_this_box_if_this_animal_is_not_available_for_adoption"),
                    UI.fp(this, "dataChanged")));

        chkCrueltyCase = (UI.CheckBox) UI.addComponent(pnlThumbnail,
                UI.getCheckBox(i18n("Cruelty_case"),
                    i18n("tick_this_box_if_this_animal_is_a_cruelty_case"),
                    UI.fp(this, "dataChanged")));

        // Fields
        txtShelterCode = new AnimalCodeField(UI.fp(this, "genCode"),
                UI.fp(this, "dataChanged"), i18n("generate_a_new_animal_code"),
                i18n("The_animal's_unique_shelter_code"),
                i18n("A_short_version_of_the_code"));
        UI.addComponent(pnlLeftFields, i18n("Code:"), txtShelterCode);

        // Switch the Acceptance Number field to litter ID if
        // automatic litter ID is on
        String littertext = Configuration.getBoolean("AutoLitterIdentification")
            ? i18n("litter_id") : i18n("Acceptance_No:");
        String littertooltip = Configuration.getBoolean(
                "AutoLitterIdentification")
            ? i18n("the_litter_identifier_if_this_animal_is_part_of_a_litter")
            : i18n("The_animal_acceptance_number_from_head_office");

        txtAcceptanceNumber = (UI.TextField) UI.addComponent(pnlLeftFields,
                littertext,
                UI.getTextField(littertooltip, UI.fp(this, "litterIDChanged")));
        txtAcceptanceNumber.setForeground(UI.getColor(0, 204, 51));

        /*
        txtAnimalName = (UI.TextField) UI.addComponent(pnlLeftFields,
                i18n("Name:"),
                UI.getTextField(i18n("The_animal's_name"),
                    UI.fp(this, "dataChanged")));
        */
        txtAnimalName = (UI.SearchTextField) UI.addComponent(pnlLeftFields,
                i18n("Name:"),
                UI.getSearchTextField(i18n("The_animal's_name"),
                    i18n("Generate_a_random_name_for_this_animal"), true,
                    IconManager.getIcon(
                        IconManager.SCREEN_EDITANIMAL_RANDOMNAME_SMALL),
                    UI.fp(this, "actionRandomName"), UI.fp(this, "dataChanged")));

        cboSex = (UI.ComboBox) UI.addComponent(pnlLeftFields, i18n("Sex:"),
                UI.getCombo(LookupCache.getSexLookup(), "Sex",
                    UI.fp(this, "dataChanged")));
        cboSex.setToolTipText(Global.i18n("uianimal", "The_animal's_sex"));

        cboType = (UI.ComboBox) UI.addComponent(pnlLeftFields, i18n("Type:"),
                UI.getCombo(i18n("Type:"), LookupCache.getAnimalTypeLookup(),
                    "AnimalType", UI.fp(this, "typeChanged"),
                    UI.fp(this, "typeFocusLost")));
        cboType.setToolTipText(i18n("The_animal_type"));

        cboColour = (UI.ComboBox) UI.addComponent(pnlLeftFields,
                i18n("Base_Colour:"),
                UI.getCombo(i18n("Base_Colour:"),
                    LookupCache.getBaseColourLookup(), "BaseColour",
                    UI.fp(this, "dataChanged")));
        cboColour.setToolTipText(i18n("The_animal's_basic_colour"));

        cboCoatType = UI.getCombo(i18n("Coat_Type"),
                LookupCache.getCoatTypeLookup(), "CoatType",
                UI.fp(this, "dataChanged"));
        cboCoatType.setToolTipText(i18n("The_animals_coat_type"));

        if (!Configuration.getBoolean("DontShowCoatType")) {
            UI.addComponent(pnlLeftFields, i18n("Coat_Type"), cboCoatType);
        }

        cboSize = (UI.ComboBox) UI.addComponent(pnlLeftFields, i18n("Size:"),
                UI.getCombo(LookupCache.getSizeLookup(), "Size",
                    UI.fp(this, "dataChanged")));
        cboSize.setToolTipText(Global.i18n("uianimal", "The_animal's_size"));

        cboSpecies = (UI.ComboBox) UI.addComponent(pnlLeftFields,
                i18n("Species:"),
                UI.getCombo(i18n("Species:"), LookupCache.getSpeciesLookup(),
                    "SpeciesName", UI.fp(this, "speciesChanged")));
        cboSpecies.setToolTipText(Global.i18n("uianimal", "The_animal's_species"));

        cboBreed = (UI.ComboBox) UI.addComponent(pnlLeftFields, i18n("Breed:"),
                UI.getCombo(i18n("Breed:"), LookupCache.getBreedLookup(),
                    "BreedName", UI.fp(this, "dataChanged")));
        cboBreed.setToolTipText(Global.i18n("uianimal", "The_animal's_breed"));

        UI.Panel pnlCross = UI.getPanel(UI.getBorderLayout(), true);

        chkCrossBreed = UI.getCheckBox(i18n("Crossbreed"),
                i18n("tick_this_box_if_this_animal_is_a_crossbreed"),
                UI.fp(this, "toggleSecondBreed"));

        cboBreed2 = UI.getCombo(i18n("Breed:"), LookupCache.getBreedLookup(),
                "BreedName", UI.fp(this, "dataChanged"));
        cboBreed2.setEnabled(false);
        cboBreed2.setToolTipText(Global.i18n("uianimal",
                "If_the_animal_is_a_cross_the_second_breed"));

        pnlCross.add(chkCrossBreed, UI.BorderLayout.WEST);
        pnlCross.add(cboBreed2);

        if (!Global.isSingleBreed()) {
            pnlLeftFields.add(UI.getLabel());
            pnlLeftFields.add(pnlCross);
        }

        cboLocation = (UI.ComboBox) UI.addComponent(pnlLeftFields,
                i18n("Location:"),
                UI.getCombo(i18n("Location:"),
                    LookupCache.getInternalLocationLookup(), "LocationName",
                    UI.fp(this, "dataChanged")));
        cboLocation.setToolTipText(Global.i18n("uianimal",
                "Where_the_animal_is_currently_located_on_the_shelter"));

        lblLocationText = UI.getLabel();
        lblLastLocation = UI.getLabel(i18n("Last_Location"));
        pnlLeftFields.add(lblLastLocation);
        pnlLeftFields.add(lblLocationText);

        txtDateOfBirth = (DateField) UI.getDateField(i18n("The_animal's_date_of_birth"),
                UI.fp(this, "dataChanged"), UI.fp(this, "updateAge"));

        chkEstimatedDOB = (UI.CheckBox) UI.getCheckBox(i18n("Estimate"),
                i18n("is_this_date_of_birth_an_estimate"),
                UI.fp(this, "dataChanged"));

        UI.Panel pdob = UI.getPanel(UI.getGridLayout(2, new int[] { 60, 40 }),
                true);
        pdob.add(txtDateOfBirth);
        pdob.add(chkEstimatedDOB);

        UI.addComponent(pnlLeftFields, i18n("Date_Of_Birth:"), pdob);

        // Lay out the left side
        pnlLeftTop.add(pnlThumbnail, UI.BorderLayout.NORTH);
        pnlLeftTop.add(pnlLeftFields, UI.BorderLayout.CENTER);
        pnlLeft.add(pnlLeftTop, UI.BorderLayout.NORTH);
        //pnlLeft.add(pnlMarkings, UI.BorderLayout.CENTER);
        pnlDetails.add(pnlLeft);

        // Details right pane =========================================
        UI.Panel pnlRight = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlRightTop = UI.getPanel(UI.getGridLayout(3));
        UI.Panel pnlRightBot = UI.getPanel(UI.getGridLayout(4));

        // Top panel

        // Row 1
        chkIdentichipped = (UI.CheckBox) UI.addComponent(pnlRightTop,
                UI.getCheckBox(i18n("Identichipped:"),
                    i18n("the_date_the_animal_was_identichipped"),
                    UI.fp(this, "dataChanged")));

        txtIdentichipDate = (DateField) pnlRightTop.add(UI.getDateField(i18n("the_date_the_animal_was_identichipped"),
                    UI.fp(this, "dataChanged")));

        txtIdentichipNo = (UI.TextField) pnlRightTop.add(UI.getTextField(i18n("The_Identichip_Number"),
                    UI.fp(this, "dataChanged")));

        // Row 2
        chkTattoo = (UI.CheckBox) UI.addComponent(pnlRightTop,
                UI.getCheckBox(i18n("Tattoo:"), null, UI.fp(this, "dataChanged")));

        txtTattooDate = (DateField) pnlRightTop.add(UI.getDateField(i18n("the_date_the_animal_had_the_tattoo"),
                    UI.fp(this, "dataChanged")));

        txtTattooNumber = (UI.TextField) pnlRightTop.add(UI.getTextField(i18n("The_Tattoo_Number"),
                    UI.fp(this, "dataChanged")));

        // Row 3
        chkNeutered = (UI.CheckBox) UI.addComponent(pnlRightTop,
                UI.getCheckBox(i18n("Neutered:"), null,
                    UI.fp(this, "dataChanged")));

        txtNeutered = (DateField) pnlRightTop.add(UI.getDateField(i18n("The_date_the_animal_was_neutered_if_known"),
                    UI.fp(this, "dataChanged")));

        chkDeclawed = (UI.CheckBox) UI.addComponent(pnlRightTop,
                UI.getCheckBox(i18n("Declawed"),
                    i18n("tick_this_box_if_the_animal_has_been_declawed"),
                    UI.fp(this, "dataChanged")));

        // Row 4
        chkHeartwormTested = (UI.CheckBox) UI.addComponent(pnlRightTop,
                UI.getCheckBox(i18n("Heartworm_Tested:"), null,
                    UI.fp(this, "dataChanged")));

        txtHeartwormTestDate = (DateField) pnlRightTop.add(UI.getDateField(i18n("the_date_the_animal_was_last_heartworm_tested"),
                    UI.fp(this, "dataChanged")));

        cboHeartwormTestResult = (UI.ComboBox) pnlRightTop.add(UI.getCombo(
                    testresults, UI.fp(this, "dataChanged")));

        // Row 5
        chkCombiTested = (UI.CheckBox) UI.addComponent(pnlRightTop,
                UI.getCheckBox(i18n("Combi-Tested:"), null,
                    UI.fp(this, "dataChanged")));

        txtCombiTested = (DateField) pnlRightTop.add(UI.getDateField(i18n("The_date_the_animal_was_combi-tested_if_known"),
                    UI.fp(this, "dataChanged")));

        // For the US, we drop the FIV Result to a separate line and label it
        // to avoid confusion since they can have FIV and FLV results
        if (Locale.getDefault().equals(Locale.US)) {
            pnlRightTop.add(UI.getLabel());
            pnlRightTop.add(UI.getLabel(i18n("FIVResult")));
            pnlRightTop.add(UI.getLabel());
        }

        cboCombiTestResult = (UI.ComboBox) pnlRightTop.add(UI.getCombo(
                    testresults, UI.fp(this, "dataChanged")));

        // For the US, output a separate FLV Result field too
        if (Locale.getDefault().equals(Locale.US)) {
            pnlRightTop.add(UI.getLabel(i18n("FLVResult")));
            pnlRightTop.add(UI.getLabel());
            cboFLVTestResult = (UI.ComboBox) pnlRightTop.add(UI.getCombo(
                        testresults, UI.fp(this, "dataChanged")));
        }

        // Bottom panel

        // Row 6
        cboGoodCats = (UI.ComboBox) UI.addComponent(pnlRightBot,
                i18n("good_with_cats"),
                UI.getCombo(yesnounknown, UI.fp(this, "dataChanged")));
        cboGoodDogs = (UI.ComboBox) UI.addComponent(pnlRightBot,
                i18n("good_with_dogs"),
                UI.getCombo(yesnounknown, UI.fp(this, "dataChanged")));

        // Row 7
        cboGoodKids = (UI.ComboBox) UI.addComponent(pnlRightBot,
                i18n("good_with_kids"),
                UI.getCombo(yesnounknown, UI.fp(this, "dataChanged")));
        cboHouseTrained = (UI.ComboBox) UI.addComponent(pnlRightBot,
                i18n("housetrained"),
                UI.getCombo(yesnounknown, UI.fp(this, "dataChanged")));

        // Comments panel
        UI.Panel pnlComments = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 35, 65 }));

        txtMarkings = (UI.TextArea) UI.addComponent(pnlComments,
                i18n("Dist._Features:"),
                UI.getTextArea(i18n("Any_distinguishing_markings_or_features_the_animal_has"),
                    UI.fp(this, "dataChanged")));

        txtHiddenAnimalComments = (UI.TextArea) UI.addComponent(pnlComments,
                i18n("Hidden_Comments:"),
                UI.getTextArea(i18n("Animal_comments_you_do_not_want_to_appear_on_any_documents"),
                    UI.fp(this, "dataChanged")));

        // Animal comments is a panel with a button to update the 
        // media notes and the label
        UI.Panel pnlAnimalComments = UI.getPanel(UI.getFlowLayout(
                    UI.FlowLayout.RIGHT));
        txtComments = UI.getTextArea(i18n("Animal_comments_you_want_to_see_on_documents"),
                UI.fp(this, "dataChanged"));
        btnCopyNotes = UI.getButton(null,
                i18n("Copy_animal_comments_to_web_media_notes"), ' ',
                IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_COPYNOTES),
                UI.fp(this, "actionMediaNotes"));
        pnlAnimalComments.add(UI.getLabel(i18n("Comments:")));
        pnlAnimalComments.add(btnCopyNotes);
        UI.addComponent(pnlComments, pnlAnimalComments, txtComments);

        pnlRight.add(pnlRightTop, UI.BorderLayout.NORTH);
        pnlRight.add(pnlComments, UI.BorderLayout.CENTER);
        pnlRight.add(pnlRightBot, UI.BorderLayout.SOUTH);
        pnlDetails.add(pnlRight);

        // Entry Details pane ====================================================
        UI.Panel pnlEntry = UI.getPanel(UI.getGridLayout(2, new int[] { 40, 60 }));

        // Entry Details left pane =================================
        UI.Panel pnlEntryLeft = UI.getPanel(UI.getGridLayout(1));

        embBroughtInBy = new OwnerLink();
        embOriginalOwner = new OwnerLink();
        embVet = new OwnerLink(OwnerLink.MODE_FULL, OwnerLink.FILTER_VETS,
                "LINK");
        embCurrentVet = new OwnerLink(OwnerLink.MODE_FULL,
                OwnerLink.FILTER_VETS, "LINK");
        embBroughtInBy.setTitle(Global.i18n("uianimal", "Brought_In_By:"));
        embOriginalOwner.setTitle(Global.i18n("uianimal", "Original_Owner:"));
        embVet.setTitle(Global.i18n("uianimal", "Owners_Vet"));
        embCurrentVet.setTitle(Global.i18n("uianimal", "Current_Vet"));
        embBroughtInBy.setParent(this);
        embOriginalOwner.setParent(this);
        embVet.setParent(this);
        embCurrentVet.setParent(this);

        pnlEntryLeft.add(embOriginalOwner);
        pnlEntryLeft.add(embBroughtInBy);

        // Entry Details right pane ===================================
        UI.Panel pnlEntryRight = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlEntryRightReasons = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 35, 65 }));

        txtReasonNotBroughtByOwner = (UI.TextArea) UI.addComponent(pnlEntryRightReasons,
                i18n("Reason_not_by_owner"),
                UI.getTextArea(i18n("The_reason_the_animal_was_not_brought_by_its_owner"),
                    UI.fp(this, "dataChanged")));

        txtReasonForEntry = (UI.TextArea) UI.addComponent(pnlEntryRightReasons,
                i18n("Reason_for_Entry:"),
                UI.getTextArea(i18n("The_reason_the_animal_was_brought_to_the_shelter"),
                    UI.fp(this, "dataChanged")));

        pnlEntryRight.add(pnlEntryRightReasons, UI.BorderLayout.CENTER);

        UI.Panel pnlEntryDetails = UI.getPanel(UI.getGridLayout(2,
                    new int[] { 35, 65 }));

        cboEntryReason = (UI.ComboBox) UI.addComponent(pnlEntryDetails,
                i18n("Entry_Category"),
                UI.getCombo(i18n("Entry_Category"),
                    LookupCache.getEntryReasonLookup(), "ReasonName",
                    UI.fp(this, "dataChanged")));

        pnlEntryDetails.add(UI.getLabel());
        chkTransferIn = (UI.CheckBox) UI.addComponent(pnlEntryDetails,
                UI.getCheckBox(i18n("Transfer_In:"),
                    i18n("Tick_this_box_if_the_animal_was_transferred_from_another_shelter"),
                    UI.fp(this, "dataChanged")));

        txtDateBroughtIn = (DateField) UI.addComponent(pnlEntryDetails,
                i18n("Date_Brought_In:"),
                UI.getDateField(i18n("The_date_the_animal_was_brought_into_the_shelter"),
                    UI.fp(this, "checkDateBroughtIn"),
                    UI.fp(this, "checkDateBroughtIn")));

        pnlEntryRight.add(pnlEntryDetails, UI.BorderLayout.SOUTH);

        pnlEntry.add(pnlEntryLeft);
        pnlEntry.add(pnlEntryRight);

        // Vet pane =====================================================
        UI.Panel pnlVet = UI.getPanel(UI.getGridLayout(1));
        UI.Panel pnlHealthProblems = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlHealthDetails = UI.getPanel(UI.getFlowLayout());
        UI.Panel pnlVets = UI.getPanel(UI.getGridLayout(2));

        pnlHealthProblems.add(UI.getLabel(UI.ALIGN_LEFT,
                i18n("Health_Problems:")), UI.BorderLayout.NORTH);
        txtHealthProblems = (UI.TextArea) UI.addComponent(pnlHealthProblems,
                UI.getTextArea(i18n("Any_health_problems_the_animal_has"),
                    UI.fp(this, "dataChanged")));

        pnlHealthDetails.add(UI.getLabel(i18n("Rabies_Tag")));
        txtRabiesTag = (UI.TextField) pnlHealthDetails.add(UI.getTextField(i18n("the_animals_rabies_tag"),
                    UI.fp(this, "dataChanged")));

        chkHasSpecialNeeds = UI.getCheckBox(i18n("special_needs"),
                i18n("special_needs_tooltip"), UI.fp(this, "dataChanged"));
        pnlHealthDetails.add(chkHasSpecialNeeds);
        pnlHealthProblems.add(pnlHealthDetails, UI.BorderLayout.SOUTH);

        pnlVets.add(embCurrentVet);
        pnlVets.add(embVet);

        pnlVet.add(pnlHealthProblems);
        pnlVet.add(pnlVets);

        // Death pane ===================================================
        UI.Panel pnlDeath = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlPTS = UI.getPanel(UI.getGridLayout(3));

        txtDateDeceased = (DateField) UI.addComponent(pnlPTS,
                i18n("Date_Deceased:"),
                UI.getDateField(i18n("The_date_the_animal_died"),
                    UI.fp(this, "dataChanged")));
        txtDateDeceased.setDateChangedListener(this);

        chkDOA = (UI.CheckBox) UI.addComponent(pnlPTS,
                UI.getCheckBox(i18n("Dead_On_Arrival:"),
                    i18n("Tick_this_box_if_the_animal_was_dead_on_entry_to_the_shelter"),
                    UI.fp(this, "deathChanged")));
        pnlPTS.add(UI.getLabel());

        chkPTS = (UI.CheckBox) UI.addComponent(pnlPTS,
                UI.getCheckBox(i18n("Put_To_Sleep:"),
                    i18n("Tick_this_box_if_the_animal_was_put_to_sleep"),
                    UI.fp(this, "deathChanged")));

        chkDiedOffShelter = (UI.CheckBox) UI.addComponent(pnlPTS,
                UI.getCheckBox(i18n("Died_Off_Shelter:"),
                    i18n("Tick_this_box_if_the_animal_died_off_the_shelter_(keep_out_of_figures)"),
                    UI.fp(this, "diedOffShelterChanged")));

        cboPTSReason = (UI.ComboBox) UI.addComponent(pnlPTS,
                i18n("Death_Category"),
                UI.getCombo(i18n("Death_Category"),
                    LookupCache.getDeathReasonLookup(), "ReasonName",
                    UI.fp(this, "dataChanged")));

        pnlDeath.add(pnlPTS, UI.BorderLayout.NORTH);

        UI.Panel pnlPTSComments = UI.getPanel(UI.getBorderLayout());

        txtPTSReason = (UI.TextArea) UI.addComponent(pnlPTSComments,
                i18n("PTS_Reason:"),
                UI.getTextArea(i18n("The_reason_the_animal_was_put_to_sleep"),
                    UI.fp(this, "dataChanged")));

        pnlDeath.add(pnlPTSComments, UI.BorderLayout.CENTER);

        // Tabs =========================================================
        medicals = new MedicalSelector();
        animaldiets = new DietSelector();
        animalcosts = new CostSelector(this);
        donations = new DonationSelector(null);
        animalmedia = new MediaSelector();
        animaldiary = new DiarySelector();
        animalmovement = new MovementSelector(this);
        animalvaccinations = new VaccinationSelector();
        log = new LogSelector();
        additional = new AdditionalFieldView(AdditionalField.LINKTYPE_ANIMAL,
                UI.fp(this, "dataChanged"));

        tabTabs = UI.getTabbedPane(UI.fp(this, "tabChanged"));
        tabTabs.addTab(Global.i18n("uianimal", "animal_details"), null,
            pnlDetails, Global.i18n("uianimal", "basic_animal_information"));

        tabTabs.addTab(Global.i18n("uianimal", "entry_details"), null,
            pnlEntry, Global.i18n("uianimal", "about_entry_details"));

        tabTabs.addTab(Global.i18n("uianimal", "vet"), null, pnlVet,
            Global.i18n("uianimal", "about_vet"));

        tabTabs.addTab(Global.i18n("uianimal", "death"), null, pnlDeath,
            Global.i18n("uianimal", "if_the_animal_is_deceased"));

        tabTabs.addTab(Global.i18n("uianimal", "Vaccination"), null,
            animalvaccinations,
            Global.i18n("uianimal", "animal_vaccination_information"));

        tabTabs.addTab(Global.i18n("uianimal", "medical"), medicals);

        tabTabs.addTab(Global.i18n("uianimal", "diet"), null, animaldiets,
            Global.i18n("uianimal", "diet_information"));

        tabTabs.addTab(i18n("Costs"), animalcosts);
        tabTabs.addTab(i18n("Donations"), donations);

        tabTabs.addTab(Global.i18n("uianimal", "media"), null, animalmedia,
            Global.i18n("uianimal", "media_for_this_animal"));

        tabTabs.addTab(Global.i18n("uianimal", "diary"), null, animaldiary,
            Global.i18n("uianimal", "diary_info_for_this_animal"));

        tabTabs.addTab(Global.i18n("uianimal", "movements"), null,
            animalmovement, Global.i18n("uianimal", "animal_movements"));

        tabTabs.addTab(Global.i18n("uianimal", "log"), null, log,
            Global.i18n("uianimal", "log_info"));

        if (additional.hasFields()) {
            tabTabs.addTab(Global.i18n("uianimal", "additional"), null,
                additional, Global.i18n("uianimal", "additional_info"));
        }

        add(tabTabs, UI.BorderLayout.CENTER);
    }

    public void nonShelterChanged() {
        enableOnShelterTabs();
        dataChanged();
    }

    public void actionViewLitterMates() {
        // Drop out if there is no acceptance number
        String acceptance = txtAcceptanceNumber.getText();

        if (acceptance.trim().equals("")) {
            return;
        }

        // Open a find animal form, set the logical location to all
        // and the acceptance number to the one currently selected

        // Create FindAnimal
        AnimalFind fa = new AnimalFind();

        // Add it to the desktop
        Global.mainForm.addChild(fa);

        // Set the appropriate values
        fa.cboLocation.setSelectedIndex(1);
        fa.txtAcceptanceNo.setText(acceptance.trim());
        fa.txtAgeFrom.setText("0");
        fa.txtAgeTo.setText("0.5");
        fa.chkIncludeDeceased.setSelected(true);

        // Run the search
        fa.runSearch();
    }

    public void actionViewOwner() {
        // Gets the latest owner for this animal and attempts to open
        // their record.
        try {
            // If this is a non-shelter animal, use the original
            // owner record instead.
            if (animal.getNonShelterAnimal().intValue() == 1) {
                OwnerEdit eo = new OwnerEdit();

                // Drop out if there's no original owner
                if ((animal.getOriginalOwnerID() == null) ||
                        (animal.getOriginalOwnerID().intValue() == 0)) {
                    return;
                }

                eo.openForEdit(animal.getOriginalOwnerID().intValue());
                Global.mainForm.addChild(eo);
                eo = null;
            }

            // Otherwise, use the latest movement record to find
            // the owner
            Adoption ad = animal.getLatestMovement();

            if (ad == null) {
                return;
            }

            Owner o = ad.getOwner();

            if (o == null) {
                return;
            }

            OwnerEdit eo = new OwnerEdit();
            eo.openForEdit(o);
            Global.mainForm.addChild(eo);

            o = null;
            eo = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionFindLitter() {
        try {
            // Find all currently active litters on the system
            AnimalLitter al = AnimalLitter.getRecentLittersForSpecies(Utils.getIDFromCombo(
                        LookupCache.getSpeciesLookup(), "SpeciesName",
                        cboSpecies).intValue());

            // If there aren't any, bomb
            if (al.getEOF()) {
                Dialog.showError(Global.i18n("uianimal",
                        "there_are_no_active_litters_on_file"));

                return;
            }

            String[] litters = new String[(int) al.getRecordCount()];
            int i = 0;

            while (!al.getEOF()) {
                try {
                    litters[i] = al.getAcceptanceNumber() + " - " +
                        al.getAnimal().getShelterCode() + ": " +
                        al.getParentName() + " (" + al.getSpeciesName() + ")";
                } catch (Exception e) {
                    litters[i] = al.getAcceptanceNumber() + " - [No parent] (" +
                        al.getSpeciesName() + ")";
                }

                i++;
                al.moveNext();
            }

            // Prompt
            String choice = (String) Dialog.getInput(Global.i18n("uianimal",
                        "select_from_active_litters"),
                    Global.i18n("uianimal", "Active_Litters"), litters,
                    litters[0]);

            // Dump the code from the returned value, or drop out if
            // nothing was selected.
            if (choice == null) {
                return;
            }

            txtAcceptanceNumber.setText(choice.substring(0, choice.indexOf(" ")));
            dataChanged();
            enableButtons();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionMatchLost() {
        // Attempt a lost lookup in the same manner as
        // doing found -> lost
        try {
            new LostFoundMatch(0, 0, animal.getID().intValue());
        } catch (Exception e) {
        }
    }

    public void actionPrint() {
        new AnimalPrint(animal);
    }

    public void diedOffShelterChanged() {
        dataChanged();
        fillDateDeceased();
    }

    public void genCode() {
        txtShelterCode.clear();
        checkType();
    }

    public void actionCreateLitter() {
        // Open a new litter screen and pre-fill it with information
        LitterEdit el = new LitterEdit(null);
        el.openForNew();

        el.cboSpecies.setSelectedIndex(this.cboSpecies.getSelectedIndex());
        el.txtNumber.setText("1");
        el.txtAcceptanceNumber.setText(this.txtAcceptanceNumber.getText());
        el.txtDate.setText(this.txtDateBroughtIn.getText());

        Global.mainForm.addChild(el);
        el = null;
    }

    public void updateAge() {
        // Recalculate the age if this is not a new record
        // and the date is valid.
        /*
        if (!isNewRecord) {
            // Update the age field in the object
            try {
            } catch (Exception e) {
            }

            // Update the title
            showTitle();
        }
        */

        // Alter the date of birth tooltip and form title
        try {
            animal.setDateOfBirth(Utils.parseDate(txtDateOfBirth.getText()));
            txtDateOfBirth.setToolTipText(animal.getAge() + " (" +
                animal.calculateAgeGroup() + ")");
            showTitle();
        } catch (Exception e) {
            txtDateOfBirth.setToolTipText(null);
        }
    }

    /** Loads the data in all tabs */
    public void loadAllTabs() {
        loadedEntry = true;

        try {
            embOriginalOwner.loadFromID(animal.getOriginalOwnerID().intValue());
            embBroughtInBy.loadFromID(animal.getBroughtInByOwnerID().intValue());
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        loadedVet = true;

        try {
            embVet.loadFromID(animal.getOwnersVetID().intValue());
            embCurrentVet.loadFromID(animal.getCurrentVetID().intValue());
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        loadedVaccinations = true;
        animalvaccinations.updateList();
        loadedMedical = true;
        medicals.updateList();
        loadedDiets = true;
        animaldiets.updateList();
        loadedCosts = true;
        animalcosts.updateList();
        loadedDonations = true;
        donations.updateList();
        loadedMedia = true;
        animalmedia.updateList();
        loadedDiary = true;
        animaldiary.updateList();
        loadedMovements = true;
        animalmovement.updateList();
        loadedLogs = true;
        log.updateList();

        setDirty(false);
    }

    /** Lazily load contents as tabs are clicked */
    public void tabChanged() {
        int si = tabTabs.getSelectedIndex();

        switch (si) {
        case TAB_ENTRY:

            if (!loadedEntry) {
                loadedEntry = true;

                try {
                    embOriginalOwner.loadFromID(animal.getOriginalOwnerID()
                                                      .intValue());
                    embBroughtInBy.loadFromID(animal.getBroughtInByOwnerID()
                                                    .intValue());
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }
            }

            break;

        case TAB_VET:

            if (!loadedVet) {
                loadedVet = true;

                try {
                    embVet.loadFromID(animal.getOwnersVetID().intValue());
                    embCurrentVet.loadFromID(animal.getCurrentVetID().intValue());
                } catch (NullPointerException npe) {
                    loadedVet = false;
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }
            }

            break;

        case TAB_VACCINATION:

            if (!loadedVaccinations) {
                loadedVaccinations = true;
                animalvaccinations.updateList();
            }

            break;

        case TAB_MEDICAL:

            if (!loadedMedical) {
                loadedMedical = true;
                medicals.updateList();
            }

            break;

        case TAB_DIET:

            if (!loadedDiets) {
                loadedDiets = true;
                animaldiets.updateList();
            }

            break;

        case TAB_COSTS:

            if (!loadedCosts) {
                loadedCosts = true;
                animalcosts.updateList();
            }

            break;

        case TAB_DONATIONS:

            if (!loadedDonations) {
                loadedDonations = true;
                donations.updateList();
            }

            break;

        case TAB_MEDIA:

            if (!loadedMedia) {
                loadedMedia = true;
                animalmedia.updateList();
            }

            break;

        case TAB_DIARY:

            if (!loadedDiary) {
                loadedDiary = true;
                animaldiary.updateList();
            }

            break;

        case TAB_MOVEMENT:

            if (!loadedMovements) {
                loadedMovements = true;
                animalmovement.updateList();
            }

            break;

        case TAB_LOG:

            if (!loadedLogs) {
                loadedLogs = true;
                log.updateList();
            }

            break;
        }
    }

    public void speciesChanged() {
        dataChanged();
        checkBreed();
    }

    public void typeChanged() {
        checkType();
        dataChanged();
    }

    public void deathChanged() {
        dataChanged();
        fillDateDeceased();
    }

    public void actionRandomName() {
        txtAnimalName.setText(AnimalName.getRandomName(Integer.toString(
                    cboSex.getSelectedIndex())));
        dataChanged();
    }

    public void actionDiaryTask() {
        DiaryTaskExecute edt = new DiaryTaskExecute(animal, this);
        Global.mainForm.addChild(edt);
        edt = null;
    }

    public void actionDocument() {
        // Create a new AnimalDocument and that should do the rest for us.
        AnimalDocument ad = new AnimalDocument(animal, animalmedia);
        ad = null;
    }

    public void actionMediaNotes() {
        // Update the animals media notes to match
        try {
            if (!isNewRecord) {
                animal.updateWebMediaNotes(txtComments.getText());
                animalmedia.updateList();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionDelete() {
        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            // Remove it from the database, along with any satellite data
            try {
                String s = "Delete From animalvaccination Where AnimalID = " +
                    animal.getID();
                DBConnection.executeAction(s);
                s = "Delete From media Where LinkID = " + animal.getID() +
                    " AND LinkTypeID = " +
                    Integer.toString(Media.LINKTYPE_ANIMAL);
                DBConnection.executeAction(s);
                s = "Delete From diary Where LinkID = " + animal.getID() +
                    " AND LinkType = " +
                    Integer.toString(Diary.LINKTYPE_ANIMAL);
                DBConnection.executeAction(s);
                s = "Delete From adoption Where AnimalID = " + animal.getID();
                DBConnection.executeAction(s);
                s = "Delete From log Where LinkID = " + animal.getID() +
                    " AND LinkType = " + Integer.toString(Log.LINKTYPE_ANIMAL);
                DBConnection.executeAction(s);
                s = "Delete From animalmedical Where AnimalID = " +
                    animal.getID();
                DBConnection.executeAction(s);
                s = "Delete From animalmedicaltreatment Where AnimalID = " +
                    animal.getID();
                DBConnection.executeAction(s);
                s = "Delete From additional Where LinkID = " + animal.getID() +
                    " AND LinkType = " +
                    Integer.toString(AdditionalField.LINKTYPE_ANIMAL);
                DBConnection.executeAction(s);
                s = "Delete From animal Where ID = " + animal.getID();
                DBConnection.executeAction(s);

                dispose();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void dateChanged(String newDate) {
        updateDeath();
    }

    public void ownerChanged(int ownerid, String id) {
        dataChanged();

        // If the brought in by doesn't have a record,
        // assign it this one
        if ((embBroughtInBy.getID() == 0) && (embOriginalOwner.getID() != 0)) {
            embBroughtInBy.setID(embOriginalOwner.getID());
        }
    }
}


class AnimalCodeField extends UI.Panel {
    UI.TextField code = null;
    UI.TextField shortcode = null;

    public AnimalCodeField(FunctionPointer onGenerate,
        FunctionPointer onChange, String buttontooltip, String tooltip,
        String shorttooltip) {
        super(UI.getBorderLayout(), true);

        UI.Panel bits = UI.getPanel(UI.getGridLayout(0, 2, 0, 0), true);
        code = UI.getTextField(tooltip, onChange);
        code.setWidth(UI.getTextBoxWidth() / 2);
        code.setForeground(UI.getColor(255, 0, 0));
        shortcode = UI.getTextField(shorttooltip, onChange);
        shortcode.setWidth(UI.getTextBoxWidth() / 2);
        bits.add(code);
        bits.add(shortcode);

        UI.Button btn = UI.getButton(null,
                ((buttontooltip != null) ? buttontooltip : tooltip), ' ',
                IconManager.getIcon(IconManager.SEARCHSMALL), onGenerate);
        add(bits, UI.BorderLayout.CENTER);

        UI.ToolBar t = UI.getToolBar();
        t.add(btn);
        add(t, UI.BorderLayout.EAST);
    }

    public String getCode() {
        return code.getText();
    }

    public String getShortCode() {
        return shortcode.getText();
    }

    public void setCode(String s) {
        code.setText(s);
    }

    public void setShortCode(String s) {
        shortcode.setText(s);
    }

    public UI.TextField getCodeField() {
        return code;
    }

    public UI.TextField getShortCodeField() {
        return shortcode;
    }

    public void clear() {
        setCode("");
        setShortCode("");
    }
}
