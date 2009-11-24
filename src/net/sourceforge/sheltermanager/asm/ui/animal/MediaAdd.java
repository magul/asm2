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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMDialog;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.dbfs.*;

import java.io.*;

import java.util.*;


/**
 * Form for attaching media to a record.
 *
 * @author Robin Rawson-Tetley
 */
public class MediaAdd extends ASMForm {
    /** The last path used when adding media */
    public static String lastPath = "";

    static {
        lastPath = Utils.getDefaultDocumentPath();
    }

    /** The parent list AnimalMedia panel */
    MediaSelector parent = null;

    /** The link ID */
    int linkID = 0;

    /** The link type */
    int linkType = 0;
    private UI.TextField txtFileName;
    private UI.Button btnCancel;
    private UI.TextArea txtNotes;
    private UI.Button btnOk;
    private UI.Button btnBrowse;
    private UI.Button btnCapture;

    public MediaAdd(int linkID, int linkType) {
        this.linkID = linkID;
        this.linkType = linkType;
        startup();
    }

    /** Creates new form AddMedia */
    public MediaAdd(MediaSelector theparent) {
        linkID = theparent.linkID;
        linkType = theparent.linkType;
        parent = theparent;
        startup();
    }

    private void startup() {
        init(Global.i18n("uianimal", "Add_New_Media"),
            IconManager.getIcon(IconManager.SCREEN_ADDMEDIA), "uianimal");

        try {
            // Check if we are defaulting animal comments->media notes
            if (Configuration.getBoolean("AutoMediaNotes")) {
                // Default the animal comments if this is an animal link
                if (linkType == Media.LINKTYPE_ANIMAL) {
                    try {
                        Animal animal = new Animal();
                        animal.openRecordset("ID = " +
                            Integer.toString(linkID));
                        this.txtNotes.setText(animal.getAnimalComments());
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Error_reading_configuration_information:\n") +
                e.getMessage());
        }
    }

    public void dispose() {
        parent = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtFileName);
        ctl.add(txtNotes);
        ctl.add(btnBrowse);
        ctl.add(btnCapture);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtFileName;
    }

    public boolean formClosing() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public boolean saveData() {
        attachMedia();
        return true;
    }

    public void loadData() {
    }

    /**
     * Does the attaching of the media, creates the entry, uploads the local
     * file to the FTP server, creating the correct directories as it goes,
     * updates the parent list and finally destroys this form
     */
    public void attachMedia() {
        // Does the file exist?
        File mf = new File(txtFileName.getText());

        if (!mf.exists()) {
            Dialog.showError(Global.i18n("uianimal", "File_does_not_exist",
                    txtFileName.getText()));
            Global.logError(Global.i18n("uianimal", "File_does_not_exist",
                    txtFileName.getText()), "AddMedia.attachMedia");

            return;
        }

        // Get the file extension from the name given
        String filename = txtFileName.getText();
        String fileextension = filename.substring(filename.lastIndexOf(".") +
                1, filename.length());

        // Have we got an image?
        if (fileextension.equalsIgnoreCase("jpg") ||
                fileextension.equalsIgnoreCase("jpeg") ||
                fileextension.equalsIgnoreCase("png") ||
                fileextension.equalsIgnoreCase("gif")) {
            // Do we have scaling enabled?
            String scaling = Configuration.getString("IncomingMediaScaling",
                    "320x200");

            if (!scaling.equals("None")) {
                int width = Integer.parseInt(scaling.substring(0,
                            scaling.indexOf("x")));
                int height = Integer.parseInt(scaling.substring(scaling.indexOf(
                                "x") + 1));

                // Scale and save the image, change our links to
                // the scaled file instead.
                UI.scaleImage(filename,
                    Global.tempDirectory + File.separator + "scaled.jpg",
                    width, height);
                filename = Global.tempDirectory + File.separator +
                    "scaled.jpg";
                mf = new File(filename);
            }
        }

        // Is the file size within our limit?
        String maxsize = Configuration.getString("MaxMediaFileSize");

        if ((maxsize != null) && !maxsize.equals("")) {
            long ms = Long.parseLong(maxsize);
            ms *= 1024; // Turn Kb to bytes

            if (mf.length() > ms) {
                // It's too big
                Dialog.showError(Global.i18n("uianimal", "File_is_too_large",
                        Long.toString(mf.length() / 1024),
                        Long.toString(ms / 1024)));
                Global.logError(Global.i18n("uianimal", "File_is_too_large",
                        Long.toString(mf.length() / 1024),
                        Long.toString(ms / 1024)), "AddMedia.attachMedia");

                return;
            }
        }

        // Create the media entry
        Media media = new Media();

        try {
            media.openRecordset("ID = 0");
            media.addNew();
            media.setLinkID(new Integer(linkID));
            media.setLinkTypeID(new Integer(linkType));
            media.setMediaName(media.getID() + "." + fileextension);
            media.setMediaNotes(this.txtNotes.getText());
            media.setDate(new Date());
            media.setNewSinceLastPublish(new Integer(1));
            media.setUpdatedSinceLastPublish(new Integer(0));

            // Does this record have a preferred and this is a jpg?
            // If so, make this the preferred automatically.
            Media m = new Media();
            m.openRecordset("LinkTypeID = " + linkType + " AND LinkID = " +
                linkID + " AND WebsitePhoto = 1");

            if (m.getEOF()) {
                if (fileextension.toLowerCase().equals("jpg") ||
                        fileextension.toLowerCase().equals("jpeg")) {
                    media.setWebSitePhoto(new Integer(1));
                } else {
                    media.setWebSitePhoto(new Integer(0));
                }
            } else {
                media.setWebSitePhoto(new Integer(0));
            }

            media.save();
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Unable_to_create_media_database_entry:_") +
                e.getMessage(), Global.i18n("uianimal", "Error"));
            Global.logException(e, getClass());

            return;
        }

        try {
            // Go to the right directory, creating it if necessary
            DBFS dbfs = Utils.getDBFSDirectoryForLink(linkType, linkID);

            // Upload the local file, giving it the media name generated earlier
            dbfs.putFile(media.getMediaName(), filename);

            // Update the onscreen list
            if (parent != null) {
                parent.updateList();
            }

            // Destroy this form
            dispose();
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Error_occurred_uploading_to_media_server:_") +
                e.getMessage(), Global.i18n("uianimal", "Error"));
            Global.logException(e, getClass());

            return;
        }
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.Panel top = UI.getPanel(UI.getGridLayout(1));

