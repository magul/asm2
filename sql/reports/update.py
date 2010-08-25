#!/usr/bin/python

"""
Creates the single "report.txt" file and uploads it to the sheltermanager.com
servre for users of the program to access and get new reports
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
os.system("scp reports.txt root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/repo/")

# Remove the temp file
os.system("rm -f reports.txt")
