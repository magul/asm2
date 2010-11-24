#!/usr/bin/python

"""
        Reads an image from the database according to the 
        parameters set in the querystring. If no image was
        found, the nopic.jpg image data is returned instead.

        id - the ID

        type - "animal" for the animal with id's web preferred image
               "thumb" for a thumbnail of the animal with id's 
                       web preferred image
               "media" for the image data for media record with id
"""

import cgi

import db

f = cgi.FieldStorage()

print "Content-Type: image/jpeg\n"

