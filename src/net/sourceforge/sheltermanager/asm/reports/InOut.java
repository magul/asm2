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

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.util.Date;


/**
 * Generates a report showing all in/out movements between two dates and
 * optionally just a summary.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class InOut extends Report {
    private boolean summaryOnly = false;
    private Date from = new Date();
    private String sqlFrom = "";
    private Date to = new Date();
    private String sqlTo = "";
    private int totalAnimalsIn = 0;
    private int totalAnimalsOut = 0;

    /** Creates a new instance of InOut */
    public InOut(boolean showSummary, Date from, Date to) {
        summaryOnly = showSummary;
        this.from = from;
        this.to = to;
        sqlFrom = Utils.getSQLDate(from);
        sqlTo = Utils.getSQLDate(to);
        this.start();
    }

    public void generateReport() {
        // Base the status bar on the number of report sections
        setStatusBarMax(14);

        try {
            // ------------- ANIMAL IN SECTIONS ---------------------
            addLevelTwoHeader(underline(Global.i18n("reports", "Animals_In")));

            genAnimalsBroughtIn();
            incrementStatusBar();
            genAnimalsTransferredIn();
            incrementStatusBar();
            genAnimalsReturnedFromAdoption();
            incrementStatusBar();
            genAnimalsReturnedFromFostering();
            incrementStatusBar();
            genAnimalsReturnedFromTransferringOut();
            incrementStatusBar();
            genAnimalsReturnedFromOther();
            incrementStatusBar();

            addLevelTwoHeader(underline(Global.i18n("reports",
                        "Total_Animals_In_", Integer.toString(totalAnimalsIn))));
            addHorizontalRule();

            // ------------- ANIMAL OUT SECTIONS ---------------------
            addLevelTwoHeader(underline(Global.i18n("reports", "Animals_Out")));

            genAnimalsAdopted();
            incrementStatusBar();
            genAnimalsDeceased();
            incrementStatusBar();
            genAnimalsReclaimed();
            incrementStatusBar();
            genAnimalsFostered();
            incrementStatusBar();
            genAnimalsTransferred();
            incrementStatusBar();
            genAnimalsEscaped();
            incrementStatusBar();
            genAnimalsStolen();
            incrementStatusBar();
            genAnimalsReleased();
            incrementStatusBar();

            addLevelTwoHeader(underline(Global.i18n("reports",
                        "Total_Animals_Out_", Integer.toString(totalAnimalsOut))));
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    private void addLocation(Animal a) throws Exception {
        String s = a.getShelterLocationName();

        if (a.getArchived().intValue() == 1) {
            s += (" (" + a.getAnimalLocationAtDateByName(new Date()) + ")");
        }

        tableAddCell(s);
    }

    private void genAnimalsBroughtIn() throws Exception {
        Animal theA = new Animal();
        theA.openRecordset("DateBroughtIn >= '" + sqlFrom +
            "' AND DateBroughtIn <= '" + sqlTo +
            "' AND IsTransfer = 0 AND NonShelterAnimal = 0 ORDER BY DateBroughtIn");

        if (!theA.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Brought_In")));

                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Brought_In")));
                tableAddCell(bold(Global.i18n("reports", "Reason")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Brought_In_By")));
                tableFinishRow();
            }

            while (!theA.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theA.getDateBroughtIn()));
                    tableAddCell(LookupCache.getEntryReasonNameForID(
                            theA.getEntryReasonID()));
                    tableAddCell(code(theA));
                    tableAddCell(theA.getAcceptanceNumber());
                    tableAddCell(theA.getIdentichipNumber());
                    tableAddCell(theA.getReportAnimalName());
                    tableAddCell(theA.getAnimalTypeName());
                    tableAddCell(theA.getSpeciesName());
                    tableAddCell(theA.getAge());
                    tableAddCell(theA.getSexName());
                    addLocation(theA);

                    try {
                        tableAddCell(theA.getBroughtInByOwner().getOwnerName());
                    } catch (Exception e) {
                        tableAddCell("");
                    }

                    tableFinishRow();
                }

                theA.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(Global.i18n("reports", "Total_Animals_Brought_In_",
                    Long.toString(theA.getRecordCount())));

            totalAnimalsIn += (int) theA.getRecordCount();
        }
    }

    private void genAnimalsTransferredIn() throws Exception {
        Animal theA = new Animal();
        theA.openRecordset("DateBroughtIn >= '" + sqlFrom +
            "' AND DateBroughtIn <= '" + sqlTo +
            "' AND IsTransfer = 1 AND NonShelterAnimal = 0 ORDER BY DateBroughtIn");

        if (!theA.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Transferred_In")));

                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Brought_In")));
                tableAddCell(bold(Global.i18n("reports", "Reason")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Brought_In_By")));
                tableFinishRow();
            }

            while (!theA.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theA.getDateBroughtIn()));
                    tableAddCell(LookupCache.getEntryReasonNameForID(
                            theA.getEntryReasonID()));
                    tableAddCell(code(theA));
                    tableAddCell(theA.getAcceptanceNumber());
                    tableAddCell(theA.getIdentichipNumber());
                    tableAddCell(theA.getReportAnimalName());
                    tableAddCell(theA.getAnimalTypeName());
                    tableAddCell(theA.getSpeciesName());
                    tableAddCell(theA.getAge());
                    tableAddCell(theA.getSexName());
                    addLocation(theA);

                    try {
                        tableAddCell(theA.getBroughtInByOwner().getOwnerName());
                    } catch (Exception e) {
                        tableAddCell("");
                    }

                    tableFinishRow();
                }

                theA.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Transferred_In_",
                        Long.toString(theA.getRecordCount()))));
            totalAnimalsIn += (int) theA.getRecordCount();
        }
    }

    private void genAnimalsReturnedFromAdoption() throws Exception {
        Adoption theAD = new Adoption();
        theAD.openRecordset("ReturnDate >= '" + sqlFrom +
            "' AND ReturnDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_ADOPTION + " ORDER BY ReturnDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Returned_From_Adoption")));

                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Returned_Date")));
                tableAddCell(bold(Global.i18n("reports", "Reason")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Returned_By")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getReturnDate()));
                    tableAddCell(LookupCache.getEntryReasonNameForID(
                            theAD.getReturnedReasonID()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    addLocation(theAD.getAnimal());
                    tableAddCell(theAD.getOwner().getOwnerName());
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Returned_From_Adoption_",
                        Long.toString(theAD.getRecordCount()))));
            totalAnimalsIn += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsReturnedFromFostering() throws Exception {
        Adoption theAD = new Adoption();
        theAD.openRecordset("ReturnDate >= '" + sqlFrom +
            "' AND ReturnDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_FOSTER + " ORDER BY ReturnDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Returned_From_Fostering")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Returned_Date")));
                tableAddCell(bold(Global.i18n("reports", "Reason")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Returned_By")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getReturnDate()));
                    tableAddCell(LookupCache.getEntryReasonNameForID(
                            theAD.getReturnedReasonID()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    addLocation(theAD.getAnimal());
                    tableAddCell(theAD.getOwner().getOwnerName());
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Returned_From_Fostering_",
                        Long.toString(theAD.getRecordCount()))));
            totalAnimalsIn += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsReturnedFromTransferringOut()
        throws Exception {
        Adoption theAD = new Adoption();
        theAD.openRecordset("ReturnDate >= '" + sqlFrom +
            "' AND ReturnDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_TRANSFER + " ORDER BY ReturnDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Returned_From_Transferring_Out")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Returned_Date")));
                tableAddCell(bold(Global.i18n("reports", "Reason")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Returned_By")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getReturnDate()));
                    tableAddCell(LookupCache.getEntryReasonNameForID(
                            theAD.getReturnedReasonID()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    addLocation(theAD.getAnimal());
                    tableAddCell(theAD.getOwner().getOwnerName());
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Returned_From_Transferring_Out_",
                        Long.toString(theAD.getRecordCount()))));
            totalAnimalsIn += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsReturnedFromOther() throws Exception {
        Adoption theAD = new Adoption();
        theAD.openRecordset("ReturnDate >= '" + sqlFrom +
            "' AND ReturnDate <= '" + sqlTo + "'" + " AND (MovementType = " +
            Adoption.MOVETYPE_RELEASED + " OR " + "MovementType = " +
            Adoption.MOVETYPE_RECLAIMED + " OR " + "MovementType = " +
            Adoption.MOVETYPE_ESCAPED + " OR " + "MovementType = " +
            Adoption.MOVETYPE_STOLEN + ")" + " ORDER BY ReturnDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Returned_From_Others")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Returned_Date")));
                tableAddCell(bold(Global.i18n("reports", "Returned_From")));
                tableAddCell(bold(Global.i18n("reports", "Reason")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getReturnDate()));
                    tableAddCell(theAD.getReadableMovementType());
                    tableAddCell(LookupCache.getEntryReasonNameForID(
                            theAD.getReturnedReasonID()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    addLocation(theAD.getAnimal());
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Returned_From_Others_",
                        Long.toString(theAD.getRecordCount()))));
            totalAnimalsIn += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsAdopted() throws Exception {
        Adoption theAD = new Adoption();
        int donations = 0;
        theAD.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_ADOPTION + " ORDER BY MovementDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Adopted")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Adoption_Date")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Adopted_To")));
                tableAddCell(bold(Global.i18n("reports", "Donation")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                String thisdonation = Global.currencySymbol + "0";

                if (theAD.getDonation() != null) {
                    thisdonation = money(theAD.getDonation());
                    donations += theAD.getDonation();
                }

                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getMovementDate()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    tableAddCell(theAD.getAnimal().getShelterLocationName());
                    tableAddCell(theAD.getOwner().getOwnerName());
                    tableAddCell(thisdonation);
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports", "Total_Animals_Adopted_",
                        Long.toString(theAD.getRecordCount())) + "<br />" +
                    bold(Global.i18n("reports", "Total_Donations_",
                            money(donations)))));

            totalAnimalsOut += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsDeceased() throws Exception {
        Animal theA = new Animal();
        theA.openRecordset("DeceasedDate >= '" + sqlFrom +
            "' AND DeceasedDate <= '" + sqlTo +
            "' AND DiedOffShelter = 0 ORDER BY DeceasedDate");

        if (!theA.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Died_On_The_Shelter")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Deceased")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Put_To_Sleep")));
                tableAddCell(bold(Global.i18n("reports", "DOA")));
                tableAddCell(bold(Global.i18n("reports", "PTS_Reason")));
                tableFinishRow();
            }

            while (!theA.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theA.getDeceasedDate()));
                    tableAddCell(code(theA));
                    tableAddCell(theA.getAcceptanceNumber());
                    tableAddCell(theA.getIdentichipNumber());
                    tableAddCell(theA.getReportAnimalName());
                    tableAddCell(theA.getAnimalTypeName());
                    tableAddCell(theA.getSpeciesName());
                    tableAddCell(theA.getAge());
                    tableAddCell(theA.getSexName());
                    tableAddCell(theA.getShelterLocationName());
                    tableAddCell(((theA.getPutToSleep().intValue() == 1)
                        ? "Yes" : "No"));
                    tableAddCell(((theA.getIsDOA().intValue() == 1) ? "Yes" : "No"));

                    String dr = Utils.nullToEmptyString(LookupCache.getDeathReasonNameForID(
                                theA.getPTSReasonID()));
                    String pc = Utils.nullToEmptyString(theA.getPTSReason());
                    tableAddCell(dr + (pc.equals("") ? "" : ": ") + pc);
                    tableFinishRow();
                }

                theA.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Died_On_Shelter_",
                        Long.toString(theA.getRecordCount()))));

            totalAnimalsOut += (int) theA.getRecordCount();
        }
    }

    private void genAnimalsReclaimed() throws Exception {
        Adoption theAd = new Adoption();
        theAd.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_RECLAIMED + " ORDER BY MovementDate");

        if (!theAd.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Reclaimed")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Reclaimed")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableFinishRow();
            }

            while (!theAd.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAd.getMovementDate()));
                    tableAddCell(code(theAd.getAnimal()));
                    tableAddCell(theAd.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAd.getAnimal().getIdentichipNumber());
                    tableAddCell(theAd.getAnimal().getReportAnimalName());
                    tableAddCell(theAd.getAnimal().getAnimalTypeName());
                    tableAddCell(theAd.getAnimal().getSpeciesName());
                    tableAddCell(theAd.getAnimal().getAge());
                    tableAddCell(theAd.getAnimal().getSexName());
                    tableAddCell(theAd.getAnimal().getShelterLocationName());
                    tableFinishRow();
                }

                theAd.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Reclaimed_",
                        Long.toString(theAd.getRecordCount()))));

            totalAnimalsOut += (int) theAd.getRecordCount();
        }
    }

    private void genAnimalsFostered() throws Exception {
        Adoption theAD = new Adoption();
        theAD.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_FOSTER + " ORDER BY MovementDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Fostered")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Fostered")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Fostered_To")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getMovementDate()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    tableAddCell(theAD.getAnimal().getShelterLocationName());
                    tableAddCell(theAD.getOwner().getOwnerName());
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports", "Total_Animals_Fostered_",
                        Long.toString(theAD.getRecordCount()))));

            totalAnimalsOut += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsTransferred() throws Exception {
        Adoption theAD = new Adoption();
        theAD.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_TRANSFER + " ORDER BY MovementDate");

        if (!theAD.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Transferred")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Transferred")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableAddCell(bold(Global.i18n("reports", "Transferred_To")));
                tableFinishRow();
            }

            while (!theAD.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAD.getMovementDate()));
                    tableAddCell(code(theAD.getAnimal()));
                    tableAddCell(theAD.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAD.getAnimal().getIdentichipNumber());
                    tableAddCell(theAD.getAnimal().getReportAnimalName());
                    tableAddCell(theAD.getAnimal().getAnimalTypeName());
                    tableAddCell(theAD.getAnimal().getSpeciesName());
                    tableAddCell(theAD.getAnimal().getAge());
                    tableAddCell(theAD.getAnimal().getSexName());
                    tableAddCell(theAD.getAnimal().getShelterLocationName());
                    tableAddCell(theAD.getOwner().getOwnerName());
                    tableFinishRow();
                }

                theAD.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports",
                        "Total_Animals_Transferred_",
                        Long.toString(theAD.getRecordCount()))));

            totalAnimalsOut += (int) theAD.getRecordCount();
        }
    }

    private void genAnimalsEscaped() throws Exception {
        Adoption theAd = new Adoption();
        theAd.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_ESCAPED + " ORDER BY MovementDate");

        if (!theAd.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Escaped")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Escaped")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableFinishRow();
            }

            while (!theAd.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAd.getMovementDate()));
                    tableAddCell(code(theAd.getAnimal()));
                    tableAddCell(theAd.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAd.getAnimal().getIdentichipNumber());
                    tableAddCell(theAd.getAnimal().getReportAnimalName());
                    tableAddCell(theAd.getAnimal().getAnimalTypeName());
                    tableAddCell(theAd.getAnimal().getSpeciesName());
                    tableAddCell(theAd.getAnimal().getAge());
                    tableAddCell(theAd.getAnimal().getSexName());
                    tableAddCell(theAd.getAnimal().getShelterLocationName());
                    tableFinishRow();
                }

                theAd.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports", "Total_Animals_Escaped_",
                        Long.toString(theAd.getRecordCount()))));

            totalAnimalsOut += (int) theAd.getRecordCount();
        }
    }

    private void genAnimalsStolen() throws Exception {
        Adoption theAd = new Adoption();
        theAd.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_STOLEN + " ORDER BY MovementDate");

        if (!theAd.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports", "Animals_Stolen")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Stolen")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableFinishRow();
            }

            while (!theAd.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAd.getMovementDate()));
                    tableAddCell(code(theAd.getAnimal()));
                    tableAddCell(theAd.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAd.getAnimal().getIdentichipNumber());
                    tableAddCell(theAd.getAnimal().getReportAnimalName());
                    tableAddCell(theAd.getAnimal().getAnimalTypeName());
                    tableAddCell(theAd.getAnimal().getSpeciesName());
                    tableAddCell(theAd.getAnimal().getAge());
                    tableAddCell(theAd.getAnimal().getSexName());
                    tableAddCell(theAd.getAnimal().getShelterLocationName());
                    tableFinishRow();
                }

                theAd.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports", "Total_Animals_Stolen_",
                        Long.toString(theAd.getRecordCount()))));

            totalAnimalsOut += (int) theAd.getRecordCount();
        }
    }

    private void genAnimalsReleased() throws Exception {
        Adoption theAd = new Adoption();
        theAd.openRecordset("MovementDate >= '" + sqlFrom +
            "' AND MovementDate <= '" + sqlTo + "' AND MovementType = " +
            Adoption.MOVETYPE_RELEASED + " ORDER BY MovementDate");

        if (!theAd.getEOF()) {
            if (!summaryOnly) {
                addParagraph(bold(Global.i18n("reports",
                            "Animals_Released_To_Wild")));
                tableNew();
                tableAddRow();
                tableAddCell(bold(Global.i18n("reports", "Date_Released")));
                tableAddCell(bold(Global.i18n("reports", "Code")));

                if (Configuration.getBoolean("AutoLitterIdentification")) {
                    tableAddCell(bold(Global.i18n("reports", "Litter_ID")));
                } else {
                    tableAddCell(bold(Global.i18n("reports", "Acceptance_Number")));
                }

                tableAddCell(bold(Global.i18n("reports", "Identichip_No")));
                tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                tableAddCell(bold(Global.i18n("reports", "Type")));
                tableAddCell(bold(Global.i18n("reports", "Species")));
                tableAddCell(bold(Global.i18n("reports", "Age")));
                tableAddCell(bold(Global.i18n("reports", "Sex")));
                tableAddCell(bold(Global.i18n("reports", "Internal_Loc")));
                tableFinishRow();
            }

            while (!theAd.getEOF()) {
                if (!summaryOnly) {
                    tableAddRow();
                    tableAddCell(Utils.formatDate(theAd.getMovementDate()));
                    tableAddCell(code(theAd.getAnimal()));
                    tableAddCell(theAd.getAnimal().getAcceptanceNumber());
                    tableAddCell(theAd.getAnimal().getIdentichipNumber());
                    tableAddCell(theAd.getAnimal().getReportAnimalName());
                    tableAddCell(theAd.getAnimal().getAnimalTypeName());
                    tableAddCell(theAd.getAnimal().getSpeciesName());
                    tableAddCell(theAd.getAnimal().getAge());
                    tableAddCell(theAd.getAnimal().getSexName());
                    tableAddCell(theAd.getAnimal().getShelterLocationName());
                    tableFinishRow();
                }

                theAd.moveNext();
            }

            if (!summaryOnly) {
                tableFinish();
                addTable();
            }

            addParagraph(bold(Global.i18n("reports", "Total_Animals_Released_",
                        Long.toString(theAd.getRecordCount()))));

            totalAnimalsOut += (int) theAd.getRecordCount();
        }
    }

    public String getTitle() {
        return Global.i18n("reports", "Daily_In/Out_Report_between_",
            Utils.formatDateLong(from), Utils.formatDateLong(to));
    }
}
