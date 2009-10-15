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
package net.sourceforge.sheltermanager.asm.ui.internet;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMConfigForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;

import java.util.Vector;


/**
 * This class contains all code for editing PetFinder settings for the PetFinder
 * publisher
 *
 * @author Robin Rawson-Tetley
 */
public class PetFinderSettings extends ASMConfigForm {
    public PetFinderSettings() {
        super(new ConfigItem[] {
                new ConfigItem(Global.i18n("uiinternet", "Host_URL:"),
                    "PetFinderFTPURL", "members.petfinder.com"),
                
            new ConfigItem(Global.i18n("uiinternet", "ShelterId"),
                    "PetFinderFTPUser"),
                
            new ConfigItem(Global.i18n("uiinternet", "Password:"),
                    "PetFinderFTPPassword"),
            }, Global.i18n("uiinternet", "PetFinder_Settings"),
            IconManager.getIcon(IconManager.SCREEN_PETFINDERSETTINGS));
    }
}
