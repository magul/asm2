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

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.LostFoundMatch;
import net.sourceforge.sheltermanager.asm.reports.SearchResults;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryEdit;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryTaskExecute;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMFind;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class contains all code for searching the animal database.
 *
 * @author Robin Rawson-Tetley
 */
public class AnimalFind extends ASMFind {
    private UI.Button btnHotGenForm;
    private UI.Button btnHotDiaryTask;
    private UI.Button btnHotDiaryNote;
    private UI.Button btnHotLostFound;
    private UI.Button btnHotMedia;
    private UI.Button btnHotMove;
    private UI.Button btnClear;
    private UI.Button btnPrint;
    private UI.Button btnSimple;
    public UI.ComboBox cboInternalLocation;
    public UI.ComboBox cboLocation;
    public UI.ComboBox cboReserved;
    public UI.ComboBox cboSex;
    public UI.ComboBox cboSize;
    public UI.ComboBox cboSpecies;
    public UI.ComboBox cboType;
    public UI.CheckBox chkIncludeDeceased;
    public UI.CheckBox chkIncludeNonShelter;
    public UI.CheckBox chkTransfersOnly;
    public UI.CheckBox chkGoodWithKids;
    public UI.CheckBox chkGoodWithCats;
    public UI.CheckBox chkGoodWithDogs;
    public UI.CheckBox chkHousetrained;
    public UI.TextField txtAcceptanceNo;
    public UI.TextField txtAdoptionNo;
    public UI.TextField txtAgeFrom;
    public UI.TextField txtAgeTo;
    public UI.TextField txtAnimalName;
    public DateField txtDateFrom;
    public DateField txtDateTo;
    public UI.TextField txtFeatures;
    public UI.TextField txtHasComments;
    public UI.TextField txtHiddenComments;
    public UI.TextField txtIdentichipNo;
    public UI.TextField txtInsuranceNo;
    public UI.TextField txtMediaNotes;
    public UI.TextField txtOOName;
    public UI.TextField txtRabiesTag;
    public UI.TextField txtShelterCode;
    SearchListener listener = null;
    StringBuffer pt = null;

    public AnimalFind(SearchListener thelistener) {
        listener = thelistener;
        init(Global.i18n("uianimal", "Find_Animal"),
            IconManager.getIcon(IconManager.SCREEN_FINDANIMAL), "uianimal", 6,
            true, thelistener != null);
    }

    /** Creates new form FindAnimal */
    public AnimalFind() {
        this(null);
    }

    public void dispose() {
        listener = null;
        super.dispose();
    }

    public boolean needsScroll() {
        return true;
    }

