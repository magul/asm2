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
package net.sourceforge.sheltermanager.asm.ui.ui;

import java.awt.Image;

import java.util.Random;

import javax.swing.ImageIcon;


/**
 * Manages retrieval of icon resources.
 *
 * This class holds at least one constant for every icon that appears in the
 * application. You can configure the entire graphical appearance of the
 * application from this one class.
 *
 * @author Robin Rawson-Tetley
 * @version 1.0
 */
public abstract class IconManager {
    // The splash screen
    public final static String SPLASH = "splash/splash.png";
    public final static String SPLASH1 = "splash/splash1.png";
    public final static String SPLASH2 = "splash/splash2.png";
    public final static String SPLASH3 = "splash/splash3.png";
    public final static String SPLASH4 = "splash/splash4.png";

    // Reusable constants for screens
    public final static String SEARCH = "actions-Search-24.png";
    public final static String SEARCHSMALL = "actions-Search-16.png";
    public final static String OPEN = "actions-Open-24.png";
    public final static String OPENSMALL = "actions-Open-16.png";
    public final static String CLEAR = "actions-Undo-24.png";
    public final static String CLEARSMALL = "actions-Undo-16.png";
    public final static String PRINT = "actions-Print-24.png";
    public final static String MAILMERGE = "actions-Document-24.png";
    public final static String NEW = "actions-New-24.png";
    public final static String NEWSMALL = "actions-New-16.png";
    public final static String EDIT = "actions-Edit-24.png";
    public final static String VIEW = "actions-Edit-24.png";
    public final static String CHECKOUT = "actions-Document-24.png";
    public final static String SAVE = "actions-Save-24.png";
    public final static String CLONE = "actions-Copy-24.png";
    public final static String CLOSE = "actions-Close-24.png";
    public final static String CLOSE_HILITE = "actions-Close-hilite-24.png";
    public final static String DELETE = "actions-Remove-24.png";
    public final static String PREVIEW = "actions-Preview-24.png";
    public final static String WEB = "actions-Web-24.png";
    public final static String DOCUMENT = "actions-Document-24.png";
    public final static String FOLDER = "actions-Folder-24.png";
    public final static String FOLDERNEW = "actions-FolderNew-24.png";
    public final static String FILE = "actions-Document-24.png";
    public final static String REFRESH = "actions-Refresh-24.png";
    public final static String SYSTEM = "system-Basic-24.png";
    public final static String ANIMAL = "animal-Basic-24.png";
    public final static String ANIMALADD = "animal-Add-24.png";
    public final static String ANIMALDELETE = "animal-Delete-24.png";
    public final static String ANIMALLOSTSEARCH = "animal-Lost-Search-24.png";
    public final static String ANIMALFOUNDSEARCH = "animal-Found-Search-24.png";
    public final static String ANIMALLOST = "animal-Lost-24.png";
    public final static String ANIMALFOUND = "animal-Found-24.png";
    public final static String LOG = "actions-Log-24.png";
    public final static String LINK = "actions-Link-24.png";
    public final static String MEDICAL = "actions-Medical-24.png";
    public final static String VIDEO = "actions-Video-24.png";
    public final static String DIET = "actions-Diet-24.png";
    public final static String MEDIA = "media-Basic-24.png";
    public final static String MEDIAADD = "media-Add-24.png";
    public final static String MEDIADELETE = "media-Delete-24.png";
    public final static String LITTER = "actions-Litter-24.png";
    public final static String LITTERNEW = "actions-LitterNew-24.png";
    public final static String OWNER = "owner-Basic-24.png";
    public final static String OWNERADD = "owner-Add-24.png";
    public final static String OWNERLINK = "owner-Attached-24.png";
    public final static String OWNERSEARCH = "owner-Search-24.png";
    public final static String TASK = "actions-Task-24.png";
    public final static String MATCH = "actions-Match-24.png";
    public final static String DIARY = "diary-View-24.png";
    public final static String DIARYPRINT = "diary-Print-24.png";
    public final static String DEATH = CLOSE;
    public final static String COMPLETE = "actions-Complete-24.png";
    public final static String RANDOM = "actions-Random-24.png";
    public final static String EXPORT = "actions-Export-24.png";
    public final static String REPORT = "actions-Report-24.png";
    public final static String EMAIL = "actions-Email-24.png";
    public final static String LOCK = "system-Lock-24.png";
    public final static String LOCKBIG = "system-Lock-48.png";
    public final static String ASMLOGO = "asm-Logo-64.png";
    public final static String QUESTION = "dialog-Question-32.png";
    public final static String INFO = "dialog-Info-32.png";
    public final static String WARNING = "dialog-Warning-32.png";
    public final static String ERROR = "dialog-Error-32.png";
    public final static String CREATERETURN = "actions-CreateReturn-24.png";
    public final static String BOOK = "actions-Book-24.png";
    public final static String DONATIONS = "donations-Basic-24.png";
    public final static String USERS = "system-Users-24.png";
    public final static String USERSSMALL = "system-Users-16.png";
    public final static String WAITINGLIST = "actions-WaitingList-24.png";
    public final static String DATABASE = "system-Database-24.png";
    public final static String DATABASESMALL = "system-Database-16.png";
    public final static String DATABASEBIG = "system-Database-64.png";
    public final static String AUDITSMALL = "actions-Document-16.png";
    public final static String UNDO = "actions-Undo-24.png";
    public final static String UP = "actions-Up-24.png";
    public final static String DOWN = "actions-Down-24.png";
    public final static String ABOUT = "actions-HelpAbout-24.png";
    public final static String DATEPICKER = "actions-WaitingList-16.png";
    public final static String BACK = "actions-Back-24.png";
    public final static String FORWARD = "actions-Forward-24.png";

    // Screen icons ======================================================
    // These are in package/class alphabetical order

