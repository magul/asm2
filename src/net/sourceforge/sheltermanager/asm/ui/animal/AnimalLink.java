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
import net.sourceforge.sheltermanager.asm.bo.AnimalFound;
import net.sourceforge.sheltermanager.asm.bo.AnimalLost;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;


/**
 * This class contains all code for embedding an animal record in another screen.
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class AnimalLink extends UI.Panel implements SearchListener {
    private int animalID = 0;
    private AnimalLinkListener parent = null;
    private UI.Button btnClear;
    private UI.Button btnFind;
    private UI.Button btnOpen;
    private UI.Label lblName;

    public AnimalLink() {
        super(true);
        initComponents();
    }

    public AnimalLink(AnimalLinkListener parent) {
        super(true);
        this.parent = parent;
        initComponents();
    }

    public void dispose() {
    }

    public String i18n(String key) {
        return Global.i18n("uianimal", key);
    }

    public void loadFromID(int ID) {
        if (ID == 0) {
            return;
        }

        animalID = ID;

        // Grab the record
        Animal a = LookupCache.getAnimalByID(ID);

        // Display it
        animalSelected(a);
    }

    public int getID() {
        return animalID;
    }

    public void setID(int newID) {
        loadFromID(newID);
    }

    public void setID(Integer newID) {
        if (newID != null) {
            loadFromID(newID.intValue());
        }
    }

    public void setEnabled(boolean b) {
        btnOpen.setEnabled(b);
        btnClear.setEnabled(b);
        btnFind.setEnabled(b);
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        lblName = UI.getTitleLabel("");
        add(lblName, UI.BorderLayout.CENTER);

        UI.ToolBar t = UI.getToolBar();
        btnOpen = (UI.Button) t.add(UI.getButton(null,
                    i18n("edit_this_animal"), ' ',
                    IconManager.getIcon(
                        IconManager.SCREEN_EMBEDANIMALSMALL_OPEN),
                    UI.fp(this, "actionOpen")));
        btnOpen.setEnabled(Global.currentUserObject.getSecChangeOwner());

        btnFind = (UI.Button) t.add(UI.getButton(null,
                    i18n("Select_an_animal"), ' ',
                    IconManager.getIcon(
                        IconManager.SCREEN_EMBEDANIMALSMALL_SEARCH),
                    UI.fp(this, "actionSearch")));
        btnFind.setEnabled(Global.currentUserObject.getSecViewOwner());

        btnClear = (UI.Button) t.add(UI.getButton(null, i18n("Clear"), ' ',
                    IconManager.getIcon(
                        IconManager.SCREEN_EMBEDANIMALSMALL_CLEAR),
                    UI.fp(this, "actionClear")));

        add(t, UI.isLTR() ? UI.BorderLayout.EAST : UI.BorderLayout.WEST);
    }

    public void actionClear() {
        lblName.setText("");
        animalID = 0;

        if (parent != null) {
            parent.animalChanged(0);
        }
    }

    public void actionSearch() {
        if (Configuration.getBoolean("AdvancedFindAnimal")) {
            Global.mainForm.addChild(new AnimalFind(this));
        } else {
            Global.mainForm.addChild(new AnimalFindText(this));
        }
    }

    public void actionOpen() {
        if (animalID == 0) {
            return;
        }

        AnimalEdit ea = new AnimalEdit();
        ea.openForEdit(animalID);
        Global.mainForm.addChild(ea);
    }

    public void animalSelected(Animal theanimal) {
        try {
            animalID = theanimal.getID().intValue();
            lblName.setText(theanimal.getCode() + " - " +
                theanimal.getAnimalName());

            if (parent != null) {
                parent.animalChanged(animalID);
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void foundAnimalSelected(AnimalFound thefoundanimal) {
    }

    public void lostAnimalSelected(AnimalLost thelostanimal) {
    }

    public void ownerSelected(Owner theowner) {
    }

    public void retailerSelected(Owner theowner) {
    }
}
