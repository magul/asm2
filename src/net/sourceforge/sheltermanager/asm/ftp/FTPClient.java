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
 *        $Log: FTPClient.java,v $
 *        Revision 1.1.1.1  2003/06/03 06:54:09  bobintetley
 *        Initial
 *
 *        Revision 1.3  2002/11/29 12:03:07  robin
 *        rev 3
 *
 *        Revision 1.2  2002/11/18 11:25:04  robin
 *        *** empty log message ***
 *
 *        Revision 1.1.1.1  2002/11/10 19:33:02  robin
 *
 *
 *        Revision 1.5  2001/02/25 15:57:02  bruceb
 *        Allow for different control port
 *
 *        Revision 1.4  2001/02/25 15:39:55  bruceb
 *        Added rename() and SOCKS support
 *
 *        Revision 1.3  2001/02/25 15:18:43  bruceb
 *        Added append ability
 *
 *        Revision 1.2  2000/10/04 21:09:18  bruceb
 *        Closed streams, added list()
 *
 *
 */
package net.sourceforge.sheltermanager.asm.ftp;

import java.io.*;

import java.net.*;

import java.util.*;


/**
 *  Supports client-side FTP. Most common
 *  FTP operations are present in this class.
 *  Lots to do, but works ok.
 *
 *  @author             Bruce Blackshaw
 *  @version        $Revision: 1.1.1.1 $
 *
 */
public class FTPClient {
    /**
     *  Revision control id
     */
    private static String cvsId = "$Id: FTPClient.java,v 1.1.1.1 2003/06/03 06:54:09 bobintetley Exp $";

    /**
     *  Socket responsible for controlling
     *  the connection
     */
    private FTPControlSocket control = null;

    /**
     *  Socket responsible for transferring
     *  the data
     */
    private Socket data = null;

