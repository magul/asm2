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
package net.sourceforge.sheltermanager.asm.ui.internet;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMConfigForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;


/**
 * This class contains all code for editing 1-800-Save-A-Pet settings for
 * the publisher

 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class SaveAPetSettings extends ASMConfigForm {
    public SaveAPetSettings() {
        super(new ConfigItem[] {
                new ConfigItem(Global.i18n("uiinternet", "Host_URL:"),
                    "SaveAPetFTPURL", "autoupload.adoptapet.com"),
                
            new ConfigItem(Global.i18n("uiinternet", "Username:"),
                    "SaveAPetFTPUser"),
                
            new ConfigItem(Global.i18n("uiinternet", "Password:"),
                    "SaveAPetFTPPassword"),
            }, Global.i18n("uiinternet", "SaveAPet_Settings"),
            IconManager.getIcon(IconManager.SCREEN_SAVEAPETSETTINGS));
    }
}
