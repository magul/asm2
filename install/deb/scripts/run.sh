#!/bin/sh
INSTDIR=/usr/share/asm
java -Xmx256m -cp "$INSTDIR/lib/edtftpj.jar:$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/asm.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar" net.sourceforge.sheltermanager.asm.startup.Startup


