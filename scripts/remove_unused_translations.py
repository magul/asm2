#!/usr/bin/python

"""
    Looks for unused translations in a source file. Accepts a single
    source file, followed by a list of property files. If the property
    files have an underscore in the name, they are ignored, since the
    purpose of this is to remove unused keys from the template.
"""

import sys, os

# Open the source file and cache it in RAM
print "Reading source from: %s" % sys.argv[1]
f = open(sys.argv[1], "r")
s = f.read()
f.close()

totunused = 0

# Go through each property file given
for x in range(2, len(sys.argv)):

    # Not a template, skip
    if sys.argv[x].find("_") != -1: continue

    # Don't do database - that's special
    if sys.argv[x].find("database") != -1: continue

    print "Processing: %s" % sys.argv[x]

    p = open(sys.argv[x], "r")
    lines = p.readlines()
    p.close()

    p = open(sys.argv[x], "w")

    for l in lines:

        if l.find("=") != -1:
            # We have a token, let's check it
            token = l[0:l.find("=")]
            token = token.replace("\\:", ":")

            if s.find("\"" + token + "\"") == -1:
                print "    " + token
                totunused += 1
            else:
                # It's used - write the line
                p.write(l)
        else:
            # It's not a token, write it back out
            p.write(l)

    p.close()

print "TOTAL UNUSED KEYS REMOVED: %d" % totunused
