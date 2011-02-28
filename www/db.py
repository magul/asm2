#!/usr/bin/python

"""
        Database access module for ASM
        contains methods for running queries against the database
"""

import os, time, datetime

class DatabaseInfo:
    """
    Handles information on connecting to a database. The default values
    here should be set to the location of your database.
    """
    dbtype = "MYSQL" # MYSQL, SQLITE or POSTGRESQL
    host = "localhost"
    username = "root"
    password = "root"
    database = "asm"
    sqlite_file = ""

def is_smcom():
    """
    Returns true if we're running on the sheltermanager.com server
    """
    return os.path.exists("/var/www/sheltermanager.com")

def connection(dbo):
    """
        Creates a connection to the database and returns it
    """
    if dbo.dbtype == "MYSQL": 
        import MySQLdb
        return MySQLdb.connect(host=dbo.host, user=dbo.username, passwd=dbo.password, db=dbo.database)
    if dbo.dbtype == "POSTGRESQL": 
        from pyPgSQL import PgSQL
        return PgSQL.connect(None, dbo.username, dbo.password, dbo.host, dbo.database)
    if dbo.dbtype == "SQLITE": 
        from pysqlite2 import dbapi2 as sqlite
        return sqlite.connect(dbo.sqlite_file)
    
def query(dbo, sql):
    """
        Runs the query given and returns the resultset
        as a list of dictionaries. All fieldnames are
	uppercased when returned.
    """
    # Grab a connection and cursor
    c = connection(dbo)
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
            v = row[i]
            if type(v) == str:
                if v != None:
                    v = v.replace("`", "'")
            rowmap[s.description[i][0].upper()] = v

        l.append(rowmap)

    # Close the cursor and connection
    s.close()
    c.close()
    return l

def query_tuple(dbo, sql):
    """
        Runs the query given and returns the resultset
        as a grid of tuples
    """
    # Grab a connection and cursor
    c = connection(dbo)
    s = c.cursor()
    # Run the query and retrieve all rows
    s.execute(sql)
    d = s.fetchall()
    # Close the cursor and connection
    s.close()
    c.close()
    return d

def query_json(dbo, sql):
    """
        Runs the query given and returns the resultset
        as a JSON array with column names
    """
    # Grab a connection and cursor
    c = connection(dbo)
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
     
def execute(dbo, sql):
    """
        Runs the action query given and returns rows affected
    """
    c = connection(dbo)
    s = c.cursor()
    s.execute(sql)
    rv = s.rowcount
    c.commit()
    s.close()
    c.close()
    return rv

def is_number(x):
    return isinstance(x, (int, long, float, complex))

def get_id(dbo, table):
    """
        Returns the next ID in sequence for a table.
        Does this by basically doing a MAX on the ID
        field and returning that +1 (or 1 if the table
        has no records)
    """
    d = query_tuple(dbo, "SELECT Max(ID) FROM %s" % table)
    if (len(d) == 0) | (d[0][0] == None):
        return 1
    else:
        return d[0][0] + 1
   
def query_int(dbo, sql):
    r = query_tuple(dbo, sql)
    try:
        v = r[0][0]
        return int(v)
    except:
        return int(0)

def query_float(dbo, sql):
    r = query_tuple(dbo, sql)
    try:
        v = r[0][0]
        return float(v)
    except:
        return float(0)

def query_string(dbo, sql):
    r = query_tuple(dbo, sql)
    try :
        v = r[0][0].replace("`", "'")
        return v.encode('ascii', 'ignore')
    except:
        return str("")

def query_date(dbo, sql):
    r = query_tuple(dbo, sql)
    try:
        v = r[0][0]
        return v
    except:
        return None

def today():
    """ Returns today as a python date """
    return datetime.date.today()

def db2python(d):
    """ Returns a python date from a database date """
    return datetime.fromtimestamp(time.strptime("%Y-%m-%d", d))

def python2db(d):
    """ Returns a database date from a Python date """
    return "%d-%02d-%02d" % ( d.year, d.month, d.day )

def dd(d):
    """ Formats a python date as a date for the database """
    if d == None: return "NULL"
    return "'%d-%02d-%02d'" % ( d.year, d.month, d.day )

def ds(s):
    """ Formats a value as a string for the database """
    if s == None: return "NULL"
    return "'%s'" % str(s).replace("'", "''")

def df(f):
    """ Formats a value as a float for the database """
    if f == None: return "NULL"
    return str(f)

def di(i):
    """ Formats a value as an integer for the database """
    if i == None: return "NULL"
    return str(i)

def escape(s):
    """ Makes a value safe for queries """
    return s.replace("'", "''")

def make_insert_sql(table, s):
    """
    Creates insert sql, 'table' is the table name,
    's' is a tuple of tuples containing the field names
    and values, eg:
    
    make_insert_sql("animal", ( ( "ID", di(52) ), ( "AnimalName", ds("Indy") ) ))
    """
    fl = ""
    fv = ""
    for r in s:
        if fl != "": 
            fl += ", "
            fv += ", "
        fl += r[0]
        fv += r[1]
    return "INSERT INTO %s (%s) VALUES (%s);" % ( table, fl, fv )

def make_insert_user_sql(table, username, s):
    """
    Creates insert sql for a user, 'table' is the table name,
    username is the name of the user to be stamped in the fields
    's' is a tuple of tuples containing the field names
    and values, eg:
    
    make_insert_user_sql("animal", "jeff", ( ( "ID", di(52) ), ( "AnimalName", ds("Indy") ) ))
    """
    l = list(s)
    l.append(("CreatedBy", ds(username)))
    l.append(("CreatedDate", dd(i18n.now())))
    l.append(("LastChangedBy", ds(username)))
    l.append(("LastChangedDate", dd(i18n.now())))
    return make_insert_sql(table, l)

def make_update_sql(table, cond, s):
    """
    Creates update sql, 'table' is the table name,
    's' is a tuple of tuples containing the field names
    and values, 'cond' is the where condition eg:
    
    make_update_sql("animal", "ID = 52", (( "AnimalName", ds("James") )))
    """
    o = "UPDATE %s SET " % table
    first = True
    for r in s:
        if not first:
            o += ", "
        first = False
        o += r[0] + "=" + r[1]
    return o

def make_update_user_sql(table, username, cond, s):
    """
    Creates update sql for a given user, 'table' is the table 
    name, username is the username of the user making the change,
    cond is the where condition eg:

    make_update_user_sql("animal", "jeff", "ID = 52", (( "AnimalName", ds("James") )))
    """
    l = list(s)
    l.append(("LastChangedBy", ds(username)))
    l.append(("LastChangedDate", dd(i18n.now())))
    return make_update_sql(table, cond, s);

def tokenise(s):
    """ Escapes chr 13/10 as \lf and \cr """
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
    # SQLite doesn't have types - everything is a string
    # Fallback, return whatever we got
    return s
