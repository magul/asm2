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
import net.sourceforge.sheltermanager.dbfs.*;

import java.io.File;


/**
 * This class wraps the dbfsupload
 * command, allowing storage of a file or directory
 * in the dbfs table
 *
 * Command: putdbfsfile
 * Options: <dbfspath> <filepath>
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class PutDBFSFile {
    public PutDBFSFile(String[] args) {
        try {
            if (args.length < 4) {
                Global.setUsingLog(true);
                Global.logError("Command requires DBFS path and path to file/directory.",
                    "PutDBFSFile");
                System.exit(1);
            }

            String dir = args[2];
            String file = args[3];

            // Go to the directory given
            DBFS dbfs = new DBFS();
            dbfs.chdir(dir);

            // Store the given file in the directory. If it's a directory
            // store the whole directory
            File f = new File(file);

            if (f.isDirectory()) {
                dbfs.importDirectory(f);
            } else {
                dbfs.putFile(f);
            }

            dbfs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
            System.exit(1);
        }
    }
}
