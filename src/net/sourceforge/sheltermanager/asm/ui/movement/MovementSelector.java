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
package net.sourceforge.sheltermanager.asm.ui.movement;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.MovementRenderer;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.MovementDocument;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * Panel class for embedding movement facilities in a frame.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MovementSelector extends ASMSelector implements MovementParent {
    private final static int DEATH_COLUMN = 8;

    /** Link to the owner ID or 0 if it is an animal link */
    private int ownerID = 0;

    /** Link to the animal ID or 0 if it is an owner link */
    private int animalID = 0;

    /** Array of tabledata */
    private String[][] tabledata = null;

    /** States whether there is any content in this form */
    private boolean hasMovements = false;

    /**
     * If this is embedded in an animal form, a reference to it, so the controls
     * can be updated according to data.
     */
    private AnimalEdit theparent = null;

    /** Same deal as EditAnimal for owners */
    private OwnerEdit theownerparent = null;
    private UI.Button btnAdd;
    private UI.Button btnDelete;
    private UI.Button btnDoc;
    private UI.Button btnEdit;
    private UI.Button btnViewAnimal;
    private UI.Button btnViewOwner;

    /** Creates new form BeanForm */
    public MovementSelector() {
        init("uimovement", false, false, new MovementRenderer(DEATH_COLUMN));
    }

    public MovementSelector(AnimalEdit theparent) {
        this();
        this.theparent = theparent;
    }

    public MovementSelector(OwnerEdit theownerparent) {
        this();
        this.theownerparent = theownerparent;
    }

    /**
     * Sets the ID link between parent and this. Also deactivates either the
     * view animal or view owner button depending on the host.
     */
    public void setLink(int owner, int animal) {
        ownerID = owner;
        animalID = animal;
        btnViewOwner.setEnabled(ownerID == 0);
        btnViewAnimal.setEnabled(animalID == 0);
    }

    public void dispose() {
        tabledata = null;
        theparent = null;
        super.dispose();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    /**
     * Fills the table with the media entries for the passed link.
     */
    public void updateList() {
        Adoption movement = new Adoption();

        if (ownerID != 0) {
            movement.openRecordset("OwnerID = " + Integer.toString(ownerID) +
                " ORDER BY MovementDate, ID");
        } else {
            movement.openRecordset("AnimalID = " + Integer.toString(animalID) +
                " ORDER BY MovementDate, ID");
        }

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        tabledata = new String[(int) movement.getRecordCount()][9];

        // Create an array of headers for the list
        String[] columnheaders = new String[7];

        // If this is an owner view, then column 3
        // becomes the animal shelter code, otherwise
        // it becomes the owner's address. column 2 is either
        // animal name or owner name depending on the view
        columnheaders[0] = Global.i18n("uimovement", "Date");
        columnheaders[1] = Global.i18n("uimovement", "Movement_Type");
        columnheaders[5] = Global.i18n("uimovement", "Adoption_No");
        columnheaders[6] = Global.i18n("uimovement", "Returned");

        if (ownerID != 0) {
            columnheaders[2] = Global.i18n("uimovement", "Animal");
            columnheaders[3] = Global.i18n("uimovement", "Code");
            columnheaders[4] = Global.i18n("uimovement", "Species");
        } else {
            columnheaders[2] = Global.i18n("uimovement", "Owner");
            columnheaders[3] = Global.i18n("uimovement", "Address");
            columnheaders[4] = Global.i18n("uimovement", "Phone");
        }

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!movement.getEOF()) {
                if (movement.getMovementDate() == null) {
                    tabledata[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                                movement.getReservationDate()));
                } else {
                    tabledata[i][0] = Utils.nullToEmptyString(Utils.formatTableDate(
                                movement.getMovementDate()));
                }

                tabledata[i][1] = movement.getReadableMovementType();

                if (ownerID == 0) {
                    Owner own = new Owner();
                    own.openRecordset("ID = " + movement.getOwnerID());

                    if (!own.getEOF()) {
                        tabledata[i][2] = movement.getOwner().getOwnerName();
                        tabledata[i][3] = Utils.formatAddress(movement.getOwner()
                                                                      .getOwnerAddress());
                        tabledata[i][4] = "H: " +
                            Utils.nullToEmptyString(movement.getOwner()
                                                            .getHomeTelephone()) +
                            ", W: " +
                            Utils.nullToEmptyString(movement.getOwner()
                                                            .getWorkTelephone());
                    } else {
                        tabledata[i][2] = Global.i18n("uimovement", "(no_owner)");
                        tabledata[i][3] = "";
                        tabledata[i][4] = "";
                    }

                    own.free();
                    own = null;
                } else {
                    tabledata[i][2] = movement.getAnimal().getAnimalName();
                    tabledata[i][3] = movement.getAnimal().getShelterCode();
                    tabledata[i][4] = movement.getAnimal().getSpeciesName();
                }

                tabledata[i][5] = Utils.nullToEmptyString(movement.getAdoptionNumber());
                tabledata[i][6] = Utils.nullToEmptyString(Utils.formatTableDate(
                            movement.getReturnDate()));
                tabledata[i][7] = movement.getID().toString();

                // Used by the renderer to determine whether or not to colour
                // the animal
                tabledata[i][8] = Utils.nullToEmptyString(Utils.formatTableDate(
                            movement.getAnimal().getDeceasedDate()));

                hasMovements = true;
                i++;
                movement.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 9, 7);

        // Tell the parent we just updated
        if (theparent != null) {
            theparent.checkLocation();
        }
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    /** Returns a value to state whether there is any content in this form */
    public boolean hasData() {
        return hasMovements;
    }

    public void updateMedia() {
        if (theparent != null) {
            theparent.animalmedia.updateList();
        }

        if (theownerparent != null) {
            theownerparent.media.updateList();
        }
    }

    /**
     * Reads current user's security settings and deactivates things they can't
     * do.
     */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAnimalMovements()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMovements()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecDeleteAnimalMovements()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnDoc.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewAnimal()) {
            btnViewAnimal.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewOwner()) {
            btnViewOwner.setEnabled(false);
        }
    }

    public void addToolButtons() {
        btnAdd = UI.getButton(null, i18n("Add_a_new_movement"), 'n',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMOVEMENT_NEW),
                UI.fp(this, "actionAdd"));
        addToolButton(btnAdd, false);

        btnEdit = UI.getButton(null, i18n("Edit_this_movement"), 'e',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMOVEMENT_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_movement"), 'd',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMOVEMENT_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnDoc = UI.getButton(null,
                i18n("Create_a_document_for_this_movement"), 'w',
                IconManager.getIcon(
                    IconManager.SCREEN_ANIMALMOVEMENT_GENERATEDOC),
                UI.fp(this, "actionDocument"));
        addToolButton(btnDoc, true);

        btnViewAnimal = UI.getButton(null, i18n("View_the_animal"), 'a',
                IconManager.getIcon(
                    IconManager.SCREEN_ANIMALMOVEMENT_VIEWANIMAL),
                UI.fp(this, "actionViewAnimal"));
        addToolButton(btnViewAnimal, true);

        btnViewOwner = UI.getButton(null, i18n("View_the_owner"), 'o',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMOVEMENT_VIEWOWNER),
                UI.fp(this, "actionViewOwner"));
        addToolButton(btnViewOwner, true);
    }

    public void actionViewOwner() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Open the movement
        Adoption movement = new Adoption();
        movement.openRecordset("ID = " + id);

        // Open the owner form
        try {
            OwnerEdit eo = new OwnerEdit();
            eo.openForEdit(movement.getOwnerID().intValue());
            Global.mainForm.addChild(eo);
        } catch (Exception e) {
            return;
        }

        try {
            movement.free();
            movement = null;
        } catch (Exception e) {
        }
    }

    public void actionViewAnimal() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Open the movement
        Adoption movement = new Adoption();
        movement.openRecordset("ID = " + id);

        // Open the animal form
        try {
            AnimalEdit ea = new AnimalEdit();
            Animal animal = LookupCache.getAnimalByID(movement.getAnimalID());
            ea.openForEdit(animal);
            Global.mainForm.addChild(ea);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            movement.free();
            movement = null;
        } catch (Exception e) {
        }
    }

    public void actionDocument() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Open the movement
        Adoption movement = new Adoption();
        movement.openRecordset("ID = " + id);

        // Generate the doc
        new MovementDocument(movement, this);
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Ask
        if (!Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            return;
        }

        // Delete it
        try {
            // Get the animal ID this movement belongs to
            Adoption ad = new Adoption();
            ad.openRecordset("ID = " + id);

            int anID = ad.getAnimalID().intValue();
            ad.free();
            ad = null;

            String sql = "DELETE FROM adoption WHERE ID = " + id;
            DBConnection.executeAction(sql);

            if (AuditTrail.enabled()) {
                AuditTrail.deleted("movement",
                    getTable().getValueAt(getTable().getSelectedRow(), 2)
                        .toString() + " " +
                    getTable().getValueAt(getTable().getSelectedRow(), 3)
                        .toString() + " " +
                    getTable().getValueAt(getTable().getSelectedRow(), 5)
                        .toString());
            }

            updateList();

            // Update the animal's denormalised fields
            Animal.updateAnimalStatus(anID);
        } catch (Exception e) {
            Dialog.showError(UI.messageDeleteError() + e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Open the movement
        Adoption movement = new Adoption();
        movement.openRecordset("ID = " + id);

        // Open the form
        MovementEdit em = new MovementEdit(this);
        em.openForEdit(movement, ((animalID != 0) ? 1 : 2));
        Global.mainForm.addChild(em);
    }

    public void actionAdd() {
        MovementEdit em = new MovementEdit(this);
        em.openForNew(animalID, ownerID);
        Global.mainForm.addChild(em);
    }
}
