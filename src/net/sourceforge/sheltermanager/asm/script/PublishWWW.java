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
import net.sourceforge.sheltermanager.asm.internet.HTMLPublisher;
import net.sourceforge.sheltermanager.asm.internet.PublishCriteria;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Vector;


/**
 * This class wraps up the publishwww command
 * and calls various bits of the system to
 * actually do it.
 *
 * Command: publishwww
 * Options:
 *              includecase
 *              includereserved
 *              includefosters
 *              excludeunder=<weeks>
 *              animalsperpage=<number to show on a page>
 *              ftproot=<override stored ftp root>
 *              ftphost=<override stored ftp host>
 *              ftpuser=<override stored ftp user>
 *              ftppass=<override stored ftp pass>
 *              forcereupload
 *              uploaddirectly
 *              uploadall
 *              generatejavascriptdb
 *              thumbnails
 *              extension=<file extension - html is the default>
 *              style=<style dir or . for base>
 *              order=<0-2, where 0 = most recent asc, 1 = desc, 2 = name>
 *              limit=<0 or a number of records to limit animals to>
 *              scaleimages=<1 to 7, where:
 *                              1 = No scaling
 *                              2 = 320x200
 *                              3 = 640x480
 *                              4 = 800x600
 *                              5 = 1024x768
 *                              6 - 300x300
 *                              7 - 95x95
 *              includelocations=<comma separated list of location names>
 *
 *  As usual, arguments must be quoted to to use spaces in them.
 *  Example call:
 *
 *      asmcmd publishwww excludeunder=8 animalsperpage=10 uploaddirectly
 *              generatejavascriptdb scaleimages=2 "includelocations=Top Block"
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class PublishWWW {
    public PublishWWW(String[] args) {
        // Enable logging
        Global.setUsingLog(true);

        // Create our publish criteria
        PublishCriteria pc = new PublishCriteria();

        // Set values from arguments
        if (args.length > 0) {
            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("includecase")) {
                    pc.includeCase = true;
                } else if (args[i].equalsIgnoreCase("includereserved")) {
                    pc.includeReserved = true;
                } else if (args[i].equalsIgnoreCase("includefosters")) {
                    pc.includeFosters = true;
                } else if (args[i].equalsIgnoreCase("includewithoutimage")) {
                    pc.includeWithoutImage = true;
                } else if (args[i].equalsIgnoreCase("uploaddirectly")) {
                    pc.uploadDirectly = true;
                } else if (args[i].equalsIgnoreCase("uploadall")) {
                    pc.uploadAllImages = true;
                } else if (args[i].equalsIgnoreCase("forcereupload")) {
                    pc.forceReupload = true;
                } else if (args[i].equalsIgnoreCase("generatejavascriptdb")) {
                    pc.generateJavascriptDB = true;
                } else if (args[i].equalsIgnoreCase("thumbnails")) {
                    pc.thumbnails = true;
                } else if (Utils.englishLower(args[i])
                                    .startsWith("excludeunder" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.excludeUnderWeeks = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'excludeunder' value supplied.",
                            "PublishWWW.PublishWWW");
                        System.exit(1);
                    }
                } else if (Utils.englishLower(args[i])
                                    .startsWith("ftproot" +
                            ScriptParser.equalsSymbol)) {
                    pc.ftpRoot = args[i].substring(args[i].indexOf(
                                ScriptParser.equalsSymbol) + 1);
                } else if (Utils.englishLower(args[i])
                                    .startsWith("ftphost" +
                            ScriptParser.equalsSymbol)) {
                    pc.ftpHost = args[i].substring(args[i].indexOf(
                                ScriptParser.equalsSymbol) + 1);
                } else if (Utils.englishLower(args[i])
                                    .startsWith("ftpuser" +
                            ScriptParser.equalsSymbol)) {
                    pc.ftpUser = args[i].substring(args[i].indexOf(
                                ScriptParser.equalsSymbol) + 1);
                } else if (Utils.englishLower(args[i])
                                    .startsWith("ftppass" +
                            ScriptParser.equalsSymbol)) {
                    pc.ftpPass = args[i].substring(args[i].indexOf(
                                ScriptParser.equalsSymbol) + 1);
                } else if (Utils.englishLower(args[i])
                                    .startsWith("animalsperpage" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.animalsPerPage = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'animalsperpage' value supplied.",
                            "PublishWWW.PublishWWW");
                        System.exit(1);
                    }
                } else if (Utils.englishLower(args[i])
                                    .startsWith("extension" +
                            ScriptParser.equalsSymbol)) {
                    pc.extension = args[i].substring(args[i].indexOf(
                                ScriptParser.equalsSymbol) + 1);
                } else if (Utils.englishLower(args[i])
                                    .startsWith("style" +
                            ScriptParser.equalsSymbol)) {
                    pc.style = args[i].substring(args[i].indexOf(
                                ScriptParser.equalsSymbol) + 1);
                } else if (Utils.englishLower(args[i])
                                    .startsWith("scaleimages" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.scaleImages = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));

                        if ((pc.scaleImages < 1) || (pc.scaleImages > 5)) {
                            Global.logError("Invalid 'scaleimages' value supplied.",
                                "PublishWWW.PublishWWW");
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'scaleimages' value supplied.",
                            "PublishWWW.PublishWWW");
                        System.exit(1);
                    }
                } else if (Utils.englishLower(args[i])
                                    .startsWith("order" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.order = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));

                        if ((pc.order < 1) || (pc.limit > 2)) {
                            Global.logError("Invalid 'order' value supplied.",
                                "PublishWWW.PublishWWW");
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'order' value supplied.",
                            "PublishWWW.PublishWWW");
                        System.exit(1);
                    }
                } else if (Utils.englishLower(args[i])
                                    .startsWith("limit" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.limit = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));

                        if ((pc.limit < 0)) {
                            Global.logError("Invalid 'limit' value supplied.",
                                "PublishWWW.PublishWWW");
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'limit' value supplied.",
                            "PublishWWW.PublishWWW");
                        System.exit(1);
                    }
                } else if (Utils.englishLower(args[i])
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
                                    locnames[z] + "'", "PublishWWW.PublishWWW");
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
                            "PublishWWW.PublishWWW");
                        System.exit(1);
                    }
                } else {
                    /*
                    ignore unrecognised
                    Global.logError("Unrecognised option: " + args[i],
                        "PublishWWW.PublishWWW");
                    System.exit(1);
                    */
                }
            }
        }

        // Publish on a single thread model for cmd line
        new HTMLPublisher(null, pc).run();

        // Once it's complete, shutdown the VM
        System.exit(0);
    }
}
