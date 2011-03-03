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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.HTMLViewer;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;


/**
 * The HTML publisher
 *
 * @author Robin Rawson-Tetley
 * @version 3.0
 */
public class HTMLPublisher extends FTPPublisher {
    /** The javaScript file */
    private String javaScript = "";
    private int javaArrayElement = 0;

    /** The navigation bar HTML */
    private String navBar = "";

    /** The number of animals to be published */
    private int totalAnimals = 0;

    public HTMLPublisher(InternetPublisher parent,
        PublishCriteria publishCriteria) {
        init("publish", parent, publishCriteria,
            Configuration.getString("FTPURL"),
            Configuration.getString("FTPUser"),
            Configuration.getString("FTPPassword"),
            Configuration.getString("FTPPort"),
            Configuration.getString("FTPRootDirectory"));
    }

    public void run() {
        // Get the header, footer and body information
        setStatusText(Global.i18n("uiinternet", "retrieving_page_templates"));

        String normHeader = getHeader();
        String normFooter = getFooter();
        String header = ""; // These are updated every page
        String footer = ""; // because the navbar changes every page
        String body = getBody();

        // Get a list of animals
        setStatusText(Global.i18n("uiinternet", "retrieving_animal_list"));

        Animal an = null;

        try {
            an = getMatchingAnimals();
        } catch (Exception e) {
            Global.logException(e, getClass());

            if (parent != null) {
                Dialog.showError(e.getMessage());
            } else {
                System.exit(1);
            }
        }

        // If there aren't any animals, there's no point do
        // anything
        if (an.size() == 0) {
            if (parent != null) {
                Dialog.showInformation(Global.i18n("uiinternet",
                        "No_matching_animals_were_found_to_publish"));

                return;
            } else {
                Global.logError(Global.i18n("uiinternet",
                        "No_matching_animals_were_found_to_publish"),
                    "HTMLPublisher.run");
                System.exit(1);
            }
        }

        // Open the FTP socket
        openFTPSocket();

        // Flush any HTML files already present on the host
        if (publishCriteria.uploadDirectly && publishCriteria.clearExisting) {
            clearExistingHTML();
        }

        // Start the progress meter
        initStatusBarMax(an.size());

        // Calculate how many animal records will match
        // for the NavBar information and thus how many
        // pages will be generated
        totalAnimals = an.size();

        int noPages = 0;
        int animalsPerPage = publishCriteria.animalsPerPage;

        // Calculate the number of pages required
        if (totalAnimals <= animalsPerPage) {
            noPages = 1;
        } else {
            double calc = ((double) totalAnimals / (double) animalsPerPage);
            double wholevalue = (double) Math.round(calc);

            // If there is a remainder (ie. calc > wholevalue),
            // then we need wholevalue + 1 pages - eg. 5.2 pages
            // required means 6 are needed.
            if (calc > wholevalue) {
                noPages = (int) wholevalue;
                noPages++;
            } else {
                noPages = (int) wholevalue;
            }
        }

        // Build the page navigation bar
        if (noPages > 1) {
            StringBuffer theNav = new StringBuffer("");

            for (int i = 1; i <= noPages; i++) {
                theNav.append("<a href=\"" + Integer.toString(i) + "." +
                    publishCriteria.extension + "\">" + Integer.toString(i) +
                    "</a>&nbsp;");
            }

            navBar = theNav.toString();
        }

        // Start a new page with a header
        String thisPageName = "1." + publishCriteria.extension;
        int currentPage = 1;
        int itemsOnPage = 0;

        // Substitute tags in the header and footer for this page
        header = substituteHFTag(normHeader, currentPage);
        footer = substituteHFTag(normFooter, currentPage);

        StringBuffer thisPage = new StringBuffer(header);
        setStatusText(Global.i18n("uiinternet", "Publishing..."));

        try {
            int anCount = 0;

            while (!an.getEOF()) {
                anCount++;

                // If a limit was set and we hit it, stop now
                if ((publishCriteria.limit > 0) &&
                        (anCount > publishCriteria.limit)) {
                    Global.logInfo("Hit publish limit of " +
                        publishCriteria.limit + ", stopping.", "Publisher.run");

                    break;
                }

                Global.logInfo("Processing: " + an.getShelterCode() + ": " +
                    an.getAnimalName() + " (" + anCount + " of " +
                    totalAnimals + ")", "Publisher.run");

                // Upload all the images for this animal to our 
                // current FTP directory
                uploadImages(an, true);

                // Update our ongoing JavaScript database if
                // the option is switched on
                if (publishCriteria.generateJavascriptDB) {
                    updateJavaScript(an);
                }

                // Is there a slot free to put it on this page?
                if (itemsOnPage < animalsPerPage) {
                    // Yes - do so
                    thisPage.append(substituteBodyTags(body, an));
                    itemsOnPage++;

                    // Clear the new/updated flags for the animal
                    // we just published
                    an.clearWebMediaFlags();

                    Global.logInfo("Finished processing " +
                        an.getShelterCode(), "Publisher.run");
                } else {
                    Global.logInfo("Current page complete.", "Publisher.run");

                    // No, append the footer, flush the page
                    // out to the publish folder and optionally
                    // upload it to the internet site
                    thisPage.append(footer);

                    Global.logInfo("Saving page to disk.", "Publisher.run");
                    saveFile(publishDir + thisPageName, thisPage.toString());
                    Global.logInfo("Page saved to disk.", "Publisher.run");

                    // Upload if the option is set
                    if (publishCriteria.uploadDirectly) {
                        Global.logInfo("Uploading page.", "Publisher.run");

                        upload(thisPageName);
                        Global.logInfo("Page uploaded.", "Publisher.run");
                    }

                    // Start a new page
                    currentPage++;
                    thisPageName = Integer.toString(currentPage) + "." +
                        publishCriteria.extension;

                    // Substitute tags in the header and footer for this
                    // page
                    header = substituteHFTag(normHeader, currentPage);
                    footer = substituteHFTag(normFooter, currentPage);

                    thisPage = new StringBuffer(header);
                    itemsOnPage = 0;

                    // append this animal
                    thisPage.append(substituteBodyTags(body, an));
                    itemsOnPage++;

                    Global.logInfo("Finished processing" + an.getShelterCode(),
                        "Publisher.run");
                }

                // Mark media records for this animal as published
                markAnimalPublished("LastPublished", an.getID());

                an.moveNext();
                incrementStatusBar();
            }
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());
        }

