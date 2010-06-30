CREATE MEMORY TABLE accounts (
  ID INTEGER NOT NULL PRIMARY KEY,
  Code VARCHAR(255) NOT NULL,
  Description VARCHAR(255) NOT NULL,
  AccountType INTEGER NOT NULL,
  DonationTypeID INTEGER NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX accounts_Code ON accounts (Code);

CREATE MEMORY TABLE accountstrx (
  ID INTEGER NOT NULL PRIMARY KEY,
  TrxDate TIMESTAMP NOT NULL,
  Description VARCHAR(255) NULL,
  Reconciled INTEGER NOT NULL,
  Amount FLOAT NOT NULL,
  SourceAccountID INTEGER NOT NULL,
  DestinationAccountID INTEGER NOT NULL,
  OwnerDonationID INTEGER NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL

);
CREATE INDEX accountstrx_TrxDate ON accountstrx (TrxDate);
CREATE INDEX accountstrx_Source ON accountstrx (SourceAccountID);
CREATE INDEX accountstrx_Dest ON accountstrx (DestinationAccountID);

CREATE MEMORY TABLE lksaccounttype (
  ID INTEGER NOT NULL PRIMARY KEY,
  AccountType VARCHAR(255) NOT NULL
);

CREATE MEMORY TABLE additionalfield (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType INTEGER NOT NULL,
  FieldName VARCHAR(255) NOT NULL,
  FieldLabel VARCHAR(255) NOT NULL,
  ToolTip VARCHAR(255) NULL,
  LookupValues VARCHAR(16384) NULL,
  FieldType INTEGER NOT NULL,
  DisplayIndex INTEGER NOT NULL
);
CREATE INDEX additionalfield_LinkType ON additionalfield (LinkType);

CREATE MEMORY TABLE additional (
  LinkType INTEGER NOT NULL,
  LinkID INTEGER NOT NULL,
  AdditionalFieldID INTEGER NOT NULL,
  Value VARCHAR(16384)
);
CREATE UNIQUE INDEX additional_LinkTypeIDAdd ON additional (LinkType, LinkID, AdditionalFieldID);
CREATE INDEX additional_LinkTypeID ON additional (LinkType, LinkID);

CREATE MEMORY TABLE adoption (
  ID INTEGER NOT NULL PRIMARY KEY,
  AdoptionNumber VARCHAR(255) NOT NULL,
  AnimalID INTEGER NOT NULL,
  OwnerID INTEGER NULL,
  RetailerID INTEGER NULL,
  OriginalRetailerMovementID INTEGER NULL,
  MovementDate TIMESTAMP NULL,
  MovementType INTEGER NOT NULL,
  ReturnDate TIMESTAMP NULL,
  ReturnedReasonID INTEGER NOT NULL,
  InsuranceNumber VARCHAR(50) NULL,
  ReasonForReturn VARCHAR(16384) NULL,
  ReservationDate TIMESTAMP NULL,
  Donation FLOAT NULL,
  ReservationCancelledDate TIMESTAMP NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX adoption_AdoptionNumber ON adoption (AdoptionNumber);
CREATE INDEX adoption_AnimalID ON adoption (AnimalID);
CREATE INDEX adoption_OwnerID ON adoption (OwnerID);
CREATE INDEX adoption_RetailerID ON adoption (RetailerID);

CREATE MEMORY TABLE animal (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalTypeID INTEGER NOT NULL,
  AnimalName VARCHAR(255) NOT NULL,
  NonShelterAnimal INTEGER NOT NULL,
  CrueltyCase INTEGER NOT NULL,
  BaseColourID INTEGER NOT NULL,
  SpeciesID INTEGER NULL,
  BreedID INTEGER NULL,
  Breed2ID INTEGER NULL,
  BreedName VARCHAR(255) NULL,
  CrossBreed INTEGER NULL,
  CoatType INTEGER NULL,
  Markings VARCHAR(16384) NULL,
  ShelterCode VARCHAR(255) NULL,
  ShortCode VARCHAR(255) NULL,
  UniqueCodeID INTEGER NULL,
  YearCodeID INTEGER NULL,
  AcceptanceNumber VARCHAR(255) NULL,
  DateOfBirth TIMESTAMP NOT NULL,
  EstimatedDOB INTEGER NULL,
  AgeGroup VARCHAR(255) NULL,
  DeceasedDate TIMESTAMP NULL,
  Sex INTEGER NOT NULL,
  Identichipped INTEGER NOT NULL,
  IdentichipNumber VARCHAR(255) NULL,
  IdentichipDate TIMESTAMP NULL,
  Tattoo INTEGER NOT NULL,
  TattooNumber VARCHAR(255) NULL,
  TattooDate TIMESTAMP NULL,
  Neutered INTEGER NOT NULL,
  NeuteredDate TIMESTAMP NULL,
  CombiTested INTEGER NOT NULL,
  CombiTestDate TIMESTAMP NULL,
  CombiTestResult INTEGER NOT NULL,
  HeartwormTested INTEGER NOT NULL,
  HeartwormTestDate TIMESTAMP NULL,
  HeartwormTestResult INTEGER NOT NULL,
  FLVResult INTEGER NOT NULL,
  Declawed INTEGER NOT NULL,
  HiddenAnimalDetails VARCHAR(16384) NULL,
  AnimalComments VARCHAR(16384) NULL,
  OwnersVetID INTEGER NOT NULL,
  CurrentVetID INTEGER NOT NULL,
  OriginalOwnerID INTEGER NOT NULL,
  BroughtInByOwnerID INTEGER NOT NULL,
  ReasonForEntry VARCHAR(16384) NULL,
  ReasonNO VARCHAR(16384) NULL,
  DateBroughtIn TIMESTAMP NOT NULL,
  EntryReasonID INTEGER NOT NULL,
  HealthProblems VARCHAR(16384) NULL,
  PutToSleep INTEGER NOT NULL,
  PTSReason VARCHAR(16384) NULL,
  PTSReasonID INTEGER NOT NULL,
  IsDOA INTEGER NOT NULL,
  IsTransfer INTEGER NOT NULL,
  IsGoodWithCats INTEGER NOT NULL,
  IsGoodWithDogs INTEGER NOT NULL,
  IsGoodWithChildren INTEGER NOT NULL,
  IsHouseTrained INTEGER NOT NULL,
  IsNotAvailableForAdoption INTEGER NOT NULL,
  HasSpecialNeeds INTEGER NULL,
  ShelterLocation INTEGER NOT NULL,
  DiedOffShelter INTEGER NOT NULL,
  Size INTEGER NOT NULL,
  RabiesTag VARCHAR(20) NULL,
  Archived INTEGER NOT NULL,
  ActiveMovementID INTEGER NOT NULL,
  ActiveMovementType INTEGER NULL,
  ActiveMovementDate TIMESTAMP NULL,
  ActiveMovementReturn TIMESTAMP NULL,
  HasActiveReserve INTEGER NOT NULL,
  MostRecentEntryDate TIMESTAMP NOT NULL,
  TimeOnShelter VARCHAR(255) NULL,
  DaysOnShelter INTEGER NULL,
  DailyBoardingCost FLOAT NULL,
  AnimalAge VARCHAR(255) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX animal_AnimalShelterCode ON animal (ShelterCode);
CREATE INDEX animal_AnimalTypeID ON animal (AnimalTypeID);
CREATE INDEX animal_AnimalName ON animal (AnimalName);
CREATE INDEX animal_AnimalSpecies ON animal (SpeciesID);
CREATE INDEX animal_Archived ON animal (Archived);
CREATE INDEX animal_ActiveMovementID ON animal (ActiveMovementID);
CREATE INDEX animal_ActiveMovementDate ON animal (ActiveMovementDate);
CREATE INDEX animal_UniqueCodeID ON animal (UniqueCodeID);
CREATE INDEX animal_YearCodeID ON animal (YearCodeID);
CREATE INDEX animal_DateBroughtIn ON animal (DateBroughtIn);

CREATE MEMORY TABLE animalcost (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  CostTypeID INTEGER NOT NULL,
  CostDate TIMESTAMP NOT NULL, 
  CostAmount FLOAT NOT NULL,
  Description VARCHAR(16384) NOT NULL,
  RecordVersion INTEGER NOT NULL, 
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);

CREATE INDEX animalcost_AnimalID ON animalcost (AnimalID);
CREATE INDEX animalcost_CostTypeID ON animalcost (CostTypeID);
CREATE INDEX animalcost_CostDate ON animalcost (CostDate);

CREATE MEMORY TABLE animaldiet (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  DietID INTEGER NOT NULL,
  DateStarted TIMESTAMP NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX animaldiet_AnimalID ON animaldiet (AnimalID);
CREATE INDEX animaldiet_DietID ON animaldiet (DietID);

CREATE MEMORY TABLE animalfound (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalTypeID INTEGER NOT NULL,
  DateReported TIMESTAMP NOT NULL,
  DateFound TIMESTAMP NOT NULL,
  BaseColourID INTEGER NOT NULL,
  DistFeat VARCHAR(16384) NOT NULL,
  AreaFound VARCHAR(255) NOT NULL,
  AreaPostcode VARCHAR(255) NULL,
  OwnerID INTEGER NOT NULL,
  ReturnToOwnerDate TIMESTAMP NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);

CREATE MEMORY TABLE animallitter (
  ID INTEGER NOT NULL PRIMARY KEY,
  ParentAnimalID INTEGER NULL,
  SpeciesID INTEGER NOT NULL,
  Date TIMESTAMP NOT NULL,
  TimeoutMonths INTEGER NOT NULL,
  AcceptanceNumber VARCHAR(255) NULL,
  CachedAnimalsLeft INTEGER NOT NULL,
  InvalidDate TIMESTAMP NULL,
  NumberInLitter INTEGER,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL
);
CREATE INDEX animallitter_ParentAnimalID ON animallitter (ParentAnimalID);

CREATE MEMORY TABLE animallost (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalTypeID INTEGER NOT NULL,
  DateReported TIMESTAMP NOT NULL,
  DateLost TIMESTAMP NOT NULL,
  DateFound TIMESTAMP NULL,
  BaseColourID INTEGER NOT NULL,
  DistFeat VARCHAR(16384) NOT NULL,
  AreaLost VARCHAR(255) NOT NULL,
  AreaPostcode VARCHAR(255) NULL,
  OwnerID INTEGER NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);

CREATE MEMORY TABLE animalmedical (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  MedicalProfileID INTEGER NOT NULL,
  TreatmentName VARCHAR(255) NOT NULL,
  StartDate TIMESTAMP NOT NULL,
  Cost FLOAT NOT NULL,
  Dosage VARCHAR(255) NULL,
  TimingRule INTEGER NOT NULL,
  TimingRuleFrequency INTEGER NOT NULL,
  TimingRuleNoFrequencies INTEGER NOT NULL,
  TreatmentRule INTEGER NOT NULL,
  TotalNumberOfTreatments INTEGER NOT NULL,
  TreatmentsGiven INTEGER NOT NULL,
  TreatmentsRemaining INTEGER NOT NULL,
  Status INTEGER NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX animalmedical_AnimalID ON animalmedical (AnimalID);
CREATE INDEX animalmedical_MedicalProfileID ON animalmedical (MedicalProfileID);

CREATE MEMORY TABLE animalmedicaltreatment (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  AnimalMedicalID INTEGER NOT NULL,
  DateRequired TIMESTAMP NOT NULL,
  DateGiven TIMESTAMP NULL,
  TreatmentNumber INTEGER NOT NULL,
  TotalTreatments INTEGER NOT NULL,
  GivenBy VARCHAR(100) NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX animalmedicaltreatment_AnimalID ON animalmedicaltreatment (AnimalID);
CREATE INDEX animalmedicaltreatment_AnimalMedicalID ON animalmedicaltreatment (AnimalMedicalID);
CREATE INDEX animalmedicaltreatment_DateRequired ON animalmedicaltreatment (DateRequired);

CREATE MEMORY TABLE animalname (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(255) Not Null,
  Sex INTEGER NOT NULL,
  RecordVersion INTEGER NOT NULL
);

CREATE MEMORY TABLE animaltype (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalType VARCHAR(255) NOT NULL,
  AnimalDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE animalvaccination (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  VaccinationID INTEGER NOT NULL,
  DateOfVaccination TIMESTAMP NULL,
  DateRequired TIMESTAMP NOT NULL,
  Cost FLOAT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX animalvaccination_AnimalID ON animalvaccination (AnimalID);

CREATE MEMORY TABLE animalwaitinglist (
  ID INTEGER NOT NULL PRIMARY KEY,
  SpeciesID INTEGER NOT NULL,
  DatePutOnList TIMESTAMP NOT NULL,
  OwnerID INTEGER NOT NULL,
  AnimalDescription VARCHAR(255) NOT NULL,
  ReasonForWantingToPart VARCHAR(16384) NULL,
  CanAffordDonation INTEGER NOT NULL,
  Urgency INTEGER NOT NULL,
  DateRemovedFromList TIMESTAMP NULL,
  AutoRemovePolicy INTEGER,
  DateOfLastOwnerContact TIMESTAMP NULL,
  ReasonForRemoval VARCHAR(16384) NULL,
  Comments VARCHAR(16384) NULL,
  UrgencyUpdateDate TIMESTAMP NULL,
  UrgencyLastUpdatedDate TIMESTAMP NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX animalwaitinglist_SpeciesID ON animalwaitinglist (SpeciesID);
CREATE INDEX animalwaitinglist_Urgency ON animalwaitinglist (Urgency);
CREATE INDEX animalwaitinglist_DatePutOnList ON animalwaitinglist (DatePutOnList);

CREATE MEMORY TABLE audittrail (
  Action INTEGER NOT NULL,
  AuditDate TIMESTAMP NOT NULL,
  UserName VARCHAR(255) NOT NULL,
  TableName VARCHAR(255) NOT NULL,
  Description VARCHAR(16384) NOT NULL
);
CREATE INDEX audittrail_Action ON audittrail (Action);
CREATE INDEX audittrail_AuditDate ON audittrail (AuditDate);
CREATE INDEX audittrail_UserName ON audittrail (UserName);
CREATE INDEX audittrail_TableName ON audittrail (TableName);

CREATE MEMORY TABLE basecolour (
  ID INTEGER NOT NULL PRIMARY KEY,
  BaseColour VARCHAR(255) NOT NULL,
  BaseColourDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE breed (
  ID INTEGER NOT NULL PRIMARY KEY,
  BreedName VARCHAR(255) NOT NULL,
  BreedDescription VARCHAR(255) NULL,
  PetFinderBreed VARCHAR(255) NULL,
  SpeciesID INTEGER NULL
);

CREATE MEMORY TABLE configuration (
  ItemName VARCHAR(255) NOT NULL,
  ItemValue VARCHAR(255) NOT NULL
);

CREATE MEMORY TABLE costtype (
  ID INTEGER NOT NULL PRIMARY KEY, 
  CostTypeName VARCHAR(255) NOT NULL,
  CostTypeDescription VARCHAR(255) NULL
);
  
CREATE MEMORY TABLE customreport (
  ID INTEGER NOT NULL PRIMARY KEY,
  Title VARCHAR(255) NOT NULL,
  SQLCommand LONGVARCHAR NOT NULL,
  HTMLBody LONGVARCHAR NOT NULL,
  Description VARCHAR(16384) NULL,
  OmitHeaderFooter INTEGER NOT NULL,
  OmitCriteria INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL,
  Category VARCHAR(100) NULL
);
CREATE INDEX customreport_Title ON customreport (Title);

CREATE CACHED TABLE dbfs (
  ID INTEGER NOT NULL PRIMARY KEY,
  Path VARCHAR(255) NOT NULL,
  Name VARCHAR(255) NOT NULL,
  Content LONGVARCHAR NULL
);
CREATE INDEX dbfs_Path ON dbfs (Path);
CREATE INDEX dbfs_Name ON dbfs (Name);

CREATE MEMORY TABLE deathreason (
  ID INTEGER NOT NULL PRIMARY KEY,
  ReasonName VARCHAR(255) NOT NULL,
  ReasonDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE diary (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkID INTEGER NOT NULL,
  LinkType INTEGER NOT NULL,
  DiaryDateTime TIMESTAMP NOT NULL,
  DiaryForName VARCHAR(255) NOT NULL,
  Subject VARCHAR(255) NOT NULL,
  Note VARCHAR(16384) NULL,
  DateCompleted TIMESTAMP NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX diary_DiaryForName ON diary (DiaryForName);

CREATE MEMORY TABLE diarytaskdetail (
  ID INTEGER NOT NULL PRIMARY KEY,
  DiaryTaskHeadID INTEGER NOT NULL,
  DayPivot INTEGER NOT NULL,
  WhoFor VARCHAR(50) NOT NULL,
  Subject VARCHAR(255) NULL,
  Note VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL
);
CREATE INDEX diarytaskdetail_DiaryTaskHeadID ON diarytaskdetail (DiaryTaskHeadID);

CREATE MEMORY TABLE diarytaskhead (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(50) NOT NULL,
  RecordType INTEGER NOT NULL,
  RecordVersion INTEGER NOT NULL
);

CREATE MEMORY TABLE diet (
  ID INTEGER NOT NULL PRIMARY KEY,
  DietName VARCHAR(255) NOT NULL,
  DietDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE donationtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  DonationName VARCHAR(255) NOT NULL,
  DonationDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE entryreason (
  ID INTEGER NOT NULL PRIMARY KEY,
  ReasonName VARCHAR(255) NOT NULL,
  ReasonDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE internallocation (
  ID INTEGER NOT NULL PRIMARY KEY,
  LocationName VARCHAR(255) NOT NULL,
  LocationDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE lkcoattype (
  ID INTEGER NOT NULL PRIMARY KEY,
  CoatType VARCHAR(40) NOT NULL
);

CREATE MEMORY TABLE lksex (
  ID INTEGER NOT NULL PRIMARY KEY,
  Sex VARCHAR(40) NOT NULL
);

CREATE MEMORY TABLE lksize (
  ID INTEGER NOT NULL PRIMARY KEY,
  Size VARCHAR(40) NOT NULL
  );

CREATE MEMORY TABLE lksmovementtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  MovementType VARCHAR(40) NOT NULL
  );

CREATE MEMORY TABLE lksfieldlink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
  );

CREATE MEMORY TABLE lksfieldtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  FieldType VARCHAR(40) NOT NULL
  );


CREATE MEMORY TABLE lksmedialink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
  );

CREATE MEMORY TABLE lksdiarylink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
  );

CREATE MEMORY TABLE lksdonationfreq (
  ID INTEGER NOT NULL PRIMARY KEY,
  Frequency VARCHAR(50) NOT NULL
  );

CREATE MEMORY TABLE lksloglink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
);

CREATE MEMORY TABLE lkurgency (
  ID INTEGER NOT NULL PRIMARY KEY,
  Urgency VARCHAR(40) NOT NULL
);

CREATE MEMORY TABLE lksyesno (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);
INSERT INTO lksyesno VALUES (0, 'No');
INSERT INTO lksyesno VALUES (1, 'Yes');

CREATE MEMORY TABLE lksynun (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);

CREATE MEMORY TABLE lksposneg (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);

CREATE MEMORY TABLE log (
  ID INTEGER NOT NULL PRIMARY KEY,
  LogTypeID INTEGER NOT NULL,
  LinkID INTEGER NOT NULL,
  LinkType INTEGER NOT NULL,
  Date TIMESTAMP NOT NULL,
  Comments VARCHAR(65535) NOT NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX log_LogTypeID ON log (LogTypeID);
CREATE INDEX log_LinkID ON log (LinkID);

CREATE MEMORY TABLE logtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  LogTypeName VARCHAR(255) NOT NULL,
  LogTypeDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE media (
  ID INTEGER NOT NULL PRIMARY KEY,
  MediaName VARCHAR(255) NOT NULL,
  MediaNotes VARCHAR(65535) NULL,
  WebsitePhoto INTEGER NOT NULL,
  DocPhoto INTEGER NOT NULL,
  NewSinceLastPublish INTEGER NULL,
  UpdatedSinceLastPublish INTEGER NULL,
  LastPublished datetime NULL,
  LastPublishedPF datetime NULL,
  LastPublishedAP datetime NULL,
  LastPublishedP911 datetime NULL,
  LastPublishedRG datetime NULL,
  LinkID INTEGER,
  LinkTypeID INTEGER,
  Date TIMESTAMP NOT NULL,
  RecordVersion INTEGER NULL
);
CREATE INDEX media_LinkID ON media (LinkID);

CREATE MEMORY TABLE medicalpayment (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalMedicalID INTEGER NOT NULL,
  MedicalPaymentTypeID INTEGER NOT NULL,
  OwnerDonationID INTEGER NOT NULL,
  VetOwnerID INTEGER NOT NULL,
  Amount FLOAT NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX medicalpayment_MedicalPaymentTypeID ON medicalpayment (MedicalPaymentTypeID);
CREATE INDEX medicalpayment_AnimalMedicalID ON medicalpayment (AnimalMedicalID);
CREATE INDEX medicalpayment_OwnerDonationID ON medicalpayment (OwnerDonationID);

CREATE MEMORY TABLE medicalpaymenttype (
  ID INTEGER NOT NULL PRIMARY KEY,
  MedicalPaymentTypeName VARCHAR(255) NOT NULL,
  MedicalPaymentTypeDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE medicalprofile (
  ID INTEGER NOT NULL PRIMARY KEY,
  ProfileName VARCHAR(255) NOT NULL,
  TreatmentName VARCHAR(255) NOT NULL,
  Dosage VARCHAR(255) NULL,
  Cost FLOAT NOT NULL,
  TimingRule INTEGER NOT NULL,
  TimingRuleFrequency INTEGER NOT NULL,
  TimingRuleNoFrequencies INTEGER NOT NULL,
  TreatmentRule INTEGER NOT NULL,
  TotalNumberOfTreatments INTEGER NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);

CREATE MEMORY TABLE owner (
  ID INTEGER NOT NULL PRIMARY KEY,
  OwnerTitle VARCHAR(50) NULL,
  OwnerInitials VARCHAR(50) NULL,
  OwnerForeNames VARCHAR(200) NULL,
  OwnerSurname VARCHAR(100) NULL,
  OwnerName VARCHAR(255) NOT NULL,
  OwnerAddress VARCHAR(255) NULL,
  OwnerTown VARCHAR(100) NULL,
  OwnerCounty VARCHAR(100) NULL,
  OwnerPostcode VARCHAR(255) NULL,
  HomeTelephone VARCHAR(255) NULL,
  WorkTelephone VARCHAR(255) NULL,
  MobileTelephone VARCHAR(255) NULL,
  EmailAddress VARCHAR(200) NULL,
  IDCheck INTEGER NOT NULL,
  Comments VARCHAR(16384) NULL,
  IsBanned INTEGER NOT NULL,
  IsVolunteer INTEGER NOT NULL,
  IsHomeChecker INTEGER NOT NULL,
  IsMember INTEGER NOT NULL,
  MembershipExpiryDate TIMESTAMP NULL,
  MembershipNumber VARCHAR(255) NULL,
  IsDonor INTEGER NOT NULL,
  IsShelter INTEGER NOT NULL,
  IsACO INTEGER NOT NULL,
  IsStaff INTEGER NOT NULL,
  IsFosterer INTEGER NOT NULL,
  IsRetailer INTEGER NOT NULL,
  IsVet INTEGER NOT NULL,
  IsGiftAid INTEGER NOT NULL,
  HomeCheckAreas VARCHAR(16384) NULL,
  DateLastHomeChecked TIMESTAMP NULL,
  HomeCheckedBy INTEGER NULL,
  MatchAdded TIMESTAMP NULL,
  MatchExpires TIMESTAMP NULL,
  MatchActive INTEGER NOT NULL,
  MatchSex INTEGER NOT NULL,
  MatchSize INTEGER NOT NULL,
  MatchAgeFrom FLOAT NOT NULL,
  MatchAgeTo FLOAT NOT NULL,
  MatchAnimalType INTEGER NOT NULL,
  MatchSpecies INTEGER NOT NULL,
  MatchBreed INTEGER NOT NULL,
  MatchBreed2 INTEGER NOT NULL,
  MatchGoodWithCats INTEGER NOT NULL,
  MatchGoodWithDogs INTEGER NOT NULL,
  MatchGoodWithChildren INTEGER NOT NULL,
  MatchHouseTrained INTEGER NOT NULL,
  MatchCommentsContain VARCHAR(255) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX owner_OwnerName ON owner (OwnerName);

CREATE MEMORY TABLE ownerdonation (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  OwnerID INTEGER NOT NULL,
  MovementID INTEGER NOT NULL,
  DonationTypeID INTEGER NOT NULL,
  Date TIMESTAMP NULL,
  DateDue TIMESTAMP NULL,
  Donation FLOAT NOT NULL,
  IsGiftAid INTEGER NOT NULL,
  Frequency INTEGER NOT NULL,
  NextCreated INTEGER NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX ownerdonation_OwnerID ON ownerdonation (OwnerID);
CREATE INDEX ownerdonation_Date ON ownerdonation (Date);

CREATE MEMORY TABLE ownervoucher (
  ID INTEGER NOT NULL PRIMARY KEY,
  OwnerID INTEGER NOT NULL,
  VoucherID INTEGER NOT NULL,
  DateIssued TIMESTAMP NOT NULL,
  DateExpired TIMESTAMP NOT NULL,
  Value FLOAT NOT NULL,
  Comments VARCHAR(16384) NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX ownervoucher_OwnerID ON ownervoucher (OwnerID);
CREATE INDEX ownervoucher_VoucherID ON ownervoucher (VoucherID);
CREATE INDEX ownervoucher_DateExpired ON ownervoucher (DateExpired);

CREATE TABLE primarykey (
  TableName VARCHAR(255) NOT NULL,
  NextID INTEGER NOT NULL
);
CREATE INDEX primarykey_TableName ON primarykey (TableName);

CREATE MEMORY TABLE species (
  ID INTEGER NOT NULL PRIMARY KEY,
  SpeciesName VARCHAR(255) NOT NULL,
  SpeciesDescription VARCHAR(255) NULL,
  PetFinderSpecies VARCHAR(100) NULL
);

CREATE MEMORY TABLE users (
  ID INTEGER NOT NULL PRIMARY KEY,
  UserName VARCHAR(255) NOT NULL,
  RealName VARCHAR(255) NULL,
  Password VARCHAR(255) NOT NULL,
  SuperUser INTEGER NOT NULL,
  OwnerID INTEGER NULL,
  SecurityMap VARCHAR(16384) NOT NULL,
  RecordVersion INTEGER NOT NULL
);
CREATE INDEX users_UserName ON users (UserName);

CREATE MEMORY TABLE voucher (
  ID INTEGER NOT NULL PRIMARY KEY,
  VoucherName VARCHAR(255) NOT NULL,
  VoucherDescription VARCHAR(255) NULL
);

CREATE MEMORY TABLE vaccinationtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  VaccinationType VARCHAR(255) NOT NULL,
  VaccinationDescription VARCHAR(255) NULL
);


INSERT INTO users VALUES (1,'user','Default system user', 'd107d09f5bbe40cade3de5c71e9e9b7',1,0,'', 0);
INSERT INTO users VALUES (2,'guest','Default guest user', '84e0343a0486ff05530df6c705c8bb4',0,0,'', 0);

INSERT INTO configuration VALUES ('DatabaseVersion','2721');
INSERT INTO configuration VALUES ('Organisation', 'Organisation');
INSERT INTO configuration VALUES ('OrganisationAddress', 'Address');
INSERT INTO configuration VALUES ('OrganisationTelephone', 'Telephone');
INSERT INTO configuration VALUES ('UseAutoInsurance','No');
INSERT INTO configuration VALUES ('AutoInsuranceStart','0');
INSERT INTO configuration VALUES ('AutoInsuranceEnd','0');
INSERT INTO configuration VALUES ('AutoInsuranceNext','0');
INSERT INTO configuration VALUES ('RecordSearchLimit', '100');
INSERT INTO configuration VALUES ('AFDefaultSpecies','2');
INSERT INTO configuration VALUES ('AFDefaultType','11');
INSERT INTO configuration VALUES ('AFDefaultLocation', '1');
INSERT INTO configuration VALUES ('AFDefaultEntryReason', '4');
INSERT INTO configuration VALUES ('AFDefaultReturnReason', '4');
INSERT INTO configuration VALUES ('Pets911FTPURL','members.pets911.com');
INSERT INTO configuration VALUES ('RescueGroupsFTPURL','ftp.rescuegroups.org');
INSERT INTO configuration VALUES ('PetFinderFTPURL','members.petfinder.com');
INSERT INTO configuration VALUES ('SaveAPetFTPURL','autoupload.adoptapet.com');
INSERT INTO configuration VALUES ('FTPPort','21');
INSERT INTO configuration VALUES ('DocumentWordProcessor','OpenOffice 3.x');
INSERT INTO configuration VALUES ('WaitingListUrgencyUpdatePeriod','14');
INSERT INTO configuration VALUES ('WaitingListDefaultUrgency', '3');
INSERT INTO configuration VALUES ('UseAnimalRecordLock','Yes');
INSERT INTO configuration VALUES ('WarnNoPendingVacc','Yes');
INSERT INTO configuration VALUES ('WarnNoHomeCheck','Yes');
INSERT INTO configuration VALUES ('WarnBannedOwner','Yes');
INSERT INTO configuration VALUES ('WarnOOPostcode','Yes');
INSERT INTO configuration VALUES ('WarnMultipleReserves','Yes');
INSERT INTO configuration VALUES ('CodingFormat', 'TYYYYNNN');
INSERT INTO configuration VALUES ('ShortCodingFormat', 'NNNT');
INSERT INTO configuration VALUES ('UseShortShelterCodes', 'Yes');
INSERT INTO configuration VALUES ('StrictAutoCodes', 'No');
INSERT INTO configuration VALUES ('AutoDefaultShelterCode', 'Yes');
INSERT INTO configuration VALUES ('WarnACTypeChange','Yes');
INSERT INTO configuration VALUES ('WarnBroughtIn','Yes');
INSERT INTO configuration VALUES ('ShowILOffShelter','Yes');
INSERT INTO configuration VALUES ('HighlightReportAnimals', 'Yes');
INSERT INTO configuration VALUES ('CancelReservesOnAdoption', 'Yes');
INSERT INTO configuration VALUES ('OwnerNameCheck', 'Yes');
INSERT INTO configuration VALUES ('OwnerAddressCheck', 'Yes');
INSERT INTO configuration VALUES ('AutoLoginOSUsers', 'No');
INSERT INTO configuration VALUES ('AutoLitterIdentification', 'Yes');
INSERT INTO configuration VALUES ('DontAutoArchiveOnExit', 'Yes');
INSERT INTO configuration VALUES ('IncomingMediaScaling', '320x200');
INSERT INTO configuration VALUES ('MaxMediaFileSize', '1000');
INSERT INTO configuration VALUES ('AllowDBAutoUpdates', 'Yes');
INSERT INTO configuration VALUES ('AgeGroup1', '0.5');
INSERT INTO configuration VALUES ('AgeGroup1Name', 'Baby');
INSERT INTO configuration VALUES ('AgeGroup2', '2');
INSERT INTO configuration VALUES ('AgeGroup2Name', 'Young Adult');
INSERT INTO configuration VALUES ('AgeGroup3', '7');
INSERT INTO configuration VALUES ('AgeGroup3Name', 'Adult');
INSERT INTO configuration VALUES ('AgeGroup4', '50');
INSERT INTO configuration VALUES ('AgeGroup4Name', 'Senior');
INSERT INTO configuration VALUES ('DefaultDateBroughtIn', 'Yes');
INSERT INTO configuration VALUES ('AutoCancelReservesDays', '14');
INSERT INTO configuration VALUES ('CreateBoardingCostOnAdoption', 'Yes');
INSERT INTO configuration VALUES ('BoardingCostType', '1');
INSERT INTO configuration VALUES ('DefaultDailyBoardingCost', '20');

