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
  AmountDonatedOnEntry FLOAT NULL,
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




INSERT INTO animaltype VALUES (2,'D (Dog)',NULL);
INSERT INTO animaltype VALUES (10, 'F (Stray Dog)', NULL);
INSERT INTO animaltype VALUES (11,'U (Unwanted Cat)',NULL);
INSERT INTO animaltype VALUES (12,'S (Stray Cat)',NULL);
INSERT INTO animaltype VALUES (13,'M (Miscellaneous)',NULL);
INSERT INTO animaltype VALUES (40, 'N (Non Shelter Animal)', NULL);






CREATE MEMORY TABLE animalvaccination (
  ID INTEGER NOT NULL PRIMARY KEY,
  AnimalID INTEGER NOT NULL,
  VaccinationID INTEGER NOT NULL,
  DateOfVaccination TIMESTAMP NULL,
  DateRequired TIMESTAMP NOT NULL,
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
  DonationSize FLOAT NOT NULL,
  RecordVersion INTEGER NOT NULL,
  CreatedBy VARCHAR(255) NOT NULL,
  CreatedDate TIMESTAMP NOT NULL,
  LastChangedBy VARCHAR(255) NOT NULL,
  LastChangedDate TIMESTAMP NOT NULL
);
CREATE INDEX animalwaitinglist_SpeciesID ON animalwaitinglist (SpeciesID);
CREATE INDEX animalwaitinglist_Urgency ON animalwaitinglist (Urgency);
CREATE INDEX animalwaitinglist_DatePutOnList ON animalwaitinglist (DatePutOnList);





CREATE MEMORY TABLE basecolour (
  ID INTEGER NOT NULL PRIMARY KEY,
  BaseColour VARCHAR(255) NOT NULL,
  BaseColourDescription VARCHAR(255) NULL
);




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





CREATE MEMORY TABLE breed (
  ID INTEGER NOT NULL PRIMARY KEY,
  BreedName VARCHAR(255) NOT NULL,
  BreedDescription VARCHAR(255) NULL,
  PetFinderBreed VARCHAR(255) NULL,
  SpeciesID INTEGER NULL
);

--
-- Dumping data for table 'breed'
--

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






CREATE MEMORY TABLE configuration (
  ItemName VARCHAR(255) NOT NULL,
  ItemValue VARCHAR(255) NOT NULL
);

INSERT INTO configuration VALUES ('DatabaseVersion','2700');
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



INSERT INTO deathreason VALUES (1, 'Dead On Arrival', '');
INSERT INTO deathreason VALUES (2, 'Died', '');
INSERT INTO deathreason VALUES (3, 'Healthy', '');
INSERT INTO deathreason VALUES (4, 'Sick/Injured', '');
INSERT INTO deathreason VALUES (5, 'Requested', '');
INSERT INTO deathreason VALUES (6, 'Culling', '');
INSERT INTO deathreason VALUES (7, 'Feral', '');
INSERT INTO deathreason VALUES (8, 'Biting', '');






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
INSERT INTO diet VALUES (1, 'Standard', '');





CREATE MEMORY TABLE donationtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  DonationName VARCHAR(255) NOT NULL,
  DonationDescription VARCHAR(255) NULL
);
INSERT INTO donationtype VALUES (1, 'Donation', '');






CREATE MEMORY TABLE entryreason (
  ID INTEGER NOT NULL PRIMARY KEY,
  ReasonName VARCHAR(255) NOT NULL,
  ReasonDescription VARCHAR(255) NULL
);



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





CREATE MEMORY TABLE internallocation (
  ID INTEGER NOT NULL PRIMARY KEY,
  LocationName VARCHAR(255) NOT NULL,
  LocationDescription VARCHAR(255) NULL
);




INSERT INTO internallocation VALUES (1,'No Locations','');


