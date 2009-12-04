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
package net.sourceforge.sheltermanager.asm.ui.main;

import apple.dts.OSXAdapter;

import net.sourceforge.sheltermanager.asm.bo.Animal;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.bo.CustomReport;
import net.sourceforge.sheltermanager.asm.bo.LookupCache;
import net.sourceforge.sheltermanager.asm.bo.Settings;
import net.sourceforge.sheltermanager.asm.db.BackToProduction;
import net.sourceforge.sheltermanager.asm.db.DBUpdate;
import net.sourceforge.sheltermanager.asm.db.DatabaseCopier;
import net.sourceforge.sheltermanager.asm.db.DatabaseImporter;
import net.sourceforge.sheltermanager.asm.db.Diagnostic;
import net.sourceforge.sheltermanager.asm.db.LocateDatabase;
import net.sourceforge.sheltermanager.asm.db.PetFinderImport;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.mailmerge.AdoptedNoReturn;
import net.sourceforge.sheltermanager.asm.mailmerge.ChipCancellation;
import net.sourceforge.sheltermanager.asm.mailmerge.ChipRegistration;
import net.sourceforge.sheltermanager.asm.mailmerge.ExpiringMembers;
import net.sourceforge.sheltermanager.asm.mailmerge.Members;
import net.sourceforge.sheltermanager.asm.mailmerge.OffShelterVaccinations;
import net.sourceforge.sheltermanager.asm.reports.CustomReportExecute;
import net.sourceforge.sheltermanager.asm.reports.DiaryNotesToday;
import net.sourceforge.sheltermanager.asm.reports.LostFoundMatch;
import net.sourceforge.sheltermanager.asm.reports.MedicalDiary;
import net.sourceforge.sheltermanager.asm.reports.VaccinationDiary;
import net.sourceforge.sheltermanager.asm.reports.Vets;
import net.sourceforge.sheltermanager.asm.startup.Startup;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFind;
import net.sourceforge.sheltermanager.asm.ui.animal.AnimalFindText;
import net.sourceforge.sheltermanager.asm.ui.animal.LitterView;
import net.sourceforge.sheltermanager.asm.ui.animal.NotAdoptionView;
import net.sourceforge.sheltermanager.asm.ui.animal.VaccinationView;
import net.sourceforge.sheltermanager.asm.ui.animalname.NamesView;
import net.sourceforge.sheltermanager.asm.ui.customreport.CustomReportExport;
import net.sourceforge.sheltermanager.asm.ui.customreport.CustomReportView;
import net.sourceforge.sheltermanager.asm.ui.customreport.GetReports;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryEdit;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryTaskView;
import net.sourceforge.sheltermanager.asm.ui.diary.DiaryView;
import net.sourceforge.sheltermanager.asm.ui.diary.VetBookView;
import net.sourceforge.sheltermanager.asm.ui.internet.FTPSettings;
import net.sourceforge.sheltermanager.asm.ui.internet.InternetPublisher;
import net.sourceforge.sheltermanager.asm.ui.internet.PetFinderMapBreed;
import net.sourceforge.sheltermanager.asm.ui.internet.PetFinderMapSpecies;
import net.sourceforge.sheltermanager.asm.ui.internet.PetFinderSettings;
import net.sourceforge.sheltermanager.asm.ui.internet.Pets911Settings;
import net.sourceforge.sheltermanager.asm.ui.internet.RescueGroupsSettings;
import net.sourceforge.sheltermanager.asm.ui.internet.SaveAPetSettings;
import net.sourceforge.sheltermanager.asm.ui.localcache.CacheView;
import net.sourceforge.sheltermanager.asm.ui.login.Login;
import net.sourceforge.sheltermanager.asm.ui.lookups.BreedSpeciesMapping;
import net.sourceforge.sheltermanager.asm.ui.lookups.LookupView;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.FoundAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.FoundAnimalFind;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.LostAnimalEdit;
import net.sourceforge.sheltermanager.asm.ui.lostandfound.LostAnimalFind;
import net.sourceforge.sheltermanager.asm.ui.medical.MedicalView;
import net.sourceforge.sheltermanager.asm.ui.medical.ProfileView;
import net.sourceforge.sheltermanager.asm.ui.movement.MovementView;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerEdit;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerFind;
import net.sourceforge.sheltermanager.asm.ui.owner.OwnerFindText;
import net.sourceforge.sheltermanager.asm.ui.splash.About;
import net.sourceforge.sheltermanager.asm.ui.splash.Credits;
import net.sourceforge.sheltermanager.asm.ui.splash.Licence;
import net.sourceforge.sheltermanager.asm.ui.system.Authentication;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureAdditional;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureDatabase;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureEmail;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureInsuranceNumbers;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureLocal;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureLookups;
import net.sourceforge.sheltermanager.asm.ui.system.ConfigureReportsDefaults;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypes;
import net.sourceforge.sheltermanager.asm.ui.system.MediaFiles;
import net.sourceforge.sheltermanager.asm.ui.system.Options;
import net.sourceforge.sheltermanager.asm.ui.system.SQLInterface;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMAccelerator;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMForm;
import net.sourceforge.sheltermanager.asm.ui.ui.ASMWindow;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.HTMLViewer;
import net.sourceforge.sheltermanager.asm.ui.ui.IconManager;
import net.sourceforge.sheltermanager.asm.ui.ui.LocaleSwitcher;
import net.sourceforge.sheltermanager.asm.ui.ui.ThrobberSmall;
import net.sourceforge.sheltermanager.asm.ui.ui.UI;
import net.sourceforge.sheltermanager.asm.ui.users.UserView;
import net.sourceforge.sheltermanager.asm.ui.waitinglist.WaitingListEdit;
import net.sourceforge.sheltermanager.asm.ui.waitinglist.WaitingListView;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.io.File;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;


/**
 * This class contains all code surrounding the main MDI parent that fires off
 * all smaller windows.
 *
 * @author Robin Rawson-Tetley
 */
public class Main extends ASMWindow {
    /** HSQLDB Database checkpointer (30 seconds) */
    private final static long CHECKPOINT_TIME = 30000;

    /** Cache updater (10 minutes) */
    private final static long CACHEUPDATE_TIME = 600000;

    /** Used in progress bar calculations */
    private int progressMax = 0;
    private int progressValue = 0;

    /** Whether to ignore close windows events because we're already closing */
    private boolean dontProcessClose = false;

    /** Auto logout related stuff */
    private java.util.Timer logoutWatcher = null;
    private java.util.TimerTask logoutWatcherTask = null;
    private Calendar lastTimeAWindowWasOpened = null;

    /** Database heartbeat */
    private java.util.Timer heartbeat = null;
    private java.util.TimerTask heartbeatTask = null;

    /** HSQLDB checkpoint */
    private java.util.Timer checkpointer = null;
    private java.util.TimerTask checkpointerTask = null;

    /** Animal cache updater */
    private java.util.Timer cacheupdater = null;
    private java.util.TimerTask cacheupdaterTask = null;

    /// UI components
    private UI.Button btnAddAnimal;
    private UI.Button btnAddDiary;
    private UI.Button btnAddFoundAnimal;
    private UI.Button btnAddLostAnimal;
    private UI.Button btnAddOwner;
    private UI.Button btnFindAnimal;
    private UI.Button btnFindFoundAnimal;
    private UI.Button btnFindLostAnimal;
    private UI.Button btnFindOwner;
    private UI.Button btnFosterBook;
    private UI.Button btnHelp;
    private UI.Button btnMatchLostandFound;
    private UI.Button btnPrintDiary;
    private UI.Button btnReservations;
    private UI.Button btnRetailerBook;
    private UI.Button btnViewMyDiary;
    private UI.Button btnWaitingList;
    private UI.DesktopPane jdpDesktop;
    private UI.Label lblStatus;
    private ThrobberSmall thrThrob;
    private UI.Menu mnuDiary;
    private UI.MenuItem mnuDiaryAddNote;
    private UI.MenuItem mnuDiaryEditMedicalProfiles;
    private UI.MenuItem mnuDiaryEditTasks;
    private UI.Menu mnuDiaryMedical;
    private UI.MenuItem mnuDiaryMedicalBook;
    private UI.MenuItem mnuDiaryMedicalDiaryPrint;
    private UI.MenuItem mnuDiaryPVAllAnimals;
    private UI.MenuItem mnuDiaryPVBook;
    private UI.MenuItem mnuDiaryPVOffShelter;
    private UI.MenuItem mnuDiaryPVOnShelter;
    private UI.MenuItem mnuDiaryPrintNotes;
    private UI.Menu mnuDiaryPrintVacc;
    private UI.MenuItem mnuDiaryVetsDiary;
    private UI.MenuItem mnuDiaryVetsBook;
    private UI.MenuItem mnuDiaryViewMyNotes;
    private UI.Menu mnuFile;
    private UI.Menu mnuFileAnimal;
    private UI.MenuItem mnuFileAnimalAddAnimal;
    private UI.MenuItem mnuFileAnimalAddWLEntry;
    private UI.MenuItem mnuFileAnimalFindAnimal;
    private UI.MenuItem mnuFileAnimalFosterBook;
    private UI.MenuItem mnuFileAnimalNotAdoptionBook;
    private UI.MenuItem mnuFileAnimalLitter;
    private UI.MenuItem mnuFileAnimalNames;
    private UI.MenuItem mnuFileAnimalReservationBook;
    private UI.MenuItem mnuFileAnimalRetailerBook;
    private UI.MenuItem mnuFileAnimalWaitingList;
    private UI.MenuItem mnuFileCloseTab;
    private UI.MenuItem mnuFileSaveTab;
    private UI.MenuItem mnuFileExit;
    private UI.Menu mnuFileFoundAnimals;
    private UI.MenuItem mnuFileFoundAnimalsAddFound;
    private UI.MenuItem mnuFileFoundAnimalsFindFound;
    private UI.MenuItem mnuFileLock;
    private UI.MenuItem mnuFileLoginAgain;
    private UI.Menu mnuFileLostAndFound;
    private UI.Menu mnuFileLostAnimals;
    private UI.MenuItem mnuFileLostAnimalsAddLost;
    private UI.MenuItem mnuFileLostAnimalsFindLost;
    private UI.MenuItem mnuFileMatchLostAndFound;
    private UI.Menu mnuFileOwner;
    private UI.MenuItem mnuFileOwnerAddOwner;
    private UI.MenuItem mnuFileOwnerFindOwner;
    private UI.Menu mnuHelp;
    private UI.MenuItem mnuHelpAbout;
    private UI.MenuItem mnuHelpContents;
    private UI.MenuItem mnuHelpContentsPDF;
    private UI.MenuItem mnuHelpCredits;
    private UI.MenuItem mnuHelpCheckUpdates;
    private UI.MenuItem mnuHelpErrorLog;
    private UI.MenuItem mnuHelpAskQuestion;
    private UI.MenuItem mnuHelpReportBug;
    private UI.MenuItem mnuHelpTranslate;
    private UI.MenuItem mnuHelpDonate;
    private UI.MenuItem mnuHelpLicence;
    private UI.Menu mnuInternet;
    private UI.MenuItem mnuInternetFTPSettings;
    private UI.Menu mnuInternetPetFinder;
    private UI.MenuItem mnuInternetPetFinderBreeds;
    private UI.MenuItem mnuInternetPetFinderMapping;
    private UI.MenuItem mnuInternetPetFinderPublish;
    private UI.MenuItem mnuInternetPetFinderSettings;
    private UI.Menu mnuInternetPets911;
    private UI.MenuItem mnuInternetPets911Publish;
    private UI.MenuItem mnuInternetPets911Settings;
    private UI.Menu mnuInternetFindMeAPet;
    private UI.MenuItem mnuInternetFindMeAPetPublish;
    private UI.MenuItem mnuInternetFindMeAPetSettings;
    private UI.Menu mnuInternetRescueGroups;
    private UI.MenuItem mnuInternetRescueGroupsPublish;
    private UI.MenuItem mnuInternetRescueGroupsSettings;
    private UI.Menu mnuInternetSaveAPet;
    private UI.MenuItem mnuInternetSaveAPetPublish;
    private UI.MenuItem mnuInternetSaveAPetSettings;
    private UI.MenuItem mnuInternetPublishAvailable;
    private UI.MenuItem mnuLookupsAnimalTypes;
    private UI.MenuItem mnuLookupsBreeds;
    private UI.MenuItem mnuLookupsBreedMap;
    private UI.MenuItem mnuLookupsColour;
    private UI.MenuItem mnuLookupsDeathReasons;
    private UI.MenuItem mnuLookupsDiets;
    private UI.MenuItem mnuLookupsDonationTypes;
    private UI.MenuItem mnuLookupsEntryReasons;
    private UI.MenuItem mnuLookupsInternalLocations;
    private UI.MenuItem mnuLookupsLogTypes;
    private UI.MenuItem mnuLookupsSpecies;
    private UI.MenuItem mnuLookupsVaccinationTypes;
    private UI.MenuItem mnuLookupsVouchers;
    private UI.MenuItem mnuLookupsRemoveBreeds;
    private UI.Menu mnuMailMerge;
    private UI.Menu mnuMailMergeAdoptions;
    private UI.MenuItem mnuMailMergeAdoptedNoReturn;
    private UI.MenuItem mnuMailMergeChipCancel;
    private UI.MenuItem mnuMailMergeChipReg;
    private UI.MenuItem mnuMailMergeOffShelterVaccination;
    private UI.Menu mnuMailMergeOwner;
    private UI.MenuItem mnuMailMergeOwnerMembership;
    private UI.MenuItem mnuMailMergeOwnerMembershipExpiry;
    private UI.MenuBar mnuMenu;
    private UI.Menu mnuPreferences;
    private UI.Menu mnuReports;
    private UI.Menu mnuSystem;
    private UI.MenuItem mnuSystemAutoInsurance;
    private UI.MenuItem mnuSystemAdditionalFields;
    private UI.MenuItem mnuSystemMediaFiles;
    private UI.MenuItem mnuSystemDBDiagnostic;
    private UI.MenuItem mnuSystemDBArchive;
    private UI.MenuItem mnuSystemDBConfigure;
    private UI.MenuItem mnuSystemDBCopy;
    private UI.MenuItem mnuSystemDBImporter;
    private UI.MenuItem mnuSystemDBPFImporter;
    private UI.MenuItem mnuSystemDBSQL;
    private UI.MenuItem mnuSystemDBUpdate;
    private UI.Menu mnuSystemDatabaseTools;
    private UI.MenuItem mnuSystemEditReports;
    private UI.MenuItem mnuSystemExportCustomReports;
    private UI.MenuItem mnuSystemEmail;
    private UI.Menu mnuSystemLookups;
    private UI.MenuItem mnuSystemOptions;
    private UI.MenuItem mnuSystemAuthentication;
    private UI.MenuItem mnuSystemReports;
    private UI.MenuItem mnuSystemUsers;
    private UI.MenuItem mnuPreferencesCallGC;
    private UI.MenuItem mnuPreferencesFileTypes;
    private UI.MenuItem mnuPreferencesSwitchDatabase;
    private UI.MenuItem mnuPreferencesLocalCache;
    private UI.Menu mnuPreferencesOpen;
    private UI.MenuItem mnuPreferencesSettings;
    public UI.ProgressBar pgStatus;
    private UI.Panel pnlStatus;
    private UI.ToolBar tlbTools;
    private UI.Label lblAudit;

