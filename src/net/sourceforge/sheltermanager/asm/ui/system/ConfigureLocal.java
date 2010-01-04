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
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Vector;


/**
 * Edits the asm.properties file for the local user
 * @author Robin Rawson-Tetley
 */
public class ConfigureLocal extends ASMForm {
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboLocale;
    private UI.ComboBox cboLabelAlign;
    private UI.ComboBox cboTabAlign;
    private UI.ComboBox cboCaptureMethod;
    private UI.CheckBox chkHotkeys;
    private UI.CheckBox chkMaximised;
    private UI.CheckBox chkUseInternal;
    private UI.CheckBox chkOneInstance;
    private UI.CheckBox chkShowUpdates;
    private UI.TextField txtCaptureCommand;
    private UI.TextField txtAutologout;
    private UI.TextField txtHeartbeatInterval;

    /** Creates new form ConfigureLocal */
    public ConfigureLocal() {
        init(Global.i18n("uisystem", "Configure_Local_Settings"),
            IconManager.getIcon(IconManager.SCREEN_CONFIGURELOCAL), "uisystem");
        loadData();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(cboLocale);
        ctl.add(cboLabelAlign);
        ctl.add(cboTabAlign);
        ctl.add(chkHotkeys);
        ctl.add(chkMaximised);
        ctl.add(chkUseInternal);
        ctl.add(chkOneInstance);
        ctl.add(chkShowUpdates);
        ctl.add(txtAutologout);
        ctl.add(txtHeartbeatInterval);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return cboLocale;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    /**
     * Reads the settings from the Global static class that this session is
     * currently using and default their values into the boxes.
     */
    public void loadData() {
        setClosestComboMatch(cboLocale, Global.settings_Locale);

        // Selected indexes - RIGHT = 0, LEFT = 1
        if (Global.GRIDLABELALIGN == UI.ALIGN_RIGHT) {
            cboLabelAlign.setSelectedIndex(0);
        } else {
            cboLabelAlign.setSelectedIndex(1);
        }

        // Selected indexes - TOP = 0, BOTTOM = 1
        if (Global.TABALIGN == UI.ALIGN_TOP) {
            cboTabAlign.setSelectedIndex(0);
        } else {
            cboTabAlign.setSelectedIndex(1);
        }

        if (!Global.applet) {
            chkMaximised.setSelected(Global.startMaximised);
            chkOneInstance.setSelected(Global.oneInstance);
            chkShowUpdates.setSelected(Global.showUpdates);
        } else {
            // These options don't make sense for an applet
            chkMaximised.setEnabled(false);
            chkOneInstance.setEnabled(false);
            chkShowUpdates.setEnabled(false);
            txtAutologout.setEnabled(false);
        }

        cboCaptureMethod.setSelectedIndex(Global.videoCaptureMethod);
        txtCaptureCommand.setText(Global.videoCaptureCommand);
        chkHotkeys.setSelected(Global.buttonHotkeys);
        chkUseInternal.setSelected(Global.useInternalReportViewer);
        txtAutologout.setText(Integer.toString(Global.autoLogout));
        txtHeartbeatInterval.setText(Integer.toString(Global.heartbeatInterval));
    }

    public void setClosestComboMatch(UI.ComboBox cbo, String value) {
        value = value.toLowerCase();

        for (int i = 0; i < cbo.getItemCount(); i++) {
            String comp = (String) cbo.getItemAt(i);
            comp = comp.toLowerCase();

            if (comp.startsWith(value)) {
                cbo.setSelectedIndex(i);

                return;
            }
        }
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getTableLayout(2));

        cboLocale = (UI.ComboBox) UI.addComponent(p, i18n("System_Locale:"),
                UI.getCombo(Global.getSupportedLocales()));
        cboLocale.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 2,
                UI.getComboBoxHeight()));

        cboLabelAlign = (UI.ComboBox) UI.addComponent(p, i18n("Align_labels"),
                UI.getCombo());
        cboLabelAlign.setToolTipText(i18n("select_where_asm_should_align_field_labels"));
        cboLabelAlign.addItem(i18n("RIGHT"));
        cboLabelAlign.addItem(i18n("LEFT"));
        cboLabelAlign.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 2, UI.getComboBoxHeight()));

        cboTabAlign = (UI.ComboBox) UI.addComponent(p, i18n("Align_tabs"),
                UI.getCombo());
        cboTabAlign.addItem(i18n("TOP"));
        cboTabAlign.addItem(i18n("BOTTOM"));
        cboTabAlign.setToolTipText(i18n("select_where_to_align_tabs"));
        cboTabAlign.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 2,
                UI.getComboBoxHeight()));

        cboCaptureMethod = (UI.ComboBox) UI.addComponent(p,
                i18n("Video_capture_method"), UI.getCombo());
        cboCaptureMethod.addItem(i18n("vgrabbj_capture"));
        cboCaptureMethod.addItem(i18n("http_capture"));
        cboCaptureMethod.addItem(i18n("command_capture"));
        cboCaptureMethod.setToolTipText(i18n("Video_capture_method_tool"));
        cboCaptureMethod.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 2, UI.getComboBoxHeight()));

        txtCaptureCommand = (UI.TextField) UI.addComponent(p,
                i18n("Video_capture_command_url"),
                UI.getTextField(i18n("command_or_uri")));
        txtCaptureCommand.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() * 2, UI.getComboBoxHeight()));

        chkHotkeys = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("use_button_hotkeys"),
                    i18n("Tick_this_box_to_assign_hotkeys_to_buttons")));

        chkMaximised = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("Start_Maximised:"),
                    i18n("Tick_this_box_to_start_ASM_in_a_maximised_window")));

        chkUseInternal = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("use_internal_report_viewer"),
                    i18n("Tick_this_box_to_use_ASMs_internal_report_viewer")));

        chkOneInstance = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("single_instance"),
                    i18n("Tick_this_box_to_only_allow_one_instance")));

        chkShowUpdates = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("show_updates"),
                    i18n("Tick_this_box_to_notify_me_of_asm_updates")));

        p.add(UI.getLabel());

        txtAutologout = (UI.TextField) UI.addComponent(p,
                i18n("automatic_logout"),
                UI.getTextField(i18n("automatically_log_the_current_user_out_after_minutes_of_inactivity")));

        txtHeartbeatInterval = (UI.TextField) UI.addComponent(p,
                i18n("heartbeat_interval"),
                UI.getTextField(i18n("heartbeat_interval_tooltip")));

        UI.Panel pb = UI.getPanel(UI.getFlowLayout());
        btnOk = (UI.Button) pb.add(UI.getButton(i18n("Ok"),
                    i18n("Save_your_changes_and_exit"), 'o', null,
                    UI.fp(this, "saveData")));
        btnOk = (UI.Button) pb.add(UI.getButton(i18n("Cancel"),
                    i18n("Discard_changes_and_exit"), 'c', null,
                    UI.fp(this, "actionCancel")));
        p.add(pb);

        add(p, UI.BorderLayout.CENTER);
    }

    public void actionCancel() {
        dispose();
    }

    public boolean saveData() {
        // Build the output file
        String locale = (String) cboLocale.getSelectedItem();

        if (locale.indexOf(" ") != -1) {
            locale = Global.getLocaleFromString(locale);
        }

        String of = "Locale = " + locale;
        of += ("\nLabelAlignment = " +
        cboLabelAlign.getSelectedItem().toString());
        of += ("\nTabAlignment = " + cboTabAlign.getSelectedItem().toString());
        of += ("\nButtonHotkeys = " +
        (chkHotkeys.isSelected() ? "True" : "False"));
        of += ("\nStartMaximised = " +
        (chkMaximised.isSelected() ? "True" : "False"));
        of += ("\nInternalReportViewer = " +
        (chkUseInternal.isSelected() ? "True" : "False"));
        of += ("\nOnlyAllowOneInstance = " +
        (chkOneInstance.isSelected() ? "True" : "False"));
        of += ("\nShowUpdates = " +
        (chkShowUpdates.isSelected() ? "True" : "False"));
        of += ("\nVideoCaptureMethod = " +
        (cboCaptureMethod.getSelectedIndex()));
        of += ("\nVideoCaptureCommand = " + (txtCaptureCommand.getText()));

        try {
            Integer.parseInt(txtAutologout.getText());
        } catch (NumberFormatException e) {
            txtAutologout.setText("0");
        }

        of += ("\nAutoLogout = " + (txtAutologout.getText()));

        try {
            Integer.parseInt(txtHeartbeatInterval.getText());
        } catch (NumberFormatException e) {
            txtHeartbeatInterval.setText("0");
        }

        of += ("\nHeartbeatInterval = " + (txtHeartbeatInterval.getText()));

        // Dump it to the file
        try {
            File f = new File(Global.tempDirectory + File.separator +
                    "asm.properties");
            FileOutputStream out = new FileOutputStream(f);
            out.write(of.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uisystem",
                    "An_error_occurred_saving_changes_to_localsettings.conf:\n") +
                e.getMessage());
            Global.logException(e, getClass());

            return false;
        }

        // Re-read the file to load the settings in again
        net.sourceforge.sheltermanager.asm.startup.Startup.readSettings();

        // Reload toolbar and menu in case language has changed
        Global.mainForm.reloadToolsAndMenu();

        dispose();

        return true;
    }
}
