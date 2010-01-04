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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableItem;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableList;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.ArrayList;
import java.util.Vector;


/**
 * This class allows mass removal of ASM default breeds and
 * species based on groups.
 * @author Robin Rawson-Tetley
 */
public class ConfigureLookups extends ASMForm {
    public UI.Button btnCancel;
    public UI.Button btnOk;
    public SelectableList options;

    public ConfigureLookups() {
        init(Global.i18n("uisystem", "Remove_unwanted_breeds_and_species"),
            IconManager.getIcon(IconManager.SCREEN_CONFIGURELOOKUPS), "uisystem");
    }

    public void dispose() {
        unregisterTabOrder();
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(options);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return options;
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public void setSecurity() {
    }

    public void initComponents() {
        ArrayList l = new ArrayList();

        if (be(220)) {
            l.add(new SelectableItem(i18n("Dogs"), "dogs", true, false));
        }

        if (be(313)) {
            l.add(new SelectableItem(i18n("Cats"), "cats", true, false));
        }

        if (be(356)) {
            l.add(new SelectableItem(i18n("Rabbits"), "rabbits", true, false));
        }

        if (be(378)) {
            l.add(new SelectableItem(i18n("Horses_Ponies"), "horses", true,
                    false));
        }

        if (be(389)) {
            l.add(new SelectableItem(i18n("Small_Furry"), "smallfurry", true,
                    false));
        }

        if (be(391)) {
            l.add(new SelectableItem(i18n("Pigs"), "pigs", true, false));
        }

        if (be(397)) {
            l.add(new SelectableItem(i18n("Amphibians_Reptiles"), "reptiles",
                    true, false));
        }

        if (be(436)) {
            l.add(new SelectableItem(i18n("Birds"), "birds", true, false));
        }

        if (be(441)) {
            l.add(new SelectableItem(i18n("Barnyard"), "barnyard", true, false));
        }

        add(UI.getLabel(UI.ALIGN_LEFT,
                i18n("untick_the_animal_groups_you_do_not_want_and_hit_ok")),
            UI.BorderLayout.NORTH);

        options = new SelectableList(l);
        add(options, UI.BorderLayout.CENTER);

        btnOk = UI.getButton(i18n("Ok"), null, 'o', null,
                UI.fp(this, "actionOk"));
        btnCancel = UI.getButton(i18n("Cancel"), null, 'c', null,
                UI.fp(this, "dispose"));

        UI.Panel p = UI.getPanel(UI.getFlowLayout());
        p.add(btnOk);
        p.add(btnCancel);
        add(p, UI.BorderLayout.SOUTH);
    }

    public void sql(String s) {
        try {
            Global.logDebug("EXECUTE: " + s, "ConfigureLookups.sql");
            DBConnection.executeAction(s);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public boolean be(int id) {
        try {
            return 0 < DBConnection.executeForCount(
                "SELECT COUNT(ID) FROM breed WHERE ID = " + id);
        } catch (Exception e) {
            Global.logException(e, getClass());

            return false;
        }
    }

    /**
     * Returns true if a breed and species range has been used in the
     * animal table
     * @param frombreed breed ID to start from
     * @param tobreed breed ID to finish at
     * @param inspecies IN clause of species
     * @return true if its used
     */
    public boolean isused(int frombreed, int tobreed, String inspecies) {
        try {
            int b = DBConnection.executeForCount(
                    "SELECT COUNT(ID) FROM animal WHERE BreedID >= " +
                    frombreed + " AND BreedID <= " + tobreed);
            int s = DBConnection.executeForCount(
                    "SELECT COUNT(ID) FROM animal WHERE SpeciesID IN ( " +
                    inspecies + " )");

            return ((b > 0) && (s > 0));
        } catch (Exception e) {
            Global.logException(e, getClass());

            return true;
        }
    }

    public boolean formClosing() {
        return false;
    }

    public void actionOk() {
        SelectableItem[] s = options.getSelections();

        for (int i = 0; i < s.length; i++) {
            if (s[i].getValue().equals("dogs") && !s[i].isSelected()) {
                if (!isused(1, 220, "1")) {
                    sql("DELETE FROM breed WHERE ID <= 220");
                    sql("DELETE FROM species WHERE ID = 1");
                }
            }

            if (s[i].getValue().equals("cats") && !s[i].isSelected()) {
                if (!isused(221, 313, "2")) {
                    sql("DELETE FROM breed WHERE ID >= 221 AND ID <= 313");
                    sql("DELETE FROM species WHERE ID = 2");
                }
            }

            if (s[i].getValue().equals("rabbits") && !s[i].isSelected()) {
                if (!isused(314, 356, "7")) {
                    sql("DELETE FROM breed WHERE ID >= 314 AND ID <= 356");
                    sql("DELETE FROM species WHERE ID = 7");
                }
            }

            if (s[i].getValue().equals("horses") && !s[i].isSelected()) {
                if (!isused(357, 378, "23, 27")) {
                    sql("DELETE FROM breed WHERE ID >= 357 AND ID <= 378");
                    sql("DELETE FROM species WHERE ID >= 23 AND ID <= 27");
                }
            }

            if (s[i].getValue().equals("smallfurry") && !s[i].isSelected()) {
                if (!isused(379, 389, "4, 5, 6, 9, 10, 18, 20, 22")) {
                    sql("DELETE FROM breed WHERE ID >= 379 AND ID <= 389");
                    sql(
                        "DELETE FROM species WHERE ID IN ( 4, 5, 6, 9, 10, 18, 20, 22 )");
                }
            }

            if (s[i].getValue().equals("pigs") && !s[i].isSelected()) {
                if (!isused(390, 391, "28")) {
                    sql("DELETE FROM breed WHERE ID >= 390 AND ID <= 391");
                    sql("DELETE FROM species WHERE ID = 28");
                }
            }

            if (s[i].getValue().equals("reptiles") && !s[i].isSelected()) {
                if (!isused(392, 397, "11, 12, 13, 21")) {
                    sql("DELETE FROM breed WHERE ID >= 392 AND ID <= 397");
                    sql("DELETE FROM species WHERE ID IN ( 11, 12, 13, 21 )");
                }
            }

            if (s[i].getValue().equals("birds") && !s[i].isSelected()) {
                if (!isused(398, 436, "3, 8, 15, 17, 19")) {
                    sql("DELETE FROM breed WHERE ID >= 398 AND ID <= 436");
                    sql("DELETE FROM species WHERE ID IN ( 3, 8, 15, 17, 19 )");
                }
            }

            if (s[i].getValue().equals("barnyard") && !s[i].isSelected()) {
                if (!isused(437, 441, "14, 16")) {
                    sql("DELETE FROM breed WHERE ID >= 437 AND ID <= 441");
                    sql("DELETE FROM species WHERE ID IN ( 14, 16 )");
                }
            }
        }

        LookupCache.invalidate();
        LookupCache.fill();
        Dialog.showInformation(i18n("Finished_removing_breeds_and_species"));
        dispose();
    }
}
