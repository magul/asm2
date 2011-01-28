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

/*
 * FlexibleFocusManager.java
 * Created on 24 October 2002, 09:20
 */
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.FocusManager;
import javax.swing.JComponent;


/**
  * A custom focus manager that supports hard-coded focus
  * ordering by the use of an array of components.
  *
  * @author Robin Rawson-Tetley
  */
public class FlexibleFocusManager extends FocusManager {
    /** How long to wait before grabbing the dispatch thread and
     * setting focus to the first component */
    public final static int GRAB_FOCUS_TIMEOUT = 250;

    /** All loaded tab order sets */
    private Vector<FrameInfo> sets = new Vector<FrameInfo>();

    public FlexibleFocusManager() {
        super();
    }

    /**
     * Some parts of a container component can't have the focus,
     * this function checks the type and returns the focusable
     * portion.
     */
    private Object resolveComponent(Object o) {
        if (o instanceof UI.ComboBox) {
            // Editable swing combos focus from the editor portion
            // instead of the combo container itself
            if (((UI.ComboBox) o).isEditable()) {
                return ((UI.ComboBox) o).getCombo().getEditor()
                        .getEditorComponent();
            } else {
                return ((UI.ComboBox) o).getCombo();
            }
        } else if (o instanceof DateField) {
            return ((DateField) o).getTextField();
        } else if (o instanceof CurrencyField) {
            return ((CurrencyField) o).getTextField();
        } else if (o instanceof UI.SearchTextField) {
            return ((UI.SearchTextField) o).getTextField();
        } else {
            return o;
        }
    }

    public void addComponentSet(Vector<Object> focusOrder, Object frame,
        final Object firstComponent) {
        // Process the set - if we have any of our composite components
        // in the list, use their appropriate embedded component instead
        for (int i = 0; i < focusOrder.size(); i++) {
            Object o = focusOrder.get(i);
            Object n = resolveComponent(o);

            if (n != o) {
                focusOrder.set(i, n);
            }
        }

        sets.add(new FrameInfo(focusOrder, frame));

        // Ensure Swing sends focus to the first component after frame 
        // is fully loaded (swing dispatch thread idle)
        UI.invokeIn(new Runnable() {
                public void run() {
                    UI.invokeLater(new InitialComponentGrab(
                            (Component) resolveComponent(firstComponent)));
                }
            }, GRAB_FOCUS_TIMEOUT);
    }

    public void removeComponentSet(Object frame) {
        Vector<FrameInfo> toRemove = new Vector<FrameInfo>();

        // Find any tab sets belonging to this frame and mark them for removal
        for (int i = 0; i < sets.size(); i++) {
            FrameInfo fi = (FrameInfo) sets.get(i);

            if (fi.theFrame == frame) {
                toRemove.add(fi);
            }
        }

        // Remove them
        for (int i = 0; i < toRemove.size(); i++) {
            sets.remove(toRemove.get(i));
        }

        toRemove.clear();
    }

    /**
     * Grabs the focus for a given component
     * @param component The component to give the focus to
     * @direction True if we are moving next, false to previous.
     */
    private void componentGrabFocus(Object component, boolean direction) {
        if (component instanceof JComponent) {
            JComponent jjc = (JComponent) component;
            jjc.grabFocus();

            // If it's disabled, check the direction and
            // do the next component instead
            if (!jjc.isEnabled()) {
                if (direction) {
                    focusNextComponent(jjc);
                } else {
                    focusPreviousComponent(jjc);
                }
            }
        } else {
            Component jjc = (Component) component;
            jjc.requestFocus();
        }
    }

    public void processKeyEvent(Component component, KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.VK_TAB) ||
                (event.getKeyChar() == '\t')) {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if ((event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
                    focusPreviousComponent(component);
                } else {
                    focusNextComponent(component);
                }
            }

            event.consume();
        }
    }

    public void focusNextComponent(Component component) {
        // Reset the autologout timer
        if (Global.mainForm != null) {
            Global.mainForm.resetAutoLogout();
        }

        // Find the current component
        int i = 0;

        Iterator<FrameInfo> it = sets.iterator();

        while (it.hasNext()) {
            FrameInfo f = it.next();
            Object[] focusOrder = f.components.toArray();
            i = 0;

            while (i < focusOrder.length) {
                if ((focusOrder[i] == null) || (component == null)) {
                    i++;

                    continue;
                }

                if (focusOrder[i].equals(component)) {
                    // If we are at the end, set focus to the first
                    // component, otherwise set focus to the next component
                    if (i >= (focusOrder.length - 1)) {
                        componentGrabFocus(focusOrder[0], true);

                        return;
                    } else {
                        componentGrabFocus(focusOrder[i + 1], true);

                        return;
                    }
                }

                i++;
            }
        }
    }

    public void focusPreviousComponent(Component component) {
        // Reset the autologout timer
        if (Global.mainForm != null) {
            Global.mainForm.resetAutoLogout();
        }

        // Find the current component
        int i = 0;

        Iterator<FrameInfo> it = sets.iterator();

        while (it.hasNext()) {
            FrameInfo f = it.next();
            Object[] focusOrder = f.components.toArray();
            i = 0;

            while (i < focusOrder.length) {
                if ((focusOrder[i] == null) || (component == null)) {
                    i++;

                    continue;
                }

                if (focusOrder[i].equals(component)) {
                    // If we are at the beginning, set focus to the last
                    // component, otherwise set focus to the previous component
                    if (i == 0) {
                        componentGrabFocus(focusOrder[focusOrder.length - 1],
                            false);

                        return;
                    } else {
                        componentGrabFocus(focusOrder[i - 1], false);

                        return;
                    }
                }

                i++;
            }
        }
    }
}


class FrameInfo {
    public Object theFrame = null;
    public Vector<Object> components = null;

    public FrameInfo(Vector<Object> components, Object theFrame) {
        this.theFrame = theFrame;
        this.components = components;
    }

    public void free() {
        theFrame = null;
        components.removeAllElements();
        components = null;
    }
}


class InitialComponentGrab implements Runnable {
    private Component jc = null;

    public InitialComponentGrab(Component jc) {
        this.jc = jc;
    }

    private void componentGrabFocus(Object component) {
        if (component instanceof JComponent) {
            JComponent jjc = (JComponent) component;
            jjc.grabFocus();
        } else {
            Component jjc = (Component) component;
            jjc.requestFocus();
        }
    }

    public void run() {
        componentGrabFocus(jc);
        jc = null;
    }
}
