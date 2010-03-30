#!/usr/bin/python

"""
ASM modules. Supplies objects representing ASM entities that can write
SQL to generate themselves. Objects are basically the SQL tables, but with
sane defaults already completed and ID generation handled.

Eg:

    a = asm.Animal() # Empty constructor generates ID
    a.AnimalName = "Socks"
    print a
    
"""

import datetime

# Next year code to use for animals when generating shelter codes
nextyearcode = 1

# Dictionary of tables and next ID
ids = {}

def today():
    """ Returns today as a Python date """
    return datetime.date.today()
def dd(d):
    if d == None: return "NULL"
    return "'%d-%02d-%02d'" % ( d.year, d.month, d.day )
def ds(s):
    if s == None: return "NULL"
    return "'%s'" % str(s).replace("'", "''")
def df(f):
    if f == None: return "NULL"
    return str(f)
def di(i):
    if i == None: return "NULL"
    return str(i)
def makesql(table, s):
    fl = ""
    fv = ""
    for r in s:
        if fl != "": 
            fl += ", "
            fv += ", "
        fl += r[0]
        fv += r[1]
    return "INSERT INTO %s (%s) VALUES (%s);" % ( table, fl, fv )

def getid(table = "animal"):
    global ids
    if ids.has_key(table):
        nextid = ids[table]
        ids[table] = nextid + 1
        return nextid
    else:
        nextid = 1
        ids[table] = nextid + 1
        return nextid

class AnimalType:
    ID = 0
    Name = ""
    Description = None
    def __init__(self, ID = 0, Name = "", Description = ""):
        self.ID = ID
        if ID == 0: self.ID = getid("animaltype")
        self.Name = Name
        self.Description = Description
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "AnimalType", ds(self.Name) ),
            ( "AnimalDescription", ds(self.Description) )
            )
        return makesql("animaltype", s)

class Breed:
    ID = 0
    Name = ""
    Description = None
    SpeciesID = 0
    def __init__(self, ID = 0, Name = "", Description = "", SpeciesID = 0):
        self.ID = ID
        if ID == 0: self.ID = getid("breed")
        self.Name = Name
        self.Description = Description
	self.SpeciesID = SpeciesID
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "BreedName", ds(self.Name) ),
            ( "BreedDescription", ds(self.Description) ),
	    ( "SpeciesID", di(self.SpeciesID) )
            )
        return makesql("breed", s)

class Species:
    ID = 0
    Name = ""
    Description = None
    PetFinder = None
    def __init__(self, ID = 0, Name = "", Description = "", PetFinder = ""):
        self.ID = ID
        if ID == 0: self.ID = getid("species")
        self.Name = Name
        self.Description = Description
        self.PetFinder = PetFinder
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "SpeciesName", ds(self.Name) ),
            ( "SpeciesDescription", ds(self.Description) ),
            ( "PetFinderSpecies", ds(self.PetFinder) ),
            )
        return makesql("species", s)

class Location:
    ID = 0
    Name = ""
    Description = None
    def __init__(self, ID = 0, Name = "", Description = ""):
        self.ID = ID
        if ID == 0: self.ID = getid("internallocation")
        self.Name = Name
        self.Description = Description
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "LocationName", ds(self.Name) ),
            ( "LocationDescription", ds(self.Description) )
            )
        return makesql("internallocation", s)


class VaccinationType:
    ID = 0
    Name = ""
    Description = None
    def __init__(self, ID = 0, Name = "", Description = ""):
        self.ID = ID
        if ID == 0: self.ID = getid("vaccinationtype")
        self.Name = Name
        self.Description = Description
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "VaccinationType", ds(self.Name) ),
            ( "VaccinationDescription", ds(self.Description) )
            )
        return makesql("vaccinationtype", s)

class DonationType:
    ID = 0
    Name = ""
    Description = None
    def __init__(self, ID = 0, Name = "", Description = ""):
        self.ID = ID
        if ID == 0: self.ID = getid("donationtype")
        self.Name = Name
        self.Description = Description
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "DonationName", ds(self.Name) ),
            ( "DonationDescription", ds(self.Description) )
            )
        return makesql("donationtype", s)


