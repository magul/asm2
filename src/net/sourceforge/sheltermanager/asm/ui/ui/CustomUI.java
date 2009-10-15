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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.asm.wordprocessor.MovementDocument;
import net.sourceforge.sheltermanager.asm.wordprocessor.OwnerDocument;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Properties;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JToolBar;


/**
 * Handles extra bits of the UI that users can configure through
 * ~/.asm/plugins.properties
 */
public class CustomUI {
    private static Vector customAnimalButtons = new Vector();
    private static Vector customOwnerButtons = new Vector();
    private static Vector customMovementButtons = new Vector();

    /** Generates the custom additional buttons for the animal form */
    public static void readCustomAnimalButtons(Properties p) {
        customAnimalButtons.removeAllElements();

        for (int i = 1; i < 10; i++) {
            String action = p.getProperty("animal.button." + i + ".action", "");

            if (action.equals("")) {
                break;
            }

            CustomButton cb = new CustomButton();
            cb.action = action;
            cb.tooltip = p.getProperty("animal.button." + i + ".tooltip", "");
            cb.hotkey = p.getProperty("animal.button." + i + ".hotkey", " ")
                         .charAt(0);
            cb.icon = IconManager.getIcon(p.getProperty("animal.button." + i +
                        ".icon"));
            cb.type = cb.ANIMAL;
            Global.logDebug("Found custom animal button: " + cb.toString(),
                "UI.readCustomAnimalButtons");
            customAnimalButtons.add(cb);
        }
    }

    /** Generates the custom additional buttons for the owner form */
    public static void readCustomOwnerButtons(Properties p) {
        customOwnerButtons.removeAllElements();

        for (int i = 1; i < 10; i++) {
            String action = p.getProperty("owner.button." + i + ".action", "");

            if (action.equals("")) {
                break;
            }

            CustomButton cb = new CustomButton();
            cb.action = action;
            cb.tooltip = p.getProperty("owner.button." + i + ".tooltip", "");
            cb.hotkey = p.getProperty("owner.button." + i + ".hotkey", " ")
                         .charAt(0);
            cb.icon = IconManager.getIcon(p.getProperty("owner.button." + i +
                        ".icon"));
            cb.type = cb.OWNER;
            Global.logDebug("Found custom owner button: " + cb.toString(),
                "UI.readCustomOwnerButtons");
            customOwnerButtons.add(cb);
        }
    }

    /** Generates the custom additional buttons for the movement form */
    public static void readCustomMovementButtons(Properties p) {
        customMovementButtons.removeAllElements();

        for (int i = 1; i < 10; i++) {
            String action = p.getProperty("movement.button." + i + ".action", "");

            if (action.equals("")) {
                break;
            }

            CustomButton cb = new CustomButton();
            cb.action = action;
            cb.tooltip = p.getProperty("movement.button." + i + ".tooltip", "");
            cb.hotkey = p.getProperty("movement.button." + i + ".hotkey", " ")
                         .charAt(0);
            cb.icon = IconManager.getIcon(p.getProperty("movement.button." + i +
                        ".icon"));
            cb.type = cb.ANIMAL;
            Global.logDebug("Found custom movement button: " + cb.toString(),
                "UI.readCustomMovementButtons");
            customMovementButtons.add(cb);
        }
    }

    public static void processAction(String action) {
        // Given an action, decides what to do with it
        Global.logDebug("Executing: " + action, "CustomUI.processAction");

        if (action.startsWith("http")) {
            Global.logDebug("HTTP action found", "CustomUI.processAction");

            // Process it with the HTML viewer
            if (Global.useInternalReportViewer) {
                HTMLViewer hv = new HTMLViewer(action);
                Global.mainForm.addChild(hv);
            } else {
                // Open it with our browser
                FileTypeManager.shellExecute(action, false);
            }
        } else {
            // Run it
            Global.logDebug("Shell action found", "CustomUI.processAction");
            Utils.execAsync(Utils.split(action, " "));
        }
    }

    public static void processCustomAnimalAction(CustomButton b, int id) {
        try {
            Animal a = new Animal();
            a.openRecordset("ID = " + id);

            AnimalDocument ad = new AnimalDocument(a, true);
            String action = ad.replaceInText(b.action);
            processAction(action);
        } catch (Exception e) {
            Global.logException(e, CustomUI.class);
        }
    }

    public static void processCustomMovementAction(CustomButton b, int id) {
        try {
            Adoption a = new Adoption();
            a.openRecordset("ID = " + id);

            MovementDocument md = new MovementDocument(a, true);
            String action = md.replaceInText(b.action);
            processAction(action);
        } catch (Exception e) {
            Global.logException(e, CustomUI.class);
        }
    }

    public static void processCustomOwnerAction(CustomButton b, int id) {
        try {
            Owner o = new Owner();
            o.openRecordset("ID = " + id);

            OwnerDocument od = new OwnerDocument(o, true);
            String action = od.replaceInText(b.action);
            processAction(action);
        } catch (Exception e) {
            Global.logException(e, CustomUI.class);
        }
    }

    public static void addCustomAnimalButtons(JToolBar parent, final int id) {
        for (int i = 0; i < customAnimalButtons.size(); i++) {
            final CustomButton cb = (CustomButton) customAnimalButtons.get(i);
            UI.Button b = UI.getButton(null, cb.tooltip, cb.hotkey, cb.icon,
                    null);
            b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        processCustomAnimalAction(cb, id);
                    }
                });
            parent.add(b);
        }
    }

    public static void readCustomMenu(Properties p) {
    }

    public static void addCustomOwnerButtons(JToolBar parent, final int id) {
        for (int i = 0; i < customOwnerButtons.size(); i++) {
            final CustomButton cb = (CustomButton) customOwnerButtons.get(i);
            UI.Button b = UI.getButton(null, cb.tooltip, cb.hotkey, cb.icon,
                    null);
            b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        processCustomOwnerAction(cb, id);
                    }
                });
            parent.add(b);
        }
    }

    public static void addCustomMovementButtons(JToolBar parent, final int id) {
        for (int i = 0; i < customMovementButtons.size(); i++) {
            final CustomButton cb = (CustomButton) customMovementButtons.get(i);
            UI.Button b = UI.getButton(null, cb.tooltip, cb.hotkey, cb.icon,
                    null);
            b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        processCustomMovementAction(cb, id);
                    }
                });
            parent.add(b);
        }
    }

    public static void addCustomMenu(JMenu parent) {
    }

    public static class CustomButton {
        public String tooltip = "";
        public Icon icon = null;
        public char hotkey = ' ';
        public String action = "";
        public final int ANIMAL = 0;
        public final int OWNER = 1;
        public int type = ANIMAL;

        public String toString() {
            return "tooltip=" + tooltip + ",icon=" + icon.toString() +
            ",hotkey=" + hotkey + ",action=" + action;
        }
    }
}
