#!/bin/sh

THISDIR=`dirname $0`
FILES="`cat $THISDIR/i18n_files.txt`"

cd `dirname $0`/../src/locale
mkdir -p po
rm -f po/*

# Create master template file of all English properties
for F in $FILES; do
    cat $F.properties >> po/t.properties
done

# Convert it to a .pot file
prop2po --duplicates=merge -P po/t.properties > po/sheltermanager.pot

# Remove temporary files
rm -f po/t.properties
