#!/bin/sh
echo Starting Animal Shelter Manager...
INSTDIR=/usr/share/asm
XULRUNNER=/usr/lib/xulrunner-1.9
java -Dorg.eclipse.swt.browser.XULRunnerPath=$XULRUNNER -Xmx256m -cp $INSTDIR/asm.jar:$INSTDIR/lib/mysql.jar:$INSTDIR/lib/charting-0.94.jar:$INSTDIR/lib/postgresql.jar:$INSTDIR/lib/hsqldb.jar:$INSTDIR/lib/swingwt.jar:$INSTDIR/lib/swt.jar net.sourceforge.sheltermanager.asm.startup.Startup $INSTDIR/data/
