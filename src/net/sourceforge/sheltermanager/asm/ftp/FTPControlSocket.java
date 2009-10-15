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
 *        $Log: FTPControlSocket.java,v $
 *        Revision 1.2  2004/11/02 13:51:04  bobintetley
 *        Serious code cleanup and a couple of bug fixes
 *
 *        Revision 1.1.1.1  2003/06/03 06:54:09  bobintetley
 *        Initial
 *
 *        Revision 1.2  2002/11/29 12:03:07  robin
 *        rev 3
 *
 *        Revision 1.1.1.1  2002/11/10 19:33:02  robin
 *
 *
 *        Revision 1.4  2001/02/25 15:57:02  bruceb
 *        Allow for different control port
 *
 *        Revision 1.3  2001/02/25 15:47:50  bruceb
 *        Added check for blank line in readReply()
 *
 *        Revision 1.2  2000/10/04 21:10:14  bruceb
 *        Added response debug
 *
 *        Revision 1.1.1.1  2000/07/31 20:16:44  bruceb
 *        New repository
 *
 *        Revision 1.1.1.1  2000/01/20 21:36:31  bruceb
 *        Imported FTP files
 *
 *
 */
package net.sourceforge.sheltermanager.asm.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.InetAddress;
import java.net.Socket;


/**
 *  Supports client-side FTP operations
 *
 *  @author             Bruce Blackshaw
 *      @version        $Revision: 1.2 $
 *
 */
public class FTPControlSocket {
    /**
     *  Revision control id
     */
    private static String cvsId = "$Id: FTPControlSocket.java,v 1.2 2004/11/02 13:51:04 bobintetley Exp $";

    /**
     *   Standard FTP end of line sequence
     */
    static final String EOL = "\r\n";

    /**
     *   The control port number for FTP
     */
    private static final int CONTROL_PORT = 21;

    /**
     *   Controls if responses sent back by the
     *   server are sent to stdout or not
     */
    private boolean debugResponses = false;

    /**
     *  The underlying socket.
     */
    private Socket controlSock = null;

    /**
     *  The write that writes to the control socket
     */
    private Writer writer = null;

    /**
     *  The reader that reads control data from the
     *  control socket
     */
    private BufferedReader reader = null;

    /**
     *   Constructor. Performs TCP connection and
     *   sets up reader/writer
     *
     *   @param   remoteHost   Remote hostname
     */
    public FTPControlSocket(String remoteHost) throws IOException, FTPException {
        this(remoteHost, CONTROL_PORT);
    }

    /**
     *   Constructor. Performs TCP connection and
     *   sets up reader/writer. Allows different control
     *   port to be used
     *
     *   @param   remoteHost   Remote hostname
     *   @param   controlPort  port for control stream
     */
    public FTPControlSocket(String remoteHost, int controlPort)
        throws IOException, FTPException {
        controlSock = new Socket(remoteHost, controlPort);
        initStreams();
        validateConnection();
    }

    /**
     *   Constructor. Performs TCP connection and
     *   sets up reader/writer
     *
     *   @param   remoteAddr   Remote inet address
     */
    public FTPControlSocket(InetAddress remoteAddr)
        throws IOException, FTPException {
        this(remoteAddr, CONTROL_PORT);
    }

    /**
     *   Constructor. Performs TCP connection and
     *   sets up reader/writer. Allows different control
     *   port to be used
     *
     *   @param   remoteAddr   Remote inet address
     *   @param   controlPort  port for control stream
     */
    public FTPControlSocket(InetAddress remoteAddr, int controlPort)
        throws IOException, FTPException {
        controlSock = new Socket(remoteAddr, controlPort);
        initStreams();
        validateConnection();
    }

    /**
     *   Checks that the standard 220 reply is returned
     *   following the initiated connection
     */
    private void validateConnection() throws IOException, FTPException {
        String reply = readReply();
        validateReply(reply, "220");
    }

