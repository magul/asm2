#!/bin/sh

# Creates the MacOSX .app 
cd `dirname $0`
rm -rf ASM.app

mkdir ASM.app
mkdir ASM.app/Contents
mkdir ASM.app/Contents/MacOS
mkdir ASM.app/Contents/Resources

# ASM.app
# =======================================================================

# Info.plist file
echo '<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.applecom/DTDs/" PropertyList=1.0.dtd">

<plist version="1.0">
  <dict>
    <key>CFBundleExecutable</key>
    <string>asm</string>
    <key>CFBundleGetInfoString</key>
    <string>Animal Shelter Manager, Copyright 2000-2008 Robin Rawson-Tetley.</string>
    <key>CFBundleIconFile</key>
    <string>asm.icns</string>
    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>
    <key>CFBundleName</key>
    <string>ASM</string>
    <key>CFBundlePackageType</key>
    <string>????</string>
    <key>CFBundleVersion</key>
    <string>1.3x</string>
  </dict>
</plist>
' > ASM.app/Contents/Info.plist

# Icon
cp ../../logo/asm2009/asm.icns ASM.app/Contents/Resources

# Startup shell scripts
echo '#!/bin/sh

# SWING RENDERER
# =============================================

# The installation directory - work 2 dirs back
# from the current one.
CDIR=`dirname $0`
cd $CDIR
cd ../..
INST=`pwd`

# Useful options for testing:
# -Dapple.awt.graphics.EnableDeferredUpdates="true"
# -agentlib:Shark
# -Dapple.awt.graphics.UseQuartz="true"
# -Dapple.awt.graphics.EnableLazyDrawingQueueSize="20"
# -Dasm.log.debug="true"
#
# Aside from dependencies, we need to specify the app name and the dock icon,
# else we get the internal Java package name and the generic Java icon after
# startup. We also want a Mac menubar at the top of the screen.

# Swing renderer
java -Xmx256m -Xdock:name="Animal Shelter Manager" -Xdock:icon=Contents/Resources/asm.icns -Dapple.laf.useScreenMenuBar="true" -cp "$INST/lib/charting-0.94.jar:$INST/lib/postgresql.jar:$INST/lib/hsqldb.jar:$INST/lib/mysql.jar:$INST/asm.jar" net.sourceforge.sheltermanager.asm.startup.Startup $INST/data

' > ASM.app/Contents/MacOS/asm
chmod +x ASM.app/Contents/MacOS/asm


# Command line interface start script
echo '#!/bin/sh
INST=`dirname $0`
java -Xmx256m -cp "$INST/lib/charting-0.94.jar:$INST/lib/postgresql.jar:$INST/lib/hsqldb.jar:$INST/lib/mysql.jar:$INST/asm.jar" net.sourceforge.sheltermanager.asm.script.Startup $INST/data $@
' > ASM.app/asmcmd
chmod +x ASM.app/asmcmd

# HSQL Database manager
echo '#!/bin/sh
cd `dirname $0`
INSTDIR=`dirname $0`
java -Xmx256m -cp asm.jar net.sourceforge.sheltermanager.asm.startup.HSQLManager $INSTDIR

' > ASM.app/run_hsqlmanager.sh
chmod +x ASM.app/run_hsqlmanager.sh

# HSQL Server
echo '#!/bin/sh
cd `dirname $0`
INSTDIR=`dirname $0`
java -cp lib/hsqldb.jar org.hsqldb.Server -database.0 file:$HOME/.asm/localdb -dbname.0 asm' > ASM.app/run_hsqlserver.sh
chmod +x ASM.app/run_hsqlserver.sh

# Copy the necessary files across
# libs
mkdir ASM.app/lib
cp LICENSE.txt ASM.app
cp ../../build/asm.jar ASM.app/asm.jar
cp ../../lib/*.jar ASM.app/lib

# docs/data
mkdir ASM.app/data
mkdir ASM.app/data/sql
cp ../../build/*.pdf ASM.app/data
cp ../../build/manual_html*.zip ASM.app/data
cp -rf ../../media ASM.app/data/
cp -rf ../../sql/*.sql ASM.app/data/sql
cd ASM.app/data
for i in manual_html*.zip; do
	unzip $i > /dev/null
	rm -f $i
done
cd ../..

# Make package
tar --exclude .svn -czvf sheltermanager_macosx.tar.gz *.app *.txt > /dev/null

