package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalLitter;
import net.sourceforge.sheltermanager.asm.bo.AnimalName;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Date;
import java.util.Vector;


public class AnimalAddMultiple extends ASMForm {
    private UI.Button btnNew;
    private UI.Button btnClone;
    private UI.Button btnSave;
    private UI.Panel rows;

    public AnimalAddMultiple() {
        init(Global.i18n("uianimal", "Add_Animals"),
            IconManager.getIcon(IconManager.ANIMALADD), "uianimal");
    }

    @Override
    public boolean formClosing() {
        return false;
    }

    @Override
    public String getAuditInfo() {
        return null;
    }

    @Override
    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    @Override
    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(btnNew);
        v.add(btnClone);
        v.add(btnSave);

        return v;
    }

    @Override
    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.ToolBar t = UI.getToolBar();

        btnNew = (UI.Button) t.add(UI.getButton(null,
                    i18n("Multiple_Add_Animal"), 'n',
                    IconManager.getIcon(IconManager.SCREEN_ADDANIMAL_NEW),
                    UI.fp(this, "actionNew")));

        btnClone = (UI.Button) t.add(UI.getButton(null,
                    i18n("Multiple_Clone_Animal"), 'c',
                    IconManager.getIcon(IconManager.SCREEN_ADDANIMAL_CLONE),
                    UI.fp(this, "actionClone")));
        btnClone.setEnabled(false);

        btnSave = (UI.Button) t.add(UI.getButton(null, i18n("Multiple_Save"),
                    's',
                    IconManager.getIcon(IconManager.SCREEN_ADDANIMAL_SAVE),
                    UI.fp(this, "saveData")));
        btnSave.setEnabled(false);

        rows = UI.getPanel(UI.getGridLayout(1));

        add(t, UI.BorderLayout.NORTH);

        UI.Panel p = UI.getPanel(UI.getBorderLayout());
        p.add(rows, UI.BorderLayout.NORTH);
        add(p, UI.BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
    }

    @Override
    public boolean saveData() {
        if (rows.getComponentCount() > 0) {
            for (int i = 0; i < rows.getComponentCount(); i++) {
                AnimalRow r = (AnimalRow) rows.getComponent(i);
                r.save();
            }
        }

        dispose();

        return false;
    }

    @Override
    public void setSecurity() {
    }

    public void actionNew() {
        rows.add(new AnimalRow());
        btnClone.setEnabled(true);
        btnSave.setEnabled(true);
    }

    public void actionClone() {
        if (rows.getComponentCount() > 0) {
            AnimalRow r = (AnimalRow) rows.getComponent(rows.getComponentCount() -
                    1);
            rows.add(new AnimalRow(r));
        }
    }

    public class AnimalRow extends UI.Panel {
        public UI.SearchTextField txtName;
        public DateField dtDOB;
        public UI.ComboBox cboType;
        public UI.ComboBox cboSpecies;
        public UI.ComboBox cboSex;
        public UI.ComboBox cboBreed;
        public UI.ComboBox cboBreed2;
        public UI.CheckBox chkCrossbreed;
        public UI.ComboBox cboColour;
        public UI.ComboBox cboLocation;
        public UI.SearchTextField txtAcceptanceNumber;

        public AnimalRow() {
            super();
            initComponents();
        }

        public AnimalRow(AnimalRow c) {
            super();
            initComponents();

            try {
                dtDOB.setDate(c.dtDOB.getDate());
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            txtName.setText(c.txtName.getText());
            cboSex.setSelectedItem(c.cboSex.getSelectedItem());
            cboType.setSelectedItem(c.cboType.getSelectedItem());
            cboSpecies.setSelectedItem(c.cboSpecies.getSelectedItem());
            checkBreed();
            cboBreed.setSelectedItem(c.cboBreed.getSelectedItem());
            cboBreed2.setSelectedItem(c.cboBreed2.getSelectedItem());
            chkCrossbreed.setSelected(c.chkCrossbreed.isSelected());
            cboColour.setSelectedItem(c.cboColour.getSelectedItem());
            cboLocation.setSelectedItem(c.cboLocation.getSelectedItem());
            txtAcceptanceNumber.setText(c.txtAcceptanceNumber.getText());
        }

        public void initComponents() {
            setLayout(UI.getBorderLayout());
            setTitle("");

            UI.Panel p = UI.getPanel(UI.getGridLayout(1), true);
            UI.Panel pbasic = UI.getPanel(UI.getGridLayout(10,
                        new int[] { 10, 15, 10, 15, 5, 10, 5, 13, 5, 12 }));
            UI.Panel pbreed = UI.getPanel(UI.getGridLayout(4,
                        new int[] { 10, 40, 10, 40 }));
            UI.Panel pextra = UI.getPanel(UI.getGridLayout(6,
                        new int[] { 10, 15, 10, 15, 10, 15 }));
            Vector tabs = new Vector();

            // Basic panel
            txtName = (UI.SearchTextField) UI.addComponent(pbasic,
                    i18n("Name:"),
                    UI.getSearchTextField(i18n("The_animal's_name"),
                        i18n("Generate_a_random_name_for_this_animal"), true,
                        IconManager.getIcon(
                            IconManager.SCREEN_EDITANIMAL_RANDOMNAME_SMALL),
                        UI.fp(this, "actionRandomName"), null));
            tabs.add(txtName);

            dtDOB = (DateField) UI.addComponent(pbasic, i18n("Date_Of_Birth:"),
                    UI.getDateField());
            dtDOB.setToToday();
            tabs.add(dtDOB);

            cboSex = UI.getCombo(LookupCache.getSexLookup(), "Sex");
            UI.addComponent(pbasic, i18n("Sex:"), cboSex);
            tabs.add(cboSex);

            cboType = UI.getCombo(LookupCache.getAnimalTypeLookup(),
                    "AnimalType");
            UI.addComponent(pbasic, i18n("Type:"), cboType);
            Utils.setComboFromID(LookupCache.getAnimalTypeLookup(),
                "AnimalType", Configuration.getInteger("AFDefaultType"), cboType);
            tabs.add(cboType);

            cboSpecies = UI.getCombo(LookupCache.getSpeciesLookup(),
                    "SpeciesName", UI.fp(this, "checkBreed"));
            UI.addComponent(pbasic, i18n("Species:"), cboSpecies);
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                Configuration.getInteger("AFDefaultSpecies"), cboSpecies);
            tabs.add(cboSpecies);

            // always applies
            p.add(pbasic);

            // Breed panel
            boolean breedEnabled = Configuration.getBoolean(
                    "AddAnimalsShowBreed");

            cboBreed = UI.getCombo(LookupCache.getBreedLookup(), "BreedName");
            UI.addComponent(pbreed, i18n("Breed:"), cboBreed);

            if (breedEnabled) {
                tabs.add(cboBreed);
            }

            chkCrossbreed = UI.getCheckBox(i18n("Crossbreed"),
                    i18n("tick_this_box_if_this_animal_is_a_crossbreed"),
                    UI.fp(this, "crossbreedChanged"));

            if (!Global.isSingleBreed() && breedEnabled) {
                UI.addComponent(pbreed, chkCrossbreed);
                tabs.add(chkCrossbreed);
            }

            cboBreed2 = UI.getCombo(LookupCache.getBreedLookup(), "BreedName");
            cboBreed2.setEnabled(false);

            if (!Global.isSingleBreed() && breedEnabled) {
                UI.addComponent(pbreed, cboBreed2);
                tabs.add(cboBreed2);
            }

            if (breedEnabled) {
                checkBreed();
                p.add(pbreed);
            }

            // Extra panel, all items configurable, if all are hidden don't
            // show the panel
            boolean colourEnabled = Configuration.getBoolean(
                    "AddAnimalsShowColour");
            boolean locationEnabled = Configuration.getBoolean(
                    "AddAnimalsShowLocation");
            boolean acceptanceEnabled = Configuration.getBoolean(
                    "AddAnimalsShowAcceptance");
            boolean extraEnabled = colourEnabled || locationEnabled ||
                acceptanceEnabled;

            cboColour = UI.getCombo(LookupCache.getBaseColourLookup(),
                    "BaseColour");
            Utils.setComboFromID(LookupCache.getBaseColourLookup(),
                "BaseColour", Configuration.getInteger("AFDefaultColour"),
                cboColour);

            if (colourEnabled) {
                UI.addComponent(pextra, i18n("Base_Colour:"), cboColour);
                tabs.add(cboColour);
            }

            cboLocation = UI.getCombo(LookupCache.getInternalLocationLookup(),
                    "LocationName");
            Utils.setComboFromID(LookupCache.getInternalLocationLookup(),
                "LocationName", Configuration.getInteger("AFDefaultLocation"),
                cboLocation);

            if (locationEnabled) {
                UI.addComponent(pextra, i18n("Location:"), cboLocation);
                tabs.add(cboLocation);
            }

            txtAcceptanceNumber = UI.getSearchTextField(i18n(Configuration.getBoolean(
                            "AutoLitterIdentification")
                        ? "the_litter_identifier_if_this_animal_is_part_of_a_litter"
                        : "The_animal_acceptance_number_from_head_office"),
                    UI.fp(this, "actionChooseLitter"));

            if (acceptanceEnabled) {
                UI.addComponent(pextra,
                    i18n(Configuration.getBoolean("AutoLitterIdentification")
                        ? "litter_id" : "Acceptance_No:"), txtAcceptanceNumber);
                tabs.add(txtAcceptanceNumber);
            }

            if (extraEnabled) {
                p.add(pextra);
            }

            add(p, UI.BorderLayout.NORTH);

            AnimalAddMultiple.this.registerTabOrder(tabs, txtName);
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

        public void crossbreedChanged() {
            cboBreed2.setEnabled(chkCrossbreed.isSelected());
        }

        public void actionChooseLitter() {
            txtAcceptanceNumber.setText(Dialog.getLitter(
                    Utils.getIDFromCombo(LookupCache.getSpeciesLookup(),
                        "SpeciesName", cboSpecies).intValue()));
        }

        public void actionRandomName() {
            txtName.setText(AnimalName.getRandomName(Integer.toString(
                        cboSex.getSelectedIndex())));
        }

        public void save() {
            try {
                Animal a = new Animal("ID = 0");
                a.addNew();
                a.setAnimalName(txtName.getText());
                a.setSex(LookupCache.getSexIDForName(
                        (String) cboSex.getSelectedItem()));
                a.setSize(new Integer(Configuration.getInteger("AFDefaultSize")));
                a.setDateOfBirth(dtDOB.getDate());
                a.setAnimalTypeID(Utils.getIDFromCombo(
                        LookupCache.getAnimalTypeLookup(), "AnimalType", cboType));
                a.setSpeciesID(Utils.getIDFromCombo(
                        LookupCache.getSpeciesLookup(), "SpeciesName",
                        cboSpecies));
                a.setBreedID(Utils.getIDFromCombo(
                        LookupCache.getBreedLookup(), "BreedName", cboBreed));
                a.setBreed2ID(a.getBreedID());
                a.setCrossBreed(chkCrossbreed.isSelected() ? new Integer(1)
                                                           : new Integer(0));
                a.setBreedName(LookupCache.getBreedName(a.getBreedID()));

                if (chkCrossbreed.isSelected()) {
                    a.setBreed2ID(Utils.getIDFromCombo(
                            LookupCache.getBreedLookup(), "BreedName", cboBreed2));
                    a.setBreedName(LookupCache.getBreedName(a.getBreedID()) +
                        " / " + LookupCache.getBreedName(a.getBreed2ID()));
                }

                a.setShelterLocation(Utils.getIDFromCombo(
                        LookupCache.getInternalLocationLookup(),
                        "LocationName", cboLocation));
                a.setBaseColourID(Utils.getIDFromCombo(
                        LookupCache.getBaseColourLookup(), "BaseColour",
                        cboColour));
                a.setAcceptanceNumber(txtAcceptanceNumber.getText());
                a.setDateBroughtIn(new Date());

                Animal.AnimalCode ac = Animal.fastGenerateAnimalCode(cboType.getSelectedItem()
                                                                            .toString(),
                        a.getDateBroughtIn());
                a.setShelterCode(ac.code);
                a.setShortCode(ac.shortcode);

                a.save(Global.currentUserName);
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }
}
