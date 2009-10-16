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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;


/**
 * Generates a report showing animal inventories on the shelter.
 *
 * @author Robin Rawson-Tetley
 */
public class AnimalPrint extends Report {
    private Animal a = null;

    public AnimalPrint(Animal a) {
        this.a = a;
        this.start();
    }

    public void generateReport() {
        try {
            generate();
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "An_error_occurred_generating_the_report", e.getMessage()));
            Global.logException(e, getClass());
        }
    }

    public String getTitle() {
        try {
            return Global.i18n("reports", "animal_detail_print",
                a.getShelterCode(), a.getAnimalName(),
                Utils.getReadableTodaysDate());
        } catch (Exception e) {
            Global.logException(e, getClass());

            return "error";
        }
    }

    /** Generates complete information about the animal */
    private void generate() throws Exception {
        addLevelTwoHeader(Global.i18n("uianimal", "animal_details"));

        try {
            String mediaName = a.getWebMedia();

            // If we got a blank, return a link to nopic.jpg instead
            if (mediaName.equals("")) {
                mediaName = "nopic.jpg";
            }

            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                    a.getID().intValue());
            dbfs.readFile(mediaName,
                net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                File.separator + mediaName);

            addImage(mediaName);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        tableNew(true);
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Code:"));
        tableAddCell(a.getShelterCode());
        tableAddBoldCell(Global.i18n("uianimal", "Name:"));
        tableAddCell(a.getReportAnimalName());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Non-Shelter"));
        tableAddCell(yesNo(a.getNonShelterAnimal()));
        tableAddBoldCell(Global.i18n("uianimal", "Not_Available_For_Adoption"));
        tableAddCell(yesNo(a.getIsNotAvailableForAdoption()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Sex:"));
        tableAddCell(a.getSexName());
        tableAddBoldCell(Global.i18n("uianimal", "Type:"));
        tableAddCell(a.getAnimalTypeName());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Base_Colour:"));
        tableAddCell(a.getBaseColourName());
        tableAddBoldCell(Global.i18n("uianimal", "Size:"));
        tableAddCell(a.getSizeName());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Species:"));
        tableAddCell(a.getSpeciesName());
        tableAddBoldCell(Global.i18n("uianimal", "Breed:"));
        tableAddCell(a.getBreedName());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Location:"));
        tableAddCell(a.fastIsAnimalOnShelter() ? a.getShelterLocationName()
                                               : a.fastGetAnimalLocationNowByName());
        tableAddBoldCell(Global.i18n("uianimal", "Date_Of_Birth:"));
        tableAddCell(date(a.getDateOfBirth()) + " (" + a.getAge() + ")");
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Comments:"));
        tableAddCell(a.getAnimalComments());
        tableAddBoldCell(Global.i18n("uianimal", "Dist._Features:"));
        tableAddCell(a.getMarkings());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "good_with_cats"));
        tableAddCell(yesNoUnknown(a.isGoodWithCats()));
        tableAddBoldCell(Global.i18n("uianimal", "good_with_dogs"));
        tableAddCell(yesNoUnknown(a.isGoodWithDogs()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "good_with_kids"));
        tableAddCell(yesNoUnknown(a.isGoodWithKids()));
        tableAddBoldCell(Global.i18n("uianimal", "housetrained"));
        tableAddCell(yesNoUnknown(a.isHouseTrained()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Identichipped:"));
        tableAddCell(yesNoNumberDate(a.getIdentichipped(),
                a.getIdentichipNumber(), a.getIdentichipDate()));
        tableAddBoldCell(Global.i18n("uianimal", "Tattoo:"));
        tableAddCell(yesNoNumberDate(a.getTattoo(), a.getTattooNumber(),
                a.getTattooDate()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Neutered:"));
        tableAddCell(yesNoDate(a.getNeutered(), a.getNeuteredDate()));
        tableAddBoldCell(Global.i18n("uianimal", "Declawed"));
        tableAddCell(yesNo(a.getDeclawed()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Heartworm_Tested:"));
        tableAddCell(yesNoDatePos(a.getHeartwormTested(),
                a.getHeartwormTestDate(), a.getHeartwormTestResult()));
        tableAddBoldCell(Global.i18n("uianimal", "Combi-Tested:"));

        if (Locale.getDefault().equals(Locale.US)) {
            tableAddCell(Global.i18n("uianimal", "FIVResult") + " - " +
                yesNoDatePos(a.getCombiTested(), a.getCombiTestDate(),
                    a.getCombiTestResult()) + ", " +
                Global.i18n("uianimal", "FLVResult") + " - " +
                yesNoDatePos(a.getCombiTested(), a.getCombiTestDate(),
                    a.getFLVTestResult()));
        } else {
            tableAddCell(yesNoDatePos(a.getCombiTested(), a.getCombiTestDate(),
                    a.getCombiTestResult()));
        }

        tableFinishRow();
        tableFinish();
        addTable();

        // Entry Details
        addLevelTwoHeader(Global.i18n("uianimal", "entry_details"));

        tableNew(true);
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Original_Owner:"));
        tableAddCell(owner(a.getOriginalOwner()));
        tableAddBoldCell(Global.i18n("uianimal", "Brought_In_By:"));
        tableAddCell(owner(a.getBroughtInByOwner()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Reason_not_by_owner"));
        tableAddCell(a.getReasonNO());
        tableAddBoldCell(Global.i18n("uianimal", "Entry_Reason"));
        tableAddCell(a.getReasonForEntry());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Entry_Category"));
        tableAddCell(LookupCache.getEntryReasonNameForID(a.getEntryReasonID()));
        tableAddBoldCell(Global.i18n("uianimal", "Entry_Donation:"));
        tableAddCell(money(a.getAmountDonatedOnEntry()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Transfer_In:"));
        tableAddCell(yesNo(a.getIsTransfer()));
        tableAddBoldCell(Global.i18n("uianimal", "Date_Brought_In:"));
        tableAddCell(date(a.getDateBroughtIn()));
        tableFinishRow();
        tableFinish();
        addTable();

        // Vet
        addLevelTwoHeader(Global.i18n("uianimal", "vet"));

        tableNew(true);
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "special_needs"));
        tableAddCell(yesNo(a.isHasSpecialNeeds()));
        tableAddBoldCell(Global.i18n("uianimal", "Health_Problems:"));
        tableAddCell(a.getHealthProblems());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Rabies_Tag"));
        tableAddCell(a.getRabiesTag());
        tableAddBoldCell(Global.i18n("uianimal", "Owners_Vet"));
        tableAddCell(owner(a.getOwnersVet()));
        tableFinishRow();
        tableFinish();
        addTable();

        // Death
        if (a.getDeceasedDate() != null) {
            addLevelTwoHeader(Global.i18n("uianimal", "death"));

            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Date_Deceased:"));
            tableAddCell(date(a.getDeceasedDate()));
            tableAddBoldCell(Global.i18n("uianimal", "Put_To_Sleep:"));
            tableAddCell(yesNo(a.getPutToSleep()));
            tableFinishRow();
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Dead_On_Arrival:"));
            tableAddCell(yesNo(a.getIsDOA()));
            tableAddBoldCell(Global.i18n("uianimal", "Died_Off_Shelter:"));
            tableAddCell(yesNo(a.getDiedOffShelter()));
            tableFinishRow();
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Death_Category"));
            tableAddCell(LookupCache.getDeathReasonNameForID(a.getPTSReasonID()));
            tableAddBoldCell("");
            tableAddCell(a.getPTSReason());
            tableFinishRow();
            tableFinish();
            addTable();
        }

        // Vaccinations
        AnimalVaccination av = new AnimalVaccination();
        av.openRecordset("AnimalID = " + a.getID());

        boolean hasvacc = false;

        if (!av.getEOF()) {
            addLevelTwoHeader(Global.i18n("uianimal", "Vaccination"));
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Vaccination"));
            tableAddBoldCell(Global.i18n("uianimal", "Date_Required:"));
            tableAddBoldCell(Global.i18n("uianimal", "Date_Given:"));
            tableAddBoldCell(Global.i18n("uianimal", "Comments:"));
            tableFinishRow();
            hasvacc = true;
        }

        while (!av.getEOF()) {
            tableAddRow();
            tableAddCell(av.getVaccinationTypeName());
            tableAddCell(date(av.getDateRequired()));
            tableAddCell(date(av.getDateOfVaccination()));
            tableAddCell(av.getComments());
            tableFinishRow();
            av.moveNext();
        }

        if (hasvacc) {
            tableFinish();
            addTable();
        }

        av.free();

        // Medical

        // Diet
        AnimalDiet ad = new AnimalDiet();
        ad.openRecordset("AnimalID = " + a.getID() + " ORDER BY DateStarted");

        boolean hasdiet = false;

        if (!ad.getEOF()) {
            addLevelTwoHeader(Global.i18n("uianimal", "diet"));
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Start_Date:"));
            tableAddBoldCell(Global.i18n("uianimal", "Diet_Type:"));
            tableAddBoldCell(Global.i18n("uianimal", "Comments:"));
            tableFinishRow();
            hasdiet = true;
        }

        while (!ad.getEOF()) {
            tableAddRow();
            tableAddCell(date(ad.getDateStarted()));
            tableAddCell(ad.getDietName());
            tableAddCell(ad.getComments());
            tableFinishRow();
            ad.moveNext();
        }

        if (hasdiet) {
            tableFinish();
            addTable();
        }

        ad.free();

        // Diary
        Diary di = new Diary();
        di.openRecordset("LinkID = " + a.getID() + "AND LinkType = " +
            Diary.LINKTYPE_ANIMAL + " ORDER BY DiaryDateTime");

        boolean hasdiary = false;

        if (!di.getEOF()) {
            addLevelTwoHeader(Global.i18n("uianimal", "diary"));
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uidiary", "Created_By"));
            tableAddBoldCell(Global.i18n("uidiary", "For"));
            tableAddBoldCell(Global.i18n("uidiary", "Date"));
            tableAddBoldCell(Global.i18n("uidiary", "Completed"));
            tableAddBoldCell(Global.i18n("uidiary", "Subject"));
            tableAddBoldCell(Global.i18n("uidiary", "Note"));
            tableFinishRow();
            hasdiary = true;
        }

        while (!di.getEOF()) {
            tableAddRow();
            tableAddCell(di.getCreatedBy());
            tableAddCell(di.getDiaryForName());
            tableAddCell(datetime(di.getDiaryDateTime()));
            tableAddCell(date(di.getDateCompleted()));
            tableAddCell(di.getSubject());
            tableAddCell(di.getNote());
            tableFinishRow();
            di.moveNext();
        }

        if (hasdiary) {
            tableFinish();
            addTable();
        }

        di.free();

        // Movements
        Adoption mo = new Adoption();
        mo.openRecordset("AnimalID = " + a.getID() +
            " ORDER BY MovementDate, ReservationDate");

        boolean hasmove = false;

        if (!mo.getEOF()) {
            addLevelTwoHeader(Global.i18n("uianimal", "movements"));
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uimovement", "Number:"));
            tableAddBoldCell(Global.i18n("uimovement", "Owner"));

            if (!Configuration.getBoolean("DisableRetailer")) {
                tableAddBoldCell(Global.i18n("uimovement", "from_retailer"));
            }

            tableAddBoldCell(Global.i18n("uimovement", "Reservation"));
            tableAddBoldCell(Global.i18n("uimovement", "Cancelled:"));
            tableAddBoldCell(Global.i18n("uimovement", "Movement"));
            tableAddBoldCell(Global.i18n("uimovement", "Type:"));
            tableAddBoldCell(Global.i18n("uimovement", "Returned"));
            tableAddBoldCell(Global.i18n("uimovement", "Reason:"));
            tableAddBoldCell(Global.i18n("uimovement", "Donation:"));
            tableAddBoldCell(Global.i18n("uimovement", "Comments:"));
            tableFinishRow();
            hasmove = true;
        }

        while (!mo.getEOF()) {
            tableAddRow();
            tableAddCell(mo.getAdoptionNumber());
            tableAddCell(owner(mo.getOwner()));

            if (!Configuration.getBoolean("DisableRetailer")) {
                tableAddCell(owner(mo.getRetailer()));
            }

            tableAddCell(date(mo.getReservationDate()));
            tableAddCell(date(mo.getReservationCancelledDate()));
            tableAddCell(date(mo.getMovementDate()));
            tableAddCell(mo.getReadableMovementType());
            tableAddCell(date(mo.getReturnDate()));
            tableAddCell(mo.getReasonForReturn());
            tableAddCell(money(mo.getDonation()));
            tableAddCell(mo.getComments());
            tableFinishRow();
            mo.moveNext();
        }

        if (hasmove) {
            tableFinish();
            addTable();
        }

        mo.free();

        // Log
        Log l = new Log();
        l.openRecordset("LinkID = " + a.getID() + " AND LinkType = " +
            Log.LINKTYPE_ANIMAL + " ORDER BY Date, ID");

        boolean haslog = false;

        if (!l.getEOF()) {
            addLevelTwoHeader(Global.i18n("uianimal", "log"));
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uilog", "Date:"));
            tableAddBoldCell(Global.i18n("uilog", "Log_Type:"));
            tableAddBoldCell(Global.i18n("uilog", "Details"));
            tableFinishRow();
            haslog = true;
        }

        while (!l.getEOF()) {
            tableAddRow();
            tableAddCell(date(l.getDate()));
            tableAddCell(l.getLogTypeName());
            tableAddCell(l.getComments());
            tableFinishRow();
            l.moveNext();
        }

        if (haslog) {
            tableFinish();
            addTable();
        }

        l.free();
    }
}
