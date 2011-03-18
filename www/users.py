#!/usr/bin/python

import db
import hashlib

def locale(dbo, username = ""):
    """
    Returns the selected locale for a user, or for the
    first user in the system if not specified
    """
    if username == "":
        sql = "SELECT Locale FROM users LIMIT 1"
    else:
        sql = "SELECT Locale FROM users WHERE UPPER(UserName) LIKE UPPER('" + username + "')"
    try:
        return db.query_string(dbo, sql)
    except:
        # Locale column doesn't exist in the table, default
        # to English instead
        return "en"

def authenticate(dbo, username, password):
    """
    Authenticates whether a username and password are valid.
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

    users = db.query(dbo, "SELECT * FROM users WHERE UPPER(UserName) LIKE UPPER('" + username + "')")
    for u in users:
        if u["PASSWORD"].strip() == password or u["PASSWORD"].strip() == javapassword:
            return u
    return None

