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

import net.sourceforge.sheltermanager.asm.bo.AnimalCost;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Vector;


public class CostEdit extends ASMForm {
    private AnimalCost cost = null;
    private String audit = null;

    /** A reference back to the parent form that spawned this form */
    private CostSelector animalcosts = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private DateField txtCostDate;
    private CurrencyField txtCostAmount;
    private UI.ComboBox cboCostType;
    private UI.TextArea txtDescription;

    public CostEdit(CostSelector animalcosts) {
        this.animalcosts = animalcosts;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITANIMAL_COSTS),
            "uianimal");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboCostType);
        ctl.add(txtCostDate.getTextField());
        ctl.add(txtCostAmount.getTextField());
        ctl.add(txtDescription);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboCostType;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    /**
     * Sets the form into creating a new cost record
     *
     * @param animalid
     *            The Animal to create the new record for.
     */
    public void openForNew(int animalid) {
        this.cost = new AnimalCost();
        this.setTitle(i18n("New_Cost"));
        cost.openRecordset("ID = 0");

        try {
            cost.addNew();
            cost.setAnimalID(new Integer(animalid));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        // Date required
        txtCostDate.setToToday();
    }

    /**
     * Opens a given diet record for editing.
     *
     * @param dietid
     *            The ID of the vaccination to open
     */
    public void openForEdit(String costid) {
        this.cost = new AnimalCost();
        cost.openRecordset("ID = " + costid);
        this.setTitle(i18n("Edit_Cost"));
        loadData();
    }

    /** Loads data into the controls */
    public void loadData() {
        try {
            try {
                txtCostDate.setText(Utils.formatDate(
                        cost.getCostDate()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            Utils.setComboFromID(LookupCache.getCostTypeLookup(), "CostTypeName",
                cost.getCostTypeID(), cboCostType);
            txtCostAmount.setValue(cost.getCostAmount().doubleValue());
            txtDescription.setText(Utils.nullToEmptyString(cost.getDescription()));
            audit = UI.messageAudit(cost.getCreatedDate(), cost.getCreatedBy(),
                    cost.getLastChangedDate(), cost.getLastChangedBy());
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Saves data from the controls to the database 
     */
    public boolean saveData() {
        try {
            cost.setDescription(txtDescription.getText());

            try {
                cost.setCostDate(Utils.parseDate(txtCostDate.getText()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }
            cost.setCostTypeID(Utils.getIDFromCombo(LookupCache.getCostTypeLookup(),
                    "CostTypeName", cboCostType));
            cost.setCostAmount(new Double(txtCostAmount.getValue()));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            cost.save(Global.currentUserName);

            // Update the edit animal form if successful
            animalcosts.updateList();

            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage(), i18n("Validation_Error"));
        }

        return false;
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel bot = UI.getPanel(UI.getFlowLayout());

        cboCostType = UI.getCombo(i18n("Type:"), LookupCache.getCostTypeLookup(),
                "CostTypeName");
        UI.addComponent(top, i18n("Type:"), cboCostType);

        txtCostDate = UI.getDateField();
        UI.addComponent(top, i18n("Date:"), txtCostDate);

        txtCostAmount = UI.getCurrencyField();
        UI.addComponent(top, i18n("Cost:"), txtCostAmount);

        txtDescription = UI.getTextArea();
        UI.addComponent(mid, i18n("Description:"), txtDescription);

        btnOk = UI.getButton(i18n("Ok"), i18n("Save_this_record"), 'o', null,
                UI.fp(this, "saveData"));
        bot.add(btnOk);
        btnCancel = UI.getButton(i18n("Cancel"),
                i18n("Abandon_changes_to_this_record"), 'c', null,
                UI.fp(this, "dispose"));
        bot.add(btnCancel);

        add(top, UI.BorderLayout.NORTH);
        add(mid, UI.BorderLayout.CENTER);
        add(bot, UI.BorderLayout.SOUTH);
    }
}