        UI.Panel browsewidget = UI.getPanel(UI.getFlowLayout(true));
        txtFileName = (UI.TextField) UI.addComponent(browsewidget,
                i18n("Local_File:"),
                UI.getTextField(i18n("The_path_to_the_local_file_you_want_to_attach")));
        txtFileName.setWidth(500);

        UI.ToolBar t = UI.getToolBar();
        browsewidget.add(t);
        btnBrowse = (UI.Button) t.add(UI.getButton(null,
                    i18n("Browse_for_the_file_to_add"),
                    IconManager.getIcon(IconManager.SCREEN_ADDMEDIA_BROWSE),
                    UI.fp(this, "actionBrowse")));
        btnCapture = (UI.Button) t.add(UI.getButton(null,
                    i18n("Capture_from_video_source"), ' ',
                    IconManager.getIcon(IconManager.SCREEN_ADDMEDIA_CAPTURE),
                    UI.fp(this, "actionCapture")));

        UI.Panel scalewidget = UI.getPanel(UI.getFlowLayout(true));
        scalewidget.add(UI.getLabel("Scale images to:"));
        scalewidget.add(UI.getLabel(Configuration.getString(
                    "IncomingMediaScaling", "320x200")));

        top.add(browsewidget);
        top.add(scalewidget);

