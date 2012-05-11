#!/usr/bin/python

JDBC_DRIVER = "org.hsqldb.jdbcDriver"
JDBC_URL = "jdbc:hsqldb:file:/home/robin/.asm/localdb"
TABLES = ( "accounts", "accountstrx", "additional", "additionalfield", "adoption", "animalcost", "animaldiet", "animalfound", "animallitter", "animallost", "animalmedical", "animalmedicaltreatment", "animal", "animalname", "animaltype", "animalvaccination", "animalwaitinglist", "basecolour", "breed", "configuration", "costtype", "customreport", "deathreason", "diary", "diarytaskdetail", "diarytaskhead", "diet", "donationtype", "entryreason", "internallocation", "lkcoattype", "lksdiarylink", "lksex", "lksize", "lksloglink", "lksmedialink", "lksmovementtype", "lkurgency", "log", "logtype", "media", "medicalprofile", "ownerdonation", "owner", "ownervoucher", "species", "users", "vaccinationtype", "voucher", "dbfs" )

import jpype, sys

jar = r"/home/robin/workspace/sheltermanager_2x/lib/hsqldb.jar"
args = "-Djava.class.path=%s" % jar
jvm_path = jpype.getDefaultJVMPath()
jpype.startJVM(jvm_path, args)

import jaydebeapi

c = jaydebeapi.connect(JDBC_DRIVER, JDBC_URL)

def query(c, sql):
    """ Runs SQL and returns a list of maps """
    s = c.cursor()
    s.execute(sql)
    d = s.fetchall()
    l = []
    for row in d:
        rowmap = {}
        for i in xrange(0, len(row)):
            v = row[i]
            if type(v) == unicode:
                if v != None:
                    v = v.encode("ascii", "xmlcharrefreplace")
                    v = v.replace("`", "'")
                    v = v.replace("\x92", "'")
            if type(v) == str:
                if v != None:
                    v = v.replace("`", "'")
                    v = v.replace("\x92", "'")
            rowmap[s.description[i][0].upper()] = v
        l.append(rowmap)
    s.close()
    return l

def outputtable(c, table):
    """ Outputs inserts for a whole table """
    sys.stderr.write("Dumping %s ...\n" % table)
    print "DELETE FROM %s;" % table
    rows = query(c, "select * from %s" % table)
    sys.stderr.write("   (%d rows) ...\n" % len(rows))
    if len(rows) == 0: return
    cols = []
    for k in rows[0].iterkeys():
        cols.append(k)
    collist = ",".join(cols)
    for r in rows:
        vals = []
        for cl in cols:
            v = r[cl]
            if v == None: 
                v = "null"
            elif type(v) == int or type(v) == float: 
                v = str(v)
            else: 
                v = "'%s'" % str(v).replace("'", "`")
            v = v.replace("\r", "\\r")
            v = v.replace("\n", "\\n")
            vals.append(v)
        print "INSERT INTO %s (%s) VALUES (%s);" % ( table, collist, ",".join(vals))

print "DELETE FROM primarykey;"
for t in TABLES:
    outputtable(c, t)

c.close()