class AnimalVaccination:
    ID = 0
    AnimalID = 0
    VaccinationID = 0
    DateOfVaccination = None
    DateRequired = today()
    Comments = ""
    RecordVersion = 0
    CreatedBy = "conversion"
    CreatedDate = today()
    LastChangedBy = "conversion"
    LastChangedDate = today()
    def __init__(self, ID = 0):
        self.ID = ID
        if ID == 0: self.ID = getid("animalvaccination")
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "AnimalID", di(self.AnimalID) ),
            ( "VaccinationID", di(self.VaccinationID) ),
            ( "DateOfVaccination", dd(self.DateOfVaccination) ),
            ( "DateRequired", dd(self.DateRequired) ),
            ( "Comments", ds(self.Comments) ),
            ( "RecordVersion", di(self.RecordVersion) ),
            ( "CreatedBy", ds(self.CreatedBy) ),
            ( "CreatedDate", dd(self.CreatedDate) ),
            ( "LastChangedBy", ds(self.LastChangedBy) ),
            ( "LastChangedDate", dd(self.LastChangedDate) )
            )
        return makesql("animalvaccination", s)


class Animal:
    ID = 0
    AnimalTypeID = 1
    AnimalName = ""
    NonShelterAnimal = 0
    BaseColourID = 0
    SpeciesID = 1
    BreedID = 1
    Breed2ID = 1
    BreedName = ""
    CrossBreed = 0
    CoatType = 0
    Markings = ""
    ShelterCode = ""
    ShortCode = ""
    UniqueCodeID = 0
    YearCodeID = 0
    AcceptanceNumber = ""
    DateOfBirth = today()
    DeceasedDate = None
    Sex = 0
    Identichipped = 0
    IdentichipNumber = ""
    IdentichipDate = None
    Tattoo = 0
    TattooNumber = ""
    TattooDate = None
    Neutered = 0
    NeuteredDate = None
    CombiTested = 0
    CombiTestDate = None
    CombiTestResult = 0
    HeartwormTested = 0
    HeartwormTestDate = None
    HeartwormTestResult = 0
    FLVResult = 0
    Declawed = 0
    HiddenAnimalDetails = ""
    AnimalComments = ""
    OwnersVetID = 0
    OriginalOwnerID = 0
    BroughtInByOwnerID = 0
    ReasonForEntry = ""
    ReasonNO = ""
    AmountDonatedOnEntry = 0.0
    DateBroughtIn = today()
    EntryReasonID = 1
    HealthProblems = ""
    PutToSleep = 0
    PTSReason = ""
    PTSReasonID = 1
    IsDOA = 0
    IsTransfer = 0
    IsGoodWithCats = 0
    IsGoodWithDogs = 0
    IsGoodWithChildren = 0
    IsHouseTrained = 0
    IsNotAvailableForAdoption = 0
    HasSpecialNeeds = 0
    ShelterLocation = 1
    DiedOffShelter = 0
    Size = 0
    RabiesTag = ""
    Archived = 0
    ActiveMovementID = 0
    ActiveMovementType = 0
    ActiveMovementDate = None
    ActiveMovementReturn = None
    HasActiveReserve = 0
    MostRecentEntryDate = today()
    TimeOnShelter = ""
    AnimalAge = ""
    RecordVersion = 0
    CreatedBy = "conversion"
    CreatedDate = today()
    LastChangedBy = "conversion"
    LastChangedDate = today()
    ExtraID = ""
    def __init__(self, ID = 0):
        self.ID = ID
        if ID == 0: self.ID = getid("animal")
    def generateCode(self, type):
        """ Generates a sheltercode and shortcode for the animal
            to the default schemes.
            type is the animaltype name (eg: Unwanted Cat). The
            year is got from DateBroughtIn, the index maintained
            internally. """
        global nextyearcode
        self.YearCodeID = nextyearcode
        self.ShelterCode = "%s%d%03d" % ( type[0:1], self.DateBroughtIn.year, nextyearcode)
        self.ShortCode = "%03d%s" % (nextyearcode, type[0:1])
        nextyearcode += 1
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "AnimalTypeID", di(self.AnimalTypeID) ),
            ( "AnimalName", ds(self.AnimalName) ),
            ( "NonShelterAnimal", di(self.NonShelterAnimal) ),
            ( "BaseColourID", di(self.BaseColourID) ),
            ( "SpeciesID", di(self.SpeciesID) ),
            ( "BreedID", di(self.BreedID) ),
            ( "Breed2ID", di(self.Breed2ID) ),
            ( "BreedName", ds(self.BreedName) ),
            ( "CrossBreed", di(self.CrossBreed) ),
	    ( "CoatType", di(self.CoatType) ),
            ( "Markings", ds(self.Markings) ),
            ( "ShelterCode", ds(self.ShelterCode) ),
            ( "ShortCode", ds(self.ShortCode) ),
            ( "UniqueCodeID", di(self.UniqueCodeID) ),
            ( "YearCodeID", di(self.YearCodeID) ),
            ( "AcceptanceNumber", ds(self.AcceptanceNumber) ),
            ( "DateOfBirth", dd(self.DateOfBirth) ),
            ( "DeceasedDate", dd(self.DeceasedDate) ),
            ( "Sex", di(self.Sex) ),
            ( "Identichipped", di(self.Identichipped) ),
            ( "IdentichipNumber", ds(self.IdentichipNumber) ),
            ( "IdentichipDate", dd(self.IdentichipDate) ),
            ( "Tattoo", di(self.Tattoo) ),
            ( "TattooNumber", ds(self.TattooNumber) ),
            ( "TattooDate", dd(self.TattooDate) ),
            ( "Neutered", di(self.Neutered) ),
            ( "NeuteredDate", dd(self.NeuteredDate) ),
            ( "CombiTested", di(self.CombiTested) ),
            ( "CombiTestDate", dd(self.CombiTestDate) ),
            ( "CombiTestResult", di(self.CombiTestResult) ),
            ( "HeartwormTested", di(self.HeartwormTested) ),
            ( "HeartwormTestDate", dd(self.HeartwormTestDate) ),
            ( "HeartwormTestResult", di(self.HeartwormTestResult) ),
            ( "FLVResult", di(self.FLVResult) ),
            ( "Declawed", di(self.Declawed) ),
            ( "HiddenAnimalDetails", ds(self.HiddenAnimalDetails) ),
            ( "AnimalComments", ds(self.AnimalComments) ),
            ( "OwnersVetID", di(self.OwnersVetID) ),
            ( "OriginalOwnerID", di(self.OriginalOwnerID) ),
            ( "BroughtInByOwnerID", di(self.BroughtInByOwnerID) ),
            ( "ReasonForEntry", ds(self.ReasonForEntry) ),
            ( "ReasonNO", ds(self.ReasonNO) ),
            ( "AmountDonatedOnEntry", df(self.AmountDonatedOnEntry) ),
            ( "DateBroughtIn", dd(self.DateBroughtIn) ),
            ( "EntryReasonID", di(self.EntryReasonID) ),
            ( "HealthProblems", ds(self.HealthProblems) ),
            ( "PutToSleep", di(self.PutToSleep) ),
            ( "PTSReason", ds(self.PTSReason) ),
            ( "PTSReasonID", di(self.PTSReasonID) ),
            ( "IsDOA", di(self.IsDOA) ),
            ( "IsTransfer", di(self.IsTransfer) ),
            ( "IsGoodWithCats", di(self.IsGoodWithCats) ),
            ( "IsGoodWithDogs", di(self.IsGoodWithDogs) ),
            ( "IsGoodWithChildren", di(self.IsGoodWithChildren) ),
            ( "IsHouseTrained", di(self.IsHouseTrained) ),
            ( "IsNotAvailableForAdoption", di(self.IsNotAvailableForAdoption) ),
            ( "HasSpecialNeeds", di(self.HasSpecialNeeds) ),
            ( "ShelterLocation", di(self.ShelterLocation) ),
            ( "DiedOffShelter", di(self.DiedOffShelter) ),
            ( "Size", di(self.Size) ),
            ( "RabiesTag", ds(self.RabiesTag) ),
            ( "Archived", di(self.Archived) ),
            ( "ActiveMovementID", di(self.ActiveMovementID) ),
            ( "ActiveMovementType", di(self.ActiveMovementType) ),
            ( "ActiveMovementDate", dd(self.ActiveMovementDate) ),
            ( "ActiveMovementReturn", dd(self.ActiveMovementReturn) ),
            ( "HasActiveReserve", di(self.HasActiveReserve) ),
            ( "MostRecentEntryDate", dd(self.MostRecentEntryDate) ),
            ( "TimeOnShelter", ds(self.TimeOnShelter) ),
            ( "AnimalAge", ds(self.AnimalAge) ),
            ( "RecordVersion", di(self.RecordVersion) ),
            ( "CreatedBy", ds(self.CreatedBy) ),
            ( "CreatedDate", dd(self.CreatedDate) ),
            ( "LastChangedBy", ds(self.LastChangedBy) ),
            ( "LastChangedDate", dd(self.LastChangedDate) )
            )
        return makesql("animal", s)

