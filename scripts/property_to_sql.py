#!/usr/bin/python

"""
Reads a Java properties file with the database translations in
and generates an SQL patch file.

args:

    file - the properties file to read
    output_directory - where to put sql patch file

"""

import os, sys

filename = sys.argv[1];
outputdir = sys.argv[2];

infile = open(filename, "r")
lines = infile.readlines()
infile.close()

# Languages we recognise and prop suffix - if we don't recognise, assume
# english and that we're generating a template (no language code)
code = ""
if filename.find("_") != 1:
    code = filename[filename.find("_")+1:filename.rfind(".")]
    bits = code.split("_")
    if bits[0] == bits[1].lower():
        code = bits[0]

# Swedish hack
if code == "sv_SE": code = "sv"

# Estonian hack
if code == "et_EE": code = "et"

# Hebrew hack
if code == "he_IL": code = "he"

# Greek hack
if code == "el_GR": code = "el"

# Open the output file
outfilename = "translate_" + code + ".sql"
outfile = open(outputdir + "/" + outfilename, "w")

# Loop through the list of lines in the file. The prefix determines
# what we write

seentables = []
cr_sql = ""
cr_val = ""

for l in lines:

    if l.startswith("#"): continue
    if l.strip() == "": continue

    kv = l.split("=")
    kb = l.split("_")
    value = kv[1].strip()
    table = kb[0]
    id = kb[1]
    if id.find("=") != -1: id = id[0:id.find("=")]

    # Escape any apostrophes
    value = value.replace("'", "''")

    seenit = False
    for t in seentables:
        if t == table:
            seenit = True
            break

    if not seenit:
        seentables.append(table)
        if table == "configuration":
            outfile.write("DELETE FROM configuration WHERE ItemName Like 'Organisation' OR ItemName Like 'AgeGroup%Name';\n");
        else:
            outfile.write("DELETE FROM %s;\n" % table)

    if table == "accounts":
        if not kb[2].startswith("desc"):
            acode = value
            atype = kb[2]
            dtype = kb[3]
        else:
            outfile.write("INSERT INTO accounts VALUES (%s, '%s', '%s', %s, %s, 0, 'translate', '2010-06-14 11:51:00', 'translate', '2010-06-14 11:51:00');\n" % ( id, acode, value, atype, dtype ))
    elif table == "animaltype":
        outfile.write("INSERT INTO animaltype VALUES (%s, '%s', NULL);\n" % (id, value))
    elif table == "basecolour":
        outfile.write("INSERT INTO basecolour VALUES (%s, '%s', NULL);\n" % (id, value))
    elif table == "breed":
        sid = kb[2]
        sid = sid[0:sid.find("=")]
        outfile.write("INSERT INTO breed VALUES (%s, '%s', '', '', %s);\n" % (id, value, sid))
    elif table == "configuration":
        outfile.write("INSERT INTO configuration VALUES ('%s', '%s');\n" % ( id, value ));
    elif table == "customreport":
        if not kb[2].startswith("category"):
            cr_sql = kb[2]
            cr_sql = cr_sql[0:cr_sql.find("=")]
            cr_val = value
        else:
            outfile.write("INSERT INTO customreport VALUES (%s, '%s', '%s', '', '', 0, 0, 'translate', '2003-07-02 11:51:00', 'translate', '2003-07-02 11:51:00', '%s');\n" % ( id, cr_val, cr_sql, value ))
    elif table == "deathreason":
        outfile.write("INSERT INTO deathreason VALUES (%s, '%s', '');\n" % (id, value))
    elif table == "donationtype":
        outfile.write("INSERT INTO donationtype VALUES (%s, '%s', '');\n" % (id, value))
    elif table == "entryreason":
        outfile.write("INSERT INTO entryreason VALUES (%s, '%s', '');\n" % (id, value))
    elif table == "internallocation":
        outfile.write("INSERT INTO internallocation VALUES (%s, '%s', '');\n" % (id, value))
    elif table == "lksex":
        outfile.write("INSERT INTO lksex VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksize":
        outfile.write("INSERT INTO lksize VALUES (%s, '%s');\n" % (id, value))
    elif table == "lkcoattype":
        outfile.write("INSERT INTO lkcoattype VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksmovementtype":
        outfile.write("INSERT INTO lksmovementtype VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksmedialink":
        outfile.write("INSERT INTO lksmedialink VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksdiarylink":
        outfile.write("INSERT INTO lksdiarylink VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksdonationfreq":
        outfile.write("INSERT INTO lksdonationfreq VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksfieldlink":
        outfile.write("INSERT INTO lksfieldlink VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksfieldtype":
        outfile.write("INSERT INTO lksfieldtype VALUES (%s, '%s');\n" % (id, value))
    elif table == "lksloglink":
        outfile.write("INSERT INTO lksloglink VALUES (%s, '%s');\n" % (id, value))
    elif table == "lkurgency":
        outfile.write("INSERT INTO lkurgency VALUES (%s, '%s');\n" % (id, value))
    elif table == "logtype":
        outfile.write("INSERT INTO logtype VALUES (%s, '%s', '');\n" % (id, value))
    elif table == "species":
        outfile.write("INSERT INTO species VALUES (%s, '%s', '', '');\n" % (id, value))
    elif table == "voucher":
        outfile.write("INSERT INTO voucher VALUES (%s, '%s', '');\n" % (id, value))
    elif table == "vaccinationtype":
        outfile.write("INSERT INTO vaccinationtype VALUES (%s, '%s', '');\n" % (id, value))


outfile.close()

# Re-encode as UTF8
os.system("recode java..u8 %s" % (outputdir + "/" + outfilename))