    public int getScrollHeight() {
        return 575;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnHotGenForm.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnHotDiaryNote.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnHotDiaryTask.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecMatchLostAndFoundAnimals()) {
            btnHotLostFound.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimalMedia()) {
            btnHotMedia.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimalMovements()) {
            btnHotMove.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewCustomReports()) {
            btnPrint.setEnabled(false);
        }
    }

    public boolean formClosing() {
        return false;
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtAnimalName);
        ctl.add(cboType);
        ctl.add(cboSpecies);
        ctl.add(cboSex);
        ctl.add(txtShelterCode);
        ctl.add(txtAcceptanceNo);
        ctl.add(cboReserved);
        ctl.add(cboLocation);
        ctl.add(cboInternalLocation);
        ctl.add(cboSize);
        ctl.add(txtDateFrom.getTextField());
        ctl.add(txtDateTo.getTextField());
        ctl.add(txtIdentichipNo);
        ctl.add(txtAgeFrom);
        ctl.add(txtAgeTo);
        ctl.add(txtHasComments);
        ctl.add(txtFeatures);
        ctl.add(txtAdoptionNo);
        ctl.add(txtInsuranceNo);
        ctl.add(txtRabiesTag);
        ctl.add(txtHiddenComments);
        ctl.add(txtOOName);
        ctl.add(txtMediaNotes);
        ctl.add(chkTransfersOnly);
        ctl.add(chkIncludeDeceased);
        ctl.add(chkIncludeNonShelter);
        ctl.add(chkGoodWithKids);
        ctl.add(chkGoodWithCats);
        ctl.add(chkGoodWithDogs);
        ctl.add(chkHousetrained);
        ctl.add(table);
        ctl.add(btnSearch);
        ctl.add(btnOpen);
        ctl.add(btnClear);
        ctl.add(btnPrint);
        ctl.add(btnSimple);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtAnimalName;
    }

    public void initCriteria(UI.Panel p) {
        txtAnimalName = (UI.TextField) UI.addComponent(p, i18n("Name:_"),
                UI.getTextField());

        cboType = (UI.ComboBox) UI.addComponent(p, i18n("Type:_"),
                UI.getCombo(i18n("Type:_"), LookupCache.getAnimalTypeLookup(),
                    "AnimalType", i18n("(all)")));

        cboSpecies = (UI.ComboBox) UI.addComponent(p, i18n("Species:_"),
                UI.getCombo(i18n("Species:_"), LookupCache.getSpeciesLookup(),
                    "SpeciesName", i18n("(all)")));

        cboSex = (UI.ComboBox) UI.addComponent(p, i18n("Sex:_"),
                UI.getCombo(LookupCache.getSexLookup(), "Sex", i18n("(all)")));

        txtShelterCode = (UI.TextField) UI.addComponent(p, i18n("Code:_"),
                UI.getTextField());

        txtAcceptanceNo = (UI.TextField) UI.addComponent(p,
                (Global.getUsingAutoLitterID() ? i18n("litter_id")
                                               : i18n("Acc._No:_")),
                UI.getTextField());

        cboReserved = (UI.ComboBox) UI.addComponent(p, i18n("Reserved:_"),
                UI.getCombo());
        cboReserved.addItem(i18n("(both)"));
        cboReserved.addItem(i18n("Reserved"));
        cboReserved.addItem(i18n("Unreserved"));

        cboLocation = (UI.ComboBox) UI.addComponent(p, i18n("Location:_"),
                UI.getCombo());
        cboLocation.addItem(i18n("On_Shelter"));
        cboLocation.addItem(i18n("(all)"));
        cboLocation.addItem(i18n("Adoptable"));
        cboLocation.addItem(i18n("Adopted"));
        cboLocation.addItem(i18n("Fostered"));
        cboLocation.addItem(i18n("Transferred"));
        cboLocation.addItem(i18n("Escaped"));
        cboLocation.addItem(i18n("Stolen"));
        cboLocation.addItem(i18n("Released_To_Wild"));
        cboLocation.addItem(i18n("Reclaimed_By_Owner"));
        cboLocation.addItem(i18n("Dead"));

        if (!Configuration.getBoolean("DisableRetailer")) {
            cboLocation.addItem(i18n("Retailer"));
        }

        cboLocation.addItem(i18n("Non-Shelter"));
        cboLocation.addItem(i18n("Not_Available_For_Adoption"));

        cboInternalLocation = (UI.ComboBox) UI.addComponent(p,
                i18n("Internal_Loc:_"),
                UI.getCombo(LookupCache.getInternalLocationLookup(),
                    "LocationName", i18n("(all)")));

        cboSize = (UI.ComboBox) UI.addComponent(p, i18n("Size:"),
                UI.getCombo(LookupCache.getSizeLookup(), "Size", i18n("(all)")));

        txtDateFrom = (DateField) UI.addComponent(p, i18n("In_Between:_"),
                UI.getDateField());

        txtDateTo = (DateField) UI.addComponent(p, i18n("and:_"),
                UI.getDateField());

        txtIdentichipNo = (UI.TextField) UI.addComponent(p,
                i18n("Identichip_No:_"), UI.getTextField());

        txtAgeFrom = (UI.TextField) UI.addComponent(p, i18n("Aged_between:_"),
                UI.getTextField());

        txtAgeTo = (UI.TextField) UI.addComponent(p, i18n("and:_"),
                UI.getTextField());

        txtHasComments = (UI.TextField) UI.addComponent(p,
                i18n("Has_Comments:_"), UI.getTextField());

        txtFeatures = (UI.TextField) UI.addComponent(p, i18n("Features_"),
                UI.getTextField());

        txtAdoptionNo = (UI.TextField) UI.addComponent(p,
                i18n("Adoption_No:_"), UI.getTextField());

        txtInsuranceNo = (UI.TextField) UI.addComponent(p,
                i18n("Insurance_No:_"), UI.getTextField());

        txtRabiesTag = (UI.TextField) UI.addComponent(p, i18n("Rabies_Tag"),
                UI.getTextField());

        txtHiddenComments = (UI.TextField) UI.addComponent(p,
                i18n("Hidden_Comments:_"), UI.getTextField());

        txtOOName = (UI.TextField) UI.addComponent(p,
                i18n("find_original_owner_name"), UI.getTextField());

        txtMediaNotes = (UI.TextField) UI.addComponent(p,
                i18n("media_notes_contain"), UI.getTextField());

        p.add(UI.getLabel());
        chkTransfersOnly = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("Show_Only_Transfers")));

        chkIncludeDeceased = UI.getCheckBox(i18n("Inc._Deceased"),
                i18n("tick_this_box_to_include_deceased_animals"));
        p.add(chkIncludeDeceased);

        chkIncludeNonShelter = UI.getCheckBox(i18n("Include_Non_Shelter"),
                i18n("tick_this_box_to_include_nonshelter_animals"));
        p.add(chkIncludeNonShelter);

        chkGoodWithKids = UI.getCheckBox(i18n("Good_with_kids"),
                i18n("tick_this_box_to_only_show_animals_who_are_good_with_children"));
        p.add(chkGoodWithKids);

        chkGoodWithCats = UI.getCheckBox(i18n("Good_with_cats"),
                i18n("tick_this_box_to_only_show_animals_who_are_good_with_cats"));
        p.add(chkGoodWithCats);

        chkGoodWithDogs = UI.getCheckBox(i18n("Good_with_dogs"),
                i18n("tick_this_box_to_only_show_animals_who_are_good_with_dogs"));
        p.add(chkGoodWithDogs);

        chkHousetrained = UI.getCheckBox(i18n("Housetrained"),
                i18n("tick_this_box_to_only_show_animals_who_are_housetrained"));
        p.add(chkHousetrained);
    }

    public void initToolbar() {
        btnClear = UI.getButton(i18n("Clear"), null, 'c',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_CLEAR),
                UI.fp(this, "actionClear"));
        addToolbarItem(btnClear, false);

        btnPrint = UI.getButton(i18n("Print"), null, 'p',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_PRINT),
                UI.fp(this, "actionPrint"));
        addToolbarItem(btnPrint, true);

        btnSimple = UI.getButton(i18n("Simple"), null, 'i',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_SIMPLE),
                UI.fp(this, "actionSimple"));
        addToolbarItem(btnSimple, false);
    }

    public void initLeftbar() {
        btnHotGenForm = UI.getButton(null,
                i18n("Generate_an_animal_letter/form"), 'f',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_GENERATEDOC),
                UI.fp(this, "actionGenForm"));
        addLeftbarItem(btnHotGenForm, true);

        btnHotDiaryTask = UI.getButton(null,
                i18n("Generate_a_diary_task_for_this_animal"), 't',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_DIARYTASK),
                UI.fp(this, "actionDiaryTask"));
        addLeftbarItem(btnHotDiaryTask, true);

        btnHotDiaryNote = UI.getButton(null,
                i18n("generate_a_diary_note_for_this_animal"), 'd',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_DIARYNOTE),
                UI.fp(this, "actionDiary"));
        addLeftbarItem(btnHotDiaryNote, true);

        btnHotLostFound = UI.getButton(null,
                i18n("look_in_lost_animal_database"), 'n',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_LOSTFOUND),
                UI.fp(this, "actionMatchLost"));
        addLeftbarItem(btnHotLostFound, true);

        btnHotMedia = UI.getButton(null, i18n("Add_new_media_to_this_animal"),
                'e',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_ADDMEDIA),
                UI.fp(this, "actionMedia"));
        addLeftbarItem(btnHotMedia, true);

        btnHotMove = UI.getButton(null, i18n("Move_this_animal"), 'm',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_MOVEANIMAL),
                UI.fp(this, "actionMove"));
        addLeftbarItem(btnHotMove, true);
    }

    public void actionGenForm() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        AnimalDocument ad = new AnimalDocument(animal);
        ad = null;
        animal = null;
    }

    public void actionDiaryTask() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        DiaryTaskExecute edt = new DiaryTaskExecute(animal, null);
        Global.mainForm.addChild(edt);
        edt = null;
        animal = null;
    }

    public void actionDiary() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        DiaryEdit ed = new DiaryEdit();

        try {
            ed.openForNew(animal.getID().intValue(), Diary.LINKTYPE_ANIMAL, null);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Global.mainForm.addChild(ed);
        ed = null;

        animal.free();
        animal = null;
    }

    public void actionMatchLost() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        try {
            new LostFoundMatch(0, 0, animal.getID().intValue());
        } catch (Exception e) {
        }

        animal.free();
        animal = null;
    }

    public void actionMedia() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        MediaAdd am = null;

        try {
            am = new MediaAdd(animal.getID().intValue(), Media.LINKTYPE_ANIMAL);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Global.mainForm.addChild(am);
        am = null;

        animal.free();
        animal = null;
    }

    public void actionMove() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        MovementEdit em = new MovementEdit(null);

        try {
            em.openForNew(animal.getID().intValue(), 0);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Global.mainForm.addChild(em);
        em = null;

        animal.free();
        animal = null;
    }

    public void actionPrint() {
        SortableTableModel tablemodel = (SortableTableModel) table.getModel();
        new SearchResults(tablemodel.getData(), tablemodel.getRowCount(),
            pt.toString());
        tablemodel = null;
    }

    public void actionSimple() {
        Global.mainForm.addChild(new AnimalFindText(listener));
        dispose();
    }

    public void actionClear() {
        // Blank and reset all controls
        this.txtAcceptanceNo.setText("");
        this.txtAdoptionNo.setText("");
        this.txtAgeFrom.setText("");
        this.txtAgeTo.setText("");
        this.txtAnimalName.setText("");
        this.txtDateFrom.setText("");
        this.txtDateTo.setText("");
        this.txtFeatures.setText("");
        this.txtHasComments.setText("");
        this.txtIdentichipNo.setText("");
        this.txtInsuranceNo.setText("");
        this.txtShelterCode.setText("");
        this.txtHiddenComments.setText("");
        this.txtOOName.setText("");
        this.txtMediaNotes.setText("");

        this.cboInternalLocation.setSelectedItem(i18n("(all)"));
        this.cboLocation.setSelectedItem(i18n("On_Shelter"));
        this.cboReserved.setSelectedItem(i18n("(both)"));
        this.cboSex.setSelectedIndex(0);
        this.cboSpecies.setSelectedItem(i18n("(all)"));
        this.cboSize.setSelectedIndex(0);
        this.cboType.setSelectedItem(i18n("(all)"));

        this.chkIncludeDeceased.setSelected(false);
        this.chkTransfersOnly.setSelected(false);
        this.chkGoodWithCats.setSelected(false);
        this.chkGoodWithDogs.setSelected(false);
        this.chkGoodWithKids.setSelected(false);
        this.chkHousetrained.setSelected(false);
        this.chkIncludeNonShelter.setSelected(false);
    }

    public void itemSelected(int id) {
        Animal animal = LookupCache.getAnimalByID(new Integer(id));

        // If we are in selection mode, return the event
        // with the object and destroy this form
        if (selectionMode) {
            listener.animalSelected(animal);
            animal = null;
            dispose();
        } else {
            // Create a new EditAnimal screen
            AnimalEdit ea = new AnimalEdit();
            // Kick it off into edit mode, passing the animal
            ea.openForEdit(animal);
            // Attach it to the main screen
            Global.mainForm.addChild(ea);
            animal = null;
            ea = null;
        }
    }

    public void addDisplay(String name, String value) {
        pt.append(name).append(" ").append(value).append("<br />");
    }

    /** Performs the search */
    public void runSearch() {
        SQLRecordset animal = new SQLRecordset();
        pt = new StringBuffer();

        // Reads all the criteria fields and performs the search

        // SQL select
        final String SELECT = "SELECT animal.ID, AnimalTypeID, BreedID, " +
            "CrossBreed, Breed2ID, BreedName, " +
            "SpeciesID, ShelterCode, ShortCode, AnimalName, " +
            "ShelterLocation, DateOfBirth, Sex, Size, " +
            "BaseColourID, Markings, IdentichipNumber, " +
            "DateBroughtIn, NonShelterAnimal, ActiveMovementID, " +
            "HasActiveReserve, ActiveMovementType, ActiveMovementDate, DeceasedDate " +
            "FROM animal ";
        final String MOVEJOIN = "INNER JOIN adoption ON animal.ActiveMovementID = adoption.ID ";
        final String MEDIAJOIN = "INNER JOIN media ON media.LinkID = animal.ID ";
        final String OOJOIN = "INNER JOIN owner ON animal.OriginalOwnerID = owner.ID ";

        boolean needMoveJoin = false;
        boolean needMediaJoin = false;
        boolean needOOJoin = false;

        // Get the IDs of selected combo boxes
        int animaltypeid = Utils.getID("animaltype", "AnimalType",
                (String) cboType.getSelectedItem()).intValue();
        int speciesid = Utils.getID("species", "SpeciesName",
                (String) cboSpecies.getSelectedItem()).intValue();
        int intlocid = Utils.getID("internallocation", "LocationName",
                (String) cboInternalLocation.getSelectedItem()).intValue();
        String logloc = (String) cboLocation.getSelectedItem();

        if (!txtAnimalName.getText().equals("")) {
            addDisplay(i18n("Name:"), txtAnimalName.getText());

            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("AnimalName Like '%" +
                    txtAnimalName.getText().replace('\'', '`') + "%'");
            } else {
                addSqlCriteria("UPPER(AnimalName) Like '%" +
                    Utils.upper(txtAnimalName.getText()).replace('\'', '`') +
                    "%'");
            }
        }

        try {
            if (animaltypeid != 0) {
                addSqlCriteria("AnimalTypeID=" + animaltypeid);
                addDisplay(i18n("Type:"),
                    LookupCache.getAnimalTypeName(animaltypeid));
            }

            if (speciesid != 0) {
                addSqlCriteria("SpeciesID=" + speciesid);
                addDisplay(i18n("Species:"),
                    LookupCache.getSpeciesName(speciesid));
            }

            if (intlocid != 0) {
                addSqlCriteria("ShelterLocation=" + intlocid);
                addDisplay(i18n("Internal_Loc:_"),
                    LookupCache.getInternalLocationName(intlocid));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        if (!txtIdentichipNo.getText().equals("")) {
            addSqlCriteria("IdentichipNumber Like '%" +
                txtIdentichipNo.getText() + "%'");
            addDisplay(i18n("Identichip_No:_"), txtIdentichipNo.getText());
        }

        if (!txtRabiesTag.getText().equals("")) {
            addSqlCriteria("RabiesTag Like '%" + txtRabiesTag.getText() + "%'");
            addDisplay(i18n("Rabies_Tag"), txtRabiesTag.getText());
        }

        String sex = (String) cboSex.getSelectedItem();

        if (!sex.equalsIgnoreCase(i18n("(all)"))) {
            addSqlCriteria("Sex = " + LookupCache.getSexIDForName(sex));
            addDisplay(i18n("Sex:"), sex);
        }

        String size = (String) cboSize.getSelectedItem();

        if (!size.equals(i18n("(all)"))) {
            addSqlCriteria("Size = " + LookupCache.getSizeIDForName(size));
            addDisplay(i18n("Size:"), size);
        }

        if (!txtShelterCode.getText().equals("")) {
            addSqlCriteria("UPPER(ShelterCode) Like '%" +
                Utils.upper(txtShelterCode.getText()) + "%'");
            addDisplay(i18n("Code:"), txtShelterCode.getText());
        }

        if (!txtAcceptanceNo.getText().equals("")) {
            addSqlCriteria("AcceptanceNumber Like '%" +
                txtAcceptanceNo.getText() + "%'");
            addDisplay(i18n("litter_id"), txtAcceptanceNo.getText());
        }

        if (!txtDateFrom.getText().equals("") &&
                !txtDateTo.getText().equals("")) {
            try {
                addSqlCriteria("((DateBroughtIn BETWEEN '" +
                    Utils.getSQLDate(txtDateFrom.getText()) + "' AND '" +
                    Utils.getSQLDate(txtDateTo.getText()) + "') OR (" +
                    "ActiveMovementReturn BETWEEN '" +
                    Utils.getSQLDate(txtDateFrom.getText()) + "' AND '" +
                    Utils.getSQLDate(txtDateTo.getText()) + "'))");
                addDisplay(i18n("Date_Brought_In:"),
                    txtDateFrom.getText() + "&lt;-&gt;" + txtDateTo.getText());
            } catch (ParseException e) {
                Dialog.showError(e.getMessage(), i18n("Bad_Date"));

                return;
            }
        }

        if (!chkIncludeDeceased.isSelected() && !logloc.equals(i18n("Dead"))) {
            addSqlCriteria("DeceasedDate Is Null");
        }

        if (chkGoodWithKids.isSelected()) {
            addSqlCriteria("IsGoodWithChildren = 0");
            addDisplay(i18n("Good_with_kids"), "");
        }

        if (chkGoodWithCats.isSelected()) {
            addSqlCriteria("IsGoodWithCats = 0");
            addDisplay(i18n("Good_with_cats"), "");
        }

        if (chkGoodWithDogs.isSelected()) {
            addSqlCriteria("IsGoodWithDogs = 0");
            addDisplay(i18n("Good_with_dogs"), "");
        }

        if (chkHousetrained.isSelected()) {
            addSqlCriteria("IsHouseTrained = 0");
            addDisplay(i18n("Housetrained"), "");
        }

        // Non-sheltered animals
        if (logloc.equals(i18n("Non-Shelter"))) {
            addSqlCriteria("NonShelterAnimal = 1");
            addDisplay(i18n("Non-Shelter"), "");
        }
        // If we are searching for non-shelter animals, then we can ignore the
        // include box (as we do with deceased)
        else if (!chkIncludeNonShelter.isSelected()) {
            addSqlCriteria("NonShelterAnimal = 0");
        }

        // These two are just for display, since they're privative - by ticking
        // them we don't filter things out so no need to change the query
        if (chkIncludeNonShelter.isSelected()) {
            addDisplay(i18n("Include_Non_Shelter"), "");
        }

        if (chkIncludeDeceased.isSelected()) {
            addDisplay(i18n("include_deceased"), "");
        }

        if (chkTransfersOnly.isSelected()) {
            addSqlCriteria("IsTransfer = 1");
            addDisplay(i18n("Show_Only_Transfers"), "");
        }

        if (!txtAgeFrom.getText().equals("") && !txtAgeTo.getText().equals("")) {
            try {
                // Get the dates from and to as today
                String yearfrom = Utils.getSQLDate(Utils.subtractYears(
                            Calendar.getInstance(),
                            Float.parseFloat(txtAgeFrom.getText())));
                String yearto = Utils.getSQLDate(Utils.subtractYears(
                            Calendar.getInstance(),
                            Float.parseFloat(txtAgeTo.getText())));
                addSqlCriteria("DateOfBirth BETWEEN '" + yearto + "' AND '" +
                    yearfrom + "'");
                addDisplay(i18n("Aged_between:"),
                    txtAgeFrom.getText() + " / " + txtAgeTo.getText());
            } catch (NumberFormatException e) {
                // Ignore number format exceptions - just don't
                // apply this criteria
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // The comment search is a bunch of individual words, separated by
        // spaces.
        if (!txtHasComments.getText().equals("")) {
            addDisplay(i18n("Comments:"), txtHasComments.getText());

            String[] words = Utils.split(txtHasComments.getText(), " ");
            int i = 0;

            while (i < words.length) {
                if (Configuration.getBoolean("CaseSensitiveSearch")) {
                    addSqlCriteria("AnimalComments Like '%" + words[i] + "%'");
                } else {
                    addSqlCriteria("UPPER(AnimalComments) Like '%" +
                        Utils.upper(words[i]) + "%'");
                }

                i++;
            }
        }

        // The hidden comment search is a bunch of individual words, separated
        // by spaces.
        if (!txtHiddenComments.getText().equals("")) {
            addDisplay(i18n("Hidden_Comments:"), txtHiddenComments.getText());

            String[] words = Utils.split(txtHiddenComments.getText(), " ");
            int i = 0;

            while (i < words.length) {
                if (Configuration.getBoolean("CaseSensitiveSearch")) {
                    addSqlCriteria("HiddenAnimalDetails Like '%" + words[i] +
                        "%'");
                } else {
                    addSqlCriteria("UPPER(HiddenAnimalDetails) Like '%" +
                        Utils.upper(words[i]) + "%'");
                }

                i++;
            }
        }

        // The features search is a bunch of individual words, separated by
        // spaces.
        if (!txtFeatures.getText().equals("")) {
            addDisplay(i18n("Features_"), txtFeatures.getText());

            String[] words = Utils.split(txtFeatures.getText(), " ");
            int i = 0;

            while (i < words.length) {
                if (Configuration.getBoolean("CaseSensitiveSearch")) {
                    addSqlCriteria("Markings Like '%" + words[i] + "%'");
                } else {
                    addSqlCriteria("UPPER(Markings) Like '%" +
                        Utils.upper(words[i]) + "%'");
                }

                i++;
            }
        }

        // Original Owner Name
        if (!txtOOName.getText().equals("")) {
            addDisplay(i18n("Original_Owner:"), txtOOName.getText());

            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                addSqlCriteria("OwnerName Like '%" + txtOOName.getText() +
                    "%'");
            } else {
                addSqlCriteria("UPPER(OwnerName) Like '%" +
                    Utils.upper(txtOOName.getText()) + "%'");
            }

            needOOJoin = true;
        }

        // Animals on shelter logical location for speed
        if (logloc.equals(i18n("On_Shelter"))) {
            addDisplay(i18n("On_Shelter"), "");
            addSqlCriteria("Archived = 0");
        }

        // Available for adoption
        if (logloc.equals(i18n("Adoptable"))) {
            addDisplay(i18n("Adoptable"), "");
            addSqlCriteria("IsNotAvailableForAdoption=0 AND Archived=0");
        }

        // Unavailable for adoption
        if (logloc.equals(i18n("Not_Available_For_Adoption"))) {
            addDisplay(i18n("Not_Available_For_Adoption"), "");
            addSqlCriteria("IsNotAvailableForAdoption=1 AND Archived=0");
        }

        String reserved = (String) cboReserved.getSelectedItem();

        // If a reservation status was picked, filter
        if (!reserved.equals(i18n("(both)"))) {
            if (reserved.equalsIgnoreCase(i18n("Reserved"))) {
                addDisplay(i18n("Reserved"), "");
                addSqlCriteria("HasActiveReserve = 1");
            }

            if (reserved.equalsIgnoreCase(i18n("Unreserved"))) {
                addDisplay(i18n("Unreserved"), "");
                addSqlCriteria("HasActiveReserve = 0");
            }
        }

        // If a logical location was selected, filter - unless the filter
        // was "On Shelter" or "Non-Shelter" - in which case, we already
        // included it in the SQL string
        if (!logloc.equals(i18n("(all)"))) {
            if (logloc.equals(i18n("Fostered"))) {
                addDisplay(i18n("Fostered"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_FOSTER);
            } else if (logloc.equals(i18n("Adopted"))) {
                addDisplay(i18n("Adopted"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_ADOPTION);
            } else if (logloc.equals(i18n("Transferred"))) {
                addDisplay(i18n("Transferred"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_TRANSFER);
            } else if (logloc.equals(i18n("Escaped"))) {
                addDisplay(i18n("Escaped"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_ESCAPED);
            } else if (logloc.equals(i18n("Stolen"))) {
                addDisplay(i18n("Stolen"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_STOLEN);
            } else if (logloc.equals(i18n("Released_To_Wild"))) {
                addDisplay(i18n("Released_To_Wild"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_RELEASED);
            } else if (logloc.equals(Global.i18n("uianimal",
                            "Reclaimed_By_Owner"))) {
                addDisplay(i18n("Reclaimed_By_Owner"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_RECLAIMED);
            } else if (logloc.equals(i18n("Dead"))) {
                addDisplay(i18n("Dead"), "");
                addSqlCriteria("DeceasedDate Is Not Null");
            } else if (logloc.equals(i18n("Retailer"))) {
                addDisplay(i18n("Retailer"), "");
                addSqlCriteria("ActiveMovementType = " +
                    Adoption.MOVETYPE_RETAILER);
            }
        }

        // Insurance Number
        if (!txtInsuranceNo.getText().equals("")) {
            addDisplay(i18n("Insurance_No:_"), txtInsuranceNo.getText());
            addSqlCriteria("InsuranceNumber Like '%" +
                txtInsuranceNo.getText() + "%'");
            needMoveJoin = true;
        }

        // Adoption Number
        if (!txtAdoptionNo.getText().equals("")) {
            addDisplay(i18n("Adoption_No:_"), txtAdoptionNo.getText());
            addSqlCriteria("AdoptionNumber Like '%" + txtAdoptionNo.getText() +
                "%'");
            needMoveJoin = true;
        }

        // Media Notes
        if (!txtMediaNotes.getText().equals("")) {
            addDisplay(i18n("media_notes_contain"), txtMediaNotes.getText());

            String[] words = Utils.split(txtMediaNotes.getText(), " ");
            int z = 0;

            while (z < words.length) {
                addSqlCriteria("MediaNotes Like '%" + words[z] + "%'");
                z++;
            }

            needMediaJoin = true;
        }

        // Create an array of headers for the animals
        String[] columnheaders = {
                i18n("Name"), i18n("Code"), i18n("Internal_Loc"),
                i18n("Species"), i18n("Breed"), i18n("Sex"), i18n("Age"),
                i18n("Size"), i18n("Colour"), i18n("Features"),
                i18n("Identichip_No"), i18n("Date_Brought_In")
            };

        StringBuffer sql = new StringBuffer(SELECT);

        if (needMoveJoin) {
            sql.append(MOVEJOIN);
        }

        if (needMediaJoin) {
            sql.append(MEDIAJOIN);
        }

        if (needOOJoin) {
            sql.append(OOJOIN);
        }

        if (sqlCriteria.length() > 0) {
            sql.append(" WHERE ").append(getSqlCriteria());
        }

        sql.append(" ORDER BY animal.AnimalName");

        // Search limit
        int limit = Global.getRecordSearchLimit();

        if (limit != 0) {
            sql.append(" LIMIT " + limit);
        }

        Global.logDebug("Animal search: " + sql, "FindAnimalSearch.run");

        try {
            animal.openRecordset(sql.toString(), "animal");
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) animal.getRecordCount()][14];

        // Initialise the progress meter
        initStatusBarMax((int) animal.getRecordCount());

        int i = 0;

        while (!animal.getEOF()) {
            // Add this animal record to the table data
            try {
                datar[i][0] = (String) animal.getField("AnimalName");

                if (Global.getShowShortCodes()) {
                    datar[i][1] = (String) animal.getField("ShortCode");
                } else {
                    datar[i][1] = (String) animal.getField("ShelterCode");
                }

                // If the option is set, either show internal location
                // or logical location if the animal is not on the shelter
                if (Configuration.getBoolean("ShowILOffShelter")) {
                    // Get animal's logical location
                    String logicallocation = Animal.fastGetAnimalLocationNowByName((Integer) animal.getField(
                                "NonShelterAnimal"),
                            (Integer) animal.getField("ActiveMovementID"),
                            (Integer) animal.getField("ActiveMovementType"),
                            (Date) animal.getField("DeceasedDate"));

                    // If it is on the shelter, show the internal location
                    if (logicallocation.equals(Global.i18n("uianimal",
                                    "On_Shelter"))) {
                        datar[i][2] = LookupCache.getInternalLocationName((Integer) animal.getField(
                                    "ShelterLocation"));
                    } else {
                        // Otherwise show the logical location
                        datar[i][2] = "[" + logicallocation + "]";
                    }
                } else {
                    // Option is not set - show internal location
                    datar[i][2] = LookupCache.getInternalLocationName((Integer) animal.getField(
                                "ShelterLocation"));
                }

                datar[i][3] = LookupCache.getSpeciesName((Integer) animal.getField(
                            "SpeciesID"));
                datar[i][4] = (String) animal.getField("BreedName");
                datar[i][5] = LookupCache.getSexNameForID((Integer) animal.getField(
                            "Sex"));
                datar[i][6] = Animal.getAge((Date) animal.getField(
                            "DateOfBirth"),
                        (Date) animal.getField("DeceasedDate"));
                datar[i][7] = LookupCache.getSizeNameForID((Integer) animal.getField(
                            "Size"));
                datar[i][8] = LookupCache.getBaseColourName((Integer) animal.getField(
                            "BaseColourID"));
                datar[i][9] = Utils.nullToEmptyString((String) animal.getField(
                            "Markings"));
                datar[i][10] = Utils.nullToEmptyString((String) animal.getField(
                            "IdentichipNumber"));
                datar[i][11] = Utils.formatTableDate((Date) animal.getField(
                            "DateBroughtIn"));
                datar[i][12] = animal.getField("ID").toString();
                datar[i][13] = Animal.getReportAnimalName((String) animal.getField(
                            "AnimalName"),
                        (Date) animal.getField("DateOfBirth"),
                        (Date) animal.getField("DeceasedDate"),
                        (Integer) animal.getField("HasActiveReserve"),
                        (Integer) animal.getField("ActiveMovementType"));
                i++;
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Next record
            try {
                animal.moveNext();
            } catch (Exception e) {
            }

            incrementStatusBar();
        }

        setTableData(columnheaders, datar, i, 14, 12);

        animal.free();
        animal = null;
        UI.cursorToPointer();
    }
}
