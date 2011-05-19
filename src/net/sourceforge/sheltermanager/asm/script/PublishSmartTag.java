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
import net.sourceforge.sheltermanager.asm.internet.PublishCriteria;
import net.sourceforge.sheltermanager.asm.internet.SmartTagPublisher;


/**
 * This class wraps up the publishsmarttag command
 * and calls various bits of the system to
 * actually do it.
 *
 * Command: publishsmarttag
 * Options:
 *              forcereupload
 *              scaleimages=<1 to 5, where:
 *                              1 = No scaling
 *                              2 = 320x200
 *                              3 = 640x480
 *                              4 = 800x600
 *                              5 = 1024x768
 *
 *  As usual, arguments must be quoted to to use spaces in them.
 *  Example call:
 *
 *      asmcmd publishsmarttag scaleimages=1
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class PublishSmartTag {
    public PublishSmartTag(String[] args) {
        // Enable logging
        Global.setUsingLog(true);

        // Create our publish criteria
        PublishCriteria pc = new PublishCriteria();
        pc.uploadDirectly = true;

        // Set values from arguments
        if (args.length > 0) {
            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("forcereupload")) {
                    pc.forceReupload = true;
                } else if (args[i].toLowerCase()
                                      .startsWith("scaleimages" +
                            ScriptParser.equalsSymbol)) {
                    try {
                        pc.scaleImages = Integer.parseInt(args[i].substring(args[i].indexOf(
                                        ScriptParser.equalsSymbol) + 1));

                        if ((pc.scaleImages < 1) || (pc.scaleImages > 5)) {
                            Global.logError("Invalid 'scaleimages' value supplied.",
                                "PublishPets911.PublishPets911");
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        Global.logError("Invalid 'scaleimages' value supplied.",
                            "PublishPets911.PublishPets911");
                        System.exit(1);
                    }
                } else {
                    /*
                    ignore unrecognised
                    Global.logError("Unrecognised option: " + args[i],
                        "PublishSmartTag.PublishSmartTag");
                    System.exit(1);
                    */
                }
            }
        }

        // Publish on a single thread model for cmd line
        new SmartTagPublisher(null, pc).run();
        System.exit(0);
    }
}