    /** Creates new form Main */
    public Main() {
        init("ASM", IconManager.getIcon(IconManager.SCREEN_MAIN), "uimain", true);

        // Make adjustments to better conform to the Mac OS X user interface
        conformToMacOSXInterfaceGuidelines();

        // Display the title based on who the product is
        // registered to.
        Settings settings = new Settings();
        String registeredTo = "";

        try {
            registeredTo = settings.getRegisteredTo();
        } catch (Exception e) {
            Dialog.showError(e.getMessage(), Global.i18n("uimain", "Error"));
        }

        // Read the version - if it's a dev build, output it all, 
        // otherwise just show the number
        String version = Global.productVersion;

        if (version.toLowerCase().indexOf("dev") != -1) {
            version = "[ " + version + " ]";
        } else {
            version = version.substring(0, version.indexOf(" "));
        }

        String ftitle = Global.productName + " " + version + " - " +
            registeredTo;
        setTitle(ftitle);

        // Window dimensions
        this.setSize(UI.getDimension(1024, 768));

        // Load custom report list
        refreshCustomReports();

        // Check if this is a dev release and offer the "back to production"
        // option on the database menu if it is.
        /* Discontinued due to stability of ASM database
        if (Global.productVersion.indexOf("DEV") != -1) {
            JMenuItem mnu = new JMenuItem();
            mnu.setText("Revert to v" + BackToProduction.lastProduction);
            mnu.setIcon(IconManager
                    .getIcon(IconManager.MENU_REVERTTOPRODUCTION));
            mnu.addActionListener(new swingwt.awt.event.ActionListener() {
                public void actionPerformed(swingwt.awt.event.ActionEvent evt) {
                    revertToProductionDatabase();
                }
            });
            mnuSystem.add(mnu);
        }
        */

        // Ensure that our shutdown routine runs when the VM dies
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        // If we are using automatic logout, set a timer to run every
        // 30 seconds to see if we have gone the required amount of time before
        // automatically logging the current user out - obviously, that
        // doesn't make sense in applet mode with a user
        if ((Global.autoLogout > 0) && (Startup.appletUser == null)) {
            Global.logInfo("ASM will auto-logout after: " + Global.autoLogout +
                " minutes of inactivity", "Main.Main");
            logoutWatcher = new java.util.Timer();
            lastTimeAWindowWasOpened = Calendar.getInstance(); // Start from
                                                               // now

            logoutWatcherTask = new java.util.TimerTask() {
                        public void run() {
                            Calendar timeCutoff = Calendar.getInstance();
                            timeCutoff.add(Calendar.MINUTE,
                                Global.autoLogout * -1);

                            if (lastTimeAWindowWasOpened.before(timeCutoff)) {
                                // Inactivity timeout - log the user out
                                logout();
                            }
                        }
                    };
            logoutWatcher.schedule(logoutWatcherTask, 30000, 30000);
        }

        // Do a database heartbeat every heartbeatInterval seconds
        if ((DBConnection.DBType != DBConnection.HSQLDB) &&
                (Global.heartbeatInterval != 0)) {
            Global.logInfo("ASM will make heartbeats to the database every " +
                (Global.heartbeatInterval / 1000) + " seconds.", "Main.Main");
            heartbeat = new java.util.Timer();
            heartbeatTask = new java.util.TimerTask() {
                        public void run() {
                            try {
                                Global.logDebug("Heartbeat",
                                    "Main.DatabaseHeartBeat");
                                DBConnection.executeAction(
                                    "SELECT COUNT(TableName) FROM primarykey");
                            } catch (Exception e) {
                                Global.logException(e, Main.class);
                                Global.logError("Resetting database connection - failed in heartbeat.",
                                    "Main.HeartbeatTask");
                                DBConnection.con = null;
                            }
                        }
                    };
            heartbeat.schedule(heartbeatTask, Global.heartbeatInterval,
                Global.heartbeatInterval);
        }

        // If we're using an animal cache, update that cache 
        // every 10 minutes (the animal cache ONLY includes animal
        // details and even then they're only used for veiwing
        // on the edit animal screen - it gets the screen open quickly).
        if (Global.isCacheActiveAnimals()) {
            Global.logInfo("ASM will update the animal details cache every " +
                (CACHEUPDATE_TIME / 1000) + " seconds", "Main.Main");
            cacheupdater = new java.util.Timer();
            cacheupdaterTask = new java.util.TimerTask() {
                        public void run() {
                            Global.logDebug("Update animal cache",
                                "Main.cacheupdater");
                            LookupCache.updateAnimalCache();
                        }
                    };
            cacheupdater.schedule(cacheupdaterTask, CACHEUPDATE_TIME,
                CACHEUPDATE_TIME);
        }

        // Do a database checkpoint every 30 seconds
        if (DBConnection.DBType == DBConnection.HSQLDB) {
            Global.logInfo("ASM will checkpoint the database every " +
                (CHECKPOINT_TIME / 1000) + " seconds.", "Main.Main");

            checkpointer = new java.util.Timer();
            checkpointerTask = new java.util.TimerTask() {
                        public void run() {
                            try {
                                Global.logDebug("Checkpoint",
                                    "Main.Checkpointer");
                                DBConnection.checkpoint();
                            } catch (Exception e) {
                                Global.logException(e, Main.class);
                            }
                        }
                    };
            checkpointer.schedule(checkpointerTask, CHECKPOINT_TIME,
                CHECKPOINT_TIME);
        }

        // Check for any updates from the sheltermanager website
        if (!Global.applet && Global.showUpdates) {
            new UpdateNotification().start();
        }

        // Load the start page
        if (!Configuration.getBoolean("DontShowStartupPage")) {
            addChild(new StartupPage());
        }
    }

    /**
     * Destroy any hanging references
     */
    public void dispose() {
        // Stop any timer we might have running
        if (logoutWatcher != null) {
            logoutWatcher.cancel();
            logoutWatcher = null;
            logoutWatcherTask = null;
        }

        Global.focusManager.removeComponentSet(this);
        super.dispose();
    }

    public void display() {
        if (!Startup.applet) {
            if (Global.startMaximised) {
                maximise();
            } else {
                // Center us on screen
                UI.centerWindow(this);
            }

            // Set us as the parent for all modal boxes
            Dialog.theParent = this;

            // Show
            setVisible(true);
        } else {
            // Nothing really to do for applet -layoutForm() already
            // did the work. Just output the title in the status bar
            Startup.appletHandle.showStatus(getTitle());
        }
    }

    public Object getDefaultFocusedComponent() {
        return jdpDesktop;
    }

    public Vector getTabOrder() {
        Vector ctl = new Vector();
        ctl.add(jdpDesktop);

        return ctl;
    }

    /** 'Quit' handler for OS X. */
    public void macOSXMenuQuitHandler() {
        actionFileExit();
    }

    /** 'About' handler for OS X. */
    public void macOSXMenuAboutHandler() {
        actionHelpAbout();
    }

    /** Make changes to the UI (after initComponents) to better conform to the Mac OS X interface guidelines.*/
    public void conformToMacOSXInterfaceGuidelines() {
        // Bail if we aren't on a Mac
        if (!UI.osIsMacOSX()) {
            return;
        }

        // If we're in applet mode, don't bother either
        if (Global.applet) {
            return;
        }

        try {
            // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
            // use as delegates for various com.apple.eawt.ApplicationListener methods
            // See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
            OSXAdapter.setQuitHandler(this,
                getClass()
                    .getDeclaredMethod("macOSXMenuQuitHandler", (Class[]) null));

            // Don't need the Exit command anymore - applets don't have it
            // in the first place, though
            if (!Startup.applet) {
                mnuFile.remove(mnuFile.getItemCount() - 1); // Separator
                mnuFile.remove(mnuFile.getItemCount() - 1); // "Exit"
            }

            // Set "About ..." menu item
            OSXAdapter.setAboutHandler(this,
                getClass()
                    .getDeclaredMethod("macOSXMenuAboutHandler", (Class[]) null));

            // mnuHelp.remove(mnuHelpAbout); No-op -- doesn't do anything...
            // menuHelp.remove(jSeparator20);
            mnuHelp.remove(mnuHelp.getItemCount() - 1); // "About"
            mnuHelp.remove(mnuHelp.getItemCount() - 1); // separator

            // we really want to integrate prefs as a submenu
            //            OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
            //            OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
        } catch (Exception e) {
            System.err.println("Error while loading the OSXAdapter:");
            e.printStackTrace();
        }
    }

    public void revertToProductionDatabase() {
        BackToProduction.revert();
    }

