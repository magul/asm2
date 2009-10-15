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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public abstract class ASMForm extends JPanel {
    /** Used to generate unique numbers for identifying opened tabs */
    protected static long nextformkey = 0;

    /** The default i18n key to use for this form */
    protected String i18nKey = "";
    protected long formkey = nextformkey++;

    // We have our own status bar functions as searches can go on
    // a while and we don't want to do a thread switch to update
    // the progress meter for every result
    int progressMax = 0;
    int progressValue = 0;
    int progressStep = 0;
    int progressStepTrigger = 10; // When to update the progress meter

    // Frame stuff
    String title = "";
    Icon icon = null;
    ASMDesktop parent = null;
    ASMTab tab = null;
    JScrollPane scroller = null;

    public ASMForm() {
        super();
        setLayout(new BorderLayout());
    }

    public void setTitle(String s) {
        title = s;

        try {
            // If it's on screen, update the title on the tab
            tab.setTitle(s);
        } catch (Exception e) {
        }
    }

    public String getTitle() {
        return title;
    }

    public void setFrameIcon(Icon i) {
        icon = i;
    }

    public Icon getFrameIcon() {
        return icon;
    }

    public void setSelected(boolean b) {
        // TODO:
    }

    public void setASMDesktop(ASMDesktop d) {
        parent = d;
    }

    public boolean needsScroll() {
        return false;
    }

    public int getScrollHeight() {
        return 650;
    }

    public JScrollPane getScroller() {
        return scroller;
    }

    public void setScroller(JScrollPane s) {
        scroller = s;
    }

    public abstract Vector getTabOrder();

    public abstract Object getDefaultFocusedComponent();

    public abstract void initComponents();

    public abstract void loadData();

    public abstract boolean saveData();

    public abstract void setSecurity();

    public abstract String getAuditInfo();

    /** Return true to cancel the close */
    public abstract boolean formClosing();

    public void init(String title, Icon icon, String i18nKey) {
        UI.cursorToWait();
        this.i18nKey = i18nKey;
        setTitle(title);
        setFrameIcon(icon);
        initComponents();
        registerTabOrder(getTabOrder(), (Component) getDefaultFocusedComponent());
        setSecurity();
        UI.cursorToPointer();
    }

    protected void registerTabOrder(Vector components,
        Component focusedComponent) {
        UI.registerTabOrder(components, this, focusedComponent);
    }

    protected void unregisterTabOrder() {
        UI.unregisterTabOrder(this);
    }

    public void dispose() {
        unregisterTabOrder();
        parent.close(this);
        tab = null;
        parent = null;
    }

    public String i18n(String key) {
        return Global.i18n(i18nKey, key);
    }

    public String i18n(String key, String arg1) {
        return Global.i18n(i18nKey, key, arg1);
    }

    public String i18n(String key, String arg1, String arg2) {
        return Global.i18n(i18nKey, key, arg1, arg2);
    }

    public String i18n(String key, String arg1, String arg2, String arg3) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3);
    }

    public String i18n(String key, String arg1, String arg2, String arg3,
        String arg4) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3, arg4);
    }

    public String i18n(String key, String arg1, String arg2, String arg3,
        String arg4, String arg5) {
        return Global.i18n(i18nKey, key, arg1, arg2, arg3, arg4, arg5);
    }

    public void initStatusBarMax(int maxvalue) {
        progressMax = maxvalue;
        progressValue = 0;

        Global.mainForm.pgStatus.setMaximum(maxvalue);
        Global.mainForm.pgStatus.setStringPainted(true);
        Global.mainForm.pgStatus.setString(null);
        Global.mainForm.pgStatus.repaint();
        // Change the mouse pointer to an hourglass
        UI.cursorToWait();
    }

    /** Resets the status bar back to empty */
    public void resetStatusBar() {
        Global.mainForm.pgStatus.setMaximum(100);
        Global.mainForm.pgStatus.setValue(0);
        Global.mainForm.pgStatus.setStringPainted(false);
        // Change the mouse pointer back to normal
        UI.cursorToPointer();
    }

    /** Increments the status bar by one value */
    public void incrementStatusBar() {
        progressValue++;
        progressStep++;

        if ((progressValue <= progressMax) &&
                (progressStep == progressStepTrigger)) {
            Global.mainForm.pgStatus.setValue(progressValue);
            progressStep = 0;
        }
    }

    /** Sets the status text to the value specified */
    public void setStatusText(String newtext) {
        Global.mainForm.setStatusText(newtext);
    }

    /** Produces a unique identifier for this form */
    public String toString() {
        return Long.toString(formkey);
    }
}
