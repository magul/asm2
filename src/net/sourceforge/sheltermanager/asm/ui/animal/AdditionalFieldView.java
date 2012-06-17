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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.Additional;
import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.FunctionPointer;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class AdditionalFieldView extends UI.Panel {
    private ArrayList<Object> additionalComponents = new ArrayList<Object>();
    private HashMap<Integer, Boolean> mandatoryFields = new HashMap<Integer, Boolean>();
    private HashMap<Integer, String> fieldLabels = new HashMap<Integer, String>();

    /**
    * Creates the components on our panel
    * @param linkType one of the AdditionalField.LINKTYPE constants
    */
    public AdditionalFieldView(int linkType, FunctionPointer onFieldChange) {
        // Grab our list of fields
        SQLRecordset f = LookupCache.getAdditionalFieldLookup();

        if (f.getRecordCount() == 0) {
            return;
        }

        // Draw them out - use two panels, one grid for non-textareas
        // and one for the text areas. Arrange them so textareas fill
        // available space
        UI.Panel p = this;
        int cols = (int) f.getRecordCount();
        int[] colwidths = null;
        int c = 0;

        /*if (cols < 10) {
            c = 1;
            colwidths = new int[] { 30, 70 };
        } else */
        if (cols < 20) {
            c = 2;
            colwidths = new int[] { 15, 35, 15, 35 };
        } else if (cols < 30) {
            c = 3;
            colwidths = new int[] { 10, 23, 10, 23, 10, 24 };
        } else {
            c = 4;
            colwidths = new int[] { 7, 18, 7, 18, 7, 18, 7, 18 };
        }

        p.setLayout(UI.getBorderLayout());

        UI.Panel pf = UI.getPanel(UI.getGridLayout(c * 2, colwidths));
        UI.Panel pt = UI.getPanel(UI.getGridLayout(c * 2, colwidths));
        p.add(pt, UI.BorderLayout.CENTER);
        p.add(pf, UI.BorderLayout.NORTH);

        try {
            f.moveFirst();

            while (!f.getEOF()) {
                if (((Integer) f.getField("LinkType")).intValue() == linkType) {
                    mandatoryFields.put(f.getInteger("ID"),
                        f.getInteger("Mandatory") == 1);
                    fieldLabels.put(f.getInteger("ID"),
                        f.getString("FieldLabel"));

                    switch (((Integer) f.getField("FieldType")).intValue()) {
                    case AdditionalField.FIELDTYPE_YESNO:
                        pf.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        UI.CheckBox cb = UI.getCheckBox(null, null,
                                onFieldChange);
                        cb.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            cb.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(cb);
                        pf.add(cb);

                        break;

                    case AdditionalField.FIELDTYPE_DATE:
                        pf.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        DateField df = UI.getDateField(null, onFieldChange);
                        df.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            df.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(df);
                        pf.add(df);

                        break;

                    case AdditionalField.FIELDTYPE_MONEY:
                        pf.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        CurrencyField mo = UI.getCurrencyField(null,
                                onFieldChange);
                        mo.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            mo.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(mo);
                        pf.add(mo);

                        break;

                    case AdditionalField.FIELDTYPE_NOTES:
                        pt.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        UI.TextArea no = UI.getTextArea(null, onFieldChange);
                        no.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            no.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(no);
                        pt.add(no);

                        break;

                    case AdditionalField.FIELDTYPE_TEXT:
                        pf.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        UI.TextField te = UI.getTextField(null, onFieldChange);
                        te.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            te.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(te);
                        pf.add(te);

                        break;

                    case AdditionalField.FIELDTYPE_NUMBER:
                        pf.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        UI.Spinner sp = UI.getSpinner(0, 0xffffff, onFieldChange);
                        sp.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            sp.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(sp);
                        pf.add(sp);

                        break;

                    case AdditionalField.FIELDTYPE_LOOKUP:
                        pf.add(UI.getLabel(f.getField("FieldLabel").toString()));

                        UI.ComboBox cbo = UI.getCombo(onFieldChange);
                        cbo.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            cbo.setToolTipText(f.getField("ToolTip").toString());
                        }

                        String[] values = Utils.split(f.getField("LookupValues")
                                                       .toString(), "|");

                        for (int i = 0; i < values.length; i++) {
                            cbo.addItem(values[i].trim());
                        }

                        additionalComponents.add(cbo);
                        pf.add(cbo);

                        break;
                    }
                }

                f.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public ArrayList<Object> getAdditionalComponents() {
        return additionalComponents;
    }

    public boolean hasFields() {
        return additionalComponents.size() > 0;
    }

    /** Returns true if fields exist for the link type */
    public static boolean hasAdditionalFields(int linkType) {
        SQLRecordset f = LookupCache.getAdditionalFieldLookup();

        return f.getRecordCount() > 0;
    }

    /** Saves the additional fields - Returns false if there
     *  was a problem with a field value
     */
    public boolean saveFields(int linkID, int linkType) {
        if (!hasFields()) {
            return true;
        }

        try {
            ArrayList<Additional.Field> f = new ArrayList<Additional.Field>();

            for (int i = 0; i < additionalComponents.size(); i++) {
                Object o = additionalComponents.get(i);
                Additional.Field af = new Additional.Field();

                if (o instanceof UI.CheckBox) {
                    af.fieldID = Integer.parseInt(((UI.CheckBox) o).getName());

                    if (((UI.CheckBox) o).isSelected()) {
                        af.value = "1";
                    } else {
                        af.value = "0";
                    }
                }

                if (o instanceof DateField) {
                    int id = Integer.parseInt(((DateField) o).getName());
                    String text = ((DateField) o).getText();
                    String validationFail = Global.i18n("uianimal",
                            "additional_field_empty", fieldLabels.get(id));
                    boolean mandatory = mandatoryFields.get(id).booleanValue();

                    if (mandatory && text.equals("")) {
                        Dialog.showError(validationFail);

                        return false;
                    }

                    af.fieldID = id;
                    af.value = text;
                }

                if (o instanceof CurrencyField) {
                    int id = Integer.parseInt(((CurrencyField) o).getName());
                    String text = Integer.toString(((CurrencyField) o).getValue());
                    String validationFail = Global.i18n("uianimal",
                            "additional_field_empty", fieldLabels.get(id));
                    boolean mandatory = mandatoryFields.get(id).booleanValue();

                    if (mandatory && text.equals("")) {
                        Dialog.showError(validationFail);

                        return false;
                    }

                    af.fieldID = id;
                    af.value = text;
                }

                if (o instanceof UI.TextField) {
                    int id = Integer.parseInt(((UI.TextField) o).getName());
                    String text = ((UI.TextField) o).getText();
                    String validationFail = Global.i18n("uianimal",
                            "additional_field_empty", fieldLabels.get(id));
                    boolean mandatory = mandatoryFields.get(id).booleanValue();

                    if (mandatory && text.equals("")) {
                        Dialog.showError(validationFail);

                        return false;
                    }

                    af.fieldID = id;
                    af.value = text;
                }

                if (o instanceof UI.TextArea) {
                    int id = Integer.parseInt(((UI.TextArea) o).getName());
                    String text = ((UI.TextArea) o).getText();
                    String validationFail = Global.i18n("uianimal",
                            "additional_field_empty", fieldLabels.get(id));
                    boolean mandatory = mandatoryFields.get(id).booleanValue();

                    if (mandatory && text.equals("")) {
                        Dialog.showError(validationFail);

                        return false;
                    }

                    af.fieldID = id;
                    af.value = text;
                }

                if (o instanceof UI.Spinner) {
                    af.fieldID = Integer.parseInt(((UI.Spinner) o).getName());
                    af.value = ((UI.Spinner) o).getValue().toString();
                }

                if (o instanceof UI.ComboBox) {
                    int id = Integer.parseInt(((UI.ComboBox) o).getName());
                    String text = ((UI.ComboBox) o).getSelectedItem().toString();
                    String validationFail = Global.i18n("uianimal",
                            "additional_field_empty", fieldLabels.get(id));
                    boolean mandatory = mandatoryFields.get(id).booleanValue();

                    if (mandatory && text.equals("")) {
                        Dialog.showError(validationFail);

                        return false;
                    }

                    af.fieldID = id;
                    af.value = text;
                }

                f.add(af);
            }

            Additional.setFieldValues(linkType, linkID, f);

            return true;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return false;
    }

    /** Calls getName() on the object and returns the
     * value as a parsed integer
     * @param o
     * @return
     */
    @SuppressWarnings("unchecked")
    private int getComponentID(Object o) {
        try {
            Class c = o.getClass();
            Method m = c.getMethod("getName", (Class[]) null);
            Object s = m.invoke(o, (Object[]) null);

            return Integer.parseInt(s.toString());
        } catch (Exception e) {
            Global.logException(e, getClass());

            return 0;
        }
    }

    public void loadFields(int linkID, int linkType) {
        if (!hasFields()) {
            return;
        }

        try {
            ArrayList<Additional.Field> f = Additional.getFieldValues(linkType,
                    linkID);

            for (int i = 0; i < additionalComponents.size(); i++) {
                Object o = additionalComponents.get(i);

                // Find the value object for this component
                int componentID = getComponentID(o);
                Additional.Field af = null;

                for (int z = 0; z < f.size(); z++) {
                    af = f.get(z);

                    if (af.fieldID == componentID) {
                        break;
                    }
                }

                // Skip if there was no value object for the component
                if (af == null) {
                    continue;
                }

                if (o instanceof UI.CheckBox) {
                    ((UI.CheckBox) o).setSelected(af.value.equals("1"));
                }

                if (o instanceof DateField) {
                    try {
                        ((DateField) o).setDate(Utils.parseDate(af.value));
                    }
                    catch (java.text.ParseException e) {
                        // Do nothing if we don't have a date
                    }
                }

                if (o instanceof CurrencyField) {
                    ((CurrencyField) o).setValue(Integer.parseInt(af.value));
                }

                if (o instanceof UI.TextField) {
                    ((UI.TextField) o).setText(af.value);
                }

                if (o instanceof UI.TextArea) {
                    ((UI.TextArea) o).setText(af.value);
                }

                if (o instanceof UI.Spinner) {
                    ((UI.Spinner) o).setValue(new Integer(af.value));
                }

                if (o instanceof UI.ComboBox) {
                    ((UI.ComboBox) o).setSelectedItem(af.value);
                }

                f.add(af);
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }
}
