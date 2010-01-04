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

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.Calendar;
import java.util.Date;


/**
 * Generates a report showing animals on the shelter that match to the active
 * owner criteria sets.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public class OwnerCriteriaSearch extends Report {
    public OwnerCriteriaSearch() {
        this.start();
    }

    public String getTitle() {
        return Global.i18n("reports", "owner_criteria_search",
            Utils.getReadableTodaysDate());
    }

    public void generateReport() {
        try {
            Owner o = new Owner();
            o.openRecordset(
                "MatchActive = 1 AND (MatchExpires Is Null OR MatchExpires > '" +
                Utils.getSQLDate(new Date()) + "')");

            if (o.getEOF()) {
                addParagraph(Global.i18n("reports",
                        "There_are_no_owners_on_file_with_criteria"));

                return;
            }

            setStatusBarMax((int) o.getRecordCount());

            while (!o.getEOF()) {
                addLevelTwoHeader(o.getOwnerName() + " (" +
                    Utils.formatAddress(o.getOwnerAddress()) + ")");

                // Execute an animal search for this owner. Has to be
                // animals on shelter (not archived)
                Animal a = new Animal();
                StringBuffer crit = new StringBuffer(
                        "Archived=0 AND DeceasedDate Is Null");

                if (o.getMatchAnimalType().intValue() != 0) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    crit.append("AnimalTypeID=" + o.getMatchAnimalType());
                }

                if (o.getMatchSpecies().intValue() != 0) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    crit.append("SpeciesID=" + o.getMatchSpecies());
                }

                String breed1crit = "";
                String breed2crit = "";

                if (o.getMatchBreed().intValue() != 0) {
                    breed1crit = "(BreedID=" + o.getMatchBreed() +
                        " OR Breed2ID=" + o.getMatchBreed() + ")";
                }

                if (o.getMatchBreed2().intValue() != 0) {
                    breed2crit = "(BreedID=" + o.getMatchBreed2() +
                        " OR Breed2ID=" + o.getMatchBreed2() + ")";
                }

                if (!breed1crit.equals("") || !breed2crit.equals("")) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    if (!breed1crit.equals("") && !breed2crit.equals("")) {
                        crit.append("(" + breed1crit + " OR " + breed2crit +
                            ")");
                    } else if (!breed1crit.equals("")) {
                        crit.append(breed1crit);
                    } else if (!breed2crit.equals("")) {
                        crit.append(breed2crit);
                    }
                }

                if (o.getMatchSex().intValue() != 0) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    int i = o.getMatchSex().intValue();
                    i--;
                    crit.append("Sex=" + i);
                }

                if (o.getMatchSize().intValue() != 0) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    int i = o.getMatchSize().intValue();
                    i--;
                    crit.append("Size=" + i);
                }

                if (o.getMatchGoodWithChildren().intValue() == 1) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    crit.append("IsGoodWithChildren = 0");
                }

                if (o.getMatchGoodWithCats().intValue() == 1) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    crit.append("IsGoodWithCats = 0");
                }

                if (o.getMatchGoodWithDogs().intValue() == 1) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    crit.append("IsGoodWithDogs = 0");
                }

                if (o.getMatchHouseTrained().intValue() == 1) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    crit.append("IsHouseTrained = 0");
                }

                if ((o.getMatchAgeFrom().doubleValue() != 0) &&
                        (o.getMatchAgeTo().doubleValue() != 0)) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    try {
                        // Get the dates from and to as today
                        String yearfrom = Utils.getSQLDateOnly(Utils.subtractYears(
                                    Calendar.getInstance(),
                                    (float) o.getMatchAgeFrom().doubleValue()));
                        String yearto = Utils.getSQLDateOnly(Utils.subtractYears(
                                    Calendar.getInstance(),
                                    (float) o.getMatchAgeTo().doubleValue()));
                        crit.append("DateOfBirth BETWEEN '" + yearto +
                            "' AND '" + yearfrom + "'");
                    } catch (NumberFormatException e) {
                        // Ignore number format exceptions - just don't
                        // apply this criteria
                    }
                }

                // The comment search is a bunch of individual words, separated
                // by spaces.
                if ((o.getMatchCommentsContain() != null) &&
                        !o.getMatchCommentsContain().equals("")) {
                    if (crit.length() != 0) {
                        crit.append(" AND ");
                    }

                    String[] words = Utils.split(o.getMatchCommentsContain(),
                            " ");
                    int i = 0;

                    while (i < words.length) {
                        crit.append("AnimalComments Like '%" + words[i] +
                            "%' AND ");
                        i++;
                    }

                    // Throw away final AND
                    crit.delete(crit.length() - 5, crit.length());
                }

                a.openRecordset(crit.toString());

                if (a.getRecordCount() == 0) {
                    addParagraph(Global.i18n("reports",
                            "no_matches_found_for_this_owner"));
                } else {
                    tableNew();
                    tableAddRow();
                    tableAddCell(Global.i18n("reports", "Sheltercode"));
                    tableAddCell(Global.i18n("reports", "Name"));
                    tableAddCell(Global.i18n("reports", "Age"));
                    tableAddCell(Global.i18n("reports", "Sex"));
                    tableAddCell(Global.i18n("reports", "Size"));
                    tableAddCell(Global.i18n("reports", "Species"));
                    tableAddCell(Global.i18n("reports", "Breed"));
                    tableAddCell(Global.i18n("reports", "GoodWithCats"));
                    tableAddCell(Global.i18n("reports", "GoodWithDogs"));
                    tableAddCell(Global.i18n("reports", "GoodWithChildren"));
                    tableAddCell(Global.i18n("reports", "Housetrained"));
                    tableAddCell(Global.i18n("reports", "Comments"));
                    tableFinishRow();

                    while (!a.getEOF()) {
                        tableAddRow();

                        tableAddCell(a.getShelterCode());
                        tableAddCell(a.getReportAnimalName());
                        tableAddCell(a.getAge());
                        tableAddCell(a.getSexName());
                        tableAddCell(a.getSizeName());
                        tableAddCell(a.getSpeciesName());
                        tableAddCell(a.getBreedName());
                        tableAddCell(getTriState(a.isGoodWithCats()));
                        tableAddCell(getTriState(a.isGoodWithDogs()));
                        tableAddCell(getTriState(a.isGoodWithKids()));
                        tableAddCell(getTriState(a.isHouseTrained()));
                        tableAddCell(a.getAnimalComments());

                        tableFinishRow();
                        a.moveNext();
                    }

                    tableFinish();
                    addTable();
                }

                incrementStatusBar();
                o.moveNext();
            }
        } catch (Exception e) {
            Dialog.showError(Global.i18n("reports",
                    "An_error_occurred_generating_the_report", e.getMessage()));
            Global.logException(e, getClass());
        }
    }

    /**
     * Returns Yes, No or Unknown given a tri-state value for the animal good
     * with children, dogs, kids or housetrained
     */
    public String getTriState(Integer value) {
        if (value.intValue() == 0) {
            return Global.i18n("reports", "Yes");
        } else if (value.intValue() == 1) {
            return Global.i18n("reports", "No");
        } else if (value.intValue() == 2) {
            return Global.i18n("reports", "Unknown");
        }

        return "[Bad tristate switch: " + value + "]";
    }
}
