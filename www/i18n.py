#!/usr/bin/python

"""
        ASM internationalisation functions
"""

import datetime
import time

VERSION = "3.00"
DATE_FORMAT = "%d/%m/%Y"

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
    return time.strftime(DATE_FORMAT, d.timetuple())