    // ANIMAL package ====================================================
    public final static String SCREEN_ADDMEDIA = MEDIAADD;
    public final static String SCREEN_ADDMEDIA_CAPTURE = VIDEO;
    public final static String SCREEN_ADDMEDIA_BROWSE = SEARCH;
    public final static String SCREEN_ANIMALDIETS = DIET;
    public final static String SCREEN_ANIMALDIETS_NEW = NEW;
    public final static String SCREEN_ANIMALDIETS_EDIT = EDIT;
    public final static String SCREEN_ANIMALDIETS_DELETE = DELETE;
    public final static String SCREEN_ANIMALMEDIA = MEDIA;
    public final static String SCREEN_ANIMALMEDIA_NEW = MEDIAADD;
    public final static String SCREEN_ANIMALMEDIA_EDIT = EDIT;
    public final static String SCREEN_ANIMALMEDIA_DELETE = MEDIADELETE;
    public final static String SCREEN_ANIMALMEDIA_PREVIEW = PREVIEW;
    public final static String SCREEN_ANIMALMEDIA_CHECKOUT = CHECKOUT;
    public final static String SCREEN_ANIMALMEDIA_WEBPREFERRED = WEB;
    public final static String SCREEN_ANIMALMEDIA_SAVE = SAVE;
    public final static String SCREEN_EDITANIMALDIET = DIET;
    public final static String SCREEN_EDITANIMALVACCINATION = OPEN;
    public final static String SCREEN_EDITLITTER = LITTER;
    public final static String SCREEN_EDITMEDIAENTRY = MEDIA;
    public final static String SCREEN_EDITVACCINATIONS = OPEN;
    public final static String SCREEN_EDITVACCINATIONS_NEW = NEW;
    public final static String SCREEN_EDITVACCINATIONS_EDIT = EDIT;
    public final static String SCREEN_EDITVACCINATIONS_DELETE = DELETE;
    public final static String SCREEN_EDITVACCINATIONS_REFRESH = REFRESH;
    public final static String SCREEN_EDITVACCINATIONS_OPENANIMAL = ANIMAL;
    public final static String SCREEN_EDITVACCINATIONS_MARKCOMPLETE = COMPLETE;
    public final static String SCREEN_VACCINATIONBOOK = BOOK;
    public final static String SCREEN_FINDANIMAL = ANIMAL;
    public final static String SCREEN_FINDANIMAL_SEARCH = SEARCH;
    public final static String SCREEN_FINDANIMAL_OPEN = OPEN;
    public final static String SCREEN_FINDANIMAL_CLEAR = CLEAR;
    public final static String SCREEN_FINDANIMAL_PRINT = PRINT;
    public final static String SCREEN_FINDANIMAL_ADVANCED = PREVIEW;
    public final static String SCREEN_FINDANIMAL_SIMPLE = PREVIEW;

    // buttons left of results
    public final static String SCREEN_FINDANIMAL_GENERATEDOC = DOCUMENT;
    public final static String SCREEN_FINDANIMAL_DIARYTASK = TASK;
    public final static String SCREEN_FINDANIMAL_DIARYNOTE = DIARY;
    public final static String SCREEN_FINDANIMAL_LOSTFOUND = MATCH;
    public final static String SCREEN_FINDANIMAL_ADDMEDIA = MEDIAADD;
    public final static String SCREEN_FINDANIMAL_MOVEANIMAL = OWNERADD;
    public final static String SCREEN_EDITANIMAL = ANIMAL;
    public final static String SCREEN_EDITANIMAL_DEATH = DEATH;
    public final static String SCREEN_EDITANIMAL_MEDICAL = MEDICAL;
    public final static String SCREEN_EDITANIMAL_DIET = DIET;
    public final static String SCREEN_EDITANIMAL_MEDIA = MEDIA;
    public final static String SCREEN_EDITANIMAL_DIARY = DIARY;
    public final static String SCREEN_EDITANIMAL_MOVEMENT = OWNERLINK;
    public final static String SCREEN_EDITANIMAL_LOG = LOG;

    // buttons, top row
    public final static String SCREEN_EDITANIMAL_SAVE = SAVE;
    public final static String SCREEN_EDITANIMAL_CLONE = CLONE;
    public final static String SCREEN_EDITANIMAL_DELETE = ANIMALDELETE;
    public final static String SCREEN_EDITANIMAL_GENERATEDOC = DOCUMENT;
    public final static String SCREEN_EDITANIMAL_PRINT = PRINT;
    public final static String SCREEN_EDITANIMAL_DIARYTASK = TASK;
    public final static String SCREEN_EDITANIMAL_RANDOMNAME = RANDOM;
    public final static String SCREEN_EDITANIMAL_VIEWOWNER = OWNERLINK;
    public final static String SCREEN_EDITANIMAL_LOSTFOUND = MATCH;
    public final static String SCREEN_EDITANIMAL_CREATELITTER = LITTERNEW;
    public final static String SCREEN_EDITANIMAL_VIEWLITTERMATES = LITTER;
    public final static String SCREEN_EDITANIMAL_FINDLITTER = SEARCH;
    public final static String SCREEN_EDITANIMAL_COPYNOTES = CLONE;

    // buttons
    public final static String SCREEN_VIEWLITTERS = LITTER;
    public final static String SCREEN_VIEWLITTERS_NEW = NEW;
    public final static String SCREEN_VIEWLITTERS_EDIT = EDIT;
    public final static String SCREEN_VIEWLITTERS_DELETE = DELETE;
    public final static String SCREEN_VIEWLITTERS_REFRESH = REFRESH;
    public final static String SCREEN_VIEWLITTERS_ANIMALS = ANIMAL;

    // ANIMALNAME package
    // ==================================================================
    public final static String SCREEN_VIEWNAMES = RANDOM;
    public final static String SCREEN_VIEWNAMES_NEW = NEW;
    public final static String SCREEN_VIEWNAMES_EDIT = EDIT;
    public final static String SCREEN_VIEWNAMES_DELETE = DELETE;
    public final static String SCREEN_EDITNAMES = RANDOM;

    // CRITERIA package
    // ==================================================================
    public final static String SCREEN_CRITERIA_DATEFROMTO = PRINT;
    public final static String SCREEN_CRITERIA_DIARY = DIARYPRINT;

