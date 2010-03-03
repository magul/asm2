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
package net.sourceforge.sheltermanager.asm.ui.internet;

import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.internet.PetFinderPublisher;
import net.sourceforge.sheltermanager.asm.internet.Pets911Publisher;
import net.sourceforge.sheltermanager.asm.internet.PublishCriteria;
import net.sourceforge.sheltermanager.asm.internet.Publisher;
import net.sourceforge.sheltermanager.asm.internet.RescueGroupsPublisher;
import net.sourceforge.sheltermanager.asm.internet.SaveAPetPublisher;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableItem;
import net.sourceforge.sheltermanager.asm.ui.ui.SelectableList;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.ArrayList;
import java.util.Vector;


/**
 * This class is a front end for publishing animals available for adoption to
 * either a standalone website or to various animal publishing services.
 *
 * @author Robin Rawson-Tetley
 * @version 3.0
 */
public class InternetPublisher extends ASMForm {
    public final static int MODE_HTML = 0;
    public final static int MODE_PETFINDER = 1;
    public final static int MODE_PETS911 = 2;
    public final static int MODE_SAVEAPET = 3;
    public final static int MODE_RESCUEGROUPS = 4;

    /** Keeps track of all the location check box components */
    public Vector locationCheckboxes = new Vector();
    public UI.Button btnClose;
    public UI.Button btnPublish;
    public SelectableList options;
    private int mode = MODE_HTML;

    public InternetPublisher(int mode) {
        this.mode = mode;

        switch (mode) {
        case MODE_HTML:
            init(Global.i18n("uiinternet",
                    "Publish_Animals_Available_For_Adoption"),
                IconManager.getIcon(IconManager.SCREEN_PUBLISH), "uiinternet");

            break;

        case MODE_PETFINDER:
            init(Global.i18n("uiinternet",
                    "Publish_Available_Animals_To_PetFinder.org"),
                IconManager.getIcon(IconManager.SCREEN_PETFINDERPUBLISH),
                "uiinternet");

            break;

        case MODE_PETS911:
            init(Global.i18n("uiinternet",
                    "Publish_Available_Animals_To_Pets911.com"),
                IconManager.getIcon(IconManager.SCREEN_PETS911PUBLISH),
                "uiinternet");

            break;

        case MODE_SAVEAPET:
            init(Global.i18n("uiinternet",
                    "Publish_Available_Animals_To_1800SaveAPet"),
                IconManager.getIcon(IconManager.SCREEN_SAVEAPETPUBLISH),
                "uiinternet");

            break;

        case MODE_RESCUEGROUPS:
            init(Global.i18n("uiinternet",
                    "Publish_Available_Animals_To_RescueGroups"),
                IconManager.getIcon(IconManager.SCREEN_RESCUEGROUPSPUBLISH),
                "uiinternet");

            break;
        }
    }

    public void dispose() {
        locationCheckboxes.removeAllElements();
        locationCheckboxes = null;
        unregisterTabOrder();
        super.dispose();
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(options);
        ctl.add(btnPublish);
        ctl.add(btnClose);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return options;
    }

    public void runPublisher() {
        actionPublish();
    }

