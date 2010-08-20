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
package net.sourceforge.sheltermanager.asm.ui.splash;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMDialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.ArrayList;
import java.util.Vector;


/**
 * About form class
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class About extends ASMDialog {
    private UI.Label lblUrl;
    private UI.Button btnOk;
    private UI.HTMLBrowser edSys;
    private UI.HTMLBrowser edCredits;
    private ArrayList names = new ArrayList();
    private ArrayList values = new ArrayList();

    /** Creates new form About */
    public About() {
        super();
        init(Global.i18n("uisplash", "About..."), null, "uisplash", false);

        String sys = "<html><head><style>* { font-family: sans-serif; }</style></head><body>";
        sys += "<h2>" + Global.productVersion + "</h2>";
        sys += "<p>" + SQLRecordset.getCursorVersion() + ", " + UI.getRendererName() + "";
        sys += "<p>" + Long.toString(Global.speedTest) + "ms -&gt; " + DBConnection.getDBInfo() + "</p>";
        sys += "<p>" + System.getProperty("java.vendor.url") + " " + System.getProperty("java.version");
        sys += " on " + System.getProperty("os.name") + " " + System.getProperty("os.version");
        sys += " (" + System.getProperty("os.arch") + ")</p>";
        sys += "<p>" + System.getProperty("user.name") + " (" + System.getProperty("user.home") + ")</p>";
        sys += "</body></html>";

        // Load in the content
        setBrowserHTML(edCredits, Credits.content);
        setBrowserHTML(edSys, sys);

        // Centre ourselves
        UI.centerWindow(this);

        // Show ourselves
        this.setVisible(true);
    }

    private void setBrowserHTML(UI.HTMLBrowser ed, String content) {
        ed.setContent(content);
    }

    public boolean windowCloseAttempt() {
        return false;
    }

    public void windowOpened() {
    }

    public void setSecurity() {
    }

    public Object getDefaultFocusedComponent() {
        return btnOk;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(btnOk);

        return v;
    }

    public void initComponents() {
        edCredits = UI.getHTMLBrowser();
        edSys = UI.getHTMLBrowser();

        UI.Panel pCredits = UI.getPanel(UI.getBorderLayout());
        UI.Panel pSys = UI.getPanel(UI.getBorderLayout());
        UI.addComponent(pCredits, edCredits);
        UI.addComponent(pSys, edSys);

        // Layout the screen like this:
        // BorderLayout for the whole thing,
        // Top pane holds splash logo
        // Center pane of main thing holds tab pane
        // South pane contains close button
        UI.Label logo = UI.getSplashLabel();
        logo.setHorizontalAlignment(UI.ALIGN_CENTER);

        UI.Panel southPane = UI.getPanel(UI.getFlowLayout());

        add(logo, UI.BorderLayout.NORTH);
        add(southPane, UI.BorderLayout.SOUTH);

        UI.TabbedPane tabs = new UI.TabbedPane();

        tabs.addTab(i18n("Credits"),
            IconManager.getIcon(IconManager.SCREEN_ABOUT_ABOUT), pCredits);
        tabs.addTab(i18n("System"),
            IconManager.getIcon(IconManager.SCREEN_ABOUT_SYSTEM), pSys);

        add(tabs, UI.BorderLayout.CENTER);

        lblUrl = UI.getURLLabel("http://sheltermanager.sf.net",
                i18n("visit_site"), UI.fp(this, "urlClicked"));
        southPane.add(lblUrl);

        btnOk = UI.getButton(i18n("Ok"), null, 'o', null, UI.fp(this, "dispose"));
        southPane.add(btnOk);

        setSize(400, 500);
    }

    public void urlClicked() {
        // Go to shelter manager site in external browser
        FileTypeManager.shellExecute(lblUrl.getText());
    }
}
