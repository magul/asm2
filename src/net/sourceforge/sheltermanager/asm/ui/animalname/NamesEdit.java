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
package net.sourceforge.sheltermanager.asm.ui.animalname;

import net.sourceforge.sheltermanager.asm.bo.AnimalName;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 *
 * @author robin
 */
public class NamesEdit extends ASMForm {
    private NamesView parent = null;
    private AnimalName an = null;
    private UI.Button btnCancel;
    private UI.TextField txtName;
    private UI.Button btnOk;
    private UI.ComboBox cboSex;

    public NamesEdit(NamesView parent) {
        this.parent = parent;
        init(Global.i18n("uianimalname", "Edit_Name"),
            IconManager.getIcon(IconManager.SCREEN_EDITNAMES), "uianimalname");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtName);
        ctl.add(cboSex);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtName;
    }

    public void dispose() {
        an.free();
        parent = null;
        an = null;
        super.dispose();
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public void loadData() {
    }

    public void openForEdit(AnimalName an) {
        this.an = an;

        try {
            this.txtName.setText(an.getName());
            this.cboSex.setSelectedIndex(an.getSex().intValue());
        } catch (Exception e) {
        }
    }

    public void openForNew() {
        try {
            this.an = new AnimalName();
            an.openRecordset("ID = 0");
            an.addNew();
        } catch (Exception e) {
            Dialog.showError(i18n("Unable_to_create_new_animal_name:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(2));

        txtName = (UI.TextField) UI.addComponent(p, i18n("Name:_"),
                UI.getTextField());

        String[] sexes = {
                Global.i18n("uianimal", "Female"),
                Global.i18n("uianimal", "Male"),
                Global.i18n("uianimal", "Unknown")
            };
        cboSex = (UI.ComboBox) UI.addComponent(p, i18n("Sex:_"),
                UI.getCombo(sexes));

        btnOk = (UI.Button) p.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) p.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        setLayout(UI.getBorderLayout());
        add(p, UI.BorderLayout.CENTER);
    }

    public boolean saveData() {
        // Save values
        try {
            an.setName(txtName.getText());
            an.setSex(new Integer(cboSex.getSelectedIndex()));
            an.save();

            // Update parent
            parent.updateList();

            dispose();

            return true;
        } catch (Exception e) {
            Dialog.showError(i18n("An_error_occurred_saving_the_data:\n") +
                e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }
}
