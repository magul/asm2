#!/bin/sh

# Reads the database properties files and generates the
# translation patch files.
cd `dirname $0`
SQLDIR=../sql
PROPDIR=../src/locale
LANGUAGES="_es_ES _lt_LT _nl_NL _fr_FR"

for L in $LANGUAGES; do

    # NOTE - we don't convert back the English one as that was
    # used as a template in the first place - I update the English
    # one when things change, then use database_to_java to generate
    # a new database.properties which becomes the new template
    if [ -f $PROPDIR/database$L.properties ]; then

        # Run our process on the file to create the sql patch
        python property_to_sql.py $PROPDIR/database$L.properties $SQLDIR

    fi

done
