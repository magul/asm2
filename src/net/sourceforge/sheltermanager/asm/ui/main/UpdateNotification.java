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
package net.sourceforge.sheltermanager.asm.ui.main;

import net.sourceforge.sheltermanager.asm.db.AutoDBUpdates;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.util.Vector;


/**
 * Update notifications from the website - designed to be run
 * by a separate thread
 */
public class UpdateNotification extends Thread {
    public void UpdateNotification() {
    }

    public void run() {
        // Grab the list of updates from the website
        String updates = "";

        try {
            updates = Utils.getURL("http://sheltermanager.sf.net/updates.txt");
        } catch (Exception e) {
            Global.logException(e, getClass());

            return;
        }

        // Split them up and make a list
        Vector v = new Vector();

        String[] messages = Utils.split(updates, "\\");

        for (int i = 0; i < messages.length; i++) {
            Global.logDebug("Parsing update: " + messages[i],
                "UpdateNotification.run");

            // Skip the line if it's a commented out
            if (messages[i].trim().startsWith("#")) {
                Global.logDebug("Skipping commented line.",
                    "UpdateNotification.run");

                continue;
            }

            String[] m = Utils.split(messages[i].trim(), "|");

            UpdateEntry e = new UpdateEntry();
            e.id = Integer.parseInt(m[0].trim());
            e.date = m[1].trim();
            e.version = m[2].trim();
            e.text = m[3].trim();
            e.urltext = m[4].trim();
            e.url = m[5].trim();

            if (e.version.equals(Global.version.trim())) {
                v.add(e);
            }
        }

        if (v.size() > 0) {
            UpdateDialog d = new UpdateDialog(v);
            d.show();
        }
    }
}


class UpdateEntry {
    public int id;
    public String date;
    public String version;
    public String text;
    public String urltext;
    public String url;
}
