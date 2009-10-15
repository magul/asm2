#!/usr/bin/python

"""
    Generates some random data for ASM

    TODO: Make Owner use new scheme, randomise more elements.
"""

import random

MAX_OWNERS = 105
MAX_ANIMALS = 105

surnames = [ "Smith", "Jones", "Spencer", "Cusack", "Tetley", "Cole",
        "Adams", "Speed", "Swift", "Davison", "Neal", "Rawson",
        "Wilson", "Hargan", "Cooper", "Felcher", "Belcher", "James",
        "Jowitt", "Webb", "Churchill", "Johnson", "Jameson", "Espie",
        "Barker", "Brutnell", "Edley", "Ager", "Cole", "Henshaw", 
        "Szeto", "Whitaker", "Murray" ]

addresses = [ "Mendip", "Central", "Whitehill" ]

roads = [ "Street", "Road", "Drive", "Rise", "Close", "Crescent" ]

titles = [ "Mr", "Mrs" ]

maleforenames = [ "James", "John", "Robert", "Jonathan", "Andrew" ,
        "Charles", "Simon", "Alan", "Lee", "Ian", "Neil", "Anthony",
        "Barry", "Jacob", "Mark", "Mathew", "Glen", "Glyn", "Peter" ]

femaleforenames = [ "Pauline", "Polly", "Patricia", "Kylie", "Ebony",
        "Keeley", "Stacey", "Tracey", "Brigid", "Helen", "Mary", 
        "Anna", "Sophie", "Joanne" ]

animalnames = [ "Benji", "Zak", "Dexter", "Snoop", "Romeo", "Boo", 
        "Lynx", "Murdoch", "Gambon", "Sniff", "Squidge", "Socks",
        "Buttons", "Freak", "Mclovin", "Thing", "Frankie", "Steve",
        "Jaws", "Damien", "Tyson", "Jake", "Max", "Momma", "Fog", 
        "Shaguar", "Percy" ]

locations = [ "Dog Block 1", "Little Block", "Isolation Block", "Dog Block 2",
            "Cattery", "Case Block" ]

# IDs
next_location = 10
next_owner = 1000
next_animal = 1000

def make_sql(table, bits):
    s = "INSERT INTO %s (" % table
    cols = ""
    for i in bits:
        if cols != "": cols += ", "
        cols += i[0]
    vals = ""
    for i in bits:
        if vals != "": vals += ", "
        vals += i[1] % i[2]
    return s + cols + ") VALUES (" + vals + ");"

def today_sql():
    return "2009-02-27"

def random_date():
    return "%0.4d-%0.2d-%0.2d" % ( random.randint(2001, 2008), random.randint(1, 12), random.randint(1, 28) )

def add_location(name, next_location):
    print "INSERT INTO internallocation (ID, LocationName, LocationDescription) VALUES (%d, '%s', '')" % (next_location, name)
    next_location += 1
    return next_location

def add_owner(next_owner):
    title = random.choice(titles)
    if title == "Mr":
        forenames = random.choice(maleforenames)
    else:
        forenames = random.choice(femaleforenames)
    initials = forenames[0]
    surname = random.choice(surnames)
    name = "%s %s %s" % (title, forenames, surname)
    address = "%d %s %s" % ( random.randint(1, 500), random.choice(addresses), random.choice(roads) )
    bits = (
            ( "ID", "%d", next_owner ),
            ( "OwnerTitle", "'%s'", title ),
            ( "OwnerInitials", "'%s'", initials ),
            ( "OwnerForeNames", "'%s'", forenames ),
            ( "OwnerSurname", "'%s'", surname ),
            ( "OwnerName", "'%s'", name ),
            ( "OwnerAddress", "'%s'", address ),
            ( "OwnerTown", "'%s'", "Demo Town" ),
            ( "OwnerCounty", "'%s'", "Demoshire" ),
            ( "OwnerPostcode", "'%s'", "D1 EM0" ),
            ( "HomeTelephone", "'%s'", "03489 398983" ),
            ( "WorkTelephone", "'%s'", "02389 928932" ),
            ( "MobileTelephone", "'%s'", "07893 398433" ),
            ( "EmailAddress", "'%s'", "random@random.com" ),
            ( "IDCheck", "%d", 0 ),
            ( "Comments", "'%s'", "" ),
            ( "IsBanned", "%d", 0 ),
            ( "IsVolunteer", "%d", 0 ),
            ( "IsHomeChecker", "%d", 0 ),
            ( "IsMember", "%d", 0 ),
            ( "MembershipExpiryDate", "%s", "null" ),
            ( "IsDonor", "%d", 0 ),
            ( "IsShelter", "%d", 0 ),
            ( "IsACO", "%d", 0 ),
            ( "IsStaff", "%d", 0 ),
            ( "IsFosterer", "%d", 0 ),
            ( "IsRetailer", "%d", 0 ),
            ( "IsVet", "%d", 0 ),
            ( "HomeCheckAreas", "'%s'", "" ),
            ( "DateLastHomeChecked", "%s", "null" ),
            ( "DatePerformedLastHomeCheck", "%s", "null" ),
            ( "MatchActive", "%d", 0 ),
            ( "MatchSex", "%d", 0 ),
            ( "MatchSize", "%d", 0 ),
            ( "MatchAgeFrom", "%d", 0 ),
            ( "MatchAgeTo", "%d", 0 ),
            ( "MatchSpecies", "%d", 0 ),
            ( "MatchBreed", "%d", 0 ),
            ( "MatchGoodWithCats", "%d", 0 ),
            ( "MatchGoodWithDogs", "%d", 0 ),
            ( "MatchGoodWithChildren", "%d", 0 ),
            ( "MatchHouseTrained", "%d", 0 ),
            ( "MatchCommentsContain", "'%s'", "" ),
            ( "RecordVersion", "%d", 0 ),
            ( "CreatedBy", "'%s'", "random" ),
            ( "CreatedDate", "'%s'", today_sql() ),
            ( "LastChangedBy", "'%s'", "random" ),
            ( "LastChangedDate", "'%s'", today_sql() )
        )

    print make_sql("owner", bits)
    next_owner += 1
    return next_owner

