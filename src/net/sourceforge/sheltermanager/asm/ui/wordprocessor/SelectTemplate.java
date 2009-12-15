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
package net.sourceforge.sheltermanager.asm.ui.wordprocessor;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.DBFSBrowser;
import net.sourceforge.sheltermanager.asm.ui.ui.DBFSBrowserParent;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.utility.WordProcessorListener;
import net.sourceforge.sheltermanager.asm.wordprocessor.GenerateDocument;

import java.io.File;

import java.util.Vector;


/**
 * Handles selecting word processor templates from the FTP server.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class SelectTemplate extends ASMForm implements DBFSBrowserParent {
    /** The type of files we are dealing with */
    private String fileextension = "";

    /** The object waiting for confirmation a file has been chosen */
    WordProcessorListener listener = null;
    private UI.CheckBox chkAttach;
    private DBFSBrowser browser = null;

    /** Creates new form SelectTemplate */
    public SelectTemplate(WordProcessorListener thelistener) {
        listener = thelistener;

        // get the file extension we should be looking for
        GenerateDocument gd = (GenerateDocument) listener;
        fileextension = gd.getDocFileType();

        init(Global.i18n("uiwordprocessor", "Select_a_Document_Template"),
            IconManager.getIcon(IconManager.SCREEN_SELECTTEMPLATE),
            "uiwordprocessor");
    }

    public void dispose() {
        listener = null;
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

    public boolean isFileTypeCorrect(String filename) {
        return ((GenerateDocument) listener).isCorrectFileType(filename);
    }

    public void initComponents() {
        browser = new DBFSBrowser(this, "templates");
        chkAttach = UI.getCheckBox(i18n("attach_generated_document_as_media"));
        add(browser, UI.BorderLayout.CENTER);
        add(chkAttach, UI.BorderLayout.SOUTH);
        if (Configuration.getBoolean("AutoAttachMedia"))
            chkAttach.setSelected(true);
    }

    public boolean formClosing() {
        // Tell the caller that the template was abandoned so
        // it stops listening
        listener.templateAbandoned();

        return false;
    }

    public void selectionChanged(String selection, boolean isDir) {
    }

    public UI.ToolBar getToolBar() {
        return null;
    }

    public void selectionDoubleClicked(File selection) {
        // Rename the file to a new temporary file so it can be
        // worked on
        try {
            File f = Utils.getNewTempFile(fileextension);
            Utils.renameFile(selection, f);

            // Copy all the images out of the current directory to the 
            // temporary folder too
            browser.saveAllImages();

            // Generate the callback event to process the document
            listener.templateSelected(f, selection.getName(),
                chkAttach.isSelected());
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uiwordprocessor",
                    "An_error_occurred_retrieving_the_file:_") +
                e.getMessage());
            Global.logException(e, getClass());
        } finally {
            UI.cursorToPointer();
        }
    }
}
