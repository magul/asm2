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
package net.sourceforge.sheltermanager.asm.wordprocessor;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.util.Date;
import java.util.Iterator;


/**
 *
 * Generates Owner donation data documents.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class OwnerDonationDocument extends GenerateDocument {
    Animal animal = null;
    Owner owner = null;
    OwnerDonation od = null;

    /** The parent component in case we add things as media */
    MediaSelector uiparent = null;

    /**
     * Creates a new owner donation document.
     *
     * @param theowner
     *            The <code>Animal</code> object to generate tags from.
     */
    public OwnerDonationDocument(OwnerDonation od, MediaSelector uiparent) {
        try {
            this.uiparent = uiparent;
            this.od = od;
            owner = od.getOwner();

            int animalid = od.getAnimalID().intValue();

            if (animalid > 0) {
                animal = new Animal("ID = " + animalid);
            }

            generateDocument();
        } catch (Exception e) {
            Global.logException(e, this.getClass());
        }
    }

    /**
     * Reads the passed owner and owner donation, and generates the search tags.
     */
    public void generateSearchTags() {
        try {
            // Generate Donation info
            addTag(Global.i18n("wordprocessor", "DonationID"),
                od.getID().toString());
            addTag(Global.i18n("wordprocessor", "DonationDate"),
                Utils.formatDate(od.getDateReceived()));
            addTag(Global.i18n("wordprocessor", "DonationDateDue"),
                Utils.formatDate(od.getDateDue()));
            addTag(Global.i18n("wordprocessor", "DonationAmount"),
                Utils.formatCurrency(od.getDonation()));
            addTag(Global.i18n("wordprocessor", "DonationComments"),
                od.getComments());
            addTag(Global.i18n("wordprocessor", "DonationCreatedBy"),
                od.getCreatedBy());
            addTag(Global.i18n("wordprocessor", "DonationCreatedByName"),
                LookupCache.getRealName(od.getCreatedBy()));
            addTag(Global.i18n("wordprocessor", "DonationCreatedDate"),
                Utils.formatDate(od.getCreatedDate()));
            addTag(Global.i18n("wordprocessor", "DonationLastChangedBy"),
                od.getLastChangedBy());
            addTag(Global.i18n("wordprocessor", "DonationLastChangedByName"),
                LookupCache.getRealName(od.getLastChangedBy()));
            addTag(Global.i18n("wordprocessor", "DonationLastChangedDate"),
                Utils.formatDate(od.getLastChangedDate()));
            addTag(Global.i18n("wordprocessor", "DonationType"),
                LookupCache.getDonationTypeName(od.getDonationTypeID()));
            addTag(Global.i18n("wordprocessor", "ReceiptNum"),
                od.getReceiptNum());
            addTag(Global.i18n("wordprocessor", "DonationGiftAid"),
                (owner.getIsGiftAid().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));

            // Generate animal tags if there's an animal link
            if (animal != null) {
                AnimalDocument ad = new AnimalDocument(animal, true);

                // Merge all the tags from the animal into the movement
                Iterator<SearchTag> i = ad.searchtags.iterator();

                while (i.hasNext()) {
                    searchtags.add(i.next());
                }
            }

            // Now generate owner tags
            OwnerDocument od = new OwnerDocument(owner, true);

            // Merge owner tags
            Iterator<SearchTag> oi = od.searchtags.iterator();

            while (oi.hasNext()) {
                searchtags.add(oi.next());
            }

            // Generate a document title based on the owner information
            // and the doc selected
            docTitle = templateName + " - " + owner.getOwnerName();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public String getImage() {
        // No images for donations
        return null;
    }

    /**
     * Attach the document to the owner as media.
     */
    public void attachMedia() {
        if (uiparent == null) {
            Global.logError("Attach media selected, but not MediaSelector object passed.",
                "OwnerDonationDocument.attachMedia");

            return;
        }

        // They do - lets add the file
        // Get the file extension from the name given
        String fileextension = localfile;
        fileextension = fileextension.substring(fileextension.lastIndexOf(".") +
                1, fileextension.length());

        // Create the media entry
        Media media = new Media();

        try {
            media.openRecordset("ID = 0");
            media.addNew();
            media.setLinkID(new Integer(uiparent.linkID));
            media.setLinkTypeID(new Integer(uiparent.linkType));
            media.setMediaName(media.getID() + "." + fileextension);
            media.setMediaNotes(docTitle);
            media.setDate(new Date());
            media.setWebSitePhoto(new Integer(0));
            media.setNewSinceLastPublish(new Integer(0));
            media.setUpdatedSinceLastPublish(new Integer(0));
            media.save();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }

        try {
            // Go to the right directory, creating it if necessary
            DBFS dbfs = Utils.getDBFSDirectoryForLink(uiparent.linkType,
                    uiparent.linkID);

            // Upload the local file, giving it the media name generated earlier
            dbfs.putFile(media.getMediaName(), localfile);

            // Update the onscreen list
            uiparent.updateList();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }
    }

    /**
     * Write a log history entry for the owner
     */
    public void writeLogEntry() {
        try {
            Log l = new Log("ID=0");
            l.addNew();
            l.setLogTypeID(Configuration.getInteger("GenerateDocumentLogType"));
            l.setLinkID(owner.getID());
            l.setLinkType(Log.LINKTYPE_OWNER);
            l.setDate(new Date());
            l.setComments(templateName);
            l.save(Global.currentUserName);
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }
    }
}
