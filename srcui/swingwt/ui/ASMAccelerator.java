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

import net.sourceforge.sheltermanager.asm.globals.Global;

import swingwt.awt.event.InputEvent;
import swingwt.awt.event.KeyEvent;

import swingwtx.swing.KeyStroke;

import java.util.Hashtable;


/**
 * Bundles up translation of keystrokes
 */
public class ASMAccelerator {
    static Hashtable keys = new Hashtable(50);

    static {
        keys.put("a", new Integer(KeyEvent.VK_A));
        keys.put("b", new Integer(KeyEvent.VK_B));
        keys.put("c", new Integer(KeyEvent.VK_C));
        keys.put("d", new Integer(KeyEvent.VK_D));
        keys.put("e", new Integer(KeyEvent.VK_E));
        keys.put("f", new Integer(KeyEvent.VK_F));
        keys.put("g", new Integer(KeyEvent.VK_G));
        keys.put("h", new Integer(KeyEvent.VK_H));
        keys.put("i", new Integer(KeyEvent.VK_I));
        keys.put("j", new Integer(KeyEvent.VK_J));
        keys.put("k", new Integer(KeyEvent.VK_K));
        keys.put("l", new Integer(KeyEvent.VK_L));
        keys.put("m", new Integer(KeyEvent.VK_M));
        keys.put("n", new Integer(KeyEvent.VK_N));
        keys.put("o", new Integer(KeyEvent.VK_O));
        keys.put("p", new Integer(KeyEvent.VK_P));
        keys.put("q", new Integer(KeyEvent.VK_Q));
        keys.put("r", new Integer(KeyEvent.VK_R));
        keys.put("s", new Integer(KeyEvent.VK_S));
        keys.put("t", new Integer(KeyEvent.VK_T));
        keys.put("u", new Integer(KeyEvent.VK_U));
        keys.put("v", new Integer(KeyEvent.VK_V));
        keys.put("w", new Integer(KeyEvent.VK_W));
        keys.put("x", new Integer(KeyEvent.VK_X));
        keys.put("y", new Integer(KeyEvent.VK_Y));
        keys.put("z", new Integer(KeyEvent.VK_Z));
        keys.put("0", new Integer(KeyEvent.VK_0));
        keys.put("1", new Integer(KeyEvent.VK_1));
        keys.put("2", new Integer(KeyEvent.VK_2));
        keys.put("3", new Integer(KeyEvent.VK_3));
        keys.put("4", new Integer(KeyEvent.VK_4));
        keys.put("5", new Integer(KeyEvent.VK_5));
        keys.put("6", new Integer(KeyEvent.VK_6));
        keys.put("7", new Integer(KeyEvent.VK_7));
        keys.put("8", new Integer(KeyEvent.VK_8));
        keys.put("9", new Integer(KeyEvent.VK_9));
        keys.put("\\", new Integer(KeyEvent.VK_BACK_SLASH));
        keys.put("f1", new Integer(KeyEvent.VK_F1));
        keys.put("f2", new Integer(KeyEvent.VK_F2));
        keys.put("f3", new Integer(KeyEvent.VK_F3));
        keys.put("f4", new Integer(KeyEvent.VK_F4));
        keys.put("f5", new Integer(KeyEvent.VK_F5));
        keys.put("f6", new Integer(KeyEvent.VK_F6));
        keys.put("f7", new Integer(KeyEvent.VK_F7));
        keys.put("f8", new Integer(KeyEvent.VK_F8));
        keys.put("f9", new Integer(KeyEvent.VK_F9));
        keys.put("f10", new Integer(KeyEvent.VK_F10));
        keys.put("f11", new Integer(KeyEvent.VK_F11));
        keys.put("f12", new Integer(KeyEvent.VK_F12));
    }

    private KeyStroke keystroke = null;

    public ASMAccelerator(String key, String modifier1, String modifier2) {
        try {
            int k = ((Integer) keys.get(key.toLowerCase())).intValue();
            int mods = 0;
            mods = mods | getMod(modifier1);
            mods = mods | getMod(modifier2);
            keystroke = KeyStroke.getKeyStroke(k, mods);
        } catch (Exception e) {
            Global.logException(e, ASMAccelerator.class);
        }
    }

    public KeyStroke getKey() {
        return keystroke;
    }

    public int getMod(String modifier) {
        String mod = modifier.toLowerCase();

        if (mod.equals("ctrl")) {
            return InputEvent.CTRL_MASK;
        }

        if (mod.equals("shift")) {
            return InputEvent.SHIFT_MASK;
        }

        if (mod.equals("alt")) {
            return InputEvent.ALT_MASK;
        }

        return 0;
    }
}
