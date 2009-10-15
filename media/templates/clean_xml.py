#!/usr/bin/python

"""
Simple script to go through an XML file, find the &lt;&lt; start
and &gt;&gt; end tags and strip all tags in between. This fixes
problems substituting tags due to formatting in HTML, XML,
Abiword and (if you extract document.xml and content.xml respectively) 
MSO2007 and OO.
"""

import sys, os

if len(sys.argv) == 1:
    print "Usage: clean_xml.py <file>"
    print "Finds &lt;&lt; and &gt;&gt; strings and removes any xml tags between them"
    sys.exit(1)

f = open(sys.argv[1], "r")
s = f.read();
f.close()

x = s.find("&lt;&lt;")
while (x != -1):
    y = s.find("&gt;&gt;", x)
    lump = s[x:y+8]

    sp = lump.find("<")
    ep = lump.find(">")
    while (sp != -1 and ep != -1):
        lump = lump[0:sp] + lump[ep+1:]
        sp = lump.find("<")
        ep = lump.find(">")
       
    s = s[0:x] + lump + s[y+8:]
    x = s.find("&lt;&lt;", x + 1)


# Now that we've fixed it, reopen the file and rewrite
f = open(sys.argv[1], "w")
f.write(s)
f.flush()
f.close()

