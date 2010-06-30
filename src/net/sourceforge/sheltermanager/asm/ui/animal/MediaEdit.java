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
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;

import java.util.Vector;


/**
 * Form class for editing media notes
 *
 * @author Robin Rawson-Tetley
 */
public class MediaEdit extends ASMForm {
    private Media media = null;
    private MediaSelector parent = null;
    private UI.Button btnCancel;
    private UI.TextArea txtNotes;
    private UI.Button btnOk;

    /** Creates new form EditMediaEntry editing the Media object passed in */
    public MediaEdit(Media media, MediaSelector parent) {
        this.media = media;
        this.parent = parent;
        init(Global.i18n("uianimal", "Edit_Media_Entry"),
            IconManager.getIcon(IconManager.SCREEN_EDITMEDIAENTRY), "uianimal");
        loadData();
    }

    public void loadData() {
        try {
            txtNotes.setText(media.getMediaNotes());
        } catch (CursorEngineException e) {
            Dialog.showError(i18n("Could_not_read_media_entry_from_database:_") +
                e.getMessage(), i18n("Error"));
            Global.logException(e, getClass());
        }
    }

    public boolean formClosing() {
        return false;
    }

    public void setSecurity() {
    }

    public String getAuditInfo() {
        return null;
    }

    public void dispose() {
        media.free();
        media = null;
        parent = null;
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(txtNotes);
        ctl.add(btnOk);
        ctl.add(btnCancel);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtNotes;
    }

    public void initComponents() {
        setLayout(UI.getBorderLayout());

        UI.Panel pnlNotes = UI.getPanel(UI.getBorderLayout());
        UI.Panel pnlButtons = UI.getPanel(UI.getFlowLayout());

        pnlNotes.add(UI.getLabel(UI.ALIGN_LEFT, i18n("Notes:")),
            UI.BorderLayout.NORTH);
        txtNotes = (UI.TextArea) UI.addComponent(pnlNotes,
                UI.getTextArea(i18n("Notes_about_the_animal,_if_you_are_using_the_internet_plugin,_these_will_be_used_as_the_animal's_description")));

        btnOk = (UI.Button) pnlButtons.add(UI.getButton(i18n("Ok"), null, 'o',
                    null, UI.fp(this, "saveData")));
        btnCancel = (UI.Button) pnlButtons.add(UI.getButton(i18n("Cancel"),
                    null, 'c', null, UI.fp(this, "dispose")));

        add(pnlNotes, UI.BorderLayout.CENTER);
        add(pnlButtons, UI.BorderLayout.SOUTH);
    }

    public boolean saveData() {
        // Save the notes back to the object and store it in the database
        try {
            media.setMediaNotes(txtNotes.getText());

            // Set the updated flag only if this animal hasn't already
            // been published
            if (media.getNewSinceLastPublish().intValue() == 0) {
                media.setUpdatedSinceLastPublish(new Integer(1));
            }

            media.save();
            
            if (AuditTrail.enabled())
            	AuditTrail.changed("media",
            		LookupCache.getMediaLinkForID(media.getLinkTypeID()) + " " +
            		media.getLinkID() + " " + media.getMediaName());
            
            parent.updateList();
            dispose();

            return true;
        } catch (CursorEngineException e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }

        return false;
    }
}
