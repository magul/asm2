#!/bin/sh
BASEDIR=`dirname $0`/..
grep -ir 'swingwt' `find $BASEDIR/src -name '*.java'`
