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
package net.sourceforge.sheltermanager.asm.internet;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;
import java.io.IOException;


/**
 * Contains functionality for publishers that upload via FTP
 * @author Robin Rawson-Tetley
 */
public class FTPPublisher extends AbstractPublisher {
    /** The upload socket */
    protected FTPClient uploadFTP = null;

    /** FTP settings */
    protected String host;

    /** FTP settings */
    protected String user;

    /** FTP settings */
    protected String password;

    /** FTP settings */
    protected String port;

    /** FTP settings */
    protected String root;

    /** Current FTP directory */
    protected String currentFTPDirectory = "";

    /** Active or Passive FTP */
    protected boolean isPassive = true;

    /** Initialise the publisher */
    protected void init(String publisherName, InternetPublisher parent,
        PublishCriteria publishCriteria, String host, String user,
        String password, String port, String root) {
        super.init(publisherName, parent, publishCriteria);

        // Open the FTP socket
        if (publishCriteria.uploadDirectly) {
            this.host = host;
            this.user = user;
            this.password = password;
            this.port = port;
            this.root = root;
        }
    }

    /** Initialise the publisher, specifying active or passive connections */
    protected void init(String publisherName, InternetPublisher parent,
        PublishCriteria publishCriteria, String host, String user,
        String password, String port, String root, boolean isPassive) {
        super.init(publisherName, parent, publishCriteria);

        // Open the FTP socket
        if (publishCriteria.uploadDirectly) {
            this.host = host;
            this.user = user;
            this.password = password;
            this.port = port;
            this.root = root;
            this.isPassive = isPassive;
        }
    }

    /**
    * Opens a connection to the users remote internet FTP server
    * @return true if the operation was successful
    */
    @SuppressWarnings("deprecation")
    protected boolean openFTPSocket() {
        if (!publishCriteria.uploadDirectly) {
            return true;
        }

        try {
            if (host.trim().equals("")) {
                Dialog.showWarning(Global.i18n("uiinternet",
                        "cannot_upload_directly"));

                return false;
            }

            // Override the FTP root if we were given one
            // with the criteria
            if (publishCriteria.ftpRoot != null) {
                this.root = publishCriteria.ftpRoot;
            }

            uploadFTP = new FTPClient(host, Integer.parseInt(port));
            uploadFTP.login(user, password);
            uploadFTP.setType(FTPTransferType.BINARY);
            uploadFTP.setConnectMode(isPassive ? FTPConnectMode.PASV
                                               : FTPConnectMode.ACTIVE);

            if (!root.trim().equals("")) {
                chdir(this.root);
            }

            return true;
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }

        return false;
    }

