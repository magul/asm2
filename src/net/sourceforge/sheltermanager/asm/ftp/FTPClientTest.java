/**
 *
 *  Java FTP client library.
 *
 *  Copyright (C) 2000  Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *
 *  bruceb@cryptsoft.com
 *
 *  or by snail mail to:
 *
 *  Bruce P. Blackshaw
 *  53 Wakehurst Road
 *  London SW11 6DB
 *  United Kingdom
 *
 *  Change Log:
 *
 *      $Log: FTPClientTest.java,v $
 *      Revision 1.1.1.1  2003/06/03 06:54:09  bobintetley
 *      Initial
 *
 *      Revision 1.3  2002/12/04 14:45:10  robin
 *      1.03 rev 4
 *
 *      Revision 1.2  2002/11/29 12:03:07  robin
 *      rev 3
 *
 *      Revision 1.1.1.1  2002/11/10 19:33:02  robin
 *
 *
 *      Revision 1.3  2001/02/25 16:28:05  bruceb
 *      Test control port
 *
 *      Revision 1.2  2001/02/25 16:14:30  bruceb
 *      Added rename and append test
 *
 *      Revision 1.2  2000/10/04 21:09:53  bruceb
 *      Added debug and list() test
 *
 *      Revision 1.1.1.1  2000/07/31 20:16:44  bruceb
 *      New repository
 *
 *      Revision 1.1.1.1  2000/01/20 21:36:31  bruceb
 *      Imported FTP files
 *
 */
package net.sourceforge.sheltermanager.asm.ftp;

import java.io.IOException;


/**
 *  Crude test harness.
 *
 *    TO DO: expand this!
 *
 *  @author             Bruce Blackshaw
 *  @version        $Revision: 1.1.1.1 $
 *
 */
public class FTPClientTest {
    /**
     *  Revision control id
     */
    private static String cvsId = "$Id: FTPClientTest.java,v 1.1.1.1 2003/06/03 06:54:09 bobintetley Exp $";

    /**
     *   Test harness. We have a long way to
     *   go here! I'll be spending most development
     *   time hence enhancing this!
     *
     *   Planned:
     *         - drive off a config file
     *         - do byte by byte file comparisons of transferred
     *           files
     *         - exercise all functionality
     *         - postive and negative tests
     *
     */
    public static void main(String[] args) {
        // we want remote host, user name and password
        if (args.length < 5) {
            System.out.println(args.length);
            usage();
            System.exit(1);
        }

        try {
            // assign args to make it clear
            String host = args[0];
            String user = args[1];
            String password = args[2];
            String filename = args[3];
            String directory = args[4];

            // connect and test supplying port no.
            FTPClient ftp = new FTPClient(host, 21);
            ftp.login(user, password);
            System.out.println("Logged in");
            ftp.quit();

            // connect again
            ftp = new FTPClient(host);

            // switch on debug of responses
            ftp.debugResponses(true);

            ftp.login(user, password);
            System.out.println("Logged in");

            // binary transfer
            ftp.setType(FTPTransferType.BINARY);
            System.out.println("Set transfer type");

            // change dir
            ftp.chdir(directory);
            System.out.println("Changed directory to " + directory);

            // put a local file to remote host
            ftp.put(filename, filename);
            System.out.println("Put " + filename);

            // get bytes
            byte[] buf = ftp.get(filename);
            System.out.println("Got " + buf.length + " bytes");

            // append local file
            ftp.put(filename, filename, true);
            System.out.println("Appended " + filename);

            // get bytes again - should be 2 x
            buf = ftp.get(filename);
            System.out.println("Got " + buf.length + " bytes");

            // rename. Yeah, using "+" is inefficient ...
            ftp.rename(filename, filename + ".new");

            // get a remote file - the renamed one
            ftp.get(filename + ".tst", filename + ".new");

            // test that list() works
            String listing = ftp.list("*.*");
            System.out.println(listing);

            // try system()
            System.out.println(ftp.system());

            ftp.quit();
        } catch (IOException ex) {
            System.out.println("Caught exception: " + ex.getMessage());
        } catch (FTPException ex) {
            System.out.println("Caught exception: " + ex.getMessage());
        }
    }

    /**
     *  Basic usage statement
     */
    public static void usage() {
        System.out.println("Usage: ");
        System.out.println("com.enterprisedt.net.ftp.FTPClientTest " +
            "remotehost user password filename directory");
    }
}