class Movement:
    ID = 0
    AdoptionNumber = 0
    AnimalID = 0
    OwnerID = 0
    RetailerID = 0
    OriginalRetailerMovementID = 0
    MovementDate = None
    MovementType = 0
    ReturnDate = None
    ReturnedReasonID = 1
    InsuranceNumber = ""
    ReasonForReturn = ""
    ReservationDate = None
    Donation = 0.0
    ReservationCancelledDate = None
    Comments = ""
    RecordVersion = 0
    CreatedBy = "conversion"
    CreatedDate = today()
    LastChangedBy = "conversion"
    LastChangedDate = today()
    def __init__(self, ID = 0):
        self.ID = ID
        if ID == 0: self.ID = getid("adoption")
        self.AdoptionNumber = self.ID
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "AdoptionNumber", ds(self.AdoptionNumber) ),
            ( "AnimalID", di(self.AnimalID) ),
            ( "OwnerID", di(self.OwnerID) ),
            ( "RetailerID", di(self.RetailerID) ),
            ( "OriginalRetailerMovementID", di(self.OriginalRetailerMovementID) ),
            ( "MovementDate", dd(self.MovementDate) ),
            ( "MovementType", di(self.MovementType) ),
            ( "ReturnDate", dd(self.ReturnDate) ),
            ( "ReturnedReasonID", di(self.ReturnedReasonID) ),
            ( "InsuranceNumber", ds(self.InsuranceNumber) ),
            ( "ReasonForReturn", ds(self.ReasonForReturn) ),
            ( "ReservationDate", dd(self.ReservationDate) ),
            ( "Donation", df(self.Donation) ),
            ( "ReservationCancelledDate", dd(self.ReservationCancelledDate) ),
            ( "Comments", ds(self.Comments) ),
            ( "RecordVersion", di(self.RecordVersion) ),
            ( "CreatedBy", ds(self.CreatedBy) ),
            ( "CreatedDate", dd(self.CreatedDate) ),
            ( "LastChangedBy", ds(self.LastChangedBy) ),
            ( "LastChangedDate", dd(self.LastChangedDate) )
            )
        return makesql("adoption", s)

