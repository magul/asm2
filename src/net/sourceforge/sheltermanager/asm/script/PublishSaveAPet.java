/*
  Animal Shelter Manager
  Copyright(c)2000-2011, R. Rawson-Tetley

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
import net.sourceforge.sheltermanager.asm.internet.AdoptAPetPublisher;
import net.sourceforge.sheltermanager.asm.internet.PublishCriteria;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * This class wraps up the publish1800sap command
 * and calls various bits of the system to
 * actually do it.
 *
 * Command: publish1800sap/publishadoptapet
 * Options:
 *              includecase
 *              includereserve
 *              includefosters
 *              includecolors
 *              forcereupload
 *              noimportfile
 *              excludeunder=<weeks>
 *              scaleimages=<1 to 5, where:
 *                              1 = No scaling
 *                              2 = 320x200
 *                              3 = 640x480
 *                              4 = 800x600
 *                              5 = 1024x768
 *              includelocations=<comma separated list of location names>
 *
 *  As usual, arguments must be quoted to to use spaces in them.
 *  Example call:
 *
 *      asmcmd publish1800sap excludeunder=8 generatejavascriptdb
 *            scaleimages=2 "includelocations=Top Block"
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class PublishSaveAPet {
    public PublishSaveAPet(String[] args) {
        // Enable logging
        Global.setUsingLog(true);

        // Create our publish criteria
        PublishCriteria pc = new PublishCriteria();
        pc.uploadDirectly = true;

        // Set values from arguments
        if (args.length > 0) {
            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("includecase")) {
                    pc.includeCase = true;
                } else if (args[i].equalsIgnoreCase("includereserved")) {
                    pc.includeReserved = true;
                } else if (args[i].equalsIgnoreCase("includefosters")) {
                    pc.includeFosters = true;
                } else if (args[i].equalsIgnoreCase("includecolors")) {
                    pc.includeColours = true;
                } else if (args[i].equalsIgnoreCase("noimportfile")) {
                    pc.noImportFile = true;
                } else if (args[i].equalsIgnoreCase("includewithoutimage")) {
                    pc.includeWithoutImage = true;
                } else if (args[i].equalsIgnoreCase("forcereupload")) {
                    pc.forceReupload = true;
                } else if (args[i].toLowerCase()
                                      .startsWith("excludeunder" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.excludeUnderWeeks = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'excludeunder' value supplied.",
                            "PublishSaveAPet.PublishSaveAPet");
                        System.exit(1);
                    }
                } else if (args[i].toLowerCase()
                                      .startsWith("scaleimages" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.scaleImages = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));

                        if ((pc.scaleImages < 1) || (pc.scaleImages > 5)) {
                            Global.logError("Invalid 'scaleimages' value supplied.",
                                "PublishSaveAPet.PublishSaveAPet");
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'scaleimages' value supplied.",
                            "PublishSaveAPet.PublishSaveAPet");
                        System.exit(1);
                    }
                } else if (args[i].toLowerCase()
                                      .startsWith("includelocations" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        String locs = args[i].substring(args[i].indexOf(
                                    ScriptParser.equalsSymbol) + 1)
                                             .replace('*', '%');
                        String[] locnames = Utils.split(locs, ",");

                        Vector<Integer> locations = new Vector<Integer>();
                        SQLRecordset il = new SQLRecordset();

                        for (int z = 0; z < locnames.length; z++) {
                            il.openRecordset(
                                "SELECT * FROM internallocation WHERE LocationName Like '" +
                                locnames[z] + "%'", "internallocation");

                            if (il.getEOF()) {
                                Global.logError(
                                    "Could not find a location matching '" +
                                    locnames[z] + "'",
                                    "PublishSaveAPet.PublishSaveAPet");
                                System.exit(1);
                            }

                            while (!il.getEOF()) {
                                locations.add((Integer) il.getField("ID"));
                                il.moveNext();
                            }
                        }

                        pc.internalLocations = locations.toArray();
                        locations.removeAllElements();
                        locations = null;
                        il.free();
                        il = null;
                    } catch (Exception e) {
                        Global.logError("Invalid 'includelocations' value supplied.",
                            "PublishSaveAPet.PublishSaveAPet");
                        System.exit(1);
                    }
                } else {
                    /*
                    ignore unrecognised
                    Global.logError("Unrecognised option: " + args[i],
                        "PublishSaveAPet.PublishSaveAPet");
                    System.exit(1);
                    */
                }
            }
        }

        // Publish on a single thread model for cmd line
        new AdoptAPetPublisher(null, pc).run();
    }
}
