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

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.reportviewer.ReportViewer;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.wordprocessor.SelectTemplate;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.utility.WordProcessorListener;
import net.sourceforge.sheltermanager.dbfs.Base64;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 *
 * Superclass for classes wanting to generate templates for a particular object.
 *
 * @version 1.0
 * @author Robin Rawson-Tetley
 */
public abstract class GenerateDocument extends Thread
    implements WordProcessorListener {
    public final static String OPENOFFICE_3 = Global.i18n("wordprocessor",
            "OpenOffice_3");
    public final static String OPENOFFICE_2 = Global.i18n("wordprocessor",
            "OpenOffice_2");
    public final static String OPENOFFICE_1 = Global.i18n("wordprocessor",
            "OpenOffice_1");
    public final static String MICROSOFT_OFFICE_2007 = Global.i18n("wordprocessor",
            "Microsoft_Office_2007");
    public final static String ABIWORD = Global.i18n("wordprocessor", "AbiWord");
    public final static String RICH_TEXT = Global.i18n("wordprocessor",
            "Rich_Text_Format");
    public final static String XML = Global.i18n("wordprocessor", "XML");
    public final static String HTML = Global.i18n("wordprocessor", "HTML");

    /** The file name and path of the local file we are altering */
    protected String localfile = "";
    protected String justFilename = "";
    protected String docTitle = "";
    protected String templateName = "";
    protected boolean attachMedia = false;
    protected boolean markedUp = true;

    /**
     * Vector of SearchTag objects, containing information on search and
     * replaces. Deliberately public, so additional subclasses can be created
     * and merged into this one.
     */
    public Vector<SearchTag> searchtags = new Vector<SearchTag>();

    /** Used to determine whether this thread should kill itself */
    protected boolean isFinished = false;

    /**
     * Determines whether this thread is ready to process its search tags and do
     * the job
     */
    protected boolean isReady = false;

    /**
     * Should be called by subclass constructors - makes call to get a template
     * from the user and starts our main thread off.
     */
    public void generateDocument() {
        getTemplate();
        this.start();
    }

    /**
     * Main threaded routine. Sits waiting for either the user to cancel or
     * select a template, then does the business, outputting to the status bar
     * where necessary.
     */
    public void run() {
        while (!getIsFinished())

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

        if (getIsReady()) {
            Global.mainForm.setStatusText("Building document...");

            // Determine if we need to mark up our tags - basically, we require
            // XML escaping for everything that's not rich text
            String wpname = Configuration.getString("DocumentWordProcessor");
            markedUp = !wpname.equalsIgnoreCase(RICH_TEXT);

            generateSearchTags();
            processFile();
            Global.mainForm.setStatusText("");
            Global.localCache.addEntry(justFilename, docTitle);

            if (getAttachMedia()) {
                attachMedia();
            }

            // Start this code off again so that users can
            // pick another template
            setIsFinished(false);
            setIsReady(false);

            run();
        }
    }

    protected synchronized boolean getIsFinished() {
        return isFinished;
    }

    protected synchronized void setIsFinished(boolean b) {
        isFinished = b;
    }

    protected synchronized boolean getIsReady() {
        return isReady;
    }

    protected synchronized void setAttachMedia(boolean b) {
        attachMedia = b;
    }

    protected synchronized boolean getAttachMedia() {
        return attachMedia;
    }

    protected synchronized void setIsReady(boolean b) {
        isReady = b;
    }

    /** Requests the template to use from the user */
    protected void getTemplate() {
        SelectTemplate st = new SelectTemplate(this);
        Global.mainForm.addChild(st);
    }

    public void templateSelected(File file, String templatename, boolean attach) {
        localfile = file.getAbsolutePath();
        templateName = templatename;

        setFilename(localfile);
        setAttachMedia(attach);
        // We have a template - we are ready to do the processing
        setIsReady(true);
        // The thread wait mechanism is finished, so go do the job
        setIsFinished(true);
    }

    /**
     * Template was abandoned - mark us as finished, but not ready to do
     * anything so we just drop out.
     */
    public void templateAbandoned() {
        setIsReady(false);
        setIsFinished(true);

        // Clear up references
        localfile = null;
        justFilename = null;
        docTitle = null;
        templateName = null;
        searchtags = null;
    }

    /**
     * Override in subclass - this routine is responsible for generating the
     * list of tags that need to be changed in the resulting document.
     */
    public abstract void generateSearchTags();

    /**
     * Adds a new tag to the collection for processing.
     *
     * @param find
     *            The search part of the tag
     * @param replace
     *            The replace part of the tag
     */
    protected void addTag(String find, String replace) {
        // If find or replace is null, ignore it
        if ((find == null) || (replace == null)) {
            return;
        }

        // If replace is Null, swap it for an empty string.
        if (replace == null) {
            replace = "";
        }

        // If we're marking things up, substitute any disallowed
        // XML characters
        if (markedUp) {
            replace = Utils.replace(replace, "&", "&amp;");
            replace = Utils.replace(replace, "<", "&lt;");
            replace = Utils.replace(replace, ">", "&gt;");
        }

        SearchTag tag = new SearchTag();
        tag.find = find;
        tag.replace = replace;
        searchtags.add(tag);
    }

    /**
     * Discovers the word processor and delegates to the correct handler.
     */
    protected void processFile() {
        // Read the configuration to work out which Word Processor we have:
        String wpname = Configuration.getString("DocumentWordProcessor");

        if (wpname.equalsIgnoreCase(OPENOFFICE_3)) {
            processOpenOffice();
        } else if (wpname.equalsIgnoreCase(OPENOFFICE_2)) {
            processOpenOffice();
        } else if (wpname.equalsIgnoreCase(OPENOFFICE_1)) {
            processOpenOffice();
        } else if (wpname.equalsIgnoreCase(MICROSOFT_OFFICE_2007)) {
            processMOOXML();
        } else if (wpname.equalsIgnoreCase(ABIWORD)) {
            processAbiword();
        } else if (wpname.equalsIgnoreCase(RICH_TEXT)) {
            processRichText(true);
        } else if (wpname.equalsIgnoreCase(HTML)) {
            processXMLText(true);
        } else if (wpname.equalsIgnoreCase(XML)) {
            processXMLText(true);
        } else {
            Global.logError("Invalid word processor selection: " + wpname,
                "GenerateDocument.processFile");
        }
    }

    /**
     * Discovers the word processor and returns the file extension we should be
     * looking for:
     */
    public String getDocFileType() {
        // Read the configuration to work out which Word Processor we have:
        String wpname = Configuration.getString("DocumentWordProcessor");

        if (wpname.equalsIgnoreCase(OPENOFFICE_3)) {
            return "odt";
        }

        if (wpname.equalsIgnoreCase(OPENOFFICE_2)) {
            return "odt";
        }

        if (wpname.equalsIgnoreCase(OPENOFFICE_1)) {
            return "sxw";
        }

        if (wpname.equalsIgnoreCase(MICROSOFT_OFFICE_2007)) {
            return "docx";
        }

        if (wpname.equalsIgnoreCase(ABIWORD)) {
            return "abw";
        }

        if (wpname.equalsIgnoreCase(RICH_TEXT)) {
            return "rtf";
        }

        if (wpname.equalsIgnoreCase(HTML)) {
            return "html";
        }

        if (wpname.equalsIgnoreCase(XML)) {
            return "xml";
        }

        return "*";
    }

    /** Given a filename, returns true if the type is correct for the word
     *  processor chosen.
     * @param filename The file
     * @return true if the file is of the correct type for the selected word processor
     */
    public boolean isCorrectFileType(String filename) {
        String doctype = getDocFileType();

        if (doctype.equals("html")) {
            if (Utils.englishLower(filename).endsWith("html") ||
                    Utils.englishLower(filename).endsWith("htm")) {
                return true;
            } else {
                return false;
            }
        } else {
            return filename.endsWith(doctype);
        }
    }

    protected void processAbiword() {
        // Switch the tags
        processXMLText();

        // Switch any image
        processAbiwordImage();

        // Show
        display();
    }

    protected void processOpenOffice() {
        // Create the scratch area
        String oodir = Global.tempDirectory + File.separator + "openoffice";
        File f = new File(oodir);
        deleteDir(f);
        f.mkdirs();

        // Back up OO name
        String oofile = localfile;

        // OpenOffice files are PKZIPped containing the 
        // images and some XML files.
        // Unzip it to a work area.
        unzip(localfile, oodir);

        // Switch the tags in the content.xml file and add
        // any image
        localfile = oodir + File.separator + "content.xml";
        processOpenOfficeImage();
        processXMLText();

        // Zip the file back up again
        localfile = oofile;
        zip(localfile, oodir);

        // Remove the scratch area
        deleteDir(f);

        // Show
        display();
    }

    protected void processMOOXML() {
        // Create the scratch area
        String oodir = Global.tempDirectory + File.separator + "mooxml";
        File f = new File(oodir);
        deleteDir(f);
        f.mkdirs();

        // Back up OO name
        String oofile = localfile;

        // MOOXML files are PKZIPped containing the 
        // images and some XML files.
        // Unzip it to a work area.
        unzip(localfile, oodir);

        // Switch the tags in the document.xml file and add
        // any image
        localfile = oodir + File.separator + "word" + File.separator +
            "document.xml";
        processMOOXMLImage();
        processXMLText();

        // Zip the file back up again
        localfile = oofile;
        zip(localfile, oodir);

        // Remove the scratch area
        // deleteDir(f);

        // Show
        display();
    }

    /**
     * Recursively removes a directory and its contents
     * @param dir The directory to delete
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();

            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /** Unzips file into dir
     * @param file
     * @param dir
     */
    protected void unzip(String file, String dir) {
        Global.logDebug("Unzip: " + file + " into " + dir,
            "GenerateDocument.unzip");

        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }

        try {
            ZipFile z = new ZipFile(file);
            Enumeration<?extends ZipEntry> entries = z.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {
                    String newdir = dir + entry.getName();
                    Global.logDebug("Created directory: " + newdir,
                        "GenerateDocument.unzip");
                    new File(newdir).mkdirs();

                    continue;
                }

                String newfile = dir + entry.getName();
                Global.logDebug("Unpacking file: " + newfile,
                    "GenerateDocument.unzip");

                // This is zip separator, should still be valid for win?
                int lastseparator = newfile.lastIndexOf("/");

                if (lastseparator > 0) {
                    String newdir = newfile.substring(0, lastseparator);
                    Global.logDebug("Created directory: " + newdir,
                        "GenerateDocument.unzip");
                    new File(newdir).mkdirs();
                }

                Global.logDebug("Calling copyInputStream",
                    "GenerateDocument.unzip");
                copyInputStream(z.getInputStream(entry),
                    new BufferedOutputStream(new FileOutputStream(newfile)),
                    true, true);
                Global.logDebug("Exited copyInputStream",
                    "GenerateDocument.unzip");
            }

            Global.logDebug("Finished unpacking zip file",
                "GenerateDocument.unzip");
            z.close();
            Global.logDebug("Closed zip file", "GenerateDocument.unzip");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    protected void copyInputStream(InputStream in, OutputStream out,
        boolean closeIn, boolean closeOut) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        // If there's nothing in the buffer, bail out
        if (in.available() == 0) {
            Global.logDebug("Empty buffer, bailing",
                "GenerateDocument.copyInputStream");

            return;
        }

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        if (closeIn) {
            in.close();
        }

        if (closeOut) {
            out.close();
        }
    }

    /** Zips dir into file (not including dir)
     *  @param file
     *  @param dir
     */
    protected void zip(String file, String dir) {
        Global.logDebug("Zip: " + dir + " into " + file, "GenerateDocument.zip");

        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            out.setLevel(Deflater.DEFAULT_COMPRESSION);

            File curdir = new File(dir);
            zipAddFiles(out, "", curdir, curdir.list());
            out.close();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    protected void zipAddFiles(ZipOutputStream z, String zipdir, File curdir,
        String[] files) {
        try {
            for (int i = 0; i < files.length; i++) {
                File thisfile = new File(curdir.getAbsolutePath() +
                        File.separator + files[i]);

                if (!thisfile.isDirectory()) {
                    // Add the file to the zip
                    Global.logDebug("Adding file: " + zipdir +
                        thisfile.getName(), "GenerateDocument.zipAddFiles");
                    z.putNextEntry(new ZipEntry(zipdir + thisfile.getName()));

                    FileInputStream in = new FileInputStream(thisfile);
                    copyInputStream(in, z, false, false);
                    z.closeEntry();
                    in.close();
                    thisfile = null;
                } else {
                    // Add the directory, then recurse into it
                    Global.logDebug("Adding dir: " + thisfile.getName() +
                        File.separator, "GenerateDocument.zipAddFiles");

                    String newzipdir = zipdir + thisfile.getName() + "/"; // Always use forward slash for zip dirs
                    File newcurdir = new File(curdir.getAbsolutePath() +
                            File.separator + thisfile.getName());
                    z.putNextEntry(new ZipEntry(newzipdir));
                    z.closeEntry();
                    zipAddFiles(z, newzipdir, newcurdir, newcurdir.list());
                }
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Replaces a MooXML image - the placeholder image has to be
     * called placeholder.jpg and be exactly 2897 bytes - this code finds the
     * placeholder, then scans the word/media folder for a jpeg file exactly
     * 2897 bytes in size and replaces it with the animal's media
     */
    protected void processMOOXMLImage() {
        final int PLACEHOLDER_SIZE = 2897;

        String moodir = Global.tempDirectory + File.separator + "mooxml";

        try {
            // Grab the media and save it into the unpacked
            // mooxml file image, renaming it to image<id>.jpeg
            String mediafile = getImage();

            // Bail if we didn't have any media
            if (mediafile == null) {
                return;
            }

            // Search the word/media folder for our placeholder
            // image - we can recognise it by it's size
            File tg = new File(moodir + File.separator + "word" +
                    File.separator + "media");

            String[] dir = tg.list();
            if (dir != null) {
                for (int i = 0; i < dir.length; i++) {
                    if (dir[i].indexOf(".jpeg") != -1) {
                        String target = moodir + File.separator + "word" +
                            File.separator + "media" + File.separator + dir[i];
                        File ph = new File(target);

                        if (Global.showDebug) {
                            Global.logDebug("Found file: " + dir[i] + ", size=" +
                                ph.length(), "processMOOXMLImage");
                        }

                        // Is this file size correct? If so, delete it and
                        // replace it with our media file instead
                        if (ph.length() == PLACEHOLDER_SIZE) {
                            ph.delete();
                            Utils.renameFile(new File(mediafile), new File(target));

                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Dialog.showError("An error occurred adding media to the document: " +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Looks for draw:name="media" chunk in the OpenOffice
     * file - if it finds one, the href to the image is replaced
     * with a link to the animal's web media and it is included
     * in the document
     */
    protected void processOpenOfficeImage() {
        final int PLACEHOLDER_SIZE = 2897;
        String oodir = Global.tempDirectory + File.separator + "openoffice";

        Global.logDebug("Processing OpenOffice image...",
            "processOpenOfficeImage");

        try {
            // Grab the media and save it into the unpacked
            // openoffice file image
            String mediafile = getImage();

            // Bail if we didn't have any media
            if (mediafile == null) {
                Global.logDebug("Couldn't find any media, bailing out",
                    "processOpenOfficeImage");

                return;
            }

            // Search the Pictures folder for our placeholder
            // image - we can recognise it by it's size
            File tg = new File(oodir + File.separator + "Pictures");
            
            String[] dir = tg.list();
            if (dir != null) {
                for (int i = 0; i < dir.length; i++) {
                    if ((dir[i].indexOf(".jpeg") != -1) ||
                            (dir[i].indexOf(".jpg") != -1)) {
                        String target = oodir + File.separator + "Pictures" +
                            File.separator + dir[i];
                        File ph = new File(target);

                        if (Global.showDebug) {
                            Global.logDebug("Found file: " + dir[i] + ", size=" +
                                ph.length(), "processOpenOfficeImage");
                        }

                        // Is this file size correct? If so, delete it and
                        // replace it with our media file instead
                        if (ph.length() == PLACEHOLDER_SIZE) {
                            ph.delete();
                            Utils.renameFile(new File(mediafile), new File(target));

                            break;
                        }
                    }
                }
            }

            Global.logDebug("Finished processing OpenOffice image.",
                "GenerateDocument.processOpenOfficeImage");
        } catch (Exception e) {
            Dialog.showError("An error occurred adding media to the document: " +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    /**
     * Looks for title="media" in an image tag, gets the dataid and then
     * uses it to find the data portion. We rewrite the data portion to
     * <image dataid="long-guid" title="media" />
     * include our image data instead.
     * <d name="long-guid" mime-type="image/jpeg" base64="yes">
     * uoerutoeurtoeorutoeutoeroutoieruotuert
     * </d>
     */
    protected void processAbiwordImage() {
        final String MAGIC_TAG = "title=\"media\"";
        final String DATA_ID = "dataid=\"";
        final String DATA_NAME = "<d name=\"";

        try {
            String s = Utils.readFile(localfile);

            // Do we have the magic tag to say we want an animal image?
            int tag = s.indexOf(MAGIC_TAG);

            if (tag == -1) {
                // No - don't bother doing anything
                Global.logDebug("No placeholder image tag found, bailing",
                    "processAbiwordImage");

                return;
            }

            // Grab the media and save it to disk - seems slightly silly
            // that we're going to decode it and re-encode it but this
            // way we reuse code sensibly
            String mediafile = getImage();

            // Bail if we didn't have any media
            if (mediafile == null) {
                Global.logDebug("No media found, bailing", "processAbiwordImage");

                return;
            }

            // Read the dataid attribute
            int did = s.lastIndexOf(DATA_ID, tag);

            if (did == -1) {
                return;
            }

            did += DATA_ID.length();

            int dide = s.indexOf("\"", did);

            if (did == -1) {
                return;
            }

            String dataid = s.substring(did, dide);
            Global.logDebug("Found placeholder dataid: " + dataid,
                "processAbiwordImage");

            // Now, find the data block for the dataid
            tag = s.indexOf(DATA_NAME + dataid);

            if (tag == -1) {
                Global.logDebug("Couldn't find data block for id " + dataid,
                    "processAbiwordImage");

                return;
            }

            Global.logDebug("Found data block, starting at position: " + tag,
                "processAbiwordImage");

            // Move to the spot after the name attribute altogether
            tag = s.indexOf("\"", tag + DATA_NAME.length() + 1);

            // If there's no closing attribute, something is wrong
            if (tag == -1) {
                Global.logDebug("Couldn't find closing speechmark to end name attribute",
                    "processAbiwordImage");

                return;
            }

            tag += 2;
            Global.logDebug("Position after attribute name: " + tag,
                "processAbiwordImage");

            // Build up our replacement data
            String newdata = "mime-type=\"image/jpeg\" base64=\"yes\">";
            newdata += new String(Base64.encode(DBFS.getBytesFromFile(
                        new File(mediafile))));

            // Carve out the old image from the file and replace
            s = s.substring(0, tag) + newdata +
                s.substring(s.indexOf("</d>", tag));

            // Replace the file on the disk with this one
            Utils.writeFile(localfile, s.getBytes(Global.CHAR_ENCODING));
        } catch (Exception e) {
            Dialog.showError("An error occurred adding media to the document: " +
                e.getMessage());
            Global.logException(e, getClass());
        }
    }

    protected void processRichText() {
        processRichText(false);
    }

    protected void processRichText(boolean displayAfterwards) {
        try {
            Global.logDebug("Entering processRichText",
                "GenerateDocument.processRichText");

            // Escape any unicode chars for Rich Text
            utf7EscapeTags();

            // Text buffer to contain the file
            String thefile = Utils.readFile(localfile);

            // Pump tags out to an array for faster access
            Object[] tags = searchtags.toArray();

            // Initalise a new StringBuffer for manipulating the file.
            StringBuffer sb = new StringBuffer(thefile);
            boolean foundTag = false;

            // Work through it in a single pass
            for (int i = 0; i < (sb.length() - 9); i++) {
                foundTag = false;

                int endMarker = -1;
                String matchTag = "";

                if (sb.substring(i, i + 2).equalsIgnoreCase("<<")) {
                    endMarker = sb.indexOf(">>", i);

                    if (endMarker != -1) {
                        foundTag = true;
                        endMarker += 2;
                        matchTag = sb.substring(i, endMarker);
                    }
                }

                // Do we have a tag?
                if (foundTag) {
                    // Test it against each one of our available tags
                    SearchTag st = null;
                    String matchs = "";

                    for (int z = 0; z < tags.length; z++) {
                        st = (SearchTag) tags[z];
                        matchs = "<<" + st.find + ">>";

                        // Does it match?
                        if (matchTag.equalsIgnoreCase(matchs)) {
                            // Replace it
                            sb.replace(i, endMarker, st.replace);

                            // Break out now, happy in the knowledge we've done
                            // it
                            break;
                        }
                    }
                }
            }

            // Dump our buffer back to the file string
            thefile = sb.toString();

            // Replace the file on the disk with this one
            Global.logDebug("Writing update document to disk",
                "GenerateDocument.processText");
            Utils.writeFile(localfile, thefile.getBytes(Global.CHAR_ENCODING));

            Global.logDebug("Completed processText",
                "GenerateDocument.processText");

            // End this now if we aren't displaying
            if (!displayAfterwards) {
                return;
            }

            display();
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError("An error occurred generating the document: " +
                e.getMessage());
        }
    }

    protected void processPlainText() {
        processPlainText(false);
    }

    protected void processPlainText(boolean displayAfterwards) {
        try {
            Global.logDebug("Entering processText",
                "GenerateDocument.processText");

            // Text buffer to contain the file
            String thefile = Utils.readFile(localfile);

            // Pump tags out to an array for faster access
            Object[] tags = searchtags.toArray();

            // Initalise a new StringBuffer for manipulating the file.
            StringBuffer sb = new StringBuffer(thefile);
            boolean foundTag = false;

            // Work through it in a single pass
            for (int i = 0; i < (sb.length() - 9); i++) {
                foundTag = false;

                int endMarker = -1;
                String matchTag = "";

                if (sb.substring(i, i + 2).equalsIgnoreCase("<<")) {
                    endMarker = sb.indexOf(">>", i);

                    if (endMarker != -1) {
                        foundTag = true;
                        endMarker += 2;
                        matchTag = sb.substring(i, endMarker);
                    }
                }

                // Do we have a tag?
                if (foundTag) {
                    // Test it against each one of our available tags
                    SearchTag st = null;
                    String matchs = "";

                    for (int z = 0; z < tags.length; z++) {
                        st = (SearchTag) tags[z];
                        matchs = "<<" + st.find + ">>";

                        // Does it match?
                        if (matchTag.equalsIgnoreCase(matchs)) {
                            // Replace it
                            sb.replace(i, endMarker, st.replace);

                            // Break out now, happy in the knowledge we've done
                            // it
                            break;
                        }
                    }
                }
            }

            // Dump our buffer back to the file string
            thefile = sb.toString();

            // Replace the file on the disk with this one
            Global.logDebug("Writing update document to disk",
                "GenerateDocument.processText");
            Utils.writeFile(localfile, thefile.getBytes(Global.CHAR_ENCODING));

            Global.logDebug("Completed processText",
                "GenerateDocument.processText");

            // End this now if we aren't displaying
            if (!displayAfterwards) {
                return;
            }

            display();
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError("An error occurred generating the document: " +
                e.getMessage());
        }
    }

    protected void processXMLText() {
        processXMLText(false);
    }

    protected void processXMLText(boolean displayAfterwards) {
        try {
            Global.logDebug("Entering processXMLText",
                "GenerateDocument.processXMLText");

            // Add markup to the tags for comparison
            markUpTags();

            // Text buffer to contain the file
            String thefile = Utils.readFile(localfile);

            // Pump tags out to an array for faster access
            Object[] tags = searchtags.toArray();

            // Initalise a new StringBuffer for manipulating the file.
            StringBuffer sb = new StringBuffer(thefile);
            boolean foundTag = false;

            // Work through it in a single pass
            for (int i = 0; i < (sb.length() - 9); i++) {
                foundTag = false;

                int endMarker = -1;
                String matchTag = "";

                if (sb.substring(i, i + 8).equalsIgnoreCase("&lt;&lt;")) {
                    // Find first end marker in case formatting has split it
                    endMarker = sb.indexOf("&gt;", i);

                    // Now find the second
                    if (endMarker != -1) {
                        endMarker = sb.indexOf("&gt;", endMarker + 1);
                    }

                    // We have a valid end marker, grab the tag
                    if (endMarker != -1) {
                        foundTag = true;
                        endMarker += 4;
                        matchTag = sb.substring(i, endMarker);

                        // Word and OO can add formatting and crap in between tags - throw any of it away
                        String bak = matchTag;
                        matchTag = Utils.removeHTML(matchTag);
                        Global.logDebug(bak + " -> " + matchTag,
                            "GenerateDocument.processXMLText");
                    }
                }

                // Do we have a tag?
                if (foundTag) {
                    // Test it against each one of our available tags
                    SearchTag st = null;
                    String matchs = "";

                    for (int z = 0; z < tags.length; z++) {
                        st = (SearchTag) tags[z];
                        matchs = "&lt;&lt;" + st.find + "&gt;&gt;";

                        // Does it match?
                        if (matchTag.equalsIgnoreCase(matchs)) {
                            // Replace it
                            sb.replace(i, endMarker, st.replace);

                            // Break out now, happy in the knowledge we've done
                            // it
                            break;
                        }
                    }
                }
            }

            // Dump our buffer back to the file string
            thefile = sb.toString();

            // Replace the file on the disk with this one
            Global.logDebug("Writing update document to disk",
                "GenerateDocument.processXMLText");
            Utils.writeFile(localfile, thefile.getBytes(Global.CHAR_ENCODING));

            Global.logDebug("Completed processXMLText",
                "GenerateDocument.processXMLText");

            // End this now if we aren't displaying
            if (!displayAfterwards) {
                return;
            }

            display();
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }
    }

    protected void display() {
        // If media attaching is on, wait for the
        // process to finish to make sure we pick up any
        // changes
        if (getAttachMedia()) {
            FileTypeManager.shellExecute(localfile);
        } else {
            if (getDocFileType().equals("html")) {
                if (Global.useInternalReportViewer) {
                    // If it's HTML and we prefer the internal report
                    // viewer, let's use that instead
                    ReportViewer rv = new ReportViewer(localfile, justFilename);
                    net.sourceforge.sheltermanager.asm.globals.Global.mainForm.addChild(rv);
                    rv.setVisible(true);

                    return;
                }
            }

            FileTypeManager.shellExecute(localfile);
        }
    }

    /**
     * Allows you to replace keys in a text string instead of using word
     * processor integration.
     *
     * @param thetext
     *            The text to search in
     * @return A string containing the replaced buffer
     */
    public String replaceInText(String thetext) {
        // Replace all those tags in our string file buffer
        Iterator<SearchTag> i = searchtags.iterator();
        String output = new String(thetext);

        while (i.hasNext()) {
            SearchTag st = (SearchTag) i.next();
            output = Utils.replace(output, "<<" + st.find + ">>", st.replace);
        }

        return output;
    }

    /**
     * Allows you to replace keys in a text string instead of using word
     * processor integration. This routine is specified to the internet
     * publisher, as it relies on tags being wrapped with $$.
     * It also converts line breaks to <br/> tags as well for paragraph
     * formatting.
     *
     * @param thetext
     *            The text to search in
     * @return A string containing the replaced buffer
     */
    public String replaceInTextInternet(String thetext) {
        // Replace all those tags in our string file buffer
        Iterator<SearchTag> i = searchtags.iterator();
        String output = new String(thetext);

        while (i.hasNext()) {
            SearchTag st = (SearchTag) i.next();
            String r = st.replace;
            r = Utils.replace(r, "\n", "<br/>");
            r = Utils.replace(r, "\r", "");
            output = Utils.replace(output, "$$" + st.find + "$$", r);
        }

        return output;
    }

    /**
     * Returns Yes, No or Unknown given a tri-state value for the animal good
     * with children, dogs, kids or housetrained
     */
    public String getTriState(Integer value) {
        if (value.intValue() == 0) {
            return Global.i18n("uiwordprocessor", "Yes");
        } else if (value.intValue() == 1) {
            return Global.i18n("uiwordprocessor", "No");
        } else if (value.intValue() == 2) {
            return Global.i18n("uiwordprocessor", "Unknown");
        }

        return "[Bad tristate switch: " + value + "]";
    }

    public String getTestResult(Integer value, boolean tested) {
        // Untested animals shouldn't return anything
        if (!tested) {
            return "";
        }

        if (value.intValue() == 0) {
            return Global.i18n("uiwordprocessor", "Unknown");
        } else if (value.intValue() == 1) {
            return Global.i18n("uiwordprocessor", "Negative");
        } else if (value.intValue() == 2) {
            return Global.i18n("uiwordprocessor", "Positive");
        }

        return "[Bad test result switch: " + value + "]";
    }

    /**
     * Sets the filename without the path by throwing away the path info
     */
    private void setFilename(String filepath) {
        try {
            File file = new File(filepath);
            justFilename = file.getName();
        } catch (Exception e) {
        }
    }

    /**
     * Go through all the chars in each tag and substitute unicode
     * codepoints above 127 for their escaped decimal version and
     * a fake substitution character
     */
    private void utf7EscapeTags() {
        Global.logDebug("Escaping unicode chars for rich text",
            "GenerateDocument.utf7EscapeTags");

        for (int i = 0; i < searchtags.size(); i++) {
            SearchTag tag = (SearchTag) searchtags.get(i);
            String s = tag.replace;
            StringBuffer o = new StringBuffer();

            for (int z = 0; z < s.length(); z++) {
                char c = s.charAt(z);

                if (c > 127) {
                    o.append("\\u").append(Integer.toString((int) c)).append("X");
                } else {
                    o.append(c);
                }
            }

            tag.replace = o.toString();
        }

        Global.logDebug("Finished escaping unicode chars for rich text",
            "GenerateDocument.utf7EscapeTags");
    }

    /**
     * If the text format we are dealing with is one of the marked up ones, they
     * should call this to mark up all replacement punctuation in the text
     * correctly. Thankfully there are only three that are necessary.
     */
    private void markUpTags() {
        Global.logDebug("Marking up search tags for XML",
            "GenerateDocument.markupTags");

        for (int i = 0; i < searchtags.size(); i++) {
            SearchTag tag = (SearchTag) searchtags.get(i);

            // Ampersands
            if (tag.replace.indexOf('&') != -1) {
                tag.replace = Utils.replace(tag.replace, "&", "&amp;");
            }

            // Less than
            if (tag.replace.indexOf('<') != -1) {
                tag.replace = Utils.replace(tag.replace, "<", "&lt;");
            }

            // Greater than
            if (tag.replace.indexOf('>') != -1) {
                tag.replace = Utils.replace(tag.replace, ">", "&lt;");
            }
        }

        Global.logDebug("Finished marking up search tags for XML",
            "GenerateDocument.markupTags");
    }

    public void free() {
        try {
            searchtags.removeAllElements();
            searchtags = null;
        } catch (Exception e) {
        }
    }

    /**
     * Override in subclass - should attach media to the appropriate record.
     */
    public abstract void attachMedia();

    /**
     * Override in subclass - downloads associated media file to temp
     * directory and returns its name or null if no media available
     */
    public abstract String getImage();
}


/** Inner class wraps up a search/replace operation */
class SearchTag {
    String find = "";
    String replace = "";
}
