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
package net.sourceforge.sheltermanager.asm.wordprocessor;

import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.util.Date;


/**
 *
 * Generates Owner donation data documents.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class OwnerDonationDocument extends GenerateDocument {
    /** The owner object */
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
            owner = new Owner();
            owner.openRecordset("ID = " + od.getOwnerID());
            this.od = od;

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
            addTag(Global.i18n("wordprocessor", "OwnerComments"),
                Utils.nullToEmptyString(owner.getComments()));
            addTag(Global.i18n("wordprocessor", "OwnerCreatedBy"),
                owner.getCreatedBy());
            addTag(Global.i18n("wordprocessor", "OwnerCreatedByName"),
                LookupCache.getRealName(owner.getCreatedBy()));
            addTag(Global.i18n("wordprocessor", "OwnerCreatedDate"),
                Utils.formatDate(owner.getCreatedDate()));
            addTag(Global.i18n("wordprocessor", "HomeTelephone"),
                Utils.nullToEmptyString(owner.getHomeTelephone()));
            addTag(Global.i18n("wordprocessor", "OwnerID"),
                owner.getID().toString());
            addTag(Global.i18n("wordprocessor", "IDCheck"),
                (owner.getIDCheck().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "OwnerLastChangedDate"),
                Utils.formatDate(owner.getLastChangedDate()));
            addTag(Global.i18n("wordprocessor", "OwnerLastChangedBy"),
                owner.getLastChangedBy());
            addTag(Global.i18n("wordprocessor", "OwnerLastChangedByName"),
                LookupCache.getRealName(owner.getLastChangedBy()));
            addTag(Global.i18n("wordprocessor", "OwnerAddress"),
                Utils.formatAddress(owner.getOwnerAddress()));
            addTag(Global.i18n("wordprocessor", "OwnerTown"),
                Utils.nullToEmptyString(owner.getOwnerTown()));
            addTag(Global.i18n("wordprocessor", "OwnerCounty"),
                Utils.nullToEmptyString(owner.getOwnerCounty()));
            addTag(Global.i18n("wordprocessor", "OwnerName"),
                owner.getOwnerName());
            addTag(Global.i18n("wordprocessor", "OwnerPostcode"),
                Utils.nullToEmptyString(owner.getOwnerPostcode()));
            addTag(Global.i18n("wordprocessor", "WorkTelephone"),
                Utils.nullToEmptyString(owner.getWorkTelephone()));
            addTag(Global.i18n("wordprocessor", "MobileTelephone"),
                Utils.nullToEmptyString(owner.getMobileTelephone()));

            addTag(Global.i18n("wordprocessor", "OwnerTitle"),
                owner.getOwnerTitle());
            addTag(Global.i18n("wordprocessor", "OwnerInitials"),
                owner.getOwnerInitials());
            addTag(Global.i18n("wordprocessor", "OwnerForenames"),
                owner.getOwnerForenames());
            addTag(Global.i18n("wordprocessor", "OwnerSurname"),
                owner.getOwnerSurname());

            // Generate Donation info
            addTag(Global.i18n("wordprocessor", "DonationID"),
                od.getID().toString());
            addTag(Global.i18n("wordprocessor", "DonationDate"),
                Utils.formatDate(od.getDateReceived()));
            addTag(Global.i18n("wordprocessor", "DonationDateDue"),
                Utils.formatDate(od.getDateDue()));
            addTag(Global.i18n("wordprocessor", "DonationAmount"),
                od.getDonation().toString());
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
}
