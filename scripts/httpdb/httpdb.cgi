#!/usr/bin/python
import time
import cgi

# MySQL DB =============================
#import MySQLdb
#mysql_host = "localhost"
#mysql_user = "root"
#mysql_password = ""
#mysql_db = "propmast"

# SQLite 3 DB ==========================
#from pysqlite2 import dbapi2 as sqlite
# SQLite 2
#import sqlite 
#sqlite_db = "pm.db"

# PostgresSQL ==========================
from pyPgSQL import PgSQL
pg_host = "host:5432"
pg_user = "user"
pg_passwd = "pass"
pg_db = "database"
    
def getConnection():
    """
        Creates a connection to the database and returns it
    """
    # MySQL
    #return MySQLdb.connect(host=mysql_host, user=mysql_user, passwd=mysql_password, db=mysql_db )
    # Postgres
    return PgSQL.connect(None, pg_user, pg_passwd, pg_host, pg_db)
    # SQLite
    #return sqlite.connect(sqlite_db)
    
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

# Get the SQL
f = cgi.FieldStorage()
sql = f["sql"].value

print "Content-Type: text-plain\n\n"

# Is it an action query?
if not sql.lower().startswith("select"):

    # Run the action and return the number of rows changed or
    # an error message
    try:
        print str(executeQuery(sql))
    except Exception, err:
        print "ERR", err

else:

    # We have a resultset type query =====
    # Grab a connection and cursor
    c = getConnection()
    s = c.cursor()
    # Run the query and retrieve all rows
    s.execute(sql)
    d = s.fetchall()
    # Get the list of columns
    cols = []
    for c in s.description:
        cols.append(( c[0], str(c[1])))
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
