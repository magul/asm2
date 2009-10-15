#!/bin/sh

# Reads any existing database translate_lang.sql files and turns them into
# the java properties files for database translating
cd `dirname $0`
SQLDIR=../sql
PROPDIR=../src/locale
FILES="translate_en.sql translate_lt.sql translate_es.sql"

for f in $FILES; do

    # Take a copy of our current file
    cp $SQLDIR/$f out.sql

    # Recode it as escaped Java
    recode u8..java out.sql

    # Run our process on it to create a property file
    python sql_to_property.py $f $PROPDIR out.sql

    # Get rid of the temporary file
    rm out.sql

done