    // CUSTOMREPORT package
    // ================================================================
    public final static String SCREEN_EDITCUSTOMREPORT = REPORT;
    public final static String SCREEN_EDITCUSTOMREPORT_SAVE = SAVE;
    public final static String SCREEN_EDITCUSTOMREPORT_CHECKSQL = SYSTEM;
    public final static String SCREEN_EDITCUSTOMREPORT_GENERATEHTML = WEB;
    public final static String SCREEN_EXPORTCUSTOMREPORTDATA = EXPORT;
    public final static String SCREEN_EXPORTCUSTOMREPORTDATA_GENCSV = NEW;
    public final static String SCREEN_EXPORTCUSTOMREPORTDATA_GENANDVIEWCSV = EDIT;
    public final static String SCREEN_VIEWCUSTOMREPORTS = REPORT;
    public final static String SCREEN_VIEWCUSTOMREPORTS_NEW = NEW;
    public final static String SCREEN_VIEWCUSTOMREPORTS_EDIT = EDIT;
    public final static String SCREEN_VIEWCUSTOMREPORTS_DELETE = DELETE;
    public final static String SCREEN_INSTALLCUSTOMREPORTS = REPORT;
    public final static String SCREEN_INSTALLCUSTOMREPORTS_INSTALL = EXPORT;

    // DIARY package
    // =======================================================================
    public final static String SCREEN_DIARYBEAN_NEW = NEW;
    public final static String SCREEN_DIARYBEAN_EDIT = EDIT;
    public final static String SCREEN_DIARYBEAN_REFRESH = REFRESH;
    public final static String SCREEN_DIARYBEAN_DELETE = DELETE;
    public final static String SCREEN_DIARYBEAN_COMPLETE = COMPLETE;
    public final static String SCREEN_DIARYTASKS = TASK;
    public final static String SCREEN_DIARYTASKS_NEW = NEW;
    public final static String SCREEN_DIARYTASKS_EDIT = EDIT;
    public final static String SCREEN_DIARYTASKS_DELETE = DELETE;
    public final static String SCREEN_EDITDIARY = DIARY;
    public final static String SCREEN_EDITDIARYTASKDETAIL = TASK;
    public final static String SCREEN_EDITDIARYTASKHEAD = TASK;
    public final static String SCREEN_EDITDIARYTASKHEAD_SAVE = SAVE;
    public final static String SCREEN_EDITDIARYTASKHEAD_NEWDETAIL = NEW;
    public final static String SCREEN_EDITDIARYTASKHEAD_EDITDETAIL = EDIT;
    public final static String SCREEN_EDITDIARYTASKHEAD_DELETEDETAIL = DELETE;
    public final static String SCREEN_EXECUTEDIARYTASK = TASK;
    public final static String SCREEN_VIEWDIARY = DIARY;
    public final static String SCREEN_VIEWDIARY_NEW = NEW;
    public final static String SCREEN_VIEWDIARY_EDIT = EDIT;
    public final static String SCREEN_VIEWDIARY_DELETE = DELETE;
    public final static String SCREEN_VIEWDIARY_REFRESH = REFRESH;
    public final static String SCREEN_VIEWDIARY_COMPLETE = COMPLETE;
    public final static String SCREEN_VIEWDIARY_VIEWLINK = LINK;
    public final static String SCREEN_VIEWVETBOOK = DIARY;
    public final static String SCREEN_VIEWVETBOOK_NEW = NEW;
    public final static String SCREEN_VIEWVETBOOK_EDIT = EDIT;
    public final static String SCREEN_VIEWVETBOOK_DELETE = DELETE;
    public final static String SCREEN_VIEWVETBOOK_REFRESH = REFRESH;
    public final static String SCREEN_VIEWVETBOOK_COMPLETE = COMPLETE;
    public final static String SCREEN_VIEWVETBOOK_VIEWLINK = OPEN;

    // ERROR package
    // ====================================================================
    public final static String SCREEN_JDBCDLG = DATABASE;
    public final static String SCREEN_JDBCDLG_DATABASE = DATABASEBIG;

    // INTERNET package
    // ====================================================================
    public final static String SCREEN_EMAILFORM = EMAIL;
    public final static String SCREEN_FTPSETTINGS = WEB;
    public final static String SCREEN_PETFINDERMAPBREED = WEB;
    public final static String SCREEN_PETFINDERMAPBREED_EDIT = EDIT;
    public final static String SCREEN_PETFINDERMAPSPECIES = WEB;
    public final static String SCREEN_PETFINDERMAPSPECIES_EDIT = EDIT;
    public final static String SCREEN_PETFINDERPUBLISH = WEB;
    public final static String SCREEN_PETFINDERSETTINGS = WEB;
    public final static String SCREEN_PETS911PUBLISH = WEB;
    public final static String SCREEN_PETS911SETTINGS = WEB;
    public final static String SCREEN_FINDMEAPETPUBLISH = WEB;
    public final static String SCREEN_FINDMEAPETSETTINGS = WEB;
    public final static String SCREEN_RESCUEGROUPSPUBLISH = WEB;
    public final static String SCREEN_RESCUEGROUPSSETTINGS = WEB;
    public final static String SCREEN_SAVEAPETPUBLISH = WEB;
    public final static String SCREEN_SAVEAPETSETTINGS = WEB;
    public final static String SCREEN_PUBLISH = WEB;

    // LOCALCACHE package
    // ==================================================================
    public final static String SCREEN_VIEWCACHE = SYSTEM;
    public final static String SCREEN_VIEWCACHE_VIEW = VIEW;
    public final static String SCREEN_VIEWCACHE_DELETE = DELETE;
    public final static String SCREEN_VIEWCACHE_PURGE = UNDO;

    // LOG package
    // =========================================================================
    public final static String SCREEN_EDITLOG = LOG;
    public final static String SCREEN_VIEWLOG_NEW = NEW;
    public final static String SCREEN_VIEWLOG_EDIT = EDIT;
    public final static String SCREEN_VIEWLOG_DELETE = DELETE;

    // LOGIN package
    // =======================================================================
    public final static String SCREEN_LOGIN = LOCKBIG;

    // LOOKUPS package
    public final static String SCREEN_EDITLOOKUP = EDIT;
    public final static String SCREEN_VIEWLOOKUP = VIEW;
    public final static String SCREEN_VIEWLOOKUP_NEW = NEW;
    public final static String SCREEN_VIEWLOOKUP_EDIT = EDIT;
    public final static String SCREEN_VIEWLOOKUP_DELETE = DELETE;
    public final static String SCREEN_MAPBREEDSPECIES = EDIT;
    public final static String SCREEN_MAPBREEDSPECIES_EDIT = EDIT;
    public final static String SCREEN_MAPBREEDSPECIES_ALL = RANDOM;

