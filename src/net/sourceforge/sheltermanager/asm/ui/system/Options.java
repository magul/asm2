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

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableItem;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableList;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.wordprocessor.GenerateDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * System Options form
 *
 * @author Robin Rawson-Tetley
 */
public class Options extends ASMForm {
    private UI.TabbedPane tabTabs;
    private UI.Button btnSave;
    private UI.Button btnClose;
    private UI.ComboBox cboDefaultUrgency;
    private UI.ComboBox cboWordProcessor;
    private UI.Panel pnlButtons;
    private UI.Panel pnlDoc;
    private SelectableList tblOptions;
    private UI.TextField txtUrgency;
    private UI.TextField txtOrgName;
    private UI.TextArea txtOrgAddress;
    private UI.ComboBox cboOrgCountry;
    private UI.TextField txtOrgTelephone;
    private UI.TextField txtOrgTelephone2;
    private UI.TextField txtCodingFormat;
    private UI.TextField txtShortCodingFormat;
    private UI.TextField txtAgeGroup1;
    private UI.TextField txtAgeGroup1Name;
    private UI.TextField txtAgeGroup2;
    private UI.TextField txtAgeGroup2Name;
    private UI.TextField txtAgeGroup3;
    private UI.TextField txtAgeGroup3Name;
    private UI.TextField txtAgeGroup4;
    private UI.TextField txtAgeGroup4Name;
    private UI.TextField txtAgeGroup5;
    private UI.TextField txtAgeGroup5Name;

    /** Creates new form Options */
    public Options() {
        init(Global.i18n("uisystem", "System_Options"),
            IconManager.getIcon(IconManager.SCREEN_OPTIONS), "uisystem");
        loadData();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtOrgName);
        ctl.add(txtOrgAddress);
        ctl.add(txtOrgTelephone);
        ctl.add(txtOrgTelephone2);
        ctl.add(cboWordProcessor);
        ctl.add(txtUrgency);
        ctl.add(cboDefaultUrgency);
        ctl.add(txtCodingFormat);
        ctl.add(txtShortCodingFormat);
        ctl.add(txtAgeGroup1);
        ctl.add(txtAgeGroup1Name);
        ctl.add(txtAgeGroup2);
        ctl.add(txtAgeGroup2Name);
        ctl.add(txtAgeGroup3);
        ctl.add(txtAgeGroup3Name);
        ctl.add(txtAgeGroup4);
        ctl.add(txtAgeGroup4Name);
        ctl.add(txtAgeGroup5);
        ctl.add(txtAgeGroup5Name);
        ctl.add(tblOptions);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboWordProcessor;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    /** Loads the data and fills the boxes */
    public void loadData() {
        txtUrgency.setText(Configuration.getString(
                "WaitingListUrgencyUpdatePeriod"));

        cboDefaultUrgency.setSelectedIndex(Configuration.getInteger(
                "WaitingListDefaultUrgency"));

        String docwp = Configuration.getString("DocumentWordProcessor");

        for (int i = 0; i < cboWordProcessor.getItemCount(); i++) {
            String theitem = (String) cboWordProcessor.getItemAt(i);

            if (theitem.equalsIgnoreCase(docwp)) {
                cboWordProcessor.setSelectedIndex(i);

                break;
            }
        }

        txtOrgName.setText(Configuration.getString("Organisation"));
        txtOrgAddress.setText(Configuration.getString("OrganisationAddress"));
        txtOrgTelephone.setText(Configuration.getString("OrganisationTelephone"));
        txtOrgTelephone2.setText(Configuration.getString(
                "OrganisationTelephone2"));

        txtAgeGroup1.setText(Configuration.getString("AgeGroup1", ""));
        txtAgeGroup1Name.setText(Configuration.getString("AgeGroup1Name", ""));
        txtAgeGroup2.setText(Configuration.getString("AgeGroup2", ""));
        txtAgeGroup2Name.setText(Configuration.getString("AgeGroup2Name", ""));
        txtAgeGroup3.setText(Configuration.getString("AgeGroup3", ""));
        txtAgeGroup3Name.setText(Configuration.getString("AgeGroup3Name", ""));
        txtAgeGroup4.setText(Configuration.getString("AgeGroup4", ""));
        txtAgeGroup4Name.setText(Configuration.getString("AgeGroup4Name", ""));
        txtAgeGroup5.setText(Configuration.getString("AgeGroup5", ""));
        txtAgeGroup5Name.setText(Configuration.getString("AgeGroup5Name", ""));

        int ci = Global.getCountryIndex(Configuration.getString(
                    "OrganisationCountry"));

        if (ci == -1) {
            ci = Global.getCountryIndex(Global.getCountryForCurrentLocale());
        }

        if (ci != -1) {
            cboOrgCountry.setSelectedIndex(ci);
        }

        txtCodingFormat.setText(Configuration.getString("CodingFormat"));
        txtShortCodingFormat.setText(Configuration.getString(
                "ShortCodingFormat"));
    }

