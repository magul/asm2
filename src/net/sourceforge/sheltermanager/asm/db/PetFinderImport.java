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
package net.sourceforge.sheltermanager.asm.db;

import net.sourceforge.sheltermanager.asm.bo.*;
import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.asm.ui.ui.*;
import net.sourceforge.sheltermanager.asm.utility.*;

import java.io.*;

import java.text.*;

import java.util.*;


public class PetFinderImport extends Thread {
    private String filename = "";
    private int atype;

    public PetFinderImport() {
        // Ask for the file
        UI.FileChooser chooser = UI.getFileChooser();
        chooser.setDialogTitle(Global.i18n("db",
                "Select_petfinder_file_to_process"));

        try {
            int returnVal = chooser.showOpenDialog(Global.mainForm);

            if (returnVal == UI.FileChooser.APPROVE_OPTION) {
                filename = chooser.getSelectedFile().getAbsolutePath();
                Global.logInfo("Processing: " + filename, "PetFinderImport");

                // Ask for the default animal type
                atype = Dialog.getAnimalType(false);

                if (atype == 0) {
                    return;
                }

                this.start();
            } else {
                return;
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public void run() {
        // Open it
        File f = new File(filename);
        FileInputStream in = null;

        try {
            in = new FileInputStream(f);
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());

            return;
        }

        // Load the contents
        String[] rows = Utils.readFile(in);

        Global.mainForm.initStatusBarMax(rows.length);
        Global.mainForm.setStatusText(Global.i18n("db", "importing_table", "."));

        for (int i = 0; i < rows.length; i++) {
            try {
                Global.logDebug("ROW: " + i, "PetFinderImport");

                // Skip blank ones
                if (rows[i].trim().equals("")) {
                    Global.logDebug("SKIP (blank)", "PetFinderImport");
                    Global.mainForm.incrementStatusBar();

                    continue;
                }

                PetFinderSQLRow r = new PetFinderSQLRow(rows[i]);

                // Create a new animal record from the row data
                Animal a = new Animal();
                a.openRecordset("ID = 0");
                a.addNew();
                a.setAnimalTypeID(new Integer(atype));
                a.setSpeciesID(new Integer(r.species));
                a.setBreedID(new Integer(r.breed));
                a.setDateOfBirth(r.dateofbirth);
                a.setDateBroughtIn(r.dateentered);
                a.setAnimalName(r.name);
                a.setSize(new Integer(r.size));
                a.setSex(new Integer(r.sex));
                a.setAnimalComments(r.notes);
                a.setNeutered(new Integer(r.neutered ? 1 : 0));

                // Generate default code for animal type
                Animal.AnimalCode ac = a.generateAnimalCode(LookupCache.getAnimalTypeName(
                            a.getAnimalTypeID()), a.getDateBroughtIn());
                a.setShelterCode(ac.code);
                a.setShortCode(ac.shortcode);

                a.save(Global.currentUserName);
                Global.logInfo("Created new record: name=" + r.name +
                    ", code=" + a.getShelterCode(), "PetFinderImport");
                Global.mainForm.incrementStatusBar();
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        Global.mainForm.resetStatusBar();
        Global.mainForm.setStatusText("");
        Dialog.showInformation(Global.i18n("db", "import_successful"),
            Global.i18n("db", "import_complete"));
    }
}


class PetFinderSQLRow {
    private final static SimpleDateFormat df = new SimpleDateFormat(
            "yyyy-MM-dd");
    public boolean neutered;
    public int species;
    public int breed;
    public Date dateofbirth;
    public Date dateentered;
    public String name;
    public int size;
    public int sex;
    public String notes;
    private int curloc = 0;
    private String row;
    final String[] pfsizes = { "XL", "L", "M", "S" };
    final String[] pfages = { "Baby", "Young", "Adult", "Senior" };
    final String[] pfsexes = { "F", "M" };

    public PetFinderSQLRow(String row) {
        this.row = row;

        // Parse the row
        String speciesname = nextTag();
        String unknown1 = nextTag();
        String breedname = nextTag();
        String agename = nextTag();
        name = nextTag();

        String pfcode = nextTag();
        String sizename = nextTag();
        String sexname = nextTag();
        notes = nextTag();

        String unknown2 = nextTag();
        String neuteredname = nextTag();
        String entereddate = nextTag();

        // Ok, update our public values
        try {
            Integer speciesInt = LookupCache.getSpeciesID(speciesname);

            if (speciesInt != null) {
                species = speciesInt.intValue();
            } else {
                species = LookupCache.getFirstID(LookupCache.getSpeciesLookup())
                                     .intValue();
            }

            // PF use multiple breeds for crosses separated by a comma - ASM
            // can't cope with that, so just use the first
            if (breedname.indexOf(",") != -1) {
                breedname = breedname.substring(0, breedname.indexOf(","));
            }

            breed = LookupCache.getBreedID(breedname).intValue();
            size = getIndex(pfsizes, sizename);

            int agecat = getIndex(pfages, agename);

            // Calculate a guessed age from the range
            // 0 - baby ( 6 months )
            // 1 - young ( 1 year )
            // 2 - adult ( 3 years )
            // 3 - senior ( 8 years )
            Calendar c = Calendar.getInstance();

            if (agecat == 0) {
                c.add(Calendar.MONTH, -6);
            } else if (agecat == 1) {
                c.add(Calendar.YEAR, -1);
            } else if (agecat == 2) {
                c.add(Calendar.YEAR, -3);
            } else if (agecat == 3) {
                c.add(Calendar.YEAR, -8);
            }

            dateofbirth = c.getTime();

            sex = getIndex(pfsexes, sexname);
            neutered = neuteredname.indexOf("altered") != -1;
            dateentered = df.parse(entereddate);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    public int getIndex(String[] set, String find) {
        for (int i = 0; i < set.length; i++)
            if (set[i].equals(find)) {
                return i;
            }

        return -1;
    }

    /**
     * Returns the next available tag (something in apostrophes). Ignores
     * escaped apostrophes with a backslash preceding them. Returns null
     * if there's no valid next tag
     */
    public String nextTag() {
        int spos = row.indexOf(",'", curloc);

        if (spos == -1) {
            return null;
        }

        int epos = -1;

        for (int i = spos + 2; i < row.length(); i++) {
            if (row.substring(i, i + 1).equals("'") &&
                    !row.substring(i - 1, i).equals("\\")) {
                epos = i;

                break;
            }
        }

        if (epos == row.length()) {
            return null;
        }

        curloc = epos + 1;

        String tag = row.substring(spos + 2, epos);
        Global.logDebug("TAG: " + tag, "nextTag");

        return tag;
    }
}
