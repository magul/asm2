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
package net.sourceforge.sheltermanager.asm.mailmerge;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.text.ParseException;


/**
 * Produces a mail merge source of all animals
 * who have been adopted in the last 3 months and
 * not returned.
 *
 * @author  Robin Rawson-Tetley
 */
public class AdoptedNoReturn extends MailMerge {
    /** MySQL date to show vaccinations for */
    private String theLDate = "";
    private String theUDate = "";

    public AdoptedNoReturn() {
        // Ask how many days in advance they would 
        // like the source for.
        String ldate = Dialog.getDateInput(Global.i18n("mailmerge",
                    "Please_enter_the_start_date_of_adoptions_to_include"),
                Global.i18n("mailmerge", "Non-Returned_Adoptions"));

        if (ldate.equals("")) {
            return;
        }

        String udate = Dialog.getDateInput(Global.i18n("mailmerge",
                    "Please_enter_the_closing_date_of_adoptions_to_include"),
                Global.i18n("mailmerge", "Non-Returned_Adoptions"));

        if (udate.equals("")) {
            return;
        }

        try {
            theLDate = Utils.getSQLDate(ldate);
            theUDate = Utils.getSQLDate(udate);
        } catch (ParseException e) {
            Dialog.showError(e.getMessage());

            return;
        }

        // Date is ok, start us off.
        this.start();
    }

    protected String getFileName() {
        return Global.i18n("mailmerge", "AdoptedNoReturn.csv");
    }

    protected int getEmailColumn() {
        return 11;
    }

    protected void getData() throws CursorEngineException, NoDataException {
        SQLRecordset rs = new SQLRecordset();

        try {
            rs.openRecordset(
                "SELECT owner.ID, owner.OwnerName, owner.OwnerAddress, owner.OwnerPostcode, " +
                "animal.AnimalName, owner.EmailAddress, owner.OwnerTown, owner.OwnerCounty, owner.OwnerTitle, " +
                "owner.OwnerInitials, owner.OwnerForenames, owner.OwnerSurname " +
                "FROM adoption " +
                "INNER JOIN owner ON adoption.OwnerID = owner.ID " +
                "INNER JOIN animal ON adoption.AnimalID = animal.ID " +
                "WHERE adoption.ReturnDate Is Null AND adoption.MovementDate >= '" +
                theLDate + "'" + " AND adoption.MovementDate <= '" + theUDate +
                "' AND adoption.MovementType = " + Adoption.MOVETYPE_ADOPTION,
                "adoption");
        } catch (Exception e) {
            throw new CursorEngineException(e.getMessage());
        }

        if (rs.getEOF()) {
            throw new NoDataException(Global.i18n("mailmerge",
                    "There_are_no_adoptions_matching_your_criteria."));
        }

        // Set array bounds
        cols = 16;
        theData = new String[(int) rs.getRecordCount() + 1][cols];

        setStatusBarMax((int) rs.getRecordCount());

        // Set header
        theData[0][0] = Global.i18n("mailmerge", "ID");
        theData[0][1] = Global.i18n("mailmerge", "Name");
        theData[0][2] = Global.i18n("mailmerge", "Address_1");
        theData[0][3] = Global.i18n("mailmerge", "Address_2");
        theData[0][4] = Global.i18n("mailmerge", "Address_3");
        theData[0][5] = Global.i18n("mailmerge", "Address_4");
        theData[0][6] = Global.i18n("mailmerge", "Address_5");
        theData[0][7] = Global.i18n("mailmerge", "Town");
        theData[0][8] = Global.i18n("mailmerge", "County");
        theData[0][9] = Global.i18n("mailmerge", "Postcode");
        theData[0][10] = Global.i18n("mailmerge", "Animal_Name");
        theData[0][11] = Global.i18n("mailmerge", "Email_Address");
        theData[0][12] = Global.i18n("mailmerge", "Title");
        theData[0][13] = Global.i18n("mailmerge", "Initials");
        theData[0][14] = Global.i18n("mailmerge", "Forenames");
        theData[0][15] = Global.i18n("mailmerge", "Surname");

        int row = 1;

        // Build data
        while (!rs.getEOF()) {
            // Fill out an entry
            theData[row][0] = rs.getField("ID").toString();
            theData[row][1] = (String) rs.getField("OwnerName");

            String[] add = Utils.separateAddress(rs.getField("OwnerAddress")
                                                   .toString());
            theData[row][2] = add[0];
            theData[row][3] = add[1];
            theData[row][4] = add[2];
            theData[row][5] = add[3];
            theData[row][6] = add[4];
            theData[row][7] = (String) rs.getField("OwnerTown");
            theData[row][8] = (String) rs.getField("OwnerCounty");
            theData[row][9] = Utils.nullToEmptyString((String) rs.getField(
                        "OwnerPostcode"));
            theData[row][10] = (String) rs.getField("AnimalName");
            theData[row][11] = Utils.nullToEmptyString((String) rs.getField(
                        "EmailAddress"));

            theData[row][12] = (String) rs.getField("OwnerTitle");
            theData[row][13] = (String) rs.getField("OwnerInitials");
            theData[row][14] = (String) rs.getField("OwnerForenames");
            theData[row][15] = (String) rs.getField("OwnerSurname");

            row++;

            incrementStatusBar();
            rs.moveNext();
        }

        // Set highest row
        rows = row;
        resetStatusBar();
    }
}