    // LOSTANDFOUND package
    // ================================================================
    public final static String SCREEN_FINDLOSTANIMAL = ANIMALLOSTSEARCH;
    public final static String SCREEN_FINDLOSTANIMAL_SEARCH = SEARCH;
    public final static String SCREEN_FINDLOSTANIMAL_OPEN = OPEN;
    public final static String SCREEN_EDITLOSTANIMAL = ANIMALLOST;
    public final static String SCREEN_EDITLOSTANIMAL_SAVE = SAVE;
    public final static String SCREEN_EDITLOSTANIMAL_DELETE = DELETE;
    public final static String SCREEN_EDITLOSTANIMAL_MATCH = MATCH;
    public final static String SCREEN_EDITLOSTANIMAL_MEDIA = MEDIA;
    public final static String SCREEN_EDITLOSTANIMAL_DIARY = DIARY;
    public final static String SCREEN_EDITLOSTANIMAL_LOG = LOG;
    public final static String SCREEN_FINDFOUNDANIMAL = ANIMALFOUNDSEARCH;
    public final static String SCREEN_FINDFOUNDANIMAL_SEARCH = SEARCH;
    public final static String SCREEN_FINDFOUNDANIMAL_OPEN = OPEN;
    public final static String SCREEN_EDITFOUNDANIMAL = ANIMALFOUND;
    public final static String SCREEN_EDITFOUNDANIMAL_SAVE = SAVE;
    public final static String SCREEN_EDITFOUNDANIMAL_DELETE = DELETE;
    public final static String SCREEN_EDITFOUNDANIMAL_MATCH = MATCH;
    public final static String SCREEN_EDITFOUNDANIMAL_CREATEANIMAL = ANIMALADD;
    public final static String SCREEN_EDITFOUNDANIMAL_MEDIA = MEDIA;
    public final static String SCREEN_EDITFOUNDANIMAL_DIARY = DIARY;
    public final static String SCREEN_EDITFOUNDANIMAL_LOG = LOG;

    // MAIN package [ Not menu/toolbars on Main class ] ===================
    public final static String SCREEN_LOCKED_ASMLOGO = ASMLOGO;
    public final static String SCREEN_LOGOUT_QUESTION = QUESTION;
    public final static String SCREEN_MAIN = ASMLOGO;
    public final static String SCREEN_MAIN_USER = USERSSMALL;
    public final static String SCREEN_MAIN_DB = DATABASESMALL;
    public final static String SCREEN_MAIN_AUDIT = AUDITSMALL;
    public final static String SCREEN_UPDATES = ASMLOGO;

    // MEDICAL package ====================================================
    public final static String SCREEN_EDITMEDICAL = MEDICAL;
    public final static String SCREEN_EDITPROFILE = MEDICAL;
    public final static String SCREEN_EDITTREATMENT = MEDICAL;
    public final static String SCREEN_MEDICALBOOK = MEDICAL;

    // buttons for regimen
    public final static String SCREEN_VIEWMEDICALS_NEW = NEW;
    public final static String SCREEN_VIEWMEDICALS_NEWFROMPROFILE = CLONE;
    public final static String SCREEN_VIEWMEDICALS_EDIT = EDIT;
    public final static String SCREEN_VIEWMEDICALS_REFRESH = REFRESH;
    public final static String SCREEN_VIEWMEDICALS_DELETE = DELETE;
    public final static String SCREEN_VIEWMEDICALS_NEWTREATMENT = NEW;
    public final static String SCREEN_VIEWMEDICALS_EDITTREATMENT = EDIT;
    public final static String SCREEN_VIEWMEDICALS_DELETETREATMENT = DELETE;
    public final static String SCREEN_VIEWMEDICALS_UPTODATE = UNDO;
    public final static String SCREEN_VIEWMEDICALS_UPTODATE_SELECTED = DOCUMENT;
    public final static String SCREEN_VIEWPROFILES = MEDICAL;
    public final static String SCREEN_VIEWPROFILES_NEW = NEW;
    public final static String SCREEN_VIEWPROFILES_EDIT = EDIT;
    public final static String SCREEN_VIEWPROFILES_DELETE = DELETE;

    // MOVEMENT package ===============================================
    public final static String SCREEN_ANIMALMOVEMENT_NEW = NEW;
    public final static String SCREEN_ANIMALMOVEMENT_EDIT = EDIT;
    public final static String SCREEN_ANIMALMOVEMENT_DELETE = DELETE;
    public final static String SCREEN_ANIMALMOVEMENT_GENERATEDOC = DOCUMENT;
    public final static String SCREEN_ANIMALMOVEMENT_VIEWANIMAL = ANIMAL;
    public final static String SCREEN_ANIMALMOVEMENT_VIEWOWNER = OWNERLINK;
    public final static String SCREEN_EDITMOVEMENT = OPEN;
    public final static String SCREEN_EDITMOVEMENT_DIARY = DIARY;
    public final static String SCREEN_EDITMOVEMENT_DONATIONS = DONATIONS;
    public final static String SCREEN_EDITMOVEMENT_LOG = LOG;
    public final static String SCREEN_EDITMOVEMENT_SAVE = SAVE;
    public final static String SCREEN_EDITMOVEMENT_GENERATEDOC = DOCUMENT;
    public final static String SCREEN_EDITMOVEMENT_VIEWANIMAL = ANIMAL;
    public final static String SCREEN_EDITMOVEMENT_VIEWOWNER = OWNERLINK;
    public final static String SCREEN_FOSTERBOOK = BOOK;
    public final static String SCREEN_FOSTERBOOK_REFRESH = REFRESH;
    public final static String SCREEN_FOSTERBOOK_NEW = NEW;
    public final static String SCREEN_FOSTERBOOK_EDIT = EDIT;
    public final static String SCREEN_FOSTERBOOK_RETURNCREATENEW = CREATERETURN;
    public final static String SCREEN_FOSTERBOOK_VIEWANIMAL = ANIMAL;
    public final static String SCREEN_FOSTERBOOK_VIEWOWNER = OWNERLINK;
    public final static String SCREEN_NOTADOPTIONBOOK = ANIMAL;
    public final static String SCREEN_NOTADOPTIONBOOK_REFRESH = REFRESH;
    public final static String SCREEN_NOTADOPTIONBOOK_EDIT = ANIMAL;
    public final static String SCREEN_RESERVATIONBOOK = BOOK;
    public final static String SCREEN_RESERVATIONBOOK_REFRESH = REFRESH;
    public final static String SCREEN_RESERVATIONBOOK_NEW = NEW;
    public final static String SCREEN_RESERVATIONBOOK_EDIT = EDIT;
    public final static String SCREEN_RESERVATIONBOOK_VIEWANIMAL = ANIMAL;
    public final static String SCREEN_RESERVATIONBOOK_VIEWOWNER = OWNERLINK;
    public final static String SCREEN_RETAILERBOOK = BOOK;
    public final static String SCREEN_RETAILERBOOK_REFRESH = REFRESH;
    public final static String SCREEN_RETAILERBOOK_NEW = NEW;
    public final static String SCREEN_RETAILERBOOK_EDIT = EDIT;
    public final static String SCREEN_RETAILERBOOK_VIEWANIMAL = ANIMAL;
    public final static String SCREEN_RETAILERBOOK_VIEWOWNER = OWNERLINK;
    public final static String SCREEN_RETAILERBOOK_RETURNCREATENEW = CREATERETURN;

