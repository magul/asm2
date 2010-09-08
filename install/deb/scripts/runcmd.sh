#!/bin/sh
INSTDIR=/usr/share/asm
java -Xmx256m -cp $INSTDIR/asm.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar net.sourceforge.sheltermanager.asm.script.Startup $@
