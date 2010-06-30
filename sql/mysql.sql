CREATE TABLE accounts (
  ID int(11) NOT NULL,
  Code varchar(255) NOT NULL,
  Description varchar(255) NOT NULL,
  AccountType smallint NOT NULL,
  DonationTypeID int(11) NULL,
  RecordVersion int(11) NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL,
  LastChangedDate datetime NOT NULL,

  PRIMARY KEY (ID),
  KEY IX_AccountsCode(Code)
) Type=MyISAM;

CREATE TABLE accountstrx (
  ID int(11) NOT NULL,
  TrxDate datetime NOT NULL,
  Description varchar(255) NULL,
  Reconciled smallint NOT NULL,
  Amount double NOT NULL,
  SourceAccountID int(11) NOT NULL,
  DestinationAccountID int(11) NOT NULL,
  OwnerDonationID int(11) NULL,
  RecordVersion int(11) NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL,
  LastChangedDate datetime NOT NULL,

  PRIMARY KEY (ID),
  KEY IX_TrxDate(TrxDate),
  KEY IX_TrxSource(SourceAccountID),
  KEY IX_TrxDest(DestinationAccountID)
) Type=MyISAM;

CREATE TABLE lksaccounttype (
  ID int(11) NOT NULL,
  AccountType VARCHAR(255) NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE additionalfield (
  ID int(11) NOT NULL,
  LinkType int(11) NOT NULL,
  FieldName varchar(255) NOT NULL,
  FieldLabel varchar(255) NOT NULL,
  ToolTip varchar(255) NULL,
  LookupValues text NULL,
  FieldType int(11) NOT NULL,
  DisplayIndex int(11) NOT NULL,

  PRIMARY KEY (ID),
  KEY IX_LinkType(LinkType)
) TYPE=MyISAM;

CREATE TABLE additional (
  LinkType int(11) NOT NULL,
  LinkID int(11) NOT NULL,
  AdditionalFieldID int(11) NOT NULL,
  Value text,
  PRIMARY KEY (LinkType, LinkID, AdditionalFieldID),
  KEY IX_LinkTypeID (LinkType, LinkID)
) TYPE=MyISAM;

CREATE TABLE adoption (
  ID int(11) NOT NULL,
  AdoptionNumber varchar(255) NOT NULL,
  AnimalID int(11) NOT NULL,
  OwnerID int(11) NULL,
  RetailerID int(11) NULL,
  OriginalRetailerMovementID int(11) NULL,
  MovementDate datetime NULL,
  MovementType smallint NOT NULL,
  ReturnDate datetime NULL,
  ReturnedReasonID int(11) NOT NULL,
  InsuranceNumber varchar(50) NULL,
  ReasonForReturn text NULL,
  ReservationDate datetime NULL,
  Donation double NULL,
  ReservationCancelledDate datetime NULL,
  Comments text NULL,
  RecordVersion int NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL,
  LastChangedDate datetime NOT NULL,
  
  PRIMARY KEY  (ID),
  UNIQUE KEY IX_AdoptionNo (AdoptionNumber),
  KEY IX_AdoptionAnimalID (AnimalID),
  KEY IX_OwnerID (OwnerID),
  KEY IX_RetailerID (RetailerID),
  KEY IX_MovementDate (MovementDate),
  KEY IX_ReturnDate (ReturnDate)
) TYPE=MyISAM;

CREATE TABLE animal (
  ID int(11) NOT NULL,
  AnimalTypeID int(11) NOT NULL,
  AnimalName varchar(255) NOT NULL,
  NonShelterAnimal tinyint NOT NULL,
  CrueltyCase tinyint NOT NULL,
  BaseColourID int(11) NOT NULL,
  SpeciesID int(11) NULL,
  BreedID int(11) NULL,
  Breed2ID int(11) NULL,
  BreedName varchar(255) NULL,
  CrossBreed tinyint(4) NULL,
  CoatType tinyint(4) NULL,
  Markings text NULL,
  ShelterCode varchar(255) NULL,
  ShortCode varchar(255) NULL,
  UniqueCodeID int(11) NULL,
  YearCodeID int(11) NULL,
  AcceptanceNumber varchar(255) NULL,
  DateOfBirth datetime NOT NULL,
  EstimatedDOB tinyint(4) NULL,
  AgeGroup varchar(255) NULL,
  DeceasedDate datetime NULL,
  Sex tinyint(4) NOT NULL,
  Identichipped tinyint(4) NOT NULL,
  IdentichipNumber varchar(255) NULL,
  IdentichipDate datetime NULL,
  Tattoo tinyint(4) NOT NULL,
  TattooNumber varchar(255) NULL,
  TattooDate datetime NULL,
  Neutered tinyint(4) NOT NULL,
  NeuteredDate datetime NULL,
  CombiTested tinyint(4) NOT NULL,
  CombiTestDate datetime NULL,
  CombiTestResult tinyint(4) NOT NULL,
  HeartwormTested tinyint(4) NOT NULL,
  HeartwormTestDate datetime NULL,
  HeartwormTestResult tinyint(4) NOT NULL,
  FLVResult tinyint(4) NOT NULL,
  Declawed tinyint(4) NOT NULL,
  HiddenAnimalDetails text NULL,
  AnimalComments text NULL,
  OwnersVetID int(11) NOT NULL,
  CurrentVetID int(11) NOT NULL,
  OriginalOwnerID int(11) NOT NULL,
  BroughtInByOwnerID int(11) NOT NULL,
  ReasonForEntry text NULL,
  ReasonNO text NULL,
  DateBroughtIn datetime NOT NULL,
  EntryReasonID int(11) NOT NULL,
  HealthProblems text NULL,
  PutToSleep tinyint(4) NOT NULL,
  PTSReason text NULL,
  PTSReasonID int(11) NOT NULL,
  IsDOA tinyint(4) NOT NULL,
  IsTransfer tinyint(4) NOT NULL,
  IsGoodWithCats tinyint(4) NOT NULL,
  IsGoodWithDogs tinyint(4) NOT NULL,
  IsGoodWithChildren tinyint(4) NOT NULL,
  IsHouseTrained tinyint(4) NOT NULL,
  IsNotAvailableForAdoption tinyint(4) NOT NULL,
  HasSpecialNeeds tinyint(4) NULL,
  ShelterLocation int(11) NULL,
  DiedOffShelter tinyint(4) NOT NULL,
  Size tinyint(4) NOT NULL,
  RabiesTag varchar(20) NULL,
  Archived tinyint(4) NOT NULL,
  ActiveMovementID int(11) NOT NULL,
  ActiveMovementType smallint NULL,
  ActiveMovementDate datetime NULL,
  ActiveMovementReturn datetime NULL,
  HasActiveReserve tinyint NOT NULL,
  MostRecentEntryDate datetime NOT NULL,
  TimeOnShelter varchar(255) NULL,
  DaysOnShelter int(11) NULL,
  DailyBoardingCost double NULL,
  AnimalAge varchar(255) NULL,
  RecordVersion int NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  UNIQUE KEY IX_AnimalShelterCode (ShelterCode),
  KEY IX_AnimalAnimalTypeID (AnimalTypeID),
  FULLTEXT KEY IX_AnimalAnimalName (AnimalName),
  KEY IX_AnimalAnimalSpecies (SpeciesID),
  KEY IX_AnimalDateBroughtIn (DateBroughtIn),
  KEY IX_AnimalActiveMovementID (ActiveMovementID),
  KEY IX_AnimalActiveMovementDate (ActiveMovementDate),
  KEY IX_AnimalArchived (Archived),
  KEY IX_AnimalMostRecentEntryDate (MostRecentEntryDate),
  KEY IX_AnimalUniqueCodeID (UniqueCodeID),
  KEY IX_AnimalYearCodeID (YearCodeID)
) TYPE=MyISAM;

CREATE TABLE animalcost (
  ID int(11) NOT NULL,
  AnimalID int(11) NOT NULL,
  CostTypeID int(11) NOT NULL,
  CostDate TIMESTAMP NOT NULL, 
  CostAmount double NOT NULL,
  Description TEXT NULL,
  RecordVersion int NOT NULL, 
  CreatedBy varchar(255) NOT NULL,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_animalcost_AnimalID (AnimalID),
  KEY IX_animalcost_CostTypeID (CostTypeID),
  KEY IX_animalcost_CostDate (CostDate)
) TYPE=MyISAM;

CREATE TABLE animaldiet (
  ID int(11) NOT NULL,
  AnimalID int(11) NOT NULL ,
  DietID int(11) NOT NULL ,
  DateStarted datetime NOT NULL, 
  Comments TEXT NULL ,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_AnimalID (AnimalID),
  KEY IX_DietID (DietID)
) TYPE=MyISAM;

CREATE TABLE animalfound (
  ID int(11) NOT NULL,
  AnimalTypeID int(11) NOT NULL,
  DateReported datetime NOT NULL,
  DateFound datetime NOT NULL,
  BaseColourID int(11) NOT NULL ,
  DistFeat text NOT NULL ,
  AreaFound varchar(255) NOT NULL ,
  AreaPostcode varchar(255) NULL,
  OwnerID int(11) NOT NULL ,
  ReturnToOwnerDate datetime NULL,
  Comments TEXT NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE animallitter (
  ID int(11) NOT NULL ,
  ParentAnimalID int(11) NULL ,
  SpeciesID int(11) NOT NULL ,
  Date datetime NOT NULL,
  TimeoutMonths tinyint(4) NOT NULL,
  AcceptanceNumber varchar(255) NULL,
  CachedAnimalsLeft int NOT NULL ,
  InvalidDate datetime NULL,
  NumberInLitter int(11) ,
  Comments text NULL ,
  RecordVersion int NOT NULL ,
  PRIMARY KEY  (ID),
  KEY IX_ParentAnimalID (ParentAnimalID)
) TYPE=MyISAM;

CREATE TABLE animallost (
  ID int(11) NOT NULL ,
  AnimalTypeID int(11) NOT NULL ,
  DateReported datetime NOT NULL,
  DateLost datetime NOT NULL,
  DateFound datetime NULL,
  BaseColourID int(11) NOT NULL ,
  DistFeat text NOT NULL ,
  AreaLost varchar(255) NOT NULL ,
  AreaPostcode varchar(255) NULL,
  OwnerID int(11) NOT NULL ,
  Comments TEXT NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE animalmedical (
  ID int(11) NOT NULL ,
  AnimalID int(11) NOT NULL ,
  MedicalProfileID int(11) NOT NULL ,
  TreatmentName varchar(255) NOT NULL ,
  StartDate datetime NOT NULL,
  Dosage varchar(255) NULL,
  Cost double NOT NULL,
  TimingRule tinyint NOT NULL ,
  TimingRuleFrequency smallint NOT NULL ,
  TimingRuleNoFrequencies smallint NOT NULL ,
  TreatmentRule tinyint NOT NULL ,
  TotalNumberOfTreatments smallint NOT NULL ,
  TreatmentsGiven smallint NOT NULL ,
  TreatmentsRemaining smallint NOT NULL ,
  Status smallint NOT NULL ,
  Comments TEXT NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_AnimalID (AnimalID),
  KEY IX_MedicalProfileID (MedicalProfileID)
) TYPE=MyISAM;

CREATE TABLE animalmedicaltreatment (
  ID int(11) NOT NULL ,
  AnimalID int(11) NOT NULL ,
  AnimalMedicalID int(11) NOT NULL ,
  DateRequired datetime NOT NULL,
  DateGiven datetime NULL,
  TreatmentNumber int(11) NOT NULL,
  TotalTreatments int(11) NOT NULL,
  GivenBy varchar(100) NOT NULL,
  Comments TEXT NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_AnimalID (AnimalID),
  KEY IX_AnimalMedicalID (AnimalMedicalID),
  KEY IX_DateRequired (DateRequired)
) TYPE=MyISAM;

CREATE TABLE animalname (
  ID int(11) NOT NULL ,
  Name varchar(255) Not Null ,
  Sex tinyint(4) NOT NULL ,
  RecordVersion int NOT NULL ,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE animaltype (
  ID int(11) NOT NULL ,
  AnimalType varchar(255) NOT NULL ,
  AnimalDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE animalvaccination (
  ID int(11) NOT NULL ,
  AnimalID int(11) NOT NULL ,
  VaccinationID int(11) NOT NULL ,
  DateOfVaccination datetime NULL,
  DateRequired datetime NOT NULL,
  Cost double NULL,
  Comments text NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_AnimalAnimalVaccination (AnimalID)
) TYPE=MyISAM;

CREATE TABLE animalwaitinglist (
  ID int(11) NOT NULL ,
  SpeciesID int(11) NOT NULL ,
  DatePutOnList datetime NOT NULL,
  OwnerID int(11) NOT NULL ,
  AnimalDescription varchar(255) NOT NULL ,
  ReasonForWantingToPart text NULL,
  CanAffordDonation tinyint(4) NOT NULL ,
  Urgency tinyint(4) NOT NULL ,
  DateRemovedFromList datetime NULL,
  AutoRemovePolicy int(11) ,
  DateOfLastOwnerContact datetime NULL,
  ReasonForRemoval text NULL,
  Comments TEXT NULL,
  UrgencyUpdateDate datetime NULL,
  UrgencyLastUpdatedDate datetime NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_SpeciesID (SpeciesID),
  KEY IX_Urgency (Urgency),
  KEY IX_DatePutOnList (DatePutOnList)
) TYPE=MyISAM;

CREATE TABLE audittrail (
  Action int(11) NOT NULL,
  AuditDate datetime NOT NULL,
  UserName varchar(255) NOT NULL,
  TableName varchar(255) NOT NULL,
  Description varchar(16384) NOT NULL,
  KEY IX_AuditTrailAction (Action),
  KEY IX_AuditTrailAuditDate (AuditDate),
  KEY IX_AuditTrailUserName (UserName),
  KEY IX_AuditTrailTableName (TableName)
);

CREATE TABLE basecolour (
  ID int(11) NOT NULL ,
  BaseColour varchar(255) NOT NULL ,
  BaseColourDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE breed (
  ID int(11) NOT NULL ,
  BreedName varchar(255) NOT NULL ,
  BreedDescription varchar(255) NULL,
  PetFinderBreed varchar(255) NULL,
  SpeciesID int(11) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE configuration (
  ItemName varchar(255) NOT NULL ,
  ItemValue varchar(255) NOT NULL 
) TYPE=MyISAM;

CREATE TABLE costtype (
  ID int(11) NOT NULL,
  CostTypeName varchar(255) NOT NULL,
  CostTypeDescription varchar(255) NULL,
  PRIMARY KEY (ID)
) Type=MyISAM;
  
CREATE TABLE customreport (
  ID int(11) NOT NULL,
  Title varchar(255) NOT NULL,
  SQLCommand text NOT NULL,
  HTMLBody text NOT NULL,
  Description text NULL,
  OmitHeaderFooter tinyint NOT NULL,
  OmitCriteria tinyint NOT NULL,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  Category varchar(100) NULL,
  PRIMARY KEY  (ID),
  KEY IX_Title (Title)
) TYPE=MyISAM;

CREATE TABLE dbfs (
  ID int(11) NOT NULL ,
  Path varchar(255) NOT NULL,
  Name varchar(255) NOT NULL,
  Content LONGTEXT NULL,
  PRIMARY KEY  (ID),
  KEY IX_Path (Path),
  KEY IX_Name (Name)
) TYPE=MyISAM;

CREATE TABLE deathreason (
  ID int(11) NOT NULL ,
  ReasonName varchar(255) NOT NULL ,
  ReasonDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE diary (
  ID int(11) NOT NULL ,
  LinkID int(11) NOT NULL ,
  LinkType tinyint(4) NOT NULL ,
  DiaryDateTime datetime NOT NULL,
  DiaryForName varchar(255) NOT NULL ,
  Subject varchar(255) NOT NULL ,
  Note text NULL,
  DateCompleted datetime NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_DiaryDiaryForName (DiaryForName)
) TYPE=MyISAM;

CREATE TABLE diarytaskdetail (
  ID int(11) NOT NULL ,
  DiaryTaskHeadID int(11) NOT NULL ,
  DayPivot int(11) NOT NULL ,
  WhoFor varchar(50) NOT NULL ,
  Subject varchar(255) NULL,
  Note text NULL,
  RecordVersion int NOT NULL ,
  PRIMARY KEY  (ID),
  KEY IX_DiaryTaskHeadID (DiaryTaskHeadID)
) TYPE=MyISAM;

CREATE TABLE diarytaskhead (
  ID int(11) NOT NULL ,
  Name varchar(50) NOT NULL ,
  RecordType smallint NOT NULL ,
  RecordVersion int NOT NULL ,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE diet (
  ID int(11) NOT NULL ,
  DietName varchar(255) NOT NULL ,
  DietDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE donationtype (
  ID int(11) NOT NULL ,
  DonationName varchar(255) NOT NULL ,
  DonationDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE entryreason (
  ID int(11) NOT NULL ,
  ReasonName varchar(255) NOT NULL ,
  ReasonDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE internallocation (
  ID int(11) NOT NULL ,
  LocationName varchar(255) NOT NULL ,
  LocationDescription varchar(255) NULL
) TYPE=MyISAM;

CREATE TABLE lkcoattype (
  ID int(11) NOT NULL DEFAULT '0',
  CoatType VARCHAR(40) NOT NULL,
  PRIMARY KEY (ID)
  ) Type=MyISAM;

CREATE TABLE lksex (
  ID smallint NOT NULL DEFAULT '0',
  Sex varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksize (
  ID smallint NOT NULL DEFAULT '0',
  Size varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksmovementtype (
  ID smallint NOT NULL DEFAULT '0',
  MovementType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksfieldlink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksfieldtype (
  ID smallint NOT NULL DEFAULT '0',
  FieldType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksmedialink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksdiarylink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksdonationfreq (
  ID smallint NOT NULL DEFAULT '0',
  Frequency varchar(50) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksloglink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
) Type=MyISAM;

CREATE TABLE lkurgency (
  ID smallint NOT NULL DEFAULT '0',
  Urgency varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

CREATE TABLE lksyesno (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);

CREATE TABLE lksynun (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);

CREATE TABLE lksposneg (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);

CREATE TABLE log (
  ID int(11) NOT NULL ,
  LogTypeID int(11) NOT NULL ,
  LinkID int(11) NOT NULL ,
  LinkType smallint NOT NULL ,
  Date datetime NOT NULL,
  Comments TEXT NOT NULL ,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_LogTypeID (LogTypeID),
  KEY IX_LinkID (LinkID)
) TYPE=MyISAM;

CREATE TABLE logtype (
  ID int(11) NOT NULL ,
  LogTypeName varchar(255) NOT NULL ,
  LogTypeDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE media (
  ID int(11) NOT NULL ,
  MediaName varchar(255) NOT NULL ,
  MediaNotes text NULL,
  WebsitePhoto tinyint(4) NOT NULL ,
  DocPhoto tinyint(4) NOT NULL ,
  NewSinceLastPublish tinyint(4) NULL,
  UpdatedSinceLastPublish tinyint(4) NULL,
  LastPublished timestamp NULL,
  LastPublishedPF timestamp NULL,
  LastPublishedAP timestamp NULL,
  LastPublishedP911 timestamp NULL,
  LastPublishedRG timestamp NULL,
  LinkID int(11) ,
  LinkTypeID tinyint(4) ,
  Date datetime NOT NULL,
  RecordVersion int(11) NOT NULL ,
  PRIMARY KEY  (ID),
  KEY IX_MediaLinkID (LinkID)
) TYPE=MyISAM;

CREATE TABLE medicalpayment (
  ID int(11) NOT NULL ,
  AnimalMedicalID int(11) NOT NULL ,
  MedicalPaymentTypeID int(11) NOT NULL ,
  OwnerDonationID int(11) NOT NULL ,
  VetOwnerID int(11) NOT NULL ,
  Amount double NOT NULL ,
  Comments TEXT NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_MedicalPaymentTypeID (MedicalPaymentTypeID),
  KEY IX_AnimalMedicalID (AnimalMedicalID),
  KEY IX_OwnerDonationID (OwnerDonationID)
) TYPE=MyISAM;

CREATE TABLE medicalpaymenttype (
  ID int(11) NOT NULL ,
  MedicalPaymentTypeName varchar(255) NOT NULL,
  MedicalPaymentTypeDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE medicalprofile (
  ID int(11) NOT NULL ,
  ProfileName varchar(255) NOT NULL ,
  TreatmentName varchar(255) NOT NULL ,
  Dosage varchar(255) NULL,
  Cost double NOT NULL,
  TimingRule tinyint NOT NULL ,
  TimingRuleFrequency smallint NOT NULL ,
  TimingRuleNoFrequencies smallint NOT NULL ,
  TreatmentRule tinyint NOT NULL ,
  TotalNumberOfTreatments smallint NOT NULL ,
  Comments TEXT NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE owner (
  ID int(11) NOT NULL ,
  OwnerTitle varchar(50) NULL,
  OwnerInitials varchar(50) NULL,
  OwnerForeNames varchar(200) NULL,
  OwnerSurname varchar(100) NULL,
  OwnerName varchar(255) NOT NULL ,
  OwnerAddress varchar(255) NULL,
  OwnerTown varchar(100) NULL,
  OwnerCounty varchar(100) NULL,
  OwnerPostcode varchar(255) NULL,
  HomeTelephone varchar(255) NULL,
  WorkTelephone varchar(255) NULL,
  MobileTelephone varchar(255) NULL,
  EmailAddress varchar(200) NULL,
  IDCheck tinyint(4) NOT NULL ,
  Comments text NULL,
  IsBanned tinyint(4) NOT NULL ,
  IsVolunteer tinyint(4) NOT NULL ,
  IsHomeChecker tinyint(4) NOT NULL ,
  IsMember tinyint(4) NOT NULL ,
  MembershipExpiryDate datetime NULL,
  MembershipNumber varchar(255) NULL,
  IsDonor tinyint(4) NOT NULL ,
  IsShelter tinyint(4) NOT NULL ,
  IsACO tinyint(4) NOT NULL ,
  IsStaff tinyint(4) NOT NULL ,
  IsFosterer tinyint(4) NOT NULL ,
  IsRetailer tinyint(4) NOT NULL ,
  IsVet tinyint(4) NOT NULL ,
  IsGiftAid tinyint(4) NOT NULL,
  HomeCheckAreas text NULL,
  DateLastHomeChecked datetime NULL,
  HomeCheckedBy int(11) NULL,
  MatchAdded datetime NULL,
  MatchExpires datetime NULL,
  MatchActive tinyint(4) NOT NULL ,
  MatchSex tinyint(4) NOT NULL ,
  MatchSize tinyint(4) NOT NULL ,
  MatchAgeFrom double NOT NULL ,
  MatchAgeTo double NOT NULL ,
  MatchAnimalType int(11) NOT NULL,
  MatchSpecies int(11) NOT NULL,
  MatchBreed int(11) NOT NULL,
  MatchBreed2 int(11) NOT NULL,
  MatchGoodWithCats tinyint(4) NOT NULL ,
  MatchGoodWithDogs tinyint(4) NOT NULL ,
  MatchGoodWithChildren tinyint(4) NOT NULL ,
  MatchHouseTrained tinyint(4) NOT NULL ,
  MatchCommentsContain varchar(255) NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  FULLTEXT KEY IX_OwnerOwnerName (OwnerName)
) TYPE=MyISAM;

CREATE TABLE ownerdonation (
  ID int(11) NOT NULL ,
  AnimalID int(11) NOT NULL ,
  OwnerID int(11) NOT NULL ,
  MovementID int(11) NOT NULL ,
  DonationTypeID int(11) NOT NULL ,
  Date datetime NULL,
  DateDue datetime NULL,
  Donation double NOT NULL,
  IsGiftAid tinyint(4) NOT NULL,
  Frequency smallint NOT NULL,
  NextCreated tinyint(4) NOT NULL,
  Comments text NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_OwnerID (OwnerID),
  KEY IX_Date (Date)
) TYPE=MyISAM;

CREATE TABLE ownervoucher (
  ID int(11) NOT NULL ,
  OwnerID int(11) NOT NULL ,
  VoucherID int(11) NOT NULL ,
  DateIssued datetime NOT NULL,
  DateExpired datetime NOT NULL,
  Value double NOT NULL ,
  Comments text NULL,
  RecordVersion int NOT NULL ,
  CreatedBy varchar(255) NOT NULL ,
  CreatedDate datetime NOT NULL,
  LastChangedBy varchar(255) NOT NULL ,
  LastChangedDate datetime NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_OwnerID (OwnerID),
  KEY IX_VoucherID (VoucherID),
  KEY IX_DateExpired (DateExpired)
) TYPE=MyISAM;

CREATE TABLE primarykey (
  TableName varchar(255) NOT NULL ,
  NextID int(11) NOT NULL ,
  KEY IX_PrimaryKeyTableName (TableName)
) TYPE=MyISAM;

CREATE TABLE species (
  ID int(11) NOT NULL ,
  SpeciesName varchar(255) NOT NULL ,
  SpeciesDescription varchar(255) NULL,
  PetFinderSpecies varchar(100) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE users (
  ID int(11) NOT NULL,
  UserName varchar(255) NOT NULL,
  RealName varchar(255) NULL,
  Password varchar(255) NOT NULL,
  SuperUser tinyint(4) NOT NULL,
  OwnerID int(11) NULL,
  SecurityMap text NOT NULL,
  RecordVersion int NOT NULL,
  PRIMARY KEY  (ID),
  KEY IX_UsersUserName (UserName)
) TYPE=MyISAM;

CREATE TABLE voucher (
  ID int(11) NOT NULL ,
  VoucherName varchar(255) NOT NULL ,
  VoucherDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

CREATE TABLE vaccinationtype (
  ID int(11) NOT NULL ,
  VaccinationType varchar(255) NOT NULL ,
  VaccinationDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

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

