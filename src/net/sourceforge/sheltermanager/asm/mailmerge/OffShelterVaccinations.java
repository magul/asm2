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

//------------------------------------------------------------------------------
//  Modified to expose name fields on the user interface and
//  disable anglocentric name parsing.
//
//  Irv.Elshoff@wldelft.nl
//  24 sep 05
//------------------------------------------------------------------------------
package net.sourceforge.sheltermanager.asm.mailmerge;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.text.ParseException;


/**
 * Produces a mail merge source of all animals
 * who require a vaccination within a specified
 * period of time.
 *
 * @author  Robin Rawson-Tetley
 */
public class OffShelterVaccinations extends MailMerge {
    /** MySQL date to show vaccinations for */
    private String theLDate = "";
    private String theUDate = "";

    public OffShelterVaccinations() {
        // Ask how many days in advance they would 
        // like the source for.
        String ldate = Dialog.getDateInput(Global.i18n("mailmerge",
                    "Please_enter_the_start_date_you_would_like_to_prepare_vaccination_letters_for"),
                Global.i18n("mailmerge", "Off-Shelter_Vaccination_Mail_Merge"));

        if (ldate.equals("")) {
            return;
        }

        String udate = Dialog.getDateInput(Global.i18n("mailmerge",
                    "Please_enter_the_end_date_you_would_like_to_prepare_vaccination_letters_for"),
                Global.i18n("mailmerge", "Off-Shelter_Vaccination_Mail_Merge"));

        if (udate.equals("")) {
            return;
        }

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
        return Global.i18n("mailmerge", "OffShelterVaccinations.csv");
    }

    protected int getEmailColumn() {
        return 11;
    }

    protected void getData() throws CursorEngineException, NoDataException {
        AnimalVaccination av = new AnimalVaccination();

        try {
            av.openRecordset("DateOfVaccination Is Null AND DateRequired >= '" +
                theLDate + "'" + " AND DateRequired <= '" + theUDate + "'");
        } catch (Exception e) {
            throw new CursorEngineException(e.getMessage());
        }

        if (av.getEOF()) {
            throw new NoDataException(Global.i18n("mailmerge",
                    "There_are_no_required_vaccinations_on_that_date."));
        }

        // Set array bounds
        cols = 16;
        theData = new String[(int) av.getRecordCount() + 1][cols];

        setStatusBarMax((int) av.getRecordCount());

        // Set header
        theData[0][0] = Global.i18n("mailmerge", "ID");
        theData[0][1] = Global.i18n("mailmerge", "Name");
        theData[0][2] = Global.i18n("mailmerge", "Address_1");
        theData[0][3] = Global.i18n("mailmerge", "Address_2");
        theData[0][4] = Global.i18n("mailmerge", "Address_3");
        theData[0][5] = Global.i18n("mailmerge", "Address_4");
        theData[0][6] = Global.i18n("mailmerge", "Address_5");
        theData[0][7] = Global.i18n("mailmerge", "Postcode");
        theData[0][8] = Global.i18n("mailmerge", "Date_Required");
        theData[0][9] = Global.i18n("mailmerge", "Vaccination_Type");
        theData[0][10] = Global.i18n("mailmerge", "Animal_Name");
        theData[0][11] = Global.i18n("mailmerge", "Email_Address");
        theData[0][12] = Global.i18n("mailmerge", "Title");
        theData[0][13] = Global.i18n("mailmerge", "Initials");
        theData[0][14] = Global.i18n("mailmerge", "Forenames");
        theData[0][15] = Global.i18n("mailmerge", "Surname");

        int row = 1;
        Adoption ad = null;
        Owner own = null;

        // Build data
        while (!av.getEOF()) {
            if (!av.getAnimal().isAnimalOnShelter() &&
                    (av.getAnimal().getDeceasedDate() == null)) {
                try {
                    // Find the latest movement
                    ad = av.getAnimal().getLatestMovement();
                    // Get the owner
                    own = ad.getOwner();

                    // Fill out an entry
                    theData[row][0] = own.getID().toString();
                    theData[row][1] = own.getOwnerName();

                    String[] add = Utils.separateAddress(own.getOwnerAddress());
                    theData[row][2] = add[0];
                    theData[row][3] = add[1];
                    theData[row][4] = add[2];
                    theData[row][5] = add[3];
                    theData[row][6] = add[4];
                    theData[row][7] = Utils.nullToEmptyString(own.getOwnerPostcode());
                    theData[row][8] = Utils.formatDateLong(av.getDateRequired());
                    theData[row][9] = av.getVaccinationName();
                    theData[row][10] = av.getAnimal().getAnimalName();
                    theData[row][11] = Utils.nullToEmptyString(own.getEmailAddress());

                    theData[row][12] = own.getOwnerTitle();
                    theData[row][13] = own.getOwnerInitials();
                    theData[row][14] = own.getOwnerForenames();
                    theData[row][15] = own.getOwnerSurname();

                    row++;
                } catch (NullPointerException e) {
                }
            }

            incrementStatusBar();
            av.moveNext();
        }

        // Set highest row
        rows = row;
        resetStatusBar();
    }
}