    /**
     * Reads the current user's security settings and disables things they
     * cannot do.
     */
    public void setSecurity() {
        this.mnuFileAnimalAddAnimal.setEnabled(Global.currentUserObject.getSecAddAnimal());
        this.mnuFileAnimalFindAnimal.setEnabled(Global.currentUserObject.getSecViewAnimal());
        this.mnuFileAnimalNames.setEnabled(Global.currentUserObject.getSecModifyAnimalNameDatabase());
        this.mnuFileOwnerAddOwner.setEnabled(Global.currentUserObject.getSecAddOwner());
        this.mnuFileOwnerFindOwner.setEnabled(Global.currentUserObject.getSecViewOwner());
        this.mnuSystem.setEnabled(Global.currentUserObject.getSecAccessSystemMenu());
        this.mnuReports.setEnabled(Global.currentUserObject.getSecViewCustomReports());
        this.mnuSystemAutoInsurance.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemAdditionalFields.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemOptions.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemAuthentication.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemReports.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemEmail.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemMediaFiles.setEnabled(Global.currentUserObject.getSecConfigureSystemOptions());
        this.mnuSystemUsers.setEnabled(Global.currentUserObject.getSecEditSystemUsers());
        this.mnuSystemLookups.setEnabled(Global.currentUserObject.getSecModifyLookups());
        this.mnuDiary.setEnabled(Global.currentUserObject.getSecViewDiaryNotes());
        this.mnuDiaryEditTasks.setEnabled(Global.currentUserObject.getSecEditDiaryTasks());
        this.mnuDiaryViewMyNotes.setEnabled(Global.currentUserObject.getSecEditMyDiaryNotes());
        this.mnuDiaryPrintNotes.setEnabled(Global.currentUserObject.getSecPrintDiaryNotes());
        this.mnuDiaryMedical.setEnabled(Global.currentUserObject.getSecPrintDiaryNotes());
        this.mnuDiaryMedicalDiaryPrint.setEnabled(Global.currentUserObject.getSecPrintDiaryNotes());
        this.mnuDiaryMedicalBook.setEnabled(Global.currentUserObject.getSecChangeAnimalMedical());
        this.mnuDiaryVetsDiary.setEnabled(Global.currentUserObject.getSecPrintDiaryNotes());
        this.mnuDiaryVetsBook.setEnabled(Global.currentUserObject.getSecEditAllDiaryNotes());
        this.mnuDiaryPrintVacc.setEnabled(Global.currentUserObject.getSecPrintVaccinationDiary());
        this.mnuFileLostAnimalsAddLost.setEnabled(Global.currentUserObject.getSecAddLostAnimal());
        this.mnuFileFoundAnimalsAddFound.setEnabled(Global.currentUserObject.getSecAddFoundAnimal());
        this.mnuFileLostAnimalsFindLost.setEnabled(Global.currentUserObject.getSecChangeLostAnimals());
        this.mnuFileFoundAnimalsFindFound.setEnabled(Global.currentUserObject.getSecChangeFoundAnimals());
        this.mnuFileMatchLostAndFound.setEnabled(Global.currentUserObject.getSecMatchLostAndFoundAnimals());
        this.mnuFileAnimalReservationBook.setEnabled(Global.currentUserObject.getSecViewAnimalMovements());
        this.mnuFileAnimalFosterBook.setEnabled(Global.currentUserObject.getSecViewAnimalMovements());
        this.mnuFileAnimalNotAdoptionBook.setEnabled(Global.currentUserObject.getSecViewAnimal());
        this.mnuFileAnimalRetailerBook.setEnabled(Global.currentUserObject.getSecViewAnimalMovements());
        this.mnuDiaryAddNote.setEnabled(Global.currentUserObject.getSecAddDiaryNote());
        this.mnuFileAnimalWaitingList.setEnabled(Global.currentUserObject.getSecViewWaitingList());
        this.mnuFileAnimalAddWLEntry.setEnabled(Global.currentUserObject.getSecAddWaitingList());
        this.mnuFileAnimalLitter.setEnabled(Global.currentUserObject.getSecViewLitterLog());
        this.mnuInternet.setEnabled(Global.currentUserObject.getSecUseInternetPublisher());
        this.mnuMailMergeAdoptions.setEnabled(Global.currentUserObject.getSecMailMergeAdoptions());
        this.mnuMailMergeOwner.setEnabled(Global.currentUserObject.getSecMailMergeOwners());
        this.mnuSystemDBUpdate.setEnabled(Global.currentUserObject.getSecRunDBUpdate());
        this.mnuSystemDBDiagnostic.setEnabled(Global.currentUserObject.getSecRunDBDiagnostic());
        this.mnuSystemDBConfigure.setEnabled(Global.currentUserObject.getSecUseSQLInterface());

        // Disable database configuration for sheltermanager.com databases
        if (DBConnection.url.indexOf("sheltermanager.com") != -1) {
            this.mnuSystemDBConfigure.setEnabled(false);
        }

        this.mnuSystemDBSQL.setEnabled(Global.currentUserObject.getSecUseSQLInterface());
        // this.mnuPreferencesSettings.setEnabled(Global.currentUserObject.getSecAccessSystemMenu());
        this.mnuSystemEditReports.setEnabled(Global.currentUserObject.getSecViewCustomReports());
        this.mnuSystemExportCustomReports.setEnabled(Global.currentUserObject.getSecViewCustomReports());
        this.mnuDiaryEditMedicalProfiles.setEnabled(Global.currentUserObject.getSecModifyLookups());

        // Toolbar buttons
        this.btnAddDiary.setEnabled(Global.currentUserObject.getSecAddDiaryNote());
        this.btnAddAnimal.setEnabled(Global.currentUserObject.getSecAddAnimal());
        this.btnFindAnimal.setEnabled(Global.currentUserObject.getSecViewAnimal());
        this.btnAddOwner.setEnabled(Global.currentUserObject.getSecAddOwner());
        this.btnFindOwner.setEnabled(Global.currentUserObject.getSecViewOwner());
        this.btnViewMyDiary.setEnabled(Global.currentUserObject.getSecEditMyDiaryNotes());
        this.btnPrintDiary.setEnabled(Global.currentUserObject.getSecPrintDiaryNotes());
        this.btnAddFoundAnimal.setEnabled(Global.currentUserObject.getSecAddFoundAnimal());
        this.btnAddLostAnimal.setEnabled(Global.currentUserObject.getSecAddLostAnimal());
        this.btnFindLostAnimal.setEnabled(Global.currentUserObject.getSecChangeLostAnimals());
        this.btnFindFoundAnimal.setEnabled(Global.currentUserObject.getSecChangeFoundAnimals());
        this.btnMatchLostandFound.setEnabled(Global.currentUserObject.getSecMatchLostAndFoundAnimals());
        this.btnReservations.setEnabled(Global.currentUserObject.getSecViewAnimalMovements());
        this.btnFosterBook.setEnabled(Global.currentUserObject.getSecViewAnimalMovements());
        this.btnRetailerBook.setEnabled(Global.currentUserObject.getSecViewAnimalMovements());
        this.btnWaitingList.setEnabled(Global.currentUserObject.getSecViewWaitingList());

        // Disable things we can't do in applet mode
        this.mnuPreferencesSwitchDatabase.setVisible(!Startup.applet);
        this.mnuHelpContentsPDF.setVisible(!Startup.applet);
        this.mnuHelpCheckUpdates.setVisible(!Startup.applet);
    }

    public void windowOpened() {
    }

    public boolean windowCloseAttempt() {
        promptForClose();

        // Always cancel the close as promptForClose() handles
        // closing the screen if necessary.
        return true;
    }

    /**
     * Reads the list of custom reports and loads them into the menu
     */
    public void refreshCustomReports() {
        CustomReport cr = new CustomReport();
        CustomReportMenu crm = null;
        String curcategory = "";
        HashMap menus = new HashMap();

        try {
            cr.openRecordset("ID > 0 ORDER BY Category, Title");
            mnuReports.removeAll();

            while (!cr.getEOF()) {
                // Is this a subreport? If so, don't bother
                // doing anything.
                if (!cr.isSubReport()) {
                    // Is the category blank? If so, use
                    // "Uncategorised"
                    if ((cr.getCategory() == null) ||
                            cr.getCategory().equals("")) {
                        curcategory = i18n("uncategorised");
                    } else {
                        curcategory = cr.getCategory();
                    }

                    // Do we have a menu already for this category?
                    boolean added = false;
                    UI.Menu mnu = (UI.Menu) menus.get(curcategory);

                    if (mnu != null) {
                        // Add the menu entry to this category
                        crm = new CustomReportMenu(cr.getID().toString(),
                                cr.getTitle());
                        mnu.add(crm);
                        crm = null;
                        added = true;
                    }

                    if (!added) {
                        // We didn't have a category menu, so add one for it
                        mnu = new UI.Menu();
                        mnu.setText(curcategory);
                        mnu.setIcon(IconManager.getIcon(IconManager.MENU_REPORT));
                        mnuReports.add(mnu);
                        menus.put(curcategory, mnu);

                        // Now add the item to it
                        crm = new CustomReportMenu(cr.getID().toString(),
                                cr.getTitle());
                        mnu.add(crm);
                        crm = null;
                    }
                }

                cr.moveNext();
            }

            // Add an extra menu item for get more reports
            mnuReports.add(UI.getSeparator());

            UI.MenuItem mnu = UI.getMenuItem(i18n("Get_more_reports"), 'g',
                    IconManager.getIcon(IconManager.MENU_REPORT),
                    UI.fp(this, "actionGetMoreReports"));
            mnuReports.add(mnu);

            if (!Global.currentUserObject.getSecCreateCustomReports()) {
                mnu.setEnabled(false);
            }
        } catch (Exception e) {
            Global.logException(e, getClass());
        } finally {
            cr.free();
            curcategory = null;
            menus.clear();
            menus = null;
            cr = null;
            crm = null;
        }
    }

    /**
     * Returns true if we're using operating system security
     * @return
     */
    public boolean usingOSSecurity() {
        return Configuration.getBoolean("AutoLoginOSUsers");
    }

