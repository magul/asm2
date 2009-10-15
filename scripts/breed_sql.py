#!/usr/bin/python
"""
	Generates INSERT sql for the breed table from a list
	of items in a file.
"""

import sys

if len(sys.argv) < 2:
	print "Usage: breed_sql.py <file>"
	sys.exit(1)

f = open(sys.argv[1])
l = f.readlines()
id = 1
for i in l:
	print "INSERT INTO breed VALUES (%d, '%s', '', '');" % ( id, i.strip().replace("'", "`") )
	id = id + 1
