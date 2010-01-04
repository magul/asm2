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
package net.sourceforge.sheltermanager.asm.ui.ui;

import java.util.Vector;


public class TableData extends Vector {
    public void add(TableRow r) {
        super.add(r);
    }

    public String[][] toTableData() {
        if (size() == 0) {
            return new String[0][0];
        }

        TableRow r = (TableRow) get(0);

        if (r == null) {
            return new String[0][0];
        }

        String[][] rv = new String[size()][];

        for (int i = 0; i < size(); i++) {
            r = (TableRow) get(i);
            rv[i] = r.toArray();
        }

        return rv;
    }
}
