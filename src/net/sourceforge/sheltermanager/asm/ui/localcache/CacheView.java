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
package net.sourceforge.sheltermanager.asm.ui.localcache;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMView;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.io.File;

import java.util.Iterator;
import java.util.Vector;


/**
 *
 * Allows viewing and destruction of the local cache.
 *
 */
public class CacheView extends ASMView {
    private UI.Button btnDelete;
    private UI.Button btnPurge;
    private UI.Button btnView;

    /** Creates new form ViewCache */
    public CacheView() {
        init(Global.i18n("uilocalcache", "View_Local_Cache"),
            IconManager.getIcon(IconManager.SCREEN_VIEWCACHE), "uilocalcache");
        updateList();
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(getTable());

        return v;
    }

    public Object getDefaultFocusedComponent() {
        return btnView;
    }

    public String getAuditInfo() {
        return null;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public boolean formClosing() {
        return false;
    }

    public boolean hasData() {
        return getTable().getRowCount() > 0;
    }

    public void setLink(int x, int y) {
    }

    public void setSecurity() {
    }

    public void updateList() {
        Vector cache = Global.localCache.getEntries();
        Iterator i = cache.iterator();

        int rows = cache.toArray().length;

        String[][] temp = new String[rows][3];
        String[] columnheaders = {
                i18n("Date/Time"), i18n("Filename"), i18n("Description")
            };

        int z = 0;

        while (i.hasNext()) {
            CacheEntry c = (CacheEntry) i.next();

            if (!c.datetime.equals("")) {
                temp[z][0] = c.datetime;
                temp[z][1] = c.filename;
                temp[z][2] = c.description;
                z++;
            }
        }

        // Make a new copy of the data, only reversed
        String[][] data = new String[z][3];
        int x = z - 1;
        z = 0;

        while (x >= 0) {
            data[z][0] = temp[x][0];
            data[z][1] = temp[x][1];
            data[z][2] = temp[x][2];
            x--;
            z++;
        }

        setTableData(columnheaders, data, z, 2);
    }

    public void addToolButtons() {
        btnView = UI.getButton(null, i18n("View_this_file"), 'e',
                IconManager.getIcon(IconManager.SCREEN_VIEWCACHE_VIEW),
                UI.fp(this, "actionView"));
        addToolButton(btnView, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_file"), 'd',
                IconManager.getIcon(IconManager.SCREEN_VIEWCACHE_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnPurge = UI.getButton(null, i18n("Delete_all_files"), 'k',
                IconManager.getIcon(IconManager.SCREEN_VIEWCACHE_PURGE),
                UI.fp(this, "actionPurge"));
        addToolButton(btnPurge, false);
    }

    public void tableClicked() {
    }

    public void tableDoubleClicked() {
        actionView();
    }

    public void actionPurge() {
        Global.localCache.removeAllEntries();
        updateList();
    }

    public void actionDelete() {
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        String filename = (String) getTable().getModel()
                                       .getValueAt(getTable().getSelectedRow(),
                1);
        Global.localCache.removeEntry(filename);
        updateList();
    }

    public void actionView() {
        // Make sure a row is selected
        if (getTable().getSelectedRow() == -1) {
            return;
        }

        // Get the filename
        String mediaName = (String) getTable().getModel()
                                        .getValueAt(getTable().getSelectedRow(),
                1);

        FileTypeManager.shellExecute(Global.tempDirectory + File.separator +
            mediaName, false);
    }
}
