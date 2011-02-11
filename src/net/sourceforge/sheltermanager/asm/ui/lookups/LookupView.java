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
package net.sourceforge.sheltermanager.asm.ui.lookups;

import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * This class contains all code for viewing a list of lookups
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class LookupView extends ASMView {
    public static final int ANIMALTYPE = 0;
    public static final int BASECOLOUR = 1;
    public static final int BREED = 2;
    public static final int INTERNALLOCATION = 3;
    public static final int SPECIES = 4;
    public static final int VACCINATIONTYPE = 5;
    public static final int ENTRYREASON = 6;
    public static final int DEATHREASON = 7;
    public static final int DIET = 8;
    public static final int VOUCHER = 9;
    public static final int LOGTYPE = 10;
    public static final int DONATIONTYPE = 11;
    public static final int COSTTYPE = 12;

    /** These values set according to edit type constant */
    private String tableName = "";
    private String nameField = "";
    private String descriptionField = "";
    private String nameDisplay = "";
    private String descriptionDisplay = "";
    private String lookupDisplay = "";

    /**
     * These values hold names of fields within tables which use the current
     * lookup. This is so they can be checked in delete operations and refuse if
     * they are in use. This is because MySQL does not support foreign keys.
     */
    private String[] foreignTable = null;
    private String[] foreignField = null;
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnView;

    /** Creates new form ViewLookup */
    public LookupView(int lookupType) {
        init("", IconManager.getIcon(IconManager.SCREEN_VIEWLOOKUP),
            "uilookups", false, true, null);
        setLookupType(lookupType);
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(btnNew);
        ctl.add(btnView);
        ctl.add(btnDelete);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        // We could have edited some lookups - repopulate the cache
        LookupCache.invalidate();
        LookupCache.fill();

        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return false;
    }

    public void loadData() {
    }

    /**
     * Sets all the local information about which lookup table we are editing
     * based on one of the constants passed in the constructor.
     */
    public void setLookupType(int lookupType) {
        switch (lookupType) {
        case ANIMALTYPE: {
            tableName = "animaltype";
            nameField = "AnimalType";
            descriptionField = "AnimalDescription";
            nameDisplay = Global.i18n("uilookups", "Animal_Type_Name");
            descriptionDisplay = Global.i18n("uilookups",
                    "Animal_Type_Description");
            lookupDisplay = Global.i18n("uilookups", "Animal_Types");
            foreignTable = new String[] { "animal" };
            foreignField = new String[] { "AnimalTypeID" };

            break;
        }

        case BASECOLOUR: {
            tableName = "basecolour";
            nameField = "BaseColour";
            descriptionField = "BaseColourDescription";
            nameDisplay = Global.i18n("uilookups", "Colour_Name");
            descriptionDisplay = Global.i18n("uilookups", "Colour_Description");
            lookupDisplay = Global.i18n("uilookups", "Base_Colours");
            foreignTable = new String[] { "animal", "animallost", "animalfound" };
            foreignField = new String[] {
                    "BaseColourID", "BaseColourID", "BaseColourID"
                };

            break;
        }

        case BREED: {
            tableName = "breed";
            nameField = "BreedName";
            descriptionField = "BreedDescription";
            nameDisplay = Global.i18n("uilookups", "Breed_Name");
            descriptionDisplay = Global.i18n("uilookups", "Breed_Description");
            lookupDisplay = Global.i18n("uilookups", "Breeds");
            foreignTable = new String[] { "animal" };
            foreignField = new String[] { "BreedID" };

            break;
        }

        case INTERNALLOCATION: {
            tableName = "internallocation";
            nameField = "LocationName";
            descriptionField = "LocationDescription";
            nameDisplay = Global.i18n("uilookups", "Location_Name");
            descriptionDisplay = Global.i18n("uilookups", "Location_Description");
            lookupDisplay = Global.i18n("uilookups", "Internal_Locations");
            foreignTable = new String[] { "animal" };
            foreignField = new String[] { "ShelterLocation" };

            break;
        }

        case SPECIES: {
            tableName = "species";
            nameField = "SpeciesName";
            descriptionField = "SpeciesDescription";
            nameDisplay = Global.i18n("uilookups", "Species_Name");
            descriptionDisplay = Global.i18n("uilookups", "Species_Description");
            lookupDisplay = Global.i18n("uilookups", "Species");
            foreignTable = new String[] {
                    "animal", "animallost", "animalfound", "animalwaitinglist"
                };
            foreignField = new String[] {
                    "SpeciesID", "AnimalTypeID", "AnimalTypeID", "SpeciesID"
                };

            break;
        }

        case VACCINATIONTYPE: {
            tableName = "vaccinationtype";
            nameField = "VaccinationType";
            descriptionField = "VaccinationDescription";
            nameDisplay = Global.i18n("uilookups", "Vaccination_Name");
            descriptionDisplay = Global.i18n("uilookups",
                    "Vaccination_Description");
            lookupDisplay = Global.i18n("uilookups", "Vaccination_Types");
            foreignTable = new String[] { "animalvaccination" };
            foreignField = new String[] { "VaccinationID" };

            break;
        }

        case ENTRYREASON: {
            tableName = "entryreason";
            nameField = "ReasonName";
            descriptionField = "ReasonDescription";
            nameDisplay = Global.i18n("uilookups", "Reason_Name");
            descriptionDisplay = Global.i18n("uilookups", "Reason_Description");
            lookupDisplay = Global.i18n("uilookups", "Entry_Reasons");
            foreignTable = new String[] { "animal", "adoption" };
            foreignField = new String[] { "EntryReasonID", "ReturnedReasonID" };

            break;
        }

        case DEATHREASON: {
            tableName = "deathreason";
            nameField = "ReasonName";
            descriptionField = "ReasonDescription";
            nameDisplay = Global.i18n("uilookups", "Reason_Name");
            descriptionDisplay = Global.i18n("uilookups", "Reason_Description");
            lookupDisplay = Global.i18n("uilookups", "Death_Reasons");
            foreignTable = new String[] { "animal" };
            foreignField = new String[] { "PTSReasonID" };

            break;
        }

        case DIET: {
            tableName = "diet";
            nameField = "DietName";
            descriptionField = "DietDescription";
            nameDisplay = Global.i18n("uilookups", "Diet_Name");
            descriptionDisplay = Global.i18n("uilookups", "Diet_Description");
            lookupDisplay = Global.i18n("uilookups", "Diets");
            foreignTable = new String[] { "animaldiet" };
            foreignField = new String[] { "DietID" };

            break;
        }

        case VOUCHER: {
            tableName = "voucher";
            nameField = "VoucherName";
            descriptionField = "VoucherDescription";
            nameDisplay = Global.i18n("uilookups", "Voucher_Name");
            descriptionDisplay = Global.i18n("uilookups", "Voucher_Description");
            lookupDisplay = Global.i18n("uilookups", "Vouchers");
            foreignTable = new String[] { "ownervoucher" };
            foreignField = new String[] { "VoucherID" };

            break;
        }

        case LOGTYPE: {
            tableName = "logtype";
            nameField = "LogTypeName";
            descriptionField = "LogTypeDescription";
            nameDisplay = Global.i18n("uilookups", "Log_Type_Name");
            descriptionDisplay = Global.i18n("uilookups", "Log_Type_Description");
            lookupDisplay = Global.i18n("uilookups", "Log_Types");
            foreignTable = new String[] { "log" };
            foreignField = new String[] { "LogTypeID" };

            break;
        }

        case DONATIONTYPE: {
            tableName = "donationtype";
            nameField = "DonationName";
            descriptionField = "DonationDescription";
            nameDisplay = Global.i18n("uilookups", "donation_type_name");
            descriptionDisplay = Global.i18n("uilookups",
                    "donation_type_description");
            lookupDisplay = Global.i18n("uilookups", "donation_types");
            foreignTable = new String[] { "ownerdonation" };
            foreignField = new String[] { "DonationTypeID" };

            break;
        }

        case COSTTYPE: {
            tableName = "costtype";
            nameField = "CostTypeName";
            descriptionField = "CostTypeDescription";
            nameDisplay = Global.i18n("uilookups", "cost_type_name");
            descriptionDisplay = Global.i18n("uilookups",
                    "cost_type_description");
            lookupDisplay = Global.i18n("uilookups", "cost_types");
            foreignTable = new String[] { "animalcost" };
            foreignField = new String[] { "CostTypeID" };

            break;
        }

        default:
            // Should never get here - invalid lookup type was passed
            dispose();

            return;
        }

        this.setTitle(Global.i18n("uilookups", "edit_x", lookupDisplay));

        // Update the table based on our selections
        updateList();
    }

    public void updateList() {
        // Get the data
        SQLRecordset rs = new SQLRecordset();

        try {
            rs.openRecordset("SELECT * FROM " + tableName + " ORDER BY " +
                nameField, tableName);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) rs.getRecordCount()][3];

        // Create an array of headers for the table
        String[] columnheaders = new String[] { nameDisplay, descriptionDisplay };

        // Build the data
        int i = 0;

        try {
            while (!rs.getEOF()) {
                datar[i][0] = (String) rs.getField(nameField);
                datar[i][1] = Utils.nullToEmptyString((String) rs.getField(
                            descriptionField));
                datar[i][2] = rs.getField("ID").toString();

                i++;
                rs.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 2);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("New"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWLOOKUP_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWLOOKUP_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, i18n("Delete"), 'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWLOOKUP_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Make sure they're sure
        if (!Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            return;
        }

        // Enumerate all selected
        int[] selrows = getTable().getSelectedRows();
        SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                 .getModel();

        for (int z = 0; z < selrows.length; z++) {
            // Get the ID of the lookup item
            String theID = tablemodel.getIDAt(selrows[z]);

            try {
                // Enumerate all the foreign keys we currently have to
                // check if this one exists
                boolean canDelete = true;

                for (int i = 0; i < foreignTable.length; i++) {
                    try {
                        SQLRecordset nrs = new SQLRecordset();
                        nrs.openRecordset("SELECT ID FROM " + foreignTable[i] +
                            " WHERE " + foreignField[i] + " = " + theID,
                            foreignTable[i]);

                        if (!nrs.getEOF()) {
                            // The record is in use - bomb out
                            Dialog.showError((String) tablemodel.getValueAt(
                                    selrows[z], 0) + " - " +
                                Global.i18n("uilookups",
                                    "This_record_is_currently_in_use_in_the_database._\nYou_must_remove_all_records_using_this_record_first."));

                            canDelete = false;
                        }
                    } catch (Exception e) {
                        Dialog.showError(e.getMessage());
                        Global.logException(e, getClass());

                        return;
                    }
                }

                String sql = "DELETE FROM " + tableName + " WHERE ID = " +
                    theID;

                try {
                    if (canDelete) {
                        DBConnection.executeAction(sql);
                    }
                } catch (Exception e) {
                    Dialog.showError(UI.messageDeleteError() + e.getMessage());
                    Global.logException(e, getClass());
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // Redisplay after deletion.
        updateList();
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Open it in the edit form
        LookupEdit el = new LookupEdit(tableName, nameField, descriptionField,
                nameDisplay, descriptionDisplay, lookupDisplay, this);
        el.openForEdit(id);
        Global.mainForm.addChild(el);
    }

    public void actionNew() {
        LookupEdit el = new LookupEdit(tableName, nameField, descriptionField,
                nameDisplay, descriptionDisplay, lookupDisplay, this);
        el.openForNew();
        Global.mainForm.addChild(el);
    }
}
