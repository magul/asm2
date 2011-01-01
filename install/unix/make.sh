#!/bin/sh

cd `dirname $0`

# Creates the unix tar.gz file

rm -rf asm
mkdir asm

# Icon
cp ../../logo/asm2009/asm.xpm asm

# Startup shell script
echo '#!/bin/sh
INSTDIR=`dirname $0`
java -Xmx256m -cp "$INSTDIR/lib/edtftpj.jar:$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/asm.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar" net.sourceforge.sheltermanager.asm.startup.Startup

' > asm/run.sh
chmod +x asm/run.sh

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
java -Xmx256m -cp "$INSTDIR/lib/edtftpj.jar:$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar:$INSTDIR/asm.jar" net.sourceforge.sheltermanager.asm.script.Startup $@

' > asm/runcmd.sh
chmod +x asm/runcmd.sh

# Copy the necessary files across
# libs
mkdir asm/lib
cp ../../build/asm.jar asm/asm.jar
cp ../../lib/*.jar asm/lib

# sql files and readme
mkdir asm/data
mkdir asm/data/sql
cp -rf ../../sql/*.sql asm/data/sql
cp README asm
cp LICENSE asm

# Put it in a tar.gz package
rm -f *.tar.gz
tar --exclude .svn -czvf sheltermanager_noarch.tar.gz asm > /dev/null
