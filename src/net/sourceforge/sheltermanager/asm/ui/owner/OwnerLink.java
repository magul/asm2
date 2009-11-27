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
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;


/**
 * This class contains all code for embedding an owner record in another screen.
 *
 * @author Robin Rawson-Tetley
 */
public class OwnerLink extends UI.Panel implements SearchListener {
    public final static int MODE_FULL = 0;
    public final static int MODE_ONELINE = 1;
    public final static int FILTER_NONE = 0;
    public final static int FILTER_ADOPTERS = 1;
    public final static int FILTER_FOSTERERS = 2;
    public final static int FILTER_SHELTERS = 3;
    public final static int FILTER_RETAILERS = 4;
    public final static int FILTER_HOMECHECKERS = 5;
    public final static int FILTER_VETS = 6;
    private int ownerID = 0;
    private OwnerLinkListener parent = null;
    private UI.Button btnClear;
    private UI.Button btnFind;
    private UI.Button btnNew;
    private UI.Button btnOpen;
    private UI.Panel pnlAddress;
    private UI.Panel pnlDetail;
    private UI.Panel pnlName;
    private UI.Panel pnlPostcode;
    private UI.ToolBar tlb;
    private UI.TextArea txtAddress;
    private UI.TextField txtHomeTelephone;
    private UI.TextField txtMobileTelephone;
    private UI.TextField txtName;
    private UI.TextField txtPostcode;
    private int filter = FILTER_NONE;
    private int mode = MODE_FULL;
    private String id = "LINK";
    private String title = "";

    public OwnerLink() {
        this(MODE_FULL, 0, "LINK");
    }

    public OwnerLink(String id) {
        this(MODE_FULL, 0, id);
    }

    public OwnerLink(int mode, int filter, String id) {
        super(true);
        this.mode = mode;
        this.filter = filter;
        this.id = id;
        initComponents();
    }

    public void setParent(OwnerLinkListener parent) {
        this.parent = parent;
    }

    public void dispose() {
        parent = null;
    }

    public String i18n(String key) {
        return Global.i18n("uiowner", key);
    }

    /**
     * Loads in the owner record from its ID
     *
     * @param ID
     *            The ID of the owner record
     */
    public void loadFromID(int ID) {
        if (ID == 0) {
            return;
        }

        ownerID = ID;

        // Grab the record
        Owner owner = new Owner();
        owner.openRecordset("ID = " + ID);
        // Display it
        ownerSelected(owner);
    }

    public int getID() {
        return ownerID;
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
        btnNew.setEnabled(b);
        btnOpen.setEnabled(b);
        btnClear.setEnabled(b);
        btnFind.setEnabled(b);
    }

