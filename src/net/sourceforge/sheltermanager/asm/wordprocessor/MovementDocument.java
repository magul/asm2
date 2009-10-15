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
package net.sourceforge.sheltermanager.asm.wordprocessor;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.ftp.FTPClient;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementParent;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;

import java.util.Date;
import java.util.Iterator;


/**
 *
 * Generates Movement data documents with animal and owner data.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class MovementDocument extends GenerateDocument {
    /** The movement object */
    Adoption movement = null;

    /**
     * Any screen that spawned the movement and has media it may appear on.
     */
    MediaSelector uiparent = null;

    /**
     * Interface parent we can call back to say that media updated
     */
    MovementParent parent = null;

    /**
     * Creates a new movement document.
     *
     * @param themovement
     *            The <code>Adoption</code> object to generate tags from.
     */
    public MovementDocument(Adoption themovement, MediaSelector uiparent) {
        this.uiparent = uiparent;
        this.movement = themovement;
        generateDocument();
    }

    public MovementDocument(Adoption themovement, MovementParent parent) {
        this.parent = parent;
        this.movement = themovement;
        generateDocument();
    }

    /**
     * Constructor to only generate the vector of tags. Useful for other classes
     * wanting movement tags.
     *
     * @param themovement
     *            The <code>Adoption</code> object to generate tags from.
     * @param tagsonly
     *            Doesn't matter what this value is set to, it's existence means
     *            that the object will only generate tags and not try to create
     *            a movement document.
     */
    public MovementDocument(Adoption themovement, boolean tagsonly) {
        this.movement = themovement;
        generateSearchTags();
    }

    /**
     * Reads the passed movement and generates the search tags. It also
     * instantiates the relevant <code>AnimalDocument</code> and
     * <code>OwnerDocument</code> objects and generates their tags too.
     */
    public void generateSearchTags() {
        try {
            addTag(Global.i18n("wordprocessor", "AdoptionID"),
                movement.getID().toString());
            addTag(Global.i18n("wordprocessor", "AdoptionDate"),
                Utils.formatDate(movement.getAdoptionDate()));
            addTag(Global.i18n("wordprocessor", "TransferDate"),
                Utils.formatDate(movement.getTransferDate()));
            addTag(Global.i18n("wordprocessor", "FosteredDate"),
                Utils.formatDate(movement.getFosteredDate()));
            addTag(Global.i18n("wordprocessor", "AdoptionNumber"),
                movement.getAdoptionNumber());
            addTag(Global.i18n("wordprocessor", "AdoptionCreatedBy"),
                movement.getCreatedBy());
            addTag(Global.i18n("wordprocessor", "AdoptionCreatedDate"),
                Utils.formatDate(movement.getCreatedDate()));
            addTag(Global.i18n("wordprocessor", "AdoptionLastChangedBy"),
                movement.getLastChangedBy());
            addTag(Global.i18n("wordprocessor", "AdoptionLastChangedDate"),
                Utils.formatDate(movement.getLastChangedDate()));
            addTag(Global.i18n("wordprocessor", "ReturnDate"),
                Utils.formatDate(movement.getReturnDate()));
            addTag(Global.i18n("wordprocessor", "InsuranceNumber"),
                Utils.nullToEmptyString(movement.getInsuranceNumber()));
            addTag(Global.i18n("wordprocessor", "AdoptionDonation"),
                movement.getDonation().toString());
            addTag(Global.i18n("wordprocessor", "ReservationDate"),
                Utils.formatDate(movement.getReservationDate()));
            addTag(Global.i18n("wordprocessor", "ReservationCancelledDate"),
                Utils.formatDate(movement.getReservationCancelledDate()));

            // Generate animal tags
            AnimalDocument ad = new AnimalDocument(movement.getAnimal(), true);

            // Now owner tags
            OwnerDocument od = new OwnerDocument(movement.getOwner(), true);

            // Merge all the tags from the animal into the movement
            Iterator i = ad.searchtags.iterator();

            while (i.hasNext()) {
                searchtags.add(i.next());
            }

            // Now do all the owner tags
            i = od.searchtags.iterator();

            while (i.hasNext()) {
                searchtags.add(i.next());
            }

            // Generate a document title based on the animal and owner
            // information
            // and the doc selected
            docTitle = templateName + " - " +
                movement.getAnimal().getShelterCode() + " " +
                movement.getAnimal().getAnimalName() +
                Global.i18n("wordprocessor", "_to_") +
                movement.getOwner().getOwnerName();
        } catch (CursorEngineException e) {
            Global.logException(e, getClass());
        }
    }

    /** If this movement/animal has media, saves it to a temporary file
     *  and returns the name to the caller - this is for use by word
     *  processors that can embed images and allows them to include
     *  them in documents.
     *  @return null if the animal has no web media
     */
    public String getImage() {
        try {
            Animal animal = movement.getAnimal();

            if (animal == null) {
                return null;
            }

            if (!animal.hasValidMedia()) {
                return null;
            }

            String medianame = animal.getWebMedia();
            String file = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                File.separator + medianame;
            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                    animal.getID().intValue());
            dbfs.readFile(medianame, file);
            dbfs = null;

            return file;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return null;
    }

    /**
     * Attaches the document to the owner and animal as media.
     */
    public void attachMedia() {
        // They do - lets add the file
        // Get the file extension from the name given
        String fileextension = localfile;
        fileextension = fileextension.substring(fileextension.lastIndexOf(".") +
                1, fileextension.length());

        // Create the 1st media entry for animal
        // -----------------------------
        int linkID = 0;

        try {
            linkID = movement.getAnimalID().intValue();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Media media = new Media();

        try {
            media.openRecordset("ID = 0");
            media.addNew();
            media.setLinkID(new Integer(linkID));
            media.setLinkTypeID(new Integer(Media.LINKTYPE_ANIMAL));
            media.setMediaName(media.getID() + "." + fileextension);
            media.setMediaNotes(docTitle);
            media.setDate(new Date());
            media.setWebSitePhoto(new Integer(0));
            media.setNewSinceLastPublish(new Integer(0));
            media.setUpdatedSinceLastPublish(new Integer(0));
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
            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                    linkID);

            // Upload the local file, giving it the media name generated earlier
            dbfs.putFile(media.getMediaName(), localfile);

            // Update the onscreen list if there is one
            if (uiparent != null) {
                uiparent.updateList();
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Error_occurred_uploading_to_media_server:_") +
                e.getMessage(), Global.i18n("uianimal", "Error"));
            Global.logException(e, getClass());

            return;
        }

        // Create the 2nd media entry for owner
        // -----------------------------
        try {
            linkID = movement.getOwnerID().intValue();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        media.free();
        media = null;

        media = new Media();

        try {
            media.openRecordset("ID = 0");
            media.addNew();
            media.setLinkID(new Integer(linkID));
            media.setLinkTypeID(new Integer(Media.LINKTYPE_OWNER));
            media.setMediaName(media.getID() + "." + fileextension);
            media.setMediaNotes(docTitle);
            media.setDate(new Date());
            media.setWebSitePhoto(new Integer(0));
            media.save();
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Error_occurred_uploading_to_media_server:_") +
                e.getMessage(), Global.i18n("uianimal", "Error"));
            Global.logException(e, getClass());

            return;
        }

        try {
            // Go to the right directory, creating it if necessary
            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_OWNER,
                    linkID);

            // Upload the local file, giving it the media name generated earlier
            dbfs.putFile(media.getMediaName(), localfile);

            // Update the onscreen list if there is one
            if (uiparent != null) {
                uiparent.updateList();
            }

            if (parent != null) {
                parent.updateMedia();
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("uianimal",
                    "Error_occurred_uploading_to_media_server:_") +
                e.getMessage(), Global.i18n("uianimal", "Error"));
            Global.logException(e, getClass());

            return;
        }
    }
}
