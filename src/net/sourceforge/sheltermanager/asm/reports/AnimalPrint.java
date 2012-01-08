/*
 Animal Shelter Manager
 Copyright(c)2000-2011, R. Rawson-Tetley

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

import net.sourceforge.sheltermanager.asm.bo.Additional;
import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalCost;
import net.sourceforge.sheltermanager.asm.bo.AnimalDiet;
import net.sourceforge.sheltermanager.asm.bo.AnimalMedical;
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.Log;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.bo.OwnerDonation;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Generates a report showing animal details
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
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }
    }

    public String getTitle() {
        try {
            return Global.i18n("reports", "animal_detail_print", code(a),
                a.getAnimalName(), Utils.getReadableTodaysDate());
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
            } else {
                DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                        a.getID().intValue());
                dbfs.readFile(mediaName,
                    net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                    File.separator + mediaName);
            }

            addImage(mediaName, "right", 320, 200);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        tableNew(true);
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Code:"));
        tableAddCell(code(a));
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
        tableAddBoldCell(Global.i18n("uianimal", "Coat_Type"));
        tableAddCell(a.getCoatTypeName());
        tableFinishRow();
        tableAddRow();
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
        tableAddRow();

        String littertext = Configuration.getBoolean("AutoLitterIdentification")
            ? Global.i18n("uianimal", "litter_id")
            : Global.i18n("uianimal", "Acceptance_No:");
        tableAddBoldCell(littertext);
        tableAddCell(a.getAcceptanceNumber());
        tableFinishRow();
        tableFinish();
        addTable();

        // Entry Details
        addLevelTwoHeader(Global.i18n("uianimal", "entry_details"));

        tableNew(true);

        if (!Configuration.getBoolean("AnimalPrintHideOriginalOwner")) {
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Original_Owner:"));
            tableAddCell(owner(a.getOriginalOwner()));
            tableAddBoldCell(Global.i18n("uianimal", "Brought_In_By:"));
            tableAddCell(owner(a.getBroughtInByOwner()));
            tableFinishRow();
        }

        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Reason_not_by_owner"));
        tableAddCell(a.getReasonNO());
        tableAddBoldCell(Global.i18n("uianimal", "Entry_Reason"));
        tableAddCell(a.getReasonForEntry());
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Transfer_In:"));
        tableAddCell(yesNo(a.getIsTransfer()));
        tableAddBoldCell(Global.i18n("uianimal", "Date_Brought_In:"));
        tableAddCell(date(a.getDateBroughtIn()));
        tableFinishRow();
        tableAddRow();
        tableAddBoldCell(Global.i18n("uianimal", "Entry_Category"));
        tableAddCell(LookupCache.getEntryReasonNameForID(a.getEntryReasonID()));

        if (!Configuration.getBoolean("DontShowBonded")) {
            tableAddBoldCell(Global.i18n("uianimal", "Bonded"));
            tableAddCell(a.getBondedAnimalDisplay());
        }

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
            tableAddBoldCell(Global.i18n("uianimal", "Cost:"));
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
            tableAddCell(av.getCost().toString());
            tableFinishRow();
            av.moveNext();
        }

        if (hasvacc) {
            tableFinish();
            addTable();
        }

        av.free();

        // Medical
        AnimalMedical am = new AnimalMedical();
        am.openRecordset("AnimalID = " + a.getID() + " ORDER BY StartDate");

        boolean hasmedical = false;
        
        if (!am.getEOF()) {
            addLevelTwoHeader(Global.i18n("uianimal", "medical"));
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uimedical", "Start_Date"));
            tableAddBoldCell(Global.i18n("uimedical", "Status"));
            tableAddBoldCell(Global.i18n("uimedical", "Treatment_Name"));
            tableAddBoldCell(Global.i18n("uimedical", "Dosage"));
            tableAddBoldCell(Global.i18n("uimedical", "Frequency"));
            tableAddBoldCell("");
            tableFinishRow();
            hasmedical = true;
        }

        while (!am.getEOF()) {
            tableAddRow();
            tableAddCell(date(am.getStartDate()));
            tableAddCell(am.getNamedStatus());
            tableAddCell(am.getTreatmentName());
            tableAddCell(am.getDosage());
            tableAddCell(am.getNamedFrequency());
            tableAddCell(am.getTreatmentsGiven() + " / " + 
                am.getTotalNumberOfTreatments());
            tableFinishRow();
            am.moveNext();
        }

        if (hasmedical) {
            tableFinish();
            addTable();
        }

        am.free();

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

        // Costs
        AnimalCost ac = new AnimalCost();
        ac.openRecordset("AnimalID = " + a.getID() + " ORDER BY CostDate");

        addLevelTwoHeader(Global.i18n("uianimal", "Costs"));

        boolean hascost = false;

        if (!ac.getEOF()) {
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uianimal", "Date"));
            tableAddBoldCell(Global.i18n("uianimal", "Type"));
            tableAddBoldCell(Global.i18n("uianimal", "Amount"));
            tableAddBoldCell(Global.i18n("uianimal", "Description"));
            tableFinishRow();
            hascost = true;
        }

        while (!ac.getEOF()) {
            tableAddRow();
            tableAddCell(date(ac.getCostDate()));
            tableAddCell(ac.getCostTypeName());
            tableAddCell(Utils.formatCurrency(ac.getCostAmount()));
            tableAddCell(ac.getDescription());
            tableFinishRow();
            ac.moveNext();
        }

        if (hascost) {
            tableFinish();
            addTable();
        }

        // Days on shelter, board line
        double tb = 0;

        if (a.getArchived().intValue() == 0) {
            double dbc = 0;
            if (a.getDailyBoardingCost() != null)
                dbc = a.getDailyBoardingCost().doubleValue();
            tb = a.getDaysOnShelter() * dbc;

            String sboard = Global.i18n("uianimal",
                    "On_shelter_days_total_cost",
                    Integer.toString(a.getDaysOnShelter()),
                    Utils.formatCurrency(tb));
            addParagraph(sboard);
        }

        // Total costs of vaccinations and medicals with total balance and donations
        SQLRecordset tots = new SQLRecordset();
        tots.openRecordset("SELECT " +
            "(SELECT SUM(Cost) FROM animalvaccination WHERE AnimalID = animal.ID AND DateOfVaccination Is Not Null) AS totvacc, " +
            "(SELECT SUM(Cost) FROM animalmedical WHERE AnimalID = animal.ID) AS totmed, " +
            "(SELECT SUM(CostAmount) FROM animalcost WHERE AnimalID = animal.ID) AS totcost, " +
            "(SELECT SUM(Donation) FROM ownerdonation WHERE AnimalID = animal.ID) AS totdon " +
            "FROM animal WHERE ID = " + a.getID(), "animal");

        double tv = tots.getDouble("totvacc");
        double tm = tots.getDouble("totmed");
        double tc = tots.getDouble("totcost");
        double td = tots.getDouble("totdon");
        double ta = tv + tm + tc + tb;
        String scost = Global.i18n("uianimal", "cost_totals",
                Utils.formatCurrency(tv), Utils.formatCurrency(tm),
                Utils.formatCurrency(tc), "<b>" + Utils.formatCurrency(ta)) +
            "</b><br />" +
            Global.i18n("uianimal", "cost_balance", Utils.formatCurrency(td),
                "<b>" + Utils.formatCurrency(td - ta) + "</b>");
        addParagraph(scost);

        // Donations
        OwnerDonation od = new OwnerDonation();
        od.openRecordset("AnimalID = " + a.getID() + " ORDER BY Date");

        addLevelTwoHeader(Global.i18n("uianimal", "Donations"));

        boolean hasdon = false;

        if (!od.getEOF()) {
            tableNew(true);
            tableAddRow();
            tableAddBoldCell(Global.i18n("uiowner", "date_due"));
            tableAddBoldCell(Global.i18n("uiowner", "date_received"));
            tableAddBoldCell(Global.i18n("uiowner", "Name"));
            tableAddBoldCell(Global.i18n("uiowner", "receipt_number"));
            tableAddBoldCell(Global.i18n("uiowner", "donation"));
            tableAddBoldCell(Global.i18n("uiowner", "type"));
            tableAddBoldCell(Global.i18n("uiowner", "frequency"));
            tableAddBoldCell(Global.i18n("uiowner", "comments"));
            tableFinishRow();
            hasdon = true;
        }

        while (!od.getEOF()) {
            tableAddRow();
            tableAddCell(date(od.getDateDue()));
            tableAddCell(date(od.getDateReceived()));
            tableAddCell(od.getOwner().getOwnerName());
            tableAddCell(Utils.nullToEmptyString(od.getReceiptNum()));
            tableAddCell(Utils.formatCurrency(od.getDonation().doubleValue()));
            tableAddCell(od.getDonationTypeName());
            tableAddCell(LookupCache.getDonationFreqForID(od.getFrequency()));
            tableAddCell(od.getComments());
            tableFinishRow();
            od.moveNext();
        }

        if (hasdon) {
            tableFinish();
            addTable();
        }

        // Diary
        Diary di = new Diary();
        di.openRecordset("LinkID = " + a.getID() + " AND LinkType = " +
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

        // Additional Fields
        ArrayList<Additional.Field> v = Additional.getFieldValues(AdditionalField.LINKTYPE_ANIMAL,
                a.getID().intValue());

        if (v.size() > 0) {
            addLevelTwoHeader(Global.i18n("uianimal", "additional"));
            tableNew(true);

            for (int i = 0; i < v.size(); i++) {
                Additional.Field af = (Additional.Field) v.get(i);
                tableAddRow();
                tableAddCell(af.fieldLabel);
                tableAddCell(af.value);
                tableFinishRow();
            }

            tableFinish();
            addTable();
        }
    }
}