        UI.Panel noteswidget = UI.getPanel(UI.getBorderLayout());
        noteswidget.add(UI.getLabel(UI.ALIGN_LEFT, i18n("Notes:")),
            UI.BorderLayout.NORTH);
        txtNotes = (UI.TextArea) UI.addComponent(noteswidget,
                UI.getTextArea(i18n("Any_information_about_this_file._If_this_is_an_animal_photo,_please_write_website_details_here")));
        ;

        UI.Panel lobut = UI.getPanel(UI.getFlowLayout());
        btnOk = (UI.Button) lobut.add(UI.getButton(i18n("Ok"), null, 'o', null,
                    UI.fp(this, "attachMedia")));
        btnCancel = (UI.Button) lobut.add(UI.getButton(i18n("Cancel"), null,
                    'c', null, UI.fp(this, "dispose")));

        add(top, UI.BorderLayout.NORTH);
        add(noteswidget, UI.BorderLayout.CENTER);
        add(lobut, UI.BorderLayout.SOUTH);
    }

    public void actionBrowse() {
        Global.logDebug("Stored path: " + MediaAdd.lastPath,
            "btnBrowseActionPerformed");

        // If the last path we used doesn't exist any more (happens for
        // mounted digital cameras, etc.), then go back to the default
        // path for the platform.
        File f = new File(MediaAdd.lastPath);

        if (!f.exists()) {
            Global.logDebug("Last path no longer exists, using default.",
                "btnBrowseActionPerformed");
            lastPath = Utils.getDefaultDocumentPath();
        }

        // Create a new filechooser and browse
        UI.FileChooser chooser = UI.getImageFileChooser(MediaAdd.lastPath);

        try {
            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == UI.FileChooser.APPROVE_OPTION) {
                // Record the last path
                MediaAdd.lastPath = chooser.getSelectedFile().getAbsolutePath();
                Global.logDebug("Last media path: " + MediaAdd.lastPath,
                    "btnBrowseActionPerformed");
                // Set the text of the filename to the selected item
                this.txtFileName.setText(chooser.getSelectedFile()
                                                .getAbsolutePath());
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void actionCapture() {
        if (MediaCapture.capture()) {
            txtFileName.setText(MediaCapture.getCaptureFileName());

            // Show the capture window, previewing this image
            new VideoCaptureWindow().show();
        }
    }

    public class VideoCaptureWindow extends ASMDialog {
        UI.Label image = UI.getLabel(UI.ALIGN_CENTER, null);

        public VideoCaptureWindow() {
            super();
            initComponents();
        }

        public void initComponents() {
            setSize(640, 480);
            UI.centerWindow(this);
            setTitle(Global.i18n("uianimal", "Video_Capture"));
            setLayout(UI.getBorderLayout());

            // Load the first captured image into the preview pane
            image.setIcon(IconManager.getIconFromPath(Global.tempDirectory +
                    File.separator + "capture.jpg"));

            UI.ToolBar bar = UI.getToolBar();
            add(bar, UI.BorderLayout.NORTH);

            // Capture button
            UI.Button btnCapture = UI.getButton(null,
                    Global.i18n("uianimal", "Capture_from_video_source"), ' ',
                    IconManager.getIcon(IconManager.SCREEN_ADDMEDIA_CAPTURE),
                    UI.fp(this, "doCapture"));
            bar.add(btnCapture);

            // Close button
            UI.Button btnClose = UI.getButton(null,
                    Global.i18n("uianimal", "Close"), ' ',
                    IconManager.getIcon(IconManager.CLOSE),
                    UI.fp(this, "dispose"));
            bar.add(btnClose);

            // Image label
            add(image, UI.BorderLayout.CENTER);
        }

        public boolean windowCloseAttempt() {
            return false;
        }

        public void windowOpened() {
        }

        public void setSecurity() {
        }

        public Object getDefaultFocusedComponent() {
            return null;
        }

        public Vector getTabOrder() {
            return new Vector();
        }

        public void doCapture() {
            // If capture failed, drop out
            if (!MediaCapture.capture()) {
                return;
            }

            // Update the image
            image.setIcon(IconManager.getIconFromPath(
                    MediaCapture.getCaptureFileName()));
        }
    }
}