    // OWNER package ==================================================
    public final static String SCREEN_CHECKOWNER = OWNER;
    public final static String SCREEN_EDITOWNER = OWNER;
    public final static String SCREEN_EDITOWNER_SAVE = SAVE;
    public final static String SCREEN_EDITOWNER_DELETE = DELETE;
    public final static String SCREEN_EDITOWNER_GENERATEDOC = DOCUMENT;
    public final static String SCREEN_EDITOWNER_DIARYTASK = TASK;
    public final static String SCREEN_EDITOWNER_EMAIL = EMAIL;
    public final static String SCREEN_EDITOWNER_MERGE = CLONE;

    // tabs
    public final static String SCREEN_EDITOWNER_DONATIONS = DONATIONS;
    public final static String SCREEN_EDITOWNER_VOUCHERS = DONATIONS;
    public final static String SCREEN_EDITOWNER_MEDIA = MEDIA;
    public final static String SCREEN_EDITOWNER_DIARY = DIARY;
    public final static String SCREEN_EDITOWNER_MOVEMENT = OWNERLINK;
    public final static String SCREEN_EDITOWNER_LINKS = LINK;
    public final static String SCREEN_EDITOWNER_LOG = LOG;
    public final static String SCREEN_EDITOWNERDONATION = DONATIONS;
    public final static String SCREEN_EDITOWNERVOUCHER = DONATIONS;
    public final static String SCREEN_EMBEDOWNER_NEW = NEW;
    public final static String SCREEN_EMBEDOWNER_OPEN = OPEN;
    public final static String SCREEN_EMBEDOWNER_SEARCH = SEARCH;
    public final static String SCREEN_EMBEDOWNER_CLEAR = CLEAR;
    public final static String SCREEN_EMBEDOWNERSMALL_NEW = NEWSMALL;
    public final static String SCREEN_EMBEDOWNERSMALL_OPEN = OPENSMALL;
    public final static String SCREEN_EMBEDOWNERSMALL_SEARCH = SEARCHSMALL;
    public final static String SCREEN_EMBEDOWNERSMALL_CLEAR = CLEARSMALL;
    public final static String SCREEN_FINDOWNER = OWNERSEARCH;
    public final static String SCREEN_FINDOWNER_SEARCH = SEARCH;
    public final static String SCREEN_FINDOWNER_MAILMERGE = MAILMERGE;
    public final static String SCREEN_FINDOWNER_OPEN = OPEN;
    public final static String SCREEN_FINDOWNER_CLEAR = CLEAR;
    public final static String SCREEN_FINDOWNER_PRINT = PRINT;
    public final static String SCREEN_OWNERDONATIONINSTALMENT = DONATIONS;
    public final static String SCREEN_OWNERLINKS_VIEW = EDIT;
    public final static String SCREEN_OWNERVOUCHERS_NEW = NEW;
    public final static String SCREEN_OWNERVOUCHERS_EDIT = EDIT;
    public final static String SCREEN_OWNERVOUCHERS_DELETE = DELETE;
    public final static String SCREEN_VIEWOWNERDONATIONS_NEW = NEW;
    public final static String SCREEN_VIEWOWNERDONATIONS_NEWINSTALMENT = CLONE;
    public final static String SCREEN_VIEWOWNERDONATIONS_EDIT = EDIT;
    public final static String SCREEN_VIEWOWNERDONATIONS_DELETE = DELETE;
    public final static String SCREEN_VIEWOWNERDONATIONS_GENERATEDOC = DOCUMENT;

    // REPORTVIEWER package
    // ==========================================================
    public final static String SCREEN_REPORTVIEWER = REPORT;
    public final static String SCREEN_REPORTVIEWER_CLOSE = CLOSE;
    public final static String SCREEN_REPORTVIEWER_PRINT = PRINT;
    public final static String SCREEN_REPORTVIEWER_EXTERNAL = PREVIEW;

    // SPLASH package
    // ================================================================
    public final static String SCREEN_ABOUT_ASMLOGO = ASMLOGO;
    public final static String SCREEN_ABOUT_ABOUT = ABOUT;
    public final static String SCREEN_ABOUT_SYSTEM = SYSTEM;