CREATE MEMORY TABLE lkcoattype (
  ID INTEGER NOT NULL PRIMARY KEY,
  CoatType VARCHAR(40) NOT NULL
);
INSERT INTO lkcoattype VALUES (0, 'Short');
INSERT INTO lkcoattype VALUES (1, 'Long');
INSERT INTO lkcoattype VALUES (2, 'Rough');
INSERT INTO lkcoattype VALUES (3, 'Curly');
INSERT INTO lkcoattype VALUES (4, 'Corded');
INSERT INTO lkcoattype VALUES (5, 'Hairless');

CREATE MEMORY TABLE lksex (
  ID INTEGER NOT NULL PRIMARY KEY,
  Sex VARCHAR(40) NOT NULL
);
INSERT INTO lksex VALUES (0, 'Female');
INSERT INTO lksex VALUES (1, 'Male');
INSERT INTO lksex VALUES (2, 'Unknown');

CREATE MEMORY TABLE lksize (
  ID INTEGER NOT NULL PRIMARY KEY,
  Size VARCHAR(40) NOT NULL
  );
INSERT INTO lksize VALUES (0, 'Very Large');
INSERT INTO lksize VALUES (1, 'Large');
INSERT INTO lksize VALUES (2, 'Medium');
INSERT INTO lksize VALUES (3, 'Small');

CREATE MEMORY TABLE lksmovementtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  MovementType VARCHAR(40) NOT NULL
  );

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

CREATE MEMORY TABLE lksfieldlink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
  );

INSERT INTO lksfieldlink VALUES (0, 'Animal');
INSERT INTO lksfieldlink VALUES (1, 'Owner');

CREATE MEMORY TABLE lksfieldtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  FieldType VARCHAR(40) NOT NULL
  );

INSERT INTO lksfieldtype VALUES (0, 'Yes/No');
INSERT INTO lksfieldtype VALUES (1, 'Text');
INSERT INTO lksfieldtype VALUES (2, 'Notes');
INSERT INTO lksfieldtype VALUES (3, 'Number');
INSERT INTO lksfieldtype VALUES (4, 'Date');
INSERT INTO lksfieldtype VALUES (5, 'Money');
INSERT INTO lksfieldtype VALUES (6, 'Lookup');

CREATE MEMORY TABLE lksmedialink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
  );

INSERT INTO lksmedialink VALUES (0, 'Animal');
INSERT INTO lksmedialink VALUES (1, 'Lost Animal');
INSERT INTO lksmedialink VALUES (2, 'Found Animal');
INSERT INTO lksmedialink VALUES (3, 'Owner');
INSERT INTO lksmedialink VALUES (4, 'Movement');

CREATE MEMORY TABLE lksdiarylink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
  );

INSERT INTO lksdiarylink VALUES (0, 'None');
INSERT INTO lksdiarylink VALUES (1, 'Animal');
INSERT INTO lksdiarylink VALUES (2, 'Owner');
INSERT INTO lksdiarylink VALUES (3, 'Lost Animal');
INSERT INTO lksdiarylink VALUES (4, 'Found Animal');
INSERT INTO lksdiarylink VALUES (5, 'Waiting List');
INSERT INTO lksdiarylink VALUES (6, 'Movement');

CREATE MEMORY TABLE lksloglink (
  ID INTEGER NOT NULL PRIMARY KEY,
  LinkType VARCHAR(40) NOT NULL
);

INSERT INTO lksloglink VALUES (0, 'Animal');
INSERT INTO lksloglink VALUES (1, 'Owner');
INSERT INTO lksloglink VALUES (2, 'Lost Animal');
INSERT INTO lksloglink VALUES (3, 'Found Animal');
INSERT INTO lksloglink VALUES (4, 'Waiting List');
INSERT INTO lksloglink VALUES (5, 'Movement');

CREATE MEMORY TABLE lkurgency (
  ID INTEGER NOT NULL PRIMARY KEY,
  Urgency VARCHAR(40) NOT NULL
);

