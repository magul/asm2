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
package net.sourceforge.sheltermanager.asm.ui.lookups;

import net.sourceforge.sheltermanager.asm.bo.Breed;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.db.DBPetFinder;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * Map breeds to species
 * @author Robin Rawson-Tetley
 */
public class BreedSpeciesMapping extends ASMView {
    private UI.Button btnView;
    private UI.Button btnAll;

    public BreedSpeciesMapping() {
        init(Global.i18n("uilookups", "Map_Breeds_to_Species"),
            IconManager.getIcon(IconManager.SCREEN_MAPBREEDSPECIES), "uilookups");
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
                i18n("Breed"), i18n("Description"), i18n("Species")
            };

        // Build the data
        int i = 0;

        try {
            while (!breed.getEOF()) {
                datar[i][0] = breed.getBreedName();
                datar[i][1] = Utils.nullToEmptyString(breed.getBreedDescription());

                String bname = LookupCache.getSpeciesName(breed.getSpeciesID());

                if (bname.equals("")) {
                    bname = i18n("any");
                }

                datar[i][2] = bname;
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
        LookupCache.invalidate();
        LookupCache.fill();

        return false;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public void addToolButtons() {
        btnView = UI.getButton(null, Global.i18n("uilookups", "Edit"), 'e',
                IconManager.getIcon(IconManager.SCREEN_MAPBREEDSPECIES_EDIT),
                UI.fp(this, "actionEdit"));
        btnAll = UI.getButton(null,
                Global.i18n("uilookups", "Map_to_all_species"), 'a',
                IconManager.getIcon(IconManager.SCREEN_MAPBREEDSPECIES_ALL),
                UI.fp(this, "actionAll"));
        addToolButton(btnView, true);
        addToolButton(btnAll, true);
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
        Vector species = new Vector();

        try {
            SQLRecordset sp = LookupCache.getSpeciesLookup();
            sp.moveFirst();

            while (!sp.getEOF()) {
                species.add(sp.getField("SpeciesName"));
                sp.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        existingMap = (String) Dialog.getInput(i18n("Select_species"),
                i18n("Species_mapping"), species.toArray(), existingMap);

        // Update the database
        try {
            if ((existingMap != null) && !existingMap.equals("")) {
                Integer sid = (Integer) LookupCache.getSpeciesID(existingMap);

                if (sid != null) {
                    String sql = "UPDATE breed SET SpeciesID = '" +
                        sid.intValue() + "' WHERE ID = " + id;
                    DBConnection.executeAction(sql);
                    // Update the values in the list
                    getTable().getModel()
                        .setValueAt(existingMap, getTable().getSelectedRow(), 2);
                    // Tell the table this row changed
                    getTable().updateSelectedRow();
                }
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
        }
    }

    public void actionAll() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Clear the existing mapping
        try {
            String sql = "UPDATE breed SET SpeciesID = null WHERE ID = " + id;
            DBConnection.executeAction(sql);
            // Update the values in the list
            getTable().getModel()
                .setValueAt(i18n("any"), getTable().getSelectedRow(), 2);
            // Tell the table this row changed
            getTable().updateSelectedRow();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
        }
    }
}
