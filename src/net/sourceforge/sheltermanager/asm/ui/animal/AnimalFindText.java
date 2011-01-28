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
package net.sourceforge.sheltermanager.asm.ui.animal;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.Diary;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Media;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.LostFoundMatch;
import net.sourceforge.sheltermanager.asm.reports.SearchResults;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryEdit;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryTaskExecute;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementEdit;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMFind;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;


/**
 * This class implements a fulltext search (web search engine style)
 * over animal data.
 *
 * @author Robin Rawson-Tetley
 */
@SuppressWarnings("serial")
public class AnimalFindText extends ASMFind {
    private UI.Button btnHotGenForm;
    private UI.Button btnHotDiaryTask;
    private UI.Button btnHotDiaryNote;
    private UI.Button btnHotLostFound;
    private UI.Button btnHotMedia;
    private UI.Button btnHotMove;
    private UI.Button btnClear;
    private UI.Button btnPrint;
    private UI.Button btnAdvanced;
    public UI.TextField txtSearch;
    SearchListener listener = null;
    StringBuffer sql = null;

    public AnimalFindText(SearchListener thelistener) {
        listener = thelistener;
        init(Global.i18n("uianimal", "Find_Animal"),
            IconManager.getIcon(IconManager.SCREEN_FINDANIMAL), "uianimal", 6,
            true, thelistener != null);
    }

    public AnimalFindText() {
        this(null);
    }

    public void dispose() {
        listener = null;
        super.dispose();
    }

    public boolean needsScroll() {
        return false;
    }

    public String getAuditInfo() {
        return null;
    }

    public void setSecurity() {
        if (!Global.currentUserObject.getSecGenerateAnimalForms()) {
            btnHotGenForm.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnHotDiaryNote.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddDiaryNote()) {
            btnHotDiaryTask.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecMatchLostAndFoundAnimals()) {
            btnHotLostFound.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimalMedia()) {
            btnHotMedia.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecAddAnimalMovements()) {
            btnHotMove.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewCustomReports()) {
            btnPrint.setEnabled(false);
        }
    }

    public boolean formClosing() {
        return false;
    }

    public Vector<Object> getTabOrder() {
        Vector<Object> ctl = new Vector<Object>();
        ctl.add(txtSearch);
        ctl.add(table);
        ctl.add(btnSearch);
        ctl.add(btnOpen);
        ctl.add(btnClear);
        ctl.add(btnPrint);
        ctl.add(btnAdvanced);

        return ctl;
    }

    public Object getDefaultFocusedComponent() {
        return txtSearch;
    }

    public void initCriteria(UI.Panel p) {
        txtSearch = (UI.TextField) UI.addComponent(p, i18n("Search"),
                UI.getTextField(null, null, null, UI.fp(this, "actionSearch")));
    }

    public void initToolbar() {
        btnClear = UI.getButton(i18n("Clear"), null, 'c',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_CLEAR),
                UI.fp(this, "actionClear"));
        addToolbarItem(btnClear, false);

