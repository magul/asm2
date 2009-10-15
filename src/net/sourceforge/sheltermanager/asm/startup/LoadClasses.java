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
package net.sourceforge.sheltermanager.asm.startup;

import java.util.*;


/**
 *
 * Responsible for loading all system classes into
 * memory.
 *
 * @author  robin
 */
public class LoadClasses {
    private Vector classes = null;

    public LoadClasses() {
        // Class list
        classes = new Vector(150);

        classes.add("net.sourceforge.sheltermanager.asm.bo.Adoption");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Animal");
        classes.add("net.sourceforge.sheltermanager.asm.bo.AnimalFound");
        classes.add("net.sourceforge.sheltermanager.asm.bo.AnimalLitter");
        classes.add("net.sourceforge.sheltermanager.asm.bo.AnimalLost");
        classes.add("net.sourceforge.sheltermanager.asm.bo.AnimalType");
        classes.add("net.sourceforge.sheltermanager.asm.bo.AnimalVaccination");
        classes.add("net.sourceforge.sheltermanager.asm.bo.AnimalWaitingList");
        classes.add("net.sourceforge.sheltermanager.asm.bo.BaseColour");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Breed");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Configuration");
        classes.add("net.sourceforge.sheltermanager.asm.bo.CustomReport");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Diary");
        classes.add("net.sourceforge.sheltermanager.asm.bo.DiaryTaskDetail");
        classes.add("net.sourceforge.sheltermanager.asm.bo.DiaryTaskHead");
        classes.add("net.sourceforge.sheltermanager.asm.bo.InternalLocation");
        classes.add("net.sourceforge.sheltermanager.asm.bo.LookupCache");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Media");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Owner");
        classes.add("net.sourceforge.sheltermanager.asm.bo.PrimaryKey");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Settings");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Species");
        classes.add("net.sourceforge.sheltermanager.asm.bo.Users");
        classes.add("net.sourceforge.sheltermanager.asm.bo.VaccinationTypes");

        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.AdoptionsPerBlock");
        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.AdoptionsPerSpecies");
        classes.add("net.sourceforge.sheltermanager.asm.charts.Chart");
        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.CommonReasonsEntry");
        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.CommonReasonsReturn");
        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.DonationsPerSpecies");
        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.MonthlyDonations");
        classes.add(
            "net.sourceforge.sheltermanager.asm.charts.WaitingListPerSpecies");

        classes.add("net.sourceforge.sheltermanager.asm.db.AutoDBUpdates");
        classes.add("net.sourceforge.sheltermanager.asm.db.DBUpdate");
        classes.add("net.sourceforge.sheltermanager.asm.db.Diagnostic");
        classes.add("net.sourceforge.sheltermanager.asm.db.ShowDBUpdate");
        classes.add("net.sourceforge.sheltermanager.asm.db.UpdateListener");

        classes.add("net.sourceforge.sheltermanager.asm.ftp.FTPClient");
        classes.add("net.sourceforge.sheltermanager.asm.ftp.FTPClientTest");
        classes.add("net.sourceforge.sheltermanager.asm.ftp.FTPControlSocket");
        classes.add("net.sourceforge.sheltermanager.asm.ftp.FTPException");
        classes.add("net.sourceforge.sheltermanager.asm.ftp.FTPTransferType");

        classes.add("net.sourceforge.sheltermanager.asm.globals.Global");

        classes.add(
            "net.sourceforge.sheltermanager.asm.mailmerge.ChipCancellation");
        classes.add(
            "net.sourceforge.sheltermanager.asm.mailmerge.ChipRegistration");
        classes.add("net.sourceforge.sheltermanager.asm.mailmerge.MailMerge");
        classes.add(
            "net.sourceforge.sheltermanager.asm.mailmerge.NoDataException");
        classes.add(
            "net.sourceforge.sheltermanager.asm.mailmerge.OffShelterVaccinations");

        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.criteria.DateFromTo");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.criteria.DiaryCriteria");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.criteria.FromToListener");

        classes.add("net.sourceforge.sheltermanager.asm.reports.AnimalFigures");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.AnimalReturnedPostSix");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.AnimalReturnedPreSix");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.CustomReportExecute");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.DiaryNotesToday");
        classes.add("net.sourceforge.sheltermanager.asm.reports.InOut");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.LongTermAnimals");
        classes.add("net.sourceforge.sheltermanager.asm.reports.LostFoundMatch");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.NeverVaccinated");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.NoMediaAttached");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.NotMicroChipped");
        classes.add("net.sourceforge.sheltermanager.asm.reports.Report");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.ReturnedAnimals");
        classes.add("net.sourceforge.sheltermanager.asm.reports.SearchResults");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.ShelterInventory");
        classes.add("net.sourceforge.sheltermanager.asm.reports.Stats");
        classes.add("net.sourceforge.sheltermanager.asm.reports.TransferIn");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.UnCombiTestedCats");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.UnderSixNotLitter");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.UnneuteredSixMonths");
        classes.add(
            "net.sourceforge.sheltermanager.asm.reports.VaccinationDiary");
        classes.add("net.sourceforge.sheltermanager.asm.reports.Vets");
        classes.add("net.sourceforge.sheltermanager.asm.reports.WaitingList");

        classes.add(
            "net.sourceforge.sheltermanager.asm.utility.DateFormatException");
        classes.add(
            "net.sourceforge.sheltermanager.asm.utility.FlexibleFocusManager");
        classes.add("net.sourceforge.sheltermanager.asm.utility.SearchListener");
        classes.add(
            "net.sourceforge.sheltermanager.asm.utility.SortableTableModel");
        classes.add("net.sourceforge.sheltermanager.asm.utility.TableMap");
        classes.add("net.sourceforge.sheltermanager.asm.utility.TableSorter");
        classes.add("net.sourceforge.sheltermanager.asm.utility.Utils");
        classes.add(
            "net.sourceforge.sheltermanager.asm.utility.WordProcessorListener");

        classes.add(
            "net.sourceforge.sheltermanager.asm.wordprocessor.AnimalDocument");
        classes.add(
            "net.sourceforge.sheltermanager.asm.wordprocessor.GenerateDocument");
        classes.add(
            "net.sourceforge.sheltermanager.asm.wordprocessor.MovementDocument");
        classes.add(
            "net.sourceforge.sheltermanager.asm.wordprocessor.OwnerDocument");

        classes.add(
            "net.sourceforge.sheltermanager.cursorengine.BOValidationException");
        classes.add(
            "net.sourceforge.sheltermanager.cursorengine.CursorEngineException");
        classes.add("net.sourceforge.sheltermanager.cursorengine.DBConnection");
        classes.add("net.sourceforge.sheltermanager.cursorengine.NormalBO");
        classes.add(
            "net.sourceforge.sheltermanager.cursorengine.SQLFieldDescriptor");
        classes.add("net.sourceforge.sheltermanager.cursorengine.SQLRecordset");
        classes.add("net.sourceforge.sheltermanager.cursorengine.SQLRowData");
        classes.add("net.sourceforge.sheltermanager.cursorengine.UserInfoBO");

        load();
    }

    public void load() {
        Iterator i = classes.iterator();
        String className = "";

        while (i.hasNext()) {
            className = (String) i.next();

            try {
                Class.forName(className);
            } catch (Exception e) {
            }
        }
    }
}
