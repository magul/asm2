/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.util.Vector;


/**
 * This class contains all code for editing additional fields
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class AdditionalFieldEdit extends ASMForm {
    private AdditionalField af = null;

    /** A reference back to the parent form that spawned this form */
    private ConfigureAdditional parent = null;
    private UI.Button btnCancel;
    private UI.Button btnOk;
    private UI.TextField txtFieldName;
    private UI.TextField txtFieldLabel;
    private UI.TextArea txtTooltip;
    private UI.TextArea txtLookupValues;
    private UI.ComboBox cboFieldLink;
    private UI.ComboBox cboFieldType;
    private UI.Spinner spnDisplayIndex;
    private UI.CheckBox chkMandatory;

    public AdditionalFieldEdit(ConfigureAdditional parent) {
        this.parent = parent;
        init("", IconManager.getIcon(IconManager.SCREEN_EDITADDITIONALFIELD),
            "uisystem");
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(txtFieldName);
        ctl.add(txtFieldLabel);
        ctl.add(cboFieldLink);
        ctl.add(cboFieldType);
        ctl.add(spnDisplayIndex);
        ctl.add(txtTooltip);
        ctl.add(txtLookupValues);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtFieldName;
    }

    public void openForNew() {
        this.af = new AdditionalField();
        this.setTitle(i18n("new_additional_field"));
        af.openRecordset("ID = 0");

        try {
            af.addNew();
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void openForEdit(int id) {
        this.af = new AdditionalField();
        af.openRecordset("ID = " + id);

        this.setTitle(i18n("edit_additional_field"));
        loadData();
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean formClosing() {
        return false;
    }

    public void loadData() {
        try {
            this.txtFieldName.setText(af.getFieldName());
            this.txtFieldLabel.setText(af.getFieldLabel());
            this.txtTooltip.setText(af.getToolTip());
            this.cboFieldLink.setSelectedIndex(af.getLinkType().intValue());
            this.cboFieldType.setSelectedIndex(af.getFieldType().intValue());
            this.spnDisplayIndex.setValue(af.getDisplayIndex());
            this.txtLookupValues.setText(af.getLookupValues());
            this.chkMandatory.setSelected(af.getMandatory().intValue() == 1);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public boolean saveData() {
        try {
            af.setFieldName(txtFieldName.getText());
            af.setFieldLabel(txtFieldLabel.getText());
            af.setToolTip(txtTooltip.getText());
            af.setFieldType(new Integer(cboFieldType.getSelectedIndex()));
            af.setLinkType(new Integer(cboFieldLink.getSelectedIndex()));
            af.setDisplayIndex(new Integer(spnDisplayIndex.getValue().toString()));
            af.setLookupValues(txtLookupValues.getText());
            af.setMandatory(new Integer(chkMandatory.isSelected() ? 1 : 0));
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        try {
            af.save();

            // Update the parent
            parent.updateList();

            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
        }

        return false;
    }

    public void initComponents() {
        UI.Panel pt = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pc = UI.getPanel(UI.getGridLayout(2, new int[] { 30, 70 }));
        UI.Panel pb = UI.getPanel(UI.getFlowLayout());

        txtFieldName = (UI.TextField) UI.addComponent(pt, i18n("fieldname"),
                UI.getTextField());

        txtFieldLabel = (UI.TextField) UI.addComponent(pt, i18n("fieldlabel"),
                UI.getTextField());

        cboFieldLink = UI.getCombo(LookupCache.getFieldLinkLookup(), "LinkType");
        cboFieldType = UI.getCombo(LookupCache.getFieldTypeLookup(), "FieldType");

        UI.addComponent(pt, i18n("linktype"), cboFieldLink);
        UI.addComponent(pt, i18n("fieldtype"), cboFieldType);
        chkMandatory = (UI.CheckBox) UI.addComponent(pt, i18n("mandatory"),
                UI.getCheckBox());

        spnDisplayIndex = UI.getSpinner(0, 0xffffff);
        UI.addComponent(pt, i18n("displayindex"), spnDisplayIndex);

        txtTooltip = (UI.TextArea) UI.addComponent(pc, i18n("tooltip"),
                UI.getTextArea());

        txtLookupValues = (UI.TextArea) UI.addComponent(pc,
                i18n("lookupvalues"), UI.getTextArea());

        btnOk = (UI.Button) pb.add(UI.getButton(Global.i18n("uianimal", "Ok"),
                    null, 'o', null, UI.fp(this, "saveData")));

        btnCancel = (UI.Button) pb.add(UI.getButton(Global.i18n("uianimal",
                        "Cancel"), null, 'c', null, UI.fp(this, "dispose")));

        add(pt, UI.BorderLayout.NORTH);
        add(pc, UI.BorderLayout.CENTER);
        add(pb, UI.BorderLayout.SOUTH);
    }
}