    /**
     * Creates the menu objects
     */
    public void initMenu() {
        if (mnuMenu == null) {
            mnuMenu = UI.getMenuBar();
        }

        mnuFile = UI.getMenu(i18n("File"));

        mnuFileAnimal = UI.getMenu(i18n("Animal"), 'A',
                IconManager.getIcon(IconManager.MENU_FILEANIMAL));

        mnuFileAnimalAddAnimal = UI.getMenuItem(i18n("Add_Animal"), 'A',
                IconManager.getIcon(IconManager.MENU_FILEANIMAL),
                new ASMAccelerator("n", "ctrl", ""),
                UI.fp(this, "actionFileAnimalAddAnimal"));

        mnuFileAnimalFindAnimal = UI.getMenuItem(i18n("Find_Animal"), 'F',
                IconManager.getIcon(IconManager.MENU_FILEANIMALFINDANIMAL),
                new ASMAccelerator("f", "ctrl", ""),
                UI.fp(this, "actionFileAnimalFindAnimal"));

        mnuFileAnimalAddWLEntry = UI.getMenuItem(i18n("Add_New_Waiting_List_Entry"),
                'W',
                IconManager.getIcon(IconManager.MENU_FILEANIMALADDWLENTRY),
                new ASMAccelerator("l", "ctrl", ""),
                UI.fp(this, "actionFileAnimalAddWLEntry"));

        mnuFileAnimalWaitingList = UI.getMenuItem(i18n("Edit_Waiting_List"),
                'i',
                IconManager.getIcon(IconManager.MENU_FILEANIMALWAITINGLIST),
                new ASMAccelerator("w", "ctrl", "shift"),
                UI.fp(this, "actionFileAnimalWaitingList"));

        mnuFileAnimalReservationBook = UI.getMenuItem(i18n("Reservation_Book"),
                'b',
                IconManager.getIcon(IconManager.MENU_FILEANIMALRESERVATIONBOOK),
                new ASMAccelerator("r", "ctrl", "shift"),
                UI.fp(this, "actionFileAnimalReservationBook"));

        mnuFileAnimalFosterBook = UI.getMenuItem(i18n("Foster_Book"), 'o',
                IconManager.getIcon(IconManager.MENU_FILEANIMALFOSTERBOOK),
                new ASMAccelerator("o", "ctrl", "shift"),
                UI.fp(this, "actionFileAnimalFosterBook"));

        mnuFileAnimalRetailerBook = UI.getMenuItem(i18n("Retailer_Book"), 'e',
                IconManager.getIcon(IconManager.MENU_FILEANIMALRETAILERBOOK),
                new ASMAccelerator("e", "ctrl", "shift"),
                UI.fp(this, "actionFileAnimalRetailerBook"));

        mnuFileAnimalNotAdoptionBook = UI.getMenuItem(i18n("Not_For_Adoption_Book"),
                'n',
                IconManager.getIcon(IconManager.MENU_FILEANIMALNOTADOPTIONBOOK),
                UI.fp(this, "actionFileAnimalNotAdoptionBook"));

        mnuFileAnimalLitter = UI.getMenuItem(i18n("Litter_Logger"), 'l',
                IconManager.getIcon(IconManager.MENU_FILEANIMALLITTER),
                new ASMAccelerator("l", "ctrl", "shift"),
                UI.fp(this, "actionFileAnimalLitter"));

        mnuFileAnimalNames = UI.getMenuItem(i18n("Animal_Name_Database"), 'b',
                IconManager.getIcon(IconManager.MENU_FILEANIMALNAMES),
                UI.fp(this, "actionFileAnimalNames"));

        mnuFileOwner = UI.getMenu(i18n("Owner"), 'O',
                IconManager.getIcon(IconManager.MENU_FILEOWNER));

        mnuFileOwnerAddOwner = UI.getMenuItem(i18n("Add_Owner"), 'A',
                IconManager.getIcon(IconManager.MENU_FILEOWNERADDOWNER),
                new ASMAccelerator("n", "ctrl", "shift"),
                UI.fp(this, "actionFileOwnerAddOwner"));

        mnuFileOwnerFindOwner = UI.getMenuItem(i18n("Find_Owner"), 'F',
                IconManager.getIcon(IconManager.MENU_FILEOWNERFINDOWNER),
                new ASMAccelerator("f", "ctrl", "shift"),
                UI.fp(this, "actionFileOwnerFindOwner"));

        mnuFileLostAndFound = UI.getMenu(i18n("Lost_and_Found"), 'L',
                IconManager.getIcon(IconManager.MENU_FILELOSTANDFOUND));

        mnuFileLostAnimals = UI.getMenu(i18n("Lost_Animals"), 'L',
                IconManager.getIcon(IconManager.MENU_FILELOSTANIMALS));

        mnuFileLostAnimalsAddLost = UI.getMenuItem(i18n("Add_Lost_Animal"),
                'A',
                IconManager.getIcon(IconManager.MENU_FILELOSTANIMALSADDLOST),
                new ASMAccelerator("0", "ctrl", ""),
                UI.fp(this, "actionFileLostAnimalsAddLost"));

        mnuFileLostAnimalsFindLost = UI.getMenuItem(i18n("Find_Lost_Animal"),
                'F',
                IconManager.getIcon(IconManager.MENU_FILELOSTANIMALSFINDLOST),
                new ASMAccelerator("1", "ctrl", ""),
                UI.fp(this, "actionFileLostAnimalsFindLost"));

        mnuFileFoundAnimals = UI.getMenu(i18n("Found_Animals"), 'F',
                IconManager.getIcon(IconManager.MENU_FILEFOUNDANIMALS));

        mnuFileFoundAnimalsAddFound = UI.getMenuItem(i18n("Add_Found_Animal"),
                'A',
                IconManager.getIcon(IconManager.MENU_FILEFOUNDANIMALSADDFOUND),
                new ASMAccelerator("a", "ctrl", "shift"),
                UI.fp(this, "actionFileFoundAnimalsAddFound"));

        mnuFileFoundAnimalsFindFound = UI.getMenuItem(i18n("Find_Found_Animal"),
                'F',
                IconManager.getIcon(IconManager.MENU_FILEFOUNDANIMALSFINDFOUND),
                new ASMAccelerator("b", "ctrl", "shift"),
                UI.fp(this, "actionFileFoundAnimalsFindFound"));

        mnuFileMatchLostAndFound = UI.getMenuItem(i18n("Match_Lost_and_Found_Animals"),
                ' ',
                IconManager.getIcon(IconManager.MENU_FILEMATCHLOSTANDFOUND),
                new ASMAccelerator("2", "ctrl", ""),
                UI.fp(this, "actionFileMatchLostAndFound"));

        mnuFileLock = UI.getMenuItem(i18n("Lock_ASM"), 'k',
                IconManager.getIcon(IconManager.MENU_FILELOCK),
                UI.fp(this, "actionFileLock"));

        mnuFileLoginAgain = UI.getMenuItem(i18n("Login_Again"), ' ',
                IconManager.getIcon(IconManager.MENU_FILELOGINAGAIN),
                new ASMAccelerator("g", "ctrl", "shift"),
                UI.fp(this, "actionFileLoginAgain"));

        mnuFileCloseTab = UI.getMenuItem(i18n("Close_Active_Tab"), ' ',
                IconManager.getIcon(IconManager.MENU_FILECLOSETAB),
                new ASMAccelerator("w", "ctrl", ""),
                UI.fp(this, "actionCloseTab"));

        mnuFileSaveTab = UI.getMenuItem(i18n("Save_Active_Tab"), ' ',
                IconManager.getIcon(IconManager.MENU_FILESAVETAB),
                new ASMAccelerator("s", "ctrl", ""),
                UI.fp(this, "actionSaveTab"));

        mnuFileExit = UI.getMenuItem(i18n("Exit"), 'X',
                IconManager.getIcon(IconManager.MENU_FILEEXIT),
                UI.fp(this, "actionFileExit"));

        mnuDiary = UI.getMenu(i18n("Diary"));

        mnuDiaryPrintVacc = UI.getMenu(i18n("Vaccination_Diary"), 'V',
                IconManager.getIcon(IconManager.MENU_DIARYPRINTVACC));

        mnuDiaryPVAllAnimals = UI.getMenuItem(i18n("All_Animals"), 'A',
                IconManager.getIcon(IconManager.MENU_DIARYPVALLANIMALS),
                new ASMAccelerator("4", "ctrl", "shift"),
                UI.fp(this, "actionDiaryPVAllAnimals"));

        mnuDiaryPVOnShelter = UI.getMenuItem(i18n("Animals_On_The_Shelter"),
                'O', IconManager.getIcon(IconManager.MENU_DIARYPVALLANIMALS),
                new ASMAccelerator("5", "ctrl", "shift"),
                UI.fp(this, "actionDiaryPVOnShelter"));

        mnuDiaryPVOffShelter = UI.getMenuItem(i18n("Off_The_Shelter"), 'F',
                IconManager.getIcon(IconManager.MENU_DIARYPVOFFSHELTER),
                new ASMAccelerator("6", "ctrl", "shift"),
                UI.fp(this, "actionDiaryPVOffShelter"));

        mnuDiaryPVBook = UI.getMenuItem(i18n("Vaccination_Book"), 'b',
                IconManager.getIcon(IconManager.MENU_DIARYPVBOOK),
                new ASMAccelerator("7", "ctrl", "shift"),
                UI.fp(this, "actionDiaryPVBook"));

        mnuDiaryMedical = UI.getMenu(i18n("Medical_Diary"), 'm',
                IconManager.getIcon(IconManager.MENU_DIARYMEDICAL));

        mnuDiaryMedicalDiaryPrint = UI.getMenuItem(i18n("Print_Medical_Diary"),
                'p',
                IconManager.getIcon(IconManager.MENU_DIARYMEDICALDIARYPRINT),
                new ASMAccelerator("8", "ctrl", "shift"),
                UI.fp(this, "actionDiaryMedicalDiaryPrint"));

        mnuDiaryMedicalBook = UI.getMenuItem(i18n("Medical_Book"), 'b',
                IconManager.getIcon(IconManager.MENU_DIARYMEDICALBOOK),
                new ASMAccelerator("9", "ctrl", "shift"),
                UI.fp(this, "actionDiaryMedicalBook"));

        mnuDiaryPrintNotes = UI.getMenuItem(i18n("Print_Diary_Notes"), 'p',
                IconManager.getIcon(IconManager.MENU_DIARYPRINTNOTES),
                new ASMAccelerator("p", "ctrl", "shift"),
                UI.fp(this, "actionDiaryPrintNotes"));

        mnuDiaryVetsDiary = UI.getMenuItem(i18n("Print_Vets_Diary"), 'y',
                IconManager.getIcon(IconManager.MENU_DIARYVETSDIARY),
                UI.fp(this, "actionDiaryVetsDiary"));

        mnuDiaryVetsBook = UI.getMenuItem(i18n("Edit_Vets_Diary"), 'v',
                IconManager.getIcon(IconManager.MENU_DIARYVETSBOOK),
                UI.fp(this, "actionDiaryVetsBook"));

        mnuDiaryEditTasks = UI.getMenuItem(i18n("Edit_Diary_Tasks"), 't',
                IconManager.getIcon(IconManager.MENU_DIARYEDITTASKS),
                new ASMAccelerator("t", "ctrl", "shift"),
                UI.fp(this, "actionDiaryEditTasks"));

        mnuDiaryEditMedicalProfiles = UI.getMenuItem(i18n("Edit_Medical_Profiles"),
                'm',
                IconManager.getIcon(IconManager.MENU_DIARYEDITMEDICALPROFILES),
                UI.fp(this, "actionDiaryEditMedicalProfiles"));

        mnuDiaryAddNote = UI.getMenuItem(i18n("Add_Diary_Note"), 'n',
                IconManager.getIcon(IconManager.MENU_DIARYADDNOTE),
                new ASMAccelerator("d", "ctrl", ""),
                UI.fp(this, "actionDiaryAddNote"));

        mnuDiaryViewMyNotes = UI.getMenuItem(i18n("Edit_Diary_Notes"), 'e',
                IconManager.getIcon(IconManager.MENU_DIARYVIEWMYNOTES),
                new ASMAccelerator("m", "ctrl", ""),
                UI.fp(this, "actionDiaryViewMyNotes"));

        mnuSystem = UI.getMenu(i18n("System"));

        mnuSystemLookups = UI.getMenu(i18n("Lookups"), 'L',
                IconManager.getIcon(IconManager.MENU_SYSTEMLOOKUPS));

        mnuLookupsAnimalTypes = UI.getMenuItem(i18n("Animal_Types"), 't',
                IconManager.getIcon(IconManager.MENU_LOOKUPSANIMALTYPES),
                UI.fp(this, "actionLookupsAnimalTypes"));

        mnuLookupsBreeds = UI.getMenuItem(i18n("Breeds"), 'b',
                IconManager.getIcon(IconManager.MENU_LOOKUPSBREEDS),
                UI.fp(this, "actionLookupsBreeds"));

        mnuLookupsBreedMap = UI.getMenuItem(i18n("Breed_Mappings"), 'm',
                IconManager.getIcon(IconManager.MENU_LOOKUPSBREEDMAP),
                UI.fp(this, "actionLookupsBreedMap"));

        mnuLookupsColour = UI.getMenuItem(i18n("Colours"), 'c',
                IconManager.getIcon(IconManager.MENU_LOOKUPSCOLOUR),
                UI.fp(this, "actionLookupsColour"));

        mnuLookupsDeathReasons = UI.getMenuItem(i18n("Death_Reasons"), 'd',
                IconManager.getIcon(IconManager.MENU_LOOKUPSDEATHREASONS),
                UI.fp(this, "actionLookupsDeathReasons"));

        mnuLookupsDiets = UI.getMenuItem(i18n("Diets"), 'i',
                IconManager.getIcon(IconManager.MENU_LOOKUPSDEATHREASONS),
                UI.fp(this, "actionLookupsDiets"));

        mnuLookupsDonationTypes = UI.getMenuItem(i18n("Donation_Types"), 'y',
                IconManager.getIcon(IconManager.MENU_LOOKUPSDONATIONTYPES),
                UI.fp(this, "actionLookupsDonationTypes"));

        mnuLookupsEntryReasons = UI.getMenuItem(i18n("Entry_Reasons"), 'e',
                IconManager.getIcon(IconManager.MENU_LOOKUPSENTRYREASONS),
                UI.fp(this, "actionLookupsEntryReasons"));

        mnuLookupsInternalLocations = UI.getMenuItem(i18n("Internal_Locations"),
                'l',
                IconManager.getIcon(IconManager.MENU_LOOKUPSINTERNALLOCATIONS),
                UI.fp(this, "actionLookupsInternalLocations"));

        mnuLookupsLogTypes = UI.getMenuItem(i18n("Log_Types"), 'p',
                IconManager.getIcon(IconManager.MENU_LOOKUPSLOGTYPES),
                UI.fp(this, "actionLookupsLogTypes"));

        mnuLookupsSpecies = UI.getMenuItem(i18n("Species"), 's',
                IconManager.getIcon(IconManager.MENU_LOOKUPSSPECIES),
                UI.fp(this, "actionLookupsSpecies"));

        mnuLookupsVaccinationTypes = UI.getMenuItem(i18n("Vaccination_Types"),
                'v',
                IconManager.getIcon(IconManager.MENU_LOOKUPSVACCINATIONTYPES),
                UI.fp(this, "actionLookupsVaccinationTypes"));

        mnuLookupsVouchers = UI.getMenuItem(i18n("Vouchers"), 'u',
                IconManager.getIcon(IconManager.MENU_LOOKUPSVOUCHERS),
                UI.fp(this, "actionLookupsVouchers"));

        mnuLookupsRemoveBreeds = UI.getMenuItem(i18n("Remove_unwanted_breeds"),
                'r', IconManager.getIcon(IconManager.MENU_LOOKUPSREMOVEBREEDS),
                UI.fp(this, "actionLookupsRemoveBreeds"));

        mnuSystemAutoInsurance = UI.getMenuItem(i18n("Configure_Automatic_Insurance_Numbers"),
                'a', IconManager.getIcon(IconManager.MENU_SYSTEMAUTOINSURANCE),
                UI.fp(this, "actionSystemAutoInsurance"));

        mnuSystemAdditionalFields = UI.getMenuItem(i18n("Configure_Additional_Fields"),
                'd',
                IconManager.getIcon(IconManager.MENU_SYSTEMCONFIGUREADDITIONAL),
                UI.fp(this, "actionSystemAdditionalFields"));

        mnuSystemEditReports = UI.getMenuItem(i18n("edit_custom_reports"), 'p',
                IconManager.getIcon(IconManager.MENU_SYSTEMEDITREPORTS),
                UI.fp(this, "actionSystemEditReports"));

        mnuSystemMediaFiles = UI.getMenuItem(i18n("media_files"), 'f',
                IconManager.getIcon(IconManager.MENU_SYSTEMMEDIAFILES),
                UI.fp(this, "actionSystemMediaFiles"));

        mnuSystemExportCustomReports = UI.getMenuItem(i18n("export_custom_report_data"),
                'x',
                IconManager.getIcon(IconManager.MENU_SYSTEMEXPORTCUSTOMREPORTS),
                UI.fp(this, "actionSystemExportCustomReports"));

        mnuSystemReports = UI.getMenuItem(i18n("Configure_Defaults"), 'r',
                IconManager.getIcon(IconManager.MENU_SYSTEMREPORTS),
                UI.fp(this, "actionSystemReports"));

        mnuSystemEmail = UI.getMenuItem(i18n("configure_email"), 'm',
                IconManager.getIcon(IconManager.MENU_SYSTEMEMAIL),
                UI.fp(this, "actionSystemEmail"));

        mnuSystemDatabaseTools = UI.getMenu(i18n("Database_Tools"), ' ',
                IconManager.getIcon(IconManager.MENU_SYSTEMDATABASETOOLS));

        mnuSystemDBUpdate = UI.getMenuItem(i18n("Run_Database_Update"), 'p',
                IconManager.getIcon(IconManager.MENU_SYSTEMDBUPDATE),
                UI.fp(this, "actionSystemDBUpdate"));

        mnuSystemDBDiagnostic = UI.getMenuItem(i18n("Run_Database_Diagnostic"),
                'o', IconManager.getIcon(IconManager.MENU_SYSTEMDBDIAGNOSTIC),
                UI.fp(this, "actionSystemDBDiagnostic"));

        mnuSystemDBArchive = UI.getMenuItem(i18n("Auto_Archive_Animal_Records"),
                'a', IconManager.getIcon(IconManager.MENU_SYSTEMDBARCHIVE),
                UI.fp(this, "actionSystemDBArchive"));

        mnuSystemDBConfigure = UI.getMenuItem(i18n("Configure_Database"), 'o',
                IconManager.getIcon(IconManager.MENU_SYSTEMDBCONFIGURE),
                UI.fp(this, "actionSystemDBConfigure"));

        mnuSystemDBCopy = UI.getMenuItem(i18n("Copy_Database"), 'c',
                IconManager.getIcon(IconManager.MENU_SYSTEMDBCOPY),
                UI.fp(this, "actionSystemDBCopy"));

        mnuSystemDBImporter = UI.getMenuItem(i18n("Import_Database"), 'i',
                IconManager.getIcon(IconManager.MENU_SYSTEMDBIMPORT),
                UI.fp(this, "actionSystemDBImport"));

        mnuSystemDBPFImporter = UI.getMenuItem(i18n("Import_PetFinder"), 'p',
                IconManager.getIcon(IconManager.MENU_SYSTEMDBPFIMPORT),
                UI.fp(this, "actionSystemDBPFImport"));

        mnuSystemDBSQL = UI.getMenuItem(i18n("SQL_Interface"), 's',
                IconManager.getIcon(IconManager.MENU_SYSTEMDBSQL),
                UI.fp(this, "actionSystemDBSQL"));

        mnuSystemAuthentication = UI.getMenuItem(i18n("Authentication"), 'a',
                IconManager.getIcon(IconManager.MENU_SYSTEMAUTHENTICATION),
                UI.fp(this, "actionSystemAuthentication"));

        mnuSystemUsers = UI.getMenuItem(i18n("Edit_System_Users"), 'u',
                IconManager.getIcon(IconManager.MENU_SYSTEMUSERS),
                UI.fp(this, "actionSystemUsers"));

        mnuSystemOptions = UI.getMenuItem(i18n("Options"), 'o',
                IconManager.getIcon(IconManager.MENU_SYSTEMOPTIONS),
                UI.fp(this, "actionSystemOptions"));

        mnuReports = UI.getMenu(i18n("Reports"));

        mnuInternet = UI.getMenu(i18n("Internet"));

        mnuInternetPublishAvailable = UI.getMenuItem(i18n("Publish_Animals_Available_for_Adoption"),
                'a',
                IconManager.getIcon(IconManager.MENU_INTERNETPUBLISHAVAILABLE),
                UI.fp(this, "actionInternetPublishAvailable"));

        mnuInternetFTPSettings = UI.getMenuItem(i18n("FTP_Settings"), 'f',
                IconManager.getIcon(IconManager.MENU_INTERNETFTPSETTINGS),
                UI.fp(this, "actionInternetFTPSettings"));

        mnuInternetPetFinder = UI.getMenu(i18n("publish_available_animals_to_petfinder"),
                ' ', IconManager.getIcon(IconManager.MENU_INTERNETPETFINDER));

        mnuInternetPetFinderPublish = UI.getMenuItem(i18n("Publish_animals_available_for_adoption_to_PetFinder.org"),
                'p',
                IconManager.getIcon(IconManager.MENU_INTERNETPETFINDERPUBLISH),
                UI.fp(this, "actionInternetPetFinderPublish"));

        mnuInternetPetFinderSettings = UI.getMenuItem(i18n("petfinder.org_settings"),
                't',
                IconManager.getIcon(IconManager.MENU_INTERNETPETFINDERSETTINGS),
                UI.fp(this, "actionInternetPetFinderSettings"));

        mnuInternetPetFinderMapping = UI.getMenuItem(i18n("PetFinder_Species_Mappings"),
                's',
                IconManager.getIcon(IconManager.MENU_INTERNETPETFINDERMAPPING),
                UI.fp(this, "actionInternetPetFinderMapping"));

        mnuInternetPetFinderBreeds = UI.getMenuItem(i18n("petfinder_breed_mapping"),
                'b',
                IconManager.getIcon(IconManager.MENU_INTERNETPETFINDERMAPPING),
                UI.fp(this, "actionInternetPetFinderBreeds"));

        mnuInternetRescueGroups = UI.getMenu(i18n("Publish_adoptable_animals_to_RescueGroups.org"),
                ' ', IconManager.getIcon(IconManager.MENU_INTERNETRESCUEGROUPS));

        mnuInternetRescueGroupsPublish = UI.getMenuItem(i18n("Publish_Animals_Available_For_Adoption_To_RescueGroups.org"),
                'p',
                IconManager.getIcon(
                    IconManager.MENU_INTERNETRESCUEGROUPSPUBLISH),
                UI.fp(this, "actionInternetRescueGroupsPublish"));

        mnuInternetRescueGroupsSettings = UI.getMenuItem(i18n("RescueGroups.org_Settings"),
                's',
                IconManager.getIcon(
                    IconManager.MENU_INTERNETRESCUEGROUPSSETTINGS),
                UI.fp(this, "actionInternetRescueGroupsSettings"));

        mnuInternetSaveAPet = UI.getMenu(i18n("Publish_adoptable_animals_to_SaveAPet.com"),
                ' ', IconManager.getIcon(IconManager.MENU_INTERNETSAVEAPET));

        mnuInternetSaveAPetPublish = UI.getMenuItem(i18n("Publish_Animals_Available_For_Adoption_To_SaveAPet.com"),
                'p',
                IconManager.getIcon(IconManager.MENU_INTERNETSAVEAPETPUBLISH),
                UI.fp(this, "actionInternetSaveAPetPublish"));

        mnuInternetSaveAPetSettings = UI.getMenuItem(i18n("SaveAPet.com_Settings"),
                's',
                IconManager.getIcon(IconManager.MENU_INTERNETSAVEAPETSETTINGS),
                UI.fp(this, "actionInternetSaveAPetSettings"));

        mnuInternetPets911 = UI.getMenu(i18n("Publish_adoptable_animals_to_pets911.com"),
                ' ', IconManager.getIcon(IconManager.MENU_INTERNETPETS911));

        mnuInternetPets911Publish = UI.getMenuItem(i18n("Publish_Animals_Available_For_Adoption_To_Pets911.com"),
                'p',
                IconManager.getIcon(IconManager.MENU_INTERNETPETS911PUBLISH),
                UI.fp(this, "actionInternetPets911Publish"));

        mnuInternetPets911Settings = UI.getMenuItem(i18n("Pets911.com_Settings"),
                's',
                IconManager.getIcon(IconManager.MENU_INTERNETPETS911SETTINGS),
                UI.fp(this, "actionInternetPets911Settings"));

        mnuMailMerge = UI.getMenu(i18n("Mail_Merge"));

        mnuMailMergeOwner = UI.getMenu(i18n("Owner_Merge"), 'o',
                IconManager.getIcon(IconManager.MENU_MAILMERGEOWNER));

        mnuMailMergeOwnerMembership = UI.getMenuItem(i18n("All_Members"), 'a',
                IconManager.getIcon(IconManager.MENU_MAILMERGEOWNERMEMBERSHIP),
                UI.fp(this, "actionMailMergeOwnerMembership"));

        mnuMailMergeOwnerMembershipExpiry = UI.getMenuItem(i18n("Expiring_Members"),
                'e',
                IconManager.getIcon(
                    IconManager.MENU_MAILMERGEOWNERMEMBERSHIPEXPIRY),
                UI.fp(this, "actionMailMergeOwnerMembershipExpiry"));

        mnuMailMergeAdoptions = UI.getMenu(i18n("Adoption_Merge"), 'a',
                IconManager.getIcon(IconManager.MENU_MAILMERGEADOPTIONS));

        mnuMailMergeOffShelterVaccination = UI.getMenuItem(i18n("Off_Shelter_Vaccinations"),
                'v',
                IconManager.getIcon(
                    IconManager.MENU_MAILMERGEOFFSHELTERVACCINATIONS),
                UI.fp(this, "actionMailMergeOffShelterVaccination"));

        mnuMailMergeChipReg = UI.getMenuItem(i18n("Microchip_Registration"),
                'g', IconManager.getIcon(IconManager.MENU_MAILMERGECHIPREG),
                UI.fp(this, "actionMailMergeChipReg"));

        mnuMailMergeChipCancel = UI.getMenuItem(i18n("Microchip_Cancellations"),
                'c', IconManager.getIcon(IconManager.MENU_MAILMERGECHIPCANCEL),
                UI.fp(this, "actionMailMergeChipCancel"));

        mnuMailMergeAdoptedNoReturn = UI.getMenuItem(i18n("Non-Returned_Adoptions"),
                'n',
                IconManager.getIcon(IconManager.MENU_MAILMERGEADOPTEDNORETURN),
                UI.fp(this, "actionMailMergeAdoptedNoReturn"));

        mnuPreferences = UI.getMenu(i18n("Preferences"));

        mnuPreferencesLocalCache = UI.getMenuItem(i18n("View_Local_Cache"),
                'c',
                IconManager.getIcon(IconManager.MENU_PREFERENCESLOCALCACHE),
                new ASMAccelerator("c", "ctrl", "shift"),
                UI.fp(this, "actionPreferencesLocalCache"));

        mnuPreferencesCallGC = UI.getMenuItem(i18n("collect_garbage_now"), 'g',
                IconManager.getIcon(IconManager.MENU_PREFERENCESCALLGC),
                new ASMAccelerator("\\", "ctrl", "shift"),
                UI.fp(this, "actionPreferencesCallGC"));

        mnuPreferencesFileTypes = UI.getMenuItem(i18n("ConfigureFiletypes"),
                't',
                IconManager.getIcon(IconManager.MENU_PREFERENCESFILETYPES),
                UI.fp(this, "actionPreferencesFileTypes"));

        mnuPreferencesSwitchDatabase = UI.getMenuItem(i18n("switch_database"),
                'd',
                IconManager.getIcon(IconManager.MENU_PREFERENCESSWITCHDATABASE),
                UI.fp(this, "actionPreferencesSwitchDatabase"));

        mnuPreferencesSettings = UI.getMenuItem(i18n("Settings"), 's',
                IconManager.getIcon(IconManager.MENU_PREFERENCESSETTINGS),
                UI.fp(this, "actionPreferencesSettings"));

        mnuHelp = UI.getMenu(i18n("Help"));

        mnuHelpContents = UI.getMenuItem(i18n("Contents"), 'c',
                IconManager.getIcon(IconManager.MENU_HELPCONTENTS),
                new ASMAccelerator("f1", "", ""),
                UI.fp(this, "actionHelpContents"));

        mnuHelpContentsPDF = UI.getMenuItem(i18n("Contents_PDF"), 'p',
                IconManager.getIcon(IconManager.MENU_HELPCONTENTSPDF),
                UI.fp(this, "actionHelpContentsPDF"));

        mnuHelpLicence = UI.getMenuItem(i18n("Licence"), 'l',
                IconManager.getIcon(IconManager.MENU_HELPLICENCE),
                UI.fp(this, "actionHelpLicence"));

        mnuHelpCredits = UI.getMenuItem(i18n("Credits"), 'r',
                IconManager.getIcon(IconManager.MENU_HELPCREDITS),
                UI.fp(this, "actionHelpCredits"));

        mnuHelpCheckUpdates = UI.getMenuItem(i18n("Check_For_Updates"), 'u',
                IconManager.getIcon(IconManager.MENU_HELPCHECKUPDATES),
                UI.fp(this, "actionHelpCheckUpdates"));

        mnuHelpErrorLog = UI.getMenuItem(i18n("Error_Log"), 'o',
                IconManager.getIcon(IconManager.MENU_HELPERRORLOG),
                UI.fp(this, "actionHelpErrorLog"));

        mnuHelpDonate = UI.getMenuItem(i18n("Donate_to_ASM!"), 'd',
                IconManager.getIcon(IconManager.MENU_HELPDONATE),
                UI.fp(this, "actionHelpDonate"));

        mnuHelpAskQuestion = UI.getMenuItem(i18n("Ask_a_question"), 'q',
                IconManager.getIcon(IconManager.MENU_HELPASKQUESTION),
                UI.fp(this, "actionHelpAskQuestion"));

        mnuHelpReportBug = UI.getMenuItem(i18n("Report_a_bug"), 'b',
                IconManager.getIcon(IconManager.MENU_HELPREPORTBUG),
                UI.fp(this, "actionHelpReportBug"));

        mnuHelpTranslate = UI.getMenuItem(i18n("Translate_ASM"), 't',
                IconManager.getIcon(IconManager.MENU_HELPTRANSLATE),
                UI.fp(this, "actionHelpTranslate"));

        mnuHelpAbout = UI.getMenuItem(i18n("About..."), 'a',
                IconManager.getIcon(IconManager.MENU_HELPABOUT),
                UI.fp(this, "actionHelpAbout"));
    }

