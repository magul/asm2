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
    private String reportTitle = "";
    private UI.Button btnClose;
    private UI.Button btnExternal;
    private UI.Button btnPage;
    private UI.Button btnPrint;
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
            edOutput.setURL("file:///" + filename);
        } catch (IOException e) {
            Dialog.showError(Global.i18n("uireportviewer",
                    "Error_occurred_accessing_") + filetoview + ": " +
                e.getMessage(), Global.i18n("uireportviewer", "Bad_File"));
            Global.logException(e, getClass());
        }
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(edOutput);
        ctl.add(btnClose);
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

    public void actionPrint() {
        // Call the Javascript print method on the browser widget
        edOutput.print();
    }

    public void actionExternal() {
        FileTypeManager.shellExecute(filename, false);
    }

    public void initComponents() {
        tlbPrintTools = UI.getToolBar();

        btnClose = UI.getButton(null, null, 'x',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_CLOSE),
                UI.fp(this, "dispose"));
        tlbPrintTools.add(btnClose);

        btnPrint = UI.getButton(null, i18n("Print"), 'p',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_PRINT),
                UI.fp(this, "actionPrint"));
        tlbPrintTools.add(btnPrint);

        btnExternal = UI.getButton(null,
                i18n("View_This_Report_In_External_Browser"), 'v',
                IconManager.getIcon(IconManager.SCREEN_REPORTVIEWER_EXTERNAL),
                UI.fp(this, "actionExternal"));
        tlbPrintTools.add(btnExternal);

        add(tlbPrintTools, UI.BorderLayout.NORTH);

        edOutput = UI.getHTMLBrowser();
        UI.addComponent(this, edOutput);
    }
}
