#!/usr/bin/python

import db
import i18n
import time

def cstring(dbo, key, default = ""):
    rows = db.query(dbo, "SELECT ITEMVALUE FROM configuration WHERE ITEMNAME LIKE '%s'" % key)
    if len(rows) == 0: return default
    v = rows[0]["ITEMVALUE"]
    if v == "": return default
    return v

def cboolean(dbo, key):
    v = cstring(dbo, key, "No")
    return v == "Yes" or v == "True"

def cint(dbo, key):
    v = cstring(dbo, key, "0")
    try:
        return int(v)
    except:
        return int(0)

def cfloat(dbo, key):
    v = cstring(dbo, key, "0")
    try:
        return float(v)
    except:
        return float(0)

def cset(dbo, key, value = ""):
    db.execute(dbo, "DELETE FROM configuration WHERE ItemName LIKE '%s'" % key)
    db.execute(dbo, "INSERT INTO configuration VALUES ('%s', '%s')" % ( key, value ))




def age_group(dbo, band):
    return cfloat(dbo, "AgeGroup%d" % band)

def age_group_name(dbo, band):
    return cstring(dbo, "AgeGroup%dName" % band)

def foster_on_shelter(dbo):
    return cboolean(dbo, "FosterOnShelter")

def organisation(dbo):
    return cstring(dbo, "Organisation", "")

def set_variable_data_updated_today(dbo):
    cset(dbo, "VariableAnimalDataUpdated", time.strftime("%Y%m%d", i18n.now().timetuple()))

def variable_data_updated_today(dbo):
    todaystr = time.strftime("%Y%m%d", i18n.now().timetuple())
    return todaystr == cstring(dbo, "VariableAnimalDataUpdated")