    /**
     * Adds the menu items to make the menu tree
     */
    public void initMenuTree() {
        mnuFileAnimal.add(mnuFileAnimalAddAnimal);
        mnuFileAnimal.add(mnuFileAnimalFindAnimal);
        mnuFileAnimal.add(UI.getSeparator());
        mnuFileAnimal.add(mnuFileAnimalAddWLEntry);
        mnuFileAnimal.add(mnuFileAnimalWaitingList);
        mnuFileAnimal.add(UI.getSeparator());
        mnuFileAnimal.add(mnuFileAnimalReservationBook);
        mnuFileAnimal.add(mnuFileAnimalFosterBook);

        // Disable retailer functionality if option set
        if (!Configuration.getBoolean("DisableRetailer")) {
            mnuFileAnimal.add(mnuFileAnimalRetailerBook);
        }

        mnuFileAnimal.add(mnuFileAnimalNotAdoptionBook);
        mnuFileAnimal.add(UI.getSeparator());
        mnuFileAnimal.add(mnuFileAnimalLitter);
        mnuFileAnimal.add(mnuFileAnimalNames);
        mnuFile.add(mnuFileAnimal);
        mnuFileOwner.add(mnuFileOwnerAddOwner);
        mnuFileOwner.add(mnuFileOwnerFindOwner);
        mnuFile.add(mnuFileOwner);
        mnuFileLostAnimals.add(mnuFileLostAnimalsAddLost);
        mnuFileLostAnimals.add(mnuFileLostAnimalsFindLost);
        mnuFileLostAndFound.add(mnuFileLostAnimals);
        mnuFileFoundAnimals.add(mnuFileFoundAnimalsAddFound);
        mnuFileFoundAnimals.add(mnuFileFoundAnimalsFindFound);
        mnuFileLostAndFound.add(mnuFileFoundAnimals);
        mnuFileLostAndFound.add(UI.getSeparator());
        mnuFileLostAndFound.add(mnuFileMatchLostAndFound);
        mnuFile.add(mnuFileLostAndFound);
        mnuFile.add(UI.getSeparator());

        if (!usingOSSecurity() && !Startup.applet) {
            // If we're using OS security, disable the lock and login again options by
            // not adding them to the menu. These options also shouldn't be available
            // for applet mode as computers should be the users personal/internet ones
            // so switching users not allowed
            mnuFile.add(mnuFileLock);
            mnuFile.add(mnuFileLoginAgain);
            mnuFile.add(UI.getSeparator());
        }

        mnuFile.add(mnuFileSaveTab);
        mnuFile.add(mnuFileCloseTab);

        // Closing the browser window exits an applet, no need
        // for the exit option
        if (!Startup.applet) {
            mnuFile.add(UI.getSeparator());
            mnuFile.add(mnuFileExit);
        }

        mnuMenu.add(mnuFile);

        mnuDiaryPrintVacc.add(mnuDiaryPVAllAnimals);
        mnuDiaryPrintVacc.add(mnuDiaryPVOnShelter);
        mnuDiaryPrintVacc.add(mnuDiaryPVOffShelter);
        mnuDiaryPrintVacc.add(UI.getSeparator());
        mnuDiaryPrintVacc.add(mnuDiaryPVBook);
        mnuDiary.add(mnuDiaryPrintVacc);
        mnuDiaryMedical.add(mnuDiaryMedicalDiaryPrint);
        mnuDiaryMedical.add(UI.getSeparator());
        mnuDiaryMedical.add(mnuDiaryMedicalBook);
        mnuDiary.add(mnuDiaryMedical);
        mnuDiary.add(UI.getSeparator());
        mnuDiary.add(mnuDiaryPrintNotes);
        mnuDiary.add(mnuDiaryVetsDiary);
        mnuDiary.add(mnuDiaryVetsBook);
        mnuDiary.add(UI.getSeparator());
        mnuDiary.add(mnuDiaryEditTasks);
        mnuDiary.add(mnuDiaryEditMedicalProfiles);
        mnuDiary.add(UI.getSeparator());
        mnuDiary.add(mnuDiaryAddNote);
        mnuDiary.add(mnuDiaryViewMyNotes);
        mnuMenu.add(mnuDiary);

        mnuSystemLookups.add(mnuLookupsAnimalTypes);
        mnuSystemLookups.add(mnuLookupsBreeds);
        mnuSystemLookups.add(mnuLookupsBreedMap);
        mnuSystemLookups.add(mnuLookupsColour);
        mnuSystemLookups.add(mnuLookupsDeathReasons);
        mnuSystemLookups.add(mnuLookupsDiets);
        mnuSystemLookups.add(mnuLookupsDonationTypes);
        mnuSystemLookups.add(mnuLookupsEntryReasons);
        mnuSystemLookups.add(mnuLookupsInternalLocations);
        mnuSystemLookups.add(mnuLookupsLogTypes);
        mnuSystemLookups.add(mnuLookupsSpecies);
        mnuSystemLookups.add(mnuLookupsVaccinationTypes);
        mnuSystemLookups.add(mnuLookupsVouchers);
        mnuSystemLookups.add(UI.getSeparator());
        mnuSystemLookups.add(mnuLookupsRemoveBreeds);
        mnuSystem.add(mnuSystemLookups);
        mnuSystem.add(UI.getSeparator());
        mnuSystem.add(mnuSystemAutoInsurance);
        mnuSystem.add(mnuSystemAdditionalFields);
        mnuSystem.add(mnuSystemEditReports);
        mnuSystem.add(mnuSystemMediaFiles);
        mnuSystem.add(mnuSystemExportCustomReports);
        mnuSystem.add(mnuSystemReports);
        mnuSystem.add(mnuSystemEmail);
        mnuSystem.add(UI.getSeparator());
        mnuSystemDatabaseTools.add(mnuSystemDBUpdate);
        mnuSystemDatabaseTools.add(mnuSystemDBDiagnostic);
        mnuSystemDatabaseTools.add(mnuSystemDBArchive);
        mnuSystemDatabaseTools.add(mnuSystemDBPFImporter);

        // Debug mode only
        if (Global.showDebug) {
            mnuSystemDatabaseTools.add(mnuSystemDBCopy);
        }

        // Applets can't import
        if (!Global.applet) {
            mnuSystemDatabaseTools.add(mnuSystemDBImporter);
        }

        mnuSystemDatabaseTools.add(UI.getSeparator());
        mnuSystemDatabaseTools.add(mnuSystemDBConfigure);
        mnuSystemDatabaseTools.add(mnuSystemDBSQL);
        mnuSystem.add(mnuSystemDatabaseTools);
        mnuSystem.add(UI.getSeparator());

        // Authentication doesn't make sense with an applet
        // user supplied, since auth is taken care of before
        // applet page is loaded
        if (Startup.appletUser == null) {
            mnuSystem.add(mnuSystemAuthentication);
        }

        mnuSystem.add(mnuSystemUsers);
        mnuSystem.add(mnuSystemOptions);
        mnuMenu.add(mnuSystem);

        mnuMenu.add(mnuReports);

        mnuInternet.add(mnuInternetPublishAvailable);
        mnuInternet.add(mnuInternetFTPSettings);

        // US-specific animal publishing services
        // ===============================================================
        if (Locale.getDefault().getCountry().equals("US")) {
            mnuInternet.add(UI.getSeparator());
            mnuInternetPetFinder.add(mnuInternetPetFinderPublish);
            mnuInternetPetFinder.add(UI.getSeparator());
            mnuInternetPetFinder.add(mnuInternetPetFinderSettings);
            mnuInternetPetFinder.add(mnuInternetPetFinderMapping);
            mnuInternetPetFinder.add(mnuInternetPetFinderBreeds);
            mnuInternet.add(mnuInternetPetFinder);
            mnuInternetRescueGroups.add(mnuInternetRescueGroupsPublish);
            mnuInternetRescueGroups.add(UI.getSeparator());
            mnuInternetRescueGroups.add(mnuInternetRescueGroupsSettings);
            mnuInternet.add(mnuInternetRescueGroups);
            mnuInternetSaveAPet.add(mnuInternetSaveAPetPublish);
            mnuInternetSaveAPet.add(UI.getSeparator());
            mnuInternetSaveAPet.add(mnuInternetSaveAPetSettings);
            mnuInternet.add(mnuInternetSaveAPet);
            mnuInternetPets911.add(mnuInternetPets911Publish);
            mnuInternetPets911.add(UI.getSeparator());
            mnuInternetPets911.add(mnuInternetPets911Settings);
            mnuInternet.add(mnuInternetPets911);
        }

        mnuMenu.add(mnuInternet);

        mnuMailMergeOwner.add(mnuMailMergeOwnerMembershipExpiry);
        mnuMailMergeOwner.add(mnuMailMergeOwnerMembership);
        mnuMailMergeAdoptions.add(mnuMailMergeOffShelterVaccination);
        mnuMailMergeAdoptions.add(mnuMailMergeChipReg);
        mnuMailMergeAdoptions.add(mnuMailMergeChipCancel);
        mnuMailMergeAdoptions.add(mnuMailMergeAdoptedNoReturn);
        mnuMailMerge.add(mnuMailMergeOwner);
        mnuMailMerge.add(mnuMailMergeAdoptions);
        mnuMenu.add(mnuMailMerge);

        mnuPreferences.add(mnuPreferencesLocalCache);
        mnuPreferences.add(UI.getSeparator());

        // Only enable garbage collection for debug mode
        if (Global.showDebug) {
            mnuPreferences.add(mnuPreferencesCallGC);
        }

        // Only add the file type menu option if we were forced to use internal
        // shellexecute, or we're not a mac, or os integration isn't available
        if (System.getProperty("asm.shellexecute", "guess").equals("internal")) {
            mnuPreferences.add(mnuPreferencesFileTypes);
        } else if (!UI.osIsMacOSX() && !UI.osShellExecuteAvailable()) {
            mnuPreferences.add(mnuPreferencesFileTypes);
        }

        mnuPreferences.add(mnuPreferencesSwitchDatabase);
        mnuPreferences.add(mnuPreferencesSettings);
        mnuMenu.add(mnuPreferences);

        mnuHelp.add(mnuHelpContents);
        mnuHelp.add(mnuHelpContentsPDF);
        mnuHelp.add(UI.getSeparator());
        mnuHelp.add(mnuHelpLicence);
        mnuHelp.add(mnuHelpCredits);
        mnuHelp.add(UI.getSeparator());
        mnuHelp.add(mnuHelpAskQuestion);
        mnuHelp.add(mnuHelpReportBug);
        mnuHelp.add(mnuHelpTranslate);
        mnuHelp.add(mnuHelpDonate);
        mnuHelp.add(UI.getSeparator());
        mnuHelp.add(mnuHelpErrorLog);
        mnuHelp.add(mnuHelpCheckUpdates);
        mnuHelp.add(UI.getSeparator());
        mnuHelp.add(mnuHelpAbout);
        mnuMenu.add(mnuHelp);
    }

