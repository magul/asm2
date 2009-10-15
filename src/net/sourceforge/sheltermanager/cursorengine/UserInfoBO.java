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
 *        $Log: UserInfoBO.java,v $
 *        Revision 1.3  2004/12/08 08:39:44  bobintetley
 *        Editing diary task allows viewing of the notes after creation, fix to bug
 *        that meant horizontal scrollbars appeared on the ownervet box, good with
 *        cats/kids/dogs/housetrained are now tri-state, cloning an animal now
 *        clones it's movements as well
 *
 *        Revision 1.2  2003/07/07 07:59:17  bobintetley
 *        Feature request added to enforce coding schemes for manually entered codes.
 *        Also, discovered a bug in the cursor engine that could cause lock errors
 *        when validation failed.
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
 * A user info business object superclass. All business objects
 * which require stampable user info should extend this class.
 * Note that this class neatly extends the NormalBO class and
 * overloads the save method to allow the stampable user info.
 */
public abstract class UserInfoBO extends NormalBO {
    public void save(String currentUserName) throws CursorEngineException {
        try {
            validate();

            if (optimisticOk()) {
                rs.save(true, currentUserName);
            } else {
                throw new CursorEngineException(
                    "\nLOCKERR: Somebody changed this record while you were making your\nalterations.You must close and reopen the record,\nthen reapply your changes.");
            }
        } catch (Exception e) {
            throw new CursorEngineException(e.getMessage());
        }
    }
}
