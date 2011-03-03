#!/usr/bin/python

import datetime
import time

VERSION = "3.00"

# Default locale, overridden by calls to set_locale 
# from pages and modules
locale = "en_GB"

def _(english):
    """
    Returns a translation string for an English phrase. 

    """
    global locale

    # If we're dealing with UK English, then just
    # return the English phrase
    if locale == "en_GB":
        return english

    # Otherwise, look up the phrase in the correct
    # po file for our locale 
    # TODO:
    return english

def get_locale():
    """
    Returns the locale currently in use
    """
    global locale
    return locale

def set_locale(newlocale):
    """
    Sets the locale to be used for subsequent calls
    """
    global locale
    locale = newlocale

def get_version():
    """
    Returns the version of ASM
    """
    return VERSION

def get_display_date_format():
    """
    Returns the display date format for the current locale
    """
    # TODO: Have a key that internationalises this
    return "%d/%m/%Y"

def get_currency_symbol():
    """
    Returns the currency symbol for the current locale
    """
    return u"\xc2"

def python2display(d):
    """
    Formats a python date as a display string. 'd' is
    a Python date, return value is a display string.
    """
    if d == None: return ""
    return time.strftime(get_display_date_format(), d.timetuple())

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
        return _("{0} weeks.").format(weeks)
    else:
        # Show in years and months
        weeks = days / 7
        years = weeks / 52
        months = float(weeks % 52)
        months = int((months / 52.0) * 12)
        return _("{0} years and {1} months.").format(years, months)

def now():
    """
    Returns a python date representing now
    """
    return datetime.datetime.now()
