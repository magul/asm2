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

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.*;


/**
 * Displays credits
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class Credits {
    public final static String content = "<html>" +
        "<head><style>p, h1, h2, li, a { font-family: sans-serif; }" +
        "</style></head>" + "<body>" + "<h1>Credits:</h1>" +
        "<h2>ASM System Design, Programming and Website</h2>" +
        "<p>Robin Rawson-Tetley [robin@rawsontetley.org]</p>" +
        "<h2>Additional Design, Testing and Feedback:</h2>" + "<ul>" +
        "<li>Adam Spencer [doctorwebbox@dsl.pipex.com]</li>" +
        "<li>Brian Link [kiselink@bellsouth.net]</li>" + "</ul>" +
        "<h2>Additional Graphics:</h2>" + "<ul>" +
        "<li>David Rolfe [drolfe@users.sourceforge.net]</li>" + "</ul>" +
        "<h2>Additional Code</h2>" + "<ul>" +
        "<li>Chris Thomas [chris@cjack.com]</li>" +
        "<li>Asoka Desilva [asoka_desilva@users.sourceforge.net]</li>" +
        "<li>Irv Elshoff [irv.elshoff@wldelft.nl]</li>" +
        "<li>David McKerlie</li>" + "</ul>" + "<h2>Translations:</h2><ul>" +
        "<li>Dutch [Irv Elshoff, Benedictus Lambrechts]</li>" +
        "<li>Estonian [muti]</li>" + "<li>French [Gregory Simon]</li>" +
        "<li>German [Matthias, swafnil]</li>" +
        "<li>Hebrew [Yaron Shahrabani, Liel Fridman, Dor Dankner]</li>" +
        "<li>Italian [Davide]</li>" + "<li>Lithuanian [Ruta Kudelyte]</li>" +
        "<li>Polish [Ruta Kudelyte, Agnieszka Wasinska]</li>" +
        "<li>Portugese [Ines Brito, Manuel Coelho]</li>" +
        "<li>Russian [Ruta Kudelyte, roxton]</li>" +
        "<li>Slovak [Zuzana Zelena, Priatelia Zvierat, Patolog Patologovic]</li>" +
        "<li>Spanish [Vasco Marques, Andres Palomares, Ivan Garcia]</li>" +
        "<li>Swedish [Miranda, Linn]</li>" +
        "<li>Thai [Krit Marukawisutthigul]</li>" +
        "<li>Turkish [electroweak]</li>" +
        "<h2>Other Free software used by ASM:</h2>" + "<ul>" +
        "<li><a href=\"http://www.hsqldb.org\">Hypersonic SQL DB</a></li>" +
        "<li><a href=\"http://www.postgresql.org\">PostgreSQL</a></li>" +
        "<li><a href=\"http://www.mysql.com\">MySQL &amp; MySQL ConnectorJ</a></li>" +
        "<li><a href=\"http://www.openoffice.org\">OpenOffice</a></li>" +
        "<li><a href=\"http://jopenchart.sourceforge.net\">JOpenChart</a></li>" +
        "<li><a href=\"http://swingwt.sourceforge.net\">SwingWT</a></li>" +
        "<li><a href=\"http://www.enterprisedt.com\">Generic Java FTP Lib</a></li>" +
        "<li><a href=\"http://www.senojflags.com\">Country Flags</a></li>" +
        "<li><a href=\"http://launchpad.net\">Launchpad</a></li>" + "</ul>" +
        "</body>" + "</html>";

    /** Creates a new instance of Credits */
    public Credits() {
        Global.mainForm.addChild(new HTMLViewer(content, "text/html"));
    }
}