    /**
     *  Record of the transfer type
     */
    private FTPTransferType transferType = null;

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     */
    public FTPClient(String remoteHost) throws IOException, FTPException {
        control = new FTPControlSocket(remoteHost);
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream
     */
    public FTPClient(String remoteHost, int controlPort)
        throws IOException, FTPException {
        control = new FTPControlSocket(remoteHost, controlPort);
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteAddr  the address of the
     *                       remote host
     */
    public FTPClient(InetAddress remoteAddr) throws IOException, FTPException {
        control = new FTPControlSocket(remoteAddr);
    }

    /**
     *  Constructor. Creates the control
     *  socket. Allows setting of control port (normally
     *  set by default to 21).
     *
     *  @param   remoteAddr  the address of the
     *                       remote host
     *  @param   controlPort  port for control stream
     */
    public FTPClient(InetAddress remoteAddr, int controlPort)
        throws IOException, FTPException {
        control = new FTPControlSocket(remoteAddr, controlPort);
    }

    /**
     *  Login into an account on the FTP server. This
     *  call completes the entire login process
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public void login(String user, String password)
        throws IOException, FTPException {
        String response = control.sendCommand("USER " + user);
        control.validateReply(response, "331");
        response = control.sendCommand("PASS " + password);
        control.validateReply(response, "230");
    }

    /**
     *  Supply the user name to log into an account
     *  on the FTP server. Must be followed by the
     *  password() method - but we allow for
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public void user(String user) throws IOException, FTPException {
        String reply = control.sendCommand("USER " + user);

        // we allow for a site with no password - 230 response
        String[] validCodes = { "230", "331" };
        control.validateReply(reply, validCodes);
    }

    /**
     *  Supplies the password for a previously supplied
     *  username to log into the FTP server. Must be
     *  preceeded by the user() method
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public void password(String password) throws IOException, FTPException {
        String reply = control.sendCommand("PASS " + password);

        // we allow for a site with no passwords (202)
        String[] validCodes = { "230", "202" };
        control.validateReply(reply, validCodes);
    }

    /**
     *  Set up SOCKS v4 proxy settings. This can be used if there
     *  is a SOCKS proxy server in place that must be connected thru.
     *
     *  @param  port  SOCKS proxy port
     *  @param  host  SOCKS proxy hostname
     */
    public void initSOCKS(String port, String host) {
        Properties props = System.getProperties();
        props.put("socksProxyPort", port);
        props.put("socksProxyHost", host);
        System.setProperties(props);
    }

    /**
     *  Get the name of the remote host
     *
     *  @return  remote host name
     */
    String getRemoteHostName() {
        return control.getRemoteHostName();
    }

    /**
     *  Issue arbitrary ftp commands to the FTP server.
     *
     *  @param command     ftp command to be sent to server
     *  @param validCodes  valid return codes for this command
     */
    public void quote(String command, String[] validCodes)
        throws IOException, FTPException {
        String reply = control.sendCommand(command);

        // allow for no validation to be supplied
        if ((validCodes != null) && (validCodes.length > 0)) {
            control.validateReply(reply, validCodes);
        }
    }

    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(String localPath, String remoteFile)
        throws IOException, FTPException {
        put(localPath, remoteFile, false);
    }

    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory. Allows appending
     *  if current file exists
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(String localPath, String remoteFile, boolean append)
        throws IOException, FTPException {
        // get an output stream
        data = control.createDataSocket();

        DataOutputStream out = new DataOutputStream(data.getOutputStream());

        // send the command to store
        String cmd = append ? "APPE " : "STOR ";
        String reply = control.sendCommand(cmd + remoteFile);

        // Can get a 125 or a 150 
        String[] validCodes1 = { "125", "150" };
        control.validateReply(reply, validCodes1);

        // open input stream to read source file
        FileInputStream input = new FileInputStream(localPath);
        byte[] buf = new byte[512];

        // read a chunk at a time and write to the data socket
        int count = 0;

        while ((count = input.read(buf)) > 0) {
            out.write(buf, 0, count);
        }

        input.close();

        // flush and clean up
        out.flush();
        out.close();

        // and close the data socket
        try {
            data.close();
        } catch (IOException ignore) {
        }

        // check the control response  
        String[] validCodes2 = { "226", "250" };
        reply = control.readReply();
        control.validateReply(reply, validCodes2);
    }

    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  data        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(byte[] bytes, String remoteFile)
        throws IOException, FTPException {
        put(bytes, remoteFile, false);
    }

    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory. Allows
     *  appending if current file exists
     *
     *  @param  data        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(byte[] bytes, String remoteFile, boolean append)
        throws IOException, FTPException {
        // get an output stream
        data = control.createDataSocket();

        DataOutputStream out = new DataOutputStream(data.getOutputStream());

        // send the command to store
        String cmd = append ? "APPE " : "STOR ";
        String reply = control.sendCommand(cmd + remoteFile);

        // Can get a 125 or a 150 
        String[] validCodes1 = { "125", "150" };
        control.validateReply(reply, validCodes1);

        // write array
        out.write(bytes, 0, bytes.length);

        // flush and clean up
        out.flush();
        out.close();

        // and close the data socket
        try {
            data.close();
        } catch (IOException ignore) {
        }

        // check the control response  
        String[] validCodes2 = { "226", "250" };
        reply = control.readReply();
        control.validateReply(reply, validCodes2);
    }

    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  localPath   local file to put data in
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void get(String localPath, String remoteFile)
        throws IOException, FTPException {
        // get an input stream to read data from
        data = control.createDataSocket();

        DataInputStream in = new DataInputStream(data.getInputStream());
        BufferedInputStream bIn = new BufferedInputStream(in);

        // send the retrieve command
        String reply = control.sendCommand("RETR " + remoteFile);

        // Can get a 125 or a 150 
        String[] validCodes1 = { "125", "150" };
        control.validateReply(reply, validCodes1);

        // do the retrieving
        int chunksize = 4096;
        byte[] chunk = new byte[chunksize];
        int count;

        // create the buffered output stream for writing the file       
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
                    localPath, false));

        // read from socket & write to file in chunks
        while ((count = bIn.read(chunk, 0, chunksize)) >= 0) {
            out.write(chunk, 0, count);
        }

        out.close();

        // close streams
        try {
            bIn.close();
            data.close();
        } catch (IOException ignore) {
        }

        // check the control response  
        String[] validCodes2 = { "226", "250" };
        reply = control.readReply();
        control.validateReply(reply, validCodes2);
    }

    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode. Retrieve as a byte array. Note
     *  that we may experience memory limitations as the
     *  entire file must be held in memory at one time.
     *
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public byte[] get(String remoteFile) throws IOException, FTPException {
        // get an input stream to read data from
        data = control.createDataSocket();

        DataInputStream in = new DataInputStream(data.getInputStream());
        BufferedInputStream bIn = new BufferedInputStream(in);

        // send the retrieve command
        String reply = control.sendCommand("RETR " + remoteFile);

        // Can get a 125 or a 150 
        String[] validCodes1 = { "125", "150" };
        control.validateReply(reply, validCodes1);

        // do the retrieving
        int chunksize = 4096;
        byte[] chunk = new byte[chunksize]; // read chunks into
        byte[] resultBuf = new byte[chunksize]; // where we place chunks
        byte[] temp = null; // temp swap buffer
        int count; // size of chunk read
        int bufsize = 0; // size of resultBuf

        // read from socket & write to file
        while ((count = bIn.read(chunk, 0, chunksize)) >= 0) {
            // new buffer to hold current buf + new chunk
            temp = new byte[bufsize + count];

            // copy current buf to temp
            System.arraycopy(resultBuf, 0, temp, 0, bufsize);

            // copy new chunk onto end of temp
            System.arraycopy(chunk, 0, temp, bufsize, count);

            // re-assign temp buffer to buf
            resultBuf = temp;

            // update size of buffer
            bufsize += count;
        }

        // close streams
        try {
            bIn.close();
            data.close();
        } catch (IOException ignore) {
        }

        // check the control response  
        String[] validCodes2 = { "226", "250" };
        reply = control.readReply();
        control.validateReply(reply, validCodes2);

        return resultBuf;
    }

