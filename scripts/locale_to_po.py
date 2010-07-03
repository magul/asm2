#!/usr/bin/python

"""
Given a java locale (eg: en_GB), returns it as a po filename, eg:
lt_LT becomes lt.po 

If the country is the same as the language, flattens it to conform to gettext
conventions.
"""

import sys

locale = sys.argv[1]
bits = locale.split("_")

# Special case for Sweden
if locale == "sv_SE":
    print "sv.po"
# Special case for Estonian
elif locale == "et_EE":
    print "et.po"
# Special case for Hebrew
elif locale == "iw_IL":
    print "he.po"
elif bits[0] == bits[1].lower():
    print bits[0] + ".po"
else:
    print locale + ".po"
