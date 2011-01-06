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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import net.sourceforge.sheltermanager.asm.bo.Additional;
import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.dbfs.DBFS;


/**
 *
 * Generates Owner data documents.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class OwnerDocument extends GenerateDocument {
    /** The owner object */
    Owner owner = null;

    /** The parent component in case we add things as media */
    MediaSelector uiparent = null;

    /**
     * Creates a new owner document.
     *
     * @param theowner
     *            The <code>Animal</code> object to generate tags from.
     */
    public OwnerDocument(Owner theowner, MediaSelector uiparent) {
        this.owner = theowner;
        this.uiparent = uiparent;
        generateDocument();
    }

    /**
     * Constructor to only generate the vector of tags. Useful for other classes
     * wanting owner tags.
     *
     * @param theowner
     *            The <code>Owner</code> object to generate tags from.
     * @param tagsonly
     *            Doesn't matter what this value is set to, it's existence means
     *            that the object will only generate tags and not try to create
     *            an owner document.
     */
    public OwnerDocument(Owner theowner, boolean tagsonly) {
        owner = theowner;
        generateSearchTags();
    }

    /**
     * Reads the passed owner and generates the search tags.
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
            addTag(Global.i18n("wordprocessor", "OwnerFirstNames"),
                owner.getOwnerForenames());
            addTag(Global.i18n("wordprocessor", "OwnerSurname"),
                owner.getOwnerSurname());
            addTag(Global.i18n("wordprocessor", "OwnerLastName"),
                owner.getOwnerSurname());
            addTag(Global.i18n("wordprocessor", "OwnerEmail"),
                owner.getEmailAddress());
            addTag(Global.i18n("wordprocessor", "MembershipNumber"),
                owner.getMembershipNumber());
            addTag(Global.i18n("wordprocessor", "MembershipExpiryDate"),
                Utils.formatDate(owner.getMembershipExpiryDate()));

            // Data held in additional fields
            try {
                ArrayList<Additional.Field> v = Additional.getFieldValues(AdditionalField.LINKTYPE_OWNER,
                        owner.getID().intValue());

                for (int i = 0; i < v.size(); i++) {
                    Additional.Field af = (Additional.Field) v.get(i);
                    String val = af.value;

                    if (af.fieldType == AdditionalField.FIELDTYPE_YESNO) {
                        val = af.value.equals("1")
                            ? Global.i18n("uiwordprocessor", "Yes")
                            : Global.i18n("uiwordprocessor", "No");
                    }

                    addTag(af.fieldName, val);
                    Global.logDebug("Added additional owner field tag, name: '" +
                        af.fieldName + "', value '" + val,
                        "OwnerDocument.generateSearchTags");
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Generate a document title based on the owner information
            // and the doc selected
            docTitle = templateName + " - " + owner.getOwnerName();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** If this movement/owner has media, saves it to a temporary file
     *  and returns the name to the caller - this is for use by word
     *  processors that can embed images and allows them to include
     *  them in documents.
     *  @return null if the animal has no web media
     */
    public String getImage() {
        try {
            if (!owner.hasImage()) {
                return null;
            }

            String medianame = owner.getThumbnailImage();
            String file = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                File.separator + medianame;
            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_OWNER,
                    owner.getID().intValue());
            dbfs.readFile(medianame, file);
            dbfs = null;

            return file;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return null;
    }

    /**
     * Attach the document to the owner as media.
     */
    public void attachMedia() {
        // Stop if there is no UI parent - not really possible, but
        // just in case sanity check
        if (uiparent == null) {
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