    /** Saves the screen results back to the database */
    public boolean saveData() {
        try {
            // Validate
            if ((txtCodingFormat.getText().indexOf("U") != -1) &&
                    (txtCodingFormat.getText().indexOf("N") != -1)) {
                Dialog.showError(i18n("Invalid_codeformat_not_allowed_both"),
                    i18n("Invalid_codeformat"));

                return false;
            }

            if ((txtShortCodingFormat.getText().indexOf("U") != -1) &&
                    (txtShortCodingFormat.getText().indexOf("N") != -1)) {
                Dialog.showError(i18n("Invalid_codeformat_not_allowed_both"),
                    i18n("Invalid_codeformat"));

                return false;
            }

            SelectableItem[] l = tblOptions.getSelections();

            for (int i = 0; i < l.length; i++) {
                if ((l[i] != null) && (l[i].getValue() != null)) {
                    Configuration.setEntry(l[i].getValue().toString(),
                        (l[i].isSelected() ? "Yes" : "No"));
                }
            }

            Configuration.setEntry("DocumentWordProcessor",
                (String) cboWordProcessor.getSelectedItem());
            Configuration.setEntry("WaitingListUrgencyUpdatePeriod",
                txtUrgency.getText());
            Configuration.setEntry("WaitingListDefaultUrgency",
                Integer.toString(cboDefaultUrgency.getSelectedIndex()));
            Configuration.setEntry("Organisation",
                txtOrgName.getText().replace('\'', '`'));
            Configuration.setEntry("OrganisationAddress",
                txtOrgAddress.getText().replace('\'', '`'));

            Configuration.setEntry("AgeGroup1", txtAgeGroup1.getText());
            Configuration.setEntry("AgeGroup1Name", txtAgeGroup1Name.getText());
            Configuration.setEntry("AgeGroup2", txtAgeGroup2.getText());
            Configuration.setEntry("AgeGroup2Name", txtAgeGroup2Name.getText());
            Configuration.setEntry("AgeGroup3", txtAgeGroup3.getText());
            Configuration.setEntry("AgeGroup3Name", txtAgeGroup3Name.getText());
            Configuration.setEntry("AgeGroup4", txtAgeGroup4.getText());
            Configuration.setEntry("AgeGroup4Name", txtAgeGroup4Name.getText());
            Configuration.setEntry("AgeGroup5", txtAgeGroup5.getText());
            Configuration.setEntry("AgeGroup5Name", txtAgeGroup5Name.getText());

            String selcountry = cboOrgCountry.getSelectedItem().toString();
            selcountry = selcountry.substring(0, selcountry.indexOf(" "));
            Configuration.setEntry("OrganisationCountry", selcountry);
            Configuration.setEntry("OrganisationTelephone",
                txtOrgTelephone.getText().replace('\'', '`'));
            Configuration.setEntry("OrganisationTelephone2",
                txtOrgTelephone2.getText().replace('\'', '`'));
            Configuration.setEntry("CodingFormat",
                txtCodingFormat.getText().replace('\'', '`'));
            Configuration.setEntry("ShortCodingFormat",
                txtShortCodingFormat.getText().replace('\'', '`'));

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uisystem",
                    "An_error_occurred_saving_the_data:_") + e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void tabChanged() {
    }

    public void initComponents() {
        tabTabs = UI.getTabbedPane(UI.fp(this, "tabChanged"));

        // Shelter info
        UI.Panel pd = UI.getPanel(UI.getTableLayout(2));
        txtOrgName = (UI.TextField) UI.addComponent(pd,
                i18n("Organisation_Name:"),
                UI.getTextField(i18n("Your_organisations_name")));
        txtOrgName.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 2,
                UI.getTextBoxHeight()));