INSERT INTO lkurgency VALUES (1, 'Urgent');
INSERT INTO lkurgency VALUES (2, 'High');
INSERT INTO lkurgency VALUES (3, 'Medium');
INSERT INTO lkurgency VALUES (4, 'Low');
INSERT INTO lkurgency VALUES (5, 'Lowest');

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
INSERT INTO lksynun VALUES (0, 'Yes');
INSERT INTO lksynun VALUES (1, 'No');
INSERT INTO lksynun VALUES (2, 'Unknown');

CREATE MEMORY TABLE lksposneg (
  ID INTEGER NOT NULL PRIMARY KEY,
  Name VARCHAR(40) NOT NULL
);
INSERT INTO lksposneg VALUES (0, 'Unknown');
INSERT INTO lksposneg VALUES (1, 'Negative');
INSERT INTO lksposneg VALUES (2, 'Positive');

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

INSERT INTO logtype VALUES (1, 'Bite', '');
INSERT INTO logtype VALUES (2, 'Complaint', '');
INSERT INTO logtype VALUES (3, 'History', '');
INSERT INTO logtype VALUES (4, 'Weight', '');






CREATE MEMORY TABLE media (
  ID INTEGER NOT NULL PRIMARY KEY,
  MediaName VARCHAR(255) NOT NULL,
  MediaNotes VARCHAR(65535) NULL,
  WebsitePhoto INTEGER NOT NULL,
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
INSERT INTO medicalpaymenttype VALUES ('1', 'Fee', '');




CREATE MEMORY TABLE medicalprofile (
  ID INTEGER NOT NULL PRIMARY KEY,
  ProfileName VARCHAR(255) NOT NULL,
  TreatmentName VARCHAR(255) NOT NULL,
  Dosage VARCHAR(255) NULL,
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
  IsDonor INTEGER NOT NULL,
  IsShelter INTEGER NOT NULL,
  IsACO INTEGER NOT NULL,
  IsStaff INTEGER NOT NULL,
  IsFosterer INTEGER NOT NULL,
  IsRetailer INTEGER NOT NULL,
  IsVet INTEGER NOT NULL,
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





CREATE MEMORY TABLE users (
  ID INTEGER NOT NULL PRIMARY KEY,
  UserName VARCHAR(255) NOT NULL,
  RealName VARCHAR(255) NULL,
  Password VARCHAR(255) NOT NULL,
  SuperUser INTEGER NOT NULL,
  SecurityMap VARCHAR(16384) NOT NULL,
  RecordVersion INTEGER NOT NULL
);
CREATE INDEX users_UserName ON users (UserName);





INSERT INTO users VALUES (1,'user','Default system user', 'd107d09f5bbe40cade3de5c71e9e9b7',1,'', 0);
INSERT INTO users VALUES (2,'guest','Default guest user', '84e0343a0486ff05530df6c705c8bb4',0,'', 0);





CREATE MEMORY TABLE voucher (
  ID INTEGER NOT NULL PRIMARY KEY,
  VoucherName VARCHAR(255) NOT NULL,
  VoucherDescription VARCHAR(255) NULL
);
INSERT INTO voucher VALUES (1, 'Neuter/Spay', '');





CREATE MEMORY TABLE vaccinationtype (
  ID INTEGER NOT NULL PRIMARY KEY,
  VaccinationType VARCHAR(255) NOT NULL,
  VaccinationDescription VARCHAR(255) NULL
);




INSERT INTO vaccinationtype VALUES (1,'Temporary Vaccination','');
INSERT INTO vaccinationtype VALUES (2,'First Vaccination','');
INSERT INTO vaccinationtype VALUES (3,'Second Vaccination','');
INSERT INTO vaccinationtype VALUES (4,'Booster','');
INSERT INTO vaccinationtype VALUES (5,'Leukaemia','');
INSERT INTO vaccinationtype VALUES (6,'Kennel Cough','');
INSERT INTO vaccinationtype VALUES (7,'Parvovirus','');

