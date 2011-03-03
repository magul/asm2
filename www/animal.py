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

