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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.lang.reflect.Method;


/** Wraps up a reference to a callable function */
public class FunctionPointer {
    public Object o = null;
    public Method m = null;

    public FunctionPointer(Object instance, Method method) {
        o = instance;
        m = method;
    }

    public FunctionPointer(Object instance, String method) {
        o = instance;

        try {
            m = o.getClass().getMethod(method, (Class[]) null);
        } catch (Exception e) {
            Global.logException(e, FunctionPointer.class);
        }
    }

    @SuppressWarnings("unchecked")
    public FunctionPointer(Object instance, String method, Class[] args) {
        o = instance;

        try {
            m = o.getClass().getMethod(method, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object call() {
        try {
            return m.invoke(o, (Object[]) null);
        } catch (Exception e) {
            Global.logException(e, FunctionPointer.class);

            return null;
        }
    }

    public Object call(Object[] args) {
        try {
            return m.invoke(o, args);
        } catch (Exception e) {
            Global.logException(e, FunctionPointer.class);

            return null;
        }
    }
}
