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

        if l.find("=") != -1 and not l.startswith("#"):
            # We have a token, let's check it
            token = l[0:l.find("=")]
            token = token.replace("\\:", ":")

            marker = s.find("\"" + token + "\"")
            if marker == -1:
                print "    " + token
                totunused += 1
            else:
                # Find all occurrences of the token and write a comment
                # for each file it appears in. We squash duplicates here too
                files = []
                while marker != -1:
                    fileref = s.rfind("##FILE:", 0, marker)
                    if fileref != -1:
                        fileref += 7
                        thefile = s[fileref:s.find("##", fileref)]
                        newfile = thefile
                        if not newfile in files:
                            files.append(newfile)
                    marker = s.find("\"" + token + "\"", marker+1)
                for f in files:
                    # Sanitise by removing any ./net/sourceforge/sheltermanager/asm
                    # prefix and .java suffix
                    # If it doesn't have that prefix, don't output it as a comment -
                    # we've coincidentally matched a string literal outside the
                    # translatable bit of the tree
                    x = f
                    if x.startswith("./net/sourceforge/sheltermanager/asm"):
                        x = x[37:]
                        if x.endswith(".java"):
                            x = x[0:len(x)-5]
                        p.write("# " + x + "\n")
                p.write(l)
        else:
            # It's not a token, ignore it
            # p.write(l)
            pass

    p.close()

print "TOTAL UNUSED KEYS REMOVED: %d" % totunused