    public void initComponents() {
        initMenu();
        initMenuTree();
        initToolbar();
        initDesktopAndStatus();
        layoutForm();
    }

    public void reloadToolsAndMenu() {
        mnuMenu.removeAll();
        tlbTools.removeAll();
        initMenu();
        initMenuTree();
        initToolbar();
        conformToMacOSXInterfaceGuidelines();
        refreshCustomReports();
    }

    public void layoutForm() {
        if (!Startup.applet) {
            add(tlbTools, UI.BorderLayout.NORTH);
            add(jdpDesktop, UI.BorderLayout.CENTER);
            add(pnlStatus, UI.BorderLayout.SOUTH);
            setJMenuBar(mnuMenu);
        } else {
            Startup.appletHandle.loadMain(tlbTools, jdpDesktop, pnlStatus,
                mnuMenu);
        }
    }

    /**
     * Does the main desktop pane and status bar at the bottom
     */
    public void initDesktopAndStatus() {
        jdpDesktop = UI.getDesktopPane();
        lblStatus = UI.getLabel();
        pgStatus = UI.getProgressBar();
        thrThrob = new ThrobberSmall();
        thrThrob.setVisible(false);

        pnlStatus = UI.getPanel(UI.getBorderLayout(), true);

        // Sort out the status bar
        UI.Panel pnlStatusLeft = UI.getPanel(UI.getFlowLayout());
        pnlStatusLeft.add(lblStatus);
        pnlStatus.add(pnlStatusLeft, UI.BorderLayout.CENTER);
        pnlStatus.add(thrThrob, UI.BorderLayout.WEST);

        UI.Panel pnlStatusRight = UI.getPanel(UI.getFlowLayout());
        pnlStatusRight.add(pgStatus);

        UI.Label lblDB = UI.getLabel(IconManager.getIcon(
                    IconManager.SCREEN_MAIN_DB), DBConnection.getDBInfo());
        UI.Label lblUser = UI.getLabel(IconManager.getIcon(
                    IconManager.SCREEN_MAIN_USER), Global.currentUserName);
        lblAudit = UI.getLabel(IconManager.getIcon(
                    IconManager.SCREEN_MAIN_AUDIT), "");

        pnlStatusRight.add(lblAudit);
        pnlStatusRight.add(lblDB);
        pnlStatusRight.add(lblUser);
        pnlStatusRight.add(new LocaleSwitcher());
        pnlStatus.add(pnlStatusRight, UI.BorderLayout.EAST);
    }