    public String getAuditInfo() {
        return null;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public void setSecurity() {
    }

    public void initComponents() {
        // Basic inclusion
        ArrayList l = new ArrayList();
        l.add(new SelectableItem(i18n("Include"), null, false, true));
        l.add(new SelectableItem(i18n("Include_Reserved_Animals"),
                "IncludeReserved", false, false));
        l.add(new SelectableItem(i18n("Include_Fostered_Animals"),
                "IncludeFostered", false, false));
        l.add(new SelectableItem(i18n("Include_Case_Animals"), "IncludeCase",
                false, false));
        l.add(new SelectableItem(i18n("Include_Without_Image"),
                "IncludeNoImage", false, false));

        // Aged under
        l.add(new SelectableItem(i18n("Exclude_animals_aged_under:"), null,
                false, true));
        l.add(new SelectableItem(i18n("x_weeks", "52"), "under52", false, false,
                "weeks"));
        l.add(new SelectableItem(i18n("x_weeks", "26"), "under26", true, false,
                "weeks"));
        l.add(new SelectableItem(i18n("x_weeks", "20"), "under20", false, false,
                "weeks"));
        l.add(new SelectableItem(i18n("x_weeks", "16"), "under16", false, false,
                "weeks"));
        l.add(new SelectableItem(i18n("x_weeks", "8"), "under8", false, false,
                "weeks"));
        l.add(new SelectableItem(i18n("1_week"), "under1", false, false, "weeks"));

        // Locations
        l.add(new SelectableItem(i18n("Include_Animals_In_Location:"), null,
                false, true));

        try {
            SQLRecordset r = LookupCache.getInternalLocationLookup();
            r.moveFirst();

            while (!r.getEOF()) {
                l.add(new SelectableItem(r.getField("LocationName").toString(),
                        "location" + r.getField("ID"), true, false));
                r.moveNext();
            }
        } catch (Exception e) {
            Global.logException(e, InternetPublisher.class);
        }

        // Animals per page (only valid for html)
        if (mode == MODE_HTML) {
            l.add(new SelectableItem(i18n("Animals_Per_Page:"), null, false,
                    true));
            l.add(new SelectableItem(i18n("Unlimited"), "page999999", false,
                    false, "page"));
            l.add(new SelectableItem("20", "page20", false, false, "page"));
            l.add(new SelectableItem("15", "page15", false, false, "page"));
            l.add(new SelectableItem("10", "page10", true, false, "page"));
            l.add(new SelectableItem("5", "page5", false, false, "page"));
        }

        // Upload Options (only valid for html)
        if (mode == MODE_HTML) {
            l.add(new SelectableItem(i18n("Upload_Options"), null, false, true));
            l.add(new SelectableItem(i18n("Upload_directly_to_the_internet"),
                    "UploadDirectly", true, false));
            l.add(new SelectableItem(i18n("Upload_all_animal_images"),
                    "UploadAllImages", false, false));
            l.add(new SelectableItem(i18n("Force_Reupload"), "ForceReupload",
                    false, false));
            l.add(new SelectableItem(i18n("Generate_JavaScript_database"),
                    "GenerateJavascript", true, false));
        }

        // Upload Options (for petfinder)
        if (mode == MODE_PETFINDER) {
            l.add(new SelectableItem(i18n("Upload_Options"), null, false, true));
            l.add(new SelectableItem(i18n("Upload_all_animal_images"),
                    "UploadAllImages", true, false));
            l.add(new SelectableItem(i18n("Force_Reupload"), "ForceReupload",
                    false, false));
        }

        // Upload Options (for saveapet)
        if (mode == MODE_SAVEAPET) {
            l.add(new SelectableItem(i18n("Upload_Options"), null, false, true));
            l.add(new SelectableItem(i18n("Include_colours"), "IncludeColours",
                    false, false));
            l.add(new SelectableItem(i18n("Force_Reupload"), "ForceReupload",
                    false, false));
        }

        // Upload Options (for pets911)
        if (mode == MODE_PETS911) {
            l.add(new SelectableItem(i18n("Upload_Options"), null, false, true));
            l.add(new SelectableItem(i18n("Force_Reupload"), "ForceReupload",
                    false, false));
        }

        // Upload Options (for rescuegroups)
        if (mode == MODE_RESCUEGROUPS) {
            l.add(new SelectableItem(i18n("Upload_Options"), null, false, true));
            l.add(new SelectableItem(i18n("Upload_all_animal_images"),
                    "UploadAllImages", true, false));
            l.add(new SelectableItem(i18n("Force_Reupload"), "ForceReupload",
                    false, false));
        }

        // Style (only valid for html)
        if (mode == MODE_HTML) {
            l.add(new SelectableItem(i18n("Style"), null, false, true));

            Vector v = Publisher.getStyles();

            for (int i = 0; i < v.size(); i++) {
                l.add(new SelectableItem(v.get(i).toString(),
                        "style" + v.get(i).toString(),
                        ((i == 0) ? true : false), false, "style"));
            }
        }

        // File extension (only valid for html)
        if (mode == MODE_HTML) {
            l.add(new SelectableItem(i18n("File_Extension"), null, false, true));
            l.add(new SelectableItem(".html", "exthtml", true, false, "ext"));
            l.add(new SelectableItem(".xml", "extxml", false, false, "ext"));
            l.add(new SelectableItem(".cgi", "extcgi", false, false, "ext"));
            l.add(new SelectableItem(".php", "extphp", false, false, "ext"));
            l.add(new SelectableItem(".py", "extpy", false, false, "ext"));
            l.add(new SelectableItem(".rb", "extrb", false, false, "ext"));
            l.add(new SelectableItem(".jsp", "extjsp", false, false, "ext"));
            l.add(new SelectableItem(".aspx", "extaspx", false, false, "ext"));
        }

        // Image scaling options
        l.add(new SelectableItem(i18n("Scale_Images:"), null, false, true));
        l.add(new SelectableItem(i18n("None"), "scaleNone", true, false, "scale"));
        l.add(new SelectableItem("320x200", "scale320x200", false, false,
                "scale"));
        l.add(new SelectableItem("300x300", "scale300x300", false, false,
                "scale"));
        l.add(new SelectableItem("95x95", "scale95x95", false, false, "scale"));

        options = new SelectableList(l);
        add(options, UI.BorderLayout.CENTER);

        btnPublish = UI.getButton(i18n("Publish"), null, 'p', null,
                UI.fp(this, "actionPublish"));
        btnClose = UI.getButton(i18n("Close"), null, 'c', null,
                UI.fp(this, "dispose"));

        UI.Panel p = UI.getPanel(UI.getFlowLayout());
        p.add(btnPublish);
        p.add(btnClose);
        add(p, UI.BorderLayout.SOUTH);
    }

    public boolean formClosing() {
        // Only allow closing if the publish button is enabled (so we aren't
        // publishing)
        return !btnPublish.isEnabled();
    }

    public void actionPublish() {
        // Deactivate publish and close button once started
        btnClose.setEnabled(false);
        btnPublish.setEnabled(false);

        SelectableItem[] s = options.getSelections();
        PublishCriteria pc = new PublishCriteria();
        Vector selectedLocations = new Vector();

        // Set defaults for mode
        if (mode != MODE_HTML) {
            pc.uploadDirectly = true;
        }

        for (int i = 0; i < s.length; i++) {
            if (s[i].getValue() == null) {
                continue; // title
            }

            if (s[i].getValue().equals("IncludeReserved")) {
                pc.includeReserved = s[i].isSelected();
            }

            if (s[i].getValue().equals("IncludeFostered")) {
                pc.includeFosters = s[i].isSelected();
            }

            if (s[i].getValue().equals("IncludeCase")) {
                pc.includeCase = s[i].isSelected();
            }

            if (s[i].getValue().equals("IncludeNoImage")) {
                pc.includeWithoutImage = s[i].isSelected();
            }

            if (s[i].getValue().equals("UploadDirectly")) {
                pc.uploadDirectly = s[i].isSelected();
            }

            if (s[i].getValue().equals("IncludeColours")) {
                pc.includeColours = s[i].isSelected();
            }

            if (s[i].getValue().equals("UploadAllImages")) {
                pc.uploadAllImages = s[i].isSelected();
            }

            if (s[i].getValue().equals("ForceReupload")) {
                pc.forceReupload = s[i].isSelected();
            }

            if (s[i].getValue().equals("GenerateJavascript")) {
                pc.generateJavascriptDB = s[i].isSelected();
            }

            // Aged Under
            if (s[i].getValue().toString().startsWith("under")) {
                if (s[i].isSelected()) {
                    int val = Integer.parseInt(s[i].getValue().toString()
                                                   .substring(5));
                    pc.excludeUnderWeeks = val;
                }
            }

            // Animals per page
            if (s[i].getValue().toString().startsWith("page")) {
                if (s[i].isSelected()) {
                    int val = Integer.parseInt(s[i].getValue().toString()
                                                   .substring(4));
                    pc.animalsPerPage = val;
                }
            }

            // Scale
            if (s[i].getValue().toString().startsWith("scale")) {
                if (s[i].isSelected()) {
                    if (s[i].getValue().equals("scaleNone")) {
                        pc.scaleImages = 1;
                    }

                    if (s[i].getValue().equals("scale320x200")) {
                        pc.scaleImages = 2;
                    }

                    if (s[i].getValue().equals("scale300x300")) {
                        pc.scaleImages = 6;
                    }

                    if (s[i].getValue().equals("scale95x95")) {
                        pc.scaleImages = 7;
                    }
                }
            }

            // Style
            if (s[i].getValue().toString().startsWith("style")) {
                if (s[i].isSelected()) {
                    String val = s[i].getValue().toString().substring(5);
                    pc.style = val;
                }
            }

            // Extension
            if (s[i].getValue().toString().startsWith("ext")) {
                if (s[i].isSelected()) {
                    String val = s[i].getValue().toString().substring(3);
                    pc.extension = val;
                }
            }

            // Locations
            if (s[i].getValue().toString().startsWith("location")) {
                if (s[i].isSelected()) {
                    String val = s[i].getValue().toString().substring(8);
                    selectedLocations.add(val);
                }
            }
        }

        pc.internalLocations = selectedLocations.toArray();

        // Start the processing
        switch (mode) {
        case MODE_HTML:

            Publisher p = new Publisher(this, pc);
            p.start();
            p = null;

            break;

        case MODE_PETFINDER:

            PetFinderPublisher pf = new PetFinderPublisher(this, pc);
            pf.start();
            pf = null;

            break;

        case MODE_PETS911:

            Pets911Publisher pn = new Pets911Publisher(this, pc);
            pn.start();
            pn = null;

            break;

        case MODE_SAVEAPET:

            SaveAPetPublisher sp = new SaveAPetPublisher(this, pc);
            sp.start();
            sp = null;

            break;

        case MODE_RESCUEGROUPS:

            RescueGroupsPublisher rg = new RescueGroupsPublisher(this, pc);
            rg.start();
            rg = null;

            break;
        }
    }
}
