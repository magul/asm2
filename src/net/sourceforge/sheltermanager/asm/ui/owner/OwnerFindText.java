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
package net.sourceforge.sheltermanager.asm.ui.owner;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Owner;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.mailmerge.OwnerMailMerge;
import net.sourceforge.sheltermanager.asm.reports.OwnerSearchResults;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMFind;
import net.sourceforge.sheltermanager.asm.ui.ui.DateField;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.SortableTableModel;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.utility.SearchListener;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.SQLRecordset;

import java.text.MessageFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * This class implements a fulltext search (web search engine style)
 * over owner data.
 *
 * @author Robin Rawson-Tetley
 */
public class OwnerFindText extends ASMFind {
    private UI.Button btnClear;
    private UI.Button btnPrint;
    private UI.Button btnMailMerge;
    public UI.Button btnAdvanced;
    public UI.TextField txtSearch;
    SearchListener listener = null;
    StringBuffer sql = null;

    /** Additional SQL where clause */
    String extraClause = "";

    /** If the call wants us to fire the retailerSelectedEvent */
    private boolean useRetailerSelectedEvent = false;
    private boolean closeAfterSelection = true;

    public OwnerFindText(SearchListener thelistener,
        boolean closeAfterSelection, boolean useRetailerSelectedEvent) {
        this.closeAfterSelection = closeAfterSelection;
        this.useRetailerSelectedEvent = useRetailerSelectedEvent;
        listener = thelistener;
        init(Global.i18n("uiowner", "Find_Owner"),
            IconManager.getIcon(IconManager.SCREEN_FINDOWNER), "uiowner", 6,
            false, thelistener != null);
    }

    public OwnerFindText() {
        this(null, false, false);
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
        if (!Global.currentUserObject.getSecMailMergeOwners()) {
            btnMailMerge.setEnabled(false);
        }

        if (!Global.currentUserObject.getSecViewCustomReports()) {
            btnPrint.setEnabled(false);
        }
    }

    public boolean formClosing() {
        return false;
    }