    /**
     * Draws the toolbar
     */
    public void initToolbar() {
        if (tlbTools == null) {
            tlbTools = UI.getToolBar();
            tlbTools.setPlatformToolbar(true);
            tlbTools.setRollover(true);
        }

        btnAddAnimal = UI.getButton(null, i18n("Add_a_new_animal"),
                IconManager.getIcon(IconManager.BUTTON_ADDANIMAL),
                UI.fp(this, "actionFileAnimalAddAnimal"));

        tlbTools.add(btnAddAnimal);

        btnFindAnimal = UI.getButton(null, i18n("Find_an_Animal"),
                IconManager.getIcon(IconManager.BUTTON_FINDANIMAL),
                UI.fp(this, "actionFileAnimalFindAnimal"));

        tlbTools.add(btnFindAnimal);

        btnAddOwner = UI.getButton(null, i18n("Add_an_Owner"),
                IconManager.getIcon(IconManager.BUTTON_ADDOWNER),
                UI.fp(this, "actionFileOwnerAddOwner"));

        tlbTools.add(btnAddOwner);

        btnFindOwner = UI.getButton(null, i18n("Find_an_Owner"),
                IconManager.getIcon(IconManager.BUTTON_FINDOWNER),
                UI.fp(this, "actionFileOwnerFindOwner"));

        tlbTools.add(btnFindOwner);

        btnHelp = UI.getButton(null, i18n("Help"),
                IconManager.getIcon(IconManager.BUTTON_HELP),
                UI.fp(this, "actionHelpContents"));
        tlbTools.add(btnHelp);

        tlbTools.addSeparator();

        btnAddLostAnimal = UI.getButton(null, i18n("Add_Lost_Animal"),
                IconManager.getIcon(IconManager.BUTTON_ADDLOSTANIMAL),
                UI.fp(this, "actionFileLostAnimalsAddLost"));

        tlbTools.add(btnAddLostAnimal);

        btnFindLostAnimal = UI.getButton(null, i18n("Find_a_Lost_Animal"),
                IconManager.getIcon(IconManager.BUTTON_FINDLOSTANIMAL),
                UI.fp(this, "actionFileLostAnimalsFindLost"));

        tlbTools.add(btnFindLostAnimal);

        btnAddFoundAnimal = UI.getButton(null, i18n("Add_a_Found_Animal"),
                IconManager.getIcon(IconManager.BUTTON_ADDFOUNDANIMAL),
                UI.fp(this, "actionFileFoundAnimalsAddFound"));

        tlbTools.add(btnAddFoundAnimal);

        btnFindFoundAnimal = UI.getButton(null, i18n("Find_Found_Animal"),
                IconManager.getIcon(IconManager.BUTTON_FINDFOUNDANIMAL),
                UI.fp(this, "actionFileFoundAnimalsFindFound"));

        tlbTools.add(btnFindFoundAnimal);

        btnMatchLostandFound = UI.getButton(null,
                i18n("Produce_a_report_matching_lost_and_found_animals"),
                IconManager.getIcon(IconManager.BUTTON_MATCHLOSTANDFOUND),
                UI.fp(this, "actionFileMatchLostAndFound"));
        tlbTools.add(btnMatchLostandFound);

        tlbTools.addSeparator();

        btnReservations = UI.getButton(null, i18n("Reservation_Book"),
                IconManager.getIcon(IconManager.BUTTON_RESERVATIONS),
                UI.fp(this, "actionFileAnimalReservationBook"));

        tlbTools.add(btnReservations);

        btnFosterBook = UI.getButton(null, i18n("Foster_Book"),
                IconManager.getIcon(IconManager.BUTTON_FOSTERBOOK),
                UI.fp(this, "actionFileAnimalFosterBook"));

        tlbTools.add(btnFosterBook);

        btnRetailerBook = UI.getButton(null, i18n("Retailer_Book"),
                IconManager.getIcon(IconManager.BUTTON_RETAILERBOOK),
                UI.fp(this, "actionFileAnimalRetailerBook"));

        // Disable retailer functionality if option set
        if (!Configuration.getBoolean("DisableRetailer")) {
            tlbTools.add(btnRetailerBook);
        }

        btnWaitingList = UI.getButton(null, i18n("Waiting_List"),
                IconManager.getIcon(IconManager.BUTTON_WAITINGLIST),
                UI.fp(this, "actionFileAnimalWaitingList"));

        tlbTools.add(btnWaitingList);

        btnAddDiary = UI.getButton(null, i18n("Add_Diary_Note"),
                IconManager.getIcon(IconManager.BUTTON_ADDDIARY),
                UI.fp(this, "actionDiaryAddNote"));

        tlbTools.add(btnAddDiary);

        btnViewMyDiary = UI.getButton(null, i18n("Edit_Diary_Notes"),
                IconManager.getIcon(IconManager.BUTTON_VIEWMYDIARY),
                UI.fp(this, "actionDiaryViewMyNotes"));

        tlbTools.add(btnViewMyDiary);

        btnPrintDiary = UI.getButton(null, i18n("Print_Diary_Notes"),
                IconManager.getIcon(IconManager.BUTTON_PRINTDIARY),
                UI.fp(this, "actionDiaryPrintNotes"));

        tlbTools.add(btnPrintDiary);
    }

    public void actionInternetPets911Publish() {
        cursorToWait();
        addChild(new InternetPublisher(InternetPublisher.MODE_PETS911));
    }

    public void actionInternetPets911Settings() {
        cursorToWait();
        addChild(new Pets911Settings());
    }

    public void actionInternetRescueGroupsPublish() {
        cursorToWait();
        addChild(new InternetPublisher(InternetPublisher.MODE_RESCUEGROUPS));
    }

    public void actionInternetRescueGroupsSettings() {
        cursorToWait();
        addChild(new RescueGroupsSettings());
    }

    public void actionInternetSaveAPetPublish() {
        cursorToWait();
        addChild(new InternetPublisher(InternetPublisher.MODE_SAVEAPET));
    }

    public void actionInternetSaveAPetSettings() {
        cursorToWait();
        addChild(new SaveAPetSettings());
    }

    public void actionLookupsLogTypes() {
        cursorToWait();
        addChild(new LookupView(LookupView.LOGTYPE));
    }

    public void actionHelpDonate() {
        FileTypeManager.shellExecute(
            "http://sourceforge.net/project/project_donations.php?group_id=82533");
    }

    public void actionHelpAskQuestion() {
        FileTypeManager.shellExecute(
            "http://answers.launchpad.net/sheltermanager");
    }

    public void actionHelpReportBug() {
        FileTypeManager.shellExecute(
            "http://bugs.launchpad.net/sheltermanager/+filebug");
    }

    public void actionHelpTranslate() {
        FileTypeManager.shellExecute(
            "http://translations.launchpad.net/sheltermanager");
    }

    public void actionDiaryMedicalDiaryPrint() {
        new MedicalDiary();
    }

    public void actionDiaryMedicalBook() {
        cursorToWait();
        addChild(new MedicalView());
    }

    public void actionDiaryEditMedicalProfiles() {
        cursorToWait();
        addChild(new ProfileView());
    }

    public void actionHelpCredits() {
        new Credits();
    }

    public void actionHelpCheckUpdates() {
        new UpdateNotification().start();
    }

    public void actionHelpErrorLog() {
        String err = Global.errors.toString();

        if (err.length() == 0) {
            err = "No errors to report. Hurrah!";
        }

        String content = "<html><body><h2>Error Log</h2><pre>" + err +
            "</pre></body></html>";
        addChild(new HTMLViewer(content, "text/html"));
    }

    public void actionLookupsVouchers() {
        cursorToWait();
        addChild(new LookupView(LookupView.VOUCHER));
    }

    public void actionLookupsRemoveBreeds() {
        cursorToWait();
        addChild(new ConfigureLookups());
    }

    public void actionLookupsDiets() {
        cursorToWait();
        addChild(new LookupView(LookupView.DIET));
    }

    public void actionLookupsDonationTypes() {
        cursorToWait();
        addChild(new LookupView(LookupView.DONATIONTYPE));
    }

    public void actionFileAnimalRetailerBook() {
        cursorToWait();
        addChild(new MovementView(MovementView.MODE_RETAILERS));
    }

    public void actionSystemEditReports() {
        cursorToWait();
        addChild(new CustomReportView());
    }

    public void actionSystemMediaFiles() {
        cursorToWait();
        addChild(new MediaFiles());
    }

    public void actionSystemExportCustomReports() {
        cursorToWait();
        addChild(new CustomReportExport());
    }

    public void actionFileLock() {
        Locked l = new Locked();
        l.show();
        l = null;
    }

    public void actionInternetPetFinderBreeds() {
        cursorToWait();
        addChild(new PetFinderMapBreed());
    }

    public void actionInternetPetFinderMapping() {
        cursorToWait();
        addChild(new PetFinderMapSpecies());
    }

    public void actionInternetPetFinderSettings() {
        cursorToWait();
        addChild(new PetFinderSettings());
    }

    public void actionInternetPetFinderPublish() {
        cursorToWait();
        addChild(new InternetPublisher(InternetPublisher.MODE_PETFINDER));
    }

    public void actionLookupsDeathReasons() {
        cursorToWait();
        addChild(new LookupView(LookupView.DEATHREASON));
    }

    public void actionLookupsEntryReasons() {
        cursorToWait();
        addChild(new LookupView(LookupView.ENTRYREASON));
    }

    public void actionPreferencesFileTypes() {
        cursorToWait();
        addChild(new FileTypes());
    }

    public void actionPreferencesSwitchDatabase() {
        LocateDatabase.switchDatabase();
    }

    public void actionSystemEmail() {
        cursorToWait();
        addChild(new ConfigureEmail());
    }

    public void actionFileAnimalFosterBook() {
        cursorToWait();
        addChild(new MovementView(MovementView.MODE_FOSTERS));
    }

    public void actionFileAnimalNotAdoptionBook() {
        cursorToWait();
        addChild(new NotAdoptionView());
    }

    public void actionHelpLicence() {
        new Licence();
    }

    public void actionPreferencesCallGC() {
        try {
            cursorToWait();

            long old_available = Runtime.getRuntime().freeMemory();

            System.runFinalization();
            System.gc();

            long new_available = Runtime.getRuntime().freeMemory();
            long max_available = Runtime.getRuntime().maxMemory();
            long new_used = max_available - new_available;

            long freed = new_available - old_available;
            long freedK = (freed / 1024);
            long usedK = (new_used / 1024);
            long totalK = (max_available / 1024);

            setStatusText("GC freed " + Long.toString(freedK) +
                "Kb. Currently using " + Long.toString(usedK) + "Kb/" +
                Long.toString(totalK) + "Kb");
        } catch (Exception e) {
            setStatusText("Failed: " + e.getMessage());
        } finally {
            cursorToPointer();
        }
    }

