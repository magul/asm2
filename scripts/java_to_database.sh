#!/bin/sh

# Reads the database properties files and generates the
# translation patch files.
cd `dirname $0`
SQLDIR=../sql
PROPDIR=../src/locale
LANGUAGES="`cat i18n_languages.txt`"

for L in $LANGUAGES; do

    # NOTE - we don't convert back the English one as that was
    # used as a template in the first place - I update the English
    # one when things change, then use database_to_java_en to generate
    # a new database.properties which becomes the new template
    if [ -f $PROPDIR/database$L.properties ]; then

        # Run our process on the file to create the sql patch
        python property_to_sql.py $PROPDIR/database_$L.properties $SQLDIR $L

    fi

done
