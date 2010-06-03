#!/usr/bin/python

"""
Given a po filename, returns a java locale, eg:
lt.po becomes lt_LT, en_GB.po becomes en_GB
"""

import sys

filename = sys.argv[1]
bits = filename.split(".")

# Special case for sweden
if filename.find("sv.po") != -1:
    print "sv_SE"
# Special case for estonian
elif filename.find("et.po") != -1:
    print "et_EE"
# Special case for hebrew
elif filename.find("he.po") != -1:
    print "iw_IL"
# Special case for slovak
elif filename.find("sk.po") != -1:
    print "sk_CZ"
elif filename.find("_") == -1:
    print bits[0] + "_" + bits[0].upper()
else:
    print bits[0]