    public void setExtraClause(String clause) {
        extraClause = clause;
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
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
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_CLEAR),
                UI.fp(this, "actionClear"));
        addToolbarItem(btnClear, false);

        btnMailMerge = UI.getButton(i18n("Mail_Merge"), null, 'm',
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_MAILMERGE),
                UI.fp(this, "actionMailMerge"));
        addToolbarItem(btnMailMerge, true);

        btnPrint = UI.getButton(i18n("Print"), null, 'p',
                IconManager.getIcon(IconManager.SCREEN_FINDOWNER_PRINT),
                UI.fp(this, "actionPrint"));
        addToolbarItem(btnPrint, true);

        btnAdvanced = UI.getButton(i18n("Advanced"), null, 'a',
                IconManager.getIcon(IconManager.SCREEN_FINDANIMAL_ADVANCED),
                UI.fp(this, "actionAdvanced"));
        addToolbarItem(btnAdvanced, false);
    }

    public void initLeftbar() {
    }

    public void actionPrint() {
        new OwnerSearchResults(((SortableTableModel) getTable().getModel()).getData(),
            getTable().getModel().getRowCount());
    }

    public void actionMailMerge() {
        if (getTable().getModel() == null) {
            return;
        }

        // Pass the data on to the Mail Merge class for this job
        new OwnerMailMerge((SortableTableModel) getTable().getModel());
    }

    public void actionAdvanced() {
        Global.mainForm.addChild(new OwnerFind(listener, closeAfterSelection,
                useRetailerSelectedEvent));
        dispose();
    }

    public void actionClear() {
        // Blank and reset all controls
        this.txtSearch.setText("");
    }

    public void itemSelected(int id) {
        // Open the owner object
        Owner owner = new Owner();
        owner.openRecordset("ID = " + id);

        // If we are in selection mode, return the event
        // with the object and destroy this form if
        // the flag is set
        if (selectionMode) {
            // If we are in retailer mode, fire the
            // correct callback event
            if (useRetailerSelectedEvent) {
                listener.retailerSelected(owner);
            } else {
                listener.ownerSelected(owner);
            }

            if (closeAfterSelection) {
                dispose();
            }
        } else {
            // Open the record for editing
            OwnerEdit eo = new OwnerEdit();
            eo.openForEdit(owner);
            Global.mainForm.addChild(eo);
            eo = null;
        }
    }

    public void addQuery(String[] fields, String join, int priority) {
        String select = "SELECT owner.*, " + priority + " AS priority " +
            "FROM owner";

        if (sql.length() != 0) {
            sql.append("\nUNION ");
        }

        sql.append(select);
        sql.append(" ").append(join);
        sql.append(" WHERE ");

        if (!extraClause.equals("")) {
            sql.append(extraClause).append(" AND (");
        }

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

        if (!extraClause.equals("")) {
            sql.append(")");
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

        SQLRecordset owner = new SQLRecordset();

        final String ADOPTION = "INNER JOIN adoption ON owner.ID = adoption.OwnerID";
        final String MEDIA = "INNER JOIN media ON media.LinkID = owner.ID";
        final String ANIMAL = "INNER JOIN adoption ON owner.ID = adoption.OwnerID " +
            "INNER JOIN animal ON adoption.AnimalID = animal.ID";

        sql = new StringBuffer();

        // If no term was given, do an all owner search
        if (term.length() == 0) {
            sql.append("SELECT owner.*, owner.ID AS priority FROM owner");

            if (!extraClause.equals("")) {
                sql.append(" WHERE ").append(extraClause);
            }
        } else {
            // Build UNION query for text results
            addQuery(new String[] {
                    "owner.OwnerName", "owner.OwnerAddress", "owner.OwnerTown",
                    "owner.OwnerCounty", "owner.OwnerPostcode",
                    "owner.HomeTelephone", "owner.WorkTelephone",
                    "owner.MobileTelephone", "owner.EmailAddress"
                }, "", 1);
            addQuery(new String[] { "owner.Comments", "owner.HomeCheckAreas" },
                "", 2);
            addQuery(new String[] { "ownerdonation.Comments" },
                "INNER JOIN ownerdonation ON ownerdonation.OwnerID = owner.ID",
                2);
            addQuery(new String[] { "ownervoucher.Comments" },
                "INNER JOIN ownervoucher ON ownervoucher.OwnerID = owner.ID", 2);
            addQuery(new String[] { "log.Comments" },
                "INNER JOIN log ON log.LinkID = owner.ID", 2);
            addQuery(new String[] { "species.SpeciesName" },
                "INNER JOIN species ON owner.MatchSpecies = species.ID", 3);
            addQuery(new String[] { "breed.BreedName" },
                "INNER JOIN breed ON owner.MatchBreed = breed.ID", 3);
            addQuery(new String[] { "breed.BreedName" },
                "INNER JOIN breed ON owner.MatchBreed2 = breed.ID", 3);
            addQuery(new String[] { "animaltype.AnimalType" },
                "INNER JOIN animaltype ON owner.MatchAnimalType = animaltype.ID",
                3);
            addQuery(new String[] { "media.MediaNotes" }, MEDIA, 4);
            addQuery(new String[] {
                    "adoption.Comments", "adoption.InsuranceNumber",
                    "adoption.ReasonForReturn"
                }, ADOPTION, 5);
            addQuery(new String[] {
                    "animal.IdentichipNumber", "animal.TattooNumber",
                    "animal.RabiesTag", "animal.ShelterCode",
                    "animal.AnimalName"
                }, ANIMAL, 6);
            addQuery(new String[] {
                    "animal.HiddenAnimalDetails", "animal.AnimalComments",
                    "animal.ReasonNO", "animal.HealthProblems",
                    "animal.PTSReason"
                }, ANIMAL, 7);
        }

        // Ordering
        sql.append(" ORDER BY priority");

        // Search limit
        int limit = Global.getRecordSearchLimit();

        if (limit != 0) {
            sql.append(" LIMIT " + limit);
        }

        Global.logDebug("Owner search: " + sql, "OwnerFindText.runSearch");

        try {
            owner.openRecordset(sql.toString(), "owner");
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }

        // Count the unique IDs
        Vector uid = new Vector();
        int dups = 0;

        try {
            if (!owner.getEOF()) {
                while (!owner.getEOF()) {
                    boolean alreadygot = false;

                    for (int y = 0; y < uid.size(); y++) {
                        if (uid.get(y).equals(owner.getField("ID"))) {
                            alreadygot = true;
                            dups++;

                            break;
                        }
                    }

                    if (!alreadygot) {
                        uid.add(owner.getField("ID"));
                    }

                    owner.moveNext();
                }

                Global.logDebug("Removed " + dups +
                    " duplicate records from results", "OwnerFindText.runSearch");
                owner.moveFirst();
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        }

        // Create an array to hold the results for the table
        String[][] tabledata = new String[(int) uid.size()][13];

        // Initialise the progress meter
        initStatusBarMax((int) uid.size());

        // Create an array of headers for the details
        String[] columnheaders = {
                i18n("Name"), i18n("Surname"), i18n("Banned"),
                i18n("Homechecked"), i18n("Address"), i18n("Town"),
                i18n("County"), i18n("Postcode"), i18n("Home_Tel"),
                i18n("Work_Tel"), i18n("Mobile_Te"), i18n("email")
            };

        int i = 0;

        while (!owner.getEOF()) {
            // Add this owner record to the table data if we haven't
            // already seen its ID before
            try {
                boolean seenit = false;

                for (int z = 0; z < i; z++) {
                    if (tabledata[z][12].equals(owner.getField("ID").toString())) {
                        seenit = true;

                        break;
                    }
                }

                if (seenit) {
                    owner.moveNext();

                    continue;
                }
            } catch (Exception e) {
                Global.logException(e, getClass());

                break;
            }

            try {
                tabledata[i][0] = owner.getField("OwnerName").toString();
                tabledata[i][1] = owner.getField("OwnerSurname").toString();

                if (owner.getField("IsBanned").equals(new Integer(0))) {
                    tabledata[i][2] = i18n("No");
                } else {
                    tabledata[i][2] = i18n("Yes");
                }

                if (owner.getField("IDCheck").equals(new Integer(0))) {
                    tabledata[i][3] = i18n("No");
                } else {
                    tabledata[i][3] = i18n("Yes");
                }

                tabledata[i][4] = Utils.formatAddress(Utils.nullToEmptyString(
                            (String) owner.getField("OwnerAddress")));
                tabledata[i][5] = Utils.nullToEmptyString((String) owner.getField(
                            "OwnerTown"));
                tabledata[i][6] = Utils.nullToEmptyString((String) owner.getField(
                            "OwnerCounty"));
                tabledata[i][7] = Utils.nullToEmptyString((String) owner.getField(
                            "OwnerPostcode"));
                tabledata[i][8] = Utils.nullToEmptyString((String) owner.getField(
                            "HomeTelephone"));
                tabledata[i][9] = Utils.nullToEmptyString((String) owner.getField(
                            "WorkTelephone"));
                tabledata[i][10] = Utils.nullToEmptyString((String) owner.getField(
                            "MobileTelephone"));
                tabledata[i][11] = Utils.nullToEmptyString((String) owner.getField(
                            "EmailAddress"));
                tabledata[i][12] = owner.getField("ID").toString();
            } catch (Exception e) {
            }

            // Next record
            try {
                owner.moveNext();
                i++;
            } catch (Exception e) {
            }

            incrementStatusBar();
        }

        setTableData(columnheaders, tabledata, i, 12);

        owner.free();
        owner = null;
        tabledata = null;
        columnheaders = null;
        UI.cursorToPointer();
    }
}
