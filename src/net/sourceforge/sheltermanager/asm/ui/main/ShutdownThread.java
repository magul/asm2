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
package net.sourceforge.sheltermanager.asm.ui.main;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.startup.Startup;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;


/**
 * Separately threaded shutdown class for updating ASM denormalised data where
 * necessary.
 */
public class ShutdownThread extends Thread {
    public static boolean shuttingDown = false;

    public void run() {
        // Is this a CTRL+C ? If so, we need to forcibly terminate the VM
        boolean isCtrlC = Global.mainForm != null;

        // If the main form is still open (ie. This was a CTRL+C or a kill)
        // try and close the form gracefully before we kill the VM
        if (Global.mainForm != null) {
            Global.mainForm.dispose();
        }

        // Only do the shutdown sequence if it hasn't already
        // started.
        if (!shuttingDown) {
            shuttingDown = true;
        } else {
            return;
        }

        // Stop the log echoing anything to stdout to prevent stream deadlock
        // when System.exit is called
        // Global.echolog = false;

        // Update litter figures to make sure any that should be cancelled are.
        // We do this here because we do not want to affect system
        // startup times, however we want this checked at least once per day.
        try {
            // Checkpoint if supported before updating as cancelling during
            // modification of statuses seems to corrupt the record
            DBConnection.checkpoint();

            // Check for expiring litters
            AnimalLitter.updateLitters();

            // Auto archive animal records if it's enabled
            if (!Configuration.getBoolean("DontAutoArchiveOnExit")) {
                Animal.updateOnShelterAnimalStatuses();
            }

            // Checkpoint if supported
            DBConnection.checkpoint();

            // Close the DB
            DBConnection.close();

            // Clear any instance lock file
            Startup.clearLock();
        } catch (Exception e) {
            Global.logException(e, getClass());
        } finally {
            try {
                Global.logInfo("Graceful shutdown.", "ShutdownThread.Run");

                // Close the log
                Global.closeLog();
            } catch (Exception e) {
            }

            Startup.terminateVM(isCtrlC);
        }
    }
}
