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
INSERT INTO lksaccounttype VALUES (1, 'Bank');
INSERT INTO lksaccounttype VALUES (2, 'Credit Card');
INSERT INTO lksaccounttype VALUES (3, 'Loan');
INSERT INTO lksaccounttype VALUES (4, 'Expense');
INSERT INTO lksaccounttype VALUES (5, 'Income');
INSERT INTO lksaccounttype VALUES (6, 'Pension');
INSERT INTO lksaccounttype VALUES (7, 'Shares');
INSERT INTO lksaccounttype VALUES (8, 'Asset');
INSERT INTO lksaccounttype VALUES (9, 'Liability');


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
  Description text NOT NULL,
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





INSERT INTO animaltype VALUES (2,'D (Dog)',NULL);
INSERT INTO animaltype VALUES (10, 'F (Stray Dog)', NULL);
INSERT INTO animaltype VALUES (11,'U (Unwanted Cat)',NULL);
INSERT INTO animaltype VALUES (12,'S (Stray Cat)',NULL);
INSERT INTO animaltype VALUES (13,'M (Miscellaneous)',NULL);
INSERT INTO animaltype VALUES (40, 'N (Non Shelter Animal)', NULL);






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





CREATE TABLE basecolour (
  ID int(11) NOT NULL ,
  BaseColour varchar(255) NOT NULL ,
  BaseColourDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;





INSERT INTO basecolour VALUES (1,'Black',NULL);
INSERT INTO basecolour VALUES (2,'White',NULL);
INSERT INTO basecolour VALUES (3,'Black and White',NULL);
INSERT INTO basecolour VALUES (4,'Ginger',NULL);
INSERT INTO basecolour VALUES (5,'White and Black',NULL);
INSERT INTO basecolour VALUES (6,'Torti',NULL);
INSERT INTO basecolour VALUES (7,'Tabby',NULL);
INSERT INTO basecolour VALUES (8,'Tan',NULL);
INSERT INTO basecolour VALUES (9,'Black and Tan',NULL);
INSERT INTO basecolour VALUES (10,'Tan and Black',NULL);
INSERT INTO basecolour VALUES (11,'Brown',NULL);
INSERT INTO basecolour VALUES (12,'Brown and Black',NULL);
INSERT INTO basecolour VALUES (13,'Black and Brown',NULL);
INSERT INTO basecolour VALUES (14,'Brindle',NULL);
INSERT INTO basecolour VALUES (15,'Brindle and Black',NULL);
INSERT INTO basecolour VALUES (16,'Brindle and White',NULL);
INSERT INTO basecolour VALUES (17,'Black and Brindle',NULL);
INSERT INTO basecolour VALUES (18,'White and Brindle',NULL);
INSERT INTO basecolour VALUES (19,'Tricolour',NULL);
INSERT INTO basecolour VALUES (20,'Liver',NULL);
INSERT INTO basecolour VALUES (21,'Liver and White',NULL);
INSERT INTO basecolour VALUES (22,'White and Liver',NULL);
INSERT INTO basecolour VALUES (23,'Cream',NULL);
INSERT INTO basecolour VALUES (24,'Tan and White',NULL);
INSERT INTO basecolour VALUES (26,'White and Tan',NULL);
INSERT INTO basecolour VALUES (27,'Torti and White',NULL);
INSERT INTO basecolour VALUES (28,'Tabby and White',NULL);
INSERT INTO basecolour VALUES (29,'Ginger and White',NULL);
INSERT INTO basecolour VALUES (30,'Grey',NULL);
INSERT INTO basecolour VALUES (31,'Grey and White',NULL);
INSERT INTO basecolour VALUES (32,'White and Grey',NULL);
INSERT INTO basecolour VALUES (33,'White and Torti',NULL);
INSERT INTO basecolour VALUES (35,'Brown and White',NULL);
INSERT INTO basecolour VALUES (36,'Blue','');
INSERT INTO basecolour VALUES (37,'White and Tabby','');
INSERT INTO basecolour VALUES (38,'Yellow and Grey','');
INSERT INTO basecolour VALUES (39,'Various',NULL);
INSERT INTO basecolour VALUES (40,'White and Brown','');
INSERT INTO basecolour VALUES (41,'Green','');





CREATE TABLE breed (
  ID int(11) NOT NULL ,
  BreedName varchar(255) NOT NULL ,
  BreedDescription varchar(255) NULL,
  PetFinderBreed varchar(255) NULL,
  SpeciesID int(11) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;


INSERT INTO breed VALUES (1,'Affenpinscher','','Affenpinscher', 1);
INSERT INTO breed VALUES (2,'Afghan Hound','','Afghan Hound', 1);
INSERT INTO breed VALUES (3,'Airedale Terrier','','Airedale Terrier', 1);
INSERT INTO breed VALUES (4,'Akbash','','Akbash', 1);
INSERT INTO breed VALUES (5,'Akita','','Akita', 1);
INSERT INTO breed VALUES (6,'Alaskan Malamute','','Alaskan Malamute', 1);
INSERT INTO breed VALUES (7,'American Bulldog','','American Bulldog', 1);
INSERT INTO breed VALUES (8,'American Eskimo Dog','','American Eskimo Dog', 1);
INSERT INTO breed VALUES (9,'American Staffordshire Terrier','','American Staffordshire Terrier', 1);
INSERT INTO breed VALUES (10,'American Water Spaniel','','American Water Spaniel', 1);
INSERT INTO breed VALUES (11,'Anatolian Shepherd','','Anatolian Shepherd', 1);
INSERT INTO breed VALUES (12,'Appenzell Mountain Dog','','Appenzell Mountain Dog', 1);
INSERT INTO breed VALUES (13,'Australian Cattle Dog/Blue Heeler','','Australian Cattle Dog/Blue Heeler', 1);
INSERT INTO breed VALUES (14,'Australian Kelpie','','Australian Kelpie', 1);
INSERT INTO breed VALUES (15,'Australian Shepherd','','Australian Shepherd', 1);
INSERT INTO breed VALUES (16,'Australian Terrier','','Australian Terrier', 1);
INSERT INTO breed VALUES (17,'Basenji','','Basenji', 1);
INSERT INTO breed VALUES (18,'Basset Hound','','Basset Hound', 1);
INSERT INTO breed VALUES (19,'Beagle','','Beagle', 1);
INSERT INTO breed VALUES (20,'Bearded Collie','','Bearded Collie', 1);
INSERT INTO breed VALUES (21,'Beauceron','','Beauceron', 1);
INSERT INTO breed VALUES (22,'Bedlington Terrier','','Bedlington Terrier', 1);
INSERT INTO breed VALUES (23,'Belgian Shepherd Dog Sheepdog','','Belgian Shepherd Dog Sheepdog', 1);
INSERT INTO breed VALUES (24,'Belgian Shepherd Laekenois','','Belgian Shepherd Laekenois', 1);
INSERT INTO breed VALUES (25,'Belgian Shepherd Malinois','','Belgian Shepherd Malinois', 1);
INSERT INTO breed VALUES (26,'Belgian Shepherd Tervuren','','Belgian Shepherd Tervuren', 1);
INSERT INTO breed VALUES (27,'Bernese Mountain Dog','','Bernese Mountain Dog', 1);
INSERT INTO breed VALUES (28,'Bichon Frise','','Bichon Frise', 1);
INSERT INTO breed VALUES (29,'Black and Tan Coonhound','','Black and Tan Coonhound', 1);
INSERT INTO breed VALUES (30,'Black Labrador Retriever','','Black Labrador Retriever', 1);
INSERT INTO breed VALUES (31,'Black Mouth Cur','','Black Mouth Cur', 1);
INSERT INTO breed VALUES (32,'Bloodhound','','Bloodhound', 1);
INSERT INTO breed VALUES (33,'Bluetick Coonhound','','Bluetick Coonhound', 1);
INSERT INTO breed VALUES (34,'Border Collie','','Border Collie', 1);
INSERT INTO breed VALUES (35,'Border Terrier','','Border Terrier', 1);
INSERT INTO breed VALUES (36,'Borzoi','','Borzoi', 1);
INSERT INTO breed VALUES (37,'Boston Terrier','','Boston Terrier', 1);
INSERT INTO breed VALUES (38,'Bouvier des Flanders','','Bouvier des Flanders', 1);
INSERT INTO breed VALUES (39,'Boykin Spaniel','','Boykin Spaniel', 1);
INSERT INTO breed VALUES (40,'Boxer','','Boxer', 1);
INSERT INTO breed VALUES (41,'Briard','','Briard', 1);
INSERT INTO breed VALUES (42,'Brittany Spaniel','','Brittany Spaniel', 1);
INSERT INTO breed VALUES (43,'Brussels Griffon','','Brussels Griffon', 1);
INSERT INTO breed VALUES (44,'Bull Terrier','','Bull Terrier', 1);
INSERT INTO breed VALUES (45,'Bullmastiff','','Bullmastiff', 1);
INSERT INTO breed VALUES (46,'Cairn Terrier','','Cairn Terrier', 1);
INSERT INTO breed VALUES (47,'Canaan Dog','','Canaan Dog', 1);
INSERT INTO breed VALUES (48,'Cane Corso Mastiff','','Cane Corso Mastiff', 1);
INSERT INTO breed VALUES (49,'Carolina Dog','','Carolina Dog', 1);
INSERT INTO breed VALUES (50,'Catahoula Leopard Dog','','Catahoula Leopard Dog', 1);
INSERT INTO breed VALUES (51,'Cattle Dog','','Cattle Dog', 1);
INSERT INTO breed VALUES (52,'Cavalier King Charles Spaniel','','Cavalier King Charles Spaniel', 1);
INSERT INTO breed VALUES (53,'Chesapeake Bay Retriever','','Chesapeake Bay Retriever', 1);
INSERT INTO breed VALUES (54,'Chihuahua','','Chihuahua', 1);
INSERT INTO breed VALUES (55,'Chinese Crested Dog','','Chinese Crested Dog', 1);
INSERT INTO breed VALUES (56,'Chinese Foo Dog','','Chinese Foo Dog', 1);
INSERT INTO breed VALUES (57,'Chocolate Labrador Retriever','','Chocolate Labrador Retriever', 1);
INSERT INTO breed VALUES (58,'Chow Chow','','Chow Chow', 1);
INSERT INTO breed VALUES (59,'Clumber Spaniel','','Clumber Spaniel', 1);
INSERT INTO breed VALUES (60,'Cockapoo','','Cockapoo', 1);
INSERT INTO breed VALUES (61,'Cocker Spaniel','','Cocker Spaniel', 1);
INSERT INTO breed VALUES (62,'Collie','','Collie', 1);
INSERT INTO breed VALUES (63,'Coonhound','','Coonhound', 1);
INSERT INTO breed VALUES (64,'Corgi','','Corgi', 1);
INSERT INTO breed VALUES (65,'Coton de Tulear','','Coton de Tulear', 1);
INSERT INTO breed VALUES (66,'Dachshund','','Dachshund', 1);
INSERT INTO breed VALUES (67,'Dalmatian','','Dalmatian', 1);
INSERT INTO breed VALUES (68,'Dandi Dinmont Terrier','','Dandi Dinmont Terrier', 1);
INSERT INTO breed VALUES (69,'Doberman Pinscher','','Doberman Pinscher', 1);
INSERT INTO breed VALUES (70,'Dogo Argentino','','Dogo Argentino', 1);
INSERT INTO breed VALUES (71,'Dogue de Bordeaux','','Dogue de Bordeaux', 1);
INSERT INTO breed VALUES (72,'Dutch Shepherd','','Dutch Shepherd', 1);
INSERT INTO breed VALUES (73,'English Bulldog','','English Bulldog', 1);
INSERT INTO breed VALUES (74,'English Cocker Spaniel','','English Cocker Spaniel', 1);
INSERT INTO breed VALUES (75,'English Coonhound','','English Coonhound', 1);
INSERT INTO breed VALUES (76,'English Pointer','','English Pointer', 1);
INSERT INTO breed VALUES (77,'English Setter','','English Setter', 1);
INSERT INTO breed VALUES (78,'English Shepherd','','English Shepherd', 1);
INSERT INTO breed VALUES (79,'English Springer Spaniel','','English Springer Spaniel', 1);
INSERT INTO breed VALUES (80,'English Toy Spaniel','','English Toy Spaniel', 1);
INSERT INTO breed VALUES (81,'Entlebucher','','Entlebucher', 1);
INSERT INTO breed VALUES (82,'Eskimo Dog','','Eskimo Dog', 1);
INSERT INTO breed VALUES (83,'Field Spaniel','','Field Spaniel', 1);
INSERT INTO breed VALUES (84,'Fila Brasileiro','','Fila Brasileiro', 1);
INSERT INTO breed VALUES (85,'Finnish Lapphund','','Finnish Lapphund', 1);
INSERT INTO breed VALUES (86,'Finnish Spitz','','Finnish Spitz', 1);
INSERT INTO breed VALUES (87,'Flat-coated Retriever','','Flat-coated Retriever', 1);
INSERT INTO breed VALUES (88,'Fox Terrier','','Fox Terrier', 1);
INSERT INTO breed VALUES (89,'Foxhound','','Foxhound', 1);
INSERT INTO breed VALUES (90,'French Bulldog','','French Bulldog', 1);
INSERT INTO breed VALUES (91,'German Pinscher','','German Pinscher', 1);
INSERT INTO breed VALUES (92,'German Shepherd Dog','','German Shepherd Dog', 1);
INSERT INTO breed VALUES (93,'German Shorthaired Pointer','','German Shorthaired Pointer', 1);
INSERT INTO breed VALUES (94,'German Wirehaired Pointer','','German Wirehaired Pointer', 1);
INSERT INTO breed VALUES (95,'Glen of Imaal Terrier','','Glen of Imaal Terrier', 1);
INSERT INTO breed VALUES (96,'Golden Retriever','','Golden Retriever', 1);
INSERT INTO breed VALUES (97,'Gordon Setter','','Gordon Setter', 1);
INSERT INTO breed VALUES (98,'Great Dane','','Great Dane', 1);
INSERT INTO breed VALUES (99,'Great Pyrenees','','Great Pyrenees', 1);
INSERT INTO breed VALUES (100,'Greater Swiss Mountain Dog','','Greater Swiss Mountain Dog', 1);
INSERT INTO breed VALUES (101,'Greyhound','','Greyhound', 1);
INSERT INTO breed VALUES (102,'Havanese','','Havanese', 1);
INSERT INTO breed VALUES (103,'Hound','','Hound', 1);
INSERT INTO breed VALUES (104,'Hovawart','','Hovawart', 1);
INSERT INTO breed VALUES (105,'Husky','','Husky', 1);
INSERT INTO breed VALUES (106,'Ibizan Hound','','Ibizan Hound', 1);
INSERT INTO breed VALUES (107,'Illyrian Sheepdog','','Illyrian Sheepdog', 1);
INSERT INTO breed VALUES (108,'Irish Setter','','Irish Setter', 1);
INSERT INTO breed VALUES (109,'Irish Terrier','','Irish Terrier', 1);
INSERT INTO breed VALUES (110,'Irish Water Spaniel','','Irish Water Spaniel', 1);
INSERT INTO breed VALUES (111,'Irish Wolfhound','','Irish Wolfhound', 1);
INSERT INTO breed VALUES (112,'Italian Greyhound','','Italian Greyhound', 1);
INSERT INTO breed VALUES (113,'Italian Spinone','','Italian Spinone', 1);
INSERT INTO breed VALUES (114,'Jack Russell Terrier','','Jack Russell Terrier', 1);
INSERT INTO breed VALUES (115,'Japanese Chin','','Japanese Chin', 1);
INSERT INTO breed VALUES (116,'Jindo','','Jindo', 1);
INSERT INTO breed VALUES (117,'Kai Dog','','Kai Dog', 1);
INSERT INTO breed VALUES (118,'Karelian Bear Dog','','Karelian Bear Dog', 1);
INSERT INTO breed VALUES (119,'Keeshond','','Keeshond', 1);
INSERT INTO breed VALUES (120,'Kerry Blue Terrier','','Kerry Blue Terrier', 1);
INSERT INTO breed VALUES (121,'Kishu','','Kishu', 1);
INSERT INTO breed VALUES (122,'Komondor','','Komondor', 1);
INSERT INTO breed VALUES (123,'Kuvasz','','Kuvasz', 1);
INSERT INTO breed VALUES (124,'Kyi Leo','','Kyi Leo', 1);
INSERT INTO breed VALUES (125,'Labrador Retriever','','Labrador Retriever', 1);
INSERT INTO breed VALUES (126,'Lakeland Terrier','','Lakeland Terrier', 1);
INSERT INTO breed VALUES (127,'Lancashire Heeler','','Lancashire Heeler', 1);
INSERT INTO breed VALUES (128,'Lhasa Apso','','Lhasa Apso', 1);
INSERT INTO breed VALUES (129,'Leonberger','','Leonberger', 1);
INSERT INTO breed VALUES (130,'Löwchen','','Löwchen', 1);
INSERT INTO breed VALUES (131,'Maltese','','Maltese', 1);
INSERT INTO breed VALUES (132,'Manchester Terrier','','Manchester Terrier', 1);
INSERT INTO breed VALUES (133,'Maremma Sheepdog','','Maremma Sheepdog', 1);
INSERT INTO breed VALUES (134,'Mastiff','','Mastiff', 1);
INSERT INTO breed VALUES (135,'McNab','','McNab', 1);
INSERT INTO breed VALUES (136,'Miniature Pinscher','','Miniature Pinscher', 1);
INSERT INTO breed VALUES (137,'Mountain Cur','','Mountain Cur', 1);
INSERT INTO breed VALUES (138,'Mountain Dog','','Mountain Dog', 1);
INSERT INTO breed VALUES (139,'Munsterlander','','Munsterlander', 1);
INSERT INTO breed VALUES (140,'Neapolitan Mastiff','','Neapolitan Mastiff', 1);
INSERT INTO breed VALUES (141,'New Guinea Singing Dog','','New Guinea Singing Dog', 1);
INSERT INTO breed VALUES (142,'Newfoundland Dog','','Newfoundland Dog', 1);
INSERT INTO breed VALUES (143,'Norfolk Terrier','','Norfolk Terrier', 1);
INSERT INTO breed VALUES (144,'Norwich Terrier','','Norwich Terrier', 1);
INSERT INTO breed VALUES (145,'Norwegian Buhund','','Norwegian Buhund', 1);
INSERT INTO breed VALUES (146,'Norwegian Elkhound','','Norwegian Elkhound', 1);
INSERT INTO breed VALUES (147,'Norwegian Lundehund','','Norwegian Lundehund', 1);
INSERT INTO breed VALUES (148,'Nova Scotia Duck-Tolling Retriever','','Nova Scotia Duck-Tolling Retriever', 1);
INSERT INTO breed VALUES (149,'Old English Sheepdog','','Old English Sheepdog', 1);
INSERT INTO breed VALUES (150,'Otterhound','','Otterhound', 1);
INSERT INTO breed VALUES (151,'Papillon','','Papillon', 1);
INSERT INTO breed VALUES (152,'Patterdale Terrier (Fell Terrier)','','Patterdale Terrier (Fell Terrier)', 1);
INSERT INTO breed VALUES (153,'Pekingese','','Pekingese', 1);
INSERT INTO breed VALUES (154,'Peruvian Inca Orchid','','Peruvian Inca Orchid', 1);
INSERT INTO breed VALUES (155,'Petit Basset Griffon Vendeen','','Petit Basset Griffon Vendeen', 1);
INSERT INTO breed VALUES (156,'Pharaoh Hound','','Pharaoh Hound', 1);
INSERT INTO breed VALUES (157,'Pit Bull Terrier','','Pit Bull Terrier', 1);
INSERT INTO breed VALUES (158,'Plott Hound','','Plott Hound', 1);
INSERT INTO breed VALUES (159,'Portugese Podengo','','Podengo Portugueso', 1);
INSERT INTO breed VALUES (160,'Pointer','','Pointer', 1);
INSERT INTO breed VALUES (161,'Polish Lowland Sheepdog','','Polish Lowland Sheepdog', 1);
INSERT INTO breed VALUES (162,'Pomeranian','','Pomeranian', 1);
INSERT INTO breed VALUES (163,'Poodle','','Poodle', 1);
INSERT INTO breed VALUES (164,'Portuguese Water Dog','','Portuguese Water Dog', 1);
INSERT INTO breed VALUES (165,'Presa Canario','','Presa Canario', 1);
INSERT INTO breed VALUES (166,'Pug','','Pug', 1);
INSERT INTO breed VALUES (167,'Puli','','Puli', 1);
INSERT INTO breed VALUES (168,'Pumi','','Pumi', 1);
INSERT INTO breed VALUES (169,'Rat Terrier','','Rat Terrier', 1);
INSERT INTO breed VALUES (170,'Redbone Coonhound','','Redbone Coonhound', 1);
INSERT INTO breed VALUES (171,'Retriever','','Retriever', 1);
INSERT INTO breed VALUES (172,'Rhodesian Ridgeback','','Rhodesian Ridgeback', 1);
INSERT INTO breed VALUES (173,'Rottweiler','','Rottweiler', 1);
INSERT INTO breed VALUES (174,'Saluki','','Saluki', 1);
INSERT INTO breed VALUES (175,'Saint Bernard St. Bernard','','Saint Bernard St. Bernard', 1);
INSERT INTO breed VALUES (176,'Samoyed','','Samoyed', 1);
INSERT INTO breed VALUES (177,'Schipperke','','Schipperke', 1);
INSERT INTO breed VALUES (178,'Schnauzer','','Schnauzer', 1);
INSERT INTO breed VALUES (179,'Scottish Deerhound','','Scottish Deerhound', 1);
INSERT INTO breed VALUES (180,'Scottish Terrier Scottie','','Scottish Terrier Scottie', 1);
INSERT INTO breed VALUES (181,'Sealyham Terrier','','Sealyham Terrier', 1);
INSERT INTO breed VALUES (182,'Setter','','Setter', 1);
INSERT INTO breed VALUES (183,'Shar Pei','','Shar Pei', 1);
INSERT INTO breed VALUES (184,'Sheep Dog','','Sheep Dog', 1);
INSERT INTO breed VALUES (185,'Shepherd','','Shepherd', 1);
INSERT INTO breed VALUES (186,'Shetland Sheepdog Sheltie','','Shetland Sheepdog Sheltie', 1);
INSERT INTO breed VALUES (187,'Shiba Inu','','Shiba Inu', 1);
INSERT INTO breed VALUES (188,'Shih Tzu','','Shih Tzu', 1);
INSERT INTO breed VALUES (189,'Siberian Husky','','Siberian Husky', 1);
INSERT INTO breed VALUES (190,'Silky Terrier','','Silky Terrier', 1);
INSERT INTO breed VALUES (191,'Skye Terrier','','Skye Terrier', 1);
INSERT INTO breed VALUES (192,'Sloughi','','Sloughi', 1);
INSERT INTO breed VALUES (193,'Smooth Fox Terrier','','Smooth Fox Terrier', 1);
INSERT INTO breed VALUES (194,'Spaniel','','Spaniel', 1);
INSERT INTO breed VALUES (195,'Spitz','','Spitz', 1);
INSERT INTO breed VALUES (196,'Staffordshire Bull Terrier','','Staffordshire Bull Terrier', 1);
INSERT INTO breed VALUES (197,'South Russian Ovcharka','','South Russian Ovcharka', 1);
INSERT INTO breed VALUES (198,'Swedish Vallhund','','Swedish Vallhund', 1);
INSERT INTO breed VALUES (199,'Terrier','','Terrier', 1);
INSERT INTO breed VALUES (200,'Thai Ridgeback','','Thai Ridgeback', 1);
INSERT INTO breed VALUES (201,'Tibetan Mastiff','','Tibetan Mastiff', 1);
INSERT INTO breed VALUES (202,'Tibetan Spaniel','','Tibetan Spaniel', 1);
INSERT INTO breed VALUES (203,'Tibetan Terrier','','Tibetan Terrier', 1);
INSERT INTO breed VALUES (204,'Tosa Inu','','Tosa Inu', 1);
INSERT INTO breed VALUES (205,'Toy Fox Terrier','','Toy Fox Terrier', 1);
INSERT INTO breed VALUES (206,'Treeing Walker Coonhound','','Treeing Walker Coonhound', 1);
INSERT INTO breed VALUES (207,'Vizsla','','Vizsla', 1);
INSERT INTO breed VALUES (208,'Weimaraner','','Weimaraner', 1);
INSERT INTO breed VALUES (209,'Welsh Corgi','','Welsh Corgi', 1);
INSERT INTO breed VALUES (210,'Welsh Terrier','','Welsh Terrier', 1);
INSERT INTO breed VALUES (211,'Welsh Springer Spaniel','','Welsh Springer Spaniel', 1);
INSERT INTO breed VALUES (212,'West Highland White Terrier Westie','','West Highland White Terrier Westie', 1);
INSERT INTO breed VALUES (213,'Wheaten Terrier','','Wheaten Terrier', 1);
INSERT INTO breed VALUES (214,'Whippet','','Whippet', 1);
INSERT INTO breed VALUES (215,'White German Shepherd','','White German Shepherd', 1);
INSERT INTO breed VALUES (216,'Wire-haired Pointing Griffon','','Wire-haired Pointing Griffon', 1);
INSERT INTO breed VALUES (217,'Wirehaired Terrier','','Wirehaired Terrier', 1);
INSERT INTO breed VALUES (218,'Yellow Labrador Retriever','','Yellow Labrador Retriever', 1);
INSERT INTO breed VALUES (219,'Yorkshire Terrier Yorkie','','Yorkshire Terrier Yorkie', 1);
INSERT INTO breed VALUES (220,'Xoloitzcuintle/Mexican Hairless','','Xoloitzcuintle/Mexican Hairless', 1);
INSERT INTO breed VALUES (221,'Abyssinian','','Abyssinian', 2);
INSERT INTO breed VALUES (222,'American Curl','','American Curl', 2);
INSERT INTO breed VALUES (223,'American Shorthair','','American Shorthair', 2);
INSERT INTO breed VALUES (224,'American Wirehair','','American Wirehair', 2);
INSERT INTO breed VALUES (225,'Applehead Siamese','','Applehead Siamese', 2);
INSERT INTO breed VALUES (226,'Balinese','','Balinese', 2);
INSERT INTO breed VALUES (227,'Bengal','','Bengal', 2);
INSERT INTO breed VALUES (228,'Birman','','Birman', 2);
INSERT INTO breed VALUES (229,'Bobtail','','Bobtail', 2);
INSERT INTO breed VALUES (230,'Bombay','','Bombay', 2);
INSERT INTO breed VALUES (231,'British Shorthair','','British Shorthair', 2);
INSERT INTO breed VALUES (232,'Burmese','','Burmese', 2);
INSERT INTO breed VALUES (233,'Burmilla','','Burmilla', 2);
INSERT INTO breed VALUES (234,'Calico','','Calico', 2);
INSERT INTO breed VALUES (235,'Canadian Hairless','','Canadian Hairless', 2);
INSERT INTO breed VALUES (236,'Chartreux','','Chartreux', 2);
INSERT INTO breed VALUES (237,'Chinchilla','','Chinchilla', 2);
INSERT INTO breed VALUES (238,'Cornish Rex','','Cornish Rex', 2);
INSERT INTO breed VALUES (239,'Cymric','','Cymric', 2);
INSERT INTO breed VALUES (240,'Devon Rex','','Devon Rex', 2);
INSERT INTO breed VALUES (241,'Dilute Calico','','Dilute Calico', 2);
INSERT INTO breed VALUES (242,'Dilute Tortoiseshell','','Dilute Tortoiseshell', 2);
INSERT INTO breed VALUES (243,'Domestic Long Hair','','Domestic Long Hair', 2);
INSERT INTO breed VALUES (252,'Domestic Medium Hair','','Domestic Medium Hair', 2);
INSERT INTO breed VALUES (261,'Domestic Short Hair','','Domestic Short Hair', 2);
INSERT INTO breed VALUES (271,'Egyptian Mau','','Egyptian Mau', 2);
INSERT INTO breed VALUES (272,'Exotic Shorthair','','Exotic Shorthair', 2);
INSERT INTO breed VALUES (273,'Extra-Toes Cat (Hemingway Polydactyl)','','Extra-Toes Cat (Hemingway Polydactyl)', 2);
INSERT INTO breed VALUES (274,'Havana','','Havana', 2);
INSERT INTO breed VALUES (275,'Himalayan','','Himalayan', 2);
INSERT INTO breed VALUES (276,'Japanese Bobtail','','Japanese Bobtail', 2);
INSERT INTO breed VALUES (277,'Javanese','','Javanese', 2);
INSERT INTO breed VALUES (278,'Korat','','Korat', 2);
INSERT INTO breed VALUES (279,'Maine Coon','','Maine Coon', 2);
INSERT INTO breed VALUES (280,'Manx','','Manx', 2);
INSERT INTO breed VALUES (281,'Munchkin','','Munchkin', 2);
INSERT INTO breed VALUES (282,'Norwegian Forest Cat','','Norwegian Forest Cat', 2);
INSERT INTO breed VALUES (283,'Ocicat','','Ocicat', 2);
INSERT INTO breed VALUES (284,'Oriental Long Hair','','Oriental Long Hair', 2);
INSERT INTO breed VALUES (285,'Oriental Short Hair','','Oriental Short Hair', 2);
INSERT INTO breed VALUES (286,'Oriental Tabby','','Oriental Tabby', 2);
INSERT INTO breed VALUES (287,'Persian','','Persian', 2);
INSERT INTO breed VALUES (288,'Pixie-Bob','','Pixie-Bob', 2);
INSERT INTO breed VALUES (289,'Ragamuffin','','Ragamuffin', 2);
INSERT INTO breed VALUES (290,'Ragdoll','','Ragdoll', 2);
INSERT INTO breed VALUES (291,'Russian Blue','','Russian Blue', 2);
INSERT INTO breed VALUES (292,'Scottish Fold','','Scottish Fold', 2);
INSERT INTO breed VALUES (293,'Selkirk Rex','','Selkirk Rex', 2);
INSERT INTO breed VALUES (294,'Siamese','','Siamese', 2);
INSERT INTO breed VALUES (295,'Siberian','','Siberian', 2);
INSERT INTO breed VALUES (296,'Singapura','','Singapura', 2);
INSERT INTO breed VALUES (297,'Snowshoe','','Snowshoe', 2);
INSERT INTO breed VALUES (298,'Somali','','Somali', 2);
INSERT INTO breed VALUES (299,'Sphynx (hairless cat)','','Sphynx (hairless cat)', 2);
INSERT INTO breed VALUES (300,'Tabby','','Tabby', 2);
INSERT INTO breed VALUES (307,'Tiger','','Tiger', 2);
INSERT INTO breed VALUES (308,'Tonkinese','','Tonkinese', 2);
INSERT INTO breed VALUES (309,'Torbie','','Torbie', 2);
INSERT INTO breed VALUES (310,'Tortoiseshell','','Tortoiseshell', 2);
INSERT INTO breed VALUES (311,'Turkish Angora','','Turkish Angora', 2);
INSERT INTO breed VALUES (312,'Turkish Van','','Turkish Van', 2);
INSERT INTO breed VALUES (313,'Tuxedo','','Tuxedo', 2);
INSERT INTO breed VALUES (314,'American','','American', 7);
INSERT INTO breed VALUES (315,'American Fuzzy Lop','','American Fuzzy Lop', 7);
INSERT INTO breed VALUES (316,'American Sable','','American Sable', 7);
INSERT INTO breed VALUES (317,'Angora Rabbit','','Angora Rabbit', 7);
INSERT INTO breed VALUES (318,'Belgian Hare','','Belgian Hare', 7);
INSERT INTO breed VALUES (319,'Beveren','','Beveren', 7);
INSERT INTO breed VALUES (320,'Britannia Petite','','Britannia Petite', 7);
INSERT INTO breed VALUES (321,'Bunny Rabbit','','Bunny Rabbit', 7);
INSERT INTO breed VALUES (322,'Californian','','Californian', 7);
INSERT INTO breed VALUES (323,'Champagne DArgent','','Champagne DArgent', 7);
INSERT INTO breed VALUES (324,'Checkered Giant','','Checkered Giant', 7);
INSERT INTO breed VALUES (325,'Chinchilla','','Chinchilla', 7);
INSERT INTO breed VALUES (326,'Cinnamon','','Cinnamon', 7);
INSERT INTO breed VALUES (327,'Crème DArgent','','Crème DArgent', 7);
INSERT INTO breed VALUES (328,'Dutch','','Dutch', 7);
INSERT INTO breed VALUES (329,'Dwarf','','Dwarf', 7);
INSERT INTO breed VALUES (330,'Dwarf Eared','','Dwarf Eared', 7);
INSERT INTO breed VALUES (331,'English Lop','','English Lop', 7);
INSERT INTO breed VALUES (332,'English Spot','','English Spot', 7);
INSERT INTO breed VALUES (333,'Flemish Giant','','Flemish Giant', 7);
INSERT INTO breed VALUES (334,'Florida White','','Florida White', 7);
INSERT INTO breed VALUES (335,'French-Lop','','French-Lop', 7);
INSERT INTO breed VALUES (336,'Harlequin','','Harlequin', 7);
INSERT INTO breed VALUES (337,'Havana','','Havana', 7);
INSERT INTO breed VALUES (338,'Himalayan','','Himalayan', 7);
INSERT INTO breed VALUES (339,'Holland Lop','','Holland Lop', 7);
INSERT INTO breed VALUES (340,'Hotot','','Hotot', 7);
INSERT INTO breed VALUES (341,'Jersey Wooly','','Jersey Wooly', 7);
INSERT INTO breed VALUES (342,'Lilac','','Lilac', 7);
INSERT INTO breed VALUES (343,'Lop Eared','','Lop Eared', 7);
INSERT INTO breed VALUES (344,'Mini-Lop','','Mini-Lop', 7);
INSERT INTO breed VALUES (345,'Mini Rex','','Mini Rex', 7);
INSERT INTO breed VALUES (346,'Netherland Dwarf','','Netherland Dwarf', 7);
INSERT INTO breed VALUES (347,'New Zealand','','New Zealand', 7);
INSERT INTO breed VALUES (348,'Palomino','','Palomino', 7);
INSERT INTO breed VALUES (349,'Polish','','Polish', 7);
INSERT INTO breed VALUES (350,'Rex','','Rex', 7);
INSERT INTO breed VALUES (351,'Rhinelander','','Rhinelander', 7);
INSERT INTO breed VALUES (352,'Satin','','Satin', 7);
INSERT INTO breed VALUES (353,'Silver','','Silver', 7);
INSERT INTO breed VALUES (354,'Silver Fox','','Silver Fox', 7);
INSERT INTO breed VALUES (355,'Silver Marten','','Silver Marten', 7);
INSERT INTO breed VALUES (356,'Tan','','Tan', 7);
INSERT INTO breed VALUES (357,'Appaloosa','','Appaloosa', 24);
INSERT INTO breed VALUES (358,'Arabian','','Arabian', 24);
INSERT INTO breed VALUES (359,'Clydesdale','','Clydesdale', 24);
INSERT INTO breed VALUES (360,'Donkey/Mule','','Donkey/Mule', 26);
INSERT INTO breed VALUES (361,'Draft','','Draft', 24);
INSERT INTO breed VALUES (362,'Gaited','','Gaited', 24);
INSERT INTO breed VALUES (363,'Grade','','Grade', 24);
INSERT INTO breed VALUES (364,'Missouri Foxtrotter','','Missouri Foxtrotter', 24);
INSERT INTO breed VALUES (365,'Morgan','','Morgan', 24);
INSERT INTO breed VALUES (366,'Mustang','','Mustang', 24);
INSERT INTO breed VALUES (367,'Paint/Pinto','','Paint/Pinto', 24);
INSERT INTO breed VALUES (368,'Palomino','','Palomino', 24);
INSERT INTO breed VALUES (369,'Paso Fino','','Paso Fino', 24);
INSERT INTO breed VALUES (370,'Percheron','','Percheron', 24);
INSERT INTO breed VALUES (371,'Peruvian Paso','','Peruvian Paso', 24);
INSERT INTO breed VALUES (372,'Pony','','Pony', 25);
INSERT INTO breed VALUES (373,'Quarterhorse','','Quarterhorse', 25);
INSERT INTO breed VALUES (374,'Saddlebred','','Saddlebred', 24);
INSERT INTO breed VALUES (375,'Standardbred','','Standardbred', 24);
INSERT INTO breed VALUES (376,'Thoroughbred','','Thoroughbred', 24);
INSERT INTO breed VALUES (377,'Tennessee Walker','','Tennessee Walker', 24);
INSERT INTO breed VALUES (378,'Warmblood','','Warmblood', 24);
INSERT INTO breed VALUES (379,'Chinchilla','','Chinchilla', 10);
INSERT INTO breed VALUES (380,'Ferret','','Ferret', 3);
INSERT INTO breed VALUES (381,'Gerbil','','Gerbil', 18);
INSERT INTO breed VALUES (382,'Guinea Pig','','Guinea Pig', 20);
INSERT INTO breed VALUES (383,'Hamster','','Hamster', 22);
INSERT INTO breed VALUES (384,'Hedgehog','','Hedgehog', 6);
INSERT INTO breed VALUES (385,'Mouse','','Mouse', 4);
INSERT INTO breed VALUES (386,'Prairie Dog','','Prairie Dog', 5);
INSERT INTO breed VALUES (387,'Rat','','Rat', 5);
INSERT INTO breed VALUES (388,'Skunk','','Skunk', 5);
INSERT INTO breed VALUES (389,'Sugar Glider','','Sugar Glider', 5);
INSERT INTO breed VALUES (390,'Pot Bellied','','Pot Bellied', 28);
INSERT INTO breed VALUES (391,'Vietnamese Pot Bellied','','Vietnamese Pot Bellied', 28);
INSERT INTO breed VALUES (392,'Gecko','','Gecko', 13);
INSERT INTO breed VALUES (393,'Iguana','','Iguana', 13);
INSERT INTO breed VALUES (394,'Lizard','','Lizard', 13);
INSERT INTO breed VALUES (395,'Snake','','Snake', 13);
INSERT INTO breed VALUES (396,'Turtle','','Turtle', 13);
INSERT INTO breed VALUES (397,'Fish','','Fish', 21);
INSERT INTO breed VALUES (398,'African Grey','','African Grey', 3);
INSERT INTO breed VALUES (399,'Amazon','','Amazon', 3);
INSERT INTO breed VALUES (400,'Brotogeris','','Brotogeris', 3);
INSERT INTO breed VALUES (401,'Budgie/Budgerigar','','Budgie/Budgerigar', 3);
INSERT INTO breed VALUES (402,'Caique','','Caique', 3);
INSERT INTO breed VALUES (403,'Canary','','Canary', 3);
INSERT INTO breed VALUES (404,'Chicken','','Chicken', 3);
INSERT INTO breed VALUES (405,'Cockatiel','','Cockatiel', 3);
INSERT INTO breed VALUES (406,'Cockatoo','','Cockatoo', 3);
INSERT INTO breed VALUES (407,'Conure','','Conure', 3);
INSERT INTO breed VALUES (408,'Dove','','Dove', 3);
INSERT INTO breed VALUES (409,'Duck','','Duck', 3);
INSERT INTO breed VALUES (410,'Eclectus','','Eclectus', 3);
INSERT INTO breed VALUES (411,'Emu','','Emu', 3);
INSERT INTO breed VALUES (412,'Finch','','Finch', 3);
INSERT INTO breed VALUES (413,'Goose','','Goose', 3);
INSERT INTO breed VALUES (414,'Guinea fowl','','Guinea fowl', 3);
INSERT INTO breed VALUES (415,'Kakariki','','Kakariki', 3);
INSERT INTO breed VALUES (416,'Lory/Lorikeet','','Lory/Lorikeet', 3);
INSERT INTO breed VALUES (417,'Lovebird','','Lovebird', 3);
INSERT INTO breed VALUES (418,'Macaw','','Macaw', 3);
INSERT INTO breed VALUES (419,'Mynah','','Mynah', 3);
INSERT INTO breed VALUES (420,'Ostrich','','Ostrich', 3);
INSERT INTO breed VALUES (421,'Parakeet (Other)','','Parakeet (Other)', 3);
INSERT INTO breed VALUES (422,'Parrot (Other)','','Parrot (Other)', 3);
INSERT INTO breed VALUES (423,'Parrotlet','','Parrotlet', 3);
INSERT INTO breed VALUES (424,'Peacock/Pea fowl','','Peacock/Pea fowl', 3);
INSERT INTO breed VALUES (425,'Pheasant','','Pheasant', 3);
INSERT INTO breed VALUES (426,'Pigeon','','Pigeon', 3);
INSERT INTO breed VALUES (427,'Pionus','','Pionus', 3);
INSERT INTO breed VALUES (428,'Poicephalus/Senegal','','Poicephalus/Senegal', 3);
INSERT INTO breed VALUES (429,'Quaker Parakeet','','Quaker Parakeet', 3);
INSERT INTO breed VALUES (430,'Rhea','','Rhea', 3);
INSERT INTO breed VALUES (431,'Ringneck/Psittacula','','Ringneck/Psittacula', 3);
INSERT INTO breed VALUES (432,'Rosella','','Rosella', 3);
INSERT INTO breed VALUES (433,'Softbill (Other)','','Softbill (Other)', 3);
INSERT INTO breed VALUES (434,'Swan','','Swan', 3);
INSERT INTO breed VALUES (435,'Toucan','','Toucan', 3);
INSERT INTO breed VALUES (436,'Turkey','','Turkey', 3);
INSERT INTO breed VALUES (437,'Cow','','Cow', 16);
INSERT INTO breed VALUES (438,'Goat','','Goat', 16);
INSERT INTO breed VALUES (439,'Sheep','','Sheep', 16);
INSERT INTO breed VALUES (440,'Llama','','Llama', 16);
INSERT INTO breed VALUES (441,'Pig (Farm)','','Pig (Farm)', 28);









CREATE TABLE configuration (
  ItemName varchar(255) NOT NULL ,
  ItemValue varchar(255) NOT NULL 
) TYPE=MyISAM;

INSERT INTO configuration VALUES ('DatabaseVersion','2704');
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



CREATE TABLE costtype (
  ID int(11) NOT NULL,
  CostTypeName varchar(255) NOT NULL,
  CostTypeDescription varchar(255) NULL,
  PRIMARY KEY (ID)
) Type=MyISAM;
  
INSERT INTO costtype VALUES (1, 'Microchip', '');


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





INSERT INTO customreport VALUES (36,'Non-Microchipped Animals','001','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Auditing');
INSERT INTO customreport VALUES (37,'Animals Without Photo Media','002','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Auditing');
INSERT INTO customreport VALUES (38,'Animals Never Vaccinated','003','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Auditing');
INSERT INTO customreport VALUES (39,'Non-Neutered/Spayed Animals Aged Over 6 Months','004','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Auditing');
INSERT INTO customreport VALUES (40,'Cats Not Combi-Tested','005','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Auditing');
INSERT INTO customreport VALUES (41,'Monthly Animal Figures','006','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Figures');
INSERT INTO customreport VALUES (42,'Long Term Animals','007','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Figures');
INSERT INTO customreport VALUES (43,'Shelter Inventory','008','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Inventories');
INSERT INTO customreport VALUES (44,'Detailed Shelter Inventory','009','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Inventories');
INSERT INTO customreport VALUES (45,'Animals Not Part Of A Litter','010','','',0,0,'asmupdate','2003-07-02 11:51:00','Adam','2003-07-02 11:56:00','Litters');
INSERT INTO customreport VALUES (46,'In/Out','011','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movements');
INSERT INTO customreport VALUES (47,'In/Out Summary','012','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movements');
INSERT INTO customreport VALUES (48,'Transfer In Report','013','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movements');
INSERT INTO customreport VALUES (49,'Volume Of Adoptions Per Retailer','014','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Retailers');
INSERT INTO customreport VALUES (50,'Average Time At Retailer Before Adoption','015','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Retailers');
INSERT INTO customreport VALUES (51,'Retailer Inventory','016','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Retailers');
INSERT INTO customreport VALUES (52,'Returned Animals Report','017','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Returns');
INSERT INTO customreport VALUES (53,'Animals Returned Within 6 Months','018','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Returns');
INSERT INTO customreport VALUES (54,'Animals Returned After 6 Months','019','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Returns');
INSERT INTO customreport VALUES (55,'Most Common Name','020','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Statistics');
INSERT INTO customreport VALUES (56,'Animal Death Reasons','021','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Statistics');
INSERT INTO customreport VALUES (57,'Common Animal Entry Areas','022','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Statistics');
INSERT INTO customreport VALUES (58,'Common Animal Adoption Areas','023','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Statistics');
INSERT INTO customreport VALUES (59,'Average Time On Waiting List','024','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Statistics');
INSERT INTO customreport VALUES (60,'Monthly Donations','025','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Financial Graphs');
INSERT INTO customreport VALUES (61,'Monthly Donations By Species','026','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Financial Graphs');
INSERT INTO customreport VALUES (62,'Monthly Adoptions By Species','027','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movement Graphs');
INSERT INTO customreport VALUES (63,'Monthly Adoptions By Location','028','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movement Graphs');
INSERT INTO customreport VALUES (64,'Animal Entry Reasons','029','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movement Graphs');
INSERT INTO customreport VALUES (65,'Animal Return Reasons','030','','',0,0,'asmupdate','2003-07-02 11:51:00','asmupdate','2003-07-02 11:51:00','Movement Graphs');
INSERT INTO customreport VALUES (66,'Owner Criteria Matching','031','','',0,0,'asmupdate','2005-08-02 12:33:00','asmupdate','2005-08-02 12:33:00','Inventories');





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




INSERT INTO deathreason VALUES (1, 'Dead On Arrival', '');
INSERT INTO deathreason VALUES (2, 'Died', '');
INSERT INTO deathreason VALUES (3, 'Healthy', '');
INSERT INTO deathreason VALUES (4, 'Sick/Injured', '');
INSERT INTO deathreason VALUES (5, 'Requested', '');
INSERT INTO deathreason VALUES (6, 'Culling', '');
INSERT INTO deathreason VALUES (7, 'Feral', '');
INSERT INTO deathreason VALUES (8, 'Biting', '');






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

INSERT INTO diet VALUES (1, 'Standard', '');





CREATE TABLE donationtype (
  ID int(11) NOT NULL ,
  DonationName varchar(255) NOT NULL ,
  DonationDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

INSERT INTO donationtype VALUES (1, 'Donation', '');
INSERT INTO donationtype VALUES (2, 'Adoption Fee', '');
INSERT INTO donationtype VALUES (3, 'Waiting List Donation', '');
INSERT INTO donationtype VALUES (4, 'Entry Donation', '');
INSERT INTO donationtype VALUES (5, 'Animal Sponsorship', '');






CREATE TABLE entryreason (
  ID int(11) NOT NULL ,
  ReasonName varchar(255) NOT NULL ,
  ReasonDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;




INSERT INTO entryreason VALUES (1, 'Marriage/Relationship split', '');
INSERT INTO entryreason VALUES (2, 'Allergies', '');
INSERT INTO entryreason VALUES (3, 'Biting', '');
INSERT INTO entryreason VALUES (4, 'Unable to Cope', '');
INSERT INTO entryreason VALUES (5, 'Unsuitable Accomodation', '');
INSERT INTO entryreason VALUES (6, 'Died', '');
INSERT INTO entryreason VALUES (7, 'Stray', '');
INSERT INTO entryreason VALUES (8, 'Sick/Injured', '');
INSERT INTO entryreason VALUES (9, 'Unable to Afford', '');
INSERT INTO entryreason VALUES (10, 'Abuse', '');
INSERT INTO entryreason VALUES (11, 'Abandoned', '');





CREATE TABLE internallocation (
  ID int(11) NOT NULL ,
  LocationName varchar(255) NOT NULL ,
  LocationDescription varchar(255) NULL
) TYPE=MyISAM;





INSERT INTO internallocation VALUES (1,'No Locations','');


CREATE TABLE lkcoattype (
  ID int(11) NOT NULL DEFAULT '0',
  CoatType VARCHAR(40) NOT NULL,
  PRIMARY KEY (ID)
  ) Type=MyISAM;

INSERT INTO lkcoattype VALUES (0, 'Short');
INSERT INTO lkcoattype VALUES (1, 'Long');
INSERT INTO lkcoattype VALUES (2, 'Rough');
INSERT INTO lkcoattype VALUES (3, 'Curly');
INSERT INTO lkcoattype VALUES (4, 'Corded');
INSERT INTO lkcoattype VALUES (5, 'Hairless');


CREATE TABLE lksex (
  ID smallint NOT NULL DEFAULT '0',
  Sex varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksex VALUES (0, 'Female');
INSERT INTO lksex VALUES (1, 'Male');
INSERT INTO lksex VALUES (2, 'Unknown');

CREATE TABLE lksize (
  ID smallint NOT NULL DEFAULT '0',
  Size varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksize VALUES (0, 'Very Large');
INSERT INTO lksize VALUES (1, 'Large');
INSERT INTO lksize VALUES (2, 'Medium');
INSERT INTO lksize VALUES (3, 'Small');

CREATE TABLE lksmovementtype (
  ID smallint NOT NULL DEFAULT '0',
  MovementType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksmovementtype VALUES (0, 'None');
INSERT INTO lksmovementtype VALUES (1, 'Adoption');
INSERT INTO lksmovementtype VALUES (2, 'Foster');
INSERT INTO lksmovementtype VALUES (3, 'Transfer');
INSERT INTO lksmovementtype VALUES (4, 'Escaped');
INSERT INTO lksmovementtype VALUES (5, 'Reclaimed');
INSERT INTO lksmovementtype VALUES (6, 'Stolen');
INSERT INTO lksmovementtype VALUES (7, 'Released To Wild');
INSERT INTO lksmovementtype VALUES (8, 'Retailer');
INSERT INTO lksmovementtype VALUES (9, 'Reservation');
INSERT INTO lksmovementtype VALUES (10, 'Cancelled Reservation');

CREATE TABLE lksfieldlink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksfieldlink VALUES (0, 'Animal');
INSERT INTO lksfieldlink VALUES (1, 'Owner');

CREATE TABLE lksfieldtype (
  ID smallint NOT NULL DEFAULT '0',
  FieldType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksfieldtype VALUES (0, 'Yes/No');
INSERT INTO lksfieldtype VALUES (1, 'Text');
INSERT INTO lksfieldtype VALUES (2, 'Notes');
INSERT INTO lksfieldtype VALUES (3, 'Number');
INSERT INTO lksfieldtype VALUES (4, 'Date');
INSERT INTO lksfieldtype VALUES (5, 'Money');
INSERT INTO lksfieldtype VALUES (6, 'Lookup');

CREATE TABLE lksmedialink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksmedialink VALUES (0, 'Animal');
INSERT INTO lksmedialink VALUES (1, 'Lost Animal');
INSERT INTO lksmedialink VALUES (2, 'Found Animal');
INSERT INTO lksmedialink VALUES (3, 'Owner');
INSERT INTO lksmedialink VALUES (4, 'Movement');

CREATE TABLE lksdiarylink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksdiarylink VALUES (0, 'None');
INSERT INTO lksdiarylink VALUES (1, 'Animal');
INSERT INTO lksdiarylink VALUES (2, 'Owner');
INSERT INTO lksdiarylink VALUES (3, 'Lost Animal');
INSERT INTO lksdiarylink VALUES (4, 'Found Animal');
INSERT INTO lksdiarylink VALUES (5, 'Waiting List');
INSERT INTO lksdiarylink VALUES (6, 'Movement');

CREATE TABLE lksdonationfreq (
  ID smallint NOT NULL DEFAULT '0',
  Frequency varchar(50) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lksdonationfreq VALUES (0, 'One-Off');
INSERT INTO lksdonationfreq VALUES (1, 'Weekly');
INSERT INTO lksdonationfreq VALUES (2, 'Monthly');
INSERT INTO lksdonationfreq VALUES (3, 'Quarterly');
INSERT INTO lksdonationfreq VALUES (4, 'Half-Yearly');
INSERT INTO lksdonationfreq VALUES (5, 'Annually');

CREATE TABLE lksloglink (
  ID smallint NOT NULL DEFAULT '0',
  LinkType varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
) Type=MyISAM;

INSERT INTO lksloglink VALUES (0, 'Animal');
INSERT INTO lksloglink VALUES (1, 'Owner');
INSERT INTO lksloglink VALUES (2, 'Lost Animal');
INSERT INTO lksloglink VALUES (3, 'Found Animal');
INSERT INTO lksloglink VALUES (4, 'Waiting List');
INSERT INTO lksloglink VALUES (5, 'Movement');

CREATE TABLE lkurgency (
  ID smallint NOT NULL DEFAULT '0',
  Urgency varchar(40) NOT NULL,
  PRIMARY KEY  (ID)
  ) Type=MyISAM;

INSERT INTO lkurgency VALUES (1, 'Urgent');
INSERT INTO lkurgency VALUES (2, 'High');
INSERT INTO lkurgency VALUES (3, 'Medium');
INSERT INTO lkurgency VALUES (4, 'Low');
INSERT INTO lkurgency VALUES (5, 'Lowest');

CREATE TABLE lksyesno (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);
INSERT INTO lksyesno VALUES (0, 'No');
INSERT INTO lksyesno VALUES (1, 'Yes');

CREATE TABLE lksynun (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);
INSERT INTO lksynun VALUES (0, 'Yes');
INSERT INTO lksynun VALUES (1, 'No');
INSERT INTO lksynun VALUES (2, 'Unknown');

CREATE TABLE lksposneg (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);
INSERT INTO lksposneg VALUES (0, 'Unknown');
INSERT INTO lksposneg VALUES (1, 'Negative');
INSERT INTO lksposneg VALUES (2, 'Positive');


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

INSERT INTO logtype VALUES (1, 'Bite', '');
INSERT INTO logtype VALUES (2, 'Complaint', '');
INSERT INTO logtype VALUES (3, 'History', '');
INSERT INTO logtype VALUES (4, 'Weight', '');






CREATE TABLE media (
  ID int(11) NOT NULL ,
  MediaName varchar(255) NOT NULL ,
  MediaNotes text NULL,
  WebsitePhoto tinyint(4) NOT NULL ,
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

INSERT INTO medicalpaymenttype VALUES ('1', 'Fee', '');




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





INSERT INTO species VALUES (1,'Dog','', 'Dog');
INSERT INTO species VALUES (2,'Cat','', 'Cat');
INSERT INTO species VALUES (3,'Bird','', 'Bird');
INSERT INTO species VALUES (4,'Mouse','', 'Small&Furry');
INSERT INTO species VALUES (5,'Rat','', 'Small&Furry');
INSERT INTO species VALUES (6,'Hedgehog','', 'Small&Furry');
INSERT INTO species VALUES (7,'Rabbit','', 'Rabbit');
INSERT INTO species VALUES (8,'Dove','', 'Bird');
INSERT INTO species VALUES (9,'Ferret','', 'Small&Furry');
INSERT INTO species VALUES (10,'Chinchilla','', 'Small&Furry');
INSERT INTO species VALUES (11,'Snake','', 'Reptile');
INSERT INTO species VALUES (12,'Tortoise','', 'Reptile');
INSERT INTO species VALUES (13,'Terrapin','', 'Reptile');
INSERT INTO species VALUES (14,'Chicken','', 'Barnyard');
INSERT INTO species VALUES (15,'Owl','', 'Bird');
INSERT INTO species VALUES (16,'Goat','', 'Barnyard');
INSERT INTO species VALUES (17,'Goose','', 'Bird');
INSERT INTO species VALUES (18,'Gerbil','', 'Small&Furry');
INSERT INTO species VALUES (19,'Cockatiel','', 'Bird');
INSERT INTO species VALUES (20,'Guinea Pig','', 'Small&Furry');
INSERT INTO species VALUES (21,'Goldfish','', 'Reptile');
INSERT INTO species VALUES (22,'Hamster','', 'Small&Furry');
INSERT INTO species VALUES (23, 'Camel', '', 'Horse');
INSERT INTO species VALUES (24, 'Horse', '', 'Horse');
INSERT INTO species VALUES (25, 'Pony', '', 'Horse');
INSERT INTO species VALUES (26, 'Donkey', '', 'Horse');
INSERT INTO species VALUES (27, 'Llama', '', 'Horse');
INSERT INTO species VALUES (28, 'Pig', '', 'Barnyard');





CREATE TABLE users (
  ID int(11) NOT NULL ,
  UserName varchar(255) NOT NULL ,
  RealName varchar(255) NULL,
  Password varchar(255) NOT NULL ,
  SuperUser tinyint(4) NOT NULL ,
  SecurityMap text NOT NULL ,
  RecordVersion int NOT NULL ,
  PRIMARY KEY  (ID),
  KEY IX_UsersUserName (UserName)
) TYPE=MyISAM;





INSERT INTO users VALUES (1,'user','Default system user', 'd107d09f5bbe40cade3de5c71e9e9b7',1,'', 0);
INSERT INTO users VALUES (2,'guest','Default guest user', '84e0343a0486ff05530df6c705c8bb4',0,'', 0);





CREATE TABLE voucher (
  ID int(11) NOT NULL ,
  VoucherName varchar(255) NOT NULL ,
  VoucherDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;

INSERT INTO voucher VALUES (1, 'Neuter/Spay', '');





CREATE TABLE vaccinationtype (
  ID int(11) NOT NULL ,
  VaccinationType varchar(255) NOT NULL ,
  VaccinationDescription varchar(255) NULL,
  PRIMARY KEY  (ID)
) TYPE=MyISAM;





INSERT INTO vaccinationtype VALUES (1,'Temporary Vaccination','');
INSERT INTO vaccinationtype VALUES (2,'First Vaccination','');
INSERT INTO vaccinationtype VALUES (3,'Second Vaccination','');
INSERT INTO vaccinationtype VALUES (4,'Booster','');
INSERT INTO vaccinationtype VALUES (5,'Leukaemia','');
INSERT INTO vaccinationtype VALUES (6,'Kennel Cough','');
INSERT INTO vaccinationtype VALUES (7,'Parvovirus','');