def add_animal(next_animal):

    bits = (
            ( "ID", "%d", next_animal ),
            ( "AnimalTypeID", "%d", random.randint(1, 2) ),
            ( "AnimalName", "'%s'", random.choice(animalnames) ),
            ( "NonShelterAnimal", "%d", 0 ),
            ( "BaseColourID", "%d", 1 ),
            ( "SpeciesID", "%d", 1 ),
            ( "BreedID", "%d", random.randint(1, 220) ),
            ( "Markings", "'%s'", "" ),
            ( "ShelterCode", "'%s'", str(next_animal) ),
            ( "ShortCode", "'%s'", str(next_animal) ),
            ( "UniqueCodeID", "%d", next_animal ),
            ( "YearCodeID", "%d", next_animal ),
            ( "AcceptanceNumber", "'%s'", ""),
            ( "DateOfBirth", "'%s'", random_date() ),
            ( "DeceasedDate", "%s", "null" ),
            ( "DiedOffShelter", "%d", 0 ),
            ( "Sex", "%d", random.randint(0, 1) ),
            ( "Size", "%d", random.randint(0, 3) ),
            ( "ShelterLocation", "%d", random.randint(10, 15) ),
            ( "Identichipped", "%d", 0 ),
            ( "IdentichipNumber", "'%s'", "" ),
            ( "IdentichipDate", "%s", "null" ),
            ( "Tattoo", "%d", 0 ),
            ( "TattooNumber", "'%s'", "" ),
            ( "TattooDate", "%s", "null" ),
            ( "Neutered", "%d", 1 ),
            ( "NeuteredDate", "%s", "null" ),
            ( "CombiTested", "%d", 0 ),
            ( "CombiTestResult", "%d", 0 ),
            ( "HeartwormTested", "%d", 0 ),
            ( "HeartwormTestDate", "%s", "null" ),
            ( "HeartwormTestResult", "%d", 0 ),
            ( "FLVResult", "%d", 0 ),
            ( "Declawed", "%d", 0 ),
            ( "HiddenAnimalDetails", "'%s'", "" ),
            ( "AnimalComments", "'%s'", "" ),
            ( "OwnersVetID", "%d", 0 ),
            ( "OriginalOwnerID", "%d", random.randint(1000, 1100) ),
            ( "BroughtInByOwnerID", "%d", random.randint(1000, 1100) ),
            ( "ReasonForEntry", "'%s'", "" ),
            ( "ReasonNO", "'%s'", "" ),
            ( "AmountDonatedOnEntry", "%d", 0 ),
            ( "DateBroughtIn", "'%s'", random_date() ),
            ( "EntryReasonID", "%d", 1 ),
            ( "HealthProblems", "'%s'", "" ),
            ( "PutToSleep", "%d", 0 ),
            ( "PTSReason", "'%s'", "" ),
            ( "PTSReasonID", "%d", 0 ),
            ( "IsDOA", "%d", 0 ),
            ( "IsTransfer", "%d", 0 ),
            ( "IsGoodWithCats", "%d", 0 ),
            ( "IsGoodWithDogs", "%d", 0 ),
            ( "IsGoodWithChildren", "%d", 0 ),
            ( "IsHouseTrained", "%d", 0 ),
            ( "IsNotAvailableForAdoption", "%d", 0 ),
            ( "HasSpecialNeeds", "%d", 0 ),
            ( "RabiesTag", "%s", "null" ),
            ( "Archived", "%d", 0 ),
            ( "ActiveMovementID", "%d", 0 ),
            ( "ActiveMovementType", "%s", "null" ),
            ( "ActiveMovementDate", "%s", "null" ),
            ( "ActiveMovementReturn", "%s", "null" ),
            ( "HasActiveReserve", "%d", 0 ),
            ( "MostRecentEntryDate", "'%s'", random_date() ),
            ( "TimeOnShelter", "'%s'", "" ),
            ( "AnimalAge", "'%s'", "" ),
            ( "RecordVersion", "%d", 0 ),
            ( "CreatedBy", "'%s'", "random" ),
            ( "CreatedDate", "'%s'", today_sql() ),
            ( "LastChangedBy", "'%s'", "random" ),
            ( "LastChangedDate", "'%s'", today_sql() )
         )

    print make_sql("animal", bits)
    next_animal += 1
    return next_animal

# Remove existing stuff
print "DELETE FROM internallocation WHERE ID >= 10;"
print "DELETE FROM animal WHERE ID >= 1000;"
print "DELETE FROM owner WHERE ID >= 1000;"

# Create some internal locations
for l in locations:
    next_location = add_location(l, next_location)

# Create some owners
for i in range(0, MAX_OWNERS):
    next_owner = add_owner(next_owner)

# Create some animals
for i in range(0, MAX_ANIMALS):
    next_animal = add_animal(next_animal)
