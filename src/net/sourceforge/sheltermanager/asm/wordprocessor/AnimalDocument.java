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
package net.sourceforge.sheltermanager.asm.wordprocessor;

import net.sourceforge.sheltermanager.asm.bo.Additional;
import net.sourceforge.sheltermanager.asm.bo.AdditionalField;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.AnimalDiet;
import net.sourceforge.sheltermanager.asm.bo.AnimalMedical;
import net.sourceforge.sheltermanager.asm.bo.AnimalVaccination;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.animal.MediaSelector;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;
import net.sourceforge.sheltermanager.dbfs.DBFS;

import java.io.File;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 *
 * Generates Animal data documents.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class AnimalDocument extends GenerateDocument {
    /** The animal object */
    Animal animal = null;

    /** The parent component in case we add things as media */
    MediaSelector uiparent = null;

    /**
     * Creates a new animal document with no media parent
     */
    public AnimalDocument(Animal theanimal) {
        this(theanimal, null);
    }

    /**
     * Creates a new animal document.
     *
     * @param theanimal
     *            The <code>Animal</code> object to generate tags from.
     */
    public AnimalDocument(Animal theanimal, MediaSelector uiparent) {
        this.animal = theanimal;
        this.uiparent = uiparent;
        generateDocument();
    }

    /**
     * Constructor to only generate the vector of tags. Useful for other classes
     * wanting animal tags.
     *
     * @param theanimal
     *            The <code>Animal</code> object to generate tags from.
     * @param tagsonly
     *            Doesn't matter what this value is set to, it's existence means
     *            that the object will only generate tags and not try to create
     *            an animal document.
     */
    public AnimalDocument(Animal theanimal, boolean tagsonly) {
        animal = theanimal;
        generateSearchTags();
    }

    /**
     * Reads the passed animal and generates the search tags.
     */
    public void generateSearchTags() {
        try {
            addTag(Global.i18n("wordprocessor", "TotalAnimals"),
                Long.toString(animal.getRecordCount()));
            addTag(Global.i18n("wordprocessor", "AnimalName"),
                animal.getAnimalName());
            addTag(Global.i18n("wordprocessor", "AnimalTypeName"),
                animal.getAnimalTypeName());
            addTag(Global.i18n("wordprocessor", "BaseColourName"),
                animal.getBaseColourName());
            addTag(Global.i18n("wordprocessor", "BreedName"),
                animal.getBreedName());
            addTag(Global.i18n("wordprocessor", "InternalLocation"),
                animal.getShelterLocationName());
            addTag(Global.i18n("wordprocessor", "HealthProblems"),
                Utils.nullToEmptyString(animal.getHealthProblems()));
            addTag(Global.i18n("wordprocessor", "AnimalCreatedBy"),
                animal.getCreatedBy());
            addTag(Global.i18n("wordprocessor", "AnimalCreatedByName"),
                LookupCache.getRealName(animal.getCreatedBy()));
            addTag(Global.i18n("wordprocessor", "AnimalCreatedDate"),
                Utils.formatDate(animal.getCreatedDate()));
            addTag(Global.i18n("wordprocessor", "DateBroughtIn"),
                Utils.formatDate(animal.getDateBroughtIn()));
            addTag(Global.i18n("wordprocessor", "DateOfBirth"),
                Utils.formatDate(animal.getDateOfBirth()));
            addTag(Global.i18n("wordprocessor", "AgeGroup"),
                animal.getAgeGroup());
            addTag(Global.i18n("wordprocessor", "EstimatedDOB"),
                ((animal.getEstimatedDOB().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "estimated") : ""));
            addTag(Global.i18n("wordprocessor", "AnimalID"),
                animal.getID().toString());
            addTag(Global.i18n("wordprocessor", "IdentichipNumber"),
                Utils.nullToEmptyString(animal.getIdentichipNumber()));
            addTag(Global.i18n("wordprocessor", "Identichipped"),
                (animal.getIdentichipped().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "IdentichipDate"),
                Utils.formatDate(animal.getIdentichipDate()));
            addTag(Global.i18n("wordprocessor", "MicrochipNumber"),
                Utils.nullToEmptyString(animal.getIdentichipNumber()));
            addTag(Global.i18n("wordprocessor", "Microchipped"),
                (animal.getIdentichipped().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "MicrochipDate"),
                Utils.formatDate(animal.getIdentichipDate()));

            addTag(Global.i18n("wordprocessor", "Tattoo"),
                (animal.getTattoo().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "TattooDate"),
                Utils.formatDate(animal.getTattooDate()));
            addTag(Global.i18n("wordprocessor", "TattooNumber"),
                Utils.nullToEmptyString(animal.getTattooNumber()));
            addTag(Global.i18n("wordprocessor", "CombiTested"),
                (animal.getCombiTested().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "CombiTestDate"),
                Utils.formatDate(animal.getCombiTestDate()));
            addTag(Global.i18n("wordprocessor", "CombiTestResult"),
                getTestResult(animal.getCombiTestResult(),
                    animal.getCombiTested().intValue() == 1));
            addTag(Global.i18n("wordprocessor", "FLVResult"),
                getTestResult(animal.getFLVTestResult(),
                    animal.getCombiTested().intValue() == 1));
            addTag(Global.i18n("wordprocessor", "HeartwormTested"),
                (animal.getHeartwormTested().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "HeartwormTestDate"),
                Utils.formatDate(animal.getHeartwormTestDate()));
            addTag(Global.i18n("wordprocessor", "HeartwormTestResult"),
                getTestResult(animal.getHeartwormTestResult(),
                    animal.getHeartwormTested().intValue() == 1));
            addTag(Global.i18n("wordprocessor", "HiddenAnimalDetails"),
                Utils.nullToEmptyString(animal.getHiddenAnimalDetails()));
            addTag(Global.i18n("wordprocessor", "AnimalLastChangedBy"),
                animal.getLastChangedBy());
            addTag(Global.i18n("wordprocessor", "AnimalLastChangedByName"),
                LookupCache.getRealName(animal.getLastChangedBy()));
            addTag(Global.i18n("wordprocessor", "AnimalLastChangedDate"),
                Utils.formatDate(animal.getLastChangedDate()));
            addTag(Global.i18n("wordprocessor", "Markings"),
                Utils.nullToEmptyString(animal.getMarkings()));
            addTag(Global.i18n("wordprocessor", "Declawed"),
                ((animal.getDeclawed().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No")));
            addTag(Global.i18n("wordprocessor", "RabiesTag"),
                Utils.nullToEmptyString(animal.getRabiesTag()));
            addTag(Global.i18n("wordprocessor", "GoodWithCats"),
                getTriState(animal.isGoodWithCats()));
            addTag(Global.i18n("wordprocessor", "GoodWithDogs"),
                getTriState(animal.isGoodWithDogs()));
            addTag(Global.i18n("wordprocessor", "GoodWithChildren"),
                getTriState(animal.isGoodWithKids()));
            addTag(Global.i18n("wordprocessor", "HouseTrained"),
                getTriState(animal.isHouseTrained()));

            try {
                addTag(Global.i18n("wordprocessor",
                        "NameOfPersonBroughtAnimalIn"),
                    Utils.nullToEmptyString(animal.getBroughtInByOwner()
                                                  .getOwnerName()));
                addTag(Global.i18n("wordprocessor",
                        "AddressOfPersonBroughtAnimalIn"),
                    Utils.formatAddress(animal.getBroughtInByOwner()
                                              .getOwnerAddress()));
                addTag(Global.i18n("wordprocessor",
                        "TownOfPersonBroughtAnimalIn"),
                    Utils.nullToEmptyString(animal.getBroughtInByOwner()
                                                  .getOwnerTown()));
                addTag(Global.i18n("wordprocessor",
                        "CountyOfPersonBroughtAnimalIn"),
                    Utils.nullToEmptyString(animal.getBroughtInByOwner()
                                                  .getOwnerCounty()));
                addTag(Global.i18n("wordprocessor", "PostcodeOfPersonBroughtIn"),
                    Utils.nullToEmptyString(animal.getBroughtInByOwner()
                                                  .getOwnerPostcode()));
            } catch (Exception e) {
                addTag(Global.i18n("wordprocessor",
                        "NameOfPersonBroughtAnimalIn"), "");
                addTag(Global.i18n("wordprocessor",
                        "AddressOfPersonBroughtAnimalIn"), "");
                addTag(Global.i18n("wordprocessor",
                        "TownOfPersonBroughtAnimalIn"), "");
                addTag(Global.i18n("wordprocessor",
                        "CountyOfPersonBroughtAnimalIn"), "");
                addTag(Global.i18n("wordprocessor", "PostcodeOfPersonBroughtIn"),
                    "");
            }

            try {
                addTag(Global.i18n("wordprocessor", "NameOfOwnersVet"),
                    Utils.nullToEmptyString(animal.getOwnersVet().getOwnerName()));
            } catch (Exception e) {
                addTag(Global.i18n("wordprocessor", "NameOfOwnersVet"), "");
            }

            addTag(Global.i18n("wordprocessor", "HasSpecialNeeds"),
                (animal.isHasSpecialNeeds().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "Neutered"),
                (animal.getNeutered().intValue() == 1)
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "NeuteredDate"),
                Utils.formatDate(animal.getNeuteredDate()));

            // 10 and 12 days after neuter
            String np10 = "";
            String np12 = "";

            if (animal.getNeuteredDate() != null) {
                Calendar cnp10 = Utils.dateToCalendar(animal.getNeuteredDate());
                cnp10.add(Calendar.DAY_OF_YEAR, 10);
                np10 = Utils.formatDate(cnp10);
                cnp10.add(Calendar.DAY_OF_YEAR, 2);
                np12 = Utils.formatDate(cnp10);
            }

            addTag(Global.i18n("wordprocessor", "NeuteredDate10"), np10);
            addTag(Global.i18n("wordprocessor", "NeuteredDate12"), np12);

            try {
                addTag(Global.i18n("wordprocessor", "OriginalOwnerName"),
                    Utils.nullToEmptyString(animal.getOriginalOwner()
                                                  .getOwnerName()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerAddress"),
                    Utils.formatAddress(animal.getOriginalOwner()
                                              .getOwnerAddress()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerTown"),
                    Utils.formatAddress(animal.getOriginalOwner().getOwnerTown()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerCounty"),
                    Utils.formatAddress(animal.getOriginalOwner()
                                              .getOwnerCounty()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerPostcode"),
                    Utils.nullToEmptyString(animal.getOriginalOwner()
                                                  .getOwnerPostcode()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerPhone"),
                    Utils.nullToEmptyString(animal.getOriginalOwner()
                                                  .getHomeTelephone()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerHomePhone"),
                    Utils.nullToEmptyString(animal.getOriginalOwner()
                                                  .getHomeTelephone()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerWorkPhone"),
                    Utils.nullToEmptyString(animal.getOriginalOwner()
                                                  .getWorkTelephone()));
                addTag(Global.i18n("wordprocessor", "OriginalOwnerMobilePhone"),
                    Utils.nullToEmptyString(animal.getOriginalOwner()
                                                  .getMobileTelephone()));
            } catch (Exception e) {
                addTag(Global.i18n("wordprocessor", "OriginalOwnerName"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerAddress"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerTown"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerPostcode"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerPhone"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerHomePhone"),
                    "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerWorkPhone"),
                    "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerMobilePhone"),
                    "");
            }

            addTag(Global.i18n("wordprocessor", "ReasonForEntry"),
                Utils.nullToEmptyString(animal.getReasonForEntry()));
            addTag(Global.i18n("wordprocessor", "ReasonNotBroughtByOwner"),
                Utils.nullToEmptyString(animal.getReasonNO()));
            addTag(Global.i18n("wordprocessor", "Sex"),
                LookupCache.getSexNameForID(animal.getSex()));
            addTag(Global.i18n("wordprocessor", "Size"),
                LookupCache.getSizeNameForID(animal.getSize()));
            addTag(Global.i18n("wordprocessor", "SpeciesName"),
                animal.getSpeciesName());
            addTag(Global.i18n("wordprocessor", "AnimalComments"),
                Utils.nullToEmptyString(animal.getAnimalComments()));
            addTag(Global.i18n("wordprocessor", "ShelterCode"),
                animal.getShelterCode());
            addTag(Global.i18n("wordprocessor", "Age"), animal.getAge());
            addTag(Global.i18n("wordprocessor", "AcceptanceNumber"),
                animal.getAcceptanceNumber());

            try {
                addTag(Global.i18n("wordprocessor", "NumberInLitter"),
                    Integer.toString(DBConnection.executeForCount(
                            "SELECT NumberInLitter FROM animallitter " +
                            "WHERE AcceptanceNumber = '" +
                            animal.getAcceptanceNumber() + "'")));
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            addTag(Global.i18n("wordprocessor", "DeceasedDate"),
                Utils.formatDate(animal.getDeceasedDate()));
            addTag(Global.i18n("wordprocessor", "NoTimesReturned"),
                animal.getNoTimesReturned());
            addTag(Global.i18n("wordprocessor", "ShortShelterCode"),
                animal.getShortCode());
            addTag(Global.i18n("wordprocessor", "MonthBroughtIn"),
                Utils.formatDateMonth(animal.getDateBroughtIn()));
            addTag(Global.i18n("wordprocessor", "ReclaimedDate"),
                Utils.formatDate(animal.getReclaimedDate()));
            addTag(Global.i18n("wordprocessor", "MostRecentEntry"),
                Utils.formatDate(animal.getMostRecentEntry()));
            addTag(Global.i18n("wordprocessor", "MostRecentMonthEntry"),
                Utils.formatDateMonth(animal.getMostRecentEntry()));
            addTag(Global.i18n("wordprocessor", "TimeOnShelter"),
                animal.getTimeOnShelter());

            try {
                addTag(Global.i18n("wordprocessor", "OriginalOwnerTitle"),
                    animal.getOriginalOwner().getOwnerTitle());
                addTag(Global.i18n("wordprocessor", "OriginalOwnerForenames"),
                    animal.getOriginalOwner().getOwnerForenames());
                addTag(Global.i18n("wordprocessor", "OriginalOwnerSurname"),
                    animal.getOriginalOwner().getOwnerSurname());
                addTag(Global.i18n("wordprocessor", "OriginalOwnerInitials"),
                    animal.getOriginalOwner().getOwnerInitials());
            } catch (Exception e) {
                addTag(Global.i18n("wordprocessor", "OriginalOwnerTitle"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerForenames"),
                    "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerSurname"), "");
                addTag(Global.i18n("wordprocessor", "OriginalOwnerInitials"), "");
            }

            // Media information
            addTag(Global.i18n("wordprocessor", "HasValidMedia"),
                (animal.hasValidMedia() ? Global.i18n("uiwordprocessor", "Yes")
                                        : Global.i18n("uiwordprocessor", "No")));

            String webMedia = "nopic.jpg";
            String webMediaNotes = animal.getAnimalComments();
            boolean webMediaNew = false;
            boolean webMediaUpdated = false;

            if (animal.hasValidMedia()) {
                webMedia = animal.getWebMedia();
                webMediaNotes = animal.getWebMediaNotes();
                webMediaNew = animal.isWebMediaNew();
                webMediaUpdated = animal.isWebMediaUpdated();
            }

            addTag(Global.i18n("wordprocessor", "WebMediaFilename"), webMedia);
            addTag(Global.i18n("wordprocessor", "WebMediaNotes"), webMediaNotes);
            addTag(Global.i18n("wordprocessor", "WebMediaNew"),
                webMediaNew ? Global.i18n("uiwordprocessor", "Yes")
                            : Global.i18n("uiwordprocessor", "No"));
            addTag(Global.i18n("wordprocessor", "WebMediaUpdated"),
                webMediaUpdated ? Global.i18n("uiwordprocessor", "Yes")
                                : Global.i18n("uiwordprocessor", "No"));

            // Additional location information
            addTag(Global.i18n("wordprocessor", "AnimalOnShelter"),
                (animal.isAnimalOnShelter()
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No")));
            addTag(Global.i18n("wordprocessor", "AnimalIsReserved"),
                (animal.isAnimalReserved()
                ? Global.i18n("uiwordprocessor", "Yes")
                : Global.i18n("uiwordprocessor", "No")));

            // Data held in additional fields
            try {
                Vector v = Additional.getFieldValues(AdditionalField.LINKTYPE_ANIMAL,
                        animal.getID().intValue());

                for (int i = 0; i < v.size(); i++) {
                    Additional.Field af = (Additional.Field) v.get(i);
                    String val = af.value;

                    if (af.fieldType == AdditionalField.FIELDTYPE_YESNO) {
                        val = af.value.equals("1")
                            ? Global.i18n("uiwordprocessor", "Yes")
                            : Global.i18n("uiwordprocessor", "No");
                    }

                    addTag(af.fieldName, val);
                    Global.logDebug(
                        "Added additional animal field tag, name: '" +
                        af.fieldName + "', value '" + val + "'",
                        "AnimalDocument.generateSearchTags");
                }
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Get a list of vaccinations for this animal
            AnimalVaccination av = new AnimalVaccination();
            av.openRecordset("AnimalID = " + animal.getID() +
                " ORDER BY DateRequired");

            int uniquecount = 1;

            while (!av.getEOF()) {
                addTag(Global.i18n("wordprocessor", "VaccinationName") +
                    Integer.toString(uniquecount), av.getVaccinationName());
                addTag(Global.i18n("wordprocessor", "VaccinationRequired") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(av.getDateRequired()));
                addTag(Global.i18n("wordprocessor", "VaccinationGiven") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(av.getDateOfVaccination()));
                addTag(Global.i18n("wordprocessor", "VaccinationComments") +
                    Integer.toString(uniquecount), av.getComments());
                uniquecount++;
                av.moveNext();
            }

            // Now, lets look at our unique count to work out how many
            // vaccination keys these would replace. Since we are 
            // going to allow a nominal value of upto 100 keys, 
            // we need to replace any not with real values, with blank
            // ones so the ugly keys don't appear on the finished thing.
            if (uniquecount < 100) {
                while (uniquecount < 101) {
                    addTag(Global.i18n("wordprocessor", "VaccinationName") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "VaccinationRequired") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "VaccinationGiven") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "VaccinationComments") +
                        Integer.toString(uniquecount), "");
                    uniquecount++;
                }
            }

            // Get a reverse list of vaccinations for this animal so that 
            // people can use most recent on documents
            av = new AnimalVaccination();
            av.openRecordset("AnimalID = " + animal.getID() +
                " ORDER BY DateRequired DESC");

            uniquecount = 1;

            while (!av.getEOF()) {
                addTag(Global.i18n("wordprocessor", "VaccinationNameLast") +
                    Integer.toString(uniquecount), av.getVaccinationName());
                addTag(Global.i18n("wordprocessor", "VaccinationRequiredLast") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(av.getDateRequired()));
                addTag(Global.i18n("wordprocessor", "VaccinationGivenLast") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(av.getDateOfVaccination()));
                addTag(Global.i18n("wordprocessor", "VaccinationCommentsLast") +
                    Integer.toString(uniquecount), av.getComments());
                uniquecount++;
                av.moveNext();
            }

            // Now, lets look at our unique count to work out how many
            // vaccination keys these would replace. Since we are 
            // going to allow a nominal value of upto 100 keys, 
            // we need to replace any not with real values, with blank
            // ones so the ugly keys don't appear on the finished thing.
            if (uniquecount < 100) {
                while (uniquecount < 101) {
                    addTag(Global.i18n("wordprocessor", "VaccinationNameLast") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor",
                            "VaccinationRequiredLast") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "VaccinationGivenLast") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor",
                            "VaccinationCommentsLast") +
                        Integer.toString(uniquecount), "");
                    uniquecount++;
                }
            }

            // Get a list of medical histories for this animal
            AnimalMedical am = new AnimalMedical();
            am.openRecordset("AnimalID = " + animal.getID() + " ORDER BY ID");

            uniquecount = 1;

            while (!am.getEOF()) {
                addTag(Global.i18n("wordprocessor", "MedicalName") +
                    Integer.toString(uniquecount), am.getTreatmentName());
                addTag(Global.i18n("wordprocessor", "MedicalComments") +
                    Integer.toString(uniquecount), am.getComments());
                addTag(Global.i18n("wordprocessor", "MedicalFrequency") +
                    Integer.toString(uniquecount), am.getNamedFrequency());
                addTag(Global.i18n("wordprocessor", "MedicalNumberOfTreatments") +
                    Integer.toString(uniquecount),
                    am.getNamedNumberOfTreatments());
                addTag(Global.i18n("wordprocessor", "MedicalStatus") +
                    Integer.toString(uniquecount), am.getNamedStatus());
                addTag(Global.i18n("wordprocessor", "MedicalDosage") +
                    Integer.toString(uniquecount), am.getDosage());
                addTag(Global.i18n("wordprocessor", "MedicalStartDate") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(am.getStartDate()));
                addTag(Global.i18n("wordprocessor", "MedicalTreatmentsGiven") +
                    Integer.toString(uniquecount),
                    am.getTreatmentsGiven().toString());
                addTag(Global.i18n("wordprocessor", "MedicalTreatmentsRemaining") +
                    Integer.toString(uniquecount),
                    am.getTreatmentsRemaining().toString());

                uniquecount++;
                am.moveNext();
            }

            // Now, lets look at our unique count to work out how many
            // medical keys these would replace. Since we are going to allow a 
            // nominal value of upto 100 keys, we need to replace any not 
            // with real values, with blank ones so
            // the ugly keys don't appear on the finished thing.
            if (uniquecount < 100) {
                while (uniquecount < 101) {
                    addTag(Global.i18n("wordprocessor", "MedicalName") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "MedicalComments") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "MedicalFrequency") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor",
                            "MedicalNumberOfTreatments") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "MedicalStatus") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "MedicalDosage") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "MedicalStartDate") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "MedicalTreatmentsGiven") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor",
                            "MedicalTreatmentsRemaining") +
                        Integer.toString(uniquecount), "");
                    uniquecount++;
                }
            }

            // Get a list of diet records for this animal
            AnimalDiet ad = new AnimalDiet();
            ad.openRecordset("AnimalID = " + animal.getID() +
                " ORDER BY DateStarted");

            uniquecount = 1;

            while (!ad.getEOF()) {
                addTag(Global.i18n("wordprocessor", "DietName") +
                    Integer.toString(uniquecount), ad.getDietName());
                addTag(Global.i18n("wordprocessor", "DietDescription") +
                    Integer.toString(uniquecount), ad.getDietDescription());
                addTag(Global.i18n("wordprocessor", "DietDateStarted") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(ad.getDateStarted()));
                addTag(Global.i18n("wordprocessor", "DietComments") +
                    Integer.toString(uniquecount), ad.getComments());
                uniquecount++;
                ad.moveNext();
            }

            // Now, lets look at our unique count to work out how many
            // diet keys these would replace. Since we are 
            // going to allow a nominal value of upto 100 keys, 
            // we need to replace any not with real values, with blank
            // ones so the ugly keys don't appear on the finished thing.
            if (uniquecount < 100) {
                while (uniquecount < 101) {
                    addTag(Global.i18n("wordprocessor", "DietName") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "DietDescription") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "DietDateStarted") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "DietComments") +
                        Integer.toString(uniquecount), "");
                    uniquecount++;
                }
            }

            // Get a reverse list of diets for this animal so that 
            // people can use most recent on documents
            ad = new AnimalDiet();
            ad.openRecordset("AnimalID = " + animal.getID() +
                " ORDER BY DateStarted DESC");

            uniquecount = 1;

            while (!ad.getEOF()) {
                addTag(Global.i18n("wordprocessor", "DietNameLast") +
                    Integer.toString(uniquecount), ad.getDietName());
                addTag(Global.i18n("wordprocessor", "DietDescriptionLast") +
                    Integer.toString(uniquecount), ad.getDietDescription());
                addTag(Global.i18n("wordprocessor", "DietDateStartedLast") +
                    Integer.toString(uniquecount),
                    Utils.formatDate(ad.getDateStarted()));
                addTag(Global.i18n("wordprocessor", "DietCommentsLast") +
                    Integer.toString(uniquecount), ad.getComments());
                uniquecount++;
                ad.moveNext();
            }

            // Now, lets look at our unique count to work out how many
            // vaccination keys these would replace. Since we are 
            // going to allow a nominal value of upto 100 keys, 
            // we need to replace any not with real values, with blank
            // ones so the ugly keys don't appear on the finished thing.
            if (uniquecount < 100) {
                while (uniquecount < 101) {
                    addTag(Global.i18n("wordprocessor", "DietNameLast") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "DietDescriptionLast") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "DietDateStartedLast") +
                        Integer.toString(uniquecount), "");
                    addTag(Global.i18n("wordprocessor", "DietCommentsLast") +
                        Integer.toString(uniquecount), "");
                    uniquecount++;
                }
            }

            // Generate a document title based on the animal information
            // and the doc selected
            docTitle = templateName + " - " + animal.getShelterCode() + " " +
                animal.getAnimalName();
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /** If this animal has media, saves it to a temporary file
     *  and returns the name to the caller - this is for use by word
     *  processors that can embed images and allows them to include
     *  them in documents.
     *  @return null if the animal has no web media
     */
    public String getImage() {
        try {
            if (!animal.hasValidMedia()) {
                return null;
            }

            String medianame = animal.getDocMedia();
            String file = net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                File.separator + medianame;
            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                    animal.getID().intValue());
            dbfs.readFile(medianame, file);
            dbfs = null;

            return file;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        return null;
    }

    /**
     * Attach the document to the animal as media.
     */
    public void attachMedia() {
        // They do - lets add the file
        // Get the file extension from the name given
        String fileextension = localfile;
        fileextension = fileextension.substring(fileextension.lastIndexOf(".") +
                1, fileextension.length());

        // Create the media entry
        Media media = new Media();

        try {
            media.openRecordset("ID = 0");
            media.addNew();
            media.setLinkID(animal.getID());
            media.setLinkTypeID(new Integer(Media.LINKTYPE_ANIMAL));
            media.setMediaName(media.getID() + "." + fileextension);
            media.setMediaNotes(docTitle);
            media.setDate(new Date());
            media.setWebSitePhoto(new Integer(0));
            media.setNewSinceLastPublish(new Integer(0));
            media.setUpdatedSinceLastPublish(new Integer(0));
            media.save();
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }

        try {
            // Go to the right directory, creating it if necessary
            DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                    animal.getID().intValue());

            // Upload the local file, giving it the media name generated earlier
            dbfs.putFile(media.getMediaName(), localfile);

            // Update the onscreen list
            if (uiparent != null) {
                uiparent.updateList();
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());

            return;
        }
    }
}
