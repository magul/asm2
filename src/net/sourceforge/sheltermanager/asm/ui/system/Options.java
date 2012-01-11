/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.Account;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.internet.AvidRegistration;
import net.sourceforge.sheltermanager.asm.startup.Startup;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFindColumns;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerFindColumns;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerLink;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableItem;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableList;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.waitinglist.WaitingListViewColumns;
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
@SuppressWarnings("serial")
public class Options extends ASMForm {
    private UI.TabbedPane tabTabs;
    private UI.ComboBox cboDefaultUrgency;
    private UI.ComboBox cboWordProcessor;
    private DateField dtAccountingPeriod;
    private SelectableList tblOptions;
    private SelectableList tblCodeOptions;
    private SelectableList tblDefaultOptions;
    private SelectableList tblAccountOptions;
    private SelectableList tblLostAndFoundOptions;
    private UI.Spinner spnUrgency;
    private UI.Spinner spnCancelReserves;
    private UI.Spinner spnMatchSpecies;
    private UI.Spinner spnMatchBreed;
    private UI.Spinner spnMatchAge;
    private UI.Spinner spnMatchSex;
    private UI.Spinner spnMatchAreaLost;
    private UI.Spinner spnMatchFeatures;
    private UI.Spinner spnMatchPostcode;
    private UI.Spinner spnMatchColour;
    private UI.Spinner spnMatchDateWithin2Weeks;
    private UI.Spinner spnMatchPointFloor;
    private UI.TextField txtOrgName;
    private UI.TextArea txtOrgAddress;
    private UI.ComboBox cboOrgCountry;
    private UI.ComboBox cboDonationTargetAccount;
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
    private UI.TextField txtAvidUrl;
    private UI.TextField txtAvidOrgName;
    private UI.TextField txtAvidOrgSerial;
    private UI.TextField txtAvidOrgPostcode;
    private UI.TextField txtAvidOrgPassword;
    private UI.TextArea txtEmailSignature;
    private UI.TextArea txtTPPublisherSig;
    private UI.TextArea txtSearchColumns;
    private UI.TextArea txtOwnerSearchColumns;
    private UI.TextArea txtWLViewColumns;
    private CurrencyField txtDefaultBoardingCost;
    private UI.CheckBox chkCreateBoardingCostAdoption;
    private UI.CheckBox chkRankBySpecies;
    private UI.CheckBox chkDisableWaitingList;
    private UI.ComboBox cboAccountView;
    private UI.ComboBox cboBoardingType;
    private UI.ComboBox cboDefaultColour;
    private UI.ComboBox cboDefaultDeath;
    private UI.ComboBox cboDefaultEntryReason;
    private UI.ComboBox cboDefaultInternalLocation;
    private UI.ComboBox cboDefaultLogFilter;
    private UI.ComboBox cboDefaultCoatType;
    private UI.ComboBox cboDefaultReturn;
    private UI.ComboBox cboDefaultSize;
    private UI.ComboBox cboNonShelter;
    private UI.ComboBox cboDefaultSpecies;
    private UI.ComboBox cboDefaultType;
    private UI.ComboBox cboDefaultDonationType;
    private UI.ComboBox cboDefaultVaccinationType;
    private UI.ComboBox cboMapDT1;
    private UI.ComboBox cboMapAc1;
    private UI.ComboBox cboMapDT2;
    private UI.ComboBox cboMapAc2;
    private UI.ComboBox cboMapDT3;
    private UI.ComboBox cboMapAc3;
    private UI.ComboBox cboMapDT4;
    private UI.ComboBox cboMapAc4;
    private UI.ComboBox cboMapDT5;
    private UI.ComboBox cboMapAc5;
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
    private UI.TextField txtVetsUser;
    private OwnerLink olDefaultBroughtInBy;

