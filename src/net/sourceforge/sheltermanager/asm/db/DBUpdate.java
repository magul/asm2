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
package net.sourceforge.sheltermanager.asm.db;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.io.*;


/**
 * Handles updating of the database in a friendly way by
 * accepting special SQL scripts. The rule is one line must
 * constitute a complete command, and don't use any
 * terminating chars (eg: ; )
 *
 * Comments, starting with a hash (#) are allowed, as is
 * whitespace.
 *
 * @author  Robin Rawson-Tetley
 */
public class DBUpdate extends Thread {
    private String updateFile = "";

    /** Creates a new instance of DBUpdate and requests the update file */
    public DBUpdate() {
        UI.FileChooser chooser = UI.getFileChooser();
        chooser.setDialogTitle(Global.i18n("db",
                "Select_database_update_file_to_process:"));

        try {
            int returnVal = chooser.showOpenDialog(Global.mainForm);

            if (returnVal == UI.FileChooser.APPROVE_OPTION) {
                updateFile = chooser.getSelectedFile().getAbsolutePath();
                this.start();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void run() {
        try {
            File file = new File(updateFile);

            // Execute the updates
            DBConnection.executeFile(file);
            Dialog.showInformation(Global.i18n("db",
                    "Database_update_was_successful."), Global.i18n("db", "OK"));
            // Reload the lookup cache
            LookupCache.invalidate();
            LookupCache.fill();
        } catch (IOException e) {
            Dialog.showError(Global.i18n("db",
                    "An_error_occurred_reading_the_file:\n") + e.getMessage());
        } catch (Exception e) {
            Dialog.showError(Global.i18n("db", "An_unexpected_error_occurred:\n") +
                e.getMessage());
        }
    }
}
