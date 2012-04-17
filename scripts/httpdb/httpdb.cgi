#!/usr/bin/python

# Modify for your backend database
DBTYPE = "MYSQL" # Should be MYSQL or POSTGRESQL
HOST = "localhost"
USERNAME = "root"
PASSWORD = "root"
DATABASE = "asm"

"""

httpdb.cgi
Robin Rawson-Tetley, September 2010

Python CGI module that allows an ASM database to be exposed 
via a webpage. 

This is (of course) compatible with ASM's built in HTTP JDBC
database driver which requires the following:

INPUTS:

sql=query1[;;actionquery2;;actionquery3]

OUTPUT:

(action queries)
Number of rows affected or ERR Error Message

(select queries)

COLCOLNAME1\\typeCOLTYPE\\colCOLNAME2\\typeCOLTYPE2...
ROWVAL1\\fldVAL2\\fld\\null

or ERR Error Message

COLTYPE is a string and one of integer, bigint, varchar, timestamp, float
\\null is the null type
\\cr \\lf are escaped carriage returns/line feeds

SPECIAL QUERIES:

You can also send the following "virtual" queries to this driver for
a response:

HTTPDB DBNAME - returns a resultset containing one string with the name
                of the database product behind this module.
		Should be one of POSTGRESQL, MYSQL, HSQLDB, SQLITE

"""

import time, cgi, sys

   
def tokenise(s):
    s = s.replace("\n", "\\lf")
    s = s.replace("\r", "\\cr")
    return s

def map_type(s):
    """ Maps the database types to integer, timestamp, varchar or float """
    s = str(s)
    # PostgreSQL types
    if s.startswith("integer"): return "integer"
    if s.startswith("bigint"): return "integer"
    if s.startswith("varchar"): return "varchar"
    if s.startswith("text"): return "varchar"
    if s.startswith("timestamp"): return "timestamp"
    if s.startswith("date"): return "timestamp"
    if s.startswith("float"): return "float"
    # MySQL types
    if s == "1": return "integer"
    if s == "2": return "integer"
    if s == "3": return "integer"
    if s == "8": return "integer"
    if s == "5": return "float"
    if s == "12": return "timestamp"
    if s == "252": return "varchar"
    if s == "253": return "varchar"
    # Fallback, return whatever we got
    return s

# Get the database connection
c = None
if DBTYPE == "MYSQL": 
    import MySQLdb
    c = MySQLdb.connect(host=HOST, user=USERNAME, passwd=PASSWORD, db=DATABASE)
if DBTYPE == "POSTGRESQL": 
    from pyPgSQL import PgSQL
    c = PgSQL.connect(None, USERNAME, PASSWORD, HOST, DATABASE)

# Get the SQL, allow the command line to override
if len(sys.argv) > 1:
    sql = sys.argv[1]
else:
    f = cgi.FieldStorage()
    sql = f["sql"].value

print "Content-Type: text-plain\n\n"

# Is it a special query?
if sql.lower().startswith("httpdb dbname"):
    print "COLdbname\\typevarchar"
    print "ROW" + DBTYPE
# Is it an action query?
elif not sql.lower().startswith("select"):
    s = c.cursor()
    queries = sql.split(";;")
    for q in queries:
	# Run the action and return the number of rows changed or
	# an error message
	try:
            s.execute(q)
	    print str(s.rowcount)
	except Exception, err:
	    print "ERR", err

    c.commit()
    s.close()
    c.close()

else:

    # We have a resultset type query =====
    # Grab a connection and cursor
    s = c.cursor()
    # Run the query and retrieve all rows
    try:
        s.execute(sql)
    except Exception, err:
        print "ERR", err
    d = s.fetchall()
    # Get the list of columns
    cols = []
    for c in s.description:
        cols.append(( c[0], map_type(c[1])))
    # Dump them out
    colmap = "COL"
    for c in cols:
        if len(colmap) > 3: colmap += "\\col"
        colmap += c[0] + "\\type" + c[1]
    print colmap
    # Now do the rows
    if d != None:
        for row in d:
            r = []
            for v in row:
                # Check the types now
                if v == None:
                    r.append("\\null")
                else:
                    r.append(tokenise(str(v)))
            # Dump it out
            print "ROW" + "\\fld".join(r)
    s.close()
    c.close()