    /** Creates new form Options */
    public Options() {
        init(Global.i18n("uisystem", "System_Options"),
            IconManager.getIcon(IconManager.SCREEN_OPTIONS), "uisystem");
        loadData();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(txtOrgName);
        ctl.add(txtOrgAddress);
        ctl.add(txtOrgTelephone);
        ctl.add(txtOrgTelephone2);
        ctl.add(cboWordProcessor);
        ctl.add(chkDisableWaitingList);
        ctl.add(spnUrgency);
        ctl.add(cboDefaultUrgency);
        ctl.add(chkRankBySpecies);
        ctl.add(spnCancelReserves);
        ctl.add(txtCodingFormat);
        ctl.add(txtShortCodingFormat);
        ctl.add(tblCodeOptions);
        ctl.add(txtVetsUser);
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
        ctl.add(spnMatchPointFloor);
        ctl.add(spnMatchSpecies);
        ctl.add(spnMatchBreed);
        ctl.add(spnMatchColour);
        ctl.add(spnMatchAge);
        ctl.add(spnMatchSex);
        ctl.add(spnMatchAreaLost);
        ctl.add(spnMatchFeatures);
        ctl.add(spnMatchPostcode);
        ctl.add(spnMatchDateWithin2Weeks);
        ctl.add(txtMappingService);
        ctl.add(txtEmailAddress);
        ctl.add(txtSMTPServer);
        ctl.add(txtEmailSignature);
        ctl.add(txtTPPublisherSig);
        ctl.add(txtDefaultBoardingCost);
        ctl.add(chkCreateBoardingCostAdoption);
        ctl.add(cboBoardingType);
        ctl.add(cboDefaultSpecies);
        ctl.add(cboDefaultType);
        ctl.add(cboNonShelter);
        ctl.add(cboDefaultInternalLocation);
        ctl.add(cboDefaultEntryReason);
        ctl.add(cboDefaultColour);
        ctl.add(cboDefaultDeath);
        ctl.add(cboDefaultReturn);
        ctl.add(cboDefaultSize);
        ctl.add(cboDefaultLogFilter);
        ctl.add(cboDefaultCoatType);
        ctl.add(cboDefaultDonationType);
        ctl.add(cboDefaultVaccinationType);
        ctl.add(olDefaultBroughtInBy);
        ctl.add(dtAccountingPeriod);
        ctl.add(cboDonationTargetAccount);
        ctl.add(cboAccountView);
        ctl.add(cboMapDT1);
        ctl.add(cboMapAc1);
        ctl.add(cboMapDT2);
        ctl.add(cboMapAc2);
        ctl.add(cboMapDT3);
        ctl.add(cboMapAc3);
        ctl.add(cboMapDT4);
        ctl.add(cboMapAc4);
        ctl.add(cboMapDT5);
        ctl.add(cboMapAc5);
        ctl.add(txtDefaultBoardingCost);
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
        ctl.add(txtAvidUrl);
        ctl.add(txtAvidOrgName);
        ctl.add(txtAvidOrgSerial);
        ctl.add(txtAvidOrgPostcode);
        ctl.add(txtAvidOrgPassword);
        ctl.add(txtSearchColumns);
        ctl.add(txtOwnerSearchColumns);
        ctl.add(txtWLViewColumns);
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
        chkDisableWaitingList.setSelected(Configuration.getBoolean(
                "DisableWaitingList"));
        spnUrgency.setValue(new Integer(Configuration.getInteger(
                    "WaitingListUrgencyUpdatePeriod")));

        cboDefaultUrgency.setSelectedIndex(Configuration.getInteger(
                "WaitingListDefaultUrgency"));

        chkRankBySpecies.setSelected(Configuration.getBoolean(
                "WaitingListRankBySpecies"));

        txtWLViewColumns.setText(Configuration.getString(
                "WaitingListViewColumns", WaitingListViewColumns.DEFAULT_COLUMNS));

        // Movements
        spnCancelReserves.setValue(new Integer(Configuration.getInteger(
                    "AutoCancelReservesDays")));

        // Search
        txtSearchColumns.setText(Configuration.getString("SearchColumns",
                AnimalFindColumns.DEFAULT_COLUMNS));
        txtOwnerSearchColumns.setText(Configuration.getString(
                "OwnerSearchColumns", OwnerFindColumns.DEFAULT_COLUMNS));

        // Diary
        txtVetsUser.setText(Global.getVetsDiaryUser());

        // Costs
        txtDefaultBoardingCost.setValue(Configuration.getInteger(
                "DefaultDailyBoardingCost"));
        chkCreateBoardingCostAdoption.setSelected(Configuration.getBoolean(
                "CreateBoardingCostOnAdoption"));
        Utils.setComboFromID(LookupCache.getCostTypeLookup(), "CostTypeName",
            new Integer(Configuration.getInteger("BoardingCostType")),
            cboBoardingType);

        // Insurance Numbers
        chkUseAutoInsurance.setSelected(Configuration.getBoolean(
                "UseAutoInsurance"));
        spnAutoInsuranceStart.setValue(new Integer(Configuration.getInteger(
                    "AutoInsuranceStart")));
        spnAutoInsuranceEnd.setValue(new Integer(Configuration.getInteger(
                    "AutoInsuranceEnd")));
        spnAutoInsuranceNext.setValue(new Integer(Configuration.getInteger(
                    "AutoInsuranceNext")));

        // Mapping Service
        txtMappingService.setText(Configuration.getString("MappingServiceURL",
                "http://maps.google.com/maps?q="));

        // Word Processor
        String docwp = Configuration.getString("DocumentWordProcessor");

        for (int i = 0; i < cboWordProcessor.getItemCount(); i++) {
            String theitem = (String) cboWordProcessor.getItemAt(i);

            if (theitem.equalsIgnoreCase(docwp)) {
                cboWordProcessor.setSelectedIndex(i);

                break;
            }
        }

        // Internet
        txtTPPublisherSig.setText(Configuration.getString("TPPublisherSig"));
        txtEmailAddress.setText(Configuration.getString("EmailAddress"));
        txtSMTPServer.setText(Configuration.getString("SMTPServer"));
        txtEmailSignature.setText(Configuration.getString("EmailSignature"));

        // Pettrac
        txtAvidUrl.setText(Configuration.getString("AvidURL",
                AvidRegistration.DEFAULT_PETTRAC_URL));
        txtAvidOrgName.setText(Configuration.getString("AvidOrgName"));
        txtAvidOrgSerial.setText(Configuration.getString("AvidOrgSerial"));
        txtAvidOrgPostcode.setText(Configuration.getString("AvidOrgPostcode"));
        txtAvidOrgPassword.setText(Configuration.getString("AvidOrgPassword"));

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

        // Accounts
        Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
            new Integer(Configuration.getInteger("DonationTargetAccount")),
            cboDonationTargetAccount);
        dtAccountingPeriod.setText(Configuration.getString("AccountingPeriod"));
        cboAccountView.setSelectedIndex(Configuration.getInteger(
                "DefaultAccountViewPeriod"));

        ArrayList<Account.DonationAccountMapping> dms = Account.getDonationAccountMappings();
        int im = 0;

        for (Account.DonationAccountMapping dm : dms) {
            im++;

            switch (im) {
            case 1:
                Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                    "DonationName", dm.donationTypeID, cboMapDT1);
                Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
                    dm.accountID, cboMapAc1);

                break;

