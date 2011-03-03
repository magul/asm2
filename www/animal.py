#!/usr/bin/python

import configuration
import db
import i18n
import movement

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

def get_most_recent_entry(dbo, animalid):
    """
    Returns the date the animal last entered the shelter
    (int) animalid: The animal to find the most recent entry date for
    """
    s = "SELECT MovementType, ReturnDate FROM adoption "
    s += "WHERE AnimalID = %d AND ReturnDate Is Not Null " % animalid
    s += "ORDER BY ReturnDate DESC"
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

def get_deceased_date(dbo, animalid):
    """
    Returns an animal's deceased date
    (int) animalid: The animal to get the deceased date
    """
    return db.query_date(dbo, "SELECT DeceasedDate FROM animal WHERE ID = %d" % animalid)


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
    deceased = get_deceased_date(dbo, animalid)
    stop = i18n.now()

    # If the animal is dead, stop there
    if deceased != None:
        stop = deceased

    # Format it as time period
    return i18n.date_diff(dob, stop)

def get_latest_movement(dbo, animalid):
    """
    Returns the latest movement for an animal. The return
    value is a resultset of the movement itself or None
    if the animal has no movements.
    """
    reserve = db.query(dbo, "SELECT * FROM adoption WHERE " +
        "AnimalID = %d AND ReservationDate Is Not Null ORDER BY ReservationDate DESC" % animalid)
    move = db.query(dbo, "SELECT * FROM adoption WHERE " +
        "AnimalID = %d AND MovementDate Is Not Null ORDER BY MovementDate DESC" % animalid)

    # If we don't have any movements, used the latest reservation
    if len(move) == 0: 
        if len(reserve) > 0:
            return reserve[0]
        else:
            return None
    else:
        # Use the latest movement
        return move[0]

def update_variable_animal_data(dbo, animalid):
    """
    Updates the variable data animal fields,
    MostRecentEntryDate, TimeOnShelter, AgrGroup, AnimalAge
    and DaysOnShelter
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
    Updates variable animal data for all animals
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

def update_on_shelter_variable_animal_data(dbo):
    """
    Updates variable animal data for all animals on shelter
    """

    # We only need to do this once a day, skip if it's already
    # been run
    if configuration.variable_data_updated_today(dbo):
        return

    # Update variable data for each animal
    animals = db.query(dbo, "SELECT ID FROM animal WHERE Archived = 0")
    for a in animals:
        update_variable_animal_data(dbo, int(a["ID"]))

    # Mark the data as updated today
    configuration.set_variable_data_updated_today(dbo)


def update_all_animal_statuses(dbo):
    """
    Updates statuses for all animals
    """
    animals = db.query(dbo, "SELECT ID FROM animal")
    for a in animals:
        update_animal_status(dbo, int(a["ID"]))

def update_on_shelter_animal_statuses(dbo):
    """
    Updates statuses for all animals on shelter
    """
    animals = db.query(dbo, "SELECT ID FROM animal WHERE Archived = 0")
    for a in animals:
        update_animal_status(dbo, int(a["ID"]))


def update_animal_status(dbo, animalid):
    """
    Updates the movement status fields on an animal
    record. ActiveMovement*, HasActiveReserve, MostRecentEntryDate
    and Archived.
    """

    on_shelter = True
    has_reserve = False
    last_return = None

    a = get_animal(dbo, animalid)
    movements = db.query(dbo, "SELECT ID, MovementType, MovementDate, ReturnDate, " +
        "ReservationDate, ReservationCancelledDate FROM adoption " +
        "WHERE AnimalID = %d ORDER BY MovementDate DESC" % animalid)

    for m in movements:

        # If there's an open movement, our animal can't be on the shelter
        if (m["MOVEMENTDATE"] != None and m["RETURNDATE"] == None) or \
            (m["MOVEMENTDATE"] != None and m["RETURNDATE"] > i18n.now()):
            on_shelter = False

        # Does it have an active reservation?
        if m["RETURNDATE"] == None and m["MOVEMENTTYPE"] == movement.NO_MOVEMENT \
            and m["MOVEMENTDATE"] == None and m["RESERVATIONCANCELLEDDATE"] == None and \
            m["RESERVATIONDATE"] != None:
            has_reserve = True

        # Update the last time the animal was returned
        if m["RETURNDATE"] != None:
            if last_return == None: last_return = m["RETURNDATE"]
            if m["RETURNDATE"] > last_return: last_return = m["RETURNDATE"]


    # Override the on shelter flag if the animal is dead
    if a["DECEASEDDATE"] != None:
        on_shelter = False

    # Stamp our latest return date (or null if there isn't one)
    db.execute(dbo, "UPDATE animal SET ActiveMovementReturn = " + db.dd(last_return) + 
        " WHERE ID = %d" % animalid)

    # If the animal is on the shelter, it has no active movement
    if on_shelter:
        db.execute(dbo, "UPDATE animal SET ActiveMovementID = 0, ActiveMovementDate = Null, " +
            "ActiveMovementType = Null WHERE ID = %d" % animalid)
    else:
        # Find the latest movement for our animal
        latest = get_latest_movement(dbo, animalid)

        # We got one, put active movement info on the animal record
        if latest != None:

            db.execute(dbo, db.make_update_sql("animal", "ID=%d" % animalid, (
                ( "ActiveMovementID", db.di(latest["ID"]) ),
                ( "ActiveMovementDate", db.dd(latest["MOVEMENTDATE"]) ),
                ( "ActiveMovementType", db.di(latest["MOVEMENTTYPE"]) ),
                ( "ActiveMovementReturn", db.dd(latest["RETURNDATE"]) )
                )))

            # If the active movement is a foster and we're treating fosters
            # as on shelter, we should mark the animal as on shelter
            if latest["MOVEMENTTYPE"] == movement.FOSTER and configuration.foster_on_shelter(dbo) \
                and a["DECEASEDDATE"] == None:
                on_shelter = True


    # Update the on shelter/reserve flags
    archived = 1
    if on_shelter: archived = 0
    has_active_reserve = 0
    if has_reserve: has_active_reserve = 1
    db.execute(dbo, db.make_update_sql("animal", "ID=%d" % animalid, (
        ( "Archived", db.di(archived) ),
        ( "HasActiveReserve", db.di(has_active_reserve) )
        )))