class Owner:
    ID = 0
    OwnerTitle = ""
    OwnerInitials = ""
    OwnerForeNames = ""
    OwnerSurname = ""
    OwnerName = ""
    OwnerAddress = ""
    OwnerTown = ""
    OwnerCounty = ""
    OwnerPostcode = ""
    HomeTelephone = ""
    WorkTelephone = ""
    MobileTelephone = ""
    EmailAddress = ""
    IDCheck = 0
    Comments = ""
    IsGiftAid = 0
    IsBanned = 0
    IsVolunteer = 0
    IsHomeChecker = 0
    IsMember = 0
    MembershipExpiryDate = None
    MembershipNumber = ""
    IsDonor = 0
    IsShelter = 0
    IsACO = 0
    IsStaff = 0
    IsFosterer = 0
    IsRetailer = 0
    IsVet = 0
    HomeCheckAreas = ""
    DateLastHomeChecked = None
    HomeCheckedBy = 0
    MatchAdded = None
    MatchExpires = None
    MatchActive = 0
    MatchSex = 0
    MatchSize = 0
    MatchAgeFrom = 0
    MatchAgeTo = 0
    MatchAnimalType = 0
    MatchSpecies = 0
    MatchBreed = 0
    MatchBreed2 = 0
    MatchGoodWithCats = 0
    MatchGoodWithDogs = 0
    MatchGoodWithChildren = 0
    MatchHouseTrained = 0
    MatchCommentsContain = ""
    RecordVersion = 0
    CreatedBy = "conversion"
    CreatedDate = today()
    LastChangedBy = "conversion"
    LastChangedDate = today()
    def __init__(self, ID = 0):
        self.ID = ID
        if ID == 0: self.ID = getid("owner")
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "OwnerTitle", ds(self.OwnerTitle) ),
            ( "OwnerInitials", ds(self.OwnerInitials) ),
            ( "OwnerForeNames", ds(self.OwnerForeNames) ),
            ( "OwnerSurname", ds(self.OwnerSurname) ),
            ( "OwnerName", ds(self.OwnerName) ),
            ( "OwnerAddress", ds(self.OwnerAddress) ),
            ( "OwnerTown", ds(self.OwnerTown) ),
            ( "OwnerCounty", ds(self.OwnerCounty) ),
            ( "OwnerPostcode", ds(self.OwnerPostcode) ),
            ( "HomeTelephone", ds(self.HomeTelephone) ),
            ( "WorkTelephone", ds(self.WorkTelephone) ),
            ( "MobileTelephone", ds(self.MobileTelephone) ),
            ( "EmailAddress", ds(self.EmailAddress) ),
            ( "IDCheck", di(self.IDCheck) ),
            ( "Comments", ds(self.Comments) ),
            ( "IsGiftAid", di(self.IsGiftAid) ),
            ( "IsBanned", di(self.IsBanned) ),
            ( "IsVolunteer", di(self.IsVolunteer) ),
            ( "IsHomeChecker", di(self.IsHomeChecker) ),
            ( "IsMember", di(self.IsMember) ),
            ( "MembershipExpiryDate", dd(self.MembershipExpiryDate) ),
	    ( "MembershipNumber", ds(self.MembershipNumber) ),
            ( "IsDonor", di(self.IsDonor) ),
            ( "IsShelter", di(self.IsShelter) ),
            ( "IsACO", di(self.IsACO) ),
            ( "IsStaff", di(self.IsStaff) ),
            ( "IsFosterer", di(self.IsFosterer) ),
            ( "IsRetailer", di(self.IsRetailer) ),
            ( "IsVet", di(self.IsVet) ),
            ( "HomeCheckAreas", ds(self.HomeCheckAreas) ),
            ( "DateLastHomeChecked", dd(self.DateLastHomeChecked) ),
            ( "HomeCheckedBy", di(self.HomeCheckedBy) ),
            ( "MatchAdded", dd(self.MatchAdded) ),
            ( "MatchExpires", dd(self.MatchExpires) ),
            ( "MatchActive", di(self.MatchActive) ),
            ( "MatchSex", di(self.MatchSex) ),
            ( "MatchSize", di(self.MatchSize) ),
            ( "MatchAgeFrom", df(self.MatchAgeFrom) ),
            ( "MatchAgeTo", df(self.MatchAgeTo) ),
            ( "MatchAnimalType", di(self.MatchAnimalType) ),
            ( "MatchSpecies", di(self.MatchSpecies) ),
            ( "MatchBreed", di(self.MatchBreed) ),
            ( "MatchBreed2", di(self.MatchBreed2) ),
            ( "MatchGoodWithCats", di(self.MatchGoodWithCats) ),
            ( "MatchGoodWithDogs", di(self.MatchGoodWithDogs) ),
            ( "MatchGoodWithChildren", di(self.MatchGoodWithChildren) ),
            ( "MatchHouseTrained", di(self.MatchHouseTrained) ),
            ( "MatchCommentsContain", ds(self.MatchCommentsContain) ),
            ( "RecordVersion", di(self.RecordVersion) ),
            ( "CreatedBy", ds(self.CreatedBy) ),
            ( "CreatedDate", dd(self.CreatedDate) ),
            ( "LastChangedBy", ds(self.LastChangedBy) ),
            ( "LastChangedDate", dd(self.LastChangedDate) )
            )
        return makesql("owner", s)

