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
package net.sourceforge.sheltermanager.asm.ui.internet;

import net.sourceforge.sheltermanager.asm.bo.Breed;
import net.sourceforge.sheltermanager.asm.db.DBPetFinder;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Vector;


/**
 * This class contains all code for mapping PetFinder's breed list to ASM's
 * internal breed list.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class PetFinderMapBreed extends ASMView {
    private UI.Button btnView;

    /** Creates new form ViewLookup */
    public PetFinderMapBreed() {
        init(Global.i18n("uiinternet", "Map_PetFinder_Breeds"),
            IconManager.getIcon(IconManager.SCREEN_PETFINDERMAPBREED),
            "uiinternet");
        updateList();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(btnView);
        ctl.add(getTable());

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return btnView;
    }

    public void updateList() {
        // Get the data
        Breed breed = new Breed();

        try {
            breed.openRecordset("ID > 0 ORDER BY BreedName");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) breed.getRecordCount()][4];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Breed"), i18n("Description"), i18n("PetFinder")
            };

        // Build the data
        int i = 0;

        try {
            while (!breed.getEOF()) {
                datar[i][0] = breed.getBreedName();
                datar[i][1] = Utils.nullToEmptyString(breed.getBreedDescription());
                datar[i][2] = Utils.nullToEmptyString(breed.getPetFinderBreed());
                datar[i][3] = breed.getID().toString();

                i++;
                breed.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        breed.free();
        breed = null;

        setTableData(columnheaders, datar, i, 3);
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public boolean formClosing() {
        return false;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public void addToolButtons() {
        btnView = UI.getButton(null, Global.i18n("uilookups", "Edit"), 'e',
                IconManager.getIcon(IconManager.SCREEN_PETFINDERMAPSPECIES_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void tableClicked() {
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Get the existing mapping
        String existingMap = (String) getTable().getModel()
                                          .getValueAt(getTable().getSelectedRow(),
                2);

        // Prompt the user to change it
        Vector petFinderBreeds = DBPetFinder.getBreeds();
        Object[] breeds = petFinderBreeds.toArray();
        petFinderBreeds.removeAllElements();
        petFinderBreeds = null;
        existingMap = (String) Dialog.getInput(i18n("Select_petfinder_type"),
                i18n("PetFinder_mapping"), breeds, existingMap);

        // Update the database
        try {
            if (existingMap != null) {
                String sql = "UPDATE breed SET PetFinderBreed = '" +
                    existingMap.replace('\'', '`') + "' WHERE ID = " + id;
                DBConnection.executeAction(sql);
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
        }

        // Update the values in the list
        getTable().getModel()
            .setValueAt(existingMap, getTable().getSelectedRow(), 2);

        // Tell the table this row changed
        getTable().updateSelectedRow();
    }
}
