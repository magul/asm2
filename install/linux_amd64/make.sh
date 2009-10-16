#!/bin/sh

cd `dirname $0`

# Creates the linux tar.gz file

rm -rf asm
mkdir asm

# Icon
cp ../../logo/asm2009/asm.xpm asm

# Startup shell script
echo '#!/bin/sh
INSTDIR=`dirname $0`
java -Xmx256m -Dorg.eclipse.swt.browser.XULRunnerPath=/usr/lib/xulrunner-1.9 -cp "$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/swingwt.jar:$INSTDIR/lib/swt.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/asm.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar" net.sourceforge.sheltermanager.asm.startup.Startup $INSTDIR/data

' > asm/run.sh
chmod +x asm/run.sh

# Swing shell script
echo '#!/bin/sh
INSTDIR=`dirname $0`
java -Xmx256m -Dorg.eclipse.swt.browser.XULRunnerPath=/usr/lib/xulrunner-1.9 -cp "$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/swingwt.jar:$INSTDIR/lib/swt.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/asm-swing.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar" net.sourceforge.sheltermanager.asm.startup.Startup $INSTDIR/data

' > asm/runswing.sh
chmod +x asm/runswing.sh

# HSQL Database manager
echo '#!/bin/sh
cd `dirname $0`
INSTDIR=`dirname $0`
java -Xmx256m -cp asm.jar net.sourceforge.sheltermanager.asm.startup.HSQLManager $INSTDIR

' > asm/run_hsqlmanager.sh
chmod +x asm/run_hsqlmanager.sh

# HSQL Server
echo '#!/bin/sh
cd `dirname $0`
INSTDIR=`dirname $0`
java -cp lib/hsqldb.jar org.hsqldb.Server -database.0 file:$HOME/.asm/localdb -dbname.0 asm' > asm/run_hsqlserver.sh
chmod +x asm/run_hsqlserver.sh

# Command line interface script
echo '#!/bin/sh
INSTDIR=`dirname $0`
java -Xmx256m -cp "$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/swingwt.jar:$INSTDIR/lib/swt.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar:$INSTDIR/asm.jar" net.sourceforge.sheltermanager.asm.script.Startup $INSTDIR/data $@

' > asm/runcmd.sh
chmod +x asm/runcmd.sh

# Copy the necessary files across
# libs
mkdir asm/lib
cp ../../build/asm.jar asm
cp ../../build/asm-swing.jar asm
cp ../../lib/*.jar asm/lib
cp ../../lib/swt_gtk2_amd64/* asm/lib

# docs/data
mkdir asm/data
mkdir asm/data/sql
cp ../../build/*.pdf asm/data
cp ../../build/manual_html*.zip asm/data
cp -rf ../../media asm/data/
cp -rf ../../sql/*.sql asm/data/sql
cd asm/data
for i in manual_html*.zip; do
	unzip $i > /dev/null
	rm -f $i
done
cd ../..
cp README asm
cp LICENSE asm

# Put it in a tar.gz package
rm -f *.tar.gz
tar --exclude .svn -czvf sheltermanager_amd64.tar.gz asm > /dev/null
