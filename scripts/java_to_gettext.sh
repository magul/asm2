#!/bin/sh

LANGUAGES="es_ES fr_FR nl_NL lt_LT"
FILES="bo charts database db mailmerge reports startup uianimal uianimalname uibeans uicustomreport uidiary uierror uiinternet uilocalcache uilog uilogin uilookups uilostandfound uimain uimedical uimovement uiowner uireportviewer uisplash uisystem uiusers uiviewers uiwaitinglist uiwordprocessor"

cd `dirname $0`/../src/locale
mkdir -p po
rm -f po/*

# Create master template file of all English properties
for F in $FILES; do
    cat $F.properties >> po/t.properties
done

# Convert it to a .pot file
prop2po --duplicates=merge -P po/t.properties > po/sheltermanager.pot

# Create combined file for each language
for L in $LANGUAGES; do
    for F in $FILES; do
        cat $F*$L.properties >> po/t_$L.properties
    done

    # Convert it to po
    prop2po --duplicates=merge -t po/t.properties -i po/t_$L.properties > po/$L.po

done
