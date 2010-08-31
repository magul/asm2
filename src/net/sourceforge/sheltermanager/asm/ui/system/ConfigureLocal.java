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
import net.sourceforge.sheltermanager.asm.ui.ui.LocaleSwitcher;
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
    private LocaleSwitcher lsLocale;
    private UI.ComboBox cboLabelAlign;
    private UI.ComboBox cboTabAlign;
    private UI.ComboBox cboCaptureMethod;
    private UI.ComboBox cboSkin;
    private UI.ComboBox cboToolbarSize;
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
        ctl.add(cboSkin);
        ctl.add(cboLabelAlign);
        ctl.add(cboTabAlign);
        ctl.add(cboCaptureMethod);
        ctl.add(txtCaptureCommand);
        ctl.add(cboToolbarSize);
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
        return cboSkin;
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

        // Skin
        cboSkin.setSelectedIndex(Global.skin);

        // Selected indexes - RIGHT = 0, LEFT = 1
        if (Global.GRIDLABELALIGN == UI.ALIGN_RIGHT) {
            cboLabelAlign.setSelectedIndex(0);
        } else {
            cboLabelAlign.setSelectedIndex(1);
        }

        // Selected indexes - TOP = 0, BOTTOM = 1, LEFT = 2, RIGHT = 3
        switch (Global.TABALIGN) {
        case UI.TabbedPane.TOP:
            cboTabAlign.setSelectedIndex(0);

            break;

        case UI.TabbedPane.BOTTOM:
            cboTabAlign.setSelectedIndex(1);

            break;

        case UI.TabbedPane.LEFT:
            cboTabAlign.setSelectedIndex(2);

            break;

        case UI.TabbedPane.RIGHT:
            cboTabAlign.setSelectedIndex(3);

            break;
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

        switch (Global.toolbarSize) {
        case 32:
            cboToolbarSize.setSelectedIndex(3);

            break;

        case 24:
            cboToolbarSize.setSelectedIndex(2);

            break;

        case 16:
            cboToolbarSize.setSelectedIndex(1);

            break;

        case 0:
            cboToolbarSize.setSelectedIndex(0);

            break;
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
        UI.Panel p = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());
        UI.Panel pn = UI.getPanel(UI.getBorderLayout());
        pn.add(pb, UI.BorderLayout.NORTH);

        lsLocale = (LocaleSwitcher) UI.addComponent(p, i18n("System_Locale:"),
                new LocaleSwitcher(LocaleSwitcher.FULL));

        cboSkin = (UI.ComboBox) UI.addComponent(p, i18n("Look_and_Feel"),
                UI.getCombo());
        cboSkin.addItem(i18n("Default"));
        cboSkin.addItem(i18n("Native_Platform"));
        cboSkin.addItem(i18n("Metal"));
        cboSkin.addItem(i18n("Metal_for_GTK"));

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
        cboTabAlign.addItem(i18n("LEFT"));
        cboTabAlign.addItem(i18n("RIGHT"));
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

        cboToolbarSize = UI.getCombo();
        cboToolbarSize.addItem(i18n("No_Toolbar"));
        cboToolbarSize.addItem(i18n("Tiny"));
        cboToolbarSize.addItem(i18n("Normal"));
        cboToolbarSize.addItem(i18n("Large"));
        UI.addComponent(p, i18n("Toolbar_Size"), cboToolbarSize);

        p.add(UI.getLabel());
        chkHotkeys = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("use_button_hotkeys"),
                    i18n("Tick_this_box_to_assign_hotkeys_to_buttons")));

        p.add(UI.getLabel());
        chkMaximised = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("Start_Maximised:"),
                    i18n("Tick_this_box_to_start_ASM_in_a_maximised_window")));

        p.add(UI.getLabel());
        chkUseInternal = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("use_internal_report_viewer"),
                    i18n("Tick_this_box_to_use_ASMs_internal_report_viewer")));

        p.add(UI.getLabel());
        chkOneInstance = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("single_instance"),
                    i18n("Tick_this_box_to_only_allow_one_instance")));

        p.add(UI.getLabel());
        chkShowUpdates = (UI.CheckBox) UI.addComponent(p,
                UI.getCheckBox(i18n("show_updates"),
                    i18n("Tick_this_box_to_notify_me_of_asm_updates")));

        txtAutologout = (UI.TextField) UI.addComponent(p,
                i18n("automatic_logout"),
                UI.getTextField(i18n("automatically_log_the_current_user_out_after_minutes_of_inactivity")));

        txtHeartbeatInterval = (UI.TextField) UI.addComponent(p,
                i18n("heartbeat_interval"),
                UI.getTextField(i18n("heartbeat_interval_tooltip")));

        btnOk = (UI.Button) pb.add(UI.getButton(i18n("Ok"),
                    i18n("Save_your_changes_and_exit"), 'o', null,
                    UI.fp(this, "saveData")));

        btnCancel = (UI.Button) pb.add(UI.getButton(i18n("Cancel"),
                    i18n("Discard_changes_and_exit"), 'c', null,
                    UI.fp(this, "actionCancel")));

        add(p, UI.BorderLayout.NORTH);
        add(pn, UI.BorderLayout.CENTER);
    }

    public void actionCancel() {
        dispose();
    }

    public boolean saveData() {
        // Build the output file
        String locale = lsLocale.getSelectedLocale();

        if (locale.indexOf(" ") != -1) {
            locale = Global.getLocaleFromString(locale);
        }

        switch (cboToolbarSize.getSelectedIndex()) {
        case 3:
            Global.toolbarSize = 32;

            break;

        case 2:
            Global.toolbarSize = 24;

            break;

        case 1:
            Global.toolbarSize = 16;

            break;

        case 0:
            Global.toolbarSize = 0;

            break;
        }

        Global.mainForm.initToolbar();

        String of = "Locale = " + locale;
        of += ("\nSkin = " + cboSkin.getSelectedIndex());
        of += ("\nToolbarSize = " + Global.toolbarSize);
        of += ("\nLabelAlignment = " + cboLabelAlign.getSelectedIndex());
        of += ("\nTabAlignment = " + cboTabAlign.getSelectedIndex());
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
            out.write(of.getBytes(Global.CHAR_ENCODING));
            out.flush();
            out.close();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
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
