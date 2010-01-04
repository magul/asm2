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
package net.sourceforge.sheltermanager.asm.ui.owner;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.FoundAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.LostAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.waitinglist.WaitingListEdit;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Vector;


/**
 * Viewing of owner links to other records. These are:
 *
 * 1. Original Owner 2. Brought in By 3. Waiting List Contact 4. Lost Animal
 * Contact 5. Found Animal Contact
 *
 * @author Robin Rawson-Tetley
 */
public class LinkSelector extends ASMSelector {
    private static final int ORIGINAL_OWNER = 0;
    private static final int BROUGHT_IN_BY = 1;
    private static final int WAITING_LIST = 2;
    private static final int LOST_ANIMAL = 3;
    private static final int FOUND_ANIMAL = 4;
    private static final int OWNERS_VET = 5;
    private static final int CURRENT_VET = 6;
    private int ownerID = 0;
    private boolean someData = false;
    private UI.Button btnView;

    public LinkSelector() {
        init("uiowner", false);
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecChangeLostAnimals() ||
                !Global.currentUserObject.getSecChangeFoundAnimals() ||
                !Global.currentUserObject.getSecViewOwner() ||
                !Global.currentUserObject.getSecViewAnimal() ||
                !Global.currentUserObject.getSecViewWaitingList()) {
            btnView.setEnabled(false);
            disableDoubleClick = true;
        }
    }

    public void setLink(int ownerID, int linkType) {
        this.ownerID = ownerID;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());
        v.add(btnView);

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    /**
     * Refreshes the list with the current set of owner donations for the owner.
     */
    public void updateList() {
        try {
            Animal ao = new Animal();
            Animal ab = new Animal();
            Animal av = new Animal();
            Animal ac = new Animal();
            AnimalWaitingList awl = new AnimalWaitingList();
            AnimalLost al = new AnimalLost();
            AnimalFound af = new AnimalFound();

            // Get data
            ao.openRecordset("OriginalOwnerID = " + ownerID);
            ab.openRecordset("BroughtInByOwnerID = " + ownerID);
            av.openRecordset("OwnersVetID = " + ownerID);
            ac.openRecordset("CurrentVetID = " + ownerID);
            awl.openRecordset("OwnerID = " + ownerID);
            al.openRecordset("OwnerID = " + ownerID);
            af.openRecordset("OwnerID = " + ownerID);

            int rows = (int) (ao.getRecordCount() + ab.getRecordCount() +
                awl.getRecordCount() + al.getRecordCount() +
                af.getRecordCount() + ac.getRecordCount() +
                av.getRecordCount());
            someData = rows > 0;

            // Build array
            String[][] data = new String[rows][6];

            // Create an array of headers for the table
            String[] columnheaders = { i18n("Link_Type"), i18n("Date"), "", "" };

            // Build the data
            int i = 0;

            // Original Owners
            while (!ao.getEOF()) {
                data[i][0] = i18n("Original_Owner");
                data[i][1] = Utils.formatTableDate(ao.getDateBroughtIn());
                data[i][2] = ao.getShelterCode();
                data[i][3] = ao.getAnimalName();
                data[i][4] = Integer.toString(ORIGINAL_OWNER);
                data[i][5] = ao.getID().toString();
                i++;
                ao.moveNext();
            }

            ao.free();
            ao = null;

            // Brought In By
            while (!ab.getEOF()) {
                data[i][0] = i18n("Brought_Animal_In");
                data[i][1] = Utils.formatTableDate(ab.getDateBroughtIn());
                data[i][2] = ab.getShelterCode();
                data[i][3] = ab.getAnimalName();
                data[i][4] = Integer.toString(BROUGHT_IN_BY);
                data[i][5] = ab.getID().toString();
                i++;
                ab.moveNext();
            }

            ab.free();
            ab = null;

            // Owners Vets
            while (!av.getEOF()) {
                data[i][0] = i18n("Owners_Vet");
                data[i][1] = Utils.formatTableDate(av.getDateBroughtIn());
                data[i][2] = av.getShelterCode();
                data[i][3] = av.getAnimalName();
                data[i][4] = Integer.toString(OWNERS_VET);
                data[i][5] = av.getID().toString();
                i++;
                av.moveNext();
            }

            av.free();
            av = null;

            // Current Vets
            while (!ac.getEOF()) {
                data[i][0] = i18n("Current_Vet");
                data[i][1] = Utils.formatTableDate(ac.getDateBroughtIn());
                data[i][2] = ac.getShelterCode();
                data[i][3] = ac.getAnimalName();
                data[i][4] = Integer.toString(CURRENT_VET);
                data[i][5] = ac.getID().toString();
                i++;
                ac.moveNext();
            }

            ac.free();
            ac = null;

            // Waiting List
            while (!awl.getEOF()) {
                data[i][0] = i18n("Waiting_List_Contact");
                data[i][1] = Utils.formatTableDate(awl.getDatePutOnList());
                data[i][2] = awl.getSpeciesName();
                data[i][3] = awl.getAnimalDescription();
                data[i][4] = Integer.toString(WAITING_LIST);
                data[i][5] = awl.getID().toString();
                i++;
                awl.moveNext();
            }

            awl.free();
            awl = null;

            // Lost Animal
            while (!al.getEOF()) {
                data[i][0] = i18n("Lost_Animal_Contact");
                data[i][1] = Utils.formatTableDate(al.getDateLost());
                data[i][2] = al.getSpeciesName();
                data[i][3] = al.getDistFeat();
                data[i][4] = Integer.toString(LOST_ANIMAL);
                data[i][5] = al.getID().toString();
                i++;
                al.moveNext();
            }

            al.free();
            al = null;

            // Found Animal
            while (!af.getEOF()) {
                data[i][0] = i18n("Found_Animal_Contact");
                data[i][1] = Utils.formatTableDate(af.getDateFound());
                data[i][2] = af.getSpeciesName();
                data[i][3] = af.getDistFeat();
                data[i][4] = Integer.toString(FOUND_ANIMAL);
                data[i][5] = af.getID().toString();
                i++;
                af.moveNext();
            }

            af.free();
            af = null;

            setTableData(columnheaders, data, i, 5);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public boolean hasData() {
        return someData;
    }

    public void addToolButtons() {
        btnView = UI.getButton(null, i18n("view_this_link"), 'v',
                IconManager.getIcon(IconManager.SCREEN_OWNERLINKS_VIEW),
                UI.fp(this, "actionView"));
        addToolButton(btnView, true);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionView();
    }

    public void actionView() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        int linktype = Integer.parseInt((String) getTable().getModel()
                                                     .getValueAt(getTable()
                                                                     .getSelectedRow(),
                    4));

        try {
            switch (linktype) {
            case ORIGINAL_OWNER:
            case BROUGHT_IN_BY:
            case CURRENT_VET:
            case OWNERS_VET:

                try {
                    AnimalEdit ea = new AnimalEdit();
                    Animal a = LookupCache.getAnimalByID(new Integer(id));
                    ea.openForEdit(a);
                    Global.mainForm.addChild(ea);
                    ea = null;
                    a = null;
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }

                break;

            case WAITING_LIST:

                try {
                    WaitingListEdit ewl = new WaitingListEdit(null);
                    AnimalWaitingList awl = new AnimalWaitingList();
                    awl.openRecordset("ID = " + id);
                    ewl.openForEdit(awl);
                    Global.mainForm.addChild(ewl);
                    ewl = null;
                    awl = null;
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }

                break;

            case LOST_ANIMAL:

                try {
                    LostAnimalEdit el = new LostAnimalEdit();
                    AnimalLost al = new AnimalLost();
                    al.openRecordset("ID = " + id);
                    el.openForEdit(al);
                    Global.mainForm.addChild(el);
                    el = null;
                    al = null;
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }

                break;

            case FOUND_ANIMAL:

                try {
                    FoundAnimalEdit ef = new FoundAnimalEdit();
                    AnimalFound af = new AnimalFound();
                    af.openRecordset("ID = " + id);
                    ef.openForEdit(af);
                    Global.mainForm.addChild(ef);
                    ef = null;
                    af = null;
                } catch (Exception e) {
                    Global.logException(e, getClass());
                }

                break;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