class OwnerDonation:
    ID = 0
    OwnerID = 0
    MovementID = 0
    DonationTypeID = 1
    Date = None
    DateDue = None
    Donation = 0.0
    Comments = ""
    RecordVersion = 0
    CreatedBy = "conversion"
    CreatedDate = today()
    LastChangedBy = "conversion"
    LastChangedDate = today()
    def __init__(self, ID = 0):
        self.ID = ID
        if ID == 0: self.ID = getid("ownerdonation")
    def __str__(self):
        s = (
            ( "ID", di(self.ID) ),
            ( "OwnerID", di(self.OwnerID) ),
            ( "MovementID", di(self.MovementID) ),
            ( "DonationTypeID", di(self.DonationTypeID) ),
            ( "Date", dd(self.Date) ),
            ( "DateDue", dd(self.DateDue) ),
            ( "Donation", df(self.Donation) ),
            ( "Comments", ds(self.Comments) ),
            ( "RecordVersion", di(self.RecordVersion) ),
            ( "CreatedBy", ds(self.CreatedBy) ),
            ( "CreatedDate", dd(self.CreatedDate) ),
            ( "LastChangedBy", ds(self.LastChangedBy) ),
            ( "LastChangedDate", dd(self.LastChangedDate) )
            )
        return makesql("ownerdonation", s)

