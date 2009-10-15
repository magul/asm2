/**
 *  Copyright (C) 2000 Enterprise Distributed Technologies Ltd.
 *
 *
 *  Change Log:
 *
 *        $Log: FTPTransferType.java,v $
 *        Revision 1.1.1.1  2003/06/03 06:54:09  bobintetley
 *        Initial
 *
 *        Revision 1.2  2002/11/29 12:03:07  robin
 *        rev 3
 *
 *        Revision 1.1.1.1  2002/11/10 19:33:02  robin
 *
 *
 *        Revision 1.2  2000/10/04 21:10:44  bruceb
 *        No change
 *
 *        Revision 1.1.1.1  2000/07/31 20:16:44  bruceb
 *        New repository
 *
 *        Revision 1.1.1.1  2000/01/20 21:36:31  bruceb
 *        Imported FTP files
 *
 */
package net.sourceforge.sheltermanager.asm.ftp;


/**
 *  Enumerates the transfer types possible. We
 *  support only the two common types, ASCII and
 *  Image (often called binary).
 *
 *  @author             Bruce Blackshaw
 *  @version        $Revision: 1.1.1.1 $
 *
 */
public class FTPTransferType {
    /**
     *  Revision control id
     */
    private static String cvsId = "$Id: FTPTransferType.java,v 1.1.1.1 2003/06/03 06:54:09 bobintetley Exp $";

    /**
     *   Represents ASCII transfer type
     */
    public static FTPTransferType ASCII = new FTPTransferType();

    /**
     *   Represents Image (or binary) transfer type
     */
    public static FTPTransferType BINARY = new FTPTransferType();

    /**
     *   The char sent to the server to set ASCII
     */
    static String ASCII_CHAR = "A";

    /**
     *   The char sent to the server to set BINARY
     */
    static String BINARY_CHAR = "I";

    /**
     *  Private so no-one else can instantiate this class
     */
    private FTPTransferType() {
    }
}
