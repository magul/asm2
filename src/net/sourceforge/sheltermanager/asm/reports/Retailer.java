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
package net.sourceforge.sheltermanager.asm.reports;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.util.*;


/**
 * Contains all retailer reports:
 *
 * Volume of adoptions per retailer (between two dates) Average time at retailer
 * until adoption Retailer inventory Income derived from retailer (between two
 * dates)
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class Retailer extends Report {
    public static final int VOLUME_ADOPTIONS_PER_RETAILER = 0;
    public static final int AVERAGE_TIME_AT_RETAILER = 1;
    public static final int RETAILER_INVENTORY = 2;
    private int reportType;
    private Date from = new Date();
    private Date to = new Date();
    private String sqlFrom = "";
    private String sqlTo = "";

    public Retailer(int reportType, Date from, Date to) {
        this.reportType = reportType;
        this.from = from;
        this.to = to;
        this.sqlFrom = Utils.getSQLDate(from);
        this.sqlTo = Utils.getSQLDate(to);
        this.start();
    }

    public void generateReport() {
        switch (reportType) {
        case VOLUME_ADOPTIONS_PER_RETAILER:
            volumeAdoptionsPerRetailer();

            break;

        case AVERAGE_TIME_AT_RETAILER:
            averageTimeAtRetailer();

            break;

        case RETAILER_INVENTORY:
            retailerInventory();

            break;
        }
    }

    private void volumeAdoptionsPerRetailer() {
        // Outputs adoption records that follow a retailer
        // record, grouped by retailer
        try {
            Owner retailers = new Owner();
            retailers.openRecordset("IsRetailer = 1 ORDER BY OwnerName");

            setStatusBarMax((int) retailers.getRecordCount());

            boolean firstTable = true;
            double totalDonationsFromRetailers = 0;
            double donationPerRetailer = 0;
            int totalAdoptionsFromRetailers = 0;
            int adoptionPerRetailer = 0;
            String lastRetailer = "";

            while (!retailers.getEOF()) {
                boolean firstAtRetailer = true;

                // Get all returned adoptions to this retailer
                Adoption adoptions = new Adoption();
                adoptions.openRecordset("OwnerID = " + retailers.getID() +
                    " AND ReturnDate Is Not Null");

                while (!adoptions.getEOF()) {
                    // Subsequent adoption in date range?
                    Adoption sub = new Adoption();
                    sub.openRecordset("AnimalID = " + adoptions.getAnimalID() +
                        " AND " + "MovementType = " +
                        Adoption.MOVETYPE_ADOPTION + " AND " +
                        "MovementDate >= '" + sqlFrom + "' AND " +
                        "MovementDate <= '" + sqlTo + "' AND " +
                        "ReturnDate Is Null");

                    if (!sub.getEOF()) {
                        // Finish any previous table and show totals
                        if (!firstTable) {
                            tableFinish();
                            addTable();
                            addParagraph(lastRetailer + " Total Adoptions: " +
                                adoptionPerRetailer);
                            addParagraph(lastRetailer + " Total Donations: " +
                                Global.currencySymbol + donationPerRetailer);
                            donationPerRetailer = 0;
                            adoptionPerRetailer = 0;
                        }

                        // Is this the first one we have shown for
                        // this retailer? If so display it
                        if (firstAtRetailer) {
                            addLevelTwoHeader(retailers.getOwnerName());
                            addParagraph(Utils.formatAddress(
                                    retailers.getOwnerAddress()) + ", " +
                                retailers.getOwnerPostcode() + ": Tel: " +
                                retailers.getHomeTelephone());
                            tableNew(100);
                            firstTable = false;
                            firstAtRetailer = false;
                            tableAddRow();
                            tableAddCell(bold(Global.i18n("reports",
                                        "Adoption_Date") + "</b>"));
                            tableAddCell(bold(Global.i18n("reports", "Code") +
                                    "</b>"));
                            tableAddCell(bold(Global.i18n("reports",
                                        "Animal_Name") + "</b>"));
                            tableAddCell(bold(Global.i18n("reports",
                                        "owner_name") + "</b>"));
                            tableAddCell(bold(Global.i18n("reports", "Donation") +
                                    "</b>"));
                            tableFinishRow();
                        }

                        tableAddRow();
                        tableAddCell(Utils.formatDateLong(sub.getMovementDate()));
                        tableAddCell(sub.getAnimal().getShelterCode());
                        tableAddCell(sub.getAnimal().getAnimalName());
                        tableAddCell(sub.getOwner().getOwnerName());

                        double val = 0;

                        if (sub.getDonation() != null) {
                            val = sub.getDonation().doubleValue();
                        }

                        tableAddCell(Global.currencySymbol + val);
                        tableFinishRow();

                        donationPerRetailer += val;
                        totalDonationsFromRetailers += donationPerRetailer;
                        adoptionPerRetailer++;
                        totalAdoptionsFromRetailers++;
                    }

                    adoptions.moveNext();
                }

                incrementStatusBar();

                lastRetailer = retailers.getOwnerName();
                retailers.moveNext();
            }

            // Finish up and show final totals
            if (!firstTable) {
                tableFinish();
                addTable();
                addParagraph(Global.i18n("reports", "retailer_adoptions",
                        lastRetailer, Integer.toString(adoptionPerRetailer)));
                addParagraph(Global.i18n("reports", "retailer_donations",
                        lastRetailer,
                        Global.currencySymbol + donationPerRetailer));
                addParagraph(bold(Global.i18n("reports",
                            "retailer_total_adoptions",
                            Integer.toString(totalAdoptionsFromRetailers))));
                addParagraph(bold(Global.i18n("reports",
                            "retailer_total_donations",
                            Global.currencySymbol +
                            totalDonationsFromRetailers)));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    private void averageTimeAtRetailer() {
        // Outputs an average based on the length of time
        // each retailer movement for a retailer spent
        // there before adoption
        try {
            Owner retailers = new Owner();
            retailers.openRecordset("IsRetailer = 1 ORDER BY OwnerName");

            setStatusBarMax((int) retailers.getRecordCount());

            double timeAtRetailerInDays = 0;
            int retailerAdoptions = 0;
            int totalAdoptions = 0;
            double totalTime = 0;

            while (!retailers.getEOF()) {
                retailerAdoptions = 0;
                timeAtRetailerInDays = 0;

                // Get all returned adoptions to this retailer
                Adoption adoptions = new Adoption();
                adoptions.openRecordset("OwnerID = " + retailers.getID() +
                    " AND ReturnDate Is Not Null");

                while (!adoptions.getEOF()) {
                    // Subsequent adoption?
                    Adoption sub = new Adoption();
                    sub.openRecordset("AnimalID = " + adoptions.getAnimalID() +
                        " AND " + "MovementType = " +
                        Adoption.MOVETYPE_ADOPTION + " AND " +
                        "ReturnDate Is Null");

                    if (!sub.getEOF()) {
                        Calendar start = Utils.dateToCalendar(adoptions.getMovementDate());
                        Calendar end = Utils.dateToCalendar(adoptions.getReturnDate());
                        double diff = (double) (Utils.getDateDiff(end, start));
                        diff = (diff / 60);
                        diff = (diff / 24);
                        timeAtRetailerInDays = diff;
                        totalTime += timeAtRetailerInDays;
                        retailerAdoptions++;
                        totalAdoptions++;
                    }

                    adoptions.moveNext();
                }

                // If we have something to show, calculate and show it
                if (retailerAdoptions > 0) {
                    addLevelTwoHeader(retailers.getOwnerName());

                    double avg = (timeAtRetailerInDays / retailerAdoptions);

                    addParagraph(Global.i18n("reports", "retailer_average",
                            Double.toString(avg),
                            Integer.toString(retailerAdoptions),
                            Double.toString(timeAtRetailerInDays)));
                }

                incrementStatusBar();
                retailers.moveNext();
            }

            // Show final totals
            addLevelTwoHeader(Global.i18n("reports", "all_retailers"));

            double avg = (totalTime / totalAdoptions);
            addParagraph(Global.i18n("reports", "retailer_average",
                    Double.toString(avg), Integer.toString(totalAdoptions),
                    Double.toString(totalTime)));
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    private void retailerInventory() {
        // Outputs a list of all animals currently
        // at a retailer
        try {
            Owner retailers = new Owner();
            retailers.openRecordset("IsRetailer = 1 ORDER BY OwnerName");

            setStatusBarMax((int) retailers.getRecordCount());

            boolean firstTable = true;
            int totalAdoptionsFromRetailers = 0;
            int adoptionPerRetailer = 0;
            String lastRetailer = "";

            while (!retailers.getEOF()) {
                boolean firstAtRetailer = true;

                // Get all non-returned adoptions to this retailer
                Adoption adoptions = new Adoption();
                adoptions.openRecordset("OwnerID = " + retailers.getID() +
                    " AND ReturnDate Is Null");

                while (!adoptions.getEOF()) {
                    // Finish any previous table and show totals
                    if (!firstTable) {
                        tableFinish();
                        addTable();
                        addParagraph(Global.i18n("reports",
                                "retailer_total_animals", lastRetailer,
                                Integer.toString(adoptionPerRetailer)));
                        adoptionPerRetailer = 0;
                    }

                    // Is this the first one we have shown for
                    // this retailer? If so display it
                    if (firstAtRetailer) {
                        addLevelTwoHeader(retailers.getOwnerName());
                        addParagraph(Utils.formatAddress(
                                retailers.getOwnerAddress()) + ", " +
                            retailers.getOwnerPostcode() + ": Tel: " +
                            retailers.getHomeTelephone());
                        tableNew(100);
                        firstTable = false;
                        firstAtRetailer = false;
                        tableAddRow();
                        tableAddCell(bold(Global.i18n("reports",
                                    "date_entered_retailer")));
                        tableAddCell(bold(Global.i18n("reports", "Code")));
                        tableAddCell(bold(Global.i18n("reports", "Animal_Name")));
                        tableFinishRow();
                    }

                    tableAddRow();
                    tableAddCell(Utils.formatDateLong(
                            adoptions.getMovementDate()));
                    tableAddCell(adoptions.getAnimal().getShelterCode());
                    tableAddCell(adoptions.getAnimal().getAnimalName());
                    tableFinishRow();

                    adoptionPerRetailer++;
                    totalAdoptionsFromRetailers++;

                    adoptions.moveNext();
                }

                incrementStatusBar();

                lastRetailer = retailers.getOwnerName();
                retailers.moveNext();
            }

            // Finish up and show final totals
            if (!firstTable) {
                tableFinish();
                addTable();
                addParagraph(Global.i18n("reports", "retailer_total_animals",
                        lastRetailer, Integer.toString(adoptionPerRetailer)));
                addParagraph(bold(Global.i18n("reports",
                            "total_animals_at_retailers",
                            Integer.toString(totalAdoptionsFromRetailers))));
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }

    public String getTitle() {
        switch (reportType) {
        case VOLUME_ADOPTIONS_PER_RETAILER:
            return Global.i18n("reports",
                "volume_of_adoptions_per_retailer_between",
                Utils.formatDateLong(from), Utils.formatDateLong(to));

        case AVERAGE_TIME_AT_RETAILER:
            return Global.i18n("reports", "average_time_at_retailer");

        case RETAILER_INVENTORY:
            return Global.i18n("reports", "retailer_inventory");
        }

        return "[error]";
    }
}
