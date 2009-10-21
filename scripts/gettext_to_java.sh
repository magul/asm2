#!/bin/sh

THISDIR=`dirname $0`
LANGUAGES="`cat $THISDIR/i18n_languages.txt`"
FILES="`cat $THISDIR/i18n_files.txt`"
LDIR="$THISDIR/../src/locale"

# We're going to read each .po file (if its in our list of languages) 
# in src/locale/po and convert it to properties files

# Clear out any existing properties files that might be in the
# way.
rm -f $LDIR/po/*.properties

# Unpack each language from the template
for L in $LANGUAGES; do
    for F in $FILES; do
        SUFFIX=_$L.properties
        OUT=po/$F$SUFFIX
        PONAME=`python $THISDIR/locale_to_po.py $L`
        po2prop -t $LDIR/$F.properties -i $LDIR/po/$PONAME -o $LDIR/$OUT
    done
done