    // SYSTEM package
    // ================================================================
    public final static String SCREEN_AUTHENTICATION = USERS;
    public final static String SCREEN_CONFIGUREADDITIONAL = SYSTEM;
    public final static String SCREEN_CONFIGUREADDITIONAL_NEW = NEW;
    public final static String SCREEN_CONFIGUREADDITIONAL_EDIT = EDIT;
    public final static String SCREEN_CONFIGUREADDITIONAL_DELETE = DELETE;
    public final static String SCREEN_CONFIGUREEMAIL = EMAIL;
    public final static String SCREEN_CONFIGUREINSURANCENUMBERS = SYSTEM;
    public final static String SCREEN_CONFIGUREDATABASE = SYSTEM;
    public final static String SCREEN_CONFIGURELOCAL = SYSTEM;
    public final static String SCREEN_CONFIGURELOOKUPS = SYSTEM;
    public final static String SCREEN_CONFIGUREREPORTS = SYSTEM;
    public final static String SCREEN_EDITADDITIONALFIELD = SYSTEM;
    public final static String SCREEN_FILETYPEEDIT = SYSTEM;
    public final static String SCREEN_FILETYPES = SYSTEM;
    public final static String SCREEN_FILETYPES_NEW = NEW;
    public final static String SCREEN_FILETYPES_SAVE = SAVE;
    public final static String SCREEN_FILETYPES_DELETE = DELETE;
    public final static String SCREEN_FILETYPES_SCAN = SYSTEM;
    public final static String SCREEN_MEDIAFILES = MEDIA;
    public final static String SCREEN_MEDIAFILES_UPLOAD = UNDO;
    public final static String SCREEN_MEDIAFILES_EDIT = EDIT;
    public final static String SCREEN_MEDIAFILES_DOWNLOAD = SAVE;
    public final static String SCREEN_MEDIAFILES_DELETE = DELETE;
    public final static String SCREEN_MEDIAFILES_NEWDIR = FOLDERNEW;
    public final static String SCREEN_MEDIAFILES_IMPORTDIR = UP;
    public final static String SCREEN_MEDIAFILES_EXPORTDIR = DOWN;
    public final static String SCREEN_MEDIAFILES_IMPORTFROMFS = MEDIA;
    public final static String SCREEN_OPTIONS = SYSTEM;
    public final static String SCREEN_OPTIONS_SAVE = SAVE;
    public final static String SCREEN_OPTIONS_CLOSE = CLOSE;
    public final static String SCREEN_SQLINTERFACE = SYSTEM;
    public final static String SCREEN_SQLINTERFACE_EXECUTE = CREATERETURN;

    // USERS package
    // =================================================================
    public final static String SCREEN_EDITUSER = USERS;
    public final static String SCREEN_VIEWUSERS = USERS;
    public final static String SCREEN_VIEWUSERS_NEW = NEW;
    public final static String SCREEN_VIEWUSERS_EDIT = EDIT;
    public final static String SCREEN_VIEWUSERS_DELETE = DELETE;
    public final static String SCREEN_VIEWUSERS_PASSWORD = USERS;

    // VIEWERS package
    // ===============================================================
    public final static String SCREEN_HTMLVIEWER = WEB;
    public final static String SCREEN_HTMLVIEWER_BACK = BACK;
    public final static String SCREEN_HTMLVIEWER_FORWARD = FORWARD;
    public final static String SCREEN_HTMLVIEWER_REFRESH = REFRESH;

    // WAITINGLIST package
    // ===========================================================
    public final static String SCREEN_EDITWAITINGLIST = WAITINGLIST;
    public final static String SCREEN_EDITWAITINGLIST_DIARY = DIARY;
    public final static String SCREEN_EDITWAITINGLIST_MEDIA = MEDIA;
    public final static String SCREEN_EDITWAITINGLIST_LOG = LOG;
    public final static String SCREEN_EDITWAITINGLIST_SAVE = SAVE;
    public final static String SCREEN_EDITWAITINGLIST_CLONE = CLONE;
    public final static String SCREEN_EDITWAITINGLIST_DELETE = DELETE;
    public final static String SCREEN_EDITWAITINGLIST_CREATEANIMAL = ANIMALADD;
    public final static String SCREEN_VIEWWAITINGLIST = WAITINGLIST;
    public final static String SCREEN_VIEWWAITINGLIST_NEW = NEW;
    public final static String SCREEN_VIEWWAITINGLIST_EDIT = EDIT;
    public final static String SCREEN_VIEWWAITINGLIST_DELETE = DELETE;
    public final static String SCREEN_VIEWWAITINGLIST_REFRESH = REFRESH;
    public final static String SCREEN_VIEWWAITINGLIST_PRINT = PRINT;
    public final static String SCREEN_VIEWWAITINGLIST_COMPLETE = COMPLETE;

    // WORDPROCESSOR package
    // =========================================================
    public final static String SCREEN_SELECTTEMPLATE = DOCUMENT;

