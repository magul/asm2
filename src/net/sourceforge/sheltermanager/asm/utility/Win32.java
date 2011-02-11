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
package net.sourceforge.sheltermanager.asm.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Windows specific library functions
 */
public abstract class Win32 {
    /**
     *  Creates a shortcut on the desktop with the given command,
     *  name and icon. Uses the Windows Scripting Host and writes
     *  a file which it then executes with it.
     *
     * @param commandLine The file to execute
     * @param args The arguments to supply to the file (if any)
     * @param name The name of the shortcut - as it appears on screen, no .lnk
     * @param iconFile The path to the file containing the icon to use.
     * @param batchFile The backup batch file to create (this is in case the shortcut fails)
     * @throws Exception if an error occurs.
     */
    public static void createDesktopShortcut(String commandLine, String args,
        String name, String iconFile, String batchFile)
        throws Exception {
        // Get path to Windows desktop
        String desktop = System.getProperty("user.home") + "\\desktop\\";
        String shortcutName = desktop + name + ".lnk";

        // Make sure a link with this name doesn't
        // already exist
        try {
            File f = new File(shortcutName);

            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
        }

        // If we have any quotation marks in our arguments, then
        // convert them so shitty VB can handle it
        String originalArgs = args;

        if (args.indexOf("\"") != -1) {
            args = replaceQuotes(args);
        }

        String crlf = new String(new byte[] { 13, 10 });
        String vbs = "Dim WSHShell" + crlf +
            "Set WSHShell = WScript.CreateObject(\"WScript.Shell\")" + crlf +
            "Dim Conjuring" + crlf +
            "Set Conjuring = WSHShell.CreateShortcut(\"" + shortcutName +
            "\")" + crlf +
            "Conjuring.TargetPath = WSHShell.ExpandEnvironmentStrings(\"" +
            commandLine + "\")" + crlf +
            "Conjuring.Arguments = WSHShell.ExpandEnvironmentStrings(\"" +
            args + "\")" + crlf + "Conjuring.WindowStyle = 4" + crlf +
            "Conjuring.IconLocation = WSHShell.ExpandEnvironmentStrings(\"" +
            iconFile + ", 0\")" + crlf + "Conjuring.Save";

        // Save this file in the user's home directory as
        // a .vbs file to create the shortcut
        String uhome = System.getProperty("user.home");
        String tempvbs = uhome + "\\cs.vbs";
        File f = new File(tempvbs);
        FileOutputStream out = new FileOutputStream(f);
        out.write(vbs.getBytes());
        out.flush();
        out.close();

        // Now execute the VBS with the Windows Scripting Host
        Runtime.getRuntime().exec("wscript.exe \"" + tempvbs + "\"");

        // Give it 5 seconds to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        // Create a batch file 
        try {
            String batch = "\"" + commandLine + "\" " + originalArgs;
            File fb = new File(batchFile);
            FileOutputStream fout = new FileOutputStream(fb);
            fout.write(batch.getBytes());
            fout.flush();
            fout.close();
        } catch (IOException e) {
        }

        // Delete the temporary VBS file once it's finished
        f.delete();
    }

    /** Looks in findin for all occurrences of find and replaces them with replacewith
     * @param findin The string to find occurrences in
     * @param find The string to find
     * @param replacewith The string to replace found occurrences with
     * @return A string with all occurrences of find replaced.
     */
    private static String replaceQuotes(String findin) {
        String out = "";
        int i = 0;

        try {
            while (i < findin.length()) {
                if (findin.substring(i, i + 1).equals("\"")) {
                    out += "\" & Chr(34) & \"";
                } else {
                    out += findin.substring(i, i + 1);
                }

                i++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            // We hit the end of the string - do nothing and carry on
        }

        return out;
    }
}
