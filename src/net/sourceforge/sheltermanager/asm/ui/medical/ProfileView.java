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

import net.sourceforge.sheltermanager.asm.bo.MedicalProfile;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * Handles viewing of medical profile database
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class ProfileView extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnNew;
    private UI.Button btnView;

    /** Creates new form ViewNames */
    public ProfileView() {
        init(Global.i18n("uimedical", "Medical_Profiles"),
            IconManager.getIcon(IconManager.SCREEN_VIEWPROFILES), "uimedical");
        updateList();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> v = new Vector<Object>();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnNew;
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

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    /**
     * Refreshes the list with the current set of profiles on the system.
     */
    public void updateList() {
        MedicalProfile mp = new MedicalProfile();

        // Get the data
        mp.openRecordset("ID > 0 ORDER BY TreatmentName");

        // Create an array to hold the results for the table
        String[][] datar = new String[(int) mp.getRecordCount()][7];

        // Create an array of headers for the table
        String[] columnheaders = {
                i18n("Profile_Name"), i18n("Treatment_Name"), i18n("Dosage"),
                i18n("Frequency"), i18n("Finishes_After"), i18n("Comments")
            };

        // Build the data
        int i = 0;

        try {
            while (!mp.getEOF()) {
                datar[i][0] = mp.getProfileName();
                datar[i][1] = mp.getTreatmentName();
                datar[i][2] = mp.getDosage();
                datar[i][3] = mp.getNamedFrequency();
                datar[i][4] = mp.getNamedNumberOfTreatments();
                datar[i][5] = mp.getComments();
                datar[i][6] = mp.getID().toString();

                i++;
                mp.moveNext();
            }

            mp.free();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, datar, i, 6);
    }

    public void addToolButtons() {
        btnNew = UI.getButton(null, i18n("Add_a_new_profile"), 'n',
                IconManager.getIcon(IconManager.SCREEN_VIEWPROFILES_NEW),
                UI.fp(this, "actionNew"));
        addToolButton(btnNew, false);

        btnView = UI.getButton(null, i18n("Edit_the_highlighted_profile"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWPROFILES_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, i18n("Delete_the_highlighted_profile"),
                'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWPROFILES_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionDelete() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        // Verify that this profile hasn't been used on a record.
        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset(
                "SELECT ID FROM animalmedical WHERE MedicalProfileID = " + id,
                "animalmedical");

            if (!rs.getEOF()) {
                Dialog.showError(Global.i18n("bo",
                        "This_medical_profile_is_currently_in_use_and_cannot_be_deleted"));
                rs.free();
                rs = null;

                return;
            }

            rs.free();
            rs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        if (Dialog.showYesNoWarning(UI.messageDeleteConfirm(),
                    UI.messageReallyDelete())) {
            try {
                String sql = "DELETE FROM medicalprofile WHERE ID = " + id;
                DBConnection.executeAction(sql);
                updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        ProfileEdit ep = new ProfileEdit(this);
        MedicalProfile mp = new MedicalProfile();
        mp.openRecordset("ID = " + id);
        ep.openForEdit(mp);
        Global.mainForm.addChild(ep);
        mp = null;
    }

    public void actionNew() {
        ProfileEdit ep = new ProfileEdit(this);
        ep.openForNew();
        Global.mainForm.addChild(ep);
    }
}