        btnPrint = UI.getButton(i18n("Print"), null, 'p',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_PRINT),
                UI.fp(this, "actionPrint"));
        addToolbarItem(btnPrint, true);
        btnAdvanced = UI.getButton(i18n("Advanced"), null, 'a',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_ADVANCED),
                UI.fp(this, "actionAdvanced"));
        addToolbarItem(btnAdvanced, false);
    }

    public void initLeftbar() {
        btnHotGenForm = UI.getButton(null,
                i18n("Generate_an_animal_letter/form"), 'f',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_GENERATEDOC),
                UI.fp(this, "actionGenForm"));
        addLeftbarItem(btnHotGenForm, true);

        btnHotDiaryTask = UI.getButton(null,
                i18n("Generate_a_diary_task_for_this_animal"), 't',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_DIARYTASK),
                UI.fp(this, "actionDiaryTask"));
        addLeftbarItem(btnHotDiaryTask, true);

        btnHotDiaryNote = UI.getButton(null,
                i18n("generate_a_diary_note_for_this_animal"), 'd',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_DIARYNOTE),
                UI.fp(this, "actionDiary"));
        addLeftbarItem(btnHotDiaryNote, true);

        btnHotLostFound = UI.getButton(null,
                i18n("look_in_lost_animal_database"), 'n',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_LOSTFOUND),
                UI.fp(this, "actionMatchLost"));

        if (!Configuration.getBoolean("DisableLostAndFound")) {
            addLeftbarItem(btnHotLostFound, true);
        }

        btnHotMedia = UI.getButton(null, i18n("Add_new_media_to_this_animal"),
                'e',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_ADDMEDIA),
                UI.fp(this, "actionMedia"));
        addLeftbarItem(btnHotMedia, true);

        btnHotMove = UI.getButton(null, i18n("Move_this_animal"), 'm',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_MOVEANIMAL),
                UI.fp(this, "actionMove"));
        addLeftbarItem(btnHotMove, true);
    }

    public void actionGenForm() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);
        new AnimalDocument(animal);
    }

    public void actionDiaryTask() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        DiaryTaskExecute edt = new DiaryTaskExecute(animal, null);
        Global.mainForm.addChild(edt);
        edt = null;
        animal = null;
    }

    public void actionDiary() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        DiaryEdit ed = new DiaryEdit();

        try {
            ed.openForNew(animal.getID().intValue(), Diary.LINKTYPE_ANIMAL, null);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Global.mainForm.addChild(ed);
        ed = null;

        animal.free();
        animal = null;
    }

    public void actionMatchLost() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        try {
            new LostFoundMatch(0, 0, animal.getID().intValue());
        } catch (Exception e) {
        }

        animal.free();
        animal = null;
    }

    public void actionMedia() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        MediaAdd am = null;

        try {
            am = new MediaAdd(animal.getID().intValue(), Media.LINKTYPE_ANIMAL);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Global.mainForm.addChild(am);
        am = null;

        animal.free();
        animal = null;
    }

    public void actionMove() {
        int id = getTable().getSelectedID();

        if (id == -1) {
            return;
        }

        Animal animal = new Animal();
        animal.openRecordset("ID=" + id);

        MovementEdit em = new MovementEdit(null);

        try {
            em.openForNew(animal.getID().intValue(), 0);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        Global.mainForm.addChild(em);
        em = null;

        animal.free();
        animal = null;
    }

    public void actionPrint() {
        SortableTableModel tablemodel = (SortableTableModel) table.getModel();
        new SearchResults(tablemodel.getData(), tablemodel.getRowCount(),
            AnimalFindColumns.getColumnCount(),
            AnimalFindColumns.getColumnCount() + 1, txtSearch.getText());

        tablemodel = null;
    }

    public void actionAdvanced() {
        Global.mainForm.addChild(new AnimalFind(listener));
        dispose();
    }

    public void actionClear() {
        // Blank and reset all controls
        this.txtSearch.setText("");
    }

    public void itemSelected(int id) {
        Animal animal = LookupCache.getAnimalByID(new Integer(id));

        // If we are in selection mode, return the event
        // with the object and destroy this form
        if (selectionMode) {
            listener.animalSelected(animal);
            animal = null;
            dispose();
        } else {
            // Create a new EditAnimal screen
            AnimalEdit ea = new AnimalEdit();
            // Kick it off into edit mode, passing the animal
            ea.openForEdit(animal);
            // Attach it to the main screen
            Global.mainForm.addChild(ea);
            animal = null;
            ea = null;
        }
    }

    public void addQuery(String[] fields, String join, int priority) {
        String select = "SELECT animal.*, " + priority + " AS priority " +
            "FROM animal";

        if (sql.length() != 0) {
            sql.append("\nUNION ");
        }

        sql.append(select);
        sql.append(" ").append(join);
        sql.append(" WHERE ");

        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sql.append(" OR ");
            }

            if (Configuration.getBoolean("CaseSensitiveSearch")) {
                sql.append(fields[i] + " LIKE '%" + txtSearch.getText() + "%'");
            } else {
                sql.append("UPPER(" + fields[i] + ") LIKE '%" +
                    Utils.upper(txtSearch.getText()) + "%'");
            }
        }
    }

    /** Performs the search */
    public void runSearch() {
        // Check search term - it must be >= 2 chars and not contain
        // any SQL punctuation
        String term = txtSearch.getText();
        term = Utils.replace(term, "%", "");
        term = Utils.replace(term, "_", "");
        term = Utils.replace(term, "'", "");
        txtSearch.setText(term);

        if ((term.length() > 0) && (term.length() < 2)) {
            Dialog.showError(i18n("search_term_bad"));

            return;
        }

        SQLRecordset animal = new SQLRecordset();

        final String ADOPTION = "INNER JOIN adoption ON animal.ID = adoption.AnimalID";
        final String MEDIA = "INNER JOIN media ON media.LinkID = animal.ID ";
        final String ADOPTER = "INNER JOIN adoption ON animal.ID = adoption.AnimalID " +
            "INNER JOIN owner ON adoption.OwnerID = owner.ID";
        final String ORIGINALOWNER = "INNER JOIN owner ON animal.OriginalOwnerID = owner.ID";
        final String ADDITIONAL = "INNER JOIN additional ON animal.ID = additional.LinkID AND additional.LinkType = 0";

        sql = new StringBuffer();

        // If no term was given, do an on-shelter search
        if (term.length() == 0) {
            sql.append("SELECT animal.*, animal.ID AS priority " +
                "FROM animal " +
                "WHERE Archived = 0 ORDER BY animal.AnimalName");
        } else {
            // Build UNION query for text results
            addQuery(new String[] {
                    "animal.AnimalName", "animal.ShelterCode",
                    "animal.ShortCode", "animal.AcceptanceNumber"
                }, "", 1);
            addQuery(new String[] { "additional.Value" }, ADDITIONAL, 1);
            addQuery(new String[] { "animal.BreedName", "animal.Markings" },
                "", 2);
            addQuery(new String[] { "log.Comments" },
                "INNER JOIN log ON log.LinkID = animal.ID", 2);
            addQuery(new String[] {
                    "animal.IdentichipNumber", "animal.TattooNumber",
                    "animal.RabiesTag"
                }, "", 3);
            addQuery(new String[] {
                    "animal.IdentichipNumber", "animal.TattooNumber",
                    "animal.RabiesTag"
                }, "", 3);
            addQuery(new String[] {
                    "animal.HiddenAnimalDetails", "animal.AnimalComments",
                    "animal.ReasonNO", "animal.HealthProblems",
                    "animal.PTSReason"
                }, "", 4);
            addQuery(new String[] { "media.MediaNotes" }, MEDIA, 5);
            addQuery(new String[] {
                    "adoption.Comments", "adoption.InsuranceNumber",
                    "adoption.ReasonForReturn"
                }, ADOPTION, 6);

            addQuery(new String[] {
                    "owner.OwnerName", "owner.OwnerAddress", "owner.OwnerTown",
                    "owner.OwnerCounty", "owner.OwnerPostcode",
                    "owner.HomeTelephone", "owner.WorkTelephone",
                    "owner.MobileTelephone", "owner.EmailAddress",
                    "owner.Comments"
                }, ADOPTER, 7);
            addQuery(new String[] {
                    "owner.OwnerName", "owner.OwnerAddress", "owner.OwnerTown",
                    "owner.OwnerCounty", "owner.OwnerPostcode",
                    "owner.HomeTelephone", "owner.WorkTelephone",
                    "owner.MobileTelephone", "owner.EmailAddress",
                    "owner.Comments"
                }, ORIGINALOWNER, 7);

            addQuery(new String[] { "animaltype.AnimalType" },
                "INNER JOIN animaltype ON animal.AnimalTypeID = animaltype.ID",
                8);
            addQuery(new String[] { "species.SpeciesName" },
                "INNER JOIN species ON animal.SpeciesID = species.ID", 8);
            addQuery(new String[] { "basecolour.BaseColour" },
                "INNER JOIN basecolour ON animal.BaseColourID = basecolour.ID",
                8);
            addQuery(new String[] { "lksex.Sex" },
                "INNER JOIN lksex ON animal.Sex = lksex.ID", 8);
            addQuery(new String[] { "internallocation.LocationName" },
                "INNER JOIN internallocation ON animal.ShelterLocation = internallocation.ID",
                8);
            addQuery(new String[] { "lksize.Size" },
                "INNER JOIN lksize ON animal.Size = lksize.ID", 8);
            addQuery(new String[] { "lkcoattype.CoatType" },
                "INNER JOIN lkcoattype ON animal.CoatType = lkcoattype.ID", 8);

            // Ordering
            sql.append(" ORDER BY priority");
        }

        // Create an array of headers for the animals
        String[] columnheaders = AnimalFindColumns.getColumnLabels();

        // Search limit
        int limit = Global.getRecordSearchLimit();

        if (limit != 0) {
            sql.append(" LIMIT " + limit);
        }

        Global.logDebug("Animal search: " + sql, "FindAnimalSearch.run");

        try {
            animal.openRecordset(sql.toString(), "animal");
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }

        // Count the unique IDs
        ArrayList<Integer> uid = new ArrayList<Integer>();
        int dups = 0;

        try {
            if (!animal.getEOF()) {
                while (!animal.getEOF()) {
                    boolean alreadygot = false;

                    for (int y = 0; y < uid.size(); y++) {
                        if (uid.get(y).equals(animal.getField("ID"))) {
                            alreadygot = true;
                            dups++;

                            break;
                        }
                    }

                    if (!alreadygot) {
                        uid.add(animal.getInt("ID"));
                    }

                    animal.moveNext();
                }

                Global.logDebug("Removed " + dups +
                    " duplicate records from results",
                    "AnimalFindText.runSearch");
                animal.moveFirst();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create a monster IN clause from the animal IDs
        StringBuffer inclause = new StringBuffer();

        for (Integer i : uid) {
            if (inclause.length() != 0) {
                inclause.append(",");
            }

            inclause.append(Integer.toString(i.intValue())); // i.toString formats we don't want that
        }

        // Grab the additional fields for these animals
        SQLRecordset add = null;

        try {
            String addsql = "SELECT additionalfield.FieldName, " +
                "additional.Value, additional.LinkID FROM " +
                "additional INNER JOIN " +
                "additionalfield ON additionalfield.ID = additional.AdditionalFieldID " +
                "WHERE additional.LinkID IN (" + inclause.toString() +
                ") AND " + "additional.LinkType = 0";
            // Global.logDebug("Get additional fields: " + addsql, "AnimalFindText.runSearch");
            add = new SQLRecordset(addsql, "additional");
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        int cols = AnimalFindColumns.getColumnCount() + 3;
        String[][] datar = new String[uid.size()][cols];

        int idColumn = cols - 3;
        int rnameColumn = cols - 2;
        int dobColumn = cols - 1;

        // Initialise the progress meter
        initStatusBarMax(uid.size());

        int i = 0;

        while (!animal.getEOF()) {
            // Add this animal record to the table data if we haven't
            // already seen it's ID before
            try {
                boolean seenit = false;

                for (int z = 0; z < i; z++) {
                    if (datar[z][idColumn].equals(animal.getField("ID")
                                                            .toString())) {
                        seenit = true;

                        break;
                    }
                }

                if (seenit) {
                    animal.moveNext();

                    continue;
                }
            } catch (Exception e) {
                Global.logException(e, getClass());

                break;
            }

            try {
                for (int z = 0; z < (cols - 3); z++) {
                    datar[i][z] = AnimalFindColumns.format(AnimalFindColumns.getColumnName(
                                z), animal, add);
                }

                datar[i][idColumn] = animal.getField("ID").toString();
                datar[i][rnameColumn] = Animal.getReportAnimalName((String) animal.getField(
                            "AnimalName"),
                        (Date) animal.getField("DateOfBirth"),
                        (Date) animal.getField("DeceasedDate"),
                        (Integer) animal.getField("HasActiveReserve"),
                        (Integer) animal.getField("ActiveMovementType"));
                datar[i][dobColumn] = Utils.formatTableDate(animal.getDate(
                            "DateOfBirth"));
                i++;
            } catch (Exception e) {
                Global.logException(e, getClass());
            }

            // Next record
            try {
                animal.moveNext();
            } catch (Exception e) {
                Global.logException(e, getClass());

                break;
            }

            incrementStatusBar();
        }

        if (AnimalFindColumns.isDefaultColumns()) {
            getTable().setSortModel(new AnimalFindText.AnimalFindSortable());
        }

        setTableData(columnheaders, datar, i, cols, cols - 3);

        animal.free();
        animal = null;
        UI.cursorToPointer();
    }

    /** Allows the age column to be sorted by date of birth instead */
    private class AnimalFindSortable extends SortableTableModel {
        @Override
        public int sortByColumnCompare(int col, int idx, String[][] dat) {
            int compared;

            if (col != 6) {
                compared = dat[idx][col].compareToIgnoreCase(dat[idx + 1][col]);
            } else {
                compared = dat[idx + 1][14].compareToIgnoreCase(dat[idx][14]);
            }

            return compared;
        }
    }
}
