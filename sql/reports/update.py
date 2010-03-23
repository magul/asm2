#!/usr/bin/python

"""
Creates the single "report.txt" file and uploads it to my webserver
for sheltermanager.sf.net for users of the program to access and
get new reports
"""

import os

de = os.listdir(".")
s = ""

for d in de:
    if d.endswith(".txt"):
        f = open(d, "r")
        if s != "": s += "&&&\n"
        s += f.read()
        f.close()

f = open("reports.txt", "w")
f.write(s)
f.flush()
f.close()

# Upload to the server
os.system("scp reports.txt root@rawsoaa3.miniserver.com:/var/www/sourceforge/sheltermanager")

# Remove the temp file
os.system("rm -f reports.txt")
