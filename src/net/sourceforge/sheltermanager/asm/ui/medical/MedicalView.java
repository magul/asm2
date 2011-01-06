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
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 * Container form for ViewMedicals showing all active.
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class MedicalView extends ASMForm {
    private MedicalSelector medical = new MedicalSelector();

    /** Creates new form ViewNames */
    public MedicalView() {
        init(Global.i18n("uimedical", "Medical_Book"),
            IconManager.getIcon(IconManager.SCREEN_MEDICALBOOK), "uimedical");
        medical.updateList();
    }

    public Object getDefaultFocusedComponent() {
        return medical;
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> v = new Vector<Object>();
        v.add(medical);

        return v;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public void setSecurity() {
    }

    public void dispose() {
        medical = null;
        super.dispose();
    }

    public void initComponents() {
        add(medical, UI.BorderLayout.CENTER);
    }
}
