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
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.cursorengine.*;


public class BaseColour extends NormalBO {
    public BaseColour() {
        tableName = "basecolour";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getBaseColour() throws CursorEngineException {
        return (String) rs.getField("BaseColour");
    }

    public void setBaseColour(String newValue) throws CursorEngineException {
        rs.setField("BaseColour", newValue);
    }

    public String getBaseColourDescription() throws CursorEngineException {
        return (String) rs.getField("BaseColourDescription");
    }

    public void setBaseColourDescription(String newValue)
        throws CursorEngineException {
        rs.setField("BaseColourDescription", newValue);
    }
}
