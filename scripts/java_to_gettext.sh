#!/bin/sh

THISDIR=`dirname $0`
LANGUAGES="`cat $THISDIR/i18n_languages.txt`"
FILES="`cat $THISDIR/i18n_files.txt`"
LDIR="$THISDIR/../src/locale"

# Create master template file of all English properties
for F in $FILES; do
    cat $LDIR/$F.properties >> $LDIR/po/t.properties
done

# Convert it to a .pot file
prop2po --duplicates=merge -P $LDIR/po/t.properties > $LDIR/po/sheltermanager.pot

# Create combined file for each language
for L in $LANGUAGES; do
    for F in $FILES; do
        cat $LDIR/$F*$L.properties >> $LDIR/po/t_$L.properties
    done

    # Convert it to a po
    prop2po --duplicates=merge -t $LDIR/po/t.properties -i $LDIR/po/t_$L.properties > $LDIR/po/`python $THISDIR/locale_to_po.py $L`

done

# Remove temporary files
rm -f $LDIR/po/t.properties $LDIR/po/t_*.properties
