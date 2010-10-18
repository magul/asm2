#!/usr/bin/python

# Modify for your backend database
DBTYPE = "MYSQL" # Should be MYSQL, SQLITE or POSTGRESQL
HOST = "localhost"
USERNAME = "root"
PASSWORD = ""
DATABASE = "asm"
SQLITE_FILE = ""

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

if DBTYPE == "MYSQL": import MySQLdb
if DBTYPE == "POSTGRESQL": import pyPgSQL
if DBTYPE == "SQLITE": from pysqlite2 import dbapi2 as sqlite
    
def getConnection():
    """
        Creates a connection to the database and returns it
    """
    if DBTYPE == "MYSQL": return MySQLdb.connect(host=HOST, user=USERNAME, passwd=PASSWORD, db=DATABASE)
    if DBTYPE == "POSTGRESQL": return PgSQL.connect(None, USERNAME, PASSWORD, HOST, DATABASE)
    if DBTYPE == "SQLITE": return sqlite.connect(SQLITE_FILE)
    
def runQuery(sql):
    """
        Runs the query given and returns the resultset
        as a list of dictionaries
    """
    # Grab a connection and cursor
    c = getConnection()
    s = c.cursor()
    # Run the query and retrieve all rows
    s.execute(sql)
    d = s.fetchall()
    # Initalise our list of results
    l = []
    for row in d:
        # Intialise a map for each row
        rowmap = {}
        for i in xrange(0, len(row)):
            rowmap[s.description[i][0]] = row[i]
        l.append(rowmap)

    # Close the cursor and connection
    s.close()
    c.close()
    return l

def runQuery2(sql):
    """
        Runs the query given and returns the resultset
        as a grid of tuples
    """
    # Grab a connection and cursor
    c = getConnection()
    s = c.cursor()
    # Run the query and retrieve all rows
    s.execute(sql)
    d = s.fetchall()
    # Close the cursor and connection
    s.close()
    c.close()
    return d

def runQueryJSON(sql):
    """
        Runs the query given and returns the resultset
        as a JSON array with column names
    """
    # Grab a connection and cursor
    c = getConnection()
    s = c.cursor()
    # Run the query
    s.execute(sql)
    # Loop round the rows
    rows = ""
    while 1:
        d = s.fetchone()
        if d == None: break

        row = "{"
        for i in xrange(0, len(d)):
            if row != "{": row += ", "
            # if it's null
            if d[i] == None: 
                value = "null"
            # if it's numeric
            elif isnumber(d[i]): 
                value = str(d[i])
            # if it's a string
            else:
                value = "\"" + str(d[i]).replace("\n", "\\n") + "\""
            row += "\"%s\" : %s" % ( s.description[i][0].upper(), value )
        row += "}"
        if rows != "": rows += ",\n"
        rows += row
    json = "[\n" + rows + "\n]"

    # Close the cursor and connection
    s.close()
    c.close()
    return json
     
def executeQuery(sql):
    """
        Runs the action query given and returns rows affected
    """
    c = getConnection()
    s = c.cursor()
    s.execute(sql)
    rv = s.rowcount
    c.commit()
    s.close()
    c.close()
    return rv

def isnumber(x):
    return isinstance(x, (int, long, float, complex))

def getId(table):
    """
        Returns the next ID in sequence for a table.
        Does this by basically doing a MAX on the ID
        field and returning that +1 (or 1 if the table
        has no records)
    """
    d = runQuery2("SELECT Max(ID) FROM %s" % table)
    if (len(d) == 0) | (d[0][0] == None):
        return 1
    else:
        return d[0][0] + 1
   
def queryInt(sql):
    r = runQuery2(sql)
    try:
        v = r[0][0]
        return int(v)
    except:
        return int(0)

def queryFloat(sql):
    r = runQuery2(sql)
    try:
        v = r[0][0]
        return float(v)
    except:
        return float(0)

def queryString(sql):
    r = runQuery2(sql)
    try :
        v = r[0][0]
        return v.encode('ascii', 'ignore')
    except:
        return str("")

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

    c = getConnection()
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
    c = getConnection()
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
