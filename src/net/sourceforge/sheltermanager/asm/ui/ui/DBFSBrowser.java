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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;


/**
 * Component to handle browsing the DBFS
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class DBFSBrowser extends JPanel implements MouseListener {
    final static String DIR = " <dir>";
    private DBFSBrowserParent parent = null;
    private DBFS dbfs = null;
    private boolean doubleClickCheckout = false;
    private JList lst = null;
    private JScrollPane scr = null;

    public DBFSBrowser(DBFSBrowserParent parent) {
        this(parent, null);
    }

    public DBFSBrowser(DBFSBrowserParent parent, String startDirectory) {
        this.parent = parent;

        lst = new JList();

        if (!UI.isLTR()) {
            lst.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        scr = new JScrollPane(lst);
        setLayout(UI.getBorderLayout());
        add(scr, UI.BorderLayout.CENTER);
        lst.addMouseListener(this);

        lst.setCellRenderer(new IconCellRenderer());
        dbfs = new DBFS();

        try {
            if (startDirectory != null) {
                dbfs.chdir(startDirectory);
            }
        } catch (Exception e) {
            Global.logException(e, DBFSBrowser.class);
        }

        displayDirectory();
    }

    public DBFS getDBFS() {
        return dbfs;
    }

    public void displayDirectory() {
        try {
            // Get a full directory listing
            String[] entries = dbfs.list();

            // Read each one and make an entry in the list
            Vector<String> en = new Vector<String>();

            // Add the ".." directory
            en.add(DIR + " ..");

            for (int i = 0; i < entries.length; i++) {
                // See if it is a directory
                if (dbfs.isDir(entries[i])) {
                    en.add(DIR + " " + entries[i]);
                } else {
                    // File type ok?
                    if (parent.isFileTypeCorrect(entries[i])) {
                        en.add(entries[i]);
                    }
                }
            }

            // Now sort the list
            Utils.sortVectorOfStrings(en);

            lst.setListData(en);

            // Update the buttons (no selection after change)
            parent.selectionChanged(null, false);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void mouseClicked(MouseEvent evt) {
        // See if it is a directory or a file they clicked
        String selitem = null;
        boolean isDir = false;

        if (lst.getSelectedIndex() != -1) {
            selitem = (String) lst.getSelectedValue();

            // is it a directory?
            if (selitem.startsWith(DIR)) {
                selitem = selitem.substring(DIR.length());
                isDir = true;
            }
        }

        // Sort out the button clicks

        // if it's a right click or a single click, update
        // the selection with the caller
        if ((evt.getButton() == 3) || (evt.getClickCount() < 2)) {
            parent.selectionChanged(selitem, isDir);
        }

        // If it's a right-click, show the context menu and finish
        if (evt.getButton() == 3) {
            UI.ToolBar t = parent.getToolBar();

            if (t != null) {
                UI.toolbarToPopupMenu(t).show(this, 0, 0);
            }

            return;
        }

        // If it wasn't a double click, finish now as the rest of the
        // processing here is for double-clicks
        if (evt.getClickCount() != 2) {
            return;
        }

        // Could take a sec
        UI.cursorToWait();

        // Was a directory selected? If so, change into it
        if (isDir) {
            try {
                dbfs.chdir(selitem);
                displayDirectory();
                // Change to normal
                UI.cursorToPointer();

                return;
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                UI.cursorToPointer();

                return;
            }
        } else {
            // It's got to be a file - if double click is mapped to check out, do
            // that now instead
            if (doubleClickCheckout) {
                checkOut();

                return;
            }

            // Download the file to the temp area for the caller and fire the
            // selection event with a reference to the file
            String filename = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                File.separator + selitem;

            // Does it exist? If so, delete the local copy first
            File file = new File(filename);

            if (file.exists()) {
                file.delete();
            }

            try {
                // Download the file
                dbfs.readFile(selitem, filename);

                // Tell the caller to handle it
                parent.selectionDoubleClicked(file);
            } catch (Exception e) {
                Dialog.showError(e.getMessage());
                Global.logException(e, getClass());
            } finally {
                UI.cursorToPointer();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void download() {
        try {
            if (lst.getSelectedIndex() == -1) {
                return;
            }

            String selitem = (String) lst.getSelectedValue();

            // Prompt for save as
            String defaultFile = Utils.getDefaultDocumentPath() +
                File.separator + selitem;
            Global.logDebug("Default download file: " + defaultFile,
                "MediaFiles.download");

            UI.FileChooser jf = new UI.FileChooser();
            jf.setSelectedFile(new File(defaultFile));

            if (jf.showSaveDialog(Global.mainForm) == UI.FileChooser.APPROVE_OPTION) {
                dbfs.readFile(selitem, jf.getSelectedFile().getAbsolutePath());
                displayDirectory();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** Saves all images in the on-screen folder to the temp directory */
    public void saveAllImages() {
        dbfs.saveAllImages(Global.tempDirectory);
    }

    public void upload() {
        try {
            // Browse for file to upload
            UI.FileChooser jf = new UI.FileChooser();

            if (jf.showOpenDialog(Global.mainForm) == UI.FileChooser.APPROVE_OPTION) {
                dbfs.putFile(jf.getSelectedFile());
                displayDirectory();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void dispose() {
    }

    public void mkdir() {
        try {
            // Ask for a name
            String newname = Dialog.getInput("New Directory name:",
                    "New Directory");

            if ((newname == null) || newname.equals("")) {
                return;
            }

            dbfs.mkdir(newname);
            displayDirectory();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void checkOut() {
        // Make sure a row is selected
        if (lst.getSelectedIndex() == -1) {
            return;
        }

        // Get the filename
        String mediaName = lst.getSelectedValue().toString();

        // Get the local temp directory
        String tempdir = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
            File.separator;

        // Make sure the file isn't already in the temp directory so
        // we have the latest version
        try {
            File f = new File(tempdir + mediaName);
            f.delete();
        } catch (Exception e) {
        }

        // Download the remote file from the DBFS repository
        try {
            dbfs.readFile(mediaName, tempdir + mediaName);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
            UI.cursorToPointer();

            return;
        }

        // Open the file with it's associated app
        if (0 != FileTypeManager.shellExecute(tempdir + mediaName)) {
            UI.cursorToPointer();

            return;
        }

        // Tell the user to hit Ok when they've finished making changes
        // and closed their application
        Dialog.showInformation(Global.i18n("uianimal", "hit_ok_when_finished"));

        // Now, ask the user if they'd like to upload their changes -
        // bomb out if they say no.
        if (!Dialog.showYesNo(Global.i18n("uianimal", "upload_back_to_server"),
                    Global.i18n("uianimal", "Unsaved_Changes"))) {
            UI.cursorToPointer();

            return;
        }

        // Upload the temp file to the DBFS server over the top
        // of the old one.
        try {
            // Delete the old file first
            dbfs.deleteFile(mediaName);
            // Upload the new one
            dbfs.putFile(new File(tempdir + mediaName));
            UI.cursorToPointer();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
            UI.cursorToPointer();

            return;
        }
    }

    public void delete() {
        try {
            if (lst.getSelectedIndex() == -1) {
                return;
            }

            // Delete selected and refresh
            String selitem = (String) lst.getSelectedValue();

            // If it's a directory, strip the prefix and delete
            // the contents
            if (selitem.startsWith(DIR)) {
                selitem = selitem.substring(DIR.length() + 1);
                dbfs.deleteDir(selitem);
            } else {
                dbfs.deleteFile(selitem);
            }

            displayDirectory();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void importFromFS() {
        try {
            // Make sure user is sure
            if (!Dialog.showYesNo(
                        "This will destroy all existing media and overwrite with the files you choose. Are you sure?",
                        "Sure?")) {
                return;
            }

            // Prompt for directory
            UI.FileChooser jf = new UI.FileChooser();
            String dir = jf.showDirectorySelectDialog(Global.mainForm, "");
            DBFS.importFromFileSystem(new File(dir));
            dbfs = new DBFS();
            displayDirectory();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void exportDir() {
        try {
            // Bail out if nothing selected
            if (lst.getSelectedIndex() == -1) {
                return;
            }

            // Bail out if it's not a directory
            if (lst.getSelectedValue().toString().indexOf(DIR) == -1) {
                return;
            }

            // Strip the dir tag
            String dir = lst.getSelectedValue().toString();
            dir = dir.substring(DIR.length() + 1);

            // Prompt for save directory
            UI.FileChooser jf = new UI.FileChooser();
            String sdir = jf.showDirectorySelectDialog(Global.mainForm, "");

            // Do it
            dbfs.exportDirectory(sdir, dir);
            displayDirectory();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void importDir() {
        try {
            // Prompt for import directory
            UI.FileChooser jf = new UI.FileChooser();
            String dir = jf.showDirectorySelectDialog(Global.mainForm, "");

            // Do it
            dbfs.importDirectory(new File(dir));
            displayDirectory();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** Instead of firing the double click event, tell the control to do a checkout instead */
    public void mapDoubleClickToCheckout() {
        doubleClickCheckout = true;
    }

    /**
     * Cell renderer to display icons in the list, so we can show either valid
     * documents or directories. Directories are denoted by the word (<dir>) at the
     * front of their entry.
     */
    private class IconCellRenderer extends JLabel implements ListCellRenderer {
        private ImageIcon dirIcon = IconManager.getIcon(IconManager.FOLDER);
        private ImageIcon docIcon = IconManager.getIcon(IconManager.FILE);

        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        public IconCellRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            String s = value.toString();
            setText(s);
            setIcon((s.startsWith(DBFSBrowser.DIR) ? dirIcon : docIcon));

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            return this;
        }
    }
}
