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

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 * File Type editing form
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class FileTypeEdit extends ASMForm {
    private FileTypes ft = null;
    private int selRow = 0;
    private UI.TextField txtExtension;
    private UI.SearchTextField txtProgram;
    private UI.Button btnOk;
    private UI.Button btnCancel;

    public FileTypeEdit(FileTypes ft) {
        this.ft = ft;
        selRow = ft.getTable().getSelectedRow();
        init(Global.i18n("uisystem", "Edit_File_Types"),
            IconManager.getIcon(IconManager.SCREEN_FILETYPEEDIT), "uisystem");
    }

    public void openForEdit() {
        loadData();
    }

    public void loadData() {
        txtExtension.setText(ft.getTable().getModel().getValueAt(selRow, 0)
                               .toString());
        txtProgram.setText(ft.getTable().getModel().getValueAt(selRow, 1)
                             .toString());
    }

    public void dispose() {
        ft = null;
        super.dispose();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(txtExtension);
        ctl.add(txtProgram);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtExtension;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(2));

        txtExtension = (UI.TextField) UI.addComponent(p, i18n("Extension"),
                UI.getTextField());
        txtProgram = (UI.SearchTextField) UI.addComponent(p,
                i18n("Application"),
                UI.getSearchTextField(null, true, UI.fp(this, "actionBrowse")));
        btnOk = (UI.Button) p.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) p.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        add(p, UI.BorderLayout.CENTER);
    }

    public boolean saveData() {
        ft.setValue(selRow, txtExtension.getText(), txtProgram.getText());
        dispose();

        return true;
    }

    public void actionBrowse() {
        UI.FileChooser chooser = UI.getFileChooser();

        try {
            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == UI.FileChooser.APPROVE_OPTION) {
                txtProgram.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
