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
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.Report;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.awt.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * View that allows different modes to view different types of
 * movements.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class MovementView extends ASMView implements MovementParent {
    public final static int MODE_RESERVATION = 0;
    public final static int MODE_FOSTERS = 1;
    public final static int MODE_RETAILERS = 2;
    private String[][] tabledata = null;
    private String[] columnheaders = null;
    private UI.Button btnNew;
    private UI.Button btnRefresh;
    private UI.Button btnReturnCreateNew;
    private UI.Button btnView;
    private UI.Button btnViewAnimal;
    private UI.Button btnViewOwner;
    private UI.Button btnPrint;
    private int mode = MODE_RESERVATION;

    /** Creates new form FosterBook */
    public MovementView(int mode) {
        this.mode = mode;

        switch (mode) {
        case MODE_RESERVATION:
            init(Global.i18n("uimovement", "Reservation_Book"),
                IconManager.getIcon(IconManager.SCREEN_RESERVATIONBOOK),
                "uimovement");

            break;

        case MODE_RETAILERS:
            init(Global.i18n("uimovement", "Retailer_Book"),
                IconManager.getIcon(IconManager.SCREEN_RETAILERBOOK),
                "uimovement");

            break;

        case MODE_FOSTERS:
            init(Global.i18n("uimovement", "Foster_Book"),
                IconManager.getIcon(IconManager.SCREEN_FOSTERBOOK), "uimovement");

            break;
        }

        updateList();
    }

    @Override
    public Vector<Component> getTabOrder() {
        Vector<Component> ctl = new Vector<Component>();
        ctl.add(btnRefresh);
        ctl.add(btnPrint);
        ctl.add(btnNew);
        ctl.add(btnView);
        ctl.add(btnViewAnimal);
        ctl.add(btnViewOwner);
        ctl.add(getTable());

        return ctl;
    }

    @Override
    public Component getDefaultFocusedComponent() {
        return btnRefresh;
    }

    @Override
    public String getAuditInfo() {
        return null;
    }

    @Override
    public void setSecurity() {
        if (!Global.currentUserObject.getSecViewAnimal()) {
            btnViewAnimal.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewOwner()) {
            btnViewOwner.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMovements()) {
            btnView.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimalMovements()) {
            btnNew.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewCustomReports()) {
            btnPrint.setEnabled(false);
        }
    }

    @Override
    public void setLink(int a, int b) {
    }

    @Override
    public void loadData() {
    }

    @Override
    public boolean saveData() {
        return true;
    }

    @Override
    public void updateList() {
        // Calculate time difference. We show all outstanding movements PLUS
        // movements that were edited in the last 5 minutes by this user.
        Calendar cminsAgo = Calendar.getInstance();
        Calendar cnow = Calendar.getInstance();
        cminsAgo.add(Calendar.MINUTE, -5);

        String minsAgo = Utils.getSQLDate(cminsAgo);
        String now = Utils.getSQLDate(cnow);

        SQLRecordset rs = new SQLRecordset();
        String sql = null;

        String shelterCode = "animal.ShelterCode AS TheCode";

        if (Global.getShowShortCodes()) {
            shelterCode = "animal.ShortCode AS TheCode";
        }

        switch (mode) {
        case MODE_RESERVATION:
            sql = "SELECT animal.AnimalName, " + shelterCode +
                ", animaltype.AnimalType, " +
                "species.SpeciesName, owner.OwnerName, adoption.ReservationDate, adoption.ReturnDate, " +
                "animal.DateOfBirth ,animal.DeceasedDate ,animal.HasActiveReserve ,animal.ActiveMovementType, " +
                "adoption.ID " + "FROM adoption " +
                "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                "INNER JOIN owner ON adoption.OwnerID = owner.ID " +
                "INNER JOIN species ON animal.SpeciesID = species.ID " +
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID " +
                "WHERE adoption.ReservationDate Is Not Null AND " +
                "adoption.ReservationCancelledDate Is Null " +
                "AND adoption.MovementDate Is Null AND adoption.MovementType = 0 " +
                "AND adoption.ReturnDate Is Null AND animal.DeceasedDate Is Null";

            break;

        case MODE_RETAILERS:
            sql = "SELECT animal.AnimalName, " + shelterCode +
                ", animaltype.AnimalType, " +
                "species.SpeciesName, owner.OwnerName, adoption.MovementDate, adoption.ID, adoption.ReturnDate " +
                ",animal.DateOfBirth ,animal.DeceasedDate ,animal.HasActiveReserve ,animal.ActiveMovementType " +
                "FROM adoption " +
                "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                "INNER JOIN owner ON adoption.OwnerID = owner.ID " +
                "INNER JOIN species ON animal.SpeciesID = species.ID " +
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID " +
                "WHERE adoption.MovementDate Is Not Null AND " +
                "adoption.MovementType = " + Adoption.MOVETYPE_RETAILER + " " +
                "AND animal.DeceasedDate Is Null AND ((" +
                "adoption.ReturnDate Is Null OR adoption.ReturnDate > '" +
                Utils.getSQLDate(new Date()) + "') OR " +
                "(adoption.LastChangedDate BETWEEN '" + minsAgo + "' AND '" +
                now + "' " + "AND adoption.LastChangedBy Like '" +
                Global.currentUserName + "'))";

            break;

        case MODE_FOSTERS:
            sql = "SELECT animal.AnimalName, " + shelterCode +
                ", animaltype.AnimalType, " +
                "species.SpeciesName, owner.OwnerName, adoption.MovementDate, adoption.ID, adoption.ReturnDate " +
                ",animal.DateOfBirth ,animal.DeceasedDate ,animal.HasActiveReserve ,animal.ActiveMovementType " +
                "FROM adoption " +
                "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                "INNER JOIN owner ON adoption.OwnerID = owner.ID " +
                "INNER JOIN species ON animal.SpeciesID = species.ID " +
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID " +
                "WHERE adoption.MovementDate Is Not Null AND " +
                "adoption.MovementType = " + Adoption.MOVETYPE_FOSTER + " " +
                "AND animal.DeceasedDate Is Null AND ((" +
                "adoption.ReturnDate Is Null OR adoption.ReturnDate > '" +
                Utils.getSQLDate(new Date()) + "') OR " +
                "(adoption.LastChangedDate BETWEEN '" + minsAgo + "' AND '" +
                now + "' " + "AND adoption.LastChangedBy Like '" +
                Global.currentUserName + "'))";

            break;
        }

        try {
            Global.logDebug(sql, "MovementView.updateList");
            rs.openRecordset(sql, "adoption");
        } catch (Exception e) {
            Global.logException(e, getClass());

            return;
        }

        // Create an array of headers for the accounts (two less than array
        // because last cols will hold the ID and the report animal name)
        columnheaders = new String[7];
        columnheaders[0] = Global.i18n("uimovement", "Name");
        columnheaders[1] = Global.i18n("uimovement", "Code");
        columnheaders[2] = Global.i18n("uimovement", "Type");
        columnheaders[3] = Global.i18n("uimovement", "Species");
        columnheaders[4] = Global.i18n("uimovement", "Owner");
        columnheaders[5] = Global.i18n("uimovement", "Date");
        columnheaders[6] = Global.i18n("uimovement", "return_date");

        // Create an array to hold the results for the table - note that we
        // have two extra columns here - the next to last column will actually hold
        // the ID, and the last column the HTML AnimalName for the Print report
        tabledata = new String[(int) rs.getRecordCount()][columnheaders.length +
            2];

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!rs.getEOF()) {
                tabledata[i][0] = rs.getField("AnimalName").toString();
                tabledata[i][1] = rs.getField("TheCode").toString();
                tabledata[i][2] = rs.getField("AnimalType").toString();
                tabledata[i][3] = rs.getField("SpeciesName").toString();
                tabledata[i][4] = rs.getField("OwnerName").toString();

                if (mode == MODE_RESERVATION) {
                    tabledata[i][5] = Utils.nullToEmptyString(Utils.formatTableDate(
                                (Date) rs.getField("ReservationDate")));
                } else {
                    tabledata[i][5] = Utils.nullToEmptyString(Utils.formatTableDate(
                                (Date) rs.getField("MovementDate")));
                }

                tabledata[i][6] = Utils.nullToEmptyString(Utils.formatTableDate(
                            (Date) rs.getField("ReturnDate")));

                tabledata[i][7] = rs.getField("ID").toString();
                tabledata[i][8] = Animal.getReportAnimalName(rs.getField(
                            "AnimalName").toString(),
                        (Date) rs.getField("DateOfBirth"),
                        (Date) rs.getField("DeceasedDate"),
                        (Integer) rs.getField("HasActiveReserve"),
                        (Integer) rs.getField("ActiveMovementType"));
                i++;
                rs.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, columnheaders.length + 2,
            columnheaders.length);

        try {
            rs.free();
            rs = null;
        } catch (Exception e) {
        }
    }

    @Override
    public void addToolButtons() {
        switch (mode) {
        case MODE_RESERVATION:
            btnRefresh = UI.getButton(null,
                    i18n("Refresh_the_list_of_reservations"), 'r',
                    IconManager.getIcon(
                        IconManager.SCREEN_RESERVATIONBOOK_REFRESH),
                    UI.fp(this, "actionRefresh"));
            addToolButton(btnRefresh, false);

            btnPrint = UI.getButton(null,
                    Global.i18n("uireportviewer", "Print"), 'p',
                    IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_PRINT),
                    UI.fp(this, "actionPrint"));
            addToolButton(btnPrint, false);

            btnNew = UI.getButton(null, i18n("Create_a_new_reservation"), 'n',
                    IconManager.getIcon(IconManager.SCREEN_RESERVATIONBOOK_NEW),
                    UI.fp(this, "actionNew"));
            addToolButton(btnNew, false);

            btnView = UI.getButton(null,
                    i18n("View_the_movement_record_for_this_reservation"), 'e',
                    IconManager.getIcon(IconManager.SCREEN_RESERVATIONBOOK_EDIT),
                    UI.fp(this, "actionEdit"));
            addToolButton(btnView, true);

            btnViewAnimal = UI.getButton(null,
                    i18n("View_the_animal_for_this_reservation"), 'a',
                    IconManager.getIcon(
                        IconManager.SCREEN_RESERVATIONBOOK_VIEWANIMAL),
                    UI.fp(this, "actionViewAnimal"));
            addToolButton(btnViewAnimal, true);

            btnViewOwner = UI.getButton(null,
                    i18n("View_the_owner_for_this_reservation"), 'o',
                    IconManager.getIcon(
                        IconManager.SCREEN_RESERVATIONBOOK_VIEWOWNER),
                    UI.fp(this, "actionViewOwner"));
            addToolButton(btnViewOwner, true);

            break;

        case MODE_RETAILERS:
            btnRefresh = UI.getButton(null,
                    i18n("Refresh_the_list_of_retailer_movements"), 'r',
                    IconManager.getIcon(IconManager.SCREEN_RETAILERBOOK_REFRESH),
                    UI.fp(this, "actionRefresh"));
            addToolButton(btnRefresh, false);

            btnPrint = UI.getButton(null,
                    Global.i18n("uireportviewer", "Print"), 'p',
                    IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_PRINT),
                    UI.fp(this, "actionPrint"));
            addToolButton(btnPrint, false);

            btnNew = UI.getButton(null, i18n("Create_a_new_retailer_movement"),
                    'n',
                    IconManager.getIcon(IconManager.SCREEN_RETAILERBOOK_NEW),
                    UI.fp(this, "actionNew"));
            addToolButton(btnNew, false);

            btnView = UI.getButton(null, i18n("View_this_retailer_movement"),
                    'e',
                    IconManager.getIcon(IconManager.SCREEN_RETAILERBOOK_EDIT),
                    UI.fp(this, "actionEdit"));
            addToolButton(btnView, true);

            btnReturnCreateNew = UI.getButton(null,
                    i18n("return_this_movement_and_create_a_new_one"), 'u',
                    IconManager.getIcon(
                        IconManager.SCREEN_RETAILERBOOK_RETURNCREATENEW),
                    UI.fp(this, "actionReturnCreate"));
            addToolButton(btnReturnCreateNew, true);

            btnViewAnimal = UI.getButton(null,
                    i18n("View_the_animal_for_this_retailer_movement"), 'a',
                    IconManager.getIcon(
                        IconManager.SCREEN_RETAILERBOOK_VIEWANIMAL),
                    UI.fp(this, "actionViewAnimal"));
            addToolButton(btnViewAnimal, true);

            btnViewOwner = UI.getButton(null,
                    i18n("View_the_retailer_for_this_movement"), 'o',
                    IconManager.getIcon(
                        IconManager.SCREEN_RETAILERBOOK_VIEWOWNER),
                    UI.fp(this, "actionViewOwner"));
            addToolButton(btnViewOwner, true);

            break;

        case MODE_FOSTERS:
            btnRefresh = UI.getButton(null,
                    i18n("Refresh_the_list_of_fosters"), 'r',
                    IconManager.getIcon(IconManager.SCREEN_FOSTERBOOK_REFRESH),
                    UI.fp(this, "actionRefresh"));
            addToolButton(btnRefresh, false);

            btnPrint = UI.getButton(null,
                    Global.i18n("uireportviewer", "Print"), 'p',
                    IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_PRINT),
                    UI.fp(this, "actionPrint"));
            addToolButton(btnPrint, false);

            btnNew = UI.getButton(null, i18n("Create_a_new_foster"), 'n',
                    IconManager.getIcon(IconManager.SCREEN_FOSTERBOOK_NEW),
                    UI.fp(this, "actionNew"));
            addToolButton(btnNew, false);

            btnView = UI.getButton(null,
                    i18n("View_the_movement_record_for_this_foster"), 'e',
                    IconManager.getIcon(IconManager.SCREEN_FOSTERBOOK_EDIT),
                    UI.fp(this, "actionEdit"));
            addToolButton(btnView, true);

            btnReturnCreateNew = UI.getButton(null,
                    i18n("return_this_movement_and_create_a_new_one"), 'u',
                    IconManager.getIcon(
                        IconManager.SCREEN_FOSTERBOOK_RETURNCREATENEW),
                    UI.fp(this, "actionReturnCreate"));
            addToolButton(btnReturnCreateNew, true);

            btnViewAnimal = UI.getButton(null,
                    i18n("View_the_animal_for_this_foster"), 'a',
                    IconManager.getIcon(
                        IconManager.SCREEN_FOSTERBOOK_VIEWANIMAL),
                    UI.fp(this, "actionViewAnimal"));
            addToolButton(btnViewAnimal, true);

            btnViewOwner = UI.getButton(null,
                    i18n("View_the_owner_for_this_foster"), 'o',
                    IconManager.getIcon(IconManager.SCREEN_FOSTERBOOK_VIEWOWNER),
                    UI.fp(this, "actionViewOwner"));
            addToolButton(btnViewOwner, true);

            break;
        }
    }

    @Override
    public void tableDoubleClicked() {
        actionEdit();
    }

    @Override
    public void tableClicked() {
    }

    @Override
    public boolean formClosing() {
        return false;
    }

    @Override
    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void actionReturnCreate() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get the movement
        Adoption ad = new Adoption();
        ad.openRecordset("ID = " + id);

        try {
            // Make sure it isn't already returned.
            if (ad.getReturnDate() != null) {
                Dialog.showError(Global.i18n("uimovement",
                        "this_movement_is_already_returned"));
                ad.free();
                ad = null;

                return;
            }

            // Request a date
            Date date = Utils.parseDate(Dialog.getDateInput(Global.i18n(
                            "uimovement", "enter_the_return_date"),
                        Global.i18n("uimovement", "return_movement")));

            // Return the movement
            ad.setReturnDate(date);
            ad.save(Global.currentUserName);

            if (AuditTrail.enabled()) {
                AuditTrail.changed("movement",
                    ad.getAdoptionNumber() + " " +
                    LookupCache.getAnimalByID(ad.getAnimalID()).getShelterCode() +
                    " " +
                    LookupCache.getAnimalByID(ad.getAnimalID()).getAnimalName());
            }

            // Prompt for a new movement
            MovementEdit f = new MovementEdit(this);
            f.openForNew(ad.getAnimalID().intValue(), 0);
            f.setMoveType(Adoption.MOVETYPE_ADOPTION, Utils.formatDate(date),
                true);
            f.setOriginalRetailerMovement(ad.getID().intValue());
            Global.mainForm.addChild(f);

            // Update the onscreen value
            tabledata[getTable().getSelectedRow()][6] = Utils.formatTableDate(date);
            getTable().repaint();

            // Free up
            ad.free();
            ad = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionViewOwner() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get the movement
        Adoption ad = new Adoption();
        ad.openRecordset("ID = " + id);

        try {
            // Open the Edit Owner form
            OwnerEdit eo = new OwnerEdit();
            eo.openForEdit(ad.getOwnerID().intValue());
            Global.mainForm.addChild(eo);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionViewAnimal() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get the movement
        Adoption ad = new Adoption();
        ad.openRecordset("ID = " + id);

        try {
            // Open the Edit Animal form
            AnimalEdit ea = new AnimalEdit();
            Animal animal = LookupCache.getAnimalByID(ad.getAnimalID());
            ea.openForEdit(animal);
            Global.mainForm.addChild(ea);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get the movement
        Adoption ad = new Adoption();
        ad.openRecordset("ID = " + id);

        // Open the Edit Movement record
        MovementEdit em = new MovementEdit(this);
        em.openForEdit(ad, 0);
        Global.mainForm.addChild(em);
    }

    public void actionNew() {
        MovementEdit em = new MovementEdit(this);
        em.openForNew(0, 0);

        switch (mode) {
        case MODE_RESERVATION:

            // em.setMoveType(Adoption.MOVETYPE_RESERVATION);
            break;

        case MODE_FOSTERS:
            em.setMoveType(Adoption.MOVETYPE_FOSTER);

            break;

        case MODE_RETAILERS:
            em.setMoveType(Adoption.MOVETYPE_RETAILER);

            break;
        }

        Global.mainForm.addChild(em);
    }

    public void actionRefresh() {
        updateList();
    }

    public void actionPrint() {
        SortableTableModel tablemodel = (SortableTableModel) table.getModel();
        new MovementResults(tablemodel.getData(), tablemodel.getRowCount(),
            columnheaders, getTitle());
    }

    @Override
    public void updateMedia() {
    }

    private class MovementResults extends Report {
        private String[][] searchResults = null;
        private String[] headers = null;
        private String title = null;
        private int max = 0;

        public MovementResults(String[][] searchResults, int max,
            String[] headers, String title) {
            this.max = max;
            this.searchResults = searchResults;
            this.headers = headers;
            this.title = title;

            if (searchResults == null) {
                return;
            }

            this.start();
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public void generateReport() {
            tableNew();

            tableAddRow();

            for (int i = 0; i < headers.length; i++) {
                tableAddCell(bold(headers[i]));
            }

            tableFinishRow();

            setStatusBarMax(max);

            for (int i = 0; i < max; i++) {
                tableAddRow();

                // For reporting purposes, substitute the report animal name
                // for the normal animal name (last extra column for column 0).
                tableAddCell(searchResults[i][headers.length + 1]);

                for (int z = 1; z < headers.length; z++) {
                    tableAddCell(searchResults[i][z]);
                }

                tableFinishRow();
                incrementStatusBar();
            }

            tableFinish();
            addTable();

            addParagraph(Global.i18n("reports", "Total__", Integer.toString(max)));
        }
    }
}
