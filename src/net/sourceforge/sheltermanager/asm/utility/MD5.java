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
package net.sourceforge.sheltermanager.asm.utility;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.math.BigInteger;

import java.security.MessageDigest;


public abstract class MD5 {
    /**
     * Returns an MD5 hash of a given string. If an exception
     * occurs, it is logged and null is returned.
     * @param target The string to hash
     * @return An MD5 hash of the string
     */
    public static String hash(String target) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(target.getBytes(), 0, target.length());

            return new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {
            Global.logException(e, MD5.class);
        }

        return null;
    }
}
