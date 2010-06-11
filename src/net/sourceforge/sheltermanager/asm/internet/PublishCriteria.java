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
package net.sourceforge.sheltermanager.asm.internet;


/**
 * This class represents the data required for a publish to
 * any source.
 *
 * @author  Robin Rawson-Tetley
 */
public class PublishCriteria {
    public boolean includeCase = false;
    public boolean includeReserved = false;
    public boolean includeFosters = false;
    public boolean includeWithoutImage = false;
    public boolean includeColours = false;
    public boolean clearExisting = false;
    public boolean uploadDirectly = false;
    public boolean uploadAllImages = false;
    public boolean forceReupload = false;
    public boolean generateJavascriptDB = false;

    /** How to order animal data -
        1 = Ascending order of entry to shelter
        2 = Descending order of entry to shelter
    */
    public int order = 1;
    public int excludeUnderWeeks = 52;
    public int animalsPerPage = 20;
    public int limit = 0;

    /** Which HTML style to use (dir under internet in dbfs) */
    public String style = ".";

    /** What file extension to use */
    public String extension = "html";

    /** Whether to scale images:
        1=No,
        2=320x200,
        3=640x480,
        4=800x600,
        5=1024x768,
        6=300x300,
        7=95x95 */
    public int scaleImages = 1;

    /** Array of internal location ID fields as Strings*/
    public Object[] internalLocations = null;

    /** Replace FTP root - null denotes use system
     *  setting. */
    public String ftpRoot = null;
}
