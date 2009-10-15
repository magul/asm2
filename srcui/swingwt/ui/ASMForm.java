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

import swingwt.awt.Component;
import swingwt.awt.LayoutManager;

import swingwtx.swing.Icon;
import swingwtx.swing.JInternalFrame;

import java.util.Vector;


public abstract class ASMForm extends JInternalFrame {
    /** The default i18n key to use for this form */
    protected String i18nKey = "";

    // We have our own status bar functions as searches can go on
    // a while and we don't want to do a thread switch to update
    // the progress meter for every result
    int progressMax = 0;
    int progressValue = 0;
    int progressStep = 0;
    int progressStepTrigger = 10; // When to update the progress meter

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
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        initComponents();
        registerTabOrder(getTabOrder(), (Component) getDefaultFocusedComponent());
        setSecurity();
        addInternalFrameListener(new swingwtx.swing.event.InternalFrameListener() {
                public void internalFrameActivated(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                }

                public void internalFrameClosed(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                }

                public void internalFrameClosing(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                    if (!formClosing()) {
                        dispose();
                    }
                }

                public void internalFrameDeactivated(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                }

                public void internalFrameDeiconified(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                }

                public void internalFrameIconified(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                }

                public void internalFrameOpened(
                    swingwtx.swing.event.InternalFrameEvent evt) {
                }
            });
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
        super.dispose();
    }

    public void setLayout(LayoutManager l) {
        getContentPane().setLayout(l);
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
}
