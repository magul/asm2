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

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;


/**
 *
 * Reverts the database back to the last production
 * release from any beta.
 *
 * @author  Robin Rawson-Tetley
 */
public abstract class BackToProduction {
    public static String lastProduction = "2.0.21 STABLE";

    public static void revert() {
        try {
            Dialog.showError(
                "Due to security updates, it is not possible to downgrade this release.");

            if (false) {
                if (!Dialog.showYesNo(
                            "This will return your database back to version " +
                            lastProduction +
                            ". Are you sure you wish to do this?", "Revert?")) {
                    return;
                }

                backTo2021();

                Dialog.showInformation(
                    "ASM will now close. You should now put version " +
                    lastProduction + " back onto your machine.", "Downgrade Now");

                System.exit(0);
            }
        } catch (Exception e) {
            Global.logException(e, BackToProduction.class);
        }
    }

    private static void backTo2021() {
        try {
            try {
                //String sql = "ALTER TABLE animal DROP COLUMN MostRecentEntryDate";
                //DBConnection.executeAction(sql);
                //sql = "DROP TABLE dbfs";
                //DBConnection.executeAction(sql);
                //sql = "ALTER TABLE ownerdonation DROP COLUMN MovementId";
                //DBConnection.executeAction(sql);
            } catch (Exception e) {
                Global.logException(e, BackToProduction.class);
            }

            Configuration.setEntry("DatabaseVersion", "2021");
        } catch (Exception e) {
            Global.logException(e, BackToProduction.class);
        }
    }
}
