#!/usr/bin/python

import db

LINK_ANIMAL = 0
LINK_LOSTANIMAL = 1
LINK_FOUNDANIMAL = 2
LINK_OWNER = 3
LINK_MOVEMENT = 4
LINK_WAITINGLIST = 5

def get_web_preferred_name(dbo, linktype, id):
    return db.query_string(dbo, "SELECT MediaName FROM media " +
        "WHERE LinkTypeID = %d AND WebsitePhoto = 1 AND LinkID = %d" % (linktype, id))

def get_name_for_id(dbo, id):
    return db.query_string(dbo, "SELECT MediaName FROM media WHERE ID = %d" % id)
