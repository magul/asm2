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
package net.sourceforge.sheltermanager.asm.utility;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.internet.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;

import java.io.*;

import java.net.*;

import java.text.*;

import java.util.*;


public class Email {
    static final int DEFAULT_PORT = 25;
    static final String EOL = "\r\n"; // network end of line
    protected DataInputStream reply = null;
    protected PrintStream send = null;
    protected Socket sock = null;

    public Email() throws UnknownHostException, IOException {
        this(getSMTPServer());
    }

    /**
     *   Create an object pointing to the specified host
     *   @param hostid The host to connect to.
     *   @exception UnknownHostException
     *   @exception IOException
     */
    public Email(String hostid) throws UnknownHostException, IOException {
        this(hostid, DEFAULT_PORT);
    }

    public Email(String hostid, int port)
        throws UnknownHostException, IOException {
        sock = new Socket(hostid, port);
        reply = new DataInputStream(sock.getInputStream());
        send = new PrintStream(sock.getOutputStream());

        String rstr = readLine(reply);

        if (!rstr.startsWith("220")) {
            throw new ProtocolException(rstr);
        }

        while (rstr.indexOf('-') == 3) {
            rstr = readLine(reply);

            if (!rstr.startsWith("220")) {
                throw new ProtocolException(rstr);
            }
        }
    }

    public Email(InetAddress address) throws IOException {
        this(address, DEFAULT_PORT);
    }

    public Email(InetAddress address, int port) throws IOException {
        sock = new Socket(address, port);
        reply = new DataInputStream(sock.getInputStream());
        send = new PrintStream(sock.getOutputStream());

        String rstr = readLine(reply);

        if (!rstr.startsWith("220")) {
            throw new ProtocolException(rstr);
        }

        while (rstr.indexOf('-') == 3) {
            rstr = readLine(reply);

            if (!rstr.startsWith("220")) {
                throw new ProtocolException(rstr);
            }
        }
    }

    public void sendmsg(String to_address, String subject, String message)
        throws IOException, ProtocolException {
        sendmsg(to_address, subject, message, getLocalEmail());
    }

    public void sendmsg(String to_address, String subject, String message,
        String localEmail) throws IOException, ProtocolException {
        String from_address = localEmail;

        String rstr;
        String sstr;

        InetAddress local;

        try {
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException ioe) {
            System.err.println(
                "No local IP address found - is your network up?");
            throw ioe;
        }

        String host = local.getHostName();
        send.print("HELO " + host);
        send.print(EOL);
        send.flush();
        rstr = readLine(reply);

        if (!rstr.startsWith("250")) {
            throw new ProtocolException(rstr);
        }

        sstr = "MAIL FROM: " + from_address;
        send.print(sstr);
        send.print(EOL);
        send.flush();
        rstr = readLine(reply);

        if (!rstr.startsWith("250")) {
            throw new ProtocolException(rstr);
        }

        sstr = "RCPT TO: " + to_address;
        send.print(sstr);
        send.print(EOL);
        send.flush();
        rstr = readLine(reply);

        if (!rstr.startsWith("250")) {
            throw new ProtocolException(rstr);
        }

        send.print("DATA");
        send.print(EOL);
        send.flush();
        rstr = readLine(reply);

        if (!rstr.startsWith("354")) {
            throw new ProtocolException(rstr);
        }

        send.print("From: " + from_address);
        send.print(EOL);
        send.print("To: " + to_address);
        send.print(EOL);
        send.print("Subject: " + subject);
        send.print(EOL);

        // Create Date - we'll cheat by assuming that local clock is right
        Calendar today_date = Calendar.getInstance();
        send.print("Date: " + msgDateFormat(today_date));
        send.print(EOL);
        send.flush();

        // Warn the world that we are on the loose - with the comments header:
        // send.print("Comment: Unauthenticated sender");
        // send.print(EOL);
        send.print("X-Mailer: " + Global.productName + " " +
            Global.productVersion);
        send.print(EOL);

        // Sending a blank line ends the header part.
        send.print(EOL);

        // Now send the message proper
        send.print(message);
        send.print(EOL);
        send.print(".");
        send.print(EOL);
        send.flush();

        rstr = readLine(reply);

        if (!rstr.startsWith("250")) {
            throw new ProtocolException(rstr);
        }
    }

    public static boolean isSetup() {
        boolean ok = ((!getLocalEmail().equals("")) &&
            (!getSMTPServer().equals("")));

        if (!ok) {
            Dialog.showError(
                "You need to set up your email address and SMTP Server.\nGo to System->Configure Email");
        }

        return ok;
    }

    public static String getLocalEmail() {
        return Configuration.getString("EmailAddress");
    }

    public static String getSMTPServer() {
        return Configuration.getString("SMTPServer");
    }

    /**
     * Opens an email form with the to address,
     * and a blank subject/body
     */
    public static void singleEmailForm(String to_email) {
        UI.cursorToWait();

        EmailForm emf = new EmailForm();
        emf.removeFields();
        emf.setTo(to_email);
        Global.mainForm.addChild(emf);
    }

    /**
     * Opens an email form with no to address,
     * and a blank subject/body
     */
    public static void multiEmailForm(EmailFormListener parent, Vector fieldlist) {
        UI.cursorToWait();

        EmailForm emf = new EmailForm();
        emf.removeTo();
        emf.setParent(parent);
        emf.addFields(fieldlist);
        Global.mainForm.addChild(emf);
        fieldlist = null;
    }

    public void close() {
        try {
            send.print("QUIT");
            send.print(EOL);
            send.flush();
            sock.close();
        } catch (IOException ioe) {
            // As though there's anything I can do about it now...
        }
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    private String msgDateFormat(Calendar senddate) {
        SimpleDateFormat sdf = new SimpleDateFormat("E dd/MM/yyyy HH:mm:ss Z");

        try {
            return sdf.format(senddate.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Replacement for deprecated DataInputStream.readLine()
     */
    private String readLine(DataInputStream reply) throws IOException {
        byte[] buff = new byte[1024];
        int bytesread = reply.read(buff);

        return new String(buff).substring(0, bytesread);
    }
}
