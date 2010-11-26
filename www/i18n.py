#!/usr/bin/python

"""
        ASM internationalisation functions
"""

import datetime
import time

VERSION = "3.00"
DISPLAY_DATE_FORMAT = "%d/%m/%Y"
DB_DATE_FORMAT = "%Y-%m-%d %H:%M:%S"

def get_version():
    """
    Returns the version of ASM
    """
    return VERSION

def python2display(d):
    """
    Formats a python date as a display string. 'd' is
    a Python date, return value is a display string.
    """
    if d == None: return ""
    return time.strftime(DISPLAY_DATE_FORMAT, d.timetuple())

def now():
    """
    Returns a python date representing now
    """
    return datetime.datetime.now()
