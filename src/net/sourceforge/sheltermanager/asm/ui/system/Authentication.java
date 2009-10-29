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
import net.sourceforge.sheltermanager.asm.utility.LDAP;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Map;
import java.util.Vector;


/**
 * Configuration of system authentication.
 * @author Robin Rawson-Tetley
 */
public class Authentication extends ASMForm {
    private UI.ComboBox cboMech;
    private UI.TextField txtLDAPUrl;
    private UI.TextField txtLDAPDN;
    private UI.TextField txtLDAPFilter;
    private UI.TextField txtLDAPUser;
    private UI.TextField txtLDAPPass;
    private UI.Button btnOk;
    private UI.Button btnCancel;

    public Authentication() {
        init(Global.i18n("uisystem", "Authentication"),
            IconManager.getIcon(IconManager.SCREEN_AUTHENTICATION), "uisystem");
        loadData();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboMech);
        ctl.add(txtLDAPUrl);
        ctl.add(txtLDAPDN);
        ctl.add(txtLDAPFilter);
        ctl.add(txtLDAPUser);
        ctl.add(txtLDAPPass);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return cboMech;
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
            if (Configuration.getBoolean("AutoLoginOSUsers")) {
                // OS auth
                cboMech.setSelectedIndex(1);
            } else if (LDAP.isConfigured()) {
                cboMech.setSelectedIndex(2);

                Map m = LDAP.getSettings();
                txtLDAPUrl.setText((String) m.get(LDAP.LDAP_URL));
                txtLDAPDN.setText((String) m.get(LDAP.LDAP_DN));
                txtLDAPFilter.setText((String) m.get(LDAP.LDAP_FILTER));
                txtLDAPUser.setText((String) m.get(LDAP.LDAP_USER));
                txtLDAPPass.setText((String) m.get(LDAP.LDAP_PASS));
            } else {
                cboMech.setSelectedIndex(0);
            }
        } catch (Exception e) {
            Global.logException(e, Authentication.class);
        }
    }

    public boolean saveData() {
        try {
            // Reset data first
            Configuration.setEntry("AutoLoginOSUsers", "No");
            DBConnection.executeAction(
                "DELETE FROM configuration WHERE ItemName Like 'LDAP%'");

            if (cboMech.getSelectedIndex() == 1) {
                Configuration.setEntry("AutoLoginOSUsers", "Yes");
            } else if (cboMech.getSelectedIndex() == 2) {
                LDAP.setSettings(txtLDAPUrl.getText(), txtLDAPDN.getText(),
                    txtLDAPFilter.getText(), txtLDAPUser.getText(),
                    txtLDAPPass.getText());
            } else {
                // DB Auth, leave reset
            }

            dispose();

            return true;
        } catch (Exception e) {
            Global.logException(e, Authentication.class);
        }

        return false;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(1));
        UI.Panel pt = UI.getPanel(UI.getTableLayout(2));
        UI.Panel pb = UI.getPanel(UI.getTableLayout(2));

        cboMech = UI.getCombo();
        cboMech.addItem(i18n("Database") + " - " + i18n("Database_Desc"));
        cboMech.addItem(i18n("Operating_System") + " - " +
            i18n("Operating_System_Desc"));
        cboMech.addItem(i18n("LDAP") + " - " + i18n("LDAP_Desc"));
        cboMech.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 3,
                UI.getComboBoxHeight()));
        UI.addComponent(pt, i18n("Authentication"), cboMech);

        p.add(pt);
        p.add(UI.getTitleLabel(i18n("LDAP")));

        txtLDAPUrl = (UI.TextField) UI.addComponent(pb, i18n("LDAP_URL"),
                UI.getTextField());
        txtLDAPDN = (UI.TextField) UI.addComponent(pb, i18n("LDAP_DN"),
                UI.getTextField());
        txtLDAPFilter = (UI.TextField) UI.addComponent(pb, i18n("LDAP_Filter"),
                UI.getTextField());
        txtLDAPUser = (UI.TextField) UI.addComponent(pb, i18n("LDAP_User"),
                UI.getTextField());
        txtLDAPPass = (UI.TextField) UI.addComponent(pb, i18n("LDAP_Pass"),
                UI.getTextField());

        btnOk = (UI.Button) pb.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "saveData")));
        btnCancel = (UI.Button) pb.add(UI.getButton(i18n("Cancel"), null, 'c',
                    null, UI.fp(this, "dispose")));

        p.add(pb);
        add(p, UI.BorderLayout.CENTER);
    }
}
