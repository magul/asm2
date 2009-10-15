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
 *        $Log: SQLRowData.java,v $
 *        Revision 1.4  2006/03/03 14:17:32  bobintetley
 *        UI uses new typed business layer correctly now. Version bumped to 2.0.0,
 *        removed apostrophes around tokens in localised strings to fix MessageFormat
 *        bug. Added Postgres and HSQLDB drivers
 *
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
 * Data structure which is used to hold information about a row
 * of data within a recordset.
 *
 * @see net.sourceforge.sheltermanager.cursorengine.DBConnection
 * @see net.sourceforge.sheltermanager.cursorengine.SQLRowData
 * @see net.sourceforge.sheltermanager.cursorengine.SQLFieldDescriptor
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class SQLRowData {
    /** Array containing the data */
    public Object[] theRowData = null;

    /** Flag determining whether this row needs saving back to the data source */
    public boolean needsSaving = false;

    /** Flag determining whether this row is new and requires an INSERT statement */
    public boolean isNew = false;

    /** Flag determining whether this row has been deleted and needs to be removed
     * from the data source */
    public boolean isDeleted = false;

    public SQLRowData(int noCols) {
        theRowData = new Object[noCols];
    }
}
