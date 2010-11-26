#!/usr/bin/python

"""
        Module containing code for retrieving lookup data
        from ASM databases.
"""

import db
import hashlib

def config_get(dbo, k, default=""):
    """
    Retrieves a config value from the database
    """
    v = db.query_string(dbo, "SELECT ItemValue FROM configuration WHERE ItemName Like '%s'" % k)
    if v == "": v = default
    return v

def config_set(dbo, k, v):
    """
    Sets a config value in the database
    """
    db.execute(dbo, "DELETE FROM configuration WHERE ItemName Like '%s'" % k)
    db.execute(dbo, "INSERT INTO configuration VALUES ('%s', '%s')" % ( k, v ))

def authenticate(dbo, username, password):
    """
    Authenticates whether a username and password or valid.
    Returns None if authentication failed, or a user row
    """
    username = db.escape(username)

    # Use MD5 hash of password
    m = hashlib.md5()
    m.update(password)
    password = m.hexdigest()

    # Java generated MD5 passwords are a digit shorter than Python
    # generated ones from hashlib for some reason -
    # guess there must be an extra 0 in the hex format string of 
    # hashlib.md5.hexdigest
    javapassword = password[1:]

    f = open("/tmp/asmlog", "w")
    f.write("username: " + username + ", password: " + password)
    f.flush()
    f.close()

    users = db.query(dbo, "SELECT * FROM users WHERE UPPER(UserName) LIKE UPPER('" + username + "')")
    for u in users:
        if u["PASSWORD"].strip() == password or u["PASSWORD"].strip() == javapassword:
            return u
    return None

