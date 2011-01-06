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
package net.sourceforge.sheltermanager.asm.script;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.dbfs.DBFS;


/**
 * This class wraps the dbfsdelete
 * command, allowing delete of a file or directory
 * from the dbfs table by directory and name
 *
 * Command: getdbfsfile
 * Options: <path> <name>
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class DeleteDBFSFile {
    public DeleteDBFSFile(String[] args) {
        try {
            if (args.length < 3) {
                Global.setUsingLog(true);
                Global.logError("Command requires DBFS path and DBFS file/directory.",
                    "DeleteDBFSFile");
                System.exit(1);
            }

            String dir = args[1];
            String file = args[2];

            // Go to the directory given
            DBFS dbfs = new DBFS();
            dbfs.chdir(dir);

            if (dbfs.isDir(file)) {
                // Delete the directory
                dbfs.deleteDir(file);
            } else {
                // Delete the file
                dbfs.deleteFile(file);
            }

            dbfs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
            System.exit(1);
        }
    }
}