    // Menu ====================================================================
    public final static String MENUBLANK = "actions-Blank-16.png";
    public final static String MENU_REVERTTOPRODUCTION = MENUBLANK;
    public final static String MENU_REPORT = MENUBLANK;
    public final static String MENU_FILEANIMAL = "animal-Basic-16.png";
    public final static String MENU_FILEANIMALADDANIMAL = "animal-Add-16.png";
    public final static String MENU_FILEANIMALFINDANIMAL = "animal-Search-16.png";
    public final static String MENU_FILEANIMALADDWLENTRY = MENUBLANK;
    public final static String MENU_FILEANIMALWAITINGLIST = "actions-WaitingList-16.png";
    public final static String MENU_FILEANIMALRESERVATIONBOOK = "actions-Book-16.png";
    public final static String MENU_FILEANIMALFOSTERBOOK = "actions-Book-16.png";
    public final static String MENU_FILEANIMALRETAILERBOOK = MENU_FILEANIMALFOSTERBOOK;
    public final static String MENU_FILEANIMALNOTADOPTIONBOOK = "animal-Search-16.png";
    public final static String MENU_FILEANIMALLITTER = "actions-Litter-16.png";
    public final static String MENU_FILEANIMALNAMES = "actions-Random-16.png";
    public final static String MENU_FILEOWNER = "owner-Attached-16.png";
    public final static String MENU_FILEOWNERADDOWNER = "owner-Add-16.png";
    public final static String MENU_FILEOWNERFINDOWNER = "owner-Search-16.png";
    public final static String MENU_FILELOSTANDFOUND = "animal-Lost-16.png";
    public final static String MENU_FILELOSTANIMALS = "animal-Lost-16.png";
    public final static String MENU_FILELOSTANIMALSADDLOST = "animal-Lost-Add-16.png";
    public final static String MENU_FILELOSTANIMALSFINDLOST = "animal-Lost-Search-16.png";
    public final static String MENU_FILEFOUNDANIMALS = "animal-Found-16.png";
    public final static String MENU_FILEFOUNDANIMALSADDFOUND = "animal-Found-Add-16.png";
    public final static String MENU_FILEFOUNDANIMALSFINDFOUND = "animal-Found-Search-16.png";
    public final static String MENU_FILEMATCHLOSTANDFOUND = "actions-Match-16.png";
    public final static String MENU_FILELOCK = "system-Lock-16.png";
    public final static String MENU_FILELOGINAGAIN = "actions-Refresh-16.png";
    public final static String MENU_FILECLOSETAB = "actions-Close-16.png";
    public final static String MENU_FILEEXIT = "actions-Close-16.png";
    public final static String MENU_DIARYPRINTVACC = "actions-Medical-16.png";
    public final static String MENU_DIARYPVALLANIMALS = "animal-Basic-16.png";
    public final static String MENU_DIARYPVONSHELTER = MENUBLANK;
    public final static String MENU_DIARYPVOFFSHELTER = MENUBLANK;
    public final static String MENU_DIARYPVBOOK = "actions-Book-16.png";
    public final static String MENU_DIARYMEDICAL = "actions-Medical-16.png";
    public final static String MENU_DIARYMEDICALDIARYPRINT = "diary-Print-16.png";
    public final static String MENU_DIARYMEDICALBOOK = "actions-Book-16.png";
    public final static String MENU_DIARYPRINTNOTES = "diary-Print-16.png";
    public final static String MENU_DIARYVETSDIARY = "actions-Print-16.png";
    public final static String MENU_DIARYVETSBOOK = "diary-View-16.png";
    public final static String MENU_DIARYEDITTASKS = "actions-Task-16.png";
    public final static String MENU_DIARYEDITMEDICALPROFILES = "actions-Medical-16.png";
    public final static String MENU_DIARYADDNOTE = "diary-Add-16.png";
    public final static String MENU_DIARYVIEWMYNOTES = "diary-View-16.png";
    public final static String MENU_SYSTEMLOOKUPS = "system-Database-16.png";
    public final static String MENU_LOOKUPSANIMALTYPES = MENUBLANK;
    public final static String MENU_LOOKUPSBREEDS = MENUBLANK;
    public final static String MENU_LOOKUPSBREEDMAP = MENUBLANK;
    public final static String MENU_LOOKUPSCOLOUR = MENUBLANK;
    public final static String MENU_LOOKUPSDEATHREASONS = MENUBLANK;
    public final static String MENU_LOOKUPSDIETS = MENUBLANK;
    public final static String MENU_LOOKUPSDONATIONTYPES = MENUBLANK;
    public final static String MENU_LOOKUPSENTRYREASONS = MENUBLANK;
    public final static String MENU_LOOKUPSINTERNALLOCATIONS = MENUBLANK;
    public final static String MENU_LOOKUPSLOGTYPES = MENUBLANK;
    public final static String MENU_LOOKUPSSPECIES = MENUBLANK;
    public final static String MENU_LOOKUPSVACCINATIONTYPES = MENUBLANK;
    public final static String MENU_LOOKUPSVOUCHERS = MENUBLANK;
    public final static String MENU_LOOKUPSREMOVEBREEDS = MENUBLANK;
    public final static String MENU_SYSTEMAUTOINSURANCE = MENUBLANK;
    public final static String MENU_SYSTEMCONFIGUREADDITIONAL = MENUBLANK;
    public final static String MENU_SYSTEMEDITREPORTS = "actions-Report-16.png";
    public final static String MENU_SYSTEMMEDIAFILES = "media-Basic-16.png";
    public final static String MENU_SYSTEMEXPORTCUSTOMREPORTS = "actions-Export-16.png";
    public final static String MENU_SYSTEMREPORTS = "actions-Report-16.png";
    public final static String MENU_SYSTEMEMAIL = "actions-Email-16.png";
    public final static String MENU_SYSTEMUSERS = "system-Users-16.png";
    public final static String MENU_SYSTEMDATABASETOOLS = "system-Database-16.png";
    public final static String MENU_SYSTEMDBUPDATE = "actions-Refresh-16.png";
    public final static String MENU_SYSTEMDBDIAGNOSTIC = "system-Database-16.png";
    public final static String MENU_SYSTEMDBCONFIGURE = "system-Basic-16.png";
    public final static String MENU_SYSTEMDBCOPY = "actions-Copy-16.png";
    public final static String MENU_SYSTEMDBARCHIVE = "system-Database-16.png";
    public final static String MENU_SYSTEMDBIMPORT = "actions-Export-16.png";
    public final static String MENU_SYSTEMDBPFIMPORT = "actions-Export-16.png";
    public final static String MENU_SYSTEMDBSQL = "system-Database-16.png";
    public final static String MENU_SYSTEMAUTHENTICATION = "system-Users-16.png";
    public final static String MENU_SYSTEMOPTIONS = "system-Basic-16.png";
    public final static String MENU_INTERNETPUBLISHAVAILABLE = "actions-Web-16.png";
    public final static String MENU_INTERNETFTPSETTINGS = "system-Basic-16.png";
    public final static String MENU_INTERNETPETFINDER = MENUBLANK;
    public final static String MENU_INTERNETPETFINDERPUBLISH = "actions-Web-16.png";
    public final static String MENU_INTERNETPETFINDERSETTINGS = "system-Basic-16.png";
    public final static String MENU_INTERNETPETFINDERMAPPING = MENUBLANK;
    public final static String MENU_INTERNETPETFINDERBREEDS = MENUBLANK;
    public final static String MENU_INTERNETPETS911 = MENUBLANK;
    public final static String MENU_INTERNETPETS911PUBLISH = "actions-Web-16.png";
    public final static String MENU_INTERNETPETS911SETTINGS = "system-Basic-16.png";
    public final static String MENU_INTERNETRESCUEGROUPS = MENUBLANK;
    public final static String MENU_INTERNETRESCUEGROUPSPUBLISH = "actions-Web-16.png";
    public final static String MENU_INTERNETRESCUEGROUPSSETTINGS = "system-Basic-16.png";
    public final static String MENU_INTERNETSAVEAPET = MENUBLANK;
    public final static String MENU_INTERNETSAVEAPETPUBLISH = "actions-Web-16.png";
    public final static String MENU_INTERNETSAVEAPETSETTINGS = "system-Basic-16.png";
    public final static String MENU_INTERNETFINDMEAPET = MENUBLANK;
    public final static String MENU_INTERNETFINDMEAPETPUBLISH = "actions-Web-16.png";
    public final static String MENU_INTERNETFINDMEAPETSETTINGS = "system-Basic-16.png";
    public final static String MENU_MAILMERGEADOPTIONS = "owner-Attached-16.png";
    public final static String MENU_MAILMERGEOWNER = "owner-Basic-16.png";
    public final static String MENU_MAILMERGEOWNERMEMBERSHIP = MENUBLANK;
    public final static String MENU_MAILMERGEOWNERMEMBERSHIPEXPIRY = MENUBLANK;
    public final static String MENU_MAILMERGEOFFSHELTERVACCINATIONS = MENUBLANK;
    public final static String MENU_MAILMERGECHIPREG = MENUBLANK;
    public final static String MENU_MAILMERGECHIPCANCEL = MENUBLANK;
    public final static String MENU_MAILMERGEADOPTEDNORETURN = MENUBLANK;
    public final static String MENU_PREFERENCESLOCALCACHE = "system-Basic-16.png";
    public final static String MENU_PREFERENCESCALLGC = "actions-Refresh-16.png";
    public final static String MENU_PREFERENCESFILETYPES = "actions-Document-16.png";
    public final static String MENU_PREFERENCESSWITCHDATABASE = "system-Database-16.png";
    public final static String MENU_PREFERENCESSETTINGS = "system-Basic-16.png";
    public final static String MENU_HELPCONTENTS = "actions-Help-16.png";
    public final static String MENU_HELPCONTENTSPDF = "actions-Help-16.png";
    public final static String MENU_HELPLICENCE = MENUBLANK;
    public final static String MENU_HELPCHECKUPDATES = MENUBLANK;
    public final static String MENU_HELPCREDITS = MENUBLANK;
    public final static String MENU_HELPERRORLOG = MENUBLANK;
    public final static String MENU_HELPDONATE = MENUBLANK;
    public final static String MENU_HELPASKQUESTION = MENUBLANK;
    public final static String MENU_HELPREPORTBUG = MENUBLANK;
    public final static String MENU_HELPTRANSLATE = MENUBLANK;
    public final static String MENU_HELPABOUT = "actions-HelpAbout-16.png";

