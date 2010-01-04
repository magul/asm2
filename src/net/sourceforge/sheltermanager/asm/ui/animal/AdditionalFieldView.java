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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.Additional;
import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.CurrencyField;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.FunctionPointer;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.lang.reflect.Method;

import java.util.Vector;


public class AdditionalFieldView extends UI.Panel {
    private boolean hasData = false;
    private Vector additionalComponents = new Vector();

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

        hasData = true;

        // Draw them out
        UI.Panel p = this;
        int cols = (int) f.getRecordCount();

        if (cols < 5) {
            cols = 1;
        } else if (cols < 10) {
            cols = 2;
        } else {
            cols = 3;
        }

        p.setLayout(UI.getTableLayout(cols * 2));

        try {
            f.moveFirst();

            while (!f.getEOF()) {
                if (((Integer) f.getField("LinkType")).intValue() == linkType) {
                    p.add(UI.getLabel(f.getField("FieldLabel").toString()));

                    switch (((Integer) f.getField("FieldType")).intValue()) {
                    case AdditionalField.FIELDTYPE_YESNO:

                        UI.CheckBox cb = UI.getCheckBox(null, null,
                                onFieldChange);
                        cb.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            cb.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(cb);
                        p.add(cb);

                        break;

                    case AdditionalField.FIELDTYPE_DATE:

                        DateField df = UI.getDateField(null, onFieldChange);
                        df.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            df.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(df);
                        p.add(df);

                        break;

                    case AdditionalField.FIELDTYPE_MONEY:

                        CurrencyField mo = UI.getCurrencyField(null,
                                onFieldChange);
                        mo.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            mo.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(mo);
                        p.add(mo);

                        break;

                    case AdditionalField.FIELDTYPE_NOTES:

                        UI.TextArea no = UI.getTextArea(null, onFieldChange);
                        no.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            no.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(no);
                        p.add(no);

                        break;

                    case AdditionalField.FIELDTYPE_TEXT:

                        UI.TextField te = UI.getTextField(null, onFieldChange);
                        te.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            te.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(te);
                        p.add(te);

                        break;

                    case AdditionalField.FIELDTYPE_NUMBER:

                        UI.Spinner sp = UI.getSpinner(0, 0xffffff, onFieldChange);
                        sp.setName(f.getField("ID").toString());

                        if (f.getField("ToolTip") != null) {
                            sp.setToolTipText(f.getField("ToolTip").toString());
                        }

                        additionalComponents.add(sp);
                        p.add(sp);

                        break;

                    case AdditionalField.FIELDTYPE_LOOKUP:

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
                        p.add(cbo);

                        break;
                    }
                }

                f.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public Vector getAdditionalComponents() {
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

    public void saveFields(int linkID, int linkType) {
        if (!hasFields()) {
            return;
        }

        try {
            Vector f = new Vector();

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
                    af.fieldID = Integer.parseInt(((DateField) o).getName());
                    af.value = ((DateField) o).getText();
                }

                if (o instanceof CurrencyField) {
                    af.fieldID = Integer.parseInt(((CurrencyField) o).getName());
                    af.value = Double.toString(((CurrencyField) o).getValue());
                }

                if (o instanceof UI.TextField) {
                    af.fieldID = Integer.parseInt(((UI.TextField) o).getName());
                    af.value = ((UI.TextField) o).getText();
                }

                if (o instanceof UI.TextArea) {
                    af.fieldID = Integer.parseInt(((UI.TextArea) o).getName());
                    af.value = ((UI.TextArea) o).getText();
                }

                if (o instanceof UI.Spinner) {
                    af.fieldID = Integer.parseInt(((UI.Spinner) o).getName());
                    af.value = ((UI.Spinner) o).getValue().toString();
                }

                if (o instanceof UI.ComboBox) {
                    af.fieldID = Integer.parseInt(((UI.ComboBox) o).getName());
                    af.value = ((UI.ComboBox) o).getSelectedItem().toString();
                }

                f.add(af);
            }

            Additional.setFieldValues(linkType, linkID, f);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** Calls getName() on the object and returns the
     * value as a parsed integer
     * @param o
     * @return
     */
    private int getComponentID(Object o) {
        try {
            Class c = o.getClass();
            Method m = c.getMethod("getName", null);
            Object s = m.invoke(o, null);

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
            Vector f = Additional.getFieldValues(linkType, linkID);

            for (int i = 0; i < additionalComponents.size(); i++) {
                Object o = additionalComponents.get(i);

                // Find the value object for this component
                int componentID = getComponentID(o);
                Additional.Field af = null;

                for (int z = 0; z < f.size(); z++) {
                    af = (Additional.Field) f.get(z);

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
                    ((DateField) o).setDate(Utils.parseDate(af.value));
                }

                if (o instanceof CurrencyField) {
                    ((CurrencyField) o).setValue(Double.parseDouble(af.value));
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