        // Remove animal object
        an.free();
        an = null;

        // Animals have now finished. Append a footer, Flush the final
        // page to disk and clean up
        thisPage.append(footer);
        saveFile(publishDir + thisPageName, thisPage.toString());

        Global.logInfo("Uploading final page.", "Publisher.run");
        upload(thisPageName);
        Global.logInfo("Final page uploaded.", "Publisher.run");

        // Upload the JavaScript database if we are doing that
        if (publishCriteria.generateJavascriptDB) {
            writeJavaScript();
        }

        resetStatusBar();
        setStatusText("");

        // If uploading was on, disconnect from the remote host
        closeFTPSocket();

        // Save any additional images required by the template
        saveTemplateImages(publishDir);

        // Ask them if they want to view the completed pages
        if (parent != null) {
            if (Dialog.showYesNo(Global.i18n("uiinternet",
                            "Web_site_creation_completed.\nWould_you_like_to_view_the_pages_now?"),
                        Global.i18n("uiinternet", "View_Site?"))) {
                display();
            }
        } else {
            Global.logInfo("Publishing complete.", "Publisher.run");
            System.exit(0);
        }

        // Re-enable buttons
        if (parent != null) {
            parent.btnClose.setEnabled(true);
            parent.btnPublish.setEnabled(true);
        }
    }

    private String getHeader() {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);

            // Get a directory listing to see if pih.dat is there
            String[] list = dbfs.list("pih.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return getDefaultHeader();
            }

            // Get it as a String
            return dbfs.readFileToString("pih.dat");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return getDefaultHeader();
        }
    }

    private String getFooter() {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);

            // Get a directory listing to see if pif.dat is there
            String[] list = dbfs.list("pif.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return getDefaultFooter();
            }

            // Get it as a String
            return dbfs.readFileToString("pif.dat");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return getDefaultFooter();
        }
    }

    private String getBody() {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);

            // Get a directory listing to see if pib.dat is there
            String[] list = dbfs.list("pib.dat");

            if (list.length == 0) {
                // It doesn't return the default
                return getDefaultBody();
            }

            // Get it as a String
            return dbfs.readFileToString("pib.dat");
        } catch (Exception e) {
            if (parent != null) {
                Dialog.showError(e.getMessage());
            }

            Global.logException(e, getClass());

            return getDefaultBody();
        }
    }

    /** Changes to the correct directory for
     *  our selected style */
    private void changeForStyle(DBFS dbfs) {
        try {
            dbfs.chdir("internet");

            String d = publishCriteria.style;

            // Done if we're on the base style
            if (d.equals(".")) {
                return;
            }

            // Change to style dir
            dbfs.chdir(d);
        } catch (Exception e) {
            Global.logException(e, HTMLPublisher.class);
        }
    }

    private String getDefaultHeader() {
        String s = "<html><head><title>Animals Available for Adoption</title></head>";
        s += "<body>";
        s += "<p>$$NAV$$</p>";
        s += "<p><table width=100%>";

        return s;
    }

    private String getDefaultFooter() {
        String s = "</table></body></html>";

        return s;
    }

    private String getDefaultBody() {
        return "<tr><td><img height=200 width=320 src=$$IMAGE$$></td><td><b>$$ShelterCode$$ - $$AnimalName$$</b><br>$$BreedName$$ $$SpeciesName$$ aged $$Age$$<br><br><b>Details</b><br><br>$$WebMediaNotes$$<hr></td></tr>";
    }

    /**
     * Exchanges special tags in the passed string for information read from the
     * system.
     *
     * @param searchin
     *            The string to search for keys in
     * @return The string with keys replaced
     */
    private String substituteHFTag(String searchin, int page) {
        // Make output string
        String output = new String(searchin);
        String todaysdate = Utils.getReadableTodaysDate();

        // $$NAV$$ tag
        String navThisPage = Utils.replace(navBar,
                "<a href=" + page + "." + publishCriteria.extension + ">" +
                page + "</a>", Integer.toString(page));
        output = Utils.replace(output, "$$NAV$$", navThisPage);

        // $$TOTAL$$ tag
        output = Utils.replace(output, "$$TOTAL$$",
                Integer.toString(totalAnimals));

        // $$DATE$$ tag //
        output = Utils.replace(output, "$$DATE$$", todaysdate);
        // $$VERSION$$ tag //
        output = Utils.replace(output, "$$VERSION$$", Global.productVersion);

        // $$REGISTEREDTO$$ tag //
        output = Utils.replace(output, "$$REGISTEREDTO$$",
                Configuration.getString("Organisation"));

        try {
            // $$USER$$ tag //
            if ((Global.currentUserName != null) &&
                    (Global.currentUserObject != null)) {
                output = Utils.replace(output, "$$USER$$",
                        Global.currentUserName + " (" +
                        Global.currentUserObject.getRealName() + ")");
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // $$ORGNAME$$ tag //
        output = Utils.replace(output, "$$ORGNAME$$",
                Configuration.getString("Organisation"));

        // $$ORGADDRESS$$ tag //
        output = Utils.replace(output, "$$ORGADDRESS$$",
                Configuration.getString("OrganisationAddress"));

        // $$ORGTEL$$ tag //
        output = Utils.replace(output, "$$ORGTEL$$",
                Configuration.getString("OrganisationTelephone"));

        // $$ORGEMAIL$$ tag //
        output = Utils.replace(output, "$$ORGEMAIL$$",
                Configuration.getString("EmailAddress"));

        return output;
    }

    private String substituteBodyTags(String findin, Animal a) {
        // Create a new animal document so we can use the tags
        AnimalDocument ad = new AnimalDocument(a, true);

        // Replace them and return
        return ad.replaceInTextInternet(findin);
    }

    /**
     * Displays the site to the user
     */
    private void display() {
        String filename = Global.tempDirectory + File.separator + "publish" +
            File.separator + "1." + publishCriteria.extension;

        if (Global.useInternalReportViewer) {
            Global.mainForm.addChild(new HTMLViewer("file:///" + filename));
        } else {
            FileTypeManager.shellExecute(filename);
        }
    }

    /**
     * Writes the JavaScript database out to a file called db.js
     */
    private void writeJavaScript() {
        // Add declarations to the front
        String decs = "";
        String idx = Integer.toString(javaArrayElement);

        decs += ("var publishDate = \"" + Utils.getReadableTodaysDate() +
        "\";\n");
        decs += ("var aname = new Array(" + idx + ");\n");
        decs += ("var age = new Array(" + idx + ");\n");
        decs += ("var image = new Array(" + idx + ");\n");
        decs += ("var breed = new Array(" + idx + ");\n");
        decs += ("var species = new Array(" + idx + ");\n");
        decs += ("var type = new Array(" + idx + ");\n");
        decs += ("var colour = new Array(" + idx + ");\n");
        decs += ("var shelterlocation = new Array(" + idx + ");\n");
        decs += ("var markings = new Array(" + idx + ");\n");
        decs += ("var details = new Array(" + idx + ");\n");
        decs += ("var sheltercode = new Array(" + idx + ");\n");
        decs += ("var dateofbirth = new Array(" + idx + ");\n");
        decs += ("var sex = new Array(" + idx + ");\n");
        decs += ("var size = new Array(" + idx + ");\n");
        decs += ("var goodwithkids = new Array(" + idx + ");\n");
        decs += ("var goodwithcats = new Array(" + idx + ");\n");
        decs += ("var goodwithdogs = new Array(" + idx + ");\n");
        decs += ("var housetrained = new Array(" + idx + ");\n");
        decs += ("var comments = new Array(" + idx + ");\n");

        javaScript = decs + javaScript;

        saveFile(Global.tempDirectory + File.separator + "publish" +
            File.separator + "db.js", javaScript);

        // If uploading is on, upload it too
        Global.logInfo("Uploading javascript database.", "Publisher.run");
        upload("db.js");
        Global.logInfo("Uploaded javascript database.", "Publisher.run");
    }

    /**
     * Updates the JavaScript database while building for use with internet site
     * search facilities.
     */
    private void updateJavaScript(Animal a) {
        // Builds up javascript instructions to create
        // multiple arrays reflecting our data. A clever frontend
        // program can then use the file to allow interactive
        // searching by a client-side browser script.
        try {
            String idx = Integer.toString(javaArrayElement);

            javaScript += ("aname[" + idx + "] = \"" + a.getAnimalName() +
            "\";\n");
            javaScript += ("image[" + idx + "] = \"" +
            (a.getWebMedia().equals("") ? "nopic.jpg" : a.getWebMedia()) +
            "\";\n");
            javaScript += ("age[" + idx + "] = \"" + a.getAge() + "\";\n");
            javaScript += ("breed[" + idx + "] = \"" + a.getBreedName() +
            "\";\n");
            javaScript += ("species[" + idx + "] = \"" + a.getSpeciesName() +
            "\";\n");
            javaScript += ("type[" + idx + "] = \"" + a.getAnimalTypeName() +
            "\";\n");
            javaScript += ("colour[" + idx + "] = \"" + a.getBaseColourName() +
            "\";\n");
            javaScript += ("shelterlocation[" + idx + "] = \"" +
            a.getShelterLocationName() + "\";\n");
            javaScript += ("markings[" + idx + "] = \"" +
            fixJSText(a.getMarkings()) + "\";\n");
            javaScript += ("details[" + idx + "] = \"" +
            fixJSText(a.getWebMediaNotes()) + "\";\n");
            javaScript += ("sheltercode[" + idx + "] = \"" +
            a.getShelterCode() + "\";\n");
            javaScript += ("dateofbirth[" + idx + "] = \"" +
            Utils.formatDateLong(a.getDateOfBirth()) + "\";\n");
            javaScript += ("sex[" + idx + "] = \"" + a.getSexName() + "\";\n");
            javaScript += ("size[" + idx + "] = \"" + a.getSizeName() +
            "\";\n");
            javaScript += ("goodwithkids[" + idx + "] = \"" +
            ((a.isGoodWithKids().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("goodwithcats[" + idx + "] = \"" +
            ((a.isGoodWithCats().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("goodwithdogs[" + idx + "] = \"" +
            ((a.isGoodWithDogs().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("housetrained[" + idx + "] = \"" +
            ((a.isHouseTrained().intValue() == 0)
            ? Global.i18n("uiinternet", "Yes") : Global.i18n("uiinternet", "No")) +
            "\";\n");
            javaScript += ("comments[" + idx + "] = \"" +
            fixJSText(a.getAnimalComments()) + "\";\n");

            javaArrayElement++;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Used by javascript outputter - it checks the given text for line breaks
     * and speech marks -- speechmarks become apostrophes, and line breaks
     * become spaces.
     *
     * @param text
     *            The text to check
     * @return the text with apostrophes and line breaks converted.
     */
    private String fixJSText(String text) {
        text = text.replace('"', '\'');
        text = Utils.replace(text, new String(new byte[] { 13, 10 }), " ");
        text = text.replace('\n', ' ');

        return text;
    }

    /**
     * Saves all the images from a template to the publish
     * directory.
     */
    private void saveTemplateImages(String publishDir) {
        try {
            DBFS dbfs = new DBFS();
            changeForStyle(dbfs);
            dbfs.saveAllImages(publishDir);
        } catch (Exception e) {
            Global.logException(e, HTMLPublisher.class);
        }
    }
}
