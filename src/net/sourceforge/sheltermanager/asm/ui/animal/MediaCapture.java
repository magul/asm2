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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.io.*;

import java.net.URL;


/**
 * Handles video capture.
 *
 * Supports three methods:
 *
 * 1. (Linux only) vgrabbj from XawTV
 * 2. HTTP grab of a user specified URL for IP cameras
 * 3. Execute a command and pass the desired output image name
 *
 * @author Robin Rawson-Tetley
 */
public class MediaCapture {
    /** Returns the filename to capture the image to */
    public static String getCaptureFileName() {
        return Global.tempDirectory + File.separator + "capture.jpg";
    }

    /** Delegates to the correct method */
    public static boolean capture() {
        switch (Global.videoCaptureMethod) {
        case Global.CAPTUREMETHOD_VGRABBJ:
            return captureVgrabbj();

        case Global.CAPTUREMETHOD_HTTP:
            return captureHttp();

        case Global.CAPTUREMETHOD_COMMAND:
            return captureCommand();
        }

        return true;
    }

    /**
     * Performs the video capture using the vgrabbj utility
     * from XawTV for Linux/v4l webcams
     *
     * @return true if the image was captured successfully.
     */
    public static boolean captureVgrabbj() {
        // If it's not Linux, we can't capture
        if (!UI.osIsLinux()) {
            Dialog.showError(Global.i18n("uianimal",
                    "video_capture_is_only_available_for_linux"));

            return false;
        }

        // Do we have the vgrabbj utility?
        try {
            File f = new File("/usr/bin/vgrabbj");

            if (!f.exists()) {
                Dialog.showError(Global.i18n("uianimal",
                        "could_not_locate_the_vgrabbj_utility"));

                return false;
            }

            // Capture to ASM's temp directory
            if (0 != Utils.exec(
                        new String[] {
                            "/usr/bin/vgrabbj -f " + getCaptureFileName()
                        })) {
                Dialog.showError(Global.i18n("uianimal",
                        "an_error_occurred_capturing_the_image"));

                return false;
            }

            f = new File(Global.tempDirectory + File.separator + "capture.jpg");

            if (!f.exists()) {
                Dialog.showError(Global.i18n("uianimal",
                        "an_error_occurred_capturing_the_image"));

                return false;
            }

            // We got one
            return true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());

            return false;
        }
    }

    /**
     * Performs the video capture by retrieving a JPG
     * from an HTTP URL.
     *
     * @return true if the image was captured successfully.
     */
    public static boolean captureHttp() {
        try {
            URL u = new URL(Global.videoCaptureCommand);
            DataInputStream d = new DataInputStream(u.openStream());

            File f = new File(getCaptureFileName());
            FileOutputStream fo = new FileOutputStream(f);

            byte[] b = new byte[16384];
            int iread = 0;

            while (true) {
                iread = d.read(b);

                if (iread == -1) {
                    break;
                }

                fo.write(b, 0, iread);
            }

            d.close();
            fo.close();

            return true;
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, MediaCapture.class);
        }

        return false;
    }

    /**
     * Performs the video capture by executing a script/binary
     * and passing it the name of the file it should put
     * output in for ASM to read.
     *
     * @return true if the image was captured successfully.
     */
    public static boolean captureCommand() {
        return 0 == Utils.exec(new String[] {
                Global.videoCaptureCommand, "\"" + getCaptureFileName() + "\""
            });
    }
}
