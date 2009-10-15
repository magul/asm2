#!/bin/sh

LANGUAGES="es_ES fr_FR nl_NL lt_LT"
FILES="bo charts database db mailmerge reports startup uianimal uianimalname uibeans uicustomreport uidiary uierror uiinternet uilocalcache uilog uilogin uilookups uilostandfound uimain uimedical uimovement uiowner uireportviewer uisplash uisystem uiusers uiviewers uiwaitinglist uiwordprocessor"

cd `dirname $0`/../src/locale

# Assume that locale/po has been created and updated translations put there
# Clear out any existing properties files though - we'll be creating updates
rm -f po/*.properties


# Unpack each language from the template
for L in $LANGUAGES; do
    for F in $FILES; do
        SUFFIX=_$L.properties
        OUT=po/$F$SUFFIX
        po2prop -t $F.properties -i po/$L.po -o $OUT
    done
done
