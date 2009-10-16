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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.bo.AnimalType;
import net.sourceforge.sheltermanager.asm.bo.BaseColour;
import net.sourceforge.sheltermanager.asm.bo.Breed;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.DeathReason;
import net.sourceforge.sheltermanager.asm.bo.EntryReason;
import net.sourceforge.sheltermanager.asm.bo.InternalLocation;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Species;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.TitleLabel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * Report configuration form
 *
 * @author Robin Rawson-Tetley
 */
public class ConfigureReportsDefaults extends ASMForm {
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboDefaultBreed;
    private UI.ComboBox cboDefaultColour;
    private UI.ComboBox cboDefaultDeath;
    private UI.ComboBox cboDefaultEntryReason;
    private UI.ComboBox cboDefaultInternalLocation;
    private UI.ComboBox cboDefaultReturn;
    private UI.ComboBox cboDefaultSize;
    private UI.ComboBox cboNonShelter;
    private UI.ComboBox cboDefaultSpecies;
    private UI.ComboBox cboDefaultType;

    public ConfigureReportsDefaults() {
        init(Global.i18n("uisystem", "Configure_Defaults"),
            IconManager.getIcon(IconManager.SCREEN_CONFIGUREREPORTS), "uisystem");
        loadData();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboDefaultSpecies);
        ctl.add(cboDefaultType);
        ctl.add(cboNonShelter);
        ctl.add(cboDefaultInternalLocation);
        ctl.add(cboDefaultEntryReason);
        //ctl.add(cboDefaultBreed);
        ctl.add(cboDefaultColour);
        ctl.add(cboDefaultDeath);
        ctl.add(cboDefaultReturn);
        ctl.add(cboDefaultSize);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboDefaultSpecies;
    }

    public void loadData() {
        try {
            Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
                new Integer(Configuration.getInteger("AFDefaultSpecies")),
                cboDefaultSpecies);

            Utils.setComboFromID(LookupCache.getAnimalTypeLookup(),
                "AnimalType",
                new Integer(Configuration.getInteger("AFDefaultType")),
                cboDefaultType);

            Utils.setComboFromID(LookupCache.getAnimalTypeLookup(),
                "AnimalType",
                new Integer(Configuration.getInteger("AFNonShelterType")),
                cboNonShelter);

            Utils.setComboFromID(LookupCache.getInternalLocationLookup(),
                "LocationName",
                new Integer(Configuration.getInteger("AFDefaultLocation")),
                cboDefaultInternalLocation);

            Utils.setComboFromID(LookupCache.getEntryReasonLookup(),
                "ReasonName",
                new Integer(Configuration.getInteger("AFDefaultEntryReason")),
                cboDefaultEntryReason);

            Utils.setComboFromID(LookupCache.getEntryReasonLookup(),
                "ReasonName",
                new Integer(Configuration.getInteger("AFDefaultReturnReason")),
                cboDefaultReturn);

            Utils.setComboFromID(LookupCache.getDeathReasonLookup(),
                "ReasonName",
                new Integer(Configuration.getInteger("AFDefaultDeathReason")),
                cboDefaultDeath);

            //Utils.setComboFromID("breed", "BreedName",
            //    new Integer(Configuration.getInteger("AFDefaultBreed")),
            //    cboDefaultBreed);
            Utils.setComboFromID(LookupCache.getBaseColourLookup(),
                "BaseColour",
                new Integer(Configuration.getInteger("AFDefaultColour")),
                cboDefaultColour);

            cboDefaultSize.setSelectedIndex(Configuration.getInteger(
                    "AFDefaultSize"));
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_loading_the_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        try {
            Configuration.setEntry("AFDefaultSpecies",
                Utils.getIDFromCombo(LookupCache.getSpeciesLookup(),
                    "SpeciesName", cboDefaultSpecies).toString());
            Configuration.setEntry("AFDefaultType",
                Utils.getIDFromCombo(LookupCache.getAnimalTypeLookup(),
                    "AnimalType", cboDefaultType).toString());
            Configuration.setEntry("AFNonShelterType",
                Utils.getIDFromCombo(LookupCache.getAnimalTypeLookup(),
                    "AnimalType", cboNonShelter).toString());
            Configuration.setEntry("AFDefaultLocation",
                Utils.getIDFromCombo(LookupCache.getInternalLocationLookup(),
                    "LocationName", cboDefaultInternalLocation).toString());
            Configuration.setEntry("AFDefaultEntryReason",
                Utils.getIDFromCombo(LookupCache.getEntryReasonLookup(),
                    "ReasonName", cboDefaultEntryReason).toString());
            Configuration.setEntry("AFDefaultReturnReason",
                Utils.getIDFromCombo(LookupCache.getEntryReasonLookup(),
                    "ReasonName", cboDefaultReturn).toString());
            Configuration.setEntry("AFDefaultDeathReason",
                Utils.getIDFromCombo(LookupCache.getDeathReasonLookup(),
                    "ReasonName", cboDefaultDeath).toString());
            //Configuration.setEntry("AFDefaultBreed",
            //    Utils.getIDFromCombo("breed", "BreedName", cboDefaultBreed)
            //         .toString());
            Configuration.setEntry("AFDefaultColour",
                Utils.getIDFromCombo(LookupCache.getBaseColourLookup(),
                    "BaseColour", cboDefaultColour).toString());
            Configuration.setEntry("AFDefaultSize",
                Integer.toString(cboDefaultSize.getSelectedIndex()));

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_saving_the_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public void initComponents() {
        UI.Panel pr = UI.getPanel(UI.getTableLayout(2));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());

        cboDefaultSpecies = UI.getCombo(i18n("default_species"),
                LookupCache.getSpeciesLookup(), "SpeciesName", i18n("None"));
        UI.addComponent(pr, i18n("default_species"), cboDefaultSpecies);

        cboDefaultType = UI.getCombo(i18n("default_type"),
                LookupCache.getAnimalTypeLookup(), "AnimalType", i18n("None"));
        UI.addComponent(pr, i18n("default_type"), cboDefaultType);

        cboNonShelter = UI.getCombo(i18n("Non_Shelter_Animal_Type:"),
                LookupCache.getAnimalTypeLookup(), "AnimalType", i18n("None"));
        UI.addComponent(pr, i18n("Non_Shelter_Animal_Type:"), cboNonShelter);

        cboDefaultInternalLocation = UI.getCombo(i18n("Default_Internal_Location:"),
                LookupCache.getInternalLocationLookup(), "LocationName");
        UI.addComponent(pr, i18n("Default_Internal_Location:"),
            cboDefaultInternalLocation);

        cboDefaultEntryReason = UI.getCombo(i18n("Default_Entry_Reason:"),
                LookupCache.getEntryReasonLookup(), "ReasonName");
        UI.addComponent(pr, i18n("Default_Entry_Reason:"), cboDefaultEntryReason);

        //cboDefaultBreed = UI.getCombo(i18n("Default_Breed:"), LookupCache.getBreedLookup(), "BreedName");
        //UI.addComponent(pd, i18n("Default_Breed:"), cboDefaultBreed);
        cboDefaultColour = UI.getCombo(i18n("Default_Colour:"),
                LookupCache.getBaseColourLookup(), "BaseColour");
        UI.addComponent(pr, i18n("Default_Colour:"), cboDefaultColour);

        cboDefaultDeath = UI.getCombo(i18n("Default_Death_Reason:"),
                LookupCache.getDeathReasonLookup(), "ReasonName");
        UI.addComponent(pr, i18n("Default_Death_Reason:"), cboDefaultDeath);

        cboDefaultReturn = UI.getCombo(i18n("Default_Return_Reason:"),
                LookupCache.getEntryReasonLookup(), "ReasonName");
        UI.addComponent(pr, i18n("Default_Return_Reason:"), cboDefaultReturn);

        cboDefaultSize = UI.getCombo(LookupCache.getSizeLookup(), "Size");
        UI.addComponent(pr, i18n("Default_Size:"), cboDefaultSize);

        btnOk = (UI.Button) pb.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) pb.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));
        pr.add(pb);
        add(pr, UI.BorderLayout.CENTER);
    }
}
