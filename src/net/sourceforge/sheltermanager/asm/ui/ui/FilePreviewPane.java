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
package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.bo.Configuration;

import java.awt.Image;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;


/**
 *
 * Special JFileChooser accessory that allows images to
 * be previewed when they are selected in the chooser.
 */
@SuppressWarnings("serial")
public class FilePreviewPane extends UI.Panel implements PropertyChangeListener,
    ActionListener {
    /** The parent FileChooser */
    protected UI.FileChooser chooser = null;
    @SuppressWarnings("unused")
    private UI.Label lblPreview;
    private UI.Label lblImage;
    private UI.Label lblImageInfo;
    private boolean useOldScaling = false;

    /** Creates new form BeanForm */
    public FilePreviewPane() {
        super();
        initComponents();
    }

    public FilePreviewPane(UI.FileChooser fc) {
        this();
        chooser = fc;
        register(chooser);
    }

    protected void register(UI.FileChooser c) {
        if (c == null) {
            return;
        }

        c.addPropertyChangeListener(this);
        c.addActionListener(this);
    }

    protected void unregister(UI.FileChooser c) {
        if (c == null) {
            return;
        }

        c.removeActionListener(this);
        c.removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            updateImage();
        }
    }

    private void updateImage() {
        // Sets the preview pane to show the new image:
        try {
            // Load the image icon
            ImageIcon imic = new javax.swing.ImageIcon(chooser.getSelectedFile()
                                                              .getAbsolutePath());

            // Now grab the image
            Image im = imic.getImage();

            // Set the image info
            lblImageInfo.setText(im.getWidth(null) + "x" + im.getHeight(null));

            // Scale it to fit neatly in our window
            Image scaled = null;
            if (Configuration.getBoolean("UseOldScaling")) {
                scaled = UI.scaleImageOld(im, 131, 103);
            }
            else {
                scaled = UI.scaleImage(im, 131, 103);
            }

            // Dump the source image
            im.flush();

            // Shove it back into the image icon so we can display it
            imic.setImage(scaled);

            // Finally, display it
            lblImage.setIcon(imic);
            lblImage.setToolTipText(chooser.getSelectedFile().getName());

            lblImageInfo.repaint();
            lblImage.repaint();
            lblImage.getParent().invalidate();
        } catch (NullPointerException e) {
            // File is not a supported image -- ignore
        }
    }

    private void initComponents() {
        lblImage = UI.getLabel();
        lblPreview = UI.getLabel("Preview:");
        lblImageInfo = UI.getLabel();

        setLayout(UI.getBorderLayout());

        setMaximumSize(UI.getDimension(131, 119));
        setMinimumSize(UI.getDimension(131, 119));
        setPreferredSize(UI.getDimension(131, 119));

        //add(lblPreview, UI.BorderLayout.NORTH);
        add(lblImage, UI.BorderLayout.CENTER);
        add(lblImageInfo, UI.BorderLayout.SOUTH);
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
    }
}