    /**
     * Destroys the users internet FTP socket
     */
    protected void closeFTPSocket() {
        if (!publishCriteria.uploadDirectly) {
            return;
        }

        try {
            uploadFTP.quit();
            uploadFTP = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Called before each upload if publishCriteria.checkSocket
     * is set to true - verifies that the socket is still active by
     * requesting a directory. If it cannot get one, the socket is reopened
     * and the current FTP directory returned to.
     */
    @SuppressWarnings("deprecation")
    protected void checkFTPSocket() {
        if (!publishCriteria.uploadDirectly) {
            return;
        }

        // Verify that the upload socket is still live by requesting
        // a directory for the current file - we are looking for an
        // error - who cares what comes back (mental note - allow
        // access to the FTP socket publically).
        try {
            uploadFTP.list("*");
            // Make sure transfers are back to binary
            uploadFTP.setType(FTPTransferType.BINARY);
        } catch (FTPException e) {
            // Destroy the current socket
            closeFTPSocket();
            // Open a new one
            openFTPSocket();

            if (!currentFTPDirectory.equals("")) {
                chdir(currentFTPDirectory);
            }
        } catch (IOException e) {
            // Destroy the current socket
            closeFTPSocket();
            // Open a new one
            openFTPSocket();

            if (!currentFTPDirectory.equals("")) {
                chdir(currentFTPDirectory);
            }
        }
    }

    /**
     * Uploads a file from the publish directory in the temp folder to the
     * internet site according to the FTP settings. If the file already exists
     * (and it is not our extension), it is not uploaded again.
     */
    @SuppressWarnings("deprecation")
    protected void upload(String filename) {
        if (!publishCriteria.uploadDirectly) {
            return;
        }

        try {
            String publishDir = publishDirectory + File.separator;

            // Make sure the local file actually exists - if it doesn't we
            // may as well drop out now and not risk blowing up the FTP
            // connection.
            File localfile = new File(publishDir + filename);

            if (!localfile.exists()) {
                return;
            }

            if (publishCriteria.checkSocket) {
                checkFTPSocket();
            }

            // If the file is our designated extension, or 
            // JavaScript, or a CSV file, or it has no extension, 
            // then just upload it over the top
            try {
                if ((filename.indexOf("." + publishCriteria.extension) != -1) ||
                    (filename.indexOf(".js") != -1) ||
                    (filename.indexOf(".") != -1) ||
                    (filename.indexOf(".csv") != -1)) {
                    uploadFTP.put(publishDir + filename, filename);
                    return;
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Does the file already exist? If so, return
            // and don't upload (unless force is set)
            try {
                String alreadyThere = uploadFTP.list(filename);

                if ((alreadyThere.indexOf(filename) != -1) &&
                        !publishCriteria.forceReupload) {
                    return;
                }
            } catch (FTPException e) {
                // Do nothing and carry on. The error occurs
                // because the list command returned no matching files -
                // hence we need to upload.
            }

            // Make sure transfers are back to binary
            uploadFTP.setType(FTPTransferType.BINARY);

            // Upload the file
            uploadFTP.put(publishDir + filename, filename);
        }
        // Ignore errors - if a file already exists then don't try to
        // upload it again.
        catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Creates a new directory on the FTP server
     * fails silently if it could not be created
     * @param newdir
     */
    protected void mkdir(String newdir) {
    	if (!publishCriteria.uploadDirectly) {
            return;
        }
    	
        try {
            uploadFTP.mkdir(newdir);
        } catch (Exception e) {
        }
    }

    /**
     * Change the current FTP directory
     * @param newdir
     */
    protected void chdir(String newdir) {
        if (!publishCriteria.uploadDirectly) {
            return;
        }

        try {
            uploadFTP.chdir(newdir);
            currentFTPDirectory = newdir;
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return;
        }
    }

    /**
     * Change the current FTP directory
     * @param newdir The directory to cd to
     * @param fromroot The directory from the root for coming back here
     */
    protected void chdir(String newdir, String fromroot) {
    	if (!publishCriteria.uploadDirectly) {
            return;
        }
        chdir(newdir);
        currentFTPDirectory = fromroot;
    }

    /**
     * Looks at the directory the upload socket is pointing to and removes any
     * existing HTML files.
     */
    @SuppressWarnings("deprecation")
    protected void clearExistingHTML() {
        if (!publishCriteria.uploadDirectly) {
            return;
        }

        try {
            String existing = uploadFTP.list("*." + publishCriteria.extension);
            String[] files = Utils.split(existing, "\n");

            for (int i = 0; i < files.length; i++) {
                if (!(files[i].trim()
                                  .equalsIgnoreCase("search." +
                            publishCriteria.extension))) {
                    uploadFTP.delete(files[i].trim());
                }
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Uploads the preferred image for an animal with
     * the name given - even if upload is off, we still
     * pull the image to the local publish folder
     * @param an
     * @param name
     */
    protected void uploadImage(Animal an, String name) {
        try {
            // Only do the file handling if the animal actually has
            // photo media
            if (an.hasValidMedia()) {
                // Get the name of the animal's image file.
                String animalweb = an.getWebMedia();
                String animalpic = name;

                // Copy the animal image to the publish folder
                Global.logInfo("Retrieving image.",
                    getClass().getName() + ".uploadImage");

                try {
                    DBFS dbfs = Utils.getDBFSDirectoryForLink(0,
                            an.getID().intValue());

                    try {
                        // Grab the image from DBFS
                        dbfs.readFile(animalweb, publishDir + animalpic);

                        // If scaling is on, scale the image
                        if (publishCriteria.scaleImages != 1) {
                            scaleImage(publishDir + animalweb,
                                publishCriteria.scaleImages);
                        }

                        // If thumbnails are on, generate one
                        if (publishCriteria.thumbnails) {
                            generateThumbnail(publishDir, animalpic,
                                "tn_" + animalpic);
                        }

                        // Upload the pic and the thumbnail
                        if (publishCriteria.uploadDirectly) {
                            upload(animalpic);

                            if (publishCriteria.thumbnails) {
                                upload("tn_" + animalweb);
                            }
                        }
                    }
                    // If an IO Error occurs, the file is already in the
                    // publish directory.
                    catch (Exception e) {
                    }

                    dbfs = null;
                }
                // Ignore errors retrieving files from the media
                // server.
                catch (Exception e) {
                }

                Global.logInfo("Retrieved image.",
                    getClass().getName() + ".uploadImage");
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }
    }

    /**
     * Uploads all images for an animal, either with the animal's
     * media ID for a single image, or with the sheltercode-X if
     * upload all is on
     * @param an
     * @param mediaIDAsName Whether to use the Media ID if uploadall is off
     * @return The number of images uploaded
     */
    protected int uploadImages(Animal an, boolean mediaIDAsName) {
        return uploadImages(an, mediaIDAsName, 0);
    }

    /**
     * Uploads all the images for an animal, either with the animal's
     * media ID for a single image, or with the sheltercode-X if
     * upload all is on - even if upload is off, we still
     * pull the images to the local publish folder
     * @param an
     * @param mediaIDAsName Whether to use the Media ID if uploadall is off
     * @param max Maximum number to upload or 0 for all
     * @return The number of images uploaded
     */
    protected int uploadImages(Animal an, boolean mediaIDAsName, int max) {
        int totalimages = 0;

        try {
            // Only do the file handling if the animal actually has
            // photo media
            if (an.hasValidMedia()) {
                // Get the name of the animal's image file. We use the
                // animal's code as the filename
                String animalweb = an.getWebMedia();
                String animalcode = an.getShelterCode();
                String animalpic = animalcode + "-1.jpg";
                totalimages = 1;

                // Copy the animal image to the publish folder
                Global.logInfo("Retrieving image.",
                    getClass().getName() + ".uploadImages");

                try {
                    DBFS dbfs = Utils.getDBFSDirectoryForLink(0,
                            an.getID().intValue());

                    try {
                    	// Handle the preferred image for this animal.
                		animalpic = animalcode + ".jpg";
                    	if (mediaIDAsName) animalpic = animalweb;
                    	if (publishCriteria.uploadAllImages) animalpic = animalcode + "-1.jpg";
                    	
	                    dbfs.readFile(animalweb, publishDir + animalpic);
	
                        // If scaling is on, scale the image
                        if (publishCriteria.scaleImages != 1) {
                            scaleImage(publishDir + animalpic,
                                publishCriteria.scaleImages);
                        }

                        // If thumbnails are on, generate one
                        if (publishCriteria.thumbnails) {
                            generateThumbnail(publishDir, animalpic,
                                "tn_" + animalpic);
                        }
                    	if (publishCriteria.uploadDirectly) {
                    		upload(animalpic);	
                    		if (publishCriteria.thumbnails) {
                    			upload("tn_" + animalpic);
                    		}
                        }
                    }
                    // If an IO Error occurs, the file is already in the
                    // publish directory.
                    catch (Exception e) {
                    }

                    // If the upload all option is set, grab the rest of
                    // the images this animal has (upto the max
                    // argument) and save them. If max is zero, then
                    // we will upload everything, since totalimages
                    // enters this loop at 1.
                    if (publishCriteria.uploadAllImages) {
                        int idx = 1;
                        String[] images = dbfs.list();

                        Global.logInfo("Animal has " + images.length +
                            " media files",
                            getClass().getName() + ".uploadImages");

                        for (int i = 0; i < images.length; i++) {
                            // Ignore the main web media - we used that
                            if (!animalweb.equals(images[i]) &&
                                    isImage(images[i])) {
                                idx++;

                                // If we've already done the max
                                // images we were told to, drop out now
                                if (totalimages == max) {
                                    return totalimages;
                                }

                                totalimages++;

                                String otherpic = animalcode + "-" + idx +
                                    ".jpg";

                                Global.logInfo("Retrieving additional image: " +
                                    otherpic + " (" + images[i] + ")",
                                    getClass().getName() + ".uploadImages");

                                dbfs.readFile(images[i], publishDir + otherpic);

                                // If scaling is on, scale the image
                                if (publishCriteria.scaleImages != 1) {
                                    scaleImage(publishDir + otherpic,
                                        publishCriteria.scaleImages);
                                }

                                // If uploading is switched on, upload the file
                                Global.logInfo("Uploading additional image: " +
                                    otherpic + " (" + images[i] + ")",
                                    getClass().getName() + ".uploadImages");

                                if (publishCriteria.uploadDirectly) {
                                    upload(otherpic);
                                }
                            }
                        }
                    }

                    dbfs = null;
                }
                // Ignore errors retrieving files from the media
                // server.
                catch (Exception e) {
                }

                Global.logInfo("Retrieved image.",
                    getClass().getName() + ".uploadImages");
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }

        return totalimages;
    }
}