    /**
     *  Obtain the reader/writer streams for this
     *  connection
     */
    private void initStreams() throws IOException {
        // input stream
        InputStream is = controlSock.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is));

        // output stream
        OutputStream os = controlSock.getOutputStream();
        writer = new OutputStreamWriter(os);
    }

    /**
     *  Get the name of the remote host
     *
     *  @return  remote host name
     */
    String getRemoteHostName() {
        InetAddress addr = controlSock.getInetAddress();

        return addr.getHostName();
    }

    /**
     *  Quit this FTP session and clean up.
     */
    public void logout() throws IOException {
        writer.close();
        reader.close();
        controlSock.close();
    }

    /**
     *  Request a data socket be created on the
     *  server, connect to it and return our
     *  connected socket.
     *
     *  @return  connected data socket
     */
    Socket createDataSocket() throws IOException, FTPException {
        // PASSIVE command - tells the server to listen for
        // a connection attempt rather than initiating it
        String reply = sendCommand("PASV");
        validateReply(reply, "227");

        // The reply to PASV is in the form:
        // 227 Entering Passive Mode (h1,h2,h3,h4,p1,p2).
        // where h1..h4 are the IP address to connect and
        // p1,p2 the port number
        // Example:
        // 227 Entering Passive Mode (128,3,122,1,15,87).

        // extract the IP data string from between the brackets
        int bracket1 = reply.indexOf('(');
        int bracket2 = reply.indexOf(')');
        String ipData = reply.substring(bracket1 + 1, bracket2);
        int[] parts = new int[6];

        int len = ipData.length();
        int partCount = 0;
        StringBuffer buf = new StringBuffer();

        // loop thru and examine each char
        for (int i = 0; (i < len) && (partCount <= 6); i++) {
            char ch = ipData.charAt(i);

            if (Character.isDigit(ch)) {
                buf.append(ch);
            } else if (ch != ',') {
                throw new FTPException("Malformed PASV reply: " + reply);
            }

            // get the part
            if ((ch == ',') || ((i + 1) == len)) { // at end or at separator

                try {
                    parts[partCount++] = Integer.parseInt(buf.toString());
                    buf.setLength(0);
                } catch (NumberFormatException ex) {
                    throw new FTPException("Malformed PASV reply: " + reply);
                }
            }
        }

        // assemble the IP address
        // we try connecting, so we don't bother checking digits etc
        String ipAddress = parts[0] + "." + parts[1] + "." + parts[2] + "." +
            parts[3];

        // assemble the port number
        int port = (parts[4] << 8) + parts[5];

        // create the data socket
        return new Socket(ipAddress, port);
    }

    /**
     *  Send a command to the FTP server and
     *  return the server's reply
     *
     *  @return  reply to the supplied command
     */
    String sendCommand(String command) throws IOException {
        // send it
        writer.write(command + EOL);
        writer.flush();

        // and read the result
        return readReply();
    }

    /**
     *  Read the FTP server's reply to a previously
     *  issued command. RFC 959 states that a reply
     *  consists of the 3 digit code followed by text.
     *  The 3 digit code is followed by a hyphen if it
     *  is a muliline response, and the last line starts
     *  with the same 3 digit code.
     *
     *  @return  reply string
     */
    String readReply() throws IOException {
        StringBuffer reply = new StringBuffer(reader.readLine());

        if (debugResponses) {
            System.out.println(reply.toString());
        }

        String replyCode = reply.toString().substring(0, 3);

        // check for multiline response and build up
        // the reply
        if (reply.charAt(3) == '-') {
            boolean complete = false;

            while (!complete) {
                String line = reader.readLine();

                // on NT4 SP1 someone gets a blank line so
                // we allow for it below

                /* Rob 2 May 2002 - it screws up with Xitami since
                   it is getting an initial response that is too short -
                   altered this line so it ignores if there are less
                   than three chars - on the upside - now works fine!

                   -- Original line --
                   if (line.length() == 0) */
                if (line.length() < 3) {
                    continue;
                }

                if (debugResponses) {
                    System.out.println(line);
                }

                if (line.substring(0, 3).equals(replyCode) &&
                        (line.charAt(3) == ' ')) {
                    reply.append(line.substring(3));
                    complete = true;
                } else { // not the last line
                    reply.append(" ");
                    reply.append(line);
                }
            }
        }

        return reply.toString();
    }

    /**
     *  Validate the response the host has supplied against the
     *  expected reply. If we get an unexpected reply we throw an
     *  exception, setting the message to that returned by the
     *  FTP server
     *
     *  @param   reply              the entire reply string we received
     *  @param   expectedReplyCode  the reply we expected to receive
     *
     */
    void validateReply(String reply, String expectedReplyCode)
        throws IOException, FTPException {
        // all reply codes are 3 chars long
        String replyCode = reply.substring(0, 3);

        // if unexpected reply, throw an exception
        if (!replyCode.equals(expectedReplyCode)) {
            throw new FTPException(reply.substring(4));
        }
    }

    /**
     *  Validate the response the host has supplied against the
     *  expected reply. If we get an unexpected reply we throw an
     *  exception, setting the message to that returned by the
     *  FTP server
     *
     *  @param   reply               the entire reply string we received
     *  @param   expectedReplyCodes  array of expected replies
     *
     */
    void validateReply(String reply, String[] expectedReplyCodes)
        throws IOException, FTPException {
        // all reply codes are 3 chars long
        String replyCode = reply.substring(0, 3);

        for (int i = 0; i < expectedReplyCodes.length; i++)
            if (replyCode.equals(expectedReplyCodes[i])) {
                return;
            }

        // got this far, not recognised
        throw new FTPException(reply.substring(4));
    }

    /**
     *  Switch debug of responses on or off
     *
     *  @param  on  true if you wish to have responses to
     *              stdout, false otherwise
     */
    void debugResponses(boolean on) {
        debugResponses = on;
    }
}
