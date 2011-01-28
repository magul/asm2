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
package net.sourceforge.sheltermanager.asm.ui.reportviewer;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.FunctionPointer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.io.File;
import java.io.IOException;

import java.util.Vector;


/**
 * Class for displaying HTML in an internal window through the SwingWT
 * EditorPane->Real browser kit
 *
 * @see net.sourceforge.sheltermanager.asm.reports.Report
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ReportViewer extends ASMForm {
    /** True if we're using the stock ASM template and scaling by adjusting font sizes */
    private boolean contentScale = false;

    /** True once we've rewritten the file content the first time */
    private boolean updatedOnce = false;
    private String filename = "";
    private int baseFontSize = 11;
    private String filecontents = "";
    @SuppressWarnings("unused")
    private String reportTitle = "";
    private UI.Button btnExternal;
    private UI.Button btnPrint;
    private UI.Button btnZoomIn;
    private UI.Button btnZoomOut;
    private UI.HTMLBrowser edOutput;
    private UI.ToolBar tlbPrintTools;

    public ReportViewer() {
    }

    /**
     * Creates new form ReportViewer
     *
     * @param filetoview
     *            The absolute name and path of the file to view
     * @param reporttitle
     *            The text to show in the title bar
     */
    public ReportViewer(String filetoview, String reporttitle) {
        showReport(filetoview, reporttitle);
    }

    public void showReport(String filetoview, String reporttitle) {
        filename = filetoview;
        reportTitle = reporttitle;
        init(reporttitle, IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER),
            "uireportviewer");

        // Load content into viewer
        try {
            Global.logDebug("Viewing: file:///" + filename, "ReportViewer.init");
            filecontents = Utils.readFile(filename);
            setContentSize();
        } catch (IOException e) {
            Dialog.showError(Global.i18n("uireportviewer",
                    "Error_occurred_accessing_") + filetoview + ": " +
                e.getMessage(), Global.i18n("uireportviewer", "Bad_File"));
            Global.logException(e, getClass());
        }
    }

    /** If possible, rewrites the <style> portion of the content to
     *  reassert font sizes from our baseFontSize variable. If not possible,
     *  disables the buttons.
     */
    public void setContentSize() {
        // Strip out any windows CR tokens
        filecontents = Utils.replace(filecontents, "\r", "");

        // Do we have an <!-- Embedded style sheet or ASM Content Scaling comment
        // if so, it's one of our default templates and we can replace the style
        // header portion with new font sizes
        if ((filecontents.indexOf("<!-- Embedded style sheet") == -1) &&
                (filecontents.indexOf("<!-- ASM") == -1)) {
            Global.logDebug("Couldn't find \"<!-- Embedded style sheet\" or " +
                "\"<!-- ASM Content Scaling\" markers, using render zoom " +
                "instead of content scaling", "ReportViewer.setContentSize");

            try {
                edOutput.setPage("file:///" + filename);
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            return;
        }

        // We can scale using font sizes
        contentScale = true;

        // Construct a new style portion
        final String font = "font-family: Sans-Serif; ";
        final String fontsize = "font-size: ";
        String style = "<style type=\"text/css\">\n" + "td { " + font +
            fontsize + baseFontSize + "pt; }\n" + "p { " + font + fontsize +
            baseFontSize + "pt; }\n" + "li { " + font + fontsize +
            baseFontSize + "pt; }\n" + "h1 { " + font + fontsize +
            (baseFontSize + 8) + "pt; }\n" + "h2 { " + font + fontsize +
            (baseFontSize + 4) + "pt; }\n" + "h3 { " + font + fontsize +
            (baseFontSize + 2) + "pt; }\n" + "</style>";

        Global.logDebug("New style string: " + style,
            "ReportViewer.setContentSize");

        filecontents = filecontents.substring(0, filecontents.indexOf("<style")) +
            style +
            filecontents.substring(filecontents.indexOf("</style>") +
                "</style>".length());

        // The old template had a font tag that shrank the footer to unreadable levels
        // it's annoying it has to be removed here, but getting people to update their
        // templates is virtually impossible
        int ft = filecontents.lastIndexOf("<font");

        if (ft != -1) {
            filecontents = filecontents.substring(0, ft) +
                filecontents.substring(filecontents.indexOf(">", ft) + 1);
        }

        // If we have a UTF8 meta tag, the HTMLEditorKit in JRE5 gets upset and 
        // refuses to interpret the style sheet
        if (filecontents.indexOf("utf-8\" />") != -1) {
            filecontents = Utils.replace(filecontents, "utf-8\" />",
                    "utf-8\"></meta>");
        }

        // Just a test
        /*
        int mt = filecontents.indexOf("<meta");
        if (mt != -1) {
            filecontents = filecontents.substring(0, mt) +
                filecontents.substring(filecontents.indexOf(">", mt + 6));
        }
        System.out.println(filecontents);
        */
        if (updatedOnce) {
            // Load the content into the viewer directly - we don't change the original
            // file so that it looks right when loaded into an external browser
            edOutput.setContent(filecontents);
        } else {
            // The first time through, we write the content to the file - this works around
            // a JRE bug that causes embedded style sheet and meta tags to not be
            // interpreted properly by the JEditorPane setText() call
            try {
                Utils.writeFile(filename,
                    filecontents.getBytes(Global.CHAR_ENCODING));
                edOutput.setPage("file:///" + filename);
                updatedOnce = true;
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(edOutput);
        ctl.add(btnPrint);
        ctl.add(btnExternal);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return edOutput;
    }

    public boolean formClosing() {
        return false;
    }

    public boolean saveData() {
        return true;
    }

    public void loadData() {
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
    }

    public void hyperlinkClicked(String target) {
        // Open hyperlinks in another browser
        FileTypeManager.shellExecute(target);
    }

    public void actionZoomIn() {
        if (contentScale) {
            baseFontSize += 1;
            setContentSize();
        } else {
            edOutput.setScale(edOutput.getScale() + 2);
        }
    }

    public void actionZoomOut() {
        if (contentScale) {
            baseFontSize -= 1;
            setContentSize();
        } else {
            edOutput.setScale(edOutput.getScale() - 2);
        }
    }

    public void actionPrint() {
        edOutput.print();
    }

    @SuppressWarnings("deprecation")
    public void actionExternal() {
        try {
            FileTypeManager.shellExecute(new File(filename).toURL().toString());
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void initComponents() {
        tlbPrintTools = UI.getToolBar();

        btnPrint = UI.getButton(null, i18n("Print"), 'p',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_PRINT),
                UI.fp(this, "actionPrint"));
        tlbPrintTools.add(btnPrint);

        btnExternal = UI.getButton(null,
                i18n("View_This_Report_In_External_Browser"), 'v',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_EXTERNAL),
                UI.fp(this, "actionExternal"));
        tlbPrintTools.add(btnExternal);

        btnZoomIn = UI.getButton(null, i18n("Zoom_In"), 'i',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_ZOOMIN),
                UI.fp(this, "actionZoomIn"));
        tlbPrintTools.add(btnZoomIn);

        btnZoomOut = UI.getButton(null, i18n("Zoom_Out"), 'o',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_ZOOMOUT),
                UI.fp(this, "actionZoomOut"));
        tlbPrintTools.add(btnZoomOut);

        add(tlbPrintTools, UI.BorderLayout.NORTH);

        edOutput = UI.getHTMLBrowser(new FunctionPointer(this,
                    "hyperlinkClicked", new Class[] { String.class }));

        UI.addComponent(this, edOutput);
    }
}
