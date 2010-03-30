#!/usr/bin/python

import asm
import codecs

f = open("ruta.txt", "r")
lines = f.readlines()
nextid = 20000

for l in lines:
	l = l.strip()
	o = asm.Owner(nextid)
	nextid += 1
	spsplit = l.split(" ")
	if len(spsplit) == 2:
		if spsplit[1].find("-") == -1:
			o.OwnerForeNames = spsplit[0]
			o.OwnerSurname = spsplit[1]
		else:
			hsplit = spsplit[1].split("-")
			o.OwnerForeNames = spsplit[0] + " " + hsplit[0]
			o.OwnerSurname = hsplit[1]
	if len(spsplit) == 3:
		o.OwnerForeNames = spsplit[0] + " " + spsplit[1]
		o.OwnerSurname = spsplit[2]
	o.OwnerName = o.OwnerForeNames + " " + o.OwnerSurname
	o.IsDonor = 1
        print o
