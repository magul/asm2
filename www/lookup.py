#!/usr/bin/python

"""
        Module containing code for retrieving lookup data
        from ASM databases.
"""

import db

def config_get(k, default=""):
    """
    Retrieves a config value from the database
    """
    v = db.query_string("SELECT ItemValue FROM configuration WHERE ItemName Like '%s'" % k)
    if v == "": v = default
    return v

def config_set(k, v):
    """
    Sets a config value in the database
    """
    db.execute("DELETE FROM configuration WHERE ItemName Like '%s'" % k)
    db.execute("INSERT INTO configuration VALUES ('%s', '%s')" % ( k, v ))

