#!/usr/bin/python

import base64
import db

def get_string(dbo, name, path = ""):
    """
    Gets DBFS file contents as a string. Returns
    an empty string if the file is not found. If no path
    is supplied, just finds the first file with that name
    in the dbfs (useful for media files, which have unique names)
    """
    if path != "":
        s = db.query_string(dbo, "SELECT Content FROM dbfs WHERE Name = '%s' AND Path = '%s'" % (name, path))
    else:
        s = db.query_string(dbo, "SELECT Content FROM dbfs WHERE Name = '%s'" % name)
    if s != "":
        s = base64.b64decode(s)
    return s

def get_file(dbo, name, path, saveto):
    """
    Gets DBFS file contents and saves them to the
    filename given. Returns True for success
    """
    s = db.query_string(dbo, "SELECT Content FROM dbfs WHERE Name = '%s' AND Path = '%s'" % (name, path))
    if s != "":
        f = open(saveto, "wb")
        f.write(base64.b64decode(s))
        f.close()
        return True
    return False

def list_contents(dbo, path):
    """
    Returns a list of items in the path given. Directories
    are identifiable by not having a file extension.
    """
    rows = db.query(dbo, "SELECT Name FROM dbfs WHERE Path = '%s'" % path)
    l = []
    for r in rows:
        l.append(r["NAME"])