    public void actionPreferencesSettings() {
        cursorToWait();
        addChild(new ConfigureLocal());
    }

    public void actionFileAnimalNames() {
        cursorToWait();
        addChild(new NamesView());
    }

    public void actionHelpContentsPDF() {
        cursorToWait();
        FileTypeManager.shellExecute(Global.dataDirectory + File.separator +
            "asm_en.pdf");
        cursorToPointer();
    }

    public void actionDiaryPVBook() {
        cursorToWait();
        new VaccinationView();
        cursorToPointer();
    }

    public void actionMailMergeOwnerMembershipExpiry() {
        new ExpiringMembers();
    }

    public void actionMailMergeOwnerMembership() {
        new Members();
    }

    public void actionMailMergeAdoptedNoReturn() {
        new AdoptedNoReturn();
    }

    public void actionDiaryVetsDiary() {
        new Vets();
    }

    public void actionDiaryVetsBook() {
        cursorToWait();
        addChild(new VetBookView());
    }

    public void actionFileAnimalAddWLEntry() {
        cursorToWait();

        WaitingListEdit ewl = new WaitingListEdit(null);
        ewl.openForNew();
        addChild(ewl);
    }

    public void actionMailMergeChipCancel() {
        new ChipCancellation();
    }

    public void actionMailMergeChipReg() {
        new ChipRegistration();
    }

    public void actionSystemDBDiagnostic() {
        new Diagnostic();
    }

    public void actionSystemDBCopy() {
        new DatabaseCopier().start();
    }

    public void actionSystemDBArchive() {
        new Thread() {
                public void run() {
                    try {
                        Animal.updateAllAnimalStatuses();
                    } catch (Exception e) {
                        Global.logException(e, getClass());
                    }
                }
            }.start();
    }

    public void actionSystemDBConfigure() {
        cursorToWait();
        addChild(new ConfigureDatabase());
    }

    public void actionSystemDBSQL() {
        cursorToWait();
        addChild(new SQLInterface());
    }

    public void actionSystemDBImport() {
        new DatabaseImporter().start();
    }

    public void actionSystemDBPFImport() {
        new PetFinderImport();
    }

    public void actionSystemDBUpdate() {
        new DBUpdate();
    }

    public void actionMailMergeOffShelterVaccination() {
        new OffShelterVaccinations();
    }

    public void actionGetMoreReports() {
        cursorToWait();
        addChild(new GetReports());
    }

    public void actionHelpContents() {
        cursorToWait();

        String h = "file:///" + Global.dataDirectory + File.separator +
            "help_en" + File.separator + "asm.html";

        // Use online help in applet mode
        if (Startup.applet) {
            h = "http://sheltermanager.sf.net/help/";
        }

        addChild(new HTMLViewer(h));
    }

    public void actionPreferencesLocalCache() {
        cursorToWait();
        addChild(new CacheView());
    }

    public void actionFileAnimalLitter() {
        cursorToWait();
        addChild(new LitterView());
    }

    public void actionInternetFTPSettings() {
        cursorToWait();
        addChild(new FTPSettings());
    }

    public void actionInternetPublishAvailable() {
        cursorToWait();
        addChild(new InternetPublisher(InternetPublisher.MODE_HTML));
    }

    public void actionDiaryPVOffShelter() {
        new VaccinationDiary(VaccinationDiary.VACC_OFFSHELTER);
    }

    public void actionDiaryPVOnShelter() {
        new VaccinationDiary(VaccinationDiary.VACC_ONSHELTER);
    }

    public void actionDiaryPVAllAnimals() {
        new VaccinationDiary(VaccinationDiary.VACC_ALL);
    }

    public void actionFileMatchLostAndFound() {
        new LostFoundMatch(0, 0);
    }

    public void actionFileLoginAgain() {
        // Collect garbage before we do anything
        actionPreferencesCallGC();

        // Reload the login form and destroy this frame
        Login login = new Login();
        login.setVisible(true);
        login = null;

        dontProcessClose = true;
        this.hide();
        this.dispose();
    }

    public void actionSystemAutoInsurance() {
        cursorToWait();
        addChild(new ConfigureInsuranceNumbers());
    }

    public void actionSystemAdditionalFields() {
        cursorToWait();
        addChild(new ConfigureAdditional());
    }

    public void actionDiaryEditTasks() {
        cursorToWait();
        addChild(new DiaryTaskView());
    }

    public void actionSystemReports() {
        cursorToWait();
        addChild(new ConfigureReportsDefaults());
    }

    public void actionFileAnimalWaitingList() {
        cursorToWait();
        addChild(new WaitingListView());
    }

    public void actionLookupsVaccinationTypes() {
        cursorToWait();
        addChild(new LookupView(LookupView.VACCINATIONTYPE));
    }

    public void actionLookupsSpecies() {
        cursorToWait();
        addChild(new LookupView(LookupView.SPECIES));
    }

    public void actionLookupsInternalLocations() {
        cursorToWait();
        addChild(new LookupView(LookupView.INTERNALLOCATION));
    }

    public void actionLookupsColour() {
        cursorToWait();
        addChild(new LookupView(LookupView.BASECOLOUR));
    }

    public void actionLookupsBreeds() {
        cursorToWait();
        addChild(new LookupView(LookupView.BREED));
    }

    public void actionLookupsBreedMap() {
        cursorToWait();
        addChild(new BreedSpeciesMapping());
    }

    public void actionLookupsAnimalTypes() {
        cursorToWait();
        addChild(new LookupView(LookupView.ANIMALTYPE));
    }

    public void actionDiaryAddNote() {
        cursorToWait();

        DiaryEdit ed = new DiaryEdit();
        ed.openForNew();
        addChild(ed);
        ed = null;
    }

    public void actionDiaryViewMyNotes() {
        cursorToWait();
        addChild(new DiaryView());
    }

    public void actionFileAnimalReservationBook() {
        cursorToWait();
        addChild(new MovementView(MovementView.MODE_RESERVATION));
    }

    public void actionFileFoundAnimalsFindFound() {
        cursorToWait();
        addChild(new FoundAnimalFind());
    }

    public void actionFileFoundAnimalsAddFound() {
        cursorToWait();

        FoundAnimalEdit efa = new FoundAnimalEdit();
        efa.openForNew();
        addChild(efa);
        efa = null;
    }

    public void actionFileLostAnimalsAddLost() {
        cursorToWait();

        LostAnimalEdit elo = new LostAnimalEdit();
        elo.openForNew();
        addChild(elo);
        elo = null;
    }

    public void actionFileLostAnimalsFindLost() {
        cursorToWait();

        LostAnimalFind flo = new LostAnimalFind();
        addChild(flo);
        flo = null;
    }

    public void actionSystemOptions() {
        cursorToWait();
        addChild(new Options());
    }

    public void actionSystemAuthentication() {
        cursorToWait();
        addChild(new Authentication());
    }

    public void actionHelpAbout() {
        // Open the about form
        new About();
    }

    public void actionFileOwnerAddOwner() {
        // Open a new owner form in edit mode
        cursorToWait();

        OwnerEdit eo = new OwnerEdit();
        eo.openForNew();
        addChild(eo);
        eo = null;
    }

    public void actionFileOwnerFindOwner() {
        // Create a new find animal form and display it in open mode
        cursorToWait();

        if (Configuration.getBoolean("AdvancedFindOwner")) {
            addChild(new OwnerFind());
        } else {
            addChild(new OwnerFindText());
        }
    }

    public void actionFileExit() {
        promptForClose();
    }

    public void actionCloseTab() {
        ASMForm f = null;

        try {
            f = (ASMForm) jdpDesktop.getSelectedFrame();
        } catch (Exception e) {
            // SwingWT can fail here if no windows open
        }

        if (f != null) {
            if (!f.formClosing()) {
                f.dispose();
            }
        }
    }

    public void actionSaveTab() {
        ASMForm f = null;

        try {
            f = (ASMForm) jdpDesktop.getSelectedFrame();
        } catch (Exception e) {
            // SwingWT can fail here if no windows open
        }

        if (f != null) {
            f.saveData();
        }
    }

    public void actionSystemUsers() {
        cursorToWait();
        addChild(new UserView());
    }

    public void actionFileAnimalAddAnimal() {
        cursorToWait();

        AnimalEdit ea = new AnimalEdit();
        ea.openForNew();
        addChild(ea);
        ea = null;
    }

    public void actionDiaryPrintNotes() {
        // Create a new diary report object to generate it.
        new DiaryNotesToday();
    }

    public void actionFileAnimalFindAnimal() {
        // Create a new find animal form and display it in open mode
        cursorToWait();

        if (Configuration.getBoolean("AdvancedFindAnimal")) {
            addChild(new AnimalFind());
        } else {
            addChild(new AnimalFindText());
        }
    }

    /** Exit the Application */
    public void promptForClose() {
        // Collect garbage before we do anything
        actionPreferencesCallGC();

        // This stops potential event loops
        if (dontProcessClose) {
            return;
        }

        // If the user hasn't said "don't ask me again",
        // make sure they want to logout/close
        boolean prompt = Configuration.getBoolean(Global.currentUserName +
                "_PromptLogout");

        if (!prompt) {
            closeApplication();
        } else {
            new Logout();
        }
    }

    public void closeApplication() {
        cursorToWait();

        // Unhook mainform reference
        Global.mainForm = null;

        // Hide the screen
        dontProcessClose = true;
        hide();
        dispose();

        // Unhook from modal boxes
        Dialog.theParent = null;

        // Spawn a new shutdown process
        new ShutdownThread().run();
    }

    public void logout() {
        actionFileLoginAgain();
    }

    /**
     * If the form has some audit info, show the icon and
     * grab the audit info to display as a tooltip
     */
    public void updateAuditInfo(ASMForm f) {
        if (f == null) {
            lblAudit.setVisible(false);

            return;
        }

        String auditinfo = f.getAuditInfo();

        if ((auditinfo == null) || auditinfo.equals("")) {
            lblAudit.setVisible(false);

            return;
        }

        lblAudit.setToolTipText(auditinfo);
        lblAudit.setVisible(true);
    }

    /**
     * Adds the specified internal frame window to the desktop environment
     *
     * @param form An ASMForm to add
     */
    public void addChild(ASMForm form) {
        cursorToWait();

        // If we're using autologout, note the time as this is
        // activity.
        resetAutoLogout();
        form.setVisible(true);

        // Add it to the desktop
        jdpDesktop.add(form);

        // Select it so it becomes the dominant
        // internal frame.
        try {
            form.setSelected(true);
        } catch (Exception e) {
            Global.logException(e, getClass());
        } finally {
            cursorToPointer();

            // Ditch our reference so the desktop
            // pane holds the only one
            form = null;
        }
    }

    /**
     * Reset the timer for automatic logout
     */
    public void resetAutoLogout() {
        if (Global.autoLogout > 0) {
            lastTimeAWindowWasOpened = Calendar.getInstance();
        }
    }

    public void cursorToWait() {
        UI.cursorToWait();
    }

    public void cursorToPointer() {
        UI.cursorToPointer();
    }

    /**
     * Sets the maximum value on the status bar.
     *
     * @param maxvalue
     *            The maximum value the status bar will go up to.
     */
    public void initStatusBarMax(int maxvalue) {
        progressMax = maxvalue;
        progressValue = 0;

        pgStatus.setMaximum(maxvalue);
        pgStatus.setStringPainted(true);
        pgStatus.setString(null);
        pgStatus.repaint();
        // Change the mouse pointer to an hourglass
        UI.cursorToWait();
        thrThrob.setVisible(true);
        pnlStatus.revalidate();
        thrThrob.start();
    }

    /** Resets the status bar back to empty */
    public void resetStatusBar() {
        pgStatus.setMaximum(100);
        pgStatus.setValue(0);
        pgStatus.setStringPainted(false);
        // Change the mouse pointer back to normal
        UI.cursorToPointer();
        thrThrob.setVisible(false);
        thrThrob.stop();
    }

    /** Increments the status bar by one value */
    public void incrementStatusBar() {
        progressValue++;

        if (progressValue <= progressMax) {
            pgStatus.setValue(progressValue);
        }
    }

    /** Sets the status text to the value specified */
    public void setStatusText(String newtext) {
        lblStatus.setText(newtext);
        lblStatus.repaint();
    }

    public class CustomReportMenu extends UI.MenuItem {
        private String customReportId = null;

        public CustomReportMenu(String customReportId, String text) {
            super();
            this.setOnClick(UI.fp(this, "activated"));
            this.customReportId = customReportId;
            this.setText(text);
            this.setIcon(IconManager.getIcon(IconManager.MENUBLANK));
        }

        public void activated() {
            try {
                new CustomReportExecute(customReportId);
            } catch (Exception e) {
            }
        }
    }
}
