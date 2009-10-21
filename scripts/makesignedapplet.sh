#!/bin/sh

cd `dirname $0`/../build

# Make a big fat applet jar
mkdir applet
cd applet
unzip ../asm.jar
rm -rf META-INF
unzip ../../lib/hsqldb.jar
rm -rf META-INF
unzip ../../lib/charting-0.94.jar
rm -rf META-INF
unzip ../../lib/mysql.jar
rm -rf META-INF
unzip ../../lib/postgresql.jar
rm -rf META-INF
zip -r9 ../fatone.jar *

cd ..
rm -rf applet

# Sign it
jarsigner -keystore ../scripts/compstore -storepass ab123d -keypass kpi555 -signedjar asm-applet.jar fatone.jar signFiles
rm -f fatone.jar

# Make an archive for it
cp asm-applet.jar ../scripts/asmweb
cd ../scripts
mkdir asm
mkdir asm/sql
cp -f asmweb/* asm
cp -r ../sql/*.sql asm/sql
cp -r ../media asm
zip -r9 ../build/sheltermanager-`cat ../VERSION`_web.zip asm
rm -rf asmweb/asm-applet.jar asm