            case 2:
                Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                    "DonationName", dm.donationTypeID, cboMapDT2);
                Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
                    dm.accountID, cboMapAc2);

                break;

            case 3:
                Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                    "DonationName", dm.donationTypeID, cboMapDT3);
                Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
                    dm.accountID, cboMapAc3);

                break;

            case 4:
                Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                    "DonationName", dm.donationTypeID, cboMapDT4);
                Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
                    dm.accountID, cboMapAc4);

                break;

            case 5:
                Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
                    "DonationName", dm.donationTypeID, cboMapDT5);
                Utils.setComboFromID(LookupCache.getAccountsLookup(), "Code",
                    dm.accountID, cboMapAc5);

                break;
            }
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

        // Lost and Found
        spnMatchSpecies.setValue(new Integer(Configuration.getInteger(
                    "MatchSpecies", 5)));
        spnMatchBreed.setValue(new Integer(Configuration.getInteger(
                    "MatchBreed", 5)));
        spnMatchAge.setValue(new Integer(Configuration.getInteger("MatchAge", 5)));
        spnMatchSex.setValue(new Integer(Configuration.getInteger("MatchSex", 5)));
        spnMatchAreaLost.setValue(new Integer(Configuration.getInteger(
                    "MatchAreaLost", 5)));
        spnMatchFeatures.setValue(new Integer(Configuration.getInteger(
                    "MatchFeatures", 5)));
        spnMatchPostcode.setValue(new Integer(Configuration.getInteger(
                    "MatchPostcode", 5)));
        spnMatchColour.setValue(new Integer(Configuration.getInteger(
                    "MatchColour", 5)));
        spnMatchDateWithin2Weeks.setValue(new Integer(Configuration.getInteger(
                    "MatchWithin2Weeks", 5)));
        spnMatchPointFloor.setValue(new Integer(Configuration.getInteger(
                    "MatchPointFloor", 20)));

        // Defaults
        Utils.setComboFromID(LookupCache.getSpeciesLookup(), "SpeciesName",
            new Integer(Configuration.getInteger("AFDefaultSpecies")),
            cboDefaultSpecies);

        Utils.setComboFromID(LookupCache.getAnimalTypeLookup(), "AnimalType",
            new Integer(Configuration.getInteger("AFDefaultType")),
            cboDefaultType);

        Utils.setComboFromID(LookupCache.getAnimalTypeLookup(), "AnimalType",
            new Integer(Configuration.getInteger("AFNonShelterType")),
            cboNonShelter);

        Utils.setComboFromID(LookupCache.getInternalLocationLookup(),
            "LocationName",
            new Integer(Configuration.getInteger("AFDefaultLocation")),
            cboDefaultInternalLocation);

        Utils.setComboFromID(LookupCache.getEntryReasonLookup(), "ReasonName",
            new Integer(Configuration.getInteger("AFDefaultEntryReason")),
            cboDefaultEntryReason);

        Utils.setComboFromID(LookupCache.getEntryReasonLookup(), "ReasonName",
            new Integer(Configuration.getInteger("AFDefaultReturnReason")),
            cboDefaultReturn);

        Utils.setComboFromID(LookupCache.getDeathReasonLookup(), "ReasonName",
            new Integer(Configuration.getInteger("AFDefaultDeathReason")),
            cboDefaultDeath);

        Utils.setComboFromID(LookupCache.getBaseColourLookup(), "BaseColour",
            new Integer(Configuration.getInteger("AFDefaultColour")),
            cboDefaultColour);

        cboDefaultSize.setSelectedIndex(Configuration.getInteger(
                "AFDefaultSize"));

        Utils.setComboFromID(LookupCache.getLogTypeLookup(), "LogTypeName",
            new Integer(Configuration.getInteger("AFDefaultLogFilter")),
            cboDefaultLogFilter);

        Utils.setComboFromID(LookupCache.getCoatTypeLookup(), "CoatType",
            new Integer(Configuration.getInteger("AFDefaultCoatType")),
            cboDefaultCoatType);

        Utils.setComboFromID(LookupCache.getDonationTypeLookup(),
            "DonationName",
            new Integer(Configuration.getInteger("AFDefaultDonationType")),
            cboDefaultDonationType);

        Utils.setComboFromID(LookupCache.getVaccinationTypeLookup(),
            "VaccinationType",
            new Integer(Configuration.getInteger("AFDefaultVaccinationType")),
            cboDefaultVaccinationType);

        olDefaultBroughtInBy.setID(Configuration.getInteger(
                "DefaultBroughtInBy"));

        // Authentication
        if (Configuration.getBoolean("AutoLoginOSUsers")) {
            // OS auth
            cboMech.setSelectedIndex(1);
        } else if (LDAP.isConfigured()) {
            cboMech.setSelectedIndex(2);

            Map<String, String> m = LDAP.getSettings();
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

            if ((txtCodingFormat.getText().indexOf("N") != -1) &&
                    (txtCodingFormat.getText().indexOf("Y") == -1)) {
                Dialog.showError(i18n("Invalid_codeformat_need_y_with_n"),
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

            // Diary
            Configuration.setEntry("VetsDiaryUser", txtVetsUser.getText());

            // Mapping Service
            Configuration.setEntry("MappingServiceURL",
                txtMappingService.getText());

            // Search
            Configuration.setEntry("SearchColumns", txtSearchColumns.getText());
            Configuration.setEntry("OwnerSearchColumns",
                txtOwnerSearchColumns.getText());

            // Costs
            Configuration.setEntry("DefaultDailyBoardingCost",
                new Integer(txtDefaultBoardingCost.getValue()).toString());
            Configuration.setEntry("CreateBoardingCostOnAdoption",
                chkCreateBoardingCostAdoption.isSelected() ? "Yes" : "No");
            Utils.getIDFromCombo(LookupCache.getCostTypeLookup(),
                "CostTypeName", cboBoardingType);

            // Waiting List
            Configuration.setEntry("DisableWaitingList",
                chkDisableWaitingList.isSelected() ? "Yes" : "No");
            Configuration.setEntry("WaitingListUrgencyUpdatePeriod",
                spnUrgency.getValue().toString());
            Configuration.setEntry("WaitingListDefaultUrgency",
                Integer.toString(cboDefaultUrgency.getSelectedIndex()));
            Configuration.setEntry("WaitingListRankBySpecies",
                chkRankBySpecies.isSelected() ? "Yes" : "No");
            Configuration.setEntry("WaitingListViewColumns",
                txtWLViewColumns.getText());

            // Movements
            Configuration.setEntry("AutoCancelReservesDays",
                spnCancelReserves.getValue().toString());

            // Internet
            Configuration.setEntry("TPPublisherSig", txtTPPublisherSig.getText());
            Configuration.setEntry("EmailAddress", txtEmailAddress.getText());
            Configuration.setEntry("SMTPServer", txtSMTPServer.getText());
            Configuration.setEntry("EmailSignature", txtEmailSignature.getText());

            // Pettrac/AVID
            Configuration.setEntry("AvidURL", txtAvidUrl.getText());
            Configuration.setEntry("AvidOrgName", txtAvidOrgName.getText());
            Configuration.setEntry("AvidOrgSerial", txtAvidOrgSerial.getText());
            Configuration.setEntry("AvidOrgPostcode",
                txtAvidOrgPostcode.getText());
            Configuration.setEntry("AvidOrgPassword",
                txtAvidOrgPassword.getText());

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

            Configuration.setEntry("AFDefaultLogFilter",
                Utils.getIDFromCombo(LookupCache.getLogTypeLookup(),
                    "LogTypeName", cboDefaultLogFilter).toString());

            Configuration.setEntry("AFDefaultCoatType",
                Utils.getIDFromCombo(LookupCache.getCoatTypeLookup(),
                    "CoatType", cboDefaultCoatType).toString());

            Configuration.setEntry("AFDefaultDonationType",
                Utils.getIDFromCombo(LookupCache.getDonationTypeLookup(),
                    "DonationName", cboDefaultDonationType).toString());

            Configuration.setEntry("AFDefaultVaccinationType",
                Utils.getIDFromCombo(LookupCache.getVaccinationTypeLookup(),
                    "VaccinationType", cboDefaultVaccinationType).toString());

            Configuration.setEntry("DefaultBroughtInBy",
                Integer.toString(olDefaultBroughtInBy.getID()));

            l = tblDefaultOptions.getSelections();

            for (int i = 0; i < l.length; i++) {
                if ((l[i] != null) && (l[i].getValue() != null)) {
                    Configuration.setEntry(l[i].getValue().toString(),
                        (l[i].isSelected() ? "Yes" : "No"));
                }
            }

            // Accounts
            Configuration.setEntry("DonationTargetAccount",
                Utils.getIDFromCombo(LookupCache.getAccountsLookup(), "Code",
                    cboDonationTargetAccount).toString());

            Configuration.setEntry("AccountingPeriod",
                dtAccountingPeriod.getText());

            Configuration.setEntry("DefaultAccountViewPeriod",
                Integer.toString(cboAccountView.getSelectedIndex()));

            String maps = "";

            if (cboMapDT1.getSelectedIndex() > 0) {
                if (!maps.equals("")) {
                    maps += ",";
                }

                maps += Utils.getIDFromCombo(LookupCache.getDonationTypeLookup(),
                    "DonationName", cboMapDT1);
                maps += "=";
                maps += Utils.getIDFromCombo(LookupCache.getAccountsLookup(),
                    "Code", cboMapAc1);
            }

            if (cboMapDT2.getSelectedIndex() > 0) {
                if (!maps.equals("")) {
                    maps += ",";
                }

                maps += Utils.getIDFromCombo(LookupCache.getDonationTypeLookup(),
                    "DonationName", cboMapDT2);
                maps += "=";
                maps += Utils.getIDFromCombo(LookupCache.getAccountsLookup(),
                    "Code", cboMapAc2);
            }

            if (cboMapDT3.getSelectedIndex() > 0) {
                if (!maps.equals("")) {
                    maps += ",";
                }

                maps += Utils.getIDFromCombo(LookupCache.getDonationTypeLookup(),
                    "DonationName", cboMapDT3);
                maps += "=";
                maps += Utils.getIDFromCombo(LookupCache.getAccountsLookup(),
                    "Code", cboMapAc3);
            }

            if (cboMapDT4.getSelectedIndex() > 0) {
                if (!maps.equals("")) {
                    maps += ",";
                }

                maps += Utils.getIDFromCombo(LookupCache.getDonationTypeLookup(),
                    "DonationName", cboMapDT4);
                maps += "=";
                maps += Utils.getIDFromCombo(LookupCache.getAccountsLookup(),
                    "Code", cboMapAc4);
            }

            if (cboMapDT5.getSelectedIndex() > 0) {
                if (!maps.equals("")) {
                    maps += ",";
                }

                maps += Utils.getIDFromCombo(LookupCache.getDonationTypeLookup(),
                    "DonationName", cboMapDT5);
                maps += "=";
                maps += Utils.getIDFromCombo(LookupCache.getAccountsLookup(),
                    "Code", cboMapAc5);
            }

            Configuration.setEntry("DonationAccountMappings", maps);

            l = tblAccountOptions.getSelections();

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

            // Lost and Found
            Configuration.setEntry("MatchSpecies",
                spnMatchSpecies.getValue().toString());
            Configuration.setEntry("MatchBreed",
                spnMatchBreed.getValue().toString());
            Configuration.setEntry("MatchAge", spnMatchAge.getValue().toString());
            Configuration.setEntry("MatchSex", spnMatchSex.getValue().toString());
            Configuration.setEntry("MatchAreaLost",
                spnMatchAreaLost.getValue().toString());
            Configuration.setEntry("MatchFeatures",
                spnMatchFeatures.getValue().toString());
            Configuration.setEntry("MatchPostcode",
                spnMatchPostcode.getValue().toString());
            Configuration.setEntry("MatchColour",
                spnMatchColour.getValue().toString());
            Configuration.setEntry("MatchWithin2Weeks",
                spnMatchDateWithin2Weeks.getValue().toString());
            Configuration.setEntry("MatchPointFloor",
                spnMatchPointFloor.getValue().toString());

            l = tblLostAndFoundOptions.getSelections();

            for (int i = 0; i < l.length; i++) {
                if ((l[i] != null) && (l[i].getValue() != null)) {
                    Configuration.setEntry(l[i].getValue().toString(),
                        (l[i].isSelected() ? "Yes" : "No"));
                }
            }

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
            Configuration.setEntry("UseAutoInsurance",
                chkUseAutoInsurance.isSelected() ? "Yes" : "No");
            Configuration.setEntry("AutoInsuranceStart",
                spnAutoInsuranceStart.getValue().toString());
            Configuration.setEntry("AutoInsuranceEnd",
                spnAutoInsuranceEnd.getValue().toString());
            Configuration.setEntry("AutoInsuranceNext",
                spnAutoInsuranceNext.getValue().toString());

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
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }

    public void tabChanged() {
    }

    public void initComponents() {
        tabTabs = UI.getTabbedPane(UI.fp(this, "tabChanged"));

        // Move our tab set to the left (or right for RTL languages)
        if (UI.isLTR()) {
            tabTabs.setTabPlacement(UI.TabbedPane.LEFT);
        } else {
            tabTabs.setTabPlacement(UI.TabbedPane.RIGHT);
        }

        // Shelter info
        UI.Panel detop = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel demid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel debot = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));

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

        // Accounts
        UI.Panel pacc = UI.getPanel(UI.getGridLayout(3, new int[] { 30, 35, 35 }));
        UI.Panel accounts = UI.getPanel(UI.getBorderLayout());

        dtAccountingPeriod = (DateField) UI.addComponent(pacc,
                i18n("accounting_period"), UI.getDateField());
        pacc.add(UI.getLabel());

        cboDonationTargetAccount = UI.getCombo(LookupCache.getAccountsLookup(),
                "Code");
        UI.addComponent(pacc, i18n("Donation_destination_account"),
            cboDonationTargetAccount);
        pacc.add(UI.getLabel());

        String[] views = new String[] {
                i18n("this_month"), i18n("this_week"), i18n("this_year"),
                i18n("last_month"), i18n("last_week")
            };
        cboAccountView = UI.getCombo(views);
        UI.addComponent(pacc, i18n("default_trx_view"), cboAccountView);
        pacc.add(UI.getLabel());

        cboMapDT1 = UI.getCombo(LookupCache.getDonationTypeLookup(),
                "DonationName", "");
        cboMapDT2 = UI.getCombo(LookupCache.getDonationTypeLookup(),
                "DonationName", "");
        cboMapDT3 = UI.getCombo(LookupCache.getDonationTypeLookup(),
                "DonationName", "");
        cboMapDT4 = UI.getCombo(LookupCache.getDonationTypeLookup(),
                "DonationName", "");
        cboMapDT5 = UI.getCombo(LookupCache.getDonationTypeLookup(),
                "DonationName", "");
        cboMapAc1 = UI.getCombo(LookupCache.getAccountsLookup(), "Code", "");
        cboMapAc2 = UI.getCombo(LookupCache.getAccountsLookup(), "Code", "");
        cboMapAc3 = UI.getCombo(LookupCache.getAccountsLookup(), "Code", "");
        cboMapAc4 = UI.getCombo(LookupCache.getAccountsLookup(), "Code", "");
        cboMapAc5 = UI.getCombo(LookupCache.getAccountsLookup(), "Code", "");
        UI.addComponent(pacc, i18n("map_donation_to_account"), cboMapDT1);
        pacc.add(cboMapAc1);
        UI.addComponent(pacc, i18n("map_donation_to_account"), cboMapDT2);
        pacc.add(cboMapAc2);
        UI.addComponent(pacc, i18n("map_donation_to_account"), cboMapDT3);
        pacc.add(cboMapAc3);
        UI.addComponent(pacc, i18n("map_donation_to_account"), cboMapDT4);
        pacc.add(cboMapAc4);
        UI.addComponent(pacc, i18n("map_donation_to_account"), cboMapDT5);
        pacc.add(cboMapAc5);

        List<SelectableItem> l = new ArrayList<SelectableItem>();
        l.add(new SelectableItem(Global.i18n("uisystem", "Accounts"), null,
                false, true));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "disable_accounts_functionality"), "DisableAccounts",
                Configuration.getString("DisableAccounts")
                             .equalsIgnoreCase("Yes"), false));

        l.add(new SelectableItem(Global.i18n("uisystem", "creating_matching_trx"),
                "CreateDonationTrx",
                Configuration.getString("CreateDonationTrx")
                             .equalsIgnoreCase("Yes"), false));

        l.add(new SelectableItem(Global.i18n("uisystem", "show_period_totals"),
                "AccountPeriodTotals",
                Configuration.getString("AccountPeriodTotals")
                             .equalsIgnoreCase("Yes"), false));

        tblAccountOptions = new SelectableList(l);

        accounts.add(pacc, UI.BorderLayout.NORTH);
        UI.addComponent(accounts, tblAccountOptions);
        tabTabs.addTab(i18n("Accounts"), null, accounts, null);

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

        // Animal code options
        UI.Panel codepanel = UI.getPanel(UI.getBorderLayout());
        UI.Panel pc = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        txtCodingFormat = (UI.TextField) UI.addComponent(pc,
                i18n("coding_format"),
                UI.getTextField(i18n("coding_format_tooltip")));

        txtShortCodingFormat = (UI.TextField) UI.addComponent(pc,
                i18n("short_coding_format"),
                UI.getTextField(i18n("short_coding_format_tooltip")));

        l = new ArrayList<SelectableItem>();
        l.add(new SelectableItem(Global.i18n("uisystem", "Coding_System"),
                null, false, true));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "show_short_shelter_code"), "UseShortShelterCodes",
                Configuration.getString("UseShortShelterCodes")
                             .equalsIgnoreCase("Yes"), false));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "disable_shortcodes_editing"), "DisableShortCodesControl",
                Configuration.getString("DisableShortCodesControl")
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

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "once_assigned_codes_cannot_be_changed"), "LockCodes",
                Configuration.getString("LockCodes").equalsIgnoreCase("Yes"),
                false));

        tblCodeOptions = new SelectableList(l);
        UI.addComponent(codepanel, tblCodeOptions);

        codepanel.add(pc, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("animal_codes"), null, codepanel, null);

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
        txtLDAPFilter = (UI.TextField) UI.addComponent(pldap,
                i18n("LDAP_Filter"), UI.getTextField());
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

        // Costs
        UI.Panel pcost = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));

        txtDefaultBoardingCost = (CurrencyField) UI.addComponent(pcost,
                i18n("Default_Daily_Boarding_Cost"), UI.getCurrencyField());
        UI.addComponent(pcost, UI.getLabel());
        chkCreateBoardingCostAdoption = (UI.CheckBox) UI.addComponent(pcost,
                UI.getCheckBox(i18n("Create_boarding_cost_on_adoption")));
        cboBoardingType = (UI.ComboBox) UI.addComponent(pcost,
                i18n("boarding_cost_type"),
                UI.getCombo(LookupCache.getCostTypeLookup(), "CostTypeName"));

        UI.Panel costoptions = UI.getPanel(UI.getBorderLayout());
        costoptions.add(pcost, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("costs"), null, costoptions, null);

        // Defaults
        UI.Panel pr = UI.getPanel(UI.getGridLayout(4,
                    new int[] { 15, 35, 15, 35 }));
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

        cboDefaultLogFilter = UI.getCombo(LookupCache.getLogTypeLookup(),
                "LogTypeName", i18n("(all)"));
        UI.addComponent(pr, i18n("Default_Log_Filter:"), cboDefaultLogFilter);

        cboDefaultCoatType = UI.getCombo(LookupCache.getCoatTypeLookup(),
                "CoatType");
        UI.addComponent(pr, i18n("Default_Coat_Type"), cboDefaultCoatType);

        cboDefaultDonationType = UI.getCombo(LookupCache.getDonationTypeLookup(),
                "DonationName");
        UI.addComponent(pr, i18n("Default_Donation_Type"),
            cboDefaultDonationType);

        cboDefaultVaccinationType = UI.getCombo(LookupCache.getVaccinationTypeLookup(),
                "VaccinationType");
        UI.addComponent(pr, i18n("Default_Vaccination_Type"),
            cboDefaultVaccinationType);

        olDefaultBroughtInBy = new OwnerLink(OwnerLink.MODE_ONELINE,
                OwnerLink.FILTER_NONE, "");
        UI.addComponent(pr, i18n("Default_brought_in_by"), olDefaultBroughtInBy);

        l = new ArrayList<SelectableItem>();
        l.add(new SelectableItem(Global.i18n("uisystem", "Defaults"), null,
                false, true));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "mark_new_animals_not_for_adoption"), "AutoNotForAdoption",
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
                Configuration.getString("AutoMediaNotes").equalsIgnoreCase("Yes"),
                false));

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
                    "Auto_attach_generated_media"), "AutoAttachMedia",
                Configuration.getString("AutoAttachMedia")
                             .equalsIgnoreCase("Yes"), false));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "Default_date_brought_in_to_today"),
                "DefaultDateBroughtIn",
                Configuration.getString("DefaultDateBroughtIn")
                             .equalsIgnoreCase("Yes"), false));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "Default_media_notes_to_original_filename"),
                "DefaultMediaNotesFromFile",
                Configuration.getString("DefaultMediaNotesFromFile")
                             .equalsIgnoreCase("Yes"), false));

        tblDefaultOptions = new SelectableList(l);

        defaults.add(pr, UI.BorderLayout.NORTH);
        UI.addComponent(defaults, tblDefaultOptions);
        tabTabs.addTab(i18n("Defaults"), null, defaults, null);

        // Diary options
        UI.Panel pd = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        txtVetsUser = (UI.TextField) UI.addComponent(pd,
                i18n("Vets_Diary_User:"), UI.getTextField());

        UI.Panel diary = UI.getPanel(UI.getBorderLayout());
        diary.add(pd, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("diary"), null, diary, null);

        // Insurance
        UI.Panel pins = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel insurancenumbers = UI.getPanel(UI.getBorderLayout());

        chkUseAutoInsurance = (UI.CheckBox) UI.addComponent(pins,
                i18n("Use_Automatic_Insurance_Numbers"), UI.getCheckBox());

        spnAutoInsuranceStart = (UI.Spinner) UI.addComponent(pins,
                i18n("Start_At:"), UI.getSpinner(0, 400000000));
        spnAutoInsuranceEnd = (UI.Spinner) UI.addComponent(pins,
                i18n("End_At:"), UI.getSpinner(0, 400000000));
        spnAutoInsuranceNext = (UI.Spinner) UI.addComponent(pins,
                i18n("Next:"), UI.getSpinner(0, 400000000));

        insurancenumbers.add(pins, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("Insurance"), null, insurancenumbers, null);

        // Internet
        UI.Panel pemail = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        UI.Panel psemail = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        UI.Panel email = UI.getPanel(UI.getBorderLayout());

        txtEmailAddress = (UI.TextField) UI.addComponent(pemail,
                i18n("email_address"),
                UI.getTextField(i18n("emails_from_ASM_come_from")));

        txtEmailSignature = (UI.TextArea) UI.addComponent(psemail,
                i18n("email_signature"), UI.getTextArea());

        txtTPPublisherSig = (UI.TextArea) UI.addComponent(psemail,
                i18n("tpp_signature"),
                UI.getTextArea(i18n("tpp_signature_tooltip")));

        txtSMTPServer = (UI.TextField) UI.addComponent(pemail,
                i18n("smtp_server"),
                UI.getTextField(i18n("address_of_smtp_server")));

        email.add(pemail, UI.BorderLayout.NORTH);
        email.add(psemail, UI.BorderLayout.CENTER);
        tabTabs.addTab(i18n("internet"), null, email, null);

        // Lost and found
        UI.Panel plf = UI.getPanel(UI.getGridLayout(2, new int[] { 40, 60 }));
        spnMatchPointFloor = (UI.Spinner) UI.addComponent(plf,
                i18n("how_many_points_to_show_on_report"),
                UI.getSpinner(0, 1000));
        spnMatchSpecies = (UI.Spinner) UI.addComponent(plf,
                i18n("species_matches"), UI.getSpinner(0, 100));
        spnMatchBreed = (UI.Spinner) UI.addComponent(plf,
                i18n("breed_matches"), UI.getSpinner(0, 100));
        spnMatchColour = (UI.Spinner) UI.addComponent(plf,
                i18n("colour_matches"), UI.getSpinner(0, 100));
        spnMatchAge = (UI.Spinner) UI.addComponent(plf,
                i18n("age_group_matches"), UI.getSpinner(0, 100));
        spnMatchSex = (UI.Spinner) UI.addComponent(plf, i18n("sex_matches"),
                UI.getSpinner(0, 100));
        spnMatchAreaLost = (UI.Spinner) UI.addComponent(plf,
                i18n("area_matches"), UI.getSpinner(0, 100));
        spnMatchFeatures = (UI.Spinner) UI.addComponent(plf,
                i18n("features_matches"), UI.getSpinner(0, 100));
        spnMatchPostcode = (UI.Spinner) UI.addComponent(plf,
                i18n("postcode_matches"), UI.getSpinner(0, 100));
        spnMatchDateWithin2Weeks = (UI.Spinner) UI.addComponent(plf,
                i18n("datewithin2weeks_matches"), UI.getSpinner(0, 100));

        l = new ArrayList<SelectableItem>();
        l.add(new SelectableItem(Global.i18n("uisystem", "lost_and_found"),
                null, false, true));

        l.add(new SelectableItem(Global.i18n("uisystem",
                    "disable_lost_and_found"), "DisableLostAndFound",
                Configuration.getString("DisableLostAndFound")
                             .equalsIgnoreCase("Yes"), false));

        l.add(new SelectableItem(Global.i18n("uisystem", "include_shelter"),
                "MatchIncludeShelter",
                Configuration.getString("MatchIncludeShelter")
                             .equalsIgnoreCase("Yes"), false));

        tblLostAndFoundOptions = new SelectableList(l);

        UI.Panel lostandfound = UI.getPanel(UI.getBorderLayout());
        lostandfound.add(plf, UI.BorderLayout.NORTH);
        lostandfound.add(tblLostAndFoundOptions);
        tabTabs.addTab(i18n("lost_and_found"), null, lostandfound, null);

        // Mapping service options
        UI.Panel pm = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        txtMappingService = (UI.TextField) UI.addComponent(pm,
                i18n("mapping_service_url"), UI.getTextField());
        txtMappingService.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 3, UI.getTextBoxHeight()));

        UI.Panel mappingservice = UI.getPanel(UI.getBorderLayout());
        mappingservice.add(pm, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("mapping_service"), null, mappingservice, null);

        // Movement options
        UI.Panel pv = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        spnCancelReserves = (UI.Spinner) UI.addComponent(pv,
                i18n("auto_cancel_reserves"),
                UI.getSpinner(0, 365, i18n("auto_cancel_reserves_tooltip"), null));

        UI.Panel movementoptions = UI.getPanel(UI.getBorderLayout());
        movementoptions.add(pv, UI.BorderLayout.NORTH);
        tabTabs.addTab(i18n("movements"), null, movementoptions, null);

        // PETtrac/AVID options (only valid for UK)
        UI.Panel ppa = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        txtAvidUrl = (UI.TextField) UI.addComponent(ppa, i18n("avid_url"),
                UI.getTextField());
        txtAvidOrgName = (UI.TextField) UI.addComponent(ppa,
                i18n("avid_org_name"), UI.getTextField());
        txtAvidOrgSerial = (UI.TextField) UI.addComponent(ppa,
                i18n("avid_org_serial"), UI.getTextField());
        txtAvidOrgPostcode = (UI.TextField) UI.addComponent(ppa,
                i18n("avid_org_postcode"), UI.getTextField());
        txtAvidOrgPassword = (UI.TextField) UI.addComponent(ppa,
                i18n("avid_org_password"), UI.getTextField());

        UI.Panel pettracoptions = UI.getPanel(UI.getBorderLayout());
        pettracoptions.add(ppa, UI.BorderLayout.NORTH);

        if (Global.settings_Locale.equalsIgnoreCase("en_GB")) {
            tabTabs.addTab(i18n("pettrac_avid"), null, pettracoptions, null);
        }

        // Search options
        UI.Panel ps = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        txtSearchColumns = (UI.TextArea) UI.addComponent(ps,
                i18n("animal_search_result_columns"),
                UI.getTextArea(i18n("animal_search_result_columns_tooltip")));
        txtOwnerSearchColumns = (UI.TextArea) UI.addComponent(ps,
                i18n("owner_search_result_columns"),
                UI.getTextArea(i18n("owner_search_result_columns_tooltip")));

        UI.Panel searchoptions = UI.getPanel(UI.getBorderLayout());
        searchoptions.add(ps, UI.BorderLayout.CENTER);
        tabTabs.addTab(i18n("search"), null, searchoptions, null);

        // Waiting list options
        UI.Panel pl = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));

        chkDisableWaitingList = UI.getCheckBox(i18n("disable_waiting_list"));
        UI.addComponent(pl, "", chkDisableWaitingList);

        spnUrgency = (UI.Spinner) UI.addComponent(pl,
                i18n("Update_Waiting_List_Period:"),
                UI.getSpinner(0, 9999,
                    i18n("The_interval_at_which_the_waiting_list_urgencies_should_be_updated_in_days"),
                    null));

        cboDefaultUrgency = UI.getCombo(LookupCache.getUrgencyLookup(),
                "Urgency");
        UI.addComponent(pl, i18n("default_waiting_list_urgency"),
            cboDefaultUrgency);

        chkRankBySpecies = UI.getCheckBox(i18n("separate_the_ranks_by_species"));
        UI.addComponent(pl, "", chkRankBySpecies);

        UI.Panel plc = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        txtWLViewColumns = (UI.TextArea) UI.addComponent(plc,
                i18n("waiting_list_view_columns"),
                UI.getTextArea(i18n("waiting_list_view_columns_tooltip")));

        UI.Panel waitinglist = UI.getPanel(UI.getBorderLayout());
        waitinglist.add(pl, UI.BorderLayout.NORTH);
        waitinglist.add(plc, UI.BorderLayout.CENTER);
        tabTabs.addTab(i18n("waiting_list"), null, waitinglist, null);

        // Word processor options
        UI.Panel pw = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));

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

        // Options
        l = new ArrayList<SelectableItem>();

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
                        "Hide_original_owner_details_when_printing_animal_records"),
                    "AnimalPrintHideOriginalOwner",
                    Configuration.getString("AnimalPrintHideOriginalOwner")
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
                        "Dont_show_media_thumbnails_on_details_screen"),
                    "NoMediaThumbnails",
                    Configuration.getString("NoMediaThumbnails")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "When_printing_dont_scale_to_page_width"),
                    "NoPrintWidthScaling",
                    Configuration.getString("NoPrintWidthScaling")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "When_scaling_incoming_media_use_old_algorithm"),
                    "UseOldScaling",
                    Configuration.getString("UseOldScaling")
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
                        "Use_a_single_breed_field"), "UseSingleBreedField",
                    Configuration.getBoolean("UseSingleBreedField"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Dont_show_the_startup_page"), "DontShowStartupPage",
                    Configuration.getBoolean("DontShowStartupPage"), false));

            l.add(new SelectableItem(Global.i18n("uisystem", "Keep_audit_trail"),
                    "AdvancedAudit", Configuration.getBoolean("AdvancedAudit"),
                    false));

            // Remove unwanted fields
            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Remove_unwanted_fields"), null, false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_town_and_county_fields"), "HideTownCounty",
                    Configuration.getString("HideTownCounty")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_coattype_field"), "DontShowCoatType",
                    Configuration.getString("DontShowCoatType")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_microchip_fields"), "DontShowMicrochip",
                    Configuration.getString("DontShowMicrochip")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_tattoo_fields"), "DontShowTattoo",
                    Configuration.getString("DontShowTattoo")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_neutered_fields"), "DontShowNeutered",
                    Configuration.getString("DontShowNeutered")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_declawed_field"), "DontShowDeclawed",
                    Configuration.getString("DontShowDeclawed")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_heartworm_fields"), "DontShowHeartworm",
                    Configuration.getString("DontShowHeartworm")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_combitest_fields"), "DontShowCombi",
                    Configuration.getString("DontShowCombi")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_goodwith_fields"), "DontShowGoodWith",
                    Configuration.getString("DontShowGoodWith")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_litter_field"), "DontShowLitterID",
                    Configuration.getString("DontShowLitterID")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "remove_bonded_field"), "DontShowBonded",
                    Configuration.getString("DontShowBonded")
                                 .equalsIgnoreCase("Yes"), false));

            // Adding Multiple Animals
            l.add(new SelectableItem(Global.i18n("uisystem",
                        "Adding_multiple_animals"), null, false, true));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "add_animals_show_breed"), "AddAnimalsShowBreed",
                    Configuration.getString("AddAnimalsShowBreed")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "add_animals_show_colour"), "AddAnimalsShowColour",
                    Configuration.getString("AddAnimalsShowColour")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        "add_animals_show_location"), "AddAnimalsShowLocation",
                    Configuration.getString("AddAnimalsShowLocation")
                                 .equalsIgnoreCase("Yes"), false));

            l.add(new SelectableItem(Global.i18n("uisystem",
                        (Configuration.getBoolean("AutoLitterIdentification")
                        ? "add_animals_show_litterid"
                        : "add_animals_show_acceptance")),
                    "AddAnimalsShowAcceptance",
                    Configuration.getString("AddAnimalsShowAcceptance")
                                 .equalsIgnoreCase("Yes"), false));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tblOptions = new SelectableList(l);
        tabTabs.addTab(i18n("options"), null, tblOptions, null);

        UI.ToolBar t = new UI.ToolBar();
        t.add(UI.getButton(null, i18n("Save_your_changes_and_exit"), 's',
                IconManager.getIcon(IconManager.SCREEN_OPTIONS_SAVE),
                UI.fp(this, "saveData")));
        add(t, UI.BorderLayout.NORTH);
        add(tabTabs, UI.BorderLayout.CENTER);
    }
}
