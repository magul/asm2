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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.TablePrefs;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Date;
import java.util.Vector;


/**
 * Screen for editing reservations without going through animals or owners
 * first.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class NotAdoptionView extends ASMView {
    private UI.Button btnRefresh;
    private UI.Button btnView;

    /** Creates new form NotAdoptionBook */
    public NotAdoptionView() {
        init(Global.i18n("uianimal", "Not_For_Adoption_Book"),
            IconManager.getIcon(IconManager.SCREEN_NOTADOPTIONBOOK), "uianimal");
        updateList();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(btnRefresh);
        ctl.add(btnView);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return btnRefresh;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecViewAnimal()) {
            btnView.setEnabled(false);
        }
    }

    public void updateList() {
        SQLRecordset rs = new SQLRecordset();

        try {
            rs.openRecordset(
                "SELECT animal.ID, animal.AnimalName, animal.ShortCode, " +
                "animal.ShelterCode, animaltype.AnimalType, " +
                "species.SpeciesName, animal.DateBroughtIn, animal.ID " +
                "FROM animal " +
                "INNER JOIN species ON animal.SpeciesID = species.ID " +
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID " +
                "WHERE animal.IsNotAvailableForAdoption = 1", "animal");
        } catch (Exception e) {
            Global.logException(e, getClass());

            return;
        }

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        String[][] tabledata = new String[(int) rs.getRecordCount()][6];

        // Create an array of headers for the accounts (one less than
        // array because 6th col will hold ID
        String[] columnheaders = {
                Global.i18n("uimovement", "Name"),
                Global.i18n("uimovement", "Code"),
                Global.i18n("uimovement", "Type"),
                Global.i18n("uimovement", "Species"),
                Global.i18n("uimovement", "Date")
            };

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!rs.getEOF()) {
                tabledata[i][0] = rs.getField("AnimalName").toString();
                tabledata[i][1] = Global.getShowShortCodes()
                    ? rs.getString("ShortCode") : rs.getString("ShelterCode");
                tabledata[i][2] = rs.getString("AnimalType");
                tabledata[i][3] = rs.getString("SpeciesName");
                tabledata[i][4] = Utils.nullToEmptyString(Utils.formatTableDate(
                            (Date) rs.getField("DateBroughtIn")));
                tabledata[i][5] = rs.getString("ID");
                i++;
                rs.moveNext();
            }
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 5);

        try {
            rs.free();
            rs = null;
        } catch (Exception e) {
        }
    }

    public void addToolButtons() {
        btnRefresh = UI.getButton(null, i18n("Refresh_the_list_of_animals"),
                'r',
                IconManager.getIcon(IconManager.SCREEN_NOTADOPTIONBOOK_REFRESH),
                UI.fp(this, "updateList"));
        addToolButton(btnRefresh, false);

        btnView = UI.getButton(null, i18n("View_the_animal_record"), 'e',
                IconManager.getIcon(IconManager.SCREEN_NOTADOPTIONBOOK_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);
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

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        try {
            // Open the Edit Animal form
            AnimalEdit ea = new AnimalEdit();
            Animal animal = LookupCache.getAnimalByID(new Integer(id));
            ea.openForEdit(animal);
            Global.mainForm.addChild(ea);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
