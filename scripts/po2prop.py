#!/usr/bin/python

"""
Reads a java properties template, a GNU PO file and outputs
the correct java properties file. I'm having to write this because
po2prop is shit, doesn't work properly and is broken in later
releases. Grrrr.
"""

import sys

if len(sys.argv) < 3:
    print "Usage: po2prop.py template.properties trans.po"
    sys.exit(1)

template = sys.argv[1]
pofile = sys.argv[2]

def chopvalue(s):
    return s[s.find("\""):len(s)-1].replace("\"", "")

def xmlchartojava(s):
    sp = s.find("&#")
    while sp != -1:
        ep = s.find(";", sp)
        dec = int(s[sp+2:ep])
        h = "\\u%04x" % dec
        s = s[0:sp] + h + s[ep+1:]
        sp = s.find("&#")
    return s

# Parse the po file
f = open(pofile, "r")
polines = f.readlines()
f.close()
po = {}
for pol in polines:
    if pol.startswith("msgid"):
        k = chopvalue(pol)
    if pol.startswith("msgstr"):
        l = pol.decode("utf-8")
        v = chopvalue(l.encode("ascii", "xmlcharrefreplace"))
        v = xmlchartojava(v)
        po[k.strip()] = v

# Parse the template file
f = open(template, "r")
tlines = f.readlines()
f.close()
temp = {}
for t in tlines:
    if t.find("=") != -1:
        k = t[0:t.find("=")].strip()
        v = t[t.find("=")+1:]
        temp[k.strip()] = v

# Now run through the template and produce the resulting properties
for k in sorted(temp.iterkeys()):
    v = temp[k]
    if po.has_key(v.strip()):
        ov = po[v.strip()]
        if ov.endswith("\\"): ov = ov[0:len(ov)-1]
        print k + "=" + ov
    else:
        ov = v.strip()
        if ov.endswith("\\"): ov = ov[0:len(ov)-1]
        print k + "=" + ov
