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

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Date;


/**
 * Generates a report showing animal inventories on the shelter.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class ShelterInventory extends Report {
    private boolean isDetailed = false;

    /** Creates a new instance of ShelterInventory */
    public ShelterInventory(boolean showdetail) {
        isDetailed = showdetail;
        this.start();
    }

    public void generateReport() {
        try {
            if (isDetailed) {
                generateDetail();
            } else {
                generate();
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public String getTitle() {
        if (isDetailed) {
            return Global.i18n("reports",
                "Detailed_Shelter_Animal_Inventory_at_",
                Utils.getReadableTodaysDate());
        } else {
            return Global.i18n("reports", "Shelter_Animal_Inventory_at_",
                Utils.getReadableTodaysDate());
        }
    }

    /** Generates normal shelter inventory */
    private void generate() throws Exception {
        SQLRecordset theAT = LookupCache.getAnimalTypeLookup();
        theAT.moveFirst();

        SQLRecordset theIL = LookupCache.getInternalLocationLookup();
        theIL.moveFirst();

        SQLRecordset theS = LookupCache.getSpeciesLookup();
        theS.moveFirst();

        int totalAnimals = 0;
        int currentAnimals = 0;

        setStatusBarMax((int) theIL.getRecordCount());

        while (!theIL.getEOF()) {
            // Current location by -------------------------------
            totalAnimals = 0;

            String spectype = "";

            theS.moveFirst();

            while (!theS.getEOF()) {
                currentAnimals = Animal.getNumberOfAnimalsOnShelter(new Date(),
                        ((Integer) theS.getField("ID")).intValue(), 0,
                        ((Integer) theIL.getField("ID")).intValue(),
                        Animal.ALLAGES);
                totalAnimals += currentAnimals;

                if (currentAnimals > 0) {
                    spectype += (theS.getField("SpeciesName").toString() +
                    ": " + Integer.toString(currentAnimals) + "<br>");
                }

                theS.moveNext();
            }

            if (totalAnimals > 0) {
                addLevelTwoHeader(theIL.getField("LocationName").toString());
                addParagraph(spectype);
            }

            incrementStatusBar();
            theIL.moveNext();
        }

        // If we are showing fostered animals as on the shelter, then
        // include a total for them too
        if (Configuration.getBoolean("FosterOnShelter")) {
            addLevelTwoHeader(Global.i18n("reports", "fostered") + ":");
            theS.moveFirst();

            String spectype = "";
            totalAnimals = 0;

            while (!theS.getEOF()) {
                currentAnimals = Animal.getNumberOfAnimalsOnFoster(new Date(),
                        ((Integer) theS.getField("ID")).intValue(), 0);
                totalAnimals += currentAnimals;

                if (currentAnimals > 0) {
                    spectype += (theS.getField("SpeciesName").toString() +
                    ": " + Integer.toString(currentAnimals) + "<br />");
                }

                theS.moveNext();
            }

            if (totalAnimals > 0) {
                addParagraph(spectype);
            }
        }
    }

    /** Generates a detailed shelter inventory */
    private void generateDetail() throws Exception {
        addLevelTwoHeader(Global.i18n("reports",
                "Animal_Breakdown_by_Location_and_Species"));

        SQLRecordset theIL = LookupCache.getInternalLocationLookup();
        theIL.moveFirst();
        setStatusBarMax((int) theIL.getRecordCount());

        int totalAnimalsAtLocation = 0;
        int totalAnimalsOfSpecies = 0;

        boolean fosterOnShelter = Configuration.getBoolean("FosterOnShelter");

        while (!theIL.getEOF()) {
            SQLRecordset theS = LookupCache.getSpeciesLookup();
            theS.moveFirst();

            // Add the location
            addLevelTwoHeader(theIL.getField("LocationName").toString());

            totalAnimalsAtLocation = 0;

            while (!theS.getEOF()) {
                totalAnimalsOfSpecies = 0;

                // Get all the animals of this species at this location
                // (excluding dead ones)
                Animal theA = new Animal();
                theA.openRecordset("SpeciesID = " + theS.getField("ID") +
                    " AND ShelterLocation = " + theIL.getField("ID") +
                    " AND DeceasedDate Is Null AND Archived = 0  AND NonShelterAnimal = 0");

                // Build the list of animals
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Code")));
                tableAddCell(bold(Global.i18n("reports", "Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Date_Entered_Shelter")));
                tableAddCell(bold(Global.i18n("reports", "Method_of_Entry")));
                tableFinishRow();

                while (!theA.getEOF()) {
                    // Make sure the animal is on the shelter
                    boolean onShelter = false;
                    String dateOfEntry = "";
                    String methodOfEntry = "";
                    Adoption theAD = null;

                    // We are only interested in movements - reserves
                    // can be ignored
                    theAD = new Adoption();
                    theAD.openRecordset("AnimalID = " + theA.getID() + " AND " +
                        "MovementDate Is Not Null");

                    if (theAD.getEOF()) {
                        onShelter = true;
                        dateOfEntry = Utils.formatDate(theA.getDateBroughtIn());
                        methodOfEntry = Global.i18n("reports",
                                "Brought_Into_Shelter");
                    } else {
                        // Have to check movement records now.
                        // If there is a returned movement, then they are on
                        // the shelter.

                        // If there is a foster movement and we are counting
                        // fostered as on the shelter, then include them

                        // If there is a retailer movement, they are on
                        // the shelter
                        theAD.moveLast();

                        boolean returned = theAD.getReturnDate() != null;
                        boolean fosterRecord = theAD.getMovementType().intValue() == Adoption.MOVETYPE_FOSTER;
                        boolean retailerRecord = theAD.getMovementType()
                                                      .intValue() == Adoption.MOVETYPE_RETAILER;

                        if (fosterOnShelter && fosterRecord && !returned) {
                            onShelter = true;
                        }

                        if (retailerRecord) {
                            onShelter = true;
                        }

                        if (returned) {
                            onShelter = true;
                        }

                        // Set method of entry
                        if (returned) {
                            dateOfEntry = Utils.formatDate(theAD.getReturnDate());

                            if (theAD.getAdoptionDate() != null) {
                                methodOfEntry = Global.i18n("reports",
                                        "adoption_to_",
                                        theAD.getOwner().getOwnerName());
                            } else if (theAD.getFosteredDate() != null) {
                                methodOfEntry = Global.i18n("reports",
                                        "fostering_to_",
                                        theAD.getOwner().getOwnerName());
                            } else if (theAD.getTransferDate() != null) {
                                methodOfEntry = Global.i18n("reports",
                                        "transfer_from_",
                                        theAD.getOwner().getOwnerName());
                            }
                        } else {
                            dateOfEntry = Utils.formatDate(theA.getDateBroughtIn());
                            methodOfEntry = Global.i18n("reports",
                                    "Brought_Into_Shelter");
                        }
                    }

                    if (onShelter) {
                        tableAddRow();
                        tableAddCell(code(theA));
                        tableAddCell(theA.getReportAnimalName());
                        tableAddCell(theA.getAnimalTypeName());
                        tableAddCell(dateOfEntry);
                        tableAddCell(methodOfEntry);
                        tableFinishRow();

                        totalAnimalsOfSpecies++;
                    }

                    theA.moveNext();
                }

                tableFinish();

                // If there actually were some animals, add the
                // section to the report
                if (totalAnimalsOfSpecies > 0) {
                    addLevelThreeHeader(theS.getField("SpeciesName") +
                        (((theS.getField("SpeciesDescription") == null) ||
                        (theS.getField("SpeciesDescription").toString()
                             .equals(""))) ? ""
                                           : ("(" +
                        theS.getField("SpeciesDescription").toString() + ")")));
                    addTable();
                    addParagraph(Global.i18n("reports", "Total_",
                            theS.getField("SpeciesName").toString(),
                            Integer.toString(totalAnimalsOfSpecies)));
                    totalAnimalsAtLocation += totalAnimalsOfSpecies;
                }

                // Next species for this location
                theS.moveNext();
            }

            // Location total
            addParagraph(bold(Global.i18n("reports", "Total_",
                        theIL.getField("LocationName").toString(),
                        Integer.toString(totalAnimalsAtLocation))));

            incrementStatusBar();
            theIL.moveNext();
        }
    }
}
