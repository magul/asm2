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
package net.sourceforge.sheltermanager.asm.ui.medical;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;


/**
 * Handles viewing of medical profile database. This is an embeddable component
 * that can be used on an animal, or as part of a standalone form to show all
 * currently active medicals for all animals.
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class MedicalSelector extends UI.Panel {
    @SuppressWarnings("unused")
    private int animalID = 0;
    private UI.TabbedPane tabTabs;
    MedicalRegimeSelector regimeview = new MedicalRegimeSelector(this);
    MedicalRegimeTreatmentSelector regimetview = new MedicalRegimeTreatmentSelector(this);
    MedicalTreatmentSelector tview = new MedicalTreatmentSelector(this);

    /** Creates new form ViewNames */
    public MedicalSelector() {
        initComponents();

        try {
            tabTabs.setTabPlacement(Global.TABALIGN);
        } catch (Exception e) {
        }
    }

    public void updateList() {
        regimeview.updateList();
        tview.updateList();
    }

    public void setLink(int animalID) {
        this.animalID = animalID;
        regimeview.setLink(animalID, 0);
        tview.setLink(animalID, 0);
    }

    public boolean getHasMedical() {
        return regimeview.hasData();
    }

    private void initComponents() {
        tabTabs = UI.getTabbedPane();
        setLayout(UI.getBorderLayout());

        UI.Panel pnlRegimes = UI.getPanel(UI.getGridLayout(1));
        pnlRegimes.add(regimeview);
        pnlRegimes.add(regimetview);

        tabTabs.addTab(Global.i18n("uimedical", "Regime_View"), pnlRegimes);
        tabTabs.addTab(Global.i18n("uimedical", "Treatment_View"), tview);
        add(tabTabs, UI.BorderLayout.CENTER);
    }
}
