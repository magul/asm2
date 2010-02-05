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
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 * Handles editing of medical profiles
 *
 * @author Robin Rawson-Tetley
 */
public class ProfileEdit extends ASMForm {
    private ProfileView parent = null;
    private MedicalProfile mp = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.ComboBox cboTimingRuleFrequency;
    private UI.ComboBox cboTreatmentRule;
    private UI.Label lblDuration;
    private UI.Label lblTimingRule;
    private UI.RadioButton radMultiple;
    private UI.RadioButton radOneOff;
    private UI.TextArea txtComments;
    private UI.TextField txtDosage;
    private CurrencyField txtCost;
    private UI.TextField txtProfileName;
    private UI.Spinner spnTimingRule;
    private UI.Spinner spnTimingRuleNoFrequencies;
    private UI.Spinner spnTotalNumberOfTreatments;
    private UI.TextField txtTreatmentName;
    private String audit = null;

    public ProfileEdit(ProfileView parent) {
        this.parent = parent;
        init(Global.i18n("uimedical", "Edit_Medical_Profile"),
            IconManager.getIcon(IconManager.SCREEN_EDITPROFILE), "uimedical");
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtProfileName);
        ctl.add(txtTreatmentName);
        ctl.add(txtDosage);
        ctl.add(txtCost.getTextField());
        ctl.add(radOneOff);
        ctl.add(radMultiple);
        ctl.add(spnTimingRule);
        ctl.add(spnTimingRuleNoFrequencies);
        ctl.add(cboTimingRuleFrequency);
        ctl.add(cboTreatmentRule);
        ctl.add(spnTotalNumberOfTreatments);
        ctl.add(txtComments);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtProfileName;
    }

    public void dispose() {
        mp.free();
        parent = null;
        mp = null;
        super.dispose();
    }

    public boolean needsScroll() {
        return true;
    }

    public int getScrollHeight() {
        return 525;
    }

    /**
     * Analyses on-screen selected controls and enables/disables portions
     * depending on what is selected
     */
    public void enableScreenParts() {
        spnTimingRule.setEnabled(radMultiple.isSelected());
        spnTimingRuleNoFrequencies.setEnabled(radMultiple.isSelected());
        cboTimingRuleFrequency.setEnabled(radMultiple.isSelected());
        lblDuration.setText(calculateTreatmentEnd());
        lblTimingRule.setEnabled(radMultiple.isSelected());

        // Deactivate duration stuff for one-off
        if (radOneOff.isSelected()) {
            lblDuration.setEnabled(radMultiple.isSelected());
            spnTotalNumberOfTreatments.setEnabled(radMultiple.isSelected());
            cboTreatmentRule.setEnabled(radMultiple.isSelected());
        } else {
            lblDuration.setEnabled(cboTreatmentRule.getSelectedIndex() == 0);
            spnTotalNumberOfTreatments.setEnabled(cboTreatmentRule.getSelectedIndex() == 0);
            cboTreatmentRule.setEnabled(true);
        }
    }

    public void openForEdit(MedicalProfile mp) {
        this.mp = mp;

        try {
            txtProfileName.setText(mp.getProfileName());
            txtTreatmentName.setText(mp.getTreatmentName());
            txtDosage.setText(mp.getDosage());
            txtCost.setValue(mp.getCost().doubleValue());

            if (mp.getTimingRule().intValue() == 0) {
                radOneOff.setSelected(true);
            } else {
                radMultiple.setSelected(true);
                spnTimingRule.setValue(mp.getTimingRule());
                spnTimingRuleNoFrequencies.setValue(mp.getTimingRuleNoFrequencies());
                cboTimingRuleFrequency.setSelectedIndex(mp.getTimingRuleFrequency()
                                                          .intValue());
            }

            cboTreatmentRule.setSelectedIndex(mp.getTreatmentRule().intValue());

            if (cboTreatmentRule.getSelectedIndex() == 0) {
                spnTotalNumberOfTreatments.setValue(mp.getTotalNumberOfTreatments());
            }

            txtComments.setText(mp.getComments());

            audit = UI.messageAudit(mp.getCreatedDate(), mp.getCreatedBy(),
                    mp.getLastChangedDate(), mp.getLastChangedBy());

            enableScreenParts();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void openForNew() {
        try {
            this.mp = new MedicalProfile();
            mp.openRecordset("ID = 0");
            mp.addNew();
            enableScreenParts();
            this.setTitle(i18n("Create_New_Medical_Profile"));
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Returns "Treatment Periods (42 treatments)" or something similar to show
     * when the treatments will end. If all info is not available, then just
     * "Treatment Periods" is returned.
     */
    public String calculateTreatmentEnd() {
        String out = i18n("treatment_periods");

        // Drop out if we don't have enough info
        if (spnTimingRule.getValue().equals(new Integer(0))) {
            return out;
        }

        if (spnTotalNumberOfTreatments.getValue().equals(new Integer(0))) {
            return out;
        }

        int no = ((Integer) spnTimingRule.getValue()).intValue();
        int fr = ((Integer) spnTotalNumberOfTreatments.getValue()).intValue();
        int tot = (no * fr);

        out += (" (" + tot + " ");

        if (tot == 1) {
            out += i18n("treatment");
        } else {
            out += i18n("treatments");
        }

        out += ")";

        return out;
    }

    public void initComponents() {
        UI.Panel p = UI.getPanel(UI.getBorderLayout());
        UI.Panel top = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        p.add(top, UI.BorderLayout.NORTH);

        UI.Panel freqdur = UI.getPanel(UI.getGridLayout(1));
        UI.Panel freqwrap = UI.getPanel(UI.getGridLayout(1));
        UI.Panel treatments = UI.getPanel(UI.getFlowLayout());
        UI.Panel frequency = UI.getPanel(UI.getFlowLayout());
        freqwrap.add(treatments);
        freqwrap.add(frequency);

        UI.Panel duration = UI.getPanel(UI.getFlowLayout());
        freqdur.add(freqwrap);
        freqdur.add(duration);
        p.add(freqdur, UI.BorderLayout.SOUTH);
        add(p, UI.BorderLayout.NORTH);

        UI.Panel comments = UI.getPanel(UI.getGridLayout(2, new int[] { 20, 80 }));
        add(comments, UI.BorderLayout.CENTER);

        UI.Panel buttons = UI.getPanel(UI.getFlowLayout());
        add(buttons, UI.BorderLayout.SOUTH);

        txtProfileName = (UI.TextField) UI.addComponent(top,
                i18n("Profile_Name:"), UI.getTextField());
        txtTreatmentName = (UI.TextField) UI.addComponent(top,
                i18n("Treatment_Name:"), UI.getTextField());
        txtDosage = (UI.TextField) UI.addComponent(top, i18n("Dosage:"),
                UI.getTextField());
        txtCost = (CurrencyField) UI.addComponent(top, i18n("Cost:"),
                UI.getCurrencyField());

        freqwrap.setTitle(i18n("Frequency"));
        radOneOff = (UI.RadioButton) treatments.add(UI.getRadioButton(i18n("One-Off"),
                    null, UI.fp(this, "selectedOneOff")));
        radMultiple = (UI.RadioButton) treatments.add(UI.getRadioButton(i18n("Multiple"),
                    null, UI.fp(this, "selectedMultiple")));

        spnTimingRule = (UI.Spinner) frequency.add(UI.getSpinner(1, 50,
                    UI.fp(this, "enableScreenParts")));
        spnTimingRule.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() / 2, UI.getTextBoxHeight()));
        lblTimingRule = (UI.Label) frequency.add(UI.getLabel(i18n("treatments_administered_every")));
        spnTimingRuleNoFrequencies = (UI.Spinner) frequency.add(UI.getSpinner(
                    1, 50));
        spnTimingRuleNoFrequencies.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() / 2, UI.getTextBoxHeight()));
        cboTimingRuleFrequency = UI.getCombo(new String[] {
                    i18n("days"), i18n("weeks"), i18n("months"), i18n("years")
                }, UI.fp(this, "enableScreenParts"));
        frequency.add(cboTimingRuleFrequency);

        duration.setTitle(i18n("Duration"));
        cboTreatmentRule = UI.getCombo(new String[] {
                    i18n("Ends_after"), i18n("Unspecified")
                }, UI.fp(this, "enableScreenParts"));
        duration.add(cboTreatmentRule);
        spnTotalNumberOfTreatments = (UI.Spinner) duration.add(UI.getSpinner(
                    1, 500, UI.fp(this, "enableScreenParts")));
        spnTotalNumberOfTreatments.setPreferredSize(UI.getDimension(
                UI.getTextBoxWidth() / 2, UI.getTextBoxHeight()));
        lblDuration = (UI.Label) duration.add(UI.getLabel(UI.ALIGN_LEFT, ""));
        lblDuration.setPreferredSize(UI.getDimension(UI.getTextBoxWidth() * 2,
                UI.getTextBoxHeight()));

        txtComments = (UI.TextArea) UI.addComponent(comments, i18n("Comments"),
                UI.getTextArea());

        btnOk = (UI.Button) buttons.add(UI.getButton(Global.i18n("reports", "Ok"),
                    null, 'o', null, UI.fp(this, "saveData")));
        btnCancel = (UI.Button) buttons.add(UI.getButton(Global.i18n(
                        "reports", "Cancel"), null, 'c', null,
                    UI.fp(this, "dispose")));

        // Default values
        radMultiple.setSelected(true);
        spnTimingRule.setValue(new Integer(1));
        spnTimingRuleNoFrequencies.setValue(new Integer(1));
        cboTimingRuleFrequency.setSelectedIndex(0);
        cboTreatmentRule.setSelectedIndex(0);
        spnTotalNumberOfTreatments.setValue(new Integer(1));
        lblDuration.setText(calculateTreatmentEnd());
    }

    public void selectedMultiple() {
        if (radMultiple.isSelected()) {
            radOneOff.setSelected(false);
            enableScreenParts();
        }
    }

    public void selectedOneOff() {
        if (radOneOff.isSelected()) {
            radMultiple.setSelected(false);
            spnTotalNumberOfTreatments.setValue(new Integer(1));
            cboTreatmentRule.setSelectedIndex(0);
            enableScreenParts();
        }
    }

    public String getAuditInfo() {
        return audit;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public void loadData() {
    }

    public boolean saveData() {
        // Save values
        try {
            mp.setProfileName(txtProfileName.getText());
            mp.setTreatmentName(txtTreatmentName.getText());
            mp.setDosage(txtDosage.getText());
            mp.setCost(new Double(txtCost.getValue()));

            if (radOneOff.isSelected()) {
                mp.setTimingRule(new Integer(0));
                mp.setTimingRuleNoFrequencies(new Integer(0));
                mp.setTimingRuleFrequency(new Integer(0));
            } else {
                mp.setTimingRule((Integer) spnTimingRule.getValue());
                mp.setTimingRuleNoFrequencies((Integer) spnTimingRuleNoFrequencies.getValue());
                mp.setTimingRuleFrequency(new Integer(
                        cboTimingRuleFrequency.getSelectedIndex()));
            }

            if (mp.getTimingRule().intValue() == 0) {
                radOneOff.setSelected(true);
            } else {
                spnTimingRule.setValue(mp.getTimingRule());
                spnTimingRuleNoFrequencies.setValue(mp.getTimingRuleNoFrequencies());
                cboTimingRuleFrequency.setSelectedIndex(mp.getTimingRuleFrequency()
                                                          .intValue());
            }

            mp.setTreatmentRule(new Integer(cboTreatmentRule.getSelectedIndex()));

            if (cboTreatmentRule.getSelectedIndex() != 0) {
                mp.setTotalNumberOfTreatments(new Integer(0));
            } else {
                mp.setTotalNumberOfTreatments((Integer) spnTotalNumberOfTreatments.getValue());
            }

            mp.setComments(txtComments.getText());

            try {
                mp.save(Global.currentUserName);
            } catch (Exception e) {
                // Validation
                Dialog.showError(e.getMessage());

                return false;
            }

            // Update parent
            parent.updateList();

            dispose();

            return true;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }
}