    // Toolbar ================================================================
    public final static String BUTTON_ADDANIMAL = "animal-Add-32.png";
    public final static String BUTTON_FINDANIMAL = "animal-Search-32.png";
    public final static String BUTTON_ADDOWNER = "owner-Add-32.png";
    public final static String BUTTON_FINDOWNER = "owner-Search-32.png";
    public final static String BUTTON_HELP = "actions-Help-32.png";
    public final static String BUTTON_ADDLOSTANIMAL = "animal-Lost-Add-32.png";
    public final static String BUTTON_FINDLOSTANIMAL = "animal-Lost-Search-32.png";
    public final static String BUTTON_ADDFOUNDANIMAL = "animal-Found-Add-32.png";
    public final static String BUTTON_FINDFOUNDANIMAL = "animal-Found-Search-32.png";
    public final static String BUTTON_MATCHLOSTANDFOUND = "actions-Match-32.png";
    public final static String BUTTON_RESERVATIONS = "actions-Book-32.png";
    public final static String BUTTON_FOSTERBOOK = "actions-Book-32.png";
    public final static String BUTTON_RETAILERBOOK = BUTTON_FOSTERBOOK;
    public final static String BUTTON_WAITINGLIST = "actions-WaitingList-32.png";
    public final static String BUTTON_ADDDIARY = "diary-Add-32.png";
    public final static String BUTTON_VIEWMYDIARY = "diary-View-32.png";
    public final static String BUTTON_PRINTDIARY = "diary-Print-32.png";

    /** Random number generator for splash screens */
    private static Random r = new Random();

    /**
     * Loads an icon according to one of the string constants available from
     * this file
     *
     * This functionality may be extended at some point to include platform
     * specifics and allow different icon sets for each platform.
     *
     * @param icon
     *            The icon to load in
     * @return The icon, ready for use by the UI
     */
    public static ImageIcon getIcon(String icon) {
        // Global.logDebug("ICON: " + icon, "IconManager.getIcon");
        return new ImageIcon(IconManager.class.getResource(
                "/net/sourceforge/sheltermanager/asm/ui/res/" + icon));
    }

    public static ImageIcon getIcon(Image image) {
        return new ImageIcon(image);
    }

    /**
     * Returns an icon from a file system path
     */
    public static ImageIcon getIconFromPath(String path) {
        return new ImageIcon(path);
    }

    /**
     * Loads an image, scales it and returns an icon instance
     * for use in components
     */
    public static ImageIcon getThumbnail(String path, int width, int height) {
        Image inImage = new ImageIcon(path).getImage();
        Image outImage = UI.scaleImage(inImage, width, height);

        // inImage.image.dispose();
        return new ImageIcon(outImage);
    }

    /** Returns a throbber image, index must be
     *  between 1 and 12 */
    public static ImageIcon getThrobber(int index) {
        if ((index < 1) || (index > 12)) {
            return null;
        }

        return getIcon("throbber/throbber" + index + ".png");
    }

    /** Returns a small throbber image, index must be
     *  between 1 and 12 */
    public static ImageIcon getThrobberSmall(int index) {
        if ((index < 1) || (index > 12)) {
            return null;
        }

        return getIcon("throbber_small/throbber" + index + ".png");
    }

    /**
     * Returns a splash screen at random from our pool
     * @return An icon containing the splash screen (400x200)
     */
    public static ImageIcon getSplashScreen() {
        int s = r.nextInt(5);

        switch (s) {
        case 0:
            return getIcon(SPLASH);

        case 1:
            return getIcon(SPLASH1);

        case 2:
            return getIcon(SPLASH2);

        case 3:
            return getIcon(SPLASH3);

        case 4:
            return getIcon(SPLASH4);

        default:
            return getIcon(SPLASH);
        }
    }
}
