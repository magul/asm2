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
import net.sourceforge.sheltermanager.asm.startup.Startup;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableItem;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableList;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.LDAP;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.GenerateDocument;

import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private SelectableList tblCodeOptions;
    private SelectableList tblDefaultOptions;
    private UI.Spinner spnUrgency;
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
    private UI.TextField txtMappingService;
    private UI.TextField txtEmailAddress;
    private UI.TextField txtSMTPServer;
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
    private UI.CheckBox chkUseAutoInsurance;
    private UI.Spinner spnAutoInsuranceStart;
    private UI.Spinner spnAutoInsuranceEnd;
    private UI.Spinner spnAutoInsuranceNext;
    private UI.ComboBox cboMech;
    private UI.TextField txtLDAPUrl;
    private UI.TextField txtLDAPDN;
    private UI.TextField txtLDAPFilter;
    private UI.TextField txtLDAPUser;
    private UI.TextField txtLDAPPass;

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
        ctl.add(spnUrgency);
        ctl.add(cboDefaultUrgency);
        ctl.add(txtCodingFormat);
        ctl.add(txtShortCodingFormat);
        ctl.add(tblCodeOptions);
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
        ctl.add(txtMappingService);
        ctl.add(txtEmailAddress);
        ctl.add(txtSMTPServer);
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
        ctl.add(chkUseAutoInsurance);
        ctl.add(spnAutoInsuranceStart);
        ctl.add(spnAutoInsuranceEnd);
        ctl.add(spnAutoInsuranceNext);
        ctl.add(cboMech);
        ctl.add(txtLDAPUrl);
        ctl.add(txtLDAPDN);
        ctl.add(txtLDAPFilter);
        ctl.add(txtLDAPUser);
        ctl.add(txtLDAPPass);
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

        // Waiting List
        spnUrgency.setValue(new Integer(Configuration.getInteger(
                "WaitingListUrgencyUpdatePeriod")));

        cboDefaultUrgency.setSelectedIndex(Configuration.getInteger(
                "WaitingListDefaultUrgency"));

        // Insurance Numbers
        chkUseAutoInsurance.setSelected(Configuration.getBoolean("UseAutoInsurance"));
        spnAutoInsuranceStart.setValue(new Integer(Configuration.getInteger("AutoInsuranceStart")));
        spnAutoInsuranceEnd.setValue(new Integer(Configuration.getInteger("AutoInsuranceEnd")));
        spnAutoInsuranceNext.setValue(new Integer(Configuration.getInteger("AutoInsuranceNext")));

        // Mapping Service
        txtMappingService.setText(Configuration.getString(
                "MappingServiceURL", "http://maps.google.com/maps?q="));

        // Word Processor
        String docwp = Configuration.getString("DocumentWordProcessor");

        for (int i = 0; i < cboWordProcessor.getItemCount(); i++) {
            String theitem = (String) cboWordProcessor.getItemAt(i);

            if (theitem.equalsIgnoreCase(docwp)) {
                cboWordProcessor.setSelectedIndex(i);

                break;
            }
        }
        
        // Email
        txtEmailAddress.setText(Configuration.getString("EmailAddress"));
        txtSMTPServer.setText(Configuration.getString("SMTPServer"));

        // Shelter Details
        txtOrgName.setText(Configuration.getString("Organisation"));
        txtOrgAddress.setText(Configuration.getString("OrganisationAddress"));
        txtOrgTelephone.setText(Configuration.getString("OrganisationTelephone"));
        txtOrgTelephone2.setText(Configuration.getString(
                "OrganisationTelephone2"));

        int ci = Global.getCountryIndex(Configuration.getString(
                    "OrganisationCountry"));

        if (ci == -1) {
            ci = Global.getCountryIndex(Global.getCountryForCurrentLocale());
        }

        if (ci != -1) {
            cboOrgCountry.setSelectedIndex(ci);
        }

        // Age Groups
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

	// Defaults
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

	// Authentication
	if (Configuration.getBoolean("AutoLoginOSUsers")) {
                // OS auth
                cboMech.setSelectedIndex(1);
            } else if (LDAP.isConfigured()) {
                cboMech.setSelectedIndex(2);

                Map m = LDAP.getSettings();
                txtLDAPUrl.setText((String) m.get(LDAP.LDAP_URL));
                txtLDAPDN.setText((String) m.get(LDAP.LDAP_DN));
                txtLDAPFilter.setText((String) m.get(LDAP.LDAP_FILTER));
                txtLDAPUser.setText((String) m.get(LDAP.LDAP_USER));
                txtLDAPPass.setText((String) m.get(LDAP.LDAP_PASS));
            } else {
                cboMech.setSelectedIndex(0);
            }

        // Animal Codes
        txtCodingFormat.setText(Configuration.getString("CodingFormat"));
        txtShortCodingFormat.setText(Configuration.getString(
                "ShortCodingFormat"));
    }

    /** Saves the screen results back to the database */
    public boolean saveData() {
        try {

            // Validation
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

            // Options
            SelectableItem[] l = tblOptions.getSelections();

            for (int i = 0; i < l.length; i++) {
                if ((l[i] != null) && (l[i].getValue() != null)) {
                    Configuration.setEntry(l[i].getValue().toString(),
                        (l[i].isSelected() ? "Yes" : "No"));
                }
            }

            // Word Processor
            Configuration.setEntry("DocumentWordProcessor",
                (String) cboWordProcessor.getSelectedItem());

            // Mapping Service
            Configuration.setEntry("MappingServiceURL",
                txtMappingService.getText());

            // Waiting List
            Configuration.setEntry("WaitingListUrgencyUpdatePeriod",
                spnUrgency.getValue().toString());
            Configuration.setEntry("WaitingListDefaultUrgency",
                Integer.toString(cboDefaultUrgency.getSelectedIndex()));

            // Email
            Configuration.setEntry("EmailAddress",
                txtEmailAddress.getText());
            Configuration.setEntry("SMTPServer",
                txtSMTPServer.getText());

	    // Defaults
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
            l = tblDefaultOptions.getSelections();
            for (int i = 0; i < l.length; i++) {
                if ((l[i] != null) && (l[i].getValue() != null)) {
                    Configuration.setEntry(l[i].getValue().toString(),
                        (l[i].isSelected() ? "Yes" : "No"));
                }
            }

            // Age Groups
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

            // Shelter Details
            String selcountry = cboOrgCountry.getSelectedItem().toString();
            selcountry = selcountry.substring(0, selcountry.indexOf(" "));
            Configuration.setEntry("OrganisationCountry", selcountry);
            Configuration.setEntry("OrganisationTelephone",
                txtOrgTelephone.getText().replace('\'', '`'));
            Configuration.setEntry("OrganisationTelephone2",
                txtOrgTelephone2.getText().replace('\'', '`'));
            Configuration.setEntry("Organisation",
                txtOrgName.getText().replace('\'', '`'));
            Configuration.setEntry("OrganisationAddress",
                txtOrgAddress.getText().replace('\'', '`'));

            // Auto Insurance Numbers
            Configuration.setEntry("UseAutoInsurance", chkUseAutoInsurance.isSelected() ? "Yes" : "No");
            Configuration.setEntry("AutoInsuranceStart", spnAutoInsuranceStart.getValue().toString());
            Configuration.setEntry("AutoInsuranceEnd", spnAutoInsuranceEnd.getValue().toString());
            Configuration.setEntry("AutoInsuranceNext", spnAutoInsuranceNext.getValue().toString());

	    // Authentication
            Configuration.setEntry("AutoLoginOSUsers", "No");
            DBConnection.executeAction(
                "DELETE FROM configuration WHERE ItemName Like 'LDAP%'");
            if (cboMech.getSelectedIndex() == 1) {
                Configuration.setEntry("AutoLoginOSUsers", "Yes");
            } else if (cboMech.getSelectedIndex() == 2) {
                LDAP.setSettings(txtLDAPUrl.getText(), txtLDAPDN.getText(),
                    txtLDAPFilter.getText(), txtLDAPUser.getText(),
                    txtLDAPPass.getText());
            } else {
                // DB Auth, leave reset
            }

            // Codes
            Configuration.setEntry("CodingFormat",
                txtCodingFormat.getText().replace('\'', '`'));
            Configuration.setEntry("ShortCodingFormat",
                txtShortCodingFormat.getText().replace('\'', '`'));
            l = tblCodeOptions.getSelections();
            for (int i = 0; i < l.length; i++) {
                if ((l[i] != null) && (l[i].getValue() != null)) {
                    Configuration.setEntry(l[i].getValue().toString(),
                        (l[i].isSelected() ? "Yes" : "No"));
                }
            }

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
        UI.Panel detop = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70}));
        UI.Panel demid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70}));
        UI.Panel debot = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70}));

        txtOrgName = (UI.TextField) UI.addComponent(detop,
                i18n("Organisation_Name:"),
                UI.getTextField(i18n("Your_organisations_name")));

        txtOrgAddress = (UI.TextArea) UI.addComponent(demid,
                i18n("Organisation_Address:"),
                UI.getTextArea(i18n("Your_organisations_address")));

        cboOrgCountry = UI.getCombo(Global.getCountries());
        UI.addComponent(debot, i18n("Country:"), cboOrgCountry);

        txtOrgTelephone = (UI.TextField) UI.addComponent(debot,
                i18n("Organisation_Telephone:"),
                UI.getTextField(i18n("Your_organisations_telephone")));

        txtOrgTelephone2 = (UI.TextField) UI.addComponent(debot,
                i18n("Organisation_Telephone:"),
                UI.getTextField(i18n("Your_organisations_telephone")));

        UI.Panel details = UI.getPanel(UI.getBorderLayout());
        details.add(detop, UI.BorderLayout.NORTH);
        details.add(demid, UI.BorderLayout.CENTER);
        details.add(debot, UI.BorderLayout.SOUTH);
        tabTabs.addTab(i18n("shelter_info"), null, details, null);

        // Word processor options
        UI.Panel pw = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70}));

        cboWordProcessor = UI.getCombo(new String[] {
                    GenerateDocument.OPENOFFICE_3, GenerateDocument.OPENOFFICE_2,
                    GenerateDocument.OPENOFFICE_1,
                    GenerateDocument.MICROSOFT_OFFICE_2007,
                    GenerateDocument.ABIWORD, GenerateDocument.RICH_TEXT,
                    GenerateDocument.XML, GenerateDocument.HTML
                });
        UI.addComponent(pw, i18n("Word_Processor:"), cboWordProcessor);

        UI.Panel wordprocessor = UI.getPanel(UI.getBorderLayout());
        wordprocessor.add(pw, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("word_processor"), null, wordprocessor, null);

        // Waiting list options
        UI.Panel pl = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70}));
        spnUrgency = (UI.Spinner) UI.addComponent(pl,
                i18n("Update_Waiting_List_Period:"),
                UI.getSpinner(0, 365, i18n("The_interval_at_which_the_waiting_list_urgencies_should_be_updated_in_days"), null));

        cboDefaultUrgency = UI.getCombo(LookupCache.getUrgencyLookup(),
                "Urgency");
        UI.addComponent(pl, i18n("default_waiting_list_urgency"),
            cboDefaultUrgency);
        UI.Panel waitinglist = UI.getPanel(UI.getBorderLayout());
        waitinglist.add(pl, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("waiting_list"), null, waitinglist, null);

        // Animal code options
        UI.Panel codepanel = UI.getPanel(UI.getBorderLayout());
        UI.Panel pc = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        txtCodingFormat = (UI.TextField) UI.addComponent(pc,
                i18n("coding_format"),
                UI.getTextField(i18n("coding_format_tooltip")));

        txtShortCodingFormat = (UI.TextField) UI.addComponent(pc,
                i18n("short_coding_format"),
                UI.getTextField(i18n("short_coding_format_tooltip")));

           
        List l = new ArrayList();
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
        tblCodeOptions = new SelectableList(l);
        UI.addComponent(codepanel, tblCodeOptions);

        codepanel.add(pc, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("animal_codes"), null, codepanel, null);

        // Mapping service options
        UI.Panel pm = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        txtMappingService = (UI.TextField) UI.addComponent(pm,
                i18n("mapping_service_url"),
                UI.getTextField());
        txtMappingService.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 3, UI.getTextBoxHeight()));
        UI.Panel mappingservice = UI.getPanel(UI.getBorderLayout());
        mappingservice.add(pm, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("mapping_service"), null, mappingservice, null);

        // Age groups
        UI.Panel pa = UI.getPanel(UI.getGridLayout(3, new int[] { 20, 20, 60 }));
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

        UI.Panel agegroups = UI.getPanel(UI.getBorderLayout());
        agegroups.add(pa, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("age_groups"), null, agegroups, null);

	// Email
        UI.Panel pemail = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        UI.Panel email = UI.getPanel(UI.getBorderLayout());

        txtEmailAddress = (UI.TextField) UI.addComponent(pemail, i18n("email_address"),
                UI.getTextField(i18n("emails_from_ASM_come_from")));

        txtSMTPServer = (UI.TextField) UI.addComponent(pemail, i18n("smtp_server"),
                UI.getTextField(i18n("address_of_smtp_server")));

        email.add(pemail, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("email"), null, email, null);

	// Defaults
	UI.Panel pr = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
	UI.Panel defaults = UI.getPanel(UI.getBorderLayout());

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
        
        l = new ArrayList();
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
                    "Auto_attach_generated_media"),
                "AutoAttachMedia",
                Configuration.getString("AutoAttachMedia")
                             .equalsIgnoreCase("Yes"), false));

        tblDefaultOptions = new SelectableList(l);

	defaults.add(pr, UI.BorderLayout.NORTH);
        UI.addComponent(defaults, tblDefaultOptions);
        tabTabs.addTab(i18n("Defaults"), null, defaults, null);

        // Automatic Insurance Numbers
        UI.Panel pins = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel insurancenumbers = UI.getPanel(UI.getBorderLayout());

        chkUseAutoInsurance = (UI.CheckBox) UI.addComponent(pins, i18n("Use_Automatic_Insurance_Numbers"), 
            UI.getCheckBox());

        spnAutoInsuranceStart = (UI.Spinner) UI.addComponent(pins, i18n("Start_At:"), UI.getSpinner(0, 400000000));
        spnAutoInsuranceEnd = (UI.Spinner) UI.addComponent(pins, i18n("End_At:"), UI.getSpinner(0, 400000000));
        spnAutoInsuranceNext = (UI.Spinner) UI.addComponent(pins, i18n("Next:"), UI.getSpinner(0,  400000000));

        insurancenumbers.add(pins, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("Automatic_Insurance_Numbers"), null, insurancenumbers, null);

	// Authentication
        UI.Panel auth = UI.getPanel(UI.getBorderLayout());
        UI.Panel authmid = UI.getPanel(UI.getBorderLayout());
        UI.Panel pmech = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pldap = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));

	cboMech = UI.getCombo();
        cboMech.addItem(i18n("Database") + " - " + i18n("Database_Desc"));
        cboMech.addItem(i18n("Operating_System") + " - " +
            i18n("Operating_System_Desc"));
        cboMech.addItem(i18n("LDAP") + " - " + i18n("LDAP_Desc"));
        cboMech.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 3,
                UI.getComboBoxHeight()));
        UI.addComponent(pmech, i18n("Authentication"), cboMech);

        pldap.setTitle(i18n("LDAP"));
        txtLDAPUrl = (UI.TextField) UI.addComponent(pldap, i18n("LDAP_URL"),
                UI.getTextField());
        txtLDAPDN = (UI.TextField) UI.addComponent(pldap, i18n("LDAP_DN"),
                UI.getTextField());
        txtLDAPFilter = (UI.TextField) UI.addComponent(pldap, i18n("LDAP_Filter"),
                UI.getTextField());
        txtLDAPUser = (UI.TextField) UI.addComponent(pldap, i18n("LDAP_User"),
                UI.getTextField());
        txtLDAPPass = (UI.TextField) UI.addComponent(pldap, i18n("LDAP_Pass"),
                UI.getTextField());

        authmid.add(pldap, UI.BorderLayout.NORTH);
        auth.add(pmech, UI.BorderLayout.NORTH);
        auth.add(authmid, UI.BorderLayout.CENTER);

        // Authentication doesn't make sense with an applet
        // user supplied, since auth is taken care of before
        // applet page is loaded
        if (Startup.appletUser == null) {
            tabTabs.addTab(i18n("Authentication"), null, auth, null);
        }

        // Options
        l = new ArrayList();
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

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_filter_the_list_of_breeds_to_mapped_species"),
                    "DontFilterBreedList",
                    Configuration.getString("DontFilterBreedList")
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
                        "Dont_show_media_thumbnails_on_details_screen"),
                    "NoMediaThumbnails",
                    Configuration.getString("NoMediaThumbnails")
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

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "disable_retailer_functionality"), "DisableRetailer",
                    Configuration.getString("DisableRetailer")
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