    /**
     *  Run a site-specific command on the
     *  server. Support for commands is dependent
     *  on the server
     *
     *  @param  command   the site command to run
     *  @return true if command ok, false if
     *          command not implemented
     */
    public boolean site(String command) throws IOException, FTPException {
        // send the retrieve command
        String reply = control.sendCommand("SITE " + command);

        // Can get a 200 (ok) or 202 (not impl). Some
        // FTP servers return 502 (not impl)
        String[] validCodes = { "200", "202", "502" };
        control.validateReply(reply, validCodes);

        // return true or false? 200 is ok, 202/502 not
        // implemented
        if (reply.substring(0, 3).equals("200")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  List a directory's contents
     *
     *  @param  mask  the file mask to use
     */
    public String list(String mask) throws IOException, FTPException {
        return list(mask, false);
    }

    /**
     *  List a directory's contents
     *
     *  @param  mask  the file mask to use
     *  @param  full  true if detailed listing required
     *                false otherwise
     */
    public String list(String mask, boolean full)
        throws IOException, FTPException {
        // first set type to ascii if binary
        boolean isBinary = false;

        if (transferType.equals(FTPTransferType.BINARY)) {
            isBinary = true;
            setType(FTPTransferType.ASCII);
        }

        // get an input stream to read data from
        data = control.createDataSocket();

        InputStreamReader in = new InputStreamReader(data.getInputStream());
        BufferedReader bIn = new BufferedReader(in);

        // send the retrieve command
        String command = full ? "LIST " : "NLST ";
        String reply = control.sendCommand(command + mask);

        // Can get a 125 or a 150 
        String[] validCodes1 = { "125", "150" };
        control.validateReply(reply, validCodes1);

        // get the listing ... this comes on the data channel
        // do the retrieving
        int chunksize = 4096;
        char[] chunk = new char[chunksize]; // read chunks into
        char[] resultBuf = new char[chunksize]; // where we place chunks
        char[] temp = null; // temp swap buffer
        int count; // size of chunk read
        int bufsize = 0; // size of resultBuf

        // read from socket & write to file
        while ((count = bIn.read(chunk, 0, chunksize)) >= 0) {
            // new buffer to hold current buf + new chunk
            temp = new char[bufsize + count];

            // copy current buf to temp
            System.arraycopy(resultBuf, 0, temp, 0, bufsize);

            // copy new chunk onto end of temp
            System.arraycopy(chunk, 0, temp, bufsize, count);

            // re-assign temp buffer to buf
            resultBuf = temp;

            // update size of buffer
            bufsize += count;
        }

        // close streams. NOTE! For NLST we close our end before
        // reading the reply! wu-ftpd requires this.
        try {
            bIn.close();
            data.close();
        } catch (IOException ignore) {
        }

        // check the control response  
        String[] validCodes2 = { "226", "250" };
        reply = control.readReply();
        control.validateReply(reply, validCodes2);

        // reset type
        if (isBinary) {
            setType(FTPTransferType.BINARY);
        }

        String result = null;

        if (resultBuf.length > 0) {
            result = new String(resultBuf);
        }

        return result; // this is the String with the listing in it
    }

    /**
     *  Switch debug of responses on or off
     *
     *  @param  on  true if you wish to have responses to
     *              stdout, false otherwise
     */
    public void debugResponses(boolean on) {
        control.debugResponses(on);
    }

    /**
     *  Get the current transfer type
     *
     *  @return  the current type of the transfer,
     *           i.e. BINARY or ASCII
     */
    public FTPTransferType getType() {
        return transferType;
    }

    /**
     *  Set the transfer type
     *
     *  @param  type  the transfer type to
     *                set the server to
     */
    public void setType(FTPTransferType type) throws IOException, FTPException {
        // determine the character to send
        String typeStr = FTPTransferType.ASCII_CHAR;

        if (type.equals(FTPTransferType.BINARY)) {
            typeStr = FTPTransferType.BINARY_CHAR;
        }

        // send the command
        String reply = control.sendCommand("TYPE " + typeStr);
        control.validateReply(reply, "200");

        // record the type
        transferType = type;
    }

    /**
     *  Delete the specified remote file
     *
     *  @param  remoteFile  name of remote file to
     *                      delete
     */
    public void delete(String remoteFile) throws IOException, FTPException {
        String reply = control.sendCommand("DELE " + remoteFile);
        control.validateReply(reply, "250");
    }

    /**
     *  Rename a file or directory
     *
     * @param from  name of file or directory to rename
     * @param to    intended name
     */
    public void rename(String from, String to) throws IOException, FTPException {
        String reply = control.sendCommand("RNFR " + from);
        control.validateReply(reply, "350");

        reply = control.sendCommand("RNTO " + to);
        control.validateReply(reply, "250");
    }

    /**
     *  Delete the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               delete
     */
    public void rmdir(String dir) throws IOException, FTPException {
        String reply = control.sendCommand("RMD " + dir);
        control.validateReply(reply, "250");
    }

    /**
     *  Create the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               create
     */
    public void mkdir(String dir) throws IOException, FTPException {
        String reply = control.sendCommand("MKD " + dir);
        control.validateReply(reply, "257");
    }

    /**
     *  Change the remote working directory to
     *  that supplied
     *
     *  @param  dir  name of remote directory to
     *               change to
     */
    public void chdir(String dir) throws IOException, FTPException {
        String reply = control.sendCommand("CWD " + dir);
        control.validateReply(reply, "250");
    }

    /**
     *  Get the current remote working directory
     *
     *  @return   the current working directory
     */
    public String pwd() throws IOException, FTPException {
        String reply = control.sendCommand("PWD");
        control.validateReply(reply, "257");

        return reply;
    }

    /**
     *  Get the type of the OS at the server
     *
     *  @return   the type of server OS
     */
    public String system() throws IOException, FTPException {
        String reply = control.sendCommand("SYST");
        control.validateReply(reply, "215");

        return reply;
    }

    /**
     *  Quit the FTP session
     *
     */
    public void quit() throws IOException, FTPException {
        String reply = control.sendCommand("QUIT" + FTPControlSocket.EOL);
        control.validateReply(reply, "221");

        control.logout();
        control = null;
    }
}
