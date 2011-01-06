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
package net.sourceforge.sheltermanager.asm.ui.diary;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.util.Vector;


/**
 * This class contains all code for viewing diary notes.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class DiaryView extends ASMForm {
    private DiarySelector diary = new DiarySelector(true);

    public DiaryView() {
        init(Global.i18n("uidiary", "View_Diary_Notes"),
            IconManager.getIcon(IconManager.SCREEN_VIEWDIARY), "uidiary");
        diary.updateList();
    }

    public void dispose() {
        super.dispose();
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean formClosing() {
        return false;
    }

    public Vector<Object> getTabOrder() {
        return diary.getTabOrder();
    }

    public Object getDefaultFocusedComponent() {
        return diary.getDefaultFocusedComponent();
    }

    public void setSecurity() {
    }

    public void refreshData() {
        diary.updateList();
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());
        add(diary, UI.BorderLayout.CENTER);
    }
}
