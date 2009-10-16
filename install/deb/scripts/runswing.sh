#!/bin/sh
INSTDIR=/usr/share/asm
java -Xmx256m -cp "$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/asm-swing.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar" net.sourceforge.sheltermanager.asm.startup.Startup $INSTDIR/data


