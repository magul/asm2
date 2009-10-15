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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.FunctionPointer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.io.File;

import java.util.Vector;


public class HTMLViewer extends ASMForm {
    /**
     * If a temporary file has to be generated, the name of it - Will be
     * automatically deleted when the screen is closed
     */
    private String tempFileName = null;
    private UI.HTMLBrowser ed;
    private UI.Label lblStatus;
    private UI.TextField txtAddress;
    private UI.Button btnBack;
    private UI.Button btnForward;

    public HTMLViewer(String url) {
        init(Global.i18n("uiviewers", "HTML_Viewer"),
            IconManager.getIcon(IconManager.SCREEN_HTMLVIEWER), "uiviewers");

        try {
            ed.setURL(url);
            txtAddress.setText(url);
            updateButtons();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public HTMLViewer(String content, String contentType) {
        init(Global.i18n("uiviewers", "HTML_Viewer"),
            IconManager.getIcon(IconManager.SCREEN_HTMLVIEWER), "uiviewers");

        try {
            txtAddress.setText("[Internal]");
            ed.setContentType(contentType);

            // Dies in GTK2
            // ed.setText(content);

            // Generate a temp file and point the browser to it
            tempFileName = Utils.createTemporaryFile(content, "html");
            ed.setPage("file://" + tempFileName);
            updateButtons();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public boolean formClosing() {
        return false;
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public void setSecurity() {
    }

    public String getAuditInfo() {
        return null;
    }

    public Object getDefaultFocusedComponent() {
        return ed;
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(ed);

        return v;
    }

    protected void deleteTempFile() {
        if (tempFileName == null) {
            return;
        }

        File f = new File(tempFileName);
        f.delete();
    }

    public void actionLink(String url) {
        txtAddress.setText(url);
        Global.logDebug("URL: " + url, "HTMLViewer.actionLink");
        updateButtons();
    }

    public void updateButtons() {
        if ((ed == null) || (ed.getSWTPeer() == null)) {
            return;
        }

        btnBack.setEnabled(((org.eclipse.swt.browser.Browser) ed.getSWTPeer()).isBackEnabled());
        btnForward.setEnabled(((org.eclipse.swt.browser.Browser) ed.getSWTPeer()).isForwardEnabled());
    }

    public void initComponents() {
        lblStatus = UI.getLabel();
        txtAddress = UI.getTextField();
        ed = UI.getHTMLBrowser(lblStatus,
                new FunctionPointer(this, "actionLink",
                    new Class[] { String.class }));

        UI.Panel p = UI.getPanel(UI.getBorderLayout());
        UI.ToolBar t = UI.getToolBar();

        txtAddress.addKeyListener(new swingwt.awt.event.KeyAdapter() {
                public void keyPressed(swingwt.awt.event.KeyEvent evt) {
                    // If they pressed ENTER, go to the URL
                    if (evt.getKeyCode() == swingwt.awt.event.KeyEvent.VK_ENTER) {
                        try {
                            ed.setPage(txtAddress.getText());
                        } catch (Exception e) {
                            Global.logException(e, getClass());
                        }
                    }
                }
            });

        btnBack = (UI.Button) t.add(UI.getButton(null, null, 'b',
                    IconManager.getIcon(IconManager.SCREEN_HTMLVIEWER_BACK),
                    UI.fp(this, "actionBack")));

        btnForward = (UI.Button) t.add(UI.getButton(null, null, 'f',
                    IconManager.getIcon(IconManager.SCREEN_HTMLVIEWER_FORWARD),
                    UI.fp(this, "actionForward")));

        t.add(UI.getButton(null, null, 'r',
                IconManager.getIcon(IconManager.SCREEN_HTMLVIEWER_REFRESH),
                UI.fp(this, "actionRefresh")));

        p.add(txtAddress, UI.BorderLayout.CENTER);
        p.add(t, UI.BorderLayout.WEST);

        add(p, UI.BorderLayout.NORTH);
        add(ed, UI.BorderLayout.CENTER);
        add(lblStatus, UI.BorderLayout.SOUTH);
    }

    public void actionBack() {
        ((org.eclipse.swt.browser.Browser) ed.getSWTPeer()).back();
        updateButtons();
    }

    public void actionForward() {
        ((org.eclipse.swt.browser.Browser) ed.getSWTPeer()).forward();
        updateButtons();
    }

    public void actionRefresh() {
        ((org.eclipse.swt.browser.Browser) ed.getSWTPeer()).refresh();
    }
}
