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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.AnimalDiet;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Vector;


/**
 * This class contains all code for editing individual diet records.
 *
 * @author Robin Rawson-Tetley
 */
public class DietEdit extends ASMForm {
    private AnimalDiet diet = null;
    private String audit = null;

    /** A reference back to the parent form that spawned this form */
    private DietSelector animaldiets = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboDiet;
    private UI.TextArea txtComments;
    private DateField txtStartDate;

    public DietEdit(DietSelector animaldiets) {
        this.animaldiets = animaldiets;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITANIMALDIET),
            "uianimal");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboDiet);
        ctl.add(txtStartDate.getTextField());
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboDiet;
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
     * Sets the form into creating a new diet record
     *
     * @param animalid
     *            The Animal to create the new record for.
     */
    public void openForNew(int animalid) {
        this.diet = new AnimalDiet();
        this.setTitle(i18n("New_Diet"));
        diet.openRecordset("ID = 0");

        try {
            diet.addNew();
            diet.setAnimalID(new Integer(animalid));
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("unable_to_create_new_record:") +
                e.getMessage(), i18n("Failed_Create"));
            Global.logException(e, getClass());
        }

        // Date required
        Calendar cal = Calendar.getInstance();
        txtStartDate.setToToday();
    }

    /**
     * Opens a given diet record for editing.
     *
     * @param dietid
     *            The ID of the vaccination to open
     */
    public void openForEdit(String dietid) {
        this.diet = new AnimalDiet();
        diet.openRecordset("ID = " + dietid);
        this.setTitle(i18n("Edit_Diet"));
        loadData();
    }

    /** Loads data into the controls */
    public void loadData() {
        try {
            try {
                this.txtStartDate.setText(Utils.formatDate(
                        diet.getDateStarted()));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            Utils.setComboFromID(LookupCache.getDietLookup(), "DietName",
                diet.getDietID(), cboDiet);
            this.txtComments.setText(Utils.nullToEmptyString(diet.getComments()));
            audit = UI.messageAudit(diet.getCreatedDate(), diet.getCreatedBy(),
                    diet.getLastChangedDate(), diet.getLastChangedBy());
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Saves data from the controls to the database and updates the animal
     * screen.
     */
    public boolean saveData() {
        try {
            diet.setComments(txtComments.getText());

            try {
                diet.setDateStarted(Utils.parseDate(txtStartDate.getText()));
            } catch (ParseException e) {
                Global.logException(e, getClass());
            }

            diet.setDietID(Utils.getIDFromCombo(LookupCache.getDietLookup(),
                    "DietName", cboDiet));
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("An_error_occurred_saving_to_the_local_recordset:_") +
                e.getMessage(), i18n("Error"));
            Global.logException(e, getClass());
        }

        try {
            diet.save(Global.currentUserName);

            // Update the edit animal form if successful
            animaldiets.updateList();

            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage(), i18n("Validation_Error"));
        }

        return false;
    }

    public void initComponents() {
        UI.Panel top = UI.getPanel(UI.getGridLayout(2));
        UI.Panel mid = UI.getPanel(UI.getGridLayout(2));
        UI.Panel bot = UI.getPanel(UI.getFlowLayout());

        cboDiet = UI.getCombo(i18n("Diet_Type:"), LookupCache.getDietLookup(),
                "DietName");
        cboDiet.setToolTipText(i18n("The_type_of_diet"));
        UI.addComponent(top, i18n("Diet_Type:"), cboDiet);

        txtStartDate = UI.getDateField(i18n("The_date_the_animal_started_this_diet"));
        UI.addComponent(top, i18n("Start_Date:"), txtStartDate);

        txtComments = UI.getTextArea(i18n("Any_comments_about_the_diet"));
        UI.addComponent(mid, i18n("Comments:"), txtComments);

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
