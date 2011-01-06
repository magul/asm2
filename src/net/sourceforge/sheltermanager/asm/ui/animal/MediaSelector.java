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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.AuditTrail;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.dbfs.DBFS;
import net.sourceforge.sheltermanager.dbfs.DBFSException;

import java.io.File;

import java.util.Vector;


/**
 * Panel class for embedding media facilities in a frame.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MediaSelector extends ASMSelector {
    /** The link type, passed from the calling form */
    public int linkType = 0;

    /** The link ID, passed from the calling form */
    public int linkID = 0;

    /** The array of data to fill the table */
    public String[][] tabledata;

    /**
     * A flag to say whether there is anything interesting in this media control
     */
    private boolean hasMedia = false;
    private UI.Button btnAdd;
    private UI.Button btnCheckOut;
    private UI.Button btnDelete;
    private UI.Button btnEdit;
    private UI.Button btnSetWebMedia;
    private UI.Button btnSetDocMedia;
    private UI.Button btnSave;
    private UI.Button btnView;
    private UI.Label lblPreview;

    public MediaSelector() {
        init("uianimal");
    }

    public Object getDefaultFocusedComponent() {
        return getTable();
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(getTable());

        return ctl;
    }

    /**
     * Reads current user's security settings and deactivates things they can't
     * do.
     */
    public void setSecurity() {
        if (!Global.currentUserObject.getSecAddAnimalMedia()) {
            btnAdd.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMedia()) {
            btnEdit.setEnabled(false);
            disableDoubleClick = true;
        }

        if (!Global.currentUserObject.getSecViewAnimalMedia()) {
            btnView.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecDeleteAnimalMedia()) {
            btnDelete.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecChangeAnimalMedia()) {
            btnCheckOut.setEnabled(false);
            btnSetWebMedia.setEnabled(false);
            btnSetDocMedia.setEnabled(false);
        }
    }

    /**
     * Sets the appropriate link up so the component knows how to create records
     * and how to fill it's table. The routine to draw the table is then called.
     *
     * @param type
     *            The linktype ID as specified in Media.LINKTYPE_
     * @param id
     *            The record id for the link
     */
    public void setLink(int type, int id) {
        linkType = type;
        linkID = id;
    }

    public void dispose() {
        tabledata = null;
    }

    /**
     * Fills the table with the media entries for the passed link.
     */
    public void updateList() {
        Media media = new Media();
        media.openRecordset(linkType, linkID);

        // Create an array to hold the results for the table - note that we
        // have an extra column on here - the last column will actually hold
        // the ID.
        tabledata = new String[(int) media.getRecordCount()][6];

        // Create an array of headers for the accounts (one less than
        // array because 4th col will hold ID
        String[] columnheaders = {
                Global.i18n("uianimal", "Name"), Global.i18n("uianimal", "Date"),
                Global.i18n("uianimal", "Web_Preferred"),
                Global.i18n("uianimal", "Doc_Preferred"),
                Global.i18n("uianimal", "Notes")
            };

        // loop through the data and fill the array
        int i = 0;

        try {
            while (!media.getEOF()) {
                tabledata[i][0] = media.getMediaName();
                tabledata[i][1] = Utils.nullToEmptyString(Utils.formatTableDate(
                            media.getDate()));

                if (media.getWebSitePhoto().intValue() == 1) {
                    tabledata[i][2] = Global.i18n("uianimal", "Yes");
                } else {
                    tabledata[i][2] = Global.i18n("uianimal", "No");
                }

                if (media.getDocPhoto().intValue() == 1) {
                    tabledata[i][3] = Global.i18n("uianimal", "Yes");
                } else {
                    tabledata[i][3] = Global.i18n("uianimal", "No");
                }

                tabledata[i][4] = Utils.nullToEmptyString(media.getMediaNotes());
                tabledata[i][5] = media.getID().toString();
                hasMedia = true;
                i++;
                media.moveNext();
            }
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        setTableData(columnheaders, tabledata, i, 5);
    }

    /** Returns true if there is some content in the list */
    public boolean hasData() {
        return hasMedia;
    }

    /**
     * Scans through the animal media trying to locate a web preferred photo. If
     * none is found, the first image is set to web preferred.
     */
    public void ensureWebPreferred() {
        Media media = new Media();
        media.openRecordset(linkType, linkID);

        boolean foundWebPreferred = false;
        int firstImage = 0;

        try {
            while (!media.getEOF()) {
                // Is this the first image?
                if (firstImage == 0) {
                    if (media.getMediaName().toLowerCase().endsWith("jpg") ||
                            media.getMediaName().toLowerCase().endsWith("jpeg")) {
                        firstImage = media.getID().intValue();
                    }
                }

                if (media.getWebSitePhoto().intValue() == 1) {
                    foundWebPreferred = true;

                    break;
                }

                media.moveNext();
            }

            // If no web preferred was found, and there is a first
            // image, stamp it.
            if (!foundWebPreferred && (firstImage != 0)) {
                DBConnection.executeAction(
                    "UPDATE media SET WebsitePhoto = 1 WHERE ID = " +
                    firstImage);
            }
        } catch (Exception e) {
            Global.logError(e.getMessage(), "AnimalMedia.ensureWebPreferred");
            Global.logException(e, getClass());
        }
    }

    /**
     * Scans through the animal media trying to locate a doc preferred photo. If
     * none is found, the first image is set to doc preferred.
     */
    public void ensureDocPreferred() {
        Media media = new Media();
        media.openRecordset(linkType, linkID);

        boolean foundDocPreferred = false;
        int firstImage = 0;

        try {
            while (!media.getEOF()) {
                // Is this the first image?
                if (firstImage == 0) {
                    if (media.getMediaName().toLowerCase().endsWith("jpg") ||
                            media.getMediaName().toLowerCase().endsWith("jpeg")) {
                        firstImage = media.getID().intValue();
                    }
                }

                if (media.getDocPhoto().intValue() == 1) {
                    foundDocPreferred = true;

                    break;
                }

                media.moveNext();
            }

            // If no web preferred was found, and there is a first
            // image, stamp it.
            if (!foundDocPreferred && (firstImage != 0)) {
                DBConnection.executeAction(
                    "UPDATE media SET DocPhoto = 1 WHERE ID = " + firstImage);
            }
        } catch (Exception e) {
            Global.logError(e.getMessage(), "AnimalMedia.ensureDocPreferred");
            Global.logException(e, getClass());
        }
    }

    public void addToolButtons() {
        // Hook the preview to the right side
        lblPreview = UI.getLabel();
        lblPreview.setHorizontalAlignment(UI.ALIGN_CENTER);
        lblPreview.setToolTipText(i18n("Preview"));
        lblPreview.setVerticalAlignment(UI.ALIGN_TOP);
        lblPreview.setVerticalTextPosition(UI.ALIGN_TOP);
        add(lblPreview, UI.isLTR() ? UI.BorderLayout.EAST : UI.BorderLayout.WEST);

        btnAdd = UI.getButton(null, i18n("Attach_new_media"), 'n',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_NEW),
                UI.fp(this, "actionAdd"));
        addToolButton(btnAdd, false);

        btnEdit = UI.getButton(null, i18n("Edit_this_media_entry"), 'e',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_EDIT),
                UI.fp(this, "actionEdit"));
        addToolButton(btnEdit, true);

        btnDelete = UI.getButton(null, i18n("Delete_this_media"), 'd',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_DELETE),
                UI.fp(this, "actionDelete"));
        addToolButton(btnDelete, true);

        btnView = UI.getButton(null, i18n("View_this_media"), 'v',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_PREVIEW),
                UI.fp(this, "actionView"));
        addToolButton(btnView, true);

        btnCheckOut = UI.getButton(null, i18n("edit_media_in_application"),
                't',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_CHECKOUT),
                UI.fp(this, "actionCheckOut"));
        addToolButton(btnCheckOut, true);

        btnSetWebMedia = UI.getButton(null,
                i18n("Mark_this_media_as_preferred_for_use_with_the_web"), 'w',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_WEBPREFERRED),
                UI.fp(this, "actionWebMedia"));
        addToolButton(btnSetWebMedia, true);

        btnSetDocMedia = UI.getButton(null,
                i18n("Mark_this_media_as_preferred_for_use_with_documents"),
                'd',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_DOCPREFERRED),
                UI.fp(this, "actionDocMedia"));
        addToolButton(btnSetDocMedia, true);

        btnSave = UI.getButton(null, i18n("Save_this_media_file_to_disk"), 'a',
                IconManager.getIcon(IconManager.SCREEN_ANIMALMEDIA_SAVE),
                UI.fp(this, "actionSave"));
        addToolButton(btnSave, true);
    }

    public void actionCheckOut() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        // Get the filename
        String mediaName = tabledata[getTable().getSelectedRow()][0];

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
            DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);

            try {
                dbfs.readFile(mediaName, tempdir + mediaName);
                dbfs = null;
            } catch (DBFSException e) {
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }

        // Open the file with it's associated app
        if (0 != FileTypeManager.shellExecute(tempdir + mediaName)) {
            return;
        }

        // Tell the user to hit Ok when they've finished making changes
        // and closed their application
        Dialog.showInformation(Global.i18n("uianimal", "hit_ok_when_finished"));

        // Now, ask the user if they'd like to upload their changes -
        // bomb out if they say no.
        if (!Dialog.showYesNo(i18n("upload_back_to_server"),
                    i18n("Unsaved_Changes"))) {
            return;
        }

        // Reopen the dbfs socket back to the media entry and
        // upload the temp file to the DBFS server over the top
        // of the old one.
        try {
            DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);

            try {
                // Delete the old file first
                dbfs.deleteFile(mediaName);
                // Upload the new one
                dbfs.putFile(new File(tempdir + mediaName));
                dbfs = null;
            } catch (DBFSException e) {
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }
    }

    public void tableClicked() {
        // Preview an image if one is selected
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        // Get the filename
        String mediaName = tabledata[getTable().getSelectedRow()][0];

        // Is at a jpg, jpeg, gif or png file?
        if ((Utils.englishLower(mediaName).indexOf(".jpg") == -1) &&
                (Utils.englishLower(mediaName).indexOf(".gif") == -1) &&
                (Utils.englishLower(mediaName).indexOf(".jpeg") == -1) &&
                (Utils.englishLower(mediaName).indexOf(".png") == -1)) {
            return;
        }

        // Try to preview it
        new ImagePreviewer(mediaName, mediaID, lblPreview, linkType, linkID,
            this).start();
    }

    public void tableDoubleClicked() {
        actionEdit();
    }

    public void actionWebMedia() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        try {
            // Reset all media rows for this link to non-web preferred
            String s = "UPDATE media SET WebSitePhoto = 0 WHERE " +
                "LinkID = " + Integer.toString(linkID) + " AND " +
                "LinkTypeID = " + Integer.toString(linkType);
            net.sourceforge.sheltermanager.cursorengine.DBConnection.executeAction(s);

            // Set this selected row to web preferred
            s = "UPDATE media SET WebSitePhoto = 1 WHERE ID = " + mediaID;
            net.sourceforge.sheltermanager.cursorengine.DBConnection.executeAction(s);

            // update the list on screen
            this.updateList();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionDocMedia() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        try {
            // Reset all media rows for this link to non-web preferred
            String s = "UPDATE media SET DocPhoto = 0 WHERE " + "LinkID = " +
                Integer.toString(linkID) + " AND " + "LinkTypeID = " +
                Integer.toString(linkType);
            DBConnection.executeAction(s);

            // Set this selected row to doc preferred
            s = "UPDATE media SET DocPhoto = 1 WHERE ID = " + mediaID;
            DBConnection.executeAction(s);

            // update the list on screen
            this.updateList();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionView() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        // Get the filename
        String mediaName = tabledata[getTable().getSelectedRow()][0];

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
            DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);

            try {
                dbfs.readFile(mediaName, tempdir + mediaName);
                dbfs = null;
            } catch (Exception e) {
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }

        FileTypeManager.shellExecute(tempdir + mediaName);
    }

    public void actionDelete() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        // Make sure they are sure about this
        if (Dialog.showYesNo(UI.messageDeleteConfirm(), UI.messageReallyDelete())) {
            // Destroy the media file on the DBFS server
            try {
                DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);
                // Destroy the file
                dbfs.deleteFile(tabledata[getTable().getSelectedRow()][0]);
                dbfs = null;
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Remove it from the database
            try {
                String s = "Delete From media Where ID = " + mediaID;
                net.sourceforge.sheltermanager.cursorengine.DBConnection.executeAction(s);

                if (AuditTrail.enabled()) {
                    AuditTrail.deleted("media",
                        LookupCache.getMediaLinkForID(linkType) + " " + linkID +
                        " " + tabledata[getTable().getSelectedRow()][0]);
                }

                // Make sure there is still a web and doc preferred
                ensureWebPreferred();
                ensureDocPreferred();

                // update the list
                this.updateList();
            } catch (Exception e) {
                Dialog.showError(UI.messageDeleteError() + e.getMessage());
                Global.logException(e, getClass());
            }
        }
    }

    public void actionEdit() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        try {
            // Get the media object
            Media media = new Media();
            media.openRecordset("ID = " + mediaID);

            // Create a new EditMediaEntry form to edit it
            MediaEdit ea = new MediaEdit(media, this);
            Global.mainForm.addChild(ea);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public void actionSave() {
        int mediaID = getTable().getSelectedID();

        if (mediaID == -1) {
            return;
        }

        // Read the highlighted table record and get the file name
        String mediaName = tabledata[getTable().getSelectedRow()][0];
        String defaultFile = Utils.getDefaultDocumentPath() + File.separator +
            mediaName;

        // Prompt user for where they'd like to save it to
        UI.FileChooser fc = UI.getFileChooser();
        fc.setSelectedFile(new File(defaultFile));

        int result = fc.showSaveDialog(this);

        // Cancel if they cancelled
        if (result != UI.FileChooser.APPROVE_OPTION) {
            return;
        }

        // Get the location
        String path = fc.getSelectedFile().getAbsolutePath();

        // Make the cursor an hourglass
        UI.cursorToWait();

        // Grab the remote file and copy it to the location the user named
        try {
            DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);

            try {
                dbfs.readFile(mediaName, path);
                dbfs = null;
            } catch (Exception e) {
                // File already exists - who cares?
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        path = null;
        fc.dispose();
        fc = null;
    }

    public void actionAdd() {
        // Create a new add media form to request the
        // new media file from the user.
        MediaAdd addmedia = new MediaAdd(this);
        Global.mainForm.addChild(addmedia);
    }
}


class ImagePreviewer extends Thread {
    private String mediaName;
    private UI.Label previewPane;
    private int linkType;
    private int linkID;

    public ImagePreviewer(String mediaName, int mediaID, UI.Label previewPane,
        int linkType, int linkID, MediaSelector parent) {
        this.mediaName = mediaName;
        this.previewPane = previewPane;
        this.linkType = linkType;
        this.linkID = linkID;
    }

    public void run() {
        // Make the cursor an hourglass
        UI.cursorToWait();

        // Grab the remote file and copy it to the local temp directory
        String tempdir = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
            File.separator;

        try {
            DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);

            try {
                dbfs.readFile(mediaName, tempdir + mediaName);
            } catch (Exception e) {
                // File already exists - who cares?
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }

        try {
            previewPane.setIcon(IconManager.getThumbnail(tempdir + mediaName,
                    300, 200));
            previewPane.repaint();

            // Delete the temporary file
            Utils.deleteTemporaryFile(mediaName);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        } finally {
            // Make the cursor normal again
            UI.cursorToPointer();
        }
    }
}
