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
package net.sourceforge.sheltermanager.asm.script;

import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.dbfs.*;


/**
 * This class wraps the getmediafile
 * command, allowing retrieval of a piece
 * of media by it's ID in the media table
 * (is therefore handy for people writing
 * scripts that read data and need to retrieve
 * media) and saving it to the current directory.
 *
 * Command: getmediafile
 * Options: <id>
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class GetMediaFile {
    public GetMediaFile(String[] args) {
        try {
            if (args.length < 3) {
                Global.setUsingLog(true);
                Global.logError("No media ID specified.", "GetMediaFile");
                System.exit(1);
            }

            // Locate the media record
            Media m = new Media();
            m.openRecordset("ID = " + args[2]);

            if (m.getEOF()) {
                Global.setUsingLog(true);
                Global.logError("Media with ID " + args[2] + " doesn't exist.",
                    "GetMediaFile");
                System.exit(1);
            }

            // Go to the right directory for the link and save the file
            // to the current directory.
            DBFS dbfs = Utils.getDBFSDirectoryForLink(m.getLinkTypeID()
                                                       .intValue(),
                    m.getLinkID().intValue());
            dbfs.readFile(m.getMediaName(), m.getMediaName());
            dbfs = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
            System.exit(1);
        }
    }
}
