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

/*
 * DiaryCriteriaListener.java
 *
 * Created on 29 November 2002, 09:10
 */
package net.sourceforge.sheltermanager.asm.ui.criteria;

import java.util.Date;


/**
 *
 * @author  robin
 */
public interface DiaryCriteriaListener {
    public void normalChosen();

    public void uptoChosen(Date upto);

    public void dateChosen(Date from, Date to);
}