    public void receiveData(Owner owner) {
        // Callback from EditOwner when we spawned
        // a create
        ownerSelected(owner);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        super.setTitle(title);
        this.title = title;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public void initComponents() {
        if (mode == MODE_FULL) {
            UI.Panel p = UI.getPanel(UI.getBorderLayout());
            UI.Panel name = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
            UI.Panel address = UI.getPanel(UI.getGridLayout(2,
                        new int[] { 30, 70 }));
            UI.Panel postcode = UI.getPanel(UI.getGridLayout(2,
                        new int[] { 30, 70 }));

            txtName = (UI.TextField) UI.addComponent(name, i18n("Name:"),
                    UI.getTextField());
            txtName.setEnabled(false);

            txtAddress = (UI.TextArea) UI.addComponent(address,
                    i18n("Address:"), UI.getTextArea());
            txtAddress.setEnabled(false);

            txtPostcode = (UI.TextField) UI.addComponent(postcode,
                    i18n("Postcode:"), UI.getTextField());
            txtPostcode.setEnabled(false);
            txtHomeTelephone = (UI.TextField) UI.addComponent(postcode,
                    i18n("Telephone:"), UI.getTextField());
            txtHomeTelephone.setEnabled(false);
            txtMobileTelephone = (UI.TextField) UI.addComponent(postcode,
                    i18n("Mobile:"), UI.getTextField());
            txtMobileTelephone.setEnabled(false);

            UI.ToolBar t = UI.getToolBar(true);
            btnNew = (UI.Button) t.add(UI.getButton(null,
                        i18n("create_a_new_owner"), ' ',
                        IconManager.getIcon(IconManager.SCREEN_EMBEDOWNER_NEW),
                        UI.fp(this, "actionNew")));
            btnNew.setEnabled(Global.currentUserObject.getSecAddOwner());

            btnOpen = (UI.Button) t.add(UI.getButton(null,
                        i18n("edit_this_owner"), ' ',
                        IconManager.getIcon(IconManager.SCREEN_EMBEDOWNER_OPEN),
                        UI.fp(this, "actionOpen")));
            btnOpen.setEnabled(Global.currentUserObject.getSecChangeOwner());

            btnFind = (UI.Button) t.add(UI.getButton(null,
                        i18n("use_an_existing_owner"), ' ',
                        IconManager.getIcon(
                            IconManager.SCREEN_EMBEDOWNER_SEARCH),
                        UI.fp(this, "actionSearch")));
            btnFind.setEnabled(Global.currentUserObject.getSecViewOwner());

            btnClear = (UI.Button) t.add(UI.getButton(null, i18n("clear"), ' ',
                        IconManager.getIcon(IconManager.SCREEN_EMBEDOWNER_CLEAR),
                        UI.fp(this, "actionClear")));

            p.add(name, UI.BorderLayout.NORTH);
            p.add(address, UI.BorderLayout.CENTER);
            p.add(postcode, UI.BorderLayout.SOUTH);

            setLayout(UI.getBorderLayout());
            add(p, UI.BorderLayout.CENTER);
            add(t, UI.BorderLayout.EAST);
        } else if (mode == MODE_ONELINE) {
            setLayout(UI.getBorderLayout());

            txtName = UI.getTextField();
            txtName.setEnabled(false);
            add(txtName, UI.BorderLayout.CENTER);

            //UI.Panel t = UI.getPanel(UI.getFlowLayout(UI.ALIGN_LEFT, true), true);
            UI.ToolBar t = UI.getToolBar();

            btnNew = (UI.Button) t.add(UI.getButton(null,
                        i18n("create_a_new_owner"), ' ',
                        IconManager.getIcon(
                            IconManager.SCREEN_EMBEDOWNERSMALL_NEW),
                        UI.fp(this, "actionNew")));
            btnNew.setEnabled(Global.currentUserObject.getSecAddOwner());

            btnOpen = (UI.Button) t.add(UI.getButton(null,
                        i18n("edit_this_owner"), ' ',
                        IconManager.getIcon(
                            IconManager.SCREEN_EMBEDOWNERSMALL_OPEN),
                        UI.fp(this, "actionOpen")));
            btnOpen.setEnabled(Global.currentUserObject.getSecChangeOwner());

            btnFind = (UI.Button) t.add(UI.getButton(null,
                        i18n("use_an_existing_owner"), ' ',
                        IconManager.getIcon(
                            IconManager.SCREEN_EMBEDOWNERSMALL_SEARCH),
                        UI.fp(this, "actionSearch")));
            btnFind.setEnabled(Global.currentUserObject.getSecViewOwner());

            btnClear = (UI.Button) t.add(UI.getButton(null, i18n("clear"), ' ',
                        IconManager.getIcon(
                            IconManager.SCREEN_EMBEDOWNERSMALL_CLEAR),
                        UI.fp(this, "actionClear")));

            add(t, UI.BorderLayout.EAST);
        }
    }

    public void actionClear() {
        ownerID = 0;

        if (mode == MODE_FULL) {
            txtName.setText("");
            txtAddress.setText("");
            txtHomeTelephone.setText("");
            txtMobileTelephone.setText("");
            txtPostcode.setText("");
        } else if (mode == MODE_ONELINE) {
            txtName.setText("");
        }

        if (parent != null) {
            parent.ownerChanged(ownerID, id);
        }
    }

    public void actionSearch() {
        OwnerFind fo = new OwnerFind(this, true, false);

        // Do we have a filter?
        switch (filter) {
        case FILTER_ADOPTERS:
            fo.cboFilter.setSelectedIndex(1);
            fo.cboFilter.setEnabled(false);

            break;

        case FILTER_FOSTERERS:
            fo.cboFilter.setSelectedIndex(13);
            fo.cboFilter.setEnabled(false);

            break;

        case FILTER_RETAILERS:
            fo.cboFilter.setSelectedIndex(12);
            fo.cboFilter.setEnabled(false);

            break;

        case FILTER_SHELTERS:
            fo.cboFilter.setSelectedIndex(9);
            fo.cboFilter.setEnabled(false);

            break;

        case FILTER_HOMECHECKERS:
            fo.cboFilter.setSelectedIndex(7);
            fo.cboFilter.setEnabled(false);

            break;

        case FILTER_VETS:
            fo.cboFilter.setSelectedIndex(14);
            fo.cboFilter.setEnabled(false);

            break;
        }

        Global.mainForm.addChild(fo);
    }

    public void actionOpen() {
        if (ownerID == 0) {
            return;
        }

        OwnerEdit eo = new OwnerEdit();
        eo.openForEdit(ownerID, this);
        Global.mainForm.addChild(eo);
    }

    public void actionNew() {
        OwnerEdit eo = new OwnerEdit();
        eo.openForNew(this);
        Global.mainForm.addChild(eo);
    }

    public void animalSelected(Animal theanimal) {
    }

    public void foundAnimalSelected(AnimalFound thefoundanimal) {
    }

    public void lostAnimalSelected(AnimalLost thelostanimal) {
    }

    public void ownerSelected(Owner theowner) {
        try {
            ownerID = theowner.getID().intValue();

            // Load the data
            if ((mode == MODE_FULL) || (mode == MODE_ONELINE)) {
                txtName.setText(Utils.nullToEmptyString(theowner.getOwnerName()));
            }

            if (mode == MODE_FULL) {
                txtAddress.setText(Utils.nullToEmptyString(
                        theowner.getOwnerAddress()));
                txtPostcode.setText(Utils.nullToEmptyString(
                        theowner.getOwnerPostcode()));
                txtHomeTelephone.setText(Utils.nullToEmptyString(
                        theowner.getHomeTelephone()));
                txtMobileTelephone.setText(Utils.nullToEmptyString(
                        theowner.getMobileTelephone()));
            }

            // Fire updated event
            if (parent != null) {
                parent.ownerChanged(ownerID, id);
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void retailerSelected(Owner theowner) {
    }
}
