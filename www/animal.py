#!/usr/bin/python

"""
        Encapsulates functionality for the animal part of
        the database
"""

import configuration
import datetime
import db
import i18n
import movement

# Medical treatment rules
FIXED_LENGTH = 0
UNSPECIFIED_LENGTH = 1

# Medical statuses
ACTIVE = 0
HELD = 1
COMPLETED = 2

# Medical frequencies
ONEOFF = 0
DAILY = 0
WEEKLY = 1
MONTHLY = 2
YEARLY = 3

def get_animal(dbo, animalid):
    """
    Returns a complete animal row by id, or None if not found
    (int) animalid: The animal to get
    """
    rows = db.query(dbo, "SELECT * FROM animal WHERE ID = %d" % animalid)
    if rows == 0:
        return None
    else:
        return rows[0]

def get_vaccinations_outstanding(dbo):
    """
    Returns a recordset of animals awaiting vaccinations:
    VACCID, ANIMALID, SHELTERCODE, ANIMALNAME, DATEREQUIRED, COMMENTS, VACCINATIONTYPE
    """
    return db.query(dbo, 
        "SELECT av.ID AS vaccid, a.ID AS animalid, a.ShelterCode, " +
        "a.AnimalName, " +
        "av.DateRequired, av.Comments, vt.VaccinationType " +
        "FROM animal a " +
        "INNER JOIN animalvaccination av ON a.ID = av.AnimalID " +
        "INNER JOIN vaccinationtype vt ON vt.ID = av.VaccinationID " +
        "WHERE av.DateRequired Is Not Null AND av.DateOfVaccination Is Null " +
        "AND av.DateRequired <= " + db.dd(i18n.now()) + " " +
        "ORDER BY av.DateRequired, a.AnimalName")

def get_treatments_outstanding(dbo):
    """
    Returns a recordset of animals awaiting medical treatments:
    AMTID, AMID, ANIMALID, SHELTERCODE, ANIMALNAME, DATEREQUIRED, STARTDATE, 
    AMCOMMENTS, TREATMENTNAME, DOSAGE
    """
    return db.query(dbo, 
        "SELECT amt.ID AS AMTID, am.ID AS AMID, a.ID AS ANIMALID, " +
        "a.ShelterCode, a.AnimalName, amt.DateRequired, am.StartDate, " +
        "am.Comments AS AMCOMMENTS, am.TreatmentName, am.Dosage " +
        "FROM animal a " +
        "INNER JOIN animalmedical am ON a.ID = am.AnimalID " +
        "INNER JOIN animalmedicaltreatment amt ON amt.AnimalMedicalID = am.ID " +
        "WHERE amt.DateRequired Is Not Null AND amt.DateGiven Is Null " +
        "AND amt.DateRequired <= " + db.dd(i18n.now()) + " " +
        "ORDER BY amt.DateRequired, a.AnimalName")

def update_vaccination_today(dbo, username, vaccid):
    """
    Marks a vaccination record as given today. 
    """
    db.execute(dbo, db.make_update_user_sql("animalvaccination", username, "ID = %d" % vaccid, (
        ( "DateOfVaccination", db.dd(i18n.now()) ), 
        None )
        ))

def update_medical_treatments(dbo, username, amid):
    """
    Called on creation of an animalmedical record and after the saving
    of a treatment record. This handles creating the next treatment
    in the sequence.

    1. Check if the record is still active, but has all treatments
       given, mark it complete if true
    2. Ignore completed records
    3. If the record has no treatments, generate one from the master
    4. If the record has no outstanding treatment records, generate
       one from the last administered record
    5. If we generated a record, increment the tally of given and
       reduce the tally of remaining. If TreatmentRule is unspecified,
       ignore this step
    """
    am = db.query(dbo, "SELECT * FROM animalmedical WHERE ID = %d" % amid)[0]
    amt = db.query(dbo, "SELECT * FROM animalmedicaltreatment " +
        "WHERE AnimalMedicalID = %d ORDER BY DateRequired DESC" % amid)

    # Drop out if it's inactive
    if am["STATUS"] != ACTIVE:
        return

    # If it's a one-off treatment and we've given it, mark complete
    if am["TIMINGRULE"] == ONEOFF:
        if len(amt) > 0:
            if amt[0]["DATEGIVEN"] != None:
                db.execute(dbo, "UPDATE animalmedical SET Status = %d WHERE ID = %d" % ( COMPLETED, amid ))
                return

    # If it's a fixed length treatment, check to see if it's 
    # complete
    if am["TREATMENTRULE"] == FIXED_LENGTH:
        
        # Do we have any outstanding treatments? 
        # Drop out if we do
        ost = db.query_int(dbo, "SELECT COUNT(ID) FROM animalmedicaltreatment " +
            "WHERE AnimalMedicalID = %d AND DateGiven Is Null" % amid)
        if ost > 0:
            return

        # Does the number of treatments given match the total? 
        # Mark the record complete if so and we're done
        if am["TREATMENTSGIVEN"] == am["TOTALNUMBEROFTREATMENTS"]:
            db.execute(dbo, "UDPATE animalmedical SET Status = %d WHERE ID = %d" % ( COMPLETED, amid ))
            return

    # If there aren't any treatment records at all, create
    # one now
    if len(amt) == 0:
        insert_treatments(dbo, username, amid, am["STARTDATE"], True)
    else:
        # We've got some treatments, use the latest given
        # date (desc order). If it doesn't have a given date then there's
        # still an outstanding treatment and we can bail
        if amt[0]["DATEGIVEN"] == None:
            return

        insert_treatments(dbo, username, amid, amt[0]["DATEGIVEN"], False)