        txtOrgAddress = (UI.TextArea) UI.addComponent(pd,
                i18n("Organisation_Address:"),
                UI.getTextArea(i18n("Your_organisations_address")));

        cboOrgCountry = UI.getCombo(Global.getCountries());
        cboOrgCountry.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 2, UI.getComboBoxHeight()));
        UI.addComponent(pd, i18n("Country:"), cboOrgCountry);

        txtOrgTelephone = (UI.TextField) UI.addComponent(pd,
                i18n("Organisation_Telephone:"),
                UI.getTextField(i18n("Your_organisations_telephone")));
        txtOrgTelephone.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 2, UI.getTextBoxHeight()));

        txtOrgTelephone2 = (UI.TextField) UI.addComponent(pd,
                i18n("Organisation_Telephone:"),
                UI.getTextField(i18n("Your_organisations_telephone")));
        txtOrgTelephone2.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 2, UI.getTextBoxHeight()));

        tabTabs.addTab(i18n("shelter_info"), null, pd, null);

        // Word processor options
        UI.Panel pw = UI.getPanel(UI.getTableLayout(2));

        cboWordProcessor = UI.getCombo(new String[] {
                    GenerateDocument.OPENOFFICE_3, GenerateDocument.OPENOFFICE_2,
                    GenerateDocument.OPENOFFICE_1,
                    GenerateDocument.MICROSOFT_OFFICE_2007,
                    GenerateDocument.ABIWORD, GenerateDocument.RICH_TEXT,
                    GenerateDocument.XML, GenerateDocument.HTML
                });
        UI.addComponent(pw, i18n("Word_Processor:"), cboWordProcessor);
        tabTabs.addTab(i18n("word_processor"), null, pw, null);

        // Waiting list options
        UI.Panel pl = UI.getPanel(UI.getTableLayout(2));
        txtUrgency = (UI.TextField) UI.addComponent(pl,
                i18n("Update_Waiting_List_Period:"),
                UI.getTextField(i18n("The_interval_at_which_the_waiting_list_urgencies_should_be_updated_in_days")));

        cboDefaultUrgency = UI.getCombo(LookupCache.getUrgencyLookup(),
                "Urgency");
        UI.addComponent(pl, i18n("default_waiting_list_urgency"),
            cboDefaultUrgency);
        tabTabs.addTab(i18n("waiting_list"), null, pl, null);

        // Animal code options
        UI.Panel pc = UI.getPanel(UI.getTableLayout(2));
        txtCodingFormat = (UI.TextField) UI.addComponent(pc,
                i18n("coding_format"),
                UI.getTextField(i18n("coding_format_tooltip")));

        txtShortCodingFormat = (UI.TextField) UI.addComponent(pc,
                i18n("short_coding_format"),
                UI.getTextField(i18n("short_coding_format_tooltip")));

        tabTabs.addTab(i18n("animal_codes"), null, pc, null);

        // Age groups
        UI.Panel pa = UI.getPanel(UI.getTableLayout(3));
        txtAgeGroup1 = (UI.TextField) UI.addComponent(pa, i18n("age_group_1"),
                UI.getTextField());
        txtAgeGroup1Name = (UI.TextField) UI.addComponent(pa, UI.getTextField());
        txtAgeGroup2 = (UI.TextField) UI.addComponent(pa, i18n("age_group_2"),
                UI.getTextField());
        txtAgeGroup2Name = (UI.TextField) UI.addComponent(pa, UI.getTextField());
        txtAgeGroup3 = (UI.TextField) UI.addComponent(pa, i18n("age_group_3"),
                UI.getTextField());
        txtAgeGroup3Name = (UI.TextField) UI.addComponent(pa, UI.getTextField());
        txtAgeGroup4 = (UI.TextField) UI.addComponent(pa, i18n("age_group_4"),
                UI.getTextField());
        txtAgeGroup4Name = (UI.TextField) UI.addComponent(pa, UI.getTextField());
        txtAgeGroup5 = (UI.TextField) UI.addComponent(pa, i18n("age_group_5"),
                UI.getTextField());
        txtAgeGroup5Name = (UI.TextField) UI.addComponent(pa, UI.getTextField());

        tabTabs.addTab(i18n("age_groups"), null, pa, null);

        // Options
        List l = new ArrayList();

        try {
            // Warnings
            l.add(new SelectableItem(Global.i18n("uisystem", "Warnings"), null,
                    false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_on_close_if_an_animal_has_no_pending_vaccinations"),
                    "WarnNoPendingVacc",
                    Configuration.getString("WarnNoPendingVacc")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_when_adopting_to_a_non-homechecked_owner"),
                    "WarnNoHomeCheck",
                    Configuration.getString("WarnNoHomeCheck")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_when_adopting_to_a_banned_owner"),
                    "WarnBannedOwner",
                    Configuration.getString("WarnBannedOwner")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_when_adopting_to_an_owner_in_the_same_postcode_as_the_original_owner"),
                    "WarnOOPostcode",
                    Configuration.getString("WarnOOPostcode")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_when_adopting_to_an_owner_who_has_brought_an_animal_in"),
                    "WarnBroughtIn",
                    Configuration.getString("WarnBroughtIn")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_on_multiple_reservations"),
                    "WarnMultipleReserves",
                    Configuration.getString("WarnMultipleReserves")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Warn_and_cancel_additional_reservations_when_an_animal_is_adopted"),
                    "CancelReservesOnAdoption",
                    Configuration.getString("CancelReservesOnAdoption")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "When_entering_new_owners,_check_for_similar_names"),
                    "OwnerNameCheck",
                    Configuration.getString("OwnerNameCheck")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "When_entering_new_owners,_check_for_similar_addresses"),
                    "OwnerAddressCheck",
                    Configuration.getString("OwnerAddressCheck")
                                 .equalsIgnoreCase("Yes"), false));

            // Coding
            l.add(new SelectableItem(Global.i18n("uisystem", "Coding_System"),
                    null, false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "show_short_shelter_code"), "UseShortShelterCodes",
                    Configuration.getString("UseShortShelterCodes")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Check_animal_codes_entered_by_users_conform_to_selected_scheme"),
                    "StrictAutoCodes",
                    Configuration.getString("StrictAutoCodes")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "auto_default_shelter_code_for_new_animals"),
                    "AutoDefaultShelterCode",
                    Configuration.getString("AutoDefaultShelterCode")
                                 .equalsIgnoreCase("Yes"), false));

            // Format and Display
            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Format_and_Display"), null, false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Show_Internal_Location_of_animals_who_have_left_the_shelter_as_logical_location_instead"),
                    "ShowILOffShelter",
                    Configuration.getString("ShowILOffShelter")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Group_the_animal_figures_report_by_species_instead_of_type"),
                    "AnimalFiguresGroupBySpecies",
                    Configuration.getString("AnimalFiguresGroupBySpecies")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Highlight_animals_under_6_months,_reserved_or_dead_on_reports"),
                    "HighlightReportAnimals",
                    Configuration.getString("HighlightReportAnimals")
                                 .equalsIgnoreCase("Yes"), false));

            // Database and System
            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Database_and_System"), null, false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Use_optimistic_record_locking_on_animal_database"),
                    "UseAnimalRecordLock",
                    Configuration.getString("UseAnimalRecordLock")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Case_sensitive_searching"), "CaseSensitiveSearch",
                    Configuration.getString("CaseSensitiveSearch")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Automatic_Litter_Identification"),
                    "AutoLitterIdentification",
                    Configuration.getString("AutoLitterIdentification")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "foster_as_on_shelter"), "FosterOnShelter",
                    Configuration.getString("FosterOnShelter")
                                 .equalsIgnoreCase("Yes"), false));
            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Cache_active_animals"), "CacheActiveAnimals",
                    Configuration.getBoolean("CacheActiveAnimals"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Lazy_load_animal_tabs"), "LazyLoadAnimalTabs",
                    Configuration.getBoolean("LazyLoadAnimalTabs"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_autoarchive_animal_records_on_exit"),
                    "DontAutoArchiveOnExit",
                    Configuration.getString("DontAutoArchiveOnExit")
                                 .equalsIgnoreCase("Yes"), false));

            // Defaults
            l.add(new SelectableItem(Global.i18n("uisystem", "Defaults"), null,
                    false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "mark_new_animals_not_for_adoption"),
                    "AutoNotForAdoption",
                    Configuration.getString("AutoNotForAdoption")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Default_today_as_the_reservation_date_for_new_movements"),
                    "AutoReservationDate",
                    Configuration.getString("AutoReservationDate")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Prefill_new_media_notes_with_animal_comments"),
                    "AutoMediaNotes",
                    Configuration.getString("AutoMediaNotes")
                                 .equalsIgnoreCase("Yes"), false));

            // Features
            l.add(new SelectableItem(Global.i18n("uisystem", "Features"), null,
                    false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Default_to_advanced_find_animal_screen"),
                    "AdvancedFindAnimal",
                    Configuration.getString("AdvancedFindAnimal")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Default_to_advanced_find_owner_screen"),
                    "AdvancedFindOwner",
                    Configuration.getString("AdvancedFindOwner")
                                 .equalsIgnoreCase("Yes"), false));


            l.add(new SelectableItem(Global.i18n("uisystem",
                        "When_adding_animals_suggest_popular_breeds"),
                    "SuggestPopularBreeds",
                    Configuration.getString("SuggestPopularBreeds")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_show_the_coat_type_field"), "DontShowCoatType",
                    Configuration.getString("DontShowCoatType")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "disable_retailer_functionality"), "DisableRetailer",
                    Configuration.getString("DisableRetailer")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_filter_the_list_of_breeds_to_mapped_species"),
                    "DontFilterBreedList",
                    Configuration.getString("DontFilterBreedList")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_show_media_thumbnails_on_details_screen"),
                    "NoMediaThumbnails",
                    Configuration.getString("NoMediaThumbnails")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_keep_separate_town_and_county_information"),
                    "HideTownCounty",
                    Configuration.getString("HideTownCounty")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Use_a_single_breed_field"), "UseSingleBreedField",
                    Configuration.getBoolean("UseSingleBreedField"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_show_the_startup_page"), "DontShowStartupPage",
                    Configuration.getBoolean("DontShowStartupPage"), false));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tblOptions = new SelectableList(l);
        tabTabs.addTab(i18n("options"), null, tblOptions, null);

        UI.ToolBar t = new UI.ToolBar();
        btnSave = (UI.Button) t.add(UI.getButton(null,
                    i18n("Save_your_changes_and_exit"), 's',
                    IconManager.getIcon(IconManager.SCREEN_OPTIONS_SAVE),
                    UI.fp(this, "saveData")));
        /*btnClose = (UI.Button) t.add(UI.getButton(null, null, 'x',
                    IconManager.getIcon(IconManager.SCREEN_OPTIONS_CLOSE),
                    UI.fp(this, "dispose")));*/
        add(t, UI.BorderLayout.NORTH);
        add(tabTabs, UI.BorderLayout.CENTER);
    }
}
