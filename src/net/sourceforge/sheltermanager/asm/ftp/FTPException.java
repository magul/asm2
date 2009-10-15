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
 *          $Log: FTPException.java,v $
 *          Revision 1.1.1.1  2003/06/03 06:54:09  bobintetley
 *          Initial
 *
 *          Revision 1.2  2002/11/29 12:03:07  robin
 *          rev 3
 *
 *          Revision 1.1.1.1  2002/11/10 19:33:02  robin
 *
 *
 *          Revision 1.2  2000/10/04 21:10:36  bruceb
 *          No change
 *
 *          Revision 1.1.1.1  2000/07/31 20:16:44  bruceb
 *          New repository
 *
 *          Revision 1.1.1.1  2000/01/20 21:36:31  bruceb
 *          Imported FTP files
 *
 */
package net.sourceforge.sheltermanager.asm.ftp;


/**
 *  FTP specific exceptions
 *
 *  @author                Bruce Blackshaw
 *      @version        $Revision: 1.1.1.1 $
 *
 */
public class FTPException extends Exception {
    /**
     *  Revision control id
     */
    private static String cvsId = "$Id: FTPException.java,v 1.1.1.1 2003/06/03 06:54:09 bobintetley Exp $";

    /**
     *   Constructor. Delegates to super.
     *
     *   @param   msg   Message that the user will be
     *                  able to retrieve
     */
    public FTPException(String msg) {
        super(msg);
    }
}
