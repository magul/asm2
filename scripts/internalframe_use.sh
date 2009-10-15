#!/bin/sh
BASEDIR=`dirname $0`/..
grep -ir 'JInternalFrame' `find $BASEDIR/src -name '*.java'`
