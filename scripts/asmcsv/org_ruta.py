#!/usr/bin/python

import asm

f = open("ruta.txt", "r")
lines = f.readlines()

for l in lines:
	l = l.strip()
	o = asm.Owner()
	spsplit = l.split(" ")
	if len(spsplit) == 2:
		o.OwnerForenames = spsplit[0]
		o.OwnerSurname = spsplit[1].replace("-", " ")
	if len(spsplit) == 3:
		o.OwnerForeNames = spsplit[0] + " " + spsplit[1]
		o.OwnerSurname = spsplit[2]
	o.OwnerName = o.OwnerForeNames + " " + o.OwnerSurname
	o.IsDonor = 1
	print o
