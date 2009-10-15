/**
 *
 *  SQLRecordset Disconnected Recordset functionality
 *  and client-side cursor engine.
 *
 *  Copyright (C) 2002  Robin Rawson-Tetley
 *
 *  www.rawsontetley.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *
 *  <robin@rawsontetley.org>
 *
 *
 *  Change Log:
 *
 *        $Log: SQLFieldDescriptor.java,v $
 *        Revision 1.3  2006/03/01 16:50:11  bobintetley
 *        Typed database handling and Animal/Adoption done
 *
 *        Revision 1.2  2004/12/08 08:39:44  bobintetley
 *        Editing diary task allows viewing of the notes after creation, fix to bug
 *        that meant horizontal scrollbars appeared on the ownervet box, good with
 *        cats/kids/dogs/housetrained are now tri-state, cloning an animal now
 *        clones it's movements as well
 *
 *        Revision 1.1.1.1  2003/06/03 06:54:28  bobintetley
 *        Initial
 *
 *        Revision 1.1.1.1  2002/11/10 19:33:07  robin
 *
 *
 *
 *        Revision 1.0  2002/05/11 17:18:03  robinrt
 *        Initial Release
 */
package net.sourceforge.sheltermanager.cursorengine;


/**
 * Data structure used to hold information about
 * a recordset field.
 *
 * @see net.sourceforge.sheltermanager.cursorengine.DBConnection
 * @see net.sourceforge.sheltermanager.cursorengine.SQLRowData
 * @see net.sourceforge.sheltermanager.cursorengine.SQLFieldDescriptor
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class SQLFieldDescriptor {
    /** Constant representing a string type */
    public static final byte SQLFIELDTYPE_STRING = 0;

    /** Constant representing an integer type */
    public static final byte SQLFIELDTYPE_INTEGER = 1;

    /** Constant representing a floating point type */
    public static final byte SQLFIELDTYPE_FLOAT = 2;

    /** Constant representing a date type */
    public static final byte SQLFIELDTYPE_DATE = 3;

    /** The field name */
    public String name = "";

    /** The field type, expressed as one of the SQLFIELDTYPE constants */
    public byte type = 0; // Use one of the constants above
}
