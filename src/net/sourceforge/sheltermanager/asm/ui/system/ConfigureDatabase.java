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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 * Configuration of database parameters
 * @author Robin Rawson-Tetley
 */
public class ConfigureDatabase extends ASMForm {
    private UI.Spinner spnRecordSearchLimit;
    private UI.ComboBox cboIncomingMediaScaling;
    private UI.Spinner spnMaxMediaFileSize;
    private UI.CheckBox chkAllowDBUpdates;
    private UI.Button btnOk;
    private UI.Button btnCancel;

    public ConfigureDatabase() {
        init(Global.i18n("uisystem", "configure_database"),
            IconManager.getIcon(IconManager.SCREEN_CONFIGUREDATABASE),
            "uisystem");
        loadData();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(spnRecordSearchLimit);
        ctl.add(cboIncomingMediaScaling);
        ctl.add(spnMaxMediaFileSize);
        ctl.add(chkAllowDBUpdates);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return spnRecordSearchLimit;
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
        try {
            spnRecordSearchLimit.setValue(new Integer(Configuration.getString(
                        "RecordSearchLimit", "100")));
            cboIncomingMediaScaling.setSelectedItem(Configuration.getString(
                    "IncomingMediaScaling", "320x200"));
            spnMaxMediaFileSize.setValue(new Integer(Configuration.getString(
                        "MaxMediaFileSize", "1000")));
            chkAllowDBUpdates.setSelected(Configuration.getString(
                    "AllowDBUpdates", "Yes").equalsIgnoreCase("Yes"));
        } catch (Exception e) {
            Global.logException(e, ConfigureDatabase.class);
        }
    }

    public boolean saveData() {
        try {
            Configuration.setEntry("RecordSearchLimit",
                spnRecordSearchLimit.getValue().toString());
            Configuration.setEntry("IncomingMediaScaling",
                cboIncomingMediaScaling.getSelectedItem().toString());
            Configuration.setEntry("MaxMediaFileSize",
                spnMaxMediaFileSize.getValue().toString());
            Configuration.setEntry("AllowDBUpdates",
                (chkAllowDBUpdates.isSelected() ? "Yes" : "No"));
            dispose();

            return true;
        } catch (Exception e) {
            Global.logException(e, ConfigureDatabase.class);
        }

        return false;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(2));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());
        spnRecordSearchLimit = (UI.Spinner) UI.addComponent(p,
                i18n("record_search_limit"),
                UI.getSpinner(0, 1000, i18n("record_search_limit_tooltip"), null));
        cboIncomingMediaScaling = (UI.ComboBox) UI.addComponent(p,
                i18n("incoming_media_scaling"),
                UI.getCombo(i18n("incoming_media_scaling_tooltip"),
                    new String[] { "None", "320x200", "640x480", "800x600" }));
        cboIncomingMediaScaling.setEditable(true);
        spnMaxMediaFileSize = (UI.Spinner) UI.addComponent(p,
                i18n("max_media_file_size"),
                UI.getSpinner(0, 512000, i18n("max_media_file_size_tooltip"),
                    null));
        chkAllowDBUpdates = (UI.CheckBox) UI.addComponent(p,
                i18n("allow_db_updates"),
                UI.getCheckBox("", i18n("allow_db_updates_tooltip")));

        btnOk = (UI.Button) pb.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) pb.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));
        p.add(pb);
        add(p, UI.BorderLayout.CENTER);
    }
}
