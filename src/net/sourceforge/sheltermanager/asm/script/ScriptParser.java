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


/**
 * This class actually parses the command sent at the command line and executes
 * it.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class ScriptParser {
    /**
     * Under windows, we can't use an equals to separate properties as batch
     * stuff doesn't allow it.
     */
    public static String equalsSymbol = (System.getProperty("os.name")
                                               .indexOf("Windows") == -1) ? "="
                                                                          : ":";

    public ScriptParser(String[] args) {
        // Make sure we have a command
        if (args.length == 0) {
            System.err.println(Startup.usage);
            System.exit(1);
        }

        // Determine what command it is to decide what
        // handler to use:

        // Publish to the web
        if (args[0].equalsIgnoreCase("publishwww")) {
            new PublishWWW(args);
        }
        // Publish to PetFinder
        else if (args[0].equalsIgnoreCase("publishpf")) {
            new PublishPF(args);
        }
        // Publish to 1-800-Save-A-Pet
        else if (args[0].equalsIgnoreCase("publish1800sap") ||
                args[0].equalsIgnoreCase("publishadoptapet")) {
            new PublishSaveAPet(args);
        }
        // Publish to Pets911
        else if (args[0].equalsIgnoreCase("publishpets911")) {
            new PublishPets911(args);
        }
        // Publish to RescueGroups
        else if (args[0].equalsIgnoreCase("publishrescuegroups")) {
            new PublishRescueGroups(args);
        }
        // Run a report by it's title
        else if (args[0].equalsIgnoreCase("runreport")) {
            new RunReport(args);
        }
        // Get a file by media id
        else if (args[0].equalsIgnoreCase("getmediafile")) {
            new GetMediaFile(args);
        }
        // Get a DBFS file/directory
        else if (args[0].equalsIgnoreCase("dbfsdownload")) {
            new GetDBFSFile(args);
        }
        // Store a DBFS file/directory
        else if (args[0].equalsIgnoreCase("dbfsupload")) {
            new PutDBFSFile(args);
        }
        // Delete a DBFS file/directory
        else if (args[0].equalsIgnoreCase("dbfsdelete")) {
            new DeleteDBFSFile(args);
        } else {
            System.err.println("Unrecognised command: " + args[0]);
        }
    }
}
