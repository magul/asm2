#!/usr/bin/python

import animal
import db
import sys

def daily(dbo):
    """
    Tasks to run once each day
    """

    # Update on shelter animal location fields
    animal.update_all_animal_statuses(dbo)

    # Update on shelter animal variable data (age, time on shelter, etc)
    animal.update_all_variable_animal_data(dbo)



if __name__ == "__main__":
    
    dbo = db.DatabaseInfo()

    if len(sys.argv) < 2:
        print "Usage: cron.py daily [dbtype] [host] [username] [password] [database] [sqlite_file]"
        sys.exit(1)

    # Have command line arguments been passed to override the
    # defaults?
    if len(sys.argv) >= 6:
        dbo.dbtype = sys.argv[2]
        dbo.host = sys.argv[3]
        dbo.username = sys.argv[4]
        dbo.password = sys.argv[5]
        dbo.database = sys.argv[6]
        dbo.sqlite_file = sys.argv[7]

    daily(dbo)

