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
import net.sourceforge.sheltermanager.asm.ui.criteria.DateFromTo;
import net.sourceforge.sheltermanager.asm.ui.criteria.FromToListener;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.util.*;


/**
 * Generates a report showing matching lost/found animals between two dates with
 * a potential cap.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class LostFoundMatch extends Report implements FromToListener {
    private boolean includeShelter = false;
    private boolean filterByDate = false;
    private Date from = new Date();
    private Date to = new Date();
    private String sqlFrom = "";
    private String sqlTo = "";
    private int lostAnimalID = 0;
    private int foundAnimalID = 0;
    private int animalID = 0;

    public LostFoundMatch(int lostID) {
        this(lostID, 0, 0);
    }

    public LostFoundMatch(int lostID, int foundID) {
        this(lostID, foundID, 0);
    }

    /**
     * Creates a new instance of LostFoundMatch
     *
     * @param lostID
     *            The ID of the lost animal to match, or 0 to match all lost
     *            animals that have not been found.
     * @param foundID
     *            The ID of the found animal to match, or 0 to match all
     *            available found animals.
     * @param animalID
     *            The ID of the shelter animal to match, or 0 to match all
     *            available shelter animals (where selected)
     */
    public LostFoundMatch(int lostID, int foundID, int animalID) {
        lostAnimalID = lostID;
        foundAnimalID = foundID;
        this.animalID = animalID;

        // This is not configurable in system options as we just assume
        // yes to include checking the animal database. It's here so that
        // folks who do want to turn it off can.
        if (Configuration.getBoolean("MatchPromptForAnimalDB")) {
            // Ask if they want to include the shelter in the search as
            // long as we don't have a found animal
            if ((foundAnimalID == 0) && (animalID == 0)) {
                includeShelter = Dialog.showYesNo(Global.i18n("reports",
                            "Do_you_want_to_include_the_animal_database_in_your_search"),
                        Global.i18n("reports", "Include_Shelter_Animals"));
            }
        } else {
            if (Configuration.getBoolean("MatchIncludeShelter")) {
                includeShelter = ((foundAnimalID == 0) && (animalID == 0));
            }
        }

        // If we have a shelter animal, then we have to check the shelter
        // animals.
        if (animalID != 0) {
            includeShelter = true;
        }

        // This is not configurable in system options as date ranges shouldn't
        // be needed any more. It's here so that shelters that do desparately need
        // it can still turn it back on again.
        if (Configuration.getBoolean("MatchPromptForDateRange")) {
            // If a shelter animal has been chosen, then there is no
            // sense in requesting a date range
            if (animalID == 0) {
                // Do they want a date range?
                filterByDate = Dialog.showYesNo(Global.i18n("reports",
                            "filter_matches_to_include_animals_found_between_two_dates"),
                        Global.i18n("reports", "Filter_Matches"));

                // If so, ask them for those dates
                if (filterByDate) {
                    DateFromTo dt = new DateFromTo(DateFromTo.REPORT_LOSTFOUND,
                            this);
                    Global.mainForm.addChild(dt);
                } else {
                    // No date required - do the stuff
                    // without them.
                    this.start();
                }
            } else {
                // No date required - do the stuff
                // without them.
                this.start();
            }
        } else {
            this.start();
        }
    }

    /**
     * Callback generated when a date has been selected - just sets the dates
     * and starts the report running on a new thread
     */
    public void dateChosen(Date from, Date to) {
        this.from = from;
        this.to = to;
        this.sqlFrom = Utils.getSQLDate(from);
        this.sqlTo = Utils.getSQLDate(to);
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "Lost/Found_Animal_Match_Report_At_",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            AnimalLost al = new AnimalLost();

            // See if we are matching just one, or all animals
            if (lostAnimalID == 0) {
                // Exclude animals that have already been found
                if (filterByDate) {
                    al.openRecordset("DateFound Is Null And DateLost >= '" +
                        sqlFrom + "' AND DateLost <= '" + sqlTo + "'");
                } else {
                    al.openRecordset("DateFound Is Null");
                }
            } else {
                al.openRecordset("ID = " + lostAnimalID);
            }

            setStatusBarMax((int) al.getRecordCount());

            String contactName = "";
            String contactNumber = "";

            while (!al.getEOF()) {
                contactName = "";
                contactNumber = "";

                try {
                    contactName = al.getOwner().getOwnerName();
                    contactNumber = al.getOwner().getHomeTelephone();
                } catch (Exception e) {
                }

                // Build the animal details, but don't add them to the report
                // until we know there are some matches:
                String la = Global.i18n("reports", "lostfound_match_detail",
                        al.getID().toString(),
                        al.getAgeGroup() + " " + al.getBaseColourName() + " " +
                        LookupCache.getSexName(al.getSex()),
                        al.getSpeciesName() + "/" +
                        LookupCache.getBreedName(al.getBreedID()),
                        al.getDistFeat(), contactName, contactNumber,
                        al.getAreaLost(), al.getAreaPostcode(),
                        Utils.formatDateLong(al.getDateLost()));

                // Get potential matches
                Vector matches = al.match(includeShelter, filterByDate, from,
                        to, foundAnimalID, animalID);

                // Were there any?
                Iterator it = matches.iterator();

                if (!it.hasNext()) {
                    // There weren't - if we are dealing with a single
                    // animal scan, show it with no matches
                    if (lostAnimalID != 0) {
                        addParagraph(la);
                        addHorizontalRule();
                        addParagraph(Global.i18n("reports", "No_matches_found."));
                        addHorizontalRule();
                    }
                } else {
                    // Show the lost animal
                    addParagraph(la);
                    addHorizontalRule();

                    // Build table spec of found matches
                    tableNew();
                    tableAddRow();
                    tableAddCell(bold(Global.i18n("reports", "Reference")));
                    tableAddCell(bold(Global.i18n("reports", "Description")));
                    tableAddCell(bold(Global.i18n("reports",
                                "Distinguishing_Features")));
                    tableAddCell(bold(Global.i18n("reports", "Area_Found")));
                    tableAddCell(bold(Global.i18n("reports", "Area_Postcode")));
                    tableAddCell(bold(Global.i18n("reports", "Date_Found")));
                    tableAddCell(bold(Global.i18n("reports", "Contact")));
                    tableAddCell(bold(Global.i18n("reports", "Number")));
                    tableAddCell(bold(Global.i18n("reports", "Percentage_Match")));
                    tableFinishRow();

                    while (it.hasNext()) {
                        String[] row = (String[]) it.next();

                        tableAddRow();
                        tableAddCell(row[12]);
                        tableAddCell(row[4] + " " + row[9] + " " + row[5] +
                            " " + row[6] + "/" + row[7]);
                        tableAddCell(row[8]);
                        tableAddCell(row[2]);
                        tableAddCell(row[3]);
                        tableAddCell(row[10]);
                        tableAddCell(row[0]);
                        tableAddCell(row[1]);

                        double pctVal = Double.parseDouble(row[11]);
                        pctVal = ((pctVal / (double) AnimalLost.getMatchMax()) * (double) 100);

                        tableAddCell(Integer.toString((int) pctVal) + "%");
                    }

                    tableFinish();
                    addTable();
                    addHorizontalRule();
                }

                al.moveNext();
                incrementStatusBar();
            }
        } catch (Exception e) {
            Dialog.showError(e.getMessage());
            Global.logException(e, getClass());
        }
    }
}
