#!/usr/bin/python

"""
    Simple CGI that allows for sending of email
"""

import os, smtplib, cgi

print "Content-type: text/plain"
print ""

f = cgi.FieldStorage();

fr = f["from"].value
to = f["to"].value
msg = f["msg"].value

smtp = smtplib.SMTP()
smtp.connect("localhost", 25)
smtp.sendmail(fr, to, msg)
smtp.quit()

print "OK"
