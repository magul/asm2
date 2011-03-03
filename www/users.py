#!/usr/bin/python

import db
import hashlib

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

    users = db.query(dbo, "SELECT * FROM users WHERE UPPER(UserName) LIKE UPPER('" + username + "')")
    for u in users:
        if u["PASSWORD"].strip() == password or u["PASSWORD"].strip() == javapassword:
            return u
    return None

