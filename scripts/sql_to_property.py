#!/usr/bin/python

"""
Reads an ASM SQL patch file and turns it into a java property file. This
assumes the file has already been re-encoded in java/latin1 format

args:

    filename - the original db filename (so we can extract lang)
    output_directory - where to put the property file
    input_file - the sql file

"""

import os, sys

filename = sys.argv[1];
outputdir = sys.argv[2];
inputfile = sys.argv[3];

infile = open(inputfile, "r")
lines = infile.readlines()
infile.close()

# Infer a java locale from the language code in translate_lang.sql
code = filename[filename.find("_")+1:filename.find(".")]
if code.find("_") != -1:
    code = code + "_" + code.upper()

# Swedish hack
if code == "sv": code = "_sv_SE"

# Estonian hack
if code == "et": code = "_et_EE"

# Hebrew hack
if code == "he": code = "_he_IL"

# Greek hack
if code == "el": code = "_el_GR"

# No lang for English as it's the template
if code == "en": code = ""

# Open the output file
outfilename = "database" + code + ".properties"
outfile = open(outputdir + "/database" + code + ".properties", "w")

# Loop through the list of lines in the file. We use the
# DELETE FROM lines to determine the current key prefix and we extract
# the ID from the first of the values list to complete the key.
# Depending on what table we're dealing with determines how many values

currentTable = ""

for l in lines:

    if l.startswith("DELETE"):
        # Get the new table
        currentTable = l.split(" ")[2].replace(";", "").strip()

    if l.startswith("INSERT"):
        # Get the values
        valuestring = l[l.find("(")+1:l.rfind(")")]
        values = valuestring.split(",")

        # ID
        id = values[0]

        # key prefix
        keyprefix = currentTable.strip() + "_" + id

        # Depending on the table determines what we extract
        if currentTable == "customreport":
            v = values[1].strip()
            if v.startswith("'"): v = v[1:]
            if v.endswith("'"): v = v[0:len(v)-1]
            cat = values[11].strip().replace("'", "")
            sqlcode = values[2].strip().replace("'", "")
            nextline = keyprefix + "_" + sqlcode + "=" + v
            outfile.write(nextline + "\n\n")
            outfile.write(keyprefix + "_category=" + cat + "\n\n")
        elif currentTable == "breed":
            v = values[1].strip()
            if v.startswith("'"): v = v[1:]
            if v.endswith("'"): v = v[0:len(v)-1]
            try:
                speciesid = values[4].strip()
            except:
                print "FAILED: " + valuestring
                speciesid = "1"
            nextline = keyprefix + "_" + speciesid + "=" + v
            outfile.write(nextline + "\n\n")
        elif currentTable == "users":
            # Ignore any users table - that should be kept in English
            # to match documentation
            pass
        elif currentTable == "configuration":
            v = values[1].strip()
            if v.startswith("'"): v = v[1:]
            if v.endswith("'"): v = v[0:len(v)-1]
            nextline = "configuration_" + id.replace("'", "") + "=" + v
            outfile.write(nextline + "\n\n")
        elif currentTable == "accounts":
            v = values[1].strip().replace("'", "")
            desc = values[2].strip().replace("'", "")
            atype = values[3].strip().replace("'", "")
            dontype = values[4].strip().replace("'", "")
            nextline = keyprefix + "_" + atype + "_" + dontype + "_code=" + v
            outfile.write(nextline + "\n\n")
            nextline = keyprefix + "_desc=" + desc
            outfile.write(nextline + "\n\n")
        else:
            # Must be a single value translation
            v = values[1].strip()
            if v.startswith("'"): v = v[1:]
            if v.endswith("'"): v = v[0:len(v)-1]
            nextline = keyprefix + "=" + v
            outfile.write(nextline + "\n\n")

outfile.close()
