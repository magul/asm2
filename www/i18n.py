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

def date_diff_days(date1, date2):
    """
    Returns the difference in days between two dates. It's
    assumed that date2 > date1
    (datetime) date1
    (datetime) date2
    """
    delta = date2 - date1
    return delta.days

def date_diff(date1, date2):
    """
    Returns a string representing the difference between two
    dates. Eg: 6 weeks and 3 days.
    It is expected that date2 > date1
    (datetime) date1
    (datetime) date2
    """
    days = date_diff_days(date1, date2)
    weeks = days / 7
    
    # If it's less than 16 weeks, show as weeks
    if weeks < 16:
        return "{0} weeks.".format(weeks)
    else:
        # Show in years and months
        weeks = days / 7
        years = weeks / 52
        months = float(weeks % 52)
        months = int((months / 52.0) * 12)
        return "{0} years and {1} months.".format(years, months)

def now():
    """
    Returns a python date representing now
    """
    return datetime.datetime.now()
