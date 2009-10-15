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
package net.sourceforge.sheltermanager.asm.mailmerge;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.text.ParseException;


/**
 * Produces a mail merge source of all animals
 * who have been returned from adoption between two dates
 * and the microchip numbers they have been given
 * etc. To cancel with the Chipping company (Eg: Petlog)
 *
 * @author  Robin Rawson-Tetley
 */
public class ChipCancellation extends MailMerge {
    private String theLDate = "";
    private String theUDate = "";

    public ChipCancellation() {
        // Ask how many days in advance they would 
        // like the source for.
        String ldate = Dialog.getDateInput(Global.i18n("mailmerge",
                    "Please_enter_the_start_date_you_would_like_to_prepare_chip_cancellation_letters_for"),
                Global.i18n("mailmerge", "Chip_Cancellation_Mail_Merge"));
        String udate = Dialog.getDateInput(Global.i18n("mailmerge",
                    "Please_enter_the_end_date_you_would_like_to_prepare_chip_cancellation_letters_for"),
                Global.i18n("mailmerge", "Chip_Cancellation_Mail_Merge"));

        try {
            theLDate = Utils.getSQLDateOnly(ldate);
            theUDate = Utils.getSQLDateOnly(udate);
        } catch (ParseException e) {
            Dialog.showError(e.getMessage());

            return;
        }

        // Date is ok, start us off.
        this.start();
    }

    protected String getFileName() {
        return Global.i18n("mailmerge", "ChipCancellations.csv");
    }

    protected int getEmailColumn() {
        return 0;
    }

    protected void getData() throws CursorEngineException, NoDataException {
        Adoption ad = new Adoption();
        ad.openRecordset("ReturnDate >= '" + theLDate + "'" +
            " AND ReturnDate <= '" + theUDate +
            "' AND MovementDate Is Not Null" + " AND MovementType = " +
            Adoption.MOVETYPE_ADOPTION);

        if (ad.getEOF()) {
            throw new NoDataException(Global.i18n("mailmerge",
                    "There_are_no_required_chip_cancellations_between_those_dates."));
        }

        // Set array bounds
        cols = 25;
        theData = new String[(int) ad.getRecordCount() + 5][cols];

        setStatusBarMax((int) ad.getRecordCount());

        // Set header
        theData[0][0] = Global.i18n("mailmerge", "ID");
        theData[0][1] = Global.i18n("mailmerge", "Animal_Name");
        theData[0][2] = Global.i18n("mailmerge", "Shelter_Code");
        theData[0][3] = Global.i18n("mailmerge", "Microchip_Number");
        theData[0][4] = Global.i18n("mailmerge", "Owner_Name");
        theData[0][5] = Global.i18n("mailmerge", "Address_1");
        theData[0][6] = Global.i18n("mailmerge", "Address_2");
        theData[0][7] = Global.i18n("mailmerge", "Address_3");
        theData[0][8] = Global.i18n("mailmerge", "Address_4");
        theData[0][9] = Global.i18n("mailmerge", "Address_5");
        theData[0][10] = Global.i18n("mailmerge", "Town");
        theData[0][11] = Global.i18n("mailmerge", "County");
        theData[0][12] = Global.i18n("mailmerge", "Postcode");
        theData[0][13] = Global.i18n("mailmerge", "Home_Telephone");
        theData[0][14] = Global.i18n("mailmerge", "Work_Telephone");
        theData[0][15] = Global.i18n("mailmerge", "Species");
        theData[0][16] = Global.i18n("mailmerge", "Breed");
        theData[0][17] = Global.i18n("mailmerge", "Sex");
        theData[0][18] = Global.i18n("mailmerge", "Neutered");
        theData[0][19] = Global.i18n("mailmerge", "Date_Of_Birth");
        theData[0][20] = Global.i18n("mailmerge", "Age");
        theData[0][21] = Global.i18n("mailmerge", "Adoption_Date");
        theData[0][22] = Global.i18n("mailmerge", "Colour");
        theData[0][23] = Global.i18n("mailmerge", "Markings");
        theData[0][24] = Global.i18n("mailmerge", "Return_Date");

        int row = 1;
        Animal an = null;
        Owner own = null;

        // Build data
        while (!ad.getEOF()) {
            try {
                // Get the owner and animal
                own = ad.getOwner();
                an = ad.getAnimal();

                // Fill out an entry
                theData[row][0] = an.getID().toString();
                theData[row][1] = an.getAnimalName();
                theData[row][2] = an.getShelterCode();
                theData[row][3] = an.getIdentichipNumber();
                theData[row][4] = own.getOwnerName();

                String[] add = Utils.separateAddress(own.getOwnerAddress());
                theData[row][5] = add[0];
                theData[row][6] = add[1];
                theData[row][7] = add[2];
                theData[row][8] = add[3];
                theData[row][9] = add[4];
                theData[row][10] = (String) own.getOwnerTown();
                theData[row][11] = (String) own.getOwnerCounty();
                theData[row][12] = Utils.nullToEmptyString(own.getOwnerPostcode());
                theData[row][13] = Utils.nullToEmptyString(own.getHomeTelephone());
                theData[row][14] = Utils.nullToEmptyString(own.getWorkTelephone());
                theData[row][15] = an.getSpeciesName();
                theData[row][16] = an.getBreedName();
                theData[row][17] = an.getSexName();
                theData[row][18] = (an.getNeutered().equals("0")
                    ? Global.i18n("mailmerge", "No")
                    : Global.i18n("mailmerge", "Yes"));
                theData[row][19] = Utils.formatDateLong(an.getDateOfBirth());
                theData[row][20] = an.getAge();
                theData[row][21] = Utils.formatDateLong(ad.getAdoptionDate());
                theData[row][22] = an.getBaseColourName();
                theData[row][23] = Utils.nullToEmptyString((String) an.getMarkings());
                theData[row][24] = Utils.formatDateLong(ad.getReturnDate());
                row++;
            } catch (NullPointerException e) {
            }

            incrementStatusBar();
            ad.moveNext();
        }

        // Set highest row
        rows = row;
        resetStatusBar();
    }
}
