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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalCost;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


@SuppressWarnings("serial")
public class CostSelector extends ASMSelector {
    /** The animal ID for the link */
    public int animalID = 0;

    /** The array of data to fill the table */
    public String[][] tabledata;

    /**
     * A flag to say whether there is anything interesting in this control
     */
    private boolean hasCost = false;
    private int totalboardcost = 0;
    private int daysonshelter = 0;
    private UI.Button btnAdd;
    private UI.Button btnDelete;
    private UI.Button btnEdit;
    private UI.Button btnRefresh;
    private AnimalEdit parent = null;
    private CurrencyField txtBoardingCost;
    private UI.Label lblBoardCost;
    private UI.Label lblTotals;

    public CostSelector(AnimalEdit parent) {
        this.parent = parent;
        init("uianimal", true);
        addBoardBar();
    }

    public Vector<Object> getTabOrder() {
        return null;
    }

    public Object getDefaultFocusedComponent() {
        return null;
    }

    /**
     * Reads current user's security settings and deactivates things they can't
     * do.
     */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAnimalCost()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalCost()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteAnimalCost()) {
            btnDelete.setEnabled(false);
        }
    }

    /**
     * Sets the appropriate animal ID
     *
     * @param animalID
     *            The ID of the animal.
     * @param linkType ignored
     */
    public void setLink(int animalID, int linkType) {
        this.animalID = animalID;

        try {
            // Calculate and show the boarding bar if the animal is on shelter
            Animal a = new Animal();
            a.openRecordset("ID = " + animalID);

            if (a.getArchived().intValue() == 0) {
                daysonshelter = a.getDaysOnShelter();
                txtBoardingCost.setValue(a.getDailyBoardingCost().intValue());
                updateOnShelterCost();
                getTopPanel().setVisible(true);
                invalidate();
            } else {
                totalboardcost = 0;
                getTopPanel().setVisible(false);
                invalidate();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void dispose() {
        tabledata = null;
    }

    public double getDailyBoardingCost() {
        return txtBoardingCost.getValue();
    }

    public double getOnShelterCost() {
        return totalboardcost;
    }

    public void setDailyBoardingCost(int d) {
        txtBoardingCost.setValue(d);
    }

    public void updateOnShelterCost() {
        totalboardcost = txtBoardingCost.getValue() * (int) daysonshelter;
        lblBoardCost.setText(i18n("On_shelter_days_total_cost",
                Integer.toString(daysonshelter),
                Utils.formatCurrency(totalboardcost)));
    }

    public void saveBoardingCost() throws Exception {
        DBConnection.executeAction("UPDATE animal SET DailyBoardingCost = " +
            txtBoardingCost.getValue() + " WHERE ID = " + animalID);
    }

    /**
     * Fills the table with the diet entries for the passed link.
     */
    public void updateList() {
        try {
            // Show the totals for vaccinations, medicals, costs and all 3 at the bottom
            SQLRecordset tots = new SQLRecordset();
            tots.openRecordset("SELECT " +
                "(SELECT SUM(Cost) FROM animalvaccination WHERE AnimalID = animal.ID AND DateOfVaccination Is Not Null) AS totvacc, " +
                "(SELECT SUM(Cost) FROM animalmedical WHERE AnimalID = animal.ID) AS totmed, " +
                "(SELECT SUM(CostAmount) FROM animalcost WHERE AnimalID = animal.ID) AS totcost, " +
                "(SELECT SUM(Donation) FROM ownerdonation WHERE AnimalID = animal.ID) AS totdon " +
                "FROM animal WHERE ID = " + animalID, "animal");

            int tv = tots.getInt("totvacc");
            int tm = tots.getInt("totmed");
            int tc = tots.getInt("totcost");
            int td = tots.getInt("totdon");
            int ta = tv + tm + tc + totalboardcost;
            String s = i18n("cost_totals", Utils.formatCurrency(tv),
                    Utils.formatCurrency(tm), Utils.formatCurrency(tc),
                    "<b>" + Utils.formatCurrency(ta)) + "</b><br />" +
                i18n("cost_balance", Utils.formatCurrency(td),
                    "<b>" + Utils.formatCurrency(td - ta) + "</b>");
            lblTotals.setText(s);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        AnimalCost ac = new AnimalCost();
        ac.openRecordset("AnimalID = " + animalID);

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        tabledata = new String[(int) ac.getRecordCount()][5];

        // Create an array of headers for the accounts (one less than
        // array because 4th col will hold ID
        String[] columnheaders = {
                Global.i18n("uianimal", "Date"), Global.i18n("uianimal", "Type"),
                Global.i18n("uianimal", "Amount"),
                Global.i18n("uianimal", "Description")
            };

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!ac.getEOF()) {
                tabledata[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                            ac.getCostDate()));
                tabledata[i][1] = ac.getCostTypeName();
                tabledata[i][2] = Utils.formatCurrency(ac.getCostAmount()
                                                         .doubleValue());
                tabledata[i][3] = ac.getDescription();
                tabledata[i][4] = ac.getID().toString();
                hasCost = true;
                i++;
                ac.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 4);
    }

    /** Returns true if there is some content in the list */
    public boolean hasData() {
        return hasCost;
    }

    public void addToolButtons() {
        btnAdd = addToolButton(UI.getButton(null, i18n("Create_new_cost"), 'n',
                    IconManager.getIcon(IconManager.SCREEN_ANIMALCOSTS_NEW),
                    UI.fp(this, "actionAdd")), false);

        btnEdit = addToolButton(UI.getButton(null, i18n("Edit_this_cost"), 'e',
                    IconManager.getIcon(IconManager.SCREEN_ANIMALCOSTS_EDIT),
                    UI.fp(this, "actionEdit")), true);

        btnDelete = addToolButton(UI.getButton(null, i18n("Delete_this_cost"),
                    'd',
                    IconManager.getIcon(IconManager.SCREEN_ANIMALCOSTS_DELETE),
                    UI.fp(this, "actionDelete")), true);

        btnRefresh = UI.getButton(null, i18n("Refresh_the_list"), 'r',
                IconManager.getIcon(IconManager.SCREEN_ANIMALCOSTS_REFRESH),
                UI.fp(this, "actionRefresh"));
        addToolButton(btnRefresh, false);
    }

    public void addBoardBar() {
        UI.Panel t = getTopPanel();
        t.setVisible(false);
        txtBoardingCost = UI.getCurrencyField(i18n("The_daily_cost_food_and_board"),
                UI.fp(this, "costChanged"));
        lblBoardCost = UI.getLabel();
        UI.addComponent(t, i18n("daily_board_cost"), txtBoardingCost);
        UI.addComponent(t, lblBoardCost);
        lblTotals = UI.getHintLabel("");
        this.add(lblTotals, UI.BorderLayout.SOUTH);
    }

    public void costChanged() {
        parent.dataChanged();
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionRefresh() {
        updateOnShelterCost();
        updateList();
    }

    public void actionDelete() {
        // Read the highlighted table record and get the ID
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            // Remove it from the database
            try {
                SortableTableModel tablemodel = (SortableTableModel) getTable()
                                                                         .getModel();
                String s = "Delete From animalcost Where ID = " + id;
                DBConnection.executeAction(s);

                if (AuditTrail.enabled()) {
                    AuditTrail.deleted("animalcost",
                        LookupCache.getAnimalByID(animalID).getShelterCode() +
                        " " +
                        LookupCache.getAnimalByID(animalID).getAnimalName() +
                        " " +
                        tablemodel.getValueAt(getTable().getSelectedRow(), 1)
                                  .toString() + " " +
                        tablemodel.getValueAt(getTable().getSelectedRow(), 3)
                                  .toString());
                }

                updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        // Read the highlighted table record and get the ID
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        try {
            CostEdit ce = new CostEdit(this);
            ce.openForEdit(Integer.toString(id));
            Global.mainForm.addChild(ce);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionAdd() {
        CostEdit ce = new CostEdit(this);
        ce.openForNew(animalID);
        Global.mainForm.addChild(ce);
    }
}