def insert_treatments(dbo, username, amid, requireddate, isstart = True):
    """
    Creates new treatment records for the given medical record
    with the required date given. isstart says that the date passed
    is the real start date, so don't look at the timing rule to 
    calculate the next date.
    """
    am = db.query(dbo, "SELECT * FROM animalmedical WHERE ID = %d" % amid)[0]
    nofreq = int(am["TIMINGRULENOFREQUENCIES"])
    if not isstart:
        if am["TIMINGRULEFREQUENCY"] == DAILY:
            requireddate += datetime.timedelta(days=nofreq)
        if am["TIMINGRULEFREQUENCY"] == WEEKLY:
            requireddate += datetime.timedelta(days=nofreq*7)
        if am["TIMINGRULEFREQUENCY"] == MONTHLY:
            requireddate += datetime.timedelta(days=nofreq*31)
        if am["TIMINGRULEFREQUENCY"] == YEARLY:
            requireddate += datetime.timedelta(days=nofreq*365)

    # Create correct number of records
    norecs = am["TIMINGRULE"]
    if norecs == 0: norecs = 1

    for x in range(1, norecs+1):
        sql = db.make_insert_user_sql("animalmedicaltreatment", username, (
            ( "ID", db.di(db.get_id(dbo, "animalmedicaltreatment"))),
            ( "RecordVersion", db.di(1)),
            ( "AnimalID", db.di(am["ANIMALID"]) ),
            ( "AnimalMedicalID", db.di(amid)),
            ( "DateRequired", db.dd(requireddate)),
            ( "DateGiven", db.dd(None)),
            ( "GivenBy", db.ds("")),
            ( "TreatmentNumber", db.di(x)),
            ( "TotalTreatments", db.di(norecs)),
            ( "Comments", db.ds(""))
        ))
        db.execute(dbo, sql)

def update_treatment_today(dbo, username, amtid, amid):
    """
    Marks a treatment record as given today. 
    """
    db.execute(dbo, db.make_update_user_sql("animalmedicaltreatment", username, "ID = %d" % amtid, (
        ( "DateGiven", db.dd(i18n.now()) ), 
        ( "GivenBy", db.ds(username))
        )))

    # Update number of treatments given and remaining
    db.execute(dbo, "UPDATE animalmedical SET " +
        "TreatmentsGiven = TreatmentsGiven + 1, " +
        "TreatmentsRemaining = TreatmentsRemaining - 1 " +
        "WHERE ID = %d" % amid)

    # Generate next treatments in sequence or complete the
    # medical record appropriately
    update_medical_treatments(dbo, username, amid)

def get_most_recent_entry(dbo, animalid):
    """
    Returns the date the animal last entered the shelter
    (int) animalid: The animal to find the most recent entry date for
    """
    s = "SELECT MovementType, ReturnDate FROM adoption "
    s += "WHERE AnimalID = %d ORDER BY ReturnDate DESC" % animalid
    rows = db.query(dbo, s)

    # If there were no movement records, return the brought in date
    if len(rows) == 0:
        return get_date_brought_in(dbo, animalid)

    for r in rows:
        # Are we treating foster as on shelter? If so, skip
        # to the next movement instead
        if configuration.foster_on_shelter(dbo) and r["MOVEMENTTYPE"] == movement.FOSTER:
            continue

        # Otherwise, this will be the latest return date
        return r["RETURNDATE"]

    # If we got here, there was only foster movements
    return get_date_brought_in(dbo, animalid)

