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
package net.sourceforge.sheltermanager.asm.ui.lostandfound;

import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMFind;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.text.ParseException;

import java.util.Vector;


/**
 * This class contains all code for searching the lost animal database.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class LostAnimalFind extends ASMFind {
    public UI.ComboBox cboColour;
    public UI.ComboBox cboSpecies;
    public UI.ComboBox cboAgeGroup;
    public UI.ComboBox cboSex;
    public UI.ComboBox cboBreed;
    public UI.TextField txtArea;
    public UI.TextField txtContact;
    public UI.TextField txtDistFeat;
    public DateField txtFrom;
    public UI.TextField txtNumber;
    public UI.CheckBox chkIncludeReturned;
    public UI.TextField txtPostcode;
    public DateField txtTo;
    public UI.Button btnClear;

    /** Creates new form FindLostAnimal */
    public LostAnimalFind() {
        init(Global.i18n("uilostandfound", "Find_Lost_Animal"),
            IconManager.getIcon(IconManager.SCREEN_FINDLOSTANIMAL),
            "uilostandfound", 4, false, false);
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(txtContact);
        ctl.add(txtArea);
        ctl.add(txtPostcode);
        ctl.add(txtDistFeat);
        ctl.add(cboAgeGroup);
        ctl.add(cboSex);
        ctl.add(cboSpecies);
        ctl.add(cboBreed);
        ctl.add(cboColour);
        ctl.add(txtFrom.getTextField());
        ctl.add(txtTo.getTextField());
        ctl.add(txtNumber);
        ctl.add(chkIncludeReturned);
        ctl.add(table);
        ctl.add(btnSearch);
        ctl.add(btnOpen);
        ctl.add(btnClear);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtContact;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void initCriteria(UI.Panel p) {
        txtContact = (UI.TextField) UI.addComponent(p,
                i18n("Contact_contains:"), UI.getTextField());

        txtArea = (UI.TextField) UI.addComponent(p, i18n("Area_Contains:"),
                UI.getTextField());

        txtPostcode = (UI.TextField) UI.addComponent(p,
                i18n("Postcode_contains:"), UI.getTextField());

        txtDistFeat = (UI.TextField) UI.addComponent(p,
                i18n("Dist._Features:"), UI.getTextField());

        cboAgeGroup = UI.getCombo(i18n("Age_Group:"),
                LookupCache.getAgeGroupNames(), i18n("(all)"));
        UI.addComponent(p, i18n("Age_Group:"), cboAgeGroup);

        cboSex = UI.getCombo(i18n("Sex:"), LookupCache.getSexLookup(), "Sex",
                i18n("(all)"));
        UI.addComponent(p, i18n("Sex:"), cboSex);

        cboSpecies = UI.getCombo(i18n("Species:"),
                LookupCache.getSpeciesLookup(), "SpeciesName", i18n("(all)"));
        UI.addComponent(p, i18n("Species:"), cboSpecies);

        cboBreed = UI.getCombo(i18n("Breed:"), LookupCache.getBreedLookup(),
                "BreedName", i18n("(all)"));
        UI.addComponent(p, i18n("Breed:"), cboBreed);
        cboColour = UI.getCombo(i18n("Colour:"),
                LookupCache.getBaseColourLookup(), "BaseColour", i18n("(all)"));
        UI.addComponent(p, i18n("Colour:"), cboColour);

        txtFrom = (DateField) UI.addComponent(p, i18n("Lost_Between:"),
                UI.getDateField());

        txtTo = (DateField) UI.addComponent(p, i18n("and"), UI.getDateField());

        txtNumber = (UI.TextField) UI.addComponent(p, i18n("Number:"),
                UI.getTextField());

        chkIncludeReturned = UI.getCheckBox(i18n("include_returned_animals"),
                i18n("include_animals_who_have_been_returned_to_their_owners"));
        p.add(chkIncludeReturned);
    }

    public void initToolbar() {
        btnClear = UI.getButton(i18n("Clear"), null, 'c',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_CLEAR),
                UI.fp(this, "actionClear"));
        addToolbarItem(btnClear, false);
    }

    public void initLeftbar() {
        // No left bar for lost/found
    }

    public void itemSelected(int id) {
        AnimalLost lostanimal = new AnimalLost();
        lostanimal.openRecordset("ID = " + id);

        // Create a new EditLostAnimal screen
        LostAnimalEdit ea = new LostAnimalEdit();
        // Kick it off into edit mode, passing the animal
        ea.openForEdit(lostanimal);
        // Attach it to the main screen
        Global.mainForm.addChild(ea);
    }

    public void actionClear() {
        txtContact.setText("");
        txtArea.setText("");
        txtPostcode.setText("");
        txtDistFeat.setText("");
        cboColour.setSelectedIndex(0);
        cboSpecies.setSelectedIndex(0);
        cboAgeGroup.setSelectedIndex(0);
        cboBreed.setSelectedIndex(0);
        cboSex.setSelectedIndex(0);
        txtFrom.setText("");
        txtTo.setText("");
        txtNumber.setText("");
    }

    public void runSearch() {
        SQLRecordset lostanimal = new SQLRecordset();

        // Reads all the criteria fields and performs the search
        String sql = "SELECT animallost.*, OwnerName, HomeTelephone FROM animallost INNER JOIN owner ON animallost.OwnerID = owner.ID WHERE ";
        addSqlCriteria("animallost.ID > 0");

        // Get the IDs of selected combo boxes
        String speciesid = Utils.getID("species", "SpeciesName",
                (String) cboSpecies.getSelectedItem()).toString();
        String colourid = Utils.getID("basecolour", "BaseColour",
                (String) cboColour.getSelectedItem()).toString();
        String sexid = Utils.getID("lksex", "Sex",
                (String) cboSex.getSelectedItem()).toString();
        String breedid = Utils.getID("breed", "BreedName",
                (String) cboBreed.getSelectedItem()).toString();
        String agegroup = cboAgeGroup.getSelectedItem().toString();

        if (!txtContact.getText().equals("")) {
            addSqlCriteria("UPPER(OwnerName) Like '%" +
                Utils.upper(txtContact.getText()).replace('\'', '`') + "%'");
        }

        if (!txtNumber.getText().equals("")) {
            try {
                Integer.parseInt(txtNumber.getText());
            } catch (NumberFormatException e) {
                Dialog.showError(i18n("Reference_number_must_be_numeric"));
            }

            addSqlCriteria("animallost.ID = " + txtNumber.getText());
        }

        if (!chkIncludeReturned.isSelected()) {
            addSqlCriteria("DateFound Is Null");
        }

        if (!colourid.equals("0")) {
            addSqlCriteria("BaseColourID=" + colourid);
        }

        if (!speciesid.equals("0")) {
            addSqlCriteria("AnimalTypeID=" + speciesid);
        }

        if (!cboSex.getSelectedItem().toString().equals(i18n("(all)"))) {
            addSqlCriteria("Sex=" + sexid);
        }

        if (!breedid.equals("0")) {
            addSqlCriteria("BreedID=" + breedid);
        }

        if (!agegroup.equals(i18n("(all)"))) {
            addSqlCriteria("AgeGroup='" + agegroup + "'");
        }

        if (!txtArea.getText().equals("")) {
            addSqlCriteria("UPPER(AreaLost) Like '%" +
                Utils.upper(txtArea.getText()).replace('\'', '`') + "%'");
        }

        if (!txtPostcode.getText().equals("")) {
            addSqlCriteria("UPPER(AreaPostcode) Like '%" +
                Utils.upper(txtPostcode.getText()) + "%'");
        }

        if (!txtDistFeat.getText().equals("")) {
            String[] words = Utils.split(txtDistFeat.getText(), " ");
            int i = 0;

            while (i < words.length) {
                addSqlCriteria("UPPER(DistFeat) Like '%" +
                    Utils.upper(words[i]) + "%'");
                i++;
            }
        }

        if (!txtFrom.getText().equals("") && !txtTo.getText().equals("")) {
            try {
                addSqlCriteria("DateLost BETWEEN '" +
                    Utils.getSQLDate(txtFrom.getText()) + "' AND '" +
                    Utils.getSQLDate(txtTo.getText()) + "'");
            } catch (ParseException e) {
                Dialog.showError(e.getMessage(), i18n("Bad_Date"));

                return;
            }
        }

        // Order
        sqlCriteria.append(" ORDER BY DateLost DESC");

        // Search limit
        int limit = Global.getRecordSearchLimit();

        if (limit != 0) {
            sqlCriteria.append(" LIMIT " + limit);
        }

        try {
            Global.logDebug("Lost animal search: " + sql + getSqlCriteria(),
                "FindLostAnimalSearch.run");
            lostanimal.openRecordset(sql + getSqlCriteria(), "lostanimal");
        } catch (Exception e) {
            Global.logException(e, getClass());

            return;
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) lostanimal.getRecordCount()][12];

        // Initialise the progress meter
        initStatusBarMax((int) lostanimal.getRecordCount());

        // Create an array of headers for the accounts
        String[] columnheaders = {
                i18n("Contact"), i18n("Number"), i18n("Area"), i18n("Postcode"),
                i18n("Date"), i18n("Age_Group"), i18n("Sex"), i18n("Species"),
                i18n("Breed"), i18n("Colour"), i18n("Features")
            };

        int i = 0;

        while (!lostanimal.getEOF()) {
            // Add this record to the table data
            try {
                datar[i][0] = lostanimal.getString("OwnerName");
                datar[i][1] = lostanimal.getString("HomeTelephone");
                datar[i][2] = Utils.formatAddress(lostanimal.getString(
                            "AreaLost"));
                datar[i][3] = lostanimal.getString("AreaPostcode");
                datar[i][4] = Utils.formatTableDate(lostanimal.getDate(
                            "DateLost"));
                datar[i][5] = lostanimal.getString("AgeGroup");
                datar[i][6] = LookupCache.getSexName(lostanimal.getInt("Sex"));
                datar[i][7] = LookupCache.getSpeciesName(lostanimal.getInt(
                            "AnimalTypeID"));
                datar[i][8] = LookupCache.getBreedName(lostanimal.getInt(
                            "BreedID"));
                datar[i][9] = LookupCache.getBaseColourName(lostanimal.getInt(
                            "BaseColourID"));
                datar[i][10] = lostanimal.getString("DistFeat");
                datar[i][11] = lostanimal.getString("ID");
                i++;
            } catch (Exception e) {
                Global.logException(e, getClass());

                break;
            }

            // Next record
            try {
                lostanimal.moveNext();
            } catch (Exception e) {
                Global.logException(e, getClass());

                break;
            }

            incrementStatusBar();
        }

        setTableData(columnheaders, datar, i, 11);
    }
}
