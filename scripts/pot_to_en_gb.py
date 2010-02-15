#!/usr/bin/python

"""
Creates an en_GB.po file from the .pot to stop people trying
to translate it in LP and wasting effort (the template is en_GB - I'm
English, why have Americans stolen our language from us?).
"""

import datetime

infile = open("src/locale/po/sheltermanager.pot", "r")
outfile = open("src/locale/po/en_GB.po", "w");

# Do the header first
d = datetime.datetime.today()
outfile.write("""msgid ""
msgstr ""
"Project-Id-Version: PACKAGE VERSION\\n"
"Report-Msgid-Bugs-To: \\n"
"PO-Revision-Date: %s+0000\\n"
"Last-Translator: Robin Rawson-Tetley <robin@rawsontetley.org>\\n"
"Language-Team: LANGUAGE <LL@li.org>\\n"
"MIME-Version: 1.0\\n"
"Content-Type: text/plain; charset=UTF-8\\n"
"Content-Transfer-Encoding: 8bit\\n"
"X-Generator: pottoengb\\n"

""" % (d.strftime("%Y-%m-%d %H:%M")))

# Read all the lines from the input
readingyet = False
lines = infile.readlines()
infile.close()
lastmsg = ""

for l in lines:
	
	if l.startswith("#. #"): readingyet = True
	if not readingyet: continue

	if l.startswith("msgid"): 
		lastmsg = l
		outfile.write(l)
		continue

	if l.startswith("msgstr"):
		outfile.write(lastmsg.replace("msgid", "msgstr"))
		continue

	outfile.write(l)

outfile.close()


