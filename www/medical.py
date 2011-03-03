#!/usr/bin/python

import datetime
import db
import i18n

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