def get_time_on_shelter(dbo, animalid):
    """
    Returns the length of time the animal has been on the shelter as a 
    formatted string, eg: "6 weeks and 3 days"
    (int) animalid: The animal to calculate time on shelter for
    """
    mre = get_most_recent_entry(dbo, animalid)
    stop = i18n.now()
    animal = get_animal(dbo, animalid)

    # If the animal is dead, or has left the shelter
    # use that date as our cutoff instead
    if animal["DECEASEDDATE"] != None:
        stop = animal["DECEASEDDATE"]
    elif animal["ACTIVEMOVEMENTDATE"] != None:
        stop = animal["ACTIVEMOVEMENTDATE"]

    # Format it as time period
    return i18n.date_diff(mre, stop)

def get_days_on_shelter(dbo, animalid):
    """
    Returns the number of days an animal has been on the shelter as an int
    (int) animalid: The animal to get the number of days on shelter for
    """
    mre = get_most_recent_entry(dbo, animalid)
    stop = i18n.now()
    animal = get_animal(dbo, animalid)

    # If the animal is dead, or has left the shelter
    # use that date as our cutoff instead
    if animal["DECEASEDDATE"] != None:
        stop = animal["DECEASEDDATE"]
    elif animal["ACTIVEMOVEMENTDATE"] != None:
        stop = animal["ACTIVEMOVEMENTDATE"]

    return i18n.date_diff_days(mre, stop)

def get_date_of_birth(dbo, animalid):
    """
    Returns an animal's date of birth
    (int) animalid: The animal to get the dob
    """
    return db.query_date(dbo, "SELECT DateOfBirth FROM animal WHERE ID = %d" % animalid)

def get_date_brought_in(dbo, animalid):
    """
    Returns the date an animal was brought in
    (int) animalid: The animal to get the brought in date from
    """
    return db.query_date(dbo, "SELECT DateBroughtIn FROM animal WHERE ID = %d" % animalid)

def get_age_group(dbo, animalid):
    """
    Returns the age group the animal fits into based on its
    date of birth.
    (int) animalid: The animal to calculate the age group for
    """

    # Calculate animal's age in days
    dob = get_date_of_birth(dbo, animalid)
    now = i18n.now()
    days = i18n.date_diff_days(dob, now)

    i = 1
    band = 0
    lastband = 0

    while True:

        # Get the next age group band
        band = configuration.age_group(dbo, i)
        if band == 0:
            break

        # The band figure is expressed in years, convert it to days
        band *= 365

        # Does the animal's current age fall into this band?
        if days >= lastband and days <= band:
            return configuration.age_group_name(dbo, i)

        lastband = band
        i += 1

    # Out of bands and none matched
    return ""

def get_age(dbo, animalid):
    """
    Returns an animal's age as a readable string
     (int) animalid: The animal to calculate time on shelter for
    """
    dob = get_date_of_birth(dbo, animalid)
    stop = i18n.now()
    animal = get_animal(dbo, animalid)

    # If the animal is dead, or has left the shelter
    # use that date as our cutoff instead
    if animal["DECEASEDDATE"] != None:
        stop = animal["DECEASEDDATE"]
    elif animal["ACTIVEMOVEMENTDATE"] != None:
        stop = animal["ACTIVEMOVEMENTDATE"]

    # Format it as time period
    return i18n.date_diff(dob, stop)

def update_variable_animal_data(dbo, animalid):
    """
    Updates the variable data animal fields
    (int) animalid: The animal to update
    """
    s = db.make_update_sql("animal", "ID = %d" % animalid, (
        ( "MostRecentEntryDate", db.dd(get_most_recent_entry(dbo, animalid))),
        ( "TimeOnShelter", db.ds(get_time_on_shelter(dbo, animalid))),
        ( "AgeGroup", db.ds(get_age_group(dbo, animalid))),
        ( "AnimalAge", db.ds(get_age(dbo, animalid))),
        ( "DaysOnShelter", db.di(get_days_on_shelter(dbo, animalid))) 
    ))
    db.execute(dbo, s)

def update_all_variable_animal_data(dbo):
    """
    Updates variable animal data for all animals on shelter
    """

    # We only need to do this once a day, skip if it's already
    # been run
    if configuration.variable_data_updated_today(dbo):
        return

    # Update variable data for each animal
    animals = db.query(dbo, "SELECT ID FROM animal")
    for a in animals:
        update_variable_animal_data(dbo, int(a["ID"]))

    # Mark the data as updated today
    configuration.set_variable_data_updated_today(dbo)

