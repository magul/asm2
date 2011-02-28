#!/usr/bin/python

"""
        Regular tasks that need to be performed on a schedule
"""

import animal
import db
import sys

def daily(dbo):
    """
    Tasks to run once each day
    """
    animal.update_all_animal_variable_data(dbo)



if __name__ == "__main__":
    
    dbo = db.DatabaseInfo()

    # Have any command line arguments been passed to override the
    # defaults?
    if len(sys.argv >= 6):
        dbo.dbtype = sys.argv[1]
        dbo.host = sys.argv[2]
        dbo.username = sys.argv[3]
        dbo.password = sys.argv[4]
        dbo.database = sys.argv[5]
        dbo.sqlite_file = sys.argv[6]

    daily(dbo)
