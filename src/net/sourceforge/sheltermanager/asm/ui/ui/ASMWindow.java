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
package net.sourceforge.sheltermanager.asm.ui.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.sourceforge.sheltermanager.asm.globals.Global;


@SuppressWarnings("serial")
public abstract class ASMWindow extends JFrame {
    /** The default i18n key to use for this form */
    protected String i18nKey = "";

    public void init(String title, Icon icon, String i18nKey, boolean resizable) {
        this.i18nKey = i18nKey;
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(title);
        setIconImage(((ImageIcon) icon).getImage());
        setResizable(resizable);
        initComponents();
        registerTabOrder(getTabOrder(), (Component) getDefaultFocusedComponent());
        setSecurity();
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    if (windowCloseAttempt()) {
                        //evt.consume();
                    } else {
                        //isClosed = false;
                        dispose();
                    }
                }
            });
    }

    public abstract Vector<Object> getTabOrder();

    public abstract Object getDefaultFocusedComponent();

    public abstract void initComponents();

    public abstract void setSecurity();

    public abstract void windowOpened();

    /** Return true to cancel the close */
    public abstract boolean windowCloseAttempt();

    /* Not sure this is needed with windowClosing/event
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            windowCloseAttempt();
        }
    }
     */
    public void maximise() {
        this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
    }

    private void registerTabOrder(Vector<Object> components, Component focusedComponent) {
        UI.registerTabOrder(components, this, focusedComponent);
    }

    private void unregisterTabOrder() {
        UI.unregisterTabOrder(this);
    }

    public void dispose() {
        unregisterTabOrder();
        //isClosed = false;
        super.dispose();
    }

    public void setVisible(boolean b) {
        //isClosed = false;
        super.setVisible(b);
    }

    /*
    public void setLayout(LayoutManager l) {
        getContentPane().setLayout(l);
    }
    */
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
}
