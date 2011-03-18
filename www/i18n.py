#!/usr/bin/python

import datetime
import time

VERSION = "3.00"

def _(locale, english):
    """
    Returns a translation string for an English phrase in
    the locale given.
    """
    # If we're dealing with English, then just
    # return the English phrase. I hate that I'm doing
    # this, but I'm going with the accepted standard of
    # US English being default even though we invented
    # the bloody language.
    if locale == "en":
        return english

    # Otherwise, look up the phrase in the correct
    # po file for our locale 
    # TODO:
    return english

def get_version():
    """
    Returns the version of ASM
    """
    return VERSION

def get_display_date_format(locale):
    """
    Returns the display date format for the current locale
    """
    # TODO: Have a key that internationalises this
    return "%d/%m/%Y"

def get_currency_symbol(locale):
    """
    Returns the currency symbol for the current locale
    """
    return u"\xc2"

def python2display(locale, d):
    """
    Formats a python date as a display string. 'd' is
    a Python date, return value is a display string.
    """
    if d == None: return ""
    return time.strftime(get_display_date_format(locale), d.timetuple())

def date_diff_days(date1, date2):
    """
    Returns the difference in days between two dates. It's
    assumed that date2 > date1
    (datetime) date1
    (datetime) date2
    """
    delta = date2 - date1
    return delta.days

def date_diff(locale, date1, date2):
    """
    Returns a string representing the difference between two
    dates. Eg: 6 weeks and 3 days.
    It is expected that date2 > date1
    (datetime) date1
    (datetime) date2
    """
    days = int(date_diff_days(date1, date2))
    if days < 0: days = 0
    weeks = int(days / 7)
    
    # If it's less than 16 weeks, show as weeks
    if weeks < 16:
        return _(locale, "{0} weeks.").format(weeks)
    else:
        # Show in years and months
        weeks = int(days / 7)
        years = int(weeks / 52)
        months = float(weeks % 52)
        months = int((months / 52.0) * 12)
        return _(locale, "{0} years and {1} months.").format(years, months)

def now():
    """
    Returns a python date representing now
    """
    return datetime.datetime.now()
