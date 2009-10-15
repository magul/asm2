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
package net.sourceforge.sheltermanager.asm.ui.system;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DBFSBrowser;
import net.sourceforge.sheltermanager.asm.ui.ui.DBFSBrowserParent;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.io.File;

import java.util.Vector;


/**
 * Allows navigation around the DBFS.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class MediaFiles extends ASMForm implements DBFSBrowserParent {
    private UI.ToolBar tb;
    private UI.Button btnUpload;
    private UI.Button btnDownload;
    private UI.Button btnMakeDir;
    private UI.Button btnDelete;
    private UI.Button btnEdit;
    private UI.Button btnExportDir;
    private UI.Button btnImportDir;
    private UI.Button btnImportFromFS;
    private DBFSBrowser browser;

    public MediaFiles() {
        init(Global.i18n("uisystem", "media_files"),
            IconManager.getIcon(IconManager.SCREEN_MEDIAFILES), "uisystem");
    }

    public void dispose() {
        browser.dispose();
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(browser);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return browser;
    }

    public void selectionChanged(String selection, boolean isDir) {
        boolean somethingSelected = selection != null;
        boolean fileSelected = !isDir;

        // Count the .. dir as no selection
        if ((selection != null) && selection.trim().equals("..") && isDir) {
            somethingSelected = false;
            isDir = false;
        }

        btnDownload.setEnabled(somethingSelected && fileSelected);
        btnUpload.setEnabled(true);
        btnImportDir.setEnabled(true);
        btnExportDir.setEnabled(somethingSelected && isDir);
        btnMakeDir.setEnabled(true);
        btnDelete.setEnabled(somethingSelected);
        btnEdit.setEnabled(somethingSelected && fileSelected);

        // btnImportFromFS.setEnabled(true);
    }

    /** Shouldn't be called */
    public void selectionDoubleClicked(File selection) {
    }

    public boolean isFileTypeCorrect(String name) {
        return true;
    }

    public UI.ToolBar getToolBar() {
        return tb;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public void initComponents() {
        tb = UI.getToolBar();

        btnDownload = (UI.Button) tb.add(UI.getButton(null, "Download", 'd',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_DOWNLOAD),
                    UI.fp(this, "actionDownload")));

        btnUpload = (UI.Button) tb.add(UI.getButton(null, "Upload", 'u',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_UPLOAD),
                    UI.fp(this, "actionUpload")));

        btnEdit = (UI.Button) tb.add(UI.getButton(null, "Edit", 'e',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_EDIT),
                    UI.fp(this, "actionEdit")));

        btnDelete = (UI.Button) tb.add(UI.getButton(null, "Delete", 'x',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_DELETE),
                    UI.fp(this, "actionDelete")));

        btnMakeDir = (UI.Button) tb.add(UI.getButton(null, "New Dir", 'n',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_NEWDIR),
                    UI.fp(this, "actionMkdir")));

        btnImportDir = (UI.Button) tb.add(UI.getButton(null,
                    "Import directory from filesystem here", 'o',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_IMPORTDIR),
                    UI.fp(this, "actionImportDir")));

        btnExportDir = (UI.Button) tb.add(UI.getButton(null,
                    "Export this directory to the filesystem", 'p',
                    IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_EXPORTDIR),
                    UI.fp(this, "actionExportDir")));

        /* Too dangerous
        btnImportFromFS = (UI.Button) tb.add(UI.getButton(null, "Import From FileSystem", 'f',
               IconManager.getIcon(IconManager.SCREEN_MEDIAFILES_IMPORTFROMFS),
               UI.fp(this, "actioImportFromFS")));
         */
        browser = new DBFSBrowser(this);
        browser.mapDoubleClickToCheckout();

        add(tb, UI.BorderLayout.NORTH);
        add(browser, UI.BorderLayout.CENTER);
    }

    public boolean formClosing() {
        return false;
    }

    public void actionDownload() {
        browser.download();
    }

    public void actionUpload() {
        browser.upload();
    }

    public void actionEdit() {
        browser.checkOut();
    }

    public void actionDelete() {
        browser.delete();
    }

    public void actionMkdir() {
        browser.mkdir();
    }

    public void actionImportDir() {
        browser.importDir();
    }

    public void actionExportDir() {
        browser.exportDir();
    }
}
