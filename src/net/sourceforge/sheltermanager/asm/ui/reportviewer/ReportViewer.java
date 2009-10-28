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
package net.sourceforge.sheltermanager.asm.ui.reportviewer;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;

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
public class ReportViewer extends ASMForm {
    private String filename = "";
    private int baseFontSize = 10;
    private String filecontents = "";
    private String reportTitle = "";
    private UI.Button btnExternal;
    private UI.Button btnPage;
    private UI.Button btnPrint;
    private UI.Button btnZoomIn;
    private UI.Button btnZoomOut;
    private UI.ComboBox cboScaling;
    private UI.HTMLBrowser edOutput;
    private UI.ToolBar tlbPrintTools;

    /**
     * Creates new form ReportViewer
     *
     * @param filetoview
     *            The absolute name and path of the file to view
     * @param reporttitle
     *            The text to show in the title bar
     */
    public ReportViewer(String filetoview, String reporttitle) {
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

        // Do we have an <!-- Embedded style sheet comment - if so,
        // it's one of our default templates so we can work with it
        if (filecontents.indexOf("<!-- Embedded style sheet") == -1) {
            Global.logDebug("Couldn't find \"<!-- Embedded style sheet\" marker, disabling zoom",
                "ReportViewer.setContentSize");
            btnZoomIn.setEnabled(false);
            btnZoomOut.setEnabled(false);

            try {
                edOutput.setPage("file:///" + filename);
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            return;
        }

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

        try {
            Utils.writeFile(filename, filecontents.getBytes("UTF8"));
            edOutput.setPage("file:///" + filename);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(edOutput);
        ctl.add(btnPrint);
        ctl.add(btnPage);
        ctl.add(btnExternal);
        ctl.add(cboScaling);

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

    public void actionZoomIn() {
        baseFontSize += 1;
        setContentSize();
    }

    public void actionZoomOut() {
        baseFontSize -= 1;
        setContentSize();
    }

    public void actionPrint() {
        edOutput.print();
    }

    public void actionExternal() {
        FileTypeManager.shellExecute(filename, false);
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

        edOutput = UI.getHTMLBrowser();
        UI.addComponent(this, edOutput);
    }
}
