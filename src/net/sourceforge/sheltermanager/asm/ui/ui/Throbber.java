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

import javax.swing.ImageIcon;


/**
 * Displays a spinning throbber
 */
@SuppressWarnings("serial")
public class Throbber extends UI.Panel implements Runnable {
    static ImageIcon[] images = new ImageIcon[13];

    static {
        for (int i = 1; i <= 12; i++)
            images[i] = IconManager.getThrobber(i);
    }

    UI.Label l = null;
    volatile boolean isStopped = true;
    int index = 1;

    public Throbber() {
        super(true); // No panel border
        initComponents();
    }

    public void start() {
        isStopped = false;
        new Thread(this).start();
    }

    public void run() {
        while (!isStopped) {
            index++;

            if (index > 12) {
                index = 1;
            }

            l.setIcon(images[index]);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stop() {
        isStopped = true;
    }

    public void dispose() {
        stop();
    }

    public void setVisible(boolean b) {
        l.setVisible(b);
    }

    private void initComponents() {
        setLayout(new UI.BorderLayout());
        l = UI.getLabel(images[1]);
        l.setHorizontalAlignment(UI.ALIGN_CENTER);
        add(l, UI.BorderLayout.CENTER);
    }
}
