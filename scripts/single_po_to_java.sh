#!/bin/sh

# This is just a convenient way of building the translation for one 
# specific language

THISDIR=`dirname $0`
FILES="`cat $THISDIR/i18n_files.txt`"
LDIR="$THISDIR/../src/locale"

if [ -z "$1" ]; then
    echo "Usage: single_po_to_java.sh [lang_COUNTRY]"
    exit 1
fi

# We're going to read the specified .po file 
# in src/locale/po and convert it to properties files

# Clear out any existing properties files that might be in the
# way.
rm -f $LDIR/po/*.properties

# Create the properties files for the language
for L in $1; do
    for F in $FILES; do
        SUFFIX=_$L.properties
        OUT=po/$F$SUFFIX
        PONAME=`python $THISDIR/locale_to_po.py $L`
        po2prop -t $LDIR/$F.properties -i $LDIR/po/$PONAME -o $LDIR/$OUT
    done
done

# Copy the new properties files into the locale
mv $LDIR/po/*.properties $LDIR

# Update the database scripts if necessary
$THISDIR/java_to_database.sh
